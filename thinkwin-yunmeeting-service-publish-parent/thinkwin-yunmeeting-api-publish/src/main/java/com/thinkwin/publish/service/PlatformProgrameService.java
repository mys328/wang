package com.thinkwin.publish.service;

import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.BizImageRecorder;
import com.thinkwin.common.model.publish.PlatformProgram;
import com.thinkwin.common.model.publish.PlatformProgramTenantMiddle;
import com.thinkwin.common.vo.FileVo;
import com.thinkwin.common.vo.consoleVo.PlatformProgrameVo;
import com.thinkwin.common.vo.programe.CustomizingTenantVo;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * 节目部分Service层
 * User: yinchunlei
 * Date: 2018/4/23
 * Company: thinkwin
 */
public interface PlatformProgrameService {
    /**
     * 给单个节目添加单个标签功能接口
     * @return
     */
    public boolean addPlatformLabelProgramMiddle (String platformProgrameId, String platformProgrameLabelId);

    /**
     * 批量节目添加标签功能接口
     * @return
     */
    public boolean programListAddLabel(List<String> platformProgramIds, String platformProgrameLabelId);

    /**
     * 节目添加功能接口
     * @return
     */
    public boolean addPlatformProgram (PlatformProgram platformProgram);

    /**
     * 节目删除功能接口
     * @return
     */
    public boolean delPlatformPrograms(List<String> platformProgramIds);
    /**
     * 节目删除功能接口(添加删除解压路径)
     * @return
     */
    public boolean delPlatformPrograms(List<String> platformProgramIds,String path);

    /**
     * 节目修改功能接口
     * @return
     */
    public boolean updatePlatformProgram(PlatformProgram  platformProgram);

    /**
     * 根据节目主键id查询节目详情的功能接口
     * @return
     */
    public PlatformProgrameVo selectPlatformProgramById(String platformProgramId);
    /**
     * 根据节目主键id查询节目详情的功能接口
     * @return
     */
    public PlatformProgrameVo selectPlatformProgramByType(String platformProgramId,String type);

    /**
     * 查询所有节目详情的功能
     * @return
     */
    public List<PlatformProgram> selectPlatformPrograms(PlatformProgram platformProgram);


    /**
     * 获取所有未删除状态的节目集合数据
     * @return
     */
    public List<PlatformProgram> selectAllPlatformProgram(BasePageEntity page,String type);

    /**
     * 根据标签id获取相对应的数据集
     * @param platformProgrameLabelId
     * @param page
     * @return
     */
    public List<PlatformProgram> selectPlatformProgramByLabelId(String platformProgrameLabelId,BasePageEntity page,String type);

    /**
     * 获取节目总数量
     * @return
     */
    public Integer selectPlatformProgramTotalNum(String type);

    /**
     * 获取节目总数量
     * @return
     */
    public Integer selectPlatformProgramTotalNum(String type,String platformProgrameLabelId);


    /**
     * 为前端获取内测、同步显示按钮机制
     * @return
     */
    public String getBatchOperationState(String type);


    /**
     * 文件存储功能接口
     * @return
     */
    public boolean addBizImageRecorder(String userId,FileVo fileVo);


    /**
     * 根据查询条件查询节目集合
     * @param seachKey
     * @param list
     * @param basePageEntity
     * @return
     */
    public List<PlatformProgram> selectAllPlatformProgramBySeachKey(String seachKey,List list,BasePageEntity basePageEntity,String type);

    /**
     * 根据条件查询符合条件的节目数量
     * @param seachKey
     * @param list
     * @return
     */
    public Integer selectPlatformProgramTotalNumBySeachKey(String seachKey,List list,String type);

    /**
     * 文件的检查和添加修改
     * @param userId
     * @param fileVo
     * @param platformProgrameId
     * @return
     */
    public boolean updateBizImageRecorderByBizId(String userId,FileVo fileVo,String platformProgrameId,String type);

    /**
     * 根据节目名称获取节目信息
     * @param platformProgramName
     * @return
     */
    public List<PlatformProgram> selectPlatformProgramsByName(String platformProgramName);


    /**
     * 根据bizid和类型获取相关信息(查询文件类型信息)
     * @param platformProgrameId
     * @return
     */
    public BizImageRecorder getBizImageRecorderByBizId(String platformProgrameId);


    /**
     * 节目状态修改功能
     * @param map
     * @return
     */
    public boolean updateProgramStatusByStatus(Map map);


    /**
     * 根据条件查询节目功能接口
     * @param example
     * @return
     */
    public List<PlatformProgram> selectPlatformProgramByExample(Example example);

    /**
     * 根据节目的key选择性修改节目内容
     * @param platformProgram
     * @return
     */
    public int updateByPrimaryKeySelective(PlatformProgram platformProgram);

    /**
     * 节目同步功能
     * @return
     */
    public boolean platformProgrameSynchronization(String nextProgramVersionNum,String type);

    /**
     * 获取定制节目上传时的租户数据集功能接口
     * @param seachKey
     * @return
     */
    public List<CustomizingTenantVo> getAllTenants(String seachKey,BasePageEntity basePageEntity);
    /**
     * 获取定制节目上传时的租户数据集功能接口
     * @param
     * @return
     */
    public List<CustomizingTenantVo> getAllTenants();

    /**
     * 获得去重后的所有的有定制节目的租户主键集合接口
     * @return
     */
    public List<PlatformProgramTenantMiddle> selectPlatformProgramTenantMiddle(String tenantId);

    /**
     * 获取所有的节目与租户之间的关联关系
     * @return
     */
    public List<PlatformProgramTenantMiddle> selectPlatformProgramTenantMiddles();

    /**
     * 根据租户主键获取相应的定制节目数量
     * @param tenantId
     * @return
     */
    public Integer selectpPlatformProgramTenantMiddleNumByTenantId(String tenantId);

}
