package com.thinkwin.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.mapper.BizImageRecorderMapper;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.model.core.SaasUserWeb;
import com.thinkwin.common.model.db.BizImageRecorder;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.StringUtil;
import com.thinkwin.console.mapper.SaasRoleMapper;
import com.thinkwin.console.mapper.SaasUserMapper;
import com.thinkwin.console.mapper.SaasUserRoleMapper;
import com.thinkwin.console.service.SaasUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service("saasUserService")
public class SaasUserServiceImpl  implements SaasUserService {
    @Autowired
    SaasUserMapper saasUserMapper;
    @Autowired
    SaasRoleMapper saasRoleMapper;
    @Autowired
    SaasUserRoleMapper saasUserRoleMapper;

    @Override
    public SaasUser selectSaasUser(SaasUser saasUser) {
        List<SaasUser> saasUserList = this.saasUserMapper.select(saasUser);
        if (saasUserList != null && saasUserList.size() >0){
            return saasUserList.get(0);
        }
        return null;
    }

    @Override
    public boolean updateSaasUser(SaasUser saasUser) {
        String id = saasUser.getId();
        if (!StringUtil.isEmpty(id)) {
            int num = saasUserMapper.updateByPrimaryKeySelective(saasUser);
            if (num > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public SaasUser selectSaasUserByUserId(String userId) {
        return saasUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public boolean saveSaasUser(SaasUser saasUser) {
        if(saasUser!=null){
            saasUser.setCreateTime(new Date());
            int i = saasUserMapper.insertSelective(saasUser);
            if(i > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean selectSaasUserByUserName(String userName) {
        if(StringUtils.isNotBlank(userName)) {
            Example ex = new Example(SaasUser.class,true,true);
            ex.or().andLike("phoneNumber",userName);
            ex.or().andLike("userName",userName);
            ex.setOrderByClause("phone_number desc");
//            SaasUser saasUser = new SaasUser();
//            saasUser.setUserName(userName);
            List<SaasUser> userList = saasUserMapper.selectByExample(ex);
            if (null != userList && userList.size() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteSaasUserByUserId(String userId) {
        if(StringUtils.isNotBlank(userId)){
            int status = saasUserMapper.deleteByPrimaryKey(userId);
            if(status > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public PageInfo<SaasUser> selectSaasUserList(String userName, BasePageEntity page) {
        Example ex = new Example(SaasUser.class,true,true);
        if(StringUtils.isNotBlank(userName)) {
            String cre = "'%" + userName + "%'";
            ex.or().andCondition("email like "+cre);
//            ex.or().andCondition("phone_number like "+cre);
            ex.or().andCondition("user_name like "+cre);
        }
        ex.setOrderByClause("create_time desc");
        if(page!=null){
            PageHelper.startPage(page.getCurrentPage(),page.getPageSize());
        }
        List<SaasUser> saasUsers = saasUserMapper.selectByExample(ex);
        return new PageInfo<>(saasUsers);
    }
    @Autowired
    private BizImageRecorderMapper bizImageRecorderMapper;

    /**
     * 存储本地图片信息功能
     * @param userId
     * @return
     */
    public boolean saveImageUrl(String userId, Map map, String imageId){
        if(StringUtils.isNotBlank(userId)&& null != map){
            Iterator entries = map.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String imageType = (String)entry.getKey();
                BizImageRecorder bizImageRecorder = new BizImageRecorder();
                bizImageRecorder.setId(CreateUUIdUtil.Uuid());
                bizImageRecorder.setBizId(userId);
                bizImageRecorder.setCreateTime(new Date());
                bizImageRecorder.setImageId(imageId);
                bizImageRecorder.setImageType(imageType);
                String imageUrl = (String)entry.getValue();
                bizImageRecorder.setImageUrl(imageUrl);
                bizImageRecorderMapper.insertSelective(bizImageRecorder);
            }
            return true;
        }
        return false;
    }



    public Map<String, String> getUploadInfo(String photo) {
        Map map = new HashMap();
        if(StringUtils.isNotBlank(photo)){
            Example example = new Example(BizImageRecorder.class);
            example.createCriteria().andEqualTo("imageId",photo);
            List<BizImageRecorder> bizImageRecorders = bizImageRecorderMapper.selectByExample(example);
            if(null != bizImageRecorders && bizImageRecorders.size() > 0){
                for (BizImageRecorder bizImageRecorder: bizImageRecorders) {
                    if(null != bizImageRecorder){
                        String imageType = bizImageRecorder.getImageType();
                        map.put(imageType,bizImageRecorder.getImageUrl());
                    }
                }
            }
        }
        return map;
    }

    /**
     * 根据图片id删除相关信息
     * @param companyLogo1
     * @return
     */
    public boolean deleteImageUrl(String companyLogo1){
        if(StringUtils.isNotBlank(companyLogo1)){
            Example example = new Example(BizImageRecorder.class);
            example.createCriteria().andEqualTo("imageId",companyLogo1);
            int i = bizImageRecorderMapper.deleteByExample(example);
            if(i >=1){
                return true;
            }
        }
        return false;
    }

}
