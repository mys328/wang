package com.thinkwin.auth.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.auth.localService.LocalUserService;
import com.thinkwin.auth.mapper.core.SaasTenantInfoMapper;
import com.thinkwin.auth.mapper.core.SaasUserWebMapper;
import com.thinkwin.auth.mapper.db.SysOrganizationMapper;
import com.thinkwin.auth.mapper.db.SysRoleMapper;
import com.thinkwin.auth.mapper.db.SysUserMapper;
import com.thinkwin.auth.mapper.db.SysUserRoleMapper;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.mapper.BizImageRecorderMapper;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.SaasTenantInfo;
import com.thinkwin.common.model.core.SaasUserWeb;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.PingYinUtil;
import com.thinkwin.common.utils.StringUtil;
import com.thinkwin.common.vo.SysOrganizationVo;
import com.thinkwin.common.vo.SysUserVo;
import com.thinkwin.common.vo.SysUserVo1;
import com.thinkwin.common.vo.meetingVo.PersonsVo;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.yuncm.service.MeetingReserveService;
import com.thinkwin.yunmeeting.framework.datasource.dynamicdatasource.DBContextHolder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * User: yinchunlei
 * Date: 2017/5/24.
 * Company: thinkwin
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SaasUserWebMapper saasUserWebMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private LocalUserService localUserService;
    @Autowired
    private SysOrganizationMapper sysOrganizationMapper;
    @Autowired
    private SaasTenantInfoMapper saasTenantInfoMapper;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private BizImageRecorderMapper bizImageRecorderMapper;
    /**
     * 添加新用户功能接口
     * @param sysUser
     * @param roleIds
     * @return
     */
    public boolean saveUser(SysUser sysUser,List<String> roleIds){
        int num = 0;
        String userName = sysUser.getUserName();
        if(null != userName && !"".equals(userName)){
            String userNamePinyin = PingYinUtil.getPingYin(userName);
            if(null != userNamePinyin && !"".equals(userNamePinyin)){
                sysUser.setUserNamePinyin(userNamePinyin);
            }
        }
        String orgId = sysUser.getOrgId();
        if(null == orgId || "".equals(orgId)){
            sysUser.setOrgId("0");
        }
        //增加微信名称转换
        String nickName = sysUser.getWechat();
        if(StringUtils.isNotBlank(nickName)) {
            try {
                sysUser.setWechat(StringUtil.bytes2Hex(nickName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String userId1 = sysUser.getId();
        if(StringUtils.isBlank(userId1)){
            return false;
        }
        SysUser sysUser1 = sysUserMapper.selectByPrimaryKey(userId1);
        int insert = 0;
        if(null == sysUser1) {
            insert = sysUserMapper.insertSelective(sysUser);
        }else{
            insert = sysUserMapper.updateByPrimaryKeySelective(sysUser);
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
            List roleIdList = new ArrayList();
            roleIdList.add("1");
            boolean status = this.saveUserRole(userId,roleIdList);
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

    //添加新用户功能接口
    //String typee 如果typee为“1”时说明是注册新的租户（此时需要切换数据源） typee为“2”时说明是注册租户下的新用户
    public boolean saveUser(SysUser sysUser,List<String> roleIds,String typee){
        int num = 0;
        int insert =0;
        if(StringUtils.isNotBlank(typee) && "1".equals(typee)){
            String tenantId = sysUser.getTenantId();
            DBContextHolder.setDBType(tenantId);
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

    /**
     * 根据用户名或手机号判断用户是否存在
     * @param userName
     * @return
     */
    public boolean selectUserName(String userName){
        SaasUserWeb saasUserWeb = new SaasUserWeb();
        saasUserWeb.setAccount(userName);
        List<SaasUserWeb> userList = saasUserWebMapper.select(saasUserWeb);
        if(null != userList && userList.size() > 0){
            return true;
        }
        return false;
    }

    /**
     * 根据用户主键ID删除用户功能
     * @param userId
     * @return
     */
    public boolean deleteUserByUserId(String userId){
        int status = sysUserMapper.deleteByPrimaryKey(userId);
        if(status > 0){
            return true;
        }
        return false;
    }

    /**
     * 根据用户的主键ID修改用户信息
     * @param sysUser
     * @return
     */
    @Override
    public boolean updateUserByUserId(SysUser sysUser) {
        String userName = sysUser.getUserName();
        if(null != userName && !"".equals(userName)){
            String userNamePinyin = PingYinUtil.getPingYin(userName);
            if(null != userNamePinyin && !"".equals(userNamePinyin)){
                sysUser.setUserNamePinyin(userNamePinyin);
            }
        }
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

    /**
     * 根据条件查询用户列表功能
     * @param sysUser
     * @return
     */
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
        return select;
    }

    /**
     * 用户角色的添加功能
     * @param userId
     * @param roleIds
     * @return
     */
    public boolean saveUserRole(String userId,List<String> roleIds){
        if(null != userId && null != roleIds && roleIds.size() > 0) {
            int number = 0;
            for (String roleId :roleIds) {
                boolean status = localUserService.userRoleNumber(userId,roleId);
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
     * 根据用户的ID删除用户的角色功能
     * @param userId
     * @param roleIds
     * @return
     */
    public boolean deleteUserRole(String userId,List<String> roleIds){
        if(null != userId && !"".equals(userId) && null != roleIds && roleIds.size() > 0){
            int deleteNum = 0;
            for (String roleId:roleIds) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(userId);
                sysUserRole.setRoleId(roleId);
                int deleteNumber = sysUserRoleMapper.delete(sysUserRole);
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

    /**
     * 根据用户的ID修改用户的角色信息（批量修改）
     * @param userId
     * @param roleIds
     * @return
     */
    public boolean updateUserRole(String userId,List<String> roleIds){
        if(null != userId && !"".equals(userId) && roleIds.size() >0){
            int num = 0;
            boolean status = false;
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            List<SysUserRole> sysUserRoleList1 =sysUserRoleMapper.select(sysUserRole);
            for (SysUserRole sysUserRolee: sysUserRoleList1) {
                String roleIdd = sysUserRolee.getRoleId();
                for (String roleIddd:roleIds) {
                    if(roleIddd.equals(roleIdd)){
                        status = true;
                    }
                }
                if(status==false){
                    SysUserRole sysUserRole3 = new SysUserRole();
                    sysUserRole3.setUserId(userId);
                    sysUserRole3.setRoleId(roleIdd);
                    int deletete = sysUserRoleMapper.delete(sysUserRole3);
                    if(deletete<=0){
                        return false;
                    }
                }else {
                    status = false;
                }
            }
            for(String roleId:roleIds) {
                SysUserRole sysUserRole2 = new SysUserRole();
                sysUserRole2.setUserId(userId);
                sysUserRole2.setRoleId(roleId);
                List<SysUserRole> sysUserRoleListtt = sysUserRoleMapper.select(sysUserRole2);
                if(null != sysUserRoleListtt && sysUserRoleListtt.size() <= 0){
                    sysUserRole.setId(CreateUUIdUtil.Uuid());
                    sysUserRole.setCreaterId(userId);
                    sysUserRole.setRoleId(roleId);
                    sysUserRole.setCreateTime(new Date());
                    num = sysUserRoleMapper.insertSelective(sysUserRole);
                }else {
                    if(null != sysUserRoleListtt) {
                        num = sysUserRoleListtt.size();
                    }
                }
            }
            if (num != 0){
                return true;
            }
        }
        return false;
    }

    /**
     * 根据用户的ID修改用户的角色信息(单一修改)
     * @return
     */
    public boolean updateUserRole(SysUserRole sysUserRole){
        if(null != sysUserRole){
            sysUserRole.setModifyTime(new Date());
            int i = sysUserRoleMapper.updateByPrimaryKeySelective(sysUserRole);
            if(i <= 1){
                return true;
            }
        }
        return false;
    }

    /**
     * 根据用户的主键ID获取用户的角色信息(分页功能暂时无效)
     * @param userId
     * @param page
     * @return
     */
    public List<SysRole> selectUserRole(String userId,Page page){
        List list = new ArrayList();
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(userId);
        List<SysUserRole> sysUserRoleList = sysUserRoleMapper.select(sysUserRole);
        if(null != sysUserRoleList && sysUserRoleList.size() > 0){
            for (SysUserRole sysUserRolee:sysUserRoleList) {
                String roleId = sysUserRolee.getRoleId();
                if(null != roleId && !"".equals(roleId)){
                    SysRole sysRole = sysRoleMapper.selectByPrimaryKey(roleId);
                    list.add(sysRole);
                }
            }
            return list;
        }
        return null;
    }

    @Override
    public List<String> selectUserRole(String userId) {
        List<String> list = new ArrayList<>();
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(userId);
        List<SysUserRole> sysUserRoleList = sysUserRoleMapper.select(sysUserRole);
        if(null != sysUserRoleList && sysUserRoleList.size() > 0){
            for (SysUserRole userRole:sysUserRoleList) {
                String roleId = userRole.getRoleId();
                list.add(roleId);
            }
            return list;
        }
        return null;
    }

    @Override
    public List<String> selectUserRole(SysUser user) {
        if(user!=null) {
            List<String> list = new ArrayList<>();
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(user.getId());
            List<SysUserRole> sysUserRoleList = sysUserRoleMapper.select(sysUserRole);
            if (null != sysUserRoleList && sysUserRoleList.size() > 0) {
                for (SysUserRole userRole : sysUserRoleList) {
                    String roleId = userRole.getRoleId();
                    list.add(roleId);
                }
                return list;
            }
        }
        return null;
    }

    /**
     * 根据用户的主键ID获取用户的信息功能
     * @param userId
     * @return
     */
    public SysUser selectUserByUserId(String userId){
        //return
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(userId);
        if(null!=sysUser) {
            String wechat = sysUser.getWechat();
            if (StringUtils.isNotBlank(wechat) && StringUtil.isHexNumber(wechat)) {
                try {
                    wechat = StringUtil.parseHexString(wechat);
                    sysUser.setWechat(wechat);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return sysUser;
        }
        return null;
    }

    /**
     * 根据组织机构主键id获取该组织下所有用户
     * @param orgId
     * @return
     */
    public Map selectUserByOrgId(String orgId,BasePageEntity basePageEntity){
        return selectDataByOrgId(orgId,basePageEntity);
    }


    /**
     * 根据组织机构主键id查询数据
     * @param orgId
     * @return
     */
    public Map selectDataByOrgId(String orgId,BasePageEntity basePageEntity){
        Map map = new HashMap();
        if(null != orgId && !"".equals(orgId)) {
            List<SysOrganizationVo> sysOrganizations1 = new ArrayList<>();
            List<SysUser> sUs = new ArrayList<>();
           /* SysUser sysUser = new SysUser();
            sysUser.setOrgId(orgId);*/
            //去除分页2017-10-28（前端贾少旺过来要求修改）
            //PageHelper.startPage(basePageEntity.getCurrentPage(),basePageEntity.getPageSize());
            // List<SysUser> sysUsers = sysUserMapper.select(sysUser);
            Example example = new Example(SysUser.class);
            example.createCriteria().andEqualTo("orgId",orgId).andNotEqualTo("status","89");
            List<SysUser> sysUsers = sysUserMapper.selectByExample(example);
            if(null != sysUsers && sysUsers.size() > 0){
                for (SysUser sysU :sysUsers) {
                    Integer status = sysU.getStatus();
                    if(null != status && status != 89){
                        String wechat = sysU.getWechat();
                        if (StringUtils.isNotBlank(wechat)&&StringUtil.isHexNumber(wechat)) {
                            try {
                                wechat = StringUtil.parseHexString(wechat);
                                sysU.setWechat(wechat);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        String photo = sysU.getPhoto();
                        if(StringUtils.isNotBlank(photo)){
                            photo = fileUploadService.selectTenementByFile(photo);
                        }
                        sysU.setPhoto(photo);
                        sUs.add(sysU);
                    }
                }
            }
            //去除分页2017-10-28（前端贾少旺过来要求修改）
            map.put("sysUser", new PageInfo<>(sUs));
            //该方法有排序功能
            List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectAllSysOrganiztionsAndOrderBy(orgId);
            if(null != sysOrganizations && sysOrganizations.size() > 0){
                sysOrganizations1 = this.organizationProcessor(sysOrganizations);
            }
            map.put("sysOrganizations", sysOrganizations1);
            ///////////////////////////////////////递归算法//////////////////////////////////////////
            List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
            JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), orgId);
            JSONArray jsonArray1 = treeMenuList1(jsonArray);
            List ll = new ArrayList();
            for (Object jsona:jsonArray1) {
                String s = jsona.toString();
                if(StringUtils.isNotBlank(s)){
                    ll.add(s);
                }
            }
            ll.add(orgId);
            ////////////////////////////////////////递归算法/////////////////////////////////////////
            Map map1 = selectUserNumInfo1(ll);
            Integer userTotalNum = 0;
            if(null != map1){
                userTotalNum = (Integer) map1.get("userTotalNum");
            }
            map.put("userTotalNum",userTotalNum);
        }
        return map;
    }

    /**
     *  根据组织机构主键id获取该组织下所有用户(人员组件中预订会议时使用)
     *  @param orgId
     *  @return
     */

    public Map selectUserByOrgId(String orgId,BasePageEntity basePageEntity,String startTime,String endTime,String conferenceId){
        return selectDataByOrgId(orgId,basePageEntity,startTime,endTime,conferenceId);
    }

    @Autowired
    private MeetingReserveService meetingReserveService;
    /**
     * 根据组织机构主键id查询数据
     * @param orgId
     * @return
     */
    public Map selectDataByOrgId(String orgId,BasePageEntity basePageEntity,String startTime,String endTime,String conferenceId){
        Map attendeeIdList = new HashMap();
        if(StringUtils.isNotBlank(startTime)&& StringUtils.isNotBlank(endTime)) {
            //获取某段时间内的所有会议
            List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate = meetingReserveService.findMeetingConferenceByTimeNew(startTime, endTime);
            if (null != byMeetingRoomIdAndMeetingtakeStartDate && byMeetingRoomIdAndMeetingtakeStartDate.size() > 0) {
                for (YunmeetingConference yunmeetingConference : byMeetingRoomIdAndMeetingtakeStartDate) {
                    if (null != yunmeetingConference) {
                        String meetingId = yunmeetingConference.getId();
                        if (StringUtils.isNotBlank(meetingId)) {
                            //根据会议id获取参会人员
                            List<PersonsVo> personsVos = meetingReserveService.selectAllParticipantByMeetingId(meetingId);
                            if(null != personsVos && personsVos.size() > 0) {
                                for (PersonsVo personsVo:personsVos) {
                                    if(null != personsVo){
                                        String userId = personsVo.getUserId();
                                        if(StringUtils.isNotBlank(userId)) {
                                            if(StringUtils.isNotBlank(conferenceId)){
                                                if (!attendeeIdList.containsKey(userId + "+" + meetingId)) {
                                                    Map mapp = new HashMap();
                                                    mapp.put("userId", userId);
                                                    mapp.put("conferenceId", meetingId);
                                                    attendeeIdList.put(userId + "+" + meetingId, mapp);
                                                }
                                            }else {
                                                if (!attendeeIdList.containsKey(userId)) {
                                                    attendeeIdList.put(userId,userId);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Map map = new HashMap();
        if(null != orgId && !"".equals(orgId)) {
            List<SysOrganizationVo> sysOrganizations1 = new ArrayList<>();
            List<SysUserVo1> sUs = new ArrayList<>();
            Example example = new Example(SysUser.class);
            example.createCriteria().andEqualTo("orgId",orgId).andNotEqualTo("status","89");
            List<SysUser> sysUsers = sysUserMapper.selectByExample(example);
            if(null != sysUsers && sysUsers.size() > 0){
                for (SysUser sysU :sysUsers) {
                    Integer status = sysU.getStatus();
                    if(null != status && status != 89){
                        SysUserVo1 sysUserVo1 = getUserInfo(sysU,attendeeIdList,conferenceId);
                        sUs.add(sysUserVo1);
                    }
                }
            }
            //去除分页2017-10-28（前端贾少旺过来要求修改）
            map.put("sysUser", new PageInfo<>(sUs));
            //该方法有排序功能
            List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectAllSysOrganiztionsAndOrderBy(orgId);
            if(null != sysOrganizations && sysOrganizations.size() > 0){
                sysOrganizations1 = this.organizationProcessor(sysOrganizations);
            }
            map.put("sysOrganizations", sysOrganizations1);
            ///////////////////////////////////////递归算法//////////////////////////////////////////
            List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
            JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), orgId);
            JSONArray jsonArray1 = treeMenuList1(jsonArray);
            List ll = new ArrayList();
            for (Object jsona:jsonArray1) {
                String s = jsona.toString();
                if(StringUtils.isNotBlank(s)){
                    ll.add(s);
                }
            }
            ll.add(orgId);
            ////////////////////////////////////////递归算法/////////////////////////////////////////
            Map map1 = selectUserNumInfo1(ll);
            Integer userTotalNum = 0;
            if(null != map1){
                userTotalNum = (Integer) map1.get("userTotalNum");
            }
            map.put("userTotalNum",userTotalNum);
        }
        return map;
    }

    //public SysUserVo1 getUserInfo(SysUser sysU,List<String> attendeeIdList){
    public SysUserVo1 getUserInfo(SysUser sysU,Map attendeeIdList,String conferenceId) {
        SysUserVo1 sysUserVo1 = new SysUserVo1();
        String sysUserId = sysU.getId();
        sysUserVo1.setId(sysUserId);
        if(StringUtils.isNotBlank(conferenceId)){
            int falg = 0;
            Iterator entries = attendeeIdList.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                Map value = (Map)entry.getValue();
                if(null != value){
                    String userId = (String) value.get("userId");
                    if(StringUtils.isNotBlank(userId)&& sysUserId.equals(userId)){
                        String conferenceId1 = (String) value.get("conferenceId");
                        if(StringUtils.isNotBlank(conferenceId1) && !conferenceId.equals(conferenceId1)){
                            falg = 1;
                        }
                    }
                }
            }
            if (falg != 1) {
                sysUserVo1.setParticipantStatus(false);
            } else {
                sysUserVo1.setParticipantStatus(true);
            }
        }else{
            if (attendeeIdList.containsKey(sysUserId)) {
                sysUserVo1.setParticipantStatus(true);
            } else {
                sysUserVo1.setParticipantStatus(false);
            }
        }
        String wechat = sysU.getWechat();
        if (StringUtils.isNotBlank(wechat)&&StringUtil.isHexNumber(wechat)) {
            try {
                wechat = StringUtil.parseHexString(wechat);
                sysUserVo1.setWechat(wechat);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String photo = sysU.getPhoto();
        if(StringUtils.isNotBlank(photo)){
//            Map<String, String> photos = fileUploadService.selectFileCommon(photo);
            Map<String, String> photos = getUploadInfo(photo);
            if(null != photos) {
                sysUserVo1.setPhoto(photos.get("primary"));
                sysUserVo1.setBigPicture(photos.get("big"));
                sysUserVo1.setInPicture(photos.get("in"));
                sysUserVo1.setSmallPicture(photos.get("small"));
            }
        }
        sysUserVo1.setAddress(sysU.getAddress());
        sysUserVo1.setBirthday(sysU.getBirthday());
        sysUserVo1.setCreater(sysU.getCreater());
        sysUserVo1.setCreateTime(sysU.getCreateTime());
        sysUserVo1.setDeviceToken(sysU.getDeviceToken());
        sysUserVo1.setDeviceType(sysU.getDeviceType());
        sysUserVo1.setEmail(sysU.getEmail());
        sysUserVo1.setIsSubscribe(sysU.getIsSubscribe());
        sysUserVo1.setModifyer(sysU.getModifyer());
        sysUserVo1.setModifyTime(sysU.getModifyTime());
        sysUserVo1.setOpenId(sysU.getOpenId());
        sysUserVo1.setOrgId(sysU.getOrgId());
        sysUserVo1.setOrgName(sysU.getOrgName());
        sysUserVo1.setPhoneNumber(sysU.getPhoneNumber());
        sysUserVo1.setPosition(sysU.getPosition());
        sysUserVo1.setReserve1(sysU.getReserve1());
        sysUserVo1.setReserve2(sysU.getReserve2());
        sysUserVo1.setReserve3(sysU.getReserve3());
        sysUserVo1.setSex(sysU.getSex());
        sysUserVo1.setStatus(sysU.getStatus());
        sysUserVo1.setTenantId(sysU.getTenantId());
        sysUserVo1.setUserName(sysU.getUserName());
        sysUserVo1.setUserNamePinyin(sysU.getUserNamePinyin());
        sysUserVo1.setUserNumber(sysU.getUserNumber());
        return sysUserVo1;
    }

    /**
     * 根据组织机构id获取人员数量信息
     * @param orgId
     * @return
     */
    public Map selectUserNumInfo(String orgId){
        Map map = new HashMap();
        //人员状态：1.已激活、2.未激活、3.已禁用、4.未分配部门、89.离职、
        if(StringUtils.isNotBlank(orgId)){
            Map mapp = new HashMap();
            mapp.put("orgId",orgId);
            mapp.put("status","89");
            Integer userTotalNum = sysUserMapper.selectUserCount(mapp);
            //用户总数
            map.put("userTotalNum",userTotalNum);
            SysUser sysUser = new SysUser();
            sysUser.setOrgId(orgId);
            sysUser.setStatus(1);
            int activatedNum = sysUserMapper.selectCount(sysUser);
            //已激活用户人数
            map.put("activatedNum",activatedNum);
            SysUser sysUser2 = new SysUser();
            sysUser2.setOrgId(orgId);
            sysUser2.setStatus(2);
            int noActivatedNum = sysUserMapper.selectCount(sysUser2);
            //未激活用户人数
            map.put("noActivatedNum",noActivatedNum);
            SysUser sysUser3 = new SysUser();
            sysUser3.setOrgId(orgId);
            sysUser3.setStatus(3);
            int disableNum = sysUserMapper.selectCount(sysUser3);
            //禁用用户人数
            map.put("disableNum",disableNum);
            if(StringUtils.isNotBlank(orgId) && "0".equals(orgId)) {
                SysUser sysUser4 = new SysUser();
                sysUser4.setOrgId(orgId);
                sysUser4.setStatus(4);
                int noDistributionDepartmentNum = sysUserMapper.selectCount(sysUser4);
                //未分配部门用户人数
                map.put("noDistributionDepartmentNum", noDistributionDepartmentNum);
            }
            return map;
        }
        return null;
    }


    /**
     * 根据组织机构id获取人员数量信息s
     * @return
     */
    public Map selectUserNumInfo1(List list){
        Map map = new HashMap();
        //人员状态：1.已激活、2.未激活、3.已禁用、4.未分配部门、89.离职、
        if(null != list && list.size() > 0){
            Map mapp = new HashMap();
            mapp.put("orgIds",list);
            mapp.put("status","89");
            Integer userTotalNum = sysUserMapper.selectUserCount(mapp);
            //用户总数
            map.put("userTotalNum",userTotalNum);
            Map map1 = new HashMap();
            map1.put("orgIds",list);
            map1.put("status",1);
            Integer activatedNum = sysUserMapper.selectUserCountByStatus(map1);
            //已激活用户人数
            map.put("activatedNum",activatedNum);
            Map map2 = new HashMap();
            map2.put("orgIds",list);
            map2.put("status",2);
            Integer noActivatedNum = sysUserMapper.selectUserCountByStatus(map2);
            //未激活用户人数
            map.put("noActivatedNum",noActivatedNum);
            Map map3 = new HashMap();
            map3.put("orgIds",list);
            map3.put("status",3);
            Integer disableNum = sysUserMapper.selectUserCountByStatus(map3);
            //禁用用户人数
            map.put("disableNum",disableNum);
            Map map4 = new HashMap();
            map4.put("orgIds",list);
            map4.put("status",4);
            Integer noDistributionDepartmentNum = sysUserMapper.selectUserCountByStatus(map4);
            //未分配部门用户人数
            map.put("noDistributionDepartmentNum", noDistributionDepartmentNum);
            return map;
        }
        return null;
    }

    /**
     * 组织机构处理工具类(判断某个组织下是否含有子级)
     * @return
     */
    public List<SysOrganizationVo> organizationProcessor(List<SysOrganization> sysOrganizations){
        List<SysOrganizationVo> sysOrganizations1 = new ArrayList<>();
        if(null != sysOrganizations && sysOrganizations.size() > 0){
            for (SysOrganization sysOrganizationn:sysOrganizations) {
                SysOrganizationVo sorgvo = new SysOrganizationVo();
                String sysOrganizationId = sysOrganizationn.getId();
                if(null != sysOrganizationId && !"".equals(sysOrganizationId)){
                    sorgvo.setSysOrganization(sysOrganizationn);
                    SysOrganization sysOrganization1 = new SysOrganization();
                    sysOrganization1.setParentId(sysOrganizationId);
                    List<SysOrganization> sysOrganizationss = sysOrganizationMapper.select(sysOrganization1);
                    if(null != sysOrganizationss && sysOrganizationss.size() > 0){
                        sorgvo.setLeaf(true);
                    }else{
                        sorgvo.setLeaf(false);
                    }
                }

                ///////////////////////////////////////递归算法//////////////////////////////////////////
                List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
                JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), sysOrganizationId);
                JSONArray jsonArray1 = treeMenuList1(jsonArray);
                List ll = new ArrayList();
                for (Object jsona:jsonArray1) {
                    String s = jsona.toString();
                    if(StringUtils.isNotBlank(s)){
                        ll.add(s);
                    }
                }
                ll.add(sysOrganizationId);
                ////////////////////////////////////////递归算法/////////////////////////////////////////
                Map map1 = selectUserNumInfo1(ll);
                Integer userTotalNum = 0;
                if(null != map1){
                    userTotalNum = (Integer) map1.get("userTotalNum");
                }
                sorgvo.setOrgUserNum(userTotalNum);
                sysOrganizations1.add(sorgvo);
            }
        }
        return sysOrganizations1;
    }
    /**
     * 组件搜索查询功能接口
     * @param searchParameter
     * @return
     */
    public Map searchLike(String searchParameter,BasePageEntity basePageEntity){
        //如果查询条件不为空则根据条件查询
        if(null != searchParameter && !"".equals(searchParameter)){
            return conditionIsNotNull(searchParameter);
            //如果查询条件为空则向下执行
        }else{
            return selectDataByOrgId("0",basePageEntity);
        }
    }

    /**
     *    组件搜索查询功能接口(扩展)
     */
    public Map searchLike(String searchParameter,BasePageEntity basePageEntity,String startTime,String endTime,String conferenceId){
        //如果查询条件不为空则根据条件查询
        if(null != searchParameter && !"".equals(searchParameter)){
            return conditionIsNotNull(searchParameter,startTime,endTime,conferenceId);
            //如果查询条件为空则向下执行
        }else{
            return selectDataByOrgId("0",basePageEntity,startTime,endTime,conferenceId);
        }
    }

    /**
     * 当单位人员组件的搜索条件不为空时逻辑(扩展)
     * @param searchParameter
     * @return
     */
    public Map conditionIsNotNull(String searchParameter,String startTime,String endTime,String conferenceId){
        Map attendeeIdList = new HashMap();
        if(StringUtils.isNotBlank(startTime)&& StringUtils.isNotBlank(endTime)) {
            //获取某段时间内的所有会议
            List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate = meetingReserveService.findMeetingConferenceByTimeNew(startTime, endTime);
            if (null != byMeetingRoomIdAndMeetingtakeStartDate && byMeetingRoomIdAndMeetingtakeStartDate.size() > 0) {
                for (YunmeetingConference yunmeetingConference : byMeetingRoomIdAndMeetingtakeStartDate) {
                    if (null != yunmeetingConference) {
                        String meetingId = yunmeetingConference.getId();
                        if (StringUtils.isNotBlank(meetingId)) {
                            //根据会议id获取参会人员
                            List<PersonsVo> personsVos = meetingReserveService.selectAllParticipantByMeetingId(meetingId);
                            if(null != personsVos && personsVos.size() > 0) {
                                for (PersonsVo personsVo:personsVos) {
                                    if(null != personsVo){
                                        String userId = personsVo.getUserId();
                                        if(StringUtils.isNotBlank(userId)){
                                            if(StringUtils.isNotBlank(conferenceId)){
                                                if (!attendeeIdList.containsKey(userId + "+" + meetingId)) {
                                                    Map mapp = new HashMap();
                                                    mapp.put("userId", userId);
                                                    mapp.put("conferenceId", meetingId);
                                                    attendeeIdList.put(userId + "+" + meetingId, mapp);
                                                }
                                            }else {
                                                if (!attendeeIdList.containsKey(userId)) {
                                                    attendeeIdList.put(userId,userId);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Map map = new HashMap();
        Map mmap = new HashMap();
        // List sysUserList = new ArrayList();
        List<SysUserVo1> sysUserList = new ArrayList();
        List<SysOrganizationVo> sysOrganizations1 = new ArrayList<>();
        if (null != searchParameter && !"".equals(searchParameter)) {
            searchParameter = "%" + searchParameter + "%";
            //此处需要考虑父子级关系！！！！！！！！！！！！！！！！！！！！！！
            //List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectUserLikeCondition(searchParameter);
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~查询子组织中是否含有相关字段~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectUserLikeConditionAll(searchParameter);
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~查询子组织中是否含有相关字段~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            ///////////////////////////////////////////////////////////////////////////////////////////////////
            if(null != sysOrganizations && sysOrganizations.size() > 0){
                List<SysOrganization> orgList = new ArrayList<>();
                for (SysOrganization sysOrg:sysOrganizations) {
                    String parentId = sysOrg.getParentId();
                    int num = 0;
                    for (SysOrganization sysOrg1:sysOrganizations) {
                        String orgId = sysOrg1.getId();
                        if(null != orgId && !"".equals(orgId)){
                            if(parentId.equals(orgId)){
                                num = 1;
                            }
                        }
                    }
                    if(num != 1 && !"1".equals(sysOrg.getId())){
                        orgList.add(sysOrg);
                    }
                }
                if(null != orgList && orgList.size() > 0) {
                    //判断是否含有子级组织机构
                    sysOrganizations1 = this.organizationProcessor(orgList);
                }
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////
            //List<SysUser> sysUsers = sysUserMapper.selectUserLickCondition(searchParameter);
            List<SysUser> sysUsers = sysUserMapper.selectUserLickConditionNew(searchParameter);
            //如果组织机构列表不为空时排除组织机构中模糊查询出的用户
            if (null != sysOrganizations && sysOrganizations.size() > 0) {
                if (null != sysUsers && sysUsers.size() > 0) {
                    for (SysUser sysUser : sysUsers) {
                        int number = 0;
                        if (null != sysUser) {
                            String orgId = sysUser.getOrgId();
                            for (SysOrganization sysOrganization : sysOrganizations) {
                                String organzitionId = sysOrganization.getId();
                                if(orgId.equals(organzitionId) && sysUser.getStatus() != 89 &&!orgId.equals("1")){
                                        number = 1;
                                }
                            }
                        }
                        if(number != 1){
                            SysUserVo1 sysUserVo1 = getUserInfo(sysUser,attendeeIdList,conferenceId);
                            sysUserList.add(sysUserVo1);
                        }
                    }
                }
                map.put("sysOrganizations", sysOrganizations1);
                mmap.put("list",sysUserList);
                map.put("sysUser", mmap);
            } else {
                map.put("sysOrganizations", sysOrganizations1);
                if (null != sysUsers && sysUsers.size() > 0) {
                    for (SysUser sysUser : sysUsers) {
                        if (null != sysUser) {
                            SysUserVo1 sysUserVo1 = getUserInfo(sysUser,attendeeIdList,conferenceId);
                            sysUserList.add(sysUserVo1);
                        }
                    }
                }
                mmap.put("list",sysUserList);
                map.put("sysUser", mmap);
            }
        }else {
            SysOrganization sysOrganizationnn = new SysOrganization();
            sysOrganizationnn.setParentId("0");
            List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.select(sysOrganizationnn);
            if(null != sysOrganizations2 && sysOrganizations2.size()> 0){
                sysOrganizations1 = this.organizationProcessor(sysOrganizations2);
            }
            map.put("sysOrganizations", sysOrganizations1);
            SysUser sysUser = new SysUser();
            sysUser.setOrgId("0");
            List<SysUser> sysUsers = sysUserMapper.select(sysUser);
            mmap.put("list",sysUsers);
            map.put("sysUser", mmap);
        }
        return map;
    }

    /**
     * 当单位人员组件的搜索条件不为空时逻辑
     * @param searchParameter
     * @return
     */
    public Map conditionIsNotNull(String searchParameter){
        Map map = new HashMap();
        Map mmap = new HashMap();
        List sysUserList = new ArrayList();
        List<SysOrganizationVo> sysOrganizations1 = new ArrayList<>();
        if (null != searchParameter && !"".equals(searchParameter)) {
            searchParameter = "%" + searchParameter + "%";
            //此处需要考虑父子级关系！！！！！！！！！！！！！！！！！！！！！！
            //List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectUserLikeCondition(searchParameter);
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~查询子组织中是否含有相关字段~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectUserLikeConditionAll(searchParameter);
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~查询子组织中是否含有相关字段~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            ///////////////////////////////////////////////////////////////////////////////////////////////////
            if(null != sysOrganizations && sysOrganizations.size() > 0){
                List<SysOrganization> orgList = new ArrayList<>();
                for (SysOrganization sysOrg:sysOrganizations) {
                    String parentId = sysOrg.getParentId();
                    int num = 0;
                    for (SysOrganization sysOrg1:sysOrganizations) {
                        String orgId = sysOrg1.getId();
                        if(null != orgId && !"".equals(orgId)){
                            if(parentId.equals(orgId)){
                                num = 1;
                            }
                        }
                    }
                    if(num != 1){
                        orgList.add(sysOrg);
                    }
                }
                if(null != orgList && orgList.size() > 0) {
                    //判断是否含有子级组织机构
                    sysOrganizations1 = this.organizationProcessor(orgList);
                }
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////
            //List<SysUser> sysUsers = sysUserMapper.selectUserLickCondition(searchParameter);
            List<SysUser> sysUsers = sysUserMapper.selectUserLickConditionNew(searchParameter);
            //如果组织机构列表不为空时排除组织机构中模糊查询出的用户
            if (null != sysOrganizations && sysOrganizations.size() > 0) {
                if (null != sysUsers && sysUsers.size() > 0) {
                    for (SysUser sysUser : sysUsers) {
                        int number = 0;
                        if (null != sysUser) {
                            String orgId = sysUser.getOrgId();
                            for (SysOrganization sysOrganization : sysOrganizations) {
                                String organzitionId = sysOrganization.getId();
                                if(orgId.equals(organzitionId) && sysUser.getStatus() != 0){
                                    number = 1;
                                }
                            }
                        }
                        if(number != 1){
                            String wechat = sysUser.getWechat();
                            if (StringUtils.isNotBlank(wechat)&&StringUtil.isHexNumber(wechat)) {
                                try {
                                    wechat = StringUtil.parseHexString(wechat);
                                    sysUser.setWechat(wechat);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            String photo = sysUser.getPhoto();
                            String photo1 = null;
                            if(StringUtils.isNotBlank(photo)){
                                photo1 = fileUploadService.selectTenementByFile(photo);
                            }
                            sysUser.setPhoto(photo1);
                            sysUserList.add(sysUser);
                        }
                    }
                }
                map.put("sysOrganizations", sysOrganizations1);
                mmap.put("list",sysUserList);
                map.put("sysUser", mmap);
            } else {
                map.put("sysOrganizations", sysOrganizations1);
                if (null != sysUsers && sysUsers.size() > 0) {
                    for (SysUser sysUser : sysUsers) {
                        if (null != sysUser) {
                            String wechat = sysUser.getWechat();
                            if (StringUtils.isNotBlank(wechat)&&StringUtil.isHexNumber(wechat)) {
                                try {
                                    wechat = StringUtil.parseHexString(wechat);
                                    sysUser.setWechat(wechat);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            String photo = sysUser.getPhoto();
                            String photo1 = null;
                            if(StringUtils.isNotBlank(photo)){
                                photo1 = fileUploadService.selectTenementByFile(photo);
                            }
                            sysUser.setPhoto(photo1);
                            sysUserList.add(sysUser);
                        }
                    }
                }
                mmap.put("list",sysUserList);
                map.put("sysUser", mmap);
            }
        }else {
            SysOrganization sysOrganizationnn = new SysOrganization();
            sysOrganizationnn.setParentId("0");
            List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.select(sysOrganizationnn);
            if(null != sysOrganizations2 && sysOrganizations2.size()> 0){
                sysOrganizations1 = this.organizationProcessor(sysOrganizations2);
            }
            map.put("sysOrganizations", sysOrganizations1);
            SysUser sysUser = new SysUser();
            sysUser.setOrgId("0");
            List<SysUser> sysUsers = sysUserMapper.select(sysUser);
            mmap.put("list",sysUsers);
            map.put("sysUser", mmap);
        }
        return map;
    }

    /**
     * 根据用户id和角色id判断该用户是否拥有该角色
     * @param userId
     * @param roleId
     * @return
     */
    public List<SysUserRole> getUserRoleByUserIdAndRoleId(String userId,String roleId){
        if(StringUtils.isNotBlank(userId)){
            if(StringUtils.isBlank(roleId)){
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(userId);
                sysUserRole.setRoleId("1");
                return sysUserRoleMapper.select(sysUserRole);
            } else {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(userId);
                sysUserRole.setRoleId(roleId);
                return sysUserRoleMapper.select(sysUserRole);
            }
        }
        return null;
    }

    /**
     * 添加子管理员功能
     * @param userId
     * @param sonList
     * @return
     */
    @Override
    public Integer addSonManager(String userId, List<String> sonList) {
        int num = 0;
        if(StringUtils.isNotBlank(userId) && null != sonList && sonList.size() > 0 && sonList.size() < 4){
            SysUserRole sysUserRolee = new SysUserRole();
            sysUserRolee.setRoleId("2");
            List<SysUserRole> select1 = sysUserRoleMapper.select(sysUserRolee);
            if(null != select1 && select1.size() >= 3){
                return 3;
            }
            for (String sonUserId:sonList) {
                if(StringUtils.isNotBlank(sonUserId)){
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(sonUserId);
                    sysUserRole.setRoleId("2");
                    List<SysUserRole> select = sysUserRoleMapper.select(sysUserRole);
                    SysUserRole sysUserRolee1 = new SysUserRole();
                    sysUserRolee1.setRoleId("2");
                    List<SysUserRole> select3 = sysUserRoleMapper.select(sysUserRolee1);
                    if(select3.size() < 3) {
                        if(sonUserId.equals(userId)){
                            return 2;
                        }
                        if(null == select || select.size() == 0) {
                            sysUserRole.setId(CreateUUIdUtil.Uuid());
                            sysUserRole.setCreaterId(userId);
                            sysUserRole.setCreateTime(new Date());

                            int i = sysUserRoleMapper.insertSelective(sysUserRole);
                            if (i != 1) {
                                num = 1;
                            }
                        }
                    }
                }
            }
        }
        return num;
    }

    /**
     * 判断用户是否（主、子）管理员
     * @param userId
     * @return
     */
    public boolean getRequestIdentity(String userId){
        if(StringUtils.isNotBlank(userId)) {
            List<SysUserRole> userRoleByUserIdAndRoleId = getUserRoleByUserIdAndRoleId(userId, "1");
            if(null != userRoleByUserIdAndRoleId && userRoleByUserIdAndRoleId.size() > 0){
                return true;
            }else {
                List<SysUserRole> userRoleByUserIdAndRoleId1 = getUserRoleByUserIdAndRoleId(userId, "2");
                if(null != userRoleByUserIdAndRoleId1 && userRoleByUserIdAndRoleId1.size() > 0){
                    return true;
                }else {
                    List<SysUserRole> userRoleByUserIdAndRoleId2 = getUserRoleByUserIdAndRoleId(userId, "3");
                    if(null != userRoleByUserIdAndRoleId2 && userRoleByUserIdAndRoleId2.size() > 0){
                        return  true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * 判断用户是否有操作人员权限
     * @param userId
     * @return
     */
    @Override
    public boolean isHandlePeoplePermission(String userId) {
        if(StringUtils.isNotBlank(userId)) {
            List<SysUserRole> userRoleByUserIdAndRoleId = getUserRoleByUserIdAndRoleId(userId, "1");
            if(null != userRoleByUserIdAndRoleId && userRoleByUserIdAndRoleId.size() > 0){
                return true;
            }else {
                List<SysUserRole> userRoleByUserIdAndRoleId1 = getUserRoleByUserIdAndRoleId(userId, "2");
                if(null != userRoleByUserIdAndRoleId1 && userRoleByUserIdAndRoleId1.size() > 0){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 删除子管理员功能
     * @param sonUserId
     * @return
     */
    public Integer delSonManagerBySonUserId(String sonUserId){
        Integer num = 0;
        if(StringUtils.isNotBlank(sonUserId)){
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(sonUserId);
            sysUserRole.setRoleId("2");//子管理员角色的主键id为“2”；
            int delete = sysUserRoleMapper.delete(sysUserRole);
            if(delete != 1){
                num = 1;
            }
        }
        return num;
    }

    /**
     * 移动某些人员至xx下
     * @param parentId
     * @param moveUsserIds
     * @return
     */
    public Integer usersMove(String parentId,List<String> moveUsserIds){
        boolean status;
        if(null != moveUsserIds && moveUsserIds.size() > 0) {
            if(null != parentId && !"".equals(parentId)){
                if(!"0".equals(parentId)){
                    SysOrganization sysOrganization = sysOrganizationMapper.selectByPrimaryKey(parentId);
                    if(null != sysOrganization){
                        status = getStatus(parentId,moveUsserIds);
                        if(status) {
                            //表示移动成功
                            return 1;
                        }else {
                            //表示移动失败
                            return 2;
                        }
                    }else {
                        //表示父id不合法
                        return 0;
                    }
                }
                status = getStatus(parentId,moveUsserIds);
                if(status) {
                    return 1;
                }else {
                    return 2;
                }
            }else{
                status = getStatus("0",moveUsserIds);
                if(status) {
                    return 1;
                }else {
                    return 2;
                }
            }
        }
        return 2;
    }

    /**
     * 修改某个活某些组织机构移动后的父id
     * @param parentId
     * @return
     */
    public boolean getStatus(String parentId,List<String> moveUserIds){
        boolean status = false;
        for (String userId : moveUserIds) {
            SysUser sysUser = new SysUser();
            sysUser.setId(userId);
            sysUser.setOrgId(parentId);
            int i = sysUserMapper.updateByPrimaryKeySelective(sysUser);
            if(i != 1){
                return false;
            }else {
                status = true;
            }
        }
        return status;
    }

    /**
     * 添加会议室管理员
     * @param userId
     * @param managerIds
     * @return
     */
    public Integer addBoardroomManager(String userId,List<String> managerIds){
        Integer num = 1;
        if(null != managerIds && managerIds.size() > 0) {
            for (String managerId :managerIds) {
                if(StringUtils.isNotBlank(managerId)) {
                    SysUserRole sysUserRolee = new SysUserRole();
                    sysUserRolee.setUserId(managerId);
                    sysUserRolee.setRoleId("3");
                    List<SysUserRole> select = sysUserRoleMapper.select(sysUserRolee);
                    if(null == select || select.size() <= 0){
                        SysUserRole sysUserRole = new SysUserRole();
                        sysUserRole.setId(CreateUUIdUtil.Uuid());
                        sysUserRole.setUserId(managerId);
                        sysUserRole.setCreaterId(userId);
                        sysUserRole.setCreateTime(new Date());
                        sysUserRole.setRoleId("3");
                        int i = sysUserRoleMapper.insertSelective(sysUserRole);
                        if (i != 1) {
                            return 2;
                        }
                    }
                }
            }
        }
        return num;
    }

    /**
     * 删除会议室管理员
     * @param userId
     * @param moveIds
     * @return
     */
    public Integer delBoardroomManager(String userId,List<String> moveIds){
        Integer num = 1;
        if(null != moveIds && moveIds.size() > 0) {
            for (String managerId :moveIds) {
                if(StringUtils.isNotBlank(managerId)){
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(managerId);
                    sysUserRole.setRoleId("3");
                    int i = sysUserRoleMapper.delete(sysUserRole);
                    if(i != 1){
                        return 2;
                    }
                }
            }
        }
        return num;
    }

    /**
     * 查询所有会议室管理员
     * @param userId
     * @return
     */
    public List<SysUser> selectBoardroomManager(String userId){
        //此处需要看是不是需要根据人员角色来看是不是能查看
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setRoleId("3");
        List<SysUserRole> sysUserRoleList = sysUserRoleMapper.select(sysUserRole);
        if(null != sysUserRoleList && sysUserRoleList.size() > 0){
            List<SysUser> sysUsers = new ArrayList<>();
            for (SysUserRole sysUserRolee: sysUserRoleList) {
                if(null != sysUserRolee){
                    String userIdd = sysUserRolee.getUserId();
                    if(StringUtils.isNotBlank(userIdd)){
                        SysUser sysUser = sysUserMapper.selectByPrimaryKey(userIdd);
                        if(null != sysUser){
                            String wechat = sysUser.getWechat();
                            if (StringUtils.isNotBlank(wechat)&&StringUtil.isHexNumber(wechat)) {
                                try {
                                    wechat = StringUtil.parseHexString(wechat);
                                    sysUser.setWechat(wechat);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            sysUsers.add(sysUser);
                        }
                    }
                }
            }
            return sysUsers;
        }
        return null;
    }

    /**
     * 添加会议室预订专员功能
     * @param userId
     * @param commissionerIds
     * @return
     */
    public Integer addBoardroomCommissioner(String userId,List<String> commissionerIds){
        Integer num = 1;
        if(null != commissionerIds && commissionerIds.size() > 0) {
            for (String commissionerId :commissionerIds) {
                if(StringUtils.isNotBlank(commissionerId)) {
                    SysUserRole sysUserRolee = new SysUserRole();
                    sysUserRolee.setUserId(commissionerId);
                    sysUserRolee.setRoleId("4");
                    List<SysUserRole> select = sysUserRoleMapper.select(sysUserRolee);
                    if(null == select || select.size() <= 0){
                        SysUserRole sysUserRole = new SysUserRole();
                        sysUserRole.setId(CreateUUIdUtil.Uuid());
                        sysUserRole.setUserId(commissionerId);
                        sysUserRole.setCreaterId(userId);
                        sysUserRole.setCreateTime(new Date());
                        sysUserRole.setRoleId("4");
                        int i = sysUserRoleMapper.insertSelective(sysUserRole);
                        if (i != 1) {
                            return 2;
                        }
                    }
                }
            }
        }
        return num;
    }

    /**
     * 删除会议室预订专员功能
     * @param userId
     * @param commissionerIds
     * @return
     */
    public Integer delBoardroomCommissioner(String userId,List<String> commissionerIds){
        Integer num = 1;
        if(null != commissionerIds && commissionerIds.size() > 0) {
            for (String commissionerId :commissionerIds) {
                if(StringUtils.isNotBlank(commissionerId)){
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(commissionerId);
                    sysUserRole.setRoleId("4");
                    int i = sysUserRoleMapper.delete(sysUserRole);
                    if(i != 1){
                        return 2;
                    }
                }
            }
        }
        return num;
    }

    /**
     * 查看所有会议室预订专员功能
     * @param userId
     * @return
     */
    public List<SysUser> selectBoardroomCommissioner(String userId){
        List list = new ArrayList();
        if(StringUtils.isNotBlank(userId)) {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId("4");
            List<SysUserRole> sysUserRoleList = sysUserRoleMapper.select(sysUserRole);
            if(null != sysUserRoleList && sysUserRoleList.size() > 0){
                for (SysUserRole sysUserRolee:sysUserRoleList) {
                    String userIdd = sysUserRolee.getUserId();
                    if(StringUtils.isNotBlank(userIdd)){
                        SysUser sysUser = sysUserMapper.selectByPrimaryKey(userIdd);
                        list.add(sysUser);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 获取管理设置中人员集合
     * @return
     */
    public Map selectManagementSettingPersons(){
        Map map = new HashMap();
        //获取所有的主管理员信息
        List<SysUser> mainAdministrator = haveRoleUsersByRoleId("1");
        map.put("mainAdministrator", mainAdministrator);
        List<SysUser> sonAdministrator = haveRoleUsersByRoleId("2");
        map.put("sonAdministrator", sonAdministrator);
        List<SysUser> boardroommanagers = haveRoleUsersByRoleId("3");
        map.put("boardroommanagers", boardroommanagers);
        List<SysUser> commissioners = haveRoleUsersByRoleId("4");
        map.put("commissioners", commissioners);
        List<SysUser> terminalAdministrators = haveRoleUsersByRoleId("5");
        map.put("terminalAdministrators",terminalAdministrators);
        return map;
    }

    public List<SysUser> haveRoleUsersByRoleId(String roleId){
        List<SysUser> list = new ArrayList();
        if(StringUtils.isNotBlank(roleId)){
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(roleId);
            List<SysUserRole> select = sysUserRoleMapper.select(sysUserRole);
            if(null != select && select.size() > 0){
                for (SysUserRole sur: select) {
                    if(null != sur){
                        String userId = sur.getUserId();
                        if(StringUtils.isNotBlank(userId)){
                            SysUser sysUser = sysUserMapper.selectByPrimaryKey(userId);
                            if(null != sysUser) {
                                list.add(sysUser);
                            }
                        }
                    }
                }
            }
        }
        return list;
    }
    /**
     * 根据租户id获取租户信息
     * @param tenantId
     * @return
     */
    public SaasTenantInfo selectSaasTenantInfoByTenantId(String tenantId){
        if(StringUtils.isNotBlank(tenantId)) {
            Example example = new Example(SaasTenantInfo.class);
            example.createCriteria().andEqualTo("tenantId",tenantId);
            List<SaasTenantInfo> saasTenantInfos = saasTenantInfoMapper.selectByExample(example);
            if(null != saasTenantInfos && saasTenantInfos.size() > 0){
                return saasTenantInfos.get(0);
            }
        }
        return null;
    }


    /**
     * 根据人员的状态查询用户功能（带分页功能）
     * @param userStatus
     * @param pageEntity
     * @return
     */
    public PageInfo getUsersByUserStatus(Integer userStatus,BasePageEntity pageEntity,String orgId,String seachCondition){
        if(StringUtils.isBlank(orgId)){
            orgId = "0";
        }
        //此处需要根据当前组织机构id获取该组织机构下的子级机构id集合
        ///////////////////////////////////////递归算法//////////////////////////////////////////
        List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
        JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), orgId);
        JSONArray jsonArray1 = treeMenuList1(jsonArray);
        List ll = new ArrayList();
        for (Object jsona:jsonArray1) {
            String s = jsona.toString();
            if(StringUtils.isNotBlank(s)){
                ll.add(s);
            }
        }
        ll.add(orgId);
        ////////////////////////////////////////递归算法/////////////////////////////////////////
        PageHelper.startPage(pageEntity.getCurrentPage(),pageEntity.getPageSize());
        Map map = new HashMap();
        map.put("orgIds",ll);
        if(null != userStatus && userStatus == 0){
            map.put("userStatus",null);
            map.put("notUserStatus","89");
        }else {
            map.put("userStatus", userStatus);
        }
        if(StringUtils.isNotBlank(seachCondition)){
            map.put("seachCondition","%"+seachCondition+"%");
        }else {
            map.put("seachCondition",null);
        }
        List<SysUser> sysUsers = sysUserMapper.selectUserByUserStatus(map);
        List<SysUserVo> sysU = new ArrayList<>();
        if(null != sysUsers && sysUsers.size() > 0){
            for (SysUser ssUser:sysUsers) {
                if(null != ssUser){
                    SysUserVo sUsV = sysUserToSysUserVo(ssUser,sysOrganizations2);
                    sysU.add(sUsV);
                }
            }
            return new PageInfo<>(sysU);
        }
        return null;
    }
    /**
     * 可按员工姓名、全拼、手机号、邮箱等字段进行人员搜索
     * @param parentId
     * @param seachCondition
     * @param pageEntity
     * @return
     */
    public PageInfo likeSeachUserByCondition(String parentId,String seachCondition,BasePageEntity pageEntity){
        if(StringUtils.isBlank(parentId)){
            parentId = "0";
        }
        //此处需要根据当前组织机构id获取该组织机构下的子级机构id集合
        ///////////////////////////////////////递归算法//////////////////////////////////////////
        List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
        JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), parentId);
        JSONArray jsonArray1 = treeMenuList1(jsonArray);
        List ll = new ArrayList();
        for (Object jsona:jsonArray1) {
            String s = jsona.toString();
            if(StringUtils.isNotBlank(s)){
                ll.add(s);
            }
        }
        ll.add(parentId);
        ////////////////////////////////////////递归算法/////////////////////////////////////////
        if(StringUtils.isNotBlank(seachCondition)){
            return likeSeachUsersByCondition(ll,seachCondition,pageEntity,sysOrganizations2);
        }else{
            return likeSeachUsers(ll,pageEntity,sysOrganizations2);
        }
    }

    /**
     * 根据条件查询用户功能辅助类（1）
     * @param seachCondition
     * @param pageEntity
     * @return
     */
    public PageInfo likeSeachUsersByCondition(List orgIdList,String seachCondition,BasePageEntity pageEntity,List<SysOrganization> sysOrganizations2){
        Map map = new HashMap();
        map.put("orgIds",orgIdList);
        if(StringUtils.isNotBlank(seachCondition)) {
            map.put("seachCondition", "%" + seachCondition + "%");
        }
        map.put("status",89);
        PageHelper.startPage(pageEntity.getCurrentPage(),pageEntity.getPageSize());
        List<SysUser> sysUsers = sysUserMapper.likeSeachUsersByCondition(map);
        List<SysUserVo> list = new ArrayList();
        // List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
        if(null != sysUsers && sysUsers.size() > 0 && null != sysOrganizations2 && sysOrganizations2.size() > 0) {
            for (SysUser sysU : sysUsers) {
                if (null != sysU) {
                    SysUserVo sUsV = sysUserToSysUserVo(sysU,sysOrganizations2);
                    list.add(sUsV);
                }
            }
            return new PageInfo<>(list);
        }
        return null;
    }

    /**
     * 根据条件查询用户功能辅助类（2）
     * @param pageEntity
     * @return
     */
    public PageInfo likeSeachUsers(List orgIdList,BasePageEntity pageEntity,List<SysOrganization> sysOrganizations2){
        Map map = new HashMap();
        map.put("orgIds",orgIdList);
        map.put("status",89);
        PageHelper.startPage(pageEntity.getCurrentPage(),pageEntity.getPageSize());
        List<SysUser> sysUsers = sysUserMapper.likeSeachUsersByConditionByOrgIds(map);
        List<SysUserVo> list = new ArrayList();
        //List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
        if(null != sysUsers && sysUsers.size() > 0 && null != sysOrganizations2 && sysOrganizations2.size() > 0) {
            for (SysUser sysU : sysUsers) {
                if (null != sysU) {
                    SysUserVo sUsV = sysUserToSysUserVo(sysU,sysOrganizations2);
                    list.add(sUsV);
                }
            }
            return new PageInfo<>(list);
        }
        return null;
    }


    /**
     * 获取系统所有的用户
     * @return
     */
    public Map selectSystemUsers(BasePageEntity basePageEntity){
        Map map = new HashMap();
        //此处根据用户状态查用户数量
        Map map1 = selectSystemUserNumInfo();
        if(null != map1){
            map.put("userNumInfo",map1);
        }
        PageInfo pageInfo = selectSystemUserss(basePageEntity);
        if(null != pageInfo){
            map.put("systemUsers",pageInfo);
        }
        return map;
    }

    /**
     * 获取人员数量信息
     * @return
     */
    public Map selectSystemUserNumInfo(){
        Map map = new HashMap();
        //人员状态：1.已激活、2.未激活、3.已禁用、4.未分配部门、89.离职、
        int userTotalNum = sysUserMapper.selectCount(new SysUser());
        //用户总数
        map.put("userTotalNum",userTotalNum);
        SysUser sysUser = new SysUser();
        sysUser.setStatus(1);
        int activatedNum = sysUserMapper.selectCount(sysUser);
        //已激活用户人数
        map.put("activatedNum",activatedNum);
        SysUser sysUser2 = new SysUser();
        sysUser2.setStatus(2);
        int noActivatedNum = sysUserMapper.selectCount(sysUser2);
        //未激活用户人数
        map.put("noActivatedNum",noActivatedNum);
        SysUser sysUser3 = new SysUser();
        sysUser3.setStatus(3);
        int disableNum = sysUserMapper.selectCount(sysUser3);
        //禁用用户人数
        map.put("disableNum",disableNum);
        SysUser sysUser4 = new SysUser();
        sysUser4.setStatus(4);
        int noDistributionDepartmentNum = sysUserMapper.selectCount(sysUser4);
        //未分配部门用户人数
        map.put("noDistributionDepartmentNum", noDistributionDepartmentNum);
        return map;
    }

    /**
     * 获取系统所有用户功能（带分页功能）
     * @return
     */
    public PageInfo selectSystemUserss(BasePageEntity basePageEntity){
        PageHelper.startPage(basePageEntity.getCurrentPage(),basePageEntity.getPageSize());
        List<SysUser> sysUsers = sysUserMapper.selectAll();
        if(null != sysUsers && sysUsers.size() > 0){
            return new PageInfo<>(sysUsers);
        }
        return null;
    }


    /**
     * 获取通讯录结构数据
     * @param orgId
     * @param basePageEntity
     * @return
     */
    public Map selectAddressListStructure(String orgId,BasePageEntity basePageEntity){
        return selectAddressListStructureData(orgId,basePageEntity);
    }

    public Map selectAddressListStructureData(String orgId,BasePageEntity basePageEntity){
        Map map = new HashMap();
        if(null != orgId && !"".equals(orgId)) {
            List<SysOrganizationVo> sysOrganizations1 = new ArrayList<>();
            List<SysUserVo> sUs = new ArrayList<>();
            List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
            if("0".equals(orgId)){
                //此处需要根据当前组织机构id获取该组织机构下的子级机构id集合
                ///////////////////////////////////////递归算法//////////////////////////////////////////
                /*List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();*/
                JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), orgId);
                JSONArray jsonArray1 = treeMenuList1(jsonArray);
                List ll = new ArrayList();
                for (Object jsona:jsonArray1) {
                    String s = jsona.toString();
                    if(StringUtils.isNotBlank(s)){
                        ll.add(s);
                    }
                }
                ll.add(orgId);
                ////////////////////////////////////////递归算法/////////////////////////////////////////
                PageHelper.startPage(basePageEntity.getCurrentPage(),basePageEntity.getPageSize());
                List<SysUser> sysUsers = sysUserMapper.batchQuery(ll);
                if(null != sysUsers && sysUsers.size() > 0){
                    for (SysUser sysU :sysUsers) {
                        Integer status = sysU.getStatus();
                        if(null != status && status != 89 && status != 0){
                           /* SysUserVo sUsV = sysUserToSysUserVo(sysU);*/
                            SysUserVo sUsV = sysUserToSysUserVo(sysU,sysOrganizations2);
                            sUs.add(sUsV);
                        }
                    }
                }
                map.put("sysUser", new PageInfo<>(sUs));
                //此处根据用户状态查用户数量
                Map map1 = selectUserNumInfo1(ll);
                if(null != map1){
                    map.put("userNumInfo",map1);
                }
                //该方法有排序功能
//                List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectAllSysOrganiztionsAndOrderBy(orgId);
//                if(null != sysOrganizations && sysOrganizations.size() > 0){
//                    sysOrganizations1 = this.organizationProcessor(sysOrganizations);
//                }
                List list = selectAllOrganizationAndOrderBy("0");
                //map.put("sysOrganizations", sysOrganizations1);
                map.put("sysOrganizations", list);
            }else{
                //此处需要根据当前组织机构id获取该组织机构下的子级机构id集合
                ///////////////////////////////////////递归算法//////////////////////////////////////////
               /* List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();*/
                JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), orgId);
                JSONArray jsonArray1 = treeMenuList1(jsonArray);
                System.out.println("jsonArray1 :"+jsonArray1);
                List ll = new ArrayList();
                for (Object jsona:jsonArray1) {
                    String s = jsona.toString();
                    if(StringUtils.isNotBlank(s)){
                        ll.add(s);
                    }
                }
                ll.add(orgId);
                ////////////////////////////////////////递归算法/////////////////////////////////////////
                PageHelper.startPage(basePageEntity.getCurrentPage(),basePageEntity.getPageSize());
                List<SysUser> sysUsers = sysUserMapper.batchQuery(ll);
                if(null != sysUsers && sysUsers.size() > 0){
                    for (SysUser sysU :sysUsers) {
                        Integer status = sysU.getStatus();
                        if(null != status && status != 0 && status != 89){
                           /* SysUserVo sUsV = sysUserToSysUserVo(sysU);*/
                            SysUserVo sUsV = sysUserToSysUserVo(sysU,sysOrganizations2);
                            sUs.add(sUsV);
                        }
                    }
                }
                map.put("sysUser", new PageInfo<>(sUs));
                //此处根据用户状态查用户数量
                Map map1 = selectUserNumInfo1(ll);
                if(null != map1){
                    map.put("userNumInfo",map1);
                }
                //该方法有排序功能
//                List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectAllSysOrganiztionsAndOrderBy(orgId);
//                if(null != sysOrganizations && sysOrganizations.size() > 0){
//                    sysOrganizations1 = this.organizationProcessor(sysOrganizations);
//                }
                // map.put("sysOrganizations", sysOrganizations1);
                List list = selectAllOrganizationAndOrderBy("0");
                map.put("sysOrganizations", list);
            }
        }
        return map;
    }

    public List<SysUser> selectAddressListStructure(String orgId){
        return selectAddressListStructureData(orgId);
    }

    public List<SysUser> selectAddressListStructureData(String orgId){
        List<SysUser> sysUsers1 = new ArrayList<>();
        if(null != orgId && !"".equals(orgId)) {
            List<SysUserVo> sUs = new ArrayList<>();
            List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
            if("0".equals(orgId)){
                //此处需要根据当前组织机构id获取该组织机构下的子级机构id集合
                ///////////////////////////////////////递归算法//////////////////////////////////////////
                /*List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();*/
                JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), orgId);
                JSONArray jsonArray1 = treeMenuList1(jsonArray);
                List ll = new ArrayList();
                for (Object jsona:jsonArray1) {
                    String s = jsona.toString();
                    if(StringUtils.isNotBlank(s)){
                        ll.add(s);
                    }
                }
                ll.add(orgId);
                ////////////////////////////////////////递归算法/////////////////////////////////////////
                List<SysUser> sysUsers = sysUserMapper.batchQuery(ll);
                if(null != sysUsers && sysUsers.size() > 0){
                    for (SysUser sysU :sysUsers) {
                        Integer status = sysU.getStatus();
                        if(null != status && status != 89 && status != 0){
                            sysUsers1.add(sysU);
                        }
                    }
                }
            }else{
                //此处需要根据当前组织机构id获取该组织机构下的子级机构id集合
                ///////////////////////////////////////递归算法//////////////////////////////////////////
               /* List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();*/
                JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), orgId);
                JSONArray jsonArray1 = treeMenuList1(jsonArray);
                System.out.println("jsonArray1 :"+jsonArray1);
                List ll = new ArrayList();
                for (Object jsona:jsonArray1) {
                    String s = jsona.toString();
                    if(StringUtils.isNotBlank(s)){
                        ll.add(s);
                    }
                }
                ll.add(orgId);
                ////////////////////////////////////////递归算法/////////////////////////////////////////
                List<SysUser> sysUsers = sysUserMapper.batchQuery(ll);
                if(null != sysUsers && sysUsers.size() > 0){
                    for (SysUser sysU :sysUsers) {
                        Integer status = sysU.getStatus();
                        if(null != status && status != 0 && status != 89){
                            sysUsers1.add(sysU);
                        }
                    }
                }
            }
        }
        return sysUsers1;
    }

    /**
     * SysUser转成SysUserVo
     * @param sysUser
     * @return
     */
    public SysUserVo sysUserToSysUserVo(SysUser sysUser){
        SysUserVo sUsV = new SysUserVo();
        sUsV.setId(sysUser.getId());
        sUsV.setAddress(sysUser.getAddress());
        sUsV.setBirthday(sysUser.getBirthday());
        sUsV.setCreater(sysUser.getCreater());
        sUsV.setCreateTime(sysUser.getCreateTime());
        sUsV.setDeviceToken(sysUser.getDeviceToken());
        sUsV.setDeviceType(sysUser.getDeviceType());
        sUsV.setEmail(sysUser.getEmail());
        sUsV.setModifyer(sysUser.getModifyer());
        sUsV.setModifyTime(sysUser.getModifyTime());
        String orgId = sysUser.getOrgId();
        sUsV.setOrgId(orgId);
        sUsV.setOrgName(sysUser.getOrgName());
  /*      String cataLog = null;
        if(StringUtils.isNotBlank(orgId)){
            getUserCataLog(orgId);
        }
        sUsV.setCatalog(cataLog);*/
        sUsV.setPhoneNumber(sysUser.getPhoneNumber());
        String photo = sysUser.getPhoto();
        if(StringUtils.isNotBlank(photo)){
            /*Map<String, String> photos = fileUploadService.selectFileCommon(photo);*/
            Map<String, String> photos = getUploadInfo(photo);
            if(null != photos) {
                sUsV.setPhoto(photos.get("primary"));
                sUsV.setBigPicture(photos.get("big"));
                sUsV.setInPicture(photos.get("in"));
                sUsV.setSmallPicture(photos.get("small"));
            }
        }
        sUsV.setPosition(sysUser.getPosition());
        sUsV.setReserve1(sysUser.getReserve1());
        sUsV.setReserve2(sysUser.getReserve2());
        sUsV.setReserve3(sysUser.getReserve3());
        sUsV.setSex(sysUser.getSex());
        sUsV.setStatus(sysUser.getStatus());
        sUsV.setTenantId(sysUser.getTenantId());
        sUsV.setUserName(sysUser.getUserName());
        sUsV.setUserNamePinyin(sysUser.getUserNamePinyin());
        sUsV.setUserNumber(sysUser.getUserNumber());
        String wechat = sysUser.getWechat();
        if (StringUtils.isNotBlank(wechat)&&StringUtil.isHexNumber(wechat)) {
            try {
                wechat = StringUtil.parseHexString(wechat);
                sysUser.setWechat(wechat);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        sUsV.setWechat(sysUser.getWechat());
        List<String> strings = selectUserRole(sysUser.getId());
        //此处角色需要待定（现在是按照1.0版本的功能一个用户有一个角色处理）
        if(null != strings && strings.size() > 0){
            String s = strings.get(0);
            if(StringUtils.isNotBlank(s)){
                SysRole sysRole = sysRoleMapper.selectByPrimaryKey(s);
                if(null != sysRole) {
                    sUsV.setRoleId(sysRole.getRoleId());
                    sUsV.setRoleName(sysRole.getRoleName());
                }
            }

        }
        return sUsV;
    }

    /**
     * SysUser转成SysUserVo(返回值中带有用户所在组织机构目录功能)
     * @param sysUser
     * @return
     */
    public SysUserVo sysUserToSysUserVo(SysUser sysUser,List<SysOrganization> orgList){
        SysUserVo sUsV = new SysUserVo();
        sUsV.setId(sysUser.getId());
        sUsV.setAddress(sysUser.getAddress());
        sUsV.setBirthday(sysUser.getBirthday());
        sUsV.setCreater(sysUser.getCreater());
        sUsV.setCreateTime(sysUser.getCreateTime());
        sUsV.setDeviceToken(sysUser.getDeviceToken());
        sUsV.setDeviceType(sysUser.getDeviceType());
        sUsV.setEmail(sysUser.getEmail());
        sUsV.setModifyer(sysUser.getModifyer());
        sUsV.setModifyTime(sysUser.getModifyTime());
        String orgId = sysUser.getOrgId();
        sUsV.setOrgId(orgId);
        String orgName = sysUser.getOrgName();
        sUsV.setOrgName(orgName);
        String cataLog = null;
        if(StringUtils.isNotBlank(orgId) && StringUtils.isNotBlank(orgName)){
            cataLog = getUserCataLog(orgId,orgList);
        }
        sUsV.setCatalog(cataLog);
        sUsV.setPhoneNumber(sysUser.getPhoneNumber());
        String photo = sysUser.getPhoto();
        if(StringUtils.isNotBlank(photo)){
            /*Map<String, String> photos = fileUploadService.selectFileCommon(photo);*/
            Map<String, String> photos = getUploadInfo(photo);
            if(null != photos) {
                sUsV.setPhoto(photos.get("primary"));
                sUsV.setBigPicture(photos.get("big"));
                sUsV.setInPicture(photos.get("in"));
                sUsV.setSmallPicture(photos.get("small"));
            }
        }
        sUsV.setPosition(sysUser.getPosition());
        sUsV.setReserve1(sysUser.getReserve1());
        sUsV.setReserve2(sysUser.getReserve2());
        sUsV.setReserve3(sysUser.getReserve3());
        sUsV.setSex(sysUser.getSex());
        sUsV.setStatus(sysUser.getStatus());
        sUsV.setTenantId(sysUser.getTenantId());
        sUsV.setUserName(sysUser.getUserName());
        sUsV.setUserNamePinyin(sysUser.getUserNamePinyin());
        sUsV.setUserNumber(sysUser.getUserNumber());
        String wechat = sysUser.getWechat();
        if (StringUtils.isNotBlank(wechat)&&StringUtil.isHexNumber(wechat)) {
            try {
                wechat = StringUtil.parseHexString(wechat);
                sysUser.setWechat(wechat);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        sUsV.setWechat(sysUser.getWechat());
//        List<String> strings = selectUserRole(sysUser.getId());
//        //此处角色需要待定（现在是按照1.0版本的功能一个用户有一个角色处理）
//        if(null != strings && strings.size() > 0){
//            String s = strings.get(0);
//            if(StringUtils.isNotBlank(s)){
//                SysRole sysRole = sysRoleMapper.selectByPrimaryKey(s);
//                if(null != sysRole) {
//                    sUsV.setRoleId(sysRole.getRoleId());
//                    sUsV.setRoleName(sysRole.getRoleName());
//                }
//            }
//
//        }
        String roleId = null;
        List<SysUserRole> SysUserRoles = getCurrentUserRoleIds(sysUser.getId());
        if(null != SysUserRoles && SysUserRoles.size() > 0){
            List list = new ArrayList();
            for (SysUserRole sysUserRole:SysUserRoles) {
                if(null != sysUserRole){
                    list.add(sysUserRole.getRoleId());
                }
            }
            roleId = Collections.min(list).toString();
            if(StringUtils.isNotBlank(roleId)){
                SysRole sysRole = sysRoleMapper.selectByPrimaryKey(roleId);
                if(null != sysRole) {
                    sUsV.setRoleId(sysRole.getRoleId());
                    sUsV.setRoleName(sysRole.getRoleName());
                }
            }
        }
        return sUsV;
    }

    /**
     * 获取用户所在的组织机构目录功能
     * @param orgId
     * @return
     */
    public String getUserCataLog(String orgId,List<SysOrganization> orgList){
        String cataLogName = "";
        for (Object object : orgList) {
            JSONObject jsonMenu = JSONObject.fromObject(object);
            String menuId = jsonMenu.getString("id");
            String orgNamee = jsonMenu.getString("orgName");
            if(StringUtils.isNotBlank(orgId) && !"1".equals(orgId) && !"0".equals(orgId) && orgId.equals(menuId)) {
                cataLogName += ">" +  orgNamee;
            }else if(orgId.equals(menuId)){
                cataLogName +=  orgNamee;
            }
            if(orgId.equals(menuId)) {
                String pid = jsonMenu.getString("parentId");
                if (StringUtils.isNotBlank(pid) && !"0".equals(pid)) {
                    String c_node = getUserCataLog(pid, orgList);
                    cataLogName = c_node +cataLogName;
                }
            }
        }
        return cataLogName;
    }
    //递归获取组织机构树
    public JSONArray treeMenuList(JSONArray menuList, String parentId){
        JSONArray childMenu = new JSONArray();
        for (Object object : menuList) {
            JSONObject jsonMenu = JSONObject.fromObject(object);
            String menuId = jsonMenu.getString("id");
            String pid = jsonMenu.getString("parentId");
            if (parentId.equals(pid)) {
                JSONArray c_node = treeMenuList(menuList, menuId);
                jsonMenu.put("childNode", c_node);
                childMenu.add(jsonMenu);
            }
        }
        return childMenu;
    }

    public JSONArray treeMenuList1(JSONArray menuList){
        JSONArray childMenu = new JSONArray();
        for (Object object : menuList) {
            JSONObject jsonMenu = JSONObject.fromObject(object);
            childMenu.add(jsonMenu.getString("id"));
            JSONArray c_node1 = jsonMenu.getJSONArray("childNode");
            if (null != c_node1 && c_node1.size() > 0) {
                JSONArray c_node2 = treeMenuList1(c_node1);
                childMenu.addAll(c_node2);
            }
        }
        return childMenu;
    }

    /**
     * 根据组织机构主键id和查询参数获取用户数量信息
     * @param parentId
     * @param seachCondition
     * @return
     */

    public Map selectUserNumInfo2(String parentId,String seachCondition){
        Map map = new HashMap();
        if(StringUtils.isBlank(parentId)){
            parentId = "0";
        }
        //此处需要根据当前组织机构id获取该组织机构下的子级机构id集合
        ///////////////////////////////////////递归算法//////////////////////////////////////////
        List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
        JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), parentId);
        JSONArray jsonArray1 = treeMenuList1(jsonArray);
        List ll = new ArrayList();
        for (Object jsona:jsonArray1) {
            String s = jsona.toString();
            if(StringUtils.isNotBlank(s)){
                ll.add(s);
            }
        }
        ll.add(parentId);
        ////////////////////////////////////////递归算法/////////////////////////////////////////
        Map map1 = new HashMap();
        map1.put("orgIds",ll);
        if(StringUtils.isNotBlank(seachCondition)) {
            map1.put("seachCondition", "%" + seachCondition + "%");
        }
        map1.put("userStatus",null);
        map1.put("notUserStatus",89);
        //人员状态：1.已激活、2.未激活、3.已禁用、4.未分配部门、89.离职、
        Integer userTotalNum = sysUserMapper.selectUserCountSeach(map1);
        //用户总数
        map.put("userTotalNum", userTotalNum);
        Map map2 = new HashMap();
        map2.put("orgIds",ll);
        if(StringUtils.isNotBlank(seachCondition)) {
            map2.put("seachCondition", "%" + seachCondition + "%");
        }
        map2.put("userStatus",1);
        map2.put("notUserStatus",null);
        int activatedNum = sysUserMapper.selectUserCountSeach(map2);
        //已激活用户人数
        map.put("activatedNum", activatedNum);
        Map map3 = new HashMap();
        map3.put("orgIds",ll);
        if(StringUtils.isNotBlank(seachCondition)) {
            map3.put("seachCondition", "%" + seachCondition + "%");
        }
        map3.put("userStatus",2);
        map3.put("notUserStatus",null);
        int noActivatedNum = sysUserMapper.selectUserCountSeach(map3);
        //未激活用户人数
        map.put("noActivatedNum", noActivatedNum);
        Map map4 = new HashMap();
        map4.put("orgIds",ll);
        if(StringUtils.isNotBlank(seachCondition)) {
            map4.put("seachCondition", "%" + seachCondition + "%");
        }
        map4.put("userStatus",3);
        map4.put("notUserStatus",null);
        int disableNum = sysUserMapper.selectUserCountSeach(map4);
        //禁用用户人数
        map.put("disableNum", disableNum);
        Map map5 = new HashMap();
        map5.put("orgIds",ll);
        if(StringUtils.isNotBlank(seachCondition)) {
            map5.put("seachCondition", "%" + seachCondition + "%");
        }
        map5.put("userStatus",4);
        map5.put("notUserStatus",null);
        int noDistributionDepartmentNum = sysUserMapper.selectUserCountSeach(map5);
        //未分配部门用户人数
        map.put("noDistributionDepartmentNum", noDistributionDepartmentNum);
        return map;
    }

    /**
     * 根据用户的查询条件和状态获取符合条件的总数
     * @param orgId
     * @param seachCondition
     * @param userStatus
     * @return
     */
    public Map selectUserNumInfo3(String orgId,String seachCondition,Integer userStatus){
        Map map = new HashMap();
        if(StringUtils.isBlank(orgId)){
            orgId = "0";
        }
        //此处需要根据当前组织机构id获取该组织机构下的子级机构id集合
        ///////////////////////////////////////递归算法//////////////////////////////////////////
        List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
        JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), orgId);
        JSONArray jsonArray1 = treeMenuList1(jsonArray);
        List ll = new ArrayList();
        for (Object jsona:jsonArray1) {
            String s = jsona.toString();
            if(StringUtils.isNotBlank(s)){
                ll.add(s);
            }
        }
        ll.add(orgId);
        ////////////////////////////////////////递归算法/////////////////////////////////////////
        Map map1 = new HashMap();
        map1.put("orgIds",ll);
        if(StringUtils.isNotBlank(seachCondition)) {
            map1.put("seachCondition", "%" + seachCondition + "%");
        }
        if(null != userStatus && userStatus == 0){
            map1.put("userStatus",null);
            map1.put("notUserStatus","89");
        }else {
            map1.put("userStatus", userStatus);
        }
        //人员状态：1.已激活、2.未激活、3.已禁用、4.未分配部门、89.离职、
        Integer userTotalNum = sysUserMapper.selectUserCountSeach(map1);
        //符合条件的用户总数
        map.put("userTotalNum", userTotalNum);
        return map;
    }

    /**
     * 查询当前企业下除离职和删除人员外的总人数
     * @return
     */
    public Integer selectUserTotalNum(){
        Integer userTotalNum = sysUserMapper.selectUserTotalCount();
        return userTotalNum;
    }

    @Override
    public List<SysUser> selectNotDimissionPerson() {
        Example example = new Example(SysUser.class,true,true);
        example.createCriteria().andNotEqualTo("status",89);
        List<SysUser> sysUsers = sysUserMapper.selectByExample(example);
        return sysUsers;
    }

    /**
     * 查询关注的OpenId的用户
     *
     * @param openId
     * @param isSubscribe
     * @return
     */
    @Override
    public SysUser findByOpenId(String openId, String isSubscribe) {
        Map<String,String> map=new HashMap<String,String>();
        map.put("openId",openId);
        map.put("isSubscribe",isSubscribe);
        map.put("notUserStatus","89");
        List<SysUser> sysUsers=this.sysUserMapper.findByUserMap(map);
        log.info("###sysUsers,{}",sysUsers);
        return (null!=sysUsers && sysUsers.size()>0)?sysUsers.get(0):null;
    }

    /**
     * 查询当前组织机构下的用户
     *
     * @param orgId
     * @return
     */
    @Override
    public List<SysUser> findByOrgId(String orgId) {
        Map<String,String> map=new HashMap<>();
        map.put("orgId",orgId);
        List<SysUser> userList=this.sysUserMapper.findByUserMap(map);
        return userList;
    }

    /**
     * 微信检查用户授权
     *
     * @param tenantId
     * @param openId
     * @param userId
     * @return
     */
    @Override
    public SysUser authSysUser(String tenantId, String openId, String userId) {
        Map<String,String> map=new HashMap<String,String>();
        if(StringUtils.isNotBlank(tenantId)){
            map.put("tenantId",tenantId);
        }
        if(StringUtils.isNotBlank(openId)){
            map.put("openId",openId);
        }
        if(StringUtils.isNotBlank(userId)){
            map.put("id",userId);
        }
        List<SysUser> list= this.sysUserMapper.findByUserMap(map);
        if(null!=list && list.size()>0){
            SysUser sysUser = list.get(0);
            String wechat = sysUser.getWechat();
            if (StringUtils.isNotBlank(wechat)&&StringUtil.isHexNumber(wechat)) {
                try {
                    wechat = StringUtil.parseHexString(wechat);
                    sysUser.setWechat(wechat);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return sysUser;
        }
        return null;
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
     * 获取所有的组织机构并排序
     * @return
     */
    public List selectAllOrganizationAndOrderBy(String parentId){
        if(StringUtils.isBlank(parentId)){
            parentId = "0";
        }
        List list = new ArrayList();
        //该方法有排序功能
        List<SysOrganization> sysOrganizations = sysOrganizationMapper.selectAllSysOrganiztionsAndOrderBy(parentId);
        if(null != sysOrganizations && sysOrganizations.size() > 0){
            for (SysOrganization sysOrg:sysOrganizations) {
                if(null != sysOrg){
                    list.add(sysOrg);
                    String orgId = sysOrg.getId();
                    if(StringUtils.isNotBlank(orgId)){
                        List<SysOrganization> sysOrganizations1 = sysOrganizationMapper.selectAllSysOrganiztionsAndOrderBy(orgId);
                        if(null != sysOrganizations1 && sysOrganizations1.size() > 0){
                            List<SysOrganization> list1 = selectAllOrganizationAndOrderBy(orgId);
//                            list.remove(list1);
//                            list.addAll(list1);
                            if(null != list1 && list1.size()> 0){
                                for (SysOrganization ssysOrg:list1) {
                                    if(null != ssysOrg){
                                        list.add(ssysOrg);
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        return list;
    }

    /**
     * 获取部门中个状态下的用户数量
     * @param orgId
     * @return
     */
    public Map getUserNumInfo1(String orgId){
        List<SysOrganization> sysOrganizations2 = sysOrganizationMapper.selectAll();
        JSONArray jsonArray = this.treeMenuList(JSONArray.fromObject(sysOrganizations2), orgId);
        JSONArray jsonArray1 = treeMenuList1(jsonArray);
        List ll = new ArrayList();
        for (Object jsona:jsonArray1) {
            String s = jsona.toString();
            if(StringUtils.isNotBlank(s)){
                ll.add(s);
            }
        }
        ll.add(orgId);
        //此处根据用户状态查用户数量
        return  selectUserNumInfo1(ll);
    }

    public List<SysUser> getUsersBybatchQuery(List<String> orgIds){
        if(null != orgIds && orgIds.size() > 0){
            return sysUserMapper.batchQuery(orgIds);
        }
        return null;
    }

    /**
     * 存储本地图片信息功能
     * @param userId
     * @return
     */
    public boolean saveImageUrl(String userId,Map map,String imageId){
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

    @Override
    public List<SysUser> findALL() {
        return this.sysUserMapper.getUserIds();
    }

    /**
     * 根据角色主键id获取所有的关联关系功能
     * @param roleId
     * @return
     */
    public List<SysUserRole> getTerminalAdministratorsByRoleId(String roleId){
        if(StringUtils.isNotBlank(roleId)){
            Example example = new Example(SysUserRole.class);
            example.createCriteria().andEqualTo("roleId",roleId);
            return sysUserRoleMapper.selectByExample(example);
        }
        return null;
    }

    /**
     * 添加会议显示终端管理员功能
     * @param userId
     * @param terminalAdminIdss
     * @return
     */
    public String addTerminalAdministrator(String userId,List<String> terminalAdminIdss){
        if(StringUtils.isNotBlank(userId) && null != terminalAdminIdss && terminalAdminIdss.size() > 0){
            List list = new ArrayList();
            List<SysUserRole> terminalAdministratorsByRoleId = getTerminalAdministratorsByRoleId("5");
            if(null != terminalAdministratorsByRoleId && terminalAdministratorsByRoleId.size() > 0){
                for (SysUserRole tabr:terminalAdministratorsByRoleId) {
                    if(null != tabr){
                        list.add(tabr.getUserId());
                    }
                }
            }
            for (String terminalAdminId:terminalAdminIdss) {
                if(StringUtils.isNotBlank(terminalAdminId)){
                    boolean contains = list.contains(terminalAdminId);
                    if(!contains){
                        SysUserRole sysUserRolee = new SysUserRole();
                        sysUserRolee.setCreaterId(userId);
                        sysUserRolee.setCreateTime(new Date());
                        sysUserRolee.setId(CreateUUIdUtil.Uuid());
                        sysUserRolee.setRoleId("5");
                        sysUserRolee.setUserId(terminalAdminId);
                        int i = sysUserRoleMapper.insertSelective(sysUserRolee);
                        if(i<0){
                            return "0";
                        }
                    }
                }
            }
            return "1";
        }
        return "0";
    }

    /**
     * 删除会议显示终端管理员功能
     * @param terminalAdminIds
     * @return
     */
    public String delTerminalAdministrator(List<String> terminalAdminIds){
        if(null != terminalAdminIds && terminalAdminIds.size() > 0){
            Example example = new Example(SysUserRole.class);
            example.createCriteria().andEqualTo("roleId","5").andIn("userId",terminalAdminIds);
            int i = sysUserRoleMapper.deleteByExample(example);
            if(i>0){
                return "1";
            }
        }
        return "0";
    }






}
