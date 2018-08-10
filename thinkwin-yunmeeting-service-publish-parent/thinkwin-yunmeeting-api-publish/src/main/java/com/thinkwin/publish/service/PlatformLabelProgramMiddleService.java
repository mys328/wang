package com.thinkwin.publish.service;

import com.thinkwin.common.model.publish.PlatformLabelProgramMiddle;

import java.util.List;

/**
 * 节目和标签关联关系中间表逻辑层
 * User: yinchunlei
 * Date: 2018/4/26
 * Company: thinkwin
 */
public interface PlatformLabelProgramMiddleService {

    /**
     * 添加节目标签关联关系表信息（多对多）
     * @return
     */
    public boolean addPlatformLabelProgramMiddle(List<String> platformProgrameIds, List<String> platformProgrameLabelIds);

    /**
     * 删除节目标签关联关系表信息（多对多）
     * @return
     */
    public boolean delPlatformLabelProgramMiddle(List<String> platformProgrameIds, List<String> platformProgrameLabelIds);


    /**
     * 根据节目id和标签id获取对应的关联关系
     * @param platformProgrameId
     * @param platformProgrameLabelId
     * @return
     */
    public List<PlatformLabelProgramMiddle> getPlatformLabeProgramMiddleBylabelIdAndProgrameId(String platformProgrameId,String platformProgrameLabelId);






}
