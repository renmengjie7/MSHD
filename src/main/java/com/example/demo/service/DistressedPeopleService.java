package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Disasterinfo;
import com.example.demo.entity.DistressedPeople;
import com.example.demo.mapper.DisasterMapper;
import com.example.demo.utility.MyJSONObject;
import com.example.demo.utility.ResultCode;
import com.example.demo.vo.Echarts;
import com.example.demo.mapper.DistressedPeopleMapper;
import com.example.demo.vo.DataVO;
import com.example.demo.vo.DistressedPeopleVO;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.sound.midi.Soundbank;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DistressedPeopleService {

    @Autowired
    private DistressedPeopleMapper distressedPeopleMapper;
    @Autowired
    private DisasterMapper disasterMapper;
    @Autowired
    private ChinaAdministrtiveService chinaAdministrtiveService;

    //获取某个震情下的数据，给个id，还要能分页，根据地理位置查询
    public DataVO<DistressedPeople> getDistressedPeopleByEarthquakeId(String earthquakeId, String key, int page, int limit){
        DataVO dataVO = new DataVO();
        dataVO.setCode(0);
        dataVO.setMsg("");

        QueryWrapper<DistressedPeople> queryWrapper = Wrappers.query();
        queryWrapper.eq("earthquake_id",earthquakeId)
                .like("location",key);
        IPage<DistressedPeople> distressedPeopleIPage = new Page<>(page, limit);
        IPage<DistressedPeople> result = distressedPeopleMapper.selectPage(distressedPeopleIPage, queryWrapper);
        dataVO.setCount(result.getTotal());
        List<DistressedPeopleVO> distressedPeopleVOList=new ArrayList<>();
        //对记录数据进行处理，时间
        for (DistressedPeople distressedPeople: result.getRecords()) {
            DistressedPeopleVO distressedPeopleVO=new DistressedPeopleVO();
            BeanUtils.copyProperties(distressedPeople,distressedPeopleVO);
            distressedPeopleVO.setDate(distressedPeople.getDate().toString().substring(0,"2021-04-21 18:15:57".length()));
            distressedPeopleVOList.add(distressedPeopleVO);
        }
        dataVO.setData(distressedPeopleVOList);
        return dataVO;
    }

    //根据震情编码获取人员死亡、受伤、失踪的百分比
    public DataVO<Echarts> getDistressedPeoplePercentage(String earthquakeId) {
        DataVO dataVO = new DataVO();
        dataVO.setCode(0);
        dataVO.setMsg("");
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("category,sum(number) as count");
        queryWrapper.eq("earthquake_id",earthquakeId);
        queryWrapper.groupBy("category");
        queryWrapper.orderByAsc("category");
        List<Map<String, Object>> mapList = distressedPeopleMapper.selectMaps(queryWrapper);
        dataVO.setCount((long) mapList.size());
        if(mapList.isEmpty())
        {
            dataVO.setCode(2);
            dataVO.setMsg("could not find distress with the earthquakeId");
        }
        else {
            List<Echarts> list = new ArrayList<Echarts>();
            String name;
            for (Map<String, Object> map : mapList) {
                if(map.get("category").equals(0))
                    name="死亡";
                else if(map.get("category").equals(1))
                    name="受伤";
                else name="失踪";
                list.add(new Echarts(name, ((BigDecimal) map.get("count")).intValue()));
            }
            dataVO.setData(list);
        }
        return dataVO;

    }

    //添加人员伤亡
    public JSONObject addDistressedPeople(String province, String city, String country, String town, String village, String datetime, int number, int category, String reportingUnit, String earthquakeId) {
        MyJSONObject myJSONObject=new MyJSONObject();
        String location=province+city+country+town+village;
        location=location.replaceAll("null","");
        Timestamp timestamp=null;
        try{
            timestamp=Timestamp.valueOf(datetime);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //进行灾情编码
        DistressedPeople distressedPeople=new DistressedPeople(province,city,country,town,village,number,category,earthquakeId);
        Map<String,String> map=chinaAdministrtiveService.doDistressedPeopleCode(distressedPeople);
        if(map.get("resCode")=="0"){
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg(map.get("msg"));
        }
        else{
            try {
                distressedPeople.setPeopleId(map.get("msg"));
                distressedPeople.setLocation(location);
                distressedPeople.setDate(timestamp);
                distressedPeople.setReportingUnit(reportingUnit);
                distressedPeople.setEarthquakeId(earthquakeId);
                if((distressedPeopleMapper.insert(distressedPeople))==0){
                    myJSONObject.putMsg("add distressed people info fail");
                    myJSONObject.putResultCode(ResultCode.exception);
                }
                else {
                    myJSONObject.putMsg("add distressed people info success");
                    myJSONObject.putResultCode(ResultCode.success);
                }
            }catch (Exception e){
                myJSONObject.putResultCode(ResultCode.exception);
                myJSONObject.putMsg(e.toString());
            }
        }
        return myJSONObject;
    }

    //删除某一条记录
    public JSONObject deleteDistressedPeopleById(String id) {
        MyJSONObject myJSONObject=new MyJSONObject();
        UpdateWrapper<DistressedPeople> distressedPeopleUpdateWrapper=Wrappers.update();
        distressedPeopleUpdateWrapper.eq("id",id);
        int re=distressedPeopleMapper.delete(distressedPeopleUpdateWrapper);
        if(re==1){
            myJSONObject.putResultCode(ResultCode.success);
            myJSONObject.putMsg("delete success");
        }
        else {
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg("DistressedPeople id does not exist");
        }
        return myJSONObject;
    }

    //修改人员伤亡记录
    public JSONObject updateDistressedPeople(int id,String province, String city, String country, String town, String village, String datetime, int number, int category, String reportingUnit, String earthquakeId) {
        MyJSONObject myJSONObject=new MyJSONObject();
        //根据ID判断数据表中是否存在该条数据
        DistressedPeople distressedPeople=distressedPeopleMapper.selectById(id);
        if(distressedPeople==null){
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg("can not find Distressed People data with the id");
            return myJSONObject;
        }
        //对更新后的灾害信息进行编码
        distressedPeople=new DistressedPeople(province,city,country,town,village,number,category,earthquakeId);
        Map<String,String> map=chinaAdministrtiveService.doDistressedPeopleCode(distressedPeople);
        if(map.get("resCode")=="0"){
            myJSONObject.putResultCode(ResultCode.invalid);
            myJSONObject.putMsg(map.get("msg"));
        }
        else{
            Timestamp timestamp=null;
            try{
                timestamp=Timestamp.valueOf(datetime);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            String location=province+city+country+town+village;
            location=location.replaceAll("null","");
            distressedPeople.setPeopleId(map.get("msg"));
            distressedPeople.setLocation(location);
            distressedPeople.setDate(timestamp);
            distressedPeople.setReportingUnit(reportingUnit);
            distressedPeople.setEarthquakeId(earthquakeId);
            UpdateWrapper updateWrapper=new UpdateWrapper();
            updateWrapper.eq("id",id);
            if(distressedPeopleMapper.update(distressedPeople,updateWrapper)==0)
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

    //将灾情信息按照地区划分统计伤亡总人数
    public DataVO<Echarts> getDistressedPeopleCountDividedByLocation(String earthquakeId) {
        DataVO dataVO = new DataVO();
        dataVO.setCount((long) 0);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("d_id",earthquakeId);
        Disasterinfo disasterinfo=disasterMapper.selectOne(queryWrapper);
        //是否存在该earthquakeId对应的震情信息
        if(disasterinfo==null){
            dataVO.setCode(ResultCode.invalid);
            dataVO.setMsg("can not find disaster information with this earthquakeId");
        }
        else{
            //获取该震情地理位置的下一级
            String nextLocationName="village";
            if(disasterinfo.getTown()==null||disasterinfo.getTown().equals(""))
                nextLocationName = "town";
            if(disasterinfo.getCountry()==null||disasterinfo.getCountry().equals(""))
                nextLocationName="country";
            if(disasterinfo.getCity()==null||disasterinfo.getCity().equals(""))
                nextLocationName="city";

            try {
                queryWrapper = new QueryWrapper();
                queryWrapper.select(nextLocationName + ",sum(number) as count");
                queryWrapper.eq("earthquake_id", earthquakeId);
                queryWrapper.groupBy(nextLocationName);
                queryWrapper.orderByAsc(nextLocationName);
                List<Map<String, Object>> mapList = distressedPeopleMapper.selectMaps(queryWrapper);
                List<Echarts> list = new ArrayList<Echarts>();
                int numberOfNull=0;
                for (Map<String, Object> map : mapList) {
                    if(map.get(nextLocationName)==null||map.get(nextLocationName).equals("")){
                        numberOfNull+=((BigDecimal) map.get("count")).intValue();
                    }
                    else {
                        list.add(new Echarts((String) map.get(nextLocationName), ((BigDecimal) map.get("count")).intValue()));
                    }
                }
                //灾情信息中不存在下一级地理位置
                if(list.isEmpty()){
                    list.add(new Echarts(disasterinfo.getLocation(), numberOfNull));
                    dataVO.setMsg("no location in DistressedPeople lower than disaster location");
                }
                dataVO.setData(list);
                dataVO.setCode(ResultCode.success);
                dataVO.setCount((long) list.size());
            }catch (Exception e){
                dataVO.setCode(ResultCode.exception);
                dataVO.setMsg(e.toString());
            }
        }
        return dataVO;
    }
}
