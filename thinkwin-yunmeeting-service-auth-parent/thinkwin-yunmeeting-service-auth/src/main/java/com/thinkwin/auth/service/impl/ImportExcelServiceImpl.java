package com.thinkwin.auth.service.impl;

import com.thinkwin.auth.service.ImportExcelService;
import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.ExcelVO;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * User: yinchunlei
 * Date: 2017/8/2.
 * Company: thinkwin
 */
@Service("importExcelService")
public class ImportExcelServiceImpl implements ImportExcelService {
    @Autowired
    private UserService userService;
    @Autowired
    private LoginRegisterService loginRegisterService;

    /**
     * 导入人员列表功能
     * @param excelVOList
     * @return
     */
    public ResponseResult importExcelBean(String userId,List<ExcelVO> excelVOList){
    return null;
    }

    /**
     * 模拟回滚功能
     * @param userIdList
     * @param oldUserIdList
     * @return
     */
    public Integer delectOldData(List<String> userIdList,List<String> oldUserIdList){
        int flag = 0;

        return 0;
    }
}
