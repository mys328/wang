package com.thinkwin.console.web.security;

import com.thinkwin.common.model.console.SaasPermission;
import com.thinkwin.common.model.console.SaasRole;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.console.service.SaasRolePermissionService;
import com.thinkwin.console.service.SaasRoleService;
import com.thinkwin.console.service.SaasUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 第六步  验证成功页面，
 * 根据url跳转
 */
public class LoginAuthenticationSuccesssHandler implements AuthenticationSuccessHandler {

    private String defaultUrl;
    private SaasUserService saasUserService;
    private SaasRoleService saasRoleService;
    private SaasRolePermissionService saasRolePermissionService;

    public void setSaasRolePermissionService(SaasRolePermissionService saasRolePermissionService) {
        this.saasRolePermissionService = saasRolePermissionService;
    }

    public void setSaasUserService(SaasUserService saasUserService) {
        this.saasUserService = saasUserService;
    }

    public void setSaasRoleService(SaasRoleService saasRoleService) {
        this.saasRoleService = saasRoleService;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String phoneNumber = request.getParameter("j_username");
        System.out.println("phoneNumber :" + phoneNumber);
        Map map = new HashMap<>();

        //查询用户
        SaasUser saasUser = new SaasUser();
//        saasUser.setPhoneNumber(phoneNumber);
        saasUser.setEmail(phoneNumber);
        SaasUser user = saasUserService.selectSaasUser(saasUser);

        String msg = "查询成功！";
        List<SaasRole> saasRoleList = saasRoleService.findUserRolesByUserId(user.getId());
        if (null != saasRoleList && saasRoleList.size() > 0) {
//            SaasRole saasRole = saasRoleList.get(0);
            map.put("roleId", saasRoleList.get(0).getRoleId());
            boolean state = false;
            for (SaasRole sr:saasRoleList) {
                String roleId = sr.getRoleId();
                if(StringUtils.isNotBlank(roleId)){
                    SaasRole saasRole = saasRoleService.selectSaasRoleById(roleId);
                    String orgCode = saasRole.getOrgCode();
                    if(orgCode!=null&&!orgCode.equals("0")){ //0为超管
                        List<SaasPermission> saasPermissions = saasRolePermissionService.selectSaasPermissionsByRoleId(roleId);
                        if(saasPermissions!=null && saasPermissions.size()>0){
                            state = true;
                        }
                    }else{
                        state = true;
                    }
                }
            }
            if(!state){
                msg = "该用户角色尚未赋予权限！";
            }
        }else{
            msg = "该用户尚未赋予角色！";
        }

        map.put("Id", user.getId());
        map.put("time", user.getModifyTime());

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();


        String userId = (String) map.get("Id");
        String tokenn = CreateUUIdUtil.Uuid();
        String roleId = (String) map.get("roleId");
        RedisUtil.set("platform_console_web_token_" + tokenn, tokenn);
        RedisUtil.expire("platform_console_web_token_" + tokenn, 1200);

        //获取当前的sessionid值
        String jsessionId = request.getSession().getId();

        out.println("{\"code\":\"1\",\"msg\":\"" + msg + "\",\"url\":\"/index.do\",\"jsessionid\":\"" + jsessionId + "\",\"userId\":\"" + userId + "\",\"token\":\"" + tokenn + "\",\"roleId\":\"" + roleId + "\"}");
    }

    /**
     * @param defaultUrl the defaultUrl to set
     */
    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }
}
