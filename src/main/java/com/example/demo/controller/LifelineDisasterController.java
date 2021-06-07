package com.example.demo.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.LifelineDisasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

@RestController
public class LifelineDisasterController {
    @Autowired
    private LifelineDisasterService lifelineDisasterService;

    //返回表格数据
    //获取某个震情下的数据，给个id，还要能分页，根据地理位置查询
    @RequestMapping("/getLifelineDisasterByEarthquakeId")
    @ResponseBody
    public ResponseEntity getLifelineDisasterByEarthquakeId(String earthquakeId, int category, int page, int limit){
        return new ResponseEntity<>(lifelineDisasterService.getLifelineDisasterByEarthquakeId(earthquakeId, category, page, limit), HttpStatus.OK);
    }

    //删除记录
    //根据ID删除某个数据
    @DeleteMapping("/deleteLifelineDisasterById")
    @ResponseBody
    public JSONObject deleteLifelineDisasterById(String id) {
        return lifelineDisasterService.deleteLifelineDisasterById(id);
    }

    //增加生命线灾情信息
    @RequestMapping("/addLifeLineDisaster")
    @ResponseBody
    public JSONObject addLifeLineDisaster(String province,
                                          String city,
                                          String country,
                                          String town,
                                          String village,
                                          String date,
                                          String note,
                                          int category,
                                          int grade,
                                          int type,
                                          MultipartFile file,
                                          String reportingUnit,
                                          String earthquakeId){
        return lifelineDisasterService.addLifeLineDisaster(province,city,country,town,village,date,note,category,grade,type,file,reportingUnit,earthquakeId);
    }

    //修改生命线灾情信息
    @RequestMapping("/updateLifeLineDisaster")
    @ResponseBody
    public JSONObject updateLifeLineDisaster(int id,
                                          String province,
                                          String city,
                                          String country,
                                          String town,
                                          String village,
                                          String date,
                                          String note,
                                          int category,
                                          int grade,
                                          int type,
                                          MultipartFile file,
                                          String reportingUnit,
                                          String earthquakeId){
        return lifelineDisasterService.updateLifeLineDisaster(id,province,city,country,town,village,date,note,category,grade,type,file,reportingUnit,earthquakeId);
    }



}
