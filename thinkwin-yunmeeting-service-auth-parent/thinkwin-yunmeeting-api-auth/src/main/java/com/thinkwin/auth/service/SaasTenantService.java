package com.thinkwin.auth.service;

import com.github.pagehelper.Page;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.core.SaasTenantInfo;

import java.util.List;

/**
 * 类名: SaasTenantService </br>
 * 描述:租户表信息接口</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/6/8 </br>
 */
public interface SaasTenantService {
    /**
     * 方法名：saveSaasTenantServcie</br>
     * 描述：新用户注册创建新用户注册信息</br>
     * 参数：[saasTenant]  租户实体信息</br>
     * 返回值：boolean</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/8  </br>
     */
    public boolean saveSaasTenantServcie(SaasTenant saasTenant);

    /**
     * 方法名：updateSaasTenantService</br>
     * 描述：修改租户信息</br>
     * 参数：saasTenant 租户实体</br>
     * 返回值：</br>
     */
    boolean updateSaasTenantService(SaasTenant saasTenant);
    /**
     * 方法名：deleteSaasTenantServcie</br>
     * 描述：删除租户信息是将租户设置为不可用</br>
     * 参数：[tenantId] 租户Id</br>
     * 返回值：boolean</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/8  </br>
     */
    public boolean deleteSaasTenantServcie(String tenantId);
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
     * 方法名：selectSaasTenantServcie</br>
     * 描述：查询租户信息根据租户Id</br>
     * 参数：租户Id</br>
     * 返回值：SaasTenant</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/8  </br>
     */
    public SaasTenant selectSaasTenantServcie(String tenantId);
    /**
     * 方法名：selectAllSaasTenantServcie</br>
     * 描述：查询全部租户信息（带分页）</br>
     * 参数：[page] 分页实体</br>
     * 返回值：List<SaasTenant></br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/8  </br>
     */
    public List<SaasTenant> selectAllSaasTenantServcie(Page page);
    /**
     * 方法名：selectAllSaasTenantByConditionServcie</br>
     * 描述：按相应的条件查询租户信息（条件查询、模糊查询）(带分页)</br>
     * 参数：[saasTenant,page]</br>
     * 返回值：返回带List<SaasTenant> 实体集合的Page对象数据</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/8  </br>
     */
    public Page selectAllSaasTenantByConditionServcie (SaasTenant saasTenant,Page page);

    /**
     * 根据租户id获取租户详细信息
     * @param tenandId
     * @return
     */
    public SaasTenantInfo selectSaasTenantInfoByTenandId(String tenandId);
}
