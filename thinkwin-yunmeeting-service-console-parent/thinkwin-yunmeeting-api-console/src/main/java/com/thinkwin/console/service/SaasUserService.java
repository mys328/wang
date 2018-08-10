package com.thinkwin.console.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasUser;

import java.util.List;
import java.util.Map;

/**
 * 人员接口
 */
public interface SaasUserService {
        /*
        * 查询用户
        * */
        SaasUser selectSaasUser(SaasUser saasUser);

        /*
        * 更新用户
        * */
        boolean updateSaasUser(SaasUser saasUser);

        /**
         * 根据用户的主键ID获取用户的信息功能
         * @param userId
         * @return
         */
        SaasUser selectSaasUserByUserId(String userId);

        /**
         * 添加新用户功能
         * @param saasUser 用户实体
         * @return
         */
        boolean saveSaasUser(SaasUser saasUser);

        /**
         * 根据用户名或手机号判断用户是否存在
         * @param userName 用户名/手机号
         * @return
         */
        boolean selectSaasUserByUserName(String userName);

        /**
         * 根据用户的主键ID删除用户功能
         * @param userId 用户Id
         * @return
         */
        boolean deleteSaasUserByUserId(String userId);

        /**
         * 根据条件查询用户列表功能
         * @param userName 条件文本框
         * @return
         */
        PageInfo<SaasUser> selectSaasUserList(String userName, BasePageEntity page);

        /**
         * 存储本地图片信息功能
         * @param userId
         * @return
         */
        public boolean saveImageUrl(String userId, Map map, String imageId);

        public Map<String, String> getUploadInfo(String photo);

        /**
         * 根据图片id删除相关信息
         * @param companyLogo1
         * @return
         */
        public boolean deleteImageUrl(String companyLogo1);

}
