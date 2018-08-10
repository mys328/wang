package com.thinkwin.yuncm.service;

import com.thinkwin.common.model.db.InfoProgrameLabel;

import java.util.List;

/*
 * 类说明：租户标签Service
 * @author lining 2018/5/10
 * @version 1.0
 *
 */
public interface InfoProgramLabelService {

    /**
     * 查询租户标签信息
     * @param id
     * @return
     */
    InfoProgrameLabel getId(String id);

    /**
     * 删除租户标签信息
     * @param id
     * @return
     */
    int delId(String id);

    public List<InfoProgrameLabel> getAll(String status, String recorderStatus);

    /**
     * 批量添加节目标签
     * @param lists
     * @return
     */
    public void batchAddProgramLabel(List<InfoProgrameLabel> lists);

    /**
     * 批量更新节目标签
     * @param lists
     * @return
     */
    public void batchUpdateProgramLabel(List<InfoProgrameLabel> lists);

    /**
     * 批量逻辑删除节目标签
     * @param ids
     * @return
     */
    public void batchLogicalDelProgramLabel(List<String> ids);

    /**
     * 批量物理删除节目标签
     * @param ids
     * @return
     */
    public void batchPhysicalDelProgramLabel(List<String > ids);

    /**
     * 批量物理删除节目标签
     * @param labelStatus
     * @return
     */
    public boolean batchPhysicalDelLabelByLabelStatus(String labelStatus);

}
