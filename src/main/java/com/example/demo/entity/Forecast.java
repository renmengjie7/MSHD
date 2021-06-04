package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Forecast {

    private Timestamp date;
    private int grade;
    private int intensity;
    private int type;
    private String picture;

}
