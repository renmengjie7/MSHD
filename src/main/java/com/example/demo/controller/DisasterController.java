package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Disasterinfo;
import com.example.demo.entity.Echarts;
import com.example.demo.service.ChinaAdministrtiveService;
import com.example.demo.service.DisasterInfoService;
import com.example.demo.utility.MyJSONObject;
import com.example.demo.utility.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class DisasterController {
    @Autowired
    private DisasterInfoService disasterInfoService;

    @Autowired
    private ChinaAdministrtiveService chinaAdministrtiveService;

    List<Disasterinfo> disasters=new ArrayList<>();

    @RequestMapping("/MapData")
    @ResponseBody
    public JSONObject MapData(String startDate,String endDate) {
        MyJSONObject myJSONObject=new MyJSONObject();
        Timestamp start;
        Timestamp end;
        try {
            start=Timestamp.valueOf(startDate);
            end= Timestamp.valueOf(endDate);
        }
        catch (Exception exception){
            myJSONObject.putMsg("Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]");
            myJSONObject.putResultCode(ResultCode.invalid);
            return myJSONObject;
        }
        return disasterInfoService.MapData(start,end);
    }


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
            date=disaster.getDate().toString();
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

    //按照年份划分计数
    @RequestMapping("/yearCount")
    @ResponseBody
    public List<Echarts> yearCount() {
        List<Map<String, Object>> mapList=disasterInfoService.countByYear();
        List<Echarts> list = new ArrayList<Echarts>();
        for (Map<String, Object> map : mapList) {
            list.add(new Echarts((String) map.get("y"),((Long)map.get("count")).intValue()));
        }
        return list;
    }

    //按照省份划分计数
    @RequestMapping("/provinceCount")
    @ResponseBody
    public List<Echarts> provinceCount() {
        List<Map<String, Object>> mapList=disasterInfoService.countByProvince();
        List<Echarts> list = new ArrayList<Echarts>();
        for (Map<String, Object> map : mapList) {
            list.add(new Echarts((String) map.get("province"),((Long)map.get("count")).intValue()));
        }
        return list;
    }

    @RequestMapping("/addDisasterInfo")
    @ResponseBody
    //增加
    public JSONObject addDisasterInfo(String province,
                                      String city,
                                      String country,
                                      String town,
                                      String village,
                                      String date,
                                      double longitude,
                                      double latitude,
                                      float depth,
                                      float magnitude,
                                      String reportingUnit,
                                      MultipartFile file){
        return disasterInfoService.addDisasterInfo(province, city, country, town, village, date, longitude, latitude, depth, magnitude, reportingUnit,file);
    }


    @DeleteMapping("/deleteDisasterInfoById")
    @ResponseBody
    //删除
    public JSONObject deleteDisasterInfoById(int id){
        return disasterInfoService.deleteDisasterInfoById(id);
    }


    @RequestMapping("/updateDisasterInfoById")
    @ResponseBody
    //修改
    public JSONObject updateDisasterInfoById(int id,
                                             String province,
                                             String city,
                                             String country,
                                             String town,
                                             String village,
                                             String date,
                                             double longitude,
                                             double latitude,
                                             float depth,
                                             float magnitude,
                                             String reportingUnit,
                                             MultipartFile file){
        return disasterInfoService.updateDisasterInfoById(id, province, city, country, town, village, date, longitude, latitude, depth, magnitude, reportingUnit,file);
    }


}