package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Disasterinfo;
import com.example.demo.entity.Echarts;
import com.example.demo.mapper.DisasterMapper;
import com.example.demo.utility.MyJSONObject;
import com.example.demo.utility.ResultCode;
import com.example.demo.vo.DataVO;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import static com.example.demo.utility.FileOperation.*;


//基本震情上传的微服务
@Service
public class DisasterInfoService {

    @Autowired
    private DisasterMapper disasterMapper;
    @Autowired
    private ChinaAdministrtiveService chinaAdministrtiveService;

    public JSONObject disasterUpload(MultipartFile srcFile) {
        JSONObject jsonObject = new JSONObject();
        if (srcFile.isEmpty()) {
            jsonObject.put("ResultCode", ResultCode.invalid);
            jsonObject.put("msg", "no file");
            jsonObject.put("data", "none");
            return jsonObject;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(srcFile.getInputStream()));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null) result += line;
            List<Disasterinfo> disasterinfos=null;
            String filename = srcFile.getOriginalFilename();
            switch (filename.split("\\.")[1]) {
                case "json":
                    disasterinfos=jsonParse(result);
                    break;
                case "xml":
                    disasterinfos=xmlParse(result);
                    break;
                default:
                    jsonObject.put("ResultCode", ResultCode.invalid);
                    jsonObject.put("msg", "file type is invalid, only accept xml and json");
                    jsonObject.put("data", "none");
                    return jsonObject;
            }
            if (disasterinfos==null){
                jsonObject.put("ResultCode", ResultCode.exception);
                jsonObject.put("msg", "data is invalid or exist");
                jsonObject.put("data", "0");
            }
            else if (disasterinfos.size()==0){
                jsonObject.put("ResultCode", ResultCode.exception);
                jsonObject.put("msg", "no data");
                jsonObject.put("data", "0");
            }
            else {
                for (int i=0;i<disasterinfos.size();i++){
                    disasterMapper.insert(disasterinfos.get(i));
                }
                jsonObject.put("ResultCode", ResultCode.success);
                jsonObject.put("msg", "success");
                jsonObject.put("data", "0");
            }
            return jsonObject;
        } catch (IOException e) {
            e.printStackTrace();
            jsonObject.put("ResultCode", ResultCode.exception);
            jsonObject.put("msg", "json data is out of standard");
            jsonObject.put("data", "0");
            return jsonObject;
        }
    }

    public DataVO<Disasterinfo> getDisaster(String key, Integer page, Integer limit) {
        DataVO dataVO = new DataVO();
        dataVO.setCode(0);
        dataVO.setMsg("");
        QueryWrapper<Disasterinfo> queryWrapper = Wrappers.query();
        IPage<Disasterinfo> disasterinfoIPage = new Page<>(page, limit);
        IPage<Disasterinfo> result = disasterMapper.selectPage(disasterinfoIPage, queryWrapper);
        dataVO.setCount(result.getTotal());
        dataVO.setData(result.getRecords());
        return dataVO;
    }


    //从数据库中选出未编码的震情
    public List<Disasterinfo> getDisasterNotCoded() {
        QueryWrapper<Disasterinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("d_id", "").or().isNull("d_id");
        return disasterMapper.selectList(queryWrapper);
    }

    //更新编码
    public void setCode(Disasterinfo disaster) {
        disasterMapper.updateById(disaster);
    }

    //增加
    public JSONObject addDisasterInfo(String province,String city,String country,String town,String village,String date,double longiude,double latitude,float depth,float magnitude,String reportingUnit,MultipartFile file){
        MyJSONObject myJSONObject=new MyJSONObject();
        String code = chinaAdministrtiveService.doCode(province, city, country, town, village, date);
        if (code == null) {
            myJSONObject.putMsg("the location or date is invalid");
            myJSONObject.putResultCode(ResultCode.invalid);
            //编码时数据格式不正确
        } else {
            try {
                String location=province+city+country+town+village;
                String picture="";
                Disasterinfo disasterinfo=new Disasterinfo(code,province,city,country, town, village, date,location,longiude,latitude,depth,magnitude,picture,reportingUnit);
                int re=disasterMapper.insert(disasterinfo);
                if(re==0){
                    throw new Exception();
                }
                //插入了，那就保存图片
                String dirPath="disaster";
                picture="/"+saveImg(file,dirPath,disasterinfo.getId()+"");
                if(picture==null){
                    throw new Exception();
                }
                else {
                    //存入数据库
                    disasterinfo.setPicture(picture);
                    UpdateWrapper<Disasterinfo> disasterinfoUpdateWrapper=Wrappers.update();
                    disasterinfoUpdateWrapper.eq("id",disasterinfo.getId());
                    disasterMapper.update(disasterinfo,disasterinfoUpdateWrapper);
                }
                myJSONObject.putMsg("add success");
                myJSONObject.putResultCode(ResultCode.success);
                JSONObject data=new JSONObject();
                data.put("id",disasterinfo.getId());
                myJSONObject.putData(data);
            }
            catch (Exception exception){
                myJSONObject.putMsg("exception occur");
                myJSONObject.putResultCode(ResultCode.exception);
            }
        }
        return myJSONObject;
    }

    //删除
    public JSONObject deleteDisasterInfoById(int id){
        MyJSONObject jsonObject=new MyJSONObject();

        UpdateWrapper<Disasterinfo> updateWrapper=Wrappers.update();
        updateWrapper.eq("id",id);
        QueryWrapper<Disasterinfo> queryWrapper=Wrappers.query();
        queryWrapper.eq("id",id);
        try {
            Disasterinfo disasterinfo=disasterMapper.selectOne(queryWrapper);
            int re=disasterMapper.delete(updateWrapper);
            //需要删除文件
            String path= ResourceUtils.getURL("classpath:static").getPath().replaceAll("%20"," ").substring(1).replace('/','\\');
            path= URLDecoder.decode(path,"UTF-8")+disasterinfo.getPicture();
            File file=new File(path);
            if(file.exists()){
                file.delete();
            }
            if(re==0){
                jsonObject.putResultCode(ResultCode.invalid);
                jsonObject.putMsg("the id doesn't exist");
            }
            else {
                jsonObject.putMsg("delete success");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            jsonObject.putMsg("exception occur");
            jsonObject.putResultCode(ResultCode.exception);
        }
        return jsonObject;
    }

    //修改
    public JSONObject updateDisasterInfoById(int id,String province,String city,String country,String town,String village,String date,double longiude,double latitude,float depth,float magnitude,String reportingUnit){
        MyJSONObject myJSONObject=new MyJSONObject();
        String code = chinaAdministrtiveService.doCode(province, city, country, town, village, date);

        if (code == null) {
            myJSONObject.putMsg("the location or date is invalid");
            myJSONObject.putResultCode(ResultCode.invalid);
            //编码时数据格式不正确
        } else {
            try {
                String location=province+city+country+town+village;
                String picture="";
                Disasterinfo disasterinfo=new Disasterinfo(id,code,province,city,country, town, village, date,location,longiude,latitude,depth,magnitude,picture,reportingUnit);
                UpdateWrapper<Disasterinfo> disasterinfoUpdateWrapper=Wrappers.update();
                disasterinfoUpdateWrapper.eq("id",id);
                int re=disasterMapper.update(disasterinfo,disasterinfoUpdateWrapper);
                if(re==0){
                    throw new Exception();
                }
                myJSONObject.putMsg("update success");
                myJSONObject.putResultCode(ResultCode.success);
            }
            catch (Exception exception){
                myJSONObject.putMsg("exception occur");
                myJSONObject.putResultCode(ResultCode.exception);
            }
        }
        return myJSONObject;
    }

    //统计每年发生的地震次数
    public  List<Map<String, Object>> countByYear() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("DATE_FORMAT(date,'%Y') as y,count(*) as count");
        queryWrapper.groupBy("DATE_FORMAT(date,'%Y')");
        queryWrapper.orderByAsc("y");
        List<Map<String, Object>> mapList = disasterMapper.selectMaps(queryWrapper);
        return mapList;
    }

    //统计每个省份发生的地震次数
    public  List<Map<String, Object>> countByProvince() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("province,count(*) as count");
        queryWrapper.groupBy("province");
        queryWrapper.orderByAsc("province");
        List<Map<String, Object>> mapList = disasterMapper.selectMaps(queryWrapper);
        return mapList;
    }



}