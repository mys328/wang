package com.thinkwin.web.service.impl;

import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.SysAttachment;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.service.TenantContext;

import com.thinkwin.web.service.FileUploadCommonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

@Service("fileUploadCommonService")
public class FileUploadCommonServiceImpl extends FileManagerConfig implements FileUploadCommonService {
    @Resource
    FileUploadService fileUploadService;
    @Resource
    SaasTenantService saasTenantCoreService;

    /**
     * 方法名：fileUploadCommon</br>
     * 描述：上传文件抽出的公共方法</br>
     */
    public String fileUploadCommon(FastdfsVo fastdfsVo, String img, String fileName, String size, String oldImg, String userId , String tenantId ){

        String fileId = null;
        /*
         * 1、解密前端传过来的加密文档
         *
         * */
        BASE64Decoder decoder = new BASE64Decoder();
        fileName=fileName.replace("data:image/png;base64,","").replace("data:image/jpeg;base64,","");
        /*
         * 2、转换为文件流
         * */
        byte[] imgByte = new byte[0];
        try {
            imgByte = decoder.decodeBuffer(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
         * 3、截取文件后缀
         * */
        String img_path ="jpg";
        if(img !=null){
            img_path = img.substring(img.lastIndexOf(".") + 1).trim().toLowerCase();
        }
        /*
         * 4、计算文件大小
         * */
        String uploadFileSize = "";
        if (StringUtils.isNotBlank(size)) {
            uploadFileSize = FileSizeConversion.convertFileSize(Long.parseLong(size));

            SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
            //获取租户上传的空间剩余大小
            String totalSpace = this.fileUploadService.selectTenantFileSize(tenantId,saasTenant.getBasePackageSpaceNum()+"");
            if (Long.valueOf(uploadFileSize) > Long.valueOf(totalSpace)) {
                fileId = BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription();
                return fileId;
            }
        }
        /*
         * 5、上传图片
         * */
        FastDFSFile file = new FastDFSFile(imgByte,img,img_path,uploadFileSize,"");
        FileManager fileManager = new FileManager(null);
        fastdfsVo = FileManager.upload(file,null);
        /*
         * 6、添加数据返回图片ID
         * */
        fileId = "";//fileUploadService.insertFileUpload(fastdfsVo, TenantContext.getTenantId(),"3",userId);
        fastdfsVo.setId(fileId);

        /*
         * 7、是否是替换图片，如果是删除上次图片
         * */
        if(oldImg !=null && !"".equals(oldImg)){
            SysAttachment sys = fileUploadService.downloadFlie(oldImg);
            if(sys != null){
                fileManager.deleteFileUpload(sys.getGroup(), sys.getAttachmentPath() + sys.getFileName());
                fileUploadService.deleteFileUpload(oldImg, TenantContext.getTenantId());
            }
        }
        return  fileId;
    }

    @Override
    public String uploadFileCommon(Map<String,BufferedImage> imageMap ,String ext,byte[] bytes,String tenantId,String userId){


        /*
         * 4、计算文件大小
         * */
        String uploadFileSize = "";
        if (StringUtils.isNotBlank(String.valueOf(bytes.length))) {
            uploadFileSize = FileSizeConversion.convertFileSize(bytes.length);

            SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
            //获取租户上传的空间剩余大小
            String totalSpace = this.fileUploadService.selectTenantFileSize(tenantId,saasTenant.getBasePackageSpaceNum()+"");
            if (Long.valueOf(uploadFileSize) > Long.valueOf(totalSpace)) {
                return "0";
            }
        }
        String size=String.valueOf(FileSizeConversion.convertFileSize(bytes.length));
        FastDFSFile file = new FastDFSFile(bytes,new Date().getTime()+"."+ext,ext,size,"");
        FileManager fileManager = new FileManager(null);
        FastdfsVo fastdfsVo = fileManager.upload(file,null);
        String fileId = fileUploadService.insertFileUploadCondition(fastdfsVo, TenantContext.getTenantId(),"3",userId,"");
        //大图
        if(imageMap.get("big") != null){
            byte[] by = ResizeImage.imageToBytes(imageMap.get("big"),ext);
            FastDFSFile file1 = new FastDFSFile(by,new Date().getTime()+"big."+ext,ext,"0","");
            FastdfsVo fastdfsVo1 = fileManager.upload(file1,null);
            fileUploadService.insertFileUploadCondition(fastdfsVo1, TenantContext.getTenantId(),"3",userId,fileId+"_big");
        }
        //中图
        if(imageMap.get("in") != null){
            byte[] by = ResizeImage.imageToBytes(imageMap.get("in"),ext);
            FastDFSFile file1 = new FastDFSFile(by,new Date().getTime()+"in."+ext,ext,"0","");
            FastdfsVo fastdfsVo1 = fileManager.upload(file1,null);
            fileUploadService.insertFileUploadCondition(fastdfsVo1, TenantContext.getTenantId(),"3",userId,fileId+"_in");
        }
        //小图
        if(imageMap.get("small") != null){
            byte[] by = ResizeImage.imageToBytes(imageMap.get("small"),ext);
            FastDFSFile file1 = new FastDFSFile(by,new Date().getTime()+"small."+ext,ext,"0","");
            FastdfsVo fastdfsVo1 = fileManager.upload(file1,null);
            fileUploadService.insertFileUploadCondition(fastdfsVo1, TenantContext.getTenantId(),"3",userId,fileId+"_small");
        }
        return fileId;
    }

    @Override
    public boolean deleteFileCommon(String fileId, String tenantId) {

        boolean success = false;
        //获取图片信息
        List<String> list = new ArrayList<String>();
        list.add(fileId);
        list.add(fileId+"_big");
        list.add(fileId+"_in");
        list.add(fileId+"_small");
        Map map =new HashMap();
        map.put("list",list);
        List<SysAttachment> sysAttachments = this.fileUploadService.selectSysAttachmentInfo(map);
        //从服务器删除图片
        for(SysAttachment sys : sysAttachments){
            boolean succ = false;
            succ = FileManager.deleteFileUpload(sys.getGroup(), sys.getAttachmentPath() + sys.getFileName());
            if(succ){
                success = this.fileUploadService.deleteByIdFileUpload(sys,tenantId);
            }
        }
        //this.fileUploadService.deleteTenantFile(sysAttachments,tenantId);
        return success;
    }

    @Override
    public Map<String, String> selectFileCommon(String fileId) {

        Map<String, String> map = new HashMap<>();
        //获取图片信息
        List<String> list = new ArrayList<String>();
        list.add(fileId);
        list.add(fileId+"_big");
        list.add(fileId+"_in");
        list.add(fileId+"_small");
        Map maps =new HashMap();
        maps.put("list",list);
        List<SysAttachment> sysAttachments = this.fileUploadService.selectSysAttachmentInfo(maps);
        for (SysAttachment sys : sysAttachments){
            String  fileUrl = TRACKER_NGNIX_ADDR
                    + SEPARATOR + sys.getGroup() + "/" + sys.getAttachmentPath() + sys.getFileName();
            //原图
            if(fileId.equals(sys.getId())){
                map.put("primary",fileUrl);
            }
            //大图
            if((fileId+"_big").equals(sys.getId())){
                map.put("big",fileUrl);
            }
            //中图
            if((fileId+"_in").equals(sys.getId())){
                map.put("in",fileUrl);
            }
            //小图
            if((fileId+"_small").equals(sys.getId())){
                map.put("small",fileUrl);
            }
        }

        return map;
    }


}
