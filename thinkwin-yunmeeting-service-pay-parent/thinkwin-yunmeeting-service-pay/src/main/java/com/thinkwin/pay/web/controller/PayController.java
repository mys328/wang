package com.thinkwin.pay.web.controller;

import com.github.wxpay.sdk.WXPayUtil;
import com.thinkwin.pay.mapper.PaymentLogMapper;
import com.thinkwin.pay.mapper.PaymentMapper;
import com.thinkwin.pay.service.EBank;
import com.thinkwin.pay.service.NotifyExecutor;
import com.thinkwin.pay.service.impl.WxPayService;
import com.thinkwin.serialnumber.service.SerialNumberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping(value = "/pay")
public class PayController {
	private static Logger log = LoggerFactory.getLogger(WxPayService.class);

	@Autowired
	private EBank zfbPayService;

	@Autowired
	private EBank wxPayService;

	@Autowired
	private NotifyExecutor notifyExecutor;

	/**
	 * 支付宝支付通知响应接口
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/alipay/notify")
	@ResponseBody
	public void aliPayNotify(HttpServletRequest request, HttpServletResponse response) {
		String message = "failed";

		Map<String, String> params = null;
		try{
			Map<String,String[]> requestParams = request.getParameterMap();

			if(null == requestParams || requestParams.size() == 0){
				response.getWriter().write(message);
				return;
			}

			params = new HashMap<String, String>();

			for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = iter.next();
				String[] values = requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				params.put(name, valueStr);
			}

			if(CollectionUtils.isEmpty(params)){
				response.getWriter().write(message);
				return;
			}

			log.debug("支付: 支付宝通知信息：{}", params);

			if (zfbPayService.verifyPayNotify(params)) {
				message = "success";

				if("TRADE_FINISHED".equals(params.get("trade_status"))){
					log.info("支付: 收到支付宝支付完成通知，TRADE_FINISHED，支付流水号: {}", params.get("out_trade_no"));
					response.getWriter().write(message);
					return;
				}

				try{
					String tradeNo = params.get("out_trade_no");
					notifyExecutor.notify(tradeNo);
				} catch (Exception e){
					log.error("支付: 收到支付宝支付结果，通知订单系统失败, 支付流水号: {}", params.get("out_trade_no"));
					log.error("支付: 收到支付宝支付结果，通知订单系统失败: {}", e);
				}
			}
		}
		catch (Exception ex){
			log.error("支付: aliPayNotify error: {}", ex);
		}
		finally {
			log.info("支付: 收到支付宝支付结果通知，支付流水号: {}，系统响应: {}", (params != null ? params.get("out_trade_no"): ""), message);
		}

		try{
			response.getWriter().write(message);
		} catch (Exception ex){
			log.error("支付: 响应支付宝支付通知异常，支付流水号: {}，异常信息: {}", (params != null ? params.get("out_trade_no"): ""), ex.getMessage());
		}
	}

	@RequestMapping(value = "/alipay/return")
	@ResponseBody
	public String aliPaySuccess(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Enumeration<String> parameterNames = request.getParameterNames();

		Map<String, String> params = new HashMap<String, String>();

		while (parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			params.put(parameterName, request.getParameter(parameterName));
		}

		if(CollectionUtils.isEmpty(params)){
			return "";
		}

		return params.get("out_trade_no");
	}

	/**
	 * 微信支付通知响应接口
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/tenpay/notify", produces="text/plain;charset=UTF-8")
	@ResponseBody
	public String wxPayNotify(HttpServletRequest request, HttpServletResponse response) {
		String message = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[失败]]></return_msg></xml> ";

		Map<String, String> params = null;
		try{
			InputStream inputStream ;
			StringBuffer sb = new StringBuffer();
			inputStream = request.getInputStream();
			String s ;
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ((s = in.readLine()) != null){
				sb.append(s);
			}
			in.close();
			inputStream.close();

			String paramsStr = sb.toString();

			if(null == paramsStr || paramsStr.equals("")){
				return message;
			}

			log.debug("微信支付通知信息：{}", paramsStr);

			params = WXPayUtil.xmlToMap(paramsStr);

			if(null == params || params.size() == 0){
				return message;
			}

			boolean success = wxPayService.verifyPayNotify(params);

			if(success){
				message = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";

				String tradeNo = params.get("out_trade_no");
				notifyExecutor.notify(tradeNo);
			}
		}
		catch (Exception ex){
			log.error("支付: 微信支付, error: {}", ex);
		}
		finally {
			log.info("支付: 收到微信支付结果通知，支付流水号: {}，系统响应: {}", (params != null ? params.get("out_trade_no"): ""), message);
		}


		return message;
	}

	private static void flushStream(String msg, HttpServletResponse response) {
		try{
			response.getWriter().write(msg);
		}
		catch (IOException ex){
			log.error("支付: flushStream error: {}", ex);
		}
	}

}
