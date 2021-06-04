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
    private String buildingDamageId    ;
    private String province              ;
    private String city                  ;
    private String country               ;
    private String town                  ;
    private String village               ;
    private String location              ;
    private String  date               ;
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
}
