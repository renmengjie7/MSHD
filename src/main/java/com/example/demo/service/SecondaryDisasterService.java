package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.LifelineDisaster;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SecondaryDisasterService {

    @Autowired
    private SecondaryDisasterMapper secondaryDisasterMapper;
    @Autowired
    private ChinaAdministrtiveService chinaAdministrtiveService;
    
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

    //增加
    public JSONObject addSecondaryDisaster(String province, String city, String country, String town, String village, String date, String note, int category, int status, int type, String picture, String reportingUnit, String earthquakeId) {
        MyJSONObject myJSONObject=new MyJSONObject();
        String location=province+city+country+town+village;
        location=location.replaceAll("null","");
        Timestamp timestamp=null;
        try{
            timestamp=Timestamp.valueOf(date);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //进行生命线灾情编码
        SecondaryDisaster secondaryDisaster=new SecondaryDisaster(province,city,country,town,village,category,status);
        Map<String,String> map=chinaAdministrtiveService.doSecondaryDisasterCode(secondaryDisaster);
        if(map.get("resCode")=="0"){
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg(map.get("msg"));
        }
        else{
            try {
                secondaryDisaster.setSecondaryId(map.get("msg"));
                secondaryDisaster.setLocation(location);
                secondaryDisaster.setDate(timestamp);
                secondaryDisaster.setReportingUnit(reportingUnit);
                secondaryDisaster.setEarthquakeId(earthquakeId);
                secondaryDisaster.setNote(note);
                secondaryDisaster.setType(type);
                secondaryDisaster.setPicture(picture);
                if((secondaryDisasterMapper.insert(secondaryDisaster))==0){
                    myJSONObject.putMsg("add secondary disaster info fail");
                    myJSONObject.putResultCode(ResultCode.exception);
                }
                else {
                    myJSONObject.putMsg("add secondary disaster info success");
                    myJSONObject.putResultCode(ResultCode.success);
                }
            }catch (Exception e){
                myJSONObject.putResultCode(ResultCode.exception);
                myJSONObject.putMsg(e.toString());
            }
        }
        return myJSONObject;
    }

    //更新
    public JSONObject updateSecondaryDisaster(int id, String province, String city, String country, String town, String village, String date, String note, int category, int status, int type, String picture, String reportingUnit, String earthquakeId) {
        MyJSONObject myJSONObject=new MyJSONObject();
        //根据ID判断数据表中是否存在该条数据
        SecondaryDisaster secondaryDisaster=secondaryDisasterMapper.selectById(id);
        if(secondaryDisaster==null){
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg("can not find secondary disaster data with the id");
            return myJSONObject;
        }
        //对更新后的灾害信息进行编码
        secondaryDisaster=new SecondaryDisaster(province,city,country,town,village,category,status);
        Map<String,String> map=chinaAdministrtiveService.doSecondaryDisasterCode(secondaryDisaster);
        if(map.get("resCode")=="0"){
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg(map.get("msg"));
        }
        else{
            Timestamp timestamp=null;
            try{
                timestamp=Timestamp.valueOf(date);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            String location=province+city+country+town+village;
            location=location.replaceAll("null","");
            secondaryDisaster.setSecondaryId(map.get("msg"));
            secondaryDisaster.setLocation(location);
            secondaryDisaster.setDate(timestamp);
            secondaryDisaster.setReportingUnit(reportingUnit);
            secondaryDisaster.setEarthquakeId(earthquakeId);
            secondaryDisaster.setNote(note);
            secondaryDisaster.setType(type);
            secondaryDisaster.setPicture(picture);
            UpdateWrapper updateWrapper=new UpdateWrapper();
            updateWrapper.eq("id",id);
            if(secondaryDisasterMapper.update(secondaryDisaster,updateWrapper)==0)
            {
                myJSONObject.putResultCode(ResultCode.exception);
                myJSONObject.putMsg("update fail");
            }
            else{
                myJSONObject.putResultCode(ResultCode.success);
                myJSONObject.putMsg("update success");
            }
        }
        return myJSONObject;
    }
}
