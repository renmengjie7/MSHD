package com.example.demo.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.FTPService;
import com.example.demo.service.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

@RestController
public class ForecastController {

    @Autowired
    private ForecastService forecastService;

    @RequestMapping("/getForecast")
    @ResponseBody
    public ResponseEntity getForecast( int page, int limit){
        return new ResponseEntity<>(forecastService.getForecast(page, limit), HttpStatus.OK);
    }

    //删除记录
    //根据ID删除某个数据
    @DeleteMapping("/deleteForecast")
    @ResponseBody
    public JSONObject deleteForecast(String id) {
        return forecastService.deleteForecast(id);
    }

    //增加预测信息
    @RequestMapping("/addForecast")
    @ResponseBody
    public JSONObject addForecast(String date,
                                  int grade,
                                  int intensity,
                                  int type,
                                  MultipartFile file){
        return forecastService.addForecast(date,grade,intensity,type,file);
    }

    //修改预测信息
    @CrossOrigin
    @RequestMapping("/updateForecast")
    @ResponseBody
    public JSONObject addForecast(int id,
                                  String date,
                                  int grade,
                                  int intensity,
                                  int type,
                                  MultipartFile file) throws Exception {
        return forecastService.updateForecast(id,date,grade,intensity,type,file);
    }


}
