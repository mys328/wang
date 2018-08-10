package com.thinkwin.web.controller;

import com.thinkwin.mailsender.service.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping(value = "/mailtest")
public class MailTestController {

	private ItemsService itemsService;

	@Autowired
	public void setItemsService(ItemsService itemsService) {
		this.itemsService = itemsService;
	}

	@RequestMapping(value = "/viewmail", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView viewMail() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("mail_sender/view_mail");
		return modelAndView;
	}



	@RequestMapping(value = "/sendermail", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView sendMail() throws Exception {
		//加载dubbo service 方法
		itemsService.sendMail();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("mail_sender/mail_back");
		return modelAndView;
	}

}
