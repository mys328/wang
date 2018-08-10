package com.thinkwin.common.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户展现实体类
 * User: yinchunlei
 * Date: 2017/8/16.
 * Company: thinkwin
 */
public class SysUserVo implements Serializable {

    /**
     * 用户Id
     */
    private String id;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户名拼音
     */
    private String userNamePinyin;

    /**
     * 已绑定邮箱
     */
    private String email;

    /**
     * 已绑定微信号
     */
    private String wechat;

    /**
     * 公众号openid
     */
    private String openId;

    /**
     * 组织机构Id
     */
    private String orgId;

    /**
     * 组织机构名称
     */
    private String orgName;

    /**
     * 职位
     */
    private String position;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 性别(0:代表男 1:代表女)
     */
    private Integer sex;

    /**
     * 用户头像源路径
     */
    private String photo;

    /**
     * 用户头像中图
     */
    private String inPicture;

    /**
     * 用户头像小图
     */
    private String smallPicture;
    /*
    * 用户大头像
    * */
    private String bigPicture;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 居住地址
     */
    private String address;

    /**
     * 有效:1 ；无效:0
     */
    private Integer status;

    /**
     * 创建人
     */
    private String creater;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifyer;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 设备token  手机唯一标识 web端可为空
     */
    private String deviceToken;

    /**
     * 设备类型 0其他 1:IOS 2:Android 3:pc
     */
    private Integer deviceType;

    /**
     * 预留字段1
     */
    private String reserve1;

    /**
     * 预留字段2
     */
    private String reserve2;

    /**
     * 预留字段3
     */
    private String reserve3;

    /**
     * 员工编号
     */
    private String userNumber;

    /**
     * 用户角色名称
     */
    private String roleName;

    /**
     * 用户角色主键id
     */
    private String roleId;

    /**
     * 用户所在组织机构的目录
     */
    private String catalog;

    private boolean isRole;

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNamePinyin() {
        return userNamePinyin;
    }

    public void setUserNamePinyin(String userNamePinyin) {
        this.userNamePinyin = userNamePinyin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getInPicture() {
        return inPicture;
    }

    public void setInPicture(String inPicture) {
        this.inPicture = inPicture;
    }

    public String getSmallPicture() {
        return smallPicture;
    }

    public void setSmallPicture(String smallPicture) {
        this.smallPicture = smallPicture;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifyer() {
        return modifyer;
    }

    public void setModifyer(String modifyer) {
        this.modifyer = modifyer;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public String getReserve1() {
        return reserve1;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1;
    }

    public String getReserve2() {
        return reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
    }

    public String getReserve3() {
        return reserve3;
    }

    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getBigPicture() {
        return bigPicture;
    }

    public void setBigPicture(String bigPicture) {
        this.bigPicture = bigPicture;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public boolean isRole() {
        return isRole;
    }

    public void setRole(boolean role) {
        isRole = role;
    }
}
