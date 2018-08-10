package com.thinkwin.yuncm.service.impl;

import com.thinkwin.common.model.db.InfoLabelProgramMiddle;
import com.thinkwin.yuncm.mapper.InfoLabelProgramMiddleMapper;
import com.thinkwin.yuncm.service.InfoLabelProgramMiddleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/*
 * 类说明：
 * @author lining 2018/5/10
 * @version 1.0
 *
 */
@Service("infoLabelProgramMiddleService")
public class InfoLabelProgramMiddleImpl implements InfoLabelProgramMiddleService{

    @Resource
    InfoLabelProgramMiddleMapper infoLabelProgramMiddleMapper;

    @Override
    public List<InfoLabelProgramMiddle> getAll() {
        return this.infoLabelProgramMiddleMapper.selectAll();
    }

    /**
     * 批量添加节目标签关联
     *
     * @param lists
     * @return
     */
    @Override
    public void batchAddLabelProgramMiddle(List<InfoLabelProgramMiddle> lists) {

        this.infoLabelProgramMiddleMapper.batchAddLabelProgramMiddle(lists);
    }

    /**
     * 批量更新节目标签关联
     *
     * @param lists
     * @return
     */
    @Override
    public void batchUpdateLabelProgramMiddle(List<InfoLabelProgramMiddle> lists) {
        //this.infoLabelProgramMiddleMapper.batchUpdateLabelProgramMiddle(lists);
        for(InfoLabelProgramMiddle m:lists){
            this.infoLabelProgramMiddleMapper.updateByPrimaryKey(m);
        }
    }

    /**
     * 批量物理删除节目标签关联
     *
     * @param ids
     * @return
     */
    @Override
    public void batchPhysicalDelLabelProgramMiddle(List<String> ids) {
        this.infoLabelProgramMiddleMapper.batchPhysicalDelLabelProgramMiddle(ids);
    }

    /**
     * 批量物理删除节目标签关联
     *
     * @param programStatus
     * @return
     */
    @Override
    public void batchPhysicalDel(String programStatus) {
        this.batchPhysicalDel(programStatus);
    }
}
