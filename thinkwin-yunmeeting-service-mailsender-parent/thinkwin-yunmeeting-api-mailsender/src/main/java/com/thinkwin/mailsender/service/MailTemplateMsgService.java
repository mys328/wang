package com.thinkwin.mailsender.service;

import com.thinkwin.common.vo.meetingVo.MeetingVo;

import java.util.Map;

public interface MailTemplateMsgService {

    /**
     *创建会议-参会通知
     * @param meetingId 会议Id
     * @return
     */
    public void createMeetingNotice(String meetingId);

    /**
     * 会议变更通知
     * @param meetingId 会议Id
     * @return
     */
    public void changeMeetingNotice(String meetingId, Map<String,Object> map);


    /**
     * 会议取消通知
     * @param meetingId 会议Id
     * @param all 发送给全部参会人
     * @return
     */
    public void cancelMeetingNotice(String meetingId,boolean all);


    /**
     * 会前会议提醒
     * param meetingVo 会议
     * @return
     */
    public boolean meetingRemid(MeetingVo meetingVo);

    /**
     * 会议审核通知
     * @param meetingId 会议Id
     * @param type 0未通过 1通过
     * @param auditReason 未通过原因
     * @return
     */
    public void attendMeetingNotice(String meetingId, int type,String auditReason);

    /**
     * 待审核会议通知
     * @param meetingId 会议Id
     * @param meetingAdminId 会议管理员Id
     * @return
     */
    public void delayAuditMeetingNotice(String meetingId,String meetingAdminId);

}
