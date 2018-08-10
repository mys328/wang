package com.thinkwin.common.model.core;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "`saas_user_oauth_info`")
public class SaasUserOauthInfo implements Serializable{
    private static final long serialVersionUID = 8393447309235852247L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`Id`")
    private String id;

    /**
     * 第三方登录表Id
     */
    @Column(name = "`user_oauth_id`")
    private String userOauthId;

    /**
     * openId
     */
    @Column(name = "`oauth_open_id`")
    private String oauthOpenId;

    /**
     * unionId
     */
    @Column(name = "`oauth_union_id`")
    private String oauthUnionId;

    /**
     * 第三方类型  1：开发平台 2：公众平台  0：其他
     */
    @Column(name = "`oauth_type`")
    private Integer oauthType;

    /**
     * 第三方用户昵称
     */
    @Column(name = "`nick_name`")
    private String nickName;

    /**
     * 性别 （0：男 1：女）
     */
    @Column(name = "`sex`")
    private Integer sex;

    /**
     * 用户语言
     */
    @Column(name = "`language`")
    private String language;

    /**
     * 城市
     */
    @Column(name = "`city`")
    private String city;

    /**
     * 省份
     */
    @Column(name = "`province`")
    private String province;

    /**
     * 国家
     */
    @Column(name = "`country`")
    private String country;

    /**
     * 是否关注1是0否
     */
    @Column(name = "`subscribe`")
    private Integer subscribe;

    /**
     * 关注时间
     */
    @Column(name = "`subscribe_time`")
    private Date subscribeTime;

    /**
     * 备注
     */
    @Column(name = "`remark`")
    private String remark;

    /**
     * 标签
     */
    @Column(name = "`tagid_list`")
    private String tagidList;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "`update_time`")
    private Date updateTime;

    /**
     * 获取主键
     *
     * @return Id - 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取第三方登录表Id
     *
     * @return user_oauth_id - 第三方登录表Id
     */
    public String getUserOauthId() {
        return userOauthId;
    }

    /**
     * 设置第三方登录表Id
     *
     * @param userOauthId 第三方登录表Id
     */
    public void setUserOauthId(String userOauthId) {
        this.userOauthId = userOauthId == null ? null : userOauthId.trim();
    }

    /**
     * 获取第三方类型  1：开发平台 2：公众平台  0：其他
     *
     * @return oauth_type - 第三方类型  1：开发平台 2：公众平台  0：其他
     */
    public Integer getOauthType() {
        return oauthType;
    }

    /**
     * 设置第三方类型  1：开发平台 2：公众平台  0：其他
     *
     * @param oauthType 第三方类型  1：开发平台 2：公众平台  0：其他
     */
    public void setOauthType(Integer oauthType) {
        this.oauthType = oauthType;
    }

    /**
     * 获取第三方用户昵称
     *
     * @return nick_name - 第三方用户昵称
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * 设置第三方用户昵称
     *
     * @param nickName 第三方用户昵称
     */
    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    /**
     * 获取性别 （0：男 1：女）
     *
     * @return sex - 性别 （0：男 1：女）
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置性别 （0：男 1：女）
     *
     * @param sex 性别 （0：男 1：女）
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取用户语言
     *
     * @return language - 用户语言
     */
    public String getLanguage() {
        return language;
    }

    /**
     * 设置用户语言
     *
     * @param language 用户语言
     */
    public void setLanguage(String language) {
        this.language = language == null ? null : language.trim();
    }

    /**
     * 获取城市
     *
     * @return city - 城市
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置城市
     *
     * @param city 城市
     */
    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    /**
     * 获取省份
     *
     * @return province - 省份
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置省份
     *
     * @param province 省份
     */
    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    /**
     * 获取国家
     *
     * @return country - 国家
     */
    public String getCountry() {
        return country;
    }

    /**
     * 设置国家
     *
     * @param country 国家
     */
    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    /**
     * 获取是否关注1是0否
     *
     * @return subscribe - 是否关注1是0否
     */
    public Integer getSubscribe() {
        return subscribe;
    }

    /**
     * 设置是否关注1是0否
     *
     * @param subscribe 是否关注1是0否
     */
    public void setSubscribe(Integer subscribe) {
        this.subscribe = subscribe;
    }

    /**
     * 获取关注时间
     *
     * @return subscribe_time - 关注时间
     */
    public Date getSubscribeTime() {
        return subscribeTime;
    }

    /**
     * 设置关注时间
     *
     * @param subscribeTime 关注时间
     */
    public void setSubscribeTime(Date subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 获取标签
     *
     * @return tagid_list - 标签
     */
    public String getTagidList() {
        return tagidList;
    }

    /**
     * 设置标签
     *
     * @param tagidList 标签
     */
    public void setTagidList(String tagidList) {
        this.tagidList = tagidList == null ? null : tagidList.trim();
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
     * 获取修改时间
     *
     * @return update_time - 修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置修改时间
     *
     * @param updateTime 修改时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getOauthOpenId() {
        return oauthOpenId;
    }

    public void setOauthOpenId(String oauthOpenId) {
        this.oauthOpenId = oauthOpenId;
    }

    public String getOauthUnionId() {
        return oauthUnionId;
    }

    public void setOauthUnionId(String oauthUnionId) {
        this.oauthUnionId = oauthUnionId;
    }
}