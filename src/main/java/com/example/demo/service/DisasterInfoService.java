package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Disasterinfo;
import com.example.demo.mapper.DisasterMapper;
import com.example.demo.utility.MyJSONObject;
import com.example.demo.utility.ResultCode;
import com.example.demo.vo.DataVO;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


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

    //如果数据出现经纬度、时间一样的情况，就不插入
    public Boolean existDisasterInfo(String date, String longitude, String latitude) {
        QueryWrapper<Disasterinfo> queryWrapper = Wrappers.query();
        queryWrapper.eq("date", date);
        queryWrapper.eq("longitude", longitude);
        queryWrapper.eq("latitude", latitude);
        List<Disasterinfo> disasterinfos = disasterMapper.selectList(queryWrapper);
        if (disasterinfos.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public List<Disasterinfo> jsonParse(String content) {
        List<Disasterinfo> disasterinfos = new ArrayList<>();
        //逻辑处理，存入数据库，需要判断是否重复
        org.json.JSONObject data = new org.json.JSONObject(content);
        org.json.JSONObject disasterInfo = new org.json.JSONObject(data.get("disasterInfo").toString());
        JSONArray infos = JSONArray.fromObject(disasterInfo.get("info").toString());
        for (int i = 0; i < infos.size(); i++) {
            org.json.JSONObject info = new org.json.JSONObject(infos.get(i).toString());
            if (existDisasterInfo(info.getString("date"), info.getString("longitude"), info.getString("latitude"))) {
                //相同的数据已经出现过了
                return null;
            } else {
                Disasterinfo disasterinfo = new Disasterinfo("", info.getString("province"),
                        info.getString("city"), info.getString("country"),
                        info.getString("town"), info.getString("village"),
                        info.getString("date"), info.getString("location"),
                        Double.parseDouble(info.getString("longitude")), Double.parseDouble(info.getString("latitude")),
                        Float.parseFloat(info.getString("depth")), Float.parseFloat(info.getString("magnitude")),
                        info.getString("picture"), info.getString("reportingUnit"));
                String code = chinaAdministrtiveService.doCode(disasterinfo.getProvince(), disasterinfo.getCity(), disasterinfo.getCountry(),
                        disasterinfo.getTown(), disasterinfo.getVillage(), disasterinfo.getDate());
                if (code == null) {
                    //编码时数据格式不正确
                    return null;
                } else {
                    disasterinfo.setDId(code);
                    disasterinfos.add(disasterinfo);
                }
            }
        }
        return disasterinfos;
    }

    public List<Disasterinfo> xmlParse(String content) {
        List<Disasterinfo> disasterinfos = new ArrayList<>();

        StringReader sr = new StringReader(content);
        InputSource inputSource = new InputSource(sr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputSource);

            //获取所有book节点的集合
            NodeList disasterInfoList = document.getElementsByTagName("info");
            //通过nodelist的getLength()方法可以获取bookList的长度
            //遍历每一个book节点
            for (int i = 0; i < disasterInfoList.getLength(); i++) {
                //通过 item(i)方法 获取一个book节点，nodelist的索引值从0开始
                Node disasterInfo = disasterInfoList.item(i);
                //解析子节点
                NodeList childNodes = disasterInfo.getChildNodes();
                Disasterinfo disasterinfoEntity = new Disasterinfo();
                //遍历childNodes获取每个节点的节点名和节点值
                for (int k = 0; k < childNodes.getLength(); k++) {
                    if (childNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
                        String temp = childNodes.item(k).getFirstChild().getNodeValue();
                        //区分出text类型的node以及element类型的node
                        switch (childNodes.item(k).getNodeName()) {
                            case "province":
                                disasterinfoEntity.setProvince(temp);
                                break;
                            case "city":
                                disasterinfoEntity.setCity(temp);
                                break;
                            case "country":
                                disasterinfoEntity.setCountry(temp);
                                break;
                            case "town":
                                disasterinfoEntity.setTown(temp);
                                break;
                            case "village":
                                disasterinfoEntity.setVillage(temp);
                                break;
                            case "date":
                                disasterinfoEntity.setDate(temp);
                                break;
                            case "location":
                                disasterinfoEntity.setLocation(temp);
                                break;
                            case "longitude":
                                disasterinfoEntity.setLongitude(Float.parseFloat(temp));
                                break;
                            case "latitude":
                                disasterinfoEntity.setLatitude(Float.parseFloat(temp));
                                break;
                            case "depth":
                                disasterinfoEntity.setDepth(Float.parseFloat(temp));
                                break;
                            case "magnitude":
                                disasterinfoEntity.setMagnitude(Float.parseFloat(temp));
                                break;
                            case "reportingUnit":
                                disasterinfoEntity.setReportingUnit(temp);
                                break;
                            case "picture":
                                disasterinfoEntity.setPicture(temp);
                                break;
                            default:
                                break;
                        }
                    }
                }
                if (existDisasterInfo(disasterinfoEntity.getDate(), disasterinfoEntity.getLongitude() + "", disasterinfoEntity.getLatitude() + "")) {
                    //信息已经存在
                    return null;
                } else {
                    //一体化编码
                    String code = chinaAdministrtiveService.doCode(disasterinfoEntity.getProvince(), disasterinfoEntity.getCity(), disasterinfoEntity.getCountry(),
                            disasterinfoEntity.getTown(), disasterinfoEntity.getVillage(), disasterinfoEntity.getDate());
                    if (code == null) {
                        //一体化编码出错
                        return null;
                    } else {
                        disasterinfoEntity.setDId(code);
                        disasterinfos.add(disasterinfoEntity);
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return disasterinfos;
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
    public JSONObject addDisasterInfo(String province,String city,String country,String town,String village,String date,double longiude,double latitude,float depth,float magnitude,String reportingUnit){
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
                myJSONObject.putMsg("add success");
                myJSONObject.putResultCode(ResultCode.success);
                JSONObject data=new JSONObject();
                data.put("id",re);
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
        try {
            int re=disasterMapper.delete(updateWrapper);
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
                myJSONObject.putMsg("update success");
                myJSONObject.putResultCode(ResultCode.success);
                JSONObject data=new JSONObject();
                data.put("id",re);
                myJSONObject.putData(data);
            }
            catch (Exception exception){
                myJSONObject.putMsg("exception occur");
                myJSONObject.putResultCode(ResultCode.exception);
            }
        }
        return myJSONObject;
    }


}