package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.BuildingDamage;
import com.example.demo.entity.ChinaAdministrative;
import com.example.demo.entity.Disasterinfo;
import com.example.demo.entity.DistressedPeople;
import com.example.demo.mapper.ChinaAdministrativeMapper;
import com.example.demo.mapper.DisasterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChinaAdministrtiveService {

    @Autowired
    private ChinaAdministrativeMapper chinaAdministrativeMapper;
    @Autowired
    private DisasterMapper disasterMapper;

    public String findCode(String province,String city,String district,String town,String village){
        QueryWrapper<ChinaAdministrative> queryWrapper=new QueryWrapper<>();
        Map<String,Object> paramsMap=new HashMap<>();
        paramsMap.put("province_name",province);
        paramsMap.put("city_name",city);
        paramsMap.put("district_name",district);
        paramsMap.put("town_name",town);
        paramsMap.put("village_name",village);
        queryWrapper.allEq(paramsMap);
        try {
            ChinaAdministrative chinaAdministrative=chinaAdministrativeMapper.selectOne(queryWrapper);
            String code=chinaAdministrative.getCode();
            return code;
        }
        catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }


    public String doBuildingDamageCode(BuildingDamage buildingDamage){
        return "";
    }

    /**
     *
     * @param distressedPeople
     * @return resCode 0失败 1成功 msg错误信息
     */
    public Map<String,String> doDistressedPeopleCode(DistressedPeople distressedPeople){
        Map<String,String> map = new HashMap<>();
        String locationCode,messageCode;
        //根据地理位置信息找到code
        locationCode=this.findCode(distressedPeople.getProvince(),
                distressedPeople.getCity(),
                distressedPeople.getCountry(),
                distressedPeople.getTown(),
                distressedPeople.getVillage());
        if(locationCode==null){
            map.put("resCode","0");
            map.put("msg","the location can not find code");
        }
        else {
            locationCode = (locationCode+"000000000000").substring(0,12);
            //灾情信息编码
            if(distressedPeople.getCategory()==0||distressedPeople.getCategory()==1||distressedPeople.getCategory()==2) {
                float magnitude = selectMagnitude(distressedPeople.getEarthquakeId());
                if (magnitude == -1) {
                    map.put("resCode", "0");
                    map.put("msg", "can not find disaster with this EarthquakeId");
                }
                else {
                    int grade = gradeCalculate(magnitude, distressedPeople.getNumber());
                    messageCode = "11"
                            + (distressedPeople.getCategory() + 1)
                            + String.format("%03d", distressedPeople.getId())
                            + grade;
                    map.put("resCode", "1");
                    map.put("msg", locationCode + messageCode);
                }
            }
            else {//类别不正确
                map.put("resCode","0");
                map.put("msg","the category is incorrect");
            }
        }
        return map;
    }

    /**
     * 计算灾害等级
     * @param magnitude
     * @param number
     * @return 1特别重大 2重大 3较大 4一般
     */
    public int gradeCalculate(float magnitude,int number){
        if(magnitude<5)//一般
            return 4;
        else {
            if(magnitude>=7.0||number>=300)//特别重大
                return 1;
            else if((6.0<=magnitude&&magnitude<7)||(number>=50&&number<300))//重大
                return 2;
            else if(magnitude<6.0||number<50)//较大
                return 3;
            else return 0;
        }
    }

    //查询震级
    public float selectMagnitude(String earthquakeId){
        QueryWrapper<Disasterinfo> disasterinfoQueryWrapper=new QueryWrapper<>();
        disasterinfoQueryWrapper.eq("d_id",earthquakeId);
        disasterinfoQueryWrapper.last("limit 1");
        try {
            Disasterinfo disasterinfo = disasterMapper.selectOne(disasterinfoQueryWrapper);
            if(disasterinfo==null)
                System.out.printf("not find %s\n",earthquakeId);
            return disasterinfo.getMagnitude();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 基本震情编码
     * @param province
     * @param city
     * @param country
     * @param town
     * @param village
     * @param date
     * @return code 编码成功   null编码失败
     */
    public String doCode(String province,String city,String country,String town,String village,String date){
        //获取未编码的震情对象集
        String code;
        //去除日期中的空格，-和:
        String date_form = date.replaceAll("[[\\s-:punct:]]","");
        //判断时间长度是否正确
        if (date_form.length()!=14){
            System.out.println("time is incorrect");
            return null;
        }
        //根据地理位置信息找到code
        code=this.findCode(province,city,country,town,village);
        if(code==null){
            System.out.println("\nthe location can not find code\n");
            return null;
        }
        else {
            code = (code+"000000000000").substring(0,12);
            code += date_form;
            return code;
        }
    }
}