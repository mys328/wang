package com.thinkwin.auth.service.impl;

import com.github.pagehelper.PageHelper;
import com.thinkwin.auth.mapper.db.SysOrganizationMapper;
import com.thinkwin.auth.mapper.db.SysUserMapper;
import com.thinkwin.auth.service.DepStatisticalAnalysisService;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.vo.meetingVo.PersonsVo;
import com.thinkwin.common.vo.statisticalAnalysisVo.DepStatisticalDataVo;
import com.thinkwin.common.vo.statisticalAnalysisVo.OrganizationalMeetingInfoVo;
import com.thinkwin.yuncm.service.MeetingReserveService;
import com.thinkwin.yuncm.service.MeetingStatisticsService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: yinchunlei
 * Date: 2017/10/24.
 * Company: thinkwin
 *
 * 部门统计service层
 */
@Service("depStatisticalAnalysisService")
public class DepStatisticalAnalysisServiceImpl implements DepStatisticalAnalysisService {
    @Autowired
    private SysOrganizationMapper sysOrganizationMapper;
    @Autowired
    private MeetingReserveService meetingReserveService;
    @Autowired
    private SysUserMapper sysUserMapper;


    SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
    /**
     * 根据开始时间和结束时间统计部门总数和跨部门会议数功能
     * @param startTime
     * @param endTime
     * @return
     */
    public Map getDepTotalNumAndCrossDepartmentMeeting(String startTime, String endTime){
        Map map = new HashMap();
  /*      try {
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);
        }catch (Exception e){
            map.put("error","日期类型转化异常");
            return map;
        }*/
        //获取当前用户所在租户下的总人数（先暂定为除移除人员外全部查出来）
        Example example = new Example(SysOrganization.class,true,true);
        Integer orgTotalNum = sysOrganizationMapper.selectCountByExample(example);
        map.put("orgTotalNum",orgTotalNum);
        //获取跨部门会议（crossDepartmentMeeting）
        Integer crossDepartmentMeeting = 0;
        //获取某段时间内的所有会议
        //List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate = meetingReserveService.findByMeetingRoomIdAndMeetingtakeStartDate(null, startTime, endTime);
        List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate = meetingReserveService.findMeetingConferenceByTime(startTime, endTime);
        if(null != byMeetingRoomIdAndMeetingtakeStartDate && byMeetingRoomIdAndMeetingtakeStartDate.size() > 0) {
            for (YunmeetingConference yunmeetingConference: byMeetingRoomIdAndMeetingtakeStartDate) {
                if(null != yunmeetingConference) {
                    String meetingId = yunmeetingConference.getId();
                    String hostUnit = yunmeetingConference.getHostUnit();
                    List list1 = new ArrayList();
                    if(!"1".equals(hostUnit)){
                    if (StringUtils.isNotBlank(meetingId)) {
                        String s = treeMenuListUp(hostUnit);
                        //根据会议id获取参会人员
                        List<PersonsVo> personsVos = meetingReserveService.selectAllParticipantByMeetingId(meetingId);
                        if (null != personsVos && personsVos.size() > 0) {
                            for (PersonsVo personsVo : personsVos) {
                                if (null != personsVo) {
                                    String userId = personsVo.getUserId();
                                    if (StringUtils.isNotBlank(userId)) {
                                        SysUser sysUser = sysUserMapper.selectByPrimaryKey(userId);
                                        if (null != sysUser) {
                                            String orgId = sysUser.getOrgId();
                                            if (StringUtils.isNotBlank(orgId)) {
                                                //此处需要待测试
                                                String s1 = treeMenuListUp(orgId);
                                                if (StringUtils.isNotBlank(s1)) {
                                                    if (StringUtils.isNotBlank(s) && !s.equals(s1) && !"1".equals(s1)) {
                                                        list1.add(orgId);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                    if(null != list1 && list1.size()>0){
                        // 创建新集合
                        ArrayList newArray = new ArrayList();
                        // 遍历旧集合,获取得到每一个元素
                        Iterator it = list1.iterator();
                        while (it.hasNext()) {
                            String s = (String) it.next();
                            // 拿这个元素到新集合去找，看有没有
                            if (!newArray.contains(s)) {
                                newArray.add(s);
                            }
                        }
                        if(null != newArray && newArray.size() > 0){
                            crossDepartmentMeeting += 1;
                        }
                    }
                }
            }
        }
        map.put("crossDepartmentMeeting",crossDepartmentMeeting);
        return map;
    }

    /**
     * 获取部门统计分析页面数据功能接口
     * @return
     */
    public List<DepStatisticalDataVo> getDepStatisticalData(List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate, BasePageEntity basePageEntity,String hostUnit1){
        List<DepStatisticalDataVo> list = new ArrayList<>();
        List<DepStatisticalDataVo> list1 = new ArrayList<>();
        if(null != byMeetingRoomIdAndMeetingtakeStartDate && byMeetingRoomIdAndMeetingtakeStartDate.size() > 0){
            //此处需要根据当前组织机构id获取该组织机构下的子级机构id集合
            ///////////////////////////////////////递归算法//////////////////////////////////////////
            List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
            if(StringUtils.isBlank(hostUnit1)){
                hostUnit1 = "0";
            }
            JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), hostUnit1);
            JSONArray jsonArray1 = treeMenuList1(jsonArray);
            List ll = new ArrayList();
            for (Object jsona:jsonArray1) {
                String s = jsona.toString();
                if(StringUtils.isNotBlank(s)){
                    ll.add(s);
                }
            }
            if(!"1".equals(hostUnit1)) {
                ll.add(hostUnit1);
            }
            ////////////////////////////////////////递归算法/////////////////////////////////////////
            List<String> hostUnitIds = new ArrayList<>();
            for (YunmeetingConference yunmeetingConference:byMeetingRoomIdAndMeetingtakeStartDate) {
                if(null != yunmeetingConference){
                    String hostUnit = yunmeetingConference.getHostUnit();
                    if(StringUtils.isNotBlank(hostUnit)){   //查看新集合中是否有指定的元素，如果没有则加入
                        SysOrganization sysOrganization = sysOrganizationMapper.selectByPrimaryKey(hostUnit);
                        String parentId = null;
                        if(null != sysOrganization){
                           parentId = sysOrganization.getParentId();
                        }
                       if(StringUtils.isNotBlank(hostUnit1)){
                           if(StringUtils.isNotBlank(parentId)){
                               if(hostUnit1.equals(parentId)){
                                   if(!hostUnitIds.contains(hostUnit)) {
                                       hostUnitIds.add(hostUnit);
                                   }
                               }
                           }
                       }
                    }
                }
            }
         //该功能待定  因为需要确定点击某个部门时需不需要获取当前部门下的数据
         if(!"1".equals(hostUnit1) && !hostUnitIds.contains(hostUnit1)) {
                hostUnitIds.add(hostUnit1);
            }
            if(null != hostUnitIds && hostUnitIds.size() > 0){
                for (String hostUnitId:hostUnitIds) {
                    if(StringUtils.isNotBlank(hostUnitId)){
                        DepStatisticalDataVo depStatisticalDataVo = getConsolidatedSectorStatistics(hostUnitId,byMeetingRoomIdAndMeetingtakeStartDate,ll);
                        if(null != depStatisticalDataVo){
                            list.add(depStatisticalDataVo);
                        }
                    }
                }
            }
            if(null != list && list.size() > 0){
                List<String> list2 = new ArrayList<>();
                for (SysOrganization sysOrganization:sysOrganizations2) {
                    if(null != sysOrganization){
                        String parentId = sysOrganization.getParentId();
                        if(StringUtils.isNotBlank(parentId)){
                            if(hostUnit1.equals(parentId)){
                                if(list2.contains(parentId)){
                                    list2.add(parentId);
                                }
                            }
                        }
                    }
                }
                if(null != list2 && list2.size() > 0){
                    for (String orggId:list2) {
                        if(StringUtils.isNotBlank(orggId)) {
                            ////////////////////////////////////////递归算法/////////////////////////////////////////
                            JSONArray jsonArrayy = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), orggId);
                            JSONArray jsonArray1y = treeMenuList1(jsonArrayy);
                            List lly = new ArrayList();
                            for (Object jsona : jsonArray1y) {
                                String s = jsona.toString();
                                if (StringUtils.isNotBlank(s)) {
                                    lly.add(s);
                                }
                            }
                            if (!"1".equals(orggId)) {
                                lly.add(orggId);
                            }
                            ////////////////////////////////////////递归算法/////////////////////////////////////////
                            for (DepStatisticalDataVo dsdv:list) {
                                if(null != dsdv){
                                    String depId = dsdv.getDepId();
                                    if(StringUtils.isNotBlank(depId)){
                                        if(!orggId.equals(depId)){

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return list1;
    }

    /**
     * 获取部门统计分析页面数据功能接口(新)
     * @return
     */
    public DepStatisticalDataVo getDepStatisticalDataNew(List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate, String hostUnit1){
        if(null != byMeetingRoomIdAndMeetingtakeStartDate && byMeetingRoomIdAndMeetingtakeStartDate.size() > 0 && StringUtils.isNotBlank(hostUnit1)) {
            SysOrganization sysOrganization = sysOrganizationMapper.selectByPrimaryKey(hostUnit1);
            if (null != sysOrganization) {
            DepStatisticalDataVo depStatisticalDataVo = new DepStatisticalDataVo();
            depStatisticalDataVo.setDepId(hostUnit1);
            depStatisticalDataVo.setDepName(sysOrganization.getOrgName());
                long meetingHours = 0;//会议时长
                Integer numberOfParticipants = 0;//参会人数
                Integer numberOfAbsentParticipants = 0;//未参会人数
                List<SysUser> nonParticipantUserInfo = new ArrayList<>();//为参会人集合
                depStatisticalDataVo.setNumberOfOrganizationalMeetings(byMeetingRoomIdAndMeetingtakeStartDate.size());//组织会议数
                List<String> attendeeList = new ArrayList<>();//参会人id集合
                for (YunmeetingConference yunmeetingConference:byMeetingRoomIdAndMeetingtakeStartDate) {
                    if(null != yunmeetingConference){
                        String meetingId = yunmeetingConference.getId();
                        if(StringUtils.isNotBlank(meetingId)){
                            //根据会议id获取参会人员
                            List<PersonsVo> personsVos = meetingReserveService.selectAllParticipantByMeetingId(meetingId);
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
                        Date takeStartDate = yunmeetingConference.getTakeStartDate();
                        Date takeEndDate = yunmeetingConference.getTakeEndDate();
                        meetingHours += takeEndDate.getTime() - takeStartDate.getTime();//获取毫秒时间(会议时长)
                    }
                }
                depStatisticalDataVo.setMeetingHours(Integer.valueOf(String.valueOf(meetingHours)));
                depStatisticalDataVo.setNumberOfAbsentParticipants(numberOfAbsentParticipants);
                depStatisticalDataVo.setNumberOfParticipants(numberOfParticipants);
                depStatisticalDataVo.setNonParticipantUserInfo(nonParticipantUserInfo);
            }
        }
        return null;
    }

    @Autowired
    private MeetingStatisticsService meetingStatisticsService;
    /**
     * 合并部门统计数据
     * @param hostUnit
     * @param byMeetingRoomIdAndMeetingtakeStartDate
     * @return
     */
    public DepStatisticalDataVo getConsolidatedSectorStatistics(String hostUnit,List<YunmeetingConference>byMeetingRoomIdAndMeetingtakeStartDate,List ll){
        if(StringUtils.isNotBlank(hostUnit) &&null != byMeetingRoomIdAndMeetingtakeStartDate && byMeetingRoomIdAndMeetingtakeStartDate.size() > 0){
            DepStatisticalDataVo depStatisticalDataVo = new DepStatisticalDataVo();
            Integer numberOfOrganizationalMeetings = 0;//组织会议数
            long meetingHoursTotalNum = 0;
            long meetingHours = 0;//会议时长
            Integer numberOfParticipants = 0;//参会人数
            Integer numberOfAbsentParticipants = 0;//未参会人数
           List<SysUser> nonParticipantUserInfo = new ArrayList();
            String depLengthRatioOfMeetings = null;//会议时长占比
                SysOrganization sysOrganization = sysOrganizationMapper.selectByPrimaryKey(hostUnit);
                if(null != sysOrganization){
                    String orgName = sysOrganization.getOrgName();
                    if(StringUtils.isNotBlank(orgName)){
                        depStatisticalDataVo.setDepName(orgName);
                    }
                }
            depStatisticalDataVo.setDepId(hostUnit);
                List<String> attendeeList = new ArrayList<>();//参会人id集合
            for (YunmeetingConference yunmeetingConferencee:byMeetingRoomIdAndMeetingtakeStartDate) {
                if(null != yunmeetingConferencee){
                    Date takeStartDate1 = yunmeetingConferencee.getTakeStartDate();
                    Date takeEndDate1 = yunmeetingConferencee.getTakeEndDate();
                    meetingHoursTotalNum += takeEndDate1.getTime() - takeStartDate1.getTime();//获取毫秒时间(会议总时长)
                    String hostUnit1 = yunmeetingConferencee.getHostUnit();
                    if(ll.contains(hostUnit1)){
                        numberOfOrganizationalMeetings+=1;//组织会议数
                        Date takeStartDate = yunmeetingConferencee.getTakeStartDate();
                        Date takeEndDate = yunmeetingConferencee.getTakeEndDate();
                        meetingHours += takeEndDate.getTime() - takeStartDate.getTime();//获取毫秒时间(会议时长)
                        //根据会议id获取参会人员
                        List<PersonsVo> personsVos = meetingReserveService.selectAllParticipantByMeetingId(yunmeetingConferencee.getId());
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
            }
            if (null != attendeeList && attendeeList.size() > 0) {
                if(null !=ll && ll.size() > 0){
                List<SysUser> sysUsers = sysUserMapper.batchQuery(ll);
                if (null != sysUsers && sysUsers.size() > 0) {
                    for (SysUser sysUser : sysUsers) {
                        if (null != sysUser) {
                            String userId = sysUser.getId();
                            if (StringUtils.isNotBlank(userId)) {
                                if (!attendeeList.contains(userId)) {
                                    numberOfAbsentParticipants += 1;
                                    nonParticipantUserInfo.add(sysUser);
                                }
                            }
                        }
                    }
                }
            }
            }
            depStatisticalDataVo.setNumberOfOrganizationalMeetings(numberOfOrganizationalMeetings);
            depStatisticalDataVo.setMeetingHours(Integer.valueOf(String.valueOf(meetingHours)));
            depStatisticalDataVo.setNumberOfAbsentParticipants(numberOfAbsentParticipants);
            depStatisticalDataVo.setNumberOfParticipants(numberOfParticipants);
            depStatisticalDataVo.setNonParticipantUserInfo(nonParticipantUserInfo);
            if(meetingHours != 0.0 || meetingHours != 0){
                double l = (double)meetingHours / meetingHoursTotalNum;
                depLengthRatioOfMeetings = meetingStatisticsService.formatPercent(l);
            }else{
                depLengthRatioOfMeetings = "0%";
            }
            depStatisticalDataVo.setDepLengthRatioOfMeetings(depLengthRatioOfMeetings);
            return depStatisticalDataVo;
        }else{
            DepStatisticalDataVo depStatisticalDataVo = new DepStatisticalDataVo();
            Integer numberOfOrganizationalMeetings = 0;//组织会议数
            long meetingHoursTotalNum = 0;
            long meetingHours = 0;//会议时长
            Integer numberOfParticipants = 0;//参会人数
            Integer numberOfAbsentParticipants = 0;//未参会人数
            List<SysUser> nonParticipantUserInfo = new ArrayList();
            String depLengthRatioOfMeetings = null;//会议时长占比
            SysOrganization sysOrganization = sysOrganizationMapper.selectByPrimaryKey(hostUnit);
            if(null != sysOrganization){
                String orgName = sysOrganization.getOrgName();
                if(StringUtils.isNotBlank(orgName)){
                    depStatisticalDataVo.setDepName(orgName);
                }
            }
            depStatisticalDataVo.setDepId(hostUnit);
            List<String> attendeeList = new ArrayList<>();//参会人id集合
            if (null != attendeeList && attendeeList.size() > 0) {
                List<SysUser> sysUsers = sysUserMapper.batchQuery(ll);
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
            depStatisticalDataVo.setNumberOfOrganizationalMeetings(numberOfOrganizationalMeetings);
            depStatisticalDataVo.setMeetingHours(Integer.valueOf(String.valueOf(meetingHours)));
            depStatisticalDataVo.setNumberOfAbsentParticipants(numberOfAbsentParticipants);
            depStatisticalDataVo.setNumberOfParticipants(numberOfParticipants);
            depStatisticalDataVo.setNonParticipantUserInfo(nonParticipantUserInfo);
            if(meetingHours != 0.0 || meetingHours != 0){
                double l = (double)meetingHours / meetingHoursTotalNum;
                depLengthRatioOfMeetings = meetingStatisticsService.formatPercent(l);
            }else{
                depLengthRatioOfMeetings = "0%";
            }
            depStatisticalDataVo.setDepLengthRatioOfMeetings(depLengthRatioOfMeetings);
            return depStatisticalDataVo;
        }
    }

    //递归获取组织机构树(向下递归)
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
    //递归获取组织机构树(向上递归)
    public String treeMenuListUp(String parentId){
        String id = null;
        SysOrganization sysOrganization = sysOrganizationMapper.selectByPrimaryKey(parentId);
        if(null != sysOrganization) {
            String parentId1 = sysOrganization.getParentId();
            String orgId = sysOrganization.getId();
            if (!"1".equals(parentId1) && !"0".equals(parentId1)) {
                id = treeMenuListUp(parentId1);
            }else if("1".equals(parentId1)){
                id = orgId;
            }else {
                id = parentId;
            }
        }
        return id;
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
     * 根据组织会议数量获取相关详细信息
     * @param byMeetingRoomIdAndMeetingtakeStartDate
     * @param orgId
     * @return
     */
    public List<OrganizationalMeetingInfoVo> getOrganizationalMeetingsInfo(List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate, String orgId){
        List<OrganizationalMeetingInfoVo> list = new ArrayList<>();
        if(null != byMeetingRoomIdAndMeetingtakeStartDate && byMeetingRoomIdAndMeetingtakeStartDate.size() > 0) {
            List<YunmeetingConference> yunmeetingConferences = new ArrayList<>();
            //此处需要根据当前组织机构id获取该组织机构下的子级机构id集合
            ///////////////////////////////////////递归算法//////////////////////////////////////////
            List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
            JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), orgId);
            JSONArray jsonArray1 = treeMenuList1(jsonArray);
            List ll = new ArrayList();
            for (Object jsona : jsonArray1) {
                String s = jsona.toString();
                if (StringUtils.isNotBlank(s)) {
                    ll.add(s);
                }
            }
            ll.add(orgId);
            ////////////////////////////////////////递归算法/////////////////////////////////////////
            for (YunmeetingConference yunmeetingConference:byMeetingRoomIdAndMeetingtakeStartDate) {
                if(null != yunmeetingConference){
                    String hostUnit = yunmeetingConference.getHostUnit();
                    if(StringUtils.isNotBlank(hostUnit)){
                        if(ll.contains(hostUnit)){
                            yunmeetingConferences.add(yunmeetingConference);
                        }
                    }
                }
            }
            if(null != yunmeetingConferences && yunmeetingConferences.size() > 0){
                for (YunmeetingConference yunmeeting:yunmeetingConferences) {
                    if(null != yunmeeting) {
                        OrganizationalMeetingInfoVo organizationalMeetingInfoVo = getMeetingNumAggregatedData(yunmeeting);
                        if(null != organizationalMeetingInfoVo){
                            list.add(organizationalMeetingInfoVo);
                        }
                    }
                }

            }
        }
        return list;
    }

    /**
     * 聚合会议室数量中的相关信息功能
     * @return
     */
    public OrganizationalMeetingInfoVo getMeetingNumAggregatedData(YunmeetingConference yunmeeting){
        OrganizationalMeetingInfoVo organizationalMeetingInfoVo = new OrganizationalMeetingInfoVo();
        if(null != yunmeeting){
            Integer numberOfParticipants = 0;//参会人数
            String id = yunmeeting.getId();
            if(StringUtils.isNotBlank(id)) {
                organizationalMeetingInfoVo.setMeetingId(id);
            }
            String conferenceName = yunmeeting.getConferenceName();
            if(StringUtils.isNotBlank(conferenceName)){
                organizationalMeetingInfoVo.setMeetingName(conferenceName);
            }
            String organizerId = yunmeeting.getOrganizerId();
            if(StringUtils.isNotBlank(organizerId)){
                SysUser sysUser = sysUserMapper.selectByPrimaryKey(organizerId);
                if(null != sysUser){
                    organizationalMeetingInfoVo.setOrganizerName(sysUser.getUserName());
                }
                organizationalMeetingInfoVo.setOrganizerId(organizerId);
            }
            Date takeStartDate1 = yunmeeting.getTakeStartDate();
            Date takeEndDate1 = yunmeeting.getTakeEndDate();
            organizationalMeetingInfoVo.setStartTime(takeStartDate1);
            organizationalMeetingInfoVo.setEndTime(takeEndDate1);

            long l = takeEndDate1.getTime() - takeStartDate1.getTime();//获取毫秒时间(会议总时长)
            organizationalMeetingInfoVo.setMeetingHours(Integer.valueOf(String.valueOf(l)));
            //根据会议id获取参会人员
            List<PersonsVo> personsVos = meetingReserveService.selectAllParticipantByMeetingId(yunmeeting.getId());
            if(null != personsVos && personsVos.size() > 0) {
                numberOfParticipants += personsVos.size();
            }
            organizationalMeetingInfoVo.setNumberOfParticipants(numberOfParticipants);
            double SignNum = 0.0;
            double responseRateNum = 0.0;
            List<YunmeetingMeetingSign> yunmeetingMeetingSigns = meetingReserveService.selectSignInfo(id);
            if (null != yunmeetingMeetingSigns && yunmeetingMeetingSigns.size() > 0) {
                SignNum = yunmeetingMeetingSigns.size();
            }
            List<YunmeetingParticipantsReply> yunmeetingParticipantsReplys = meetingReserveService.selectParticipantsReply(id);
            if(null != yunmeetingParticipantsReplys && yunmeetingParticipantsReplys.size() > 0){
                responseRateNum = yunmeetingParticipantsReplys.size();
            }
            if(SignNum != 0.0 || SignNum != 0){
                organizationalMeetingInfoVo.setAttendanceRate(meetingStatisticsService.formatPercent(SignNum / numberOfParticipants ));
            }else{
                organizationalMeetingInfoVo.setAttendanceRate("0%");
            }
            if(responseRateNum != 0.0 || responseRateNum != 0){
                organizationalMeetingInfoVo.setMeetingResponseRate(meetingStatisticsService.formatPercent(responseRateNum / numberOfParticipants ));
            }else{
                organizationalMeetingInfoVo.setMeetingResponseRate("0%");
            }
        }
        return organizationalMeetingInfoVo;
    }

    /**
     * 递归获取数据功能接口
     * @return
     */
    public List<String> getNewList(List<SysOrganization> sysOrganizations2, String orgIddd){
        JSONArray jsonArray = treeMenuList(JSONArray.fromObject(sysOrganizations2), orgIddd);
        JSONArray jsonArray1 =treeMenuList1(jsonArray);
        List ll = new ArrayList();
        for (Object jsona:jsonArray1) {
            String s = jsona.toString();
            if(StringUtils.isNotBlank(s)){
                ll.add(s);
            }
        }
        ll.add(orgIddd);
        return ll;
    }

}
