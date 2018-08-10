/**
* Copyright © 1998-2017, Glodon Inc. All Rights Reserved.
*/
package com.thinkwin.common.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.*;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipException;


/**
 *APK工具类
 *  
 */

public class ApkFileUtil {
	
	private static final int BUFFER = 2028;


    /**
     * 解压Zip文件
     * @param path 文件目录
     */
    public static void unZip(String path)
    {
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
                byte buf[] = new byte[BUFFER];

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
                bos = new BufferedOutputStream(fos, BUFFER);

                while((count = is.read(buf)) > -1)
                {
                    bos.write(buf, 0, count);
                }
                bos.flush();
                bos.close();
                fos.close();

                is.close();
            }

            zipFile.close();

        }catch(IOException ioe){
            ioe.printStackTrace();
        }finally{
            try{
                if(bos != null){
                    bos.close();
                }
                if(fos != null) {
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
    }

	/**
	 * 不解压直接读取apk压缩包中文件内容呢
	 * TODO
	 * @param zipFile
	 * @param readFileName
	 * @return
	 * @throws ZipException
	 * @throws IOException
	 */
	public static String getZipFileContent(File zipFile, String readFileName) {  
	    StringBuilder content = new StringBuilder();  
	    ZipFile zip = null;
		try {
			zip = new ZipFile(zipFile);
			Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.getEntries();  
			ZipEntry ze;  
	        // 枚举zip文件内的文件
		    while (entries.hasMoreElements()) {  
		        ze = entries.nextElement();
		        String tarName = readFileName;//目标文件名称
		        String aa = ze.getName();
		        // 读取目标对象  
		        if (ze.getName().equals(tarName)) {  
		            Scanner scanner = null;
					try {
						scanner = new Scanner(zip.getInputStream(ze),"UTF-8");
					} catch (ZipException e) {
						e.printStackTrace();
					} 
		            while (scanner.hasNextLine()) {  
		                content.append(scanner.nextLine());  
		            }
		            scanner.close();
		            break;
		        }  
		    }  
		    zip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}   
	    return content.toString();  
	}  
	
	/**  
	    * 解压缩zip文件   
	    * @param upfile 要解压的文件
	    * @param filePath 解压后存放文件的路径 如："c:\\temp"  
	    * @throws Exception  
	    */   
	 public static void unZip(File upfile, String filePath) throws Exception{    
	       ZipFile zipFile = new ZipFile(upfile);     
	       Enumeration emu = zipFile.getEntries();  
	          
	       while(emu.hasMoreElements()){    
	            ZipEntry entry = (ZipEntry) emu.nextElement();    
	            if (entry.isDirectory()){    
	                new File(filePath + File.separator + entry.getName()).mkdirs();    
	                continue;    
	            }    
	            BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));    
	               
	            File file = new File(filePath + File.separator + entry.getName());    
	            File parent = file.getParentFile();    
	            if(parent != null && (!parent.exists())){    
	                parent.mkdirs();    
	            }    
	            FileOutputStream fos = new FileOutputStream(file);    
	            BufferedOutputStream bos = new BufferedOutputStream(fos,BUFFER);    
	        
	            byte [] buf = new byte[BUFFER];    
	            int len = 0;    
	            while((len=bis.read(buf,0,BUFFER))!=-1){    
	                fos.write(buf,0,len);    
	            }    
	            bos.flush();    
	            bos.close();    
	            bis.close();    
	           }    
	           zipFile.close();    
	    }    
		/**
		 * 
		 * TODO
		 * @param filePath 文件路径
		 */
		 public static String readTxt(String filePath) {
			  String content = "";
			  try {
			    File file = new File(filePath);
			    if(file.isFile() && file.exists()) {
			      InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
			      BufferedReader br = new BufferedReader(isr);
			      String lineTxt = null;
			      while ((lineTxt = br.readLine()) != null) {
			        System.out.println(lineTxt);
			        content = content + lineTxt;
			      }
			      br.close();
			    } else {
			      System.out.println("文件不存在!");
			    }
			  } catch (Exception e) {
			    System.out.println("文件读取错误!");
			  }
			return content;
			  
		}
	/**
	 * 读取文本中JSON格式数据
	 * TODO
	 * @param file
	 */
	public static void readJsonFromFile(File file){  
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));// 读取原始json文件  
            String s = null, ws = null;
            while ((s = br.readLine()) != null) {
                try {
                    JSONObject dataJson = new JSONObject();// 创建一个包含原始json串的json对象
                    String fileName = dataJson.get("file_name").toString();
                    String version = dataJson.get("version").toString();
                    String projectInfo = dataJson.get("project_info").toString();
                    String terminalType = dataJson.get("terminal_type").toString();
                    String verion_detail = dataJson.get("verion_detail").toString();
//                    JSONArray jsonArray = new JSONArray(verion_detail);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
  
            br.close();  
  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
	  
	}  
	
	/**
	 * 删除目录文件
	 * TODO
	 * @param dir
	 * @return
	 */
	@SuppressWarnings("unused")
	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static byte[] fileToBytes(String filePath) {
        byte[] buffer = null;
        File file = new File(filePath);

        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;

        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];

            int n;

            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            buffer = bos.toByteArray();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (null != bos) {
                    bos.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                try {
                    if(null!=fis){
                        fis.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return buffer;
    }
	
	public static void main(String[] args) {
		String zipFilePath = "D:/file/11.json";
		File file = new File(zipFilePath);
		File file2 = new File("d:/file/cr.apk");
		ApkFileUtil.getZipFileContent(file2, "readMe.txt");
		
		
	}
	
	
}
