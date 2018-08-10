package com.thinkwin.common.log;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取类型详细信息
 */
public class TypeDesc {
    public static Map<String, String> businessTypes;

    static {
        businessTypes = new HashMap<String, String>();

        businessTypes.put(BusinessType.general.toString(), "基本操作");
        businessTypes.put(BusinessType.meetingOp.toString(), "会议室管理");
        businessTypes.put(BusinessType.loginOp.toString(), "登录");
        businessTypes.put(BusinessType.registerOp.toString(), "注册");
        businessTypes.put(BusinessType.companyOp.toString(), "企业管理");
        businessTypes.put(BusinessType.contactsOp.toString(), "通讯录管理");
        businessTypes.put(BusinessType.ordersOp.toString(), "订单管理");
        businessTypes.put(BusinessType.statisticalOp.toString(), "统计分析");

        businessTypes.put(EventType.general.toString(), "基本操作");
        businessTypes.put(EventType.platform_login.toString(), "登录失败");
        businessTypes.put(EventType.platform_login.toString(), "登录成功");
    }

    public static String formatTypeDesc(String businessType) {
        return businessTypes.get(businessType);
    }
}
