package com.thinkwin.web.terminalController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thinkwin.TenantUserVo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.model.core.SaasSetting;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.CityDictionary;
import com.thinkwin.common.model.db.InfoReleaseTerminal;
import com.thinkwin.common.model.db.SysDictionary;
import com.thinkwin.common.model.log.TerminalLog;
import com.thinkwin.common.model.publish.PlatformClientVersionUpgradeRecorder;
import com.thinkwin.common.model.publish.PlatformTenantTerminalMiddle;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.TerminalInfoVo.*;
import com.thinkwin.common.vo.city.CountyCityVo;
import com.thinkwin.common.vo.city.SuperiorCityVo;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.dictionary.service.DictionaryService;
import com.thinkwin.log.service.TerminalLogService;
import com.thinkwin.publish.service.ConfigManagerService;
import com.thinkwin.publish.service.PlatformClientVersionUpgradeRecorderService;
import com.thinkwin.publish.service.PlatformTenantTerminalMiddleService;
import com.thinkwin.push.service.ChannelHelperService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.InfoReleaseTerminalService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

/**
 * 类名: TerminalController </br>
 * 描述: 终端controller</br>
 * 开发人员： weining </br>
 * 创建时间：  2018/5/2 </br>
 */
@Controller
public class TerminalLoginController {

    @Resource
    InfoReleaseTerminalService infoReleaseTerminalService;
    @Resource
    SaasTenantService saasTenantCoreService;
    @Resource
    DictionaryService dictionaryService;
    @Resource
    PlatformClientVersionUpgradeRecorderService platformClientVersionUpgradeRecorderService;
    @Resource
    PlatformTenantTerminalMiddleService platformTenantTerminalMiddleService;
    @Resource
    TerminalLogService terminalLogService;
    @Resource
    ConfigManagerService configManagerService;
    @Resource
    ChannelHelperService channelHelperService;

    @RequestMapping(value = "/system/offline",method = RequestMethod.GET)
    @ResponseBody
    public Object offline(String tenantId,String terminalId){
        boolean offline = channelHelperService.offline(tenantId, terminalId);
        if(offline){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),BusinessExceptionStatusEnum.Success.getCode());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.DataNull.getDescription(),BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /**
     * 方法名：getPushIp</br>
     * 描述：获取push Ip接口</br>
     * 参数：[request]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/system/getPushIp",method = RequestMethod.POST)
    @ResponseBody
    public Object getPushIp(){
        //获取Ip
        SaasSetting saasSetting = saasTenantCoreService.selectCommodityConfigInfo("push.config");
        if(null != saasSetting){
            String content = saasSetting.getContent();
            JSONObject contents = JSON.parseObject(content);
            String ip = (String) contents.get("ip");
            String port = (String) contents.get("port");
            //获取App下载地址
            String terminalAppUrl = configManagerService.getTerminalAppUrlConfig();
            Map<String,Object> map = new HashMap<>();
            map.put("ip",ip);
            map.put("port",port);
            map.put("appUrl",terminalAppUrl);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),map,BusinessExceptionStatusEnum.Success.getCode());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.DataNull.getDescription(),BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /**
     * 方法名：perfectTerminalInfo</br>
     * 描述：完善终端信息接口</br>
     * 参数：[terminalInfoVo]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/terminal/perfectTerminalInfo" ,method = RequestMethod.POST)
    @ResponseBody
    public Object perfectTerminalInfo(TerminalInfoVo terminalInfoVo){
        if(null != terminalInfoVo){
            String tenantId = terminalInfoVo.getTenantId();
            String hardwareId = terminalInfoVo.getHardwareId();
            if(StringUtils.isNotBlank(hardwareId) && StringUtils.isNotBlank(tenantId)){
                //TenantContext.setTenantId(tenantId);
                InfoReleaseTerminal infoReleaseTerminal = infoReleaseTerminalService.selectInfoReleaseTerminalByHardwareId(hardwareId);
                if(null != infoReleaseTerminal){
                    TenantUserVo userInfo = TenantContext.getUserInfo();
                    if(null != userInfo){
                        String userId = userInfo.getUserId();
                        if(StringUtils.isNotBlank(userId)){
                            infoReleaseTerminal.setCreater(userId);
                        }
                    }
                    //处理终端信息Vo的数据
                    infoReleaseTerminal = handleInfoReleaseTerminalVoParam(terminalInfoVo,infoReleaseTerminal);
                    if(null != infoReleaseTerminal){
                        boolean b = infoReleaseTerminalService.updateInfoReleaseTerminal(infoReleaseTerminal);
                        if(b){
                            //维护租户终端关系表的终端名称字段
                            if(StringUtils.isNotBlank(infoReleaseTerminal.getTerminalName())){
                                PlatformTenantTerminalMiddle platformTenantTerminalMiddle = new PlatformTenantTerminalMiddle();
                                platformTenantTerminalMiddle.setId(infoReleaseTerminal.getId());
                                platformTenantTerminalMiddle.setTerminalName(infoReleaseTerminal.getTerminalName());
                                platformTenantTerminalMiddleService.updatePTenantTerminalMByEntity(platformTenantTerminalMiddle);
                            }
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,"完善成功", BusinessExceptionStatusEnum.Success.getCode());
                        }else{
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.SysError.getDescription(),BusinessExceptionStatusEnum.SysError.getCode());
                        }
                    }
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.DataNull.getDescription(),BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 方法名：registTerminal</br>
     * 描述：注册终端</br>
     * 参数：[terminalMark, tenantId]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/terminal/registTerminal",method = RequestMethod.POST)
    @ResponseBody
    public Object registTerminal(String hardwareId){
        TerminalLog log = new TerminalLog();
        try {
            if(StringUtils.isBlank(hardwareId)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
            }
            //获取用户Id
            TenantUserVo userInfo = TenantContext.getUserInfo();
            String userId = userInfo.getUserId();
            String tenantId = userInfo.getTenantId();
            log.setTenantId(userInfo.getTenantId());
            log.setOperateUserId(userId);
            log.setOperator(userInfo.getUserName());
            log.setMethodname(Thread.currentThread().getStackTrace()[1].getMethodName());
            log.setMethodarg(hardwareId);
            log.setClassname(this.getClass().getName());
            log.setBusinesstype(BusinessType.terminalRegisterOp.toString());
            log.setEventtype(EventType.terminal_register.toString());
            log.setBusinesstime(new Date());
            log.setContent("终端注册");
            //查询终端显示屏是否绑定租户
            String terminalId = "";
            boolean b = false;
            PlatformTenantTerminalMiddle platformTenantTerminalMiddle = platformTenantTerminalMiddleService.selectPTenantTerminalMByHardwareId(hardwareId);
            if (null == platformTenantTerminalMiddle) {    //为空  新建立租户和显示屏关系
                terminalId = CreateUUIdUtil.Uuid();
                //增加租户终端中间表数据
                platformTenantTerminalMiddle = new PlatformTenantTerminalMiddle();
                platformTenantTerminalMiddle.setHardwareId(hardwareId);
                platformTenantTerminalMiddle.setStatus("1");
                platformTenantTerminalMiddle.setTenantId(tenantId);
                platformTenantTerminalMiddle.setId(CreateUUIdUtil.Uuid());
                platformTenantTerminalMiddle.setTerminalId(terminalId);
                platformTenantTerminalMiddle.setTerminalName(hardwareId);
                boolean b1 = platformTenantTerminalMiddleService.insertPTenantTerminalMByEntity(platformTenantTerminalMiddle);
                if(b1) {
                    InfoReleaseTerminal infoReleaseTerminal = new InfoReleaseTerminal();
                    infoReleaseTerminal.setId(terminalId);
                    infoReleaseTerminal.setCreater(userId);
                    infoReleaseTerminal.setHardwareId(hardwareId);
                    infoReleaseTerminal.setTenantId(tenantId);
                    infoReleaseTerminal.setTerminalName(hardwareId);
                    log.setTerminalId(terminalId);
                    log.setTerminalName(hardwareId);
                    b = infoReleaseTerminalService.insertInfoReleaseTerminal(infoReleaseTerminal);
                }
            }else{//不为空  重新新建立租户和显示屏关系
                log.setTerminalId(platformTenantTerminalMiddle.getTerminalId());
                log.setTerminalName(platformTenantTerminalMiddle.getTerminalName());
                String tenantIdOld = platformTenantTerminalMiddle.getTenantId(); //获取终端原租户Id
                terminalId = platformTenantTerminalMiddle.getTerminalId();
                if(!tenantId.equals(tenantIdOld)){//如果租户为同一租户下面  则不做修改
                    platformTenantTerminalMiddle.setTenantId(tenantId);
                    platformTenantTerminalMiddleService.updatePTenantTerminalMByEntity(platformTenantTerminalMiddle);//修改租户终端中间表
                    TenantContext.setTenantId(tenantIdOld);//切换到终端原租户
                    InfoReleaseTerminal infoReleaseTerminal = infoReleaseTerminalService.selectInfoReleaseTerminalById(terminalId);
                    if(null != infoReleaseTerminal){
                        boolean b1 = infoReleaseTerminalService.delectInfoReleaseTerminal(terminalId);//删除终端原租户信息
                        if(b1){
                            //查询原终端租户数量
                            List<InfoReleaseTerminal> infoReleaseTerminals = infoReleaseTerminalService.selectInfoReleaseTerminalByTenantId(tenantIdOld);
                            SaasTenant saasTenant = new SaasTenant();
                            saasTenant.setId(tenantIdOld);
                            if(null == infoReleaseTerminals || infoReleaseTerminals.size() == 0){
                                saasTenant.setTerminalCount(0);
                            }else {
                                saasTenant.setTerminalCount(infoReleaseTerminals.size());
                            }
                            saasTenant.setModifyTime(new Date());
                            //更新老租户终端数量
                            saasTenantCoreService.updateSaasTenantService(saasTenant);

                            TenantContext.setTenantId(tenantId);//切换到终端新注册租户
                            InfoReleaseTerminal infoReleaseTerminal1 = new InfoReleaseTerminal();
                            infoReleaseTerminal1.setId(terminalId);
                            infoReleaseTerminal1.setCreater(userId);
                            infoReleaseTerminal1.setHardwareId(hardwareId);
                            infoReleaseTerminal1.setTenantId(tenantId);
                            infoReleaseTerminal1.setTerminalName(hardwareId);
                            b = infoReleaseTerminalService.insertInfoReleaseTerminal(infoReleaseTerminal1);//重新关联新租户
                        }
                    }
                }else{
                    b = true;
                }
            }
            if (b) {
                //查询终端数量
                List<InfoReleaseTerminal> infoReleaseTerminals = infoReleaseTerminalService.selectInfoReleaseTerminalByTenantId(tenantId);
                if (null != infoReleaseTerminals && infoReleaseTerminals.size() > 0) {
                    //增加已注册终端数量到租户表
                    SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                    if (null != saasTenant) {
                        saasTenant.setTerminalCount(infoReleaseTerminals.size());
                        saasTenant.setModifyTime(new Date());
                        if (StringUtils.isBlank(saasTenant.getPublicKey())) {
                            //生成Rsa密钥对
                            try {
                                KeyPair rsa = CreateRSAUtil.genKeyPair(1024, "RSA");
                                PrivateKey privateKey = rsa.getPrivate();    //私钥
                                PublicKey publicKey = rsa.getPublic();       //公钥
                                String privateKeyStr = CreateRSAUtil.encryptBASE64(privateKey.getEncoded());
                                String publicKeyStr = CreateRSAUtil.encryptBASE64(publicKey.getEncoded());
                                saasTenant.setPrivateKey(privateKeyStr);
                                saasTenant.setPublicKey(publicKeyStr);
                                saasTenant.setSignCode(CreateUUIdUtil.Uuid());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        saasTenantCoreService.updateSaasTenantService(saasTenant);
                    }
                }
                //保存成功的时候把信息存到redis
                Map<String, Object> map = new HashMap<>();
                map.put("tenantId", tenantId);
                map.put("terminalMark", hardwareId);
                map.put("terminalId", terminalId);
                String s = JSON.toJSONString(map);
                RedisUtil.set(hardwareId + "_register_terminal", s);
                //写入终端日志
                log.setStatus(1);
                terminalLogService.addTerminalLog(log);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "注册成功", BusinessExceptionStatusEnum.Success.getCode());
            }
        }catch (Exception e){
            log.setStatus(0);
            log.setResult(e.getMessage());
            terminalLogService.addTerminalLog(log);
            e.printStackTrace();
        }
        log.setStatus(0);
        terminalLogService.addTerminalLog(log);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "注册失败", BusinessExceptionStatusEnum.Failure.getCode());
    }

    /**
     * 方法名：getTerminalNum</br>
     * 描述：获取已注册终端数量</br>
     * 参数：[tenantId, token]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/terminal/getTerminalNum" ,method = RequestMethod.POST)
    @ResponseBody
    public Object getTerminalNum(){
        String tenantId = TenantContext.getTenantId();
        if(StringUtils.isNotBlank(tenantId)){
            Map<String,Object> map = new HashMap<>();
            List<InfoReleaseTerminal> infoReleaseTerminals = infoReleaseTerminalService.selectInfoReleaseTerminalByTenantId(tenantId);
            if(null != infoReleaseTerminals && infoReleaseTerminals.size() > 0){
                map.put("terminalNum",infoReleaseTerminals.size());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),map,BusinessExceptionStatusEnum.Success.getCode());
            }else{
                map.put("terminalNum",0);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),map,BusinessExceptionStatusEnum.Success.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 方法名：obtainPublicKey</br>
     * 描述：获取公钥</br>
     * 参数：[]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/system/obtainPublicKey" , method = RequestMethod.POST)
    @ResponseBody
    public Object obtainPublicKey(String terminalMark){
        try {
            //先生成随机码
            String randomCode = CreateUUIdUtil.Uuid();
            //根据终端唯一标识查询redis
            String tenantInfo = RedisUtil.get(terminalMark + "_register_terminal");
            if(null == tenantInfo){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.DataNull.getDescription(),BusinessExceptionStatusEnum.DataNull.getCode());
            }
            JSONObject jsonObject = JSON.parseObject(tenantInfo);
            String tenantId = (String) jsonObject.get("tenantId");
            //查询租户库  是否已经存在公钥私钥
            SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
            if(null != saasTenant){
                if(saasTenant.getStatus() == 2){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"企业已解散");
                }
                if(StringUtils.isNotBlank(saasTenant.getPublicKey())){
                    String publicKeyStr = saasTenant.getPublicKey();
                    String privateKeyStr = saasTenant.getPrivateKey();
                    Map<String,Object> map = new HashMap<>();
                    map.put("privateKey",privateKeyStr);
                    map.put("publicKey",publicKeyStr);
                    //把key存入redis
                    String s = JSON.toJSONString(map);
                    RedisUtil.set(randomCode,s,600);
                    map.remove("privateKey");
                    map.put("randomCode",randomCode);
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),map,BusinessExceptionStatusEnum.Success.getCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.DataNull.getDescription(),BusinessExceptionStatusEnum.DataNull.getCode());
    }


    /**
     * 方法名：decryptTerminalMark</br>
     * 描述：解密终端标识</br>
     * 参数：[terminalMark, randomCode]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/system/decryptTerminalMark",method = RequestMethod.POST)
    @ResponseBody
    public Object decryptTerminalMark(String terminalMark,String randomCode){
        if(StringUtils.isNotBlank(terminalMark) && StringUtils.isNotBlank(randomCode)){
            try {
                //根据randomCode获取Redis缓存私钥
                String keyStr = RedisUtil.get(randomCode);
                JSONObject keyJsonObject = JSON.parseObject(keyStr);
                String privateKeyStr = (String) keyJsonObject.get("privateKey");
                if(StringUtils.isNotBlank(privateKeyStr)){
                    //把私钥串转成privateKey类型
                    PrivateKey privateKey = CreateRSAUtil.getPrivateKey(privateKeyStr);
                    //根据私钥解密终端Id
                    byte[] decrypt = CreateRSAUtil.decrypt(CreateRSAUtil.decryptBASE64(terminalMark), privateKey);
                    String content = new String(decrypt);
                    System.out.println("解密后++++++++++"+content);
                    String tenantInfo = RedisUtil.get(content + "_register_terminal");
                    if (StringUtils.isBlank(tenantInfo)) {
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.TerminalNotRegister.getDescription(),BusinessExceptionStatusEnum.TerminalNotRegister.getCode());
                    }
                    JSONObject jsonObject = JSON.parseObject(tenantInfo);
                    String tenantId = (String) jsonObject.get("tenantId");
                    String terminalId = (String) jsonObject.get("terminalId");
                    Map<String,String> map = new HashMap<>();
                    map.put("tenantId",tenantId);
                    map.put("terminalId",terminalId);
                    SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                    if(null != saasTenant){
                        map.put("signCode",saasTenant.getSignCode());
                    }
                    //删除注册的租户信息
                    //RedisUtil.remove(content + "_register_terminal");
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),map,BusinessExceptionStatusEnum.Success.getCode());
                }
                //删除随机码
                RedisUtil.remove(randomCode);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.DataNull.getDescription(),BusinessExceptionStatusEnum.DataNull.getCode());
            } catch (Exception e) {
                if (null != e.getMessage() && e.getMessage().equals("Decryption error")) {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DecryptionError.getDescription(),BusinessExceptionStatusEnum.DecryptionError.getCode());
                } else {
                    e.printStackTrace();
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SysError.getDescription(), BusinessExceptionStatusEnum.SysError.getCode());
                }
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 方法名：getCityList</br>
     * 描述：按顺序查询城市列表</br>
     * 参数：[city]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/terminal/getCityList", method = RequestMethod.POST)
    @ResponseBody
    public Object getCityList(String city){
        Map<String, Object> map = new HashMap<>();
        //如果city不为空按照上级市查询县
        if(StringUtils.isNotBlank(city)){
            List<CityDictionary> countyListByParentCity = dictionaryService.selectCountyListByParentCity(city);
            List<CountyCityVo> countyCityVos = new ArrayList<>();
            for (CityDictionary cityDictionary : countyListByParentCity) {
                CountyCityVo countyCityVo = new CountyCityVo();
                String cityChinese = cityDictionary.getCityChinese();
                if (cityChinese.equals(city)) {
                    countyCityVo.setCityPinYin("");
                    countyCityVo.setCityName("全城");
                    countyCityVos.add(countyCityVo);
                } else {
                    countyCityVo.setCityPinYin(cityDictionary.getCityEnglish());
                    countyCityVo.setCityName(cityChinese);
                    countyCityVos.add(countyCityVo);
                }
            }
            map.put("countyCitys", countyCityVos);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map, BusinessExceptionStatusEnum.Success.getCode());
        }else{
            String city_dictionary_app = RedisUtil.get("city_dictionary_app");
            if(StringUtils.isBlank(city_dictionary_app)){
                String abcs[] = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
                        "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
                for (String abc : abcs) {
                    List<SuperiorCityVo> superiorCityVos = new ArrayList<>();
                    List<CityDictionary> cityDictionaries = dictionaryService.selectParentCityListsByABC(abc);
                    if(null != cityDictionaries){
                        for (CityDictionary cityDictionary : cityDictionaries) {
                            SuperiorCityVo superiorCityVo = new SuperiorCityVo();
                            String parentCityChinese = cityDictionary.getParentCityChinese();
                            //根据上级市的名字 查询所有的县
                            List<CityDictionary> countyListByParentCity = dictionaryService.selectCountyListByParentCity(parentCityChinese);
                            List<CountyCityVo> countyCityVos = new ArrayList<>();
                            boolean flag = true;
                            for (CityDictionary countyCity : countyListByParentCity) {
                                CountyCityVo countyCityVo = new CountyCityVo();
                                String cityChinese = countyCity.getCityChinese();
                                if (flag) {
                                    countyCityVo.setCityName("全城");
                                    countyCityVo.setCityPinYin("");
                                    countyCityVos.add(countyCityVo);
                                    flag = false;
                                    countyCityVo = new CountyCityVo();
                                }
                                if (cityChinese.equals(parentCityChinese)) {
                                    superiorCityVo.setCityName(cityChinese);
                                    superiorCityVo.setCityPinYin(countyCity.getCityEnglish());
                                    superiorCityVo.setProvinceName(countyCity.getProvinceChinese());
                                } else {
                                    countyCityVo.setCityName(cityChinese);
                                    countyCityVo.setCityPinYin(countyCity.getCityEnglish());
                                    countyCityVos.add(countyCityVo);
                                }
                            }
                            superiorCityVo.setCountyCityList(countyCityVos);
                            superiorCityVos.add(superiorCityVo);
                        }
                        map.put(abc.toUpperCase(), superiorCityVos);
                    }
                }
                //把查询出来的信息存到redis
                city_dictionary_app = JSON.toJSONString(map);
                RedisUtil.set("city_dictionary_app",city_dictionary_app);
            }else{
                map = JSON.parseObject(city_dictionary_app);
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map, BusinessExceptionStatusEnum.Success.getCode());
        }
    }

    /**
     * 方法名：getAllProvince</br>
     * 描述：web端获取城市列表信息</br>
     * 参数：[]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/terminal/getallprovince", method = RequestMethod.POST)
    @ResponseBody
    public Object getAllProvince(){
        //查询redis是否存在
        String city_dictionary_web = RedisUtil.get("city_dictionary_web");
        AllCityVos allCityVos = new AllCityVos();
        if(StringUtils.isBlank(city_dictionary_web)){
            //分组查询所有的省
            List<CityDictionary> provinces = dictionaryService.groupSelectAllProvince();
            if(provinces.size()>0){
                List<ProvinceVo> allProvinces = new ArrayList<>();
                List<CityVo> allCitys = new ArrayList<>();
                List<CountyVo> allCountys = new ArrayList<>();
                for(CityDictionary province : provinces){
                    ProvinceVo provinceVo = new ProvinceVo();
                    String provinceChinese = province.getProvinceChinese();
                    String provinceEnglish = province.getProvinceEnglish();
                    String sortCode = province.getCityCode();
                    provinceVo.setId(provinceEnglish);
                    provinceVo.setName(provinceChinese);
                    provinceVo.setSortFlag(sortCode);
                    allProvinces.add(provinceVo);   //保存省
                    //根据省分组查询所有的市
                    List<CityDictionary> citys = dictionaryService.groupSelectAllParentCity(provinceChinese);
                    if(null != citys){
                        for(CityDictionary city:citys){
                            CityVo cityVo = new CityVo();
                            String parentCityChinese = city.getParentCityChinese();
                            String cityCode = city.getCityCode();
                            //保存上级市和省
                            cityVo.setName(parentCityChinese);
                            cityVo.setProvinceId(provinceEnglish);
                            cityVo.setId(cityCode);
                            allCitys.add(cityVo);   //保存上级市
                            //查询县
                            List<CityDictionary> countys = dictionaryService.selectCountysByParentCity(parentCityChinese);
                            if(null != countys){
                                for(CityDictionary county : countys){
                                    String cityChinese = county.getCityChinese();
                                    String cityEnglish = county.getCityEnglish();
                                    if(!cityChinese.equals(parentCityChinese)){
                                        CountyVo countyVo = new CountyVo();
                                        countyVo.setId(cityEnglish);
                                        countyVo.setName(cityChinese);
                                        countyVo.setProvinceId(provinceEnglish);
                                        countyVo.setCityId(cityCode);
                                        allCountys.add(countyVo);
                                    }
                                }
                            }
                        }
                    }
                }
                allCityVos.setAllCity(allCitys);
                allCityVos.setAllCounty(allCountys);
                allCityVos.setAllProvince(allProvinces);
                //把结果存到redis
                city_dictionary_web = JSON.toJSONString(allCityVos);
                RedisUtil.set("city_dictionary_web",city_dictionary_web);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.DataNull.getDescription(),BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }else{
            allCityVos = JSONObject.parseObject(city_dictionary_web,AllCityVos.class);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),allCityVos,BusinessExceptionStatusEnum.Success.getCode());
    }

    /**
     * 方法名：loginOut</br>
     * 描述：退出登录</br>
     * 参数：[]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/system/logoff" ,method = RequestMethod.POST)
    @ResponseBody
    public Object loginOff(HttpServletRequest request, String userId, String tenantId){
        String ua = ((HttpServletRequest) request).getHeader("user-agent");
        String source = RequestSourceUtil.getRequestSource(ua);
        String source1 = "WEB";
        if(source.equals("iPhone")||source.equals("Android") || source.equals("iPad")){
            source1 = "APP";
        }
        String key = tenantId + "_yunmeeting_"+source1+"_token_" + userId;
        //查询缓存redis的用户信息
        String s = RedisUtil.get(key);
        if(StringUtils.isNotBlank(s)){
            //删除缓存用户信息
            RedisUtil.remove(key);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),BusinessExceptionStatusEnum.Success.getCode());
    }

    /**
     * 方法名：handleInfoReleaseTerminalParam</br>
     * 描述：处理终端信息vo参数</br>
     * 参数：[terminalInfoVo]</br>
     * 返回值：com.thinkwin.common.model.db.InfoReleaseTerminal</br>
     */
    public InfoReleaseTerminal handleInfoReleaseTerminalVoParam(TerminalInfoVo terminalInfoVo,InfoReleaseTerminal infoReleaseTerminal) {
        try {
            if (null != terminalInfoVo) {
                String id = infoReleaseTerminal.getId();
                infoReleaseTerminal = new InfoReleaseTerminal();
                infoReleaseTerminal.setId(id);
                if (StringUtils.isNotBlank(terminalInfoVo.getTenantId()))
                    infoReleaseTerminal.setTenantId(terminalInfoVo.getTenantId());

                if (StringUtils.isNotBlank(terminalInfoVo.getHardwareId()))
                    infoReleaseTerminal.setHardwareId(terminalInfoVo.getHardwareId());

                if (StringUtils.isNotBlank(terminalInfoVo.getTerminalName())) {
                    infoReleaseTerminal.setTerminalName(/*new String(*/terminalInfoVo.getTerminalName()/*.getBytes("ISO-8859-1"), "UTF-8")*/);
                } else {
                    infoReleaseTerminal.setTerminalName(terminalInfoVo.getHardwareId());
                }

                if (StringUtils.isNotBlank(terminalInfoVo.getProvince())) {
                    infoReleaseTerminal.setProvince(/*new String(*/terminalInfoVo.getProvince()/*.getBytes("ISO-8859-1"), "UTF-8")*/);
                }

                if (StringUtils.isNotBlank(terminalInfoVo.getCity())) {
                    infoReleaseTerminal.setCity(/*new String(*/terminalInfoVo.getCity()/*.getBytes("ISO-8859-1"), "UTF-8")*/);
                }

                if (StringUtils.isNotBlank(terminalInfoVo.getCounty())) {
                    infoReleaseTerminal.setCounty(/*new String(*/terminalInfoVo.getCounty()/*.getBytes("ISO-8859-1"), "UTF-8")*/);
                }

                if (StringUtils.isNotBlank(terminalInfoVo.getStreet())) {
                    infoReleaseTerminal.setStreet(/*new String(*/terminalInfoVo.getStreet()/*.getBytes("ISO-8859-1"), "UTF-8")*/);
                }
                return infoReleaseTerminal;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 方法名：removeDuplicate</br>
     * 描述：List去除重复</br>
     * 参数：[list]</br>
     * 返回值：java.util.List</br>
     */
    public static List<ProvinceVo> removeDuplicate(List<ProvinceVo> list) {
        Map<String,ProvinceVo> map = new HashMap<>();
        Set<ProvinceVo> ts = new HashSet<>();
        for(ProvinceVo p : list){
            if(map.get(p.getName()) == null){
                map.put(p.getName(),p);
                ts.add(p);
            }
        }
        list.clear();
        list.addAll(ts);
        return list;
    }
}
