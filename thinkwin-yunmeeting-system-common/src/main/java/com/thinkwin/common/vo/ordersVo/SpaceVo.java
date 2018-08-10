package com.thinkwin.common.vo.ordersVo;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/25.
 */
public class SpaceVo implements Serializable {
    private static final long serialVersionUID = -2015922180457131262L;
    /**
     * 总空间
     */
    private String totalSpace;
    /**
     * 使用空间
     */
    private String usedSpace;
    /**
     * 剩余空间
     */
    private String freeSpace;


    public String getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(String totalSpace) {
        this.totalSpace = totalSpace;
    }

    public String getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(String usedSpace) {
        this.usedSpace = usedSpace;
    }

    public String getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(String freeSpace) {
        this.freeSpace = freeSpace;
    }
}
