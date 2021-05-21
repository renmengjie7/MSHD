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
public class DistressedPeople {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private String peopleId;
    private String province;
    private String city;
    private String country;
    private String town;
    private String village;
    private String location;
    private Timestamp date;
    private int number;
    private int category;
    private String reportingUnit;
    private String earthquakeId;
}
