package com.example.demo.controller;

import com.example.demo.service.DisasterUpload;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DisasterController {
    @Autowired
    private DisasterUpload disasterUpload;

    @RequestMapping("/disasterUpload")
    @ResponseBody
    public JSONObject disasterUpload(@RequestParam("file")MultipartFile file){
        return disasterUpload.disasterUpload(file);
    }

}
