package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Disasterinfo;
import com.example.demo.mapper.DisasterMapper;
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
import java.util.List;


//基本震情上传的微服务
@Service
public class DisasterInfoService {

    @Autowired
    private DisasterMapper disasterMapper;
    @Autowired
    private ChinaAdministrtiveService chinaAdministrtiveService;

    public JSONObject disasterUpload(MultipartFile srcFile){
        JSONObject jsonObject=new JSONObject();
        if(srcFile.isEmpty()){
            jsonObject.put("ResultCode", ResultCode.invalid);
            jsonObject.put("msg","no file");
            jsonObject.put("data","none");
            return jsonObject;
        }
        try {
            BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(srcFile.getInputStream()));
            String line;
            String result = "";
            while((line=bufferedReader.readLine()) != null ) result+=line;
            String filename=srcFile.getOriginalFilename();
            switch (filename.split("\\.")[1]){
                case "json":
                    return jsonParse(result);
                case "xml":
                    return xmlParse(result);
                default:
                    jsonObject.put("ResultCode", ResultCode.invalid);
                    jsonObject.put("msg","file type is invalid, only accept xml and json");
                    jsonObject.put("data","none");
                    return jsonObject;
            }
        } catch (IOException e) {
            e.printStackTrace();
            jsonObject.put("ResultCode", ResultCode.exception);
            jsonObject.put("msg","json data is out of standard");
            jsonObject.put("data","0");
            return jsonObject;
        }
    }

//    如果数据出现经纬度、时间一样的情况，就不插入
    public Boolean existDisasterInfo(String date,String longitude,String latitude){
        QueryWrapper<Disasterinfo> queryWrapper = Wrappers.query();
        queryWrapper.eq("date",date);
        queryWrapper.eq("longitude",longitude);
        queryWrapper.eq("latitude",latitude);
        List<Disasterinfo> disasterinfos=disasterMapper.selectList(queryWrapper);
        if(disasterinfos.size()==0){
            return false;
        }
        else {
            return true;
        }
    }

    public JSONObject jsonParse(String content) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ResultCode", ResultCode.success);
            jsonObject.put("msg", "json file parse success");
            jsonObject.put("data", "1");
            //            逻辑处理，存入数据库，需要判断是否重复
            org.json.JSONObject data = new org.json.JSONObject(content);
            org.json.JSONObject disasterInfo = new org.json.JSONObject(data.get("disasterInfo").toString());
            JSONArray infos = JSONArray.fromObject(disasterInfo.get("info").toString());
            for (int i = 0; i < infos.size(); i++) {
                org.json.JSONObject info = new org.json.JSONObject(infos.get(i).toString());
                if(existDisasterInfo(info.getString("date"),info.getString("longitude"),info.getString("latitude"))){
                    jsonObject.put("msg","some info exist");
                }
                else {
                    Disasterinfo disasterinfo=new Disasterinfo("", info.getString("province"),
                            info.getString("city"), info.getString("country"),
                            info.getString("town"), info.getString("village"),
                            info.getString("date"), info.getString("location"),
                            Double.parseDouble(info.getString("longitude")), Double.parseDouble(info.getString("latitude")),
                            Float.parseFloat(info.getString("depth")), Float.parseFloat(info.getString("magnitude")),
                            info.getString("picture"), info.getString("reportingUnit"));
                    String code=chinaAdministrtiveService.doCode(disasterinfo.getProvince(),disasterinfo.getCity(),disasterinfo.getCountry(),
                            disasterinfo.getTown(),disasterinfo.getVillage(),disasterinfo.getDate());
                    if(code==null){
                        throw new Exception();
                    }
                    else {
                        disasterinfo.setDId(code);
                        disasterMapper.insert(disasterinfo);
                    }
                }
            }
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("ResultCode", ResultCode.exception);
            jsonObject.put("msg", "json data is out of standard");
            jsonObject.put("data", "0");
            return jsonObject;
        }
    }

    public JSONObject xmlParse(String content){
        JSONObject jsonObject=new JSONObject();
        StringReader sr = new StringReader(content);
        InputSource inputSource = new InputSource(sr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder= null;
        try {
            jsonObject.put("ResultCode", ResultCode.success);
            jsonObject.put("msg", "json file parse success");
            jsonObject.put("data", "1");
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
                Disasterinfo disasterinfoEntity=new Disasterinfo();
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
                if(existDisasterInfo(disasterinfoEntity.getDate(),disasterinfoEntity.getLongitude()+"",disasterinfoEntity.getLatitude()+"")){
                    jsonObject.put("msg","some info exist");
                }
                else {
                    //一体化编码
                    String code=chinaAdministrtiveService.doCode(disasterinfoEntity.getProvince(),disasterinfoEntity.getCity(),disasterinfoEntity.getCountry(),
                            disasterinfoEntity.getTown(),disasterinfoEntity.getVillage(),disasterinfoEntity.getDate());
                    if(code==null){
                        throw new Exception();
                    }
                    else {
                        disasterinfoEntity.setDId(code);
                        disasterMapper.insert(disasterinfoEntity);
                    }
                }
            }
            jsonObject.put("ResultCode", ResultCode.success);
            jsonObject.put("msg", "xml file parse success");
            jsonObject.put("data", "1");
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("ResultCode", ResultCode.exception);
            jsonObject.put("msg", "json data is out of standard");
            jsonObject.put("data", "0");
            return jsonObject;
        }
    }

    public DataVO<Disasterinfo> getDisaster(String key, Integer page, Integer limit){
        DataVO dataVO=new DataVO();
        dataVO.setCode(0);
        dataVO.setMsg("");
        QueryWrapper<Disasterinfo> queryWrapper = Wrappers.query();
        IPage<Disasterinfo> disasterinfoIPage=new Page<>(page,limit);
        IPage<Disasterinfo> result=disasterMapper.selectPage(disasterinfoIPage,queryWrapper);
        dataVO.setCount(result.getTotal());
        dataVO.setData(result.getRecords());
        return dataVO;
    }

    //从数据库中选出未编码的震情
    public List<Disasterinfo> getDisasterNotCoded() {
        QueryWrapper<Disasterinfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("d_id","").or().isNull("d_id");

        return disasterMapper.selectList(queryWrapper);
    }

    //更新编码
    public void setCode(Disasterinfo disaster) {
        disasterMapper.updateById(disaster);
    }

}
