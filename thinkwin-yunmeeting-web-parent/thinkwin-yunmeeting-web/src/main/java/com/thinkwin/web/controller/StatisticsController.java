package com.thinkwin.web.controller;

import com.thinkwin.auth.service.DepStatisticalAnalysisService;
import com.thinkwin.auth.service.UserStatisticalAnalysisService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.YuncmRoomArea;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.Sublist;
import com.thinkwin.common.vo.meetingVo.MeetingRoomStatisticsVo;
import com.thinkwin.common.vo.meetingVo.MeetingStatisticsDetails;
import com.thinkwin.common.vo.meetingVo.StatisticsRoomVo;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.MeetingRoomStatisticsService;
import com.thinkwin.yuncm.service.YuncmMeetingService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计controller
 */
@RequestMapping("/meetingStatistics")
@Controller
public class StatisticsController {

    @Resource
    private MeetingRoomStatisticsService meetingRoomStatisticsService;
    @Resource
    private UserStatisticalAnalysisService userStatisticalAnalysisService;
    @Resource
    private DepStatisticalAnalysisService depStatisticalAnalysisService;
    @Resource
    YuncmMeetingService yuncmMeetingService;
    @Resource
    private SaasTenantService saasTenantCoreService;

    /**
     * 跳转到按人员统计分析页面
     * @return
     */
    @RequestMapping(value = "gotoMeetingRoomStatisticsPage",method = RequestMethod.POST)
    public String gotoMeetingRoomStatisticsPage(){
        return "";
    }




    /**
     * 统计首页
     * @param staTime 开始时间
     * @param endTime 结束时间；
     * @return
     */
    @RequestMapping(value = "/getStatisticsDetails",method = RequestMethod.POST)
    @ResponseBody
    public Object getStatisticsDetails(String staTime,String endTime){
        staTime = staTime +" 00:01:00";
        endTime = endTime +" 23:59:00";
        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
        Map<String,Object> map = this.meetingRoomStatisticsService.selectMeetingRoomStatistics(staTime,endTime);
        //获取人员统计
        Map umap = userStatisticalAnalysisService.getUserTotalNumAndNumberOfAbsentParticipants(staTime,endTime);
        //获取部门统计
        Map dmap = depStatisticalAnalysisService.getDepTotalNumAndCrossDepartmentMeeting(staTime, endTime);
        //获取会议室区域
        List<YuncmRoomArea> areas = this.yuncmMeetingService.selectAllListYuncmRoomArea();
        map.put("userTotalNum",umap.get("userTotalNum"));
        map.put("numberOfAbsentParticipants",umap.get("numberOfAbsentParticipants"));
        map.put("orgTotalNum",dmap.get("orgTotalNum"));
        map.put("crossDepartmentMeeting",dmap.get("crossDepartmentMeeting"));
        map.put("area",areas);
        map.put("jurisdiction",saasTenant.getTenantType());
        /*if(StringUtils.isNotBlank(saasTenant.getBasePackageType())){
           if("1000".equals(saasTenant.getBasePackageType())){
               map.put("jurisdiction","0");
           }else {
               map.put("jurisdiction","1");
           }
        }else {
            map.put("jurisdiction","0");
        }*/
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map, BusinessExceptionStatusEnum.Success.getCode());

    }


    /**
     * 会议室统计详情
     * @param staTime 开始时间
     * @param endTime 结束时间
     * @param areaId 区域id
     * @param searchKey 搜索关键字
     * @param isAudit 是否审核
     * @param type    排序：1：会议数量，2：使用时长，3：闲置时长，4：使用率，5：未通过会议数量  默认会议数量排序
     * @return
     */
    @RequestMapping(value = "/getMeetingRoomStatisticsDetails",method = RequestMethod.POST)
    @ResponseBody
    public Object getMeetingRoomStatisticsDetails(String staTime,String endTime,String areaId,String searchKey,String isAudit,String type,BasePageEntity page){
        staTime = staTime +" 00:01:00";
        endTime = endTime +" 23:59:00";
        List<MeetingRoomStatisticsVo> roomStatisticsVos = new ArrayList<MeetingRoomStatisticsVo>();
        List<StatisticsRoomVo> vos = new ArrayList<StatisticsRoomVo>(); //环形图
        List<String> xAxisData = new ArrayList<String>();//柱状图 会议室名称
        List<Long> durationList = new ArrayList<Long>();//使用时长
        List<String> percentList = new ArrayList<String>();//使用率
        Map<String,Object> map = new HashMap();
        Map<String,Object> maps = this.meetingRoomStatisticsService.selectMeetingRoomStatisticsDetails(staTime,endTime,areaId,searchKey,isAudit,type,page);
        if(!StringUtils.isNotBlank(type)) {
            //获取图表信息
            for (MeetingRoomStatisticsVo statisticsVo : (List<MeetingRoomStatisticsVo>)maps.get("statistics")) {
                if (!"全部".equals(statisticsVo.getName())) {
                    StatisticsRoomVo vo = new StatisticsRoomVo();
                    vo.setName(statisticsVo.getName());
                    vo.setValue(statisticsVo.getMeetingTotalNum()+"");
                    xAxisData.add(statisticsVo.getName());
                    durationList.add(statisticsVo.getMeetingUseTime());
                    String result = String.format("%.1f", statisticsVo.getNum());
                    percentList.add(result);
                    vos.add(vo);
                }
            }
        }
        map.put("total",maps.get("total"));
        map.put("dataList",vos);
        map.put("xAxisData",xAxisData);
        map.put("durationList",durationList);
        map.put("percentList",percentList);
        //获取会议室区域
        List<YuncmRoomArea> areas = this.yuncmMeetingService.selectAllListYuncmRoomArea();
        map.put("statistics", (List<MeetingRoomStatisticsVo>)maps.get("statistics"));
        map.put("area",areas);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map, BusinessExceptionStatusEnum.Success.getCode());

    }


    /**
     * 点击会议数量获取详情
     * @param roomId 会议室id
     * @param type 0查看未审核。4.已结束的
     * @param staTime
     * @param endTime
     * @return
     */
    @RequestMapping(value = "/getMeetingDetails",method = RequestMethod.POST)
    @ResponseBody
    public Object getMeetingDetails(String roomId,String type,String staTime, String endTime, BasePageEntity page){
        staTime = staTime +" 00:01:00";
        endTime = endTime +" 23:59:00";
        List<MeetingStatisticsDetails> meetingDetails = this.meetingRoomStatisticsService.selectMeetingDetails(roomId, type, staTime, endTime,page);

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), meetingDetails, BusinessExceptionStatusEnum.Success.getCode());

    }







}
