package com.thinkwin.common.response;

public enum ResponseResultStatusEnum {
    success(1),
	fail(0);
	private int id;
	private ResponseResultStatusEnum(int id){
		this.id=id;
	}
	public int getId(){
		return this.id;
	}
}