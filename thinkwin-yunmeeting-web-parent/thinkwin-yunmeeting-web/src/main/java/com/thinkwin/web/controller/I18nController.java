package com.thinkwin.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 测试I18n配置读取
 */
@Controller
public class I18nController {

    @RequestMapping(value = "/hello")
    public ModelAndView welcome() {
        ModelAndView modelAndView = new ModelAndView("welcome");

        return modelAndView;
    }

}
