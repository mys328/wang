package com.thinkwin.publish.service.impl;

import com.thinkwin.common.model.publish.PlatformProgramComponents;
import com.thinkwin.common.model.publish.PlatformProgramComponentsMiddle;
import com.thinkwin.publish.mapper.PlatformProgramComponentsMapper;
import com.thinkwin.publish.mapper.PlatformProgramComponentsMiddleMapper;
import com.thinkwin.publish.service.PlatformProgramComponentsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 节目组件Service实现类
 * User: yinchunlei
 * Date: 2018/7/17
 * Company: thinkwin
 */
@Service("platformProgramComponentsService")
public class PlatformProgramComponentsServiceImpl implements PlatformProgramComponentsService {


    @Autowired
    PlatformProgramComponentsMiddleMapper platformProgramComponentsMiddleMapper;

    @Autowired
    PlatformProgramComponentsMapper platformProgramComponentsMapper;
    /**
     * 根据节目主键集合获取节目组件关联关系数据集
     * @param platformProgramIds
     * @return
     */
    public List<PlatformProgramComponentsMiddle> selectPlatformProgramComponentsMiddleByProgramIds(List<String> platformProgramIds){
        if(null != platformProgramIds && platformProgramIds.size() > 0){
            Example example = new Example(PlatformProgramComponentsMiddle.class);
            example.createCriteria().andIn("programId",platformProgramIds);
            List<PlatformProgramComponentsMiddle> platformProgramComponentsMiddles = platformProgramComponentsMiddleMapper.selectByExample(example);
            return platformProgramComponentsMiddles;
        }
        return null;
    }

    /**
     * 根据节目主键id集合获取组件数据集
     * @param components
     * @return
     */
    public  List<PlatformProgramComponents> selectPlatformProgramComponentsByProgramIds(List<String> components){
        if (null != components && components.size() > 0) {
            Example example = new Example(PlatformProgramComponents.class);
            example.createCriteria().andIn("cCode",components);
            List<PlatformProgramComponents> platformProgramComponents = platformProgramComponentsMapper.selectByExample(example);
            return platformProgramComponents;
        }
        return null;
    }


    /**
     * 根据code获取组件信息
     * @param ttt
     * @return
     */
    public  boolean selectPlatformProgramComponentsByComponentsCode(String ttt){
        if(StringUtils.isNotBlank(ttt)){
            Example example = new Example(PlatformProgramComponents.class);
            example.createCriteria().andEqualTo("cCode",ttt);
            List<PlatformProgramComponents> platformProgramComponents = platformProgramComponentsMapper.selectByExample(example);
            if(null != platformProgramComponents && platformProgramComponents.size() == 1){
                return true;
            }else if(null == platformProgramComponents || platformProgramComponents.size() > 1 || platformProgramComponents.size() <1){
                return false;
            }
        }
        return false;
    }
}
