package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

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
    private String date;
    private String location;
    private Float longitude;
    private Float latitude;
    private Float depth;
    private Float magnitude;
    private String picture;
    private String reportingUnit;

    public Disasterinfo(String unifiedId, String dId, String province, String city, String country, String town, String village, String date, String location, Float longitude, Float latitude, Float depth, Float magnitude, String picture, String reportingUnit) {
        this.unifiedId = unifiedId;
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
