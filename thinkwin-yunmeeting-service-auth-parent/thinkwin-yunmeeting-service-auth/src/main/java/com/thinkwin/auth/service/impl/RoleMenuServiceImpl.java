package com.thinkwin.auth.service.impl;

import com.github.pagehelper.Page;
import com.thinkwin.auth.mapper.db.SysMenuMapper;
import com.thinkwin.auth.mapper.db.SysRoleMenuMapper;
import com.thinkwin.auth.service.RoleMenuService;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.utils.CreateUUIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 角色菜单业务逻辑类
 * User: yinchunlei
 * Date: 2017/6/2.
 * Company: thinkwin
 */
@Service("roleMenuService")
public class RoleMenuServiceImpl implements RoleMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;
    //角色菜单的添加功能
    public boolean saveRoleAndMenu(String roleId,List<String> menuIds){
        if(null != roleId && null != menuIds && menuIds.size() > 0) {
            int number = 0;
            for (String menuId :menuIds) {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(roleId);
                sysRoleMenu.setMenuId(menuId);
                List<SysRoleMenu> sysRoleMenuList = sysRoleMenuMapper.select(sysRoleMenu);
                //如果sysRolePermissionList为空说明库中没有当前权限ID和该角色的关联关系
                if(sysRoleMenuList.size()<=0){
                    SysRoleMenu sysRoleMenu1 = new SysRoleMenu();
                    sysRoleMenu1.setRoleId(roleId);
                    sysRoleMenu1.setMenuId(menuId);
                    sysRoleMenu1.setId(CreateUUIdUtil.Uuid());
                    sysRoleMenu1.setCreateTime(new Date());
                    int num = sysRoleMenuMapper.insertSelective(sysRoleMenu1);
                    if(num > 0){
                        number = num;
                    }else {
                        return false;
                    }
                }else {
                    number = sysRoleMenuList.size();
                }
            }
            if(number > 0){
                return true;
            }
        }
        return false;
    }

    //角色菜单的批量删除功能
    public boolean deleteRoleAndMenu(String roleId,List<String> menuIds){
        if(null != roleId && !"".equals(roleId) && null != menuIds && menuIds.size() > 0){
            int deleteNum = 0;
            for (String menuId:menuIds) {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(roleId);
                sysRoleMenu.setMenuId(menuId);
                int deleteNumber = sysRoleMenuMapper.delete(sysRoleMenu);
                if(deleteNumber > 0){
                    deleteNum = deleteNumber;
                }else {
                    return false;
                }
            }
            if(deleteNum != 0) {
                return true;
            }
        }
        return false;
    }

    //角色菜单的单个删除
    public boolean deleteRoleAndMenu(String roleId,String menuId){
        if(null != roleId && !"".equals(roleId)&& null != menuId && !"".equals(menuId)){
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(menuId);
            int deleteNumber = sysRoleMenuMapper.delete(sysRoleMenu);
            if(deleteNumber > 0){
                return true;
            }
        }
        return false;
    }

    //角色菜单修改关联关系功能
    public boolean updateRoleAndMenu(String roleId,List<String> menuIds){
        if(null != roleId && !"".equals(roleId) && menuIds.size() >0){
            int num = 0;
            boolean status = false;
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            List<SysRoleMenu> sysRoleMenuList1 =sysRoleMenuMapper.select(sysRoleMenu);
            for (SysRoleMenu sysRoleMenuu: sysRoleMenuList1) {
                String menuIdd = sysRoleMenuu.getMenuId();
                for (String menuIddd:menuIds) {
                    if(menuIddd.equals(menuIdd)){
                        status = true;
                    }
                }
                if(status==false){
                    SysRoleMenu sysRoleMenu3 = new SysRoleMenu();
                    sysRoleMenu3.setMenuId(menuIdd);
                    sysRoleMenu3.setRoleId(roleId);
                    int deletete = sysRoleMenuMapper.delete(sysRoleMenu3);
                    if(deletete<=0){
                        return false;
                    }
                }else {
                    status = false;
                }
            }
            for(String menuId:menuIds) {
                SysRoleMenu sysRoleMenu2 = new SysRoleMenu();
                sysRoleMenu2.setMenuId(menuId);
                sysRoleMenu2.setRoleId(roleId);
                List<SysRoleMenu> sysRoleMenuListtt = sysRoleMenuMapper.select(sysRoleMenu2);
                if(null != sysRoleMenuListtt && sysRoleMenuListtt.size() <= 0){
                    sysRoleMenu.setId(CreateUUIdUtil.Uuid());
                    sysRoleMenu.setRoleId(roleId);
                    sysRoleMenu.setCreateTime(new Date());
                    num = sysRoleMenuMapper.insertSelective(sysRoleMenu);
                }else {
                    if(null != sysRoleMenuListtt) {
                        num = sysRoleMenuListtt.size();
                    }
                }
            }
            if (num != 0){
                return true;
            }
        }
        return false;
    }

    //根据角色id获取该id拥有哪些菜单
    public List<SysMenu> selectRoleAndMenuByRoleId(String roleId){
        List list = new ArrayList();
        SysRoleMenu sysRoleMenu = new SysRoleMenu();
        sysRoleMenu.setRoleId(roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuMapper.select(sysRoleMenu);
        if(null != sysRoleMenuList && sysRoleMenuList.size() > 0){
            for (SysRoleMenu sysRoleMenuu:sysRoleMenuList) {
                String menuId = sysRoleMenuu.getMenuId();
                if(null != menuId && !"".equals(menuId)){
                    SysMenu sysMenu = sysMenuMapper.selectByPrimaryKey(menuId);
                    list.add(sysMenu);
                }
            }

        }
        return list;
    }

    //根据角色id获取该id拥有哪些菜单的对象集合(加分页功能)
    public List<String> selectRoleAndMenuByRoleId(String roleId,Page page){
        List list = new ArrayList();
        SysRoleMenu sysRoleMenu = new SysRoleMenu();
        sysRoleMenu.setRoleId(roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuMapper.select(sysRoleMenu);
        if(null != sysRoleMenuList && sysRoleMenuList.size() > 0){
            for (SysRoleMenu sysRoleMenuu:sysRoleMenuList) {
                String menuId = sysRoleMenuu.getMenuId();
                if(null != menuId && !"".equals(menuId)){
                    SysMenu sysMenu = sysMenuMapper.selectByPrimaryKey(menuId);
                    list.add(sysMenu);
                }
            }

        }
        return list;
    }



}
