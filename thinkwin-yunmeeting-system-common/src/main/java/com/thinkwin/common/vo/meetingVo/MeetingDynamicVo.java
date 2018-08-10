package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;
import java.util.List;

/**
 * 类名: MeetingDynamicVo </br>
 * 描述: 会议vo类</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/9 </br>
 */
public class MeetingDynamicVo implements Serializable{
    private static final long serialVersionUID = 8591542667040690186L;

    private String id;       //会议id
    private String title;     //会议名称
    private String location;        //会议地点
    private Long start;       //会议开始时间
    private Long end;     //会议结束时间
    private String status;      //状态
    private List<DynamicVo> dynamics;       //动态Vo
    private String dateState;  //时间状态  0：七天以前   1：七天以后

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<DynamicVo> getDynamics() {
        return dynamics;
    }

    public void setDynamics(List<DynamicVo> dynamics) {
        this.dynamics = dynamics;
    }

    public String getDateState() {
        return dateState;
    }

    public void setDateState(String dateState) {
        this.dateState = dateState;
    }
}
