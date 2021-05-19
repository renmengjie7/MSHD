package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Disasterinfo {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private String unifiedId="501";
    private String dId;
    private String province;
    private String city;
    private String country;
    private String town;
    private String village;
    private Timestamp date;
    private String location;
    private double longitude;
    private double latitude;
    private float depth;
    private float magnitude;
    private String picture;
    private String reportingUnit;

    public Disasterinfo(int id, String dId, String province, String city, String country, String town, String village, Timestamp date, String location, double longitude, double latitude, float depth, float magnitude, String picture, String reportingUnit) {
        this.id = id;
        this.dId = dId;
        this.province = province;
        this.city = city;
        this.country = country;
        this.town = town;
        this.village = village;
        this.date = date;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.depth = depth;
        this.magnitude = magnitude;
        this.picture = picture;
        this.reportingUnit = reportingUnit;
    }

    public Disasterinfo(String dId, String province, String city, String country, String town, String village, Timestamp date, String location, double longitude, double latitude, float depth, float magnitude, String picture, String reportingUnit) {
        this.dId = dId;
        this.province = province;
        this.city = city;
        this.country = country;
        this.town = town;
        this.village = village;
        this.date = date;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.depth = depth;
        this.magnitude = magnitude;
        this.picture = picture;
        this.reportingUnit = reportingUnit;
    }

}
