package com.thinkwin.publish.service;


import tk.mybatis.mapper.entity.Example;
import com.thinkwin.common.model.publish.PlatformProgrameLabel;

import java.util.List;
import java.util.Map;

/**
 * 标签部分service层
 * User: yinchunlei
 * Date: 2018/4/23
 * Company: thinkwin
 */
public interface PlatformProgrameLabelService {

    /**
     * 创建新的标签功能
     * @param platformProgrameLabel
     * @return
     */
    public String createdPlatformProgrameLabel(PlatformProgrameLabel platformProgrameLabel);

    /**
     * 删除标签功能接口
     * @return
     */
    public boolean delPlatformProgrameLabel (List<String> platformProgrameLabelIds);

    /**
     * 修改标签功能接口
     * @return
     */
    public boolean updatePlatformProgrameLabel (PlatformProgrameLabel platformProgrameLabel);

    /**
     * 查询标签功能接口
     * @return
     */
    public List<PlatformProgrameLabel> selectPlatformProgrameLabel (PlatformProgrameLabel platformProgrameLabel);

    /**
     * 根据主键id查询标签功能接口
     * @return
     */
    public PlatformProgrameLabel selectPlatformProgrameLabelByLabelId (String platformProgrameLabelId);
    /**
     * 根据标签id获取有该标签的所有节目数量功能接口
     * @return
     */
    public Integer selectPlatformProgramNumByLabelId(String labelId);

    /**
     * 根据租户id获取相关的节目、标签、节目标签关系列表功能接口
     * @return
     */
    public Map selectTenantDateByTenantId (String tenantId);

    /**
     * 获取所有的标签
     * @return
     */
    public List<PlatformProgrameLabel> selectPlatformProgrameLabels();
    /**
     * 获取所有的标签
     * @return
     */
    public List<PlatformProgrameLabel> selectPlatformProgrameLabelsNew(String platformProgramLabelId);

    /**
     * 标签排序功能
     * @param platformProgrameLabelId
     * @param sortPlatformProgrameLabelId
     * @param sortType 被移动的标签排序类型 1 参考标签的上方 2 参考标签的下方 3移动到参考标签的目录下
     * @return
     */
    public boolean platformProgrameLabelSort(String platformProgrameLabelId,String sortPlatformProgrameLabelId,String sortType);

    /**
     * 根据标签id获取所有与该标签关联的节目id集合
     * @param platformProgrameLabelId
     * @return
     */
    public List<String> selectPlatformProgrameIdsByLabelId(String platformProgrameLabelId);

    /**
     * 根据标签id获取所有与该标签关联的节目id集合
     * @param platformProgrameLabeId
     * @return
     */
    public List<String> selectPlatformProgrameIdsByLabelType(String platformProgrameLabeId);

    /**
     * 修改标签的状态
     * @param map
     * @return
     */
    public boolean updateLabelStatusByLabelStatus(Map map);

    /**
     * 根据条件查询标签列表功能
     * @param example
     * @return
     */
    public List<PlatformProgrameLabel> selectPlatformProgrameLabelByExample(Example example);

    /**
     * 根据标签主键id选择性修改标签内容
     * @param platformProgrameLabel
     * @return
     */
    public int updateByPrimaryKeySelective(PlatformProgrameLabel platformProgrameLabel);

}
