package com.thinkwin.console.web.controller;

import com.thinkwin.TenantUserVo;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.FileUploadUtil;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.common.vo.FileVo;
import com.thinkwin.common.vo.publish.WeatherVo;
import com.thinkwin.console.constant.ConsoleConstant;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.publish.service.ConfigManagerService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类说明：
 *      配置管理接口
 * @author lining
 * @version 1.0
 * @Date 2018/5/31
 */
@Controller
@RequestMapping("/configManager")
public class ConfigManagerController {

    private final Logger log = LoggerFactory.getLogger(ConfigManagerController.class);

    @Resource
    ConfigManagerService configManagerService;
    @Resource
    FileUploadService fileUploadService;

    /**
     * 配置管理菜单接口
     * @return
     */
    @RequestMapping("/page")
    public String page(){
        return "console/management";
    }


    /**
     * 获取配置值
     * @return
     */
    @RequestMapping("/getConfig")
    @ResponseBody
    public ResponseResult getWeatherConfig(){
        ResponseResult responseResult=new ResponseResult();
        Map<String,String> weatherMap=new HashMap<>();
        String weatherConfig=this.configManagerService.getWeatherConfig();
        weatherMap.put("isWeather","1");
        weatherMap.put("weatherConfig",weatherConfig);
        String terminalBackgrounp=this.configManagerService.getTerminalBackgrounp();
        weatherMap.put("isBackgrounp","1");
        weatherMap.put("terminalBackgrounp", terminalBackgrounp);
        String terminalAppUrl=this.configManagerService.getTerminalAppUrlConfig();
        weatherMap.put("terminalAppUrl", terminalAppUrl);

        responseResult.setIfSuc(1);
        responseResult.setData(weatherMap);
        return responseResult;
    }

    /**
     * 设置天气组件配置
     * @return
     */
    @RequestMapping("/setWeatherConfig")
    @ResponseBody
    public ResponseResult setWeatherConfig(String weatherConfig){
        ResponseResult responseResult=new ResponseResult();
        try {
            if (StringUtils.isNotBlank(weatherConfig)) {
                this.configManagerService.setWeatherConfig(weatherConfig);
                responseResult.setIfSuc(1);
                responseResult.setMsg("设置成功");
            }else{
                responseResult.setIfSuc(0);
                responseResult.setMsg("天气组件不能为空");
            }
        }catch (Exception e){
            responseResult.setIfSuc(0);
            responseResult.setMsg("设置失败");
        }
        return responseResult;
    }

    /**
     * 设置终端APP地址
     * @return
     */
    @RequestMapping("/terminalAppUrlConfig")
    @ResponseBody
    public ResponseResult setTerminalAppUrlConfig(String terminalAppUrl){
        ResponseResult responseResult=new ResponseResult();
        try {
            if (StringUtils.isNotBlank(terminalAppUrl)) {
                this.configManagerService.setTerminalAppUrlConfig(terminalAppUrl);
                responseResult.setIfSuc(1);
                responseResult.setMsg("设置成功");
            }else{
                responseResult.setIfSuc(0);
                responseResult.setMsg("APP地址不能为空");
            }
        }catch (Exception e){
            responseResult.setIfSuc(0);
            responseResult.setMsg("设置失败");
        }
        return responseResult;
    }

    /**
     * 设置终端背景图片
     * @return
     */
    @RequestMapping(value = "/setTerminalBackgrounp",method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult setTerminalBackgrounp(MultipartFile file){
        ResponseResult responseResult=new ResponseResult();
        TenantUserVo userVo = TenantContext.getUserInfo();
        try {
            if(null!=file) {
                byte[] bytes = file.getBytes();
                String  ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                List<FastdfsVo> vos = FileUploadUtil.fileUpload(null, bytes, ext);
                FileVo fileVo = fileUploadService.insertFileUpload(ConsoleConstant.source, userVo.getUserId(), null, null, ext, vos);
                if(null!=fileVo){
                    this.configManagerService.setTerminalBackgrounp(fileVo.getId());
                    responseResult.setIfSuc(1);
                    responseResult.setMsg("设置成功");
                }
            }
            }catch (Exception e){
            responseResult.setIfSuc(0);
            responseResult.setMsg("设置失败");
        }
        return responseResult;
    }

    @RequestMapping("/test")
    @ResponseBody
    public ResponseResult test(String location,String parent_city) {
        ResponseResult responseResult = new ResponseResult();

        WeatherVo weatherVo=this.configManagerService.getWeather(location,parent_city);

        responseResult.setData(weatherVo);
        return responseResult;

    }


}
