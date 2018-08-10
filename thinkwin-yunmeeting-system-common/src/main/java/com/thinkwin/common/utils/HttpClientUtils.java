package com.thinkwin.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * httpClient请求的工具类
 * @author yinchunlei
 * @date 2016年9月14日
 */
public class HttpClientUtils {
	private static Logger logger = Logger.getLogger(HttpClientUtils.class);
	/**
	 * 发送 get请求
	 */
	public static String get(String url) {  
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String jsonStr = null;
        try {  
            // 创建httpget.    
           // HttpGet httpget = new HttpGet("http://115.29.109.91:8080/data/follow-clubs?ids=" + idsStr);  
           // HttpGet httpget = new HttpGet("http://192.168.0.101:8888/follow-clubs?ids=" + idsStr); 
            HttpGet httpget = new HttpGet(url); 
            // System.out.println("executing request " + httpget.getURI());  
            // 执行get请求.    
            CloseableHttpResponse response = httpclient.execute(httpget);  
            try {  
                // 获取响应实体    
                HttpEntity entity = response.getEntity();
                // System.out.println("--------------------------------------");  
                // 打印响应状态 
               System.out.println(response.getStatusLine());  
                if (entity != null) {  
                    // 打印响应内容长度    
                    //System.out.println("Response content length: " + entity.getContentLength());  
                    // 打印响应内容    
                    // System.out.println("Response content: " + EntityUtils.toString(entity));
                	jsonStr = EntityUtils.toString(entity);
                }  
               // System.out.println("------------------------------------");  
            } finally {  
                response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // 关闭连接,释放资源    
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return jsonStr;
    }

    /**********************************************************************************/

    private static HttpClient client = null;
    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(128);
        cm.setDefaultMaxPerRoute(128);
        client = HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * 发送一个 Post 请求, 使用指定的字符集编码.
     *
     * @param url
     * @param body
     *            RequestBody
     * @param mimeType
     *            例如 application/xml
     * @param charset
     *            编码
     * @param connTimeout
     *            建立链接超时时间,毫秒.
     * @param readTimeout
     *            响应超时时间,毫秒.
     * @return ResponseBody, 使用指定的字符集编码.
     *
     * @throws ConnectTimeoutException
     *             建立链接超时异常
     * @throws SocketTimeoutException
     *             响应超时
     * @throws Exception
     */
    public static String post(String url, String body, String mimeType,
                              String charset, Integer connTimeout, Integer readTimeout){
    	try{
        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        String result = "";
        try {
            if (StringUtils.isNotBlank(body)) {
                HttpEntity entity = new StringEntity(body, ContentType.create(
                        mimeType, charset));
                post.setEntity(entity);
            }
            // 设置参数
            Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());

            HttpResponse res;
            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(post);
            }
            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } finally {
            post.releaseConnection();
            if (url.startsWith("https") && client != null
                    && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
        return result;
    	}catch (Exception e) {
    	
    	logger.error("HttpClient请求错误！！！");
    		
		e.printStackTrace();
		}
    	return null;
    }

    /**
     * 提交form表单
     *
     * @param url
     * @param params
     * @param connTimeout
     * @param readTimeout
     * @return
     * @throws ConnectTimeoutException
     * @throws SocketTimeoutException
     * @throws Exception
     */
    public static String postForm(String url, Map<String, String> params,
                                  Map<String, String> headers, Integer connTimeout,
                                  Integer readTimeout) throws ConnectTimeoutException,
            SocketTimeoutException, Exception {

        HttpClient client = null;

        HttpPost post = new HttpPost(url);
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> formParams = new ArrayList<org.apache.http.NameValuePair>();
                Set<Entry<String, String>> entrySet = params.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry
                            .getValue()));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        formParams, Consts.UTF_8);
                post.setEntity(entity);
            }
            if (headers != null && !headers.isEmpty()) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置参数
            Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());
            HttpResponse res = null;
            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(post);
            }
            return IOUtils.toString(res.getEntity().getContent(), "UTF-8");
        } finally {
            post.releaseConnection();
            if (url.startsWith("https") && client != null
                    && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
    }

    /**
     * 发送一个 GET 请求
     *
     * @param url
     * @param charset
     * @return
     * @throws Exception
     */
    public static String get(String url, String charset) throws Exception {
        return get(url, charset, null, null);
    }

    /**
     * 发送一个 GET 请求
     *
     * @param url
     * @param charset
     * @param connTimeout
     *            建立链接超时时间,毫秒.
     * @param readTimeout
     *            响应超时时间,毫秒.
     * @return
     * @throws ConnectTimeoutException
     *             建立链接超时
     * @throws SocketTimeoutException
     *             响应超时
     * @throws Exception
     */
    public static String get(String url, String charset, Integer connTimeout,
                             Integer readTimeout) throws ConnectTimeoutException,
            SocketTimeoutException, Exception {
        HttpClient client = null;

        HttpGet get = new HttpGet(url);
        String result = "";
        try {
            // 设置参数
            Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            get.setConfig(customReqConf.build());

            HttpResponse res = null;

            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(get);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(get);
            }

            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } finally {
            get.releaseConnection();
            if (url.startsWith("https") && client != null
                    && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
        return result;
    }

    /**
     * 从 response 里获取 charset
     *
     * @param ressponse
     * @return
     */
    @SuppressWarnings("unused")
    private static String getCharsetFromResponse(HttpResponse ressponse) {
        // Content-Type:text/html; charset=GBK
        if (ressponse.getEntity() != null
                && ressponse.getEntity().getContentType() != null
                && ressponse.getEntity().getContentType().getValue() != null) {
            String contentType = ressponse.getEntity().getContentType()
                    .getValue();
            if (contentType.contains("charset=")) {
                return contentType
                        .substring(contentType.indexOf("charset=") + 8);
            }
        }
        return null;
    }

    private static CloseableHttpClient createSSLInsecureClient()
            throws GeneralSecurityException {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
                    null, new TrustStrategy() {
                        public boolean isTrusted(X509Certificate[] chain,
                                                 String authType) throws CertificateException {
                            return true;
                        }
                    }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslContext, new X509HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

                @Override
                public void verify(String host, SSLSocket ssl)
                        throws IOException {
                }

                @Override
                public void verify(String host, X509Certificate cert)
                        throws SSLException {
                }

                @Override
                public void verify(String host, String[] cns,
                                   String[] subjectAlts) throws SSLException {
                }

            });
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (GeneralSecurityException e) {
            throw e;
        }
    }

    
    public static String doPost(String url, Map<String, String> param) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建参数列表
			if (param != null) {
				List<NameValuePair> paramList = new ArrayList<>();
				for (String key : param.keySet()) {
					paramList.add(new BasicNameValuePair(key, param.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
				httpPost.setEntity(entity);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return resultString;
	}
    public static String doPost0(String url, Map<String, String> param) {
    	// 创建Httpclient对象
    	CloseableHttpClient httpClient = HttpClients.createDefault();
    	CloseableHttpResponse response = null;
    	String resultString = "";
    	try {
    		// 创建Http Post请求
    		HttpPost httpPost = new HttpPost(url);
    		// 创建参数列表
    		if (param != null) {
    			List<NameValuePair> paramList = new ArrayList<>();
    			for (String key : param.keySet()) {
    				paramList.add(new BasicNameValuePair(key, param.get(key)));
    			}
    			// 模拟表单
    			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
    			httpPost.setEntity(entity);
    		}
    		// 执行http请求
    		response = httpClient.execute(httpPost);
    		resultString = EntityUtils.toString(response.getEntity(), "utf-8");
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			response.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	return resultString;
    }

	public static String doPost(String url) {
		return doPost(url, null);
	}
	
    public static void main(String[] args) {
        try {
            String xml = IOUtils.toString(new FileInputStream(new File(
                    "D:\\hongkangtest.txt")));
            System.out
                    .println(post(
                            "https://trade.tongbanjie.com/insurance/callback/hk/redeem.htm",
                            xml, "html/text", "GBK", 10000, 10000));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**********************************************************************************/
}
