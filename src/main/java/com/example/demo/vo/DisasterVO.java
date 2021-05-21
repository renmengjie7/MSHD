package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisasterVO {
    private int id;
    private String unifiedId="501";
    private String dId;
    private String province;
    private String city;
    private String country;
    private String town;
    private String village;
    private String date;
    private String location;
    private double longitude;
    private double latitude;
    private float depth;
    private float magnitude;
    private String picture;
    private String reportingUnit;
//    012死亡、受伤、失踪？
    private int deathPeople=0;
    private int injuredPeople=0;
    private int missingPeople=0;
}
