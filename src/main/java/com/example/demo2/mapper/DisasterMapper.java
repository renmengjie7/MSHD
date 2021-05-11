package com.example.demo2.mapper;

import com.example.demo2.bean.Disaster;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DisasterMapper {
    List<Disaster> getDisasterNotCoded();
}