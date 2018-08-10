package com.thinkwin.cron.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.MiddleBizAttachment;
import com.thinkwin.common.model.db.SysAttachment;
import com.thinkwin.common.utils.FileManager;
import com.thinkwin.core.service.LoginRegisterCoreService;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.cron.service.TimingTaskService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.log.service.SysLogService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/2 0002.
 */
@Service("timingTaskService")
public class TimingTaskServiceImpl implements TimingTaskService {
    private static final Logger logger = Logger.getLogger(TimingTaskServiceImpl.class);

    @Resource
    FileUploadService fileUploadService;

    @Autowired
    SaasTenantService saasTenantService;

    @Autowired
    private LoginRegisterCoreService loginRegisterCoreService;

    @Autowired
    SysLogService sysLogService;

  /*  @Override
    public String deleteAllTenantFile(String param) {
        return this.fileUploadService.deleteTenantFile(param);
    }*/

    /**
     * 删除租户所有文件信息
     * @param param
     * @return
     */
      @Override
      public String deleteAllTenantFile(String param) {
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
                      List<MiddleBizAttachment> middleBizAttachments = this.fileUploadService.selectMiddleBizAttachment(tenantId);
                      if (middleBizAttachments.size() != 0) {
                          List<String> fileValues = new ArrayList<String>();
                          for (MiddleBizAttachment attachment : middleBizAttachments) {
                              fileValues.add(attachment.getSysAttachment());
                          }
                          List<SysAttachment> sysAttachments = this.fileUploadService.selectTenantIdSysAttachment(fileValues);
                          for (SysAttachment sys : sysAttachments) {
                              boolean succ = false;
                              succ = fileManager.deleteFileUpload(sys.getGroup(), sys.getAttachmentPath() + sys.getFileName());
                              if (succ) {
                                  boolean success = this.fileUploadService.deleteByIdFileUpload(sys, tenantId);
                                  logger.info("删除解散企业的图片文件信息，删除结果：" + success);
                                  sysLogService.createLog(BusinessType.companyOp.toString(), EventType.company_dissolution.toString(), "删除解散企业的图片文件信息", BusinessExceptionStatusEnum.Success.getDescription(), Loglevel.info.toString());
                              }
                          }
                      }
                      this.saasTenantService.updateSaasTenantDeleteFileStatus(tenantId,1);
                      message = "true";
                  }
              } catch (Exception e) {
                  e.printStackTrace();
                  message = "false";
                  logger.error("删除解散企业的图片文件信息，异常！");
              }
          }else{
              message = "参数不能为空！";
              logger.error("删除解散企业的图片文件信息，参数不能为空！");
          }
          return message;
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
//                        loginRegisterCoreService.delDataBaseByName(dataBaseName);
//                    }
//                }
//            }
//        }
//    }
    public void delDBDate(){
        List<SaasTenant> saasTenants = saasTenantService.selectDisbandedEnterprise();
        if(null != saasTenants && saasTenants.size() > 0){
            for (SaasTenant saasTenant:saasTenants) {
                if(null != saasTenant){
                    String dataBaseName = saasTenant.getTenantCode() + "_" + saasTenant.getId();
                    if(org.apache.commons.lang3.StringUtils.isNotBlank(dataBaseName)){
                        //执行删除db数据库功能
                        boolean b = loginRegisterCoreService.delDataBaseByName(dataBaseName);
                        if(b) {
                            logger.info("删除企业解散时没有删除成功的DB物理数据库功能");
                            sysLogService.createLog(BusinessType.companyOp.toString(), EventType.company_dissolution.toString(), "删除企业解散时没有删除成功的DB物理数据库功能", BusinessExceptionStatusEnum.Success.getDescription(), Loglevel.info.toString());
                        }
                    }
                }
            }
        }
    }
}
