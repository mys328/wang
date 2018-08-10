package com.thinkwin.common.response;

/**
 * 返回前端的Json封装类
 * @author yyq
 *
 */

import java.io.Serializable;

public class ResponseResult implements Serializable {
	private static final long serialVersionUID = 8345180272869760086L;
	//是否成功  成功返回1失败返回0
    private int ifSuc;
    //操作完成后的描述信息
	private String msg;
	// 返回结果数据，任意类型
	private Object data;
	//请求失败后的异常状态码  成功的时候可为空
	private String code;

	public int getIfSuc() {
		return ifSuc;
	}

	public void setIfSuc(int ifSuc) {
		this.ifSuc = ifSuc;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}