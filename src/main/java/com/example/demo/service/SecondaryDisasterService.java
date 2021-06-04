package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.SecondaryDisaster;
import com.example.demo.entity.SecondaryDisaster;
import com.example.demo.mapper.SecondaryDisasterMapper;
import com.example.demo.utility.MyJSONObject;
import com.example.demo.utility.ResultCode;
import com.example.demo.vo.SecondaryDisasterVO;
import com.example.demo.vo.DataVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecondaryDisasterService {

    @Autowired
    private SecondaryDisasterMapper secondaryDisasterMapper;
    
    //获取某个震情下的数据，给个id，还要能分页，根据地理位置查询
    public DataVO<SecondaryDisaster> getSecondaryDisasterByEarthquakeId(String earthquakeId, int category, int page, int limit){
        DataVO dataVO = new DataVO();
        dataVO.setCode(0);
        dataVO.setMsg("");
        QueryWrapper<SecondaryDisaster> queryWrapper = Wrappers.query();
        queryWrapper.eq("earthquake_id",earthquakeId);
        if(category!=-1){
            queryWrapper.eq("category",category);
        }
        IPage<SecondaryDisaster> secondaryDisasterIPage = new Page<>(page, limit);
        IPage<SecondaryDisaster> result = secondaryDisasterMapper.selectPage(secondaryDisasterIPage, queryWrapper);
        dataVO.setCount(result.getTotal());
        List<SecondaryDisasterVO> secondaryDisasterVOS=new ArrayList<>();
        //对记录数据进行处理，时间
        for (SecondaryDisaster secondaryDisaster: result.getRecords()) {
            SecondaryDisasterVO secondaryDisasterVO=new SecondaryDisasterVO();
            BeanUtils.copyProperties(secondaryDisaster,secondaryDisasterVO);
            secondaryDisasterVO.setDate(secondaryDisaster.getDate().toString().substring(0,"2021-04-21 18:15:57".length()));
            secondaryDisasterVOS.add(secondaryDisasterVO);
        }
        dataVO.setData(secondaryDisasterVOS);
        return dataVO;
    }

    //删除某一条记录
    public JSONObject deleteSecondaryDisasterById(String id) {
        MyJSONObject myJSONObject=new MyJSONObject();
        UpdateWrapper<SecondaryDisaster> secondaryDisasterUpdateWrapper= Wrappers.update();
        secondaryDisasterUpdateWrapper.eq("id",id);
        int re= secondaryDisasterMapper.delete(secondaryDisasterUpdateWrapper);
        if(re==1){
            myJSONObject.putResultCode(ResultCode.success);
            myJSONObject.putMsg("delete success");
        }
        else {
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg("SecondaryDisaster id does not exist");
        }
        return myJSONObject;
    }
}
