package com.thinkwin.yuncm.service;

import com.thinkwin.common.model.db.InfoLabelProgramMiddle;

import java.util.List;

/*
 * 类说明：租户标签节目关联表Service
 * @author lining 2018/5/10
 * @version 1.0
 *
 */
public interface InfoLabelProgramMiddleService {

    public List<InfoLabelProgramMiddle> getAll();

    /**
     * 批量添加节目标签关联
     * @param lists
     * @return
     */
    public void batchAddLabelProgramMiddle(List<InfoLabelProgramMiddle> lists);

    /**
     * 批量更新节目标签关联
     * @param lists
     * @return
     */
    public void batchUpdateLabelProgramMiddle(List<InfoLabelProgramMiddle> lists);

    /**
     * 批量物理删除节目标签关联
     * @param ids
     * @return
     */
    public void batchPhysicalDelLabelProgramMiddle(List<String> ids);

    /**
     * 批量物理删除节目标签关联
     * @param programStatus
     * @return
     */
    public void batchPhysicalDel(String programStatus);

}
