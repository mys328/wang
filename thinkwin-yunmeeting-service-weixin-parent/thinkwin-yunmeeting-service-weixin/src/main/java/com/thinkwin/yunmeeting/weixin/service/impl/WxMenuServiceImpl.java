package com.thinkwin.yunmeeting.weixin.service.impl;

import com.thinkwin.yunmeeting.weixin.service.WxMenuService;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * 类说明：
 * @author lining 2017/7/5
 * @version 1.0
 *
 */
@Service("wxMenuService")
public class WxMenuServiceImpl implements WxMenuService {

    @Autowired
    private WxMpService wxService;

    /**
     * 创建微信菜单
     * @param wxMenu
     * @return
     */
    @Override
    public String createMenu(WxMenu wxMenu) {
        try{
            String menu=this.wxService.getMenuService().menuCreate(wxMenu);
            return menu;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
