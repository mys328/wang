package com.thinkwin.publish.service;

/**
 * 节目与租户关联关系Service层
 * User: yinchunlei
 * Date: 2018/7/16
 * Company: thinkwin
 */
public interface PlatformProgramTenantMiddleService {

    /**
     * 添加节目与租户关联关系功能接口
     * @param platformProgrameId
     * @param tenantId
     * @return
     */
    boolean addPlatformProgramTenantMiddle(String platformProgrameId, String tenantId);


    /**
     * 根据租户主键id删除该租户有关的定制节目信息
     * @param tenantId
     * @return
     */
    boolean delTenantCustomizedProgramByTenantId(String tenantId);
}
