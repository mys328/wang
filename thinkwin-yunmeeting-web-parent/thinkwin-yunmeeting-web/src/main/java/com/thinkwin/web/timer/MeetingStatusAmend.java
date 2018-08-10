package com.thinkwin.web.timer;

import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.web.timer.Utils.ContextHolder;
import com.thinkwin.yuncm.service.MeetingReserveService;

/**
 * 类名: MeetingStatusAmend </br>
 * 描述:定时修改会议状态定时器</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/30 </br>
 */
public class MeetingStatusAmend implements Runnable {

    private String meetingId;

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    private String tenantId;
    private String state;

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public void run() {
        MeetingReserveService meetingReserveService = (MeetingReserveService) ContextHolder.getApplicationContext().getBean("meetingReserveService");
        SaasTenantService saasTenantCoreService  = (SaasTenantService) ContextHolder.getApplicationContext().getBean("saasTenantCoreService");
        SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
        if(null != saasTenant && saasTenant.getStatus() != 2) {
            TenantContext.setTenantId(tenantId);
            YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
            String state = yunmeetingConference.getState();
            if (state.equals("2") || state.equals("3")) {
                meetingReserveService.updateMeetingStatus(meetingId, this.state);
            }
            //任务Id存到Redis里面
            if (this.state.equals("3")) {
                RedisUtil.remove(tenantId + "_QYH_Timer_Meeting_" + meetingId + "3");
            } else {
                RedisUtil.remove(tenantId + "_QYH_Timer_Meeting_" + meetingId + "4");
            }
        }
    }
}
