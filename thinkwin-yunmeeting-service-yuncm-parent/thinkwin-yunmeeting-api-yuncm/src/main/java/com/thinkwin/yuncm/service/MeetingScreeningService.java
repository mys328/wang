package com.thinkwin.yuncm.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.vo.mobile.MobileMeetingNum;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
  *
  *  会议筛选
  *  开发人员:daipengkai
  *  创建时间:2017/8/1
  *
  */
public interface MeetingScreeningService {

    /**
     * 查看我组织的进七天的会议 不分页 当前时间往后七天 按创建时间排序
     * @param userId 用户id
     * @return
     */
    Map<String,Object> selectMyYunmeetingConferenceSevenDays(String userId, String myType) throws ParseException;




    /**
     * 查看未来的会议 分页 当前时间往后 按创建时间排序
     * @param page 当前页
     * @param countPage 每页个数
     * @return
     */
    Map<String,Object> selectYunmeetingConferenceFuture(BasePageEntity page, String myType, String userId) throws ParseException;

    /**
     * 查看过去的会议 分页 当前时间往前 按创建时间排序
     * @param page 当前页
     * @param countPage 每页个数
     * @return
     */
    Map<String,Object> selectYunmeetingConferenceFormerly(BasePageEntity page, String myType, String userId) throws ParseException;


    /**
     * 查看我审核的会议筛选
     * @param page
     * @param auditType
     * @param userId
     * @return
     */
    Map<String,Object> selectAuditYunmeetingConference(BasePageEntity page, String auditType, String userId);

    /**
     * 按条件所搜我的会议 近七天
     * @param page 当前页
     * @param countPage 每页个数
     * @return
     */
    Map<String,Object> selectSearchYunmeetingConference(BasePageEntity page,String meetingType, String myType, String userId,String searchKey) throws ParseException;

    /**
     * 按条件所搜我的会议 过去 未来
     * @param page 当前页
     * @param countPage 每页个数
     * @return
     */
    Map<String,Object> selectSearchYunmeetingConferenceAfter(BasePageEntity page,String meetingType, String myType, String userId,String searchKey) throws ParseException;

    /**
     * 查看我审核的会议筛选
     * @param page
     * @param auditType
     * @param userId
     * @return
     */
    Map<String,Object> selectAuditSearchYunmeetingConference(BasePageEntity page, String auditType, String userId,String searchKey);

    /**
     * 根据会议室id 和时间段查看 会议 会议室看板
     * @param roomId
     * @param startTime
     * @param endTime
     * @return
     */
    List<YunmeetingConference> selectTimeYunmeetingConference(String roomId,String startTime,String endTime);

    /**
     * 微信H5 近七天会议
     * @param userId
     * @param myType
     * @return
     * @throws ParseException
     */
    Map<String,Object> h5MyYunmeetingSevenDays(String userId, String myType) throws ParseException;

    /**
     * 微信H5 未来会议
     * @param page
     * @param myType
     * @param userId
     * @return
     * @throws ParseException
     */
    Map<String,Object> h5MyYunmeetingFuture(BasePageEntity page, String myType, String userId) throws ParseException;

    /**
     * 微信H5 历史会议
     * @param page
     * @param myType
     * @param userId
     * @return
     * @throws ParseException
     */
    Map<String,Object> h5MyYunmeetingFormerly(BasePageEntity page, String myType, String userId) throws ParseException;

    /**
     * 分类统计
     *      我组织的：近七天00、未来01、过去02
     *      我参与的：近七天10、未来11、过去12
     * @param userId
     * @return
     */
    public Integer h5MyYunMeetingKindCount(String userId,String isCreate,String meetingType);

    /**
     * h5获取我审核的会议数量
     * @return
     */
    public Integer h5MyAuditMeeting();

    /**
     * h5获取我审核的会议详情
     * @param status 状态 0待办，1已办
     * @param page 分页对象
     * @param userId 用户ID
     * @return
     */
    public PageInfo<YunmeetingConference> h5MyAuditMeetingInfo(String status,String userId,BasePageEntity page);


    /**
     * h5模糊搜索我审核的会议
     * @param page
     * @param status 状态 0待办，1已办
     * @param userId
     * @param content 搜索内容
     * @return
     */
    public PageInfo<YunmeetingConference> h5AuditSearchYunmeetingConference(String status, String userId,String content,BasePageEntity page);

    /**
     * h5查看审核详情
     * @param meetingId 会议室id
     * @param userId 当前用户id
     * @return
     */
    public YunmeetingConference h5AuditYunmeetingConferenceInfo(String meetingId,String userId);

    /**
     * 查看代办已办数量
     * @return
     */
    public MobileMeetingNum selectYunmeetingConferenceNumber();
}
