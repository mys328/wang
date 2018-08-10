package com.thinkwin.common.model;

import java.io.Serializable;

/**
 * User: yinchunlei
 * Date: 2017/8/2.
 * Company: thinkwin
 * excle模板实体类
 */
public class ExcelVO implements Serializable {

    private String userNum;//员工编号
    private String position;//职位
    private String depar;//部门
    private String name;//姓名
    private String sex;//性别
    private String phoneNum;//手机号
    private String email;//邮箱

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepar() {
        return depar;
    }

    public void setDepar(String depar) {
        this.depar = depar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
