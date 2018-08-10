package com.thinkwin.yuncm.service;

import com.thinkwin.common.model.db.YunmeetingConference;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/5/18 0018.
 */
public interface ConferenceService {


   /**
    * 获取当前天的会议
    * @param roomId 会议室id
    * @param date 当前时间
    * @return
    */
   public  List<YunmeetingConference> getSameDayConference(String roomId, Date date);

   /**
    * 获取当前会议
    * @param roomId 会议室id
    * @param date 当前时间
    * @return
    */
   public List<YunmeetingConference> getNextConference(String roomId, Date date);


   /**
    * 获取本周的会议
    * @param roomId
    * @param sta
    * @param end
    * @return
    */
   public List<YunmeetingConference> getThisWeekConference(String roomId,Date sta,Date end);

   /**
    * 根据终端id获取节目id
    * @param terminalId
    * @return
    */
   public String getProgramId(String terminalId);
}
