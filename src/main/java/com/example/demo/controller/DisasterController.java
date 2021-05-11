package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.DisasterInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DisasterController {
    @Autowired
    private DisasterInfoService disasterInfoService;

    @RequestMapping("/disasterUpload")
    @ResponseBody
    public JSONObject disasterUpload(@RequestParam("file")MultipartFile file){
        return disasterInfoService.disasterUpload(file);
    }

//    拿到全部的disaster，带搜索，分页
    @RequestMapping("/getDisaster")
    @ResponseBody
    public JSONObject getAllDisaster(String key,
                                    @RequestParam("page")Integer page,
                                    @RequestParam("limit")Integer limit){
        return disasterInfoService.getDisaster(key,page,limit);
    }

}
