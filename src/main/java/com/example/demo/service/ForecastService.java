package com.example.demo.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Forecast;
import com.example.demo.entity.LifelineDisaster;
import com.example.demo.mapper.ForecastMapper;
import com.example.demo.utility.MyJSONObject;
import com.example.demo.utility.ResultCode;
import com.example.demo.vo.DataVO;
import com.example.demo.vo.ForecastVO;
import com.example.demo.vo.LifelineDisasterVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ForecastService {

    @Autowired
    private ForecastMapper forecastMapper;

    //获取某个震情下的数据，给个id，还要能分页，根据地理位置查询
    public DataVO<Forecast> getForecast( int page, int limit){
        DataVO dataVO = new DataVO();
        dataVO.setCode(0);
        dataVO.setMsg("");
        QueryWrapper<Forecast> queryWrapper = Wrappers.query();
        IPage<Forecast> forecastIPage = new Page<>(page, limit);
        IPage<Forecast> result = forecastMapper.selectPage(forecastIPage, queryWrapper);
        dataVO.setCount(result.getTotal());
        List<ForecastVO> forecastVOS=new ArrayList<>();
        //对记录数据进行处理，时间
        for (Forecast forecast: result.getRecords()) {
            ForecastVO forecastVO=new ForecastVO();
            BeanUtils.copyProperties(forecast,forecastVO);
            forecastVO.setDate(forecast.getDate().toString().substring(0,"2021-04-21 18:15:57".length()));
            forecastVOS.add(forecastVO);
        }
        dataVO.setData(forecastVOS);
        return dataVO;
    }

    //删除某一条记录
    public JSONObject deleteForecast(String id) {
        MyJSONObject myJSONObject=new MyJSONObject();
        UpdateWrapper<Forecast> forecastUpdateWrapper= Wrappers.update();
        forecastUpdateWrapper.eq("id",id);

        int re= forecastMapper.delete(forecastUpdateWrapper);
        if(re==1){
            myJSONObject.putResultCode(ResultCode.success);
            myJSONObject.putMsg("delete success");
        }
        else {
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg("forecast id does not exist");
        }
        return myJSONObject;
    }

}
