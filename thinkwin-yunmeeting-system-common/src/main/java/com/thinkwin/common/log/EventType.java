package com.thinkwin.common.log;

/**
 * 事件类型枚举
 */
public enum EventType {
    /*
    * 默认类型
    * */
    general,
    /*
    * 业务类型
    * */

    //平台注册
    platform_register,
    //平台登成
    platform_login,
    //忘记密码获取
    forget_password_access,

    //控制台登录
    console_login,

    //会议预定
    meeting_reservation,
    //取消预定
    cancel_reservation,
    //修改会议
    update_meeting,
    //审核会议
    audit_meeting,

    //公司信息保存
    company_information,
    //公司信息保存成功
    //company_information_success,
    //公司信息保存失败
   //company_information_fail,
    //变更主管理员
    change_admin,
    //变更主管理员成功
    //change_admin_success,
    //变更主管理员失败
    //change_admin_fail,
    //子管理员设置
    child_admin_settings,
    //会议室管理员设置
    meeting_room_admin_settings,
    //会议室预定专员设置
    meeting_room_specialist_settings,


    //添加部门
    add_depart,
    //添加部门成功
//    add_depart_success,
    //添加部门失败
//    add_depart_fail,
    //修改部门
    update_depart,
    //修改部门成功
//    update_depart_success,
    //修改部门失败
//    update_depart_fail,
    //删除部门
    del_depart,
    //删除部门成功
//    del_depart_success,
    //删除部门失败
//    del_depart_fail,

    //添加人员
    add_person,
    //添加人员成功
//    add_person_success,
    //添加人员失败
//    add_person_fail,
    //修改人员
    update_person,
    //修改人员成功
//    update_person_success,
    //修改人员失败
//    update_person_fail,
    //删除人员,
    del_person,
    //删除人员成功
//    del_person_success,
    //删除人员失败
//    del_person_fail,
    //批量导入
    batch_import,

    //添加会议室
    add_meeting_room,
    //修改会议室
    update_meeting_room,
    //删除会议室
    del_meeting_room,
    //del_meeting_fail,
    //会议室设置
    meeting_settins,
    //会议室设置成功
//    meeting_settins_success,
    //会议室设置失败
//    meeting_settins_fail,
    //打印二维码
    print_code,
    //打印二维码成功
//    print_code_success,
    //打印二维码失败
//    print_code_fail,
    //会议室区域
    meeting_room_area,

    //创建订单
    add_orders,
    //创建订单成功
//    add_orders_success,
    //创建订单失败
//    add_orders_fail,
    //订单支付
    orders_pay,
    //订单支付成功
//    orders_pay_success,
    //订单支付失败
//    orders_pay_fail,
    //取消订单
    cancel_order,
    //索取发票
    invoice_claim,
    //索取发票成功
    //invoice_claim_fail;

    //企业解散
    company_dissolution,
    //发布节目
    publish_program,
    //删除节目
    delete_program,
    //插播消息
    add_msg,
    //删除消息
    delete_msg,
    //版本更新
    version_upgrade,
    //修改声音亮度
    update_voiceLight,
    //获取实时画面
    get_screenshot,
    //远程重启
    remote_reboot,
    //会议变更
    meeting_update,
    //启动任务
    start_task,
    //停止任务
    stop_task,
    //修改任务
    update_task,
    //删除任务
    delete_task,
    //终端注册
    terminal_register,
    //终端登录
    terminal_login,
    //应用异常
    client_system_fail,
    //终端发布节目
    client_publish_program,
    //终端获取节目数据
    client_get_program,
    //终端更新版本
    client_version_upgrade,
    //终端插播消息
    client_add_msg,
    //终端删除消息
    client_delete_msg,
    //终端修改声音亮度
    client_update_voiceLight,
    //终端获取实时画面
    client_get_screenshot;
}
