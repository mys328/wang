package com.thinkwin.publish.service;

import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.publish.PlatformClientVersionUpgradeRecorder;

import java.util.Map;

/*
 * 类说明：
 * @author lining 2018/5/23
 * @version 1.0
 *
 */
public interface PlatformClientVersionUpgradeRecorderService {

    /**
     * 添加终端升级日志
     * @param upgradeRecorder 日志
     * @return
     */
    boolean add(PlatformClientVersionUpgradeRecorder upgradeRecorder);

    /**
     * 查询终端版本升级日志
     * @param clientVerId 终端版本id
     * @param tenantName 租户名称
     * @return
     */
    Map findByClientVersionIdAndTenantName(String clientVerId, String tenantName, BasePageEntity basePageEntity);

    /**
     * 根据租户主键id删除终端版本升级日志
     * @param tenantId
     * @return
     */
    boolean delPlatformClientVersionUpgradeRecorderByTenantId(String tenantId);

    /**
     * 方法名：findPlCliVerUpgradeRecorder</br>
     * 描述查询版本日志是否存在</br>
     * 参数：[tenantId, clientVersionId, hardwareId]</br>
     * 返回值：boolean</br>
     */
    public boolean findPlCliVerUpgradeRecorder(String tenantId,String clientVersionId,String hardwareId/*,String innerTest*/);


}
