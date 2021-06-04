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
    private String buildingDamageId    ;
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
    private String reportingUnit        ;
    private String earthquakeId          ;
    private double slightDamagedSquare;
    private double moderateDamagedSquare;
    private double seriousDamagedSquare ;

    public BuildingDamage(String buildingDamageId, String province, String city, String country, String town, String village, String location, Timestamp date, int category, double basicallyIntactSquare, double damagedSquare, double destroyedSquare, String note, String reportingUnit, String earthquakeId, double slightDamagedSquare, double moderateDamagedSquare, double seriousDamagedSquare) {
        this.buildingDamageId = buildingDamageId;
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
        this.reportingUnit = reportingUnit;
        this.earthquakeId = earthquakeId;
        this.slightDamagedSquare = slightDamagedSquare;
        this.moderateDamagedSquare = moderateDamagedSquare;
        this.seriousDamagedSquare = seriousDamagedSquare;
    }
}
