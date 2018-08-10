package com.thinkwin.publish.service.impl;

import com.thinkwin.common.model.publish.PlatformProgramComponentsMiddle;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.publish.mapper.PlatformProgramComponentsMiddleMapper;
import com.thinkwin.publish.service.PlatformProgramComponentsMiddleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * User: yinchunlei
 * Date: 2018/7/16
 * Company: thinkwin
 */
@Service("platformProgramComponentsMiddleService")
public class PlatformProgramComponentsMiddleServiceImpl implements PlatformProgramComponentsMiddleService {
    @Autowired
    PlatformProgramComponentsMiddleMapper platformProgramComponentsMiddleMapper;

    /**
     * 添加节目与组件关联关系功能接口
     * @param platformProgrameId
     * @param ttt
     * @return
     */
    public boolean addPlatformProgramComponentsMiddle(String platformProgrameId,String ttt){
            if(StringUtils.isNotBlank(platformProgrameId) && StringUtils.isNotBlank(ttt)){
                Example example = new Example(PlatformProgramComponentsMiddle.class);
                example.createCriteria().andEqualTo("programId",platformProgrameId).andEqualTo("componentsId",ttt);
                List<PlatformProgramComponentsMiddle> platformProgramComponentsMiddles = platformProgramComponentsMiddleMapper.selectByExample(example);
                if(null != platformProgramComponentsMiddles && platformProgramComponentsMiddles.size() > 0) {
                    return true;
                }else {
                    PlatformProgramComponentsMiddle ppcm = new PlatformProgramComponentsMiddle();
                    ppcm.setId(CreateUUIdUtil.Uuid());
                    ppcm.setProgramId(platformProgrameId);
                    ppcm.setComponentsId(ttt);
                    int i = platformProgramComponentsMiddleMapper.insertSelective(ppcm);
                    if (i < 0) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        return false;
    }

}
