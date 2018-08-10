package com.thinkwin.orders.model;

public enum OrderType {
	UPGRADING(0) {
		@Override
		public String toString() {
			return "升级";
		}
	},
	EXPANDING(1) {
		@Override
		public String toString() {
			return "增容";
		}
	},
	RENEWAL(2) {
		@Override
		public String toString() {
			return "续费";
		}
	};

	private Integer code;

	OrderType(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return this.code;
	}
}
