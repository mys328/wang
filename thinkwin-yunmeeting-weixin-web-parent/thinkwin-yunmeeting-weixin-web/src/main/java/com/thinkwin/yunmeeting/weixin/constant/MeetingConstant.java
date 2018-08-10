package com.thinkwin.yunmeeting.weixin.constant;

/*
 * 类说明：
 * @author lining 2017/12/15
 * @version 1.0
 *
 */
public class MeetingConstant {

    //会议时间信息
    public static class MeetingDateMsgInfo {
        public static final String GREATER_BEGIN_TIME= "当前时间大于或等于开始时间";
        public static final String GREATER_END_TIME= "当前时间大于或等于结束时间";

        public MeetingDateMsgInfo(){
        }
    }

    //会议状态
    public static class MeetingStatus{

        public static final String FAIL="0"; //审核未通过
        public static final String AUDIT="1"; //待审核
        public static final String BEFORE="2"; //未开始
        public static final String START="3"; //进行中
        public static final String END="4"; //已结束
        public static final String CANCEL="5"; //已取消

        public MeetingStatus(){

        }

    }

    //会议签到返回信息
    public static class ScanMeetingSignMsg{

        public static final String SUCCESS="1"; //签到成功
        public static final String FAIL="2"; //签到失败
        public static final String EXPIRE="3"; //二维码过期
        public static final String REPEAT="4"; //重复签到
        public static final String NOT_PERSON="5"; //不是参会人
        public static final String MEETING_INVALID="6"; //会议结束、取消、删除等

        public ScanMeetingSignMsg(){

        }

    }


}
