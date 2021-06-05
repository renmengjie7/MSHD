package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.*;
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


    //房屋编码
    public Map<String,String> doBuildingDamageCode(BuildingDamage buildingDamage){
        Map<String,String> map = new HashMap<>();
        String locationCode,messageCode;
        //根据地理位置信息找到code
        locationCode=findCode(buildingDamage.getProvince(),
                buildingDamage.getCity(),
                buildingDamage.getCountry(),
                buildingDamage.getTown(),
                buildingDamage.getVillage());
        if(locationCode==null){
            map.put("resCode","0");
            map.put("msg","the location "+buildingDamage.getProvince()+ buildingDamage.getCity()+ buildingDamage.getCountry()+ buildingDamage.getTown()+ buildingDamage.getVillage()+"can not find code");
        }
        else {
            locationCode = (locationCode+"000000000000").substring(0,12);
            //灾情信息编码
            //0土木 1砖木 2砖混 3框架 4其他
            int category=buildingDamage.getCategory();
            if(category==0||category==1||category==2||category==3||category==4) {
                float magnitude = selectMagnitude(buildingDamage.getEarthquakeId());
                if (magnitude == -1) {
                    map.put("resCode", "0");
                    map.put("msg", "can not find disaster with this EarthquakeId: "+ buildingDamage.getEarthquakeId());
                }
                else {
                    int grade =gradeCalculate(magnitude,(buildingDamage.getDamagedSquare()+buildingDamage.getDestroyedSquare())/2,1);
                    messageCode = "55"
                            + (buildingDamage.getCategory() + 1)
                            + String.format("%03d", buildingDamage.getId())
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

    //地理位置编码+33+category+id+grade 3405210000003310001
    public Map<String,String> doLifelineDisasterCode(LifelineDisaster lifelineDisaster){
        Map<String,String> map = new HashMap<>();
        String locationCode,messageCode;
        //根据地理位置信息找到code
        locationCode=findCode(lifelineDisaster.getProvince(),
                lifelineDisaster.getCity(),
                lifelineDisaster.getCountry(),
                lifelineDisaster.getTown(),
                lifelineDisaster.getVillage());
        if(locationCode==null){
            map.put("resCode","0");
            map.put("msg","the location "+lifelineDisaster.getProvince()+ lifelineDisaster.getCity()+ lifelineDisaster.getCountry()+ lifelineDisaster.getTown()+ lifelineDisaster.getVillage()+"can not find code");
        }
        else {
            locationCode = (locationCode+"000000000000").substring(0,12);
            //灾情信息编码
            int category=lifelineDisaster.getCategory();//0交通 1供水 2输油 3燃气 4电力 5通信 6水利
            if(category==0||category==1||category==2||category==3||category==4||category==5||category==6) {
                int grade =lifelineDisaster.getGrade();
                messageCode = "33"
                        + (lifelineDisaster.getCategory() + 1)
                        + String.format("%03d", lifelineDisaster.getId())
                        + grade;
                map.put("resCode", "1");
                map.put("msg", locationCode + messageCode);
            }
            else {//类别不正确
                map.put("resCode","0");
                map.put("msg","the category is incorrect");
            }
        }
        return map;
    }

    //地理位置编码+44+category+id+status
    public Map<String,String> doSecondaryDisasterCode(SecondaryDisaster secondaryDisaster){
        Map<String,String> map = new HashMap<>();
        String locationCode,messageCode;
        //根据地理位置信息找到code
        locationCode=findCode(secondaryDisaster.getProvince(),
                secondaryDisaster.getCity(),
                secondaryDisaster.getCountry(),
                secondaryDisaster.getTown(),
                secondaryDisaster.getVillage());
        if(locationCode==null){
            map.put("resCode","0");
            map.put("msg","the location "+secondaryDisaster.getProvince()+secondaryDisaster.getCity()+secondaryDisaster.getCountry()+secondaryDisaster.getTown()+secondaryDisaster.getVillage()+"can not find code");
        }
        else {
            locationCode = (locationCode+"000000000000").substring(0,12);
            //灾情信息编码
            int category=secondaryDisaster.getCategory();//0崩塌 1滑坡 2泥石流 3岩溶塌陷 4地裂缝 5地面沉降 6其他
            if(category==0||category==1||category==2||category==3||category==4||category==5||category==6) {
                int status =secondaryDisaster.getStatus();
                messageCode = "44"
                        + (category+ 1)
                        + String.format("%03d", secondaryDisaster.getId())
                        + status;
                map.put("resCode", "1");
                map.put("msg", locationCode + messageCode);
            }
            else {//类别不正确
                map.put("resCode","0");
                map.put("msg","the category is incorrect");
            }
        }
        return map;
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
        locationCode=findCode(distressedPeople.getProvince(),
                distressedPeople.getCity(),
                distressedPeople.getCountry(),
                distressedPeople.getTown(),
                distressedPeople.getVillage());
        if(locationCode==null){
            map.put("resCode","0");
            map.put("msg","the location "+distressedPeople.getProvince()+ distressedPeople.getCity()+ distressedPeople.getCountry()+ distressedPeople.getTown()+ distressedPeople.getVillage()+"can not find code");
        }
        else {
            locationCode = (locationCode+"000000000000").substring(0,12);
            //灾情信息编码
            if(distressedPeople.getCategory()==0||distressedPeople.getCategory()==1||distressedPeople.getCategory()==2) {
                float magnitude = selectMagnitude(distressedPeople.getEarthquakeId());
                if (magnitude == -1) {
                    map.put("resCode", "0");
                    map.put("msg", "can not find disaster with this EarthquakeId: "+ distressedPeople.getEarthquakeId());
                }
                else {
                    int grade = gradeCalculate(magnitude, distressedPeople.getNumber(),0);
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
     * @param category 0人员伤亡 1房屋损坏
     * @return 1特别重大 2重大 3较大 4一般
     */
    public int gradeCalculate(float magnitude,double number,int category){

            if (magnitude < 5)//一般
                return 4;
            else {
                //人员伤亡
                if(category==0) {
                    if (magnitude >= 7.0 || number >= 300)//特别重大
                        return 1;
                    else if ((6.0 <= magnitude && magnitude < 7) || (number >= 50 && number < 300))//重大
                        return 2;
                    else if (magnitude < 6.0 || number < 50)//较大
                        return 3;
                    else return 0;
                }
                //房屋损坏面积
                else {
                    if (magnitude >= 7.0 || number >= 200)//特别重大
                        return 1;
                    else if ((6.0 <= magnitude && magnitude < 7) || (number >= 60 && number < 200))//重大
                        return 2;
                    else if (magnitude < 6.0 || number < 60)//较大
                        return 3;
                    else return 0;
                }
            }
    }

    //查询震级
    public float selectMagnitude(String earthquakeId){
        QueryWrapper<Disasterinfo> disasterinfoQueryWrapper=new QueryWrapper<>();
        disasterinfoQueryWrapper.eq("d_id",earthquakeId);
        disasterinfoQueryWrapper.last("limit 1");
        try {
            Disasterinfo disasterinfo = disasterMapper.selectOne(disasterinfoQueryWrapper);
            if(disasterinfo==null) {
                System.out.printf("not find %s\n", earthquakeId);
                return -1;
            }
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
        code=findCode(province,city,country,town,village);
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