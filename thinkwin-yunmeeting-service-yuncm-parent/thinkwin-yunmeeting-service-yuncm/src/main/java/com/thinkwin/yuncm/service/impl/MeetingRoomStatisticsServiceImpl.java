package com.thinkwin.yuncm.service.impl;

import com.github.pagehelper.PageHelper;
import com.sun.net.httpserver.Headers;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.YuncmMeetingRoom;
import com.thinkwin.common.model.db.YuncmRoomReserveConf;
import com.thinkwin.common.utils.Sublist;
import com.thinkwin.common.utils.TimeUtil;
import com.thinkwin.common.vo.meetingVo.*;
import com.thinkwin.yuncm.localservice.LocalMeetingReserveService;
import com.thinkwin.yuncm.mapper.YummeetingConferenceRoomMiddleMapper;
import com.thinkwin.yuncm.mapper.YuncmMeetingRoomMapper;
import com.thinkwin.yuncm.mapper.YunmeetingConferenceMapper;
import com.thinkwin.yuncm.service.MeetingReserveService;
import com.thinkwin.yuncm.service.MeetingRoomStatisticsService;
import com.thinkwin.yuncm.service.YuncmMeetingService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 会议室统计接口实现
 * 创建时间：  2017/10/24
 */
@Service("meetingRoomStatisticsService")
public class MeetingRoomStatisticsServiceImpl implements MeetingRoomStatisticsService {

    @Resource
    YuncmMeetingService yuncmMeetingService;
    @Resource
    YummeetingConferenceRoomMiddleMapper yummeetingConferenceRoomMiddleMapper;
    @Resource
    LocalMeetingReserveService localMeetingReserveService;
    @Resource
    YuncmMeetingRoomMapper yuncmMeetingRoomMapper;
    @Resource
    YunmeetingConferenceMapper yunmeetingConferenceMapper;
    @Resource
    MeetingReserveService meetingReserveService;
    @Override
    public Map<String, Object> selectMeetingRoomStatistics(String staTime, String endTime) {
        Map<String, Object> maps = new HashedMap();
        //获取统计的天数
        int days = TimeUtil.getTimeDifference(staTime,endTime);
        //查询会议室可预订时间获取会议室预定时长
        YuncmRoomReserveConf conf = localMeetingReserveService.selectYuncmRoomReserveConf();
        long reserveTime = conf.getReserveTimeEnd().getTime() - conf.getReserveTimeStart().getTime();
        //获取会议室总数量
        List<YuncmMeetingRoom> rooms = this.yuncmMeetingService.selectTenantMeetingRoomCount();
        //获取会议室预定总时长
        long totalRoomTime = reserveTime * (days + 1) * rooms.size();
        //获取时间段的总会议
        Map map = new HashMap();
        map.put("staTime",staTime);
        map.put("endTime",endTime);
        List<RoomStatisticsVo> statisticsVoList = this.yummeetingConferenceRoomMiddleMapper.selectMeetingRoomStatistics(map);
        //获取会议室总时长
        long totalTime = getMeetingTotalTime(statisticsVoList);
        //获取使用率保留一位小数
        Double tota = (double) totalTime / totalRoomTime *100;
        if(!tota.isNaN()){
            String result = String .format("%.1f",tota);
            maps.put("roomUseRate",result+"%");
        }else{
            maps.put("roomUseRate","0%");
        }

        maps.put("roomTota",rooms.size());
        //统计会议室全部会议时间等
        Map<String,Object> gmap = getMeetingStatistics(staTime,endTime,rooms);
        //首页图表数据返回
        Map<String,Object> omap = getHomePageChartInfo(staTime,endTime);
        maps.put("meettingNumber",omap.get("meettingNumber"));
        maps.put("meettingDuration",omap.get("meettingDuration"));
        maps.put("meettingTime",omap.get("meettingTime"));
        maps.put("meetingNum",gmap.get("meetingNum"));
        maps.put("meetingTotalTime",gmap.get("meetingTotalTime"));
        maps.put("signRate",gmap.get("signRate"));
        maps.put("replyRate",gmap.get("replyRate"));
        return maps;
    }



    @Override
    public long getMeetingTotalTime(List<RoomStatisticsVo> statisticsVoList) {
        long totalTime = 0;
        for(RoomStatisticsVo vo : statisticsVoList){
            if("4".equals(vo.getState()) || "3".equals(vo.getState())){
                totalTime += vo.getEndDate().getTime() - vo.getStaDate().getTime();
            }
        }

        return totalTime;
    }

    @Override
    public Map<String,Object> selectMeetingRoomStatisticsDetails(String staTime, String endTime,String areaId,String searchKey,String isAudit,String type,BasePageEntity page) {
        Map<String,Object> objectMap = new HashedMap();
        List<YuncmMeetingRoom> rooms = new ArrayList<YuncmMeetingRoom>();
        Map map = new HashMap();
        Map maps = new HashedMap();
        //获取统计的天数
        int days = TimeUtil.getTimeDifference(staTime,endTime);
        //查询会议室可预订时间获取会议室预定时长
        YuncmRoomReserveConf conf = localMeetingReserveService.selectYuncmRoomReserveConf();
        long reserveTime = conf.getReserveTimeEnd().getTime() - conf.getReserveTimeStart().getTime();
        //是否为区域会议室
        if(StringUtils.isNotBlank(areaId)){
            maps.put("areaId",areaId);
            map.put("areaId",areaId);
        }
        if(StringUtils.isNotBlank(searchKey)){
            maps.put("searchKey",searchKey);
        }
        if(StringUtils.isNotBlank(isAudit)){
            maps.put("isAudit",isAudit);
        }
        //查看会议室 需要时加分页
        rooms = this.yuncmMeetingRoomMapper.selectConditionMeetingRoom(maps);
        //获取会议室预定总时长全部
        long totalRoomTime = reserveTime * (days + 1) * rooms.size();
        //获取会议室预定时长单个会议室
        long singleRoomTime = reserveTime * (days + 1);
        map.put("staTime",staTime);
        map.put("endTime",endTime);
        //获取时间段的总会议
        List<RoomStatisticsVo> statisticsVoList = this.yummeetingConferenceRoomMiddleMapper.selectMeetingRoomStatistics(map);
        List<MeetingRoomStatisticsVo> vos = this.getMeetingRoomStatisticsDetails(statisticsVoList,rooms,totalRoomTime,singleRoomTime,searchKey,page,isAudit);
        //获取全部信息
        List<MeetingRoomStatisticsVo> statistics = new ArrayList<>();
        MeetingRoomStatisticsVo roomVo = null;
        if(vos.size() != 0){
            if(vos.get(0).getName().equals("全部")){
                roomVo = new MeetingRoomStatisticsVo();
                roomVo = vos.get(0) ;
                vos.remove(0);
            }
        }
        //按需要排序
        this.sortMeetingRoom(vos,type);
        //分页
        List<MeetingRoomStatisticsVo> list = Sublist.page(page.getCurrentPage(),page.getPageSize(),vos);
        if(roomVo != null){
            statistics.add(roomVo);
        }
        for (MeetingRoomStatisticsVo vo : list) {
            statistics.add(vo);
        }
        objectMap.put("statistics",statistics);
        objectMap.put("total",rooms.size());
        return objectMap;
    }

    @Override
    public List<MeetingStatisticsDetails> selectMeetingDetails(String roomId, String type, String staTime, String endTime,BasePageEntity page) {
        Map map = new HashMap();
        List<MeetingStatisticsDetails> detailsList = new ArrayList<MeetingStatisticsDetails>();
        List<String> list = new ArrayList<>();
        String str[] = roomId.split(",");
        for(int i=0;i<str.length;i++){
            list.add(str[i].toString());
        }
        map.put("roomIds",list);
        map.put("staTime",staTime);
        map.put("endTime",endTime);
        if("0".equals(type)){
            map.put("type","0");
        }else{
            map.put("state","4");
        }
        //获取需要的会议
        //暂不需要分页
        //PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<RoomStatisticsVo> voList = this.yunmeetingConferenceMapper.selectMeetingAndUserName(map);

        //获取
        for(RoomStatisticsVo vo : voList){
            MeetingStatisticsDetails details = new MeetingStatisticsDetails();
            if("0".equals(type)){
                details.setName(vo.getMeetingName());
                details.setStaTime(vo.getStaDate());
                details.setEndTime(vo.getEndDate());
                details.setReserveName(vo.getUserName());
            }else {
                //获取参会，签到，回复人数
                Map<String, Integer> intMap = meetingReserveService.getCountMeetingSignNum(vo.getId());
                //参会人员
                int all = intMap.get("all");
                //签到人员
                int signNum = intMap.get("signNum");
                //回复人员
                int reply = intMap.get("reply");
                details.setName(vo.getMeetingName());
                details.setStaTime(vo.getStaDate());
                details.setEndTime(vo.getEndDate());
                details.setReserveName(vo.getUserName());
                details.setTimeLength(TimeUtil.dateTimeMs(vo.getEndDate().getTime() - vo.getStaDate().getTime()));
                //响应率
                double rep = (double) reply / all * 100;
                String result = String .format("%.1f",rep);
                details.setAnswerRate(result + "%");
                //签到率
                double sig = (double) signNum / all * 100;
                String res = String .format("%.1f",sig);
                details.setSignRate(res + "%");
            }
            detailsList.add(details);
        }

        return detailsList;
    }

    /**
     * 获取会议室统计详情
     * @return
     */
    public List<MeetingRoomStatisticsVo> getMeetingRoomStatisticsDetails(List<RoomStatisticsVo> statisticsVoList,List<YuncmMeetingRoom> rooms,long totalRoomTime,long singleRoomTime,String searchKey,BasePageEntity page,String isAudit){

        Map<String,List<RoomStatisticsVo>> map = new HashedMap();
        List<MeetingRoomStatisticsVo> vos = new ArrayList<MeetingRoomStatisticsVo>();
        String roomId = "";
        //会议数量
        int meetingNum = 0;
        //未通过会议数量
        int notMeetingNum = 0;
        //使用总时长
        long useTotalTime = 0;
        //获取全部会议室会议
        int i = 0;
        for (YuncmMeetingRoom room : rooms) {
            i++;
            List<RoomStatisticsVo> voList = new ArrayList<RoomStatisticsVo>();
            for (RoomStatisticsVo svo : statisticsVoList) {
                if (svo.getRoomId().equals(room.getId())) {
                    voList.add(svo);
                    //获取未审核会议
                    if ("4".equals(svo.getState()) || "3".equals(svo.getState())) {
                        meetingNum++;
                        useTotalTime += svo.getEndDate().getTime() - svo.getStaDate().getTime();
                    } else {
                        notMeetingNum++;
                    }
                }
            }
            roomId +=room.getId()+",";
            //不为空的时候放入map
            map.put(room.getId(), voList);

        }
        //是否搜索
        if (!StringUtils.isNotBlank(searchKey) ) {
            //是否是第一页
            if(page.getCurrentPage() == 1 && rooms.size() != 0 &&  !StringUtils.isNotBlank(isAudit)) {
                MeetingRoomStatisticsVo vo = new MeetingRoomStatisticsVo();
                vo.setRoomId(roomId);
                vo.setName("全部");
                vo.setMeetingTotalNum(meetingNum);
                vo.setNotPassMeetingNum(notMeetingNum);
                vo.setMeetingUseTime(useTotalTime);
                vo.setMeetingFreeTime(totalRoomTime - useTotalTime);
                //获取使用率保留一位小数
                Double tota = (double) useTotalTime / totalRoomTime * 100;
                String result = String.format("%.1f", tota);
                vo.setMeetingRoomUtilization(result + "%");
                vo.setNum(tota);
                vos.add(vo);
            }
        }
        if(map.size()!= 0){
            //获取单个会议室会议
            for(YuncmMeetingRoom room : rooms){
                //会议数量
                meetingNum = 0;
                //使用总时长
                useTotalTime = 0;
                //审核未通过会议
                notMeetingNum = 0;
                List<RoomStatisticsVo> voList = map.get(room.getId());
                for(RoomStatisticsVo svo : voList){
                    //获取未审核会议
                    if("4".equals(svo.getState()) || "3".equals(svo.getState())){
                        meetingNum ++;
                        useTotalTime += svo.getEndDate().getTime() - svo.getStaDate().getTime();
                    }else{
                        notMeetingNum ++;
                    }
                }
                MeetingRoomStatisticsVo vo = new MeetingRoomStatisticsVo();
                vo.setRoomId(room.getId());
                vo.setName(room.getName());
                vo.setMeetingTotalNum(meetingNum);
                vo.setNotPassMeetingNum(notMeetingNum);
                vo.setMeetingUseTime(useTotalTime);
                vo.setMeetingFreeTime(singleRoomTime - useTotalTime);
                //获取使用率保留一位小数
                Double tota = (double) useTotalTime / singleRoomTime *100;
                String result = String .format("%.1f",tota);
                vo.setMeetingRoomUtilization(result+"%");
                vo.setNum(tota);
                vos.add(vo);
            }
        }
        return vos;
    }

    /**
     * 获取会议统计信息
     * @return
     */
    public Map<String,Object> getMeetingStatistics(String staTime,String endTime,List<YuncmMeetingRoom> rooms){

        Map<String,Object> maps = new HashMap<String,Object>();
        List<String> list = new ArrayList<String>();
        for(YuncmMeetingRoom room : rooms){
            list.add(room.getId());
        }
        Map map = new HashMap();
        List<MeetingStatisticsDetails> detailsList = new ArrayList<MeetingStatisticsDetails>();
        List<RoomStatisticsVo> voList = new ArrayList<RoomStatisticsVo>();
        if(list.size() != 0){
            map.put("roomIds",list);
            map.put("staTime",staTime);
            map.put("endTime",endTime);
            map.put("state","4");
            //获取需要的会议
           voList = this.yunmeetingConferenceMapper.selectMeetingAndUserName(map);
        }
        //所有会议总时长
        long meetingTotalTime = 0;
        //所有会议总参会人数
        int totalPerson = 0;
        //所有会议总签到人
        int totalSign = 0;
        //所有总参会人数响应率
        int totalReply = 0;
        for(RoomStatisticsVo vo : voList){
            meetingTotalTime += vo.getEndDate().getTime() - vo.getStaDate().getTime();
            //获取参会，签到，回复人数
            Map<String, Integer> intMap = meetingReserveService.getCountMeetingSignNum(vo.getId());
            //参会人员
            int all = intMap.get("all");
            //签到人员
            int signNum = intMap.get("signNum");
            //回复人员
            int reply = intMap.get("reply");
            totalPerson += all;
            totalSign += signNum;
            totalReply += reply;
        }
        maps.put("meetingNum",voList.size());
        maps.put("meetingTotalTime",meetingTotalTime);
        //获取使用率保留一位小数
        Double tota = (double) totalSign / totalPerson *100;
        if(!tota.isNaN()){
            String signRate = String .format("%.1f",tota);
            maps.put("signRate",signRate+"%");
        }else{
            maps.put("signRate","0%");
        }
        Double ble = (double) totalReply / totalPerson *100;
        if(!ble.isNaN()){
            String replyRate = String .format("%.1f",ble);
            maps.put("replyRate",replyRate+"%");
        }else{
            maps.put("replyRate","0%");
        }
        return maps;
    }



    public Map<String,Object> getHomePageChartInfo(String staTime,String endTime){
        Map<String,Object> maps = new HashMap<String,Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //会议数量
        List<String> meettingNumber = new ArrayList<String>();
        //会议时长
        List<String> meettingDuration = new ArrayList<String>();
        //当天时间
        List<String> meettingTime = new ArrayList<String>();
        try {
            Map map = new HashMap();
            Date sdate = sdf.parse(staTime);
            Date edate = sdf.parse(endTime);
            List<String> times = TimeUtil.getBetweenDates(sdate,edate);
            for (int i = 0; i < times.size(); i++){
                //会议数量
                int meetingNum = 0;
                //会议时长
                long meetingTime = 0;
                map.put("staTime",times.get(i)+" 00:01:00");
                map.put("endTime",times.get(i)+" 23:59:00");
                List<RoomStatisticsVo> statisticsVoList = this.yummeetingConferenceRoomMiddleMapper.selectMeetingRoomStatistics(map);
                for(RoomStatisticsVo vo : statisticsVoList){
                    //结束的会议
                    if("4".equals(vo.getState()) || "3".equals(vo.getState())){
                        meetingNum ++;
                        meetingTime = meetingTime + vo.getEndDate().getTime() - vo.getStaDate().getTime();
                    }

                }
                meettingNumber.add(meetingNum+"");
                meettingDuration.add(meetingTime+"");
                meettingTime.add(sdf.parse(times.get(i)).getTime()+"");
            }
            maps.put("meettingNumber",(String[]) meettingNumber.toArray(new String[0]));
            maps.put("meettingDuration",(String[]) meettingDuration.toArray(new String[0]));
            maps.put("meettingTime",(String[]) meettingTime.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  maps;
    };

    /**
     * 会议室排序
     * @param statisticsVost 会议室
     * @param type 排序类型
     * @return
     */
    public  List<MeetingRoomStatisticsVo> sortMeetingRoom(List<MeetingRoomStatisticsVo> statisticsVost,String type){

        if(statisticsVost.size() != 0) {
            //排序 ：1：会议数量，默认为空时按会议数量排序
            if ("1".equals(type) || !StringUtils.isNotBlank(type)) {
                Collections.sort(statisticsVost, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        MeetingRoomStatisticsVo stu1 = (MeetingRoomStatisticsVo) o1;
                        MeetingRoomStatisticsVo stu2 = (MeetingRoomStatisticsVo) o2;
                        if (stu1.getMeetingTotalNum() < stu2.getMeetingTotalNum()) {
                            return 1;
                        } else if (stu1.getMeetingTotalNum() == stu2.getMeetingTotalNum()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
            }
            // 2：使用时长，
            if ("2".equals(type)) {
                Collections.sort(statisticsVost, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        MeetingRoomStatisticsVo stu1 = (MeetingRoomStatisticsVo) o1;
                        MeetingRoomStatisticsVo stu2 = (MeetingRoomStatisticsVo) o2;
                        if (stu1.getMeetingUseTime() < stu2.getMeetingUseTime()) {
                            return 1;
                        } else if (stu1.getMeetingUseTime() == stu2.getMeetingUseTime()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
            }
            // 3：闲置时长，
            if ("3".equals(type)) {
                Collections.sort(statisticsVost, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        MeetingRoomStatisticsVo stu1 = (MeetingRoomStatisticsVo) o1;
                        MeetingRoomStatisticsVo stu2 = (MeetingRoomStatisticsVo) o2;
                        if (stu1.getMeetingFreeTime() < stu2.getMeetingFreeTime()) {
                            return 1;
                        } else if (stu1.getMeetingFreeTime() == stu2.getMeetingFreeTime()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
            }
            // 4：使用率，
            if ("4".equals(type)) {
                Collections.sort(statisticsVost, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        MeetingRoomStatisticsVo stu1 = (MeetingRoomStatisticsVo) o1;
                        MeetingRoomStatisticsVo stu2 = (MeetingRoomStatisticsVo) o2;
                        if (stu1.getNum() < stu2.getNum()) {
                            return 1;
                        } else if (stu1.getNum() == stu2.getNum()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
            }
            // 5：未通过会议数量。
            if ("5".equals(type)) {
                Collections.sort(statisticsVost, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        MeetingRoomStatisticsVo stu1 = (MeetingRoomStatisticsVo) o1;
                        MeetingRoomStatisticsVo stu2 = (MeetingRoomStatisticsVo) o2;
                        if (stu1.getNotPassMeetingNum() < stu2.getNotPassMeetingNum()) {
                            return 1;
                        } else if (stu1.getNotPassMeetingNum() == stu2.getNotPassMeetingNum()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
            }
        }else{
            return new ArrayList<MeetingRoomStatisticsVo>();
        }
        return statisticsVost;
    }
}
