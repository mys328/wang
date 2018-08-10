
package com.thinkwin.fileupload.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.promotion.CapacityConfig;
import com.thinkwin.common.dto.promotion.PricingConfigDto;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.MiddleBizAttachment;
import com.thinkwin.common.model.db.SysAttachment;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.FileSizeConversion;
import com.thinkwin.common.utils.ResizeImage;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.common.vo.FileVo;
import com.thinkwin.core.service.LoginRegisterCoreService;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.fileupload.mapper.MiddleBizAttachmentMapper;
import com.thinkwin.fileupload.mapper.SysAttachmentMapper;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.fileupload.util.FastDFSFile;
import com.thinkwin.fileupload.util.FileCache;
import com.thinkwin.fileupload.util.FileManager;
import com.thinkwin.fileupload.util.FileManagerConfig;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.promotion.service.PricingConfigService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * 文件上传接口实现
 * <p>
 * 开发人员:daipengkai
 * 创建时间:2017/6/30
 */


@Service("fileUploadService")
public class FileUploadServiceImpl extends FileManagerConfig implements FileUploadService  {

    private static final Logger logger = Logger.getLogger(FileUploadService.class);
    @Autowired
    MiddleBizAttachmentMapper middleBizAttachmentMapper;
    @Autowired
    PricingConfigService pricingConfigService;
    @Autowired
    SysAttachmentMapper sysAttachmentMapper;
    @Resource
    SaasTenantService saasTenantService;

    @Autowired
    SysLogService sysLogService;

    @Autowired
    private LoginRegisterCoreService loginRegisterCoreService;

    @Override
    public List<SysAttachment>  deleteFileUpload(String fileId, String tenantId) {

        boolean success = false;
        //获取图片信息
        List<String> list = new ArrayList<String>();
        list.add(fileId);
        list.add(fileId+"_big");
        list.add(fileId+"_in");
        list.add(fileId+"_small");
        Map map =new HashMap();
        map.put("list",list);
        List<SysAttachment> sysAttachments = this.selectSysAttachmentInfo(map);
        int flag = 0;
        if (sysAttachments.size() != 0) {
             for (SysAttachment sys : sysAttachments) {
                 if(sys.getId().equals(fileId) && StringUtils.isNotBlank(tenantId)){
                     //查看本地缓存是否存当前租户的所使用空间
                     String useSpace = FileCache.getTenantFile(tenantId);
                     if (StringUtils.isNotBlank(sys.getAttachmentSize()) && StringUtils.isNotBlank(useSpace)) {
                         long size = Long.valueOf(useSpace) - Long.valueOf(sys.getAttachmentSize());
                         FileCache.addTenantFile(tenantId, String.valueOf(size));
                     }
                 }
                 Example example = new Example(MiddleBizAttachment.class);
                 Example.Criteria criteria = example.createCriteria();
                 criteria.andEqualTo("sysAttachment", sys.getId());
                 List<MiddleBizAttachment> attachments = this.middleBizAttachmentMapper.selectByExample(example);
                 //成功则删除系统文件表
                 if (attachments.size() != 0) {
                     int n = middleBizAttachmentMapper.deleteByPrimaryKey(attachments.get(0).getId());
                     if (n > 0) {
                         flag = sysAttachmentMapper.deleteByPrimaryKey(sys.getId());
                     }
                 }
             }
        }
        if (flag > 0) {
            return sysAttachments;
        }
        return null;
    }

    @Override
    public SysAttachment downloadFlie(String fileId) {
        SysAttachment sysAttachment = null;

        sysAttachment = new SysAttachment();
        sysAttachment.setId(fileId);
        sysAttachment = sysAttachmentMapper.selectOne(sysAttachment);

        return sysAttachment;
    }

    @Override
    public String selectTenementByFile(String fileId) {
        String fileUrl = null;

        SysAttachment sysAttachment = new SysAttachment();
        if(StringUtils.isNotBlank(fileId)) {
            sysAttachment.setId(fileId);
            //根据ID查看图片路径
            SysAttachment table = sysAttachmentMapper.selectOne(sysAttachment);
            if (table != null) {
                fileUrl = TRACKER_NGNIX_ADDR
                        + SEPARATOR + table.getGroup() + "/" + table.getAttachmentPath() + table.getFileName();
            }
        }
        return fileUrl;
    }

    @Override
    public SysAttachment selectByidFile(String fileId) {

        SysAttachment table = null;

        SysAttachment sysAttachment = new SysAttachment();
        sysAttachment.setId(fileId);
        //根据ID查看图片路径
        table = sysAttachmentMapper.selectOne(sysAttachment);

        return table;
    }

    @Override
    public String selectTenantFileSize(String tenantId,String space) {
        String fileSize = null;
        SaasTenant saasTenant = saasTenantService.selectByIdSaasTenantInfo(tenantId);
        if("0".equals(saasTenant.getTenantType())){
            //获取定价配置
            PricingConfigDto configDto = pricingConfigService.getPricingConfig();
            //获取免费价格
            List<CapacityConfig> configs = configDto.getFreeAccountConfig();
            for(CapacityConfig config : configs){
                //储存空间
                if("101".equals(config.getSku())){
                    space = config.getQty()+"";
                    break;
                }
            }
        }
        //向redis获取租户的总空间大小
        long lo= Long.valueOf(space) * 1024 * 1024;
        String totalSpace =  String.valueOf(lo);
        //从本地缓存获取租户已使用空间的大小
        String useSpace = FileCache.getTenantFile(tenantId);
        if (useSpace == null) {
            //获取租户使用的空
            useSpace = selectTenantuseSpace(tenantId);

        }
        if(Integer.parseInt(useSpace) < 1){
            useSpace = "1";
        }
        //判断当前是否为数字
        if (FileSizeConversion.isNumeric(totalSpace) && FileSizeConversion.isNumeric(useSpace)) {
            long totalSpaceInt = Long.valueOf(totalSpace);
            long useSpaceInt = Long.valueOf(useSpace);
            //获取剩余空间大小
            fileSize = String.valueOf(totalSpaceInt - useSpaceInt);
        } else {
            return totalSpace;
        }
        return fileSize;
    }

    @Override
    public String selectTenantuseSpace(String tenantId) {
        String useSpace = null;
        MiddleBizAttachment middleBizAttachment = new MiddleBizAttachment();
        middleBizAttachment.setTenantId(tenantId);
        List<MiddleBizAttachment> middleBizAttachments = this.middleBizAttachmentMapper.select(middleBizAttachment);
        if (middleBizAttachments.size() == 0) {//租户第一次上传文件没有使用存储空间
            //租户使用空间
            useSpace = "0";
        } else {
            List<Integer> integers = new ArrayList<Integer>();
            List<String> fileValues = new ArrayList<String>();
            for (MiddleBizAttachment attachment : middleBizAttachments) {
                fileValues.add(attachment.getSysAttachment());
            }
            //查询所有文件
            Example example = new Example(SysAttachment.class, true, true);
            example.or().andIn("id", fileValues);
            List<SysAttachment> sysAttachments = this.sysAttachmentMapper.selectByExample(example);
            long b = 0;
            for (int i = 0; i < sysAttachments.size(); i++) {
                long a = Long.valueOf(sysAttachments.get(i).getAttachmentSize());
                b = b + a;
            }
            useSpace = String.valueOf(b);
            FileCache.addTenantFile(tenantId, useSpace);
        }
        return useSpace;
    }

    @Override
    public void timingTaskTenementFile() {
        //获取所有租户ID
        List<MiddleBizAttachment> middleBizAttachments = middleBizAttachmentMapper.insertTimingTaskTenementFile();
        if (middleBizAttachments.size() != 0) {
            for (MiddleBizAttachment attachment : middleBizAttachments) {
                String tenentId = attachment.getTenantId();
                selectTenantuseSpace(tenentId);
            }
        }
    }



    @Override
    public String selectAllTenantuseSpace(String tenantId, String type) {
        String useSpace=null;
        long size = 0 ;
        MiddleBizAttachment middleBizAttachment=new MiddleBizAttachment();
        middleBizAttachment.setTenantId(tenantId);
        List<MiddleBizAttachment> middleBizAttachments=this.middleBizAttachmentMapper.select(middleBizAttachment);
        if(middleBizAttachments.size()==0){//租户第一次上传文件没有使用存储空间
            //租户使用空间
            useSpace="0";
        }else {
            List<Integer> integers = new ArrayList<Integer>();
            List<String> fileValues = new ArrayList<String>();
            for (MiddleBizAttachment attachment : middleBizAttachments) {
                fileValues.add(attachment.getSysAttachment());
            }
            //查询所有文件
            Example example = new Example(SysAttachment.class, true, true);
            example.or().andIn("id", fileValues);
            List<SysAttachment> sysAttachments = this.sysAttachmentMapper.selectByExample(example);
            long b = 0;
            for (int i = 0; i < sysAttachments.size(); i++) {
                long a = Long.valueOf(sysAttachments.get(i).getAttachmentSize());
                b = b + a;
            }
            size = b;
        }
        if(!"0".equals(useSpace)){
            if("MB".equals(type)){
                FileSizeConversion.getPrintSizeMB(size);
            }
            if("GB".equals(type)){
                FileSizeConversion.getPrintSizeGB(size);
            }

        }
        return useSpace;
    }

    @Override
    public String insertFileUploadCondition(FastdfsVo fastdfsVo, String tenantId, String source, String userId, String uuId) {

        String success = "";
        if(fastdfsVo != null) {
            //文件中间表
            MiddleBizAttachment middleBizAttachment = new MiddleBizAttachment();
            middleBizAttachment.setId(CreateUUIdUtil.Uuid());
            //文件表
            SysAttachment sysAttachment = new SysAttachment();
            if(!StringUtils.isNotBlank(uuId)){
                sysAttachment.setId(CreateUUIdUtil.Uuid());
            }else{
                sysAttachment.setId(uuId);
            }
            middleBizAttachment.setBizId("");
            middleBizAttachment.setBizType("");
            middleBizAttachment.setPlatformType(source);
            middleBizAttachment.setSysAttachment(sysAttachment.getId());
            middleBizAttachment.setTenantId(tenantId);

            sysAttachment.setAttachmentPath(fastdfsVo.getPath());
            sysAttachment.setCreaterId(userId);
            sysAttachment.setCreateTime(new Date());
            sysAttachment.setFileCnName(fastdfsVo.getName());
            sysAttachment.setFileName(fastdfsVo.getFileName());
            sysAttachment.setGroup(fastdfsVo.getGroup());
            sysAttachment.setModifyerId("");
            sysAttachment.setModifyTime(new Date());
            sysAttachment.setUnit("KB");
            sysAttachment.setAttachmentSize(fastdfsVo.getLength());
            middleBizAttachmentMapper.insert(middleBizAttachment);
            sysAttachmentMapper.insert(sysAttachment);
            //是否需要增加租户空间容量
            if(StringUtils.isNotBlank(tenantId)) {
                //查看本地缓存是否存当前租户的所使用空间
                String useSpace = FileCache.getTenantFile(tenantId);
                if (useSpace == null) {
                    //不存在的话查询增加到本地缓存
                    selectTenantuseSpace(tenantId);
                } else { //存在的话在本地缓存上增加
                    //当前存储的文件大小
                    logger.info("***************------使用空间：" + useSpace + "-------********************");
                    String str = FileSizeConversion.convertFileSize(Long.valueOf(fastdfsVo.getLength()));
                    logger.info("***************------使用空间：" + str + "-------********************");
                    long size = Long.valueOf(useSpace) + Long.valueOf(str);
                    //更新本地缓存
                    FileCache.addTenantFile(tenantId, String.valueOf(size));
                }
            }
            success = sysAttachment.getId();
        }
        return success;
    }



    /**
     * 租户上传方法
      * @param map
     * @param tenantId
     * @param source
     * @param userId
     * @param bytes
     * @param spaceNum
     * @return
     */
    public FileVo insertFileUploadTenant(String tenantId, String source, String userId,Integer spaceNum,String ext,List<FastdfsVo> vos) {

        FileVo fileVo = new FileVo();
        String uploadFileSize = "";
        if(vos.size() != 0) {
            FastdfsVo fsvo = vos.get(0);
            //是否为租户上传租户计算空间大小
            if(StringUtils.isNotBlank(tenantId)) {
                String size = FileSizeConversion.convertFileSize(Long.valueOf(fsvo.getLength()));
                if (StringUtils.isNotBlank(String.valueOf(fsvo.getLength()))) {
                    uploadFileSize = size;
                    //获取租户上传的空间剩余大小
                    String totalSpace = this.selectTenantFileSize(tenantId, spaceNum + "");
                    if (Long.valueOf(uploadFileSize) > Long.valueOf(totalSpace)) {
                        fileVo.setId("0");
                        return fileVo;
                    }
                }
            }
            String fileId = "";
            for (int i=0;i<vos.size();i++){
                 switch (i){
                    case 0:
                        fileId = this.insertFileUploadCondition(vos.get(i), tenantId, source, userId, "");
                        continue;
                    case 1:
                        this.insertFileUploadCondition(vos.get(i), tenantId, source, userId, fileId+"_big");
                        fileVo.setBig(vos.get(i).getFileUrl());
                        continue;
                    case 2:
                        this.insertFileUploadCondition(vos.get(i), tenantId, source, userId, fileId+"_in");
                        fileVo.setIn(vos.get(i).getFileUrl());
                        continue;
                    case 3:
                        this.insertFileUploadCondition(vos.get(i), tenantId, source, userId, fileId+"_small");
                        fileVo.setSmall(vos.get(i).getFileUrl());
                        continue;
                 }
            }
            fileVo.setId(fileId);
            fileVo.setPrimary(fsvo.getFileUrl());
        }
        return fileVo;
    }


    @Override
    public List<SysAttachment> selectSysAttachmentInfo(Map map) {
        return this.sysAttachmentMapper.selectSysAttachmentInfo(map);
    }
    @Override
    public boolean deleteByIdFileUpload(SysAttachment sysAttachment, String tenantId) {
        boolean success = false;
        int flag = 0;
        //查看本地缓存是否存当前租户的所使用空间
        if(StringUtils.isNotBlank(tenantId)) {
            String useSpace = FileCache.getTenantFile(tenantId);
            if (StringUtils.isNotBlank(sysAttachment.getAttachmentSize()) && StringUtils.isNotBlank(useSpace)) {
                long size = Long.valueOf(useSpace) - Long.valueOf(sysAttachment.getAttachmentSize());
                FileCache.addTenantFile(tenantId, String.valueOf(size));
            }
        }
        Example example = new Example(MiddleBizAttachment.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sysAttachment", sysAttachment.getId());
        List<MiddleBizAttachment> attachments = this.middleBizAttachmentMapper.selectByExample(example);
        //成功则删除系统文件表
        if (attachments.size() != 0) {
            int n = middleBizAttachmentMapper.deleteByPrimaryKey(attachments.get(0).getId());
            if (n > 0) {
                flag = sysAttachmentMapper.deleteByPrimaryKey(sysAttachment.getId());
            }
        }
        if (flag > 0) {
            success = true;
        } else {
            success = false;
        }

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
        Map<String,Object> maps = new HashMap<>();
        maps.put("list",list);
        List<SysAttachment> sysAttachments = this.sysAttachmentMapper.selectSysAttachmentInfo(maps);
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

    @Override
    public void deleteTenantFile(List<SysAttachment> sysAttachments, String tenantId) {
        //从服务器删除图片
        String [] str = {"10.10.11.56:22122"};
        FileManager fileManager = new FileManager();
        for(SysAttachment sys : sysAttachments){
            boolean succ = false;
            succ = fileManager.deleteFileUpload(sys.getGroup(), sys.getAttachmentPath() + sys.getFileName());
            if(succ){
              boolean  success = deleteByIdFileUpload(sys,tenantId);
              logger.info("删除结果：" + success);
            }
        }
    }

    /*@Override
    public String deleteTenantFile(String param) {
        String message = "";
        if(StringUtils.isNotBlank(param)) {

            try {
                //JSONObject object = JSON.parseObject(param);
                JSON.parseArray(param);
                JSONObject ss = JSON.parseObject(JSON.parseArray(param).get(0).toString());
                String ip = (String)ss.get("fastDfsServerIp");
                Integer port = (Integer)ss.get("port");
                String [] str = {ip+":"+port};
                FileManager fileManager = new FileManager(str);
                //获取租户id
                List<SaasTenant> saasTenants = saasTenantService.selectDisbandedEnterprise();
                System.out.println(saasTenants);
                for(SaasTenant saasTenant : saasTenants) {
                    String tenantId = saasTenant.getId();
                    //获取租户下的所有文件
                    MiddleBizAttachment middleBizAttachment = new MiddleBizAttachment();
                    middleBizAttachment.setTenantId(tenantId);
                    List<MiddleBizAttachment> middleBizAttachments = this.middleBizAttachmentMapper.select(middleBizAttachment);
                    if (middleBizAttachments.size() != 0) {
                        List<String> fileValues = new ArrayList<String>();
                        for (MiddleBizAttachment attachment : middleBizAttachments) {
                            fileValues.add(attachment.getSysAttachment());
                        }
                        //查询所有文件
                        Example example = new Example(SysAttachment.class, true, true);
                        example.or().andIn("id", fileValues);
                        List<SysAttachment> sysAttachments = this.sysAttachmentMapper.selectByExample(example);
                        for (SysAttachment sys : sysAttachments) {
                            boolean succ = false;
                            succ = fileManager.deleteFileUpload(sys.getGroup(), sys.getAttachmentPath() + sys.getFileName());
                            if (succ) {
                                boolean success = deleteByIdFileUpload(sys, tenantId);
                                logger.info("删除结果：" + success);
                                sysLogService.createLog(BusinessType.companyOp.toString(), EventType.company_dissolution.toString(), "删除解散企业的图片文件信息", BusinessExceptionStatusEnum.Success.getDescription(), Loglevel.info.toString());
                            }
                        }
                    }
                    message = "true";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }else{
            message = "参数不能为空！";
            logger.error("参数不能为空！");
        }
        return message;
    }*/

    @Override
    public List<MiddleBizAttachment> selectMiddleBizAttachment(String tenantId) {
        MiddleBizAttachment middleBizAttachment = new MiddleBizAttachment();
        middleBizAttachment.setTenantId(tenantId);
        return this.middleBizAttachmentMapper.select(middleBizAttachment);
    }

    @Override
    public List<SysAttachment> selectTenantIdSysAttachment(List<String> fileValues) {
        Example example = new Example(SysAttachment.class, true, true);
        example.or().andIn("id", fileValues);
        return this.sysAttachmentMapper.selectByExample(example);
    }



    @Override
    public String downloadFile(String fileId, String path) {

        File fp = new File(path);
        if(!fp.exists()){
            fp.mkdirs();
        }
        SysAttachment att=  this.selectByidFile(fileId);
        if(att != null) {
            boolean b = FileManager.downloadFile(att.getGroup(), att.getAttachmentPath() + att.getFileName(), path + att.getFileCnName());
            if (b) {
                return att.getFileCnName();
            }
        }
        return "";
    }


    @Override
    public FileVo insertFileUpload(String source, String userId,  String tenantId, Integer spaceNum,String ext,List<FastdfsVo> vos) {

        FileVo vo = this.insertFileUploadTenant(tenantId,source,userId,spaceNum,ext,vos);
        return vo;
    }

    @Override
    public String insertPlatformFileUpload(FastdfsVo fastdfsVo, String source, String userId) {
        String success = "";
        if(fastdfsVo != null) {
            //文件中间表
            MiddleBizAttachment middleBizAttachment = new MiddleBizAttachment();
            middleBizAttachment.setId(CreateUUIdUtil.Uuid());
            //文件表
            SysAttachment sysAttachment = new SysAttachment();
            sysAttachment.setId(CreateUUIdUtil.Uuid());

            middleBizAttachment.setBizId("");
            middleBizAttachment.setBizType("");
            middleBizAttachment.setPlatformType(source);
            middleBizAttachment.setSysAttachment(sysAttachment.getId());
            middleBizAttachment.setTenantId("");

            sysAttachment.setAttachmentPath(fastdfsVo.getPath());
            sysAttachment.setCreaterId(userId);
            sysAttachment.setCreateTime(new Date());
            sysAttachment.setFileCnName(fastdfsVo.getName());
            sysAttachment.setFileName(fastdfsVo.getFileName());
            sysAttachment.setGroup(fastdfsVo.getGroup());
            sysAttachment.setModifyerId("");
            sysAttachment.setModifyTime(new Date());
            sysAttachment.setUnit("KB");
            sysAttachment.setAttachmentSize(fastdfsVo.getLength());
            middleBizAttachmentMapper.insert(middleBizAttachment);
            sysAttachmentMapper.insert(sysAttachment);
            success = sysAttachment.getId();
        }
        return success;
    }



    /**
     * 处理数据库删除时未能删除的物理库的定时删除接口功能
     */
//    public void delDBDate(){
//        List<SaasTenant> saasTenants = saasTenantService.selectDisbandedEnterprise();
//        if(null != saasTenants && saasTenants.size() > 0){
//            for (SaasTenant saasTenant:saasTenants) {
//                if(null != saasTenant){
//                    String dataBaseName = saasTenant.getTenantCode() + "_" + saasTenant.getId();
//                    if(org.apache.commons.lang3.StringUtils.isNotBlank(dataBaseName)){
//                        //执行删除db数据库功能
//                        boolean b = loginRegisterCoreService.delDataBaseByName(dataBaseName);
//                        if(b) {
//                            logger.info("删除企业解散时没有删除成功的DB物理数据库功能");
//                            sysLogService.createLog(BusinessType.companyOp.toString(), EventType.company_dissolution.toString(), "删除企业解散时没有删除成功的DB物理数据库功能", BusinessExceptionStatusEnum.Success.getDescription(), Loglevel.info.toString());
//                        }
//                        }
//                }
//            }
//        }
//    }
















}

