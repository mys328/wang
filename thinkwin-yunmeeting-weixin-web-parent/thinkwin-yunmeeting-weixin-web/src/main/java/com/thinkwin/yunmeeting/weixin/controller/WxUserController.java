package com.thinkwin.yunmeeting.weixin.controller;

import com.thinkwin.auth.service.SaasTenantService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.vo.meetingVo.MeetingParticipantsVo;
import com.thinkwin.common.vo.meetingVo.MeetingVo;
import com.thinkwin.yunmeeting.weixin.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LiNing
 *         微信用户
 */
@RestController
@RequestMapping("/wechat/wxUser")
public class WxUserController {

    private final Logger log = LoggerFactory.getLogger(WxUserController.class);


    @Resource
    private UserService userService;
    @Resource
    private SaasTenantService saasTenantService;
    @Resource
    private WeixinService weixinService;
    @Resource
    private WxUserService wxUserService;
    @Resource
    private WxMenuService wxMenuService;
    @Resource
    private WxQrcodeService wxQrcodeService;
    @Resource
    private WxTemplateMsgService wxTemplateMsgService;


    @RequestMapping("/getTest")
    @ResponseBody
    public ResponseResult getTest() {
        ResponseResult responseResult = new ResponseResult();


        MeetingVo meeting = new MeetingVo();
        meeting.setMeetingSubject("007新项目需求评审(测试数据)");
        meeting.setMeetingAddress("文化创意园A区10号楼104");
        meeting.setStart(System.currentTimeMillis());
        List<MeetingParticipantsVo> mpv = new ArrayList<MeetingParticipantsVo>();
        MeetingParticipantsVo p1 = new MeetingParticipantsVo();
        MeetingParticipantsVo p2 = new MeetingParticipantsVo();
        p1.setId("284111b7606a4a4a88ff9ab32d2887c0");
        p2.setId("4d2f63c1dce14a6fa07d3dcdf3de8db8");

        mpv.add(p1);
        mpv.add(p2);
        meeting.setUserIds(mpv);

        //参会通知
        this.wxTemplateMsgService.createMeetingNotice(meeting);
        //会议提醒
        this.wxTemplateMsgService.meetingRemid(meeting);
        //会议取消通知
        this.wxTemplateMsgService.cancelMeetingNotice(meeting);
        //会议变更通知
        this.wxTemplateMsgService.changeMeetingNotice(meeting);
        //会议审核通知（管理员）
        this.wxTemplateMsgService.attendMeetingNotice(meeting, 0);
        //会议审核通知（通过）
        this.wxTemplateMsgService.attendMeetingNotice(meeting, 1);
        //会议审核通知（未通过）
        this.wxTemplateMsgService.attendMeetingNotice(meeting, 2);


        return responseResult;
    }


}
