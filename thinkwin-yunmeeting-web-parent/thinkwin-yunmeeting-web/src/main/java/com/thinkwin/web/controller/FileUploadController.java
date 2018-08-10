package com.thinkwin.web.controller;

import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.OrganizationService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.SysAttachment;
import com.thinkwin.common.model.db.YuncmMeetingRoom;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.common.vo.FileVo;
import com.thinkwin.common.vo.WechatSNSUserInfoVo;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.serialnumber.service.SerialNumberService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.YuncmMeetingService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
  *
  *  文件上传controller
  *  开发人员:daipengkai
  *  创建时间:2017/5/23
  *
  */
@Controller
@RequestMapping(value = "/upload")
public class FileUploadController extends FileManagerConfig {

    private final Logger log = LoggerFactory.getLogger(FileUploadController.class);
    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private SerialNumberService serialNumberService;
    @Autowired
    private YuncmMeetingService yuncmMeetingService;
    @Autowired
    SaasTenantService saasTenantCoreService;


    /**
     * 路径上传文件
     * @param userId
     * @param file
     * @param fileName
     * @param fileSize
     * @return
     */
    @RequestMapping(value = "/fileUpload",method = RequestMethod.POST)
    @ResponseBody
    public Object fileUpload(String userId,String file,String fileName,String fileSize) throws IOException {
        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);

        //解密
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] b = decoder.decodeBuffer(file.replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,", ""));
        //获取租户上传的空间剩余大小
        String totalSpace = this.fileUploadService.selectTenantFileSize(tenantId,saasTenant.getBasePackageSpaceNum()+"");
        if (totalSpace != null) {
            //将当前文件大小转换成kb
            String uploadFileSize = FileSizeConversion.convertFileSize(Long.valueOf(fileSize));
            if (Long.valueOf(uploadFileSize) > Long.valueOf(totalSpace)) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(), BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
            }
            //添加数据返回图片ID
            //获取用户信息
            FastdfsVo fastdfsVo = new FastdfsVo();
            TenantUserVo userInfo = TenantContext.getUserInfo();
            FileVo vo  = null;//fileUploadService.insertFileUpload(null,"3",userId,b,tenantId,saasTenant.getBasePackageSpaceNum(),"png");
            fastdfsVo.setId(vo.getId());
            fastdfsVo.setFileUrl(vo.getPrimary());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),fastdfsVo);
        } else {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(), BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
        }
    }

    /**
     * 路径上传文件
     * @param userId
     * @param file
     * @param fileName
     * @param fileSize
     * @return
     */
    @RequestMapping(value = "/fileUploadModify",method = RequestMethod.POST)
    @ResponseBody
    public Object fileUploadModify(String userId,String fileName,String fileSize,MultipartFile file) throws IOException {
        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);

        //获取租户上传的空间剩余大小
        String totalSpace = this.fileUploadService.selectTenantFileSize(tenantId,saasTenant.getBasePackageSpaceNum()+"");
        if (totalSpace != null) {
            //将当前文件大小转换成kb
            String uploadFileSize = FileSizeConversion.convertFileSize(Long.valueOf(fileSize));
            if (Long.valueOf(uploadFileSize) > Long.valueOf(totalSpace)) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(), BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
            }
            byte[] bytes = ResizeImage.resizeImageByte(file.getBytes(),0);
            //上传文件
            //添加数据返回图片ID
            //获取用户信息
            TenantUserVo userInfo = TenantContext.getUserInfo();
            FileVo vo = null;//fileUploadService.insertFileUpload(null,"3",userId,bytes,tenantId,saasTenant.getBasePackageSpaceNum(),"png");
            FastdfsVo fastdfsVo = new FastdfsVo();
            fastdfsVo.setId(vo.getId());
            fastdfsVo.setFileUrl(vo.getPrimary());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),fastdfsVo);
        } else {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(), BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
        }
    }



        /**
         *  上传文件
         *  @param request
         *  @param fileName		文件参数名称
         *  @param tenementId		租户ID
         *  @param userId		用户ID
         *  @param source		平台
         *  @param businessId		业务类型
         *  @return
         */
    @RequestMapping(value = "/singleFileUpload")
    @ResponseBody
    public Object singleFileUpload(String userId,String source,String fileId,HttpServletRequest request,MultipartFile fileName) throws IOException {
        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
            /*MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile attach =multipartRequest.getFile("fileName");*/
            String ext = fileName.getOriginalFilename().substring(fileName.getOriginalFilename().lastIndexOf(".")+1);
            long size=fileName.getSize();
            String size1=String.valueOf(size);
            //获取租户上传的空间剩余大小
            String totalSpace = this.fileUploadService.selectTenantFileSize(tenantId,saasTenant.getBasePackageSpaceNum()+"");
           if(totalSpace!=null){
                //将当前文件大小转换成kb
              String uploadFileSize = FileSizeConversion.convertFileSize(size);
                if(Long.valueOf(uploadFileSize)>Long.valueOf(totalSpace)){
                  return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0 ,BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(),BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
                }
               FastDFSFile file = null;
               try {
                   file = new FastDFSFile(fileName.getBytes(),fileName.getOriginalFilename(),ext,size1,"");
               } catch (IOException e) {
                   e.printStackTrace();
               }
               //上传文件
                FastdfsVo fastdfsVo = FileManager.upload(file,null);
                //添加数据返回图片ID
                FileVo vo = null;// fileUploadService.insertFileUpload(null,"3",userId,fileName.getBytes(),tenantId,saasTenant.getBasePackageSpaceNum(),"png");
                if(vo != null){
                    fastdfsVo.setId(vo.getId());
                    //删除原图片
                    if(StringUtils.isNotBlank(fileId)){
                        this.fileUploadService.deleteFileUpload(fileId,tenantId);
                    }
                   return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),fastdfsVo);
                }
            }else{
               return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0 ,BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(),BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());

           }

           return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0 ,BusinessExceptionStatusEnum.FastdfsConnectionFail.getDescription(),BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
    }

    /**
     *  图片下载
     * @param fileId 图片ID
     * @return 返回下载地址
     */
    @RequestMapping("/download")
    @ResponseBody
    public Object download(String fileId) {

            Map<String, Object> map = new HashMap<String, Object>();
            SysAttachment sysAttachment = this.fileUploadService.downloadFlie(fileId);
            if(sysAttachment != null){
                FastdfsVo fastdfsVo = new FastdfsVo();
                String fileUrl=
                        TRACKER_NGNIX_ADDR
                        + SEPARATOR + sysAttachment.getGroup()+"/"+sysAttachment.getAttachmentPath()+sysAttachment.getFileName()+"?"+sysAttachment.getFileCnName();
                fastdfsVo.setFileUrl(fileUrl);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),fastdfsVo);

            }

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0 ,BusinessExceptionStatusEnum.FastdfsConnectionFail.getDescription(),BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
    }

    /**
     *  删除图片
     * @param fileId  图片ID
     * @param tenementId 租户ID
     * @param userId 用户ID
     * @param source 平台
     * @param businessId 业务类型
     * @return
     */
    @RequestMapping("/deleteFileUpload")
    @ResponseBody
    public Object deleteFileUpload(	String fileId,String tenementId,String userId,
            	String source,String businessId){
            boolean success = false;//this.fileUploadService.deleteFileUpload(fileId,tenementId);
            if(success){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription());
            }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0 ,BusinessExceptionStatusEnum.FastdfsConnectionFail.getDescription(),BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
    }

    /**
     * 根据图片ID查看单条图片
     * @param fileId
     * @return
     */
    @RequestMapping("/selectTenementByFile")
    @ResponseBody
    public Object selectTenementByFile(String fileId){

        String fileUrl = this.fileUploadService.selectTenementByFile(fileId);
            FastdfsVo fastdfsVo=new FastdfsVo();
            fastdfsVo.setFileUrl(fileUrl);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),fastdfsVo);

    }

    @RequestMapping("/test1")
    @ResponseBody
    public Object test1(){
        ResponseResult responseResult=new ResponseResult();
        String fileUrl= null;
        try {
             this.fileUploadService.timingTaskTenementFile();
            FastdfsVo fastdfsVo=new FastdfsVo();
            responseResult.setIfSuc(1);
            responseResult.setMsg("success");
            fastdfsVo.setFileUrl(fileUrl);
            responseResult.setData(fastdfsVo);

        } catch (Exception e) {
            log.error("FileUploadController selectTenementByFile error, e : {}",e);
            e.printStackTrace();
        }
        return responseResult;
    }

    @RequestMapping("/test2")
    @ResponseBody
    public Object test2(){
       /* ResponseResult responseResult=new ResponseResult();

        try {
          //  List<YuncmMeetingRoom>rooms= this.yuncmMeetingService.selectAllListYuncmMeetingRoom(1,5);
            responseResult.setData(rooms);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;*/
       return null;
    }


    @RequestMapping("downloadTemplate")
    @ResponseBody
    public ResponseEntity<byte[]> downloadTemplate(HttpServletRequest request,HttpServletResponse response) throws IOException {

        String path= request.getRealPath("static/DownloadFile/001.xls");
        File file = new File(path);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        byte[] b= new byte[1024];
        int len = 0;
        try {
            inputStream = new FileInputStream(file);
            outputStream = response.getOutputStream();
            response.setContentType("multipart/form-data charset=UTF-8");
            String filename = "通讯录导入模板.xls";
            // 通常解决汉字乱码方法用URLEncoder.encode(...)
            String filenamedisplay = URLEncoder.encode(filename, "UTF-8");
            if ("FF".equals(getBrowser(request))) {
                // 针对火狐浏览器处理方式不一样了
                filenamedisplay = new String(filename.getBytes("UTF-8"),
                        "iso-8859-1");
            }
            response.addHeader("Content-Disposition","attachment; filename=" +filenamedisplay);
            response.setContentLength( (int) file.length( ) );

            while((len = inputStream.read(b)) != -1){
                outputStream.write(b, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(inputStream != null){
                try {
                    inputStream.close();
                    inputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outputStream != null){
                try {
                    outputStream.close();
                    outputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }



    // 以下为服务器端判断客户端浏览器类型的方法
    private String getBrowser(HttpServletRequest request) {
        String UserAgent = request.getHeader("USER-AGENT").toLowerCase();
        if (UserAgent != null) {
            if (UserAgent.indexOf("msie") >= 0)
                return "IE";
            if (UserAgent.indexOf("firefox") >= 0)
                return "FF";
            if (UserAgent.indexOf("safari") >= 0)
                return "SF";
        }
        return null;
    }




    /**
     * 上传测试
     */
    @RequestMapping(value = "/test",method = RequestMethod.POST)
    @ResponseBody
    public Object test(HttpServletRequest request,HttpServletResponse response) {
        //获取租户id

        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
          MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
          MultipartFile attach =multipartRequest.getFile("fileName");
            FastDFSFile file = null;
            try {
                file = new FastDFSFile(attach.getBytes(),attach.getOriginalFilename(),"jpg",attach.getSize()+"","");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //上传文件
            FastdfsVo fastdfsVo = FileManager.upload(file,null);

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),fastdfsVo);
    }


}
