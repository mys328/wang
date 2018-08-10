package com.thinkwin.yuncm.service.impl;

import com.thinkwin.common.model.db.BizImageRecorder;
import com.thinkwin.yuncm.mapper.BizImageRecorderMapper;
import com.thinkwin.yuncm.service.BizImageRecorderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 类说明：
 * @author lining 2018/5/15
 * @version 1.0
 *
 */
@Service("bizImageRecorderService")
public class BizImageRecorderServiceImpl implements BizImageRecorderService {

    @Resource
    public BizImageRecorderMapper bizImageRecorderMapper;

    @Override
    public List<BizImageRecorder> findByBizIDType(String bizId, String type){
        Example example = new Example(BizImageRecorder.class);
        example.createCriteria().andEqualTo("bizId", bizId)
                .andEqualTo("type", type);

        return bizImageRecorderMapper.selectByExample(example);
    }

    @Override
    public List<BizImageRecorder> findByType(String type) {
        BizImageRecorder bizImageRecorder=new BizImageRecorder();
        bizImageRecorder.setType(type);
        return bizImageRecorderMapper.select(bizImageRecorder);
    }

    @Override
    public List<BizImageRecorder> findByType(String type1,String type2) {
        Map<String,String> map=new HashMap<>();
        if(StringUtils.isNotBlank(type1) && StringUtils.isNotBlank(type2)){
            map.put("type1",type1);
            map.put("type2",type2);
        }

        return bizImageRecorderMapper.findByType(map);
    }

    /**
     * 批量添加节目背景图片
     *
     * @param lists
     * @return
     */
    @Override
    public void batchAddBizImageRecorder(List<BizImageRecorder> lists) {

        this.bizImageRecorderMapper.batchAddBizImageRecorder(lists);

    }

    /**
     * 批量更新节目背景图片
     *
     * @param lists
     * @return
     */
    @Override
    public void batchUpdateBizImageRecorder(List<BizImageRecorder> lists) {

        //this.bizImageRecorderMapper.batchUpdateBizImageRecorder(lists);
        for(BizImageRecorder i:lists){
            this.bizImageRecorderMapper.updateByPrimaryKey(i);
        }
    }

    /**
     * 批量物理删除节目背景图片
     *
     * @param ids
     * @return
     */
    @Override
    public void batchPhysicalDelBizImageRecorder(List<String> ids) {
        this.bizImageRecorderMapper.batchPhysicalDelBizImageRecorder(ids);
    }
}
