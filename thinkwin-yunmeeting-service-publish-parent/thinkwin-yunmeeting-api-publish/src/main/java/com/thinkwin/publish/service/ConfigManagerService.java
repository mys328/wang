package com.thinkwin.publish.service;

import com.thinkwin.common.vo.publish.WeatherVo;

/**
 * 类说明：
 *
 * @author lining
 * @version 1.0
 * @Date 2018/5/31
 */
public interface ConfigManagerService {

    /**
     * 获取天气配置
     * @return
     */
    String getWeatherConfig();

    /**
     * 保存终端APP地址
     * @param terminalAppUrl
     * @return
     */
    void setTerminalAppUrlConfig(String terminalAppUrl);

    /**
     * 获取终端APP地址
     * @return
     */
    String getTerminalAppUrlConfig();

    /**
     * 保存天气配置
     * @param weatherConfig
     * @return
     */
    void setWeatherConfig(String weatherConfig);

    /**
     * 获取背景图片
     * @return
     */
    String getTerminalBackgrounp();

    /**
     * 保存背景图片
     * @param terminalBackgrounp
     * @return
     */
    void setTerminalBackgrounp(String terminalBackgrounp);

    /**
     * 根据城市查询该城市天气
     * @param location
     * @param parent_city
     * @return
     */
    WeatherVo getWeather(String location,String parent_city);

    /**
     * 根据城市ID查询该城市天气
     * @param cityId
     */
    WeatherVo getWeather(String cityId);

    /**
     * 会议签到二维码地址
     * @return
     */
    String getMeetingSignQrUrl();


}
