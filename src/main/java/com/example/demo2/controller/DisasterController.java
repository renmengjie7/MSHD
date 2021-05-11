package com.example.demo2.controller;

import com.example.demo2.bean.Disaster;
import com.example.demo2.service.DisasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DisasterController {
    @Autowired
    private DisasterService disasterService;

    List<Disaster> disasters;

    @GetMapping("/get")
    public void doCode(){
        disasters=disasterService.getDisasterNotCoded();

    }



}
