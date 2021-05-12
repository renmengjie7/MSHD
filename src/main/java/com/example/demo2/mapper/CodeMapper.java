package com.example.demo2.mapper;

import com.example.demo2.bean.Code;

public interface CodeMapper {
    int deleteByPrimaryKey(Integer caId);

    int insert(Code record);

    int insertSelective(Code record);

    Code selectByPrimaryKey(Integer caId);

    int updateByPrimaryKeySelective(Code record);

    int updateByPrimaryKey(Code record);

    String findCode(String province,String city,String district,String town,String village);
}