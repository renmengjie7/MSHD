package com.example.demo.utility;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class MyJSONObject extends JSONObject {
    public MyJSONObject(){
        this.put("ResultCode", ResultCode.success);
        this.put("msg", "success");
        this.put("data", "none");
    }

    public void putResultCode(int code){
        this.put("ResultCode",code);
    }

    public void putMsg(String msg){
        this.put("msg",msg);
    }

    public void putData(Object data){
        this.put("data",data);
    }

}
