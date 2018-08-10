package com.thinkwin.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasMenu;
import com.thinkwin.common.model.console.SaasRoleMenu;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.console.mapper.SaasMenuMapper;
import com.thinkwin.console.mapper.SaasRoleMenuMapper;
import com.thinkwin.console.service.SaasRoleMenuService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("saasRoleMenuService")
public class SaasRoleMenuServiceImpl implements SaasRoleMenuService {
    @Autowired
    private SaasRoleMenuMapper saasRoleMenuMapper;
    @Autowired
    private SaasMenuMapper saasMenuMapper;

    @Override
    public List<SaasRoleMenu> selectMenuIdByRoleId(String roleId) {
        if(StringUtils.isNotBlank(roleId)){
            SaasRoleMenu saasRoleMenu = new SaasRoleMenu();
            saasRoleMenu.setRoleId(roleId);
            return saasRoleMenuMapper.select(saasRoleMenu);
        }
        return null;
    }

    @Override
    public boolean saveSaasRoleMenu(String roleId, List<String> menuIds) {
        boolean flag = false;
        if(roleId != null && !"".equals(roleId)){
            if(menuIds != null && menuIds.size()>0){
                for (String menuId:menuIds) {
                    SaasRoleMenu saasRoleMenu = new SaasRoleMenu();
                    saasRoleMenu.setRoleId(roleId);
                    saasRoleMenu.setMenuId(menuId);
                    List<SaasRoleMenu> saasRoleMenuList = saasRoleMenuMapper.select(saasRoleMenu);
                    if (saasRoleMenuList.size()<=0){
                        saasRoleMenu.setCreateTime(new Date());
                        saasRoleMenu.setId(CreateUUIdUtil.Uuid());
                        int i = saasRoleMenuMapper.insertSelective(saasRoleMenu);
                        if(i > 0){
                            flag = true;
                        }else{
                            return false;
                        }
                    }
                }
            }
        }
        return flag;
    }

    @Override
    public boolean deleteSaasRoleMenu(String roleId, List<String> menuIds) {
        boolean flag = false;
        if(roleId != null && !"".equals(roleId)) {
            if (menuIds != null && menuIds.size() > 0) {
                for (String menuId : menuIds) {
                    SaasRoleMenu saasRoleMenu = new SaasRoleMenu();
                    saasRoleMenu.setRoleId(roleId);
                    saasRoleMenu.setMenuId(menuId);
                    int i = saasRoleMenuMapper.delete(saasRoleMenu);
                    if (i > 0){
                        flag = true;
                    }else{
                        return false;
                    }
                }
            }
        }
        return flag;
    }

    @Override
    public boolean deleteSaasRoleMenuOne(String roleId, String menuId) {
        if(null != roleId && !"".equals(roleId)&& null != menuId && !"".equals(menuId)){
            SaasRoleMenu saasRoleMenu = new SaasRoleMenu();
            saasRoleMenu.setRoleId(roleId);
            saasRoleMenu.setMenuId(menuId);
            int deleteNumber = saasRoleMenuMapper.delete(saasRoleMenu);
            if(deleteNumber > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateSaasRoleMenu(String roleId, List<String> menuIds) {
        if(null != roleId && !"".equals(roleId)&& null != menuIds && menuIds.size()>0){
            int num = 0;
            boolean status = false;
            SaasRoleMenu SaasRoleMenu = new SaasRoleMenu();
            SaasRoleMenu.setRoleId(roleId);
            List<SaasRoleMenu> SaasRoleMenuList1 =saasRoleMenuMapper.select(SaasRoleMenu);
            for (SaasRoleMenu SaasRoleMenuu: SaasRoleMenuList1) {
                String menuIdd = SaasRoleMenuu.getMenuId();
                for (String menuIddd:menuIds) {
                    if(menuIddd.equals(menuIdd)){
                        status = true;
                    }
                }
                if(!status){
                    SaasRoleMenu SaasRoleMenu3 = new SaasRoleMenu();
                    SaasRoleMenu3.setMenuId(menuIdd);
                    SaasRoleMenu3.setRoleId(roleId);
                    int deletete = saasRoleMenuMapper.delete(SaasRoleMenu3);
                    if(deletete<=0){
                        return false;
                    }
                }else {
                    status = false;
                }
            }
            for(String menuId:menuIds) {
                SaasRoleMenu SaasRoleMenu2 = new SaasRoleMenu();
                SaasRoleMenu2.setMenuId(menuId);
                SaasRoleMenu2.setRoleId(roleId);
                List<SaasRoleMenu> SaasRoleMenuListtt = saasRoleMenuMapper.select(SaasRoleMenu2);
                if(null != SaasRoleMenuListtt && SaasRoleMenuListtt.size() <= 0){
                    SaasRoleMenu.setId(CreateUUIdUtil.Uuid());
                    SaasRoleMenu.setMenuId(menuId);
                    SaasRoleMenu.setCreateTime(new Date());
                    num = saasRoleMenuMapper.insertSelective(SaasRoleMenu);
                }else {
                    num = SaasRoleMenuListtt.size();
                }
            }
            if (num != 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<SaasMenu> selectSaasRoleMenuByRoleId(String roleId, BasePageEntity page) {
        List<SaasMenu> l = null;
        if(null != roleId && !"".equals(roleId)){
            SaasRoleMenu saasRoleMenu = new SaasRoleMenu();
            saasRoleMenu.setRoleId(roleId);
            PageHelper.startPage(page.getCurrentPage(),page.getPageSize());
            List<SaasRoleMenu> list  = saasRoleMenuMapper.select(saasRoleMenu);
            if (list.size()>0){
                l = new ArrayList<>();
                for (SaasRoleMenu roleMenu:list) {
                    l.add(saasMenuMapper.selectByPrimaryKey(roleMenu.getMenuId()));
                }
            }
        }
        return l;
    }
}
