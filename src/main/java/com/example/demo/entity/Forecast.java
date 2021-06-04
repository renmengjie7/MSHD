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
public class Forecast {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private Timestamp date;
    private int grade;
    private int intensity;
    private int type;
    private String picture;
}
