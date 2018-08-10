package com.thinkwin.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasMenu;
import com.thinkwin.common.model.console.SaasRoleMenu;
import com.thinkwin.common.model.console.SaasUserRole;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.console.mapper.SaasMenuMapper;
import com.thinkwin.console.mapper.SaasRoleMenuMapper;
import com.thinkwin.console.service.SaasMenuService;
import com.thinkwin.console.service.SaasRoleMenuService;
import com.thinkwin.console.service.SaasUserRoleService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service("saasMenuService")
public class SaasMenuServiceImpl implements SaasMenuService {
    @Autowired
    private SaasMenuMapper saasMenuMapper;
    @Autowired
    private SaasRoleMenuMapper saasRoleMenuMapper;
    @Autowired
    private SaasUserRoleService saasUserRoleService;
    @Autowired
    private SaasRoleMenuService saasRoleMenuService;
    @Override
    public boolean saveSaasMenu(SaasMenu saasMenu, List<String> roleIds) {
        if(null != saasMenu){
            String menuId = CreateUUIdUtil.Uuid();
            String parentId = StringUtils.isBlank(saasMenu.getParentId()) ? "0" : saasMenu.getParentId();
            saasMenu.setParentId(parentId);
            saasMenu.setId(menuId);
            saasMenu.setCreateTime(new Date());
            saasMenu.setCreater(TenantContext.getUserInfo().getUserId());

            SaasMenu saasMenuu = new SaasMenu();
            saasMenuu.setParentId(parentId);
            List<SaasMenu> saasMenuList = saasMenuMapper.select(saasMenuu);
            if(null != saasMenuList && saasMenuList.size() > 0){
                for (SaasMenu s:saasMenuList) {
                    SaasMenu sm = new SaasMenu();
                    sm.setId(s.getId());
                    //把该parentId下的菜单排序序号加1
                    sm.setSortNum(s.getSortNum()+1);
                    int i = saasMenuMapper.updateByPrimaryKeySelective(sm);
                    if(i<1){
                        return false;
                    }
                }
            }

            saasMenu.setSortNum(1);
            int num = saasMenuMapper.insert(saasMenu);
            if(num > 0){
                if(null != roleIds && roleIds.size() > 0){
                    for (String roleId:roleIds) {
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

    @Override
    public boolean saveSaasMenu(SaasMenu saasMenu) {
        if(saasMenu!=null){
            String menuId = CreateUUIdUtil.Uuid();
            String parentId = StringUtils.isBlank(saasMenu.getParentId()) ? "0" : saasMenu.getParentId();
            saasMenu.setParentId(parentId);
            saasMenu.setId(menuId);
            saasMenu.setCreateTime(new Date());
            saasMenu.setCreater(TenantContext.getUserInfo().getUserId());
            saasMenu.setSortNum(1);
            int i = saasMenuMapper.insertSelective(saasMenu);
            if(i > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer deleteSaasMenuByMenuId(String menuId) {
        if(StringUtils.isNotBlank(menuId)){
            SaasMenu saasMenu = new SaasMenu();
            saasMenu.setParentId(menuId);
            List<SaasMenu> menus = saasMenuMapper.select(saasMenu);
            if(null == menus || menus.size() == 0){
                SaasMenu saasMenu1= saasMenuMapper.selectByPrimaryKey(menuId);
                String parentId = saasMenu1.getParentId();
                Integer compositor = saasMenu1.getSortNum();
                int num = saasMenuMapper.deleteByPrimaryKey(menuId);
                if (num > 0) {
                    SaasRoleMenu srm = new SaasRoleMenu();
                    srm.setMenuId(menuId);
                    int delete = saasRoleMenuMapper.delete(srm);
                    if(delete <= 0){
                        return 0;
                    }
                    if (StringUtils.isNotBlank(parentId)) {
                        SaasMenu saasMenu2 = new SaasMenu();
                        saasMenu2.setParentId(parentId);
                        List<SaasMenu> saasMenuList = saasMenuMapper.select(saasMenu2);
                        if (null != saasMenuList && saasMenuList.size() > 0) {
                            for (SaasMenu s : saasMenuList) {
                                Integer sortNum = s.getSortNum();
                                //排序若大于删除d
                                if (sortNum > compositor) {
                                    Integer integer = updateSaasMenuCompositorDown(s.getId(), sortNum);
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

    @Override
    public boolean deleteSaasMenu(List<String> menuIds) {
        if (menuIds != null && menuIds.size()>0){
            int num = 0;
            for (String menuId:menuIds) {
                if (StringUtils.isNotBlank(menuId)){
                    int i = saasMenuMapper.deleteByPrimaryKey(menuId);
                    if(i <= 0){
                        num = 1;
                    }
                }
            }
            if (num == 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateSaasMenu(SaasMenu saasMenu) {
        String menuId = saasMenu.getId();
        if(null != menuId && !"".equals(menuId)){
            saasMenu.setModifyTime(new Date());
            int num = saasMenuMapper.updateByPrimaryKeySelective(saasMenu);
            if(num > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<SaasMenu> selectSaasMenusByUserId(String userId, String parentId) {
        if(StringUtils.isBlank(parentId)){
            parentId = "0";
        }
        List<SaasMenu> menuList = new ArrayList<>();
        if(StringUtils.isNotBlank(userId)) {
            List<SaasUserRole> userRoles = saasUserRoleService.selectSaasUserRole(userId);
            List<String> list = new ArrayList<>();
            if(null != userRoles && userRoles.size() > 0){
                //获取去重后的菜单主键id集合
                list = getUnrepeated(userRoles);
            }
            if(null != list && list.size() > 0){
                for (String menuId:list) {
                    SaasMenu saasMenu = saasMenuMapper.selectByPrimaryKey(menuId);
                    if(null != saasMenu){
                        String parentId1 = saasMenu.getParentId();
                        if(StringUtils.isNotBlank(parentId1) && parentId.equals(parentId1)){
                            menuList.add(saasMenu);
                        }
                    }
                }
            }
            //////////////////////////////////做排序功能///////////////////////////////////////////
            Collections.sort(menuList, new Comparator<SaasMenu>() {
                public int compare(SaasMenu arg0, SaasMenu arg1) {
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
            return menuList;
        }
        return null;
    }

    @Override
    public PageInfo selectSaasMenus(BasePageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getCurrentPage(),pageEntity.getPageSize());
        Example example = new Example(SaasMenu.class,true,true);
        example.setOrderByClause("sort_num");
        List<SaasMenu> menus = saasMenuMapper.selectByExample(example);
        if(null != menus && menus.size() > 0) {
            return new PageInfo<>(menus);
        }
        return null;
    }

    @Override
    public List<SaasMenu> selectSaasMenuByCondition(SaasMenu saasMnu) {
        if(null != saasMnu) {
            return saasMenuMapper.select(saasMnu);
        }
        return null;
    }

    @Override
    public SaasMenu selectSaasMenuByMenuId(String menuId) {
        if(null != menuId && !"".equals(menuId)){
            return saasMenuMapper.selectByPrimaryKey(menuId);
        }
        return null;
    }

    /**
     * 添加角色菜单中间表信息
     * @param menuId 菜单Id
     * @param roleId 角色Id
     * @return
     */
    private boolean insertRoleMenu(String menuId,String roleId){
        if(StringUtils.isNotBlank(menuId)&&StringUtils.isNotBlank(roleId)){
            SaasRoleMenu saasRoleMenu = new SaasRoleMenu();
            saasRoleMenu.setId(CreateUUIdUtil.Uuid());
            saasRoleMenu.setMenuId(menuId);
            saasRoleMenu.setRoleId(roleId);
            saasRoleMenu.setCreater(TenantContext.getUserInfo().getUserId());
            saasRoleMenu.setCreateTime(new Date());
            int i = saasRoleMenuMapper.insertSelective(saasRoleMenu);
            if(i >= 0){
                return true;
            }
        }
        return false;
    }
    /**
     * 向下修改菜单的排序号
     * @param menuId 菜单Id
     * @param sortNum 排序号
     * @return
     */
    private Integer updateSaasMenuCompositorDown(String menuId,Integer sortNum){
        SaasMenu saasMenu = new SaasMenu();
        saasMenu.setId(menuId);
        saasMenu.setModifyTime(new Date());
        saasMenu.setSortNum(sortNum-1);
        int i = saasMenuMapper.updateByPrimaryKeySelective(saasMenu);
        if(i != 1){
            return 2;
        }else{
            return 1;
        }
    }

    /**
     * 根据用户角色集合获得去重后的菜单集合
     * @return
     */
    private List<String> getUnrepeated(List<SaasUserRole> userRoles){
        List<String> list = new ArrayList<>();
        for (SaasUserRole saasUserRole:userRoles) {
            String roleId = saasUserRole.getRoleId();
            if(StringUtils.isNotBlank(roleId)){
                List<SaasRoleMenu> menuIds = saasRoleMenuService.selectMenuIdByRoleId(roleId);
                List<String> list1 = new ArrayList<>();
                if(null != menuIds && menuIds.size() > 0){
                    for (SaasRoleMenu saasRoleMenu: menuIds) {
                        String id = saasRoleMenu.getMenuId();
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
}
