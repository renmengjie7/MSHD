package com.example.demo.service;

import com.example.demo.vo.ResultCode;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


//基本震情上传的微服务
@Service
public class DisasterUpload {

    public JSONObject disasterUpload(MultipartFile srcFile){
        System.out.println("----------------------------");
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
            JSONObject jsonObject1=new JSONObject(result);
            System.out.println(jsonObject1);
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
