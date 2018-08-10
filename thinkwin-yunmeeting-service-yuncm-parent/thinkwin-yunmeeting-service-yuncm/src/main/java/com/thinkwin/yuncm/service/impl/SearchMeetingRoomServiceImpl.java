package com.thinkwin.yuncm.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.utils.Sublist;
import com.thinkwin.common.vo.meetingVo.MeetingRoomVo;
import com.thinkwin.common.vo.meetingVo.YummeetingConferenceRoomMiddleVo;
import com.thinkwin.yuncm.mapper.YummeetingConferenceRoomMiddleMapper;
import com.thinkwin.yuncm.mapper.YuncmMeetingRoomMapper;
import com.thinkwin.yuncm.mapper.YuncmRoomAreaMapper;
import com.thinkwin.yuncm.mapper.YunmeetingConferenceMapper;
import com.thinkwin.yuncm.service.SearchMeetingRoomService;
import com.thinkwin.yuncm.service.YuncmMeetingService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
  *  会议室搜索实现
  *
  *  开发人员:daipengkai
  *  创建时间:2017/7/12
  *
  */
@Service("searchMeetingRoomService")
public class SearchMeetingRoomServiceImpl implements SearchMeetingRoomService {


    @Autowired
    YuncmMeetingRoomMapper yuncmMeetingRoomMapper;

    @Autowired
    YuncmRoomAreaMapper yuncmRoomAreaMapper;

    @Autowired
    YuncmMeetingService yuncmMeetingService;

    @Autowired
    YunmeetingConferenceMapper yunmeetingConferenceMapper;
    @Autowired
    YummeetingConferenceRoomMiddleMapper yummeetingConferenceRoomMiddleMapper;

    @Autowired
    UserService userService;
    @Override
    public PageInfo<YuncmMeetingRoom> selectSearchMeetingRoom(String searchKey, BasePageEntity page) {

        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<YuncmMeetingRoom> meetingRooms = yuncmMeetingRoomMapper.selectSearchMeetingRoom(searchKey);
        if(meetingRooms.size() != 0){
            for(YuncmMeetingRoom room:meetingRooms){
                Map map = this.yuncmMeetingService.findMeetingEquipment(room);
                room.setImageUrl((String) map.get("img"));
                room.setDeviceService((String) map.get("str"));
                room.setDeviceServices((List<String>) map.get("list"));
                room.setReservationStatus(this.yuncmMeetingService.selectMeetingRoomReservation(room.getId()));
            }
        }
        return new PageInfo<>(meetingRooms);
    }

    @Override
    public PageInfo<YuncmMeetingRoom> selectSearchAreaMeetingRoom(String searchKey,String areaId, BasePageEntity page) {
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        Map map = new HashMap();
        map.put("searchKey",searchKey);
        map.put("areaId",areaId);
        List<YuncmMeetingRoom> meetingRooms = yuncmMeetingRoomMapper.selectSearchAreaMeetingRoom(map);
        if(meetingRooms.size() != 0){
            for(YuncmMeetingRoom room:meetingRooms){
                Map maps = this.yuncmMeetingService.findMeetingEquipment(room);
                room.setDeviceService((String) maps.get("str"));
                room.setDeviceServices((List<String>) maps.get("list"));
                room.setImageUrl((String) maps.get("img"));
                room.setReservationStatus(this.yuncmMeetingService.selectMeetingRoomReservation(room.getId()));
            }

        }

        return new PageInfo<>(meetingRooms);
    }
    @Override
    public PageInfo<YuncmMeetingRoom> selectSearchAreaMeetingRoomChange(String searchKey, String areaId, BasePageEntity page) {
        //获取区域名称判断当前区域是否包含关键字
        Example examples = new Example(YuncmRoomArea.class, true, true);
        examples.or().andEqualTo("id", areaId);
        List<YuncmRoomArea> areas = this.yuncmRoomAreaMapper.selectByExample(examples);
        boolean bool = false;
        for (YuncmRoomArea yun : areas) {
            bool = yun.getName().contains(searchKey);
        }
        PageInfo<YuncmMeetingRoom> info = new  PageInfo<YuncmMeetingRoom>();
        //获取区域下匹配关键字的会议室
        Map map = new HashMap();
        map.put("searchKey",searchKey);
        map.put("areaId",areaId);
        List<YuncmMeetingRoom> meetingRooms = yuncmMeetingRoomMapper.selectSearchAreaMeetingRoom(map);
        if(bool) {
            //获取会议室区域全部会议室
            Example ex = new Example(YuncmMeetingRoom.class, true, true);
            ex.setOrderByClause("sort");
            Example.Criteria criteria = ex.createCriteria();
            criteria.andEqualTo("deleteState", 0)
                    .andEqualTo("areaId", areaId);
            List<YuncmMeetingRoom> rooms = this.yuncmMeetingRoomMapper.selectByExample(ex);
            //是否都包含关键字
            if (rooms.size() != meetingRooms.size()) {
                List<YuncmMeetingRoom> roomList = new ArrayList<YuncmMeetingRoom>();
                //搜索关键字会议室是否不为空
                if(meetingRooms.size() != 0) {
                    //去除重复数据
                    for (YuncmMeetingRoom room : rooms) {
                        boolean success = false;
                        for (YuncmMeetingRoom meetingRoom : meetingRooms) {
                            if (room.getId().equals(meetingRoom.getId())) {
                                success = false;
                                break;
                            } else {
                                success = true;
                            }
                        }
                        if (success) {
                            roomList.add(room);
                        }
                    }
                }else{
                    for (YuncmMeetingRoom room : rooms) {
                        roomList.add(room);
                    }
                }
                for (YuncmMeetingRoom room : roomList) {
                    meetingRooms.add(room);
                }
            }
        }

        //分页处理
        List<YuncmMeetingRoom> list = Sublist.page(page.getCurrentPage(),page.getPageSize(), meetingRooms);
        int num = 0;
        if (meetingRooms.size() % page.getPageSize() == 0) {
            num = meetingRooms.size() / page.getPageSize();
        } else {
            num = meetingRooms.size() / page.getPageSize() + 1;
        }
        if(list.size() != 0){
            for(YuncmMeetingRoom room : list){
                Map maps = this.yuncmMeetingService.findMeetingEquipment(room);
                room.setDeviceService((String) maps.get("str"));
                room.setDeviceServices((List<String>) maps.get("list"));
                room.setImageUrl((String) maps.get("img"));
                room.setReservationStatus(this.yuncmMeetingService.selectMeetingRoomReservation(room.getId()));
            }

        }
        info.setList(list);
        info.setTotal(meetingRooms.size());
        info.setPages(num);
        info.setPageNum(page.getCurrentPage());
        info.setPageSize(page.getPageSize());
        info.setSize(list.size());
        return info;
    }

    @Override
    public List<YuncmRoomArea> selectSearchYuncmRoomArea(String searchKey) {
        List<YuncmMeetingRoom> rooms = yuncmMeetingRoomMapper.selectSearchMeetingRoom(searchKey);
        List<YuncmRoomArea> areaList=new ArrayList<YuncmRoomArea>();
        Example example=new Example(YuncmRoomArea.class,true,true);
        example.setOrderByClause("sort");
        example.or().andLike("name","%"+searchKey+"%").andEqualTo("deleteState","0");
        List<YuncmRoomArea> roomAreas = this.yuncmRoomAreaMapper.selectByExample(example);
        //查看包含区域下的会议室数量
            for (YuncmRoomArea room : roomAreas){
                 int num=0;
                  for(YuncmMeetingRoom meeting : rooms){
                      if(room.getId().equals(meeting.getAreaId())){
                          num++;
                      }
                  }
                room.setNumber(num);
                areaList.add(room);
            }
            //查看包含搜索会议室的区域
            for(YuncmMeetingRoom meeting : rooms) {
                boolean success = false;
                if(areaList.size()!=0) {
                    for (YuncmRoomArea cm : areaList) {
                        //区域会议室是否已存在，存在自身+1
                        if (!cm.getId().equals(meeting.getAreaId())) {
                            success = true;
                        } else {
                            if(roomAreas.size() != 0){
                                for (YuncmRoomArea room : roomAreas) {
                                    if (room.getId().equals(meeting.getAreaId())) {
                                        success = false;
                                    } else {
                                        success = true;
                                        break;
                                    }
                                }
                            }else{
                                success = true;
                            }
                            if (success) {
                                cm.setNumber(cm.getNumber() + 1);
                            }
                            success = false;
                            break;
                        }
                    }
                }else{
                    success = true;
                }
                  //当前区域是否存在
                 if(success){
                     Example examples = new Example(YuncmRoomArea.class, true, true);
                     examples.or().andEqualTo("id", meeting.getAreaId());
                     List<YuncmRoomArea> areas = this.yuncmRoomAreaMapper.selectByExample(examples);
                     for (YuncmRoomArea yun : areas) {
                         yun.setNumber(1);
                         areaList.add(yun);
                     }
                 }


            }
            //排序
        Collections.sort(areaList, new Comparator(){
            @Override
            public int compare(Object o1, Object o2) {
                YuncmRoomArea stu1=(YuncmRoomArea)o1;
                YuncmRoomArea stu2=(YuncmRoomArea)o2;
                if(stu1.getSort()>stu2.getSort()){
                    return 1;
                }else if(stu1.getSort()==stu2.getSort()){
                    return 0;
                }else{
                    return -1;
                }
            }
        });
        return areaList;
    }

    @Override
    public  Map<String,Object>  selectSearchYuncmRoomAreaChange(String searchKey) {
        Map<String,Object> map = new HashMap<String,Object>();
        //搜索会议室
        List<YuncmMeetingRoom> rooms = yuncmMeetingRoomMapper.selectSearchMeetingRoom(searchKey);
        List<YuncmRoomArea> areaList=new ArrayList<YuncmRoomArea>();
        //搜索包含关键字的会议室区域
        Example example=new Example(YuncmRoomArea.class,true,true);
        example.setOrderByClause("sort");
        example.or().andLike("name","%"+searchKey+"%").andEqualTo("deleteState","0");
        List<YuncmRoomArea> roomAreas = this.yuncmRoomAreaMapper.selectByExample(example);
        List<String> list = new ArrayList<>();
        //获取所有包含会议室的区域id
        for(YuncmMeetingRoom meeting : rooms) {
            list.add(meeting.getAreaId());
        }
        for (YuncmRoomArea yun : roomAreas) {
            list.add(yun.getId());
        }
        //去重区域
        Set<String> setList = new TreeSet<String>();
        setList.addAll(list);
        int num = 0;
        //合并包含区域关键字，和会议室关键字的区域
        for(String id :setList){
            boolean success = false;
            for (YuncmRoomArea yun : roomAreas) {
                 if(id.equals(yun.getId())){
                     success = true;
                     break;
                 }else{
                    success = false;
                 }
            }
            //是否包含关键字的区域
            if(success) {
                Example examples = new Example(YuncmRoomArea.class, true, true);
                examples.or().andEqualTo("id", id);
                List<YuncmRoomArea> areas = this.yuncmRoomAreaMapper.selectByExample(examples);
                for (YuncmRoomArea yun : areas) {
                    Example ex = new Example(YuncmMeetingRoom.class, true, true);
                    ex.setOrderByClause("sort");
                    Example.Criteria criteria = ex.createCriteria();
                    criteria.andEqualTo("deleteState", 0)
                            .andEqualTo("areaId", id);
                    List<YuncmMeetingRoom> meetingRooms = this.yuncmMeetingRoomMapper.selectByExample(ex);
                    yun.setNumber(meetingRooms.size());
                    num += meetingRooms.size();
                    areaList.add(yun);
                }
            }else{
                //不包含
                Example examples = new Example(YuncmRoomArea.class, true, true);
                examples.or().andEqualTo("id", id);
                List<YuncmRoomArea> areas = this.yuncmRoomAreaMapper.selectByExample(examples);
                for (YuncmRoomArea yun : areas) {
                    Map maps = new HashMap();
                    maps.put("searchKey",searchKey);
                    maps.put("areaId",id);
                    List<YuncmMeetingRoom> meetingRooms = yuncmMeetingRoomMapper.selectSearchAreaMeetingRoom(maps);
                    yun.setNumber(meetingRooms.size());
                    num += meetingRooms.size();
                    areaList.add(yun);
                }
            }
        }
        //排序
        Collections.sort(areaList, new Comparator(){
            @Override
            public int compare(Object o1, Object o2) {
                YuncmRoomArea stu1=(YuncmRoomArea)o1;
                YuncmRoomArea stu2=(YuncmRoomArea)o2;
                if(stu1.getSort()>stu2.getSort()){
                    return 1;
                }else if(stu1.getSort()==stu2.getSort()){
                    return 0;
                }else{
                    return -1;
                }
            }
        });
        map.put("list",areaList);
        map.put("num",num);
        return map;
    }




    @Override
    public PageInfo<YuncmMeetingRoom> selectAllMeetingRoom(BasePageEntity page) {

        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        Example example=new Example(YuncmMeetingRoom.class,true,true);
        example.setOrderByClause("global_sort");
        example.or().andEqualTo("deleteState",0);
        List<YuncmMeetingRoom> meetingRooms = this.yuncmMeetingRoomMapper.selectByExample(example);
        if(meetingRooms.size() != 0){
            for(YuncmMeetingRoom room:meetingRooms){
                Map map = this.yuncmMeetingService.findMeetingEquipment(room);
                room.setDeviceService((String) map.get("str"));
                room.setDeviceServices((List<String>) map.get("list"));
                room.setImageUrl((String) map.get("img"));
                room.setReservationStatus(this.yuncmMeetingService.selectMeetingRoomReservation(room.getId()));
            }
        }
        return new PageInfo<>(meetingRooms);
    }

    @Override
    public PageInfo<YuncmMeetingRoom> selectAreaMeetingRoom(String areaId, BasePageEntity page) {

        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        Example example=new Example(YuncmMeetingRoom.class,true,true);
        example.setOrderByClause("sort");
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("deleteState",0)
                      .andEqualTo("areaId",areaId);
        List<YuncmMeetingRoom> meetingRooms = this.yuncmMeetingRoomMapper.selectByExample(example);
        if(meetingRooms.size() != 0){
            for(YuncmMeetingRoom room : meetingRooms){
                Map map = this.yuncmMeetingService.findMeetingEquipment(room);
                room.setDeviceService((String) map.get("str"));
                room.setDeviceServices((List<String>) map.get("list"));
                room.setImageUrl((String) map.get("img"));
                room.setReservationStatus(this.yuncmMeetingService.selectMeetingRoomReservation(room.getId()));
            }
        }
        return new PageInfo<>(meetingRooms);
    }

    @Override
    public List<YuncmMeetingRoom> selectMeetingScreenYuncmMeetingRoom(String userId, String personNum, String deviceService, String staTime, String endTime, String areaId, String isAudit) {

       Map map = new HashedMap();
       if(StringUtils.isNotBlank(personNum)){
           map.put("number",personNum);
       }
       if(StringUtils.isNotBlank(areaId)){
           map.put("area",areaId);
       }
       if(StringUtils.isNotBlank(isAudit)){
           map.put("isAudit",isAudit);
       }
       List<YuncmMeetingRoom> rooms = this.yuncmMeetingRoomMapper.selectScreeningMeetingRoomYunmeeting(map);
       for(YuncmMeetingRoom room : rooms){
           Map maps = this.yuncmMeetingService.findMeetingEquipment(room);
           room.setDeviceService((String) maps.get("str"));
           room.setDeviceServices((List<String>) maps.get("list"));
           room.setImageUrl((String) maps.get("img"));
           room.setReservationStatus(this.yuncmMeetingService.selectMeetingRoomReservation(room.getId()));
       }

       List<YuncmMeetingRoom> meetingRooms = new ArrayList<YuncmMeetingRoom>();
       //是否根据会议时间筛选空闲会议
       if(StringUtils.isNotBlank(staTime)){
             Map map1 = new HashedMap();
             map1.put("staDate",staTime);
             map1.put("endDate",endTime);
             List<YummeetingConferenceRoomMiddle> middles = this.yummeetingConferenceRoomMiddleMapper.selectTimeYunmeetingConference(map1);
             //进行时间筛选
             for(YuncmMeetingRoom room : rooms){
                 int a = 0;
                 boolean success = true;
                 for(YummeetingConferenceRoomMiddle middle : middles){
                     if(room.getId().equals(middle.getRoomId())){
                         success = false;
                         break;
                     }else{
                         success = true;
                     }
                 }
                 if(success){
                     //进行设备筛选
                     if(StringUtils.isNotBlank(deviceService)){
                        String str [] = deviceService.split(",");
                        List<String> list = room.getDeviceServices();
                        List<String> list1 = new ArrayList<String>();
                        for(int i=0; i< str.length; i++){
                            list1.add(str[i].toString());
                        }
                        //判断两个list是否相同
                         if(list.size() >= list1.size()){
                             for(int i=0;i<list.size();i++){
                                 if (list1.contains(list.get(i))) {
                                     a++;
                                 }
                             }
                             if (a == list1.size()){
                                 //过滤停用的会议室
                                 if("2".equals(room.getState())){
                                     meetingRooms.add(room);
                                 }
                             }
                         }
                     }else{
                         //过滤停用的会议室
                         if("2".equals(room.getState())){
                             meetingRooms.add(room);
                         }
                     }
                 }
             }
       }else{
            //是否筛选设备
            if(StringUtils.isNotBlank(deviceService)){
                for (YuncmMeetingRoom room : rooms){
                    int a = 0;
                    String str [] = deviceService.split(",");
                    List<String> list = room.getDeviceServices();
                    List<String> list1 = new ArrayList<String>();
                    for(int i=0; i< str.length; i++){
                        list1.add(str[i].toString());
                    }
                    //判断两个list是否相同
                    if(list.size() >= list1.size()){
                        for(int i=0;i<list.size();i++){
                            if (list1.contains(list.get(i))) {
                                a++;
                            }
                        }
                        if (a == list1.size()){
                            //过滤停用的会议室
                            if("2".equals(room.getState())){
                                meetingRooms.add(room);
                            }
                        }
                    }
                }
            }else{
                for (YuncmMeetingRoom room : rooms){
                    //过滤停用的会议室
                    if("2".equals(room.getState())){
                        meetingRooms.add(room);
                    }
                }
            }
        }
        return meetingRooms;
    }

    @Override
    public List<YuncmMeetingRoom> selectUseFrequencyMany() {

      List<YuncmMeetingRoom> rooms = new ArrayList<>();
      List<YummeetingConferenceRoomMiddleVo> middleVos = this.yummeetingConferenceRoomMiddleMapper.selectMeetingRoomUseMany();
      for(int i = 0;i<middleVos.size();i++){
         String roomId = middleVos.get(i).getRoomId();
          YuncmMeetingRoom room = this.yuncmMeetingRoomMapper.selectByPrimaryKey(roomId);
          Map maps = this.yuncmMeetingService.findMeetingEquipment(room);
          room.setDeviceService((String) maps.get("str"));
          room.setDeviceServices((List<String>) maps.get("list"));
          room.setImageUrl((String) maps.get("img"));
          room.setReservationStatus(this.yuncmMeetingService.selectMeetingRoomReservation(room.getId()));
          //过滤停用的会议室
          if("2".equals(room.getState())){
              rooms.add(room);
          }

      }
        return rooms;
    }

    @Override
    public List<YuncmMeetingRoom> selectScreenAllMeetingRoom(String deviceService, String staTime, String endTime) {

        List<YuncmMeetingRoom> meetingRooms = new ArrayList<YuncmMeetingRoom>();
        Map map = new HashedMap();
        //获取全部会议室
        List<YuncmMeetingRoom> rooms = this.yuncmMeetingRoomMapper.selectScreeningMeetingRoomYunmeeting(map);
        //筛选设备
        if(StringUtils.isNotBlank(deviceService)){
            for (YuncmMeetingRoom room : rooms){
                Map<String,Object> maps = this.yuncmMeetingService.findMeetingEquipment(room);
                int a = 0;
                String str [] = deviceService.split(",");
                List<String> list = (List<String>) maps.get("list");
                List<String> list1 = new ArrayList<String>();
                for(int i=0; i< str.length; i++){
                    list1.add(str[i].toString());
                }
                //判断两个list是否相同
                if(list.size() >= list1.size()){
                    for(int i=0;i<list.size();i++){
                        if (list1.contains(list.get(i))) {
                            a++;
                        }
                    }
                    if (a == list1.size()){
                        //过滤停用的会议室
                        if("2".equals(room.getState())){
                            meetingRooms.add(room);
                        }
                    }
                }
            }
        }else{
            for (YuncmMeetingRoom room : rooms){
                //过滤停用的会议室
                if("2".equals(room.getState())){
                    meetingRooms.add(room);
                }
            }
        }
        //获取占用信息等
      for(YuncmMeetingRoom room : meetingRooms){
          Map maps = this.yuncmMeetingService.findMeetingEquipment(room);
          room.setDeviceService((String) maps.get("str"));
          room.setDeviceServices((List<String>) maps.get("list"));
          room.setImageUrl((String) maps.get("img"));
          room.setReservationStatus(this.yuncmMeetingService.selectMeetingRoomReservation(room.getId()));
          room = selectMeetingRoomOccupyInfo(room,staTime,endTime);
      }
        return meetingRooms;
    }

    @Override
    public List<YuncmMeetingRoom> selectClickCommonMeetingRoom(String roomId) {

        List<YuncmMeetingRoom> rooms = new ArrayList<YuncmMeetingRoom>();
       String [] str = roomId.split(",");
       List<YummeetingConferenceRoomMiddleVo> middleVos = new ArrayList<YummeetingConferenceRoomMiddleVo>();
       //获取常用会议室
       List<YummeetingConferenceRoomMiddleVo> middleVoList = this.yummeetingConferenceRoomMiddleMapper.selectMeetingRoomUseMany();
       //获取勾选的会议室
       for(YummeetingConferenceRoomMiddleVo vo : middleVoList){
           for(int i = 0; i<str.length;i++){
                if(str[i].equals(vo.getRoomId())){
                    middleVos.add(vo);
                }
           }
       }
        for(int i = 0;i<middleVos.size();i++){
            String id = middleVos.get(i).getRoomId();
            YuncmMeetingRoom room = this.yuncmMeetingRoomMapper.selectByPrimaryKey(id);
            Map maps = this.yuncmMeetingService.findMeetingEquipment(room);
            room.setDeviceService((String) maps.get("str"));
            room.setDeviceServices((List<String>) maps.get("list"));
            room.setImageUrl((String) maps.get("img"));
            room.setReservationStatus(this.yuncmMeetingService.selectMeetingRoomReservation(room.getId()));
            rooms.add(room);
       }
        return rooms;
    }


    /**
     * 查看会议室占用等信息
     * @param room
     * @param staTime
     * @param endTime
     * @return
     */
    public YuncmMeetingRoom selectMeetingRoomOccupyInfo(YuncmMeetingRoom room , String staTime, String endTime ){
       //获取当前会议室是否被占用
       boolean success = this.yuncmMeetingService.findMeetingRoomTakeInfo(room.getId(),staTime,endTime,null);
       if(success){
           room.setIsOccupy("1");
       }else{
           //未占用
           room.setIsOccupy("0");
       }
        //获取当天占用信息
        List<MeetingRoomVo> roomVos = new ArrayList<MeetingRoomVo>();
        Map map = new HashedMap();
        String str []= endTime.split(" ");
        map.put("roomId",room.getId());
        map.put("staDate",staTime);
        map.put("endDate",str[0].toString()+" 23:59");
        List<YunmeetingConference> conferenceList = this.yunmeetingConferenceMapper.selectCurrentMeetingRoomOccupyInfo(map);
        for(YunmeetingConference conference : conferenceList){
            MeetingRoomVo vo = new MeetingRoomVo();
            vo.setStaDate(conference.getTakeStartDate());
            vo.setEndDate(conference.getTakeEndDate());
            //获取创建者
            SysUser sysUser = userService.selectUserByUserId(conference.getCreaterId());
            if(sysUser != null){
                vo.setPersonName(sysUser.getUserName());
            }
            vo.setMeetingName(conference.getConferenceName());
            roomVos.add(vo);
        }
        room.setRoomVos(roomVos);
        return room;
    }


    @Override
    public List<YuncmMeetingRoom> selectH5MeetingScreenYuncmMeetingRoom(String userId, String number, String devices, String startTime, String endTime, String area, String isAudit,String content,String type) {

        Map map = new HashedMap();
        if(StringUtils.isNotBlank(number)){
            map.put("number",number);
        }
        if(StringUtils.isNotBlank(area)){
            map.put("area",area);
        }
        if(StringUtils.isNotBlank(isAudit)){
            map.put("isAudit",isAudit);
        }
        if(StringUtils.isNotBlank(content)){
            map.put("content",content);
        }
        List<YuncmMeetingRoom> rooms = this.yuncmMeetingRoomMapper.selectScreeningMeetingRoomYunmeeting(map);

        //获取占用等信息等
        for(YuncmMeetingRoom room : rooms){
            Map maps = this.yuncmMeetingService.findMeetingEquipment(room);
            room.setDeviceService((String) maps.get("str"));
            room.setDeviceServices((List<String>) maps.get("list"));
            room.setImageUrl((String) maps.get("img"));
            room.setSmallPicture((String) maps.get("small")); //小
            room.setInPicture((String) maps.get("in")); //中
            room.setBigPicture((String) maps.get("big")); //大

            room.setReservationStatus(this.yuncmMeetingService.selectMeetingRoomReservation(room.getId()));
            room = selectH5MeetingRoomOccupyInfo(room,startTime,endTime,type);
        }

        List<YuncmMeetingRoom> meetingRooms = new ArrayList<YuncmMeetingRoom>();
        //是否根据会议时间筛选空闲会议
        if(StringUtils.isNotBlank(type)){
           /* Map map1 = new HashedMap();
            map1.put("staDate",startTime);
            map1.put("endDate",endTime);
            List<YummeetingConferenceRoomMiddle> middles = this.yummeetingConferenceRoomMiddleMapper.selectTimeYunmeetingConference(map1);*/
            //进行时间筛选
            for(YuncmMeetingRoom room : rooms){
                int a = 0;
                //boolean success = true;
                boolean success = this.yuncmMeetingService.findMeetingRoomTakeInfo(room.getId(),startTime,endTime);
              /*  for(YummeetingConferenceRoomMiddle middle : middles){
                    if(room.getId().equals(middle.getRoomId())){
                        success = false;
                        break;
                    }else{
                        success = true;
                    }
                }*/
                if(!success){
                    //进行设备筛选
                    if(StringUtils.isNotBlank(devices)){
                        String str [] = devices.split(",");
                        List<String> list = room.getDeviceServices();
                        List<String> list1 = new ArrayList<String>();
                        for(int i=0; i< str.length; i++){
                            list1.add(str[i].toString());
                        }
                        //判断两个list是否相同
                        if(list.size() >= list1.size()){
                            for(int i=0;i<list.size();i++){
                                if (list1.contains(list.get(i))) {
                                    a++;
                                }
                            }
                            if (a == list1.size()){
                                //过滤停用的会议室
                                if("2".equals(room.getState())){
                                    meetingRooms.add(room);
                                }
                            }
                        }
                    }else{
                        //过滤停用的会议室
                        if("2".equals(room.getState())){
                            meetingRooms.add(room);
                        }
                    }
                }
            }
        }else{
            //是否筛选设备
            if(StringUtils.isNotBlank(devices)){
                for (YuncmMeetingRoom room : rooms){
                    int a = 0;
                    String str [] = devices.split(",");
                    List<String> list = room.getDeviceServices();
                    List<String> list1 = new ArrayList<String>();
                    for(int i=0; i< str.length; i++){
                        list1.add(str[i].toString());
                    }
                    //判断两个list是否相同
                    if(list.size() >= list1.size()){
                        for(int i=0;i<list.size();i++){
                            if (list1.contains(list.get(i))) {
                                a++;
                            }
                        }
                        if (a == list1.size()){
                            //过滤停用的会议室
                            if("2".equals(room.getState())){
                                meetingRooms.add(room);
                            }
                        }
                    }
                }
            }else{
                for (YuncmMeetingRoom room : rooms){
                    //过滤停用的会议室
                    if("2".equals(room.getState())){
                        meetingRooms.add(room);
                    }
                }
            }
        }
        return meetingRooms;

    }




    /**
     * h5查看会议室占用等信息
     * @param room
     * @param staTime
     * @param endTime
     * @return
     */
    public YuncmMeetingRoom selectH5MeetingRoomOccupyInfo(YuncmMeetingRoom room , String staTime, String endTime,String type){

        List<MeetingRoomVo> roomVos = new ArrayList<MeetingRoomVo>();
        if(!StringUtils.isNotBlank(type)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date endDate = null;
            try {
                endDate = sdf.parse(endTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //获取当前会议室是否被占用
            boolean success = false;//this.yuncmMeetingService.findMeetingRoomTakeInfo(room.getId(),staTime,endTime,null);

            //获取当天占用信息
            Map map = new HashedMap();
            String str[] = endTime.split(" ");
            map.put("roomId", room.getId());
            map.put("staDate", staTime);
            map.put("endDate", str[0].toString() + " 23:59");
            List<YunmeetingConference> conferenceList = this.yunmeetingConferenceMapper.selectCurrentMeetingRoomOccupyInfo(map);
            for (YunmeetingConference conference : conferenceList) {
                MeetingRoomVo vo = new MeetingRoomVo();
                vo.setStaDate(conference.getTakeStartDate());
                vo.setEndDate(conference.getTakeEndDate());
                vo.setIsPublic(conference.getIsPublic());
                vo.setMeetingName(("0".equals(conference.getIsPublic())?"非公开会议":conference.getConferenceName()));
                //获取创建者
                SysUser sysUser = userService.selectUserByUserId(conference.getCreaterId());
                if (sysUser != null) {
                    vo.setPersonName(sysUser.getUserName());
                    vo.setPhone(sysUser.getPhoneNumber());
                }

                roomVos.add(vo);
            }
            for (int i = 0; i < conferenceList.size(); i++) {
                if (i == 0) {
                    if (new Date().getTime() > conferenceList.get(i).getTakeStartDate().getTime() && new Date().getTime() < conferenceList.get(i).getTakeEndDate().getTime()) {
                        success = true;
                        break;
                    }
                }
            }
            if (success) {
                room.setIsOccupy("1");
            } else {
                //未占用
                room.setIsOccupy("0");
            }
            room.setRoomVos(roomVos);
        }else{
            room.setIsOccupy("0");
            room.setRoomVos(roomVos);
        }
        return room;
    }



    /**
     * 获取会议室数量
     * @return
     */
    public List<YuncmMeetingRoom> selectMeetingRoomNum(){
        Example example = new Example(YuncmMeetingRoom.class);
        example.createCriteria().andNotEqualTo("deleteState",1);
        return yuncmMeetingRoomMapper.selectByExample(example);
    }










}
