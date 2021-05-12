package com.example.demo2.controller;

import com.example.demo2.bean.Disaster;
import com.example.demo2.service.CodeService;
import com.example.demo2.service.DisasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class DisasterController {
    @Autowired
    private DisasterService disasterService;

    @Autowired
    private CodeService codeService;

    List<Disaster> disasters;

    //从数据库中读取位置信息
    /*
    @GetMapping("/code")
    @ResponseBody
    public void doCode(){
        //获取未编码的震情对象集
        disasters=disasterService.getDisasterNotCoded();
        if(disasters.isEmpty()){
            System.out.println("\n不存在未编码的震情\n");
        }
        String province,city,country,town,village,date,code;
        for(Disaster disaster:disasters) {
            province=disaster.getProvince();
            city=disaster.getCity();
            country=disaster.getCountry();
            town= disaster.getTown();
            village=disaster.getVillage();
            date=disaster.getDate();
            //去除日期中的空格，-和:
            String date_form = date.replaceAll("[[\\s-:punct:]]","");
            //根据地理位置信息找到code
            code=codeService.findCode(province,city,country,town,village);
            if(code==null){
                System.out.println("\ncode为空\n");
            }
            else {
                code += date_form;
                disaster.setdId(code);
                System.out.println(code);
                disasterService.setCode(disaster);
            }
        }

    }*/

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
        code=codeService.findCode(province,city,country,town,village);
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
