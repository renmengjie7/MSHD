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
public class LifelineDisaster {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private String lifelineId;
    private String province;
    private String city;
    private String country;
    private String town;
    private String village;
    private String location;
    private Timestamp date;
    private String note;

    private int category;
    private int grade;
    private int type;
    private String picture;
    private String reportingUnit;
    private String earthquakeId;

    public LifelineDisaster(String lifelineId, String province, String city, String country, String town, String village, String location, Timestamp date, String note, int category, int grade, int type, String picture, String reportingUnit, String earthquakeId) {
        this.lifelineId = lifelineId;
        this.province = province;
        this.city = city;
        this.country = country;
        this.town = town;
        this.village = village;
        this.location = location;
        this.date = date;
        this.note = note;
        this.category = category;
        this.grade = grade;
        this.type = type;
        this.picture = picture;
        this.reportingUnit = reportingUnit;
        this.earthquakeId = earthquakeId;
    }

    public LifelineDisaster(String province, String city, String country, String town, String village, int category, int grade) {
        this.province = province;
        this.city = city;
        this.country = country;
        this.town = town;
        this.village = village;
        this.category = category;
        this.grade = grade;
    }
}
