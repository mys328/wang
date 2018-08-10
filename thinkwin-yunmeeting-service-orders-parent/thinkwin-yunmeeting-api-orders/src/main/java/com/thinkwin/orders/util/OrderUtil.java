package com.thinkwin.orders.util;

import com.thinkwin.orders.dto.AdminOrderQuery;
import com.thinkwin.orders.dto.OrderCancelRequest;
import com.thinkwin.orders.dto.OrderPaymentConfirm;
import org.apache.commons.lang3.StringUtils;

public class OrderUtil {

	public static String getSignContent(OrderCancelRequest request){
		if(null == request){
			return "";
		}

		if(StringUtils.isBlank(request.getOrderId())){
			return "";
		}

		if(StringUtils.isBlank(request.getTimestamp())){
			return "";
		}

		if(StringUtils.isBlank(request.getNonceStr())){
			return "";
		}


		String content = "orderId=" + request.getOrderId() + ";timestamp=" + request.getTimestamp() + ";nonceStr=" + request.getNonceStr();
		return content;
	}

	public static String getSignContent(AdminOrderQuery query){
		if(null == query){
			return "";
		}

		if(StringUtils.isBlank(query.getTimeStamp())){
			return "";
		}

		if(StringUtils.isBlank(query.getNonceStr())){
			return "";
		}

		String content = "timestamp=" + query.getTimeStamp() + ";nonceStr=" + query.getNonceStr();
		return content;
	}
}
