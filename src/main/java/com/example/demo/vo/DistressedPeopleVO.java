package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistressedPeopleVO {
    private int id;
    private String peopleId;
    private String province;
    private String city;
    private String country;
    private String town;
    private String village;
    private String location;
    private String date;
    private int number;
    private int category;
    private String reportingUnit;
    private String earthquakeId;
}
