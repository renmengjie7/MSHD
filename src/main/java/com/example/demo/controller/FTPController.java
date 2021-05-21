package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.FTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//FTP机子的操作
@RestController
public class FTPController {

    @Autowired
    private FTPService ftpService;

    /**
     *                  ftp的配置信息
     * @param ip        主机IP
     * @param user      用户名
     * @param passwd    密码
     * @param interval  时间间隔——天数为单位
     * @return
     */
    @RequestMapping("/ftpConfig")
    @ResponseBody
    public JSONObject ftpConfig(String ip,String user,String passwd,int interval){
        System.out.println(ip);
        System.out.println(user);
        System.out.println(passwd);
        return ftpService.ftpConfig(ip, user, passwd,interval);
    }

}
