package com.thinkwin.publish.service;

import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.publish.PlatformProgramVersion;

import java.util.List;
import java.util.Map;

/**
 * 版本管理部分service逻辑接口层
 * User: yinchunlei
 * Date: 2018/4/26
 * Company: thinkwin
 */
public interface PlatformProgramVersionSerevice {

    /**
     * 获取全部的版本更新记录信息集合
     * @return
     */
    public List<PlatformProgramVersion> selectPlatformProgramVersion();

    /**
     * 获取全部的版本更新记录信息集合(带有版本号的集合)
     * @return
     */
    public List<PlatformProgramVersion> selectPlatformProgramVersionNew();

    /**
     * 获取全部的版本更新记录信息集合(带有版本号的集合和分页)
     * @return
     */
    public List<PlatformProgramVersion> selectPlatformProgramVersionNew1(BasePageEntity basePageEntity);

    /**
     * 根据id获取版本更新记录信息
     * @return
     */
    public PlatformProgramVersion selectPlatformProgramVersionById(String platformProgramVersionId);

    /**
     * 添加新的版本更新记录
     * @param platformProgramVersion
     * @return
     */
    public boolean addPlatformProgramVersion(PlatformProgramVersion platformProgramVersion);


    /**
     * 根据版本更新的主键id删除版本更新内容
     * @param platformProgarmVersionId
     * @return
     */
    public boolean delPlatformProgramVersion(String platformProgarmVersionId);

    /**
     * 修改版本更新内容
     * @param platformProgramVersion
     * @return
     */
    public boolean updatePlatformProgarmVersion(PlatformProgramVersion platformProgramVersion);


    /**
     * 同步节目功能
     * @param platformProgramVersion
     * @return
     */
    public boolean newAddPlatformProgramVersion(PlatformProgramVersion platformProgramVersion);


    /**
     * 版本更新功能中批次号和ver维护方法
     * @param type  1:只修改批次号 2：只修改ver字段
     * @return
     */
    public boolean updatePlatformProgramVersionVersionUpdateBatch(String type);


    /**
     * 获取更新状态接口
     * @return
     */
    public Map getUpdateInfo(String tenantId);

    /**
     * 节目内测功能
     * @return
     */
    public boolean platformProgrameBeta(String type);

    /**
     * 根据类型获取对应的节目版本数据集
     * @param type
     * @return
     */
    public List<PlatformProgramVersion> newSelectPlatformProgramVersion(String type,String tenantId);

}
