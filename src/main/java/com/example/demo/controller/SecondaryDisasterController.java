package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.SecondaryDisasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SecondaryDisasterController {
    @Autowired
    private SecondaryDisasterService secondaryDisasterService;

    //返回表格数据
    //获取某个震情下的数据，给个id，还要能分页，根据地理位置查询
    @RequestMapping("/getSecondaryDisasterByEarthquakeId")
    @ResponseBody
    public ResponseEntity getSecondaryDisasterByEarthquakeId(String earthquakeId, String key, int page, int limit){
        if(key==null){
            key="";
        }
        return new ResponseEntity<>(secondaryDisasterService.getSecondaryDisasterByEarthquakeId(earthquakeId, key, page, limit), HttpStatus.OK);
    }

    //删除记录
    //根据ID删除某个数据
    @DeleteMapping("/deleteSecondaryDisasterById")
    @ResponseBody
    public JSONObject deleteSecondaryDisasterById(String id) {
        return secondaryDisasterService.deleteSecondaryDisasterById(id);
    }


}