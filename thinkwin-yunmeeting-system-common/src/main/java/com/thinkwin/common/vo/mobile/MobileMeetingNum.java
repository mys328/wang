package com.thinkwin.common.vo.mobile;

import java.io.Serializable;

/**
 * h5代办会议数量
 */
public class MobileMeetingNum implements Serializable {

    private static final long serialVersionUID = -4918812310770985117L;
    /**
     * 代办数量
     */
    private Integer agencyNumber;
    /**
     * 已办数量
     */
    private Integer alreadyNumber;

    public Integer getAgencyNumber() {
        return agencyNumber;
    }

    public void setAgencyNumber(Integer agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    public Integer getAlreadyNumber() {
        return alreadyNumber;
    }

    public void setAlreadyNumber(Integer alreadyNumber) {
        this.alreadyNumber = alreadyNumber;
    }
}
