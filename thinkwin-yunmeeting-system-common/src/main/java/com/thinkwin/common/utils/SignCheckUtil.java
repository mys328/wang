package com.thinkwin.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thinkwin.common.businessException.BusinessException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *@功能：渠道合作sign验证类
 *@author 杨嶷岍
 *@Time 2012-10-30 下午04:42:40
 */
public class SignCheckUtil {

	private static final Logger logging = LoggerFactory.getLogger(SignCheckUtil.class);
	//调用的API
	private String partnerInvokApi;
	//信发终端安全key
	private String parnterKey;
	//请求参数Map
	private Map<String,String[]> parameterMap;
	//构造方法标识
	private int  construction=0;


	public SignCheckUtil() {}

	public SignCheckUtil(String parnterKey, Map<String,String[]> parameterMap) {
		this.construction=2;
		this.parnterKey = parnterKey;
		this.parameterMap =parameterMap;
	}

	public SignCheckUtil(String partnerInvokApi, String parnterKey, Map<String,String[]> parameterMap) {
		this.construction=3;
		this.partnerInvokApi = partnerInvokApi;
		this.parnterKey = parnterKey;
		this.parameterMap =parameterMap;
	}

	/**
	 * 功能：检测渠道合作方的Sign
	 * @author 杨嶷岍
	 * @return
	 */

	public boolean checkSecureKey() {
		boolean isOK= false;
		Map<String,String> newParamMap = convertRequestParamMap(this.parameterMap);
		if(newParamMap == null){
			logging.error("信发终端请求参数为空，请联系信发终端技术人员。" );
			throw new BusinessException("信发终端请求参数为空，请联系信发终端技术人员。");
		}
		String [] ucParamArr =null;
		Set<String> newParamSet = newParamMap.keySet();
		ucParamArr= newParamSet.toArray(new String[newParamSet.size()]);
		//对数组进行排序
		Arrays.sort(ucParamArr);
		String sortedParam =getParamStr(ucParamArr,newParamMap);
		if(construction == 3){
			if(this.partnerInvokApi == null || "".equals(partnerInvokApi)){
				logging.error("信发终端调用的API为空" );
				throw new BusinessException("信发终端调用的API为空");
			}
		}
		//this.partnerInvokApi="";
		if(this.parnterKey == null || "".equals(parnterKey)){
			logging.error("信发终端安全key为空，请检查系统配置" );
			throw new BusinessException("信发终端ID为空，请检查系统配置");
		}

		//构建加密前的字符串，api+参数串+安全key
		StringBuilder waitingEncrypStringSB  = new StringBuilder();
		if(StringUtils.isNotBlank(partnerInvokApi)) {
			waitingEncrypStringSB.append(this.partnerInvokApi)
					.append(sortedParam)
					.append(this.parnterKey);
		}else{
			waitingEncrypStringSB.append(sortedParam)
					.append(this.parnterKey);
		}
		//进行SHA1加密
		logging.info("加密前字符串："+waitingEncrypStringSB.toString());
		String encryptionStr = EncoderHandler.encode("SHA1", waitingEncrypStringSB.toString());
		//加密后的字条串与UC传过来的加密串进行比较，判断是否合乎权限
		String signStr = this.parameterMap.get("sign")[0];
		if(encryptionStr.equals(signStr)){
			isOK =true;
		}
		logging.info("加密串："+encryptionStr);
		return isOK;
	}
	/**
	 * 功能：合并参数字符串
	 * @author 杨嶷岍
	 * @param arr 参数数组
	 * @return
	 */
	private String getParamStr(String [] arr,Map paramMap){
		StringBuilder sb = new StringBuilder();
		if(arr != null && arr.length >0){
			for (int i = 0; i < arr.length; i++) {
				sb.append(arr[i]+paramMap.get(arr[i]));
			}
		}
		return sb.toString();
	}

	/**
	 * 功能：转换合作方参数Map为适合sign排序格式的新Map
	 * @author 杨嶷岍
	 * @param parameterMap 请求过来的参数Map
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String,String> convertRequestParamMap(Map parameterMap) {
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
	//加密串，上线前删掉，测试使用
	//删除开始
	/**
	 * 提供给测试的sign值方法，上线后删除
	 * @return
	 */

	public String checkSecureKeyTest() {
		Map<String,String> newParamMap = convertRequestParamMap(this.parameterMap);
		if(newParamMap == null){
			logging.error("信发终端请求参数为空，请联系信发终端技术人员。" );
			throw new BusinessException("信发终端请求参数为空，请联系信发终端技术人员。");
		}
		String [] ucParamArr =null;
		Set<String> newParamSet = newParamMap.keySet();
		ucParamArr= newParamSet.toArray(new String[newParamSet.size()]);
		//对数组进行排序
		Arrays.sort(ucParamArr);
		String sortedParam =getParamStr(ucParamArr,newParamMap);
		if(construction == 3){
			if(this.partnerInvokApi == null || "".equals(partnerInvokApi)){
				logging.error("信发终端调用的API为空" );
				throw new BusinessException("信发终端调用的API为空");
			}
		}
		//this.partnerInvokApi="";
		if(this.parnterKey == null || "".equals(parnterKey)){
			logging.error("信发终端安全key为空，请检查系统配置" );
			throw new BusinessException("信发终端ID为空，请检查系统配置");
		}

		//构建加密前的字符串，api+参数串+安全key
		StringBuilder waitingEncrypStringSB  = new StringBuilder();
		waitingEncrypStringSB.append(this.partnerInvokApi)
				.append(sortedParam)
				.append(this.parnterKey);
		//进行SHA1加密
		logging.info("加密前字符串："+waitingEncrypStringSB.toString());
		String encryptionStr = EncoderHandler.encode("SHA1", waitingEncrypStringSB.toString());
		logging.info("加密串："+encryptionStr);
		return encryptionStr;
	}
	//  删除结束
	public String getPartnerInvokApi() {
		return partnerInvokApi;
	}

	public void setPartnerInvokApi(String partnerInvokApi) {
		this.partnerInvokApi = partnerInvokApi;
	}

	public String getCooperatorSecureKey() {
		return parnterKey;
	}


	public void setCooperatorSecureKey(String cooperatorSecureKey) {
		this.parnterKey = cooperatorSecureKey;
	}

	public Map<String, String[]> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<String, String[]> parameterMap) {
		this.parameterMap = parameterMap;
	}


	/**
	 *@功能：生成sign 这是生成sign的测试类
	 *@author 杨嶷岍
	 *@Time 2012-11-5 下午02:50:22
	 *@return
	 */
	public String generateSign(String testSecureKye,String interFaceName,Map<String, String[]> parameterMap){

		Map<String,String> newParamMap = convertRequestParamMap(parameterMap);
		if(newParamMap == null){
			logging.error("第三方信发终端请求参数为空，请联系第三方信发终端技术人员。" );
			throw new BusinessException("第三方信发终端请求参数为空，请联系第三方信发终端技术人员。");
		}
		String [] ucParamArr =null;
		Set<String> newParamSet = newParamMap.keySet();
		ucParamArr= newParamSet.toArray(new String[newParamSet.size()]);
		//对数组进行排序
		Arrays.sort(ucParamArr);
		String sortedParam =getParamStr(ucParamArr,newParamMap);
		if(interFaceName == null || "".equals(interFaceName)){
			logging.error("第三方信发终端调用的API为空，" );
			throw new BusinessException("第三方信发终端调用的API为空");
		}

		if(testSecureKye == null || "".equals(testSecureKye)){
			logging.error("第三方信发终端安全key为空" );
			throw new BusinessException("第三方信发终端ID为空");
		}

		//构建加密前的字符串，api+参数串+安全key
		StringBuilder waitingEncrypStringSB  = new StringBuilder();
		waitingEncrypStringSB.append(interFaceName)
				.append(sortedParam)
				.append(testSecureKye);
		//进行Md5加密
		logging.debug("加密前字符串："+waitingEncrypStringSB.toString());
		String encryptionStr = EncoderHandler.encode("SHA1", waitingEncrypStringSB.toString());
		//String encryptionStr = getMd5String(waitingEncrypStringSB.toString());
		return encryptionStr;
	}
	/**
	 *@功能：生成sign 这是生成sign的测试类
	 *@author 尹春磊
	 *@Time 2018-5-23 下午15:15:22
	 *@return
	 */
	public String generateSign(String testSecureKye,Map<String, String[]> parameterMap){

		Map<String,String> newParamMap = convertRequestParamMap(parameterMap);
		if(newParamMap == null){
			logging.error("第三方信发终端请求参数为空，请联系第三方信发终端技术人员。" );
			throw new BusinessException("第三方信发终端请求参数为空，请联系第三方信发终端技术人员。");
		}
		String [] ucParamArr =null;
		Set<String> newParamSet = newParamMap.keySet();
		ucParamArr= newParamSet.toArray(new String[newParamSet.size()]);
		//对数组进行排序
		Arrays.sort(ucParamArr);
		String sortedParam =getParamStr(ucParamArr,newParamMap);

		if(testSecureKye == null || "".equals(testSecureKye)){
			logging.error("第三方信发终端安全key为空" );
			throw new BusinessException("第三方信发终端ID为空");
		}

		//构建加密前的字符串，参数串+安全key
		StringBuilder waitingEncrypStringSB  = new StringBuilder();
		waitingEncrypStringSB.append(sortedParam)
				.append(testSecureKye);
		//进行Md5加密
		logging.debug("加密前字符串："+waitingEncrypStringSB.toString());
		String encryptionStr = EncoderHandler.encode("SHA1", waitingEncrypStringSB.toString());
		return encryptionStr;
	}


	public static void main(String[] args) {
		//String orderInterface="payCallBack";
		String secretKey ="566d4c762431455cb239e7eb19c0fb60";
		SignCheckUtil ss =new SignCheckUtil();
		ss.setCooperatorSecureKey(secretKey);
		//ss.setPartnerInvokApi(orderInterface);

		Map<String, String[]> paramMap = new HashMap<String, String[]>();
		paramMap.put("programId", new String[]{"2c19bf699cee4c3c83d678af3e9e0f7b"});
		paramMap.put("tenantId", new String[]{"59dc00f202f145c29e9719475dbfe8a9"});
//		paramMap.put("amount", new String[]{"15.6"});
//		paramMap.put("status", new String[]{"success"});

		System.out.println(ss.generateSign(secretKey,paramMap));

//
//		String actionPath="payment";
//		String ver="v1";
//		String apiName="payCallBack";
//		StringBuilder urlSb = new StringBuilder();
//		urlSb.append("http://localhost:8080/").append(actionPath)
//				.append("/").append(ver).append("/").append(apiName).append("?");
//		Set<String> paramSet  = paramMap.keySet();
//		List<String> paramList = new ArrayList<String>(paramSet);
//
//		for (int i = 0; i < paramList.size(); i++) {
//			String str  =  paramList.get(i);
//			String[] strArr = paramMap.get(str);
//			urlSb.append(str).append("=").append(strArr[0]).append("&");
//		}

		//urlSb.append("sign=").append(ss.generateSign(secretKey, orderInterface, paramMap));
		//System.out.println(urlSb.toString());

		/*String sign=ss.getMd5String("orderstatusxxx中文yyy3zzz2" + secretKey);
		System.out.println(sign);*/

	}
}
