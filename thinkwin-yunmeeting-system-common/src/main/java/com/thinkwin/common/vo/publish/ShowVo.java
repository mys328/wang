package com.thinkwin.common.vo.publish;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/11 0011.
 */
public class ShowVo implements Serializable{


    private static final long serialVersionUID = 466488566453652087L;
    /**
     * 模板路径
     */
    private String template;

    /**
     * 会议室信息
     */
    private Rooms rooms;

    /**
     * 当前会议的信息
     */
    private MeetingInfo current;
    /**
     * 下一个会议的信息
     */
    private MeetingInfo next;
    /**
     * 天气
     */
    private WeatherVo weather;


    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Rooms getRooms() {
        return rooms;
    }

    public void setRooms(Rooms rooms) {
        this.rooms = rooms;
    }

    public MeetingInfo getCurrent() {
        return current;
    }

    public void setCurrent(MeetingInfo current) {
        this.current = current;
    }

    public MeetingInfo getNext() {
        return next;
    }

    public void setNext(MeetingInfo next) {
        this.next = next;
    }

    public WeatherVo getWeather() {
        return weather;
    }

    public void setWeather(WeatherVo weather) {
        this.weather = weather;
    }
}
