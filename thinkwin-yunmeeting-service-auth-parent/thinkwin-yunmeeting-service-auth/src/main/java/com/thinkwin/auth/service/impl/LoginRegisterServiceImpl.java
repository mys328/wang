package com.thinkwin.auth.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.auth.localService.LocalLoginRegisterService;
import com.thinkwin.auth.localService.LocalSaasDbConfigService;
import com.thinkwin.auth.localService.LocalSaasTenantServcie;
import com.thinkwin.auth.localService.LocalUserService;
import com.thinkwin.auth.mapper.core.*;
import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.SaasTenantService;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.*;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.utils.CreateRandomNumber;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.PingYinUtil;
import com.thinkwin.common.utils.StringUtil;
import com.thinkwin.common.utils.createDatabase.RandomUtils;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.WechatSNSUserInfoVo;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 类名: LoginRegisterServiceImpl </br>
 * 描述:登录注册接口实现</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/5/19 </br>
 */
@Service("loginRegisterService")
public class LoginRegisterServiceImpl implements LoginRegisterService {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(LoginRegisterServiceImpl.class);

    @Autowired
    SaasUserOauthMapper saasUserOauthMapper;
    @Autowired
    SaasUserWebMapper saasUserWebMapper;
    @Autowired
    LocalLoginRegisterService localLoginRegisterService;
    @Autowired
    LocalSaasTenantServcie localSaasTenantServcie;
    @Autowired
    LocalUserService localUserService;
    @Autowired
    LocalSaasDbConfigService localSaasDbConfigService;
    @Autowired
    SaasUserTenantMiddleMapper saasUserTenantMiddleMapper;
    @Autowired
    SaasUserOauthInfoMapper saasUserOauthInfoMapper;

    @Autowired
    SaasTenantService saasTenantServcie;

    @Autowired
    private TenantOrderLogMapper tenantOrderLogMapper;

    @Override
    public SaasUserWeb selectUserLoginInfo(SaasUserWeb saasUserWeb) {
        List<SaasUserWeb> sysUserWebs = this.saasUserWebMapper.select(saasUserWeb);
        //判断sysUserWebs是否为空
        if (null != sysUserWebs && sysUserWebs.size() > 0) {
            //如果sysUserWebs为空则抛出手机号和密码不正确异常
            //throw new PhoneNumberPasswordException();
            return sysUserWebs.get(0);
        }
        return null;
    }

    @Override
    public SaasUserWeb selectUserLoginInfo(String userId, String phoneNumber) {
        SaasUserWeb saasUserWeb = new SaasUserWeb();
        saasUserWeb.setUserId(userId);
        saasUserWeb.setAccount(phoneNumber);
        List<SaasUserWeb> sysUserWebs = this.saasUserWebMapper.select(saasUserWeb);
        //判断sysUserWebs是否为空
        if (null != sysUserWebs && sysUserWebs.size() > 0) {
            return sysUserWebs.get(0);
        }
        return null;
    }

    @Override
    public List<SaasUserOauth> selectOAuthLoginInfo(SaasUserOauth saasUserOauth) {
        String nickName = saasUserOauth.getOauthUserName();
        if(StringUtils.isNotBlank(nickName)) {
            try {
                saasUserOauth.setOauthUserName(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        List<SaasUserOauth>  sysUserOauths = saasUserOauthMapper.selectSaasUserOauthByWechat(saasUserOauth);
        if(null != sysUserOauths && sysUserOauths.size() > 0) {
            String wechat = sysUserOauths.get(0).getOauthUserName();
            if (StringUtils.isNotBlank(wechat)&&StringUtil.isHexNumber(wechat)) {
                try {
                    wechat = StringUtil.parseHexString(wechat);
                    sysUserOauths.get(0).setOauthUserName(wechat);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return sysUserOauths;
        }
        return null;
    }

    @Override
    public SaasUserOauthInfo selectUserOauthInfoByOauthUserId(String userOauthId, String oauthType) {
        SaasUserOauthInfo saasUserOauthInfo = new SaasUserOauthInfo();
        //判断查询类型为微信类型
        if ("3".equals(oauthType)) {
            saasUserOauthInfo = saasUserOauthInfoMapper.selectOauthInfoWechat(userOauthId);
            if(null != saasUserOauthInfo){
                String wechat = saasUserOauthInfo.getNickName();
                if (StringUtils.isNotBlank(wechat)&&StringUtil.isHexNumber(wechat)) {
                    try {
                        wechat = StringUtil.parseHexString(wechat);
                        saasUserOauthInfo.setNickName(wechat);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return saasUserOauthInfo;
            }
        } else if ("2".equals(oauthType)) {
            //断查询类型为微博类型
            saasUserOauthInfo = null;
        } else if ("1".equals(oauthType)) {
            //断查询类型为qq类型
            saasUserOauthInfo = null;
        }
        return null;
    }

    @Override
    public boolean saveOAuthLoginInfo(SaasUserOauth saasUserOauth) {
        String nickName = saasUserOauth.getOauthUserName();
        if(StringUtils.isNotBlank(nickName)){
            try {
                saasUserOauth.setOauthUserName(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        int i = this.saasUserOauthMapper.insertSelective(saasUserOauth);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean saveOAuthUserInfo(SaasUserOauthInfo saasUserOauthInfo) {
        String nickName = saasUserOauthInfo.getNickName();
        if(StringUtils.isNotBlank(nickName)) {
            try {
                saasUserOauthInfo.setNickName(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        int i = this.saasUserOauthInfoMapper.insertSelective(saasUserOauthInfo);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean saveUserLoginInfo(SaasUserWeb saasUserWeb) {
        int i = this.saasUserWebMapper.insertSelective(saasUserWeb);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateUserLoginInfo(SaasUserWeb saasUserWeb) {
        int i = this.saasUserWebMapper.updateByPrimaryKeySelective(saasUserWeb);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateUserLoginInfo(SaasUserWeb saasUserWeb, Object example) {
        int i = this.saasUserWebMapper.updateByExampleSelective(saasUserWeb, example);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateOAuthLoginInfo(SaasUserOauth saasUserOauth) {
        String nickName = saasUserOauth.getOauthUserName();
        if(StringUtils.isNotBlank(nickName)) {
            try {
                saasUserOauth.setOauthUserName(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        int i = this.saasUserOauthMapper.updateByPrimaryKeySelective(saasUserOauth);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateOAuthUserInfo(SaasUserOauthInfo saasUserOauthInfo) {
        String nickName = saasUserOauthInfo.getNickName();
        if(StringUtils.isNotBlank(nickName)) {
            try {
                saasUserOauthInfo.setNickName(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        int i = this.saasUserOauthInfoMapper.updateByPrimaryKeySelective(saasUserOauthInfo);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteOAuthLoginInfo(SaasUserOauth saasUserOauth) {
        String nickName = saasUserOauth.getOauthUserName();
        if(StringUtils.isNotBlank(nickName)) {
            try {
                saasUserOauth.setOauthUserName(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        int i = this.saasUserOauthMapper.delete(saasUserOauth);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteOAuthUserInfo(SaasUserOauthInfo saasUserOauthInfo) {
        String nickName = saasUserOauthInfo.getNickName();
        if(StringUtils.isNotBlank(nickName)) {
            try {
                saasUserOauthInfo.setNickName(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        int i = this.saasUserOauthInfoMapper.delete(saasUserOauthInfo);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Map saveUserLoginAndTenantInfo(SaasUserWeb saasUserWeb, SaasTenant saasTenant, SysUser sysUser, SaasTenantInfo saasTenantInfo, SaasUserOauth saasUserOauth, SaasUserOauthInfo saasUserOauthInfo) {
        Map<String, Object> map = new HashMap<>();
        String userLoginId = CreateUUIdUtil.Uuid();
        String userId = CreateUUIdUtil.Uuid();
        String tenantId = CreateUUIdUtil.Uuid();
        String tenantName = saasTenant.getTenantName();

        //记录租户订单信息，在订单支付成功并更新租户授权信息的同时更新此表,
        //防止同一订单多次刷新授权信息
        try{
            this.tenantOrderLogMapper.createTenantOrderLog(tenantId);
            logger.info("创建租户订单信息成功，用户ID: {}, 租户名称: {}", userId, tenantName);
        } catch (Exception e){
	        logger.error("创建租户订单信息失败，用户ID: {}, 租户名称: {}, 异常信息: {}", userId, tenantName, e);
	        throw e;
        }
        try {
            //查询所有数据库域
            List<SaasDbConfig> saasDbConfigList = this.localSaasDbConfigService.selectAllSaasDbConfig();
            //从多个域中随机抽取一条
            SaasDbConfig saasDbConfig = saasDbConfigList.get(RandomUtils.getRandomInt(saasDbConfigList.size()));
            logger.info("*************随机抽取一条数据库域：", saasDbConfig.getUsername() + "**时间：" + new Date());
            //创建租户数据库和表
            saasTenant = this.localSaasTenantServcie.createTenantDataBaseAndTable(tenantName, tenantId, saasDbConfig);
            if (StringUtils.isNotBlank(saasTenant.getTenantCode())) {
                logger.info("*************数据库和表创建成功：", saasTenant.getTenantName() + "**时间：" + new Date());
                //1.增加用户登录信息  失败返回false
                saasUserWeb.setId(userLoginId);
                saasUserWeb.setUserId(userId);
                saasUserWeb.setTenantId(tenantId);
                boolean saveUserLogin = localLoginRegisterService.saveUserLoginInfo(saasUserWeb);
                if (!saveUserLogin) {
                    map.put("status", false);
                    return map;
                }
                logger.info("*************增加用户登录表成功：", saasUserWeb.getAccount() + "**时间：" + new Date());
                //增加微信登录表
                if (null != saasUserOauth) {
                    //增加微信名
                    String oauthUserName = saasUserOauth.getOauthUserName();
                    String userOauthId = CreateUUIdUtil.Uuid();
                    saasUserOauth.setUserId(userId);
                    saasUserOauth.setTenantId(tenantId);
                    saasUserOauth.setId(userOauthId);
                    saasUserOauth.setCreateTime(new Date());
                    saasUserOauth.setOauthUserName(oauthUserName);
                    saasUserOauth.setIsBind(1);
                    boolean saveOauthLogin = localLoginRegisterService.saveOAuthLoginInfo(saasUserOauth);
                    if (!saveOauthLogin) {
                        map.put("status", false);
                        return map;
                    }
                    logger.info("*************增加微信登录表成功：", saasUserOauth.getOauthUserName() + "**时间：" + new Date());
                    //保存第三方登录信息表
                    saasUserOauthInfo.setUserOauthId(userOauthId);
                    saasUserOauthInfo.setId(CreateUUIdUtil.Uuid());
                    saasUserOauthInfo.setCreateTime(new Date());
                    saasUserOauthInfo.setNickName(oauthUserName);
                    localLoginRegisterService.saveOAuthUserInfo(saasUserOauthInfo);
                    //删除微信缓存信息
                    RedisUtil.remove("WeChat" + saasUserOauth.getOauthUnionId());
                    logger.info("*************增加微信登录信息表成功：", saasUserOauth.getOauthUserName() + "**时间：" + new Date());
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
                boolean saveTenantService = localSaasTenantServcie.saveSaasTenantServcie(saasTenant);
                logger.info("*************增加saasTenant表成功：", saasTenant.getTenantName() + "**时间：" + new Date());
                if (saveTenantService) {
                    //增加公司信息表
                    saasTenantInfo.setId(CreateUUIdUtil.Uuid());
                    saasTenantInfo.setTenantId(tenantId);
                    saasTenantInfo.setAdminId(userId);
                    boolean saveSaasTenantInfo = this.localSaasTenantServcie.saveSaasTenantInfo(saasTenantInfo);
                    if (!saveSaasTenantInfo) {
                        map.put("status", false);
                        return map;
                    }
                    logger.info("*************增加saasTenantInfo表成功：", saasTenant.getTenantName() + "**时间：" + new Date());
                    //3.增加公司和用户中间表
                    SaasUserTenantMiddle saasUserTenantMiddle = new SaasUserTenantMiddle();
                    saasUserTenantMiddle.setId(CreateUUIdUtil.Uuid());
                    saasUserTenantMiddle.setCreateId(userId);
                    saasUserTenantMiddle.setUserId(userId);
                    saasUserTenantMiddle.setTenantId(tenantId);
                    saasUserTenantMiddle.setCreateTime(new Date());
                    boolean saveUserTenantMiddle = localLoginRegisterService.saveSysUserTenantMiddle(saasUserTenantMiddle);
                    if (!saveUserTenantMiddle) {
                        map.put("status", false);
                        return map;
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
                    map.put("status", true);
                    map.put("sysUser", sysUser);
                    return map;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("创建租户失败："+e.getMessage());
        }
        map.put("status", false);
        return map;
    }

    @Override
    public Map saveCreateEterprise(String tenantName, SysUser sysUser,String uId) {
        Map<String, Object> map = new HashMap<>();
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
            throw e;
        }

	    //查询所有数据库域
        List<SaasDbConfig> saasDbConfigList = this.localSaasDbConfigService.selectAllSaasDbConfig();
        //从多个域中随机抽取一条
        SaasDbConfig saasDbConfig = saasDbConfigList.get(RandomUtils.getRandomInt(saasDbConfigList.size()));
        logger.info("*************随机抽取一条数据库域：", saasDbConfig.getUsername() + "**时间："+new Date());
        //创建租户数据库和表
        saasTenant = this.localSaasTenantServcie.createTenantDataBaseAndTable(tenantName, tenantId, saasDbConfig);
        if (StringUtils.isNotBlank(saasTenant.getTenantCode())) {
            logger.info("*************数据库和表创建成功：", saasTenant.getTenantName() + "**时间："+new Date());
            //1.修改用户登录信息  失败返回false
            SaasUserWeb saasUserWeb = new SaasUserWeb();
            saasUserWeb.setUserId(userId);
            SaasUserWeb saasUserWeb1 = localLoginRegisterService.selectUserLoginInfo(saasUserWeb);
            saasUserWeb1.setTenantId(tenantId);
            saasUserWeb1.setStatus(1);
            boolean saveUserLogin = localLoginRegisterService.updateUserLoginInfo(saasUserWeb1);
            if (!saveUserLogin) {
                map.put("status", false);
                return map;
            }
            logger.info("*************增加用户登录表成功：", saasUserWeb.getAccount() + "**时间："+new Date());
            if(StringUtils.isNotBlank(uId)) {
                //修改微信登录表
                SaasUserOauth saasUserOauth = new SaasUserOauth();
                saasUserOauth.setUserId(userId);
                saasUserOauth.setOauthType(2);
                SaasUserOauth saasUserOauth1 = localLoginRegisterService.selectOAuthLoginInfo(saasUserOauth);
                if(null!=saasUserOauth1){
                    saasUserOauth1.setTenantId(tenantId);
                    saasUserOauth1.setStatus(1);
                    saasUserOauth1.setUpdateTime(new Date());
                    String oauthUserName = saasUserOauth1.getOauthUserName();
                    if(StringUtils.isNotBlank(oauthUserName)) {
                        try {
                            saasUserOauth1.setOauthUserName(StringUtil.bytes2Hex(oauthUserName));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    saasUserOauth1.setIsBind(1);
                    boolean saveOauthLogin = localLoginRegisterService.updateOAuthLoginInfo(saasUserOauth1);
                    if (!saveOauthLogin) {
                        map.put("status", false);
                        return map;
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
            boolean saveTenantService = localSaasTenantServcie.saveSaasTenantServcie(saasTenant);
            if (saveTenantService) {
                logger.info("*************增加saasTenant表成功：", saasTenant.getTenantName() + "**时间："+new Date());
                SaasTenantInfo saasTenantInfo = new SaasTenantInfo();
                //增加公司信息表
                saasTenantInfo.setId(CreateUUIdUtil.Uuid());
                saasTenantInfo.setTenantId(tenantId);
                saasTenantInfo.setAdminId(userId);
                saasTenantInfo.setTenantName(tenantName);
                saasTenantInfo.setCompanyInvitationCode(CreateRandomNumber.createSixByteRandomNumber());
                boolean saveSaasTenantInfo = this.localSaasTenantServcie.saveSaasTenantInfo(saasTenantInfo);
                if (!saveSaasTenantInfo) {
                    map.put("status", false);
                    return map;
                }
                logger.info("*************增加saasTenantInfo表成功：", saasTenant.getTenantName() + "**时间："+new Date());
                //3.增加公司和用户中间表
                SaasUserTenantMiddle saasUserTenantMiddle = new SaasUserTenantMiddle();
                saasUserTenantMiddle.setId(CreateUUIdUtil.Uuid());
                saasUserTenantMiddle.setCreateId(userId);
                saasUserTenantMiddle.setUserId(userId);
                saasUserTenantMiddle.setTenantId(tenantId);
                saasUserTenantMiddle.setCreateTime(new Date());
                boolean saveUserTenantMiddle = localLoginRegisterService.saveSysUserTenantMiddle(saasUserTenantMiddle);
                if (!saveUserTenantMiddle) {
                    map.put("status", false);
                    return map;
                }
                logger.info("*************增加SaasUserTenantMiddle表成功：", saasTenant.getTenantName() + "**时间："+new Date());
                sysUser.setId(userId);
                sysUser.setCreater(userId);
                sysUser.setTenantId(tenantId);
                sysUser.setStatus(1);
                sysUser.setCreateTime(new Date());
                map.put("status", true);
                map.put("sysUser", sysUser);
                return map;
            }
        }
        map.put("status", false);
        return map;
    }

    @Override
    public boolean checkPhoneNumber(String phoneNumber) {
        SaasUserWeb saasUserWeb = new SaasUserWeb();
        saasUserWeb.setAccount(phoneNumber);
        SaasUserWeb isSysUserWeb = this.localLoginRegisterService.selectUserLoginInfo(saasUserWeb);
        if (null != isSysUserWeb) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkTenantName(String tenantName) {
        //查询公司名是否存在
        SaasTenant saasTenant = new SaasTenant();
        saasTenant.setTenantName(tenantName);
        SaasTenant isSaasTenant = saasTenantServcie.selectSaasTenantServcie(saasTenant);
        if (null != isSaasTenant) {
            return true;
        }
        return false;
    }

    @Override
    public boolean saveSysUserTenantMiddle(SaasUserTenantMiddle saasUserTenantMiddle) {
        int i = this.saasUserTenantMiddleMapper.insertSelective(saasUserTenantMiddle);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSysUserTenantMiddle(SaasUserTenantMiddle saasUserTenantMiddle) {
        int i = this.saasUserTenantMiddleMapper.updateByPrimaryKeySelective(saasUserTenantMiddle);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<SaasUserTenantMiddle> selectSaasUserTenantMiddle(String userId, String tenantId) {
        SaasUserTenantMiddle saasUserTenantMiddle = new SaasUserTenantMiddle();
        saasUserTenantMiddle.setTenantId(tenantId);
        saasUserTenantMiddle.setUserId(userId);
        List<SaasUserTenantMiddle> select = this.saasUserTenantMiddleMapper.select(saasUserTenantMiddle);
        if (select.size() > 0) {
            return select;
        }
        return null;
    }

    @Override
    public PageInfo selectSaasUserTenantMiddleByPage(BasePageEntity basePageEntity, SaasUserTenantMiddle saasUserTenantMiddle) {
        PageHelper.startPage(basePageEntity.getCurrentPage(), basePageEntity.getPageSize()); //开始分页
        List<SaasUserTenantMiddle> select = this.saasUserTenantMiddleMapper.select(saasUserTenantMiddle);
        if (select.size() > 0) {
            return new PageInfo<>(select);
        }
        return null;
    }

    @Override
    public Map<String, Object> checkWechatInfoChange(WechatSNSUserInfoVo snsUserInfo) {
        int flag = 0;
        int flagg = 0;
        Map<String, Object> map = new HashMap<>();
        String nickname = snsUserInfo.getNickname();
        String headImgUrl = snsUserInfo.getHeadImgUrl();
        Integer sex = snsUserInfo.getSex();
        String city = snsUserInfo.getCity();
        String province = snsUserInfo.getProvince();
        String country = snsUserInfo.getCountry();
        String unionId = snsUserInfo.getUnionid();
        //根据unionId查询用户第三方登录表和第三方登录信息表
        SaasUserOauth saasUserOauth = new SaasUserOauth();
        saasUserOauth.setOauthUnionId(unionId);
        List<SaasUserOauth> userOauth = selectOAuthLoginInfo(saasUserOauth);
        SaasUserOauth saasUserOauth1 = new SaasUserOauth();
        List<SaasUserOauth> saasUserOauths = new ArrayList<>();
        String userOauthId = "";
        if(null!=userOauth&&userOauth.size()>0) {
            for(SaasUserOauth saasUserOauth2:userOauth) {
                if (!saasUserOauth2.getOauthUserName().equals(nickname)) {
                    saasUserOauth1.setOauthUserName(nickname);
                    flagg = 1;
                }
                if (!saasUserOauth2.getOauthPhoto().equals(headImgUrl)) {
                    saasUserOauth1.setOauthPhoto(headImgUrl);
                    flagg = 1;
                }
                if(saasUserOauth2.getOauthType()==2){
                    userOauthId = saasUserOauth2.getId();
                }
                if (flagg == 1) {
                    saasUserOauth1.setId(saasUserOauth2.getId());
                }
                saasUserOauths.add(saasUserOauth1);
            }
        }
        SaasUserOauthInfo saasUserOauthInfo = new SaasUserOauthInfo();
        if(StringUtils.isNotBlank(userOauthId)) {
            SaasUserOauthInfo userOauthInfo = selectUserOauthInfoByOauthUserId(userOauthId, "3");
            if (null != userOauthInfo) {
                if (!userOauthInfo.getNickName().equals(nickname)) {
                    saasUserOauthInfo.setNickName(nickname);
                    flag = 1;
                }
                if (!userOauthInfo.getSex().equals(sex)) {
                    saasUserOauthInfo.setSex(sex);
                    flag = 1;
                }
                if (!userOauthInfo.getCity().equals(city)) {
                    saasUserOauthInfo.setCity(city);
                    flag = 1;
                }
                if (!userOauthInfo.getProvince().equals(province)) {
                    saasUserOauthInfo.setProvince(province);
                    flag = 1;
                }
                if (!userOauthInfo.getCountry().equals(country)) {
                    saasUserOauthInfo.setCountry(country);
                    flag = 1;
                }
                if (flag == 1) {
                    saasUserOauthInfo.setId(userOauthInfo.getId());
                }
            }
            map.put("saasUserOauthInfo", saasUserOauthInfo.getId() == null ? null : saasUserOauthInfo);
        }
        map.put("saasUserOauth", saasUserOauths);
        return map;
    }

    @Override
    public boolean createCompany() {
        return false;
    }
}
