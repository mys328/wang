package com.thinkwin.yuncm.service.impl;

import com.thinkwin.common.dto.publish.TerminalProgramDto;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.InfoProgram;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.mapper.InfoProgramMapper;
import com.thinkwin.yuncm.service.InfoProgramService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 类说明：
 * @author lining 2018/5/10
 * @version 1.0
 *
 */
@Service("infoProgramService")
public class InfoProgramServiceImpl implements InfoProgramService {

    @Resource
    InfoProgramMapper infoProgramMapper;

    @Resource
    SaasTenantService saasTenantCoreService;

    /**
     * 根据id查询租户节目
     *
     * @param id
     * @return
     */
    @Override
    public InfoProgram getId(String id) {
        return infoProgramMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据Id删除
     *
     * @param id
     * @return
     */
    @Override
    public int delId(String id) {
        return this.infoProgramMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据状态返回节目
     * @param programStatus 1发布状态;2内测状态
     * @param recorderStatus 有效:1 ;删除:0
     * @return
     */
    @Override
    public List<InfoProgram> findInfoProgramByProgramStatusAndRecorderStatus(String programStatus,String recorderStatus) {
        Map<String,String> map=new HashMap<>();
        if(StringUtils.isNotBlank(programStatus)){
            map.put("programStatus",programStatus);
        }
        if(StringUtils.isNotBlank(recorderStatus)){
            map.put("recorderStatus",recorderStatus);
        }
        List<InfoProgram> list=this.infoProgramMapper.findInfoProgramByProgramStatusAndRecorderStatus(map);
        return list;
    }

    /**
     * 批量添加节目
     *
     * @param lists
     * @return
     */
    @Override
    public void batchAddProgram(List<InfoProgram> lists) {

        this.infoProgramMapper.batchAddProgram(lists);
    }

    /**
     * 批量更新节目
     *
     * @param lists
     * @return
     */
    @Override
    public void batchUpdateProgram(List<InfoProgram> lists) {

        //this.infoProgramMapper.batchUpdateProgram(lists);
        for(InfoProgram p:lists){
            this.infoProgramMapper.updateByPrimaryKey(p);
        }
    }

    /**
     * 批量逻辑删除节目
     *
     * @param lists
     * @return
     */
    @Override
    public void batchLogicalDelProgram(List<String> lists) {

        this.infoProgramMapper.batchLogicalDelProgram(lists);
    }

    /**
     * 批量物理删除节目
     *
     * @param ids
     * @return
     */
    @Override
    public void batchPhysicalDelProgram(List<String> ids) {

        this.infoProgramMapper.batchPhysicalDelProgram(ids);
    }

    @Override
    public List<TerminalProgramDto> selectProgramByTerminalIds(List<String> terminalIds) {
        if(terminalIds!=null && terminalIds.size()>0){
            SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
            if(null == saasTenant){
                return null;
            }

            String isInnerTest = saasTenant.getIsInnerTest();

            return infoProgramMapper.selectProgramByTerminalIds(terminalIds, isInnerTest);
        }
        return null;
    }

    /**
     * 批量物理删除节目
     *
     * @param programStatus 内测状态2
     * @return
     */
    @Override
    public void batchPhysicalDelProgramByProgramStatus(String programStatus) {

        this.infoProgramMapper.batchPhysicalDelProgramByProgramStatus(programStatus);
    }
}
