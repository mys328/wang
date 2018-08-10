package com.thinkwin.auth.service.impl;

import com.thinkwin.auth.localService.LocalUserService;
import com.thinkwin.auth.mapper.db.SysOrganizationMapper;
import com.thinkwin.auth.mapper.db.SysUserMapper;
import com.thinkwin.auth.service.UserStatisticalAnalysisService;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.meetingVo.PersonsVo;
import com.thinkwin.common.vo.statisticalAnalysisVo.ConferenceNumInfoVo;
import com.thinkwin.common.vo.statisticalAnalysisVo.UserStatisticalDataVo;
import com.thinkwin.yuncm.service.MeetingDynamicService;
import com.thinkwin.yuncm.service.MeetingReserveService;
import com.thinkwin.yuncm.service.MeetingStatisticsService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: yinchunlei
 * Date: 2017/10/24.
 * Company: thinkwin
 * 按人员处理统计分析service实现层
 */
@Service("userStatisticalAnalysisService")
public class UserStatisticalAnalysisServiceImpl implements UserStatisticalAnalysisService{
    @Autowired
    private LocalUserService localUserService;
    @Autowired
    private MeetingReserveService meetingReserveService;
    @Autowired
    private MeetingDynamicService meetingDynamicService;

    @Autowired
    private MeetingStatisticsService meetingStatisticsService ;


    SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
    SimpleDateFormat sdf1 =   new SimpleDateFormat( " yyyy-MM-dd" );
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 根据开始时间和结束时间统计人员总数和未参会人数功能
     * @param startTime
     * @param endTime
     * @return
     */
    public Map getUserTotalNumAndNumberOfAbsentParticipants(String startTime, String endTime){
        Map map = new HashMap();
        //获取当前用户所在租户下的总人数（先暂定为除移除人员外全部查出来）
        Example example = new Example(SysUser.class,true,true);
        example.createCriteria().andNotEqualTo("status",89);
        Integer userTotalNum = localUserService.selectUserTotalNum(example);
        map.put("userTotalNum",userTotalNum);
        //获取未参会人数（numberOfAbsentParticipants）
        Integer numberOfAbsentParticipants = 0;
        //获取某段时间内的所有会议
        List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate = meetingReserveService.findByMeetingRoomIdAndMeetingtakeStartDate(null, startTime, endTime);
        if(null != byMeetingRoomIdAndMeetingtakeStartDate && byMeetingRoomIdAndMeetingtakeStartDate.size() > 0) {
            //根据会议id获取参与人的id集合
            List<String> list = getUserIdListByYunmeetingCnferenceId(byMeetingRoomIdAndMeetingtakeStartDate);
            //获取当前用户所在租户下的总人数（先暂定为除移除人员外全部查出来）
            Example example1 = new Example(SysUser.class,true,true);
            example1.createCriteria().andNotEqualTo("status",89);
            List<SysUser> sysUsers = sysUserMapper.selectByExample(example1);
            List<String> list2 = new ArrayList();
            if(null != sysUsers && sysUsers.size() > 0){
                for (SysUser sysUser : sysUsers) {
                    if(null != sysUser){
                        String userId = sysUser.getId();
                        if(StringUtils.isNotBlank(userId)){
                            list2.add(userId);
                        }
                    }
                }
            }
            if(null != list2 && list2.size() > 0) {
                for (String userIdd : list2 ) {
                    if (!list.contains(userIdd)) {
                        numberOfAbsentParticipants += 1;
                    }
                }
            }
        }
        map.put("numberOfAbsentParticipants", numberOfAbsentParticipants);
        return map;
    }


    /**
     * 获取人员统计分析页面数据功能接口(与搜索功能共用该接口)
     * @param orgId
     * @param queryCriteria
     * @return
     */
    public List<UserStatisticalDataVo> getUserStatisticalData(List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate, String orgId, String queryCriteria,String tenantId){
        List<UserStatisticalDataVo> userStatisticalDataVolist = new ArrayList();
        //if(null != byMeetingRoomIdAndMeetingtakeStartDate && byMeetingRoomIdAndMeetingtakeStartDate.size() > 0) {
            List<SysUser> list = sysUserMapper.getUserIds();
            userStatisticalDataVolist = getUserInfo(orgId,queryCriteria,list,byMeetingRoomIdAndMeetingtakeStartDate,tenantId);
       // }
        return userStatisticalDataVolist;
    }
    /**
     * 获取会议数量
     * @return
     */
    public Map getMeetingNum(String userId,List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate,String tenantId) {
        Map map = new HashMap();
        //会议组织数
        int organizationalMeetingNum = 0;
        //会议参与数
        int participationConferenceNum = 0;
        long meetingHours = 0;
        int conferenceMessageNumber = 0;
        String individualAttendanceRate = "0%";//个人签到率
        double SignNum = 0.0;
        String individualResponseRate = "0%";  //个人响应率
        double responseRateNum = 0.0;
        double individualCorrespondingRateNum = 0.0;
        double individualAttendanceRateNum = 0.0;
        if (null != byMeetingRoomIdAndMeetingtakeStartDate && byMeetingRoomIdAndMeetingtakeStartDate.size() > 0){
            for (YunmeetingConference yunmeetingConference : byMeetingRoomIdAndMeetingtakeStartDate) {
                if (null != yunmeetingConference) {
                    String meetingId = yunmeetingConference.getId();
                    if (StringUtils.isNotBlank(meetingId)) {
                        List<String> userIdListByMeetingId = new ArrayList<>();
                        //String meetingUserIds = RedisUtil.get("userIdListByMeetingId"+tenantId+meetingId);
                        //redis中缓存key的规则切换
                        String meetingUserIds = RedisUtil.get(tenantId+"_userIdListByMeetingId_"+meetingId);
                        if(StringUtils.isNotBlank(meetingUserIds)){
                            userIdListByMeetingId = java.util.Arrays.asList(meetingUserIds.split(","));
                        }else {
                            userIdListByMeetingId = getUserIdListByMeetingId(meetingId);
                            String join = StringUtils.join(userIdListByMeetingId, ",");
                            if(StringUtils.isNotBlank(join)) {
                                RedisUtil.set(tenantId+"_userIdListByMeetingId_"+meetingId, join);
                            }
                        }
                        //获取会议组织者主键id
                        String organizerId = yunmeetingConference.getOrganizerId();
                        if (StringUtils.isNotBlank(organizerId)) {
                            if (userId.equals(organizerId)) {
                                organizationalMeetingNum += 1;
                            } else {
                                if (null != userIdListByMeetingId && userIdListByMeetingId.size() > 0) {
                                    if (userIdListByMeetingId.contains(userId)) {
                                        participationConferenceNum += 1;
                                    }
                                }
                            }
                        }
                        if (null != userIdListByMeetingId && userIdListByMeetingId.size() > 0) {
                            if (userIdListByMeetingId.contains(userId)) {
                                Date takeStartDate = yunmeetingConference.getTakeStartDate();//会议预定开始时间
                                Date takeEndDate = yunmeetingConference.getTakeEndDate();//会议预定结束时间
                                meetingHours += takeEndDate.getTime() - takeStartDate.getTime();//获取毫秒时间
                            }
                        }
                        Integer num = meetingDynamicService.selectMeetingDynamicCreateIdsByMeetingId(meetingId, userId);
                        if(null != num && num >0){
                            conferenceMessageNumber += num;
                        }
                        String signInfo = RedisUtil.get(tenantId+"_signInfo_"+meetingId+"_"+userId);
                        if(StringUtils.isNotBlank(signInfo)) {
                           if("1".equals(signInfo)){
                               SignNum+=1;
                           }
                        }else {
                            YunmeetingMeetingSign yunmeetingMeetingSign = meetingReserveService.selectSignInfo(meetingId, userId);
                            if(null != yunmeetingMeetingSign) {
                                SignNum+=1;
                                RedisUtil.set(tenantId +"_signInfo_" + meetingId+"_"+userId,"1");
                            }else{
                                RedisUtil.set(tenantId +"_signInfo_" + meetingId+"_"+userId,"0");
                            }
                        }
                        String yunmeetingParticipantsReply = RedisUtil.get(tenantId+"_yunmeetingParticipantsReply_"+meetingId+"_"+userId);
                        if(StringUtils.isNotBlank(yunmeetingParticipantsReply)) {
                            if("1".equals(yunmeetingParticipantsReply)){
                                responseRateNum += 1;
                            }
                        }else {
                            YunmeetingParticipantsReply yunmeetingParticipantsReplyyy = meetingReserveService.selectParticipantsReply(meetingId, userId);
                            if(null != yunmeetingParticipantsReplyyy) {
                                responseRateNum += 1;
                                RedisUtil.set(tenantId +"_yunmeetingParticipantsReply_" +  meetingId+"_"+userId,"1");
                            }else{
                                RedisUtil.set(tenantId +"_yunmeetingParticipantsReply_" +  meetingId+"_"+userId,"0");
                            }
                        }
                    }
                }
            }
            if(SignNum != 0.0 || SignNum != 0){
                int rateNum =  organizationalMeetingNum+participationConferenceNum;
                if(rateNum > 0) {
                    individualAttendanceRateNum = SignNum /rateNum;
                    individualAttendanceRate = meetingStatisticsService.formatPercent(individualAttendanceRateNum);
                }else{
                    individualAttendanceRate = "0%";
                }
            }else{
                individualAttendanceRate = "0%";
            }
            if(responseRateNum != 0.0 || responseRateNum != 0){
                int rateNum =  organizationalMeetingNum+participationConferenceNum;
                if(rateNum > 0) {
                individualCorrespondingRateNum = responseRateNum / (organizationalMeetingNum+participationConferenceNum) ;
                individualResponseRate = meetingStatisticsService.formatPercent(individualCorrespondingRateNum);
                }else{
                    individualAttendanceRate = "0%";
                }
            }else{
                individualResponseRate = "0%";
            }
    }
        map.put("organizationalMeetingNum",organizationalMeetingNum);
        map.put("participationConferenceNum",participationConferenceNum);
        map.put("meetingNum",organizationalMeetingNum+participationConferenceNum);
        map.put("meetingHours",meetingHours);
        map.put("conferenceMessageNumber",conferenceMessageNumber);
        map.put("individualAttendanceRate",individualAttendanceRate);
        map.put("individualResponseRate",individualResponseRate);
        map.put("individualAttendanceRateNum",individualAttendanceRateNum);
        map.put("individualCorrespondingRateNum",individualCorrespondingRateNum);
        return map;
    }

    /**
     * 根据会议id获取参与人的id集合
     * @param byMeetingRoomIdAndMeetingtakeStartDate
     * @return
     */
    public List<String> getUserIdListByYunmeetingCnferenceId(List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate){
        List<String> list = new ArrayList();
        for (YunmeetingConference yunmeetingConference : byMeetingRoomIdAndMeetingtakeStartDate) {
            if (null != yunmeetingConference) {
               String meetingId = yunmeetingConference.getId();
                List list1 = new ArrayList();
                if(StringUtils.isNotBlank(meetingId)){
                    list1 = getUserIdListByMeetingId(meetingId);
                }
                list.removeAll(list1);
                list.addAll(list1);
            }
        }
        return list;
    }

    /**
     * 根据会议id获取参与人id集合
     * @param meetingId
     * @return
     */
    public List<String> getUserIdListByMeetingId(String meetingId){
        List<String> list1 = new ArrayList<>();
        if (StringUtils.isNotBlank(meetingId)) {
            //根据会议id获取参会人员
            List<PersonsVo> personsVos = meetingReserveService.selectAllParticipantByMeetingId(meetingId);
            if (null != personsVos && personsVos.size() > 0) {
                for (PersonsVo personsVo : personsVos) {
                    if (null != personsVo) {
                        String userId = personsVo.getUserId();
                        if (StringUtils.isNotBlank(userId)) {
                            if(!list1.contains(userId)) {
                                list1.add(userId);
                            }
                        }
                    }
                }
            }
        }
    return list1;
    }

    @Autowired
    SysOrganizationMapper sysOrganizationMapper;

    /**
     * 根据查询条件获取用户信息列表
     * @param orgId
     * @param queryCriteria
     * @param list
     * @return
     */
    public List<UserStatisticalDataVo> getUserInfo(String orgId,String queryCriteria,List<SysUser> list,List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate,String tenantId){
        List<UserStatisticalDataVo> newList = new ArrayList<>();
        List ll = new ArrayList();
        if(StringUtils.isNotBlank(orgId)){
            ///////////////////////////////////////递归算法//////////////////////////////////////////
            List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
            JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), orgId);
            JSONArray jsonArray1 = treeMenuList1(jsonArray);
            for (Object jsona:jsonArray1) {
                String s = jsona.toString();
                if(StringUtils.isNotBlank(s)){
                    ll.add(s);
                }
            }
            ll.add(orgId);
            ////////////////////////////////////////递归算法/////////////////////////////////////////
        }
        if(null != list && list.size() > 0) {
                for (SysUser sysUser:list) {
                       if(null != sysUser){
                           if (StringUtils.isNotBlank(orgId)&&StringUtils.isBlank(queryCriteria)) {
                           String orgId1 = sysUser.getOrgId();
                           if(StringUtils.isNotBlank(orgId1)&&null != ll && ll.size() > 0 && ll.contains(orgId1)){
                               UserStatisticalDataVo userStatisticalDataVo = getUserStatisticalDataVo(sysUser,byMeetingRoomIdAndMeetingtakeStartDate,tenantId);
                               if(null != userStatisticalDataVo){
                                   newList.add(userStatisticalDataVo);
                               }
                            }
                           }else if (StringUtils.isBlank(orgId)&&StringUtils.isNotBlank(queryCriteria)){
                               int i = getIsHaveUser(sysUser,queryCriteria);
                               if(i == 1){
                                   UserStatisticalDataVo userStatisticalDataVo = getUserStatisticalDataVo(sysUser,byMeetingRoomIdAndMeetingtakeStartDate,tenantId);
                                   if(null != userStatisticalDataVo){
                                       newList.add(userStatisticalDataVo);
                                   }
                               }
                           }else if(StringUtils.isNotBlank(orgId)&&StringUtils.isNotBlank(queryCriteria)){
                               String orgId1 = sysUser.getOrgId();
                               if(StringUtils.isNotBlank(orgId1)&& orgId.equals(orgId1)){
                                   int i = getIsHaveUser(sysUser,queryCriteria);
                                   if(i == 1){
                                       UserStatisticalDataVo userStatisticalDataVo = getUserStatisticalDataVo(sysUser,byMeetingRoomIdAndMeetingtakeStartDate,tenantId);
                                       if(null != userStatisticalDataVo){
                                           newList.add(userStatisticalDataVo);
                                       }
                                   }
                               }
                           }else{
                               UserStatisticalDataVo userStatisticalDataVo = getUserStatisticalDataVo(sysUser,byMeetingRoomIdAndMeetingtakeStartDate,tenantId);
                               if(null != userStatisticalDataVo){
                                   newList.add(userStatisticalDataVo);
                               }
                           }
                   }
            }
        }
        return newList;
    }


    //递归获取组织机构树
    public JSONArray treeMenuList(JSONArray menuList, String parentId){
        JSONArray childMenu = new JSONArray();
        for (Object object : menuList) {
            JSONObject jsonMenu = JSONObject.fromObject(object);
            String menuId = jsonMenu.getString("id");
            String pid = jsonMenu.getString("parentId");
            if (parentId.equals(pid)) {
                JSONArray c_node = treeMenuList(menuList, menuId);
                jsonMenu.put("childNode", c_node);
                childMenu.add(jsonMenu);
            }
        }
        return childMenu;
    }

    public JSONArray treeMenuList1(JSONArray menuList){
        JSONArray childMenu = new JSONArray();
        for (Object object : menuList) {
            JSONObject jsonMenu = JSONObject.fromObject(object);
            childMenu.add(jsonMenu.getString("id"));
            JSONArray c_node1 = jsonMenu.getJSONArray("childNode");
            if (null != c_node1 && c_node1.size() > 0) {
                JSONArray c_node2 = treeMenuList1(c_node1);
                childMenu.addAll(c_node2);
            }
        }
        return childMenu;
    }



    /**
     * 聚合UserStatisticalDataVo数据功能类
     * @param sysUser
     * @return
     */
    public UserStatisticalDataVo getUserStatisticalDataVo(SysUser sysUser,List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate,String tenantId){
        UserStatisticalDataVo userStatisticalDataVo = new UserStatisticalDataVo();
            if(null != sysUser){
                /////////////////////////////////////////////////////////////////////
                String userId = sysUser.getId();
                if(StringUtils.isNotBlank(userId)) {
                    Map meetingNum = getMeetingNum(userId, byMeetingRoomIdAndMeetingtakeStartDate,tenantId);
                    if(null != meetingNum) {
                        if(null != meetingNum.get("organizationalMeetingNum")) {
                            userStatisticalDataVo.setOrganizationalMeetingNum(String.valueOf(meetingNum.get("organizationalMeetingNum")));
                        }
                        if(null != meetingNum.get("participationConferenceNum")) {
                            userStatisticalDataVo.setParticipationConferenceNum(String.valueOf(meetingNum.get("participationConferenceNum")));
                        }
                        if(null != meetingNum.get("meetingNum")) {
                            userStatisticalDataVo.setMeetingNum(Integer.parseInt(meetingNum.get("meetingNum").toString()));
                        }
                        if(null !=meetingNum.get("meetingHours")) {
                            userStatisticalDataVo.setMeetingHours(new Integer(meetingNum.get("meetingHours").toString()));
                        }
                        if(null !=meetingNum.get("conferenceMessageNumber")) {
                            userStatisticalDataVo.setConferenceMessageNumber(Integer.parseInt(meetingNum.get("conferenceMessageNumber").toString()));
                        }
                        if(null !=meetingNum.get("individualAttendanceRate")) {
                            userStatisticalDataVo.setIndividualAttendanceRate((String) meetingNum.get("individualAttendanceRate"));
                        }
                        if(null !=meetingNum.get("individualResponseRate")) {
                            userStatisticalDataVo.setIndividualCorrespondingRate((String) meetingNum.get("individualResponseRate"));
                        }
                        if(null !=meetingNum.get("individualAttendanceRateNum")) {
                            userStatisticalDataVo.setIndividualAttendanceRateNum(Double.valueOf(meetingNum.get("individualAttendanceRateNum").toString()));
                        }
                        if(null !=meetingNum.get("individualCorrespondingRateNum")) {
                            userStatisticalDataVo.setIndividualCorrespondingRateNum(Double.valueOf(meetingNum.get("individualCorrespondingRateNum").toString()));
                        }
                    }
                }
                /////////////////////////////////////////////////////////////////////
                    userStatisticalDataVo.setUserId(sysUser.getId());
                    userStatisticalDataVo.setUserName(sysUser.getUserName());
            }
            return userStatisticalDataVo;
    }
    /**
     * 判断是否有符合条件的用户（辅助类）
     * @param sysUser
     * @param queryCriteria
     * @return
     */
    public int getIsHaveUser(SysUser sysUser,String queryCriteria){
        String userName = sysUser.getUserName();
        String userNamePinyin = sysUser.getUserNamePinyin();
        int i = 0;
        if(StringUtils.isNotBlank(userName)){
            if(userName.indexOf(queryCriteria)!=-1){
                i = 1;
            }
        }
        if(StringUtils.isNotBlank(userNamePinyin)){
            if(userNamePinyin.indexOf(queryCriteria)!=-1){
                i = 1;
            }
        }
        return i;
    }

    /**
     * 获取会议数量信息功能
     * @param userId
     * @param byMeetingRoomIdAndMeetingtakeStartDate
     * @return
     */
    public List<ConferenceNumInfoVo> getConferenceNumInfo(String userId, List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate){
        List list = new ArrayList();
        if(StringUtils.isNotBlank(userId) && null != byMeetingRoomIdAndMeetingtakeStartDate && byMeetingRoomIdAndMeetingtakeStartDate.size() > 0){
            Map<Object,Object> map = new HashMap<>();
            for (YunmeetingConference yunmeetingConference:byMeetingRoomIdAndMeetingtakeStartDate) {
                if(null != yunmeetingConference){
                    String meetingId = yunmeetingConference.getId();
                    if(StringUtils.isNotBlank(meetingId)){
                        //根据会议id获取参会人员
                        List<PersonsVo> personsVos = meetingReserveService.selectAllParticipantByMeetingId(meetingId);
                        if (null != personsVos && personsVos.size() > 0) {
                            for (PersonsVo personsVo : personsVos) {
                                if (null != personsVo) {
                                    String userId1 = personsVo.getUserId();
                                    if (StringUtils.isNotBlank(userId1)) {
                                        if(userId.equals(userId1)) {
                                            if(!map.containsKey(meetingId)) {
                                                ConferenceNumInfoVo conferenceNumInfoVo = new ConferenceNumInfoVo();
                                                conferenceNumInfoVo.setMeetingName(yunmeetingConference.getConferenceName());
                                                conferenceNumInfoVo.setMeetingStartTime(yunmeetingConference.getTakeStartDate());
                                                conferenceNumInfoVo.setMeetingEndTime(yunmeetingConference.getTakeEndDate());
                                                conferenceNumInfoVo.setParticipantsNum(personsVos.size()+"");
                                                String organizerId = yunmeetingConference.getOrganizerId();
                                                if(StringUtils.isNotBlank(organizerId)){
                                                    SysUser sysUserr = localUserService.getUserInfoByUserId(organizerId);
                                                    if(null != sysUserr){
                                                        String userName = sysUserr.getUserName();
                                                        if(StringUtils.isNotBlank(userName)){
                                                            conferenceNumInfoVo.setOrganizerName(userName);
                                                        }
                                                    }
                                                }
                                                map.put(meetingId,conferenceNumInfoVo);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(null != map){
                Iterator entries = map.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    ConferenceNumInfoVo value = (ConferenceNumInfoVo)entry.getValue();
                    if(null != value){
                        list.add(value);
                    }
                }
            }
        }
        return list;
    }

}
