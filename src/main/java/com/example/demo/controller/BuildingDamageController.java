package com.example.demo.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.BuildingDamageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;


@RestController
public class BuildingDamageController {

    @Autowired
    private BuildingDamageService buildingDamageService;

    //返回表格数据
    //获取某个震情下的数据，给个id，还要能分页，根据地理位置查询
    @RequestMapping("/getBuildingDamageByEarthquakeId")
    @ResponseBody
    public ResponseEntity getBuildingDamageByEarthquakeId(String earthquakeId, int category, int page, int limit){
        return new ResponseEntity<>(buildingDamageService.getBuildingDamageByEarthquakeId(earthquakeId, category, page, limit), HttpStatus.OK);
    }

    //删除记录
    //根据ID删除某个数据
    @DeleteMapping("/deleteBuildingDamageById")
    @ResponseBody
    public JSONObject deleteBuildingDamageById(String id) {
        return buildingDamageService.deleteBuildingDamageById(id);
    }

    //增加房屋灾情信息
    @RequestMapping("/addBuildingDamage")
    @ResponseBody
    public JSONObject addBuildingDamage(String province,
                                        String city,
                                        String country,
                                        String town,
                                        String village,
                                        String date,
                                        int category,
                                        Double basicallyIntactSquare,
                                        Double damagedSquare,
                                        Double destroyedSquare,
                                        String note,
                                        String reportingUnit,
                                        String earthquakeId,
                                        Double slightDamagedSquare,
                                        Double moderateDamagedSquare,
                                        Double seriousDamagedSquare){
        return buildingDamageService.addBuildingDamage(province,city,country,town,village,date,note,category,basicallyIntactSquare, damagedSquare,destroyedSquare,reportingUnit,earthquakeId,slightDamagedSquare,moderateDamagedSquare,seriousDamagedSquare);
    }

    //修改房屋破坏灾情信息
    @RequestMapping("/updateBuildingDamage")
    @ResponseBody
    public JSONObject updateBuildingDamage(int id,
                                           String province,
                                           String city,
                                           String country,
                                           String town,
                                           String village,
                                           String date,
                                           int category,
                                           Double basicallyIntactSquare,
                                           Double damagedSquare,
                                           Double destroyedSquare,
                                           String note,
                                           String reportingUnit,
                                           String earthquakeId,
                                           Double slightDamagedSquare,
                                           Double moderateDamagedSquare,
                                           Double seriousDamagedSquare){
        return buildingDamageService.updateBuildingDamage(id,province,city,country,town,village,date,note,category,basicallyIntactSquare, damagedSquare,destroyedSquare,reportingUnit,earthquakeId,slightDamagedSquare,moderateDamagedSquare,seriousDamagedSquare);

    }


}
