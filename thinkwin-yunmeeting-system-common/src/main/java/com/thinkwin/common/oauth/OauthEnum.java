package com.thinkwin.common.oauth;

/**
 * 第三方授权认证方式Oauth
 */
public enum OauthEnum {


    /**
     * 微信服务号
     */
    WxService(1,"微信服务号"),

    /**
     * 微信开放平台
     */
    WxOpen(2,"微信开放平台");



    private Integer code;
    private String description;
    private OauthEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
