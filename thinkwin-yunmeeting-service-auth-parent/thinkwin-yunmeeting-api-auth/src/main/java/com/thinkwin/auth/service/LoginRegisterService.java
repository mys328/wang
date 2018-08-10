package com.thinkwin.auth.service;

import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.*;
import com.thinkwin.common.model.db.SysUser;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.vo.WechatSNSUserInfoVo;

import java.util.List;
import java.util.Map;

/**
 * 类名: LoginRegisterService </br>
 * 描述: 登录注册接口</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/5/19 </br>
 */
public interface LoginRegisterService {
    /**
     * 方法名：selectUserLoginInfo</br>
     * 描述：查询用户名密码表</br>
     * 参数：saasUserWeb 用户名密码实体表  只按照不为空字段查询</br>
     * 返回值：SaasUserWeb  用户名密码表实体对象</br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public SaasUserWeb selectUserLoginInfo(SaasUserWeb saasUserWeb);

    /**
     * 方法名：selectUserLoginInfo</br>
     * 描述：根据用户Id和手机号查询用户名密码表 可以单个条件查询另一个为空</br>
     * 参数：userId 根据用户Id查</br>
     * 返回值：SaasUserWeb  用户名密码表实体对象</br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public SaasUserWeb selectUserLoginInfo(String userId, String phoneNumber);

    /**
     * 方法名：selectOAuthLoginInfo</br>
     * 描述：查询第三方登录表</br>
     * 参数：saasUserOauth 第三方登录表实体 只按照不为空字段查询</br>
     * 返回值：SaasUserOauth  第三方登录表实体对象</br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public List<SaasUserOauth> selectOAuthLoginInfo(SaasUserOauth saasUserOauth);

    /**
     * 方法名：selectUserOauthInfoByOauthUserId</br>
     * 描述：查询第三方信息表根据第三方用户Id和第三方类型</br>
     * 参数：oauthUserId 第三方用户Id</br>
     * 参数：oauthType 第三方类型  QQ:1;weibo:2;weixin:3</br>
     * 返回值：</br>
     */
    public SaasUserOauthInfo selectUserOauthInfoByOauthUserId(String oauthUserId, String oauthType);

    /**
     * 方法名：saveOAuthLoginInfo</br>
     * 描述：增加第三方登录信息</br>
     * 参数：saasUserOauth  第三方登录表信息实体对象</br>
     * 返回值：boolean </br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public boolean saveOAuthLoginInfo(SaasUserOauth saasUserOauth);

    /**
     * 方法名：saveOAuthUserInfo</br>
     * 描述：增加第三方登录信息表信息</br>
     * 参数：saasUserOauthInfo 第三方登录信息表实体对象</br>
     * 返回值：</br>
     */
    public boolean saveOAuthUserInfo(SaasUserOauthInfo saasUserOauthInfo);

    /**
     * 方法名：saveuserlogininfo</br>
     * 描述：增加用户名和密码登录表信息</br>
     * 参数：saasuserweb 用户名密码登录表实体对象</br>
     * 返回值：boolean </br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public boolean saveUserLoginInfo(SaasUserWeb saasUserWeb);

    /**
     * 方法名：updateUserLoginInfo</br>
     * 描述：修改用户名密码登录表信息根据实体key</br>
     * 参数：saasUserWeb 用户名密码登录表实体对象</br>
     * 返回值：boolean </br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public boolean updateUserLoginInfo(SaasUserWeb saasUserWeb);

    /**
     * 方法名：updateUserLoginInfo</br>
     * 描述：修改用户名密码登录表信息根据Example条件</br>
     * 参数：saasUserWeb 用户名密码登录表实体对象</br>
     * 返回值：boolean </br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public boolean updateUserLoginInfo(SaasUserWeb saasUserWeb, Object example);

    /**
     * 方法名：updateOAuthLoginInfo</br>
     * 描述：修改第三方登录表</br>
     * 参数：saasUserOauth  第三方登录表信息实体对象</br>
     * 返回值：boolean </br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public boolean updateOAuthLoginInfo(SaasUserOauth saasUserOauth);

    /**
     * 方法名：updateOAuthUserInfo</br>
     * 描述：修改第三方登录信息表信息</br>
     * 参数：saasUserOauthInfo 第三方登录信息表实体对象</br>
     * 返回值：boolean</br>
     */
    public boolean updateOAuthUserInfo(SaasUserOauthInfo saasUserOauthInfo);

    /**
     * 方法名：deleteOAuthLoginInfo</br>
     * 描述：删除第三方登录表(用于第三方用户解绑)</br>
     * 参数：saasUserOauth  第三方登录表信息实体对象</br>
     * 返回值：boolean </br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public boolean deleteOAuthLoginInfo(SaasUserOauth saasUserOauth);

    /**
     * 方法名：deleteOAuthUserInfo</br>
     * 描述：删除第三方信息表</br>
     * 参数：saasUserOauthInfo 第三方登录信息表实体对象</br>
     * 返回值：boolean</br>
     */
    public boolean deleteOAuthUserInfo(SaasUserOauthInfo saasUserOauthInfo);

    /**
     * 方法名：saveUserLoginAndTenantInfo</br>
     * 描述：增加用户登录表和公司表</br>
     * 参数：saasUserWeb saasTenant sysUser</br>
     * 返回值：boolean</br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/27  </br>
     */
    //public boolean saveUserLoginAndTenantInfo(SaasUserWeb saasUserWeb, SaasTenant saasTenant, SysUser sysUser, SaasTenantInfo saasTenantInfo);
    public Map saveUserLoginAndTenantInfo(SaasUserWeb saasUserWeb,
                                          SaasTenant saasTenant,
                                          SysUser sysUser,
                                          SaasTenantInfo saasTenantInfo,
                                          SaasUserOauth saasUserOauth,
                                          SaasUserOauthInfo saasUserOauthInfo);

    /**
     * 方法名：saveCreateEterprise</br>
     * 描述：单独增加企业接口</br>
     * 参数：tenantName,sysUser</br>
     * 返回值：boolean</br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/27  </br>
     */
    public Map saveCreateEterprise(String tenantName, SysUser sysUser,String uId);

    /**
     * 方法名：checkPhoneNumber</br>
     * 描述：校验手机号是否存在</br>
     * 参数：[phoneNumber]</br>
     * 返回值：boolean</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/8  </br>
     */
    public boolean checkPhoneNumber(String phoneNumber);

    /**
     * 方法名：checkTenantName</br>
     * 描述：校验公司名是否存在</br>
     * 参数：[tenantName]</br>
     * 返回值：boolean</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/8  </br>
     */
    public boolean checkTenantName(String tenantName);

    /**
     * 方法名：saveSysUserTenantMiddle</br>
     * 描述：增加用户和公司中间表</br>
     * 参数：saasUserTenantMiddle</br>
     * 返回值：boolean</br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/27  </br>
     */
    public boolean saveSysUserTenantMiddle(SaasUserTenantMiddle saasUserTenantMiddle);

    /**
     * 方法名：updateSysUserTenantMiddle</br>
     * 描述：修改用户和公司中间表</br>
     * 参数：saasUserTenantMiddle</br>
     * 返回值：boolean</br>
     */
    public boolean updateSysUserTenantMiddle(SaasUserTenantMiddle saasUserTenantMiddle);

    /**
     * 方法名：selectSaasUserTenantMiddle</br>
     * 描述：查询用户和租户中间表</br>
     * 参数：userId 用户Id，tenantId 租户Id</br>
     * 返回值：</br>
     */
    public List<SaasUserTenantMiddle> selectSaasUserTenantMiddle(String userId, String tenantId);

    /**
     * 方法名：selectSaasUserTenantMiddle</br>
     * 描述：查询用户和租户中间表带分页</br>
     * 参数：userId 用户Id，tenantId 租户Id</br>
     * 返回值：</br>
     */
    public PageInfo selectSaasUserTenantMiddleByPage(BasePageEntity basePageEntity, SaasUserTenantMiddle saasUserTenantMiddle);
    /**
     * 方法名：checkWechatInfoChange</br>
     * 描述：校验微信用户信息是否改变</br>
     * 参数：wechatUserInfo  微信信息</br>
     * 返回值：</br>
     */
    public Map<String,Object> checkWechatInfoChange(WechatSNSUserInfoVo snsUserInfo);
    public boolean createCompany();
}
