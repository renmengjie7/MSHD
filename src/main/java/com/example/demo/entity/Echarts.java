package com.example.demo.entity;

import lombok.Data;

//用于echarts图显示
@Data
public class Echarts {
    private String name;
    private Integer value;

    public Echarts(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public Echarts() {
    }
}
