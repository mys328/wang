package com.thinkwin.yunmeeting.weixin.service;

import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.vo.ChangeAdminVo;
import com.thinkwin.common.vo.meetingVo.MeetingVo;

public interface WxTemplateMsgService {

    /**
     *创建会议-参会通知
     * @param meetingVo 会议
     * @return
     */
    public boolean createMeetingNotice(MeetingVo meetingVo);

    /**
     * 会议变更通知
     * @param meetingVo 会议
     * @return
     */
    public boolean changeMeetingNotice(MeetingVo meetingVo);


    /**
     * 会议取消通知
     * param meetingVo 会议
     * @return
     */
    public boolean cancelMeetingNotice(MeetingVo meetingVo);


    /**
     * 会前会议提醒
     * param meetingVo 会议
     * @return
     */
    public boolean meetingRemid(MeetingVo meetingVo);

    /**
     * 会议审核通知
     * param meetingVo 会议
     * @return
     */
    public boolean attendMeetingNotice(MeetingVo meetingVo,int type);

    /**
     * 变更主管理员
     * param
     * @return
     */

    public boolean changeAdmin(ChangeAdminVo changeAdminVo);


    /**
     * 解散企业
     * @param sysUser 当用启用
     * @param saasTenant 租户
     * @return
     */
    public boolean dissolutionCompanyNotice(SysUser sysUser, SaasTenant saasTenant);


}
