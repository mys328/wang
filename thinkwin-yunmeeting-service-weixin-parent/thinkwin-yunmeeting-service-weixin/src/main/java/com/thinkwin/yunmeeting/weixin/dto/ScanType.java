package com.thinkwin.yunmeeting.weixin.dto;

/**
 * 扫码几种类型
 */
public enum ScanType {

    /**
     * 非企云会公众号二维码
     */
    YUNMEETING_NULL,
    /**
     * 企云会公众号二维码
     */
    YUNMEETING,
    /**
     * 已绑定当前租户
     */
    SETTLED,
    /**
     * 租户邀请码已绑定其它租户
     */
    TENANT_SETTLED_OTHER,

    /**
     * 租户邀请码未绑定
     */
    TENANT_UNSETTLED,

    /**
     * 账户二维码已绑定其它用户
     */
    ACCOUNT_SETTLED_OTHER,

    /**
     * 账户二维码未绑定
     */
    ACCOUNT_UNSETTLED,

    /**
     * 账户二维码移除
     */
    ACCOUNT_REMOVE


}
