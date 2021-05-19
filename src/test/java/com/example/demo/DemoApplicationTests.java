package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Disasterinfo;
import com.example.demo.service.DisasterInfoService;
import com.example.demo.service.FTPService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.ParseException;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private DisasterInfoService disasterInfoService;

    @Autowired
    private FTPService ftpService;


//    @Test
//    void Test() {
//        disasterInfoService.MapData(Timestamp.valueOf("2021-04-22 13:11:22"),Timestamp.valueOf("2021-04-23 13:11:22"));
//    }

}
