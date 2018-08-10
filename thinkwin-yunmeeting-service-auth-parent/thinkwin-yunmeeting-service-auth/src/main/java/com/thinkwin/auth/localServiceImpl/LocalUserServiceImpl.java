package com.thinkwin.auth.localServiceImpl;

import com.thinkwin.auth.localService.LocalUserService;
import com.thinkwin.auth.mapper.db.SysRoleMapper;
import com.thinkwin.auth.mapper.db.SysUserMapper;
import com.thinkwin.auth.mapper.db.SysUserRoleMapper;
import com.thinkwin.common.utils.StringUtil;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yunmeeting.framework.datasource.dynamicdatasource.DBContextHolder;
import org.apache.commons.lang.StringUtils;
import com.thinkwin.common.model.db.SysRole;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.model.db.SysUserRole;
import com.thinkwin.common.utils.CreateUUIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: yinchunlei
 * Date: 2017/5/24.
 * Company: thinkwin
 */
@Service("localUserService")
public class LocalUserServiceImpl implements LocalUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    //添加新用户功能接口
    //String typee 如果typee为“1”时说明是注册新的租户（此时需要切换数据源） typee为“2”时说明是注册租户下的新用户
    public boolean saveUser(SysUser sysUser,List<String> roleIds,String typee){
        int num = 0;
        int insert =0;
        if(StringUtils.isNotBlank(typee) && "1".equals(typee)){
            DBContextHolder.setDBType(sysUser.getTenantId());
        }
        try {
            //增加微信名称转换
            String nickName = sysUser.getWechat();
            if(StringUtils.isNotBlank(nickName)) {
                try {
                    sysUser.setWechat(StringUtil.bytes2Hex(nickName));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            insert = sysUserMapper.insertSelective(sysUser);
        }catch (Exception e){
            throw e;
        }
        if(insert <= 0){
            num = 1;
        }
        String userId = sysUser.getId();
        if(null != roleIds && roleIds.size() > 0){
            boolean status = this.saveUserRole(userId,roleIds);
            if(status){
                num = 0;
            }else {
                num = 1;
            }
        }else {
            //num = this.userRoleAddStatus(userId,"1");
            List roleIdss = new ArrayList();
            roleIdss.add("1");
            boolean status = this.saveUserRole(userId,roleIdss);
            if(status){
                num = 0;
            }else {
                num = 1;
            }
        }
        if(num == 0){
            return true;
        }else {
            return false;
        }
    }
    //用户角色的添加功能
    public boolean userRoleNumber(String userId,String roleId){
        String userId1 = userId;
        if(TenantContext.getUserInfo() != null){
            userId1 = TenantContext.getUserInfo().getUserId();
        }
        boolean number = true;
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setRoleId(roleId);
        sysUserRole.setUserId(userId);
        List<SysUserRole> sysUserRoleList = sysUserRoleMapper.select(sysUserRole);
        if(sysUserRoleList.size()<=0){
            sysUserRole.setId(CreateUUIdUtil.Uuid());
            sysUserRole.setCreaterId(userId1);
            sysUserRole.setCreateTime(new Date());
            int num = sysUserRoleMapper.insertSelective(sysUserRole);
            if(num <= 0){
                return false;
            }
        }else {
            int numberr = sysUserRoleList.size();
            if(numberr <= 0){
                number = false;
            }
        }
        return number;

    }

    //根据用户名或手机号判断用户是否存在
    public boolean selectUserName(String userName){
        SysUser sysUser = new SysUser();
        sysUser.setUserName(userName);
        List<SysUser> userList = sysUserMapper.select(sysUser);
        if(null != userList && userList.size() > 0){
            return true;
        }
        return false;
    }
    //根据用户主键ID删除用户功能
    public boolean deleteUserByUserId(String userId){
        int status = sysUserMapper.deleteByPrimaryKey(userId);
        if(status > 0){
            return true;
        }
        return false;
    }

    //根据用户的主键ID修改用户信息
    @Override
    public boolean updateUserByUserId(SysUser sysUser) {
        sysUser.setModifyTime(new Date());
        //增加微信名称转换
        String nickName = sysUser.getWechat();
        if(StringUtils.isNotBlank(nickName)) {
            try {
                sysUser.setWechat(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        int i = sysUserMapper.updateByPrimaryKeySelective(sysUser);
        if(i > 0){
            return true;
        }
        return false;
    }

    //根据条件查询用户列表功能
    public List<SysUser> selectUser(SysUser sysUser){
        List<SysUser> select = sysUserMapper.select(sysUser);
        for(SysUser sysUser1:select){
            String wechat = sysUser1.getWechat();
            if (StringUtils.isNotBlank(wechat)&&StringUtil.isHexNumber(wechat)) {
                try {
                    wechat = StringUtil.parseHexString(wechat);
                    sysUser1.setWechat(wechat);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return  select;
    }

    //用户角色的添加功能
    public boolean saveUserRole(String userId,List<String> roleIds){
        if(null != userId && null != roleIds && roleIds.size() > 0) {
            int number = 0;
            for (String roleId :roleIds) {
                boolean status = this.userRoleNumber(userId,roleId);
                if(status){
                    number = 1;
                }else {
                    number = 0;
                }
            }
            if(number > 0){
                return true;
            }
        }
        return false;
    }
//    //获取所有角色列表
//    public List<SysRole> findAllRoles(){
//        return sysRoleMapper.selectAll();
//    }
    /**
     * 获取所有角色列表
     * @return
     */
    public List<SysRole> findAllRoles(){
        String dbType = DBContextHolder.getDBType();
        if(StringUtils.isBlank(dbType)||"0".equals(dbType)){
            DBContextHolder.setDBType("plantform_init_yunmeeting_db");
        }

        List<SysRole> sysRoles = sysRoleMapper.selectAll();
        return sysRoles;
    }

    /**
     * 根据用户的主键id获取用户拥有的角色id
     * @param userId
     * @return
     */
    public List<SysUserRole> selectUserRole(String userId){
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(userId);
        List<SysUserRole> select = sysUserRoleMapper.select(sysUserRole);
        return select;
    }

    /**
     * 获取用户总数量
     * @return
     */
    public Integer selectUserTotalNum(Example example){
        if(null != example){
            return sysUserMapper.selectCountByExample(example);
        }
        return 0;
    }

    /**
     * 根据用户的主键id获取
     * @param userId
     * @return
     */
    public List<SysUserRole> getCurrentUserRoleIds(String userId){
        Example example = new Example(SysUserRole.class);
        example.createCriteria().andEqualTo("userId",userId);
        return sysUserRoleMapper.selectByExample(example);
    }

    /**
     * 根据用户的主键id获取用户信息
     * @return
     */
    public SysUser getUserInfoByUserId(String userId){
        return sysUserMapper.selectByPrimaryKey(userId);
    }
}
