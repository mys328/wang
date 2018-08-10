package com.thinkwin.yunmeeting.weixin.service;

import me.chanjar.weixin.common.bean.menu.WxMenu;

public interface WxMenuService {

    /**
     * 创建微信菜单
     * @param wxMenu
     * @return
     */
    public String createMenu(WxMenu wxMenu);
}
