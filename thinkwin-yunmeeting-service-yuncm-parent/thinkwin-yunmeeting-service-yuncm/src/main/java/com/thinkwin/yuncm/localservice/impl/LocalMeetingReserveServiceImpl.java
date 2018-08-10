package com.thinkwin.yuncm.localservice.impl;

import com.thinkwin.common.model.db.*;
import com.thinkwin.common.vo.meetingVo.PersonsVo;
import com.thinkwin.yuncm.localservice.LocalMeetingReserveService;
import com.thinkwin.yuncm.mapper.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 类名: LocalMeetingRoomReserveServiceImpl </br>
 * 描述:</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/7/28 </br>
 */
@Service("localMeetingReserveService")
public class LocalMeetingReserveServiceImpl implements LocalMeetingReserveService {
    @Autowired
    YuncmRoomReserveConfMapper yuncmRoomReserveConfMapper;
    @Autowired
    YunmeetingConferenceMapper yunmeetingConferenceMapper;
    @Autowired
    YunmeetingParticipantsInfoMapper yunmeetingParticipantsInfoMapper;
    @Autowired
    YunmeetingConferenceUserInfoMapper yunmeetingConferenceUserInfoMapper;
    @Autowired
    YuncmMeetingRoomMapper yuncmMeetingRoomMapper;
    @Autowired
    YunmeetingDynamicsMapper yunmeetingDynamicsMapper;
    @Autowired
    YummeetingConferenceRoomMiddleMapper yummeetingConferenceRoomMiddleMapper;

    @Override
    public YuncmRoomReserveConf selectYuncmRoomReserveConf() {
        List<YuncmRoomReserveConf> confs = this.yuncmRoomReserveConfMapper.selectAll();
        if (confs.size() != 0) {
            return confs.get(0);
        }
        return null;
    }

    @Override
    public YunmeetingConference selectMeetingByMeetingId(String meetingId) {
        YunmeetingConference yunmeetingConference = new YunmeetingConference();
        yunmeetingConference.setId(meetingId);
        List<YunmeetingConference> select = yunmeetingConferenceMapper.select(yunmeetingConference);
        if (select.size() > 0) {
            return select.get(0);
        }
        return null;
    }

    @Override
    public List<YunmeetingConference> selectMeeting(String rPersonId, String organizerId) {
        YunmeetingConference yunmeetingConference = new YunmeetingConference();
        yunmeetingConference.setReservationPersonId(rPersonId);
        yunmeetingConference.setOrganizerId(organizerId);
        List<YunmeetingConference> select = yunmeetingConferenceMapper.select(yunmeetingConference);
        if (select.size() > 0) {
            return select;
        }
        return null;
    }

    @Override
    public List<YunmeetingConference> selectMeeting(YunmeetingConference yunmeetingConference) {
        List<YunmeetingConference> select = yunmeetingConferenceMapper.select(yunmeetingConference);
        if (select.size() > 0) {
            return select;
        }
        return null;
    }

    @Override
    public List<YunmeetingConference> selectTenantTotalMeeting(String userId, Date time) {
        //查询当月所有的会议
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("time", time);
        List<YunmeetingConference> yunmeetingConferences = yunmeetingConferenceMapper.selectCurrentMonthAllMeeting(map);
        if (null != yunmeetingConferences && yunmeetingConferences.size() > 0) {
            return yunmeetingConferences;
        }
        return null;
    }

    @Override
    public List<YunmeetingParticipantsInfo> selectPersonParticipateMeetingByUserNum(String conferenceId, String userId, String type) {
        //查询参会信息表
        if (StringUtils.isNotBlank(conferenceId)) {
            Map<String,Object> map = new HashMap<>();
            map.put("conferenceId",conferenceId);
            map.put("userId",userId);
            map.put("type",type);
            List<YunmeetingParticipantsInfo> yunmeetingPInfos = yunmeetingParticipantsInfoMapper.selectParticipateMeetingByUser(map);
            if (null != yunmeetingPInfos && yunmeetingPInfos.size() > 0)
                return yunmeetingPInfos;
        }
        return null;
    }

    @Override
    public List<YunmeetingConferenceUserInfo> selectPersonParticipateMeetingByOrgNum(String participateInfoId, String userId) {
        if (StringUtils.isNotBlank(participateInfoId)) {
            Map<String,Object> map = new HashMap<>();
            map.put("participateInfoId",participateInfoId);
            map.put("userId",userId);
            List<YunmeetingConferenceUserInfo> conferenceUserInfos = yunmeetingConferenceUserInfoMapper.selectParticipateMeetingByOrg(map);
            if (null != conferenceUserInfos && conferenceUserInfos.size() > 0)
                return conferenceUserInfos;
        }
        return null;
    }

    @Override
    public YuncmMeetingRoom selectByidYuncmMeetingRoom(String id) {
        YuncmMeetingRoom yuncmMeetingRoom = this.yuncmMeetingRoomMapper.selectByPrimaryKey(id);
        return yuncmMeetingRoom;
    }

    @Override
    public List<YunmeetingDynamics> selectMeetingDynamic(Map<String,Object> map) {
        List<YunmeetingDynamics> yunmeetingDynamics = yunmeetingDynamicsMapper.selectMeetingDynamicByTime(map);
        if (yunmeetingDynamics.size() > 0) {
            return yunmeetingDynamics;
        }
        return null;
    }

    @Override
    public List<YunmeetingParticipantsInfo> selectParticipateMeetingByMeetingId(Map<String,Object> map) {
        List<YunmeetingParticipantsInfo> yunmeetingParticipantsInfos = yunmeetingParticipantsInfoMapper.selectMeetingReplay(map);
        if (null != yunmeetingParticipantsInfos && yunmeetingParticipantsInfos.size() > 0) {
            return yunmeetingParticipantsInfos;
        }
        return null;
    }

    @Override
    public List<PersonsVo> selectAllParticipantByMeetingId(String meetingId) {
        List<PersonsVo> list = new ArrayList<>();
        //根据会议Id查询参会人信息
        Map<String,Object> map = new HashMap<>();
        map.put("meetingId",meetingId);
        List<YunmeetingParticipantsInfo> yunmeetingParticipantsInfos = selectParticipateMeetingByMeetingId(map);
        if (null != yunmeetingParticipantsInfos && yunmeetingParticipantsInfos.size() > 0) {
            for (YunmeetingParticipantsInfo yunmeetingParticipantsInfo : yunmeetingParticipantsInfos) {
                String participantsId = yunmeetingParticipantsInfo.getId();
                String type = yunmeetingParticipantsInfo.getType();
                //判断参会人员信息为组织机构的时候
                if (StringUtils.isNotBlank(type) && type.equals("1")) {
                    //根据参会Id查询参会机构人员表
                    List<YunmeetingConferenceUserInfo> yunmeetingConferenceUserInfos = selectPersonParticipateMeetingByOrgNum(participantsId, null);
                    if(null!=yunmeetingConferenceUserInfos){
                        for (YunmeetingConferenceUserInfo yunmeetingConferenceUserInfo : yunmeetingConferenceUserInfos) {
                            PersonsVo personsVo = new PersonsVo();
                            personsVo.setUserId(yunmeetingConferenceUserInfo.getParticipantsId());
                            personsVo.setUserName(yunmeetingConferenceUserInfo.getParticipantsName());
                            list.add(personsVo);
                        }
                    }
                } else if (StringUtils.isNotBlank(type) && type.equals("0")) {
                    PersonsVo personsVo = new PersonsVo();
                    personsVo.setUserId(yunmeetingParticipantsInfo.getParticipantsId());
                    personsVo.setUserName(yunmeetingParticipantsInfo.getParticipantsName());
                    list.add(personsVo);
                }
            }
        }
        return list;
    }

    @Override
    public YummeetingConferenceRoomMiddle selectMCRoomMiddleByMId(String meetingId) {
        YummeetingConferenceRoomMiddle yCRoomMiddle = new YummeetingConferenceRoomMiddle();
        yCRoomMiddle.setConfrerenId(meetingId);
        List<YummeetingConferenceRoomMiddle> select = this.yummeetingConferenceRoomMiddleMapper.select(yCRoomMiddle);
        if (null != select && select.size() > 0) {
            return select.get(0);
        }
        return null;
    }

    @Override
    public boolean deleteMCRoomMiddleByMId(String meetingId) {
        Example example = new Example(YummeetingConferenceRoomMiddle.class, true, true);
        example.or().andEqualTo("confrerenId", meetingId);
        int i = yummeetingConferenceRoomMiddleMapper.deleteByExample(example);
        if (i > 0) {
            return true;
        }
        return false;
    }

}
