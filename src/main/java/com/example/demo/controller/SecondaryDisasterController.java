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
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;


@RestController
public class SecondaryDisasterController {
    @Autowired
    private SecondaryDisasterService secondaryDisasterService;

    //返回表格数据
    //获取某个震情下的数据，给个id，还要能分页，根据地理位置查询
    @RequestMapping("/getSecondaryDisasterByEarthquakeId")
    @ResponseBody
    public ResponseEntity getSecondaryDisasterByEarthquakeId(String earthquakeId, int category, int page, int limit){
        return new ResponseEntity<>(secondaryDisasterService.getSecondaryDisasterByEarthquakeId(earthquakeId, category, page, limit), HttpStatus.OK);
    }

    //删除记录
    //根据ID删除某个数据
    @DeleteMapping("/deleteSecondaryDisasterById")
    @ResponseBody
    public JSONObject deleteSecondaryDisasterById(String id) {
        return secondaryDisasterService.deleteSecondaryDisasterById(id);
    }

    //增加次生灾害灾情信息
    @RequestMapping("/addSecondaryDisaster")
    @ResponseBody
    public JSONObject addSecondaryDisaster(String province,
                                           String city,
                                           String country,
                                           String town,
                                           String village,
                                           String date,
                                           int category,
                                           int type,
                                           int status,
                                           MultipartFile file,
                                           String reportingUnit,
                                           String note,
                                           String earthquakeId){
        return secondaryDisasterService.addSecondaryDisaster(province,city,country,town,village,date,note,category,status,type,file,reportingUnit,earthquakeId);
    }

    //修改生命线灾情信息
    @RequestMapping("/updateSecondaryDisaster")
    @ResponseBody
    public JSONObject updateSecondaryDisaster(int id,
                                              String province,
                                              String city,
                                              String country,
                                              String town,
                                              String village,
                                              String date,
                                              int category,
                                              int type,
                                              int status,
                                              MultipartFile file,
                                              String reportingUnit,
                                              String note,
                                              String earthquakeId) throws Exception {
        return secondaryDisasterService.updateSecondaryDisaster(id,province,city,country,town,village,date,note,category,status,type,file,reportingUnit,earthquakeId);
    }


}
