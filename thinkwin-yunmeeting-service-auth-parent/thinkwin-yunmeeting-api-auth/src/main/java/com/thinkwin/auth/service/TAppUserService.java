package com.thinkwin.auth.service;

/**
 * Created by dell on 2017/5/3.
 */
public interface TAppUserService<T> {

    T selectByPrimaryKey(Object key);
}
