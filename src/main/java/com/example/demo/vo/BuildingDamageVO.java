package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuildingDamageVO {
    private int id;
    private String building_damage_id    ;
    private String province              ;
    private String city                  ;
    private String country               ;
    private String town                  ;
    private String village               ;
    private String location              ;
    private String date                  ;
    private int category                 ;
    private double basicallyIntactSquare ;
    private double damagedSquare         ;
    private double destroyedSquare       ;
    private String note                  ;
    private String reporting_unit        ;
    private String earthquakeId          ;
}
