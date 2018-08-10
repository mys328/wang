package com.thinkwin.pay.model;

import java.util.HashMap;
import java.util.Map;

public enum PayChannelEnum {
	WEIXIN(1){
		@Override
		public String toString() {
			return "微信";
		}
	},
	ZFB(2){
		@Override
		public String toString() {
			return "支付宝";
		}
	},
	WANGYIN(3){
		@Override
		public String toString() {
			return "网银支付";
		}
	},
	HUIKUAN(4){
		@Override
		public String toString() {
			return "银行汇款";
		}
	};

	private static Map<Integer, PayChannelEnum> valuesLookup = new HashMap<Integer, PayChannelEnum>();
	static {
		for (PayChannelEnum d : PayChannelEnum.values()) {
			valuesLookup.put(d.getCode(), d);
		}
	}

	private Integer code;

	PayChannelEnum(Integer code){
		this.code = code;
	}

	public static PayChannelEnum fromCode(String code){
		Integer _code = Integer.parseInt(code);
		if(!valuesLookup.containsKey(_code)){
			return null;
		}
		return valuesLookup.get(_code);
	}

	public static PayChannelEnum fromCode(Integer code){
		if(!valuesLookup.containsKey(code)){
			return null;
		}
		return valuesLookup.get(code);
	}

	public Integer getCode(){
		return this.code;
	}
}
