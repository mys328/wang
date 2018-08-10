package com.thinkwin.web.controller;

import com.alibaba.fastjson.JSON;
import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.*;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.core.SaasTenantInfo;
import com.thinkwin.common.model.core.SaasUserWeb;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.ChineseInital;
import com.thinkwin.common.utils.PingYinUtil;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.SaasTenantInfoVo;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.mailsender.service.YunmeetingSendMailService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.web.security.MySecurityMetadataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: yinchunlei
 * Date: 2017/5/24.
 * Company: thinkwin
 */
@Controller
public class TestUserController extends BaseController {
    @Resource
    private UserService userService;

//    @Resource
//    YunmeetingSendMailService sendMailService;
@Resource
private com.thinkwin.core.service.SaasTenantService saasTenantCoreService;
    @Resource
    private FileUploadService fileUploadService;

    @RequestMapping(value = "testAddUser")
    public String addSysUser(){
        SysUser sysUser = new SysUser();
        sysUser.setUserName("小明");
        sysUser.setSex(1);
        List<SysUser> sysUserList = userService.selectUser(sysUser);
        return "redirect:testUpdateUser";
    }
    @RequestMapping(value = "testUpdateUser")
    public String updateSysUser(){
        return "index";
    }
    @Resource
    private MenuService menuService;
    @Resource
    private LoginRegisterService loginRegisterService;
    @Resource
    private SaasTenantInfoService saasTenantInfoService;

    /**
     * 登录跳转中间页
     * @param userId
     * @param model
     * @param token
     * @return
     */
    @RequestMapping("/index.do")
    public String index(String userId,Model model,String token){
        return "redirect:/gotoIndexPage?token="+token;
    }

    /**
     * 跳转到index主页面
     * @param userId
     * @param model
     * @param token
     * @return
     */
    @RequestMapping("/gotoIndexPage")
    public String gotoIndexPage(String userId,Model model,String token){
        return "index";
    }

    /**
     * 跳转到企业设置页面
     * @return
     */
    //@RequestMapping(value = "/gotoEnterpriseSettingsPage")
    @RequestMapping(value = "/gotoEnterpriseSettingPage")
    public String addSysUserRole(HttpServletRequest request){
        ///////////////////////////添加资源拦截功能/////////////////////////////////
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null != userInfo) {
            String userId = userInfo.getUserId();
            if(StringUtils.isNotBlank(userId)) {
                //在此处需要获取用户请求的路径
                String url = request.getRequestURI();
                boolean userJurisdiction = permissionService.getUserJurisdiction(userId,url);
                if(userJurisdiction){
                    return "enterprise_settings/admin";
                }
            }
        }
        return "redirect:/logout";
        ////////////////////////////添加资源拦截功能///////////////////////////
            //return "enterprise_settings/admin";
    }

    /**
     * 跳转到会议预定页面
     * @return
     */
    @RequestMapping(value = "/gotomeetingreservepage")
    public String gotoMeetingReservePage(){
        return "meeting_res/reserve";
    }

    /**
     * 跳转到立即续费界面
     * @return
     */
    @RequestMapping(value = "/gotopromptlyrenewpage")
    public String gotoPromptlyrenewPage(){
        return "meeting_res/reserve";
    }

    /**
     * 跳转到企业设置页面
     * @return
     */
    //@RequestMapping(value = "/gotoEnterpriseSettingsPage")
    public String gotoEnterpriseSettingPage(HttpServletRequest request){
        ///////////////////////////添加资源拦截功能/////////////////////////////////
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null != userInfo) {
            String userId = userInfo.getUserId();
            if(StringUtils.isNotBlank(userId)) {
                //在此处需要获取用户请求的路径
                String url = request.getRequestURI();
                boolean userJurisdiction = permissionService.getUserJurisdiction(userId,url);
                if(userJurisdiction){
                    return "enterprise_settings/admin";
                }
            }
        }
        return "redirect:/logout";
        ////////////////////////////添加资源拦截功能///////////////////////////
        //return "enterprise_settings/admin";
    }

    /**
     * 跳转到主top页
     * @return
     */
    @RequestMapping("/gotoMainTopPage")
    public String gotoMainTopPage(){
        return "common/mainTop";
    }

    /**
     * 跳转到通讯录页
     * @return
     */
    @RequestMapping("/gotoAddressListPage")
    public String gotoAddressListPage(HttpServletRequest request){
        ///////////////////////////添加资源拦截功能/////////////////////////////////
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null != userInfo) {
            String userId = userInfo.getUserId();
            if(StringUtils.isNotBlank(userId)) {
                //在此处需要获取用户请求的路径
                String url = request.getRequestURI();
                boolean userJurisdiction = permissionService.getUserJurisdiction(userId,url);
                if(userJurisdiction){
                    return "address_list/directories";
                }
            }
        }
        return "redirect:/logout";
        ////////////////////////////添加资源拦截功能///////////////////////////
       // return "address_list/directories";
    }


    ////////////////////////////////////////测试///////////////////////////////////////////////
    //菜单树形结构
/*    public JSONArray treeMenuList(JSONArray menuList, int parentId) {
        JSONArray childMenu = new JSONArray();
        for (Object object : menuList) {
            JSONObject jsonMenu = JSONObject.fromObject(object);
            int menuId = jsonMenu.getInt("id");
            int pid = jsonMenu.getInt("parentId");
            if (parentId == pid) {
                JSONArray c_node = treeMenuList(menuList, menuId);
                jsonMenu.put("childNode", c_node);
                childMenu.add(jsonMenu);
            }
        }
        return childMenu;
    }*/

public static void main(String args[]) {
/*    JSONArray jsonArray = new JSONArray();
        SysOrganization menu1 = new SysOrganization();
        menu1.setId("11");
        menu1.setParentId("0");
        SysOrganization menu2 = new SysOrganization();
        menu2.setId("21");
        menu2.setParentId("0");
        SysOrganization menu3 = new SysOrganization();
        menu3.setId("31");
        menu3.setParentId("11");
        SysOrganization menu4 = new SysOrganization();
        menu4.setId("41");
        menu4.setParentId("11");
        SysOrganization menu5 = new SysOrganization();
        menu5.setId("51");
        menu5.setParentId("21");
        SysOrganization menu6 = new SysOrganization();
        menu6.setId("61");
        menu6.setParentId("21");

        jsonArray.add(menu1);
        jsonArray.add(menu2);
        jsonArray.add(menu3);
        jsonArray.add(menu4);
        jsonArray.add(menu5);
        jsonArray.add(menu6);
        JSONArray jsonArray1 = treeMenuList(jsonArray, "0");

        System.out.print(jsonArray1);*/
//List<String> list1 = new ArrayList();
//list1.add("1");
//    list1.add("2");
//    list1.add("3");
//    list1.add("4");
//List<String> list2 = new ArrayList();
//list2.add("3");
//    list2.add("4");
//    list2.add("5");
//    list2.add("6");
//    List<String> result = new ArrayList();
//    for(int i=0;i<list1.size();i++){
//        String tempA = list1.get(i);
//        for(int j=0;j<list2.size();j++){
//            String tempB = list2.get(j);
//            if(tempA == tempB){
//                result.add(tempB);
//            }
//        }
//    }
//    Set<String> oldList = new HashSet<>();
//    Set<String> newList = new HashSet<>();
//    if(null != result){
//        for (String newNewData:result) {
//            list1.remove(newNewData);
//            list2.remove(newNewData);
//        }
//    }
//    System.out.println("result "+result);
//    System.out.println("oldList "+list1);
//    System.out.println("newList "+list2);
//    String i = "2";
//    String j = "3";
//    int result = j.compareTo(i);
//    System.out.println("rerererer :"+result);
//    }
//    public static  JSONArray treeMenuList(JSONArray menuList, String parentId){
//        JSONArray childMenu = new JSONArray();
//        for (Object object : menuList) {
//            JSONObject jsonMenu = JSONObject.fromObject(object);
//            String menuId = jsonMenu.getString("id");
//            String pid = jsonMenu.getString("parentId");
//            if (parentId.equals(pid) || parentId == pid) {
//                JSONArray c_node = treeMenuList(menuList, menuId);
//                jsonMenu.put("childNode", c_node);
//                childMenu.add(jsonMenu);
//            }
//        }
//        return childMenu;
//    List<String> keys = RedisUtil.keys("yunmeeting_qiyejiesan_statu*");
//   // RedisUtil.
//    if(null != keys){
//        for (String key :keys) {
//            System.out.println("key :"+key);
//        }
//    }
//    RedisUtil.remove("111");
    //fileUploadService.delDBDate();
//    List list = new ArrayList();
//    list.add(1);
//    list.add(2);
//    list.add(3);
//    list.add(4);
//    System.out.println((int)Collections.max(list)+1);
//    String   s   =   "2.0 ";  //要确保字符串为一个数值，否则会出异常
//    double   d   =   Double.parseDouble(s);
//    String s1 = d +1+ "";
//    System.out.println(d);
//    System.out.println(s1);
//    String allFirstLetter = ChineseInital.getAllFirstLetter("蓝灰风格节目名称过长蓝灰风格节目名称过长蓝灰风格节目名称过长");
//    String allFirstLetter1 = PingYinUtil.getFirstSpell("蓝灰风格节目名称过长蓝灰风格节目名称过长蓝灰风格节目名称过长");
//    System.out.println("cccccc :"+allFirstLetter+"bbbbbbbbbb"+allFirstLetter1);
//    public static String addDateMinut(String day, int x)//返回的是字符串型的时间，输入的
////是String day, int x
//    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
//引号里面个格式也可以是 HH:mm:ss或者HH:mm等等，很随意的，不过在主函数调用时，要和输入的变
//量day格式一致
        Date date = null;
        try {
            date = format.parse("2018-06-26 14:16:36");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //if (date == null)
            //return "";
        System.out.println("front:" + format.format(date)); //显示输入的日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, 1);// 24小时制
        date = cal.getTime();
        System.out.println("after:" + format.format(date));  //显示更新后的日期
        cal = null;
       // return format.format(date);

 //   }
}
    ///////////////////////////////////////////////////////////////////////////////////////

    /**
     * 管理员控制台后跳转的index页面controller
     * @return
     */
    @RequestMapping("/gotoConsolePage")
    public String gotoConsolePage(HttpServletRequest request){
        ///////////////////////////添加资源拦截功能/////////////////////////////////
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null != userInfo) {
            String userId = userInfo.getUserId();
            if(StringUtils.isNotBlank(userId)) {
                //在此处需要获取用户请求的路径
                String url = request.getRequestURI();
                boolean userJurisdiction = permissionService.getUserJurisdiction(userId,url);
                if(userJurisdiction){
                    return "firstpage/consoleIndex";
                }
            }
        }
        return "redirect:/logout";
        ////////////////////////////添加资源拦截功能///////////////////////////
      //  return "firstpage/consoleIndex";
    }

    @Resource
    private UserRoleService userRoleService;
    @Resource
    private PermissionService permissionService;
    /**
     * 判断某个用户是否具有访问某个路径的权限功能
     * @param userId
     * @param requestUrl
     * @return
     */
//    public boolean getUserJurisdiction(String userId,String requestUrl){
//        if(StringUtils.isNotBlank(userId)) {
//            String userHighestRoleId = null;
//            List<SysUserRole> SysUserRoles = userService.getCurrentUserRoleIds(userId);
//            if(null != SysUserRoles && SysUserRoles.size() > 0){
//                List list = new ArrayList();
//                for (SysUserRole sysUserRole:SysUserRoles) {
//                    if(null != sysUserRole){
//                        list.add(sysUserRole.getRoleId());
//                    }
//                }
//                userHighestRoleId = Collections.min(list).toString();
//            }
//            if(StringUtils.isNotBlank(userHighestRoleId)){
//                //根据角色id获取资源列表功能
//               List<String> permissionUrls = permissionService.selectPermissionUrlsByRoleId(userHighestRoleId);
//               if(null != permissionUrls && permissionUrls.size() > 0){
//                   for (String permissionUrl:permissionUrls) {
//                       if(requestUrl.equals(permissionUrl)){
//                           return true;
//                       }
//                   }
//               }
//            }
//        }
//        return false;
//    }


    /**
     * 管理员去往控制台登录页面功能
     * @return
     */
    @RequestMapping("/gotoConsoleLoginPage")
    public String gotoConsoleLoginPage(Model model){
        String tenantId = TenantContext.getTenantId();
        if(StringUtils.isNotBlank(tenantId)) {
            SaasTenantInfo saasTenantInfo = new SaasTenantInfo();
            String s1 = RedisUtil.get(tenantId+"_SaasTenantInfo");
            SaasTenantInfoVo saasTenantInfoVo = new SaasTenantInfoVo();
            if (StringUtils.isNotBlank(s1)) {
                saasTenantInfoVo = JSON.parseObject(s1, SaasTenantInfoVo.class);
            } else {
                    saasTenantInfo = saasTenantCoreService.selectSaasTenantInfoByTenantId(tenantId);
                    if(null != saasTenantInfo) {
                        BeanUtils.copyProperties(saasTenantInfo,saasTenantInfoVo);
                        String companyLogo = saasTenantInfo.getCompanyLogo();
                        if (StringUtils.isNotBlank(companyLogo)) {
                            /*Map<String, String> photos = fileUploadService.selectFileCommon(companyLogo);*/
                            Map<String, String> photos = userService.getUploadInfo(companyLogo);
                            if(null != photos) {
                                saasTenantInfoVo.setCompanyLogo(photos.get("primary"));
                                saasTenantInfoVo.setBigPicture(photos.get("big"));
                                saasTenantInfoVo.setInPicture(photos.get("in"));
                                saasTenantInfoVo.setSmallPicture(photos.get("small"));
                            }
                        }
                        String s = JSON.toJSONString(saasTenantInfoVo);
                        //把字符串存redis里面
                        RedisUtil.set(tenantId+"_SaasTenantInfo", s);
                        RedisUtil.expire( tenantId+"_SaasTenantInfo", 1200);
                    }
            }
            model.addAttribute("companyLogo",saasTenantInfoVo.getInPicture());
            model.addAttribute("companyName",saasTenantInfoVo.getTenantName());
        }
        return "login-register/console-login";
    }

    /**
     * 测试处理security缓存数据
     */
    @Resource
    private MySecurityMetadataSource mySecurityMetadataSource;
    @RequestMapping("/testSecurityMetadataSource")
    public void testLoadResourcesDefine(){
        mySecurityMetadataSource.loadResourcesDefine();
    }

    @RequestMapping("/testGetAllOrganization")
    @ResponseBody
    public ResponseResult testGetAllOrganization(){
        List list = userService.selectAllOrganizationAndOrderBy(null);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),list);
    }

    /**
     * 跳转到离职状态页面
     * @return
     */
    @RequestMapping("/system/gotoQuitPage")
    public String gotoQuitPage(){
        return "quitPage";
    }

    /**
     * 跳转到用户被禁用状态页面
     * @return
     */
    @RequestMapping("/system/gotoDisablePage")
    public String gotoDisablePage(){
        return "disablePage";
    }

    /**
     * 跳转到权限不足页面
     * @return
     */
    @RequestMapping("/system/gotoAccessPage")
    public String gotoAccessPage(){
        return "access";
    }

    /**
     * 跳转到错误页面
     * @return
     */
    @RequestMapping("/system/gotoErrorPage")
    public String gotoErrorPage(){
        return "error";
    }

//    @RequestMapping("/system/testDelDBDate")
//    @ResponseBody
//    public String delDBDateTest(){
//        fileUploadService.delDBDate();
//        return "1";
//    }
}
