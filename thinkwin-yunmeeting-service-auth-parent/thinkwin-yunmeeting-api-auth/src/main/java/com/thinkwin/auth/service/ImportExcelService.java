package com.thinkwin.auth.service;

import com.thinkwin.common.model.ExcelVO;
import com.thinkwin.common.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * User: yinchunlei
 * Date: 2017/8/2.
 * Company: thinkwin
 */
public interface ImportExcelService {

    /**
     * 导入人员列表功能
     * @param excelVOList
     * @return
     */
    public ResponseResult importExcelBean(String userId,List<ExcelVO> excelVOList);

    /**
     * 模拟回滚功能
     * @param userIdList
     * @param oldUserIdList
     * @return
     */
    public Integer delectOldData(List<String> userIdList,List<String> oldUserIdList);
}
