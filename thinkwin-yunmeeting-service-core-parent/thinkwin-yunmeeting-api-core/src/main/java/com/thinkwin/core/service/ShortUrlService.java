package com.thinkwin.core.service;

import com.thinkwin.common.model.core.ShortUrl;

import java.util.List;

/**
 * User:wangxilei
 * Date:2018/7/22
 * Company:thinkwin
 */
public interface ShortUrlService {
    /**
     * 添加短地址映射
     * @param shortUrl
     * @return
     */
    boolean save(ShortUrl shortUrl);

    /**
     * 获取短地址映射
     * @param shortUrl
     * @return
     */
    List<ShortUrl> get(ShortUrl shortUrl);
}
