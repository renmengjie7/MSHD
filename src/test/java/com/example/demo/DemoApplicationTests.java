package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Disasterinfo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void TestXml(){
        disasterUploadXml("<disasterInfo>\n" +
                "    <info>\n" +
                "        <province>北京市</province>\n" +
                "        <city>市辖区</city>\n" +
                "        <country>东城区</country>\n" +
                "        <town>东华门街道</town>\n" +
                "        <village>多福巷社区居委会</village>\n" +
                "        <category>基本震情</category>\n" +
                "        <date>2021-04-21 10:16:57</date>\n" +
                "        <location>北京东城区</location>\n" +
                "        <longitude>116.63</longitude>\n" +
                "        <latitude>40.16</latitude>\n" +
                "        <depth>10</depth>\n" +
                "        <magnitude>2.3</magnitude>\n" +
                "        <reportingUnit>北京地震局</reportingUnit>\n" +
                "        <picture>bj1.jpg</picture>\n" +
                "    </info>\n" +
                "    <info>\n" +
                "        <province>北京市</province>\n" +
                "        <city>市辖区</city>\n" +
                "        <country>东城区</country>\n" +
                "        <town>东华门街道</town>\n" +
                "        <village>多福巷社区居委会</village>\n" +
                "        <category>基本震情</category>\n" +
                "        <date>2021-04-21 11:17:50</date>\n" +
                "        <location>北京东城区</location>\n" +
                "        <longitude>116.63</longitude>\n" +
                "        <latitude>40.16</latitude>\n" +
                "        <depth>12</depth>\n" +
                "        <magnitude>5.3</magnitude>\n" +
                "        <reportingUnit>北京地震局</reportingUnit>\n" +
                "        <picture>bj2.jpg</picture>\n" +
                "    </info>\n" +
                "    <info>\n" +
                "        <province>北京市</province>\n" +
                "        <city>市辖区</city>\n" +
                "        <country>东城区</country>\n" +
                "        <town>东华门街道</town>\n" +
                "        <village>多福巷社区居委会</village>\n" +
                "        <category>基本震情</category>\n" +
                "        <date>2021-04-21 11:19:52</date>\n" +
                "        <location>北京东城区</location>\n" +
                "        <longitude>116.63</longitude>\n" +
                "        <latitude>40.16</latitude>\n" +
                "        <depth>15</depth>\n" +
                "        <magnitude>7.3</magnitude>\n" +
                "        <reportingUnit>北京地震局</reportingUnit>\n" +
                "        <picture>bj3.jpg</picture>\n" +
                "    </info>\n" +
                "    <info>\n" +
                "        <province>山西省</province>\n" +
                "        <city>长治市</city>\n" +
                "        <country>城区</country>\n" +
                "        <town>西街街道办事处</town>\n" +
                "        <village>参府社区居委会</village>\n" +
                "        <category>基本震情</category>\n" +
                "        <date>2021-04-21 21:16:00</date>\n" +
                "        <location>山西省长治市城区</location>\n" +
                "        <longitude>113.01</longitude>\n" +
                "        <latitude>35.50</latitude>\n" +
                "        <depth>15</depth>\n" +
                "        <magnitude>2.5</magnitude>\n" +
                "        <reportingUnit>山西地震局</reportingUnit>\n" +
                "        <picture>sx1.jpg</picture>\n" +
                "    </info>\n" +
                "    <info>\n" +
                "        <province>山西省</province>\n" +
                "        <city>长治市</city>\n" +
                "        <country>城区</country>\n" +
                "        <town>西街街道办事处</town>\n" +
                "        <village>参府社区居委会</village>\n" +
                "        <category>基本震情</category>\n" +
                "        <date>2021-04-22 19:17:00</date>\n" +
                "        <location>山西省长治市城区</location>\n" +
                "        <longitude>113.01</longitude>\n" +
                "        <latitude>35.50</latitude>\n" +
                "        <depth>15</depth>\n" +
                "        <magnitude>2.7</magnitude>\n" +
                "        <reportingUnit>山西地震局</reportingUnit>\n" +
                "        <picture>sx2.jpg</picture>\n" +
                "    </info>\n" +
                "</disasterInfo>");
    }

    public JSONObject disasterUploadXml(String content){
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
                System.out.println(disasterinfoEntity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
