package com.example.demo.vo;
import lombok.Data;

import java.util.List;

@Data
//json传递格式(泛型)
public class DataVO<T> {
    private Integer code;
    private String msg;
    private Long count;
    private List<T> data;
}
