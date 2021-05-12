package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.ChinaAdministrative;
import com.example.demo.mapper.ChinaAdministrativeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
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
        ChinaAdministrative chinaAdministrative=chinaAdministrativeMapper.selectOne(queryWrapper);
        String code=chinaAdministrative.getCode();
        System.out.println(code);
        return code;
    }
}
