package com.thinkwin.yuncm.service.impl;

import com.thinkwin.common.model.db.InfoProgrameLabel;
import com.thinkwin.yuncm.mapper.InfoProgrameLabelMapper;
import com.thinkwin.yuncm.service.InfoProgramLabelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/*
 * 类说明：
 * @author lining 2018/5/10
 * @version 1.0
 *
 */
@Service("infoProgramLabelService")
public class InfoProgramLabelServiceImpl implements InfoProgramLabelService {

    @Resource
    InfoProgrameLabelMapper infoProgrameLabelMapper;

    /**
     * 查询租户信息
     *
     * @param id
     * @return
     */
    @Override
    public InfoProgrameLabel getId(String id) {
        return this.infoProgrameLabelMapper.selectByPrimaryKey(id);
    }

    /**
     * 删除租户标签信息
     *
     * @param id
     * @return
     */
    @Override
    public int delId(String id) {
        int f=this.infoProgrameLabelMapper.deleteByPrimaryKey(id);
        return f;
    }

    @Override
    public List<InfoProgrameLabel> getAll(String status,String recorderStatus) {
        InfoProgrameLabel infoProgrameLabel =new InfoProgrameLabel();
        infoProgrameLabel.setLabelStatus(status);
        infoProgrameLabel.setRecorderStatus(recorderStatus);
        List<InfoProgrameLabel> list=this.infoProgrameLabelMapper.select(infoProgrameLabel);

        return list;
    }

    /**
     * 批量添加节目标签
     *
     * @param lists
     * @return
     */
    @Override
    public void batchAddProgramLabel(List<InfoProgrameLabel> lists) {
        this.infoProgrameLabelMapper.batchAddProgramLabel(lists);
    }

    /**
     * 批量更新节目标签
     *
     * @param lists
     * @return
     */
    @Override
    public void batchUpdateProgramLabel(List<InfoProgrameLabel> lists)
    {
        //this.infoProgrameLabelMapper.batchUpdateProgramLabel(lists);
        for(InfoProgrameLabel l:lists){
            this.infoProgrameLabelMapper.updateByPrimaryKey(l);
        }
    }

    /**
     * 批量逻辑删除节目标签
     *
     * @param ids
     * @return
     */
    @Override
    public void batchLogicalDelProgramLabel(List<String> ids) {
        this.infoProgrameLabelMapper.batchLogicalDelProgramLabel(ids);

    }

    /**
     * 批量物理删除节目标签
     *
     * @param ids
     * @return
     */
    @Override
    public void batchPhysicalDelProgramLabel(List<String> ids) {
        this.infoProgrameLabelMapper.batchPhysicalDelProgramLabel(ids);
    }

    /**
     * 批量物理删除节目标签
     *
     * @param labelStatus
     * @return
     */
    @Override
    public boolean batchPhysicalDelLabelByLabelStatus(String labelStatus) {
        return this.infoProgrameLabelMapper.batchPhysicalDelLabelByLabelStatus(labelStatus);
    }
}
