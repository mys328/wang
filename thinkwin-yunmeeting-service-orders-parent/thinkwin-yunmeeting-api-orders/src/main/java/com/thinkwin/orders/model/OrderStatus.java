package com.thinkwin.orders.model;

import java.util.HashMap;
import java.util.Map;

public enum OrderStatus {
	/**
	 ===支付状态===
	 0: 未支付
	 1: 已付款
	 2: 已退款
	 3: 支付失败
	 4: 开票中
	 5: 已开票
	 8: 待银行汇款
	 99: 已取消
	 */
	WEIZHIFU(0){
		@Override
		public String toString() {
			return "未支付";
		}
	},
	YIFUKUAN(1){
		@Override
		public String toString() {
			return "已付款";
		}
	},
	YITUIKUAN(2){
		@Override
		public String toString() {
			return "已退款";
		}
	},
	ZHIFUSHIBAI(3){
		@Override
		public String toString() {
			return "支付失败";
		}
	},
	KAIPIAOZHONG(4){
		@Override
		public String toString() {
			return "开票中";
		}
	},
	YIKAIPIAO(5){
		@Override
		public String toString() {
			return "已开票";
		}
	},

	DAIHUIKUAN(8){
		@Override
		public String toString() {
			return "待银行汇款";
		}
	},
	QUXIAO(99){
		@Override
		public String toString() {
			return "已取消";
		}
	};

	private static Map<Integer, OrderStatus> valuesLookup = new HashMap<Integer, OrderStatus>();
	static {
		for (OrderStatus d : OrderStatus.values()) {
			valuesLookup.put(d.getCode(), d);
		}
	}

	private Integer code;

	OrderStatus(Integer code){
		this.code = code;
	}

	public static OrderStatus fromCode(Integer code){
		if(!valuesLookup.containsKey(code)){
			return null;
		}
		return valuesLookup.get(code);
	}

	public static String getName(Integer code){
		if(!valuesLookup.containsKey(code)){
			return null;
		}
		return valuesLookup.get(code).toString();
	}

	public static String getName(String code){
		if(!valuesLookup.containsKey(code)){
			return null;
		}
		return valuesLookup.get(Integer.parseInt(code)).toString();
	}

	public Integer getCode(){
		return this.code;
	}
}
