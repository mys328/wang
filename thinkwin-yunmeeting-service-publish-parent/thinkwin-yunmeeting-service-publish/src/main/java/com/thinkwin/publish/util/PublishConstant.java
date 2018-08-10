package com.thinkwin.publish.util;

/*
 * 类说明：
 * @author lining 2018/5/3
 * @version 1.0
 *
 */
public class PublishConstant {

    //发布版本==等于数据库状态2，3
    public static final String releaseStatus="0";

    //内测租户
    public static final String testTenand="0";
    //正式租户
    public static final String formalTenand="1";

    //天气组件Key
    public static final String weatherKey="weather.config";
    //终端背景图片Key
    public static final String terminalBackgrounp="terminal.image.path";

    //终端APP地址
    public static final String terminalAppUrl="terminal.app.url";

    //会议二维码地址
    public static final String meetingSignQrUrl="meetingsign.qrurl";



    //版本状态 初始版本:0;内测版本:1;正式版本:2;历史正式：3
    public static class VerStatus {
        public static final String  INIT="0";
        public static final String ALPHA= "1";
        public static final String RELEASE="2";
        public static final String HISTORY="3";

        public VerStatus(){
        }
    }
}
