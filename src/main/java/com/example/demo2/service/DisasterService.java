package com.example.demo2.service;

import com.example.demo2.bean.Disaster;
import com.example.demo2.mapper.DisasterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DisasterService {
    @Resource
    private DisasterMapper disasterMapper;

    public List<Disaster> getDisasterNotCoded() {
        return disasterMapper.getDisasterNotCoded();
    }
}
