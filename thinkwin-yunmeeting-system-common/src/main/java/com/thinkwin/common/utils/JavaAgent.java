package com.thinkwin.common.utils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thinkwin.common.model.db.YuncmMeetingRoom;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class JavaAgent {


    /**
     *
     * @param rooms 会议室List
     * @param path 保存的路径
     * @param fileName 文档名称
     * @param tenantId 租户ID 用于创建文件夹
     */
  public static void NotesMain(List<YuncmMeetingRoom> rooms,String path,String fileName) {


       try {
          String suffix=".doc";
        if (!"".equals(path)) {
         // 检查目录是否存在
            File fp = new File(path);
            if(!fp.exists()){
                fp.mkdirs();
            }
         File fileDir = new File(path);
         if (fileDir.exists()) {
          String buf="";
          int i = 0;
          String page =""+(char)11+(char)11+(char)11+(char)11+(char)11+(char)11+(char)11+(char)11+(char)11+(char)11+(char)11+(char)11+(char)11;
             for(YuncmMeetingRoom room:rooms){
               i ++;
              buf += "" +(char)11+(char)11+(char)11+(char)11+(char)11+(char)11+(char)11+
                      "<div style='height: 100%; width: 100%;  text-align:center; overflow:hidden; margin:0 auto;' >" +
                      "" +
                      "<table   style='margin:5 auto; border:1px solid; width:550px;' >" +
                      "    <tr style='text-align:center; height:40px;'>" +
                      "<td>"+room.getName()+"</td>" +
                      "</tr>" +
                      "    <tr  style='text-align:center; height:40px;'>" +
                      "<td><img src='"+room.getQrCodeUrl()+"' height='400' width='400'></td>" +
                      "</tr>" +
                      "<tr style='text-align:center; height:40px;'>" +
                      "<td>临时会议预订，请扫描二维码即扫即订</td>" +
                      "</tr>" +
                      "</table>";
                     if(i < rooms.size()){
                         buf +=  page;
                     }
          }
             String content = "<html>" +
                     "<head>" +
                     "" +
                     "</head>" +
                     "<body>"+buf+
                        "</body>" +
                         "</html>";




        // bs = content.getBytes("unicode");
          byte b[] = content.getBytes("unicode");
          ByteArrayInputStream bais = new ByteArrayInputStream(b);
          POIFSFileSystem poifs = new POIFSFileSystem();
          DirectoryEntry directory = poifs.getRoot();
          DocumentEntry documentEntry = directory.createDocument("WordDocument", bais);
          FileOutputStream ostream = new FileOutputStream(path+fileName+suffix);
          poifs.writeFilesystem(ostream);
          bais.close();
          ostream.close();
         System.out.println("文件生成！");
             }else{
                 System.out.println("该路径不存在！");
             }
            }else {
                System.out.println("路径不能为空！");
            }

              } catch (IOException e) {
               e.printStackTrace();
             }

     }


    
    }


