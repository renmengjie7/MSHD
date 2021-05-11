package com.example.demo2.mapper;

import com.example.demo2.bean.Disaster;

public interface DisasterMapper {
    int deleteByPrimaryKey(String id);

    int insert(Disaster record);

    int insertSelective(Disaster record);

    Disaster selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Disaster record);

    int updateByPrimaryKeyWithBLOBs(Disaster record);

    int updateByPrimaryKey(Disaster record);
}