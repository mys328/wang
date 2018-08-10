package com.thinkwin.publish.service.impl;

import com.github.pagehelper.PageHelper;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.publish.PlatformProgram;
import com.thinkwin.common.model.publish.PlatformProgramVersion;
import com.thinkwin.common.model.publish.PlatformProgrameLabel;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.publish.mapper.PlatformProgramMapper;
import com.thinkwin.publish.mapper.PlatformProgramVersionMapper;
import com.thinkwin.publish.mapper.PlatformProgrameLabelMapper;
import com.thinkwin.publish.service.PlatformProgramVersionSerevice;
import com.thinkwin.publish.service.PlatformProgrameLabelService;
import com.thinkwin.publish.service.PlatformProgrameService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 版本管理部分service逻辑层
 * User: yinchunlei
 * Date: 2018/4/26
 * Company: thinkwin
 */
@Service("platformProgramVersionSerevice")
public class PlatformProgramVersionSereviceImpl implements PlatformProgramVersionSerevice {

    @Autowired
    PlatformProgramVersionMapper platformProgramVersionMapper;
    @Autowired
    PlatformProgrameLabelService platformProgramLabelService;
    @Autowired
    PlatformProgrameService platformProgrameService;
    @Autowired
    PlatformProgrameLabelMapper platformProgrameLabelMapper;
    @Autowired
    PlatformProgramMapper platformProgramMapper;
    /**
     * 获取全部的版本更新记录信息集合
     * @return
     */
    public List<PlatformProgramVersion> selectPlatformProgramVersion(){
        Example example = new Example(PlatformProgramVersion.class);
        example.createCriteria().andIsNull("customTenantId");
        example.setOrderByClause("publish_time desc");
        return platformProgramVersionMapper.selectByExample(example);
    }

    /**
     * 获取全部的版本更新记录信息集合(带有版本号的集合)
     * @return
     */
    public List<PlatformProgramVersion> selectPlatformProgramVersionNew(){
        Example example = new Example(PlatformProgramVersion.class);
        example.createCriteria().andIsNotNull("programVersionNum").andIsNull("customTenantId");
        example.setOrderByClause("publish_time desc");
        return platformProgramVersionMapper.selectByExample(example);
    }

    /**
     * 获取全部的版本更新记录信息集合(带有版本号的集合和分页)
     * @return
     */
    public List<PlatformProgramVersion> selectPlatformProgramVersionNew1(BasePageEntity basePageEntity){
        Example example = new Example(PlatformProgramVersion.class);
        example.createCriteria().andIsNotNull("programVersionNum").andIsNull("customTenantId");
        example.setOrderByClause("publish_time desc");
        PageHelper.startPage(basePageEntity.getCurrentPage(),basePageEntity.getPageSize());
        return platformProgramVersionMapper.selectByExample(example);
    }

    /**
     * 获取全部的版本更新记录信息集合
     * @return
     */
    public PlatformProgramVersion selectPlatformProgramVersionById(String platformProgramVersionId){
        if(StringUtils.isNotBlank(platformProgramVersionId)){
            return platformProgramVersionMapper.selectByPrimaryKey(platformProgramVersionId);
        }
        return null;
    }

    /**
     * 添加新的版本更新记录
     * @param platformProgramVersion
     * @return
     */
    public boolean addPlatformProgramVersion(PlatformProgramVersion platformProgramVersion){
        if(null != platformProgramVersion){
            String id = platformProgramVersion.getId();
            if(StringUtils.isBlank(id)){
                platformProgramVersion.setId(CreateUUIdUtil.Uuid());
            }
            platformProgramVersion.setVersionUpdateBatch(CreateUUIdUtil.Uuid());
            platformProgramVersion.setCreatTime(new Date());
            platformProgramVersion.setVer(CreateUUIdUtil.Uuid());
            int i = platformProgramVersionMapper.insertSelective(platformProgramVersion);
            if(i > 0){
                return true;
            }
        }
        return false;
    }


    /**
     * 根据版本更新的主键id删除版本更新内容
     * @param platformProgarmVersionId
     * @return
     */
    public boolean delPlatformProgramVersion(String platformProgarmVersionId){
        if(StringUtils.isNotBlank(platformProgarmVersionId)){
            int i = platformProgramVersionMapper.deleteByPrimaryKey(platformProgarmVersionId);
            if(i < 0){
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 修改版本更新内容
     * @param platformProgramVersion
     * @return
     */
    public boolean updatePlatformProgarmVersion(PlatformProgramVersion platformProgramVersion){
        if(null != platformProgramVersion){
            String id = platformProgramVersion.getId();
            if(StringUtils.isBlank(id)){
                return false;
            }
            platformProgramVersion.setModifyTime(new Date());
            platformProgramVersion.setVersionUpdateBatch(CreateUUIdUtil.Uuid());
            platformProgramVersion.setVer(CreateUUIdUtil.Uuid());
            int i = platformProgramVersionMapper.updateByPrimaryKeySelective(platformProgramVersion);
            if (i < 0) {
                return false;
            }else {
                return true;
            }
        }
        return false;
    }
    /**
     * 同步节目功能
     * @param platformProgramVersion
     * @return
     */
    @Transactional
    public boolean newAddPlatformProgramVersion(PlatformProgramVersion platformProgramVersion){
        platformProgramVersion.setPublishTime(new Date());
        boolean b = addPlatformProgramVersion(platformProgramVersion);
        if(b){
            return true;
        }
        return false;
    }


    /**
     * 版本更新功能中批次号和ver维护方法
     * @param type  1:只修改批次号 2：只修改ver字段
     * @return
     */
    public boolean updatePlatformProgramVersionVersionUpdateBatch(String type){
        if(StringUtils.isNotBlank(type)){
            List<PlatformProgramVersion> platformProgramVersions = selectPlatformProgramVersion();
            if(null != platformProgramVersions && platformProgramVersions.size() > 0) {
                PlatformProgramVersion platformProgramVersion1 = platformProgramVersions.get(0);
                PlatformProgramVersion platformProgramVersion = new PlatformProgramVersion();
                platformProgramVersion.setId(platformProgramVersion1.getId());
                if ("1".equals(type)) {
                    platformProgramVersion.setVersionUpdateBatch(CreateUUIdUtil.Uuid());
                } else if ("2".equals(type)) {
                    platformProgramVersion.setVer(CreateUUIdUtil.Uuid());
                }else if("3".equals(type)){
                    platformProgramVersion.setVersionUpdateBatch(CreateUUIdUtil.Uuid());
                    platformProgramVersion.setVer(CreateUUIdUtil.Uuid());
                }
                platformProgramVersion.setModifyTime(new Date());
                int i = platformProgramVersionMapper.updateByPrimaryKeySelective(platformProgramVersion);
                if (i < 0) {
                    return false;
                } else {
                    return false;
                }
            }else{
                PlatformProgramVersion ppv = new PlatformProgramVersion();
                return addPlatformProgramVersion(ppv);
            }
        }
        return false;
    }


    /**
     * 获取更新状态接口
     * @return
     */
    public Map getUpdateInfo(String tenantId){
        Map map = new HashMap();
        List<PlatformProgramVersion> platformProgramVersions = selectPlatformProgramVersionNew2(null);
        if (null != platformProgramVersions && platformProgramVersions.size() > 0) {
            PlatformProgramVersion platformProgramVersion1 = platformProgramVersions.get(0);
            if (null != platformProgramVersion1) {
                String programVersionNum = platformProgramVersion1.getProgramVersionNum();
                if (StringUtils.isNotBlank(programVersionNum)) {
                    map.put("programVersionNum", programVersionNum);
                }
                String versionUpdateBatch = platformProgramVersion1.getVersionUpdateBatch();
                if (StringUtils.isNotBlank(versionUpdateBatch)) {
                    map.put("versionUpdateBatch", versionUpdateBatch);
                }
            }
        }
        if(StringUtils.isBlank(tenantId)) {
            map.put("customizedProgramVersionNum",null);
            map.put("customizedVersionUpdateBatch",null);
        }else {
            List<PlatformProgramVersion> platformProgramVersions11 = selectPlatformProgramVersionNew2(tenantId);
            if (null != platformProgramVersions11 && platformProgramVersions11.size() > 0) {
                PlatformProgramVersion platformProgramVersion12 = platformProgramVersions11.get(0);
                if (null != platformProgramVersion12) {
                    String programVersionNum1 = platformProgramVersion12.getProgramVersionNum();
                    if (StringUtils.isNotBlank(programVersionNum1)) {
                        map.put("customizedProgramVersionNum",programVersionNum1);
                    }
                    String versionUpdateBatch1 = platformProgramVersion12.getVersionUpdateBatch();
                    if (StringUtils.isNotBlank(versionUpdateBatch1)) {
                        map.put("customizedVersionUpdateBatch",versionUpdateBatch1);
                    }
                }
            }

        }
        return map;
    }

    /**
     * 获取对应的版本信息数据集
     * @param tenantId
     * @return
     */
    public List<PlatformProgramVersion> selectPlatformProgramVersionNew2(String tenantId){
        Example example = new Example(PlatformProgramVersion.class);
        if(StringUtils.isBlank(tenantId)){
            example.createCriteria().andIsNull("customTenantId");
        }else{
            example.createCriteria().andEqualTo("customTenantId",tenantId);
        }
        example.setOrderByClause("publish_time desc");
        return platformProgramVersionMapper.selectByExample(example);
    }

    /**
     * 节目内测功能
     * @return
     */
    public boolean platformProgrameBeta(String type){
        if(StringUtils.isBlank(type) || "0".equals(type)) {
            Example example = new Example(PlatformProgrameLabel.class);
            example.createCriteria().andEqualTo("labelStatus", 0);
            List<PlatformProgrameLabel> platformProgrameLabels = platformProgrameLabelMapper.selectByExample(example);
            if (null != platformProgrameLabels && platformProgrameLabels.size() > 0) {
                for (PlatformProgrameLabel platformProgrameLabel : platformProgrameLabels) {
                    //编辑需要改变的标签值表格中需要添加ver属性
                    if (null != platformProgrameLabel) {
                        PlatformProgrameLabel ppl1 = new PlatformProgrameLabel();
                        ppl1.setId(platformProgrameLabel.getId());
                        ppl1.setLabelUpdateBatch(CreateUUIdUtil.Uuid());
                        ppl1.setModifyTime(new Date());
                        ppl1.setVer(CreateUUIdUtil.Uuid());
                        ppl1.setLabelStatus("2");
                        int i = platformProgrameLabelMapper.updateByPrimaryKeySelective(ppl1);
                        if (i < 0) {
                            return false;
                        }
                    }
                }
            }
        }
        List<PlatformProgram> platformPrograms = new ArrayList<>();
        if(StringUtils.isBlank(type) || "0".equals(type)) {
            Example example = new Example(PlatformProgram.class);
            example.createCriteria().andEqualTo("recorderStatus",1).andEqualTo("programType",type).andEqualTo("programStatus", 0);
            //if(type.equals("0")){
                Example example1 = new Example(PlatformProgram.class);
                Example.Criteria programType = example1.createCriteria().andIsNull("programType").andEqualTo("recorderStatus",1).andEqualTo("programStatus", 0);
                example.or(programType);
            //}
            platformPrograms = platformProgramMapper.selectByExample(example);
        }
        if(StringUtils.isNotBlank(type) && "1".equals(type)){
            Example example1 = new Example(PlatformProgram.class);
            example1.createCriteria().andEqualTo("programType",type).andEqualTo("programStatus", 0).andEqualTo("recorderStatus", 1);
            platformPrograms = platformProgramMapper.selectByExample(example1);
        }
        if(null != platformPrograms && platformPrograms.size() > 0){
            for (PlatformProgram pfp:platformPrograms) {
                if(null != pfp){
                    PlatformProgram pfp1 = new PlatformProgram();
                    pfp1.setId(pfp.getId());
                    pfp1.setProgramStatus("2");
                    pfp1.setVer(CreateUUIdUtil.Uuid());
                    pfp1.setModifyTime(new Date());
                    pfp1.setProgramUpdateBatch(CreateUUIdUtil.Uuid());
                    int i1 = platformProgramMapper.updateByPrimaryKeySelective(pfp1);
                    if(i1 < 0){
                        return false;
                    }
                }
            }
        }
        return true;
    }



    /**
     * 根据类型获取对应的节目版本数据集
     * @param type
     * @return
     */
    public List<PlatformProgramVersion> newSelectPlatformProgramVersion(String type,String tenantId){
        Example example = new Example(PlatformProgramVersion.class);
        if(StringUtils.isBlank(type) || "0".equals(type)){
            example.createCriteria().andIsNull("customTenantId");
        }else if(StringUtils.isNotBlank(type) || "1".equals(type)){
            example.createCriteria().andEqualTo("customTenantId",tenantId);
        }
        example.setOrderByClause("publish_time desc");
        return platformProgramVersionMapper.selectByExample(example);
    }













}
