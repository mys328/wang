package com.thinkwin.common.utils;

import com.thinkwin.common.response.ResponseResult;

/**
 * User: yinchunlei
 * Date: 2017/6/12.
 * Company: thinkwin
 */
public class ResponseResultAuxiliaryUtil {

    private Integer ifSuc;
    private String msg;
    private Object data;
    private String code;

    public Integer getIfSuc() {
        return ifSuc;
    }

    public void setIfSuc(Integer ifSuc) {
        this.ifSuc = ifSuc;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public static ResponseResult responseResultAuxiliaryUtil(Integer ifSuc, String msg){

        return responseResultAuxiliaryUtil(ifSuc,msg,null,null);
    }

    public static ResponseResult responseResultAuxiliaryUtil(Integer ifSuc, String msg,String code){

        return responseResultAuxiliaryUtil(ifSuc,msg,null,code);
    }
    public static ResponseResult responseResultAuxiliaryUtil(Integer ifSuc, String msg,Object data){

        return responseResultAuxiliaryUtil(ifSuc,msg,data,null);
    }
    public static ResponseResult responseResultAuxiliaryUtil(Integer ifSuc, String msg, Object data, String code){
        ResponseResult responseResult = new ResponseResult();
        if(null != ifSuc && !"".equals(ifSuc)){
            responseResult.setIfSuc(ifSuc);
        }
        if(null != msg && !"".equals(msg)){
            responseResult.setMsg(msg);
        }
        if(null != data){
            responseResult.setData(data);
        }
        if(null != code && !"".equals(code)){
            responseResult.setCode(code);
        }
        return responseResult;
    }
}
