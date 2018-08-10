package com.thinkwin.common.vo.publish;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/7 0007.
 */
public class Weather implements Serializable {


    private static final long serialVersionUID = 3882136885773231177L;
    private String  city;//	当前城市
    private String temperature;//		气温
    private String weather;//		天气
    private String  dayTemperature;//		当天气温
    private String  direction;//		风向

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getDayTemperature() {
        return dayTemperature;
    }

    public void setDayTemperature(String dayTemperature) {
        this.dayTemperature = dayTemperature;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
