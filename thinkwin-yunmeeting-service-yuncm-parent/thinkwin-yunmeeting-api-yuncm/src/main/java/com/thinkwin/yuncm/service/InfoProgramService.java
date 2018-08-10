package com.thinkwin.yuncm.service;

import com.thinkwin.common.dto.publish.TerminalProgramDto;
import com.thinkwin.common.model.db.InfoProgram;

import java.util.List;

/*
 * 类说明：租户节目Service
 * @author lining 2018/5/10
 * @version 1.0
 *
 */
public interface InfoProgramService {

    /**
     * 根据id查询租户节目
     * @param id
     * @return
     */
    InfoProgram getId(String id);

    /**
     * 根据Id删除
     * @param id
     * @return
     */
    int delId(String id);


    /**
     * 根据状态返回节目
     * @param programStatus 1发布状态;2内测状态
     * @param recorderStatus 有效:1 ;删除:0
     * @return
     */
    public List<InfoProgram> findInfoProgramByProgramStatusAndRecorderStatus(String programStatus,String recorderStatus);


    /**
     * 批量添加节目
     * @param lists
     * @return
     */
    public void batchAddProgram(List<InfoProgram> lists);

    /**
     * 批量更新节目
     * @param lists
     * @return
     */
    public void batchUpdateProgram(List<InfoProgram> lists);

    /**
     * 批量逻辑删除节目
     * @param ids
     * @return
     */
    public void batchLogicalDelProgram(List<String> ids);

    /**
     * 批量物理删除节目
     * @param ids
     * @return
     */
    public void batchPhysicalDelProgram(List<String> ids);

    /**
     * 批量物理删除节目
     * @param programStatus  内测状态2
     * @return
     */
    public void batchPhysicalDelProgramByProgramStatus(String programStatus);

    /**
     * 获取终端节目列表
     * @param terminalIds
     * @return
     */
    List<TerminalProgramDto> selectProgramByTerminalIds(List<String> terminalIds);

}
