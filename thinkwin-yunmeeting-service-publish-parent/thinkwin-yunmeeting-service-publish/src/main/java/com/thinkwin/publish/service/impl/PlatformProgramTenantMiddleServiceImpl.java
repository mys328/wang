package com.thinkwin.publish.service.impl;

import com.thinkwin.common.model.db.BizImageRecorder;
import com.thinkwin.common.model.publish.PlatformProgram;
import com.thinkwin.common.model.publish.PlatformProgramComponentsMiddle;
import com.thinkwin.common.model.publish.PlatformProgramTenantMiddle;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.publish.mapper.BizImageRecorderMapper;
import com.thinkwin.publish.mapper.PlatformProgramComponentsMiddleMapper;
import com.thinkwin.publish.mapper.PlatformProgramMapper;
import com.thinkwin.publish.mapper.PlatformProgramTenantMiddleMapper;
import com.thinkwin.publish.service.PlatformProgramTenantMiddleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 节目与租户关联关系Service层实现类
 * User: yinchunlei
 * Date: 2018/7/16
 * Company: thinkwin
 */
@Service("platformProgramTenantMiddleService")
public class PlatformProgramTenantMiddleServiceImpl implements PlatformProgramTenantMiddleService {

    @Autowired
    PlatformProgramTenantMiddleMapper platformProgramTenantMiddleMapper;

       /**
     * 添加节目与租户关联关系功能接口
     * @param platformProgrameId
     * @param tenantId
     * @return
     */
       public  boolean addPlatformProgramTenantMiddle(String platformProgrameId, String tenantId){
           if (StringUtils.isNotBlank(platformProgrameId) && StringUtils.isNotBlank(tenantId)){
               Example example = new Example(PlatformProgramTenantMiddle.class);
               example.createCriteria().andEqualTo("tenantId",tenantId).andEqualTo("programId",platformProgrameId);
               List<PlatformProgramTenantMiddle> platformProgramTenantMiddles = platformProgramTenantMiddleMapper.selectByExample(example);
               if(null != platformProgramTenantMiddles && platformProgramTenantMiddles.size() > 0) {
                return true;
               }else {
                   PlatformProgramTenantMiddle platformProgramTenantMiddle = new PlatformProgramTenantMiddle();
                   platformProgramTenantMiddle.setId(CreateUUIdUtil.Uuid());
                   platformProgramTenantMiddle.setProgramId(platformProgrameId);
                   platformProgramTenantMiddle.setTenantId(tenantId);
                   int i = platformProgramTenantMiddleMapper.insertSelective(platformProgramTenantMiddle);
                   if(i <0){
                       return false;
                   }else {
                       return true;
                   }
               }
           }
           return false;
    }
    @Autowired
    PlatformProgramMapper platformProgramMapper;
       @Autowired
       PlatformProgramComponentsMiddleMapper platformProgramComponentsMiddleMapper;
       @Autowired
       BizImageRecorderMapper bizImageRecorderMapper;

    /**
     * 根据租户主键id删除该租户有关的定制节目信息
     * @param tenantId
     * @return
     */
    public boolean delTenantCustomizedProgramByTenantId(String tenantId){
        if(StringUtils.isNotBlank(tenantId)){
            //删除租户定制的节目信息功能
            List<String> strings = platformProgramTenantMiddleMapper.selectPlatformProgrameIdsByLabelType(tenantId);
            if(null != strings && strings.size() > 0){
                Example example = new Example(PlatformProgram.class);
                example.createCriteria().andEqualTo("recorderStatus",1).andEqualTo("programType",1).andIn("id",strings);
                List<PlatformProgram> platformPrograms = platformProgramMapper.selectByExample(example);
                List list = new ArrayList();//正式版节目id集合
                List list1 = new ArrayList();//测试和草稿的节目id集合
                if(null != platformPrograms && platformPrograms.size() > 0){
                    for (PlatformProgram pp:platformPrograms) {
                        if(null != pp){
                            String id = pp.getId();
                            String programVersionNum = pp.getProgramVersionNum();
                            if(StringUtils.isNotBlank(programVersionNum)){
                                list.add(id);
                            }else{
                                list1.add(id);
                            }
                        }
                    }
                    if(null != list && list.size() > 0) {
                        Map map = new HashMap();
                        map.put("platformProgramIds", list);
                        int i = platformProgramMapper.delPlatformPrograms(map);
                        if(i<0){
                            return false;
                        }
                    }
                    if(null != list1 && list1.size() > 0){
                        Example example1 = new Example(PlatformProgram.class);
                        example1.createCriteria().andIn("id",list1);
                        int i = platformProgramMapper.deleteByExample(example1);
                        if(i<0){
                            return false;
                        }
                        Example example3 = new Example(BizImageRecorder.class);
                        example3.createCriteria().andIn("bizId",list1);
                        int i1 = bizImageRecorderMapper.deleteByExample(example3);
                        if(i1 < 0){
                            return false;
                        }
                    }
                    Example example2 = new Example(PlatformProgramComponentsMiddle.class);
                    example2.createCriteria().andEqualTo("componentsId",tenantId).andIn("programId",strings);
                    int i = platformProgramComponentsMiddleMapper.deleteByExample(example2);
                    if(i>=0){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
