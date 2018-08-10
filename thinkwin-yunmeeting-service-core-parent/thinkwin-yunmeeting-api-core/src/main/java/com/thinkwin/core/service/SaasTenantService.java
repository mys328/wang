package com.thinkwin.core.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.*;
import com.thinkwin.common.vo.WechatSNSUserInfoVo;

import java.util.List;
import java.util.Map;

/**
 * 租户接口
 */
public interface SaasTenantService {
    /*
   * 带参数 分页查询
   * */
    PageInfo<SaasTenant> selectSaasTenantListByPage(BasePageEntity page, SaasTenant saasTenant);

    /**
     * 根据租户id查询租户信息
     * @param tenantId 租户id
     * @return
     */
    SaasTenant selectByIdSaasTenantInfo(String tenantId);

    /**
     * 方法名：updateSaasTenantService</br>
     * 描述：修改租户信息</br>
     * 参数：saasTenant 租户实体</br>
     * 返回值：</br>
     */
    boolean updateSaasTenantService(SaasTenant saasTenant);

    /**
     * 方法名：saveSaasTenantServcie</br>
     * 描述：保存租户信息</br>
     * 参数：saasTenant 租户实体</br>
     * 返回值：</br>
     */
    public boolean saveSaasTenantServcie(SaasTenant saasTenant);

    /**
     * 方法名：updateSaasTenantInfo</br>
     * 描述：修改租户信息表</br>
     * 参数：[saasTenantInfo]</br>
     * 返回值：boolean</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    public boolean updateSaasTenantInfo(SaasTenantInfo saasTenantInfo);

    /**
     * 方法名：saveSaasTenantInfo</br>
     * 描述：增加租户信息</br>
     * 参数：[saasTenantInfo] 租户信息实体</br>
     * 返回值：boolean</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/14  </br>
     */
    public boolean saveSaasTenantInfo(SaasTenantInfo saasTenantInfo);


    /**
     * 方法名：updateSaasTenantService</br>
     * 描述：订单支付成功后修改租户信息</br>
     * 参数：saasTenant 租户实体</br>
     * orderId: 订单ID
     * 返回值：</br>
     */
    boolean updateSaasTenantService(SaasTenant saasTenant, String orderId);

    /**
     * 创建订单后更新租户当前订单状态
     * @param tenantId 租户ID
     * @param orderId 订单ID
     * @return
     */
    boolean updateTenantOrder(String tenantId, String orderId);

    /**
     * 查询租户信息功能接口
     * @param saasTenant
     * @return
     */
    public SaasTenant selectSaasTenantServcie(SaasTenant saasTenant);
    /**
     * 根据租户id查询租户信息功能接口
     * @return
     */
    public SaasTenant selectSaasTenantServcie(String tenantId);

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
     * 方法名：updateOAuthLoginInfo</br>
     * 描述：修改第三方登录表</br>
     * 参数：saasUserOauth  第三方登录表信息实体对象</br>
     * 返回值：boolean </br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/19  </br>
     */
    public boolean updateOAuthLoginInfo(SaasUserOauth saasUserOauth);

    /**
     * 方法名：saveOAuthUserInfo</br>
     * 描述：增加第三方登录信息表信息</br>
     * 参数：saasUserOauthInfo 第三方登录信息表实体对象</br>
     * 返回值：</br>
     */
    public boolean saveOAuthUserInfo(SaasUserOauthInfo saasUserOauthInfo);

    /**
     * 方法名：updateOAuthUserInfo</br>
     * 描述：修改第三方登录信息表信息</br>
     * 参数：saasUserOauthInfo 第三方登录信息表实体对象</br>
     * 返回值：boolean</br>
     */
    public boolean updateOAuthUserInfo(SaasUserOauthInfo saasUserOauthInfo);

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
     * 方法名：selectSaasTenantInfo</br>
     * 描述：查询租户信息表</br>
     * 参数：[saasTenantInfo]</br>
     * 返回值：com.thinkwin.common.model.core.SaasTenantInfo</br>
     */
    public SaasTenantInfo selectSaasTenantInfo(SaasTenantInfo saasTenantInfo);
    /**
     * 方法名：selectSaasTenantInfo</br>
     * 描述：查询租户信息表根据租户Id</br>
     * 参数：[saasTenantInfo]</br>
     * 返回值：com.thinkwin.common.model.core.SaasTenantInfo</br>
     */
    public SaasTenantInfo selectSaasTenantInfo(String tenantId);

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
     * 根据用户信息查询用户
     * @param saasUserWeb
     * @return
     */
    public SaasUserWeb selectUserLoginInfo(SaasUserWeb saasUserWeb);

    public SaasUserWeb selectUserLoginInfo(String userId, String phoneNumber);

    /**
     * 修改用户信息功能
     * @param saasUserWeb
     * @return
     */
    public boolean updateUserLoginInfo(SaasUserWeb saasUserWeb);

    public boolean saveUserLoginInfo(SaasUserWeb saasUserWeb);

    public SaasTenantInfo selectSaasTenantInfoByTenantId(String tenantId);

    /**
     * 方法名：checkPhoneNumber</br>
     * 描述：校验手机号是否存在</br>
     * 参数：[phoneNumber]</br>
     * 返回值：boolean</br>
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
     * 方法名：checkWechatInfoChange</br>
     * 描述：校验微信用户信息是否改变</br>
     * 参数：wechatUserInfo  微信信息</br>
     * 返回值：</br>
     */
    public Map<String,Object> checkWechatInfoChange(WechatSNSUserInfoVo snsUserInfo);
    /**
     * 根据租户id更改租户下用户的公共默认租户id
     */
    public boolean updateSysUserStatusByTenantId(String tenantId);





    /**
     * 根据key查看配置信息
     * @param settingKey
     * @return
     */
    SaasSetting selectCommodityConfigInfo(String settingKey);
    /**
     * 根据租户id删除租户信息功能
     * @param tenantId
     * @return
     */
    public int delSaasTenantById(String tenantId);

    /**
     * 删除saastenantinfo功能
     * @param tenantId
     * @return
     */
    public int delSaasTenantInfoByTenantId(String tenantId);

    /**
     * 根据租户id修改saas_user_oauth租户id信息
     * @param tenantId
     * @return
     */
    public boolean updateSaasUserOauthTenantIdByTenantId(String tenantId);

    /**
     * 根据租户id获取saasUserOuth的数量
     * @param tenantId
     * @return
     */
    public int selectSaasUserOuthByTenantId(String tenantId);

    /**
     * 根据租户id删除saas_user_oauth_info租户id信息
     * @param tenantId
     * @return
     */
    public int delSaasUserTenantMiddleByTenantId(String tenantId);

    /**
     * 根据租户id删除tenant_order_log信息
     * @param tenantId
     * @return
     */
    public int delTenantOrderLogByTenantId(String tenantId);

    /**
     * 保存解散企业用户的备份信息功能
     * @param dissolutionuserinfo
     */
    public boolean saveDissolutionuserinfo(Dissolutionuserinfo dissolutionuserinfo);

    /**
     * 备份租户统计部分信息功能
     * @param datasnapshot
     * @return
     */
    public boolean saveDataSnapshot(Datasnapshot datasnapshot);

    /**
     * 根据用户的主键id获取用户备份信息数据
     * @param userId
     * @return
     */
    public Dissolutionuserinfo selectDissolutionUserInfo(String userId);

    /**
     * 根据用户的主键删除用户的备份信息
     * @param userId
     * @return
     */
    public boolean deleteDissolutionUserInfoByUserId(String userId);

    /**
     * 获取全部解散的企业
     * @return
     */
    public List<SaasTenant> selectDisbandedEnterprise();

    /**
     * 修改删除文件的状态
     * @param deleteFileStatus
     * @return
     */
    public boolean updateSaasTenantDeleteFileStatus(String tenantId,int deleteFileStatus);

    /**
     * 修改saasUserWeb表中用户的信息
     * @param saasUserWeb
     * @return
     */
    public boolean updateSaasUserWeb(SaasUserWeb saasUserWeb);

    /**
     * 根据saasUserWeb的主键获取详细信息功能
     * @param userId
     * @return
     */
    public SaasUserWeb selectSaasUserWebByUserId(String userId);


    /**
     * 根据租户id获取租户详细信息
     * @param tenandId
     * @return
     */
    public SaasTenantInfo selectSaasTenantInfoByTenandId(String tenandId);


    /**
     * 获取定制节目上传时的租户数据集功能接口
     * @param seachKey
     * @return
     */
    public List<SaasTenant> selectAllSaasTenantBySeachKey(String seachKey,BasePageEntity basePageEntity);

    /**
     * 获取所有定制节目数据列表
     * @return
     */
    public List<SaasTenant> selectAllSaasTenants();

}
