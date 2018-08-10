package com.thinkwin.common.utils.createDatabase;

import com.alibaba.druid.filter.config.ConfigTools;

/**
 * 第二步：
 * 使用私钥对明文密码进行加密
 */
public class ConfigToolsDemo {
    //上述生成的私钥
    private static final String PRIVATE_KEY_STRING = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKMOGb8kjfO2arvOhtnZU+0IpG9SFp77klygV7ZOOriosU5GnFv+YFSEwGD928xhxaTJkyeqvQq5I6rfflO5B+jPQVMzp4DvX7uoPauej6+8spSy8csEqb4ATMIU3gEGcwTgCLliKPxJ5pgquqhw/yq92n8Rh0/FN7wfnPniLZPBAgMBAAECgYAFVEqnIqy5EHdAmCl0KVRC5Qtq0AYJQDOyb1MulDP7IRhadJhbsV64pHYdNW7fqTpqfmQe4ce3+AEgbpmUZvHJLtGymvs8UBnEzPAkQZrLLwq6lSGKErJvnSqUmX3WtVnKgilphaJIdfasflvzqNt1CPZcIXiFPZez/DjBnYQIeQJBAM7ywhrKpAM8QLsvLWPU2m3RA2v/lUzMwz44LoACkjh0LNPbYEhS/DlwEa8C8jAuO3BMbHcaX7LQIhOVh0hEFI8CQQDJs/vcpX4bdOGfvNdq41+5vM1KRykFfJ0bNwZOh7+xfcj+j92jD9V82LYqZQFzW1hs8Kk2MdAHS+zCjv8eIxqvAkEAlo64NoOchRoNweCVth6B9wjr42Ni/CewF5EKC2lTdazXfB4K0zNEeokU5G1RQ0ovRgjjlFbkrpOLZxIPBL068wJANAwEbqAplU4otj5VW9iGJ/hnBMat+CN6qr4dG4B933/0sHNRHrWEQi3te60KncixXZMQJtLdujRGSMH7ZLxsMwJAG7XM20Tr87pNIsA0/7VxHA3MJOoxqdOizc4Z5/U0eeLrxtdE87Poj9X2oWQ64d6AxkDFFDVdtlGor40sQZ/8yg==";

    public static void main(String[] args) {
        try {
            //密码明文，也就是数据库的密码
            String plainText = "yunmeeting";
            System.out.printf("数据库明文密码生成密文：" + ConfigTools.encrypt(PRIVATE_KEY_STRING, plainText));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
