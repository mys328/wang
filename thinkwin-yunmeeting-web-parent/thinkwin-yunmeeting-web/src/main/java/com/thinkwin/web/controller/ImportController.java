package com.thinkwin.web.controller;

import com.thinkwin.auth.service.*;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.promotion.CapacityConfig;
import com.thinkwin.common.dto.promotion.PricingConfigDto;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.ExcelVO;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.core.SaasUserTenantMiddle;
import com.thinkwin.common.model.core.SaasUserWeb;
import com.thinkwin.common.model.db.SysOrganization;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.validation.LoginRegisterValidationUtil;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.promotion.service.PricingConfigService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.Login;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

/**
 * User: yinchunlei
 * Date: 2017/8/2.
 * Company: thinkwin
 * 导入功能controller类
 */
@Controller
public class ImportController {
    private static Log log = LogFactory.getLog(ImportController.class);
    @Resource
    private UserService userService;
    @Resource
    private SysLogService sysLogService;
    @Resource
    private LoginRegisterService loginRegisterService;

    @Resource
    private OrganizationService organizationService;
    @Resource
    private ImportExcelService importExcelService;
    @Resource
    private PricingConfigService pricingConfigService;
    @Resource
    private SaasTenantService saasTenantService;
    @Resource
    private com.thinkwin.core.service.SaasTenantService saasTenantCoreService;

    /**
     * 测试跳转页
     * @param model
     * @param token
     * @return
     */
   @RequestMapping("/goImprotPage")
   public String goToImportPage(Model model, String token){
       String userId = TenantContext.getUserInfo().getUserId();
       model.addAttribute("userId",userId);
       model.addAttribute("token",token);
       return "importexcel";
   }


    /**
     * 导入预览功能
     * @return
     * @throws Exception
     *//*
    @RequestMapping(value="/importPreviewExcel",method= RequestMethod.POST)
    @ResponseBody
   public ResponseResult importPreviewExcel(HttpServletRequest request) throws Exception {
        Properties properties = new Properties();
            try {
                InputStream is = ImportController.class.getClassLoader().getResourceAsStream("server.properties");
                properties.load(is);
            } catch (Exception e) {
                e.getStackTrace();
            }
            String basePath = properties.getProperty("file.outputPath");
        //第一步转化request
        MultipartHttpServletRequest rm = (MultipartHttpServletRequest) request;
        //该方法可以直接根据前端传过参数名直接转为commonsMultipartFile类型
            //CommonsMultipartFile cfile = (CommonsMultipartFile) myFile;
        CommonsMultipartFile cfile = (CommonsMultipartFile) rm.getFile("myfile");
            //调用文件上传功能做临时缓存功能并返回缓存路径
            String picture = FileOperateUtil.uploads(cfile, "template");
 //       String os = System.getProperty("os.name");
//        if(os.toLowerCase().startsWith("win")){
//            System.out.println(os + " can't gunzip4444444444444444444444444");
//            path = basePath + "\\" + picture;
//        }else {
//            path = basePath + "/" + picture;
//        }
        String path = basePath + File.separator+ picture;

        //读取Excel
            Map<String, String> table = new HashMap<String, String>();
            table.put("员工编号", "userNum");
            table.put("职位", "position");
            table.put("部门", "depar");
            table.put("姓名", "name");
            table.put("性别", "sex");
            table.put("手机号", "phoneNum");
            table.put("邮箱", "email");
            String[] myfieldNames = ReadExcel.impotrHead(path, table);//头解析
            if(myfieldNames==null || (myfieldNames!=null && myfieldNames.length==0)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "导入的数据模板头信息有误");
            }
            List<ExcelVO> excellist = new ArrayList<>();
            Map<Integer, String> map = ReadExcel.readExcelContent(path);//数据内容
            excellist = ReadExcel.readExcels(ExcelVO.class, table, myfieldNames, map);
//        if(os.toLowerCase().startsWith("win")){
//            System.out.println(os + " can't gunzip555555555555555555555555");
//            clearFiles(basePath + "\\template");
//        }else {
//            clearFiles(basePath + "/template");
//        }
        clearFiles(basePath + File.separator+"template");
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), excellist);
 }

    //删除文件和目录
    private void clearFiles(String workspaceRootPath){
        File file = new File(workspaceRootPath);
        if(file.exists()){
            deleteFile(file);
        }
    }
    //删除文件和目录
    private void deleteFile(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(int i=0; i<files.length; i++){
                deleteFile(files[i]);
            }
        }
        file.delete();
    }*/
    /**
     * 导入预览功能
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/importPreviewExcel", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult importPreviewExcel(HttpServletRequest request) throws Exception {
        Properties properties = new Properties();
        try {
            InputStream is = ImportController.class.getClassLoader().getResourceAsStream("server.properties");
            properties.load(is);
        } catch (Exception e) {
            e.getStackTrace();
            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.batch_import.toString(),"批量导入员工失败",BusinessExceptionStatusEnum.Failure.getDescription(), Loglevel.error.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
        }
        String basePath = properties.getProperty("file.outputPath");
        log.info("basePath basePath :::::"+basePath);
        //第一步转化request
        MultipartHttpServletRequest rm = (MultipartHttpServletRequest) request;
        //该方法可以直接根据前端传过参数名直接转为commonsMultipartFile类型
        CommonsMultipartFile cfile = (CommonsMultipartFile) rm.getFile("myfile");
        //调用文件上传功能做临时缓存功能并返回缓存路径
        String picture = FileOperateUtil.uploads(cfile, "template");
        String path = basePath + File.separator + picture;
        log.info("path11 path11 :::::"+path);
        //读取Excel
        Map<String, String> table = new HashMap<String, String>();
        table.put("员工编号", "userNum");
        table.put("职位", "position");
        table.put("部门", "depar");
        table.put("姓名", "name");
        table.put("性别", "sex");
        table.put("手机号", "phoneNum");
        table.put("邮箱", "email");
        String[] myfieldNames = null;
        List<ExcelVO> excellist = new ArrayList<>();
        String prefix=path.substring(path.lastIndexOf("."));
        if(StringUtils.isNotBlank(prefix) && ".csv".equals(prefix)){
            File file = new File(path);
            BufferedReader br = null;
            InputStreamReader reader =   null;
            try {
                reader = new InputStreamReader(new FileInputStream(file),"GBK");
                br = new BufferedReader(reader);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "导入的文件信息有误！");
            }
            int i = 0;
            String line = "";
            String everyLine = "";
            try {
                while ((line = br.readLine()) != null) {  //读取到的内容给line变量
                    i += 1;
                    everyLine = line;
                    if(i==8){
                        String[] split = everyLine.split(",");
                        myfieldNames = split;
                    }
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
                clearFiles(basePath + File.separator + "template");
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }else {
            myfieldNames = ReadExcel.impotrHead(path, table);//头解析
        }
        List<String> arrList = new ArrayList<>();
        if(null != myfieldNames)
        {
            arrList =  Arrays.asList(myfieldNames);
        }else{
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "导入的数据模板头信息有误");
        }
        if(!arrList.contains("userNum") && !arrList.contains("depar")&& !arrList.contains("phoneNum") && !arrList.contains("员工编号") && !arrList.contains("部门")){
            //clearFiles(path);
            //调用文件上传功能做临时缓存功能并返回缓存路径
            String picture1 = FileOperateUtil.newUploads(cfile, path);

            path = picture1;
            //String path1 = basePath + File.separator + picture1;
            myfieldNames = ReadExcel.impotrHead(picture1, table);//头解析
        }
        if (myfieldNames == null || (myfieldNames != null && myfieldNames.length == 0)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "导入的数据模板头信息有误");
        }
        log.info("myfieldNames  数据： :::"+myfieldNames);
        Map<Integer, String> map = new HashMap<>();
        if(StringUtils.isNotBlank(prefix) && ".csv".equals(prefix)){
            if(!arrList.contains("userNum") && !arrList.contains("depar")&& !arrList.contains("phoneNum")&& !arrList.contains("员工编号") && !arrList.contains("部门")){
                map = ReadExcel.readExcelContent(path);//数据内容
            }else {
                map = readCsvFile(path);
            }
        }else {
            map = ReadExcel.readExcelContent(path);//数据内容
        }
        if (null == map) {
            clearFiles(basePath + File.separator + "template");
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
        }
        if(StringUtils.isNotBlank(prefix) && ".csv".equals(prefix)){
            if(!arrList.contains("userNum") && !arrList.contains("depar")&& !arrList.contains("phoneNum")&& !arrList.contains("员工编号") && !arrList.contains("部门")){
                excellist = ReadExcel.readExcels(ExcelVO.class, table, myfieldNames, map);
            }else {
                excellist = readCsvExcelFiles(table, myfieldNames, map);
            }
        }else {
            excellist = ReadExcel.readExcels(ExcelVO.class, table, myfieldNames, map);
        }
        String s = basePath + File.separator + "template";
        clearFiles(basePath + File.separator + "template");
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), excellist);
    }

    /**
     * 读取csv文件内容
     * @param table
     * @param fieldNames
     * @param map
     * @return
     */
    public List<ExcelVO> readCsvExcelFiles(Map<String, String> table,String[] fieldNames,Map<Integer, String> map){
        List<ExcelVO> list = new ArrayList<>();
        if(null != map) {
            for (int i = 0; i < map.size(); i++) {
                String s = map.get(i + 3);
                if (StringUtils.isNotBlank(s)) {
                    ExcelVO excelVO = new ExcelVO();
                    String row[] = s.split(",");
                    for (int j = 0; j < fieldNames.length; j++) {
                        String fieldName = fieldNames[j];
                        if (j == row.length) {
                            break;
                        }
                        // 获取excel单元格的内容
                        String cell = row[j];
                        String content = "";
                        if (cell != null) {
                            content = cell;
                        }
                        String s1 = table.get(fieldName);
                        if(StringUtils.isNotBlank(s1) && StringUtils.isNotBlank(content)) {
                            log.info("content :: content :: content ::"+content);
                            try {
                                log.info("csv 数据： "+s1+"........."+content);
                                setValue(excelVO, s1, content);
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    }
                    list.add(excelVO);
                }
            }

        }
        return list;
    }

    /**
     使用反射机制动态调用dto的set方法根据参数 属性名 如 name 调用dto的 setName方法 完成赋值
     * @param dto
     * @param name
     * @param value
     * @throws Exception
     */
    public void setValue(Object dto,String name,Object value) throws Exception{
        Method[] m = dto.getClass().getMethods();
        for(int i=0;i<m.length;i++){
            if(("set"+name).toLowerCase().equals(m[i].getName().toLowerCase())){
                m[i].invoke(dto,value);
                break;
            }
        }
    }
    /**
     * 获取csv文件的数据
     * @param path
     * @return
     */
    public Map<Integer, String> readCsvFile(String path){
        File file1 = new File(path);
        Map<Integer, String> map = new HashMap<>();
        InputStreamReader reader =   null;
        int j = 2;
        BufferedReader br = null;
        try {
//            reader = new InputStreamReader(new FileInputStream(file1),"UTF-8");
            reader = new InputStreamReader(new FileInputStream(file1),"GBK");
            br = new BufferedReader(reader);
           // br = new BufferedReader(new FileReader(path));
        } catch (Exception e) {
            log.info("xxxxxxx new BufferedReader(new FileReader(path));时问题出现啦！！！！！！！！！！！！！！！！！！");
            e.printStackTrace();
            return null;
        }
        int i = 0;
        String line = "";
        String everyLine = "";
        try {
            while ((line = br.readLine()) != null) {  //读取到的内容给line变量
                i += 1;
                everyLine = line;
                log.info("everyLine   everyLine :::::"+everyLine);
                if(i>8){
                    j += 1;
                    map.put(j,everyLine);
                }
            }
            br.close();
        } catch (IOException e) {
            log.info("xxxxxxx(line = br.readLine())时问题出现啦！！！！！！！！！！！！！！！！！！");
            e.printStackTrace();
            return null;
        }
        log.info("xxxxxxx map数据：：：！！！！！！！！！！！！！！！！！！"+map);
        return map;
    }
    //删除文件和目录
    private void clearFiles(String workspaceRootPath) {
        File file = new File(workspaceRootPath);
        if (file.exists()) {
            deleteFile(file);
        }
    }

    //删除文件和目录
    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
        }
        file.delete();
    }
    /**
     * 用户获取可上传人员上限和公司现有人数
     * @return
     */
    @RequestMapping("/getUserNum")
    @ResponseBody
    public ResponseResult userGetMixNmuAndNowNum(String orgId){
        String tenantId = TenantContext.getTenantId();
        Map map = new HashMap();
       // TenantContext.setTenantId("0");
        int userMaixNum = 0;
        SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
        TenantContext.setTenantId(tenantId);
        //判断用户是否是免费用户
        if(null != saasTenant){
            if("0".equals(saasTenant.getTenantType())) {
                //获取定价配置
                PricingConfigDto configDto = pricingConfigService.getPricingConfig();
                //获取免费价格
                List<CapacityConfig> configs = configDto.getFreeAccountConfig();
                for (CapacityConfig config : configs) {
                    //员工人数
                    if ("102".equals(config.getSku())) {
                        saasTenant.setExpectNumber(config.getQty());
                        break;
                    }
                }
            }
            Integer expectNumber = saasTenant.getExpectNumber();
            if(null != expectNumber && expectNumber != 0){
                userMaixNum = expectNumber;
            }
        }
        map.put("userMaixNum",userMaixNum);
        int userNowNum = 0;
        Integer userTotalNum = userService.selectUserTotalNum();
        if(null != userTotalNum && userTotalNum != 0){
            userNowNum = userTotalNum;
        }
        map.put("userNowNum",userNowNum);
        if(StringUtils.isNotBlank(orgId)){
            List<SysOrganization> sysOrganizations2 = organizationService.selectOrganiztions();
            if(null != sysOrganizations2 && sysOrganizations2.size() > 0) {
                String catalog = userService.getUserCataLog(orgId,sysOrganizations2);
                if(StringUtils.isNotBlank(catalog)) {
                    map.put("catalog", catalog);
                }
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map ,BusinessExceptionStatusEnum.Success.getCode());
    }

    public Map excelValidation(List<ExcelVO> excelVOList){
        String tenantId = TenantContext.getTenantId();
        Map map = new HashMap();
        if(null != excelVOList && excelVOList.size() > 0){
            for (ExcelVO excelVo:excelVOList) {
                if(null != excelVo){
                    //校验手机号码
                    String phoneNum = excelVo.getPhoneNum();
                    if(StringUtils.isNotBlank(phoneNum)){
                        boolean mobile = ValidatorUtil.isMobile(phoneNum);
                        if(!mobile){
                            map.put("errorMsg",ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "手机号码 :"+phoneNum+" 格式有误！修改后重新导入！"));
                            return map;
                        }
                        String name = excelVo.getName();
                        if(StringUtils.isBlank(name)){
                            map.put("errorMsg",ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "手机号码 :"+phoneNum+" 用户名不能为空！信息完善后重新导入！"));
                            return map;
                        }
                        if(!ValidatorUtil.isUsername(name)){
                            map.put("errorMsg",ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "用户名 :"+name +" 格式有误！修改后重新导入！"));
                            return map;
                        }
                        if(name.length()>10){
                            map.put("errorMsg",ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "用户名 :"+name +" 长度受限！修改后重新导入！"));
                            return map;
                        }
                        //添加判断用户与以前公司的状态
                        //切换数据源
                        //TenantContext.setTenantId("0");
                        SaasUserWeb saasUserWeb = saasTenantCoreService.selectUserLoginInfo(null, phoneNum);
                       // SaasUserWeb saasUserWeb = loginRegisterService.selectUserLoginInfo(null, phoneNum);
                        if (null != saasUserWeb) {
                            String userWebId = saasUserWeb.getUserId();
                            String userOldTenantId = saasUserWeb.getTenantId();
                            if(StringUtils.isNotBlank(userOldTenantId) && !userOldTenantId.equals(tenantId)) {
                                SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(userOldTenantId);
                                if (null != saasTenant && saasTenant.getStatus() != 2){
                                    TenantContext.setTenantId(userOldTenantId);
                                SysUser sysUser = userService.selectUserByUserId(userWebId);
                                if (null != sysUser) {
                                    Integer status = sysUser.getStatus();
                                    if (null != status && status != 89) {
                                        map.put("errorMsg", ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请" + sysUser.getPhoneNumber() + "与上家公司确认离职关系"));
                                        return map;
                                    }
                                }
                            }
                            }
                        }
                    }else{
                        map.put("errorMsg",ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "手机号不能为空！修改后重新导入！"));
                        return map;
                    }
                    //校验email
                    String email = excelVo.getEmail();
                    if(StringUtils.isNotBlank(email)){
                        boolean email1 = ValidatorUtil.isEmail(email);
                        if(!email1){
                            map.put("errorMsg",ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "Email :"+email+" 格式有误！修改后重新导入！"));
                            return map;
                        }
                        if(email.length()>50){
                            map.put("errorMsg",ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "邮箱 :"+email +" 长度受限！修改后重新导入！"));
                            return map;
                        }
                    }
                    TenantContext.setTenantId(tenantId);
                    //2017-9-12日上午会议定为批量导入时部门不存在就新建所以该判断需要去掉
                    //校验部门信息
                    String depar = excelVo.getDepar();
                    if (StringUtils.isBlank(depar)) {
                        map.put("errorMsg",ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "手机号 ："+phoneNum+" 的部门名称不能为空，修改后重新导入！"));
                        return map;
                    }
//                        List<SysOrganization> sysOrganizations = organizationService.selectOrganiztionByName(depar);
//                        if (null != sysOrganizations && sysOrganizations.size() > 0) {
//                            SysOrganization sysOrganization = sysOrganizations.get(0);
//                        } else {
//                             map.put("errorMsg",ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "组织机构名称 :"+depar+" 有误，校验后重新导入"));
//                             return map;
//                        }
                   // }
                    String sex = excelVo.getSex();
                    if(StringUtils.isNotBlank(sex)){
                        if(!"男".equals(sex)&&!"女".equals(sex)){
                            map.put("errorMsg",ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "性别 :"+sex+" 有误，校验后重新导入"));
                            return map;
                        }
                    }

                }
            }
            return map;
        }
            map.put("errorMsg",ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription() ,BusinessExceptionStatusEnum.ParamErr.getCode()));
            return map;
    }

    /**
     * 通讯录导入功能（新1）
     */
    @RequestMapping(value = "/importExcel",method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult importExcel(@RequestBody List<ExcelVO> excelVOList,String selctStatus){//selctStatus 1为不覆盖信息  2为覆盖原有信息
        if(null != excelVOList && excelVOList.size() > 0) {
            String tenantId = TenantContext.getTenantId();
            String userId = TenantContext.getUserInfo().getUserId();
            Map map = excelValidation(excelVOList);
            if(null != map.get("errorMsg")){
                return (ResponseResult) map.get("errorMsg");
            }
            for (ExcelVO excelVo : excelVOList) {
                if (null != excelVo) {
                    String phoneNum = excelVo.getPhoneNum();
                    if(StringUtils.isNotBlank(phoneNum)) {
                        SaasUserWeb saasUserWeb = saasTenantCoreService.selectUserLoginInfo(null, phoneNum);
                        //用户id需要提出去
                        String userrId = CreateUUIdUtil.Uuid();
                        if (null == saasUserWeb) {
                            String userIdd = userrId;
                            SaasUserWeb saasUserWeb1 = new SaasUserWeb();
                            saasUserWeb1.setId(CreateUUIdUtil.Uuid());
                            saasUserWeb1.setUserId(userIdd);
                            saasUserWeb1.setAccount(phoneNum);
                            saasUserWeb1.setTenantId(tenantId);
                            boolean b1 = saasTenantCoreService.saveUserLoginInfo(saasUserWeb1);
                            if (!b1) {
                                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.batch_import.toString(),"批量导入员工失败", BusinessExceptionStatusEnum.ParamErr.getDescription(), Loglevel.error.toString());
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
                            }
                            SaasUserTenantMiddle saasUserTenantMiddle = new SaasUserTenantMiddle();
                            String saasUserTenantMiddleId = CreateUUIdUtil.Uuid();
                            saasUserTenantMiddle.setCreateId(userId);
                            saasUserTenantMiddle.setUserId(userIdd);
                            saasUserTenantMiddle.setTenantId(tenantId);
                            saasUserTenantMiddle.setCreateTime(new Date());
                            saasUserTenantMiddle.setId(saasUserTenantMiddleId);
                            boolean b = saasTenantCoreService.saveSysUserTenantMiddle(saasUserTenantMiddle);
                            if(!b){
                                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.batch_import.toString(),"批量导入员工失败",BusinessExceptionStatusEnum.ParamErr.getDescription(), Loglevel.error.toString());
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
                            }
                        }else {
                            userrId = saasUserWeb.getUserId();
                            String userWebId = userrId;
                            SaasUserTenantMiddle saasUserTenantMiddle = new SaasUserTenantMiddle();
                            String userOldTenantId = saasUserWeb.getTenantId();
                            if(StringUtils.isNotBlank(userOldTenantId) && !userOldTenantId.equals(tenantId)){
                                SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(userOldTenantId);
                                if(null != saasTenant) {
                                    Integer status1 = saasTenant.getStatus();
                                    if (2 == status1) {
                                    saasTenantCoreService.deleteDissolutionUserInfoByUserId(userrId);
                                    saasUserTenantMiddle.setTenantId(tenantId);
                                    saasUserWeb.setTenantId(tenantId);
                                    saasTenantCoreService.updateSaasUserWeb(saasUserWeb);
                                    }else{
                                    TenantContext.setTenantId(userOldTenantId);
                                    SysUser sysUser = userService.selectUserByUserId(userWebId);
                                    if (null != sysUser) {
                                        Integer status = sysUser.getStatus();
                                        if (null != status && status == 89) {
                                            saasUserTenantMiddle.setTenantId(tenantId);
                                            saasUserWeb.setTenantId(tenantId);
                                        } else {
                                            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.batch_import.toString(), "批量导入员工失败", "请" + sysUser.getPhoneNumber() + "与上家公司确认离职关系", Loglevel.error.toString());
                                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请" + sysUser.getPhoneNumber() + "与上家公司确认离职关系");
                                        }
                                    }
                                }
                                }
                                TenantContext.setTenantId("0");
                            }
                            saasUserTenantMiddle.setCreateId(userId);
                            saasUserTenantMiddle.setUserId(userWebId);
                            saasUserTenantMiddle.setCreateTime(new Date());
                            saasUserTenantMiddle.setId(CreateUUIdUtil.Uuid());
                            //添加租户用户关联表
                            boolean b = saasTenantCoreService.saveSysUserTenantMiddle(saasUserTenantMiddle);
                            if (!b) {
                                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.batch_import.toString(),"批量导入员工失败",BusinessExceptionStatusEnum.ParamErr.getDescription(), Loglevel.error.toString());
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
                            }
                            boolean b1 = saasTenantCoreService.updateSaasUserWeb(saasUserWeb);
                            if(!b1){
                                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.batch_import.toString(),"批量导入员工失败",BusinessExceptionStatusEnum.ParamErr.getDescription(), Loglevel.error.toString());
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
                            }
                        }
                        TenantContext.setTenantId(tenantId);
                        //此处需要根据用户手机号去用户表中查询是否存在
                        SysUser sysUser1 = new SysUser();
                        sysUser1.setPhoneNumber(phoneNum);
                        List<SysUser> sysUsers = userService.selectUser(sysUser1);
                        SysUser sysUser = new SysUser();
                        if(null != sysUsers && sysUsers.size() > 0){
                            userrId = sysUsers.get(0).getId();
                        }
                        sysUser.setId(userrId);
                        sysUser.setUserName(excelVo.getName());
                        int sex = 0;//0：男 1：女
                        String userSex = excelVo.getSex();
                        if (StringUtils.isNotBlank(userSex)) {
                            if ("男".equals(userSex)) {
                                sex = 0;
                            } else if ("女".equals(userSex)) {
                                sex = 1;
                            }
                            sysUser.setSex(sex);
                        }
                        sysUser.setUserNumber(excelVo.getUserNum());
                        sysUser.setPosition(excelVo.getPosition());
                        sysUser.setCreateTime(new Date());
                        sysUser.setCreater(userId);
                        sysUser.setEmail(excelVo.getEmail());
                        sysUser.setPhoneNumber(phoneNum);
                        sysUser.setTenantId(tenantId);
                        sysUser.setStatus(2);//1.有效 0.无效 激活后状态给为1 离职状态为89
                        String depar = excelVo.getDepar();
                        if(StringUtils.isBlank(depar)){
                            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.batch_import.toString(),"批量导入员工失败","手机号 ："+phoneNum+" 的部门名称不能为空，修改后重新导入", Loglevel.error.toString());
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "手机号 ："+phoneNum+" 的部门名称不能为空，修改后重新导入！");
                        }
                        String[] deparNames = depar.split("-");
                        if(null == deparNames || deparNames.length <= 0){
                            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.batch_import.toString(),"批量导入员工失败","手机号 ："+phoneNum+" 的部门名称不能为空，修改后重新导入", Loglevel.error.toString());
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "手机号 ："+phoneNum+" 的部门名称不能为空，修改后重新导入！");
                        }
                        List<String> list = Arrays.asList(deparNames);
                        if(null == list || list.size() <= 0){
                            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.batch_import.toString(),"批量导入员工失败","手机号 ："+phoneNum+" 的部门名称不能为空，修改后重新导入", Loglevel.error.toString());
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "手机号 ："+phoneNum+" 的部门名称不能为空，修改后重新导入！");
                        }
                        Map deparIdAndName = getDeparIdAndName(tenantId, list, null);
                        if(deparIdAndName.isEmpty()){
                            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.batch_import.toString(),"批量导入员工失败","手机号 ："+phoneNum+" 的部门名称不能为空，修改后重新导入", Loglevel.error.toString());
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "手机号 ："+phoneNum+" 的部门名称不能为空，修改后重新导入！");
                        }
                                sysUser.setOrgId((String) deparIdAndName.get("orgId"));
                                sysUser.setOrgName((String) deparIdAndName.get("orgName"));
                        List<String> list1 = new ArrayList<>();
                        list1.add("99");
                        if(null == sysUsers || sysUsers.size() == 0) {
                            boolean b = userService.saveUser(sysUser, list1);
                            if (!b) {
                                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.batch_import.toString(),"批量导入员工失败","用户添加失败", Loglevel.error.toString());
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "用户添加失败");
                            }
                        }else{
                            //selctStatus 1为不覆盖信息  2为覆盖原有信息
                            if("2".equals(selctStatus)) {
                                SysUser sysUser2 = sysUsers.get(0);
                                if(null != sysUser2){
                                    Integer status = sysUser2.getStatus();
                                    if(status == 89){
                                        sysUser.setStatus(2);
                                    }
                                }
                                userService.updateUserByUserId(sysUser);
                            }else{
                                SysUser sysUser2 = sysUsers.get(0);
                                if(null != sysUser2){
                                    Integer status = sysUser2.getStatus();
                                    if(status == 89){
                                        sysUser2.setStatus(2);
                                        userService.updateUserByUserId(sysUser2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.batch_import.toString(),"批量导入员工成功！",null, Loglevel.info.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),BusinessExceptionStatusEnum.Success.getCode());
        }
        sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.batch_import.toString(),"批量导入员工失败",BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }
//////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 递归添加部门名称和部门id
     * @param tenantId
     * @param list
     * @return
     */
    public Map getDeparIdAndName(String tenantId,List<String> list,String parentId){
        Map map = new HashMap();
        if(StringUtils.isBlank(parentId)) {
            parentId = "1";
        }
        //循环部门如没有就新建有就就循环(待处理)
        for (int i = 0; i < list.size(); i++) {
            String deparNamee = list.get(i);
            List<SysOrganization> sysOrganizations = organizationService.selectOrganiztionByNameAndParentId(deparNamee,parentId);
            if(null != sysOrganizations && sysOrganizations.size() > 0) {
                SysOrganization sysOrganization = sysOrganizations.get(0);
                List llist = new ArrayList();
                for(int j = 1; j < list.size(); j++){
                    String deparNamee3 = list.get(j);
                    llist.add(deparNamee3);
                }
                if (i < list.size()-1 && i != list.size()-1) {
                    map = getDeparIdAndName(tenantId,llist,sysOrganization.getId());
                    if(map.isEmpty() && map.size() >0){
                        return map;
                    }
                }else {
                    map.put("orgId", sysOrganization.getId());
                    map.put("orgName", sysOrganization.getOrgName());
                }
                return map;
            }else{
                String parentIdId = parentId;
                for (int j=0; j<list.size();j++) {
                    String deparName2 = list.get(j);
                    if(StringUtils.isNotBlank(deparName2)){
                    String orgId = CreateUUIdUtil.Uuid();
                    SysOrganization sysOrg = new SysOrganization();
                    sysOrg.setId(orgId);
                    sysOrg.setParentId(parentIdId);
                    sysOrg.setOrgName(deparName2);
                    sysOrg.setCreateTime(new Date());
                    boolean b = organizationService.saveOrganizationNew(sysOrg);
                    if(b){
                        parentIdId = orgId;
                    }
                    if(j==list.size()-1){
                        map.put("orgId", orgId);
                        map.put("orgName", deparName2);
                    }
                    }
                }
                return map;
            }
        }
        return map;
    }
}
