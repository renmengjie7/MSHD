package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Disasterinfo;
import com.example.demo.mapper.DisasterMapper;
import com.example.demo.utility.ResultCode;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;


//基本震情上传的微服务
@Service
public class DisasterInfoService {

    @Autowired
    private DisasterMapper disasterMapper;

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
            System.out.println(filename);
            System.out.println(filename.split("\\.")[0]);
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

    public JSONObject jsonParse(String content) {
        JSONObject jsonObject = new JSONObject();
        try {
            //            逻辑处理，存入数据库，需要判断是否重复
            org.json.JSONObject data = new org.json.JSONObject(content);
            org.json.JSONObject disasterInfo = new org.json.JSONObject(data.get("disasterInfo").toString());
            JSONArray infos = JSONArray.fromObject(disasterInfo.get("info").toString());
            for (int i = 0; i < infos.size(); i++) {
                org.json.JSONObject info = new org.json.JSONObject(infos.get(i).toString());
                int r = disasterMapper.insert(new Disasterinfo(
                        "501", "", info.getString("province"),
                        info.getString("city"), info.getString("country"),
                        info.getString("town"), info.getString("village"),
                        info.getString("date"), info.getString("location"),
                        Float.parseFloat(info.getString("longitude")), Float.parseFloat(info.getString("latitude")),
                        Float.parseFloat(info.getString("depth")), Float.parseFloat(info.getString("magnitude")),
                        info.getString("picture"), info.getString("reportingUnit")));
                this.encodeDisasterInfo(r);
            }
            jsonObject.put("ResultCode", ResultCode.success);
            jsonObject.put("msg", "json file parse success");
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

    public JSONObject xmlParse(String content){
        JSONObject jsonObject=new JSONObject();
        StringReader sr = new StringReader(content);
        InputSource inputSource = new InputSource(sr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder= null;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputSource);

            //获取所有book节点的集合
            NodeList disasterInfoList = document.getElementsByTagName("info");
            //通过nodelist的getLength()方法可以获取bookList的长度
            System.out.println("一共有" + disasterInfoList.getLength() + "个灾情");
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
                disasterMapper.insert(disasterinfoEntity);
                System.out.println(disasterinfoEntity);
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

    public JSONObject getDisaster(String key, Integer page, Integer limit){
        JSONObject jsonObject=new JSONObject();

        QueryWrapper<Disasterinfo> queryWrapper = Wrappers.query();
        IPage<Disasterinfo> disasterinfoIPage=new Page<>(page,limit);
        IPage<Disasterinfo> result=disasterMapper.selectPage(disasterinfoIPage,queryWrapper);

        JSONObject data=new JSONObject();
        data.put("count",result.getTotal());
        data.put("disasterinfos",result.getRecords());
        data.put("pageNumber",result.getPages());

        jsonObject.put("ResultCode", ResultCode.success);
        jsonObject.put("msg","success");
        jsonObject.put("data",data);
        return jsonObject;
    }


//     根据传入的ID，查询数据库，
    public Boolean encodeDisasterInfo(int id){
        return true;
    }


}
