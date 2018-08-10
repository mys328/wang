package com.thinkwin.yunmeeting.weixin.timer;

import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.meetingVo.MeetingVo;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.service.TenantContext;import com.thinkwin.yuncm.service.MeetingReserveService;
import com.thinkwin.yunmeeting.weixin.service.WxTemplateMsgService;
import com.thinkwin.yunmeeting.weixin.utils.ContextHolder;

/**
 * 类名: WechatInformTimer </br>
 * 描述:</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/9/7 </br>
 */
public class WechatInformTimer implements Runnable {

    private MeetingVo meetingVo;
    private String messageInformId;
    private String tenantId;

    public void setMeetingVo(MeetingVo meetingVo) {
        this.meetingVo = meetingVo;
    }

    public void setMessageInformId(String messageInformId) {
        this.messageInformId = messageInformId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public void run() {
        String meetingId = meetingVo.getConferenceId();
        MeetingReserveService meetingReserveService = (MeetingReserveService) ContextHolder.getApplicationContext().getBean("meetingReserveService");
        SaasTenantService saasTenantCoreService  = (SaasTenantService) ContextHolder.getApplicationContext().getBean("saasTenantCoreService");
        SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
        if(null != saasTenant && saasTenant.getStatus() != 2) {
            TenantContext.setTenantId(tenantId);
            YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
            String state = yunmeetingConference.getState();
            if (state.equals("5")) {
                RedisUtil.remove(tenantId + "_QYH_Timer_Wechat_wechat_" + messageInformId);
            } else {
                WxTemplateMsgService wxTemplateMsgService = (WxTemplateMsgService) ContextHolder.getApplicationContext().getBean("wxTemplateMsgService");
                wxTemplateMsgService.meetingRemid(meetingVo);
                RedisUtil.remove(tenantId + "_QYH_Timer_Wechat_wechat_" + messageInformId);
            }
        }
    }
}
