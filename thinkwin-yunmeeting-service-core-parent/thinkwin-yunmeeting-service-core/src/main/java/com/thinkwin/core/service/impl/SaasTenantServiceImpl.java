package com.thinkwin.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.*;
import com.thinkwin.common.utils.StringUtil;
import com.thinkwin.common.vo.WechatSNSUserInfoVo;
import com.thinkwin.core.mapper.*;
import com.thinkwin.core.service.SaasTenantService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service("saasTenantService")
public class SaasTenantServiceImpl implements SaasTenantService {
    private static Logger LOG = LoggerFactory.getLogger(SaasTenantServiceImpl.class);

    @Resource
    private SaasTenantMapper saasTenantMapper;

    @Resource
    private TenantOrderLogMapper tenantOrderLogMapper;
    @Resource
    private SaasSettingMapper saasSettingMapper;

    @Resource
    SaasUserTenantMiddleMapper saasUserTenantMiddleMapper;
    @Resource
    SaasTenantInfoMapper saasTenantInfoMapper;
    @Resource
    SaasUserOauthMapper saasUserOauthMapper;
    @Resource
    SaasUserOauthInfoMapper saasUserOauthInfoMapper;
    @Autowired
    private SaasUserWebMapper saasUserWebMapper;

    @Override
    public PageInfo<SaasTenant> selectSaasTenantListByPage(BasePageEntity page, SaasTenant saasTenant) {

        Map map = new HashMap();

        if (!StringUtil.isEmpty(saasTenant.getTenantName())) {
            map.put("tenantName", saasTenant.getTenantName());
        }
        if (saasTenant.getTerminalCount()!=null&&saasTenant.getTerminalCount()>0) {
            map.put("terminalCount", saasTenant.getTerminalCount());
        }
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<SaasTenant> saasTenantList = this.saasTenantMapper.selectSaasTenantListByPage(map);
        return new PageInfo<>(saasTenantList);
    }

    @Override
    public SaasTenant selectByIdSaasTenantInfo(String tenantId) {
        return this.saasTenantMapper.selectByPrimaryKey(tenantId);
    }

    @Override
    public boolean updateSaasTenantService(SaasTenant saasTenant) {
        int i = this.saasTenantMapper.updateByPrimaryKeySelective(saasTenant);
        if(i>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean saveSaasTenantServcie(SaasTenant saasTenant) {
        int i = this.saasTenantMapper.insertSelective(saasTenant);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSaasTenantInfo(SaasTenantInfo saasTenantInfo) {
        int i = this.saasTenantInfoMapper.updateByPrimaryKeySelective(saasTenantInfo);
        if(i>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean saveSaasTenantInfo(SaasTenantInfo saasTenantInfo) {
        int i = this.saasTenantInfoMapper.insertSelective(saasTenantInfo);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSaasTenantService(SaasTenant saasTenant, String orderId) {
        if(StringUtils.isBlank(orderId)){
            return false;
        }

        TenantOrderLog log = tenantOrderLogMapper.selectByPrimaryKey(saasTenant.getId());

        if(null == log || !orderId.equals(log.getCurrentOrder())){
            return false;
        }

        if(Integer.valueOf(1).equals(log.getOrderProcessed())){
            return true;
        }

        TenantOrderLog newLog = new TenantOrderLog();
        newLog.setTenantId(saasTenant.getId());
        newLog.setOrderProcessed(1);
        newLog.setUpdateTime(new Date());

        int result = 0;
        result = this.tenantOrderLogMapper.updateByPrimaryKeySelective(newLog);

        if(result != 1){
            LOG.error("订单: 更新租户订单日志信息失败");
            return false;
        }

        result = this.saasTenantMapper.updateByPrimaryKeySelective(saasTenant);

        if(result != 1){
            LOG.error("订单: 更新租户授权信息失败");
            return false;
        }

        return true;
    }

    @Override
    public boolean updateTenantOrder(String tenantId, String orderId) {
        TenantOrderLog orderLog = tenantOrderLogMapper.selectByPrimaryKey(tenantId);

        if(null == orderLog){
            return false;
        }

        TenantOrderLog newLog = new TenantOrderLog();
        newLog.setTenantId(tenantId);
        newLog.setOrderProcessed(0);
        newLog.setCurrentOrder(orderId);
        newLog.setUpdateTime(new Date());
        int result = this.tenantOrderLogMapper.updateByPrimaryKeySelective(newLog);

        return result == 1;
    }

    /**
     * 查询租户信息功能接口
     * @param saasTenant
     * @return
     */
    public SaasTenant selectSaasTenantServcie(SaasTenant saasTenant){
        List<SaasTenant> saasTenants = this.saasTenantMapper.select(saasTenant);
        if (null != saasTenants && saasTenants.size() > 0) {
            return saasTenants.get(0);
        }
        return null;
    }

    @Override
    public SaasTenant selectSaasTenantServcie(String tenantId) {
        SaasTenant saasTenant = this.saasTenantMapper.selectByPrimaryKey(tenantId);
        if(null!=saasTenant){
            return saasTenant;
        }
        return null;
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
    public boolean updateOAuthLoginInfo(SaasUserOauth saasUserOauth) {
        LOG.info("saasUserOauth :::"+saasUserOauth.getId()+"::"+saasUserOauth.getOauthUserName());
        if(null != saasUserOauth) {
            String nickName = saasUserOauth.getOauthUserName();
            if (StringUtils.isNotBlank(nickName)) {
                try {
                    saasUserOauth.setOauthUserName(StringUtil.bytes2Hex(nickName));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            String id = saasUserOauth.getId();
            if (StringUtils.isNotBlank(id)) {
//                Map map = new HashMap();
//                map.put("id",id);
//                String userId = saasUserOauth.getUserId();
//                if(StringUtils.isNotBlank(userId)){
//                    map.put("userId",userId);
//                }
//                Integer oauthType = saasUserOauth.getOauthType();
//                if(null != oauthType){
//                    map.put("oauthType",oauthType);
//                }
//                String oauthOpenId = saasUserOauth.getOauthOpenId();
//                if(StringUtils.isNotBlank(oauthOpenId)){
//                    map.put("oauthOpenId",oauthOpenId);
//                }
//                String oauthUnionId = saasUserOauth.getOauthUnionId();
//                if(StringUtils.isNotBlank(oauthUnionId)){
//                    map.put("oauthUnionId",oauthUnionId);
//                }
//                String ticket = saasUserOauth.getTicket();
//                if(StringUtils.isNotBlank(ticket)){
//                    map.put("ticket",ticket);
//                }
//                Date ticketTime = saasUserOauth.getTicketTime();
//                if(null != ticketTime){
//                    map.put("ticketTime",ticketTime);
//                }
//                String password = saasUserOauth.getPassword();
//                if(StringUtils.isNotBlank(password)){
//                    map.put("password",password);
//                }
//                String tenantId = saasUserOauth.getTenantId();
//                if(StringUtils.isNotBlank(tenantId)){
//                    map.put("tenantId",tenantId);
//                }
//                String oauthPhoto = saasUserOauth.getOauthPhoto();
//                if(StringUtils.isNotBlank(oauthPhoto)){
//                    map.put("oauthPhoto",oauthPhoto);
//                }
//                String oauthUserName = saasUserOauth.getOauthUserName();
//                if(StringUtils.isNotBlank(oauthUserName)){
//                    map.put("oauthUserName",oauthUserName);
//                }
//                String oauthAccessToken = saasUserOauth.getOauthAccessToken();
//                if(StringUtils.isNotBlank(oauthAccessToken)){
//                    map.put("oauthAccessToken",oauthAccessToken);
//                }
//                String oauthRefreshToken = saasUserOauth.getOauthRefreshToken();
//                if(StringUtils.isNotBlank(oauthRefreshToken)){
//                    map.put("oauthRefreshToken",oauthRefreshToken);
//                }
//                String oauthExpires = saasUserOauth.getOauthExpires();
//                if(StringUtils.isNotBlank(oauthExpires)){
//                    map.put("oauthExpires",oauthExpires);
//                }
//                Integer state = saasUserOauth.getState();
//                if(null != state){
//                    map.put("state",state);
//                }
//                Integer status = saasUserOauth.getStatus();
//                if(null != status) {
//                    map.put("stauts", status);
//                }
//                Integer isBind = saasUserOauth.getIsBind();
//                if(null != isBind){
//                    map.put("isBind",isBind);
//                }
//                map.put("updateTime",new Date());
//                int i = saasUserOauthMapper.updateSaasUserOauth(map);
                int i = saasUserOauthMapper.updateByPrimaryKeySelective(saasUserOauth);
                if (i > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean saveOAuthUserInfo(SaasUserOauthInfo saasUserOauthInfo) {
        String nickName = saasUserOauthInfo.getNickName();
        if(org.apache.commons.lang.StringUtils.isNotBlank(nickName)) {
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
    public boolean updateOAuthUserInfo(SaasUserOauthInfo saasUserOauthInfo) {
        String nickName = saasUserOauthInfo.getNickName();
        if(org.apache.commons.lang.StringUtils.isNotBlank(nickName)) {
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
    public boolean saveOAuthLoginInfo(SaasUserOauth saasUserOauth) {
        String nickName = saasUserOauth.getOauthUserName();
        if(org.apache.commons.lang.StringUtils.isNotBlank(nickName)) {
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
    public SaasTenantInfo selectSaasTenantInfo(SaasTenantInfo saasTenantInfo) {
        List<SaasTenantInfo> select = this.saasTenantInfoMapper.select(saasTenantInfo);
        if(null!=select&&select.size()>0){
            return select.get(0);
        }
        return null;
    }

    @Override
    public SaasTenantInfo selectSaasTenantInfo(String tenantId) {
        if(org.apache.commons.lang.StringUtils.isNotBlank(tenantId)){
            SaasTenantInfo saasTenantInfo = new SaasTenantInfo();
            saasTenantInfo.setTenantId(tenantId);
            return selectSaasTenantInfo(saasTenantInfo);
        }
        return null;
    }

    @Override
    public boolean deleteOAuthLoginInfo(SaasUserOauth saasUserOauth) {
        String nickName = saasUserOauth.getOauthUserName();
        if(org.apache.commons.lang.StringUtils.isNotBlank(nickName)) {
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
        if(org.apache.commons.lang.StringUtils.isNotBlank(nickName)) {
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
            List<SaasUserOauth> list = new ArrayList<>();
            for(SaasUserOauth saasUserOauths1 : sysUserOauths) {
                String wechat = saasUserOauths1.getOauthUserName();
                if (StringUtils.isNotBlank(wechat) && StringUtil.isHexNumber(wechat)) {
                    try {
                        wechat = StringUtil.parseHexString(wechat);
                        saasUserOauths1.setOauthUserName(wechat);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                list.add(saasUserOauths1);
            }
            return list;
        }
        return null;
    }

    @Override
    public SaasUserOauthInfo selectUserOauthInfoByOauthUserId(String userOauthId, String oauthType) {
        SaasUserOauthInfo saasUserOauthInfo = new SaasUserOauthInfo();
        //判断查询类型为微信类型
        if ("3".equals(oauthType)) {
            Example example = new Example(SaasUserOauthInfo.class,true,true);
            example.createCriteria().andEqualTo("userOauthId",userOauthId);
            List<SaasUserOauthInfo> saasUserOauthInfos = saasUserOauthInfoMapper.selectByExample(example);
            if(null != saasUserOauthInfos&&saasUserOauthInfos.size()>0){
                saasUserOauthInfo = saasUserOauthInfos.get(0);
                String wechat = saasUserOauthInfo.getNickName();
                if (org.apache.commons.lang.StringUtils.isNotBlank(wechat)&&StringUtil.isHexNumber(wechat)) {
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
    public SaasUserWeb selectUserLoginInfo(SaasUserWeb saasUserWeb) {
        if(null != saasUserWeb) {
            List<SaasUserWeb> sysUserWebs = saasUserWebMapper.select(saasUserWeb);
            //判断查询出的sysUserWebs是否为空
            if (null != sysUserWebs && sysUserWebs.size() > 0) {
                return sysUserWebs.get(0);
            }
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
    public boolean updateUserLoginInfo(SaasUserWeb saasUserWeb) {
        int i = this.saasUserWebMapper.updateByPrimaryKeySelective(saasUserWeb);
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

/*    @Autowired
    private SaasTenantInfoMapper saasTenantInfoMapper;*/
    /**
     * 根据租户id获取租户信息
     * @param tenantId
     * @return
     */
    public SaasTenantInfo selectSaasTenantInfoByTenantId(String tenantId){
        if(StringUtils.isNotBlank(tenantId)) {
            Example example = new Example(SaasTenantInfo.class);
            example.createCriteria().andEqualTo("tenantId",tenantId);
            List<SaasTenantInfo> saasTenantInfos = saasTenantInfoMapper.selectByExample(example);
            if(null != saasTenantInfos && saasTenantInfos.size() > 0){
                return saasTenantInfos.get(0);
            }
        }
        return null;
    }

    @Override
    public boolean checkPhoneNumber(String phoneNumber) {
        SaasUserWeb saasUserWeb = new SaasUserWeb();
        saasUserWeb.setAccount(phoneNumber);
        SaasUserWeb isSysUserWeb = selectUserLoginInfo(saasUserWeb);
        if (null != isSysUserWeb) {
            return true;
        }
        return false;
    }

/*    @Override
    public boolean checkTenantName(String tenantName) {
        //查询公司名是否存在
        SaasTenant saasTenant = new SaasTenant();
        saasTenant.setTenantName(tenantName);
        SaasTenant isSaasTenant = selectSaasTenantServcie(saasTenant);
        if (null != isSaasTenant) {
            return true;
        }
        return false;
    }*/
@Override
public boolean checkTenantName(String tenantName) {
    //查询公司名是否存在
    Example example = new Example(SaasTenant.class);
    example.createCriteria().andEqualTo("tenantName",tenantName).andNotEqualTo("status",2);
    List<SaasTenant> isSaasTenant = saasTenantMapper.selectByExample(example);
    if (null != isSaasTenant && isSaasTenant.size() > 0) {
        return true;
    }
    return false;
}
    @Override
    public Map<String, Object> checkWechatInfoChange(WechatSNSUserInfoVo snsUserInfo) {
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
        List<SaasUserOauth> saasUserOauths = new ArrayList<>();
        String userOauthId = "";
        if(null!=userOauth&&userOauth.size()>0) {
            for(SaasUserOauth saasUserOauth2:userOauth) {
                int flagg = 0;
                SaasUserOauth saasUserOauth1 = new SaasUserOauth();
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
                if(StringUtils.isNotBlank(saasUserOauth1.getId())){
                    saasUserOauths.add(saasUserOauth1);
                }
            }
            if(saasUserOauths.size()>0){
                map.put("saasUserOauth", saasUserOauths);
            }
        }
        SaasUserOauthInfo saasUserOauthInfo = new SaasUserOauthInfo();
        if(StringUtils.isNotBlank(userOauthId)) {
            SaasUserOauthInfo userOauthInfo = selectUserOauthInfoByOauthUserId(userOauthId, "3");
            if (null != userOauthInfo) {
                int flag = 0;
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
        return map;
    }

    @Override
    public SaasSetting selectCommodityConfigInfo(String settingKey) {
        return this.saasSettingMapper.selectBySettingKeySaasSetting(settingKey);
    }

    /**
     * 根据租户id更改租户下用户的公共默认租户id
     */
    public boolean updateSysUserStatusByTenantId(String tenantId){
        if(StringUtils.isNotBlank(tenantId)) {
            return saasUserWebMapper.updateUserTenantIdByTenantId(tenantId);
        }
        return false;
    }
    /**
     * 根据租户id删除租户信息功能
     * @param tenantId
     * @return
     */
    public int delSaasTenantById(String tenantId){
        if(StringUtils.isNotBlank(tenantId)) {
            return saasTenantMapper.deleteByPrimaryKey(tenantId);
        }
        return 0;
    }

    /**
     * 删除saastenantinfo功能
     * @param tenantId
     * @return
     */
    public int delSaasTenantInfoByTenantId(String tenantId){
        if(StringUtils.isNotBlank(tenantId)) {
            Example example = new  Example(SaasTenantInfo.class);
            example.createCriteria().andEqualTo("tenantId",tenantId);
            return saasTenantInfoMapper.deleteByExample(example);
        }
        return 0;
    }

    /**
     * 根据租户id修改saas_user_oauth租户id信息
     * @param tenantId
     * @return
     */
    public boolean updateSaasUserOauthTenantIdByTenantId(String tenantId){
        if(StringUtils.isNotBlank(tenantId)) {
            return saasUserOauthMapper.updateSaasUserOauthTenantIdByTenantId(tenantId);
        }
        return false;
    }
    /**
     * 根据租户id获取saasUserOuth的数量
     * @param tenantId
     * @return
     */
    public int selectSaasUserOuthByTenantId(String tenantId){
        int num = 0;
        if(StringUtils.isNotBlank(tenantId)) {
            Example example = new Example(SaasUserOauth.class);
            example.createCriteria().andEqualTo("tenantId", tenantId);
            List<SaasUserOauth> saasUserOauths = saasUserOauthMapper.selectByExample(example);
            if(saasUserOauths != null && saasUserOauths.size() > 0){
                num = saasUserOauths.size();
            }
        }
        return num;
    }

    /**
     * 根据租户id修改saas_user_tenant_middle租户id信息
     * @param tenantId
     * @return
     */
    public int delSaasUserTenantMiddleByTenantId(String tenantId){
        if(StringUtils.isNotBlank(tenantId)) {
            return saasUserTenantMiddleMapper.delSaasUserTenantMiddleByTenantId(tenantId);
        }
        return 0;
    }

    /**
     * 根据租户id删除tenant_order_log信息
     * @param tenantId
     * @return
     */
    public int delTenantOrderLogByTenantId(String tenantId){
        if(StringUtils.isNotBlank(tenantId)){
           return tenantOrderLogMapper.deleteByPrimaryKey(tenantId);
        }
        return 0;
    }

    @Autowired
    private DissolutionuserinfoMapper dissolutionuserinfoMapper;

    @Autowired
    private DatasnapshotMapper datasnapshotMapper;
    /**
     * 保存解散企业用户的备份信息功能
     * @param dissolutionuserinfo
     */
    public boolean saveDissolutionuserinfo(Dissolutionuserinfo dissolutionuserinfo){
        if(null != dissolutionuserinfo){
            Example example = new Example(Dissolutionuserinfo.class);
            example.createCriteria().andEqualTo("userId",dissolutionuserinfo.getUserId()).andEqualTo("tenantId",dissolutionuserinfo.getTenantId());
            List<Dissolutionuserinfo> dissolutionuserinfos = dissolutionuserinfoMapper.selectByExample(example);
            if(null == dissolutionuserinfos || dissolutionuserinfos.size() <= 0) {
                int i = dissolutionuserinfoMapper.insertSelective(dissolutionuserinfo);
                if (i <= 0) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 备份租户统计部分信息功能
     * @param datasnapshot
     * @return
     */
    public boolean saveDataSnapshot(Datasnapshot datasnapshot){
        if(null != datasnapshot){
            Example example = new Example(Datasnapshot.class);
            example.createCriteria().andEqualTo("tenantId",datasnapshot.getTenantId());
            List<Datasnapshot> datasnapshots = datasnapshotMapper.selectByExample(example);
            if(null == datasnapshots || datasnapshots.size() <= 0){
                int i = datasnapshotMapper.insertSelective(datasnapshot);
                if(i <= 0){
                    return false;
                }
            }
                return true;


        }
        return  false;
    }

    /**
     * 根据用户的主键id获取用户备份信息数据
     * @param userId
     * @return
     */
    public Dissolutionuserinfo selectDissolutionUserInfo(String userId){
        if(StringUtils.isNotBlank(userId)){
            Example example = new Example(Dissolutionuserinfo.class);
            example.createCriteria().andEqualTo("userId",userId);
            List<Dissolutionuserinfo> dissolutionuserinfos = dissolutionuserinfoMapper.selectByExample(example);
            if(null != dissolutionuserinfos && dissolutionuserinfos.size() > 0){
                return dissolutionuserinfos.get(0);
            }

        }
        return null;
    }

    /**
     * 根据用户的主键删除用户的备份信息
     * @param userId
     * @return
     */
    public boolean deleteDissolutionUserInfoByUserId(String userId){
        if(StringUtils.isNotBlank(userId)){
            Example example = new Example(Dissolutionuserinfo.class);
            example.createCriteria().andEqualTo("userId",userId);
            int i = dissolutionuserinfoMapper.deleteByExample(example);
            if(i >0){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取全部解散的企业
     * @return
     */
    public List<SaasTenant> selectDisbandedEnterprise(){
        Example example = new Example(SaasTenant.class);
        example.createCriteria().andEqualTo("status",2).andEqualTo("deleteFile",0);
       return saasTenantMapper.selectByExample(example);
    }

    /**
     * 修改删除文件的状态
     * @param deleteFileStatus
     * @return
     */
    public boolean updateSaasTenantDeleteFileStatus(String tenantId,int deleteFileStatus){
        SaasTenant saasTenant = new SaasTenant();
        saasTenant.setDeleteFile(deleteFileStatus);
        Example example = new Example(SaasTenant.class);
        example.createCriteria().andEqualTo("id",tenantId);
        int i = saasTenantMapper.updateByExampleSelective(saasTenant, example);
        if(i>0){
            return true;
        }
        return false;
    }

    /**
     * 修改saasUserWeb表中用户的信息
     * @param saasUserWeb
     * @return
     */
    public boolean updateSaasUserWeb(SaasUserWeb saasUserWeb){
        if(null != saasUserWeb){
            int i = saasUserWebMapper.updateByPrimaryKeySelective(saasUserWeb);
            if(i > 0){
                return true;
            }
        }
      return false;
    }
    /**
     * 根据saasUserWeb的主键获取详细信息功能
     * @param userId
     * @return
     */
    public SaasUserWeb selectSaasUserWebByUserId(String userId){
        if(StringUtils.isNotBlank(userId)){
            Example example = new Example(SaasUserWeb.class);
            example.createCriteria().andEqualTo("userId",userId);
            List<SaasUserWeb> saasUserWebs = saasUserWebMapper.selectByExample(example);
            if(null != saasUserWebs && saasUserWebs.size() > 0){
                return saasUserWebs.get(0);
            }
        }
        return null;
    }

    /**
     * 根据租户id获取租户详细信息
     * @param tenandId
     * @return
     */
    public SaasTenantInfo selectSaasTenantInfoByTenandId(String tenandId){
        if(org.apache.commons.lang.StringUtils.isNotBlank(tenandId)){
            Example example = new Example(SaasTenantInfo.class);
            example.createCriteria().andEqualTo("tenantId",tenandId);
            List<SaasTenantInfo> saasTenantInfos = saasTenantInfoMapper.selectByExample(example);
            if(null != saasTenantInfos && saasTenantInfos.size() > 0){
                return saasTenantInfos.get(0);
            }
        }
        return null;
    }

    /**
     * 获取定制节目上传时的租户数据集功能接口
     * @param seachKey
     * @return
     */
    public List<SaasTenant> selectAllSaasTenantBySeachKey(String seachKey,BasePageEntity basePageEntity){
        if(StringUtils.isNotBlank(seachKey)){
            seachKey = "%"+seachKey+"%";
            Map map = new HashMap();
            map.put("seachKey",seachKey);
            if(null != basePageEntity) {
                Integer pageSize = basePageEntity.getPageSize();
                if (null != pageSize && pageSize > 0) {
                    Integer currentPage = basePageEntity.getCurrentPage();
                    if (null != currentPage && currentPage > 0) {
                        map.put("start", (currentPage-1)*pageSize);
                        map.put("pageSize", pageSize);
                    }
                }
            }
            List<SaasTenant> saasTenants = saasTenantMapper.selectSaasTenantListBySeachKey(map);
            return saasTenants;
        }
        return null;
    }


    /**
     * 获取所有定制节目数据列表
     * @return
     */
    public List<SaasTenant> selectAllSaasTenants(){
        Example example = new Example(SaasTenant.class);
        example.createCriteria().andEqualTo("status",1).andEqualTo("isCustomizedTenant",1);
        example.setOrderByClause("create_time desc");
        List<SaasTenant> saasTenants = saasTenantMapper.selectByExample(example);
        return saasTenants;
    }







}
