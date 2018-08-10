package com.thinkwin.yuncm.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.auth.service.UserRoleService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.yuncm.mapper.*;
import com.thinkwin.yuncm.service.YuncmMeetingService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;

/**
  *
  *  会议模块接口实现
  *  开发人员:daipengkai
  *  创建时间:2017/7/11
  *
  */
@Service("yuncmMeetingService")
public class YuncmMeetingServiceImpl implements YuncmMeetingService {


    @Autowired
    YuncmMeetingRoomMapper yuncmMeetingRoomMapper;
    @Autowired
    YuncmRoomAreaMapper yuncmRoomAreaMapper;
    @Autowired
    YuncmDeviceServiceMapper yuncmDeviceServiceMapper;
    @Autowired
    YuncmRoomReserveConfMapper yuncmRoomReserveConfMapper;
    @Autowired
    YuncmRoomReserveAuthMapper yuncmRoomReserveAuthMapper;
    @Autowired
    YuncmReserveConfigMapper yuncmReserveConfigMapper;
    @Autowired
    FileUploadService fileUploadService;
    @Autowired
    UserRoleService userRoleService;

    private final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");


    @Override
    public PageInfo<YuncmMeetingRoom> selectAllListYuncmMeetingRoom(BasePageEntity page) {

        List<YuncmMeetingRoom> yuncmMeetingRooms=new ArrayList<YuncmMeetingRoom>();
        //获取默认区域
        Example exampleArea = new Example(YuncmRoomArea.class,true,true);
        exampleArea.or().andEqualTo("deleteState","0")
        .andEqualTo("isDefault","1");
        List<YuncmRoomArea> yuncmRoomAreas = this.yuncmRoomAreaMapper.selectByExample(exampleArea);
        if(yuncmRoomAreas.size()!=0){
            PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
            //查看默认区域的会议室
            Example example = new Example(YuncmMeetingRoom.class,true,true);
            example.setOrderByClause("sort");
            example.or().andEqualTo("deleteState",0)
            .andEqualTo("areaId",yuncmRoomAreas.get(0).getId());
             yuncmMeetingRooms=yuncmMeetingRoomMapper.selectByExample(example);
             //获取每个会议室设备和预定状态
             for(YuncmMeetingRoom room : yuncmMeetingRooms){
                  Map map = findMeetingEquipment(room);
                  room.setDeviceService((String) map.get("str"));
                  room.setDeviceServices((List<String>) map.get("list"));
                  room.setImageUrl((String) map.get("img"));
                  room.setReservationStatus(selectMeetingRoomReservation(room.getId()));
             }
}
        return  new PageInfo<>(yuncmMeetingRooms);
    }

    @Override
    public YuncmMeetingRoom selectByidYuncmMeetingRoom(String id) {
        YuncmMeetingRoom yuncmMeetingRoom = this.yuncmMeetingRoomMapper.selectByPrimaryKey(id);
        if(null!=yuncmMeetingRoom) {
            Map map = findMeetingEquipment(yuncmMeetingRoom);
            yuncmMeetingRoom.setDeviceService((String) map.get("str"));
            yuncmMeetingRoom.setDeviceServices((List<String>) map.get("list"));
            yuncmMeetingRoom.setImageUrl((String) map.get("img"));
            yuncmMeetingRoom.setReservationStatus(selectMeetingRoomReservation(yuncmMeetingRoom.getId()));
            return yuncmMeetingRoom;
        }
        return null;
    }

    @Override
    public List<YuncmRoomArea> selectAllListYuncmRoomArea() {
        List<YuncmRoomArea> roomAreaList = new ArrayList<YuncmRoomArea>();
       Example example = new Example(YuncmRoomArea.class,true,true);
       example.setOrderByClause("sort");
       example.or().andEqualTo("deleteState","0");
       List<YuncmRoomArea> yuncmRoomAreas=this.yuncmRoomAreaMapper.selectByExample(example);
       //用户首次进入添加默认区域
       if(yuncmRoomAreas.size() == 0){
           YuncmRoomArea success = insertYuncmRoomArea("默认区域",null,"1");
          //创建成功
          if(success != null){
              List<YuncmRoomArea> yuncmRoomAreaList=this.yuncmRoomAreaMapper.selectByExample(example);
              for (YuncmRoomArea yuncmRoomArea:yuncmRoomAreaList){
                   yuncmRoomArea.setNumber(0);
                   roomAreaList.add(yuncmRoomArea);
              }
          }
       }else {
           //存在区域查看区域里面存在的会议室
           for (YuncmRoomArea yuncmRoomArea:yuncmRoomAreas){
               Example examples = new Example(YuncmMeetingRoom.class,true,true);
               examples.setOrderByClause("sort");
               Example.Criteria criteria=examples.createCriteria();
               criteria.andEqualTo("deleteState",0);
               criteria.andEqualTo("areaId",yuncmRoomArea.getId());
               List<YuncmMeetingRoom> yuncmMeetingRooms = yuncmMeetingRoomMapper.selectByExample(examples);
               yuncmRoomArea.setNumber(yuncmMeetingRooms.size());
               roomAreaList.add(yuncmRoomArea);
           }
       }
        return roomAreaList;
    }

    @Override
    public YuncmRoomArea selectByidYuncmRoomArea(String id) {
        return yuncmRoomAreaMapper.selectByPrimaryKey(id);
    }

    @Override
    public YuncmRoomArea insertYuncmRoomArea(String name ,String userId,String isDefault) {


        YuncmRoomArea yuncmRoomArea = new YuncmRoomArea();
        yuncmRoomArea.setId(CreateUUIdUtil.Uuid());
        yuncmRoomArea.setName(name);
        yuncmRoomArea.setCreaterId(userId);
        yuncmRoomArea.setIsDefault(isDefault);
        yuncmRoomArea.setCreateTime(new Date());
        yuncmRoomArea.setDeleteState("0");
        //查看区域数量用于排序
        Example example = new Example(YuncmRoomArea.class,true,true);
        example.setOrderByClause("sort");
        example.or().andEqualTo("deleteState","0");
        //查看排序
        List<YuncmRoomArea> yuncmRoomAreas = this.yuncmRoomAreaMapper.selectByExample(example);
        if(yuncmRoomAreas.size() != 0){
            yuncmRoomArea.setSort(yuncmRoomAreas.get(yuncmRoomAreas.size()-1).getSort()+1);
        }else{
            yuncmRoomArea.setSort(1);
        }
        Integer flag = this.yuncmRoomAreaMapper.insert(yuncmRoomArea);
        if(flag > 0){
           return yuncmRoomArea;
        }
        return null;
    }

    @Override
    public YuncmRoomArea updateYuncmRoomArea(String areaId, String name,String userId) {


        YuncmRoomArea yuncmRoomArea = this.yuncmRoomAreaMapper.selectByPrimaryKey(areaId);
        if(yuncmRoomArea != null){
            yuncmRoomArea.setName(name);
            yuncmRoomArea.setModifyerId(userId);
            yuncmRoomArea.setModifyTime(new Date());
            Integer flag = this.yuncmRoomAreaMapper.updateByPrimaryKey(yuncmRoomArea);
            if(flag > 0){
              return yuncmRoomArea;
            }
        }

        return null;
    }

    @Autowired
    private UserService userService;
    @Override
    public Map<String,Object> findMeetingEquipment(YuncmMeetingRoom room){
        Map<String,Object> map = new HashedMap();
        List<String> list = new ArrayList<String>();
        String str = "";
        String img = "";
        if(StringUtils.isNotBlank(room.getImageUrl())){
            //获取会议室图片
//            Map<String,String> strMap = this.fileUploadService.selectFileCommon(room.getImageUrl());
            Map<String, String> strMap = userService.getUploadInfo(room.getImageUrl());
            if(strMap.get("big") != null){
                room.setBigPicture(strMap.get("big"));
            }else {
                room.setBigPicture("");
            }
            if(strMap.get("in") != null){
                room.setInPicture(strMap.get("in"));
            }else {
                room.setInPicture("");
            }
            if(strMap.get("small") != null){
                room.setSmallPicture(strMap.get("small"));
            }else {
                room.setSmallPicture("");
            }
            room.setImageUrl(strMap.get("primary"));
            img = strMap.get("primary");
        }else{
            room.setBigPicture("");
            room.setInPicture("");
            room.setSmallPicture("");
            room.setImageUrl("");
            img = "";
        }
        int i=0;
        List<YuncmDeviceService> deviceServices = yuncmDeviceServiceMapper.selectYuncmDeviceService(room.getId());
        if(deviceServices.size() != 0) {
            for (YuncmDeviceService service : deviceServices) {
                list.add(service.getId());
                i++;
                if (i == deviceServices.size()) {
                    str += service.getName();
                } else {
                    str += service.getName() + "、";
                }
            }
        }else{
            str = "无";
        }
        if(!StringUtils.isNotBlank(room.getLocation())){
            room.setLocation("无");
        }
        map.put("str",str);
        map.put("list",list);
        map.put("img",img);
        map.put("in",room.getInPicture());
        map.put("small",room.getSmallPicture());
        map.put("big",room.getBigPicture());

        return map;
    }

    @Override
    public List<YuncmDeviceService> selectYuncmDeviceService() {

        List<YuncmDeviceService> deviceServices = this.yuncmDeviceServiceMapper.selectAll();

        return deviceServices;
    }

    @Override
    public List<YuncmRoomArea> selectAllYuncmRoomArea() {
        Example example = new Example(YuncmRoomArea.class,true,true);
        example.setOrderByClause("sort");
        example.or().andEqualTo("deleteState","0");
        List<YuncmRoomArea> yuncmRoomAreas = this.yuncmRoomAreaMapper.selectByExample(example);
        return yuncmRoomAreas;
    }

    @Override
    public List<YuncmMeetingRoom> selectAllMeetingRoom(String areaId) {
        Example example=new Example(YuncmMeetingRoom.class,true,true);
        example.setOrderByClause("sort");
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("deleteState",0)
                .andEqualTo("areaId",areaId);
        List<YuncmMeetingRoom> meetingRooms = this.yuncmMeetingRoomMapper.selectByExample(example);
       return meetingRooms;
    }

    @Override
    public boolean updateMeetingRoomSorting(String areaId, String nowRoomId, String purposeRoomId,String userId) {

        boolean success = false;
        boolean succ = false;
        if(Integer.parseInt(nowRoomId) < Integer.parseInt(purposeRoomId)){
            succ = true;
        }
        int num = 0;
        int flag = 0;
        List<YuncmMeetingRoom> rooms = new ArrayList<YuncmMeetingRoom>();
        //拖动的
        YuncmMeetingRoom nowMeetingRoom = null;
        //目标的
        YuncmMeetingRoom purposeMeetingRoom = null;
        if(!"0".equals(areaId)){
            //区域会议室
           Example example = new Example(YuncmMeetingRoom.class,true,true);
           example.setOrderByClause("sort");
           Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("deleteState",0)
                    .andEqualTo("areaId",areaId);
           rooms = this.yuncmMeetingRoomMapper.selectByExample(example);
            if(rooms.size() != 0){
                purposeRoomId = rooms.get(Integer.parseInt(purposeRoomId)).getId();
                nowRoomId = rooms.get(Integer.parseInt(nowRoomId)).getId();
            }
            //获取需要替换的对象
            for(YuncmMeetingRoom room : rooms){
                if(room.getId().equals(purposeRoomId)){
                    purposeMeetingRoom = room;
                }
                if(room.getId().equals(nowRoomId)){
                    nowMeetingRoom = room;
                }
            }
            //修改会议室排序
            if(nowMeetingRoom != null){
                //获取拖动位置的排序
                num = purposeMeetingRoom.getSort();
                for(YuncmMeetingRoom room : rooms){
                    //判断是从上还是从下
                    if(succ) {
                        //获取拖动之间的元素
                        if (room.getSort() > nowMeetingRoom.getSort() && room.getSort() <= purposeMeetingRoom.getSort()) {
                            room.setSort(room.getSort() - 1);
                            room.setModifyerId(userId);
                            room.setModifyTime(new Date());
                            flag = this.yuncmMeetingRoomMapper.updateByPrimaryKey(room);
                        }

                    }else{
                        //从下往上
                        if(room.getSort() >= purposeMeetingRoom.getSort() && room.getSort() < nowMeetingRoom.getSort()){
                            room.setSort(room.getSort() + 1);
                            room.setModifyerId(userId);
                            room.setModifyTime(new Date());
                            flag = this.yuncmMeetingRoomMapper.updateByPrimaryKey(room);
                        }
                    }
                }
                if(succ){
                    nowMeetingRoom.setSort(num);
                    nowMeetingRoom.setModifyerId(userId);
                    nowMeetingRoom.setModifyTime(new Date());
                    this.yuncmMeetingRoomMapper.updateByPrimaryKey(nowMeetingRoom);
                }else{
                    nowMeetingRoom.setSort(num);
                    nowMeetingRoom.setModifyerId(userId);
                    nowMeetingRoom.setModifyTime(new Date());
                    this.yuncmMeetingRoomMapper.updateByPrimaryKey(nowMeetingRoom);
                }
            }
        }else {
            //全部会议室
            Example example = new Example(YuncmMeetingRoom.class,true,true);
            example.setOrderByClause("global_sort");
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("deleteState",0);
            rooms = this.yuncmMeetingRoomMapper.selectByExample(example);
            if(rooms.size() != 0){
                //需要替换的位置
                purposeRoomId = rooms.get(Integer.parseInt(purposeRoomId)).getId();
                //拖动的位置
                nowRoomId = rooms.get(Integer.parseInt(nowRoomId)).getId();
            }
            //获取需要替换的对象
            for(YuncmMeetingRoom room : rooms){
                if(room.getId().equals(purposeRoomId)){
                    purposeMeetingRoom = room;
                }
                if(room.getId().equals(nowRoomId)){
                    nowMeetingRoom = room;
                }
            }
            //修改会议室排序
            if(nowMeetingRoom != null){
                //获取拖动位置的排序
                num = purposeMeetingRoom.getGlobalSort();
                for(YuncmMeetingRoom room : rooms){
                    //判断是从上还是从下
                    if(succ) {
                        //获取拖动之间的元素
                        if (room.getGlobalSort() > nowMeetingRoom.getGlobalSort() && room.getGlobalSort() <= purposeMeetingRoom.getGlobalSort()) {
                            room.setGlobalSort(room.getGlobalSort() - 1);
                            room.setModifyerId(userId);
                            room.setModifyTime(new Date());
                            flag = this.yuncmMeetingRoomMapper.updateByPrimaryKey(room);
                        }

                    }else{
                        //从下往上
                        if(room.getGlobalSort() >= purposeMeetingRoom.getGlobalSort() && room.getGlobalSort() < nowMeetingRoom.getGlobalSort()){
                            room.setGlobalSort(room.getGlobalSort() + 1);
                            room.setModifyerId(userId);
                            room.setModifyTime(new Date());
                            flag = this.yuncmMeetingRoomMapper.updateByPrimaryKey(room);
                        }
                    }
                }
                if(succ){
                    nowMeetingRoom.setGlobalSort(num);
                    nowMeetingRoom.setModifyerId(userId);
                    nowMeetingRoom.setModifyTime(new Date());
                    this.yuncmMeetingRoomMapper.updateByPrimaryKey(nowMeetingRoom);
                }else{
                    nowMeetingRoom.setGlobalSort(num);
                    nowMeetingRoom.setModifyerId(userId);
                    nowMeetingRoom.setModifyTime(new Date());
                    this.yuncmMeetingRoomMapper.updateByPrimaryKey(nowMeetingRoom);
                }
            }
        }
        if(flag > 0){
             success = true;
        }
        return success;
    }

    @Override
    public boolean updateMeetingRoomAreaSorting(String nowRoomId, String purposeRoomId,String userId,String moveType) {

        boolean success = false;
        boolean succ = false;
        if("1".equals(moveType)){
            succ = true;
        }
        int num = 0;
        Integer flag = 0;
        Example example = new Example(YuncmRoomArea.class,true,true);
        example.setOrderByClause("sort");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleteState","0");
        List<YuncmRoomArea> roomAreas = this.yuncmRoomAreaMapper.selectByExample(example);
        YuncmRoomArea roomArea = null;
        for(YuncmRoomArea area : roomAreas){
            if(area.getId().equals(purposeRoomId)){
                roomArea = area;
            }
        }
        if(roomArea != null) {
            num = roomArea.getSort();
            for (YuncmRoomArea area : roomAreas) {
                if(area.getId().equals(nowRoomId)){
                    area.setSort(num);
                    area.setModifyerId(userId);
                    area.setModifyTime(new Date());
                    this.yuncmRoomAreaMapper.updateByPrimaryKey(area);
                }
                if (area.getSort() >= roomArea.getSort() && !area.getId().equals(nowRoomId)) {
                    if(succ){
                        if(area.getId().equals(purposeRoomId)){
                            area.setSort(area.getSort() - 1);
                            area.setModifyerId(userId);
                            area.setModifyTime(new Date());
                            this.yuncmRoomAreaMapper.updateByPrimaryKey(area);
                        }else{
                            succ = false;
                        }
                    }
                    if(!succ) {
                        area.setSort(area.getSort() + 1);
                        area.setModifyerId(userId);
                        area.setModifyTime(new Date());
                        flag = this.yuncmRoomAreaMapper.updateByPrimaryKey(area);
                    }
                }
            }
        }
        if(flag > 0){
            success = true;
        }
        return success;
    }

    /*  @Override
      public boolean updateMeetingRoomSortingDown(String areaId, String nowRoomId, String purposeRoomId) {

          boolean success = false;
          List<YuncmMeetingRoom> rooms = new ArrayList<YuncmMeetingRoom>();
          YuncmMeetingRoom meetingRoom = null;
          int num = 0;
          int flag = 0;
          //是否是区域托动
          if(StringUtils.isNotBlank(areaId)){
              //区域会议室
              Example example = new Example(YuncmMeetingRoom.class,true,true);
              example.setOrderByClause("sort");
              Example.Criteria criteria = example.createCriteria();
              criteria.andEqualTo("deleteState",0)
                      .andEqualTo("areaId",areaId);
              rooms = this.yuncmMeetingRoomMapper.selectByExample(example);
              for(YuncmMeetingRoom room : rooms){
                  //获取需要替换的会议室
                  if(room.getId().equals(purposeRoomId)){
                      meetingRoom = room;
                  }
              }
              //修改会议室排序
              if(meetingRoom != null){
                  num = meetingRoom.getSort();
                  for(YuncmMeetingRoom room : rooms){
                      if(room.getSort() >= meetingRoom.getSort()){
                          if(room.getId().equals(nowRoomId)){
                              room.setSort(num);
                              this.yuncmMeetingRoomMapper.updateByPrimaryKey(room);
                          }else {
                              if(room.getSort() h){}


                              room.setSort(room.getSort() + 1);
                              flag = this.yuncmMeetingRoomMapper.updateByPrimaryKey(room);
                          }
                      }
                  }
              }





          }else{
              //全部会议室



          }




          return success;
      }
  */
    @Override
    public YuncmRoomReserveConf selectYuncmRoomReserveConf() {
        YuncmRoomReserveConf conf = new YuncmRoomReserveConf();
        List<YuncmRoomReserveConf> confs = this.yuncmRoomReserveConfMapper.selectAll();
        if(confs.size() != 0){
            conf=confs.get(0);
        }
        return conf;
    }

    @Override
    public String selectMeetingRoomReservation(String meetingId) {

       String str = "";
       List<YuncmReserveConfig> configs = this.yuncmReserveConfigMapper.selectYuncmReserveConfig(meetingId);
       if(configs.size()!=0) {
           boolean success = false ;
           boolean succe = false;
           for (YuncmReserveConfig config : configs){
                if("会议室管理员".equals(config.getRoleName())){
                    success = true;
                }
               if("会议室预定专员".equals(config.getRoleName())){
                   succe = true;
               }
           }


           if(success && succe){
               str = "3";
           }else{
               if(success){
                   str = "1";
               }
               if(succe){
                   str = "2";
               }
           }
       }else {
           str = "0";
       }
        return str;
    }

    @Override
    public YuncmMeetingRoom selectByYuncmMeetingRoom(String id) {
        return  this.yuncmMeetingRoomMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<YuncmMeetingRoom> selectTenantMeetingRoomCount() {
        Example example = new Example(YuncmMeetingRoom.class,true,true);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleteState","0");
        List<YuncmMeetingRoom> rooms = this.yuncmMeetingRoomMapper.selectByExample(example);
        return rooms;
    }

    /**
     * 查询某个时间段某个会议室的占用情况
     *
     * @param meetingRoomId 会议室Id
     * @param begin     开始时间
     * @param end       结束时间
     * @param state     状态
     * @return
     */
    @Override
    public Boolean  findMeetingRoomTakeInfo(String meetingRoomId, String begin, String end, String state) {
        Map<String,String> map=new HashMap<>();
        map.put("meetingRoomId",meetingRoomId);
        map.put("begin",begin+":00");
        map.put("end",end+":00");
        if(StringUtils.isNotBlank(state)){
            map.put("state",state);
        }
        List<YuncmMeetingRoom> rooms=this.yuncmMeetingRoomMapper.findMeetingRoomTakeInfo(map);
        return (null!=rooms && rooms.size()>0)?true:false;
    }
    /**
     * 查询某个时间段某个会议室的占用情况
     * 会议修改时调用
     * @return
     */
    @Override
    public Boolean  findMeetingRoomTakeInfo(Map<String,Object> map) {
        List<YuncmMeetingRoom> rooms=this.yuncmMeetingRoomMapper.findMeetingRoomTakeInfo(map);
        return (null!=rooms && rooms.size()>0)?true:false;
    }
    /**
     * 查询某个用户对某个会议室的权限
     *
     * @param meetingRoomId
     * @param userId
     * @return true:有权限 false:无权限
     */
    @Override
    public Boolean findMeetingRoomAndUser(String meetingRoomId, String userId) {
        Boolean flag=false;
        //获取人员所有角色
        List<SysRole> sysRoles=userRoleService.findUserRolesByUserId(userId);
        for(SysRole s:sysRoles){
            if(findMeetingRoomAndRole(meetingRoomId,s.getRoleId())){
                flag=true;
                return flag;
            }
        }

        return flag;
    }

    /**
     * 查询某个角色对某个会议室的权限
     *
     * @param meetingRoomId
     * @param roleId
     * @return true:有权限 false:无权限
     */
    @Override
    public Boolean findMeetingRoomAndRole(String meetingRoomId, String roleId) {
        Map<String,String> map=new HashMap<>();
        map.put("meetingRoomId",meetingRoomId);
        map.put("isAudit",null);
        map.put("roleId",roleId);
        List<YuncmMeetingRoom> rooms=this.yuncmMeetingRoomMapper.findMeetingRoomAndRole(map);
        return (null!=rooms && rooms.size()>0)?true:false;
    }

    /**
     * 判断该会议室预订权限是否全体人员
     *
     * @param meetingRoomId
     * @return
     */
    @Override
    public Boolean findMeetingRoomId(String meetingRoomId) {
        Map<String,String> map=new HashMap<>();
        map.put("meetingRoomId",meetingRoomId);
        List<YuncmRoomReserveAuth> rooms=this.yuncmRoomReserveAuthMapper.findMeetingRoomId(map);
        return (null!=rooms && rooms.size()>0)?true:false;
    }

    /**
     * 查询会议室
     *
     * @param meetingRoomId
     * @return
     */
    @Override
    public YuncmMeetingRoom findMeetingRoom(String meetingRoomId) {
        Map<String,String> map=new HashMap<>();
        map.put("meetingRoomId",meetingRoomId);
        return this.yuncmMeetingRoomMapper.findMeetingRoom(map);
    }

    @Override
    public Boolean findMeetingRoomTakeInfo(String meetingRoomId, String begin, String end) {
        Map<String,String> map=new HashMap<>();
        map.put("meetingRoomId",meetingRoomId);
        map.put("begin",begin);
        map.put("end",end);
        List<YuncmMeetingRoom> rooms=this.yuncmMeetingRoomMapper.findMeetingRoomTakeInfo(map);
        return (null!=rooms && rooms.size()>0)?true:false;
    }

    @Autowired
    private YunmeetingMeetingSignMapper yunmeetingMeetingSignMapper;
    @Autowired
    private YunmeetingParticipantsReplyMapper yunmeetingParticipantsReplyMapper;
    /**
     * 获取用户回复数量
     * @return
     */
    public Integer getUserReply(){
        int i = 0;//yunmeetingParticipantsReplyMapper.selectCount(new YunmeetingParticipantsReply());
        List<YunmeetingParticipantsReply> yunmeetingParticipantsReplies = yunmeetingParticipantsReplyMapper.selectAll();
        if(null != yunmeetingParticipantsReplies && yunmeetingParticipantsReplies.size() > 0){
            i += yunmeetingParticipantsReplies.size();
        }
        return i;
    }

    /**
     * 获取用户回复数量（根据会议id集合查询）
     * @return
     */
    public Integer getUserReply( List<String> meetingIds){
        int i = 0;
        if(null != meetingIds && meetingIds.size() > 0){
            Map map = new HashMap();
            map.put("meetingIds",meetingIds);
        List<YunmeetingParticipantsReply> yunmeetingParticipantsReplies = yunmeetingParticipantsReplyMapper.selectAllReplyByMeetingIds(map);
        if(null != yunmeetingParticipantsReplies && yunmeetingParticipantsReplies.size() > 0){
            i += yunmeetingParticipantsReplies.size();
        }
        }
        return i;
    }

    /**
     * 获取用户签到数量
     * @return
     */
    public Integer getUserSign(){
        return yunmeetingMeetingSignMapper.selectCount(new YunmeetingMeetingSign());
    }

    /**
     * 获取用户签到数量(根据会议id查询)
     * @return
     */
    public Integer getUserSign(List<String> meetingIds){
        int i = 0;
        if(null != meetingIds && meetingIds.size() > 0) {
            Map map = new HashMap();
            map.put("meetingIds",meetingIds);
            List<YunmeetingMeetingSign> yunmeetingMeetingSigns = yunmeetingMeetingSignMapper.selectAllSignByMeetingIds(map);
            if (null != yunmeetingMeetingSigns && yunmeetingMeetingSigns.size() > 0) {
                i += yunmeetingMeetingSigns.size();
            }
        }
        return i;
    }

   /* @Override
    public List<YuncmMeetingRoom> selectYuncmMeetingRoomAll() {
        return this.yuncmMeetingRoomMapper.selectAll();
    }*/
}
