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
    private String code;
    private Timestamp date;
    private int grade;
    private int intensity;
    private int type;
    private String picture;

    public Forecast(Timestamp date, int grade, int intensity, int type, String picture) {
        this.date = date;
        this.grade = grade;
        this.intensity = intensity;
        this.type = type;
        this.picture = picture;
    }
}
