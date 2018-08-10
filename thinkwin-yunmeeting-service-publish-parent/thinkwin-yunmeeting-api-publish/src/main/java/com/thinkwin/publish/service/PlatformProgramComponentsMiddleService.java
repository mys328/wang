package com.thinkwin.publish.service;

/**
 * 节目与组件关联关系service层
 * User: yinchunlei
 * Date: 2018/7/16
 * Company: thinkwin
 */
public interface PlatformProgramComponentsMiddleService {

    /**
     * 添加节目与组件关联关系功能接口
     * @param platformProgrameId
     * @param ttt
     * @return
     */
    boolean addPlatformProgramComponentsMiddle(String platformProgrameId,String ttt);
}
