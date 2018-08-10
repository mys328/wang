package com.thinkwin.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.core.*;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.createDatabase.AntExecSqlCreateDatabase;
import com.thinkwin.common.utils.createDatabase.AntExecSqlCreateTable;
import com.thinkwin.common.utils.createDatabase.RandomUtils;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.core.mapper.SaasDbConfigMapper;
import com.thinkwin.core.mapper.TenantOrderLogMapper;
import com.thinkwin.core.service.LoginRegisterCoreService;
import com.thinkwin.core.service.SaasTenantService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名: LoginRegisterCoreServiceImpl </br>
 * 描述:登录注册serviceImpl</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/11/13 </br>
 */
@Service("loginRegisterCoreService")
public class LoginRegisterCoreServiceImpl implements LoginRegisterCoreService {

    private static Logger logger = LoggerFactory.getLogger(LoginRegisterCoreServiceImpl.class);

    @Resource
    private SaasTenantService saasTenantService;
    @Resource
    private TenantOrderLogMapper tenantOrderLogMapper;
    @Resource
    private SaasDbConfigMapper saasDbConfigMapper;

    @Override
    public List<SaasDbConfig> selectAllSaasDbConfig() {
        List<SaasDbConfig> saasDbConfigList = this.saasDbConfigMapper.selectAll();
        if(null!=saasDbConfigList&&saasDbConfigList.size()>0){
            return saasDbConfigList;
        }
        return null;
    }

    @Override
    public SaasTenant createTenantDataBaseAndTable(String tenantName, String tenantId, SaasDbConfig saasDbConfig) {
        SaasTenant saasTenant = new SaasTenant();
        try{
            String tenantFirstSpell = PingYinUtil.getFirstSpell(tenantName);
            String createSchemaName = null;
            if (StringUtils.isNotBlank(tenantFirstSpell)) {
                saasTenant.setTenantCode(tenantFirstSpell);
                //获取公司Id
                createSchemaName = tenantFirstSpell + "_" + tenantId;
                saasTenant.setDbConfig(createSchemaInfo(createSchemaName));
            }
            if (StringUtils.isNotBlank(createSchemaName)) {
                logger.info("*************开始创建数据库：", createSchemaName + "**开始时间："+new Date());
                //创建租户数据库
                boolean createDatabase = AntExecSqlCreateDatabase.createDatabase(createSchemaName, saasDbConfig);
                if (createDatabase) {
                    logger.info("*************创建数据库结束：", createSchemaName + "**结束时间："+new Date());
                    logger.info("*************开始创建表：", createSchemaName + "**开始时间："+new Date());
                    //创建租户数据库表
                    boolean createTable = AntExecSqlCreateTable.createTable(createSchemaName, saasDbConfig);
                    if (createTable) {
                        logger.info("*************创建表结束：", createSchemaName + "**结束时间："+new Date());
                        return saasTenant;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("LocalSaasTenantServcieImpl createTenantDataBaseAndTable tenantName:" + tenantName,",tenantId:"+tenantId+",saasDbConfig"+saasDbConfig,e);
        }
        return null;
    }

    @Override
    public Map<String, Object> registerValidation(Map<String, Object> map) {
        //获取前端请求参数
        String tenantName = (String)map.get("tenantName");//公司名称
        String userName = (String)map.get("userName");//用户名
        String phoneNumber = (String)map.get("phoneNumber");//用户手机号码
        String password = (String)map.get("password");//密码
        String verifyCode = (String)map.get("verifyCode");//验证码
        String deviceToken = (String)map.get("deviceToken");//设备token
        String deviceType = (String)map.get("deviceType");//设备类型
        String unionId = (String)map.get("uId");//用户第三方唯一Id
        ResponseResult responseResult = new ResponseResult();
        Map<String,Object> map1 = new HashMap<>();
        //参数校验
        responseResult.setIfSuc(0);
        //参数是否为空
        map.put("paramState", "isBlank");
        //校验参数不能为空
        if (StringUtils.isBlank(tenantName)) {
            responseResult.setMsg("公司名称不能为空！");
            map.put("param", "tenantName");
        } else if (tenantName.length()>30){
            responseResult.setMsg("公司名称长度受限！");
            map.put("param", "tenantNameLength");
        }else if (StringUtils.isBlank(userName)) {
            responseResult.setMsg("用户名不能为空！");
            map.put("param", "userName");
        }else if(userName.length()>10){
            responseResult.setMsg("用户名长度受限！");
            map.put("param", "userNameLength");
        } else if (StringUtils.isBlank(phoneNumber)) {
            responseResult.setMsg("手机号码不能为空！");
            map.put("param", "phoneNumber");
        } else if (StringUtils.isBlank(password)) {
            responseResult.setMsg("密码不能为空！");
            map.put("param", "password");
        } else if (StringUtils.isBlank(verifyCode)) {
            responseResult.setMsg("验证码不能为空！");
            map.put("param", "verifyCode");
        } else {
            //校验格式不正确
            boolean b = ValidatorUtil.isPassword(password);
            boolean mobile = ValidatorUtil.isMobile(phoneNumber);
            boolean tenantName1 = ValidatorUtil.isTenantName(tenantName);
            boolean username = ValidatorUtil.isUsername(userName);
            map.put("paramState", "isNotFormat");
            if (!b) {
                responseResult.setMsg("密码格式不正确！");
                //参数状态
                map.put("param", "password");
            } else if (!mobile) {
                responseResult.setMsg("手机号码格式不正确！");
                map.put("param", "phoneNumber");
            } else if(!tenantName1){
                responseResult.setMsg("公司名称格式不正确！");
                map.put("param", "tenantName");
            }else if(!username){
                responseResult.setMsg("用户名称格式不正确！");
                map.put("param", "userName");
            }else{
                //校验通过
                responseResult.setIfSuc(1);
                responseResult.setMsg("success");
                map.put("paramState", "");
                map.put("param", "");
            }
        }
        //查询公司是否存在
        boolean checkTenantName = this.saasTenantService.checkTenantName(tenantName);
        if (checkTenantName) {
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.TenantNameExist.getDescription());
            responseResult.setCode(BusinessExceptionStatusEnum.TenantNameExist.getCode());
            //参数状态
            map.put("paramState", "isExist");
            map.put("param", "tenantName");
        }
        //查询手机号码是否存在
        boolean checkPhoneNumber = this.saasTenantService.checkPhoneNumber(phoneNumber);
        if (checkPhoneNumber) {
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.PhoneNumberRegister.getDescription());
            responseResult.setCode(BusinessExceptionStatusEnum.PhoneNumberRegister.getCode());
            //参数状态
            map.put("paramState", "isRegist");
            map.put("param", "phoneNumber");
        }
        responseResult.setData(map);
        if (responseResult.getIfSuc() == 0) {
            map1.put("responseResult", responseResult);
            return map1;
        }
        //用户信息表实体  给实体赋值
        SysUser sysUser = new SysUser();
        sysUser.setUserName(userName);
        sysUser.setDeviceToken(deviceToken);
        sysUser.setPhoneNumber(phoneNumber);
        int deviceTypeNum = 0;
        if (null != deviceType && !deviceType.equals("")) {
            deviceTypeNum = Integer.parseInt(deviceType);
        }
        sysUser.setDeviceType(deviceTypeNum);
        //用户登录表实体
        SaasUserWeb saasUserWeb = new SaasUserWeb();
        saasUserWeb.setAccount(phoneNumber);
        password = SHA1Util.SHA1(password);
        saasUserWeb.setPassword(password);
        //公司表实体
        SaasTenant saasTenant = new SaasTenant();
        saasTenant.setTenantName(tenantName);
        //公司信息表实体
        SaasTenantInfo saasTenantInfo = new SaasTenantInfo();
        saasTenantInfo.setTenantName(tenantName);
        saasTenantInfo.setCompanyInvitationCode(CreateRandomNumber.createSixByteRandomNumber());
        //用户第三方登录表实体
        if (StringUtils.isNotBlank(unionId)) {
            SaasUserOauth userOauth = new SaasUserOauth();
            userOauth.setOauthUnionId(unionId);
            map1 = wechatLoginHandle(userOauth);
        }
        map1.put("responseResult", responseResult);
        map1.put("sysUser", sysUser);
        map1.put("saasUserWeb", saasUserWeb);
        map1.put("saasTenant", saasTenant);
        map1.put("saasTenantInfo", saasTenantInfo);
        map1.put("verifyCode", verifyCode);
        map1.put("phoneNumber", phoneNumber);
        return map1;
    }

    @Override
    public SysUser saveUserLoginAndTenantInfo(SaasUserWeb saasUserWeb, SaasTenant saasTenant, SysUser sysUser, SaasTenantInfo saasTenantInfo, SaasUserOauth saasUserOauth, SaasUserOauthInfo saasUserOauthInfo) {
        Map<String, Object> map = new HashMap<>();
        String userLoginId = CreateUUIdUtil.Uuid();
        String userId = CreateUUIdUtil.Uuid();
        String tenantId = CreateUUIdUtil.Uuid();
        String tenantName = saasTenant.getTenantName();
        //记录租户订单信息，在订单支付成功并更新租户授权信息的同时更新此表,
        //防止同一订单多次刷新授权信息
        try {
            this.tenantOrderLogMapper.createTenantOrderLog(tenantId);
            logger.info("创建租户订单信息成功，用户ID: {}, 租户名称: {}", userId, tenantName);
        } catch (Exception e) {
            logger.error("创建租户订单信息失败，用户ID: {}, 租户名称: {}, 异常信息: {}", userId, tenantName, e);
        }

        //查询所有数据库域
        List<SaasDbConfig> saasDbConfigList = selectAllSaasDbConfig();
        //从多个域中随机抽取一条
        SaasDbConfig saasDbConfig = saasDbConfigList.get(RandomUtils.getRandomInt(saasDbConfigList.size()));
        logger.info("*************随机抽取一条数据库域：", saasDbConfig.getUsername() + "**时间：" + new Date());
        //创建租户数据库和表
        saasTenant = createTenantDataBaseAndTable(tenantName, tenantId, saasDbConfig);
        if (null!=saasTenant&&StringUtils.isNotBlank(saasTenant.getTenantCode())) {
            logger.info("*************数据库和表创建成功：", saasTenant.getTenantName() + "**时间：" + new Date());
            //1.增加用户登录信息  失败返回false
            saasUserWeb.setId(userLoginId);
            saasUserWeb.setUserId(userId);
            saasUserWeb.setTenantId(tenantId);
            boolean saveUserLogin = saasTenantService.saveUserLoginInfo(saasUserWeb);
            if (!saveUserLogin) {
                return null;
            }
            logger.info("*************增加用户登录表成功：", saasUserWeb.getAccount() + "**时间：" + new Date());
            //增加微信登录表
            if (null != saasUserOauth) {
                //增加微信名
                String oauthUserName = saasUserOauth.getOauthUserName();
                //查询微信登录表
                SaasUserOauth saasUserOauth1 = new SaasUserOauth();
                saasUserOauth1.setOauthUnionId(saasUserOauth.getOauthUnionId());
                saasUserOauth1.setOauthType(2);
                List<SaasUserOauth> saasUserOauth2 = saasTenantService.selectOAuthLoginInfo(saasUserOauth1);
                if(null!=saasUserOauth2&&saasUserOauth2.size()>0){
                    SaasUserOauth userOauth = saasUserOauth2.get(0);
                    userOauth.setTenantId(tenantId);
                    userOauth.setUserId(userId);
                    userOauth.setStatus(1);
                    userOauth.setUpdateTime(new Date());
                    userOauth.setOauthUserName(oauthUserName);
                    sysUser.setWechat(oauthUserName);
                    userOauth.setIsBind(1);
                    //修改微信登录表
                    boolean saveOauthLogin = saasTenantService.updateOAuthLoginInfo(userOauth);
                    if (!saveOauthLogin) {
                        return null;
                    }
                }else {
                    //增加微信登录表
                    String userOauthId = CreateUUIdUtil.Uuid();
                    saasUserOauth.setUserId(userId);
                    saasUserOauth.setTenantId(tenantId);
                    saasUserOauth.setId(userOauthId);
                    saasUserOauth.setCreateTime(new Date());
                    saasUserOauth.setOauthUserName(oauthUserName);
                    sysUser.setWechat(oauthUserName);
                    boolean saveOauthLogin = saasTenantService.saveOAuthLoginInfo(saasUserOauth);
                    if (!saveOauthLogin) {
                        return null;
                    }
                    logger.info("*************增加微信登录表成功：", saasUserOauth.getOauthUserName() + "**时间：" + new Date());
                    //保存第三方登录信息表
                    saasUserOauthInfo.setUserOauthId(userOauthId);
                    saasUserOauthInfo.setId(CreateUUIdUtil.Uuid());
                    saasUserOauthInfo.setCreateTime(new Date());
                    saasUserOauthInfo.setNickName(oauthUserName);
                    saasTenantService.saveOAuthUserInfo(saasUserOauthInfo);
                    logger.info("*************增加微信登录信息表成功：", saasUserOauth.getOauthUserName() + "**时间：" + new Date());
                }
            }
            //2.增加公司表
            saasTenant.setId(tenantId);
            saasTenant.setCreateTime(new Date());
            saasTenant.setStatus(1);
            saasTenant.setSaasDbConfigId(saasDbConfig.getId());
            saasTenant.setTenantName(tenantName);
            saasTenant.setExpectNumber(30);
            saasTenant.setBasePackageType("1000");
            saasTenant.setBasePackageRoomNum(2);
            saasTenant.setBasePackageSpaceNum(1);
            saasTenant.setBasePackageSpacUnit("GB");
            saasTenant.setTenantType("0");
            saasTenant.setBuyRoomNumTotal(0);
            saasTenant.setBuySpaceNumTotal(0);
            saasTenant.setContacts(sysUser.getUserName());
            saasTenant.setContactsTel(sysUser.getPhoneNumber());
            saasTenant.setIsInnerTest("0");
            boolean saveTenantService = saasTenantService.saveSaasTenantServcie(saasTenant);
            logger.info("*************增加saasTenant表成功：", saasTenant.getTenantName() + "**时间：" + new Date());
            if (saveTenantService) {
                //增加公司信息表
                saasTenantInfo.setId(CreateUUIdUtil.Uuid());
                saasTenantInfo.setTenantId(tenantId);
                saasTenantInfo.setAdminId(userId);
                boolean saveSaasTenantInfo = saasTenantService.saveSaasTenantInfo(saasTenantInfo);
                if (!saveSaasTenantInfo) {
                    return null;
                }
                logger.info("*************增加saasTenantInfo表成功：", saasTenant.getTenantName() + "**时间：" + new Date());
                //3.增加公司和用户中间表
                SaasUserTenantMiddle saasUserTenantMiddle = new SaasUserTenantMiddle();
                saasUserTenantMiddle.setId(CreateUUIdUtil.Uuid());
                saasUserTenantMiddle.setCreateId(userId);
                saasUserTenantMiddle.setUserId(userId);
                saasUserTenantMiddle.setTenantId(tenantId);
                saasUserTenantMiddle.setCreateTime(new Date());
                boolean saveUserTenantMiddle = saasTenantService.saveSysUserTenantMiddle(saasUserTenantMiddle);
                if (!saveUserTenantMiddle) {
                    return null;
                }
                logger.info("*************增加SaasUserTenantMiddle表成功：", saasTenant.getTenantName() + "**时间：" + new Date());
                //增加用户表
                sysUser.setId(userId);
                sysUser.setCreater(userId);
                sysUser.setCreateTime(new Date());
                sysUser.setTenantId(tenantId);
                sysUser.setStatus(1);
                String userName = sysUser.getUserName();
                if (null != userName && !"".equals(userName)) {
                    String userNamePinyin = PingYinUtil.getPingYin(userName);
                    sysUser.setUserNamePinyin(userNamePinyin);
                }
                return sysUser;
            }
        }
        return null;
    }

    @Override
    public SysUser saveCreateEterprise(String tenantName, SysUser sysUser, String uId) {
        SaasTenant saasTenant = new SaasTenant();
        String tenantId = CreateUUIdUtil.Uuid();
        String userId = sysUser.getId();
        saasTenant.setTenantName(tenantName);
        saasTenant.setId(tenantId);
        //记录租户订单信息，在订单支付成功并更新租户授权信息的同时更新此表,
        //防止同一订单多次刷新授权信息
        try{
            this.tenantOrderLogMapper.createTenantOrderLog(tenantId);
            logger.info("创建租户订单信息成功，用户ID: {}, 租户名称: {}", userId, tenantName);
        } catch (Exception e){
            logger.error("创建租户订单信息失败，用户ID: {}, 租户名称: {}, 异常信息: {}", userId, tenantName, e);
        }

        //查询所有数据库域
        List<SaasDbConfig> saasDbConfigList = selectAllSaasDbConfig();
        //从多个域中随机抽取一条
        SaasDbConfig saasDbConfig = saasDbConfigList.get(RandomUtils.getRandomInt(saasDbConfigList.size()));
        logger.info("*************随机抽取一条数据库域：", saasDbConfig.getUsername() + "**时间："+new Date());
        //创建租户数据库和表
        saasTenant = createTenantDataBaseAndTable(tenantName, tenantId, saasDbConfig);
        if (StringUtils.isNotBlank(saasTenant.getTenantCode())) {
            logger.info("*************数据库和表创建成功：", saasTenant.getTenantName() + "**时间："+new Date());
            //1.修改用户登录信息  失败返回false
            SaasUserWeb saasUserWeb = new SaasUserWeb();
            saasUserWeb.setUserId(userId);
            SaasUserWeb saasUserWeb1 = saasTenantService.selectUserLoginInfo(saasUserWeb);
            saasUserWeb1.setTenantId(tenantId);
            saasUserWeb1.setStatus(1);
            boolean saveUserLogin = saasTenantService.updateUserLoginInfo(saasUserWeb1);
            if (!saveUserLogin) {
                return null;
            }
            logger.info("*************增加用户登录表成功：", saasUserWeb.getAccount() + "**时间："+new Date());
            if(StringUtils.isNotBlank(uId)) {
                //修改微信登录表
                SaasUserOauth saasUserOauth = new SaasUserOauth();
                saasUserOauth.setOauthUnionId(uId);
                saasUserOauth.setOauthType(2);
                List<SaasUserOauth> saasUserOauth1 = saasTenantService.selectOAuthLoginInfo(saasUserOauth);
                if(null!=saasUserOauth1&&saasUserOauth1.size()>0){
                    SaasUserOauth userOauth = saasUserOauth1.get(0);
                    userOauth.setTenantId(tenantId);
                    userOauth.setUserId(userId);
                    userOauth.setStatus(1);
                    userOauth.setUpdateTime(new Date());
                    String oauthUserName = userOauth.getOauthUserName();
                    userOauth.setOauthUserName(oauthUserName);
                    sysUser.setWechat(oauthUserName);
                    userOauth.setIsBind(1);
                    boolean saveOauthLogin = saasTenantService.updateOAuthLoginInfo(userOauth);
                    if (!saveOauthLogin) {
                        return null;
                    }
                }
                logger.info("*************修改微信登录表成功：", saasUserOauth.getOauthUserName() + "**时间："+new Date());
            }
            //2.增加公司表
            saasTenant.setId(tenantId);
            saasTenant.setTenantName(tenantName);
            saasTenant.setCreateTime(new Date());
            saasTenant.setStatus(1);
            saasTenant.setSaasDbConfigId(saasDbConfig.getId());
            saasTenant.setExpectNumber(30);
            saasTenant.setBasePackageType("1000");
            saasTenant.setBasePackageRoomNum(2);
            saasTenant.setBasePackageSpaceNum(1);
            saasTenant.setBasePackageSpacUnit("GB");
            saasTenant.setTenantType("0");
            saasTenant.setBuyRoomNumTotal(0);
            saasTenant.setBuySpaceNumTotal(0);
            saasTenant.setContacts(sysUser.getUserName());
            saasTenant.setContactsTel(sysUser.getPhoneNumber());
            saasTenant.setIsInnerTest("0");
            boolean saveTenantService = saasTenantService.saveSaasTenantServcie(saasTenant);
            if (saveTenantService) {
                logger.info("*************增加saasTenant表成功：", saasTenant.getTenantName() + "**时间："+new Date());
                SaasTenantInfo saasTenantInfo = new SaasTenantInfo();
                //增加公司信息表
                saasTenantInfo.setId(CreateUUIdUtil.Uuid());
                saasTenantInfo.setTenantId(tenantId);
                saasTenantInfo.setAdminId(userId);
                saasTenantInfo.setTenantName(tenantName);
                saasTenantInfo.setCompanyInvitationCode(CreateRandomNumber.createSixByteRandomNumber());
                boolean saveSaasTenantInfo = saasTenantService.saveSaasTenantInfo(saasTenantInfo);
                if (!saveSaasTenantInfo) {
                    return null;
                }
                logger.info("*************增加saasTenantInfo表成功：", saasTenant.getTenantName() + "**时间："+new Date());
                //3.增加公司和用户中间表
                SaasUserTenantMiddle saasUserTenantMiddle = new SaasUserTenantMiddle();
                saasUserTenantMiddle.setId(CreateUUIdUtil.Uuid());
                saasUserTenantMiddle.setCreateId(userId);
                saasUserTenantMiddle.setUserId(userId);
                saasUserTenantMiddle.setTenantId(tenantId);
                saasUserTenantMiddle.setCreateTime(new Date());
                boolean saveUserTenantMiddle = saasTenantService.saveSysUserTenantMiddle(saasUserTenantMiddle);
                if (!saveUserTenantMiddle) {
                    return null;
                }
                logger.info("*************增加SaasUserTenantMiddle表成功：", saasTenant.getTenantName() + "**时间："+new Date());
                //sysUser.setId(userId);
                sysUser.setCreater(userId);
                sysUser.setTenantId(tenantId);
                sysUser.setStatus(1);
                sysUser.setCreateTime(new Date());
                return sysUser;
            }
        }
        return null;
    }


    /**
     * 方法名：wechatLoginHandle</br>
     * 描述：微信登录参数处理</br>
     * 参数：[userOauth]</br>
     * 返回值：com.thinkwin.common.model.core.SaasUserOauth</br>
     */
    private static Map<String, Object> wechatLoginHandle(SaasUserOauth saasUserOauth) {
        SaasUserOauthInfo saasUserOauthInfo = new SaasUserOauthInfo();
        String uId = saasUserOauth.getOauthUnionId();
        if (StringUtils.isNotBlank(uId)) {
            //获取redis里面的第三方信息
            String wechatUser = RedisUtil.get("WeChat" + uId);
            Map<String, Object> jsonObject = (Map) JSON.parseObject(wechatUser);
            Map snsUserInfo = (Map) jsonObject.get("snsUserInfo");
            //给第三方赋值
            saasUserOauth.setOauthUserName((String) snsUserInfo.get("nickname"));
            saasUserOauth.setOauthUnionId((String) snsUserInfo.get("unionid"));
            saasUserOauth.setOauthOpenId((String) snsUserInfo.get("openId"));
            saasUserOauth.setOauthPhoto((String) snsUserInfo.get("headImgUrl"));
            saasUserOauthInfo.setNickName((String) snsUserInfo.get("nickname"));
            saasUserOauthInfo.setSex((Integer) snsUserInfo.get("sex"));
            saasUserOauthInfo.setCity((String) snsUserInfo.get("city"));
            saasUserOauthInfo.setProvince((String) snsUserInfo.get("province"));
            saasUserOauthInfo.setCountry((String) snsUserInfo.get("country"));

            Map wechatAccessTokenVo = (Map) jsonObject.get("wechatAccessTokenVo");
            saasUserOauth.setOauthAccessToken((String) wechatAccessTokenVo.get("accessToken"));
            Integer expiresIn = (Integer) wechatAccessTokenVo.get("expiresIn");
            saasUserOauth.setOauthExpires(expiresIn + "");
            saasUserOauth.setOauthRefreshToken((String) wechatAccessTokenVo.get("refreshToken"));
            saasUserOauth.setPassword(SHA1Util.SHA1(uId));
        }
        saasUserOauth.setOauthType(2);
        saasUserOauth.setIsBind(1);
        //增加第三方登录信息表实体
        saasUserOauthInfo.setOauthType(2);
        Map<String, Object> map = new HashMap<>();
        map.put("saasUserOauth",saasUserOauth);
        map.put("saasUserOauthInfo",saasUserOauthInfo);
        return map;
    }

    /**
     * 方法名：createSchemaInfo</br>
     * 描述：替换租户数据表配置信息  存库用</br>
     * 参数：[createSchemaName]</br>
     * 返回值：java.lang.String</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/20  </br>
     */
    public String createSchemaInfo(String createSchemaName) {
        String info = "{\n" + "    \"db.driverClass\": \"com.mysql.jdbc.Driver\",\n" + "    \"db.maxWait\": \"5000\",\n" + "    \"db.initialSize\": \"5\",\n" + "    \"db.minIdle\": \"5\",\n" + "    \"db.maxActive\": \"30\",\n" + "    \"db.maxIdle\": \"10\"\n" + "}";
        return info;
    }
    /**
     * 根据数据库SchemaName删除数据库
     * @param dataBaseName
     * @return
     */
    public boolean delDataBaseByName(String dataBaseName){
        //查询所有数据库域
        List<SaasDbConfig> saasDbConfigList = selectAllSaasDbConfig();
        //从多个域中随机抽取一条
        SaasDbConfig saasDbConfig = saasDbConfigList.get(RandomUtils.getRandomInt(saasDbConfigList.size()));
        return AntExecSqlCreateDatabase.delDatabase(dataBaseName, saasDbConfig);
    }
}
