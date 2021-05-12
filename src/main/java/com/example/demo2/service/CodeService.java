package com.example.demo2.service;

import com.example.demo2.mapper.CodeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CodeService {

    @Resource
    private CodeMapper codeMapper;

    public String findCode(String province,String city,String district,String town,String village){
        return codeMapper.findCode(province,city,district,town,village);
    }
}
