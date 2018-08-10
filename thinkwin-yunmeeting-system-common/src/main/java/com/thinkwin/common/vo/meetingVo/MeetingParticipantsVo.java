package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;

/**
 * 类名: MeetingParticipantsVo </br>
 * 描述:</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/3 </br>
 */
public class MeetingParticipantsVo implements Serializable{
    private static final long serialVersionUID = 5203122514008813929L;

    //参会人Id
    private String id;
    //判断userId 是人员还是组织 0是人员  1是机构或组织
    private String dep;
    //参会人姓名
    private String name;
    //参会人回复状态 1:接受 2:暂定 0:拒绝 3:未回复
    private String statu;
    //签到时间
    private Long signTime;
    //手机号码
    private String phoneNumber;
    //用户原头像
    private String photo;
    private String bigPicture; //大图
    private String inPicture; //中图
    private String smallPicture;  // 小图
    //是否离职
    private String isDimisson;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatu() {
        return statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
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

    public String getIsDimisson() {
        return isDimisson;
    }

    public void setIsDimisson(String isDimisson) {
        this.isDimisson = isDimisson;
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
