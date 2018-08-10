package com.thinkwin.fileupload.service;

import com.thinkwin.common.model.db.MiddleBizAttachment;
import com.thinkwin.common.model.db.SysAttachment;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.common.vo.FileVo;

import java.util.List;
import java.util.Map;

/**
  *  文件上传接口
  *
  *  开发人员:daipengkai
  *  创建时间:2017/6/30
  *
  */
public interface FileUploadService {
    /**
     *  文件上传
     * @param file
     * @return
     */
    FileVo insertFileUpload(String source, String userId,String tenantId, Integer spaceNum,String ext,List<FastdfsVo> vos);
    /**
     * 平台级上传
     * @param
     * @return
     */
    String insertPlatformFileUpload(FastdfsVo fastdfsVos,String source,String userId);
    /**
     * 删除文件
     * @param group 图片所在组
     * @param fileUrl 图片名称
     * @return
     */
    List<SysAttachment>  deleteFileUpload(String fileId,String tenantId);

    /**
     *  文件下载
     * @param groupName 组名
     * @param remoteFileName 上传的文件名
     * @param specFileName 下载后的文件名 自定义
     * @return
     */
    SysAttachment downloadFlie(String fileId);

    /**
     * 根据图片ID查看单条图片
     * @param fileId
     * @return
     */
    String  selectTenementByFile(String  fileId);

    /**
     * 根据ID查看图片信息
     * @param id
     * @return
     */
    SysAttachment selectByidFile(String fileId);

    /**
     * 查看租户使用空间剩余大小
     * @param tenantId
     * @return
     */
    String selectTenantFileSize(String tenantId,String space);

    /**
     * 查看租户的使用空间 缓存用
     * @param tenantId
     * @return
     */
    String selectTenantuseSpace(String tenantId);

    /**
     * 定时用于把租户的空间使用信息添加到本地缓存
     */
    public void timingTaskTenementFile();


    /**
     * 查看租户的使用空间 缓存用
     * @param tenantId 租户id
     * @param type     转换的类型  MB GB 为空时返回KB
     * @return
     */
    String selectAllTenantuseSpace(String tenantId,String type);

    /**
     *  wenjian shangchhua
     * @param fastdfsVos 文件类
     * @param tenantId zuhu id
     * @param source
     * @param userId 用户id
     * @param uuId uuid
     * @return
     */
    String insertFileUploadCondition(FastdfsVo fastdfsVos,String tenantId,String source,String userId,String uuId);


    /**
     * 批量查询图片信息
     * @param list
     * @return
     */
    List<SysAttachment> selectSysAttachmentInfo(Map map);

    /**
     * 删除文件
     * @param group 图片所在组
     * @param fileUrl 图片名称
     * @return
     */
    boolean deleteByIdFileUpload(SysAttachment sysAttachment,String tenantId);
    /**
     * 查看图片
     * @param fileId
     * @return
     */
    public Map<String,String> selectFileCommon(String fileId);


    public void deleteTenantFile(List<SysAttachment> sysAttachments,String tenantId);

    /**
     * 删除租户所有文件信息
     * @param param
     */
    //public String deleteTenantFile(String param);

    /**
     * 获取租户下所有文件
     * @param tenantId 租住id
     * @return
     */
    List<MiddleBizAttachment> selectMiddleBizAttachment(String tenantId);

    /**
     * 根据租户id查询租户所有文件
     * @param fileValues
     * @return
     */
    List<SysAttachment> selectTenantIdSysAttachment(List<String> fileValues);
    /**
     * 处理数据库删除时未能删除的物理库的定时删除接口功能
     */
//    public void delDBDate();




    /**
     * 文件下载
     * @param groupName 文件所在
     * @param remoteFileName
     * @param specFileName
     * @return
     */
    String downloadFile (String fileId,String path);
}
