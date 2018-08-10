package com.thinkwin.publish.service.impl;

import com.thinkwin.common.model.publish.PlatformLabelProgramMiddle;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.publish.mapper.PlatformLabelProgramMiddleMapper;
import com.thinkwin.publish.service.PlatformLabelProgramMiddleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 节目和标签关联关系中间表逻辑层
 * User: yinchunlei
 * Date: 2018/4/26
 * Company: thinkwin
 */
@Service("platformLabelProgramMiddleService")
public class PlatformLabelProgramMiddleServiceImpl implements PlatformLabelProgramMiddleService {

    @Autowired
    PlatformLabelProgramMiddleMapper platformLabelProgramMiddleMapper;

    /**
     * 添加节目标签关联关系表信息（多对多）
     * @return
     */
    @Transactional
    public boolean addPlatformLabelProgramMiddle(List<String> platformProgrameIds, List<String> platformProgrameLabelIds){
        if(null != platformProgrameIds && platformProgrameIds.size() > 0 && null != platformProgrameLabelIds && platformProgrameLabelIds.size() > 0){
            for (String platformProgrameId:platformProgrameIds) {
                if(StringUtils.isNotBlank(platformProgrameId)){
                    for (String platformProgrameLabelId:platformProgrameLabelIds) {
                       if(StringUtils.isNotBlank(platformProgrameLabelId)){
                           PlatformLabelProgramMiddle platformLabelProgramMiddle = new  PlatformLabelProgramMiddle();
                           platformLabelProgramMiddle.setId(CreateUUIdUtil.Uuid());
                           platformLabelProgramMiddle.setProgramId(platformProgrameId);
                           platformLabelProgramMiddle.setProgramLabelId(platformProgrameLabelId);
                           int i = platformLabelProgramMiddleMapper.insertSelective(platformLabelProgramMiddle);
                           if(i <= 0){
                               return false;
                           }
                       }
                    }
                }
            }
            return true;
        }
        return false;
    }


    /**
     * 删除节目标签关联关系表信息（多对多）
     * @return
     */
    public boolean delPlatformLabelProgramMiddle(List<String> platformProgrameIds, List<String> platformProgrameLabelIds){
        if(null != platformProgrameIds && platformProgrameIds.size() > 0 && null != platformProgrameLabelIds && platformProgrameLabelIds.size() > 0){
            for (String platformProgrameId:platformProgrameIds) {
                if(StringUtils.isNotBlank(platformProgrameId)){
                    for (String platformProgrameLabelId:platformProgrameLabelIds) {
                        if(StringUtils.isNotBlank(platformProgrameLabelId)){
                            Example example = new Example(PlatformLabelProgramMiddle.class);
                            example.createCriteria().andEqualTo("programLabelId",platformProgrameLabelId).andEqualTo("programId",platformProgrameId);
                            int i = platformLabelProgramMiddleMapper.deleteByExample(example);
                            if(i < 0){
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }


    /**
     * 根据节目id和标签id获取对应的关联关系
     * @param platformProgrameId
     * @param platformProgrameLabelId
     * @return
     */
    public List<PlatformLabelProgramMiddle> getPlatformLabeProgramMiddleBylabelIdAndProgrameId(String platformProgrameId,String platformProgrameLabelId){
    if(StringUtils.isNotBlank(platformProgrameId) && StringUtils.isNotBlank(platformProgrameLabelId)){
        Example example = new Example(PlatformLabelProgramMiddle.class);
        example.createCriteria().andEqualTo("programId",platformProgrameId).andEqualTo("programLabelId",platformProgrameLabelId);
        return platformLabelProgramMiddleMapper.selectByExample(example);
    }
        return null;
    }













}
