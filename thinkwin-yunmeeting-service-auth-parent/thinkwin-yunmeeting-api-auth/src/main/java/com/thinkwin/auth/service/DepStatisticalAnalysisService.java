package com.thinkwin.auth.service;

import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.SysOrganization;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.vo.statisticalAnalysisVo.DepStatisticalDataVo;
import com.thinkwin.common.vo.statisticalAnalysisVo.OrganizationalMeetingInfoVo;
import net.sf.json.JSONArray;

import java.util.List;
import java.util.Map;

/**
 * User: yinchunlei
 * Date: 2017/10/24.
 * Company: thinkwin
 * 部门统计service层
 */
public interface DepStatisticalAnalysisService {
    /**
     * 根据开始时间和结束时间统计部门总数和跨部门会议数功能
     * @param startTime
     * @param endTime
     * @return
     */
    public Map getDepTotalNumAndCrossDepartmentMeeting(String startTime, String endTime);

    /**
     * 获取部门统计分析页面数据功能接口
     * @return
     */
    public List<DepStatisticalDataVo> getDepStatisticalData(List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate, BasePageEntity basePageEntity,String hostUnit1);
    /**
     * 获取部门统计分析页面数据功能接口(新)
     * @return
     */
    public DepStatisticalDataVo getDepStatisticalDataNew(List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate, String hostUnit1);

    /**
     * 根据组织会议数量获取相关详细信息
     * @param byMeetingRoomIdAndMeetingtakeStartDate
     * @param orgId
     * @return
     */
    public List<OrganizationalMeetingInfoVo> getOrganizationalMeetingsInfo(List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate,String orgId);

    //递归获取组织机构树
    public JSONArray treeMenuList(JSONArray menuList, String parentId);

    public JSONArray treeMenuList1(JSONArray menuList);

    /**
     * 合并部门统计数据
     * @param hostUnit
     * @param byMeetingRoomIdAndMeetingtakeStartDate
     * @return
     */
    public DepStatisticalDataVo getConsolidatedSectorStatistics(String hostUnit,List<YunmeetingConference>byMeetingRoomIdAndMeetingtakeStartDate,List ll);


    /**
     * 递归获取数据功能接口
     * @return
     */
    public List<String> getNewList(List<SysOrganization> sysOrganizations2, String orgIddd);
}
