package com.thinkwin.pay.model;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class WxPayConfig extends WXPayConfig {
	private String appID;
	private String mchID;
	private String key;
	private String keyPath;
	private String notifyUrl;
	private String returnUrl;

	public String getAppID() {
		return appID;
	}

	public void setAppId(String appID) {
		this.appID = appID;
	}

	public String getMchID() {
		return mchID;
	}

	public void setMchId(String mchID) {
		this.mchID = mchID;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	protected InputStream getCertStream() {
		File file = new File(keyPath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return fis;
	}

	@Override
	public int getHttpConnectTimeoutMs() {
		return 3000;
	}

	@Override
	public int getHttpReadTimeoutMs() {
		return 3000;
	}

	@Override
	protected IWXPayDomain getWXPayDomain() {
		return new WxPayDomain("api.mch.weixin.qq.com");
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKeyPath() {
		return keyPath;
	}

	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public static class WxPayDomain implements IWXPayDomain{
		private  DomainInfo domainInfo;

		@Override
		public void report(String s, long l, Exception e) {

		}

		public WxPayDomain(String domain) {
			this.domainInfo = new DomainInfo(domain, true);
		}

		@Override
		public DomainInfo getDomain(WXPayConfig wxPayConfig) {
			return this.domainInfo;
		}
	}
}
