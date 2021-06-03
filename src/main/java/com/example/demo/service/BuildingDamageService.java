package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.BuildingDamage;
import com.example.demo.mapper.BuildingDamageMapper;
import com.example.demo.utility.MyJSONObject;
import com.example.demo.utility.ResultCode;
import com.example.demo.vo.BuildingDamageVO;
import com.example.demo.vo.DataVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BuildingDamageService {

    @Autowired
    private BuildingDamageMapper buildingDamageMapper;

    //获取某个震情下的数据，给个id，还要能分页，根据地理位置查询
    public DataVO<BuildingDamage> getBuildingDamageByEarthquakeId(String earthquakeId, String key, int page, int limit){
        DataVO dataVO = new DataVO();
        dataVO.setCode(0);
        dataVO.setMsg("");
        QueryWrapper<BuildingDamage> queryWrapper = Wrappers.query();
        queryWrapper.like("location",key);
        IPage<BuildingDamage> buildingDamageIPage = new Page<>(page, limit);
        IPage<BuildingDamage> result = buildingDamageMapper.selectPage(buildingDamageIPage, queryWrapper);
        dataVO.setCount(result.getTotal());
        List<BuildingDamageVO> buildingDamageVOS=new ArrayList<>();
        //对记录数据进行处理，时间
        for (BuildingDamage buildingDamage: result.getRecords()) {
            BuildingDamageVO buildingDamageVO=new BuildingDamageVO();
            BeanUtils.copyProperties(buildingDamage,buildingDamageVO);
            buildingDamageVO.setDate(buildingDamage.getDate().toString().substring(0,"2021-04-21 18:15:57".length()));
            buildingDamageVOS.add(buildingDamageVO);
        }
        dataVO.setData(buildingDamageVOS);
        return dataVO;
    }

    //删除某一条记录
    public JSONObject deleteBuildingDamageById(String id) {
        MyJSONObject myJSONObject=new MyJSONObject();
        UpdateWrapper<BuildingDamage> buildingDamageUpdateWrapper= Wrappers.update();
        buildingDamageUpdateWrapper.eq("id",id);
        int re= buildingDamageMapper.delete(buildingDamageUpdateWrapper);
        if(re==1){
            myJSONObject.putResultCode(ResultCode.success);
            myJSONObject.putMsg("delete success");
        }
        else {
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg("BuildingDamage id does not exist");
        }
        return myJSONObject;
    }

}
