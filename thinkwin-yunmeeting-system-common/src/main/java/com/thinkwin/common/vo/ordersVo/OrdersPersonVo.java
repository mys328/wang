package com.thinkwin.common.vo.ordersVo;

import java.io.Serializable;

/**
 * 类名: OrdersPersonVo </br>
 * 描述:订单授权升级页面人员统计Vo</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/22 </br>
 */
public class OrdersPersonVo implements Serializable{
    private static final long serialVersionUID = -2159723365256673637L;

    //统计人员
    private Integer totalPersonNum;       //总人数
    private Integer activePersonNum;        //已激活人数
    private Integer notActivePersonNum;     //未激活人数
    private Integer residuePersonNum;       //剩余人数
    //统计会议室
    private String totalRooms;          //总会议室
    private String  usedRooms;       //使用会议室
    private String freeRooms;       //剩余会议室
    //统计空间
    private String totalSpace;      //总空间
    private String usedSpace;       //使用空间
    private String freeSpace;       //剩余空间
    //版本信息
    private String version;     //0 免费 1：专业1 2：专业2  3：专业3
    private Long validity;    //有效期

    public Integer getTotalPersonNum() {
        return totalPersonNum;
    }

    public void setTotalPersonNum(Integer totalPersonNum) {
        this.totalPersonNum = totalPersonNum;
    }

    public Integer getActivePersonNum() {
        return activePersonNum;
    }

    public void setActivePersonNum(Integer activePersonNum) {
        this.activePersonNum = activePersonNum;
    }

    public Integer getNotActivePersonNum() {
        return notActivePersonNum;
    }

    public void setNotActivePersonNum(Integer notActivePersonNum) {
        this.notActivePersonNum = notActivePersonNum;
    }

    public Integer getResiduePersonNum() {
        return residuePersonNum;
    }

    public void setResiduePersonNum(Integer residuePersonNum) {
        this.residuePersonNum = residuePersonNum;
    }

    public String getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(String totalRooms) {
        this.totalRooms = totalRooms;
    }

    public String getUsedRooms() {
        return usedRooms;
    }

    public void setUsedRooms(String usedRooms) {
        this.usedRooms = usedRooms;
    }

    public String getFreeRooms() {
        return freeRooms;
    }

    public void setFreeRooms(String freeRooms) {
        this.freeRooms = freeRooms;
    }

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getValidity() {
        return validity;
    }

    public void setValidity(Long validity) {
        this.validity = validity;
    }
}
