package com.thinkwin.mailsender.service.impl;

import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.model.db.YuncmMeetingRoom;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.model.db.YunmeetingParticipantsInfo;
import com.thinkwin.common.vo.meetingVo.MeetingVo;
import com.thinkwin.common.vo.meetingVo.PersonsVo;
import com.thinkwin.mailsender.service.MailTemplateMsgService;
import com.thinkwin.mailsender.service.YunmeetingSendMailService;
import com.thinkwin.mailsender.vo.MailVo;
import com.thinkwin.yuncm.service.MeetingReserveService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名: MailTemplateMsgServiceImpl </br>
 * 描述: 邮箱通知消息模板信息类</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/10/17 </br>
 */
@Service("mailTemplateMsgService")
public class MailTemplateMsgServiceImpl implements MailTemplateMsgService {
    @Resource
    MeetingReserveService meetingReserveService;
    @Resource
    UserService userService;
    @Resource
    YunmeetingSendMailService yunmeetingSendMailService;

    @Override
    public void createMeetingNotice(String meetingId) {
        MailVo mailVo = new MailVo();
        //模板变量Map集合
        Map<String, String> templateParamMaps = new HashMap<>();
        //收件人Map集合
        Map<String, String> recipientsMaps = new HashMap<>();
        if (StringUtils.isNotBlank(meetingId)) {
            YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
            if(null != yunmeetingConference) {
                if (yunmeetingConference.getIsAudit().equals("0")||(!yunmeetingConference.getState().equals("0")&&!yunmeetingConference.getState().equals("1"))) {
                    //获取组织者姓名信息
                    String organizerId = yunmeetingConference.getOrganizerId();
                    SysUser sysUser = userService.selectUserByUserId(organizerId);
                    if (null != sysUser) {
                        templateParamMaps.put("organizer", sysUser.getUserName());
                    }
                    templateParamMaps.put("meetingTheme", yunmeetingConference.getConferenceName());
                    templateParamMaps.put("meetingTime", fomatDate(yunmeetingConference.getTakeStartDate(), yunmeetingConference.getTakeEndDate()));
                    //根据会议Id查询会议室
                    List<YuncmMeetingRoom> yuncmMeetingRooms = meetingReserveService.selectMeetingRoomByMeetingId(meetingId);
                    String address = "";
                    if (null != yuncmMeetingRooms) {
                        address = yuncmMeetingRooms.get(0).getName();
                    }
                    templateParamMaps.put("meetingAddr", address);
                    templateParamMaps.put("meetingContent", yunmeetingConference.getConterenceContent());
                    //获取预定人姓名信息
                    String reservationPersonId = yunmeetingConference.getReservationPersonId();
                    sysUser = userService.selectUserByUserId(reservationPersonId);
                    if (null != sysUser) {
                        String userName = sysUser.getUserName();
                        String phoneNumber = sysUser.getPhoneNumber();
                        templateParamMaps.put("reservePerson", userName);
                        templateParamMaps.put("reservePersonTel", phoneNumber);
                    }
                    //根据会议Id查询所有参会人员
                    Map<String, Object> condition = new HashMap<>();
                    condition.put("type", "1");
                    condition.put("meetingId", meetingId);
                    String userNames = "";
                    List<YunmeetingParticipantsInfo> yunmeetingParticipantsInfos = meetingReserveService.selectParticipateMeetingByMeetingId(condition);
                    if (null != yunmeetingParticipantsInfos && yunmeetingParticipantsInfos.size() > 0) {
                        for (YunmeetingParticipantsInfo yunmeetingParticipantsInfo : yunmeetingParticipantsInfos) {
                            userNames += yunmeetingParticipantsInfo.getParticipantsName() + "、";
                        }
                    }
                    condition.put("type", "0");
                    yunmeetingParticipantsInfos = meetingReserveService.selectParticipateMeetingByMeetingId(condition);
                    if (null != yunmeetingParticipantsInfos && yunmeetingParticipantsInfos.size() > 0) {
                        for (YunmeetingParticipantsInfo yunmeetingParticipantsInfo : yunmeetingParticipantsInfos) {
                            userNames += yunmeetingParticipantsInfo.getParticipantsName() + "、";
                        }
                    }
                    templateParamMaps.put("participants", userNames.substring(0, userNames.length() - 1));
                    mailVo.setTemplateName("participants.notice.ftl");
                    mailVo.setTemplateParamMap(templateParamMaps);
                    mailVo.setSubject("参会通知");
                    //查询所有参会人员
                    List<PersonsVo> list = meetingReserveService.selectAllParticipantByMeetingId(meetingId);
                    for (PersonsVo personsVo : list) {
                        String userId = personsVo.getUserId();
                        SysUser sysUser1 = userService.selectUserByUserId(userId);
                        if (null != sysUser1) {
                            String email = sysUser1.getEmail();
                            String userName = sysUser1.getUserName();
                            recipientsMaps.put(email, userName);
                        }
                    }
                    mailVo.setRecipientsMap(recipientsMaps);
                    yunmeetingSendMailService.sendMail(mailVo);
                } else {
                    //查询会议室管理员角色
                    List<SysUser> sysUsers = userService.selectBoardroomManager("");
                    for(SysUser sysUser:sysUsers){
                        String id = sysUser.getId();
                        //发送待审核邮件通知
                        delayAuditMeetingNotice(meetingId,id);
                    }
                }
            }
        }
    }

    @Override
    public void changeMeetingNotice(String meetingId, Map<String, Object> map) {
        MailVo mailVo = new MailVo();
        //模板变量Map集合
        Map<String, String> templateParamMaps = new HashMap<>();
        //收件人Map集合
        Map<String, String> recipientsMaps = new HashMap<>();
        if (StringUtils.isNotBlank(meetingId)) {
            YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
            if(null!=yunmeetingConference) {
                if(yunmeetingConference.getIsAudit().equals("0")||(!yunmeetingConference.getState().equals("0")&&!yunmeetingConference.getState().equals("1"))){
                    //获取预定人信息
                    String reservationPersonId = yunmeetingConference.getReservationPersonId();
                    SysUser sysUser = userService.selectUserByUserId(reservationPersonId);
                    if (null != sysUser) {
                        String userName = sysUser.getUserName();
                        String email = sysUser.getEmail();
                        templateParamMaps.put("reservePerson",userName);
                        recipientsMaps.put(email, userName);
                    }
                    if (null != map) {
                        String startBefore = (String) map.get("startBefore");
                        templateParamMaps.put("meetingStartTimeBefore", startBefore);
                        templateParamMaps.put("meetingStartTimeAfter", fomatDate(yunmeetingConference.getTakeStartDate(), yunmeetingConference.getTakeEndDate()));
                    } else {
                        templateParamMaps.put("meetingStartTime", fomatDate(yunmeetingConference.getTakeStartDate(), yunmeetingConference.getTakeEndDate()));
                    }
                    //根据会议Id查询会议室
                    List<YuncmMeetingRoom> yuncmMeetingRooms = meetingReserveService.selectMeetingRoomByMeetingId(meetingId);
                    String address = "";
                    if (null != yuncmMeetingRooms) {
                        address = yuncmMeetingRooms.get(0).getName();
                    }
                    if (null != map) {
                        String addressBefore = (String) map.get("addressBefore");
                        mailVo.setTemplateName("meeting.change2.ftl");
                        templateParamMaps.put("meetingAddrBefore", addressBefore);
                        templateParamMaps.put("meetingAddrAfter", address);
                        if (map.containsKey("changeReason")) {
                            String changeReason = (String) map.get("changeReason");
                            templateParamMaps.put("changeReason", changeReason);
                        }
                    } else {
                        mailVo.setTemplateName("meeting.change1.ftl");
                        templateParamMaps.put("meetingAddr", address);
                    }
                    templateParamMaps.put("meetingTheme", yunmeetingConference.getConferenceName());
                    mailVo.setTemplateParamMap(templateParamMaps);
                    mailVo.setSubject("会议变更通知");
                    //查询所有参会人员
                    List<PersonsVo> list = meetingReserveService.selectAllParticipantByMeetingId(meetingId);
                    for (PersonsVo personsVo : list) {
                        String userId = personsVo.getUserId();
                        if (!userId.equals(reservationPersonId)) {
                            SysUser sysUser1 = userService.selectUserByUserId(userId);
                            if (null != sysUser1) {
                                String email = sysUser1.getEmail();
                                String userName = sysUser1.getUserName();
                                recipientsMaps.put(email, userName);
                            }
                        }
                    }
                    mailVo.setRecipientsMap(recipientsMaps);
                    yunmeetingSendMailService.sendMail(mailVo);
                }else{
                    //查询会议室管理员角色
                    List<SysUser> sysUsers = userService.selectBoardroomManager("");
                    for(SysUser sysUser:sysUsers){
                        String id = sysUser.getId();
                        //发送待审核邮件通知
                        delayAuditMeetingNotice(meetingId,id);
                    }
                }
            }
        }
    }

    @Override
    public void cancelMeetingNotice(String meetingId,boolean all) {
        MailVo mailVo = new MailVo();
        //模板变量Map集合
        Map<String, String> templateParamMaps = new HashMap<>();
        //收件人Map集合
        Map<String, String> recipientsMaps = new HashMap<>();
        if (StringUtils.isNotBlank(meetingId)) {
            YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
            if (null != yunmeetingConference) {
                //会议时间
                templateParamMaps.put("meetingTime",fomatDate(yunmeetingConference.getTakeStartDate(), yunmeetingConference.getTakeEndDate()));
                //根据会议Id查询会议室
                List<YuncmMeetingRoom> yuncmMeetingRooms = meetingReserveService.selectMeetingRoomByMeetingId(meetingId);
                String address = "";
                if (null != yuncmMeetingRooms) {
                    address = yuncmMeetingRooms.get(0).getName();
                }
                //会议地点
                templateParamMaps.put("meetingRoom",address);
                //会议主题
                templateParamMaps.put("meetingTheme",yunmeetingConference.getConferenceName());
                //会议组织者
                String organizerId = yunmeetingConference.getOrganizerId();
                SysUser sysUser = userService.selectUserByUserId(organizerId);
                if (null != sysUser) {
                    String userName = sysUser.getUserName();
                    String phoneNumber = sysUser.getPhoneNumber();
                    templateParamMaps.put("organizer",userName);
                    templateParamMaps.put("organizerTel",phoneNumber);
                }
                String cancelReason = yunmeetingConference.getCancelReason();
                if(StringUtils.isNotBlank(cancelReason)){
                    templateParamMaps.put("concelReason", "取消原因：“" + cancelReason + "”。");
                }else{
                    templateParamMaps.put("concelReason", "");
                }
                //查询所有参会人员
                if((!yunmeetingConference.getState().equals("0")&&!yunmeetingConference.getState().equals("1"))||all) {
                    List<PersonsVo> list = meetingReserveService.selectAllParticipantByMeetingId(meetingId);
                    for (PersonsVo personsVo : list) {
                        String userId = personsVo.getUserId();
                        SysUser sysUser1 = userService.selectUserByUserId(userId);
                        if (null != sysUser1) {
                            String email = sysUser1.getEmail();
                            String userName = sysUser1.getUserName();
                            recipientsMaps.put(email, userName);
                        }
                    }
                }else {
                    sysUser = userService.selectUserByUserId(yunmeetingConference.getReservationPersonId());
                    if (null != sysUser) {
                        String userName = sysUser.getUserName();
                        String email = sysUser.getEmail();
                        recipientsMaps.put(email,userName);
                    }
                }
                mailVo.setRecipientsMap(recipientsMaps);
                mailVo.setTemplateName("meeting.cancel.ftl");
                mailVo.setTemplateParamMap(templateParamMaps);
                mailVo.setSubject("取消会议通知");
                yunmeetingSendMailService.sendMail(mailVo);
            }
        }
    }

    @Override
    public boolean meetingRemid(MeetingVo meetingVo) {
        return false;
    }

    @Override
    public void attendMeetingNotice(String meetingId, int type,String auditReason) {
        MailVo mailVo = new MailVo();
        //模板变量Map集合
        Map<String, String> templateParamMaps = new HashMap<>();
        //收件人Map集合
        Map<String, String> recipientsMaps = new HashMap<>();
        if (StringUtils.isNotBlank(meetingId)) {
            YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
            if (null != yunmeetingConference) {
                templateParamMaps.put("meetingName",yunmeetingConference.getConferenceName());
                templateParamMaps.put("meetingTime",fomatDate(yunmeetingConference.getTakeStartDate(), yunmeetingConference.getTakeEndDate()));
                //根据会议Id查询会议室
                List<YuncmMeetingRoom> yuncmMeetingRooms = meetingReserveService.selectMeetingRoomByMeetingId(meetingId);
                String address = "";
                if (null != yuncmMeetingRooms) {
                    address = yuncmMeetingRooms.get(0).getName();
                }
                //会议地点
                templateParamMaps.put("meetingRoomName",address);
                if(type == 0){
                    templateParamMaps.put("auditReason",auditReason);
                    mailVo.setTemplateName("meeting.audit.notpass.ftl");
                    mailVo.setSubject("会议审核未通过通知");
                }else{
                    mailVo.setTemplateName("meeting.audit.pass.ftl");
                    mailVo.setSubject("会议审核通过通知");
                }
                SysUser sysUser = userService.selectUserByUserId(yunmeetingConference.getReservationPersonId());
                if (null != sysUser) {
                    String userName = sysUser.getUserName();
                    String email = sysUser.getEmail();
                    recipientsMaps.put(email,userName);
                }
                mailVo.setRecipientsMap(recipientsMaps);
                mailVo.setTemplateParamMap(templateParamMaps);
                yunmeetingSendMailService.sendMail(mailVo);
            }
        }
    }

    @Override
    public void delayAuditMeetingNotice(String meetingId, String meetingAdminId) {
        MailVo mailVo = new MailVo();
        //模板变量Map集合
        Map<String, String> templateParamMaps = new HashMap<>();
        //收件人Map集合
        Map<String, String> recipientsMaps = new HashMap<>();
        if (StringUtils.isNotBlank(meetingId)) {
            YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
            if (null != yunmeetingConference) {
                String reservationPersonId = yunmeetingConference.getReservationPersonId();
                SysUser sysUser = userService.selectUserByUserId(reservationPersonId);
                if (null != sysUser) {
                    String userName = sysUser.getUserName();
                    String email = sysUser.getEmail();
                    String phoneNumber = sysUser.getPhoneNumber();
                    templateParamMaps.put("reservePerson", userName);
                    templateParamMaps.put("mail", email);
                    templateParamMaps.put("reservePersonContact", phoneNumber);
                }
                //根据会议Id查询会议室
                List<YuncmMeetingRoom> yuncmMeetingRooms = meetingReserveService.selectMeetingRoomByMeetingId(meetingId);
                String address = "";
                if (null != yuncmMeetingRooms) {
                    address = yuncmMeetingRooms.get(0).getName();
                }
                templateParamMaps.put("meetingRoomName", address);
                templateParamMaps.put("meetingStartTime",fomatDate(yunmeetingConference.getTakeStartDate(), yunmeetingConference.getTakeEndDate()));
                sysUser = userService.selectUserByUserId(meetingAdminId);
                if (null != sysUser) {
                    String userName = sysUser.getUserName();
                    String email = sysUser.getEmail();
                    recipientsMaps.put(email,userName);
                }
                mailVo.setSubject("待审核会议通知");
                mailVo.setTemplateParamMap(templateParamMaps);
                mailVo.setTemplateName("meeting.audit.ftl");
                mailVo.setRecipientsMap(recipientsMaps);
                yunmeetingSendMailService.sendMail(mailVo);
            }
        }
    }


    private String fomatDate(Date start, Date end) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        SimpleDateFormat sf1 = new SimpleDateFormat("HH:mm");
        String startTime = sf.format(start);
        String endTime = sf1.format(end);
        return startTime + "~" + endTime;
    }
}
