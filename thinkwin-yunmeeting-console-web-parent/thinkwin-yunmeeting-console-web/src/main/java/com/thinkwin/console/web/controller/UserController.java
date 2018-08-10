package com.thinkwin.console.web.controller;

import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.console.service.SaasUserService;
import com.thinkwin.service.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户操作
 */
@RestController
public class UserController {

    @Autowired
    private SaasUserService saasUserService;

    /**
     * 获取用户的信息
     *
     * @return
     */
    @RequestMapping("/getSysUserInfoByUserId")
    public ResponseResult getSysUserByUserId() {
        SaasUser saasUser = new SaasUser();
        saasUser.setId(TenantContext.getUserInfo().getUserId());
        SaasUser saasUser1 = this.saasUserService.selectSaasUser(saasUser);

        if (null != saasUser1) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasUser1);
        } else {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), saasUser1, BusinessExceptionStatusEnum.DataNull.getCode());
        }
    }
}
