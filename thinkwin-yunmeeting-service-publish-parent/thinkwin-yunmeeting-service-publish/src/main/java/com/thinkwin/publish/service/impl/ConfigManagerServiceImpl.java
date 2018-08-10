package com.thinkwin.publish.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.heweather.sdk.api.HeConfig;
import com.heweather.sdk.api.HeWeather;
import com.heweather.sdk.bean.weather.forecast.Forecast;
import com.heweather.sdk.util.Callback;
import com.thinkwin.common.model.db.CityDictionary;
import com.thinkwin.common.utils.PropertiesUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.publish.WeatherVo;
import com.thinkwin.core.service.SaasSetingService;
import com.thinkwin.dictionary.service.DictionaryService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.publish.service.ConfigManagerService;
import com.thinkwin.publish.util.PublishConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 类说明：
 *
 * @author lining
 * @version 1.0
 * @Date 2018/5/31
 */
@Service("configManagerService")
public class ConfigManagerServiceImpl implements ConfigManagerService {

    private static Logger logger = LoggerFactory.getLogger(ConfigManagerServiceImpl.class);

    @Resource
    SaasSetingService saasSetingService;
    @Resource
    FileUploadService fileUploadService;
    @Resource
    DictionaryService dictionaryService;


    private static String WEATHER_FREFIX="yunmeeting_weather:";
    private static String WEATHER_SUFFIX_AM="_AM";
    private static String WEATHER_SUFFIX_PM="_PM";
    private static int WEATHER_12CLOCK=12;
    private static String FORMAT="HH";
    private static int SECONDS=43200; //60*60*12 12个小时失效

    /**
     * 获取天气配置
     *
     * @return
     */
    @Override
    public String getWeatherConfig() {

        return this.saasSetingService.get(PublishConstant.weatherKey);
    }

    /**
     * 保存天气配置
     *
     * @param weatherConfig
     * @return
     */
    @Override
    public void setWeatherConfig(String weatherConfig) {

        this.saasSetingService.set(PublishConstant.weatherKey,weatherConfig);
    }

    /**
     * 保存终端APP地址
     *
     * @param terminalAppUrl
     * @return
     */
    @Override
    public void setTerminalAppUrlConfig(String terminalAppUrl) {

        this.saasSetingService.set(PublishConstant.terminalAppUrl,terminalAppUrl);
    }

    /**
     * 获取终端APP地址
     *
     * @return
     */
    @Override
    public String getTerminalAppUrlConfig() {

        return this.saasSetingService.get(PublishConstant.terminalAppUrl);
    }

    /**
     * 获取默认终端背景图片地址
     *
     * @return
     */
    @Override
    public String getTerminalBackgrounp() {
        String terminalBackgrounp=null;
        String imageId=this.saasSetingService.get(PublishConstant.terminalBackgrounp);
        if(StringUtils.isNotBlank(imageId)) {
            terminalBackgrounp = this.fileUploadService.selectTenementByFile(imageId);
        }
        return terminalBackgrounp;
    }

    /**
     * 保存背景图片
     *
     * @param terminalBackgrounp
     * @return
     */
    @Override
    public void setTerminalBackgrounp(String terminalBackgrounp) {

        this.saasSetingService.set(PublishConstant.terminalBackgrounp,terminalBackgrounp);

    }

    /**
     * 根据城市查询该城市天气
     *
     * @param location
     * @param parent_city
     * @return
     */
    @Override
    public WeatherVo getWeather(String location, String parent_city) {

        WeatherVo weatherVo=null;
        CityDictionary cityDictionary=this.dictionaryService.selectCityAndParentCity(location,parent_city);
        if(null!=cityDictionary){
            weatherVo=this.getWeather(cityDictionary.getCityCode());
        }
        return weatherVo;
    }

    /**
     * 根据城市ID查询该城市天气
     *
     * @param cityId
     */
    @Override
    public WeatherVo getWeather(String cityId) {

        //判断当是时间是AM还是PM
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        int now = 0;
        try {
            String hour=format.format(new Date());
            now=Integer.parseInt(hour);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //组合Redis中该城市Key值
        String weatherKey=null;
        WeatherVo weatherValue=null;
        if(now<=WEATHER_12CLOCK){
            weatherKey=WEATHER_FREFIX+cityId+WEATHER_SUFFIX_AM;
            weatherValue=this.getHeweather(cityId,weatherKey);
        }else{
            weatherKey=WEATHER_FREFIX+cityId+WEATHER_SUFFIX_PM;
            weatherValue=this.getHeweather(cityId,weatherKey);
        }

          return weatherValue;
    }

    /**
     * 访问和风天气服务
     * @param cityId
     * @param weatherKey
     * @return
     */
    public WeatherVo getHeweather(String cityId,String weatherKey){

        String value = RedisUtil.get(weatherKey);
        if(StringUtils.isBlank(value)){
            String heweatherUserId= PropertiesUtil.getString("weather.userId");
            String heweatherKey=this.getWeatherConfig();
            String heweatherPaid=PropertiesUtil.getString("weather.paid");

            logger.debug("天气预报配置：{} ====== {}", heweatherUserId, heweatherKey);
            if(StringUtils.isNotBlank(heweatherUserId) && StringUtils.isNotBlank(heweatherKey)){
                //初始化参数
                HeConfig.init(heweatherUserId,heweatherKey);
                if(heweatherPaid.equals("1")){
                    //切换至中国付费服务域名
                    HeConfig.switchToCNBusinessServerNode();
                }else{
                    //切换至普通服务域名
                    HeConfig.switchToFreeServerNode();
                }

                //实时天气
                HeWeather.s6Forecast(cityId,new Callback<List<Forecast>>() {
                    @Override
                    public void onSuccess(List<Forecast> nows){
	                    logger.debug("天气预报结果：{}", nows);
	                    RedisUtil.set(weatherKey,JSON.toJSONString(nows),SECONDS);
                    }
                    @Override
                    public void onError(Throwable e) {
                        logger.error("天气预报", e);
                    }
                });
            }
        }


        long startTime = (new Date()).getTime();
	    WeatherVo weatherVo=new WeatherVo();
	    while(true){
	        if((System.currentTimeMillis() - startTime) > 5000){
	        	logger.error("查询天气预报超时");
	        	break;
	        }

		    value = RedisUtil.get(weatherKey);
		    JSONArray jsonArray = JSON.parseArray(value);

		    if(jsonArray == null || jsonArray.size() == 0){
		        try {
			        Thread.sleep(500);
		        } catch (InterruptedException e) {
			        e.printStackTrace();
			        break;
		        }
	        }
	        //logger.info("#####Weather:"+jsonArray.toJSONString());
	        if(null!=jsonArray && jsonArray.size()>0){
		        Forecast forecast=jsonArray.getObject(0,Forecast.class);
		        weatherVo.setCid(forecast.getBasic().getCid());
		        weatherVo.setLocation(forecast.getBasic().getLocation());
		        weatherVo.setParent_city(forecast.getBasic().getParent_city());
		        weatherVo.setAdmin_area(forecast.getBasic().getAdmin_area());
		        if(null!=forecast.getDaily_forecast() && forecast.getDaily_forecast().size()>0){
			        weatherVo.setTmp_max(forecast.getDaily_forecast().get(0).getTmp_max());
			        weatherVo.setTmp_min(forecast.getDaily_forecast().get(0).getTmp_min());
			        weatherVo.setCond_code_d(forecast.getDaily_forecast().get(0).getCond_code_d());
			        weatherVo.setCond_code_n(forecast.getDaily_forecast().get(0).getCond_code_n());
			        weatherVo.setCond_txt_d(forecast.getDaily_forecast().get(0).getCond_txt_d());
			        weatherVo.setCond_txt_n(forecast.getDaily_forecast().get(0).getCond_txt_n());
			        weatherVo.setWind_dir(forecast.getDaily_forecast().get(0).getWind_dir());
			        weatherVo.setWind_sc(forecast.getDaily_forecast().get(0).getWind_sc());
			        weatherVo.setVis(forecast.getDaily_forecast().get(0).getVis());
		        }
	        }
        }


        return weatherVo;
    }

    /**
     * 会议签到二维码地址
     *
     * @return
     */
    @Override
    public String getMeetingSignQrUrl() {
        return this.saasSetingService.get(PublishConstant.meetingSignQrUrl);
    }
}
