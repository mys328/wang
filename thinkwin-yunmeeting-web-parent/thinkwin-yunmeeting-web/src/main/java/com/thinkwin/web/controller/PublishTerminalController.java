package com.thinkwin.web.controller;

import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.publish.TerminalDto;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.pay.dto.PaymentVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/publish")
@Controller
public class PublishTerminalController {

	@RequestMapping(value = "/terminal")
	public String goTerminal() {
		return "publish_terminal/terminal";
	}

}
