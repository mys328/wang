package com.thinkwin.yunmeeting.weixin.controller;

import com.thinkwin.common.utils.PropertyUtil;
import com.thinkwin.yunmeeting.weixin.service.WxMenuService;
import com.thinkwin.yunmeeting.weixin.service.WxOAuth2Service;
import com.thinkwin.yunmeeting.weixin.service.WxQrcodeService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author LiNing
 * 微信菜单
 */
@RestController
@RequestMapping("/wechat/wxMenu")
public class WxMenuController {

    private final Logger log = LoggerFactory.getLogger(WxMenuController.class);

    private final String m41="https://mp.weixin.qq.com/s?__biz=MzU2MjA2NzUxOA==&mid=2247483659&idx=1&sn=cc6862134f1062c81d3e28688652ae48&chksm=fc6e605acb19e94cc84f5e54548b8767f59931253a10c4f12fceb6a41ff718292b0b1e22fee8#rd";


    @Resource
    private WxMenuService wxMenuService;

    @Resource
    private WxOAuth2Service wxOAuth2Service;

    @Resource
    private WxQrcodeService wxQrcodeService;





    /**
     * 生成微信公众号菜单
     * @return
     */
    @RequestMapping("/createMenu")
    @ResponseBody
    public String  createMenu() {

        WxMenu wxMenu=this.getWxMenu();

        this.wxMenuService.createMenu(wxMenu);
        return "SUCCESS";
    }

    public WxMenu getWxMenu(){
        String site= PropertyUtil.getWechatServer();
        WxMenu wxMenu=new WxMenu();


        //*****************************************
        /*WxMenuButton button1 = new WxMenuButton();
        button1.setName("扫一扫");
        button1.setType(WxConsts.BUTTON_SCANCODE_PUSH);
        button1.setKey("M1_SCAN");*/

        //*****************************************
        WxMenuButton button2 = new WxMenuButton();
        button2.setType(WxConsts.MenuButtonType.VIEW);
        button2.setName("预订会议");
        //button2.setKey("M2_RESERVE");
        button2.setUrl(site+"/wechat/wxMeeting/qyh?type=1");
        //button2.setUrl(site+"/wechat/wxMeeting/yunmeeting");

        //*****************************************
        WxMenuButton button3 = new WxMenuButton();
        button3.setType(WxConsts.MenuButtonType.VIEW);
        button3.setName("查询会议");
        //button3.setKey("M3_QUERY");
        button3.setUrl(site+"/wechat/wxMeeting/qyh?type=2");

        //*****************************************
        WxMenuButton button4 = new WxMenuButton();
        button4.setName("更多服务");
        WxMenuButton button41 = new WxMenuButton();
        button41.setType(WxConsts.MenuButtonType.VIEW);
        button41.setName("常见问题");
        button41.setUrl(m41);

        button4.getSubButtons().add(button41);


        //*****************************************
        //wxMenu.getButtons().add(button1);
        wxMenu.getButtons().add(button2);
        wxMenu.getButtons().add(button3);
        wxMenu.getButtons().add(button4);

        return wxMenu;
    }




}
