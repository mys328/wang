package com.thinkwin.yunmeeting.weixin.constant;

/*
 * 类说明：该服务中常用的常量配置信息
 * @author lining 2017/11/17
 * @version 1.0
 *
 */
public class WxMpConstant {

    public static final String DB_CORE="0"; //切换Core库

    public static final  String YUNMEETING_DISSOLUTION_COMPANY_PREFIX="yunmeeting_qiyejiesan_status_";

    public static final String YUNMEETING="企云会";

    public static final Integer USER_STOP_STATUS=89; //禁用人员的状态

    public static final Integer MAN=1; //男
    public static final Integer WOMAN=0; //女

    public static final String MAN_ZH="男"; //男
    public static final String WOMAN_ZH="女"; //女

    public static final String SUBSCRIBE="1"; //公众号订阅
    public static final String UNSUBSCRIBE="0"; //公众号取消订阅

    public static final Integer WX_USER_BOUND=1; //公众号绑定
    public static final Integer WX_USER_UNBOUND=0; //公众号未绑定

    public static final Integer SAVE=0;
    public static final Integer UPDATE=1;




    //UserOauth 状态
    public static class UserOauthStatus {
        public static final Integer ENABLE= 1; //启用
        public static final Integer DISABLE=0; //禁用

        UserOauthStatus(){
        }

    }



    //会议信息模板
    public static class MeetingTemplateType {
        public static final String MEETING_NOTICE= "会议通知";
        public static final String MEETING_CANCEL= "会议取消通知";
        public static final String MEETING_REMID= "会议提醒";
        public static final String MEETING_CHANGE= "会议变更通知";
        public static final String MEETING_ATTEND= "会议审核通知";
        public static final String CHANGE_ADMIN= "权限变更提醒";
        public static final String DISSOLUTION_NOTICE="解散通知";

        public MeetingTemplateType() {
        }
    }

    //二维码前缀
    public static class PrefixQrType{

        public static final String QRSCENE= "qrscene_"; //关注
        public static final String QR_TENANT= "qrtenant_"; //租户
        public static final String QR_USER= "qruser_"; //用户

        public PrefixQrType() {
        }

    }





}
