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
public class BuildingDamage {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private String building_damage_id    ;
    private String province              ;
    private String city                  ;
    private String country               ;
    private String town                  ;
    private String village               ;
    private String location              ;
    private Timestamp date               ;
    private int category                 ;
    private double basicallyIntactSquare ;
    private double damagedSquare         ;
    private double destroyedSquare       ;
    private String note                  ;
    private String reporting_unit        ;
    private String earthquakeId          ;


    public BuildingDamage(String building_damage_id, String province, String city, String country, String town, String village, String location, Timestamp date, int category, double basicallyIntactSquare, double damagedSquare, double destroyedSquare, String note, String reporting_unit, String earthquakeId) {
        this.building_damage_id = building_damage_id;
        this.province = province;
        this.city = city;
        this.country = country;
        this.town = town;
        this.village = village;
        this.location = location;
        this.date = date;
        this.category = category;
        this.basicallyIntactSquare = basicallyIntactSquare;
        this.damagedSquare = damagedSquare;
        this.destroyedSquare = destroyedSquare;
        this.note = note;
        this.reporting_unit = reporting_unit;
        this.earthquakeId = earthquakeId;
    }
}
