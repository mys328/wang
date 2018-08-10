package com.thinkwin.auth.localService;

import com.thinkwin.common.model.core.SaasDbConfig;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.core.SaasTenantInfo;

/**
 * 租户接口
 */
public interface LocalSaasTenantServcie {

    public boolean saveSaasTenantServcie(SaasTenant saasTenant);

    /**
     * 方法名：selectSaasTenantServcie</br>
     * 描述：查询单个租户信息信息</br>
     * 参数：[saasTenant] 租户表实体对象</br>
     * 返回值：SaasTenant</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/8  </br>
     */
    public SaasTenant selectSaasTenantServcie(SaasTenant saasTenant);

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
     * 方法名：selectSaasTenantInfoBySaasTenantInfo</br>
     * 描述：根据租户信息表实体查询租户信息</br>
     * 参数：[saasTenantInfo] 租户信息表实体</br>
     * 返回值：List<SaasTenantInfo></br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/14  </br>
     */
    public SaasTenantInfo selectSaasTenantInfoBySaasTenantInfo(SaasTenantInfo saasTenantInfo);

    /**
     * 方法名：saveSaasTenantInfo</br>
     * 描述：增加租户信息</br>
     * 参数：[saasTenantInfo] 租户信息实体</br>
     * 返回值：boolean</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/14  </br>
     */
    public boolean saveSaasTenantInfo(SaasTenantInfo saasTenantInfo);
}
