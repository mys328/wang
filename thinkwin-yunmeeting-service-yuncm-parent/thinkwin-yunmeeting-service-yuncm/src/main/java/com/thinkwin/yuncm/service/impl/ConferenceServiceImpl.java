package com.thinkwin.yuncm.service.impl;

import com.thinkwin.common.model.db.InfoProgramTerminalMiddle;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.yuncm.mapper.InfoProgramTerminalMiddleMapper;
import com.thinkwin.yuncm.mapper.YunmeetingConferenceMapper;
import com.thinkwin.yuncm.service.ConferenceService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/18 0018.
 */
@Service("conferenceService")
public class ConferenceServiceImpl implements ConferenceService {

    @Resource
    private YunmeetingConferenceMapper yunmeetingConferenceMapper;
    @Resource
    private InfoProgramTerminalMiddleMapper infoProgramTerminalMiddleMapper;

    @Override
    public List<YunmeetingConference>  getSameDayConference(String roomId, Date date) {
        Map map = new HashMap();
        map.put("roomId",roomId);
        map.put("date",date);
        return this.yunmeetingConferenceMapper.selectCurrentConference(map);
    }


    @Override
    public List<YunmeetingConference> getNextConference(String roomId, Date date) {
        Map map = new HashMap();
        map.put("roomId",roomId);
        map.put("date",date);
        return this.yunmeetingConferenceMapper.selectNextConference(map);
    }

    @Override
    public List<YunmeetingConference> getThisWeekConference(String roomId, Date sta, Date end) {
        Map map = new HashMap();
        map.put("roomId",roomId);
        map.put("sta",sta);
        map.put("end",end);
        return this.yunmeetingConferenceMapper.selectSameDayConference(map);
    }

    @Override
    public String getProgramId(String terminalId) {
        Example example = new Example(InfoProgramTerminalMiddle.class);
        example.createCriteria().andEqualTo("terminalId",terminalId);
        List<InfoProgramTerminalMiddle> middles = infoProgramTerminalMiddleMapper.selectByExample(example);
        for(InfoProgramTerminalMiddle m : middles){
            return m.getProgramId();
        }
        return null;
    }
}
