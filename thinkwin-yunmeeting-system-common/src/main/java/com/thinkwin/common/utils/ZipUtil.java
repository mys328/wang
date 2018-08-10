package com.thinkwin.common.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
/**
 * zip包解压工具包
 * User: yinchunlei
 * Date: 2018/5/3
 * Company: thinkwin
 */
public class ZipUtil {
    private static final int buffer = 2048;

    /**
     * 解压Zip文件
     * @param path 文件目录
     */
    public static boolean unZip(String path)
    {
        boolean flag = false;
        int count = -1;
        String savepath = "";

        File file = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        savepath = path.substring(0, path.lastIndexOf(".")) + File.separator; //保存解压文件目录
        new File(savepath).mkdir(); //创建保存目录
        ZipFile zipFile = null;
        try
        {
            zipFile = new ZipFile(path,"gbk"); //解决中文乱码问题
            Enumeration<?> entries = zipFile.getEntries();

            while(entries.hasMoreElements())
            {
                byte buf[] = new byte[buffer];

                ZipEntry entry = (ZipEntry)entries.nextElement();

                String filename = entry.getName();
                boolean ismkdir = false;
                if(filename.lastIndexOf("/") != -1){ //检查此文件是否带有文件夹
                    ismkdir = true;
                }
                filename = savepath + filename;

                if(entry.isDirectory()){ //如果是文件夹先创建
                    file = new File(filename);
                    file.mkdirs();
                    continue;
                }
                file = new File(filename);
                if(!file.exists()){ //如果是目录先创建
                    if(ismkdir){
                        new File(filename.substring(0, filename.lastIndexOf("/"))).mkdirs(); //目录先创建
                    }
                }
                file.createNewFile(); //创建文件

                is = zipFile.getInputStream(entry);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, buffer);

                while((count = is.read(buf)) > -1)
                {
                    bos.write(buf, 0, count);
                }
                bos.flush();
                bos.close();

                fos.flush();
                fos.close();

                is.close();
            }

            zipFile.close();

            flag = true;
        }catch(IOException ioe){
            ioe.printStackTrace();
        }finally{
            try{
                if(bos != null){
                    bos.flush();
                    bos.close();
                }
                if(fos != null) {
                    fos.flush();
                    fos.close();
                }
                if(is != null){
                    is.close();
                }
                if(zipFile != null){
                    zipFile.close();
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }



public static void main(String[] args)
    {
        unZip("D:\\fileServer\\template\\2018-05-04\\1525401526604.zip");
        String f = "D:\\fileServer\\template\\2018-05-04\\1525401526604";
        File file = new File(f);
        String[] test=file.list();
        for(int i=0;i<test.length;i++){
            System.out.println(test[i]);
        }

        System.out.println("------------------");

        String fileName = "";

        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                System.out.println("文     件："+tempList[i]);

                fileName = tempList[i].getName();

                System.out.println("文件名："+fileName);
            }
            if (tempList[i].isDirectory()) {
                System.out.println("文件夹："+tempList[i]);
            }
        }
    }
}
