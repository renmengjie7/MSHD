package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.LifelineDisaster;
import com.example.demo.mapper.LifelineDisasterMapper;
import com.example.demo.utility.MyJSONObject;
import com.example.demo.utility.ResultCode;
import com.example.demo.vo.LifelineDisasterVO;
import com.example.demo.vo.DataVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LifelineDisasterService{

    @Autowired
    private LifelineDisasterMapper lifelineDisasterMapper;

    //获取某个震情下的数据，给个id，还要能分页，根据地理位置查询
    public DataVO<LifelineDisaster> getLifelineDisasterByEarthquakeId(String earthquakeId, String key, int page, int limit){
        DataVO dataVO = new DataVO();
        dataVO.setCode(0);
        dataVO.setMsg("");
        QueryWrapper<LifelineDisaster> queryWrapper = Wrappers.query();
        queryWrapper.like("location",key);
        IPage<LifelineDisaster> lifelineDisasterIPage = new Page<>(page, limit);
        IPage<LifelineDisaster> result = lifelineDisasterMapper.selectPage(lifelineDisasterIPage, queryWrapper);
        dataVO.setCount(result.getTotal());
        List<LifelineDisasterVO> lifelineDisasterVOS=new ArrayList<>();
        //对记录数据进行处理，时间
        for (LifelineDisaster lifelineDisaster: result.getRecords()) {
            LifelineDisasterVO lifelineDisasterVO=new LifelineDisasterVO();
            BeanUtils.copyProperties(lifelineDisaster,lifelineDisasterVO);
            lifelineDisasterVO.setDate(lifelineDisaster.getDate().toString().substring(0,"2021-04-21 18:15:57".length()));
            lifelineDisasterVOS.add(lifelineDisasterVO);
        }
        dataVO.setData(lifelineDisasterVOS);
        return dataVO;
    }

    //删除某一条记录
    public JSONObject deleteLifelineDisasterById(String id) {
        MyJSONObject myJSONObject=new MyJSONObject();
        UpdateWrapper<LifelineDisaster> lifelineDisasterUpdateWrapper= Wrappers.update();
        lifelineDisasterUpdateWrapper.eq("id",id);
        int re= lifelineDisasterMapper.delete(lifelineDisasterUpdateWrapper);
        if(re==1){
            myJSONObject.putResultCode(ResultCode.success);
            myJSONObject.putMsg("delete success");
        }
        else {
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg("LifelineDisaster id does not exist");
        }
        return myJSONObject;
    }
    
}
