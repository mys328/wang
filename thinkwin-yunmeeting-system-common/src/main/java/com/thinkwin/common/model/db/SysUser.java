package com.thinkwin.common.model.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "`sys_user`")
public class SysUser implements Serializable {
    /**
     * 用户Id
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 手机号
     */
    @Column(name = "`phone_number`")
    private String phoneNumber;

    /**
     * 用户名称
     */
    @Column(name = "`user_name`")
    private String userName;

    /**
     * 用户名拼音
     */
    @Column(name = "`user_name_pinyin`")
    private String userNamePinyin;

    /**
     * 已绑定邮箱
     */
    @Column(name = "`email`")
    private String email;

    /**
     * 已绑定微信号
     */
    @Column(name = "`wechat`")
    private String wechat;

    /**
     * 公众号OpenId
     */
    @Column(name = "`openid`")
    private String openId;

    /**
     * 是否关注公众号 1：是，0：否
     */
    @Column(name = "`is_subscribe`")
    private String isSubscribe;

    /**
     * 组织机构Id
     */
    @Column(name = "`org_id`")
    private String orgId;

    /**
     * 组织机构名称
     */
    @Column(name = "`org_name`")
    private String orgName;

    /**
     * 职位
     */
    @Column(name = "`position`")
    private String position;

    /**
     * 租户Id
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * 性别(0:代表男 1:代表女)
     */
    @Column(name = "`sex`")
    private Integer sex;

    /**
     * 用户头像源路径
     */
    @Column(name = "`photo`")
    private String photo;

    /**
     * 用户头像中图
     */
    @Column(name = "`centralGraph`")
    private String centralgraph;

    /**
     * 用户头像小图
     */
    @Column(name = "`thumbnails`")
    private String thumbnails;

    /**
     * 生日
     */
    @Column(name = "`birthday`")
    private Date birthday;

    /**
     * 居住地址
     */
    @Column(name = "`address`")
    private String address;

    /**
     * 有效:1 ；无效:0
     */
    @Column(name = "`status`")
    private Integer status;

    /**
     * 创建人
     */
    @Column(name = "`creater`")
    private String creater;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * 修改人
     */
    @Column(name = "`modifyer`")
    private String modifyer;

    /**
     * 修改时间
     */
    @Column(name = "`modify_time`")
    private Date modifyTime;

    /**
     * 设备token  手机唯一标识 web端可为空
     */
    @Column(name = "`device_token`")
    private String deviceToken;

    /**
     * 设备类型 0其他 1:IOS 2:Android 3:pc
     */
    @Column(name = "`device_type`")
    private Integer deviceType;

    /**
     * 预留字段1
     */
    @Column(name = "`reserve_1`")
    private String reserve1;

    /**
     * 预留字段2
     */
    @Column(name = "`reserve_2`")
    private String reserve2;

    /**
     * 预留字段3
     */
    @Column(name = "`reserve_3`")
    private String reserve3;

    /**
     * 员工编号
     */
    @Column(name = "`user_number`")
    private String userNumber;

    /**
     * 获取用户Id
     *
     * @return id - 用户Id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置用户Id
     *
     * @param id 用户Id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取手机号
     *
     * @return phone_number - 手机号
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 设置手机号
     *
     * @param phoneNumber 手机号
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
    }

    /**
     * 获取用户名称
     *
     * @return user_name - 用户名称
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名称
     *
     * @param userName 用户名称
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    /**
     * 获取用户名拼音
     *
     * @return user_name_pinyin - 用户名拼音
     */
    public String getUserNamePinyin() {
        return userNamePinyin;
    }

    /**
     * 设置用户名拼音
     *
     * @param userNamePinyin 用户名拼音
     */
    public void setUserNamePinyin(String userNamePinyin) {
        this.userNamePinyin = userNamePinyin == null ? null : userNamePinyin.trim();
    }

    /**
     * 获取已绑定邮箱
     *
     * @return email - 已绑定邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置已绑定邮箱
     *
     * @param email 已绑定邮箱
     */
    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    /**
     * 获取已绑定微信号
     *
     * @return wechat - 已绑定微信号
     */
    public String getWechat() {
        return wechat;
    }

    /**
     * 设置已绑定微信号
     *
     * @param wechat 已绑定微信号
     */
    public void setWechat(String wechat) {
        this.wechat = wechat == null ? null : wechat.trim();
    }

    /**
     * 获取组织机构Id
     *
     * @return org_id - 组织机构Id
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * 设置组织机构Id
     *
     * @param orgId 组织机构Id
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId == null ? null : orgId.trim();
    }

    /**
     * 获取组织机构名称
     *
     * @return org_name - 组织机构名称
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * 设置组织机构名称
     *
     * @param orgName 组织机构名称
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName == null ? null : orgName.trim();
    }

    /**
     * 获取职位
     *
     * @return position - 职位
     */
    public String getPosition() {
        return position;
    }

    /**
     * 设置职位
     *
     * @param position 职位
     */
    public void setPosition(String position) {
        this.position = position == null ? null : position.trim();
    }

    /**
     * 获取租户Id
     *
     * @return tenant_id - 租户Id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 设置租户Id
     *
     * @param tenantId 租户Id
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    /**
     * 获取性别(0:代表男 1:代表女)
     *
     * @return sex - 性别(0:代表男 1:代表女)
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置性别(0:代表男 1:代表女)
     *
     * @param sex 性别(0:代表男 1:代表女)
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取用户头像源路径
     *
     * @return photo - 用户头像源路径
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * 设置用户头像源路径
     *
     * @param photo 用户头像源路径
     */
    public void setPhoto(String photo) {
        this.photo = photo == null ? null : photo.trim();
    }

    /**
     * 获取用户头像中图
     *
     * @return centralGraph - 用户头像中图
     */
    public String getCentralgraph() {
        return centralgraph;
    }

    /**
     * 设置用户头像中图
     *
     * @param centralgraph 用户头像中图
     */
    public void setCentralgraph(String centralgraph) {
        this.centralgraph = centralgraph == null ? null : centralgraph.trim();
    }

    /**
     * 获取用户头像小图
     *
     * @return thumbnails - 用户头像小图
     */
    public String getThumbnails() {
        return thumbnails;
    }

    /**
     * 设置用户头像小图
     *
     * @param thumbnails 用户头像小图
     */
    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails == null ? null : thumbnails.trim();
    }

    /**
     * 获取生日
     *
     * @return birthday - 生日
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * 设置生日
     *
     * @param birthday 生日
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * 获取居住地址
     *
     * @return address - 居住地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置居住地址
     *
     * @param address 居住地址
     */
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    /**
     * 获取有效:1 ；无效:0
     *
     * @return status - 有效:1 ；无效:0
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置有效:1 ；无效:0
     *
     * @param status 有效:1 ；无效:0
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取创建人
     *
     * @return creater - 创建人
     */
    public String getCreater() {
        return creater;
    }

    /**
     * 设置创建人
     *
     * @param creater 创建人
     */
    public void setCreater(String creater) {
        this.creater = creater == null ? null : creater.trim();
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取修改人
     *
     * @return modifyer - 修改人
     */
    public String getModifyer() {
        return modifyer;
    }

    /**
     * 设置修改人
     *
     * @param modifyer 修改人
     */
    public void setModifyer(String modifyer) {
        this.modifyer = modifyer == null ? null : modifyer.trim();
    }

    /**
     * 获取修改时间
     *
     * @return modify_time - 修改时间
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * 设置修改时间
     *
     * @param modifyTime 修改时间
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * 获取设备token  手机唯一标识 web端可为空
     *
     * @return device_token - 设备token  手机唯一标识 web端可为空
     */
    public String getDeviceToken() {
        return deviceToken;
    }

    /**
     * 设置设备token  手机唯一标识 web端可为空
     *
     * @param deviceToken 设备token  手机唯一标识 web端可为空
     */
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken == null ? null : deviceToken.trim();
    }

    /**
     * 获取设备类型 0其他 1:IOS 2:Android 3:pc
     *
     * @return device_type - 设备类型 0其他 1:IOS 2:Android 3:pc
     */
    public Integer getDeviceType() {
        return deviceType;
    }

    /**
     * 设置设备类型 0其他 1:IOS 2:Android 3:pc
     *
     * @param deviceType 设备类型 0其他 1:IOS 2:Android 3:pc
     */
    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * 获取预留字段1
     *
     * @return reserve_1 - 预留字段1
     */
    public String getReserve1() {
        return reserve1;
    }

    /**
     * 设置预留字段1
     *
     * @param reserve1 预留字段1
     */
    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1 == null ? null : reserve1.trim();
    }

    /**
     * 获取预留字段2
     *
     * @return reserve_2 - 预留字段2
     */
    public String getReserve2() {
        return reserve2;
    }

    /**
     * 设置预留字段2
     *
     * @param reserve2 预留字段2
     */
    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2 == null ? null : reserve2.trim();
    }

    /**
     * 获取预留字段3
     *
     * @return reserve_3 - 预留字段3
     */
    public String getReserve3() {
        return reserve3;
    }

    /**
     * 设置预留字段3
     *
     * @param reserve3 预留字段3
     */
    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3 == null ? null : reserve3.trim();
    }

    /**
     * 获取员工编号
     *
     * @return user_number - 员工编号
     */
    public String getUserNumber() {
        return userNumber;
    }

    /**
     * 设置员工编号
     *
     * @param userNumber 员工编号
     */
    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber == null ? null : userNumber.trim();
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(String isSubscribe) {
        this.isSubscribe = isSubscribe;
    }
}