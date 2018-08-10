package com.thinkwin.common.utils;

import com.thinkwin.common.vo.FastdfsVo;
import javafx.concurrent.Task;
import org.apache.log4j.Logger;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;

import org.csource.fastdfs.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;

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

    public  FileManager(String []  tracker_server){

            boolean success = false;
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
                success = true;
                logger.info("***************------第一次读取路径失败-------********************");
            }
            try {
                if (success) {
                    ClientGlobal.setG_connect_timeout(connect_timeout);
                    ClientGlobal.setG_network_timeout(network_timeout);
                    ClientGlobal.setG_charset(charset);
                    ClientGlobal.setG_tracker_http_port(tracker_http_port);
                    ClientGlobal.setG_anti_steal_token(anti_steal_token);
                    ClientGlobal.setG_secret_key(secret_key);

                    InetSocketAddress[] tracker_servers = new InetSocketAddress[tracker_server.length];

                    for (int i = 0; i < tracker_server.length; ++i) {
                        String[] parts = tracker_server[i].split("\\:", 2);
                        if (parts.length != 2) {
                            throw new MyException("the value of item \"tracker_server\" is invalid, the correct format is host:port");
                        }

                        tracker_servers[i] = new InetSocketAddress(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                    }
                    TrackerGroup g_tracker_group = new TrackerGroup(tracker_servers);
                    ClientGlobal.setG_tracker_group(g_tracker_group);
                    logger.info("***************------第二次读取成功路径：" + tracker_server + "-------********************");
                    trackerClient = new TrackerClient();
                    trackerServer = trackerClient.getConnection();
                    storageClient = new StorageClient1(trackerServer, storageServer);
                }

            } catch (Exception e) {
                logger.info("***************------第二次读取路径失败-------********************");
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
