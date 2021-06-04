package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.entity.*;
import com.example.demo.mapper.DisasterMapper;
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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FileOperation {

    String mainPath="static/";

    @Autowired
    private DisasterMapper disasterMapper;
    @Autowired
    private ChinaAdministrtiveService chinaAdministrtiveService;

    public Boolean deleteImg(String img){
        //需要删除文件
        String path= null;
        try {
            path= mainPath+img;
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
        System.out.println("存图片");
        //前端没有选择文件，srcFile为空
        if (file.isEmpty()) {
            return null;
        }
        String[] ttt=file.getOriginalFilename().split("\\.");
        //选择了文件，开始上传操作z
        String suffixName;
        try {
            //拼接子路径
            File upload = new File(mainPath,dirPath);
            //若目标文件夹不存在，则创建
            if (!upload.exists()) {
                upload.mkdirs();
            }
            //根据srcFile大小，准备一个字节数组
            byte[] bytes = file.getBytes();
            //拼接上传路径
            //通过项目路径，拼接上传路径
            Path path = Paths.get(upload.getAbsolutePath() + "/" + filename+"."+ttt[ttt.length-1]);
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

    public List<Disasterinfo> jsonParseBasic(String content) {
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

    public List<DistressedPeople> jsonParsePeople(String content) {
        List<DistressedPeople> distressedPeopleList = new ArrayList<>();
        //逻辑处理，存入数据库，需要判断是否重复
        org.json.JSONObject data = new org.json.JSONObject(content);
        org.json.JSONObject disasterInfo = new org.json.JSONObject(data.get("disasterInfo").toString());
        JSONArray infos = JSONArray.fromObject(disasterInfo.get("info").toString());
        for (int i = 0; i < infos.size(); i++) {
            org.json.JSONObject info = new org.json.JSONObject(infos.get(i).toString());


            String peopleId="";
            String province=info.getString("province");
            String city=info.getString("city");
            String country=info.getString("country");
            String town=info.getString("town");
            String village=info.getString("village");
            String location=info.getString("location");
            Timestamp date=Timestamp.valueOf(info.getString("date"));
            int number=Integer.parseInt(info.getString("number"));
            int category;
            String reportingUnit=info.getString("reportingUnit");
            String earthquakeId=info.getString("earthquakeId");
            String cate=info.getString("category");
            switch (cate){
                case "人员死亡":
                    category=0;
                    break;
                case "人员受伤":
                    category=1;
                    break;
                case "人员失踪":
                    category=2;
                    break;
                default:
                    category=0;
            }
            DistressedPeople distressedPeople=new DistressedPeople(peopleId,province,city,country,town,village,location,date,number,category,reportingUnit,earthquakeId);

            //这里添加一个编码的代码
            //这里添加一个编码的代码
            Map<String,String> map=chinaAdministrtiveService.doDistressedPeopleCode(distressedPeople);
            if (map.get("resCode")=="0") {
                //编码时数据格式不正确
                System.out.println("people encode fail because of the invalid format");
                return null;
            } else {
                peopleId=map.get("msg");
                distressedPeople.setPeopleId(peopleId);
                distressedPeopleList.add(distressedPeople);
            }
        }

        return distressedPeopleList;
    }

    //解析生命灾情数据
    public List<LifelineDisaster> jsonParseLifelineDisaster(String content) {
        List<LifelineDisaster> lifelineDisasters = new ArrayList<>();
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

            String lifelineId="";
            String province=info.getString("province");
            String city=info.getString("city");
            String country=info.getString("country");
            String town=info.getString("town");
            String village=info.getString("village");
            String location=info.getString("location");
            Timestamp date=Timestamp.valueOf(info.getString("date"));
            String note=info.getString("note");
            String reportingUnit=info.getString("reportingUnit");
            String earthquakeId=info.getString("earthquakeId");
            String picture=info.getString("picture");

            int grade=Integer.parseInt(info.getString("grade"));
            int type=Integer.parseInt(info.getString("type"));
            int category;
            String cate=info.getString("category");
            switch (cate){
                case "交通":
                    category=0;
                    break;
                case "供水":
                    category=1;
                    break;
                case "输油":
                    category=2;
                    break;
                case "燃气":
                    category=3;
                    break;
                case "电力":
                    category=4;
                    break;
                case "通信":
                    category=5;
                    break;
                case "水利":
                    category=6;
                    break;
                default:
                    category=0;
            }
            LifelineDisaster lifelineDisaster=new LifelineDisaster(lifelineId,province,city,country,town,village,location,date,note,category,grade,type, picture,reportingUnit,earthquakeId);
            String code=chinaAdministrtiveService.doLifelineDisasterCode(lifelineDisaster);
           if (code == null) {
                //编码时数据格式不正确
                System.out.println("编码时数据格式不正确");
                return null;
            } else {
                lifelineDisaster.setLifelineId(code);
                lifelineDisasters.add(lifelineDisaster);
            }
        }
//        }
        return lifelineDisasters;
    }

    //解析二次灾害数据
    public List<SecondaryDisaster> jsonParseSecondaryDisaster(String content) {
        List<SecondaryDisaster> secondaryDisasters = new ArrayList<>();
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


            String secondaryId="";
            String province=info.getString("province");
            String city=info.getString("city");
            String country=info.getString("country");
            String town=info.getString("town");
            String village=info.getString("village");
            String location=info.getString("location");
            Timestamp date=Timestamp.valueOf(info.getString("date"));
            String note=info.getString("note");
            String reportingUnit=info.getString("reportingUnit");
            String earthquakeId=info.getString("earthquakeId");
            String picture=info.getString("picture");

            int type=Integer.parseInt(info.getString("type"));
            int status=Integer.parseInt(info.getString("status"));


            int category;
            String cate=info.getString("category");
            switch (cate){
                case "崩塌":
                    category=0;
                    break;
                case "滑坡":
                    category=1;
                    break;
                case "泥石流":
                    category=2;
                    break;
                case "岩溶塌陷":
                    category=3;
                    break;
                case "地裂缝":
                    category=4;
                    break;
                case "地面沉降":
                    category=5;
                    break;
                case "其他":
                    category=6;
                    break;
                default:
                    category=0;
            }
            SecondaryDisaster secondaryDisaster=new SecondaryDisaster(secondaryId,province,city,country,town,village,location,
                    date,category,type,status,picture,reportingUnit,note,earthquakeId);
            String code=chinaAdministrtiveService.doSecondaryDisasterCode(secondaryDisaster);
            if (code == null) {
                //编码时数据格式不正确
                System.out.println("编码时数据格式不正确");
                return null;
            } else {
                secondaryDisaster.setSecondaryId(code);
                secondaryDisasters.add(secondaryDisaster);
            }
        }
//        }
        return secondaryDisasters;
    }

    //解析受灾房屋
    public List<BuildingDamage> jsonParseBuildingDamage(String content) {
        List<BuildingDamage> buildingDamageList = new ArrayList<>();
        //逻辑处理，存入数据库，需要判断是否重复
        org.json.JSONObject data = new org.json.JSONObject(content);
        org.json.JSONObject disasterInfo = new org.json.JSONObject(data.get("disasterInfo").toString());
        JSONArray infos = JSONArray.fromObject(disasterInfo.get("info").toString());
        for (int i = 0; i < infos.size(); i++) {
            org.json.JSONObject info = new org.json.JSONObject(infos.get(i).toString());

            String building_damage_id="";
            String province=info.getString("province");
            String city=info.getString("city");
            String country=info.getString("country");
            String town=info.getString("town");
            String village=info.getString("village");
            String location=info.getString("location");
            Timestamp date=Timestamp.valueOf(info.getString("date"));
            String note=info.getString("note");
            String reportingUnit=info.getString("reportingUnit");
            String earthquakeId=info.getString("earthquakeId");
            double basicallyIntactSquare=Double.parseDouble(info.getString("basicallyIntactSquare"));
            double damagedSquare=Double.parseDouble(info.getString("damagedSquare"));
            double destroyedSquare=Double.parseDouble(info.getString("destroyedSquare"));
            int category;
            String cate=info.getString("category");
            switch (cate){
                case "房屋破坏土木":
                    category=0;
                    break;
                case "房屋破坏砖木":
                    category=1;
                    break;
                case "房屋破坏砖混":
                    category=2;
                    break;
                case "房屋破坏框架":
                    category=3;
                    break;
                case "房屋破坏其他":
                    category=4;
                    break;
                default:
                    category=0;
            }
            BuildingDamage buildingDamage=new BuildingDamage(building_damage_id,province,city,country,town,village,location,
                    date,category,basicallyIntactSquare,damagedSquare,destroyedSquare,note,reportingUnit,earthquakeId);

            //这里添加一个编码的代码
            String result=chinaAdministrtiveService.doBuildingDamageCode(buildingDamage);
            if (result=="") {
                //编码时数据格式不正确
                System.out.println("BuildingDamage encode fail because of the invalid format");
                return null;
            } else {
                buildingDamage.setBuildingDamageId(result);
                buildingDamageList.add(buildingDamage);
            }
        }

        return buildingDamageList;
    }

    //解析受灾房屋
    public List<Forecast> jsonParseForecast(String content) {
        List<Forecast> forecasts = new ArrayList<>();
        //逻辑处理，存入数据库，需要判断是否重复
        org.json.JSONObject data = new org.json.JSONObject(content);
        org.json.JSONObject disasterInfo = new org.json.JSONObject(data.get("disasterInfo").toString());
        JSONArray infos = JSONArray.fromObject(disasterInfo.get("info").toString());
        for (int i = 0; i < infos.size(); i++) {
            org.json.JSONObject info = new org.json.JSONObject(infos.get(i).toString());

            int grade=Integer.parseInt(info.getString("grade"));
            int intensity=Integer.parseInt(info.getString("intensity"));
            int type=Integer.parseInt(info.getString("type"));
            String picture=info.getString("picture");
            Timestamp date=Timestamp.valueOf(info.getString("date"));
            Forecast forecast=new Forecast(date,grade,intensity,type,picture);

            //这里添加一个编码的代码
            String result=chinaAdministrtiveService.doForecastCode(forecast);
            if (result=="") {
                //编码时数据格式不正确
                System.out.println("forecast encode fail because of the invalid format");
                return null;
            } else {
                forecast.setCode(result);
                forecasts.add(forecast);
            }
        }

        return forecasts;
    }

}
