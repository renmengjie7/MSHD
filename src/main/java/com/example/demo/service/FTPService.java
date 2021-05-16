package com.example.demo.service;


import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Disasterinfo;
import com.example.demo.mapper.DisasterMapper;
import com.example.demo.utility.Ftp;
import com.example.demo.utility.FtpInterface;
import com.example.demo.utility.MyJSONObject;
import com.example.demo.utility.ResultCode;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URLDecoder;
import java.util.List;


@Service
public class FTPService {

    @Autowired
    private FileOperation fileOperation;
    @Autowired
    private DisasterMapper disasterMapper;

    FTPClient ftp;

    /**
     * 本地字符编码
     */
    private static String LOCAL_CHARSET = "GBK";

    // FTP协议里面，规定文件名编码为iso-8859-1
    private static String SERVER_CHARSET = "ISO-8859-1";

    public JSONObject ftpConfig(String ip, String user, String passwd) {
        MyJSONObject myJSONObject = new MyJSONObject();
        if (connect(ip, user, passwd)) {
            return saveBasicDisaster();
        }
        else {
            myJSONObject.putMsg("connect fail (please check the ip,username,password)");
            myJSONObject.putResultCode(ResultCode.invalid);
        }
        return myJSONObject;
    }

    public Boolean connect(String ip, String user, String passwd) {
        Boolean result;
        // 创建接口服务
        FtpInterface ftpInterface = new Ftp();
        // 登录ftp，获取事件
        ftp = ftpInterface.ftp(ip, user, passwd);//这里是ip，用户名，密码
        if (null != ftp) {
            try {
                //这便是设置编码的
                ftp.enterLocalPassiveMode();
                if (FTPReply.isPositiveCompletion(ftp.sendCommand(
                        "OPTS UTF8", "ON"))) {// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
                    LOCAL_CHARSET = "UTF-8";
                }
                ftp.setControlEncoding(LOCAL_CHARSET);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }

    public JSONObject saveBasicDisaster() {
        MyJSONObject jsonObject = new MyJSONObject();
        try {
            ftp.changeToParentDirectory();
            // 更改当前工作目录
            ftp.changeWorkingDirectory("/ftpfile/disaster_data/basic_earthquake");
            InputStream inputStream = ftp.retrieveFileStream("EarthquakeTemplate.json");
            ftp.completePendingCommand();

            if (inputStream==null){
                throw new Exception();
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null) result += line;
            List<Disasterinfo> disasterinfos = fileOperation.jsonParse(result);

            if (disasterinfos == null) {
                jsonObject.put("ResultCode", ResultCode.exception);
                jsonObject.put("msg", "connect success, but data is invalid or exist");
                jsonObject.put("data", "0");
            } else if (disasterinfos.size() == 0) {
                jsonObject.put("ResultCode", ResultCode.exception);
                jsonObject.put("msg", "connect success, but no data");
                jsonObject.put("data", "0");
            } else {
                //拼接子路径
                String directory= ResourceUtils.getURL("classpath:static").getPath().replaceAll("%20"," ").substring(1).replace('/','\\');
                directory= URLDecoder.decode(directory,"UTF-8");
                for (int i = 0; i < disasterinfos.size(); i++) {
                    disasterMapper.insert(disasterinfos.get(i));
                    //对每一个，需要找到文件，存起来
                    //若目标文件夹不存在，则创建
                    String path=directory+"/disaster/basic_earthquake/";
                    File upload = new File(path);
                    if (!upload.exists()) {
                        upload.mkdirs();
                    }
                    path+=disasterinfos.get(i).getPicture();
                    getFileByFtp(disasterinfos.get(i).getPicture(),path);
                }
                jsonObject.put("ResultCode", ResultCode.success);
                jsonObject.put("msg", "connect success, and insert success");
                jsonObject.put("data", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("ResultCode", ResultCode.exception);
            jsonObject.put("msg", "connect success, but exception occur");
            jsonObject.put("data", "0");
        }
        return jsonObject;
    }

    /**
     * FTPClient 下载文件
     *
     * @param remotePath
     * @param localPath
     */
    public void getFileByFtp(String remotePath, String localPath)  {
        InputStream inputStream;
        FileOutputStream fileOutputStream;
        try {
            ftp.changeToParentDirectory();
            ftp.changeWorkingDirectory("/ftpfile/disaster_data/basic_earthquake");
            fileOutputStream=new FileOutputStream(localPath);
            inputStream=ftp.retrieveFileStream(remotePath);
            byte[] b = new byte[1024];
            int length;
            while((length= inputStream.read(b))>0){
                fileOutputStream.write(b,0,length);
            }
            fileOutputStream.close();
            inputStream.close();
            ftp.completePendingCommand();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean disconnect(){
        return true;
    }

}
