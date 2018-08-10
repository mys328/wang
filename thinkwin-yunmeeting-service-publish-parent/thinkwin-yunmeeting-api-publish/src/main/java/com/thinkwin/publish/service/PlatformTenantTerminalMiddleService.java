package com.thinkwin.publish.service;

import com.thinkwin.common.model.publish.PlatformTenantTerminalMiddle;

import java.util.List;

/**
 * 类名: PlatformTenantTerminalMiddleService </br>
 * 描述:租户终端中间关系表接口</br>
 * 开发人员： weining </br>
 * 创建时间：  2018/5/31 </br>
 */
public interface PlatformTenantTerminalMiddleService {

    /**
     * 方法名：selectPTenantTerminalMByTenantId</br>
     * 描述：根据租户Id查询租户终端中间表</br>
     * 参数：[tenantId]</br>
     * 返回值：java.util.List<com.thinkwin.common.model.publish.PlatformTenantTerminalMiddle></br>
     */
    List<PlatformTenantTerminalMiddle> selectPTenantTerminalMByTenantId(String tenantId);

    /**
     * 方法名：selectPTenantTerminalMByHardwareId</br>
     * 描述：根据终端唯一标识查询租户终端中间表</br>
     * 参数：[hardwareId]</br>
     * 返回值：com.thinkwin.common.model.publish.PlatformTenantTerminalMiddle</br>
     */
    PlatformTenantTerminalMiddle selectPTenantTerminalMByHardwareId(String hardwareId);

    PlatformTenantTerminalMiddle selectPTenantTerminalMByTerminalId(String terminalId);

    /**
     * 方法名：selectPTenantTerminalMByEntity</br>
     * 描述：根据租户终端租户表实体查询租户终端中间表</br>
     * 参数：[platformTenantTerminalMiddle]</br>
     * 返回值：java.util.List<com.thinkwin.common.model.publish.PlatformTenantTerminalMiddle></br>
     */
    List<PlatformTenantTerminalMiddle> selectPTenantTerminalMByEntity(PlatformTenantTerminalMiddle platformTenantTerminalMiddle);

    /**
     * 方法名：insertPTenantTerminalMByEntity</br>
     * 描述：增加租户终端中间表根据租户终端中间表实体</br>
     * 参数：[platformTenantTerminalMiddle]</br>
     * 返回值：boolean</br>
     */
    boolean insertPTenantTerminalMByEntity(PlatformTenantTerminalMiddle platformTenantTerminalMiddle);

    /**
     * 方法名：updatePTenantTerminalMByEntity</br>
     * 描述：修改租户终端中间表根据租户终端中间表实体</br>
     * 参数：[platformTenantTerminalMiddle]</br>
     * 返回值：boolean</br>
     */
    boolean updatePTenantTerminalMByEntity(PlatformTenantTerminalMiddle platformTenantTerminalMiddle);

    /**
     * 根据租户id删除该租户与终端的关联关系数据
     * @param tenantId
     * @return
     */
    boolean delPlatformTenantTerminalMiddlByTenantId(String tenantId);
}
