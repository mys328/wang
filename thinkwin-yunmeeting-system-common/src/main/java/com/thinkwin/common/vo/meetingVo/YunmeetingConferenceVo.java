package com.thinkwin.common.vo.meetingVo;

import com.thinkwin.common.model.db.YunmeetingConference;

import java.io.Serializable;
import java.util.List;

/**
  *
  *  会议Vo
  *  开发人员:daipengkai
  *  创建时间:2017/8/1
  *
  */
public class YunmeetingConferenceVo implements Serializable {

    private static final long serialVersionUID = -1952718460706938871L;
    //天数
    private String date;
    //每天的会议数量
    private Integer num;
    //会议
    private List<YunmeetingConference> meeting;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public List<YunmeetingConference> getMeeting() {
        return meeting;
    }

    public void setMeeting(List<YunmeetingConference> meeting) {
        this.meeting = meeting;
    }
}
