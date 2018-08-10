package com.thinkwin.web.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.auth.service.DepStatisticalAnalysisService;
import com.thinkwin.auth.service.OrganizationService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.ComparatorDepStatisticalDataVo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.SysOrganization;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.vo.DepStatisticalDataVooo;
import com.thinkwin.common.vo.meetingVo.PersonsVo;
import com.thinkwin.common.vo.statisticalAnalysisVo.DepStatisticalDataVo;
import com.thinkwin.common.vo.statisticalAnalysisVo.OrganizationalMeetingInfoVo;
import com.thinkwin.yuncm.service.MeetingReserveService;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * User: yinchunlei
 * Date: 2017/10/24.
 * Company: thinkwin
 * 部门统计分析controller层
 */
@Controller
public class DepStatisticalAnalysisController {

    @Resource
    private DepStatisticalAnalysisService depStatisticalAnalysisService;
    @Resource
    private MeetingReserveService meetingReserveService;
    /**
     * 跳转到按部门统计分析页面
     * @return
     */
    @RequestMapping("gotoDepStatisticalPage")
    public String gotoDepStatisticalPage(){
        return "";
    }

    /**
     *获取部门总数和跨部门会议数功能接口
     * @return
     */
    @RequestMapping("getDepTotalNumAndCrossDepartmentMeeting")
    @ResponseBody
    public Map getDepTotalNumAndCrossDepartmentMeeting(String startTime, String endTime){
        Map map = new HashMap();
        Map depTotalNumAndCrossDepartmentMeeting = depStatisticalAnalysisService.getDepTotalNumAndCrossDepartmentMeeting(startTime, endTime);
        if(null != depTotalNumAndCrossDepartmentMeeting){
            String errorMessage = (String) depTotalNumAndCrossDepartmentMeeting.get("error");
            if(StringUtils.isNotBlank(errorMessage)){
                map.put("error",errorMessage);
                return map;
            }else {
                Integer orgTotalNum = (Integer) depTotalNumAndCrossDepartmentMeeting.get("orgTotalNum");
                map.put("orgTotalNum",orgTotalNum);//部门总数
                Object crossDepartmentMeeting = depTotalNumAndCrossDepartmentMeeting.get("crossDepartmentMeeting");
                map.put("crossDepartmentMeeting",crossDepartmentMeeting);//获取跨部门会议数量
            }
        }
        return map;
    }

 /*   *//**
     * 获取部门统计分析页面数据功能接口
     * paramType;//排序条件 //默认为0，按照会议时长排序； 按照1，组织会议数量排序；按照2，参会人次排序；按照3，为参会人数排序；
     * sortType;//排序类型 默认为1， 0为升序 1为降序
     * @return
     *//*
    @RequestMapping("getDepStatisticalData")
    @ResponseBody
    public ResponseResult getDepStatisticalData(String startTime, String endTime, BasePageEntity basePageEntity,Integer paramType,Integer sortType,String orgId) {
        if(StringUtils.isBlank(orgId)){
            orgId = "1";
        }
        SysOrganization sysOrganization = organizationService.selectOrganiztionById(orgId);
        Map map = new HashMap();
        //获取某段时间内的所有会议
        List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate = meetingReserveService.findMeetingConferenceByTime(startTime, endTime);
        if(null != byMeetingRoomIdAndMeetingtakeStartDate && byMeetingRoomIdAndMeetingtakeStartDate.size() > 0) {
            List<DepStatisticalDataVo> list = depStatisticalAnalysisService.getDepStatisticalData(byMeetingRoomIdAndMeetingtakeStartDate, basePageEntity, orgId);
            List dataList = new ArrayList();
            ////////////////////////////////////排序功能////////////////////////////////////
            if (null != list && list.size() > 0) {
                if (null != list && list.size() > 1) {
                    if (null == paramType) {
                        paramType = 0;//默认按会议数量排序
                    }
                    if (null == sortType) {
                        sortType = 1;
                    }
                    ComparatorDepStatisticalDataVo comparatorDepStatisticalDataVo = new ComparatorDepStatisticalDataVo(paramType, sortType);
                    Collections.sort(list, comparatorDepStatisticalDataVo);
                }
            }
            ////////////////////////////////////排序功能////////////////////////////////////
            DepStatisticalDataVo depStatisticalDataVo = new DepStatisticalDataVo();
            Integer NumberOfOrganizationalMeetings = 0;//组织会议数
            Integer meetingHours = 0;//会议时长
            Integer numberOfParticipants = 0;//参会人数
            Integer numberOfAbsentParticipants = 0;//未参会人数
            depStatisticalDataVo.setDepName("全部");
             if (null != list && list.size() > 0) {
            for (DepStatisticalDataVo depstatisticelDataVooo : list) {
                if (null != depstatisticelDataVooo) {
                    DepStatisticalDataVooo depVo = new DepStatisticalDataVooo();
                    depVo.setName(depstatisticelDataVooo.getDepName());
                    depVo.setValue(depstatisticelDataVooo.getMeetingHours().toString());
                    dataList.add(depVo);
                }
            }
        }
                        if (null != sysOrganization) {
                            String parentId = sysOrganization.getParentId();
                            if (StringUtils.isNotBlank(parentId) &&"1".equals(parentId)) {
                                if(null != list && list.size() > 0 ) {
                                for (DepStatisticalDataVo depStatisticalDataVoo : list) {
                                    if (null != depStatisticalDataVoo) {
                                        NumberOfOrganizationalMeetings += depStatisticalDataVoo.getNumberOfOrganizationalMeetings();
                                        meetingHours += depStatisticalDataVoo.getMeetingHours();
                                        numberOfParticipants += depStatisticalDataVoo.getNumberOfParticipants();
                                        numberOfAbsentParticipants += depStatisticalDataVoo.getNumberOfAbsentParticipants();
                                    }
                                }
                            }
                            }
                            depStatisticalDataVo.setNumberOfOrganizationalMeetings(NumberOfOrganizationalMeetings);
                            depStatisticalDataVo.setMeetingHours(meetingHours);
                            depStatisticalDataVo.setNumberOfParticipants(numberOfParticipants);
                            depStatisticalDataVo.setNumberOfAbsentParticipants(numberOfAbsentParticipants);
                            if(StringUtils.isNotBlank(orgId) && "1".equals(orgId)) {
                                list.add(0, depStatisticalDataVo);
                            }
                        }else {
                            if (StringUtils.isNotBlank(orgId) && "1".equals(orgId)) {
                                if(null != list && list.size() > 0 ) {
                                for (DepStatisticalDataVo depStatisticalDataVoo : list) {
                                    if (null != depStatisticalDataVoo) {
                                        NumberOfOrganizationalMeetings += depStatisticalDataVoo.getNumberOfOrganizationalMeetings();
                                        meetingHours += depStatisticalDataVoo.getMeetingHours();
                                        numberOfParticipants += depStatisticalDataVoo.getNumberOfParticipants();
                                        numberOfAbsentParticipants += depStatisticalDataVoo.getNumberOfAbsentParticipants();
                                    }
                                }
                            }
                            }
                            depStatisticalDataVo.setNumberOfOrganizationalMeetings(NumberOfOrganizationalMeetings);
                            depStatisticalDataVo.setMeetingHours(meetingHours);
                            depStatisticalDataVo.setNumberOfParticipants(numberOfParticipants);
                            depStatisticalDataVo.setNumberOfAbsentParticipants(numberOfAbsentParticipants);
                            if(StringUtils.isNotBlank(orgId) && "1".equals(orgId)) {
                                list.add(0, depStatisticalDataVo);
                            }
                        }
                int depStatisticalInfoTotal = list.size();
                ////////////////////////////////////////////list分页功能//////////////////////////////////////////
                List<DepStatisticalDataVo> MemberArticleBeanPage = new ArrayList<DepStatisticalDataVo>();
                Integer currentPage = basePageEntity.getCurrentPage();
                Integer pageSize = basePageEntity.getPageSize();
                int currIdx = (currentPage > 1 ? (currentPage -1) * pageSize : 0);
                for (int i = 0; i < pageSize && i < list.size() - currIdx; i++) {
                    DepStatisticalDataVo memberArticleBean = list.get(currIdx + i);
                    MemberArticleBeanPage.add(memberArticleBean);
                }
                /////////////////////////////////////////////list分页功能/////////////////////////////////////
                PageInfo pageInfo = new PageInfo<>();
                pageInfo.setTotal(depStatisticalInfoTotal);
                pageInfo.setList(MemberArticleBeanPage);
                map.put("statisticalAnalysisInfos", pageInfo);//添加分页功能
                map.put("depStatisticalInfoTotalNum",depStatisticalInfoTotal);
            map.put("dataList",dataList);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map, BusinessExceptionStatusEnum.Success.getCode());
    }*/

@Resource
private UserService userService;
    /**
     * 获取部门统计分析页面数据功能接口
     * paramType;//排序条件 //默认为0，按照会议时长排序； 按照1，组织会议数量排序；按照2，参会人次排序；按照3，为参会人数排序；
     * sortType;//排序类型 默认为1， 0为升序 1为降序
     * @return
     */
    @RequestMapping("getDepStatisticalData")
    @ResponseBody
    public ResponseResult getDepStatisticalData(String startTime, String endTime, BasePageEntity basePageEntity,Integer paramType,Integer sortType,String orgId) {
        Map map = new HashMap();
        if(StringUtils.isBlank(orgId)){
            orgId = "1";
        }
        List<SysOrganization> sysOrganizations2 = organizationService.selectOrganiztions();
        List<String> list = new ArrayList();
        for (SysOrganization sysOrganization:sysOrganizations2) {
            if(null != sysOrganization){
                String parentId = sysOrganization.getParentId();
                if(StringUtils.isNotBlank(parentId)){
                    if(orgId.equals(parentId)){
                        list.add(sysOrganization.getId());
                    }
                }
            }
        }
            List<DepStatisticalDataVo> llist = new ArrayList();
            for (String  orgIddd : list) {
                int hasChild = 0;
                for (SysOrganization sysOrganizationn:sysOrganizations2) {
                    String parentId = sysOrganizationn.getParentId();
                    if(orgIddd.equals(parentId)){
                        hasChild = 1;
                        break;
                    }
                }
                List<String> ll = depStatisticalAnalysisService.getNewList(sysOrganizations2,orgIddd);
                //获取某段时间内的所有会议
                List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate = meetingReserveService.findMeetingConferenceByTimeNew(startTime, endTime, ll);
                DepStatisticalDataVo depStatisticalDataVo = depStatisticalAnalysisService.getConsolidatedSectorStatistics(orgIddd,byMeetingRoomIdAndMeetingtakeStartDate,ll);
                if(null != depStatisticalDataVo){
                    depStatisticalDataVo.setHasChild(hasChild);
                    llist.add(depStatisticalDataVo);
                }
            }
            List dataList = new ArrayList();
            ////////////////////////////////////排序功能////////////////////////////////////
                if (null != llist && llist.size() > 1) {
                    if (null == paramType) {
                        paramType = 0;//默认按会议数量排序
                    }
                    if (null == sortType) {
                        sortType = 1;
                    }
                    ComparatorDepStatisticalDataVo comparatorDepStatisticalDataVo = new ComparatorDepStatisticalDataVo(paramType, sortType);
                    Collections.sort(llist, comparatorDepStatisticalDataVo);
                }
            //////////////////////////////////排序功能////////////////////////////////////
            if ("1".equals(orgId)) {
                //获取某段时间内的所有会议
                List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDateee = meetingReserveService.findMeetingConferenceByTime(startTime, endTime);
                DepStatisticalDataVo depStatisticalDataVo111 = new DepStatisticalDataVo();
                Integer meetingHours = 0;//会议时长
                Integer numberOfParticipants = 0;//参会人数
                Integer numberOfAbsentParticipants = 0;//未参会人数
                depStatisticalDataVo111.setDepName("全部");
                List<SysUser> nonParticipantUserInfo = new ArrayList<>();//为参会人集合
                if (null != llist && llist.size() > 0) {
                    for (DepStatisticalDataVo depstatisticelDataVooo : llist) {
                        if (null != depstatisticelDataVooo) {
                            DepStatisticalDataVooo depVo = new DepStatisticalDataVooo();
                            depVo.setName(depstatisticelDataVooo.getDepName());
                            depVo.setValue(depstatisticelDataVooo.getMeetingHours().toString());
                            dataList.add(depVo);
                        }
                    }
                }
                List<String> attendeeList = new ArrayList<>();//参会人id集合
                Integer numberOfOrganizationalMeetingss = 0;//组织会议数
                long meetingHoursTotalNum = 0;
                Integer numberOfParticipantss = 0;//参会人数
                Integer numberOfAbsentParticipantss = 0;//未参会人数
                if (StringUtils.isNotBlank(orgId) &&"1".equals(orgId)) {
                    if(null != byMeetingRoomIdAndMeetingtakeStartDateee && byMeetingRoomIdAndMeetingtakeStartDateee.size() > 0) {
                            for (YunmeetingConference yunmeetingConference : byMeetingRoomIdAndMeetingtakeStartDateee) {
                                if (null != yunmeetingConference) {
                                    Date takeStartDate1 = yunmeetingConference.getTakeStartDate();
                                    Date takeEndDate1 = yunmeetingConference.getTakeEndDate();
                                    meetingHoursTotalNum += takeEndDate1.getTime() - takeStartDate1.getTime();//获取毫秒时间(会议总时长)
                                    //根据会议id获取参会人员
                                    List<PersonsVo> personsVos = meetingReserveService.selectAllParticipantByMeetingId(yunmeetingConference.getId());
                                    if(null != personsVos && personsVos.size() > 0) {
                                        numberOfParticipants += personsVos.size();
                                        for (PersonsVo personsVo:personsVos) {
                                            if(null != personsVo){
                                                String userId = personsVo.getUserId();
                                                if(StringUtils.isNotBlank(userId)){
                                                    if(!attendeeList.contains(userId)){
                                                        attendeeList.add(userId);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        if (null != attendeeList && attendeeList.size() > 0) {
                            List<String> lll = depStatisticalAnalysisService.getNewList(sysOrganizations2,orgId);
                            List<SysUser> sysUsers =userService.getUsersBybatchQuery(lll);
                            if(null != sysUsers && sysUsers.size() > 0) {
                                for (SysUser sysUser:sysUsers) {
                                    if(null != sysUser){
                                        String userId = sysUser.getId();
                                        if(StringUtils.isNotBlank(userId)){
                                            if(!attendeeList.contains(userId)){
                                                numberOfAbsentParticipants += 1;
                                                nonParticipantUserInfo.add(sysUser);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                depStatisticalDataVo111.setNonParticipantUserInfo(nonParticipantUserInfo);
                depStatisticalDataVo111.setNumberOfOrganizationalMeetings(byMeetingRoomIdAndMeetingtakeStartDateee.size());
                depStatisticalDataVo111.setMeetingHours(Integer.parseInt(String.valueOf(meetingHoursTotalNum)));
                depStatisticalDataVo111.setNumberOfParticipants(numberOfParticipants);
                depStatisticalDataVo111.setNumberOfAbsentParticipants(numberOfAbsentParticipants);
                if(StringUtils.isNotBlank(orgId) && "1".equals(orgId)) {
                    llist.add(0, depStatisticalDataVo111);
                }
            }
            int depStatisticalInfoTotal = llist.size();
            ////////////////////////////////////////////list分页功能//////////////////////////////////////////
            List<DepStatisticalDataVo> MemberArticleBeanPage = new ArrayList<DepStatisticalDataVo>();
            Integer currentPage = basePageEntity.getCurrentPage();
            Integer pageSize = basePageEntity.getPageSize();
            int currIdx = (currentPage > 1 ? (currentPage -1) * pageSize : 0);
            for (int i = 0; i < pageSize && i < llist.size() - currIdx; i++) {
                DepStatisticalDataVo memberArticleBean = llist.get(currIdx + i);
                MemberArticleBeanPage.add(memberArticleBean);
            }
            /////////////////////////////////////////////list分页功能/////////////////////////////////////
            PageInfo pageInfo = new PageInfo<>();
            pageInfo.setTotal(depStatisticalInfoTotal);
            pageInfo.setList(MemberArticleBeanPage);
            map.put("statisticalAnalysisInfos", pageInfo);//添加分页功能
            map.put("dataList",dataList);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map, BusinessExceptionStatusEnum.Success.getCode());
    }

    @Resource
    private OrganizationService organizationService;

    /**
     * 根据组织机构id获取部门下的组织会议数
     * @param startTime
     * @param endTime
     * @param orgId
     * @return
     */
    @RequestMapping("/getOrganizationalMeetingsInfo")
    @ResponseBody
    public ResponseResult organizationalMeetingsInfo(String startTime, String endTime,String orgId) {
        if (StringUtils.isBlank(orgId)) {
            orgId = "1";
        }
        SysOrganization sysOrganization = organizationService.selectOrganiztionById(orgId);
        if(null == sysOrganization){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
        }
        String parentId = sysOrganization.getParentId();
        if(StringUtils.isBlank(parentId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
        }
            Map map = new HashMap();
        //获取某段时间内的所有会议
        List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate = meetingReserveService.findMeetingConferenceByTime(startTime, endTime);
        List<OrganizationalMeetingInfoVo> organizationalMeetingInfoVoList = new ArrayList<>();
            if (null != byMeetingRoomIdAndMeetingtakeStartDate && byMeetingRoomIdAndMeetingtakeStartDate.size() > 0) {
               organizationalMeetingInfoVoList = depStatisticalAnalysisService.getOrganizationalMeetingsInfo(byMeetingRoomIdAndMeetingtakeStartDate, orgId);
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), organizationalMeetingInfoVoList, BusinessExceptionStatusEnum.Success.getCode());
        }
}
