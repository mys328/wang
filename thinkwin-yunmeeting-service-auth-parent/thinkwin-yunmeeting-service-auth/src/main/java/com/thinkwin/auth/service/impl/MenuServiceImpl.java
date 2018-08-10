package com.thinkwin.auth.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.auth.localService.LocalRoleMenuService;
import com.thinkwin.auth.localService.LocalSaasTenantServcie;
import com.thinkwin.auth.localService.LocalUserService;
import com.thinkwin.auth.mapper.db.SysMenuMapper;
import com.thinkwin.auth.mapper.db.SysRoleMenuMapper;
import com.thinkwin.auth.service.MenuService;
import com.thinkwin.auth.service.SaasTenantService;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.SysMenu;
import com.thinkwin.common.model.db.SysRoleMenu;
import com.thinkwin.common.model.db.SysUserRole;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yunmeeting.framework.datasource.dynamicdatasource.DBContextHolder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 菜单部分业务逻辑层
 * User: yinchunlei
 * Date: 2017/6/2.
 * Company: thinkwin
 */
@Service("menuService")
public class MenuServiceImpl implements MenuService {
    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Autowired
    private LocalUserService localUserService;

    @Autowired
    private LocalRoleMenuService localRoleMenuService;

    /**
     * 添加菜单功能
     * @param sysMenu
     * @return
     */
    public boolean saveMenu(SysMenu sysMenu,List<String> list){
        if(null != sysMenu){
            String parentId;
            String menuId = CreateUUIdUtil.Uuid();
            String parentId1 = sysMenu.getParentId();
            if(StringUtils.isBlank(parentId1)){
                parentId = "0";
            }else{
                parentId = parentId1;
            }
            sysMenu.setParentId(parentId);
            sysMenu.setId(menuId);
            sysMenu.setCreateTime(new Date());
            //////////////////////////////处理排序/////////////////////////////////////
            SysMenu sysMenuu = new SysMenu();
            sysMenuu.setParentId(parentId);
            List<SysMenu> sysMenuList = sysMenuMapper.select(sysMenuu);
            if(null != sysMenuList && sysMenuList.size() > 0){
                for (SysMenu sysM:sysMenuList) {
                    SysMenu sysMM = new SysMenu();
                    sysMM.setId(sysM.getId());
                    sysMM.setSortNum(sysM.getSortNum()+1);
                    int i = sysMenuMapper.updateByPrimaryKeySelective(sysMM);
                    if(i!=1){
                        return false;
                    }
                }
            }
            //////////////////////////////处理排序/////////////////////////////////////
            sysMenu.setCreater(TenantContext.getUserInfo().getUserId());
            sysMenu.setSortNum(1);
            int num = sysMenuMapper.insert(sysMenu);
            if(num > 0){
                if(null != list && list.size() > 0){
                    for (String roleId:list) {
                        boolean b = insertRoleMenu(menuId, roleId);
                        if(!b){
                            return false;
                        }
                    }
                }else{
                    boolean b = insertRoleMenu(menuId, "1");
                    if(!b){
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 添加角色菜单中间表信息
     * @param menuId
     * @param roleId
     * @return
     */
    public boolean insertRoleMenu(String menuId,String roleId){
        if(StringUtils.isNotBlank(menuId)&&StringUtils.isNotBlank(roleId)){
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setId(CreateUUIdUtil.Uuid());
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setCreater(TenantContext.getUserInfo().getUserId());
            sysRoleMenu.setCreateTime(new Date());
            int i = sysRoleMenuMapper.insertSelective(sysRoleMenu);
            if(i >= 0){
                return true;
            }
        }
        return false;
    }
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;
    /**
     * 根据菜单的主键删除相应的信息
     * @param menuId
     * @return
     */
    public Integer deleteMenu(String menuId){
        if(StringUtils.isNotBlank(menuId)){
            SysMenu sysMenu = new SysMenu();
            sysMenu.setParentId(menuId);
            List<SysMenu> menus = sysMenuMapper.select(sysMenu);
            if(null == menus || menus.size() == 0){
                SysMenu sysMenu1= sysMenuMapper.selectByPrimaryKey(menuId);
                String parentId = sysMenu1.getParentId();
                Integer compositor = sysMenu1.getSortNum();
                int num = sysMenuMapper.deleteByPrimaryKey(menuId);
                if (num > 0) {
                    SysRoleMenu srm = new SysRoleMenu();
                    srm.setMenuId(menuId);
                    int delete = sysRoleMenuMapper.delete(srm);
                    if(delete <= 0){
                        return 0;
                    }
                    if (StringUtils.isNotBlank(parentId)) {
                        SysMenu sysMenu2 = new SysMenu();
                        sysMenu2.setParentId(parentId);
                        List<SysMenu> sysOrganizations = sysMenuMapper.select(sysMenu2);
                        if (null != sysOrganizations && sysOrganizations.size() > 0) {
                            for (SysMenu sysOrg : sysOrganizations) {
                                Integer compositor1 = sysOrg.getSortNum();
                                if (compositor1 > compositor) {
                                    Integer integer = updateMenuCompositorDown(sysOrg.getId(), compositor1);
                                    if (integer != 1) {
                                        return 0;
                                    }
                                }
                            }
                        }
                    }
                    return 1;
                }
            }else {
                return 2;
            }
        }
        return 0;
    }

    /**
     * 向下修改菜单的排序号
     * @param menuId
     * @param compositor
     * @return
     */
    public Integer updateMenuCompositorDown(String menuId,Integer compositor){
        SysMenu sysMenu = new SysMenu();
        sysMenu.setId(menuId);
        sysMenu.setModifyTime(new Date());
        sysMenu.setSortNum(compositor-1);
        int i = sysMenuMapper.updateByPrimaryKeySelective(sysMenu);
        if(i != 1){
            return 2;
        }else{
            return 1;
        }
    }

    /**
     * 批量删除菜单功能
     * @param menuIds
     * @return
     */
    public boolean deleteMenu(List<String> menuIds){
        if(null != menuIds && menuIds.size() > 0){
            int num = 0;
            for (String menuId:menuIds) {
                if(null != menuId && !"".equals(menuId)){
                    int status = sysMenuMapper.deleteByPrimaryKey(menuId);
                    if(status <= 0){
                        num = 1;
                    }
                }
            }
            if(num == 0){
                return true;
            }
        }
        return false;
    }

    /**
     * 修改菜单功能
     * @param sysMenu
     * @return
     */
    public boolean updateMenu(SysMenu sysMenu){
        String menuId = sysMenu.getId();
        if(null != menuId && !"".equals(menuId)){
            sysMenu.setModifyTime(new Date());
            int num = sysMenuMapper.updateByPrimaryKeySelective(sysMenu);
            if(num > 0){
                return true;
            }
        }
        return false;
    }

    @Autowired
    //private SaasTenantService saasTenantService;
    private LocalSaasTenantServcie localSaasTenantServcie;
    /**
     * 不带分页查询全部菜单功能
     * @param userId
     * @param parentId
     * @return
     */
    public List<SysMenu> selectMenus(String userId,String parentId,String tenantType){
        if(StringUtils.isBlank(parentId)){
            parentId = "0";
        }
        List<SysMenu> menuList = new ArrayList<>();
        if(StringUtils.isNotBlank(userId)) {
           List<SysUserRole> userRoles = localUserService.selectUserRole(userId);
            List<String> list = new ArrayList();
           if(null != userRoles && userRoles.size() > 0){
               //获取去重后的菜单主键id集合
               list = getUnrepeated(userRoles);
           }
            if(null != list && list.size() > 0){
               for (String menuId:list) {
                   SysMenu sysMenu = sysMenuMapper.selectByPrimaryKey(menuId);
                   if(null != sysMenu){
                       String parentId1 = sysMenu.getParentId();
                       if(StringUtils.isNotBlank(parentId1) && parentId.equals(parentId1)){
                           if("0".equals(tenantType)){
                               if(null != sysMenu.getVersionType() &&"0".equals(sysMenu.getVersionType())) {
                                   menuList.add(sysMenu);
                               }
                           }else if("1".equals(tenantType)){
                               if(null != sysMenu.getVersionType() &&("0".equals(sysMenu.getVersionType()) || "1".equals(sysMenu.getVersionType()))) {
                                   menuList.add(sysMenu);
                               }
                           }
                       }
                   }
               }
           }
           //////////////////////////////////做排序功能///////////////////////////////////////////
            Collections.sort(menuList, new Comparator<SysMenu>() {
                public int compare(SysMenu arg0, SysMenu arg1) {
                    int sortNum0 = arg0.getSortNum();
                    int sortNum1 = arg1.getSortNum();
                    if (sortNum1 < sortNum0) {//如果sortNum1>sortNum0时就是从大到小排序、现在是从小到大排序
                        return 1;
                    } else if (sortNum1 == sortNum0) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
            ////////////////////////////////////////////////////////////////////////////
            return menuList;
        }
        return null;
    }

    /**
     * 根据用户角色集合获得去重后的菜单集合
     * @return
     */
    public List<String> getUnrepeated(List<SysUserRole> userRoles){
        List<String> list = new ArrayList();
        for (SysUserRole sysUserRole:userRoles) {
            String roleId = sysUserRole.getRoleId();
            if(StringUtils.isNotBlank(roleId)){
                List<SysRoleMenu> menuIds = localRoleMenuService.selectMenuIdByRoleId(roleId);
                List list1 = new ArrayList();
                if(null != menuIds && menuIds.size() > 0){
                    for (SysRoleMenu sysRoleMenu: menuIds) {
                        String id = sysRoleMenu.getMenuId();
                        if(StringUtils.isNotBlank(id)){
                            list1.add(id);
                        }
                    }
                }
                list.removeAll(list1);
                list.addAll(list1);
            }
        }
        return list;
    }

    //带分页查询全部菜单功能
    public PageInfo selectMenus(BasePageEntity pageEntity){
        PageHelper.startPage(pageEntity.getCurrentPage(),pageEntity.getPageSize());
        Example example = new Example(SysMenu.class,true,true);
        example.setOrderByClause("sort_num");
        List<SysMenu> menus = sysMenuMapper.selectByExample(example);
        if(null != menus && menus.size() > 0) {
            return new PageInfo<>(menus);
        }
        return null;
    }

    //带条件查询菜单集合
    public List<SysMenu> selectMenuByCondition(SysMenu sysMnu){
        if(null != sysMnu) {
            return sysMenuMapper.select(sysMnu);
        }
        return null;
    }

    //根据菜单主键id获取菜单详细信息
    public SysMenu selectMenuByMenuId(String menuId){
        if(null != menuId && !"".equals(menuId)){
            return sysMenuMapper.selectByPrimaryKey(menuId);
        }
        return null;
    }
}
