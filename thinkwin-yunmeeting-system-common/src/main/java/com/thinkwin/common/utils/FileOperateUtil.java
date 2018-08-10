package com.thinkwin.common.utils;


import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * User: yinchunlei
 * Date: 2017/8/2.
 * Company: thinkwin
 * 修改导入功能辅助类
 */
public class FileOperateUtil {

    private static final Logger logger = Logger
            .getLogger(FileOperateUtil.class);
    public static String FILEDIR = null;
    /**
     * 上传
     *
     * @throws IOException
     */
    public static String upload(MultipartFile file, String dir) throws IOException {
        Properties properties = new Properties();
        try {
            InputStream is = FileOperateUtil.class.getClassLoader().getResourceAsStream("server.properties");
            properties.load(is);
        } catch (Exception e) {
            e.getStackTrace();
        }
        String basePath = properties.getProperty("file.outputPath");
        String fileName = null;
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String data = dateformat.format(new Date());
        FILEDIR = basePath + "/" + dir + "/" + data;
        File filePath = new File(FILEDIR);
        if (!filePath.exists()) {
            mkDir(filePath);
        }

        if (!"".equals(file.getOriginalFilename())) {
            String fn = file.getOriginalFilename();
            String prefix = fn.substring(fn.lastIndexOf("."));
            long name = System.currentTimeMillis();
            write(file.getInputStream(), new FileOutputStream(FILEDIR + "/" + name + prefix));
            fileName = dir + "/" + data + "/" + name + prefix;
        }
        return fileName;
    }

    /**
     * 写入数据
     *
     * @param in
     * @param out
     * @throws IOException
     */
    public static void write(InputStream in, OutputStream out) throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
            }
            try {
                out.close();
            } catch (IOException ex) {
            }
        }
    }

    public static String uploads(MultipartFile file, String dir) throws IOException {
        Properties properties = new Properties();
        try {
            InputStream is = FileOperateUtil.class.getClassLoader().getResourceAsStream("server.properties");
            properties.load(is);
        } catch (Exception e) {
            e.getStackTrace();
        }
        String basePath = properties.getProperty("file.outputPath");
        String fileName = null;
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String data = dateformat.format(new Date());
        FILEDIR = basePath + File.separator + dir;
        File filePath = new File(FILEDIR);
        if (!filePath.exists()) {
            mkDir(filePath);
        }
        FILEDIR += File.separator + data;
        File filePath1 = new File(FILEDIR);
        if (!filePath1.exists()) {
            mkDir(filePath1);
        }
        if (file.getSize() != 0 && !"".equals(file.getOriginalFilename())) {
            String fn = file.getOriginalFilename();
            String prefix = fn.substring(fn.lastIndexOf("."));
            long name = System.currentTimeMillis();
            write(file.getInputStream(), new FileOutputStream(FILEDIR + File.separator + name + prefix));
            fileName = dir + File.separator + data + File.separator + name + prefix;
        }
        return fileName;
    }


    public static String newUploads(MultipartFile file, String oldPath) throws IOException {
        String newPath = null;
        if (file.getSize() != 0 && !"".equals(file.getOriginalFilename())) {
            String substring = oldPath.substring(0, oldPath.indexOf(".csv"));
            write(file.getInputStream(), new FileOutputStream(substring + ".xls"));
            newPath = substring+".xls";
        }
        return newPath;
    }

    //递归创建文件目录
    public static void mkDir(File file) {
        if (file.getParentFile().exists()) {
            file.mkdir();
        } else {
            mkDir(file.getParentFile());
            file.mkdir();
        }
    }

    /**
     * 上传
     *
     * @throws IOException
     */
    public static String uploadImage(String img, String dir) throws IOException {
        Properties properties = new Properties();
        try {
            InputStream is = FileOperateUtil.class.getClassLoader().getResourceAsStream("server.properties");
            properties.load(is);
        } catch (Exception e) {
            e.getStackTrace();
        }
        String basePath = properties.getProperty("file.outputPath");
        String fileName = "";
        try {
            //路径
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            String data = dateformat.format(new Date());
            FILEDIR = basePath + "/" + dir + "/" + data;
            File filePath = new File(FILEDIR);
            if (!filePath.exists()) {
                mkDir(filePath);
            }
            //解密
            BASE64Decoder decoder = new BASE64Decoder();
            img=img.replace("data:image/png;base64,","").replace("data:image/jpeg;base64,","");

            // 解密
            byte[] b = decoder.decodeBuffer(img);

            long name = System.currentTimeMillis();

            FileOutputStream out = new FileOutputStream(FILEDIR + "/" + name + ".png");
            out.write(b);
            out.flush();
            out.close();

            fileName = dir + "/" + data + "/" + name + ".png";
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("FileOperateUtil uploadImage error, e: " +e);
        }
        return fileName;
    }
    /**
     * @Description: 根据图片地址转换为base64编码字符串
     * @Author:
     * @CreateTime:
     * @return
     */
    public static String getImageStr(String imgFile) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加密
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }
}