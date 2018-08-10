package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 类名: MeetingVo </br>
 * 描述: 会议vo类</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/2 </br>
 */
public class MeetingDetailsVo implements Serializable {
    private static final long serialVersionUID = 3199134747752264213L;

    private String conferenceId;       //会议Id
    private String title;    // 会议主题
    private Long start;      // 开始时间
    private Long end;       // 结束时间
    private String contents;       //会议内容
    private String clientType;      //会议来源
    private String isPublic;       //是否公开 0:不公开；1:公开
    private String resourceId;      //会议室Id
    private String notice;       // 1:微信、2:&邮件、1,2:微信&邮件
    private String remind;      // 1:准时、2:提前15分钟、3:提前1小时、4:提前2小时、5:提前1天
    private String status;      // 会议状态:0:审核未通过;1:待审核； 2:未开始；3:进行中；4:已结束；5：已取消
    private String userRole;    //当前用户角色 0：其他 1：会议室管理员 2：会议参与者 3：预订人和参与者 4：预订人管理员 5：预订人管理员参与者 6：预订人 7：管理员参与者
    private Map<String, Object> location;    // 会议地点
    private Map<String, Object> organizer;       // 会议组织者
    private Map<String, Object> department;      //主办方
    private List<MeetingParticipantsVo> attendees;      // 会议参会人
    private List<MeetingParticipantsVo> allAttendees;      // 会议参会人
    private List<DynamicVo> dynamics;       //会议动态
    private Map<String, Object> bookeder;      //预订人
    private String signInfo; //签到统计
    private String signStatus="0"; //当前人员签到状态,0:未签到,1签到
    private String replyInfo; //回复统计
    private String replyStatus; //响应状态
    private Long createTime; //创建时间
    private String tenantType;
    private String isShowSign;  //是否显示签到按钮   0不显示  1显示

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }

    public Map<String, Object> getLocation() {
        return location;
    }

    public void setLocation(Map<String, Object> location) {
        this.location = location;
    }

    public Map<String, Object> getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Map<String, Object> organizer) {
        this.organizer = organizer;
    }

    public Map<String, Object> getDepartment() {
        return department;
    }

    public void setDepartment(Map<String, Object> department) {
        this.department = department;
    }

    public List<MeetingParticipantsVo> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<MeetingParticipantsVo> attendees) {
        this.attendees = attendees;
    }

    public List<MeetingParticipantsVo> getAllAttendees() {
        return allAttendees;
    }

    public void setAllAttendees(List<MeetingParticipantsVo> allAttendees) {
        this.allAttendees = allAttendees;
    }

    public List<DynamicVo> getDynamics() {
        return dynamics;
    }

    public void setDynamics(List<DynamicVo> dynamics) {
        this.dynamics = dynamics;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Map<String, Object> getBookeder() {
        return bookeder;
    }

    public void setBookeder(Map<String, Object> bookeder) {
        this.bookeder = bookeder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getSignInfo() {
        return signInfo;
    }

    public void setSignInfo(String signInfo) {
        this.signInfo = signInfo;
    }

    public String getReplyInfo() {
        return replyInfo;
    }

    public void setReplyInfo(String replyInfo) {
        this.replyInfo = replyInfo;
    }

    public String getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(String replyStatus) {
        this.replyStatus = replyStatus;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getTenantType() {
        return tenantType;
    }

    public void setTenantType(String tenantType) {
        this.tenantType = tenantType;
    }

    public String getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(String signStatus) {
        this.signStatus = signStatus;
    }

    public String getIsShowSign() {
        return isShowSign;
    }

    public void setIsShowSign(String isShowSign) {
        this.isShowSign = isShowSign;
    }
}
