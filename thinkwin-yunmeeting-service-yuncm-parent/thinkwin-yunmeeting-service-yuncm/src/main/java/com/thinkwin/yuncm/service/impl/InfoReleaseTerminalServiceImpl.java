package com.thinkwin.yuncm.service.impl;

import com.thinkwin.common.model.db.InfoMessageTerminalMiddle;
import com.thinkwin.common.model.db.InfoProgramTerminalMiddle;
import com.thinkwin.common.model.db.InfoReleaseTerminal;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.yuncm.mapper.InfoMessageTerminalMiddleMapper;
import com.thinkwin.yuncm.mapper.InfoProgramTerminalMiddleMapper;
import com.thinkwin.yuncm.mapper.InfoReleaseTerminalMapper;
import com.thinkwin.yuncm.service.InfoReleaseTerminalService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * 类名: InfoReleaseTerminalServiceImpl </br>
 * 描述: InfoReleaseTerminalService接口实现类</br>
 * 开发人员： weining </br>
 * 创建时间：  2018/4/28 </br>
 */
@Service("infoReleaseTerminalService")
public class InfoReleaseTerminalServiceImpl implements InfoReleaseTerminalService {

    @Autowired
    InfoReleaseTerminalMapper infoReleaseTerminalMapper;

    @Autowired
    InfoProgramTerminalMiddleMapper infoProgramTerminalMiddleMapper;

    @Autowired
    InfoMessageTerminalMiddleMapper infoMessageTerminalMiddleMapper;

    @Override
    public boolean insertInfoReleaseTerminal(InfoReleaseTerminal infoReleaseTerminal) {
        if(null != infoReleaseTerminal){
            String terminalId = infoReleaseTerminal.getId();
            //查询终端和节目中间表是否存在
            Example example = new Example(InfoProgramTerminalMiddle.class);
            example.createCriteria().andEqualTo("terminalId",terminalId);
            List<InfoProgramTerminalMiddle> infoProgramTerminalMiddles = infoProgramTerminalMiddleMapper.selectByExample(example);
            if(null == infoProgramTerminalMiddles || infoProgramTerminalMiddles.size() == 0){
                InfoProgramTerminalMiddle pt = new InfoProgramTerminalMiddle();
                pt.setId(CreateUUIdUtil.Uuid());
                pt.setTerminalId(terminalId);
                infoProgramTerminalMiddleMapper.insertSelective(pt);
            }
            //查询终端消息中间表 是否存在
            example = new Example(InfoMessageTerminalMiddle.class);
            example.createCriteria().andEqualTo("terminalId",terminalId);
            List<InfoMessageTerminalMiddle> infoMessageTerminalMiddles = infoMessageTerminalMiddleMapper.selectByExample(example);
            if(null == infoMessageTerminalMiddles || infoMessageTerminalMiddles.size() == 0){
                InfoMessageTerminalMiddle mt = new InfoMessageTerminalMiddle();
                mt.setId(CreateUUIdUtil.Uuid());
                mt.setTerminalId(infoReleaseTerminal.getId());
                infoMessageTerminalMiddleMapper.insertSelective(mt);
            }
            //增加终端表信息
            infoReleaseTerminal.setCreatTime(new Date());
            int i = infoReleaseTerminalMapper.insertSelective(infoReleaseTerminal);
            if(i>0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateInfoReleaseTerminal(InfoReleaseTerminal infoReleaseTerminal) {
        if(null != infoReleaseTerminal){
            infoReleaseTerminal.setModifyTime(new Date());
            int i = infoReleaseTerminalMapper.updateByPrimaryKeySelective(infoReleaseTerminal);
            if(i>0){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<InfoReleaseTerminal> selectInfoReleaseTerminalByTenantId(String tenantId) {
        Example example = new Example(InfoReleaseTerminal.class);
        example.createCriteria().andEqualTo("tenantId",tenantId);
        List<InfoReleaseTerminal> infoReleaseTerminals = infoReleaseTerminalMapper.selectByExample(example);
        if(null != infoReleaseTerminals && infoReleaseTerminals.size() > 0){
            return infoReleaseTerminals;
        }
        return null;
    }

    @Override
    public InfoReleaseTerminal selectInfoReleaseTerminalById(String terminalId) {
        InfoReleaseTerminal infoReleaseTerminal = infoReleaseTerminalMapper.selectByPrimaryKey(terminalId);
        if(null != infoReleaseTerminal){
            return  infoReleaseTerminal;
        }
        return null;
    }

    @Override
    public InfoReleaseTerminal selectInfoReleaseTerminalByHardwareId(String hardwareId) {
        Example example = new Example(InfoReleaseTerminal.class);
        example.createCriteria().andEqualTo("hardwareId",hardwareId);
        List<InfoReleaseTerminal> infoReleaseTerminals = infoReleaseTerminalMapper.selectByExample(example);
        if(null != infoReleaseTerminals && infoReleaseTerminals.size() > 0){
            return infoReleaseTerminals.get(0);
        }
        return null;
    }

    @Override
    public List<InfoReleaseTerminal> selectInfoReleaseTerminalByMettingRoomId(String meetingRoomId) {
        Example example = new Example(InfoReleaseTerminal.class);
        example.createCriteria().andEqualTo("meetingRoomId",meetingRoomId);
        List<InfoReleaseTerminal> infoReleaseTerminals = infoReleaseTerminalMapper.selectByExample(example);
        if(null != infoReleaseTerminals && infoReleaseTerminals.size()>0){
            return infoReleaseTerminals;
        }
        return null;
    }

    @Override
    public List<InfoReleaseTerminal> selectInfoReleaseTerminals(InfoReleaseTerminal infoReleaseTerminal) {
        List<InfoReleaseTerminal> select = infoReleaseTerminalMapper.select(infoReleaseTerminal);
        if(null != select && select.size() > 0){
            return select;
        }
        return null;
    }

    @Override
    public void updateInfoReleaseTerminalByRoomId(String roomId) {
        Example example = new Example(InfoReleaseTerminal.class);
        example.createCriteria().andEqualTo("meetingRoomId",roomId);
        List<InfoReleaseTerminal> terminals = infoReleaseTerminalMapper.selectByExample(example);
        if(terminals != null){
            for(InfoReleaseTerminal terminal : terminals){
                terminal.setMeetingRoomId(null);
                infoReleaseTerminalMapper.updateByPrimaryKey(terminal);
            }
        }
    }

    @Override
    public boolean delectInfoReleaseTerminal(String terminalId) {
        if(StringUtils.isNotBlank(terminalId)){
            int i = infoReleaseTerminalMapper.deleteByPrimaryKey(terminalId);
            if(i>0){
                return true;
            }
        }
        return false;
    }

}
