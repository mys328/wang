package com.thinkwin.yunmeeting.weixin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thinkwin.common.model.core.ShortUrl;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.core.service.ShortUrlService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * User:wangxilei
 * Date:2018/7/22
 * Company:thinkwin
 */
@Controller
public class ShortUrlController {
    @Resource
    private ShortUrlService shortUrlService;

    @RequestMapping(value = "/s/{shortUrl}", method = RequestMethod.GET)
    @ResponseBody
    public void shortUrlHandle(HttpServletRequest req, HttpServletResponse res, @PathVariable String shortUrl) throws IOException {
//        String queryString = req.getQueryString();
//        String basePath = req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+"/";
        String timestamp = req.getParameter("t");
        String tenantId="";
        String meetingId="";
        String timeStep="";
        if(StringUtils.isNotBlank(shortUrl)){
            String url = RedisUtil.get(shortUrl);
            if(StringUtils.isBlank(url)){
                ShortUrl su = new ShortUrl();
                su.setShortUrl(shortUrl);
                List<ShortUrl> shortUrls = shortUrlService.get(su);
                if(shortUrls!=null&&shortUrls.size()>0){
                    ShortUrl s = shortUrls.get(0);
                    url = s.getRealUrl();
                }
            }
            JSONObject jsonObject = JSON.parseObject(url);
            tenantId = jsonObject.getString("tenantId");
            meetingId = jsonObject.getString("meetingId");
            timeStep = jsonObject.getString("timeStep");
        }
        String redirectUrl = "/wechat/scanMeetingSign/scanMeetingSignQR?tenantId="+
                tenantId+"&meetingId="+meetingId+"&timeStep="+timeStep+"&createTime="+timestamp;
        res.sendRedirect(redirectUrl);
    }
}
