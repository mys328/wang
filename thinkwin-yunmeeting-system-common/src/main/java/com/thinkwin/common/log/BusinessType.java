package com.thinkwin.common.log;

/**
 * 业务类型枚举
 */
public enum BusinessType {

    /*
   * 默认类型
   * */
    general, /*
     * 会议
    * */
    meetingOp, /*
    * 登录
    * */
    loginOp,

    /*
    * 注册
    * */
    registerOp,
    /*
    * 企业管理
    * */
    companyOp,
    /*
    * 通讯录
    * */
    contactsOp,
    /*
    * 订单
    * */
    ordersOp,
    /*
    * 统计分析
    * */
    statisticalOp,

    /*
     * 控制台登录
     * */
    loginConsoleOp,
    /*
   * 会议操作
   * */
    meetingOperationOp,
    //终端监控
    terminalMonitorOp,
    //计划开关机
    planSwitchOp,
    //终端注册
    terminalRegisterOp,
    //终端登录
    terminalLoginOp,
    //终端程序
    terminalClientOp;
}
