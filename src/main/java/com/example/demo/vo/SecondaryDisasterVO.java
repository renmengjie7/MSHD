package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondaryDisasterVO {

    private int id;
    private String secondaryId;
    private String province;
    private String city;
    private String country;
    private String town;
    private String village;
    private String location;
    private String date;
    private int category;
    private int type=0;
    private int status;
    private String picture;
    private String reportingUnit;
    private String note;
    private String earthquakeId;


}
