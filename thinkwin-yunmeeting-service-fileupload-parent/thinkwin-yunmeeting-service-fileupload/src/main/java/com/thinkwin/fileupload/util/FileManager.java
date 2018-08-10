package com.thinkwin.fileupload.util;

import com.thinkwin.common.vo.FastdfsVo;
import org.apache.log4j.Logger;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;

/**
 * fastdfs 图片实现工具类
 */
public class FileManager extends FileManagerConfig {
    private static final Logger logger = Logger.getLogger(FileManager.class);

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

    private  String []  tracker_server ;

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
    /**
     *
     * @param String
     *            groupName
     * @param String
     *            remoteFileName
     * @return returned value comment here
     * @author Wang Liang
     */
    public static ResponseEntity<byte[]> download(String groupName,
                                                  String remoteFileName, String specFileName) {
        byte[] content = null;
        HttpHeaders headers = new HttpHeaders();
        try {
            storageClient=new StorageClient1();

            content = storageClient.download_file(groupName, remoteFileName);
            headers.setContentDispositionFormData("attachment",  new String(specFileName.getBytes("UTF-8"),"iso-8859-1"));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ResponseEntity<byte[]>(content, headers, HttpStatus.CREATED);
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
                return true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
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






}
