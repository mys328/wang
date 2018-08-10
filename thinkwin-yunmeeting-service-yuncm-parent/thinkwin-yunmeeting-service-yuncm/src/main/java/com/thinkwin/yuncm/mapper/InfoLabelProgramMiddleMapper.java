package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.InfoLabelProgramMiddle;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface InfoLabelProgramMiddleMapper extends Mapper<InfoLabelProgramMiddle> {

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