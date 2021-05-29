package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.DistressedPeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    //获取人员死亡、受伤、失踪的百分比
    @RequestMapping("/getDistressedPeoplePercentage")
    @ResponseBody
    public ResponseEntity getDistressedPeoplePercentage(String earthquakeId) {
        return new ResponseEntity<>(distressedPeopleService.getDistressedPeoplePercentage(earthquakeId),HttpStatus.OK);
    }

    //人员伤亡灾情地区
    @RequestMapping("/getDistressedPeopleCountDividedByLocation")
    @ResponseBody
    public ResponseEntity getDistressedPeopleCountDividedByLocation(String earthquakeId){
        return new ResponseEntity<>(distressedPeopleService.getDistressedPeopleCountDividedByLocation(earthquakeId),HttpStatus.OK);
    }

    //增加人员伤亡信息
    @RequestMapping("/addDistressedPeople")
    @ResponseBody
    public JSONObject addDistressedPeople(String province,
                                                     String city,
                                                     String country,
                                                     String town,
                                                     String village,
                                                     String datetime,
                                                     int number,
                                                     int category,
                                                     String reportingUnit,
                                                     String earthquakeId){
        return distressedPeopleService.addDistressedPeople(province,city,country,town,village,datetime,number,category,reportingUnit,earthquakeId);
    }

    //修改人员伤亡信息
    @RequestMapping("/updateDistressedPeople")
    @ResponseBody
    public JSONObject updateDistressedPeople(int id,
                                          String province,
                                          String city,
                                          String country,
                                          String town,
                                          String village,
                                          String datetime,
                                          int number,
                                          int category,
                                          String reportingUnit,
                                          String earthquakeId){
        return distressedPeopleService.updateDistressedPeople(id,province,city,country,town,village,datetime,number,category,reportingUnit,earthquakeId);
    }

    //根据ID删除某个数据
    @DeleteMapping("/deleteDistressedPeopleById")
    @ResponseBody
    public JSONObject deleteDistressedPeopleById(String id) {
        return distressedPeopleService.deleteDistressedPeopleById(id);
    }

}
