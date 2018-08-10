package com.thinkwin.yuncm.service.impl;

import com.github.pagehelper.PageHelper;
import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.model.db.YunmeetingDynamics;
import com.thinkwin.common.model.db.YunmeetingDynamicsClickRecord;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.vo.meetingVo.DynamicVo;
import com.thinkwin.common.vo.meetingVo.MeetingParticipantsVo;
import com.thinkwin.common.vo.meetingVo.PersonsVo;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.localservice.LocalMeetingReserveService;
import com.thinkwin.yuncm.mapper.YunmeetingDynamicsClickRecordMapper;
import com.thinkwin.yuncm.mapper.YunmeetingDynamicsMapper;
import com.thinkwin.yuncm.service.MeetingDynamicService;
import com.thinkwin.yuncm.service.MeetingReserveService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * 类名: MeetingDynamicService </br>
 * 描述: 会议动态</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/9 </br>
 */
@Service("meetingDynamicService")
public class MeetingDynamicServiceImpl implements MeetingDynamicService {

    @Resource
    YunmeetingDynamicsMapper yunmeetingDynamicsMapper;
    @Resource
    UserService userService;
    @Resource
    LocalMeetingReserveService localMeetingReserveService;
    @Resource
    FileUploadService fileUploadService;
    @Resource
    YunmeetingDynamicsClickRecordMapper yunmeetingDynamicsClickRecordMapper;
    @Resource
    MeetingReserveService meetingReserveService;

    @Override
    public List<DynamicVo> selectRecentMeetingDynamic(Map map) {
        List<DynamicVo> dynamicVos = new ArrayList<>();
        List<YunmeetingDynamics> yunmeetingDynamics = new ArrayList<>();
        /*获取用户Id*/
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId1 = userInfo.getUserId();
        map.put("userId", userId1);
        if (null != map.get("search")) {
            String search = (String) map.get("search");
            map.put("search","%" + search + "%");
            yunmeetingDynamics = yunmeetingDynamicsMapper.selectMeetingDynamicSearch(map);
        } else {
            yunmeetingDynamics = yunmeetingDynamicsMapper.selectMeetingDynamicByTime(map);
        }
        if (yunmeetingDynamics.size() > 0) {
            for (YunmeetingDynamics yunmeetingDynamic : yunmeetingDynamics) {
                DynamicVo dynamicVo = new DynamicVo();
                String userId = yunmeetingDynamic.getCreateId();
                if (StringUtils.isNotBlank(userId)) {
                    //根据用户Id查询用户头像和昵称
                    SysUser sysUser = userService.selectUserByUserId(userId);
                    if (null != sysUser) {
                        dynamicVo.setName(sysUser.getUserName());
                        String photo = sysUser.getPhoto();
                        if(StringUtils.isNotBlank(photo)){
                           // Map<String, String> photos = fileUploadService.selectFileCommon(photo);
//                            Map<String, String> photos = this.fileUploadService.selectFileCommon(photo);
                            Map<String, String> photos = userService.getUploadInfo(photo);
                            if(null != photos) {
                                dynamicVo.setPhoto(photos.get("primary"));
                                dynamicVo.setBigPicture(photos.get("big"));
                                dynamicVo.setSmallPicture(photos.get("small"));
                                dynamicVo.setInPicture(photos.get("in"));
                            }
                        }
                        dynamicVo.setUserId(userId);
                    }
                }
                dynamicVo.setMessage(yunmeetingDynamic.getContent());
                dynamicVo.setId(yunmeetingDynamic.getId());
                dynamicVo.setSys(yunmeetingDynamic.getDynamicsType());
                //判断会议动态是否在七天以后
                boolean b = timeJudge(yunmeetingDynamic.getCreateTime());
                dynamicVo.setDateState("0");
                if(b){
                    dynamicVo.setDateState("1");
                }
                dynamicVo.setTimeago(yunmeetingDynamic.getCreateTime().getTime());
                dynamicVos.add(dynamicVo);
            }
            return dynamicVos;
        }
        return null;
    }

    @Override
    public boolean insertMeetingDynamic(YunmeetingDynamics yunmeetingDynamics, String dynamicType,String userId) {
        yunmeetingDynamics.setCreateTime(new Date());
        yunmeetingDynamics.setCreateId(userId);
        yunmeetingDynamics.setId(CreateUUIdUtil.Uuid());
        yunmeetingDynamics.setDeleteState("0");
        yunmeetingDynamics.setDynamicsType(dynamicType);
        int i = yunmeetingDynamicsMapper.insertSelective(yunmeetingDynamics);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateMeetingDynamic(YunmeetingDynamics yunmeetingDynamics) {
        int i = yunmeetingDynamicsMapper.updateByPrimaryKeySelective(yunmeetingDynamics);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public YunmeetingDynamics selectMeetingDynamicById(String dynamicId) {
        YunmeetingDynamics yunmeetingDynamics = yunmeetingDynamicsMapper.selectByPrimaryKey(dynamicId);
        if (null != yunmeetingDynamics) {
            return yunmeetingDynamics;
        }
        return null;
    }

    @Override
    public List<YunmeetingDynamics> selectMeetingDynamic(YunmeetingDynamics yunmeetingDynamics) {
        List<YunmeetingDynamics> select = yunmeetingDynamicsMapper.select(yunmeetingDynamics);
        if (select.size() > 0) {
            return select;
        }
        return null;
    }

    @Override
    public List<YunmeetingDynamics> selectMeetingDynamic(String meetingId) {
        Example example = new Example(YunmeetingDynamics.class, true, true);
        example.or().andEqualTo("conferenceId", meetingId);
        example.or().andEqualTo("deleteState", "0");
        example.setOrderByClause("create_time DESC");
        List<YunmeetingDynamics> yunmeetingDynamics = yunmeetingDynamicsMapper.selectByExample(example);
        if (yunmeetingDynamics.size() > 0) {
            return yunmeetingDynamics;
        }
        return null;
    }

    @Override
    public List<String> selectMeetingDynamic(BasePageEntity pageEntity, Map<String, Object> map) {
        String all = (String) map.get("all");
        if (StringUtils.isNotBlank(all)) {
            PageHelper.startPage(pageEntity.getCurrentPage(), pageEntity.getPageSize());
        }
        /*获取用户Id*/
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        map.put("userId", userId);
        List<YunmeetingDynamics> yunmeetingDynamics = yunmeetingDynamicsMapper.selectDynaicOrderBy(map);
        if (yunmeetingDynamics.size() > 0) {
            List<String> list = new ArrayList<>();
            for (YunmeetingDynamics yunmeetingDynamic : yunmeetingDynamics) {
                list.add(yunmeetingDynamic.getConferenceId());
            }
            return list;
        }
        return null;
    }

    @Override
    public List<MeetingParticipantsVo> insertMeetingDynamicByMeetingId(Map<String, Object> map) {
        List<MeetingParticipantsVo> userIds = new ArrayList<>();
        String meetingId = (String) map.get("meetingId");
        String content = (String) map.get("content");
        String dynamicType = (String) map.get("dynamicType");
        String currentUserId = (String) map.get("userId");
        if(StringUtils.isBlank(dynamicType)){
            dynamicType = "1";
        }
        if (StringUtils.isBlank(meetingId) || StringUtils.isBlank(content)) {
            return userIds;
        }
        //获取所有参会人员信息根据会议Id
        List<PersonsVo> list = localMeetingReserveService.selectAllParticipantByMeetingId(meetingId);
        YunmeetingConference userParticipantsStatus = meetingReserveService.findUserParticipantsStatus(map);
        YunmeetingConference yunmeetingConference = new YunmeetingConference();
        if(null==userParticipantsStatus){
            yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
            PersonsVo personsVo = new PersonsVo();
            personsVo.setUserId(yunmeetingConference.getReservationPersonId());
            list.add(personsVo);
        }
        for (PersonsVo personsVo : list) {
            String userId = personsVo.getUserId();
            YunmeetingDynamics yunmeetingDynamics = new YunmeetingDynamics();
            yunmeetingDynamics.setParticipantsId(userId);
            yunmeetingDynamics.setConferenceId(meetingId);
            yunmeetingDynamics.setContent(content);
            insertMeetingDynamic(yunmeetingDynamics, dynamicType,currentUserId);
            if(null==userParticipantsStatus){
                if(yunmeetingConference.getReservationPersonId().equals(userId)){
                    continue;
                }
            }
            MeetingParticipantsVo meetingParticipantsVo = new MeetingParticipantsVo();
            meetingParticipantsVo.setId(userId);
            userIds.add(meetingParticipantsVo);
        }
        return userIds;
    }

    @Override
    public YunmeetingDynamicsClickRecord selectDynamicClickInfo(String dynamicId, String userId) {
        Example example = new Example(YunmeetingDynamicsClickRecord.class,true,true);
        example.createCriteria().andEqualTo("participantsId",userId).andEqualTo("dynamicsId",dynamicId);
        List<YunmeetingDynamicsClickRecord> select = yunmeetingDynamicsClickRecordMapper.selectByExample(example);
        if(select.size()>0){
            return  select.get(0);
        }
        return null;
    }

    @Override
    public boolean insertDynamicClickInfo(YunmeetingDynamicsClickRecord yunmeetingDynamicsClickRecord) {
        int i = yunmeetingDynamicsClickRecordMapper.insertSelective(yunmeetingDynamicsClickRecord);
        if(i>0){
            return true;
        }
        return false;
    }

    private boolean timeJudge(Date startTime){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(Calendar.DATE,+7);
        if(new Date().getTime()>=calendar.getTime().getTime()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 根据会议id获取dynamic的创建者id集合
     * @param yunmeetingConferenceId
     * @return
     */
    public Integer selectMeetingDynamicCreateIdsByMeetingId(String yunmeetingConferenceId,String userId){
        Map map = new HashMap();
        map.put("yunmeetingConferenceId",yunmeetingConferenceId);
        map.put("userId",userId);
        return yunmeetingDynamicsMapper.selectMeetingDynamicCreateIdsByMeetingId(map);
    }
}
