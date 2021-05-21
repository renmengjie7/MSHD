package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Disasterinfo;
import com.example.demo.entity.DistressedPeople;
import com.example.demo.mapper.DistressedPeopleMapper;
import com.example.demo.utility.MyJSONObject;
import com.example.demo.vo.DataVO;
import com.example.demo.vo.DisasterVO;
import com.example.demo.vo.DistressedPeopleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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


}
