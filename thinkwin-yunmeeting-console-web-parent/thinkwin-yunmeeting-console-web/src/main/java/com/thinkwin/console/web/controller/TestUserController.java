package com.thinkwin.console.web.controller;

import com.thinkwin.publish.service.PlatformProgrameLabelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class TestUserController extends BaseController {

    /**
     * 登录跳转中间页
     *
     * @param userId
     * @param model
     * @param token
     * @return
     */
    @RequestMapping("/index.do")
    public String index(String userId, Model model, String token) {
        return "redirect:/gotoIndexPage?token=" + token;
    }

    /**
     * 跳转到index主页面
     *
     * @param userId
     * @param model
     * @param token
     * @return
     */
    @RequestMapping("/gotoIndexPage")
    public String gotoIndexPage(String userId, Model model, String token) {
        return "console/tenant";
    }

    /**
     * 跳转到index主页面
     *
     * @param userId
     * @param model
     * @param token
     * @return
     */
    @RequestMapping("/gotoTestPage")
    public String gotoTestPage(String userId, Model model, String token) {
        return "test";
    }


    /**
     * 跳转到meetingShow页面
     *
     * @return
     */
    @RequestMapping("/gotoMeetingShowPage")
    public String gotoMeetingShowPage() {
        return "console/meetingShow";
    }


    /**
     * 跳转到versionsLog页面
     *
     * @return
     */
    @RequestMapping("/gotoVersionsLogPage")
    public String gotoVersionsLogPage() {
        return "console/versionsLog";
    }
@Resource
    PlatformProgrameLabelService platformProgrameLabelService;

    @RequestMapping("/testPlatformProgrameDate")
    @ResponseBody
    public Object testPlatformProgrameDateByTenantId(String tenantId){
        Map map = platformProgrameLabelService.selectTenantDateByTenantId(tenantId);
        return map;
    }
}
