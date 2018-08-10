package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.InfoProgrameLabel;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface InfoProgrameLabelMapper extends Mapper<InfoProgrameLabel> {

    /**
     * 批量添加节目标签
     * @param lists
     * @return
     */
    public boolean batchAddProgramLabel(List<InfoProgrameLabel> lists);

    /**
     * 批量更新节目标签
     * @param lists
     * @return
     */
    public boolean batchUpdateProgramLabel(List<InfoProgrameLabel> lists);

    /**
     * 批量逻辑删除节目标签
     * @param ids
     * @return
     */
    public boolean batchLogicalDelProgramLabel(List<String> ids);

    /**
     * 批量物理删除节目标签
     * @param ids
     * @return
     */
    public boolean batchPhysicalDelProgramLabel(List<String > ids);

    /**
     * 批量物理删除节目标签
     * @param labelStatus
     * @return
     */
    public boolean batchPhysicalDelLabelByLabelStatus(String labelStatus);
}