package com.thinkwin.common.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 
 * 摘要算法
 *
 */
public class EncoderHandler {

	private static final String ALGORITHM = "MD5";

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * encode string
	 * 
	 * @param algorithm
	 * @param str
	 * @return String
	 */
	public static String encode(String algorithm, String str) {
		if (str == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(str.getBytes());
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * encode By MD5
	 * 
	 * @param str
	 * @return String
	 */
	public static String encodeByMD5(String str) {
		if (str == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
			messageDigest.update(str.getBytes());
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Takes the raw bytes from the digest and formats them correct.
	 * 
	 * @param bytes
	 *            the raw bytes from the digest.
	 * @return the formatted bytes.
	 */
	private static String getFormattedText(byte[] bytes) {
		int len = bytes.length;
		StringBuilder buf = new StringBuilder(len * 2);
		// 把密文转换成十六进制的字符串形式
		for (int j = 0; j < len; j++) {
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}
	/**
	 * des  加密
	 * @param datasource
	 * @param password   长度要是8的倍数
	 * @return
	 */
	public static byte[] desCrypto(byte[] datasource, String password) {              
        try{  
        SecureRandom random = new SecureRandom();  
        DESKeySpec desKey = new DESKeySpec(password.getBytes());  
        //创建一个密匙工厂，然后用它把DESKeySpec转换成  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
        SecretKey securekey = keyFactory.generateSecret(desKey);  
        //Cipher对象实际完成加密操作  
        Cipher cipher = Cipher.getInstance("DES");  
        //用密匙初始化Cipher对象  
        cipher.init(Cipher.ENCRYPT_MODE, securekey, random);  
        //现在，获取数据并加密  
        //正式执行加密操作  
        return cipher.doFinal(datasource);  
        }catch(Throwable e){  
                e.printStackTrace();  
        }  
        return null;  
	}  
	
	/**
	 * des  解密
	 * @param src
	 * @param password  αβΣΩγΔηλ
	 * @return
	 * @throws Exception
	 */
	private static byte[] decrypt(byte[] src, String password) throws Exception {  
        // DES算法要求有一个可信任的随机数源  
        SecureRandom random = new SecureRandom();  
        // 创建一个DESKeySpec对象  
        DESKeySpec desKey = new DESKeySpec(password.getBytes());  
        // 创建一个密匙工厂  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
        // 将DESKeySpec对象转换成SecretKey对象  
        SecretKey securekey = keyFactory.generateSecret(desKey);  
        // Cipher对象实际完成解密操作  
        Cipher cipher = Cipher.getInstance("DES");  
        // 用密匙初始化Cipher对象  
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);  
        // 真正开始解密操作  
        return cipher.doFinal(src);  
	}  

//	public static void main(String[] args) {
//		String string1= "appid=wxf8b4f85f3a794e77&appkey=2Wozy2aksie1puXUBpWD8oZxiD1DfQuEaiC7KcRATv1Ino3mdopKaPGQQ7TtkNySuAmCaDCrw4xhPY5qKTBl7Fzm0RgR3c0WaVYIXZARsxzHV2x7iwPPzOz94dnwPWSn&noncestr=adssdasssd13d&productid=123456&timestamp=189026618";
//
//		System.out.println("111111 MD5  :"
//				+ EncoderHandler.encodeByMD5("bank_type=WX&body=XXX&fee_type=1&input_charset=GBK&notify_url=http://www.qq.com&out_trade_no=16642817866003386000&partner=1900000109&spbill_create_ip=127.0.0.1&total_fee=1&key=8934e7d15453e97507ef794cf7b0519d").toUpperCase());
//		System.out.println("111111 MD5  :"
//				+ EncoderHandler.encode("MD5", "111111"));
//		System.out.println("111111 SHA1 :"
//				+ EncoderHandler.encode("SHA1", string1));
////	微信SHA1结果是（18c6122878f0e946ae294e016eddda9468de80df）
//	}

}