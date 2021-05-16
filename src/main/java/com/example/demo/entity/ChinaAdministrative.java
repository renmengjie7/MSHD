package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChinaAdministrative {
    @TableId(value = "ca_id", type = IdType.AUTO)
    private int caId;
    private String code;
    private String typeCode;
    private String name;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String townName;
    private String villageName;
    private String img;
    private String level;
    private String searchCount;
}
