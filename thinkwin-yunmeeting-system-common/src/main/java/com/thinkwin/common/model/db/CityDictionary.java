package com.thinkwin.common.model.db;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`city_dictionary`")
public class CityDictionary implements Serializable{
    private static final long serialVersionUID = 5819526561526910989L;
    /**
     * 城市地区编码
     */
    @Id
    @Column(name = "`city_code`")
    private String cityCode;

    /**
     * 城市英文
     */
    @Column(name = "`city_english`")
    private String cityEnglish;

    /**
     * 城市中文
     */
    @Column(name = "`city_chinese`")
    private String cityChinese;

    /**
     * 国家代码
     */
    @Column(name = "`country_code`")
    private String countryCode;

    /**
     * 国家英文
     */
    @Column(name = "`country_english`")
    private String countryEnglish;

    /**
     * 国家中文
     */
    @Column(name = "`country_chinese`")
    private String countryChinese;

    /**
     * 省英文
     */
    @Column(name = "`province_english`")
    private String provinceEnglish;

    /**
     * 省中文
     */
    @Column(name = "`province_chinese`")
    private String provinceChinese;

    /**
     * 所属上级市英文
     */
    @Column(name = "`parent_city_english`")
    private String parentCityEnglish;

    /**
     * 所属上级市中文
     */
    @Column(name = "`parent_city_chinese`")
    private String parentCityChinese;

    /**
     * 维度
     */
    @Column(name = "`latitude`")
    private String latitude;

    /**
     * 经度
     */
    @Column(name = "`longitude`")
    private String longitude;

    /**
     * 编码
     */
    @Column(name = "`adcode`")
    private String adcode;

    /**
     * 预留字段
     */
    @Column(name = "`reserve_1`")
    private String reserve1;

    /**
     * 预留字段2
     */
    @Column(name = "`reserve_2`")
    private String reserve2;

    /**
     * 预留字段3
     */
    @Column(name = "`reserve_3`")
    private String reserve3;

    /**
     * 获取城市地区编码
     *
     * @return city_code - 城市地区编码
     */
    public String getCityCode() {
        return cityCode;
    }

    /**
     * 设置城市地区编码
     *
     * @param cityCode 城市地区编码
     */
    public void setCityCode(String cityCode) {
        this.cityCode = cityCode == null ? null : cityCode.trim();
    }

    /**
     * 获取城市英文
     *
     * @return city_english - 城市英文
     */
    public String getCityEnglish() {
        return cityEnglish;
    }

    /**
     * 设置城市英文
     *
     * @param cityEnglish 城市英文
     */
    public void setCityEnglish(String cityEnglish) {
        this.cityEnglish = cityEnglish == null ? null : cityEnglish.trim();
    }

    /**
     * 获取城市中文
     *
     * @return city_chinese - 城市中文
     */
    public String getCityChinese() {
        return cityChinese;
    }

    /**
     * 设置城市中文
     *
     * @param cityChinese 城市中文
     */
    public void setCityChinese(String cityChinese) {
        this.cityChinese = cityChinese == null ? null : cityChinese.trim();
    }

    /**
     * 获取国家代码
     *
     * @return country_code - 国家代码
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * 设置国家代码
     *
     * @param countryCode 国家代码
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode == null ? null : countryCode.trim();
    }

    /**
     * 获取国家英文
     *
     * @return country_english - 国家英文
     */
    public String getCountryEnglish() {
        return countryEnglish;
    }

    /**
     * 设置国家英文
     *
     * @param countryEnglish 国家英文
     */
    public void setCountryEnglish(String countryEnglish) {
        this.countryEnglish = countryEnglish == null ? null : countryEnglish.trim();
    }

    /**
     * 获取国家中文
     *
     * @return country_chinese - 国家中文
     */
    public String getCountryChinese() {
        return countryChinese;
    }

    /**
     * 设置国家中文
     *
     * @param countryChinese 国家中文
     */
    public void setCountryChinese(String countryChinese) {
        this.countryChinese = countryChinese == null ? null : countryChinese.trim();
    }

    /**
     * 获取省英文
     *
     * @return province_english - 省英文
     */
    public String getProvinceEnglish() {
        return provinceEnglish;
    }

    /**
     * 设置省英文
     *
     * @param provinceEnglish 省英文
     */
    public void setProvinceEnglish(String provinceEnglish) {
        this.provinceEnglish = provinceEnglish == null ? null : provinceEnglish.trim();
    }

    /**
     * 获取省中文
     *
     * @return province_chinese - 省中文
     */
    public String getProvinceChinese() {
        return provinceChinese;
    }

    /**
     * 设置省中文
     *
     * @param provinceChinese 省中文
     */
    public void setProvinceChinese(String provinceChinese) {
        this.provinceChinese = provinceChinese == null ? null : provinceChinese.trim();
    }

    /**
     * 获取所属上级市英文
     *
     * @return parent_city_english - 所属上级市英文
     */
    public String getParentCityEnglish() {
        return parentCityEnglish;
    }

    /**
     * 设置所属上级市英文
     *
     * @param parentCityEnglish 所属上级市英文
     */
    public void setParentCityEnglish(String parentCityEnglish) {
        this.parentCityEnglish = parentCityEnglish == null ? null : parentCityEnglish.trim();
    }

    /**
     * 获取所属上级市中文
     *
     * @return parent_city_chinese - 所属上级市中文
     */
    public String getParentCityChinese() {
        return parentCityChinese;
    }

    /**
     * 设置所属上级市中文
     *
     * @param parentCityChinese 所属上级市中文
     */
    public void setParentCityChinese(String parentCityChinese) {
        this.parentCityChinese = parentCityChinese == null ? null : parentCityChinese.trim();
    }

    /**
     * 获取维度
     *
     * @return latitude - 维度
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * 设置维度
     *
     * @param latitude 维度
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude == null ? null : latitude.trim();
    }

    /**
     * 获取经度
     *
     * @return longitude - 经度
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * 设置经度
     *
     * @param longitude 经度
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude == null ? null : longitude.trim();
    }

    /**
     * 获取编码
     *
     * @return adcode - 编码
     */
    public String getAdcode() {
        return adcode;
    }

    /**
     * 设置编码
     *
     * @param adcode 编码
     */
    public void setAdcode(String adcode) {
        this.adcode = adcode == null ? null : adcode.trim();
    }

    /**
     * 获取预留字段
     *
     * @return reserve_1 - 预留字段
     */
    public String getReserve1() {
        return reserve1;
    }

    /**
     * 设置预留字段
     *
     * @param reserve1 预留字段
     */
    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1 == null ? null : reserve1.trim();
    }

    /**
     * 获取预留字段2
     *
     * @return reserve_2 - 预留字段2
     */
    public String getReserve2() {
        return reserve2;
    }

    /**
     * 设置预留字段2
     *
     * @param reserve2 预留字段2
     */
    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2 == null ? null : reserve2.trim();
    }

    /**
     * 获取预留字段3
     *
     * @return reserve_3 - 预留字段3
     */
    public String getReserve3() {
        return reserve3;
    }

    /**
     * 设置预留字段3
     *
     * @param reserve3 预留字段3
     */
    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3 == null ? null : reserve3.trim();
    }
}