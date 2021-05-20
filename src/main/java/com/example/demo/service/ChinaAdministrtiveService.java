package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.ChinaAdministrative;
import com.example.demo.mapper.ChinaAdministrativeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChinaAdministrtiveService {

    @Autowired
    private ChinaAdministrativeMapper chinaAdministrativeMapper;

    public String findCode(String province,String city,String district,String town,String village){
        QueryWrapper<ChinaAdministrative> queryWrapper=new QueryWrapper<>();
        Map<String,Object> paramsMap=new HashMap<>();
        paramsMap.put("province_name",province);
        paramsMap.put("city_name",city);
        paramsMap.put("district_name",district);
        paramsMap.put("town_name",town);
        paramsMap.put("village_name",village);
        queryWrapper.allEq(paramsMap);
        try {
            ChinaAdministrative chinaAdministrative=chinaAdministrativeMapper.selectOne(queryWrapper);
            String code=chinaAdministrative.getCode();
            return code;
        }
        catch (Exception exception){
            exception.printStackTrace();
            return null;
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
        //判断时间长度是否正确
        if (date_form.length()!=14){
            System.out.println("time is incorrect");
            return null;
        }
        //根据地理位置信息找到code
        code=this.findCode(province,city,country,town,village);
        if(code==null){
            System.out.println("\nthe location can not find code\n");
            return null;
        }
        else {
            code = (code+"000000000000").substring(0,12);
            code += date_form;
            return code;
        }
    }
}