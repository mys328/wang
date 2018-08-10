package com.thinkwin.auth.service;

import com.thinkwin.common.model.core.SaasTenantInfo;

/**
 * 类名: SaasTenantInfoService </br>
 * 描述: 租户信息表接口</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/6/19 </br>
 */
public interface SaasTenantInfoService {
    /**
     * 方法名：saveSaasTenantInfo</br>
     * 描述：增加租户信息表接口</br>
     * 参数：[saasTenantInfo]</br>
     * 返回值：boolean</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    public boolean saveSaasTenantInfo(SaasTenantInfo saasTenantInfo);
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
     * 方法名：selectSaasTenantInfo</br>
     * 描述：查询租户信息表</br>
     * 参数：[saasTenantInfo]</br>
     * 返回值：com.thinkwin.common.model.core.SaasTenantInfo</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    public SaasTenantInfo selectSaasTenantInfo(SaasTenantInfo saasTenantInfo);
    /**
     * 方法名：selectSaasTenantInfo</br>
     * 描述：查询租户信息表根据租户Id</br>
     * 参数：[saasTenantInfo]</br>
     * 返回值：com.thinkwin.common.model.core.SaasTenantInfo</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    public SaasTenantInfo selectSaasTenantInfo(String tenantId);
}
