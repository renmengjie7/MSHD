package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.DistressedPeople;
import com.example.demo.vo.Echarts;
import com.example.demo.mapper.DistressedPeopleMapper;
import com.example.demo.vo.DataVO;
import com.example.demo.vo.DistressedPeopleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DistressedPeopleService {

    @Autowired
    private DistressedPeopleMapper distressedPeopleMapper;

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
}
