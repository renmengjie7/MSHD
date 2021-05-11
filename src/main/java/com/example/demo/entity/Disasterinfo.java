package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Disasterinfo {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private String unifiedId;
    private String dId;
    private String province;
    private String date;
    private String location;
    private Float longitude;
    private Float latitude;
    private Float depth;
    private Float magnitude;
    private String reportingUnit;
    private String city;
    private String country;
    private String town;
    private String village;
    private byte[] picture;
}
