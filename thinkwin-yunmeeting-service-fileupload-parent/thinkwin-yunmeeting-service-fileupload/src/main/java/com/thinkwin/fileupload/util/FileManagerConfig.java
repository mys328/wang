package com.thinkwin.fileupload.util;

import com.thinkwin.common.utils.FileOperateUtil;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * fastdfs 图片实现工具类
 */
public class FileManagerConfig implements Serializable {


    public static String basePath(){
        Properties properties = new Properties();
        try {
            InputStream is = FileOperateUtil.class.getClassLoader().getResourceAsStream("server.properties");
            properties.load(is);
        } catch (Exception e) {
            e.getStackTrace();
        }
        String path = properties.getProperty("fastdfs.path");
        return path;
    }

    public static final String FILE_DEFAULT_AUTHOR = "";

  /*  public static final String PROTOCOL = "http://";*/

    public static final String SEPARATOR = "/";

    public static final String TRACKER_NGNIX_ADDR = basePath();

    public static final String TRACKER_NGNIX_PORT = "";

    public static final String CLIENT_CONFIG_FILE = "fdfs_client.conf";
    private static final long serialVersionUID = 4331984835738044372L;
}
