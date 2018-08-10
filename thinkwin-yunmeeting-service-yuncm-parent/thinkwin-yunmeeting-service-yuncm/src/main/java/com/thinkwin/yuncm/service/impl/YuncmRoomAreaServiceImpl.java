package com.thinkwin.yuncm.service.impl;

import com.thinkwin.common.model.db.*;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.yuncm.mapper.*;
import com.thinkwin.yuncm.service.YuncmRoomAreaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 会议室接口实现
 * 开发人员:daipengkai
 * 创建时间:2017/7/11
 */
@Service("yuncmRoomAreaService")
public class YuncmRoomAreaServiceImpl implements YuncmRoomAreaService {

    @Autowired
    YuncmMeetingRoomMapper yuncmMeetingRoomMapper;
    @Autowired
    YunmcRoomDeviceServiceMapper yunmcRoomDeviceServiceMapper;
    @Autowired
    YuncmRoomReserveAuthMapper yuncmRoomReserveAuthMapper;
    @Autowired
    YuncmRoomReserveConfMapper yuncmRoomReserveConfMapper;
    @Autowired
    YummeetingConferenceRoomMiddleMapper yummeetingConferenceRoomMiddleMapper;
    @Autowired
    YuncmRoomAreaMapper yuncmRoomAreaMapper;
    @Autowired
    YuncmReserveConfigMapper yuncmReserveConfigMapper;
    @Autowired
    YuncmDeviceServiceMapper yuncmDeviceServiceMapper;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public boolean insertYuncmMeetingRoom(YuncmMeetingRoom room, String roleId, String userId) {
        boolean success = false;
        room.setAdminId("");//管理员ID
        room.setCreaterId(userId);//创建人ID
        room.setDeleteState("0");
        room.setState("2");
        room.setCreateTime(new Date());
        //查看区域的会议室用于排序区域排序
        Example example = new Example(YuncmMeetingRoom.class,true,true);
        example.setOrderByClause("sort");
        example.or().andEqualTo("areaId",room.getAreaId());
        List<YuncmMeetingRoom> yuncmMeetingRooms = this.yuncmMeetingRoomMapper.selectByExample(example);
        if (yuncmMeetingRooms.size() != 0) {
            room.setSort(yuncmMeetingRooms.get(yuncmMeetingRooms.size() - 1).getSort() + 1);
        } else {
            room.setSort(1);
        }
        //用于全局排序
        Example ex = new Example(YuncmMeetingRoom.class,true,true);
        ex.setOrderByClause("global_sort");
        List<YuncmMeetingRoom> rooms = this.yuncmMeetingRoomMapper.selectByExample(ex);
        if (rooms.size() != 0) {
            room.setGlobalSort(rooms.get(rooms.size() - 1).getGlobalSort() + 1);
        } else {
            room.setGlobalSort(1);
        }
        //添加会议室设备
        if (StringUtils.isNotBlank(room.getDeviceService())) {
            String str[] = room.getDeviceService().split(",");
            for (int i = 0; i < str.length; i++) {
                YunmcRoomDeviceService roomDeviceService = new YunmcRoomDeviceService();
                roomDeviceService.setId(CreateUUIdUtil.Uuid());
                roomDeviceService.setDeviceServiceId(str[i].toString());
                roomDeviceService.setMeetingRootId(room.getId());
                this.yunmcRoomDeviceServiceMapper.insert(roomDeviceService);
            }
        }
        //添加会议室预定专员
        if (StringUtils.isNotBlank(roleId)) {
            //用auth接口取角色ID存入 yuncm_reserve_config 表
            String str[] = roleId.split(",");
            for (int i = 0; i < str.length; i++) {
                Example examples = new Example(YuncmReserveConfig.class, true, true);
                if (str[i].toString().equals("1")) {
                    examples.or().andEqualTo("roleName", "会议室管理员");
                    List<YuncmReserveConfig> configs = this.yuncmReserveConfigMapper.selectByExample(examples);
                    for (YuncmReserveConfig config : configs) {
                        YuncmRoomReserveAuth auth = new YuncmRoomReserveAuth();
                        auth.setId(CreateUUIdUtil.Uuid());
                        auth.setMeetingRootId(room.getId());
                        auth.setCreaterId(userId);
                        auth.setRoleId(config.getRoleId());
                        auth.setCreateTime(new Date());
                        this.yuncmRoomReserveAuthMapper.insert(auth);
                    }
                }
                if (str[i].toString().equals("2")) {
                    examples.or().andEqualTo("roleName", "会议室预定专员");
                    List<YuncmReserveConfig> configs = this.yuncmReserveConfigMapper.selectByExample(examples);
                    for (YuncmReserveConfig config : configs) {
                        YuncmRoomReserveAuth auth = new YuncmRoomReserveAuth();
                        auth.setId(CreateUUIdUtil.Uuid());
                        auth.setCreaterId(userId);
                        auth.setMeetingRootId(room.getId());
                        auth.setRoleId(config.getRoleId());
                        auth.setCreateTime(new Date());
                        this.yuncmRoomReserveAuthMapper.insert(auth);
                    }
                }
            }
        }
        int flag = yuncmMeetingRoomMapper.insert(room);
        if (flag > 0) {
            success = true;
        }
        return success;
    }

    @Override
    public List<String> selectMeetingRoomRole(String meetingRootId) {
        Example example = new Example(YuncmRoomReserveAuth.class, true, true);
        example.or().andEqualTo("meetingRootId", meetingRootId);
        List<YuncmRoomReserveAuth> yuncmRoomReserveAuths = yuncmRoomReserveAuthMapper.selectByExample(example);
        List<String> list = new ArrayList<>();
        if (null != yuncmRoomReserveAuths && yuncmRoomReserveAuths.size() > 0) {
            for (YuncmRoomReserveAuth yuncmRoomReserveAuth : yuncmRoomReserveAuths) {
                String roleId = yuncmRoomReserveAuth.getRoleId();
                list.add(roleId);
            }
        }
        return list;
    }

    @Override
    public boolean updateYuncmMeetingRoom(String id, String name, String areaId, String persionNumber,
                                          String location, String deviceService, String isAudit,
                                          String roleId, String imageUrl, String userId) {

        boolean success = false;
        YuncmMeetingRoom meetingRoom = yuncmMeetingRoomMapper.selectByPrimaryKey(id);
        if (meetingRoom != null) {
            meetingRoom.setName(name);
            meetingRoom.setAreaId(areaId);
            meetingRoom.setPersionNumber(Integer.parseInt(persionNumber));
            meetingRoom.setLocation(location);
            meetingRoom.setIsAudit(isAudit);
            meetingRoom.setImageUrl(imageUrl);
            meetingRoom.setModifyerId(userId);//修改人ID
            meetingRoom.setModifyTime(new Date());
            Integer flag = this.yuncmMeetingRoomMapper.updateByPrimaryKey(meetingRoom);

            //添加会议室设备
            if (StringUtils.isNotBlank(deviceService)) {
                //删除上一次会议室设备
                Example example = new Example(YunmcRoomDeviceService.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("meetingRootId", id);
                List<YunmcRoomDeviceService> services = this.yunmcRoomDeviceServiceMapper.selectByExample(example);
                for (YunmcRoomDeviceService service : services) {
                    this.yunmcRoomDeviceServiceMapper.deleteByPrimaryKey(service.getId());
                }
                String str[] = deviceService.split(",");
                for (int i = 0; i < str.length; i++) {
                    YunmcRoomDeviceService roomDeviceService = new YunmcRoomDeviceService();
                    roomDeviceService.setId(CreateUUIdUtil.Uuid());
                    roomDeviceService.setDeviceServiceId(str[i].toString());
                    roomDeviceService.setMeetingRootId(meetingRoom.getId());
                    this.yunmcRoomDeviceServiceMapper.insert(roomDeviceService);
                }
            }else{
                //删除上一次会议室设备
                Example example = new Example(YunmcRoomDeviceService.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("meetingRootId", id);
                List<YunmcRoomDeviceService> services = this.yunmcRoomDeviceServiceMapper.selectByExample(example);
                for (YunmcRoomDeviceService service : services) {
                    this.yunmcRoomDeviceServiceMapper.deleteByPrimaryKey(service.getId());
                }
            }
            //添加会议室预定专员
            if (StringUtils.isNotBlank(roleId)) {
                //用auth接口取角色ID存入 yuncm_reserve_config 表,需先删除
                Example example = new Example(YuncmRoomReserveAuth.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("meetingRootId", id);
                List<YuncmRoomReserveAuth> auths = this.yuncmRoomReserveAuthMapper.selectByExample(example);
                for (YuncmRoomReserveAuth auth : auths) {
                    this.yuncmRoomReserveAuthMapper.deleteByPrimaryKey(auth.getId());
                }
                String str[] = roleId.split(",");
                for (int i = 0; i < str.length; i++) {
                    Example examples = new Example(YuncmReserveConfig.class, true, true);
                    if (str[i].toString().equals("1")) {
                        examples.or().andEqualTo("roleName", "会议室管理员");
                        List<YuncmReserveConfig> configs = this.yuncmReserveConfigMapper.selectByExample(examples);
                        for (YuncmReserveConfig config : configs) {
                            YuncmRoomReserveAuth auth = new YuncmRoomReserveAuth();
                            auth.setId(CreateUUIdUtil.Uuid());
                            auth.setMeetingRootId(meetingRoom.getId());
                            auth.setCreaterId(userId);
                            auth.setRoleId(config.getRoleId());
                            auth.setCreateTime(new Date());
                            this.yuncmRoomReserveAuthMapper.insert(auth);
                        }
                    }
                    if (str[i].toString().equals("2")) {
                        examples.or().andEqualTo("roleName", "会议室预定专员");
                        List<YuncmReserveConfig> configs = this.yuncmReserveConfigMapper.selectByExample(examples);
                        for (YuncmReserveConfig config : configs) {
                            YuncmRoomReserveAuth auth = new YuncmRoomReserveAuth();
                            auth.setId(CreateUUIdUtil.Uuid());
                            auth.setCreaterId(userId);
                            auth.setMeetingRootId(meetingRoom.getId());
                            auth.setRoleId(config.getRoleId());
                            auth.setCreateTime(new Date());
                            this.yuncmRoomReserveAuthMapper.insert(auth);
                        }
                    }
                }


            }
            if (flag > 0) {
                success = true;
            }
        }


        return success;
    }

    @Override
    public boolean updateStopYuncmMeetingRoom(String id, String startTime, String endTime,
                                              String state, String operReason, String userId) {
        boolean success = false;
        //查看会议室
        YuncmMeetingRoom yuncmMeetingRoom = this.yuncmMeetingRoomMapper.selectByPrimaryKey(id);
        if (yuncmMeetingRoom != null) {
            //临时停用
            if ("3".equals(state)) {
                yuncmMeetingRoom.setState(state);
                try {
                    yuncmMeetingRoom.setStartTime(sdf.parse(startTime));
                    yuncmMeetingRoom.setEndTime(sdf.parse(endTime));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                yuncmMeetingRoom.setOperReason(operReason);
            } else {
                //永久停用
                yuncmMeetingRoom.setState(state);
                yuncmMeetingRoom.setOperReason(operReason);
            }
            yuncmMeetingRoom.setModifyTime(new Date());
            yuncmMeetingRoom.setModifyerId(userId);
            Integer flag = this.yuncmMeetingRoomMapper.updateByPrimaryKey(yuncmMeetingRoom);
            if (flag > 0) {
                success = true;
            }
        }
        return success;
    }

    @Override
    public boolean updateStartYuncmMeetingRoom(String id, String userId) {
        boolean success = false;
        //查看会议室
        YuncmMeetingRoom yuncmMeetingRoom = this.yuncmMeetingRoomMapper.selectByPrimaryKey(id);
        if (yuncmMeetingRoom != null) {
            yuncmMeetingRoom.setState("2");
            yuncmMeetingRoom.setModifyerId(userId);
            yuncmMeetingRoom.setModifyTime(new Date());
            Integer flag = this.yuncmMeetingRoomMapper.updateByPrimaryKey(yuncmMeetingRoom);
            if (flag > 0) {
                success = true;
            }
        }
        return success;
    }

    @Override
    public YuncmRoomReserveConf updateMeetingRoomReserve(String reserveTimeStart, String reserveTimeEnd, String meetingMaximum,
                                                         String meetingMinimum, String reserveCycle, String userId,String qrDuration,String signSet) {


        List<YuncmRoomReserveConf> reserveConfs = this.yuncmRoomReserveConfMapper.selectAll();
        if (reserveConfs.size() != 0) {
            for (YuncmRoomReserveConf conf : reserveConfs) {
                try {
                    conf.setReserveTimeStart(formatter.parse(reserveTimeStart));
                    conf.setReserveTimeEnd(formatter.parse(reserveTimeEnd));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                conf.setMeetingMinimum(Integer.parseInt(meetingMinimum));
                conf.setMeetingMaximum(Integer.parseInt(meetingMaximum));
                conf.setReserveCycle(Integer.parseInt(reserveCycle));
                conf.setQrDuration(Integer.parseInt(qrDuration));
                conf.setSignSet(signSet);
                conf.setModifyerId(userId);//用户ID
                conf.setModifyTime(new Date());
                Integer flag = this.yuncmRoomReserveConfMapper.updateByPrimaryKey(conf);
                if (flag > 0) {
                    return conf;
                }
            }
        }
        return null;
    }

    @Override
    public List<YummeetingConferenceRoomMiddle> selectMCRoomMiddleByMRoomId(String meetingRoomId) {
        YummeetingConferenceRoomMiddle yCRoomMiddle = new YummeetingConferenceRoomMiddle();
        yCRoomMiddle.setRoomId(meetingRoomId);
        List<YummeetingConferenceRoomMiddle> select = this.yummeetingConferenceRoomMiddleMapper.select(yCRoomMiddle);
        if (null != select && select.size() > 0) {
            return select;
        }
        return null;
    }

    @Override
    public YummeetingConferenceRoomMiddle selectMCRoomMiddleByMId(String meetingId) {
        YummeetingConferenceRoomMiddle yCRoomMiddle = new YummeetingConferenceRoomMiddle();
        yCRoomMiddle.setConfrerenId(meetingId);
        List<YummeetingConferenceRoomMiddle> select = this.yummeetingConferenceRoomMiddleMapper.select(yCRoomMiddle);
        if (null != select && select.size() > 0) {
            return select.get(0);
        }
        return null;
    }

    @Override
    public YummeetingConferenceRoomMiddle selectMCRoomMiddleByMId(String meetingId, String roomId) {
        YummeetingConferenceRoomMiddle yCRoomMiddle = new YummeetingConferenceRoomMiddle();
        yCRoomMiddle.setRoomId(roomId);
        yCRoomMiddle.setConfrerenId(meetingId);
        List<YummeetingConferenceRoomMiddle> select = this.yummeetingConferenceRoomMiddleMapper.select(yCRoomMiddle);
        if (null != select && select.size() > 0) {
            return select.get(0);
        }
        return null;
    }

    @Override
    public boolean deleteYuncmMeetingRoom(String meetingRoomId, String deleteState) {
        int i = 0;
        if (StringUtils.isBlank(deleteState)) {

            i = this.yuncmMeetingRoomMapper.deleteByPrimaryKey(meetingRoomId);
        } else {
            YuncmMeetingRoom yuncmMeetingRoom = new YuncmMeetingRoom();
            yuncmMeetingRoom.setId(meetingRoomId);
            yuncmMeetingRoom.setDeleteState(deleteState);
            i = this.yuncmMeetingRoomMapper.updateByPrimaryKeySelective(yuncmMeetingRoom);
        }
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteMCRoomMiddleByMId(String meetingId) {
        Example example = new Example(YummeetingConferenceRoomMiddle.class, true, true);
        example.or().andEqualTo("confrerenId", meetingId);
        int i = yummeetingConferenceRoomMiddleMapper.deleteByExample(example);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteYuncmRooArea(String areaId, boolean success) {
        int i = 0;
        if (success) {
            //进行物理删除
            i = this.yuncmRoomAreaMapper.deleteByPrimaryKey(areaId);
        } else {
            //进行逻辑删除
            YuncmRoomArea area = new YuncmRoomArea();
            area.setId(areaId);
            area.setDeleteState("1");
            i = this.yuncmRoomAreaMapper.updateByPrimaryKeySelective(area);
        }
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<YuncmRoomArea> getRoomAreaAll() {
        return this.yuncmRoomAreaMapper.selectAll();
    }

    @Override
    public List<YuncmDeviceService> getDeviceAll() {
        return this.yuncmDeviceServiceMapper.selectAll();
    }

    @Override
    public YuncmRoomArea selectRoomAreaByAreaId(String areaId) {
        YuncmRoomArea yuncmRoomArea = yuncmRoomAreaMapper.selectByPrimaryKey(areaId);
        if(null!=yuncmRoomArea){
            return yuncmRoomArea;
        }
        return null;
    }

    @Override
    public YuncmRoomReserveConf selectRoomReserveConf() {
        List<YuncmRoomReserveConf> yuncmRoomReserveConfs = yuncmRoomReserveConfMapper.selectAll();
        if(null!=yuncmRoomReserveConfs&&yuncmRoomReserveConfs.size()>0){
            return yuncmRoomReserveConfs.get(0);
        }
        return null;
    }


}
