package com.thinkwin.common.utils.wechat;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 类名: MyX509TrustManager </br>
 * 描述: 信任管理器  </br>
 * 开发人员： weining </br>
 * 创建时间：  2017/4/27 </br>
 */
public class WechatX509TrustManager implements X509TrustManager {

    // 检查客户端证书
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    // 检查服务器端证书
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    // 返回受信任的X509证书数组
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
