package com.thinkwin.common.vo.meetingVo;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

/**
 * 类名: DynamicVo </br>
 * 描述: 动态Vo</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/9 </br>
 */
public class DynamicVo implements Serializable {
    private static final long serialVersionUID = -8250308036272321064L;

    private String id;       //动态Id
    private String photo;       //用户头像原图
    private String smallPicture;   //小图
    private String bigPicture;       //大图
    private String inPicture;        //中图
    private String userId;      //用户Id
    private String name;        //用户昵称
    private String message;      //动态内容
    private Long timeago;     //动态创建时间
    private String sys;     //动态类型 0:用户留言；1:系统推送
    private String dateState;  //时间状态  0：七天以前   1：七天以后

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimeago() {
        return timeago;
    }

    public void setTimeago(Long timeago) {
        this.timeago = timeago;
    }

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }

    public String getDateState() {
        return dateState;
    }

    public void setDateState(String dateState) {
        this.dateState = dateState;
    }

    public String getSmallPicture() {
        return smallPicture;
    }

    public void setSmallPicture(String smallPicture) {
        this.smallPicture = smallPicture;
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
}
