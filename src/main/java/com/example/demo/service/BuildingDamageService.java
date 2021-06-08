package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.BuildingDamage;
import com.example.demo.entity.LifelineDisaster;
import com.example.demo.mapper.BuildingDamageMapper;
import com.example.demo.utility.MyJSONObject;
import com.example.demo.utility.ResultCode;
import com.example.demo.vo.BuildingDamageVO;
import com.example.demo.vo.DataVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BuildingDamageService {

    @Autowired
    private BuildingDamageMapper buildingDamageMapper;

    @Autowired
    private ChinaAdministrtiveService chinaAdministrtiveService;

    //获取某个震情下的数据，给个id，还要能分页，根据地理位置查询
    public DataVO<BuildingDamage> getBuildingDamageByEarthquakeId(String earthquakeId, int category, int page, int limit){
        DataVO dataVO = new DataVO();
        dataVO.setCode(0);
        dataVO.setMsg("");
        QueryWrapper<BuildingDamage> queryWrapper = Wrappers.query();
        queryWrapper.eq("earthquake_id",earthquakeId);
        if(category!=-1){
            queryWrapper.eq("category",category);
        }
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

    //增加
    public JSONObject addBuildingDamage(String province, String city, String country, String town, String village, String date, String note, int category, Double basicallyIntactSquare, Double damagedSquare, Double destroyedSquare, String reportingUnit, String earthquakeId, Double slightDamagedSquare,Double moderateDamagedSquare, Double seriousDamagedSquare) {
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
        destroyedSquare=(destroyedSquare==null?0:(double)destroyedSquare);
        damagedSquare=(damagedSquare==null?0:(double)damagedSquare);
        BuildingDamage buildingDamage=new BuildingDamage(province,city,country,town,village,category,destroyedSquare,damagedSquare,earthquakeId);
        Map<String,String> map=chinaAdministrtiveService.doBuildingDamageCode(buildingDamage);
        if(map.get("resCode")=="0"){
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg(map.get("msg"));
        }
        else{
            try {
                basicallyIntactSquare=(basicallyIntactSquare==null?0:(double)basicallyIntactSquare);
                slightDamagedSquare=(slightDamagedSquare==null?0:(double)slightDamagedSquare);
                moderateDamagedSquare=(moderateDamagedSquare==null?0:(double)moderateDamagedSquare);
                seriousDamagedSquare=(seriousDamagedSquare==null?0:(double)seriousDamagedSquare);
                buildingDamage.setBasicallyIntactSquare(basicallyIntactSquare);
                buildingDamage.setSlightDamagedSquare(slightDamagedSquare);
                buildingDamage.setModerateDamagedSquare(moderateDamagedSquare);
                buildingDamage.setSeriousDamagedSquare(seriousDamagedSquare);
                buildingDamage.setBuildingDamageId(map.get("msg"));
                buildingDamage.setLocation(location);
                buildingDamage.setDate(timestamp);
                buildingDamage.setReportingUnit(reportingUnit);
                buildingDamage.setEarthquakeId(earthquakeId);
                buildingDamage.setNote(note);
                if((buildingDamageMapper.insert(buildingDamage))==0){
                    myJSONObject.putMsg("add buildingDamage disaster info fail");
                    myJSONObject.putResultCode(ResultCode.exception);
                }
                else {
                    myJSONObject.putMsg("add buildingDamage disaster info success");
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
    public JSONObject updateBuildingDamage(int id,String province, String city, String country, String town, String village, String date, String note, int category, Double basicallyIntactSquare, Double damagedSquare, Double destroyedSquare, String reportingUnit, String earthquakeId, Double slightDamagedSquare, Double moderateDamagedSquare, Double seriousDamagedSquare) {
        MyJSONObject myJSONObject=new MyJSONObject();
        //根据ID判断数据表中是否存在该条数据
        BuildingDamage buildingDamage=buildingDamageMapper.selectById(id);
        if(buildingDamage==null){
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg("can not find buildingDamage disaster data with the id");
            return myJSONObject;
        }
        //对更新后的灾害信息进行编码
        destroyedSquare=(destroyedSquare==null?0:(double)destroyedSquare);
        damagedSquare=(damagedSquare==null?0:(double)damagedSquare);
        buildingDamage=new BuildingDamage(province,city,country,town,village,category,destroyedSquare,damagedSquare,earthquakeId);
        Map<String,String> map=chinaAdministrtiveService.doBuildingDamageCode(buildingDamage);
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
            basicallyIntactSquare=(basicallyIntactSquare==null?0:(double)basicallyIntactSquare);
            slightDamagedSquare=(slightDamagedSquare==null?0:(double)slightDamagedSquare);
            moderateDamagedSquare=(moderateDamagedSquare==null?0:(double)moderateDamagedSquare);
            seriousDamagedSquare=(seriousDamagedSquare==null?0:(double)seriousDamagedSquare);
            buildingDamage.setBasicallyIntactSquare(basicallyIntactSquare);
            buildingDamage.setSlightDamagedSquare(slightDamagedSquare);
            buildingDamage.setModerateDamagedSquare(moderateDamagedSquare);
            buildingDamage.setSeriousDamagedSquare(seriousDamagedSquare);
            buildingDamage.setBuildingDamageId(map.get("msg"));
            buildingDamage.setLocation(location);
            buildingDamage.setDate(timestamp);
            buildingDamage.setReportingUnit(reportingUnit);
            buildingDamage.setEarthquakeId(earthquakeId);
            buildingDamage.setNote(note);
            UpdateWrapper updateWrapper=new UpdateWrapper();
            updateWrapper.eq("id",id);
            if(buildingDamageMapper.update(buildingDamage,updateWrapper)==0)
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
