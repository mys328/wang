package com.thinkwin.pay.model;

import java.util.EnumSet;
import java.util.Set;

public enum PayStatus {
	PREPAY(0){
		@Override
		public String toString() {
			return "支付已创建";
		}
	},
	WAIT_BUYER_PAY(1){
		@Override
		public String toString() {
			return "待付款";
		}
	},
	TRADE_SUCCESS(2){
		@Override
		public String toString() {
			return "支付成功";
		}
	},
	TRADE_FAILED(3){
		@Override
		public String toString() {
			return "支付失败";
		}
	},
	TRADE_FINISHED(4){
		@Override
		public String toString() {
			return "支付失败";
		}
	},
	TRADE_CLOSED(5){
		@Override
		public String toString() {
			return "交易关闭";
		}
	};

	private Integer value;
	PayStatus(Integer value){
		this.value = value;
	}

	public Integer getValue(){
		return value;
	}

	public static Set<PayStatus> TERMINATED = EnumSet.of(PayStatus.TRADE_CLOSED, PayStatus.TRADE_FINISHED, PayStatus.TRADE_SUCCESS);

	public static PayStatus fromCode(Integer value) {
		for (PayStatus e : values()) {
			if (e.getValue() == value) {
				return e;
			}
		}

		return null;
	}
}