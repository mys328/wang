package com.thinkwin.common.vo.publish;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/18 0018.
 */
public class QRCodeVo implements Serializable {
    private static final long serialVersionUID = -4220023864273649185L;

    /**
     * 会议室id
     */
    private String id;
    /**
     * 租户id
     */
    private String tenantId;
    /**
     * 刷新频率
     */
    private Integer timeStep;
    /**
     * 二维码路径
     */
    private String qrCodeUrl;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(Integer timeStep) {
        this.timeStep = timeStep;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}
