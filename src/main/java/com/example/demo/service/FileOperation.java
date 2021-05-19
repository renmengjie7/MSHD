package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.entity.Disasterinfo;
import com.example.demo.mapper.DisasterMapper;
import com.example.demo.service.ChinaAdministrtiveService;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileOperation {

    @Autowired
    private DisasterMapper disasterMapper;
    @Autowired
    private ChinaAdministrtiveService chinaAdministrtiveService;

    public Boolean deleteImg(String img){
        //需要删除文件
        String path= null;
        try {
            path = ResourceUtils.getURL("classpath:static").getPath().replaceAll("%20"," ").substring(1).replace('/','\\');
            path= URLDecoder.decode(path,"UTF-8")+img;
            File file=new File(path);
            if(file.exists()){
                file.delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //保存文件
    public String saveImg(MultipartFile file,String dirPath,String filename){
        //前端没有选择文件，srcFile为空
        if (file.isEmpty()) {
            return null;
        }
        String[] ttt=file.getOriginalFilename().split("\\.");
        //选择了文件，开始上传操作z
        String suffixName;
        try {
            //拼接子路径

            System.out.println("test----------"+ClassUtils.getDefaultClassLoader().getResource("static").getPath());
            String directory= ResourceUtils.getURL("classpath:static").getPath().replace("!","");
//            String directory= ResourceUtils.getURL("classpath:static").getPath().replaceAll("%20"," ").substring(1).replace('/','\\');
            System.out.println("directory--------------"+directory);
            System.out.println("dirPath--------------"+dirPath);
            directory= URLDecoder.decode(directory,"UTF-8");
            File upload = new File("test.jar/BOOT-INF/classes/static",dirPath);
            //若目标文件夹不存在，则创建
            if (!upload.exists()) {
                upload.mkdirs();
            }
            //根据srcFile大小，准备一个字节数组
            byte[] bytes = file.getBytes();
            //拼接上传路径
            //通过项目路径，拼接上传路径
            Path path = Paths.get(upload.getAbsolutePath() + "\\" + filename+"."+ttt[ttt.length-1]);
            System.out.println("path--------------"+path.toString());
            //** 开始将源文件写入目标地址
            Files.write(path, bytes);
            // 获得文件原始名称
            String fileName = file.getOriginalFilename();
            // 获得文件后缀名称
           suffixName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        //写入数据库？
        return dirPath+"/"+filename+"."+suffixName;
    }

    public List<Disasterinfo> jsonParse(String content) {
        List<Disasterinfo> disasterinfos = new ArrayList<>();
        //逻辑处理，存入数据库，需要判断是否重复
        org.json.JSONObject data = new org.json.JSONObject(content);
        org.json.JSONObject disasterInfo = new org.json.JSONObject(data.get("disasterInfo").toString());
        JSONArray infos = JSONArray.fromObject(disasterInfo.get("info").toString());
        for (int i = 0; i < infos.size(); i++) {
            org.json.JSONObject info = new org.json.JSONObject(infos.get(i).toString());
//            if (existDisasterInfo(info.getString("date"), info.getString("longitude"), info.getString("latitude"))) {
//                //相同的数据已经出现过了
//                return null;
//            } else {
                Disasterinfo disasterinfo = new Disasterinfo("", info.getString("province"),
                        info.getString("city"), info.getString("country"),
                        info.getString("town"), info.getString("village"),
                        Timestamp.valueOf(info.getString("date")), info.getString("location"),
                        Double.parseDouble(info.getString("longitude")), Double.parseDouble(info.getString("latitude")),
                        Float.parseFloat(info.getString("depth")), Float.parseFloat(info.getString("magnitude")),
                        info.getString("picture"), info.getString("reportingUnit"));
                String code = chinaAdministrtiveService.doCode(disasterinfo.getProvince(), disasterinfo.getCity(), disasterinfo.getCountry(),
                        disasterinfo.getTown(), disasterinfo.getVillage(), info.getString("date"));
                if (code == null) {
                    //编码时数据格式不正确
                    System.out.println("编码时数据格式不正确");
                    return null;
                } else {
                    disasterinfo.setDId(code);
                    disasterinfos.add(disasterinfo);
                }
            }
//        }
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
                                disasterinfoEntity.setDate(Timestamp.valueOf(temp));
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
                if (existDisasterInfo(disasterinfoEntity.getDate().toString(), disasterinfoEntity.getLongitude() + "", disasterinfoEntity.getLatitude() + "")) {
                    //信息已经存在
                    return null;
                } else {
                    //一体化编码
                    String code = chinaAdministrtiveService.doCode(disasterinfoEntity.getProvince(), disasterinfoEntity.getCity(), disasterinfoEntity.getCountry(),
                            disasterinfoEntity.getTown(), disasterinfoEntity.getVillage(), disasterinfoEntity.getDate().toString());
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

}
