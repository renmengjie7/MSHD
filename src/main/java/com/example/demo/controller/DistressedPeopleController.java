package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.DistressedPeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DistressedPeopleController {

    @Autowired
    private DistressedPeopleService distressedPeopleService;

    //获取某个震情下的数据，给个id，还要能分页，根据地理位置查询
    @RequestMapping("/getDistressedPeopleByEarthquakeId")
    @ResponseBody
    public ResponseEntity getDistressedPeopleByEarthquakeId(String earthquakeId, String key, int page, int limit){
        if(key==null){
            key="";
        }
        return new ResponseEntity<>(distressedPeopleService.getDistressedPeopleByEarthquakeId(earthquakeId, key, page, limit), HttpStatus.OK);
    }

}
