package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.DistressedPeople;
import com.example.demo.entity.Forecast;
import com.example.demo.entity.LifelineDisaster;
import com.example.demo.mapper.LifelineDisasterMapper;
import com.example.demo.utility.MyJSONObject;
import com.example.demo.utility.ResultCode;
import com.example.demo.vo.LifelineDisasterVO;
import com.example.demo.vo.DataVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LifelineDisasterService{
    String dirPath = "lifelineDisaster";

    @Autowired
    private FileOperation fileOperation;
    @Autowired
    private LifelineDisasterMapper lifelineDisasterMapper;
    @Autowired
    private ChinaAdministrtiveService chinaAdministrtiveService;

    //获取某个震情下的数据，给个id，还要能分页，根据地理位置查询
    public DataVO<LifelineDisaster> getLifelineDisasterByEarthquakeId(String earthquakeId, int category, int page, int limit){
        DataVO dataVO = new DataVO();
        dataVO.setCode(0);
        dataVO.setMsg("");
        QueryWrapper<LifelineDisaster> queryWrapper = Wrappers.query();
        queryWrapper.eq("earthquake_id",earthquakeId);
        if(category!=-1){
            queryWrapper.eq("category",category);
        }
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

    //增加一条记录
    public JSONObject addLifeLineDisaster(String province, String city, String country, String town, String village, String date, String note, int category, int grade, int type, MultipartFile file, String reportingUnit, String earthquakeId) {
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
        LifelineDisaster lifelineDisaster=new LifelineDisaster(province,city,country,town,village,category,grade);
        Map<String,String> map=chinaAdministrtiveService.doLifelineDisasterCode(lifelineDisaster);
        if(map.get("resCode")=="0"){
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg(map.get("msg"));
        }
        else{
            try {
                String picture="";
                lifelineDisaster.setLifelineId(map.get("msg"));
                lifelineDisaster.setLocation(location);
                lifelineDisaster.setDate(timestamp);
                lifelineDisaster.setReportingUnit(reportingUnit);
                lifelineDisaster.setEarthquakeId(earthquakeId);
                lifelineDisaster.setNote(note);
                lifelineDisaster.setType(type);
                lifelineDisaster.setPicture(picture);
                if((lifelineDisasterMapper.insert(lifelineDisaster))==0){
                    myJSONObject.putMsg("add lifeline disaster info fail");
                    myJSONObject.putResultCode(ResultCode.exception);
                }
                else {
                    //插入成功
                    String fileName = file.getOriginalFilename();
                    fileName=fileName.substring(0,fileName.lastIndexOf("."));;
                    picture = "/" + fileOperation.saveImg(file, dirPath, fileName);
                    System.out.printf("\n"+picture.split("/"+dirPath+"/")[1]);
                    System.out.printf("\n"+picture);
                    if (picture == null) {
                        throw new Exception();
                    } else {
                        //存入数据库
                        lifelineDisaster.setPicture(picture.split("/"+dirPath+"/")[1]);
                        UpdateWrapper<LifelineDisaster> lifelineDisasterUpdateWrapper = Wrappers.update();
                        lifelineDisasterUpdateWrapper.eq("id", lifelineDisaster.getId());
                        lifelineDisasterMapper.update(lifelineDisaster, lifelineDisasterUpdateWrapper);
                    }
                    myJSONObject.putMsg("add lifeline disaster info success");
                    myJSONObject.putResultCode(ResultCode.success);
                }
            }catch (Exception e){
                myJSONObject.putResultCode(ResultCode.exception);
                myJSONObject.putMsg(e.toString());
            }
        }
        return myJSONObject;
    }

    //更新生命线灾情数据
    public JSONObject updateLifeLineDisaster(int id, String province, String city, String country, String town, String village, String date, String note, int category, int grade, int type, MultipartFile file, String reportingUnit, String earthquakeId) throws Exception {
        MyJSONObject myJSONObject=new MyJSONObject();
        //根据ID判断数据表中是否存在该条数据
        LifelineDisaster lifelineDisaster=lifelineDisasterMapper.selectById(id);
        if(lifelineDisaster==null){
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg("can not find lifeline disaster data with the id");
            return myJSONObject;
        }
        //对更新后的灾害信息进行编码
        lifelineDisaster=new LifelineDisaster(province,city,country,town,village,category,grade);
        Map<String,String> map=chinaAdministrtiveService.doLifelineDisasterCode(lifelineDisaster);
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
            lifelineDisaster.setLifelineId(map.get("msg"));
            lifelineDisaster.setLocation(location);
            lifelineDisaster.setDate(timestamp);
            lifelineDisaster.setReportingUnit(reportingUnit);
            lifelineDisaster.setEarthquakeId(earthquakeId);
            lifelineDisaster.setNote(note);
            lifelineDisaster.setType(type);

            String picture="";
            String fileName="";
            if (file!=null&&!file.isEmpty()) {
                if(lifelineDisaster.getPicture()==null||lifelineDisaster.getPicture()==""){//原来不存在图片
                    fileName = file.getOriginalFilename();
                    fileName=fileName.substring(0,fileName.lastIndexOf("."));;
                    System.out.printf("\nfilename="+fileName);
                }
                else {
                    //删除原来的文件，保存现在的文件
                    fileOperation.deleteImg(dirPath + "/" + lifelineDisaster.getPicture());
                    fileName=lifelineDisaster.getPicture();
                }
                picture = "/" + fileOperation.saveImg(file, dirPath, fileName);
                System.out.printf("\n"+picture.split("/"+dirPath+"/")[1]);
                System.out.printf("\n"+picture);
                if (picture == null) {
                    throw new Exception();
                }
                lifelineDisaster.setPicture(picture.split("/"+dirPath+"/")[1]);
            }
            UpdateWrapper updateWrapper=new UpdateWrapper();
            updateWrapper.eq("id",id);
            if(lifelineDisasterMapper.update(lifelineDisaster,updateWrapper)==0)
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
