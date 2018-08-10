package com.thinkwin.core.service.impl;

import com.thinkwin.common.model.core.ShortUrl;
import com.thinkwin.core.mapper.ShortUrlMapper;
import com.thinkwin.core.service.ShortUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * User:wangxilei
 * Date:2018/7/22
 * Company:thinkwin
 */
@Service("shortUrlService")
public class ShortUrlServiceImpl implements ShortUrlService {
    @Autowired
    private ShortUrlMapper shortUrlMapper;

    @Override
    public boolean save(ShortUrl shortUrl) {
        if(shortUrl!=null) {
            shortUrl.setCreateTime(new Date());
            int i = shortUrlMapper.insertSelective(shortUrl);
            if (i > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ShortUrl> get(ShortUrl shortUrl) {
        if(shortUrl!=null){
            return shortUrlMapper.select(shortUrl);
        }
        return null;
    }
}
