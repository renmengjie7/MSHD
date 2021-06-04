package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForecastVO {
    private int id;
    private String code;
    private String date;
    private int grade;
    private int intensity;
    private int type;
    private String picture;
}
