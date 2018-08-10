package com.thinkwin.common.vo.publish;

import java.io.Serializable;

/**
 * User:wangxilei
 * Date:2018/5/8
 * Company:thinkwin
 */
public class TerminalPlanSwitchVo implements Serializable {
    private static final long serialVersionUID = 832671648237597801L;
    private String id;
    private String terminalName;
    private String meetingRoomName;
    private Integer state; //运行状态（1空闲0离线-1正在运行其他任务）
    private String taskName; //正在运行的其他任务的名称
    private String taskId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getMeetingRoomName() {
        return meetingRoomName;
    }

    public void setMeetingRoomName(String meetingRoomName) {
        this.meetingRoomName = meetingRoomName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
