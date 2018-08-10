package com.thinkwin.pay.util;

import java.io.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class CryptoUtil {
	public static String getSignContent(Map<String, String> params) {
		StringBuffer content = new StringBuffer();
		List<String> keys = new ArrayList(params.keySet());
		Collections.sort(keys);
		int index = 0;

		for(int i = 0; i < keys.size(); ++i) {
			String key = keys.get(i);
			String value = params.get(key);
			if (areNotEmpty(key, value)) {
				content.append((index == 0 ? "" : "&") + key + "=" + value);
				++index;
			}
		}

		return content.toString();
	}

	public static boolean rsaCheck(Map<String, String> params, String publicKey, String charset) throws Exception {
		String sign = params.get("sign");
		String content = getSignCheckContent(params);
		return rsaCheckContent(content, sign, publicKey, charset);
	}

	public static boolean rsaCheckContent(String content, String sign, String publicKey, String charset) throws Exception {
		PublicKey pubKey = getPublicKeyFromX509("RSA", new ByteArrayInputStream(publicKey.getBytes()));
		Signature signature = Signature.getInstance("SHA1WithRSA");
		signature.initVerify(pubKey);
		if (isEmpty(charset)) {
			signature.update(content.getBytes());
		} else {
			signature.update(content.getBytes(charset));
		}

		return signature.verify(Base64.getDecoder().decode(sign.getBytes()));
	}

	public static String genRandomStr() {
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();

		for(int i = 0; i < 16; ++i) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}

		return sb.toString();
	}

	private static String getSignCheckContent(Map<String, String> params) {
		if (params == null) {
			return null;
		} else {
			params.remove("sign");
			StringBuffer content = new StringBuffer();
			List<String> keys = new ArrayList(params.keySet());
			Collections.sort(keys);

			for(int i = 0; i < keys.size(); ++i) {
				String key = (String)keys.get(i);
				String value = (String)params.get(key);
				content.append((i == 0 ? "" : "&") + key + "=" + value);
			}

			return content.toString();
		}
	}

	private static boolean areNotEmpty(String... values) {
		boolean result = true;
		if (values != null && values.length != 0) {
			String[] paramArr = values;
			int len = values.length;

			for(int i = 0; i < len; ++i) {
				String value = paramArr[i];
				result &= !isEmpty(value);
			}
		} else {
			result = false;
		}

		return result;
	}

	private static boolean isEmpty(String value) {
		int strLen;
		if (value != null && (strLen = value.length()) != 0) {
			for(int i = 0; i < strLen; ++i) {
				if (!Character.isWhitespace(value.charAt(i))) {
					return false;
				}
			}

			return true;
		} else {
			return true;
		}
	}

	public static String rsaSign(String content, String privateKey, String charset) throws Exception {
		PrivateKey priKey = getPrivateKeyFromPKCS8("RSA", new ByteArrayInputStream(privateKey.getBytes()));
		Signature signature = Signature.getInstance("SHA1WithRSA");
		signature.initSign(priKey);
		if (isEmpty(charset)) {
			signature.update(content.getBytes());
		} else {
			signature.update(content.getBytes(charset));
		}

		byte[] signed = signature.sign();
		return new String(Base64.getEncoder().encode(signed));
	}

	private static PrivateKey getPrivateKeyFromPKCS8(String algorithm, InputStream ins) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		byte[] encodedKey = readText(ins).getBytes();
		encodedKey = Base64.getDecoder().decode(encodedKey);
		return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
	}

	private static String readText(InputStream in) throws IOException {
		return readText(in, (String)null, -1);
	}

	private static String readText(InputStream in, String encoding, int bufferSize) throws IOException {
		Reader reader = encoding == null ? new InputStreamReader(in) : new InputStreamReader(in, encoding);
		return readText(reader, bufferSize);
	}

	private static String readText(Reader reader, int bufferSize) throws IOException {
		StringWriter writer = new StringWriter();
		io(reader, writer, bufferSize);
		return writer.toString();
	}

	private static void io(Reader in, Writer out, int bufferSize) throws IOException {
		if (bufferSize == -1) {
			bufferSize = 4096;
		}

		char[] buffer = new char[bufferSize];

		int amount;
		while((amount = in.read(buffer)) >= 0) {
			out.write(buffer, 0, amount);
		}

	}

	private static PublicKey getPublicKeyFromX509(String algorithm, InputStream ins) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		StringWriter writer = new StringWriter();
		io(new InputStreamReader(ins), writer);
		byte[] encodedKey = writer.toString().getBytes();
		encodedKey = Base64.getDecoder().decode(encodedKey);
		return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
	}

	private static void io(Reader in, Writer out) throws IOException {
		io(in, out, -1);
	}
}
