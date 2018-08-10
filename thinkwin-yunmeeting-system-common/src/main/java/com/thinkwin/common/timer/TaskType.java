package com.thinkwin.common.timer;

public enum TaskType {
	MEETING_EXPIRE(0),
	ORDER_EXPIRE(1),
	PAY_SUCCESS_NOTIFY_RETRY(2){
		@Override
		public String toString() {
			return "支付成功通知订单系统失败后重试";
		}
	};

	private Integer code;

	TaskType(Integer code) {
		this.code = code;
	}

	public Integer getCode(){
		return this.code;
	}}
