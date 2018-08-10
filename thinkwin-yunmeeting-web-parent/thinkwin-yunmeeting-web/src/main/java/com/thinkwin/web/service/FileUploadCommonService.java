package com.thinkwin.web.service;

import com.thinkwin.common.vo.FastdfsVo;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * 图片上传公共service
 */
public interface FileUploadCommonService {

    public String fileUploadCommon(FastdfsVo fastdfsVo, String img, String fileName, String size, String oldImg, String userId,String tenantId );

    /**
     * 上传图片公共方法
     * @param imageMap
     * @param ext
     * @param bytes
     * @param tenantId
     * @param userId
     * @return
     */
    public String uploadFileCommon(Map<String,BufferedImage> imageMap , String ext, byte[] bytes, String tenantId, String userId);

    /**
     * 删除图片公共方法
     * @param fileId
     * @param tenantId
     * @return
     */
    public boolean deleteFileCommon(String fileId,String tenantId);

    /**
     * 查看图片
     * @param fileId
     * @return
     */
    public Map<String,String> selectFileCommon(String fileId);

}
