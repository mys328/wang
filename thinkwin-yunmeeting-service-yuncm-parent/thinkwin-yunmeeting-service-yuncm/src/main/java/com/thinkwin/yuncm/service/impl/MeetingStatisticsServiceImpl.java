package com.thinkwin.yuncm.service.impl;

import com.thinkwin.auth.service.SaasTenantService;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.YuncmRoomReserveConf;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.model.db.YunmeetingConferenceUserInfo;
import com.thinkwin.common.model.db.YunmeetingParticipantsInfo;
import com.thinkwin.common.vo.meetingVo.MeetingStatisticsVo;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.localservice.LocalMeetingReserveService;
import com.thinkwin.yuncm.mapper.YunmeetingConferenceMapper;
import com.thinkwin.yuncm.service.MeetingStatisticsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 类名: MeetingStatisticsServiceImpl </br>
 * 描述:</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/7/21 </br>
 */
@Service("meetingStatisticsService")
public class MeetingStatisticsServiceImpl implements MeetingStatisticsService {
    @Autowired
    YunmeetingConferenceMapper yunmeetingConferenceMapper;
    @Autowired
    LocalMeetingReserveService localMeetingReserveService;
    @Autowired
    SaasTenantService saasTenantService;


    @Override
    public MeetingStatisticsVo selectMeetingStatics(String userId,String tenantId) {
        MeetingStatisticsVo meetingStatisticsVo = new MeetingStatisticsVo();
        //查询会议室可预订时间设置
        YuncmRoomReserveConf yuncmRoomReserveConf = localMeetingReserveService.selectYuncmRoomReserveConf();
        //查询总会议
        List<YunmeetingConference> yunmeetingConferences = localMeetingReserveService.selectTenantTotalMeeting(null, new Date());
        if (null != yunmeetingConferences) {
            //查询当月会议总时长
            Long meetingTotalDuration = meetingTotalDuration(yunmeetingConferences);
            long reserveTimes = 8 * 60 * 60 * 1000;  //默认8小时
            if (null != yuncmRoomReserveConf) {
                Date reserveTimeStart = yuncmRoomReserveConf.getReserveTimeStart();
                Date reserveTimeEnd = yuncmRoomReserveConf.getReserveTimeEnd();
                SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
                String formatTimeStart = sf.format(reserveTimeStart);
                String formatTimeEnd = sf.format(reserveTimeEnd);
                try {
                    Date parseTimeStart = sf.parse(formatTimeStart);
                    Date parseTimeEnd = sf.parse(formatTimeEnd);
                    reserveTimes = parseTimeEnd.getTime() - parseTimeStart.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //查询当月会议室利用率
                double times = (meetingTotalDuration / 60000.0) / (22 * (reserveTimes / 60000.0));
                //保存会议室利用率
                meetingStatisticsVo.setMeetingRoomUtilization(formatPercent(times));
            }
            //当月总会议数
            meetingStatisticsVo.setMeetingTotalNum(yunmeetingConferences.size());
            //当月会议总时长数
            meetingStatisticsVo.setMeetingTotalTime(formatDateUtil(meetingTotalDuration, true));
            //查询总参会人次
            meetingStatisticsVo.setMeetingTotalPeopleNum(selectMeetingTotalPeopleNum(yunmeetingConferences));
            //个人统计
            //查询当月个人会议总时长
            Long personTotalDuration = selectMeetingPersonTotalDuration(yunmeetingConferences, userId);
            meetingStatisticsVo.setPersonMeetingTotalTime(formatDateUtil(personTotalDuration,false));
            //查询个人预订多少次会议
            Integer personReserveNum = selectPersonReserveMeetingNum(yunmeetingConferences, userId);
            meetingStatisticsVo.setReserveMeetingNum(personReserveNum);
            //查询个人参与多少次会议
            Integer participateMeetingTotalNum = selectPersonParticipateMeetingTotalNum(yunmeetingConferences, userId);
            meetingStatisticsVo.setParticipateMeetingNum(participateMeetingTotalNum);
            //个人会议占比
            double i = (double) (personReserveNum+participateMeetingTotalNum) / yunmeetingConferences.size();
            meetingStatisticsVo.setPersonMeetingProportion(formatPercent(i));
            if(StringUtils.isNotBlank(tenantId)){
                //切换数据源
                TenantContext.setTenantId("0");
                SaasTenant saasTenant = saasTenantService.selectSaasTenantServcie(tenantId);
                //切换数据源
                TenantContext.setTenantId(tenantId);
                if(null!=saasTenant){
                    //计算从企业创建到当前时间总时长
                    Date createTime = saasTenant.getCreateTime();
                    Calendar c1 = Calendar.getInstance();
                    Calendar c2 = Calendar.getInstance();
                    c1.setTime(new Date());
                    c2.setTime(createTime);
                    int i1 = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
                    int monthNum = i1 * 12 + c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
                    if(monthNum==0){
                        monthNum = 1;
                    }
                    //计算从创建企业到当前月
                    long totalDuration = monthNum * 30 * reserveTimes;
                    //查询所有会议
                    List<YunmeetingConference> yunmeetingAllConferences = localMeetingReserveService.selectTenantTotalMeeting(null, null);
                    //查询所有会议总时长
                    long allMeetingTotalDuration = meetingTotalDuration(yunmeetingAllConferences);
                    double timeFloat = (allMeetingTotalDuration / 60000.0) / (totalDuration / 60000.0);
                    //计算已开会议总时长占所有会议总时长百分比
                    meetingStatisticsVo.setMeetingUserOf(formatDouble(timeFloat));
                    //查询所有会议个人总时长
                    Long allPersonTotalDuration = selectMeetingPersonTotalDuration(yunmeetingAllConferences, userId);
                    double personTimeFloat = (allPersonTotalDuration / 60000.0) / (totalDuration / 60000.0);
                    //计算个人已开会议总可预订会议时长数
                    meetingStatisticsVo.setMeetingPersonaLength(formatDouble(personTimeFloat));
                }
            }
        }
        return meetingStatisticsVo;
    }

    /**
     * 方法名：meetingTotalDuration</br>
     * 描述：计算会议总时长数</br>
     * 参数：yunmeetingConferences 总会议实体</br>
     * 返回值：</br>
     */
    private Long meetingTotalDuration(List<YunmeetingConference> yunmeetingConferences) {
        long time = 0;
        for (YunmeetingConference yunmeetingConference : yunmeetingConferences) {
            Date takeEndDate = yunmeetingConference.getTakeEndDate();
            Date takeStartDate = yunmeetingConference.getTakeStartDate();
            time += takeEndDate.getTime() - takeStartDate.getTime();
        }
        return time;
    }

    /**
     * 方法名：selectMeetingTotalPeopleNum</br>
     * 描述：查询总参会人数</br>
     * 参数：yunmeetingConferences 总会议实体</br>
     * 返回值：</br>
     */
    private Integer selectMeetingTotalPeopleNum(List<YunmeetingConference> yunmeetingConferences) {
        int num = 0;
        if (null != yunmeetingConferences && yunmeetingConferences.size() > 0) {
            for (YunmeetingConference yunmeetingConference : yunmeetingConferences) {
                String conferenceId = yunmeetingConference.getId();
                //根据会议Id查询参会人数 不包括人员是组织机构
                List<YunmeetingParticipantsInfo> nums = localMeetingReserveService.selectPersonParticipateMeetingByUserNum(conferenceId, null, "0");
                if (null != nums) {
                    for(YunmeetingParticipantsInfo numss:nums){
                        /*if(numss.getParticipantsId().equals(yunmeetingConference.getReservationPersonId())){
                            continue;
                        }*/
                        num ++;
                    }
                }
                //根据会议Id 查询参会人员 为组织机构的时候
                List<YunmeetingParticipantsInfo> participantsInfos = localMeetingReserveService.selectPersonParticipateMeetingByUserNum(conferenceId, null, "1");
                if (null != participantsInfos) {
                    for (YunmeetingParticipantsInfo participantsInfo : participantsInfos) {
                        String participantsInfoId = participantsInfo.getId();
                        List<YunmeetingConferenceUserInfo> userInfos = localMeetingReserveService.selectPersonParticipateMeetingByOrgNum(participantsInfoId, null);
                        if(null!=userInfos){
                            for(YunmeetingConferenceUserInfo userInfoss:userInfos){
                                /*if(userInfoss.getParticipantsId().equals(yunmeetingConference.getReservationPersonId())){
                                    continue;
                                }*/
                                num++;
                            }

                        }
                    }
                }
            }
        }
        return num;
    }

    /**
     * 方法名：selectMeetingPersonTotalDuration</br>
     * 描述：查询个人当月会议总时长</br>
     * 参数：yunmeetingConferences 总会议实体</br>
     * 参数：userId  用户Id
     * 返回值：</br>
     */
    private Long selectMeetingPersonTotalDuration(List<YunmeetingConference> yunmeetingConferences, String userId) {
        long times = 0;
        if (null != yunmeetingConferences && yunmeetingConferences.size() > 0) {
            for (YunmeetingConference yunmeetingConference : yunmeetingConferences) {
                String conferenceId = yunmeetingConference.getId();
                String reservationPersonId = yunmeetingConference.getReservationPersonId();
                Date takeEndDate = yunmeetingConference.getTakeEndDate();
                Date takeStartDate = yunmeetingConference.getTakeStartDate();
                if(!userId.equals(reservationPersonId)) {
                    //根据会议Id和用户Id查询参会人 不包括人员是组织机构
                    List<YunmeetingParticipantsInfo> participantsInfos = localMeetingReserveService.selectPersonParticipateMeetingByUserNum(conferenceId, userId, "0");
                    if (null != participantsInfos && participantsInfos.size() > 0) {
                        times += takeEndDate.getTime() - takeStartDate.getTime();
                    } else {
                        //根据会议Id 查询参会人员 为组织机构的时候
                        boolean flag = checkPersonIsExits(conferenceId, userId);
                        if (flag) {
                            times += takeEndDate.getTime() - takeStartDate.getTime();
                        }
                    }
                }else{
                    times += takeEndDate.getTime() - takeStartDate.getTime();
                }
            }
        }
        return times;
    }

    /**
     * 方法名：selectPersonReserveMeetingNum</br>
     * 描述：查询个人当月预定会议数</br>
     * 参数：yunmeetingConferences 总会议实体</br>
     * 参数：userId  用户Id
     * 返回值：</br>
     */
    private Integer selectPersonReserveMeetingNum(List<YunmeetingConference> yunmeetingConferences, String userId) {
        int num = 0;
        if (null != yunmeetingConferences && yunmeetingConferences.size() > 0) {
            for (YunmeetingConference yunmeetingConference : yunmeetingConferences) {
                //预订人等于当前用户
                if (userId.equals(yunmeetingConference.getReservationPersonId())) {
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * 方法名：selectPersonParticipateMeetingTotalNum</br>
     * 描述：查询个人当月参与会议数</br>
     * 参数：yunmeetingConferences 总会议实体</br>
     * 参数：userId  用户Id
     * 返回值：</br>
     */
    private Integer selectPersonParticipateMeetingTotalNum(List<YunmeetingConference> yunmeetingConferences, String userId) {
        boolean flag = false;
        int num = 0;
        if (null != yunmeetingConferences && yunmeetingConferences.size() > 0) {
            for (YunmeetingConference yunmeetingConference : yunmeetingConferences) {
                String conferenceId = yunmeetingConference.getId();
                if(!yunmeetingConference.getReservationPersonId().equals(userId)){
                    //根据会议Id和用户Id查询参会人 不包括人员是组织机构
                    List<YunmeetingParticipantsInfo> participantsInfos = localMeetingReserveService.selectPersonParticipateMeetingByUserNum(conferenceId, userId, "0");
                    if (null != participantsInfos && participantsInfos.size() > 0) {
                        num++;
                    } else {
                        //根据会议Id 查询参会人员 为组织机构的时候
                        flag = checkPersonIsExits(conferenceId, userId);
                        if (flag) {
                            num++;
                        }
                    }
                }
            }
        }
        return num;
    }

    public String countPersonMeetingProportion(List<YunmeetingConference> yunmeetingConferences, String userId){
        int num  = 0;
        boolean flag = false;
        if (null != yunmeetingConferences && yunmeetingConferences.size() > 0) {
            for (YunmeetingConference yunmeetingConference:yunmeetingConferences){
                String conferenceId = yunmeetingConference.getId();
                String reservationPersonId = yunmeetingConference.getReservationPersonId();
                if(userId.equals(reservationPersonId)){
                    num++;
                }else{
                    //根据会议Id和用户Id查询参会人 不包括人员是组织机构
                    List<YunmeetingParticipantsInfo> participantsInfos = localMeetingReserveService.selectPersonParticipateMeetingByUserNum(conferenceId, userId, "0");
                    if (null != participantsInfos && participantsInfos.size() > 0) {
                        num++;
                    } else {
                        //根据会议Id 查询参会人员 为组织机构的时候
                        flag = checkPersonIsExits(conferenceId, userId);
                        if (flag) {
                            num++;
                        }
                    }
                }
            }
            double i = num / yunmeetingConferences.size();
            return formatPercent(i);
        }
        return "0%";
    }
    /**
     * 方法名：formatDateUtil</br>
     * 描述：本类用时间格式化</br>
     * 参数：firstMinusSecond 毫秒数</br>
     * 返回值：</br>
     */
    private String formatDateUtil(long firstMinusSecond, boolean isHours) {
        //毫秒转为秒
        long milliSeconds = firstMinusSecond;
        int totalSeconds = (int) (milliSeconds / 1000);
        //得到总天数
        int days = totalSeconds / (3600 * 24);
        int days_remains = totalSeconds % (3600 * 24);
        //得到总小时数
        int hours = totalSeconds / 3600;
        int remains_hours = totalSeconds % 3600;
        if (isHours) {
            return hours + "";
        }
        //得到分种数
        int minutes = remains_hours / 60;
        //得到总秒数
        int seconds = remains_hours % 60;
        return hours + "小时" + minutes + "分钟";
    }

    /**
     * 方法名：checkPersonIsExits</br>
     * 描述：根据会议Id和用户Id 查询该用户是否参与该会议</br>
     * 参数：conferenceId 会议Id</br>
     * 参数：userId 用户Id</br>
     * 返回值：</br>
     */
    private boolean checkPersonIsExits(String conferenceId, String userId) {
        boolean flag = false;
        //根据会议Id 查询参会人员 为组织机构的时候
        List<YunmeetingParticipantsInfo> pInfos = localMeetingReserveService.selectPersonParticipateMeetingByUserNum(conferenceId, null, "1");
        if(null!=pInfos) {
            for (YunmeetingParticipantsInfo pInfo : pInfos) {
                String pInfoId = pInfo.getId();
                List<YunmeetingConferenceUserInfo> userInfos = localMeetingReserveService.selectPersonParticipateMeetingByOrgNum(pInfoId, userId);
                if (null!=userInfos&&userInfos.size() > 0) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 获取百分比
     * @param times
     * @return
     */
    public String formatPercent(double times) {
        //创建BigDecimal对象
        BigDecimal bigLoanAmount = new BigDecimal(times);
        //建立百分比格式化用
        NumberFormat percent = NumberFormat.getPercentInstance();
        //百分比小数点最多3位
        percent.setMaximumFractionDigits(0);
        return percent.format(bigLoanAmount);
    }

    public static double formatDouble(double d) {
        // 新方法，如果不需要四舍五入，可以使用RoundingMode.DOWN
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);
        return bg.doubleValue();
    }

}
