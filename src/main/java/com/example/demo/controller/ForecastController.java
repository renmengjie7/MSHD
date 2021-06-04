package com.example.demo.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.FTPService;
import com.example.demo.service.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

}
