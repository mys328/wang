package com.thinkwin.pay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinkwin.pay.model.PayChannel;
import com.thinkwin.pay.model.PayStatus;
import com.thinkwin.pay.util.CryptoUtil;
import org.junit.Assert;
import org.junit.Test;

public class UtilTest {

	@Test
	public void testEnum() throws Exception {
		PayStatus status = PayStatus.valueOf("NOT_LAUNCHED");
		status = PayStatus.fromCode(2);
	}

	@Test
	public void testSign() throws Exception {
		String content = "orderId=8635440c48774aaaa445f43ac7a0e3e1;appId=daemon;tenantId=96436bd003404b85b0d604d2b5e0548b;tradeNo=17091310514;payChannel=1;timestamp=1505268904606;nonceStr=RF6buIV0DPqr8a2t";
		String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKte9GD/UJQD8Sz7qSTLBNqb/uIaBHr0OF8zomEtyJQYJin0efDuWb8dTlMAZDEXeS8mS8a30piD8pkSYiEDvnrT4P7l2GSkCEl7XI7dah6IrqG9RHffB9aRycG58+zY0DqcFac7Y8pOiOnhsEDLEpW1VwM6HWlhnpQAOXpyYt8PAgMBAAECgYBSuLQ4OiYYPMrnTtd39jPynohrdemhvE+VzOPeCu6eZgXODL/sB/H0ad4L/M1gURdjJhY/5y3WByNMtm39x8Pi1WTPfY6IyHt4xkgfWUB3+exN5ZQ16GMC15fk+c7AAQucRaGKULxaAeXLka8eI1HZoAN5FQbnI7E+ouOfyY7u2QJBAOVt0geV9fnFMJPrbvt8BZVrm1b6H/t6Xr5p2Mgg5CLrQOrwOwf/i5Ujv/tlsCJTL84319hdXI2bpPxUSSn6UiMCQQC/N8+iQT+AxVMbHLFEDPvjOou0fEboCVGkLKP+Fh7ozFLZ3C3+NB00MHqf3+TOt4ItrYETRWsCeW9G8izYxwAlAkAKpjhThdBvEYoZs5npLm6L6vzA4sdNQvbW287mKzCrtkPDI/d5fZEbERe+MZAlZ7sWVV09e8fqAsl7tOXYfvgFAkBPvPVPwwe5KhAC0U82gneThPl3FQ/4eNJbtOXZ8d2H1JSEFuXzCEZZWhH4k0P1095sL9sQbzU1ffAiVKDQ8QMlAkADWN9WQDDuvIRveFxcJcgzL4xbQ+sDMr/fGXyrz7DDXrWhwlK+1Zq3irRcf/xt96pTIRemmuKcDC/JiIH27xL7";
		String sign = CryptoUtil.rsaSign(content, privateKey, "utf-8");

		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCrXvRg/1CUA/Es+6kkywTam/7iGgR69DhfM6JhLciUGCYp9Hnw7lm/HU5TAGQxF3kvJkvGt9KYg/KZEmIhA7560+D+5dhkpAhJe1yO3WoeiK6hvUR33wfWkcnBufPs2NA6nBWnO2PKTojp4bBAyxKVtVcDOh1pYZ6UADl6cmLfDwIDAQAB";
		boolean valid =  CryptoUtil.rsaCheckContent(content, sign, publicKey, "utf-8");

		Assert.assertTrue(valid);
	}

}
