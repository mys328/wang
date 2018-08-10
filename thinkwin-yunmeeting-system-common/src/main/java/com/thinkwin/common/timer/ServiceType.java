package com.thinkwin.common.timer;

public enum ServiceType {
	MEETING(0){
		@Override
		public String toString() {
			return "会议";
		}
	},
	ORDER(1){
		@Override
		public String toString() {
			return "订单";
		}
	},
	PAY(2){
		@Override
		public String toString() {
			return "支付";
		}
	};

	private Integer code;

	ServiceType(Integer code) {
		this.code = code;
	}

	public Integer getCode(){
		return this.code;
	}
}
