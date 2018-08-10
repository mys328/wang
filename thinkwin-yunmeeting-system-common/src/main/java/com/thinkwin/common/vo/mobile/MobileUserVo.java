package com.thinkwin.common.vo.mobile;

import java.io.Serializable;

/*
 * 类说明：
 * @author lining 2017/8/30
 * @version 1.0
 *
 */
public class MobileUserVo implements Serializable{
    private static final long serialVersionUID = -2983044575832262834L;

    /**
     * 人员主键Id标识
     */
    private String userId;
    /**
     * 人员用户姓名
     */
    private String userName;
    /**
     * 人员头像地址
     */
    private String headPicAddress;

    /**
     * 人员所在部门名称
     */
    private String department;
    /**
     * 人员职位
     */
    private String position;
    /**
     * 人员电话号码
     */
    private String phoneNumber;
    /**
     * 人员邮箱
     */
    private String email;
    /**
     * 人员性别（0男，1女）
     */
    private Integer sex;
    /**
     * 组织机构id
     */
    private String orgaId;
    /**
     * 是否为创建者，0表示不是创建者，1表示为创建者
     */
    private Integer isCreator;

    /**
     * 是否占用
     */
    private Integer isOccupy;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadPicAddress() {
        return headPicAddress;
    }

    public void setHeadPicAddress(String headPicAddress) {
        this.headPicAddress = headPicAddress;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getOrgaId() {
        return orgaId;
    }

    public void setOrgaId(String orgaId) {
        this.orgaId = orgaId;
    }

    public Integer getIsCreator() {
        return isCreator;
    }

    public void setIsCreator(Integer isCreator) {
        this.isCreator = isCreator;
    }

    public Integer getIsOccupy() {
        return isOccupy;
    }

    public void setIsOccupy(Integer isOccupy) {
        this.isOccupy = isOccupy;
    }
}
