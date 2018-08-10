package com.thinkwin.common.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 类名: CreateRSAUtil </br>
 * 描述: 生成RSA密钥工具类</br>
 * 开发人员： weining </br>
 * 创建时间：  2018/4/17 </br>
 */
public class CreateRSAUtil {
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 方法名：genKeyPair</br>
     * 描述：生成密钥对</br>
     * 参数：[keySize key长度, keyAlgorithm key类型]</br>
     * 返回值：java.security.KeyPair</br>
     */
    public static KeyPair genKeyPair(int keySize, String keyAlgorithm) throws Exception{
        KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance(keyAlgorithm);
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.generateKeyPair();
    }

    //加密
    public static byte[] encrypt(byte[] content, Key key) throws Exception{
        Cipher cipher=Cipher.getInstance(KEY_ALGORITHM);//java默认"RSA"="RSA/ECB/PKCS1Padding"
        cipher.init(Cipher.ENCRYPT_MODE, key);
        int inputLen = content.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > 117) {
                cache = cipher.doFinal(content, offSet, 117);
            } else {
                cache = cipher.doFinal(content, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * 117;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    //解密
    public static byte[] decrypt(byte[] encrypt, Key key) throws Exception{
        Cipher cipher=Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        int inputLen = encrypt.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > 128) {
                cache = cipher.doFinal(encrypt, offSet, 128);
            } else {
                cache = cipher.doFinal(encrypt, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * 128;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }


    //字符串转byte
    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    //RSA转字符串
    public static String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }

    //将字符串公钥转成PublicKey实例
    public static PublicKey getPublicKey(String publicKey) throws Exception{
        byte[ ] keyBytes = decryptBASE64(publicKey);
        X509EncodedKeySpec keySpec=new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory=KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    //将字符串私钥转成PrivateKey实例
    public static PrivateKey getPrivateKey(String privateKey) throws Exception{
        byte[ ] keyBytes=decryptBASE64(privateKey);
        PKCS8EncodedKeySpec keySpec=new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory=KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    //将公钥modulus和exponent转成PublicKey实例
    public static PublicKey getPublicKey(BigInteger modulusStr, BigInteger exponentStr) throws Exception{
        RSAPublicKeySpec publicKeySpec=new RSAPublicKeySpec(modulusStr, exponentStr);
        KeyFactory keyFactory=KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicKeySpec);
    }
    //将私钥modulus和exponent转成PrivateKey实例
    public static PrivateKey getPrivateKey(BigInteger modulusStr, BigInteger exponentStr) throws Exception{
        RSAPrivateKeySpec privateKeySpec=new RSAPrivateKeySpec(modulusStr, exponentStr);
        KeyFactory keyFactory=KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(privateKeySpec);
    }

    public static void main(String [] args){
        String content = "hello world!";
        try {
            //生成密钥对
            KeyPair rsa = CreateRSAUtil.genKeyPair(1024, "RSA");
            //获取公钥
            RSAPublicKey publicKey = (RSAPublicKey) rsa.getPublic();
            //获取私钥
            RSAPrivateKey privateKey = (RSAPrivateKey) rsa.getPrivate();
            //公钥加密
            //byte[] encrypt = CreateRSAUtil.encrypt(content.getBytes(), publicKey);
            //System.out.println("公钥加密后：" + new String(encrypt));
            //私钥解密
            //byte[] decrypt = CreateRSAUtil.decrypt(encrypt, privateKey);
            //System.out.println("私钥解密后：" + new String(decrypt));

            //把公钥转成字符串类型
            String publicKeyString = CreateRSAUtil.encryptBASE64(publicKey.getEncoded());
            System.out.println("公钥转字符串后：" + publicKeyString);
            //把私钥转成字符串类型
            String privateKeyString = CreateRSAUtil.encryptBASE64(privateKey.getEncoded());
            System.out.println("私钥转成字符串后：" + privateKeyString);

            //把字符串公钥转成公钥类型
            //PublicKey publicKey1 = CreateRSAUtil.getPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMZq2vuCIBoW3OgU4622+mKtxSLSziRMGJ1MsB\r\nh/lOMQO/DwVWIIx4NJQR/bArH9UFRCjuDbfDcXurb32SqJL01lotkxyBxlM2P9XmnJrf3PrH/SUH\r\nLw58+VIlqK2rSylggzaCrRsFI9owuWYoDjqiYCZosCnMK1MvpXMEDMRjOwIDAQAB\r\n");
            PublicKey publicKey1 = CreateRSAUtil.getPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCCCyzIsbSIQSooRU6G+BNj9KG4EhDXkfR9hXWH\r\npXXF6EbwlByeIdh1xBeO9d+7jCdo53aVzjeYfPwmWcnIKbI4ClztIOAI0sdiGjK4rZS5BPiNoRjA\r\nBEOn1N6o57LkkD/fLEQo/m46VM3LllzBl0x7eD7aXPCDp3qSN73k0/UgDQIDAQAB\r\n");
            //把字符串私钥转成私钥类型
            PrivateKey privateKey1 = CreateRSAUtil.getPrivateKey("MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIILLMixtIhBKihFTob4E2P0obgS\r\n" +
                    "ENeR9H2FdYeldcXoRvCUHJ4h2HXEF47137uMJ2jndpXON5h8/CZZycgpsjgKXO0g4AjSx2IaMrit\r\n" +
                    "lLkE+I2hGMAEQ6fU3qjnsuSQP98sRCj+bjpUzcuWXMGXTHt4Ptpc8IOnepI3veTT9SANAgMBAAEC\r\n" +
                    "gYAxQHQ8qnkaZbvGtsEQAyKs57jcExOH4YJ8i7BAqJzbUKaVdTMFfQITUCov/dU3ndRLOebCw/Bw\r\n" +
                    "c124VKdEhO43Gr1jB8lykrb4M1KQYL3aeJ1/jF7AHYQGC1eIlOr2jx8bWS3nSHUqgCpLBx6nV6A2\r\n" +
                    "UDTZ2D+D7XQCSQAOlzbCQQJBAMQFId9/sZuBUYjj39PMA9w6onl5kZSYRs/JcOUMKZvY1H162txp\r\n" +
                    "FMLwosrP0Hdd1DlW1HYm81uRuqrGMxDoj50CQQCp1edb23v7rLgk3D4hKex89GpOJBSjdtL77bUN\r\n" +
                    "ADKv8VCLuhuvI8zI5pWPiZ22uav0cEykpri/bN9L16dKYj8xAkEAwFdU5IzVBwS+r7cCIz4WL5wt\r\n" +
                    "X0sFqaw+F5fPOkFT9sOtxa6uYQaPQ7WB18ZRBT/Bhb4I3iLcOmblprqy0w2QFQJAOD/6crgNJlwc\r\n" +
                    "WUkA4ucJxpEFLIBSvZk7Y+llD7tDDnb0YHLxfF51Ew0b+AByVfTE+R7Ao7/8bJpYkrwIUmCb4QJA\r\n" +
                    "STnudhsD2KiNEMQVwYGgR+AHyD5PpkzqY0F7XQlHQ4m5RyJrLySnOTuw18DLxy4KavT4H7qhZyLd\r\n" +
                    "D1xqsksuSQ==\r\n");
            //公钥加密
            byte[] encrypt = CreateRSAUtil.encrypt(content.getBytes(), publicKey1);
            System.out.println("公钥加密后：" + encryptBASE64(encrypt));
            byte[] bytes = decryptBASE64("euMZ0FzjiCcDQj/SS2/hD2Q/d6VZxAziNkkzlwftoKIll2Xm2GifUSBp/aG1f5c62NLXSN5FrZN5\n" +
                    "pP5sPUHyFHHV9SzOnd7810riJHoSaHyh/Ub/0jQytwf3I/jhyY2Vd28Dety2HOr1+cRPPSDWgDwV\n" +
                    "uM7z6fEGJx7vxRtECj8=");
            //私钥解密
            byte[] decrypt = CreateRSAUtil.decrypt(bytes, privateKey1);
            System.out.println("私钥解密后：" + new String(decrypt));

            //将公钥生成modulus和exponent
            BigInteger publicKeyModulus=publicKey.getModulus();
            BigInteger publicKeyExponent=publicKey.getPublicExponent();
            System.out.println("公钥生成modulus：" + publicKeyModulus);
            System.out.println("公钥生成exponent：" + publicKeyExponent);
            //将私钥生成modulus和exponent
            BigInteger privateKeyModulus=privateKey.getModulus();
            BigInteger privateExponent=privateKey.getPrivateExponent();
            System.out.println("私钥生成modulus：" + privateKeyModulus);
            System.out.println("私钥生成exponent：" + privateExponent);

            //将公钥modulus和exponent转成PublicKey实例
            PublicKey publicKey2 = CreateRSAUtil.getPublicKey(publicKeyModulus, publicKeyExponent);
            //将私钥modulus和exponent转成PrivateKey实例
            PrivateKey privateKey2 = CreateRSAUtil.getPrivateKey(privateKeyModulus, privateExponent);
            //公钥加密
            //byte[] encrypt = CreateRSAUtil.encrypt(content.getBytes(), publicKey2);
            //System.out.println("公钥加密后：" + new String(encrypt));
            //私钥解密
            //byte[] decrypt = CreateRSAUtil.decrypt(encrypt, privateKey2);
            //System.out.println("私钥解密后：" + new String(decrypt));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
