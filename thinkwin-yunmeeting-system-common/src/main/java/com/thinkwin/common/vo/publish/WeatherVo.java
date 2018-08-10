package com.thinkwin.common.vo.publish;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/17 0017.
 */
public class WeatherVo implements Serializable {

    private static final long serialVersionUID = 3882136885773231177L;

    /**
     * 地区/城市ID
     */
    private String cid;
    /**
     * 地区/城市名称
     */
    private String  location;

    /**
     * 该地区／城市的上级城市
     */
    private String parent_city;
    /**
     * 该地区／城市所属行政区域
     */
    private String admin_area;

    /**
     * 最高气温
     */
    private String tmp_max;
    /**
     * 最低气温
     */
    private String tmp_min;

    /**
     * 白天天气状况代码
     */
    private String cond_code_d;

    /**
     * 晚间天气状况代码
     */
    private String cond_code_n;


    /**
     * 白天天气
     */
    private String cond_txt_d;
    /**
     * 夜里天气
     */
    private String cond_txt_n;
    /**
     * 风向
     */
    private String wind_dir;
    /**
     * 风力
     */
    private String wind_sc;

    /**
     * 能见度，默认单位：公里
     */
    private String vis;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getParent_city() {
        return parent_city;
    }

    public void setParent_city(String parent_city) {
        this.parent_city = parent_city;
    }

    public String getAdmin_area() {
        return admin_area;
    }

    public void setAdmin_area(String admin_area) {
        this.admin_area = admin_area;
    }

    public String getTmp_max() {
        return tmp_max;
    }

    public void setTmp_max(String tmp_max) {
        this.tmp_max = tmp_max;
    }

    public String getTmp_min() {
        return tmp_min;
    }

    public void setTmp_min(String tmp_min) {
        this.tmp_min = tmp_min;
    }

    public String getCond_txt_d() {
        return cond_txt_d;
    }

    public void setCond_txt_d(String cond_txt_d) {
        this.cond_txt_d = cond_txt_d;
    }

    public String getCond_txt_n() {
        return cond_txt_n;
    }

    public void setCond_txt_n(String cond_txt_n) {
        this.cond_txt_n = cond_txt_n;
    }

    public String getWind_dir() {
        return wind_dir;
    }

    public void setWind_dir(String wind_dir) {
        this.wind_dir = wind_dir;
    }

    public String getWind_sc() {
        return wind_sc;
    }

    public void setWind_sc(String wind_sc) {
        this.wind_sc = wind_sc;
    }

    public String getVis() {
        return vis;
    }

    public void setVis(String vis) {
        this.vis = vis;
    }

    public String getCond_code_d() {
        return cond_code_d;
    }

    public void setCond_code_d(String cond_code_d) {
        this.cond_code_d = cond_code_d;
    }

    public String getCond_code_n() {
        return cond_code_n;
    }

    public void setCond_code_n(String cond_code_n) {
        this.cond_code_n = cond_code_n;
    }
}
