package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondaryDisaster {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private String secondaryId;
    private String province;
    private String city;
    private String country;
    private String town;
    private String village;
    private String location;
    private Timestamp date;
    private int category;
    private int type=0;
    private int status;
    private String picture;
    private String reportingUnit;
    private String note;
    private String earthquakeId;

    public SecondaryDisaster(String secondaryId, String province, String city, String country, String town, String village, String location, Timestamp date, int category, int type, int status, String picture, String reportingUnit, String note, String earthquakeId) {
        this.secondaryId = secondaryId;
        this.province = province;
        this.city = city;
        this.country = country;
        this.town = town;
        this.village = village;
        this.location = location;
        this.date = date;
        this.category = category;
        this.type = type;
        this.status = status;
        this.picture = picture;
        this.reportingUnit = reportingUnit;
        this.note = note;
        this.earthquakeId = earthquakeId;
    }

    public SecondaryDisaster(String province, String city, String country, String town, String village, int category, int status) {
        this.province = province;
        this.city = city;
        this.country = country;
        this.town = town;
        this.village = village;
        this.category = category;
        this.status = status;
    }
}
