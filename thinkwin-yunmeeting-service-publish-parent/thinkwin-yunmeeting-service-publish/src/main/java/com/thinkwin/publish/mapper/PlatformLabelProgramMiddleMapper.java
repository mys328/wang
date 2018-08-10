package com.thinkwin.publish.mapper;

import com.thinkwin.common.model.publish.PlatformLabelProgramMiddle;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface PlatformLabelProgramMiddleMapper extends Mapper<PlatformLabelProgramMiddle> {
    /**
     * 根据节目主键id批量删除节目和标签的关联关系表
     * @param map
     * @return
     */
    int delPlatformLabelProgramMiddle(Map map);

    /**
     * 根据节目标签主键id删除相对应的节目和标签的关联关系
     * @param platformProgrameLabelId
     * @return
     */
    int deletePlatformLabelProgramMiddleByLabelId(String platformProgrameLabelId);

    /**
     * 根据节目的主键id获取该节目的所有标签的主键集合
     * @param platformProgramId
     * @return
     */
    List<String> selPlatformProgramLabelIdsByprogramId(String platformProgramId);


    /**
     * 根据标签id获取所有的节目主键集合
     * @param platformProgrameLabelId
     * @return
     */
    List<String> selectPlatformProgrameIdsByLabelId(String platformProgrameLabelId);

}