package com.thinkwin.yunmeeting.weixin.service.impl;

import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.model.db.SysWechatTemplate;
import com.thinkwin.common.vo.ChangeAdminVo;
import com.thinkwin.common.vo.meetingVo.MeetingParticipantsVo;
import com.thinkwin.common.vo.meetingVo.MeetingVo;
import com.thinkwin.yunmeeting.weixin.config.ThinkWinConfig;
import com.thinkwin.yunmeeting.weixin.constant.WxMpConstant;
import com.thinkwin.yunmeeting.weixin.mapper.db.SysWechatTemplateMapper;
import com.thinkwin.yunmeeting.weixin.service.WxTemplateMsgService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
 * 类说明：消息模块Service
 * @author lining 2017/7/5
 * @version 1.0
 *
 */
@Service("wxTemplateMsgService")
public class WxTemplateMsgServiceImpl implements WxTemplateMsgService {
    private static Logger logger = LoggerFactory.getLogger(WxTemplateMsgServiceImpl.class);



    @Autowired
    private ThinkWinConfig twConfig;
    @Autowired
    private WxMpService wxService;
    @Autowired
    private SysWechatTemplateMapper sysWechatTemplateMapper;
    @Resource(name="yunUserService")
    private UserService userService;
    @Resource
    private LoginRegisterService loginRegisterService;

    //模式化日期格式
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * 创建会议-参会通知
     *
     * @param meetingVo 会议
     * @return
     */
    @Override
    public boolean createMeetingNotice(MeetingVo meetingVo) {

        SysWechatTemplate temp=new SysWechatTemplate();
        temp.setTitle(WxMpConstant.MeetingTemplateType.MEETING_NOTICE);
        SysWechatTemplate sysWechatTemplate=this.sysWechatTemplateMapper.selectOne(temp);
        String templateId=sysWechatTemplate.getTemplateId();

        try {
            logger.info("会议内容：{} ====== {} ====={} ====== {} ======{}",meetingVo.getMeetingSubject(),dateFormat.format(meetingVo.getStart()),meetingVo.getMeetingAddress(),meetingVo.getMeetingContent());
            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().templateId(templateId).build();
            WxMpTemplateData first = new WxMpTemplateData("first", "您好，您有需参加的会议", "#FF00FF");
            WxMpTemplateData keyword1 = new WxMpTemplateData("keyword1", meetingVo.getMeetingSubject());
            WxMpTemplateData keyword2 = new WxMpTemplateData("keyword2", dateFormat.format(meetingVo.getStart()));
            WxMpTemplateData keyword3 = new WxMpTemplateData("keyword3", meetingVo.getMeetingAddress());
            WxMpTemplateData keyword4 = new WxMpTemplateData("keyword4", meetingVo.getMeetingContent());
            WxMpTemplateData remark = new WxMpTemplateData("remark", "请准时到会，不要迟到");
            templateMessage.addWxMpTemplateData(first);
            templateMessage.addWxMpTemplateData(keyword1);
            templateMessage.addWxMpTemplateData(keyword2);
            templateMessage.addWxMpTemplateData(keyword3);
            templateMessage.addWxMpTemplateData(keyword4);
            templateMessage.addWxMpTemplateData(remark);
            //templateMessage.setUrl();

            //轮询发送微信信息
            for (MeetingParticipantsVo mp : meetingVo.getUserIds()) {
                SysUser sysUser=this.userService.selectUserByUserId(mp.getId());
                if (null != sysUser && StringUtils.isNotBlank(sysUser.getOpenId()) && StringUtils.isNotBlank(sysUser.getIsSubscribe()) && "1".equals(sysUser.getIsSubscribe())) {
                    templateMessage.setToUser(sysUser.getOpenId());
                    templateMessage.setUrl(twConfig.getHttpServer()+"?tenantId="+sysUser.getTenantId()+"&userId="+sysUser.getId()+"&openId="+sysUser.getOpenId()+"&meetingId="+meetingVo.getConferenceId());

                    String msgId=send(templateMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 会议变更通知
     *
     * @param meetingVo 会议
     * @return
     */
    @Override
    public boolean changeMeetingNotice(MeetingVo meetingVo) {

        SysWechatTemplate temp=new SysWechatTemplate();
        temp.setTitle(WxMpConstant.MeetingTemplateType.MEETING_CHANGE);
        SysWechatTemplate sysWechatTemplate=this.sysWechatTemplateMapper.selectOne(temp);
        String templateId=sysWechatTemplate.getTemplateId();

        try {

            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().templateId(templateId).build();
            WxMpTemplateData first = new WxMpTemplateData("first", "您有一条会议变更，请您留意", "#FF00FF");
            WxMpTemplateData keyword1 = new WxMpTemplateData("keyword1", meetingVo.getMeetingSubject());
            WxMpTemplateData keyword2 = new WxMpTemplateData("keyword2", dateFormat.format(meetingVo.getStart()));
            WxMpTemplateData keyword3 = new WxMpTemplateData("keyword3", meetingVo.getMeetingAddress());
            WxMpTemplateData remark = new WxMpTemplateData("remark", "您好，您参加的" + meetingVo.getMeetingSubject() + "会议变更，请以上述最新会议内容为准");
            templateMessage.addWxMpTemplateData(first);
            templateMessage.addWxMpTemplateData(keyword1);
            templateMessage.addWxMpTemplateData(keyword2);
            templateMessage.addWxMpTemplateData(keyword3);
            templateMessage.addWxMpTemplateData(remark);

            //轮询发送微信信息
            for (MeetingParticipantsVo mp : meetingVo.getUserIds()) {
                SysUser sysUser=this.userService.selectUserByUserId(mp.getId());
                if (null != sysUser && StringUtils.isNotBlank(sysUser.getOpenId()) && StringUtils.isNotBlank(sysUser.getIsSubscribe()) && "1".equals(sysUser.getIsSubscribe())) {
                    templateMessage.setToUser(sysUser.getOpenId());
                    String msgId=send(templateMessage);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 会议取消通知
     * param meetingVo 会议
     *
     * @param meetingVo
     * @return
     */
    @Override
    public boolean cancelMeetingNotice(MeetingVo meetingVo) {

        SysWechatTemplate temp=new SysWechatTemplate();
        temp.setTitle(WxMpConstant.MeetingTemplateType.MEETING_CANCEL);
        SysWechatTemplate sysWechatTemplate=this.sysWechatTemplateMapper.selectOne(temp);
        String templateId=sysWechatTemplate.getTemplateId();

        try {

            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().templateId(templateId).build();
            WxMpTemplateData first = new WxMpTemplateData("first", "您有一条会议取消通知", "#FF00FF");
            WxMpTemplateData keyword1 = new WxMpTemplateData("keyword1", meetingVo.getMeetingSubject());
            WxMpTemplateData keyword2 = new WxMpTemplateData("keyword2", dateFormat.format(meetingVo.getStart()));
            WxMpTemplateData keyword3 = new WxMpTemplateData("keyword3",meetingVo.getCancelReason());
            WxMpTemplateData remark = new WxMpTemplateData("remark", "请注意，" + meetingVo.getMeetingSubject() + "临时变动，会议已经取消，请调整您的计划");
            templateMessage.addWxMpTemplateData(first);
            templateMessage.addWxMpTemplateData(keyword1);
            templateMessage.addWxMpTemplateData(keyword2);
            templateMessage.addWxMpTemplateData(keyword3);
            templateMessage.addWxMpTemplateData(remark);

            //轮询发送微信信息
            for (MeetingParticipantsVo mp : meetingVo.getUserIds()) {
                SysUser sysUser=this.userService.selectUserByUserId(mp.getId());
                if (null != sysUser && StringUtils.isNotBlank(sysUser.getOpenId()) && StringUtils.isNotBlank(sysUser.getIsSubscribe()) && "1".equals(sysUser.getIsSubscribe())) {
                    templateMessage.setToUser(sysUser.getOpenId());
                    String msgId=send(templateMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 会前会议提醒
     * param meetingVo 会议
     *
     * @param meetingVo
     * @return
     */
    @Override
    public boolean meetingRemid(MeetingVo meetingVo) {

        SysWechatTemplate temp=new SysWechatTemplate();
        temp.setTitle(WxMpConstant.MeetingTemplateType.MEETING_REMID);
        SysWechatTemplate sysWechatTemplate=this.sysWechatTemplateMapper.selectOne(temp);
        String templateId=sysWechatTemplate.getTemplateId();
        try {

            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().templateId(templateId).build();
            WxMpTemplateData first = new WxMpTemplateData("first", "您有一条会议提醒", "#FF00FF");
            WxMpTemplateData keyword1 = new WxMpTemplateData("keyword1", meetingVo.getMeetingSubject());
            WxMpTemplateData keyword2 = new WxMpTemplateData("keyword2", dateFormat.format(meetingVo.getStart()));
            WxMpTemplateData keyword3 = new WxMpTemplateData("keyword3", meetingVo.getMeetingAddress());
            WxMpTemplateData remark = new WxMpTemplateData("remark", "请准时到会，不要迟到");
            templateMessage.addWxMpTemplateData(first);
            templateMessage.addWxMpTemplateData(keyword1);
            templateMessage.addWxMpTemplateData(keyword2);
            templateMessage.addWxMpTemplateData(keyword3);
            templateMessage.addWxMpTemplateData(remark);

            //轮询发送微信信息
            for (MeetingParticipantsVo mp : meetingVo.getUserIds()) {
                SysUser sysUser=this.userService.selectUserByUserId(mp.getId());
                if (null != sysUser && StringUtils.isNotBlank(sysUser.getOpenId()) && StringUtils.isNotBlank(sysUser.getIsSubscribe()) && "1".equals(sysUser.getIsSubscribe())) {
                    templateMessage.setToUser(sysUser.getOpenId());
                    String msgId=send(templateMessage);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 会议审核通知
     * param meetingVo 会议
     *
     * @param meetingVo
     * @return
     */
    @Override
    public boolean attendMeetingNotice(MeetingVo meetingVo, int type) {

        SysWechatTemplate temp=new SysWechatTemplate();
        temp.setTitle(WxMpConstant.MeetingTemplateType.MEETING_ATTEND);
        SysWechatTemplate sysWechatTemplate=this.sysWechatTemplateMapper.selectOne(temp);
        String templateId=sysWechatTemplate.getTemplateId();
        try {

            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().templateId(templateId).build();
            WxMpTemplateData first = new WxMpTemplateData("first", "您有一条会议审核通知", "#FF00FF");
            WxMpTemplateData keyword1 = new WxMpTemplateData("keyword1", meetingVo.getMeetingSubject());
            WxMpTemplateData keyword2 = new WxMpTemplateData("keyword2", dateFormat.format(meetingVo.getStart()));
            WxMpTemplateData keyword3 = new WxMpTemplateData("keyword3", meetingVo.getMeetingAddress());
            templateMessage.addWxMpTemplateData(first);
            templateMessage.addWxMpTemplateData(keyword1);
            templateMessage.addWxMpTemplateData(keyword2);
            templateMessage.addWxMpTemplateData(keyword3);

            if (0 == type) {
                WxMpTemplateData remark = new WxMpTemplateData("remark", "有您需审核的会议，等待您审批：" + meetingVo.getMeetingSubject());
                templateMessage.addWxMpTemplateData(remark);
            } else if (1 == type) {
                WxMpTemplateData remark = new WxMpTemplateData("remark", "您预订的" + meetingVo.getMeetingSubject() + "已经审核通过");
                templateMessage.addWxMpTemplateData(remark);
            } else if (2 == type) {
                WxMpTemplateData remark = new WxMpTemplateData("remark", "您预订的" + meetingVo.getMeetingSubject() + "审核未通过");
                templateMessage.addWxMpTemplateData(remark);
            }

            //轮询发送微信信息
            for (MeetingParticipantsVo mp : meetingVo.getUserIds()) {
                SysUser sysUser=this.userService.selectUserByUserId(mp.getId());
                if (null != sysUser && StringUtils.isNotBlank(sysUser.getOpenId()) && StringUtils.isNotBlank(sysUser.getIsSubscribe()) && "1".equals(sysUser.getIsSubscribe())) {
                    templateMessage.setToUser(sysUser.getOpenId());
                   /* if (0 == type) {
                        templateMessage.setUrl(twConfig.getHttpServer() + "/#/app/meeting/meeting?tenendId=" + sysUser.getTenantId() + "&userId=" + sysUser.getId() + "&openId=" + sysUser.getOpenId() + "&meetingId=" + meetingVo.getConferenceId());
                    }*/
                    String msgId=send(templateMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 会前会议提醒
     * param meetingVo 会议
     *
     * @param
     * @return
     */
    @Override
    public boolean changeAdmin(ChangeAdminVo changeAdminVo) {
        SysWechatTemplate temp=new SysWechatTemplate();
        temp.setTitle(WxMpConstant.MeetingTemplateType.CHANGE_ADMIN);
        SysWechatTemplate sysWechatTemplate=this.sysWechatTemplateMapper.selectOne(temp);
        String templateId=sysWechatTemplate.getTemplateId();
        try {

            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().templateId(templateId).build();
            WxMpTemplateData first = new WxMpTemplateData("first", "您有一条权限变更提醒", "#FF00FF");
            WxMpTemplateData keyword1 = new WxMpTemplateData("keyword1", "权限变更");
            WxMpTemplateData keyword2 = new WxMpTemplateData("keyword2", changeAdminVo.getTenantName()+"的企业管理员：由"+changeAdminVo.getOldAdmin().getUserName()+"变更为"+changeAdminVo.getNewAdmin().getUserName());

            WxMpTemplateData remark = new WxMpTemplateData("remark", "");
            templateMessage.addWxMpTemplateData(first);
            templateMessage.addWxMpTemplateData(keyword1);
            templateMessage.addWxMpTemplateData(keyword2);
            templateMessage.addWxMpTemplateData(remark);

            //旧管理发送信息
            SysUser old=changeAdminVo.getOldAdmin();
            if (null != old && StringUtils.isNotBlank(old.getOpenId()) && StringUtils.isNotBlank(old.getIsSubscribe()) && "1".equals(old.getIsSubscribe())) {
                templateMessage.setToUser(old.getOpenId());
                String msgId = send(templateMessage);
            }

            //新管理发送信息
            SysUser newAdmin=changeAdminVo.getNewAdmin();
            if (null != newAdmin && StringUtils.isNotBlank(newAdmin.getOpenId()) && StringUtils.isNotBlank(newAdmin.getIsSubscribe()) && "1".equals(newAdmin.getIsSubscribe())) {
                templateMessage.setToUser(newAdmin.getOpenId());
                String msgId = send(templateMessage);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;


    }

    /**
     * 解散企业
     * @param sysUser 当用启用
     * @param saasTenant 租户
     * @return
     */
    @Override
    public boolean dissolutionCompanyNotice(SysUser sysUser, SaasTenant saasTenant) {

        SysWechatTemplate temp=new SysWechatTemplate();
        temp.setTitle(WxMpConstant.MeetingTemplateType.DISSOLUTION_NOTICE);
        SysWechatTemplate sysWechatTemplate=this.sysWechatTemplateMapper.selectOne(temp);
        if(null == sysWechatTemplate){
            return false;
        }
        String templateId=sysWechatTemplate.getTemplateId();
        try {

            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().templateId(templateId).build();
            WxMpTemplateData first = new WxMpTemplateData("first", "您好，您有一个解散通知", "#FF00FF");
            WxMpTemplateData keyword1 = new WxMpTemplateData("keyword1", saasTenant.getTenantName());
            WxMpTemplateData keyword2 = new WxMpTemplateData("keyword2", dateFormat.format(new Date()));
            WxMpTemplateData keyword3 = new WxMpTemplateData("keyword3", "企业管理员"+sysUser.getUserName()+",解散企云会的"+saasTenant.getTenantName()+"企业");
            templateMessage.addWxMpTemplateData(first);
            templateMessage.addWxMpTemplateData(keyword1);
            templateMessage.addWxMpTemplateData(keyword2);
            templateMessage.addWxMpTemplateData(keyword3);

            WxMpTemplateData remark = new WxMpTemplateData("remark", "请知晓");
            templateMessage.addWxMpTemplateData(remark);


            //轮询发送微信信息
            List<SysUser> users=this.userService.findALL();
            for (SysUser u:users) {

                if ( StringUtils.isNotBlank(u.getOpenId()) && "1".equals(u.getIsSubscribe())) {
                    templateMessage.setToUser(u.getOpenId());

                    String msgId=send(templateMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 异步发送模板信息
     * @param templateMessage
     * @return
     */
    @Async
    public String send(WxMpTemplateMessage templateMessage){
        try{
            String msgId = this.wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            return msgId;
        }catch(WxErrorException e){
            e.printStackTrace();
        }
        return null;
    }
}
