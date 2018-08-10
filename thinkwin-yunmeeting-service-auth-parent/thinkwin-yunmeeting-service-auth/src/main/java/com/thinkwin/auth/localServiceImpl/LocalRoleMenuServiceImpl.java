package com.thinkwin.auth.localServiceImpl;

import com.thinkwin.auth.localService.LocalRoleMenuService;
import com.thinkwin.auth.mapper.db.SysRoleMenuMapper;
import com.thinkwin.common.model.db.SysRoleMenu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: yinchunlei
 * Date: 2017/7/14.
 * Company: thinkwin
 */
@Service("localRoleMenuService")
public class LocalRoleMenuServiceImpl implements LocalRoleMenuService {
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    /**
     * 根据角色id获取角色和菜单的关联表集合
     * @param roleId
     * @return
     */
    public List<SysRoleMenu> selectMenuIdByRoleId(String roleId){
        if(StringUtils.isNotBlank(roleId)){
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            List<SysRoleMenu> sysRoleMenus = sysRoleMenuMapper.select(sysRoleMenu);
            return sysRoleMenus;
        }
        return null;
    }
}
