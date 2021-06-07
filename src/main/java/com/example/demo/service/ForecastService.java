package com.example.demo.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Disasterinfo;
import com.example.demo.entity.Forecast;
import com.example.demo.entity.LifelineDisaster;
import com.example.demo.entity.SecondaryDisaster;
import com.example.demo.mapper.ForecastMapper;
import com.example.demo.utility.MyJSONObject;
import com.example.demo.utility.ResultCode;
import com.example.demo.vo.DataVO;
import com.example.demo.vo.ForecastVO;
import com.example.demo.vo.LifelineDisasterVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ForecastService {
    String dirPath = "forecast";

    @Autowired
    private FileOperation fileOperation;

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

    //增加
    public JSONObject addForecast(String date, int grade, int intensity, int type, MultipartFile file) {
        MyJSONObject myJSONObject=new MyJSONObject();
        Timestamp timestamp=null;
        try{
            timestamp=Timestamp.valueOf(date);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        String picture="";
        Forecast forecast=new Forecast(timestamp,grade,intensity,type,picture);
        try {
            if((forecastMapper.insert(forecast))==0){
                myJSONObject.putMsg("add forecast info fail");
                myJSONObject.putResultCode(ResultCode.exception);
            }
            else {
                //插入了，那就保存图片
                picture = "/" + fileOperation.saveImg(file, dirPath, forecast.getId() + "");
                if (picture == null) {
                    throw new Exception();
                } else {
                    //存入数据库
                    forecast.setPicture(picture.split("/"+dirPath+"/")[1]);
                    UpdateWrapper<Forecast> forecastUpdateWrapper = Wrappers.update();
                    forecastUpdateWrapper.eq("id", forecast.getId());
                    forecastMapper.update(forecast, forecastUpdateWrapper);
                }
                myJSONObject.putMsg("add forecast info success");
                myJSONObject.putResultCode(ResultCode.success);
            }
        }catch (Exception e){
            myJSONObject.putResultCode(ResultCode.exception);
            myJSONObject.putMsg(e.toString());
        }
        return myJSONObject;
    }

    //更新
    public JSONObject updateForecast(int id, String date, int grade, int intensity, int type, MultipartFile file) throws Exception {
        MyJSONObject myJSONObject=new MyJSONObject();
        //根据ID判断数据表中是否存在该条数据
        Forecast forecast=forecastMapper.selectById(id);
        if(forecast==null){
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg("can not find forecast data with the id");
            return myJSONObject;
        }
        Timestamp timestamp=null;
        try{
            timestamp=Timestamp.valueOf(date);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String picture="";
        forecast=new Forecast(timestamp,grade,intensity,type,picture);
        if (file!=null&&!file.isEmpty()) {
            //删除原来的文件，保存现在的文件
            fileOperation.deleteImg(dirPath+"/"+forecast.getPicture());
            picture = "/" + fileOperation.saveImg(file, dirPath, forecast.getPicture());
            if (picture == null) {
                throw new Exception();
            }
            forecast.setPicture(picture.split("/"+dirPath+"/")[1]);
        }
        UpdateWrapper updateWrapper=new UpdateWrapper();
        updateWrapper.eq("id",id);
        if(forecastMapper.update(forecast,updateWrapper)==0)
        {
            myJSONObject.putResultCode(ResultCode.exception);
            myJSONObject.putMsg("update fail");
        }
        else{
            myJSONObject.putResultCode(ResultCode.success);
            myJSONObject.putMsg("update success");
        }
        return myJSONObject;
    }
}
