package com.example.demo.service;

import com.example.demo.entity.Disasterinfo;
import com.example.demo.mapper.DisasterMapper;
import com.example.demo.vo.ResultCode;
import net.sf.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


//基本震情上传的微服务
@Service
public class DisasterUpload {

    @Autowired
    private DisasterMapper disasterMapper;

    public JSONObject disasterUpload(MultipartFile srcFile){
        JSONObject jsonObject=new JSONObject();
        if(srcFile.isEmpty()){
            jsonObject.put("ResultCode", ResultCode.abnormal);
            jsonObject.put("msg","no file");
            jsonObject.put("data","none");
            return jsonObject;
        }
        try {
            BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(srcFile.getInputStream()));
            String line;
            String result = "";
            while((line=bufferedReader.readLine()) != null ) result+=line;
//            逻辑处理，存入数据库，需要判断是否重复
            JSONObject data=new JSONObject(result);
            JSONObject disasterInfo=new JSONObject(data.get("disasterInfo").toString());
            JSONArray infos = JSONArray.fromObject(disasterInfo.get("info").toString());
            for (int i=0;i<infos.size();i++){
                JSONObject info=new JSONObject(infos.get(i).toString());
                disasterMapper.insert(new Disasterinfo(
                        "501","",info.getString("province"),
                        info.getString("city"),info.getString("country"),
                        info.getString("town"),info.getString("village"),
                        info.getString("date"),info.getString("location"),
                        Float.parseFloat(info.getString("longitude")),Float.parseFloat(info.getString("latitude")),
                        Float.parseFloat(info.getString("depth")),Float.parseFloat(info.getString("magnitude")),
                        info.getString("picture"),info.getString("reportingUnit")));
            }
            jsonObject.put("ResultCode", ResultCode.success);
            jsonObject.put("msg","success");
            jsonObject.put("data","1");
            return jsonObject;
        } catch (IOException e) {
            e.printStackTrace();
            jsonObject.put("ResultCode", ResultCode.exception);
            jsonObject.put("msg","json data is out of standard");
            jsonObject.put("data","0");
            return jsonObject;
        }
    }

}
