package com.thinkwin.core.service;

import com.thinkwin.common.model.core.*;
import com.thinkwin.common.model.db.SysUser;

import java.util.List;
import java.util.Map;

/**
 * 类名: LoginRegisterService </br>
 * 描述: 登录注册service</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/11/13 </br>
 */
public interface LoginRegisterCoreService {

    //根据条件查询域
    List<SaasDbConfig> selectAllSaasDbConfig();

    /**
     * 方法名：createDataBaseAndTable</br>
     * 描述：创建租户数据库和表</br>
     * 参数：[tenantName,tenantId] 租户名称，租户Id</br>
     * 返回值：</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/9  </br>
     */
    public SaasTenant createTenantDataBaseAndTable(String tenantName, String tenantId, SaasDbConfig saasDbConfig);

    /**
     * 方法名：registerValidation</br>
     * 描述：注册校验</br>
     * 参数：[map]</br>
     * 返回值：java.util.Map<java.lang.String,java.lang.Object></br>
     */
    Map<String,Object> registerValidation(Map<String, Object> map);

    /**
     * 方法名：saveUserLoginAndTenantInfo</br>
     * 描述：增加用户登录表和公司表</br>
     * 参数：saasUserWeb saasTenant sysUser</br>
     * 返回值：boolean</br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/27  </br>
     */
    SysUser saveUserLoginAndTenantInfo(SaasUserWeb saasUserWeb, SaasTenant saasTenant, SysUser sysUser, SaasTenantInfo saasTenantInfo, SaasUserOauth saasUserOauth, SaasUserOauthInfo saasUserOauthInfo);

    /**
     * 方法名：saveCreateEterprise</br>
     * 描述：单独增加企业接口</br>
     * 参数：tenantName,sysUser</br>
     * 返回值：boolean</br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/27  </br>
     */
    public SysUser saveCreateEterprise(String tenantName, SysUser sysUser, String uId);

    /**
     * 根据数据库SchemaName删除数据库
     * @param dataBaseName
     * @return
     */
    public boolean delDataBaseByName(String dataBaseName);
}
