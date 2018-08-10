package com.thinkwin.common.utils;

import com.thinkwin.common.model.db.SysAttachment;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.common.vo.FileVo;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import static com.thinkwin.common.utils.FileOperateUtil.mkDir;
import static com.thinkwin.common.utils.ZipUtil.unZip;

/**
 * Created by Administrator on 2018/5/9 0009.
 */
public class FileUploadUtil extends FileManagerConfig {

    private static final Logger logger = Logger.getLogger(FileUploadUtil.class);

    private static final long serialVersionUID = 1L;
    private static TrackerClient trackerClient;
    private static TrackerServer trackerServer;
    private static StorageServer storageServer;
    private static StorageClient storageClient;
    //连接时间
    private static final Integer connect_timeout = 30;
    //
    private static final Integer network_timeout = 60;
    //字符集
    private static final String charset = "UTF-8";

    private static final Integer tracker_http_port = 8080;

    private static final boolean anti_steal_token = false;

    private static final String secret_key = "FastDFS1234567890";

    static {
        try {
            String classPath = new File(FileManager.class.getResource("/").getFile()).getCanonicalPath();
            String fdfsClientConfigFilePath = classPath + File.separator + CLIENT_CONFIG_FILE;
            logger.info("***************------第一次路径：" + fdfsClientConfigFilePath + "-------********************");
            ClientGlobal.init(fdfsClientConfigFilePath);
            logger.info("***************------第一次读取成功路径：" + fdfsClientConfigFilePath + "-------********************");
            trackerClient = new TrackerClient();
            trackerServer = trackerClient.getConnection();
            storageClient = new StorageClient1(trackerServer, storageServer);
        } catch (Exception e) {
            logger.info("***************------第一次读取路径失败-------********************");
        }

    }

    /**
     * 裁剪并上传
     * @param map
     * @param source
     * @param userId
     * @param bytes
     * @param tenantId
     * @param spaceNum
     * @param ext
     * @return
     */
    public static List<FastdfsVo> fileUpload(Map<String,String> map , byte[] bytes, String ext){

        List<FastdfsVo> vos = new ArrayList<FastdfsVo>();
        //存储原图片
        String size=String.valueOf(FileSizeConversion.convertFileSize(bytes.length));
        FastDFSFile file = new FastDFSFile(bytes,new Date().getTime()+"."+ext,ext,size,"");
        FastdfsVo fastdfsVo = upload(file,null);
        vos.add(fastdfsVo);
        if(map!=null){
            //处理图片
            Map<String,BufferedImage> imageMap = ResizeImage.resizeImage(bytes,map);
            //大图
            if(imageMap.get("big") != null){
                byte[] by = ResizeImage.imageToBytes(imageMap.get("big"),ext);
                FastDFSFile file1 = new FastDFSFile(by,new Date().getTime()+"big."+ext,ext,"0","");
                FastdfsVo fastdfsVo1 = upload(file1,null);
                vos.add(fastdfsVo1);
            }
            //中图
            if(imageMap.get("in") != null){
                byte[] by = ResizeImage.imageToBytes(imageMap.get("in"),ext);
                FastDFSFile file1 = new FastDFSFile(by,new Date().getTime()+"in."+ext,ext,"0","");
                FastdfsVo fastdfsVo1 = upload(file1,null);
                vos.add(fastdfsVo1);
            }
            //小图
            if(imageMap.get("small") != null){
                byte[] by = ResizeImage.imageToBytes(imageMap.get("small"),ext);
                FastDFSFile file1 = new FastDFSFile(by,new Date().getTime()+"small."+ext,ext,"0","");
                FastdfsVo fastdfsVo1 = upload(file1,null);
                vos.add(fastdfsVo1);
            }
        }
        return vos;

    };

    public static boolean deleteFile(List<SysAttachment> sysAttachments){
         boolean success = false;
        for (SysAttachment sys : sysAttachments){
            success = deleteFileUpload(sys.getGroup(),sys.getAttachmentPath() + sys.getFileName());
        }
        return success;

    }

    /**
     *
     * @param FastDFSFile
     *            file
     * @return fileAbsolutePath
     * @author Wang Liang
     */
    public static FastdfsVo upload(FastDFSFile file, NameValuePair[] valuePairs) {
        String[] uploadResults = null;
        FastdfsVo fastdfsVo;

        fastdfsVo = null;


        try {
            fastdfsVo=new FastdfsVo();
            uploadResults = storageClient.upload_file(file.getContent(),file.getExt(), valuePairs);
            String groupName = uploadResults[0];
            String remoteFileName = uploadResults[1];
            String fileName = remoteFileName.substring(remoteFileName.lastIndexOf("/")+1);
            String path=remoteFileName.substring(0,remoteFileName.lastIndexOf("/")+1);
            fastdfsVo.setGroup(groupName);
            fastdfsVo.setFileName(fileName);
            fastdfsVo.setPath(path);
            fastdfsVo.setName(file.getName());
            fastdfsVo.setLength(file.getLength());
            String fileAbsolutePath =
                    TRACKER_NGNIX_ADDR
                            + SEPARATOR + groupName
                            + SEPARATOR + remoteFileName;
            fastdfsVo.setFileUrl(fileAbsolutePath);
            logger.info("***************------上传图片地址：" + fileAbsolutePath + "-------********************");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fastdfsVo;
    }


    public static boolean deleteFileUpload(String group,String fileUrl){
        boolean success=false;
        try {
            trackerClient = new TrackerClient();
            trackerServer = trackerClient.getConnection();
            storageClient = new StorageClient1(trackerServer,storageServer);
            int i = storageClient.delete_file(group,fileUrl);
            if(i!=1){
                success=true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }



    public static String downloadFile( String path,SysAttachment sysAttachment) {
        File fp = new File(path);
        if(!fp.exists()){
            fp.mkdirs();
        }
        if(sysAttachment != null) {
            boolean b = downloadFile(sysAttachment.getGroup(), sysAttachment.getAttachmentPath() + sysAttachment.getFileName(), path + sysAttachment.getFileCnName());
            if (b) {
                return sysAttachment.getFileCnName();
            }
        }
        return "";
    }

    //将节目包下载到本地
    public static String downloadLocalFile( String path,SysAttachment sysAttachment) {
        File fp = new File(path);
        if(!fp.exists()){
            fp.mkdirs();
        }
        if(sysAttachment != null) {
            boolean b = downloadFile(sysAttachment.getGroup(), sysAttachment.getAttachmentPath() + sysAttachment.getFileName(), path+ "program.zip");
            if (b) {
                return "program.zip";
            }
        }
        return "";
    }




    /**
     *
     * @param String
     *            groupName
     * @param String
     *            remoteFileName
     * @return returned value comment here
     * @author Wang Liang
     */
    public static boolean downloadFile(String groupName,
                                       String remoteFileName, String specFileName) {
        byte[] content = null;
        try {
            storageClient=new StorageClient1();
            //下载文件
            content = storageClient.download_file(groupName, remoteFileName);
            if (content!=null && content.length>0) {
                File file = new File(specFileName);
                FileOutputStream fop = new FileOutputStream(file);
                fop.write(content);
                fop.close();
                logger.info("***************------下载文件成功：" + file + "-------********************");
                return true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error("***************------下载文件异常：" + e.getMessage() + "-------********************");
        }
        return false;
    }


    /**
     * 从平台同步节目包到租户
     * @param newPath String类型：本地地址 ~+image_id\
     * @param filePath File类型：本地文件夹路径， ~+image_id
     * @param sysAttachment 远程附件
     * @return
     */
    public static boolean syncProgramFile(String newPath,File filePath,SysAttachment sysAttachment){

        boolean flag = false;
        if(null!=sysAttachment){
            mkDir(filePath);
            String fileName = FileUploadUtil.downloadLocalFile(newPath,sysAttachment);/*fileUploadService.downloadFile(imageId, newPath);*/
            if(org.apache.commons.lang3.StringUtils.isBlank(fileName)){
                return false;
            }
            String s = newPath + fileName;
            boolean b = unZip(s);
            clearFiles(s);
            if(b){
                flag = true;
            }
            return flag;
        }
        return false;

    }

    //删除文件和目录
    private static void clearFiles(String workspaceRootPath) {
        File file = new File(workspaceRootPath);
        if (file.exists()) {
            deleteFile(file);
        }
    }

    //删除文件和目录
    private static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
        }
        file.delete();
    }





}
