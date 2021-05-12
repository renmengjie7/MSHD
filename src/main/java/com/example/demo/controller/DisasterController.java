package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Disasterinfo;
import com.example.demo.service.ChinaAdministrtiveService;
import com.example.demo.service.DisasterInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class DisasterController {
    @Autowired
    private DisasterInfoService disasterInfoService;

    @Autowired
    private ChinaAdministrtiveService chinaAdministrtiveService;

    List<Disasterinfo> disasters=new ArrayList<>();

    @RequestMapping("/disasterUpload")
    @ResponseBody
    public JSONObject disasterUpload(@RequestParam("file")MultipartFile file){
        return disasterInfoService.disasterUpload(file);
    }

//    拿到全部的disaster，带搜索，分页
    @RequestMapping("/getDisaster")
    @ResponseBody
    public ResponseEntity getAllDisaster(String key,
                                         @RequestParam("page")Integer page,
                                         @RequestParam("limit")Integer limit){
        return new ResponseEntity<>(disasterInfoService.getDisaster(key,page,limit), HttpStatus.OK);
    }



    //从数据库中读取位置信息
    @GetMapping("/code")
    @ResponseBody
    public void doCode(){
        //获取未编码的震情对象集
        disasters=disasterInfoService.getDisasterNotCoded();
        if(disasters.isEmpty()){
            System.out.println("\n不存在未编码的震情\n");
        }
        String province,city,country,town,village,date,code,date_form;
        for(Disasterinfo disaster:disasters) {
            province=disaster.getProvince();
            city=disaster.getCity();
            country=disaster.getCountry();
            town= disaster.getTown();
            village=disaster.getVillage();
            date=disaster.getDate();
            if(date==null) {
                date_form="00000000000000";
                System.out.println("\n时间信息为空\n");
            }
            //去除日期中的空格，-和:
            else date_form = date.replaceAll("[[\\s-:punct:]]","");
            //根据地理位置信息找到code
            code=chinaAdministrtiveService.findCode(province,city,country,town,village);
            if(code==null){
                System.out.println("\ncode为空\n");
            }
            else {
                code += date_form;
                disaster.setDId(code);
                System.out.println(code);
                disasterInfoService.setCode(disaster);
            }
        }

    }

    /**
     * 作为一个函数被调用 位置和时间信息作为参数
     * @param province
     * @param city
     * @param country
     * @param town
     * @param village
     * @param date
     * @return code 编码成功   null编码失败
     */
    public String doCode(String province,String city,String country,String town,String village,String date){
        //获取未编码的震情对象集
        String code;
        //去除日期中的空格，-和:
        String date_form = date.replaceAll("[[\\s-:punct:]]","");
        //根据地理位置信息找到code
        code=chinaAdministrtiveService.findCode(province,city,country,town,village);
        if(code==null){
            System.out.println("\n未找到该位置信息对应的代码\n");
            return null;
        }
        else {
            code += date_form;
            return code;
        }
    }


}
