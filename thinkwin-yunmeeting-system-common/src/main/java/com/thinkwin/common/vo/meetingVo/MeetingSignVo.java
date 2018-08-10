package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;

/**
 * 类名: MeetingSignVo </br>
 * 描述:</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/9/9 </br>
 */
public class MeetingSignVo implements Serializable{
    private static final long serialVersionUID = -1559673638269439692L;

    //参会人Id
    private String id;
    //参会人姓名
    private String name;
    //签到时间
    private Long signTime;
    //手机号码
    private String phoneNumber;
    //用户头像
    private String photo;
    //用户头像（大）
    private String  bigPicture;
    //用户头像（中）
    private String inPicture;
    //用户头像(小)
    private String smallPicture;
    //签到状态 0 未签到  1  已签到
    private String signState;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSignTime() {
        return signTime;
    }

    public void setSignTime(Long signTime) {
        this.signTime = signTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSignState() {
        return signState;
    }

    public void setSignState(String signState) {
        this.signState = signState;
    }

    public String getBigPicture() {
        return bigPicture;
    }

    public void setBigPicture(String bigPicture) {
        this.bigPicture = bigPicture;
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
}
