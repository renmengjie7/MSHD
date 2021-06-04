package com.example.demo.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.entity.*;
import com.example.demo.mapper.*;
import com.example.demo.utility.Ftp;
import com.example.demo.utility.FtpInterface;
import com.example.demo.utility.MyJSONObject;
import com.example.demo.utility.ResultCode;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;


@Service
public class FTPService {

    String mainPath="static/";

    @Autowired
    private FileOperation fileOperation;
    @Autowired
    private DisasterMapper disasterMapper;
    @Autowired
    private DistressedPeopleMapper distressedPeopleMapper;
    @Autowired
    private BuildingDamageMapper buildingDamageMapper;
    @Autowired
    private LifelineDisasterMapper lifelineDisasterMapper;
    @Autowired
    private SecondaryDisasterMapper secondaryDisasterMapper;
    @Autowired
    private ForecastMapper forecastMapper;

    FTPClient ftp;

    /**
     * 本地字符编码
     */
    private static String LOCAL_CHARSET = "GBK";

    // FTP协议里面，规定文件名编码为iso-8859-1
    private static String SERVER_CHARSET = "ISO-8859-1";


    public JSONObject ftpSaveBasicDisaster(String ip, String user, String passwd){
        if (connect(ip, user, passwd)) {
           return saveBasicDisaster();
        }
        else {
            MyJSONObject myJSONObject = new MyJSONObject();
            myJSONObject.putMsg("connect fail (please check the ip,username,password)");
            myJSONObject.putResultCode(ResultCode.invalid);
            return myJSONObject;
        }
    }

    public JSONObject ftpSavePeople(String ip, String user, String passwd){
        if (connect(ip, user, passwd)) {
            return savePeople();
        }
        else {
            MyJSONObject myJSONObject = new MyJSONObject();
            myJSONObject.putMsg("connect fail (please check the ip,username,password)");
            myJSONObject.putResultCode(ResultCode.invalid);
            return myJSONObject;
        }
    }

    public JSONObject ftpSaveBuildingDamage(String ip, String user, String passwd){
        if (connect(ip, user, passwd)) {
            return saveBuildingDamage();
        }
        else {
            MyJSONObject myJSONObject = new MyJSONObject();
            myJSONObject.putMsg("connect fail (please check the ip,username,password)");
            myJSONObject.putResultCode(ResultCode.invalid);
            return myJSONObject;
        }
    }

    public JSONObject ftpSaveLifelineDisaster(String ip, String user, String passwd){
        if (connect(ip, user, passwd)) {
            return saveLifelineDisaster();
        }
        else {
            MyJSONObject myJSONObject = new MyJSONObject();
            myJSONObject.putMsg("connect fail (please check the ip,username,password)");
            myJSONObject.putResultCode(ResultCode.invalid);
            return myJSONObject;
        }
    }

    public JSONObject ftpSaveForecast(String ip, String user, String passwd){
        if (connect(ip, user, passwd)) {
            return saveForecast();
        }
        else {
            MyJSONObject myJSONObject = new MyJSONObject();
            myJSONObject.putMsg("connect fail (please check the ip,username,password)");
            myJSONObject.putResultCode(ResultCode.invalid);
            return myJSONObject;
        }
    }

    public JSONObject ftpSaveSecondaryDisaster(String ip, String user, String passwd){
        if (connect(ip, user, passwd)) {
            return saveSecondaryDisaster();
        }
        else {
            MyJSONObject myJSONObject = new MyJSONObject();
            myJSONObject.putMsg("connect fail (please check the ip,username,password)");
            myJSONObject.putResultCode(ResultCode.invalid);
            return myJSONObject;
        }
    }

    public JSONObject ftpConfig(String ip, String user, String passwd,int interval) {
        MyJSONObject myJSONObject = new MyJSONObject();
        if (connect(ip, user, passwd)) {
            MyJSONObject jsonObject1=saveBasicDisaster();
            MyJSONObject jsonObject2=savePeople();
            MyJSONObject jsonObject3=saveBuildingDamage();
            MyJSONObject jsonObject4=saveLifelineDisaster();
            MyJSONObject jsonObject5=saveSecondaryDisaster();
            MyJSONObject jsonObject6=saveForecast();

            if (jsonObject1.getResultCode()==ResultCode.success
                    &&jsonObject2.getResultCode()==ResultCode.success
                    &&jsonObject3.getResultCode()==ResultCode.success
                    &&jsonObject4.getResultCode()==ResultCode.success
                    &&jsonObject5.getResultCode()==ResultCode.success
                    &&jsonObject6.getResultCode()==ResultCode.success){

                jsonObject1.putMsg(jsonObject1.getMsg()+"    "+jsonObject2.getMsg()+"    "+jsonObject3.getMsg()
                                    +"    "+jsonObject4.getMsg()+"    "+jsonObject5.getMsg()+"    "+jsonObject6.getMsg());
                return jsonObject1;
            }
            else {
                //这边就报错了
                jsonObject1.putResultCode(ResultCode.fail);
                jsonObject1.putMsg(jsonObject1.getMsg()+"    "+jsonObject2.getMsg()+"    "+jsonObject3.getMsg()
                                    +"    "+jsonObject1.getMsg()+"    "+jsonObject2.getMsg()+"    "+jsonObject3.getMsg());
                return jsonObject1;
            }
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
                //这边是设置被动服务
                ftp.enterLocalPassiveMode();
                if (FTPReply.isPositiveCompletion(ftp.sendCommand(
                        "OPTS UTF8", "ON"))) {
                    // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
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

    //存储基本震情
    public MyJSONObject saveBasicDisaster() {
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
            List<Disasterinfo> disasterinfos = fileOperation.jsonParseBasic(result);
            if (disasterinfos == null) {
                jsonObject.put("ResultCode", ResultCode.exception);
                jsonObject.put("msg", "connect success, but basic disaster data is invalid or exist");
                jsonObject.put("data", "0");
            } else {
                //拼接子路径
                int id;
                String filename="";
                for (int i = 0; i < disasterinfos.size(); i++) {
                    disasterMapper.insert(disasterinfos.get(i));
                    //对每一个，需要找到文件，存起来
                    //若目标文件夹不存在，则创建
                    String path=mainPath+"disaster/";
                    File upload = new File(path);
                    if (!upload.exists()) {
                        upload.mkdirs();
                    }
                    filename=disasterinfos.get(i).getId()+"."+disasterinfos.get(i).getPicture().split("\\.")[1];
                    path+=filename;
                    getFileByFtp(disasterinfos.get(i).getPicture(),path,0);
                    UpdateWrapper<Disasterinfo> disasterinfoUpdateWrapper= Wrappers.update();
                    disasterinfoUpdateWrapper.eq("id",disasterinfos.get(i).getId());
                    disasterinfos.get(i).setPicture(filename);
                    disasterMapper.update(disasterinfos.get(i),disasterinfoUpdateWrapper);
                }
                jsonObject.put("ResultCode", ResultCode.success);
                jsonObject.put("msg", "connect success, and basic disaster insert success");
                jsonObject.put("data", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("ResultCode", ResultCode.exception);
            jsonObject.put("msg", "connect success, but basic disaster insert exception occur");
            jsonObject.put("data", "0");
        }
        return jsonObject;
    }

    //存储受灾、死完、失踪人群
    public MyJSONObject savePeople(){
        MyJSONObject jsonObject = new MyJSONObject();
        try {
            ftp.changeToParentDirectory();
            // 更改当前工作目录
            ftp.changeWorkingDirectory("/ftpfile/disaster_data/casualties and missing persons");
            InputStream inputStream = ftp.retrieveFileStream("people.json");
            ftp.completePendingCommand();

            if (inputStream==null){
                throw new Exception();
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null) result += line;
            //这边的处理有一些变化
            List<DistressedPeople> distressedPeopleList = fileOperation.jsonParsePeople(result);
            if (distressedPeopleList == null) {
                jsonObject.put("ResultCode", ResultCode.exception);
                jsonObject.put("msg", "connect success, but people data is invalid or exist");
                jsonObject.put("data", "0");
            } else {
                for (int i = 0; i < distressedPeopleList.size(); i++) {
                    distressedPeopleMapper.insert(distressedPeopleList.get(i));
                }
                jsonObject.put("ResultCode", ResultCode.success);
                jsonObject.put("msg", "connect success, and people data insert success");
                jsonObject.put("data", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("ResultCode", ResultCode.exception);
            jsonObject.put("msg", "connect success, but  people data insert  exception occur");
            jsonObject.put("data", "0");
        }
        return jsonObject;
    }

    //存储受灾房屋数据
    public MyJSONObject saveBuildingDamage(){
        MyJSONObject jsonObject = new MyJSONObject();
        try {
            ftp.changeToParentDirectory();
            // 更改当前工作目录
            ftp.changeWorkingDirectory("/ftpfile/disaster_data/building damage");
            InputStream inputStream = ftp.retrieveFileStream("HouseTemplate.json");
            ftp.completePendingCommand();

            if (inputStream==null){
                throw new Exception();
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null) result += line;
            //这边的处理有一些变化
            List<BuildingDamage> buildingDamageList = fileOperation.jsonParseBuildingDamage(result);
            if (buildingDamageList == null) {
                jsonObject.put("ResultCode", ResultCode.exception);
                jsonObject.put("msg", "connect success, but building damage data is invalid or exist");
                jsonObject.put("data", "0");
            } else {
                for (int i = 0; i < buildingDamageList.size(); i++) {
                    buildingDamageMapper.insert(buildingDamageList.get(i));
                }
                jsonObject.put("ResultCode", ResultCode.success);
                jsonObject.put("msg", "connect success, and building damage data insert success");
                jsonObject.put("data", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("ResultCode", ResultCode.exception);
            jsonObject.put("msg", "connect success, but building damage data insert  exception occur");
            jsonObject.put("data", "0");
        }
        return jsonObject;
    }

    //存储生命线灾情
    public MyJSONObject saveLifelineDisaster(){
        MyJSONObject jsonObject = new MyJSONObject();
        try {
            ftp.changeToParentDirectory();
            // 更改当前工作目录
            ftp.changeWorkingDirectory("/ftpfile/disaster_data/lifeline disaster");
            InputStream inputStream = ftp.retrieveFileStream("LifeLineTemplate1.json");
            ftp.completePendingCommand();

            if (inputStream==null){
                throw new Exception();
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null) result += line;
            List<LifelineDisaster> lifelineDisasters = fileOperation.jsonParseLifelineDisaster(result);
            if (lifelineDisasters == null) {
                jsonObject.put("ResultCode", ResultCode.exception);
                jsonObject.put("msg", "connect success, but lifeline Disaster data is invalid or exist");
                jsonObject.put("data", "0");
            } else {
                //拼接子路径
                int id;
                String filename="";
                for (int i = 0; i < lifelineDisasters.size(); i++) {
                    lifelineDisasterMapper.insert(lifelineDisasters.get(i));
                    //对每一个，需要找到文件，存起来
                    //若目标文件夹不存在，则创建
                    String path=mainPath+"lifeline/";
                    File upload = new File(path);
                    if (!upload.exists()) {
                        upload.mkdirs();
                    }
                    filename=lifelineDisasters.get(i).getId()+"."+lifelineDisasters.get(i).getPicture().split("\\.")[1];
                    path+=filename;
                    getFileByFtp(lifelineDisasters.get(i).getPicture(),path,2);
                    UpdateWrapper<LifelineDisaster> lifelineDisasterUpdateWrapper= Wrappers.update();
                    lifelineDisasterUpdateWrapper.eq("id",lifelineDisasters.get(i).getId());
                    lifelineDisasters.get(i).setPicture(filename);
                    lifelineDisasterMapper.update(lifelineDisasters.get(i),lifelineDisasterUpdateWrapper);
                }
                jsonObject.put("ResultCode", ResultCode.success);
                jsonObject.put("msg", "connect success, and lifeline Disaster insert success");
                jsonObject.put("data", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("ResultCode", ResultCode.exception);
            jsonObject.put("msg", "connect success, but lifeline Disaster insert exception occur");
            jsonObject.put("data", "0");
        }
        return jsonObject;
    }

    public MyJSONObject saveForecast(){
        MyJSONObject jsonObject = new MyJSONObject();
        try {
            ftp.changeToParentDirectory();
            // 更改当前工作目录
            ftp.changeWorkingDirectory("/ftpfile/disaster_data/forecast");
            InputStream inputStream = ftp.retrieveFileStream("PredictionTemplate.json");
            ftp.completePendingCommand();

            if (inputStream==null){
                throw new Exception();
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null) result += line;
            List<Forecast> forecasts = fileOperation.jsonParseForecast(result);
            if (forecasts == null) {
                jsonObject.put("ResultCode", ResultCode.exception);
                jsonObject.put("msg", "connect success, but forecast data is invalid or exist");
                jsonObject.put("data", "0");
            } else {
                //拼接子路径
                String filename="";
                for (int i = 0; i < forecasts.size(); i++) {
                    forecastMapper.insert(forecasts.get(i));
                    //对每一个，需要找到文件，存起来
                    //若目标文件夹不存在，则创建
                    String path=mainPath+"forecast/";
                    File upload = new File(path);
                    if (!upload.exists()) {
                        upload.mkdirs();
                    }
                    filename=forecasts.get(i).getId()+"."+forecasts.get(i).getPicture().split("\\.")[1];
                    path+=filename;
                    getFileByFtp(forecasts.get(i).getPicture(),path,4);
                    UpdateWrapper<Forecast> forecastUpdateWrapper= Wrappers.update();
                    forecastUpdateWrapper.eq("id",forecasts.get(i).getId());
                    forecasts.get(i).setPicture(filename);
                    forecastMapper.update(forecasts.get(i),forecastUpdateWrapper);
                }
                jsonObject.put("ResultCode", ResultCode.success);
                jsonObject.put("msg", "connect success, and forecast insert success");
                jsonObject.put("data", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("ResultCode", ResultCode.exception);
            jsonObject.put("msg", "connect success, but forecast insert exception occur");
            jsonObject.put("data", "0");
        }
        return jsonObject;
    }

    //存储二次灾害
    public MyJSONObject saveSecondaryDisaster(){
        MyJSONObject jsonObject = new MyJSONObject();
        try {
            ftp.changeToParentDirectory();
            // 更改当前工作目录
            ftp.changeWorkingDirectory("/ftpfile/disaster_data/secondary");
            InputStream inputStream = ftp.retrieveFileStream("SecondTemplate2.json");
            ftp.completePendingCommand();

            if (inputStream==null){
                throw new Exception();
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null) result += line;
            List<SecondaryDisaster> secondaryDisasters= fileOperation.jsonParseSecondaryDisaster(result);
            if (secondaryDisasters == null) {
                jsonObject.put("ResultCode", ResultCode.exception);
                jsonObject.put("msg", "connect success, but Secondary Disaster data is invalid or exist");
                jsonObject.put("data", "0");
            } else {
                //拼接子路径
                int id;
                String filename="";
                for (int i = 0; i < secondaryDisasters.size(); i++) {
                    secondaryDisasterMapper.insert(secondaryDisasters.get(i));
                    //对每一个，需要找到文件，存起来
                    //若目标文件夹不存在，则创建
                    String path=mainPath+"secondary/";
                    File upload = new File(path);
                    if (!upload.exists()) {
                        upload.mkdirs();
                    }
                    filename=secondaryDisasters.get(i).getId()+"."+secondaryDisasters.get(i).getPicture().split("\\.")[1];
                    path+=filename;
                    getFileByFtp(secondaryDisasters.get(i).getPicture(),path,5);
                    UpdateWrapper<SecondaryDisaster> secondaryDisasterUpdateWrapper= Wrappers.update();
                    secondaryDisasterUpdateWrapper.eq("id",secondaryDisasters.get(i).getId());
                    secondaryDisasters.get(i).setPicture(filename);
                    secondaryDisasterMapper.update(secondaryDisasters.get(i),secondaryDisasterUpdateWrapper);
                }
                jsonObject.put("ResultCode", ResultCode.success);
                jsonObject.put("msg", "connect success, and Secondary Disaster insert success");
                jsonObject.put("data", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("ResultCode", ResultCode.exception);
            jsonObject.put("msg", "connect success, but Secondary Disaster insert exception occur");
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
    public void getFileByFtp(String remotePath, String localPath,int type)  {
        String[] directory={"/ftpfile/disaster_data/basic_earthquake",
                "/ftpfile/disaster_data/casualties and missing persons",
                "/ftpfile/disaster_data/lifeline disaster",
                "/ftpfile/disaster_data/building damage",
                "/ftpfile/disaster_data/forecast",
                "/ftpfile/disaster_data/secondary",};
        InputStream inputStream;
        FileOutputStream fileOutputStream;
        try {
            ftp.changeToParentDirectory();
            ftp.changeWorkingDirectory(directory[type]);
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
