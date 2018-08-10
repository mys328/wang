package com.thinkwin.yuncm.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.utils.TimeUtil;
import com.thinkwin.common.vo.meetingVo.PersonsVo;
import com.thinkwin.common.vo.meetingVo.YunmeetingConferenceVo;
import com.thinkwin.common.vo.mobile.MobileMeetingNum;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.yuncm.mapper.*;
import com.thinkwin.yuncm.service.MeetingScreeningService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
  *
  *  会议筛选实现
  *  开发人员:daipengkai
  *  创建时间:2017/8/1
  *
  */
@Service("meetingScreeningService")
public class MeetingScreeningServiceImpl implements MeetingScreeningService {

    @Resource
    YunmeetingConferenceMapper yunmeetingConferenceMapper;

    @Resource
    YuncmMeetingRoomMapper yuncmMeetingRoomMapper;

    @Resource
    YummeetingConferenceRoomMiddleMapper yummeetingConferenceRoomMiddleMapper;

    @Resource
    YunmeetingParticipantsInfoMapper yunmeetingParticipantsInfoMapper;

    @Resource
    YunmeetingConferenceUserInfoMapper yunmeetingConferenceUserInfoMapper;

    @Resource
    UserService userService;
    @Resource
    FileUploadService fileUploadService;

    @Resource
    YunmeetingConferenceAuditMapper yunmeetingConferenceAuditMapper;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    public Map<String,Object> selectMyYunmeetingConferenceSevenDays(String userId,String myType) throws ParseException {

        Map<String,Object> omap = new HashedMap();
        List<Object> objects = new ArrayList<Object>();
         //获取当前时间的后7天得到数组
         List<String> times = TimeUtil.timeArray(7);
        List<YunmeetingConferenceVo> conferenceVos = new ArrayList<YunmeetingConferenceVo>();
        //获取前三天每一天的会议
        Map maps = new HashedMap();
        String date = format.format(new Date());
        if("0".equals(myType)){
            maps.put("myAll",myType);
        }
        if("1".equals(myType)){
            maps.put("myOrgan",myType);
        }
        if("2".equals(myType)){
            maps.put("myJoin",myType);
        }
        maps.put("userId",userId);
        for(int i = 0; i < 3; i++){
            maps.put("days","days");
            //获取三天的会议
            YunmeetingConferenceVo conferenceVo =null;
            try {
                maps.put("date",format.parse(times.get(i)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            List<YunmeetingConference> conferences = new ArrayList<YunmeetingConference>();
            if("0".equals(myType)){
                conferences = this.yunmeetingConferenceMapper.selectAllMeetingScreen(maps);
            }else{
                conferences = this.yunmeetingConferenceMapper.selectFutureYunmeetingConference(maps);
            }
            List<YunmeetingConference> conferenceList = new ArrayList<YunmeetingConference>();
            //当天是否存在会议
            if(conferences.size() != 0){
                for(YunmeetingConference conference : conferences){
                    boolean b = true;
                    conference = obtainYunmeetingConferenceInfo(conference,userId);
                   //查看参会人员
                    Map<String,Object> map = selectAttendMeetingPerson(conference.getId(),userId,conference);
                    boolean success = (boolean) map.get("success");
                    //查看我的全部会议
                    if("0".equals(myType)){
                        if(conference.getCreaterId().equals(userId) || success || conference.getOrganizerId().equals(userId)){
                                conferenceList.add(conference);
                        }
                    }
                    //查看我组织的会议
                    if("1".equals(myType)){
                        if(conference.getCreaterId().equals(userId) ||  conference.getOrganizerId().equals(userId)){
                            conferenceList.add(conference);
                        }
                    }
                    //查看我参加的会议
                    if("2".equals(myType)){
                        if(success){
                            conferenceList.add(conference);
                        }
                    }

                }
                if(conferenceList.size() != 0) {
                    conferenceVo = new YunmeetingConferenceVo();
                    if (i == 0) {
                        conferenceVo.setDate("今天");
                    }
                    if (i == 1) {
                        conferenceVo.setDate("明天");
                    }
                    if (i == 2) {
                        conferenceVo.setDate("后天");
                    }
                    conferenceVo.setNum(conferences.size());
                    conferenceVo.setMeeting(conferenceList);
                }
            }
            if(conferenceVo != null){
                objects.add(conferenceVo);
            }

        }
       //获取三天后的会议
        //删除key
        maps.remove("days");
        maps.remove("date");
        maps.put("after","after");
        try {
            maps.put("staDate",format.parse(times.get(3)));
            maps.put("endDate",format.parse(times.get(times.size()-1)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<YunmeetingConference> conferences = new ArrayList<YunmeetingConference>();
        if("0".equals(myType)){
            conferences = this.yunmeetingConferenceMapper.selectAllMeetingScreen(maps);
        }else{
            conferences = this.yunmeetingConferenceMapper.selectFutureYunmeetingConference(maps);
        }
        //当天是否存在会议
        YunmeetingConferenceVo conferenceVo = null;
        if(conferences.size() != 0){
            List<YunmeetingConference> conferenceList = new ArrayList<YunmeetingConference>();
            for(YunmeetingConference conference : conferences) {
                boolean b = true;
                conference = obtainYunmeetingConferenceInfo(conference,userId);
                //查看参会人员
                Map<String,Object> map = selectAttendMeetingPerson(conference.getId(),userId,conference);
                boolean success = (boolean) map.get("success");
                //查看我的全部会议
                if("0".equals(myType)){
                    if(conference.getCreaterId().equals(userId) || success || conference.getOrganizerId().equals(userId)){
                            conferenceList.add(conference);
                    }
                }
                //查看我组织的会议
                if("1".equals(myType)){
                    if(conference.getCreaterId().equals(userId) || conference.getOrganizerId().equals(userId)){
                        conferenceList.add(conference);
                    }
                }
                //查看我参加的会议
                if("2".equals(myType)){
                    if(success){
                        conferenceList.add(conference);
                    }
                }
            }
            if(conferenceList.size() != 0) {
                conferenceVo = new YunmeetingConferenceVo();
                conferenceVo.setDate("三天后");
                conferenceVo.setNum(conferences.size());
                conferenceVo.setMeeting(conferences);
            }
        }
        if(conferenceVo != null){
            objects.add(conferenceVo);
        }
        omap = acquireMeetingAmount(userId,"");
        Map map = selectRecentRelatedMeeting(userId,"");
        omap.put("myOrgan",map.get("myOrgan"));
        omap.put("myJoin",map.get("myJoin"));
        omap.put("conference",objects);

        return omap;
    }



    @Override
    public Map<String,Object> selectYunmeetingConferenceFuture(BasePageEntity page, String myType, String userId) throws ParseException {

        //获取我组织的会议数量
        int myOrgan = 0;
        int myJoin = 0;
        Map<String,Object> omap = new HashedMap();
        Map map = new HashedMap();
        List<String> times = TimeUtil.timeArray(7);
        if("0".equals(myType)){
            map.put("myAll",myType);
        }
        if("1".equals(myType)){
            map.put("myOrgan",myType);
        }
        if("2".equals(myType)){
            map.put("myJoin",myType);
        }
        map.put("userId",userId);
        map.put("date",format.parse(times.get(times.size()-1)));
        map.put("future","future");
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<YunmeetingConference> conferenceList = new ArrayList<YunmeetingConference>();
        if("0".equals(myType)){
            conferenceList = this.yunmeetingConferenceMapper.selectAllMeetingScreen(map);
        }else{
            conferenceList = this.yunmeetingConferenceMapper.selectFutureYunmeetingConference(map);
        }
        for(YunmeetingConference conference : conferenceList){

            conference = obtainYunmeetingConferenceInfo(conference,userId);
            if("0".equals(myType)) {
                boolean b = true;
                //查看参会人员
                Map<String, Object> join = selectAttendMeetingPerson(conference.getId(), userId,conference);
                boolean success = (boolean) join.get("success");
                //获取我组织的会议
                if (conference.getOrganizerId().equals(userId) || conference.getCreaterId().equals(userId)) {
                    myOrgan++;
                    b = false;
                }
                //获取我参加的数量
                if (success) {
                    if (b) {
                        myJoin++;
                    }
                }

            }
        }
        omap = acquireMeetingAmount(userId,"");

        Map maps = new HashedMap();
        maps.put("userId",userId);
        maps.put("date",format.parse(times.get(times.size()-1)));
        maps.put("future","future");
        //获取我参加的会议
        maps.remove("myOrgan");
        maps.put("myJoin",myType);
        List<YunmeetingConference> conferences = yunmeetingConferenceMapper.selectFutureYunmeetingConference(maps);
        //我参与的会议数量1
        omap.put("myJoin",conferences.size());
        //获取我参加的会议
        maps.remove("myJoin");
        maps.put("myOrgan",myOrgan);
        List<YunmeetingConference> conferences1 = yunmeetingConferenceMapper.selectFutureYunmeetingConference(maps);
        //我组织的会议数量
        omap.put("myOrgan",conferences1.size());
        omap.put("meeting",new PageInfo<>(conferenceList));
        return omap;
    }

    @Override
    public Map<String,Object>  selectYunmeetingConferenceFormerly(BasePageEntity page, String myType, String userId) throws ParseException {

        List<String> times = TimeUtil.timeArray(7);
        //获取我组织的会议数量
        int myOrgan = 0;
        int myJoin = 0;
        Map<String,Object> omap = new HashedMap();
        Map map = new HashedMap();
        String date = format.format(new Date());
        if("0".equals(myType)){
            map.put("myAll",myType);
        }
        if("1".equals(myType)){
            map.put("myOrgan",myType);
        }
        if("2".equals(myType)){
            map.put("myJoin",myType);
        }
        map.put("orderBy","");
        map.put("userId",userId);
        map.put("date",format.parse(date));
        map.put("formerly","formerly");
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<YunmeetingConference> conferenceList = new ArrayList<YunmeetingConference>();
        if("0".equals(myType)){
            conferenceList = this.yunmeetingConferenceMapper.selectAllMeetingScreen(map);
        }else{
            conferenceList = this.yunmeetingConferenceMapper.selectFutureYunmeetingConference(map);
        }
        for(YunmeetingConference conference : conferenceList){
            conference = obtainYunmeetingConferenceInfo(conference,userId);
            if("0".equals(myType)) {
                boolean b = true;
                //查看参会人员
                Map<String, Object> join = selectAttendMeetingPerson(conference.getId(), userId,null);
                boolean success = (boolean) join.get("success");

                //获取我组织的会议
                if (conference.getOrganizerId().equals(userId) || conference.getCreaterId().equals(userId)) {
                    myOrgan++;
                    b = false;
                }

                //获取我参加的数量
                if (success) {
                    if (b) {
                        myJoin++;
                    }
                }
            }
        }
        omap = acquireMeetingAmount(userId,"");
        Map maps =new HashedMap();
        maps.put("userId",userId);
        maps.put("date",format.parse(date));
        maps.put("formerly","formerly");
            //获取我参加的会议
        maps.remove("myOrgan");
        maps.put("myJoin",myType);
        List<YunmeetingConference> conferences = yunmeetingConferenceMapper.selectFutureYunmeetingConference(maps);
        //我参与的会议数量
        omap.put("myJoin",conferences.size());
        //获取我参加的会议
        maps.remove("myJoin");
        maps.put("myOrgan",myOrgan);
        List<YunmeetingConference> conferences1 = yunmeetingConferenceMapper.selectFutureYunmeetingConference(maps);
            //我组织的会议数量
        omap.put("myOrgan",conferences1.size());
        omap.put("meeting",new PageInfo<>(conferenceList));
        return omap;
    }

    @Override
    public Map<String,Object> selectAuditYunmeetingConference(BasePageEntity page, String auditType, String userId) {

        Map<String,Object> objectMap = new HashedMap();
        Map map =new HashedMap();
        List<YunmeetingConference> conferenceList = new ArrayList<YunmeetingConference>();
        //显示全部
        if(auditType.equals("3")){
            map.put("myAll","myAll");
        }
        //未通过
        if(auditType.equals("0")){
           map.put("for","for");
        }
        //已通过
        if(auditType.equals("1")){
           map.put("undue","undue");
        }
        //待审批
        if(auditType.equals("2")){
           map.put("centre","centre");
        }
        //待审批已通过
        if(auditType.equals("4")){
            map.put("undueAndCentre","undueAndCentre");
        }
        //待审批未通过
        if(auditType.equals("5")){
            map.put("forAndCentre","forAndCentre");
        }
        //已审批未通过
        if(auditType.equals("6")){
            map.put("forAndUndue","forAndUndue");
        }
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        conferenceList = this.yunmeetingConferenceMapper.selectAuditYunmeetingConference(map);
        //获取我组织的会议
        for(YunmeetingConference conference : conferenceList) {
            conference = obtainYunmeetingConferenceInfo(conference,userId);

        }
        objectMap.put("meeting",conferenceList);
        //获取审核通过 未通过 未审核的会议数量
        Map maps = new HashedMap();
        List<YunmeetingConference> conferences = new ArrayList<YunmeetingConference>();
        //未通过
        maps.put("for","for");
        conferences = this.yunmeetingConferenceMapper.selectAuditYunmeetingConference(maps);
        objectMap.put("notPass",conferences.size());
        //已通过
        maps.remove("for");
        maps.put("undue","undue");
        conferences = this.yunmeetingConferenceMapper.selectAuditYunmeetingConference(maps);
        objectMap.put("pass",conferences.size());
        //待审批
        maps.remove("undue");
        maps.put("centre","centre");
        conferences = this.yunmeetingConferenceMapper.selectAuditYunmeetingConference(maps);
        objectMap.put("pending",conferences.size());

        return objectMap;
    }




    @Override
    public Map<String, Object> selectSearchYunmeetingConference(BasePageEntity page,String meetingType, String myType, String userId,String searchKey) throws ParseException {

        Map<String, Object> objectMap = new HashedMap();
        Map map =new HashedMap();
        //获取当前时间的后7天得到数组
        List<String> times = TimeUtil.timeArray(7);
        //获取我组织的会议数量
        int myOrgan = 0;
        int myJoin = 0;
        String date = format.format(new Date());
        // 判断查询 ：0：我的全部会议，1：我组织的会议，2：我参与的会议，
        if("0".equals(myType)){
            map.put("myAll",myType);
        }
        if("1".equals(myType)){
            map.put("myOrgan",myType);
        }
        if("2".equals(myType)){
            map.put("myJoin",myType);
        }
        //判断是否是 0：最近7天的会议，1：未来的会议，2：过去的会议
        if("1".equals(meetingType)){
            map.put("future","future");
            map.put("date",format.parse(times.get(times.size()-1)));
        }
        if("2".equals(meetingType)){
            map.put("formerly","formerly");
            map.put("date",format.parse(date));
        }
        map.put("userId",userId);
        map.put("searchKey",searchKey);
        //分页
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<YunmeetingConference> conferenceList = this.yunmeetingConferenceMapper.selectSearchYunmeetingConference(map);
        //获取我组织的会议
        for(YunmeetingConference conference : conferenceList) {

            conference = obtainYunmeetingConferenceInfo(conference,userId);
            if("0".equals(myType)) {
                boolean b = true;
                //查看参会人员
                Map<String, Object> join = selectAttendMeetingPerson(conference.getId(), userId,null);
                boolean success = (boolean) join.get("success");

                if (conference.getOrganizerId().equals(userId) || conference.getCreaterId().equals(userId)) {
                    myOrgan++;
                    b = false;
                }

                //获取我参加的数量
                if (success) {
                    if (b) {
                        myJoin++;
                    }
                }
            }
        }

        objectMap = acquireMeetingAmount(userId,searchKey);
        objectMap.put("meeting", new PageInfo<>(conferenceList));
          //获取我参加的会议
        map.remove("myAll");
        map.remove("myOrgan");
        map.remove("myJoin");
        map.put("myJoin",myType);
        List<YunmeetingConference> conferences = yunmeetingConferenceMapper.selectSearchYunmeetingConference(map);
            //我参与的会议数量1
        objectMap.put("myJoin",conferences.size());
            //获取我参加的会议
        map.remove("myJoin");
        map.put("myOrgan",myOrgan);
        List<YunmeetingConference> conferences1 = yunmeetingConferenceMapper.selectSearchYunmeetingConference(map);
            //我组织的会议数量
         objectMap.put("myOrgan",conferences1.size());
        return objectMap;
    }

    @Override
    public Map<String, Object> selectSearchYunmeetingConferenceAfter(BasePageEntity page, String meetingType, String myType, String userId, String searchKey) throws ParseException {


        Map<String,Object> omap = new HashedMap();
        List<Object> objects = new ArrayList<Object>();
        //获取当前时间的后7天得到数组
        List<String> times = TimeUtil.timeArray(7);
        List<YunmeetingConferenceVo> conferenceVos = new ArrayList<YunmeetingConferenceVo>();
        Map maps = new HashedMap();
        String date = format.format(new Date());
        if("0".equals(myType)){
            maps.put("myAll",myType);
        }
        if("1".equals(myType)){
            maps.put("myOrgan",myType);
        }
        if("2".equals(myType)){
            maps.put("myJoin",myType);
        }
        maps.put("searchKey",searchKey);
        maps.put("userId",userId);
        for(int i = 0; i < 3; i++){
            maps.put("days","days");
            //获取三天的会议
            YunmeetingConferenceVo conferenceVo =null;
            try {
                maps.put("date",format.parse(times.get(i)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            List<YunmeetingConference> conferences = this.yunmeetingConferenceMapper.selectSearchYunmeetingConference(maps);
            List<YunmeetingConference> conferenceList = new ArrayList<YunmeetingConference>();
            //当天是否存在会议
            if(conferences.size() != 0){
                for(YunmeetingConference conference : conferences){

                    conference = obtainYunmeetingConferenceInfo(conference,userId);
                    //查看参会人员
                    Map<String,Object> map = selectAttendMeetingPerson(conference.getId(),userId,null);
                    boolean success = (boolean) map.get("success");
                    //查看我的全部会议
                    if("0".equals(myType)){
                        if(conference.getCreaterId().equals(userId) || success || conference.getOrganizerId().equals(userId)){
                            conferenceList.add(conference);
                        }
                    }
                    //查看我组织的会议
                    if("1".equals(myType)){
                        if(conference.getCreaterId().equals(userId) ||  conference.getOrganizerId().equals(userId)){
                            conferenceList.add(conference);
                        }
                    }
                    //查看我参加的会议
                    if("2".equals(myType)){
                        if(success){
                            conferenceList.add(conference);
                        }
                    }

                }
                if(conferenceList.size() != 0) {
                    conferenceVo = new YunmeetingConferenceVo();
                    if (i == 0) {
                        conferenceVo.setDate("今天");
                    }
                    if (i == 1) {
                        conferenceVo.setDate("明天");
                    }
                    if (i == 2) {
                        conferenceVo.setDate("后天");
                    }
                    conferenceVo.setNum(conferences.size());
                    conferenceVo.setMeeting(conferenceList);
                }
            }
            if(conferenceVo != null){
                objects.add(conferenceVo);
            }

        }
        //获取三天后的会议
        //删除key
        maps.remove("days");
        maps.remove("date");
        maps.put("after","after");
        try {
            maps.put("staDate",format.parse(times.get(3)));
            maps.put("endDate",format.parse(times.get(times.size()-1)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<YunmeetingConference> conferences = this.yunmeetingConferenceMapper.selectSearchYunmeetingConference(maps);
        //当天是否存在会议
        YunmeetingConferenceVo conferenceVo = null;
        if(conferences.size() != 0){
            List<YunmeetingConference> conferenceList = new ArrayList<YunmeetingConference>();
            for(YunmeetingConference conference : conferences) {
                conference = obtainYunmeetingConferenceInfo(conference,userId);
                //查看参会人员
                Map<String,Object> map = selectAttendMeetingPerson(conference.getId(),userId,null);
                boolean success = (boolean) map.get("success");
                //查看我的全部会议
                if("0".equals(myType)){
                    if(conference.getCreaterId().equals(userId) || success || conference.getOrganizerId().equals(userId)){
                        conferenceList.add(conference);
                    }
                }
                //查看我组织的会议
                if("1".equals(myType)){
                    if(conference.getCreaterId().equals(userId) || conference.getOrganizerId().equals(userId)){
                        conferenceList.add(conference);
                    }
                }
                //查看我参加的会议
                if("2".equals(myType)){
                    if(success){
                        conferenceList.add(conference);
                    }
                }
            }
            if(conferenceList.size() != 0) {
                conferenceVo = new YunmeetingConferenceVo();
                conferenceVo.setDate("三天后");
                conferenceVo.setNum(conferences.size());
                conferenceVo.setMeeting(conferences);
            }
        }
        if(conferenceVo != null){
            objects.add(conferenceVo);
        }
        omap = acquireMeetingAmount(userId,searchKey);
        Map map = selectRecentRelatedMeeting(userId,searchKey);
        omap.put("conference", objects);
        omap.put("myOrgan",map.get("myOrgan"));
        omap.put("myJoin",map.get("myJoin"));
        return omap;

    }

    @Override
    public Map<String, Object> selectAuditSearchYunmeetingConference(BasePageEntity page, String auditType, String userId, String searchKey) {

        Map<String,Object> objectMap = new HashedMap();
        Map map =new HashedMap();
        List<YunmeetingConference> conferenceList = new ArrayList<YunmeetingConference>();
        //显示全部
        if(auditType.equals("3")){
            map.put("myAll","myAll");
        }
        //未通过
        if(auditType.equals("0")){
            map.put("for","for");
        }
        //已通过
        if(auditType.equals("1")){
            map.put("undue","undue");
        }
        //待审批
        if(auditType.equals("2")){
            map.put("centre","centre");
        }
        //待审批已通过
        if(auditType.equals("4")){
            map.put("undueAndCentre","undueAndCentre");
        }
        //待审批未通过
        if(auditType.equals("5")){
            map.put("forAndCentre","forAndCentre");
        }
        //已审批未通过
        if(auditType.equals("6")){
            map.put("forAndUndue","forAndUndue");
        }
        map.put("searchKey",searchKey);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        conferenceList = this.yunmeetingConferenceMapper.selectAuditSearchYunmeetingConference(map);
        //获取我组织的会议
        for(YunmeetingConference conference : conferenceList) {
            conference = obtainYunmeetingConferenceInfo(conference,userId);
        }
        objectMap.put("meeting",conferenceList);
        //获取审核通过 未通过 未审核的会议数量
        Map maps = new HashedMap();
        maps.put("searchKey",searchKey);
        List<YunmeetingConference> conferences = new ArrayList<YunmeetingConference>();
        //未通过
        maps.put("for","for");
        conferences = this.yunmeetingConferenceMapper.selectAuditSearchYunmeetingConference(maps);
        objectMap.put("notPass",conferences.size());
        //已通过
        maps.remove("for");
        maps.put("undue","undue");
        conferences = this.yunmeetingConferenceMapper.selectAuditSearchYunmeetingConference(maps);
        objectMap.put("pass",conferences.size());
        //待审批
        maps.remove("undue");
        maps.put("centre","centre");
        conferences = this.yunmeetingConferenceMapper.selectAuditSearchYunmeetingConference(maps);
        objectMap.put("pending",conferences.size());

        return objectMap;
    }

    public Map<String,Object> selectAttendMeetingPerson(String id,String userId,YunmeetingConference conference){

        Map<String,Object> map =new HashedMap();
        boolean success = false;
        List<PersonsVo> personsVos = new ArrayList<PersonsVo>();
        Example example = new Example(YunmeetingParticipantsInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("conferenceId",id);
        List<YunmeetingParticipantsInfo> infos = yunmeetingParticipantsInfoMapper.selectByExample(example);
        for(YunmeetingParticipantsInfo info : infos){
            PersonsVo personsVo = new PersonsVo();
            if("0".equals(info.getType())){
                if(conference != null) {
                    if (userId.equals(info.getParticipantsId()) && !"0".equals(conference.getState()) && !"1".equals(conference.getState()) && !"5".equals(conference.getState())) {
                        success = true;
                    }
                }else{
                    if (userId.equals(info.getParticipantsId())){
                        success = true;
                    }
                }
                personsVo.setUserId(info.getParticipantsId());
                personsVo.setUserName(info.getParticipantsName());
                personsVos.add(personsVo);
            }
            if("1".equals(info.getType())){
                Example infoExample = new Example(YunmeetingConferenceUserInfo.class);
                Example.Criteria criteria1 = infoExample.createCriteria();
                criteria1.andEqualTo("participantsInfoId",info.getId());
                List<YunmeetingConferenceUserInfo> userInfos = this.yunmeetingConferenceUserInfoMapper.selectByExample(infoExample);
                for (YunmeetingConferenceUserInfo userInfo : userInfos){
                    if(conference != null) {
                        if (userId.equals(userInfo.getParticipantsId()) && !"0".equals(conference.getState()) && !"1".equals(conference.getState()) && !"5".equals(conference.getState())) {
                            success = true;
                        }
                    }else{
                        if (userId.equals(userInfo.getParticipantsId())){
                            success = true;
                        }
                    }
                    PersonsVo persons = new PersonsVo();
                    persons.setUserId(userInfo.getParticipantsId());
                    persons.setUserName(userInfo.getParticipantsName());
                    personsVos.add(persons);
                }

            }
        }
        map.put("success",success);
        map.put("person",personsVos);
        return map;
    }




   //获取会议其他信息
    public YunmeetingConference obtainYunmeetingConferenceInfo(YunmeetingConference conference,String userId){

        //查看会议室所在地
        List<YuncmMeetingRoom> room = this.yuncmMeetingRoomMapper.selectYuncmMeetingRoomYunmeetingConference(conference.getId());
        if (room != null) {
            if (StringUtils.isNotBlank(room.get(0).getName())) {
                conference.setAddress(room.get(0).getName());
                conference.setRoomId(room.get(0).getId());
            }else{
                conference.setAddress("");
                conference.setRoomId("");
            }
        }else{
            conference.setAddress("");
            conference.setRoomId("");
        }

        //获取创建者
        SysUser sysUser = userService.selectUserByUserId(conference.getCreaterId());
        if(sysUser != null){
            conference.setUserName(sysUser.getUserName());
            //获取创建者头像
            if(StringUtils.isNotBlank(sysUser.getPhoto())){
               /* Map<String,String> strMap = this.fileUploadService.selectFileCommon(sysUser.getPhoto());*/
                Map<String, String> strMap = userService.getUploadInfo(sysUser.getPhoto());
                if(null != strMap) {
                    if (strMap.get("big") != null) {
                        conference.setUserNameBigPicture(strMap.get("big"));
                    } else {
                        conference.setUserNameBigPicture("");
                    }
                    if (strMap.get("in") != null) {
                        conference.setUserNameInPicture(strMap.get("in"));
                    } else {
                        conference.setUserNameInPicture("");
                    }
                    if (strMap.get("small") != null) {
                        conference.setUserNameSmallPicture(strMap.get("small"));
                    } else {
                        conference.setUserNameSmallPicture("");
                    }
                    conference.setUserNameUrl(strMap.get("small"));
                }
            }else{
                conference.setUserNameUrl("");
                conference.setUserNameBigPicture("");
                conference.setUserNameInPicture("");
                conference.setUserNameSmallPicture("");
            }
        }
        Example example = new Example(YunmeetingConferenceAudit.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("baseConfrerenId",conference.getId())
                .andEqualTo("deleteState","0");
        List<YunmeetingConferenceAudit> audits = this.yunmeetingConferenceAuditMapper.selectByExample(example);
        if(audits.size() != 0){
            conference.setAuditWhy(audits.get(0).getAuditAnnotations());
            conference.setExamineName(audits.get(0).getActAuditor());
            conference.setExamineTime(audits.get(0).getActAuditTime());
        }else {
            conference.setAuditWhy("");
            conference.setExamineName("");
            conference.setExamineNameUrl("");
        }
        if(StringUtils.isNotBlank(userId)) {
            //查看参会人员
            Map<String, Object> map1 = selectAttendMeetingPerson(conference.getId(), userId,null);
            List<PersonsVo> personsVos = (List<PersonsVo>) map1.get("person");
            conference.setCount(personsVos.size() + "");
            conference.setPersonsVos(personsVos);
            if (userId.equals(conference.getCreaterId())) {
                conference.setIsOrg("1");
            } else {
                conference.setIsOrg("0");
            }
        }
        return conference;
    }


    /**
     * 获取过去的 未来的 最近的 我组织的 我参与的 会议数量
     * @return
     */
  public Map<String,Object> acquireMeetingAmount(String userId,String searchKey) {

      Map<String, Object> maps = new HashedMap();
    try {
        //获取当前时间的后7天得到数组
        List<String> times = TimeUtil.timeArray(7);
        String date = format.format(new Date());
        if (StringUtils.isNotBlank(searchKey)) {
            //获取未来的搜索会议
            Map mapo = new HashedMap();
            mapo.put("myAll", "");
            mapo.put("userId", userId);
            mapo.put("searchKey", searchKey);
            //查看未来的
            mapo.put("future", "future");
            mapo.put("date", format.parse(times.get(times.size() - 1)));
            List<YunmeetingConference> conferenceFuture = this.yunmeetingConferenceMapper.selectSearchYunmeetingConference(mapo);
            //未来的会议数量
            maps.put("future", conferenceFuture.size());
            //查看过去的会议
            mapo.remove("future");
            mapo.put("formerly", "formerly");
            mapo.put("date", format.parse(date));
            List<YunmeetingConference> conferenceFormerly = this.yunmeetingConferenceMapper.selectSearchYunmeetingConference(mapo);
            maps.put("formerly", conferenceFormerly.size());
            //查看最近7天的所有会议
            mapo.remove("formerly");
            mapo.remove("date");
            mapo.put("after", "after");
            mapo.put("staDate", format.parse(times.get(0)));
            mapo.put("endDate", format.parse(times.get(times.size() - 1)));
            List<YunmeetingConference> conferenceRecent = this.yunmeetingConferenceMapper.selectSearchYunmeetingConference(mapo);
            maps.put("recent", conferenceRecent.size());
        } else {
            Map map = new HashedMap();
            //获取未来的所有会议
            map.put("myAll", "");
            map.put("userId", userId);
            map.put("date", format.parse(times.get(times.size() - 1)));
            map.put("future", "future");
            List<YunmeetingConference> conferenceList = yunmeetingConferenceMapper.selectAllMeetingScreen(map);
            //获取过去的会议
            map.remove("future");
            map.put("formerly", "formerly");
            map.put("date", format.parse(date));
            List<YunmeetingConference> conference = yunmeetingConferenceMapper.selectAllMeetingScreen(map);
            //最近7天的会议数量
            //获取最近7天的会
            Map map1 = new HashedMap();
            //获取未来的所有会议
            map1.put("myAll", "");
            map1.put("userId", userId);
            map1.put("after", "after");
            map1.put("staDate", format.parse(times.get(0)));
            map1.put("endDate", format.parse(times.get(times.size() - 1)));
            List<YunmeetingConference> conferenc = yunmeetingConferenceMapper.selectAllMeetingScreen(map1);
            //未来的
            maps.put("future", conferenceList.size());
            //过去的
            maps.put("formerly", conference.size());
            //最近7天的
            maps.put("recent", conferenc.size());
        }
    }catch (Exception e){
        e.printStackTrace();
    }
      return maps;
  }


  public Map<String,Object> selectRecentRelatedMeeting(String userId,String searchKey) throws ParseException {

      Map<String,Object> map = new HashedMap();
      //获取我组织的会议数量
      int myOrgan = 0;
      //获取我参加的会议数量
      int myJoin = 0;
      //获取当前时间的后7天得到数组
      List<String> times = TimeUtil.timeArray(7);
      List<YunmeetingConference> conferences = new ArrayList<YunmeetingConference>();
      if(StringUtils.isNotBlank(searchKey)){
          //获取未来的搜索会议
          Map mapo =new HashedMap();
          mapo.put("myAll","");
          mapo.put("userId",userId);
          mapo.put("searchKey",searchKey);
          mapo.put("after","after");
          mapo.put("staDate",format.parse(times.get(0)));
          mapo.put("endDate",format.parse(times.get(times.size()-1)));
          conferences = this.yunmeetingConferenceMapper.selectSearchYunmeetingConference(mapo);
      }else {
          //获取最近7天的会
          Map map1 = new HashedMap();
          //获取未来的所有会议
          map1.put("myAll", "");
          map1.put("userId", userId);
          map1.put("after", "after");
          map1.put("staDate", format.parse(times.get(0)));
          map1.put("endDate", format.parse(times.get(times.size() - 1)));
          conferences = yunmeetingConferenceMapper.selectFutureYunmeetingConference(map1);
      }
      for (YunmeetingConference conference : conferences) {
          boolean b = true;
          //查看参会人员
          Map<String, Object> join = selectAttendMeetingPerson(conference.getId(), userId,null);
          boolean success = (boolean) join.get("success");
          //获取我组织的会议
          if (conference.getCreaterId().equals(userId)) {
              myOrgan++;
              b = false;
          }
          //获取我参加的数量
          if (success) {
              if (b) {
                  if(!"0".equals(conference.getState()) && !"1".equals(conference.getState()) && !"5".equals(conference.getState())){
                      myJoin++;
                  }
              }
          }
      }
      map.put("myJoin", myJoin);
      map.put("myOrgan", myOrgan);

      return map;
  }


    @Override
    public List<YunmeetingConference> selectTimeYunmeetingConference(String roomId, String startTime, String endTime) {

        List<YunmeetingConference> conferences = new ArrayList<YunmeetingConference>();
        Map map = new HashedMap();
        map.put("roomId",roomId);
        map.put("staDate",startTime);
        map.put("endDate",endTime);
        conferences = this.yunmeetingConferenceMapper.selectSectionTimeRoomYunmeetingConference(map);
        for(YunmeetingConference conference : conferences){
            boolean b = true;
            conference = obtainYunmeetingConferenceInfo(conference,null);
        }
        return conferences;
    }

    /**
     * 微信H5 近七天会议
     *
     * @param userId
     * @param myType
     * @return
     * @throws ParseException
     */
    @Override
    public Map<String, Object> h5MyYunmeetingSevenDays(String userId, String myType) throws ParseException {
        Map<String,Object> omap = new HashedMap();
        List<Object> objects = new ArrayList<Object>();
        //获取当前时间的后7天得到数组
        List<String> times = TimeUtil.timeArray(7);
        List<YunmeetingConferenceVo> conferenceVos = new ArrayList<YunmeetingConferenceVo>();
        //获取前三天每一天的会议
        Map maps = new HashedMap();
        String date = format.format(new Date());
        if("1".equals(myType)){
            maps.put("myOrgan",myType);
        }
        if("2".equals(myType)){
            maps.put("myJoin",myType);
        }
        maps.put("userId",userId);
        for(int i = 0; i < 3; i++){
            maps.put("days","days");
            //获取三天的会议
            YunmeetingConferenceVo conferenceVo =null;
            try {
                maps.put("date",format.parse(times.get(i)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            List<YunmeetingConference> conferences = this.yunmeetingConferenceMapper.selectFutureYunmeetingConference(maps);
            List<YunmeetingConference> conferenceList = new ArrayList<YunmeetingConference>();
            //当天是否存在会议
            if(conferences.size() != 0){
                for(YunmeetingConference conference : conferences){
                    boolean b = true;
                    conference = obtainYunmeetingConferenceInfo(conference,userId);
                    //查看参会人员
                    Map<String,Object> map = selectAttendMeetingPerson(conference.getId(),userId,null);
                    boolean success = (boolean) map.get("success");
                    //查看我组织的会议
                    if("1".equals(myType)){
                        if(conference.getCreaterId().equals(userId) ||  conference.getOrganizerId().equals(userId)){
                            conferenceList.add(conference);
                        }
                    }
                    //查看我参加的会议
                    if("2".equals(myType)){
                        if(success){
                            conferenceList.add(conference);
                        }
                    }

                }
                if(conferenceList.size() != 0) {
                    conferenceVo = new YunmeetingConferenceVo();
                    if (i == 0) {
                        conferenceVo.setDate("今天");
                    }
                    if (i == 1) {
                        conferenceVo.setDate("明天");
                    }
                    if (i == 2) {
                        conferenceVo.setDate("后天");
                    }
                    conferenceVo.setNum(conferences.size());
                    conferenceVo.setMeeting(conferenceList);
                }
            }
            if(conferenceVo != null){
                objects.add(conferenceVo);
            }
        }
        //获取三天后的会议
        //删除key
        maps.remove("days");
        maps.remove("date");
        maps.put("after","after");
        try {
            maps.put("staDate",format.parse(times.get(3)));
            maps.put("endDate",format.parse(times.get(times.size()-1)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<YunmeetingConference> conferences = this.yunmeetingConferenceMapper.selectFutureYunmeetingConference(maps);
        //当天是否存在会议
        YunmeetingConferenceVo conferenceVo = null;
        if(conferences.size() != 0){
            List<YunmeetingConference> conferenceList = new ArrayList<YunmeetingConference>();
            for(YunmeetingConference conference : conferences) {
                boolean b = true;
                conference = obtainYunmeetingConferenceInfo(conference,userId);
                //查看参会人员
                Map<String,Object> map = selectAttendMeetingPerson(conference.getId(),userId,null);
                boolean success = (boolean) map.get("success");
                //查看我组织的会议
                if("1".equals(myType)){
                    if(conference.getCreaterId().equals(userId) || conference.getOrganizerId().equals(userId)){
                        conferenceList.add(conference);
                    }
                }
                //查看我参加的会议
                if("2".equals(myType)){
                    if(success){
                        conferenceList.add(conference);
                    }
                }
            }
            if(conferenceList.size() != 0) {
                conferenceVo = new YunmeetingConferenceVo();
                conferenceVo.setDate("三天后");
                conferenceVo.setNum(conferences.size());
                conferenceVo.setMeeting(conferences);
            }
        }
        if(conferenceVo != null){
            objects.add(conferenceVo);
        }
        omap.put("conference",objects);

        return omap;

    }

    /**
     * 微信H5 未来会议
     *
     * @param page
     * @param myType
     * @param userId
     * @return
     * @throws ParseException
     */
    @Override
    public Map<String, Object> h5MyYunmeetingFuture(BasePageEntity page, String myType, String userId) throws ParseException {
        //获取我组织的会议数量
        int myOrgan = 0;
        int myJoin = 0;
        Map<String,Object> omap = new HashedMap();
        Map map = new HashedMap();
        List<String> times = TimeUtil.timeArray(7);
        if("1".equals(myType)){
            map.put("myOrgan",myType);
        }
        if("2".equals(myType)){
            map.put("myJoin",myType);
        }
        map.put("userId",userId);
        map.put("date",format.parse(times.get(times.size()-1)));
        map.put("future","future");
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<YunmeetingConference> conferenceList = yunmeetingConferenceMapper.selectFutureYunmeetingConference(map);
        for(YunmeetingConference conference : conferenceList){

            conference = obtainYunmeetingConferenceInfo(conference,userId);
            if("0".equals(myType)) {
                boolean b = true;
                //查看参会人员
                Map<String, Object> join = selectAttendMeetingPerson(conference.getId(), userId,null);
                boolean success = (boolean) join.get("success");
                //获取我组织的会议
                if (conference.getOrganizerId().equals(userId) || conference.getCreaterId().equals(userId)) {
                    myOrgan++;
                    b = false;
                }
                //获取我参加的数量
                if (success) {
                    if (b) {
                        myJoin++;
                    }
                }

            }
        }
        omap.put("meeting",new PageInfo<>(conferenceList));
        return omap;
    }

    /**
     * 微信H5 历史会议
     *
     * @param page
     * @param myType
     * @param userId
     * @return
     * @throws ParseException
     */
    @Override
    public Map<String, Object> h5MyYunmeetingFormerly(BasePageEntity page, String myType, String userId) throws ParseException {
        List<String> times = TimeUtil.timeArray(7);
        //获取我组织的会议数量
        int myOrgan = 0;
        int myJoin = 0;
        Map<String,Object> omap = new HashedMap();
        Map map = new HashedMap();
        String date = format.format(new Date());
        if("1".equals(myType)){
            map.put("myOrgan",myType);
        }
        if("2".equals(myType)){
            map.put("myJoin",myType);
        }
        map.put("userId",userId);
        map.put("date",format.parse(date));
        map.put("formerly","formerly");
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<YunmeetingConference> conferenceList = yunmeetingConferenceMapper.selectFutureYunmeetingConference(map);
        for(YunmeetingConference conference : conferenceList){
            conference = obtainYunmeetingConferenceInfo(conference,userId);
            if("0".equals(myType)) {
                boolean b = true;
                //查看参会人员
                Map<String, Object> join = selectAttendMeetingPerson(conference.getId(), userId,null);
                boolean success = (boolean) join.get("success");

                //获取我组织的会议
                if (conference.getOrganizerId().equals(userId) || conference.getCreaterId().equals(userId)) {
                    myOrgan++;
                    b = false;
                }

                //获取我参加的数量
                if (success) {
                    if (b) {
                        myJoin++;
                    }
                }
            }
        }
        omap.put("meeting",new PageInfo<>(conferenceList));
        return omap;
    }

    /**
     * 分类统计
     * 我组织的：近七天、未来、过去
     * 我参与的：近七天、未来、过去
     *
     * @param userId
     * @return
     */
    @Override
    public Integer h5MyYunMeetingKindCount(String userId,String isCreate,String meetingType) {
        Map<String,String> map=new HashMap<>();
        map.put("userId",userId);
        if("0".equals(isCreate)) {
            map.put("isCreate", isCreate);
        }
        switch (meetingType) {
            case "recent": //近七天
                String startTime=format.format(new Date());
                String endTime=TimeUtil.getFetureDate(6);
                map.put("recent",meetingType);
                map.put("startTime",startTime+" 00:00:00");
                map.put("endTime",endTime+" 23:59:59");
                break;
            case "future"://未来
                endTime=TimeUtil.getFetureDate(7);
                map.put("future",meetingType);
                map.put("startTime",endTime+" 00:00:00");
                break;
            case "formerly"://历史
                startTime=format.format(new Date());
                map.put("formerly",meetingType);
                map.put("startTime",startTime+" 00:00:00");
                break;
            default:
                break;
        }
        List<YunmeetingConference> list=new ArrayList<>();
        if("0".equals(isCreate)) {
            list=this.yunmeetingConferenceMapper.h5MyYunMeetingKindCount2(map);
        }else{
            list=this.yunmeetingConferenceMapper.h5MyYunMeetingKindCount1(map);
        }
        return (null!=list &&list.size()>0)?list.size():0;
    }

    @Override
    public Integer h5MyAuditMeeting() {
        //获取审核通过 未通过 未审核的会议数量
        Map map = new HashedMap();
        map.put("centre","centre");
        List<YunmeetingConference>  conferences = this.yunmeetingConferenceMapper.selectAuditYunmeetingConference(map);
        return conferences.size();
    }

    @Override
    public PageInfo<YunmeetingConference> h5MyAuditMeetingInfo(String status, String userId,BasePageEntity page) {
        Map<String,Object> objectMap = new HashedMap();
        Map map =new HashedMap();
        List<YunmeetingConference> conferenceList = new ArrayList<YunmeetingConference>();
        //已通过
        if(status.equals("1")){
            map.put("forAndUndue","forAndUndue");
        }
        //待审批
        if(status.equals("0")) {
            map.put("centre", "centre");
        }
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        conferenceList = this.yunmeetingConferenceMapper.selectAuditYunmeetingConference(map);
        for(YunmeetingConference conference : conferenceList){
            //获取创建者
            SysUser sysUser = userService.selectUserByUserId(conference.getCreaterId());
            if(sysUser != null){
                conference.setUserName(sysUser.getUserName());
                String photo = sysUser.getPhoto();
                //获取创建者头像
                if(StringUtils.isNotBlank(photo)) {
                    Map<String, String> picMap = userService.getUploadInfo(photo);
                    if(null != picMap) {
                        conference.setUserNameUrl(picMap.get("primary"));
                        conference.setUserNameBigPicture(picMap.get("big"));
                        conference.setUserNameInPicture(picMap.get("in"));
                        conference.setUserNameSmallPicture(picMap.get("small"));
                    }
                }
            }
        }
        return new PageInfo<>(conferenceList);
    }

    @Override
    public PageInfo<YunmeetingConference> h5AuditSearchYunmeetingConference(String status, String userId, String content,BasePageEntity page) {
        Map<String,Object> objectMap = new HashedMap();
        Map map =new HashedMap();
        //已通过
        if(status.equals("1")){
            map.put("undue","undue");
        }
        //待审批
        if(status.equals("0")){
            map.put("centre","centre");
        }
        map.put("searchKey",content);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<YunmeetingConference>  conferenceList = this.yunmeetingConferenceMapper.selectAuditSearchYunmeetingConference(map);
        for(YunmeetingConference conference : conferenceList){
            //获取创建者
            SysUser sysUser = userService.selectUserByUserId(conference.getCreaterId());
            if(sysUser != null){
                conference.setUserName(sysUser.getUserName());
                //获取创建者头像
//                String url  = this.fileUploadService.selectTenementByFile(sysUser.getPhoto());
//                if(StringUtils.isNotBlank(url)){
//                    conference.setUserNameUrl(url);
//                }else{
//                    conference.setUserNameUrl("");
//                }
                String photo = sysUser.getPhoto();
//                    if (StringUtils.isNotBlank(photo)) {
//                        Map<String, String> picMap = userService.getUploadInfo(photo);
//                        if (null != picMap) {
//                            conference.setUserNameUrl(picMap.get("big"));
//                        } else {
//                            conference.setUserNameUrl("");
//                        }
//                    } else {
//                        conference.setUserNameUrl("");
//                    }
                if(StringUtils.isNotBlank(photo)) {
                    Map<String, String> picMap = userService.getUploadInfo(photo);
                    if(null != picMap) {
                        conference.setUserNameUrl(picMap.get("primary"));
                        conference.setUserNameBigPicture(picMap.get("big"));
                        conference.setUserNameInPicture(picMap.get("in"));
                        conference.setUserNameSmallPicture(picMap.get("small"));
                    }
                }
            }
        }
        return new PageInfo<>(conferenceList);
    }

    @Override
    public YunmeetingConference h5AuditYunmeetingConferenceInfo(String meetingId,String userId) {

        YunmeetingConference conference = null;
        conference = this.yunmeetingConferenceMapper.selectByPrimaryKey(meetingId);
        if(conference != null){
            conference =obtainYunmeetingConferenceInfo(conference,userId);
        }
        return conference;
    }

    @Override
    public MobileMeetingNum selectYunmeetingConferenceNumber() {

        MobileMeetingNum num = new MobileMeetingNum();
        Map maps = new HashedMap();
        List<YunmeetingConference> conferences = new ArrayList<YunmeetingConference>();
        //未通过,已通过
        maps.put("forAndUndue","forAndUndue");
        conferences = this.yunmeetingConferenceMapper.selectAuditYunmeetingConference(maps);
        num.setAlreadyNumber(conferences.size());
        //待审批
        maps.remove("forAndUndue");
        maps.put("centre","centre");
        conferences = this.yunmeetingConferenceMapper.selectAuditYunmeetingConference(maps);
        num.setAgencyNumber(conferences.size());
        return num;
    }
}
