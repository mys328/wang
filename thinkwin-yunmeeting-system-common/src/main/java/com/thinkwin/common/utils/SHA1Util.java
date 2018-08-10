package com.thinkwin.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkwin.common.businessException.BusinessException;

public class SHA1Util {
	private static final String ENCODE = "UTF-8";
	private static final Logger log = LoggerFactory.getLogger(SHA1Util.class);

	private static MessageDigest sha1MD;

	public static String SHA1(String text) {
		if (null == sha1MD) {
			try {
				sha1MD = MessageDigest.getInstance("SHA-1");
			} catch (NoSuchAlgorithmException e) {
				log.error("获取sha1MD数据出现异常！",e);
				return null;
			}
		}
		try {
			sha1MD.update(text.getBytes(ENCODE), 0, text.length());
		} catch (UnsupportedEncodingException e) {
			sha1MD.update(text.getBytes(), 0, text.length());
		}
		return toHexString(sha1MD.digest());

	}

	private static String toHexString(byte[] hashs) {
		StringBuffer sBuffer = new StringBuffer();
		for (byte hash : hashs) {
			sBuffer.append(Integer.toString((hash & 0xFF) + 0x100, 16)
					.substring(1));
		}
		return sBuffer.toString();
	}

	
	private static String getParamStr(String [] arr,Map paramMap){
		StringBuilder sb = new StringBuilder();
		if(arr != null && arr.length >0){
			for (int i = 0; i < arr.length; i++) {
				sb.append(arr[i]+paramMap.get(arr[i]));
			}
		}
		  return sb.toString();
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String,String> convertRequestParamMap(Map parameterMap) {
		Map<String,String>  paramMap=null;
		if(parameterMap != null && parameterMap.size() >0){
			Iterator it = parameterMap.entrySet().iterator();
			paramMap = new HashMap();
			while(it.hasNext()){
				Map.Entry entry = (Map.Entry)it.next();
				Object obj = entry.getValue();
				String entryKye = (String)entry.getKey();
				if("sign".equalsIgnoreCase(entryKye)){
					continue;//sign值不参与验证
				}
				String val = "";
				if (obj instanceof String[]){
					String[] strs = (String[])obj;
					for (int i = 0; i < strs.length; i++) {
						val += strs[i];
					}
				}else{
					val = obj.toString();
				}
				paramMap.put(entryKye,val);
			}
		}
		return paramMap;
	}


	/**
	 * 
	 * @param testSecureKye    c5405b0849正式key    3d0b19a235 测试key

	 * @param interFaceName    
	 * @param parameterMap
	 * @return
	 * @throws BusinessException
	 */
	public static String generateSign(String testSecureKye,String interFaceName,Map<String, String[]> parameterMap) throws BusinessException{
		
		Map<String,String> newParamMap = convertRequestParamMap(parameterMap);
		if(newParamMap == null){
			log.error("合作商请求参数为空，请联系方合作商技术人员。" );
			throw new BusinessException("方合作商请求参数为空，请联系方合作商技术人员。");
		}
		String [] ucParamArr =null;
		Set<String> newParamSet = newParamMap.keySet();
		ucParamArr= newParamSet.toArray(new String[newParamSet.size()]);
		//对数组进行排序
		Arrays.sort(ucParamArr);
		String sortedParam =getParamStr(ucParamArr,newParamMap);
		if(interFaceName == null || "".equals(interFaceName)){
			log.error("方合作商调用的API为空，" );
			throw new BusinessException("方合作商调用的API为空");
		}
		
		if(testSecureKye == null || "".equals(testSecureKye)){
			log.error("方合作商安全key为空" );
			throw new BusinessException("方合作商ID为空");
		}
		
		//构建加密前的字符串，api+参数串+安全key
		StringBuilder waitingEncrypStringSB  = new StringBuilder();
		waitingEncrypStringSB.append(interFaceName)
		.append(sortedParam)
		.append(testSecureKye);
		 //进行Md5加密
		log.debug("加密前字符串："+waitingEncrypStringSB.toString());
		String encryptionStr =  EncoderHandler.encode("SHA1", waitingEncrypStringSB.toString());
		
		return encryptionStr;
	}
	
}
