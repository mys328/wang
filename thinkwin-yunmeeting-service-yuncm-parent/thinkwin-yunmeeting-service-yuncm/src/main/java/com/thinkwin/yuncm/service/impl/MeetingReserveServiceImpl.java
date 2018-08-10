package com.thinkwin.yuncm.service.impl;

import com.github.pagehelper.PageHelper;
import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.OrganizationService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.PingYinUtil;
import com.thinkwin.common.utils.TimeUtil;
import com.thinkwin.common.vo.meetingVo.*;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.localservice.LocalMeetingReserveService;
import com.thinkwin.yuncm.mapper.*;
import com.thinkwin.yuncm.service.MeetingReserveService;
import com.thinkwin.yuncm.service.SysSetingService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 类名: MeetingReserveServiceImpl </br>
 * 描述: 会议预定接口实现</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/7/31 </br>
 */
@Service("meetingReserveService")
public class MeetingReserveServiceImpl implements MeetingReserveService {
    @Resource
    YunmeetingConferenceMapper yunmeetingConferenceMapper;
    @Resource
    YunmeetingParticipantsInfoMapper yunmeetingParticipantsInfoMapper;
    @Resource
    YunmeetingConferenceUserInfoMapper yunmeetingConferenceUserInfoMapper;
    @Resource
    YunmeetingMessageInformMapper yunmeetingMessageInformMapper;
    @Resource
    YummeetingConferenceRoomMiddleMapper yummeetingConferenceRoomMiddleMapper;
    @Resource
    YuncmMeetingRoomMapper yuncmMeetingRoomMapper;
    @Resource
    YunmeetingParticipantsReplyMapper yunmeetingParticipantsReplyMapper;
    @Resource
    YunmeetingConferenceAuditMapper yunmeetingConferenceAuditMapper;
    @Resource
    YunmeetingMeetingSignMapper yunmeetingMeetingSignMapper;
    @Resource
    YunmeetingDynamicsClickRecordMapper yunmeetingDynamicsClickRecordMapper;

    @Resource
    UserService userService;
    @Resource
    OrganizationService organizationService;
    @Resource
    FileUploadService fileUploadService;
    @Resource
    LocalMeetingReserveService localMeetingReserveService;
    @Resource
    MeetingReserveService meetingReserveService;
    @Resource
    SysSetingService sysSetingService;


    @Override
    public YunmeetingConference selectMeetingByMeetingId(String meetingId) {
        YunmeetingConference yunmeetingConference = new YunmeetingConference();
        yunmeetingConference.setId(meetingId);
        yunmeetingConference.setDeleteState("0");
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
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("time", time);
        //查询当月所有的会议
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
    public List<YunmeetingParticipantsInfo> selectParticipateMeetingByMeetingId(Map<String, Object> map) {
        List<YunmeetingParticipantsInfo> yunmeetingParticipantsInfos = yunmeetingParticipantsInfoMapper.selectMeetingReplay(map);
        if (null != yunmeetingParticipantsInfos && yunmeetingParticipantsInfos.size() > 0) {
            return yunmeetingParticipantsInfos;
        }
        return null;
    }

    @Override
    public boolean insertMeeting(YunmeetingConference yunmeetingConference) {
        int i = yunmeetingConferenceMapper.insertSelective(yunmeetingConference);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean insertMeetingMessageInform(YunmeetingMessageInform yunmeetingMessageInform) {
        int i = yunmeetingMessageInformMapper.insertSelective(yunmeetingMessageInform);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean insertYummeetingConferenceRoomMiddle(YummeetingConferenceRoomMiddle yummeetingConferenceRoomMiddle) {
        int i = yummeetingConferenceRoomMiddleMapper.insertSelective(yummeetingConferenceRoomMiddle);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateMeeting(YunmeetingConference yunmeetingConference) {
        int i = yunmeetingConferenceMapper.updateByPrimaryKeySelective(yunmeetingConference);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateMeetingMessageInform(YunmeetingMessageInform yunmeetingMessageInform) {
        int i = yunmeetingMessageInformMapper.updateByPrimaryKeySelective(yunmeetingMessageInform);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteMeetingMessageInform(String meetingId) {
        Example example = new Example(YunmeetingMessageInform.class,true,true);
        example.createCriteria().andEqualTo("conferenceId",meetingId);
        int i = yunmeetingMessageInformMapper.deleteByExample(example);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean insertYunmeetingParticipantsInfo(YunmeetingParticipantsInfo yunmeetingParticipantsInfo) {
        int i = yunmeetingParticipantsInfoMapper.insertSelective(yunmeetingParticipantsInfo);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean insertYunmeetingConferenceUserInfo(YunmeetingConferenceUserInfo yunmeetingConferenceUserInfo) {
        int i = yunmeetingConferenceUserInfoMapper.insertSelective(yunmeetingConferenceUserInfo);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean insertMeetingPeople(List<MeetingParticipantsVo> peopleIds, String meetingId, String userId) {
        YunmeetingParticipantsInfo yunmeetingParticipantsInfo = new YunmeetingParticipantsInfo();
        YunmeetingConferenceUserInfo yunmeetingConferenceUserInfo = new YunmeetingConferenceUserInfo();
        boolean b = false;
        if (null != peopleIds && peopleIds.size() > 0) {
            for (MeetingParticipantsVo meetingParticipantsVo : peopleIds) {
                String peopleId = meetingParticipantsVo.getId();
                String isOrg = meetingParticipantsVo.getDep();
                String participantsId = CreateUUIdUtil.Uuid();
                if (StringUtils.isNotBlank(isOrg) && StringUtils.isNotBlank(peopleId)) {
                    //判断是组织还是人员Id 0是人员 1是组织机构
                    if (isOrg.equals("0")) {
                        //根据用户Id查询用户信息
                        SysUser sysUser = userService.selectUserByUserId(peopleId);
                        yunmeetingParticipantsInfo.setOrgId(sysUser.getOrgId());
                        yunmeetingParticipantsInfo.setParticipantsName(sysUser.getUserName());
                        yunmeetingParticipantsInfo.setParticipantsNamePinyin(PingYinUtil.getPingYin(sysUser.getUserName()));
                    } else {
                        //根据组织Id查询组织信息
                        SysOrganization organization = organizationService.selectOrganiztionById(peopleId);
                        if (null != organization) {
                            yunmeetingParticipantsInfo.setOrgId(peopleId);
                            yunmeetingParticipantsInfo.setParticipantsName(organization.getOrgName());
                            yunmeetingParticipantsInfo.setParticipantsNamePinyin(PingYinUtil.getPingYin(organization.getOrgName()));
                        }
                        //根据组织Id查询用户信息
                        List<SysUser> sysUsers = userService.selectAddressListStructure(peopleId);
                        if (null != sysUsers && sysUsers.size() > 0) {
                            for(SysUser sysUser : sysUsers) {
                                String userIds = sysUser.getId();
                                boolean flag = true;
                                //增加人员去重
                                for(MeetingParticipantsVo meetingParticipantsVos : peopleIds){
                                    if(meetingParticipantsVos.getDep().equals("0")){
                                        if(userIds.equals(meetingParticipantsVos.getId())){
                                            flag = false;
                                            break;
                                        }
                                    }
                                }
                                //增加组织用户信息
                                if(flag) {
                                    yunmeetingConferenceUserInfo.setCreaterId(userId);
                                    yunmeetingConferenceUserInfo.setCreateTime(new Date());
                                    yunmeetingConferenceUserInfo.setId(CreateUUIdUtil.Uuid());
                                    yunmeetingConferenceUserInfo.setParticipantsId(sysUser.getId());
                                    yunmeetingConferenceUserInfo.setParticipantsInfoId(participantsId);
                                    yunmeetingConferenceUserInfo.setParticipantsName(sysUser.getUserName());
                                    yunmeetingConferenceUserInfo.setParticipantsNamePinyin(sysUser.getUserNamePinyin());
                                    insertYunmeetingConferenceUserInfo(yunmeetingConferenceUserInfo);
                                }
                            }
                        }
                    }
                    yunmeetingParticipantsInfo.setConferenceId(meetingId);
                    yunmeetingParticipantsInfo.setCreaterId(userId);
                    yunmeetingParticipantsInfo.setId(participantsId);
                    yunmeetingParticipantsInfo.setCreateTime(new Date());
                    yunmeetingParticipantsInfo.setIsInner("1");
                    yunmeetingParticipantsInfo.setParticipantsId(peopleId);
                    yunmeetingParticipantsInfo.setType(isOrg);
                    b = insertYunmeetingParticipantsInfo(yunmeetingParticipantsInfo);
                }
            }
        }
        return b;
    }

    @Override
    public boolean deleteMeetingPeople(String meetingId) {
        boolean b = false;
        if (StringUtils.isNotBlank(meetingId)) {
            //根据会议Id查询会议信息
            Map<String, Object> map = new HashMap<>();
            map.put("meetingId", meetingId);
            List<YunmeetingParticipantsInfo> yunmeetingParticipantsInfos = selectParticipateMeetingByMeetingId(map);
            for (YunmeetingParticipantsInfo yunmeetingParticipantsInfo : yunmeetingParticipantsInfos) {
                String ParticipantsInfo = yunmeetingParticipantsInfo.getId();
                Example example = new Example(YunmeetingConferenceUserInfo.class, true, true);
                example.or().andEqualTo("participantsInfoId", ParticipantsInfo);
                int i = yunmeetingConferenceUserInfoMapper.deleteByExample(example);
                if (i > 0) {
                    b = true;
                }
            }
            Example example = new Example(YunmeetingParticipantsInfo.class, true, true);
            example.or().andEqualTo("conferenceId", meetingId);
            int i = yunmeetingParticipantsInfoMapper.deleteByExample(example);
            if (i > 0) {
                b = true;
            }
        }
        return b;
    }

    @Override
    public List<YunmeetingConference> selectMeetingDayViewByTime(Map<String, Object> map) {
        List<YunmeetingConference> yunmeetingConferences = yunmeetingConferenceMapper.selectFutureYunmeetingConference(map);
        if (yunmeetingConferences.size() > 0) {
            return yunmeetingConferences;
        }
        return null;
    }


    @Override
    public List<YunmeetingConference> selectSetTimeMeeting(Map<String, Object> map) {
        List<YunmeetingConference> yunmeetingConferences = yunmeetingConferenceMapper.selectFutureYunmeetingConference(map);
        if (yunmeetingConferences.size() > 0) {
            return yunmeetingConferences;
        }
        return null;
    }

    @Override
    public List<YuncmMeetingRoom> selectMeetingRoomByMeetingId(String meetingId) {
        List<YuncmMeetingRoom> list = new ArrayList<>();
        //根据会议Id查询会议和会议室中间表
        Example example = new Example(YummeetingConferenceRoomMiddle.class, true, true);
        example.or().andEqualTo("confrerenId", meetingId);
        List<YummeetingConferenceRoomMiddle> yummeetingConferenceRoomMiddles = yummeetingConferenceRoomMiddleMapper.selectByExample(example);
        if (null != yummeetingConferenceRoomMiddles && yummeetingConferenceRoomMiddles.size() > 0) {
            for (YummeetingConferenceRoomMiddle yummeetingConferenceRoomMiddle : yummeetingConferenceRoomMiddles) {
                String roomId = yummeetingConferenceRoomMiddle.getRoomId();
                YuncmMeetingRoom yuncmMeetingRoom = yuncmMeetingRoomMapper.selectByPrimaryKey(roomId);
                if (null != yuncmMeetingRoom) {
                    list.add(yuncmMeetingRoom);
                }
            }
        }
        if (list.size() > 0) {
            return list;
        }
        return null;
    }

    @Override
    public List<YunmeetingMessageInform> selectMeetingMessageeInform(String meetingId) {
        Example example = new Example(YunmeetingMessageInform.class, true, true);
        example.createCriteria().andEqualTo("conferenceId", meetingId);
        List<YunmeetingMessageInform> yunmeetingMessageInforms = yunmeetingMessageInformMapper.selectByExample(example);
        if (yunmeetingMessageInforms.size() > 0) {
            return yunmeetingMessageInforms;
        }
        return null;
    }

    @Override
    public MeetingDetailsVo selectMeetingDetails(String meetingId) {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String tenantId = userInfo.getTenantId();
        String userId1 = userInfo.getUserId();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("meetingId",meetingId);
        //查询当前用户权限
        List<SysUserRole> userRoleByUserIdAndRoleId = userService.getUserRoleByUserIdAndRoleId(userId1, "3");
        if(null!=userRoleByUserIdAndRoleId&&userRoleByUserIdAndRoleId.size()>0){
            map1.put("admin",userId1);
        }else{
            map1.put("userId",userId1);
        }
        MeetingDetailsVo meetingDetailsVo = new MeetingDetailsVo();
        Map<String, Object> map = new HashMap<>();
        //查询会议信息
        YunmeetingConference yunmeetingConference = selectMeetingByUserId(map1);
        if(null!=yunmeetingConference) {
            meetingDetailsVo.setContents(yunmeetingConference.getConterenceContent());
            meetingDetailsVo.setTitle(yunmeetingConference.getConferenceName());
            String state = yunmeetingConference.getState();
            //获取会议开始时间和结束时间
            long takeStartDate = yunmeetingConference.getTakeStartDate().getTime();
            long takeEndDate = yunmeetingConference.getTakeEndDate().getTime();
            meetingDetailsVo.setStart(takeStartDate);
            meetingDetailsVo.setEnd(takeEndDate);
            meetingDetailsVo.setStatus(state);
            meetingDetailsVo.setIsPublic(yunmeetingConference.getIsPublic());
            meetingDetailsVo.setConferenceId(yunmeetingConference.getId());
            meetingDetailsVo.setCreateTime(yunmeetingConference.getCreateTime().getTime());
            //查询通知时间
            meetingDetailsVo = formatMessageInforms(meetingDetailsVo, meetingId, yunmeetingConference.getTakeStartDate());
            //查询会议是地点
            List<YuncmMeetingRoom> yuncmMeetingRooms = selectMeetingRoomByMeetingId(meetingId);
            if (null != yuncmMeetingRooms) {
                YuncmMeetingRoom yuncmMeetingRoom = yuncmMeetingRooms.get(0);
                map = formatMapParam(yuncmMeetingRoom.getId(), yuncmMeetingRoom.getName());
                meetingDetailsVo.setLocation(map);
            }
            //查询会议组织者信息
            String organizerId = yunmeetingConference.getOrganizerId();
            if (StringUtils.isNotBlank(organizerId)) {
                SysUser sysUser = userService.selectUserByUserId(organizerId);
                if (null != sysUser) {
                    map = formatMapParam(sysUser.getId(), sysUser.getUserName());
                    meetingDetailsVo.setOrganizer(map);
                }
            }
            //查询会议预定人信息
            String reservationPersonId = yunmeetingConference.getReservationPersonId();
            TenantContext.setTenantId(tenantId);
            if (StringUtils.isNotBlank(reservationPersonId)) {
                SysUser sysUser = userService.selectUserByUserId(reservationPersonId);
                if (null != sysUser) {
                    map = formatMapParam(sysUser.getId(), sysUser.getUserName());
                    meetingDetailsVo.setBookeder(map);
                }
            }
            //根据主办方Id查询主办方信息
            String hostUnit = yunmeetingConference.getHostUnit();
            if (StringUtils.isNotBlank(hostUnit)) {
                SysOrganization sysOrganization = organizationService.selectOrganiztionById(hostUnit);
                if (null != sysOrganization) {
                    map = formatMapParam(sysOrganization.getId(), sysOrganization.getOrgName());
                    meetingDetailsVo.setDepartment(map);
                }
            }
            //根据会议Id查询参会人信息表(机构组织和人员混合)
            meetingDetailsVo.setAttendees(meetingParticipantsUtil(meetingId, "0", reservationPersonId, null));
            //根据会议Id查询所有参会人信息表（只是人员）
            meetingDetailsVo.setAllAttendees(meetingParticipantsUtil(meetingId, "1", reservationPersonId, null));
            //根据会议查询会议动态
            Map<String,Object> dynamicMap = new HashMap<>();
            dynamicMap.put("meetingId",meetingId);
            dynamicMap.put("userId",userId1);
            /*//根据会议Id查询会议预订人  修改预订人不参会看不到预定和修改的会议动态问题
            YunmeetingConference yunmeetingConference1 = meetingReserveService.findUserParticipantsStatus(map);
            if(null==yunmeetingConference1){
                map.put("reservationPersonId",yunmeetingConference1.getReservationPersonId());
            }*/
            List<YunmeetingDynamics> yunmeetingDynamics = localMeetingReserveService.selectMeetingDynamic(dynamicMap);
            List<DynamicVo> dynamicVos = new ArrayList<>();
            if((!state.equals("0")&&!state.equals("1"))||userId1.equals(reservationPersonId)) {
                if (null != yunmeetingDynamics) {
                    for (YunmeetingDynamics yunmeetingDynamic : yunmeetingDynamics) {
                        DynamicVo dynamicVo = new DynamicVo();
                        String userId = yunmeetingDynamic.getParticipantsId();
                        dynamicVo.setUserId(userId);
                        dynamicVo.setTimeago(yunmeetingDynamic.getCreateTime().getTime());
                        dynamicVo.setMessage(yunmeetingDynamic.getContent());
                        dynamicVo.setSys(yunmeetingDynamic.getDynamicsType());
                        //根据用户Id查询用户信息
                        SysUser sysUser = userService.selectUserByUserId(userId);
                        if (null != sysUser) {
                            String photo = sysUser.getPhoto();
                 /*           if (StringUtils.isNotBlank(photo)) {
                            //    photo = fileUploadService.selectTenementByFile(photo);
                                Map<String, String> strMap = userService.getUploadInfo(photo);
                                if(null != strMap){
                                    dynamicVo.setPhoto(strMap.get("big"));
                                }
                            }*/
                            if(org.apache.commons.lang.StringUtils.isNotBlank(photo)) {
                                Map<String, String> picMap = userService.getUploadInfo(photo);
                                if(null != picMap) {
                                    dynamicVo.setPhoto(picMap.get("primary"));
                                    dynamicVo.setBigPicture(picMap.get("big"));
                                    dynamicVo.setInPicture(picMap.get("in"));
                                    dynamicVo.setSmallPicture(picMap.get("small"));
                                }
                            }

                            dynamicVo.setName(sysUser.getUserName());
                            dynamicVo.setId(yunmeetingDynamic.getId());
                            dynamicVos.add(dynamicVo);
                        }
                    }
                }
            }
            meetingDetailsVo.setDynamics(dynamicVos);
            return meetingDetailsVo;
        }
        return null;
    }

    @Override
    public YunmeetingParticipantsReply selectParticipantsReply(String meetingId, String userId) {
        YunmeetingParticipantsReply yunmeetingParticipantsReply = new YunmeetingParticipantsReply();
        yunmeetingParticipantsReply.setConferenceId(meetingId);
        yunmeetingParticipantsReply.setParticipantsId(userId);
        List<YunmeetingParticipantsReply> select = yunmeetingParticipantsReplyMapper.select(yunmeetingParticipantsReply);
        if (select.size() > 0) {
            return select.get(0);
        }
        return null;
    }

    /**
     * 方法名：selectParticipantsReply</br>
     * 描述：查询会议回复表根据会议Id和用户Id</br>
     * 参数：meetingId 会议Id</br>
     * 参数：userId 用户Id
     * 返回值：</br>
     */
    public List<YunmeetingParticipantsReply> selectParticipantsReply(String meetingId){
        if(StringUtils.isNotBlank(meetingId)){
        YunmeetingParticipantsReply yunmeetingParticipantsReply = new YunmeetingParticipantsReply();
        yunmeetingParticipantsReply.setConferenceId(meetingId);
        return yunmeetingParticipantsReplyMapper.select(yunmeetingParticipantsReply);
        }
        return null;
    }

    @Override
    public List<PersonsVo> selectAllParticipantByMeetingId(String meetingId) {
        List<PersonsVo> list = new ArrayList<>();
        //根据会议Id查询参会人信息
        Map<String, Object> map = new HashMap<>();
        map.put("meetingId", meetingId);
        List<YunmeetingParticipantsInfo> yunmeetingParticipantsInfos = selectParticipateMeetingByMeetingId(map);
        if(null != yunmeetingParticipantsInfos && yunmeetingParticipantsInfos.size() > 0) {
            for (YunmeetingParticipantsInfo yunmeetingParticipantsInfo : yunmeetingParticipantsInfos) {
                String participantsId = yunmeetingParticipantsInfo.getId();
                String type = yunmeetingParticipantsInfo.getType();
                //判断参会人员信息为组织机构的时候
                if (StringUtils.isNotBlank(type) && type.equals("1")) {
                    //根据参会Id查询参会机构人员表
                    List<YunmeetingConferenceUserInfo> yunmeetingConferenceUserInfos = selectPersonParticipateMeetingByOrgNum(participantsId, null);
                    if(null!=yunmeetingConferenceUserInfos) {
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
    public boolean insertMeetingAuditInfo(YunmeetingConferenceAudit yunmeetingConferenceAudit) {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        yunmeetingConferenceAudit.setId(CreateUUIdUtil.Uuid());
        yunmeetingConferenceAudit.setActAuditor(userId);
        yunmeetingConferenceAudit.setActAuditTime(new Date());
        yunmeetingConferenceAudit.setCreateId(userId);
        yunmeetingConferenceAudit.setCreateTime(new Date());
        yunmeetingConferenceAudit.setDeleteState("0");
        int i = yunmeetingConferenceAuditMapper.insertSelective(yunmeetingConferenceAudit);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean insertH5MeetingAuditInfo(YunmeetingConferenceAudit yunmeetingConferenceAudit,String userId) {
        yunmeetingConferenceAudit.setId(CreateUUIdUtil.Uuid());
        yunmeetingConferenceAudit.setActAuditor(userId);
        yunmeetingConferenceAudit.setActAuditTime(new Date());
        yunmeetingConferenceAudit.setCreateId(userId);
        yunmeetingConferenceAudit.setCreateTime(new Date());
        yunmeetingConferenceAudit.setDeleteState("0");
        int i = yunmeetingConferenceAuditMapper.insertSelective(yunmeetingConferenceAudit);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public YunmeetingConference reserveMeeting(YummeetingConferenceRoomMiddle yummeetingConferenceRoomMiddle, YunmeetingConference yunmeetingConference) {
        //获取会议室Id
        String roomIds = yummeetingConferenceRoomMiddle.getRoomId();
        //查询会议室是否需要审核
        YuncmMeetingRoom yuncmMeetingRoom = localMeetingReserveService.selectByidYuncmMeetingRoom(roomIds.split(",")[0]);
        if (null != yuncmMeetingRoom) {
            String isAudit = yuncmMeetingRoom.getIsAudit();
            if (isAudit.equals("1")) {
                yunmeetingConference.setIsAudit("1");
                yunmeetingConference.setState("1");
            } else {
                yunmeetingConference.setIsAudit("0");
                yunmeetingConference.setState("2");
            }
        }
        String conferenceId = yunmeetingConference.getId();
        //保存会议和会议室中间表
        if (StringUtils.isNotBlank(roomIds)) {
            YummeetingConferenceRoomMiddle conferenceRoomMiddle = localMeetingReserveService.selectMCRoomMiddleByMId(conferenceId);
            if (null != conferenceRoomMiddle) {
                //根据会议Id删除关联表
                localMeetingReserveService.deleteMCRoomMiddleByMId(conferenceId);
            }
            boolean b = false;
            //处理一个会议多个会议室的情况
            for (String roomId : roomIds.split(",")) {
                if (StringUtils.isNotBlank(roomId)) {
                    yummeetingConferenceRoomMiddle.setRoomId(roomId);
                    b = insertYummeetingConferenceRoomMiddle(yummeetingConferenceRoomMiddle);
                }
            }
        }
        return yunmeetingConference;
    }

    @Override
    public boolean insertMeetingReply(YunmeetingParticipantsReply yunmeetingParticipantsReply) {
        int i = yunmeetingParticipantsReplyMapper.insertSelective(yunmeetingParticipantsReply);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateMeetingReply(YunmeetingParticipantsReply yunmeetingParticipantsReply) {
        int i = yunmeetingParticipantsReplyMapper.updateByPrimaryKeySelective(yunmeetingParticipantsReply);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean insertMeetingSign(YunmeetingMeetingSign yunmeetingMeetingSign) {
        int i = yunmeetingMeetingSignMapper.insertSelective(yunmeetingMeetingSign);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public YunmeetingMeetingSign selectSignInfo(String meetingId, String userId) {
        Example example = new Example(YunmeetingMeetingSign.class, true, true);
        example.createCriteria().andEqualTo("confrerenId", meetingId).andEqualTo("participantsId", userId);
        List<YunmeetingMeetingSign> yunmeetingMeetingSigns = yunmeetingMeetingSignMapper.selectByExample(example);
        if (null != yunmeetingMeetingSigns && yunmeetingMeetingSigns.size() > 0) {
            return yunmeetingMeetingSigns.get(0);
        }
        return null;
    }
    /**
     * 方法名：selectSignInfo</br>
     * 描述：查询签到信息根据会议Id和用户Id</br>
     * 参数：meetingId 会议id</br>
     * 参数：userId  用户Id
     * 返回值：</br>
     */
    public List<YunmeetingMeetingSign> selectSignInfo(String meetingId){
        if(StringUtils.isNotBlank(meetingId)) {
            Example example = new Example(YunmeetingMeetingSign.class, true, true);
            example.createCriteria().andEqualTo("confrerenId", meetingId);
            return yunmeetingMeetingSignMapper.selectByExample(example);
        }
        return null;
    }

    @Override
    public List<RecentMeetingVo> selectRecentMeeting(BasePageEntity basePageEntity) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //获取用户Id
            String userId = TenantContext.getUserInfo().getUserId();
            //获取七天后的时间
            String pastDate = TimeUtil.getPastDate(7);
            Map<String, Object> map = new HashMap<>();
            map.put("recentTimes", format.parse(pastDate));
            map.put("myAll", "myAll");
            map.put("userId", userId);
            map.put("orderBy", "DESC");
            PageHelper.startPage(basePageEntity.getCurrentPage(), basePageEntity.getPageSize());
            List<YunmeetingConference> conferenc = yunmeetingConferenceMapper.selectFutureYunmeetingConference(map);
            List<RecentMeetingVo> list = new ArrayList<>();
            for (YunmeetingConference yunmeetingConference : conferenc) {
                RecentMeetingVo recentMeetingVo = new RecentMeetingVo();
                String state = yunmeetingConference.getState();
                String meetingId = yunmeetingConference.getId();
                //获取会议开始时间和结束时间
                //long currentDate = new Date().getTime();
                long takeStartDate = yunmeetingConference.getTakeStartDate().getTime();
                long takeEndDate = yunmeetingConference.getTakeEndDate().getTime();
                if ((StringUtils.isNotBlank(state))) {
                    /*if(currentDate >= takeStartDate && currentDate <= takeEndDate && state.equals("2")){
                        //修改会议状态为进行中
                        updateMeetingStatus(meetingId,"3");
                        state = "3";
                    }else if(currentDate > takeEndDate && (state.equals("3") || state.equals("2"))){
                        //修改会议状态为已结束
                        updateMeetingStatus(meetingId,"4");
                        continue;
                    }*/
                    if (state.equals("3")) {
                        recentMeetingVo.setState("1");
                    } else if (state.equals("2")) {
                        recentMeetingVo.setState("0");
                    }
                }
                recentMeetingVo.setConferenceId(meetingId);
                recentMeetingVo.setStart(takeStartDate);
                recentMeetingVo.setEnd(takeEndDate);
                //根据会议Id 查询会议室信息
                List<YuncmMeetingRoom> yuncmMeetingRooms = selectMeetingRoomByMeetingId(meetingId);
                if (null != yuncmMeetingRooms) {
                    recentMeetingVo.setLocation(yuncmMeetingRooms.get(0).getName());
                }
                recentMeetingVo.setTitle(yunmeetingConference.getConferenceName());
                if (userId.equals(yunmeetingConference.getOrganizerId())) {
                    recentMeetingVo.setOrganizer("1");
                } else {
                    recentMeetingVo.setOrganizer("0");
                }
                list.add(recentMeetingVo);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer selectAuthstrNum() {
        Map<String, Object> map = new HashMap<>();
        //待审批
        map.put("centre", "centre");
        List<YunmeetingConference> yunmeetingConferences = this.yunmeetingConferenceMapper.selectAuditYunmeetingConference(map);
        if (null != yunmeetingConferences && yunmeetingConferences.size() > 0) {
            return yunmeetingConferences.size();
        }
        return 0;
    }

    /**
     * 查询某时间段某个会议室的所有会议
     *
     * @param meetingRoomId 会议室Id
     * @param meetingBegin  开始时间
     * @param meetingEnd    结束时间
     * @return 会议列表
     */
    public List<YunmeetingConference> findByMeetingRoomIdAndMeetingtakeStartDate(String meetingRoomId, String meetingBegin, String meetingEnd) {
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isNotBlank(meetingRoomId)) {
            map.put("meetingRoomId", meetingRoomId);
        }
        if(StringUtils.isNotBlank(meetingBegin)) {
            map.put("meetingBegin", meetingBegin);
        }
        if(StringUtils.isNotBlank(meetingEnd)) {
            map.put("meetingEnd", meetingEnd);
        }

        return this.yunmeetingConferenceMapper.findByMeetingRoomIdAndMeetingtakeStartDate(map);
    }

    /**
     * 方法名：formatMapParam</br>
     * 描述：转换为Map格式</br>
     */
    private static Map<String, Object> formatMapParam(String id, String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        return map;
    }

    /**
     * 方法名：formatMessageInforms</br>
     * 描述：格式化消息通知信息</br>
     * 参数：</br>
     * 返回值：</br>
     */
    private MeetingDetailsVo formatMessageInforms(MeetingDetailsVo meetingDetailsVo, String meetingId, Date startTime) {
        //根据会议Id查询会议通知表
        List<YunmeetingMessageInform> yunmeetingMessageInforms = selectMeetingMessageeInform(meetingId);
        if (null != yunmeetingMessageInforms) {
            meetingDetailsVo.setNotice(yunmeetingMessageInforms.get(0).getInformType());
            String time = "";
            for (YunmeetingMessageInform yunmeetingMessageInform : yunmeetingMessageInforms) {
                Date informTime = yunmeetingMessageInform.getInformTime();
                if(null!=informTime){
                    time += formatDate(startTime,informTime ) + ",";
                }
            }
            if(!"".equals(time)){
                meetingDetailsVo.setRemind(time.substring(0,time.length()-1));
            }else{
                meetingDetailsVo.setRemind("");
            }

        }else {
            meetingDetailsVo.setRemind("");
        }
        return meetingDetailsVo;
    }

    /**
     * 方法名：formatDate</br>
     * 描述：格式化消息通知时间 </br>
     * 参数：</br>
     * 返回值：</br>
     */
    private static String formatDate(Date start, Date end) {
        long time = start.getTime() - end.getTime();
        long day = time / (24 * 60 * 60 * 1000);
        long hour = (time / (60 * 60 * 1000) - day * 24);
        long min = ((time / (60 * 1000)) - day * 24 * 60 - hour * 60);
        //long s = (time / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (day != 0) {
            return day + "d";
        } else if (hour != 0) {
            return hour + "h";
        } else if (min != 0) {
            return min + "";
        } else {
            return 0 + "";
        }
    }

    /**
     * 查询七天前所有我参与的会议的信息
     * @return
     */
    public Integer selectUnreadMessage(String userId){
        Integer messageNum = 0;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        lastDate.roll(Calendar.DATE, -7);//日期回滚7天
        Date startTime = lastDate.getTime();
        String format = sdf.format(startTime);
        System.out.println("七天前的时间是  ："+format);
        Map map = new HashMap();
        map.put("startTime",startTime);
//        map.put("userId",userId);
        List<YunmeetingConference> yunmeetingConferenceList = yunmeetingConferenceMapper.selectUnreadMessageSevenDaysAgo(map);
        List<String> yunmeetingConferences = new ArrayList();
        if(null != yunmeetingConferenceList && yunmeetingConferenceList.size() > 0){
            for (YunmeetingConference yunmeetingConferencee: yunmeetingConferenceList) {
                if(null != yunmeetingConferencee){
                    String yunmeetingConferenceeId = yunmeetingConferencee.getId();
                    if(StringUtils.isNotBlank(yunmeetingConferenceeId)){
                        //根据会议id获取参会人员列表
                        List<PersonsVo> personsVos = selectAllParticipantByMeetingId(yunmeetingConferenceeId);
                            if(null != personsVos && personsVos.size() > 0){
                                for (PersonsVo personsVo:personsVos) {
                                    if(null != personsVo){
                                        String userId1 = personsVo.getUserId();
                                        if(StringUtils.isNotBlank(userId1) && userId.equals(userId1)){
                                            yunmeetingConferences.add(yunmeetingConferenceeId);
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }
        if(null != yunmeetingConferences && yunmeetingConferences.size() > 0){
            for (String meetingId: yunmeetingConferences) {
                if (StringUtils.isNotBlank(meetingId)) {
                    //根据会议查询会议动态
                    Map<String,Object> dynamicMap = new HashMap<>();
                    dynamicMap.put("meetingId",meetingId);
                    dynamicMap.put("userId",userId);
                    List<YunmeetingDynamics> yunmeetingDynamicss = localMeetingReserveService.selectMeetingDynamic(dynamicMap);
                    if(null != yunmeetingDynamicss && yunmeetingDynamicss.size() > 0){
                        for (YunmeetingDynamics yunmeetingDynamics:yunmeetingDynamicss) {
                            if(null != yunmeetingDynamics){
                                String yunmeetingDynamicsId = yunmeetingDynamics.getId();
                                if(StringUtils.isNotBlank(yunmeetingDynamicsId)){
                                    YunmeetingDynamicsClickRecord yunmeetingDynamicsClickRecord= new YunmeetingDynamicsClickRecord();
                                    yunmeetingDynamicsClickRecord.setDynamicsId(yunmeetingDynamicsId);
                                    yunmeetingDynamicsClickRecord.setParticipantsId(userId);
                                    List<YunmeetingDynamicsClickRecord> select = yunmeetingDynamicsClickRecordMapper.select(yunmeetingDynamicsClickRecord);
                                    if(null == select || select.size() == 0){
                                        messageNum += 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return messageNum;
    }

    @Override
    public void updateMeetingStatus(String meetingId, String status) {
        if (StringUtils.isNotBlank(meetingId) && StringUtils.isNotBlank(status)) {
            YunmeetingConference yunmeetingConference = new YunmeetingConference();
            yunmeetingConference.setId(meetingId);
            yunmeetingConference.setState(status);
            updateMeeting(yunmeetingConference);
        }
    }

    /**
     * 微信端-我组织的会议
     *
     * @param userId    用户id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pageBegin 开始页码
     * @param step      每页数量
     * @return
     */
    @Override
    public List<YunmeetingConference> organizeMeeting(String userId, String startTime, String endTime, String pageBegin, String step) {
        Map<String ,String> map=new HashMap<String,String>();
        map.put("userId",userId);
        map.put("startTime",startTime);
        if(StringUtils.isNotBlank(endTime)){
            map.put("endTime",startTime);
        }
        if(StringUtils.isNotBlank(pageBegin)){
            map.put("pageBegin",pageBegin);
        }
        if(StringUtils.isNotBlank(step)){
            map.put("step",step);
        }
        List<YunmeetingConference> list=this.yunmeetingConferenceMapper.organizeMeeting(map);
        return list;
    }

    /**
     * 微信端-我参与的会议
     *
     * @param userId    用户id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pageBegin 开始页码
     * @param step      每页数量
     * @return
     */
    @Override
    public List<YunmeetingConference> aboutMeeting(String userId, String startTime, String endTime, String pageBegin, String step) {
        Map<String ,String> map=new HashMap<String,String>();
        map.put("userId",userId);
        map.put("startTime",startTime);
        if(StringUtils.isNotBlank(endTime)){
            map.put("endTime",startTime);
        }
        if(StringUtils.isNotBlank(pageBegin)){
            map.put("pageBegin",pageBegin);
        }
        if(StringUtils.isNotBlank(step)){
            map.put("step",step);
        }
        List<YunmeetingConference> list=this.yunmeetingConferenceMapper.aboutMeeting(map);
        return list;
    }

    /**
     * 查询与我相关的会议
     *
     * @param userId
     * @param meetingName 会议名称
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @param status      状态
     * @return
     */
    @Override
    public List<YunmeetingConference> findByMyMeeting(String userId, String meetingName, String startTime, String endTime, String status) {
        Map<String,String> map=new HashMap<>();
        map.put("userId",userId);
        map.put("searchKey",meetingName);
        map.put("startTime",startTime);
        map.put("endTime",endTime);
        map.put("status",status);
        List<YunmeetingConference> list=this.yunmeetingConferenceMapper.findByMyMeeting(map);
        if(null!=list){
            for(YunmeetingConference conference : list){
                conference = obtainYunmeetingConferenceInfo(conference,null);

            }
            return list;
        }
        return null;
    }

    @Override
    public Map<String,Object> findMeetingReplay(String type, String search, String meetingId, String reservationPersonId) {
        List<MeetingParticipantsVo> meetingParticipantsVos = new ArrayList<>();
        if (StringUtils.isNotBlank(meetingId)) {
            if (StringUtils.isNotBlank(type)) {
                //查询全部  包括组织和人员
                if (type.equals("0")) {
                    meetingParticipantsVos = meetingParticipantsUtil(meetingId, "0", reservationPersonId, search);
                } else {
                    //查询所有人员
                    List<PersonsVo> allMeetingPerson = findAllMeetingPerson(meetingId, search);
                    for (PersonsVo personsVo : allMeetingPerson) {
                        String userId = personsVo.getUserId();
                        MeetingParticipantsVo meetingParticipantsVo = new MeetingParticipantsVo();
                        boolean flag = false;
                        //查询参会回复信息
                        String state = selectMeetingReplayByUserId(meetingId, userId, reservationPersonId);
                        if (StringUtils.isNotBlank(state)) {
                            if (state.equals(type) && (type.equals("1") || type.equals("2"))) {
                                meetingParticipantsVo.setStatu(state);
                                flag = true;
                            } else if (type.equals("3") && state.equals("0")) {
                                meetingParticipantsVo.setStatu("0");
                                flag = true;
                            }else if(type.equals("4") && state.equals("3")){
                                meetingParticipantsVo.setStatu("3");
                                flag = true;
                            }
                        } else {
                            if (type.equals("4")) {
                                meetingParticipantsVo.setStatu("3");
                                flag = true;
                            }
                        }
                        if (flag) {
                            //查询用户信息
                            SysUser sysUser = userService.selectUserByUserId(userId);
                            if (null != sysUser) {
                                meetingParticipantsVo.setName(sysUser.getUserName());
                                meetingParticipantsVo.setPhoneNumber(sysUser.getPhoneNumber());
                                String photo = sysUser.getPhoto();
                                if (StringUtils.isNotBlank(photo)) {
                                   // meetingParticipantsVo.setPhoto(fileUploadService.selectTenementByFile(photo));
                                    Map<String, String> photos = userService.getUploadInfo(photo);
                                    if(null != photos) {
                                        meetingParticipantsVo.setPhoto(photos.get("primary"));
                                        meetingParticipantsVo.setBigPicture(photos.get("big"));
                                        meetingParticipantsVo.setInPicture(photos.get("in"));
                                        meetingParticipantsVo.setSmallPicture(photos.get("small"));
                                    }
                                }
                            }
                            meetingParticipantsVo.setDep("0");
                            meetingParticipantsVos.add(meetingParticipantsVo);
                        }
                    }
                }
                Map<String,Object> map = new HashMap<>();
                map.put("meetingParticipantsVos",meetingParticipantsVos);
                return map;
            }
        }
        return null;
    }

    @Override
    public List<Integer> countMeetingReplyNum(String meetingId, String reservationPersonId){
        //all 为全部人员数  accept 接受人数 pause 暂定人数  refuse 谢绝人数 noReply未回复人数
        //类型0全部，1接受，2暂定，3谢绝，4未回复
        int all=0,accept = 0,pause = 0,refuse = 0,noReply = 0;
        List list = new ArrayList();
        //查询所有人员
        List<PersonsVo> allMeetingPerson = findAllMeetingPerson(meetingId,null);
        all = allMeetingPerson.size();
        for (PersonsVo personsVo : allMeetingPerson) {
            String userId = personsVo.getUserId();
            //查询参会回复信息
            String state = selectMeetingReplayByUserId(meetingId, userId, reservationPersonId);
            if (StringUtils.isNotBlank(state)) {
                switch (state){
                    case "0":
                        refuse++;
                        break;
                    case "1":
                        accept++;
                        break;
                    case "2":
                        pause++;
                        break;
                    default:
                        noReply++;
                }
            }
        }
        //把回复人数放进list
        list.add(all);
        list.add(accept);
        list.add(pause);
        list.add(refuse);
        noReply = all - (accept+refuse+pause);
        list.add(noReply);
        return list;
    }

    @Override
    public List<PersonsVo> findAllMeetingPerson(String meetingId, String search) {
        List<PersonsVo> list = new ArrayList<>();
        //先查询不包括组织机构的人员
        Map<String, Object> map = new HashMap<>();
        map.put("meetingId", meetingId);
        if (StringUtils.isNotBlank(search)) {
            map.put("search", "%"+search+"%");
        }
        map.put("type", "0");
        List<YunmeetingParticipantsInfo> yunmeetingParticipantsInfos = yunmeetingParticipantsInfoMapper.selectMeetingReplay(map);
        for (YunmeetingParticipantsInfo yunmeetingParticipantsInfo : yunmeetingParticipantsInfos) {
            PersonsVo personsVo = new PersonsVo();
            personsVo.setUserName(yunmeetingParticipantsInfo.getParticipantsName());
            personsVo.setUserId(yunmeetingParticipantsInfo.getParticipantsId());
            list.add(personsVo);
        }
        //查询为组织机构下面的所有人员
        map.put("type", "1");
        map.remove("search");
        yunmeetingParticipantsInfos = yunmeetingParticipantsInfoMapper.selectMeetingReplay(map);
        if (StringUtils.isNotBlank(search)) {
            map.put("search", "%"+search+"%");
        }
        for (YunmeetingParticipantsInfo yunmeetingParticipantsInfo : yunmeetingParticipantsInfos) {
            String participantsInfoId = yunmeetingParticipantsInfo.getId();
            map.put("participantsInfoId", participantsInfoId);
            List<YunmeetingConferenceUserInfo> yunmeetingConferenceUserInfos = yunmeetingConferenceUserInfoMapper.selectMeetingAllPerson(map);
            for (YunmeetingConferenceUserInfo yunmeetingConferenceUserInfo : yunmeetingConferenceUserInfos) {
                PersonsVo personsVo = new PersonsVo();
                personsVo.setUserName(yunmeetingConferenceUserInfo.getParticipantsName());
                personsVo.setUserId(yunmeetingConferenceUserInfo.getParticipantsId());
                list.add(personsVo);
            }
        }
        return list;
    }

    @Override
    public List<MeetingSignVo> findMeetingSign(String type, String meetingId, String search) {
        List<MeetingSignVo> meetingSignVos = new ArrayList<>();
        if (StringUtils.isNotBlank(meetingId)) {
            List<PersonsVo> allMeetingPerson = findAllMeetingPerson(meetingId, search);
            for (PersonsVo personsVo : allMeetingPerson) {
                boolean flag = false;
                MeetingSignVo meetingSignVo = new MeetingSignVo();
                String userId = personsVo.getUserId();
                //根据用户Id和会议Id查询会议签到表
                YunmeetingMeetingSign yunmeetingMeetingSign = selectSignInfo(meetingId, userId);
                if (type.equals("0")) {
                    if (null != yunmeetingMeetingSign) {
                        meetingSignVo.setSignState("1");
                        meetingSignVo.setSignTime(yunmeetingMeetingSign.getSignTime().getTime());
                    } else {
                        meetingSignVo.setSignState("0");
                    }
                    flag = true;
                } else if (type.equals("1")) {
                    if (null != yunmeetingMeetingSign) {
                        meetingSignVo.setSignState("1");
                        meetingSignVo.setSignTime(yunmeetingMeetingSign.getSignTime().getTime());
                        flag = true;
                    }
                } else if (type.equals("2")) {
                    if (null == yunmeetingMeetingSign) {
                        meetingSignVo.setSignState("0");
                        flag = true;
                    }
                }
                if (flag) {
                    //查询用户信息
                    SysUser sysUser = userService.selectUserByUserId(userId);
                    if (null != sysUser) {
                        meetingSignVo.setId(sysUser.getId());
                        meetingSignVo.setName(sysUser.getUserName());
                        meetingSignVo.setPhoneNumber(sysUser.getPhoneNumber());
                        String photo = sysUser.getPhoto();
            /*            if (StringUtils.isNotBlank(photo)) {
                            //meetingSignVo.setPhoto(fileUploadService.selectTenementByFile(photo));
                            Map<String, String> strMap = userService.getUploadInfo(photo);
                            if(null != strMap){
                                meetingSignVo.setPhoto(strMap.get("big"));
                            }
                        }*/
                        if(StringUtils.isNotBlank(photo)) {
                            Map<String, String> picMap = userService.getUploadInfo(photo);
                            if(null != picMap) {
                                meetingSignVo.setPhoto(picMap.get("primary"));
                                meetingSignVo.setBigPicture(picMap.get("big"));
                                meetingSignVo.setInPicture(picMap.get("in"));
                                meetingSignVo.setSmallPicture(picMap.get("small"));
                            }
                        }
                    }
                    meetingSignVos.add(meetingSignVo);
                }
            }
            return meetingSignVos;
        }
        return null;
    }

    public List<Integer> countMeetingSignNum(String meetingId){
        int all = 0,signNum = 0,notSignNum = 0;
        List<Integer> list = new ArrayList<>();
        List<PersonsVo> allMeetingPerson = findAllMeetingPerson(meetingId, null);
        all = allMeetingPerson.size();
        for (PersonsVo personsVo : allMeetingPerson) {
            String userId = personsVo.getUserId();
            //根据用户Id和会议Id查询会议签到表
            YunmeetingMeetingSign yunmeetingMeetingSign = selectSignInfo(meetingId, userId);
            if(null!=yunmeetingMeetingSign){
                signNum++;
            }else{
                notSignNum++;
            }
        }
        list.add(all);
        list.add(signNum);
        list.add(notSignNum);
        return list;
    }

    @Override
    public YunmeetingConference selectMeetingByUserId(Map<String,Object> map) {
        YunmeetingConference yunmeetingConference = yunmeetingConferenceMapper.selectMeeting(map);
        if (null != yunmeetingConference) {
            return yunmeetingConference;
        }
        return null;
    }

    @Override
    public List<YunmeetingConference> findMeetingTakeInfo(Map<String, Object> map) {
        List<YunmeetingConference> meetingTakeInfo = yunmeetingConferenceMapper.findMeetingTakeInfo(map);
        if (null != meetingTakeInfo && meetingTakeInfo.size() > 0) {
            return meetingTakeInfo;
        }
        return null;
    }

    @Override
    public Map<String, Integer> getCountMeetingSignNum(String meetingId) {
        Map<String, Integer> map = new HashMap<>();
        int all = 0,signNum = 0,notSignNum = 0;
        List<PersonsVo> allMeetingPerson = selectAllParticipantByMeetingId(meetingId);
        all = allMeetingPerson.size();
        for (PersonsVo personsVo : allMeetingPerson) {
            String userId = personsVo.getUserId();
            //根据用户Id和会议Id查询会议签到表
            YunmeetingMeetingSign yunmeetingMeetingSign = selectSignInfo(meetingId, userId);
            if(null != yunmeetingMeetingSign){
                signNum++;
            }else{
                notSignNum++;
            }
        }
        //获取会议响应率
        YunmeetingParticipantsReply yunmeetingParticipantsReply = new YunmeetingParticipantsReply();
        yunmeetingParticipantsReply.setConferenceId(meetingId);
        List<YunmeetingParticipantsReply> select = yunmeetingParticipantsReplyMapper.select(yunmeetingParticipantsReply);
        map.put("reply",select.size());
        map.put("all",all);
        map.put("signNum",signNum);
        map.put("notSignNum",notSignNum);
        return map;
    }

    /**
         * 方法名：meetingParticipantsUtil</br>
         * 描述：查询某个会议参会人员工具类（处理参会人员回复状态  查询所有参会人等功能）</br>
         * 参数：meetingId 会议id </br>
         * 参数：personType 类型  1 查询所有人员  0 查询组织和人员混合<br/>
         * 参数：reservationPersonId 预定人Id<br/>
         * 返回值：</br>
         */
        private List<MeetingParticipantsVo> meetingParticipantsUtil (String meetingId, String personType, String reservationPersonId, String search){
            List<MeetingParticipantsVo> meetingParticipantsVos = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("meetingId", meetingId);
            if (StringUtils.isNotBlank(search)) {
                map.put("search", "%"+search+"%");
            }
            if (personType.equals("0")) {
                List<YunmeetingParticipantsInfo> yunmeetingParticipantsInfos = selectParticipateMeetingByMeetingId(map);
                if (null != yunmeetingParticipantsInfos) {
                    for (YunmeetingParticipantsInfo yunmeetingParticipantsInfo : yunmeetingParticipantsInfos) {
                        MeetingParticipantsVo meetingParticipantsVo = new MeetingParticipantsVo();
                        String type = yunmeetingParticipantsInfo.getType();
                        String userId = yunmeetingParticipantsInfo.getParticipantsId();
                        if (type.equals("0")) {       //普通用户
                            meetingParticipantsVo.setId(userId);
                            //根据用户Id查询用户表
                            SysUser sysUser = userService.selectUserByUserId(userId);
                            if (null != sysUser) {
                                meetingParticipantsVo.setName(sysUser.getUserName());
                                meetingParticipantsVo.setPhoneNumber(sysUser.getPhoneNumber());
                                meetingParticipantsVo.setIsDimisson(89==sysUser.getStatus()?"1":"0");
                                String photo = sysUser.getPhoto();
                                if (StringUtils.isNotBlank(photo)) {
//                                    Map<String, String> photos = fileUploadService.selectFileCommon(photo);
                                    Map<String, String> photos = userService.getUploadInfo(photo);
                                    if(null != photos) {
                                        meetingParticipantsVo.setPhoto(photos.get("primary"));
                                        meetingParticipantsVo.setBigPicture(photos.get("big"));
                                        meetingParticipantsVo.setInPicture(photos.get("in"));
                                        meetingParticipantsVo.setSmallPicture(photos.get("small"));
                                    }
                                }
                                //查询参会回复信息
                                String state = selectMeetingReplayByUserId(meetingId, userId, reservationPersonId);
                                meetingParticipantsVo.setStatu(state);
                            }
                            meetingParticipantsVo.setDep("0");
                        } else {      //组织机构
                            if(StringUtils.isNotBlank(search)){ //为搜索的时候  搜索机构下面的人  不显示机构
                                Map<String,Object> map1 = new HashMap<>();
                                map1.put("participantsInfoId",userId);
                                map1.put("search","%"+search+"%");
                                List<YunmeetingConferenceUserInfo> yunmeetingConferenceUserInfos = yunmeetingConferenceUserInfoMapper.selectMeetingAllPerson(map);
                                for (YunmeetingConferenceUserInfo yunmeetingConferenceUserInfo : yunmeetingConferenceUserInfos) {
                                    meetingParticipantsVo.setId(yunmeetingConferenceUserInfo.getParticipantsId());
                                    meetingParticipantsVo.setName(yunmeetingConferenceUserInfo.getParticipantsName());
                                    meetingParticipantsVo.setDep("0");
                                }
                            }else{ //搜索为空的时候  只显示机构
                                meetingParticipantsVo.setId(userId);
                                meetingParticipantsVo.setName(yunmeetingParticipantsInfo.getParticipantsName());
                                meetingParticipantsVo.setDep("1");
                            }
                        }
                        meetingParticipantsVos.add(meetingParticipantsVo);
                    }
                }
            } else {
                List<PersonsVo> personsVos = selectAllParticipantByMeetingId(meetingId);
                for (PersonsVo personsVo : personsVos) {
                    MeetingParticipantsVo meetingParticipantsVo = new MeetingParticipantsVo();
                    String userId = personsVo.getUserId();
                    meetingParticipantsVo.setId(userId);
                    meetingParticipantsVo.setName(personsVo.getUserName());
                    //查询参会回复信息
                    String state = selectMeetingReplayByUserId(meetingId, userId, reservationPersonId);
                    meetingParticipantsVo.setStatu(state);
                    //查询签到信息
                    YunmeetingMeetingSign yunmeetingMeetingSign = selectSignInfo(meetingId, userId);
                    if (null != yunmeetingMeetingSign) {
                        meetingParticipantsVo.setSignTime(yunmeetingMeetingSign.getSignTime().getTime());
                    }
                    //根据用户Id查询用户表
                    SysUser sysUser = userService.selectUserByUserId(userId);
                    if (null != sysUser) {
                        String photo = sysUser.getPhoto();
                        if (StringUtils.isNotBlank(photo)) {
//                            Map<String, String> photos = fileUploadService.selectFileCommon(photo);
                            Map<String, String> photos = userService.getUploadInfo(photo);
                            if(null != photos) {
                                meetingParticipantsVo.setPhoto(photos.get("primary"));
                                meetingParticipantsVo.setBigPicture(photos.get("big"));
                                meetingParticipantsVo.setInPicture(photos.get("in"));
                                meetingParticipantsVo.setSmallPicture(photos.get("small"));
                            }
                        }
                        meetingParticipantsVo.setIsDimisson(89==sysUser.getStatus()?"1":"0");
                    }
                    meetingParticipantsVos.add(meetingParticipantsVo);
                }
            }
            return meetingParticipantsVos;
        }

        /**
         * 方法名：selectMeetingReplayByUserId</br>
         * 描述：查询会议参会回复信息 根据用户Id和会议Id</br>
         * 参数：meetingId 会议Id</br>
         * 参数：userId 用户Id</br>
         * 参数：reservationPersonId 预定人员Id</br>
         * 返回值：</br>
         */

    private String selectMeetingReplayByUserId(String meetingId, String userId, String reservationPersonId) {
        String state = "";
        //查询参会回复信息
        YunmeetingParticipantsReply yunmeetingParticipantsReply = selectParticipantsReply(meetingId, userId);
        if (null != yunmeetingParticipantsReply) {
            state = yunmeetingParticipantsReply.getReplyState();
        } else {
            if(StringUtils.isNotBlank(reservationPersonId)){
                if (reservationPersonId.equals(userId)) {
                    YunmeetingParticipantsReply yunmeetingParticipantsReply1 = new YunmeetingParticipantsReply();
                    yunmeetingParticipantsReply1.setId(CreateUUIdUtil.Uuid());
                    yunmeetingParticipantsReply1.setReplyState("1");
                    yunmeetingParticipantsReply1.setReplyTime(new Date());
                    yunmeetingParticipantsReply1.setConferenceId(meetingId);
                    yunmeetingParticipantsReply1.setParticipantsId(userId);
                    insertMeetingReply(yunmeetingParticipantsReply1);
                    state = "1";
                } else {
                    state = "3";
                }
            }else {
                state = "3";
            }
        }
        return state;
    }

    //获取会议其他信息
    public YunmeetingConference obtainYunmeetingConferenceInfo(YunmeetingConference conference,String userId){

        //查看会议室所在地
        List<YuncmMeetingRoom> room = this.yuncmMeetingRoomMapper.selectYuncmMeetingRoomYunmeetingConference(conference.getId());
        if (room != null) {
            if (org.apache.commons.lang.StringUtils.isNotBlank(room.get(0).getName())) {
                conference.setAddress(room.get(0).getName());
            }else{
                conference.setAddress("");
            }
        }else{
            conference.setAddress("");
        }

        //获取创建者
        SysUser sysUser = userService.selectUserByUserId(conference.getCreaterId());
        if(sysUser != null){
            conference.setUserName(sysUser.getUserName());
            String photo = sysUser.getPhoto();
     /*       if (StringUtils.isNotBlank(photo)) {
                Map<String, String> picMap = userService.getUploadInfo(photo);
                if(null != picMap) {
                    conference.setUserNameUrl(picMap.get("big"));
                }
            }*/
            //获取创建者头像
            if(StringUtils.isNotBlank(photo)) {
                Map<String, String> picMap = userService.getUploadInfo(photo);
                if(null != picMap) {
                    conference.setUserNameUrl(picMap.get("primary"));
                    conference.setUserNameBigPicture(picMap.get("big"));
                    conference.setUserNameInPicture(picMap.get("in"));
                    conference.setUserNameSmallPicture(picMap.get("small"));
                }
            }
        }
        Example example = new Example(YunmeetingConferenceAudit.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("baseConfrerenId",conference.getId())
                .andEqualTo("deleteState","0");
        List<YunmeetingConferenceAudit> audits = this.yunmeetingConferenceAuditMapper.selectByExample(example);
        if(audits.size() != 0){
            conference.setAuditWhy(audits.get(0).getAuditAnnotations());
        }else {
            conference.setAuditWhy("");
        }
        if(org.apache.commons.lang.StringUtils.isNotBlank(userId)) {
            //查看参会人员
            Map<String, Object> map1 = selectAttendMeetingPerson(conference.getId(), userId);
            List<PersonsVo> personsVos = (List<PersonsVo>) map1.get("person");
            conference.setCount(personsVos.size() + "");
            conference.setPersonsVos(personsVos);
            if (userId.equals(conference.getCreaterId())) {
                conference.setIsOrg("1");
            } else {
                conference.setIsOrg("0");
            }
        }
        return conference;
    }

    public Map<String,Object> selectAttendMeetingPerson(String id,String userId){

        Map<String,Object> map =new HashedMap();
        boolean success = false;
        List<PersonsVo> personsVos = new ArrayList<PersonsVo>();
        Example example = new Example(YunmeetingParticipantsInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("conferenceId",id);
        List<YunmeetingParticipantsInfo> infos = yunmeetingParticipantsInfoMapper.selectByExample(example);
        for(YunmeetingParticipantsInfo info : infos){
            PersonsVo personsVo = new PersonsVo();
            if("0".equals(info.getType())){
                if(userId.equals(info.getParticipantsId())){
                    success = true;
                }
                personsVo.setUserId(info.getParticipantsId());
                personsVo.setUserName(info.getParticipantsName());
                personsVos.add(personsVo);
            }
            if("1".equals(info.getType())){
                Example infoExample = new Example(YunmeetingConferenceUserInfo.class);
                Example.Criteria criteria1 = infoExample.createCriteria();
                criteria1.andEqualTo("participantsInfoId",info.getId());
                List<YunmeetingConferenceUserInfo> userInfos = this.yunmeetingConferenceUserInfoMapper.selectByExample(infoExample);
                for (YunmeetingConferenceUserInfo userInfo : userInfos){
                    if(userId.equals(userInfo.getParticipantsId())){
                        success = true;
                    }
                    PersonsVo persons = new PersonsVo();
                    persons.setUserId(userInfo.getParticipantsId());
                    persons.setUserName(userInfo.getParticipantsName());
                    personsVos.add(persons);
                }

            }
        }
        map.put("success",success);
        map.put("person",personsVos);
        return map;
    }

    /**
     * 根据起始时间获取会议列表
     * @param startTime
     * @param endTime
     * @return
     */
    public List<YunmeetingConference> findMeetingConferenceByTime(String startTime, String endTime){
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        /*** 加一天*/
        if(StringUtils.isNotBlank(endTime)) {
            try {
                Date dd = df.parse(endTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dd);
                calendar.add(Calendar.DAY_OF_MONTH, 2);//加一天
                System.out.println("增加一天之后：" + df.format(calendar.getTime()));
                endTime = df.format(calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
            Map map = new HashMap();
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            return yunmeetingConferenceMapper.findMeetingConferenceByTime(map);
    }
    /**
     * 根据起始时间获取会议列表
     * @param startTime
     * @param endTime
     * @return
     */
    public List<YunmeetingConference> findMeetingConferenceByTimeNew(String startTime, String endTime){
        Map map = new HashMap();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return yunmeetingConferenceMapper.findMeetingConferenceByTimeNew(map);
    }

    /**
     * 查询某人在某个时间段会议占用状态
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param userId    用户Id
     * @return
     */
    @Override
    public List<YunmeetingConference> findByUserByOccupy(String startTime, String endTime, String userId) {
        Map map = new HashMap();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("userId",userId);
        return this.yunmeetingConferenceMapper.findByUserByOccupy(map);
    }

    @Override
    public YunmeetingConference findUserParticipantsStatus(Map<String, Object> map) {
        YunmeetingConference userParticipantsStatus = yunmeetingConferenceMapper.findUserParticipantsStatus(map);
        if(null!=userParticipantsStatus){
            return userParticipantsStatus;
        }
        return null;
    }

    /**
     * 根据起始时间获取会议列表(新)
     * @param startTime
     * @param endTime
     * @return
     */
    public List<YunmeetingConference> findMeetingConferenceByTimeNew(String startTime, String endTime, List<String> ll){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        /*** 加一天*/
        if(StringUtils.isNotBlank(endTime)) {
            try {
                Date dd = df.parse(endTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dd);
                calendar.add(Calendar.DAY_OF_MONTH, 1);//加一天
                System.out.println("增加一天之后：" + df.format(calendar.getTime()));
                endTime = df.format(calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
        Map map = new HashMap();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        if(null != ll && ll.size() > 0) {
            map.put("orgIds", ll);
        }
        return yunmeetingConferenceMapper.findMeetingConferenceByTime(map);
    }

    /**
     * 查询公司的所有已结束的会议列表
     * @return
     */
    public List<YunmeetingConference> selectMeetingConferenceNum(String status){
        Example example = new Example(YunmeetingConference.class);
        example.createCriteria().andEqualTo("state",status).andNotEqualTo("deleteState",1);
        return yunmeetingConferenceMapper.selectByExample(example);
    }
    @Autowired
    YuncmRoomReserveConfMapper yuncmRoomReserveConfMapper;
    //查询会议室可预订时间获取会议室预定时长
    @Override
    public YuncmRoomReserveConf selectYuncmRoomReserveConf() {
        List<YuncmRoomReserveConf> confs = this.yuncmRoomReserveConfMapper.selectAll();
        if (confs.size() != 0) {
            return confs.get(0);
        }
        return null;
    }

    @Override
    public String getMeetingIdAndRoomId(String meetingId) {
        Example example = new Example(YummeetingConferenceRoomMiddle.class);
        example.createCriteria().andEqualTo("confrerenId",meetingId);
        List<YummeetingConferenceRoomMiddle> list =  yummeetingConferenceRoomMiddleMapper.selectByExample(example);
        String roomId = "";
        if(list != null){
            for (YummeetingConferenceRoomMiddle middle : list){
                 roomId = middle.getRoomId();
                 break;
            }
        }
        return roomId;
    }

    @Override
    public MeetingDetailsVo isShowSignButton(MeetingDetailsVo meetingDetailsVo,String userId) {
        //获取当前用户Id
        String role = "0";
        boolean flag =false;
        boolean isAdmin = false;
        List<MeetingParticipantsVo> allAttendees = meetingDetailsVo.getAllAttendees();
        for(MeetingParticipantsVo meetingParticipantsVo:allAttendees){
            String id = meetingParticipantsVo.getId();
            if(id.equals(userId)){
                flag = true;
                break;
            }
        }
        //获取预订人Id
        String reserveId = (String) meetingDetailsVo.getBookeder().get("id");
        //查询当前用户权限
        List<SysUserRole> userRoleByUserIdAndRoleId = userService.getUserRoleByUserIdAndRoleId(userId, "3");
        //增加当前用户角色
        if (null != userRoleByUserIdAndRoleId && userRoleByUserIdAndRoleId.size() > 0) {
            isAdmin = true;
        }
        if(isAdmin){
            role = "1";
        }
        if(flag){
            role = "2";
        }
        if(isAdmin&&flag){
            role = "7";
        }
        if(reserveId.equals(userId)){
            role = "6";
        }
        if(flag&&userId.equals(reserveId)){
            role = "3";
        }
        if(isAdmin&&userId.equals(reserveId)){
            role = "4";
        }
        if(flag&&userId.equals(reserveId)&&isAdmin){
            role = "5";
        }
        meetingDetailsVo.setUserRole(role);
        //查询会议室提前开始设置
        SysSetting byKey = sysSetingService.findByKey("meetingroom.time");
        //获取会议开始结束时间
        Long start = meetingDetailsVo.getStart();
        Long end = meetingDetailsVo.getEnd();
        //把long类型时间戳转成Date时间类型
        Date startTime = new Date(start);
        Date endTime = new Date(end);
        //SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean isShow = false;
        if(null != byKey){
            String content = byKey.getContent();
            int i = Integer.parseInt(content) * 60 * 1000;
            long start1 = start - i;
            Date startTime1 = new Date(start1);
            Date date = new Date();
            if(date.after(startTime1) && date.before(endTime)){
                isShow = true;
            }
        }else{
            Date date = new Date();
            if(date.after(startTime) && date.before(endTime)){
                isShow = true;
            }
        }
        //查询签到信息
        boolean isShow1 = false;
        YunmeetingMeetingSign yunmeetingMeetingSign = meetingReserveService.selectSignInfo(meetingDetailsVo.getConferenceId(), userId);
        if(null == yunmeetingMeetingSign){
            isShow1 = true;
        }else{
            meetingDetailsVo.setSignStatus("1");
        }
        //查询会议室预定设置
        YuncmRoomReserveConf yuncmRoomReserveConf = meetingReserveService.selectYuncmRoomReserveConf();
        if(null != yuncmRoomReserveConf){
            String signSet = yuncmRoomReserveConf.getSignSet();
            if(StringUtils.isBlank(signSet) || signSet.equals("0")){
                isShow = false;
            }
        }
        //会议不为未开始和进行中状态 不显示签到按钮
        String status = meetingDetailsVo.getStatus();
        if(!(status.equals("2") || status.equals("3"))){
            isShow = false;
        }
        //判断是否显示签到按钮
        if(flag && isShow && isShow1){
            meetingDetailsVo.setIsShowSign("1");
        }else{
            meetingDetailsVo.setIsShowSign("0");
        }

        return meetingDetailsVo;
    }

    @Override
    public boolean delectSignInfoByMeetingId(String meetingId) {
        Example example = new Example(YunmeetingMeetingSign.class);
        example.createCriteria().andEqualTo("confrerenId",meetingId);
        int i = yunmeetingMeetingSignMapper.deleteByExample(example);
        if(i>0){
            return true;
        }
        return false;
    }

    @Override
    public YunmeetingConference selectMeetingByMeetingIdAndUserId(Map<String, Object> map) {
        YunmeetingConference yunmeetingConference = yunmeetingConferenceMapper.selectMeetingByMeetingIdAndUserId(map);
        if(null != yunmeetingConference){
            return yunmeetingConference;
        }
        return null;
    }
}
