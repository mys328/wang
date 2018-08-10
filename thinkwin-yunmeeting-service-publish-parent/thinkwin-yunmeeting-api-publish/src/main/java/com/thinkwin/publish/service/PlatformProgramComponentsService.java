package com.thinkwin.publish.service;

import com.thinkwin.common.model.publish.PlatformProgramComponents;
import com.thinkwin.common.model.publish.PlatformProgramComponentsMiddle;

import java.util.List;

/**
 * 节目组件Service层
 * User: yinchunlei
 * Date: 2018/7/17
 * Company: thinkwin
 */
public interface PlatformProgramComponentsService {

    /**
     * 根据节目主键集合获取节目组件关联关系数据集
     * @param platformProgramIds
     * @return
     */
    public List<PlatformProgramComponentsMiddle> selectPlatformProgramComponentsMiddleByProgramIds(List<String> platformProgramIds);


    /**
     * 根据节目主键id集合获取组件数据集
     * @param components
     * @return
     */
    public  List<PlatformProgramComponents> selectPlatformProgramComponentsByProgramIds(List<String> components);

    /**
     * 根据code获取组件信息
     * @param ttt
     * @return
     */
    boolean selectPlatformProgramComponentsByComponentsCode(String ttt);
}
