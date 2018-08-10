package com.thinkwin.common.utils.validation;

import com.thinkwin.common.model.db.YummeetingConferenceRoomMiddle;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.model.db.YunmeetingMessageInform;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.vo.meetingVo.MeetingDetailsVo;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 类名: meetingValidationUtil </br>
 * 描述:</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/2 </br>
 */
public class MeetingValidationUtil {

    public static Map<String, Object> addMeetingValidation(MeetingDetailsVo meetingDetailsVo, String userId) {
        if (null == meetingDetailsVo) {
            return null;
        }
        String meetingSubject = meetingDetailsVo.getTitle();
        if (StringUtils.isBlank(meetingSubject)) {
            return null;
        }
        if(meetingSubject.length()>50){
            return null;
        }
        if(StringUtils.isNotBlank(meetingDetailsVo.getContents())&&meetingDetailsVo.getContents().length()>300){
            return null;
        }
        Map<String, Object> map1 = new HashMap<>();
        String conferenceId = meetingDetailsVo.getConferenceId();
        String state = "update";
        if (StringUtils.isBlank(conferenceId)) {
            conferenceId = CreateUUIdUtil.Uuid();
            state = "save";
        }
        //会议实体
        YunmeetingConference yunmeetingConference = new YunmeetingConference();
        Map<String, Object> organizer = meetingDetailsVo.getOrganizer();
        if (null != organizer) {
            if (StringUtils.isNotBlank((String) organizer.get("id"))) {
                yunmeetingConference.setOrganizerId((String) organizer.get("id"));
            } else {
                yunmeetingConference.setOrganizerId(userId);
            }
        } else {
            yunmeetingConference.setOrganizerId(userId);
        }
        yunmeetingConference.setReservationPersonId(userId);
        yunmeetingConference.setConferenceName(meetingSubject);
        yunmeetingConference.setClientType(meetingDetailsVo.getClientType());
        yunmeetingConference.setConterenceContent(meetingDetailsVo.getContents());
        long start = meetingDetailsVo.getStart();
        long end = meetingDetailsVo.getEnd();
        Map map = fomatDate(start, end);
        if (null != map) {
            Date startTime = (Date) map.get("startTime");
            yunmeetingConference.setTakeStartDate(startTime);
            yunmeetingConference.setTakeEndDate((Date) map.get("endTime"));
            //处理提前提醒时间  变成list
            if(StringUtils.isNotBlank(meetingDetailsVo.getRemind())) {
                map1.put("times", dateFormat(startTime, meetingDetailsVo.getRemind()));
            }
        }
        if (state.equals("update")) {
            boolean isUpdate = true;
            if (map == null || null == meetingDetailsVo.getLocation()) {
                isUpdate = false;
            }
            map1.put("isUpdate", isUpdate);
        }
        yunmeetingConference.setConfrerenceCreateTime(new Date());
        yunmeetingConference.setReservationSuccessTime(new Date());
        String isPublic = meetingDetailsVo.getIsPublic();
        yunmeetingConference.setIsPublic("1");
        if(StringUtils.isNotBlank(isPublic)){
            yunmeetingConference.setIsPublic(isPublic);
        }

        Map<String, Object> department = meetingDetailsVo.getDepartment();
        if (null != department) {
            yunmeetingConference.setHostUnit((String) department.get("id"));
        }
        if(state.equals("save")){
            yunmeetingConference.setCreateTime(new Date());
            yunmeetingConference.setCreaterId(userId);
        }else{
            yunmeetingConference.setModifyerId(userId);
            yunmeetingConference.setModifyTime(new Date());
        }
        yunmeetingConference.setDeleteState("0");
        yunmeetingConference.setCancelState("0");
        yunmeetingConference.setId(conferenceId);
        //会议通知表实体
        if(StringUtils.isNotBlank(meetingDetailsVo.getNotice())) {
            YunmeetingMessageInform yunmeetingMessageInform = new YunmeetingMessageInform();
            yunmeetingMessageInform.setConferenceId(conferenceId);
            yunmeetingMessageInform.setInformType(meetingDetailsVo.getNotice());
            map1.put("yunmeetingMessageInform", yunmeetingMessageInform);
        }
        //会议和会议室关联表
        YummeetingConferenceRoomMiddle yummeetingConferenceRoomMiddle = new YummeetingConferenceRoomMiddle();
        Map<String, Object> location = meetingDetailsVo.getLocation();
        if (null != location) {
            yummeetingConferenceRoomMiddle.setRoomId((String) location.get("id"));
            yummeetingConferenceRoomMiddle.setConfrerenId(conferenceId);
            yummeetingConferenceRoomMiddle.setId(CreateUUIdUtil.Uuid());
            map1.put("yummeetingConferenceRoomMiddle", yummeetingConferenceRoomMiddle);
            map1.put("loction",location.get("name"));
        }
        map1.put("yunmeetingConference", yunmeetingConference);
        map1.put("state", state);
        return map1;
    }


    private static Map fomatDate(long start, long end) {
        if (start > 0 && end > 0) {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String starts = sf.format(start);
            String ends = sf.format(end);
            Map<String, Object> m = new HashMap<>();
            try {
                Date startTime = sf.parse(starts);
                Date endTime = sf.parse(ends);
                m.put("startTime", startTime);
                m.put("endTime", endTime);
                return m;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static List<Date> dateFormat(Date stratTime, String informTimes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stratTime);
        List<Date> list = new ArrayList<>();
        if (StringUtils.isNotBlank(informTimes)) {
            String[] splits = informTimes.split(",");
            for (String split : splits) {
                if (StringUtils.isNotBlank(split)) {
                    switch (split) {
                        case "15":
                            calendar.add(Calendar.MINUTE, -15);
                            break;
                        case "1h":
                            calendar.add(Calendar.HOUR_OF_DAY, -1);
                            break;
                        case "2h":
                            calendar.add(Calendar.HOUR_OF_DAY, -2);
                            break;
                        case "1d":
                            calendar.add(Calendar.DATE, -1);
                            break;
                    }
                    list.add(calendar.getTime());
                    calendar.setTime(stratTime);
                }
            }
            return list;
        }
        return null;
    }
}
