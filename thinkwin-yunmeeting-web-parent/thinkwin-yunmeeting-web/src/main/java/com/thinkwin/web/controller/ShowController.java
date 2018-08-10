package com.thinkwin.web.controller;

import com.alibaba.fastjson.JSON;
import com.thinkwin.auth.service.OrganizationService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.publish.TerminalProgramCommandDto;
import com.thinkwin.common.model.core.ShortUrl;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.publish.*;
import com.thinkwin.core.service.ShortUrlService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.publish.service.ConfigManagerService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2018/5/17 0017.
 */
@Controller
@RequestMapping(value = {"/terminalClient/program","/system"})
public class ShowController{

    private static Logger logger = LoggerFactory.getLogger(ShowController.class);


    @Resource
    private YuncmMeetingService yuncmMeetingService;
    @Resource
    private ConferenceService conferenceService;
    @Resource
    private UserService userService;
    @Resource
    private InfoReleaseTerminalService infoReleaseTerminalService;
    @Resource
    private TerminalService terminalService;
    @Resource
    private BizImageRecorderService bizImageRecorderService;
    @Resource
    private FileUploadService fileUploadService;
    @Resource
    private OrganizationService organizationService;
    @Resource
    private ConfigManagerService configManagerService;
    @Resource
    private SysSetingService sysSetingService;
    @Resource
    private ShortUrlService shortUrlService;


    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 播放节目接口
     * @param type 会议室信息8001。当前节目8002。下一个节目8003。会议二维码8004。 当天会议列表8005，本周的会议列表8006。天气信息9001。
     * @return
     */
    @RequestMapping(value = "/obtainProgramInfo",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Object obtainProgramInfo(HttpServletRequest req,String terminalId,String type,String tenantId) throws ParseException {

        //将模板信息存入redis
        String programId = this.conferenceService.getProgramId(terminalId);
        logger.info("模板类型============================="+type);
        if (StringUtils.isNotBlank(programId)) {
            String flag = RedisUtil.get(tenantId + "_QYH_play_program_" + programId);
            if (flag == null) {
                RedisUtil.set(tenantId + "_QYH_play_program_" + programId, type);
            }else{
                RedisUtil.set(tenantId + "_QYH_play_program_" + programId,  getRedisValue(flag,type));
            }
            boolean success = false;
            Map<String, Object> map = new HashMap<String, Object>();
            //根据终端标识获取绑定的会议室信息和节目模板
            InfoReleaseTerminal terminal = this.infoReleaseTerminalService.selectInfoReleaseTerminalById(terminalId);
            logger.info("终端信息+++++++++++++++++++++++++++"+terminal);
            if (terminal != null) {
                String roomId = terminal.getMeetingRoomId();//非空判断
                //获取会议室信息
                YuncmMeetingRoom room = this.yuncmMeetingService.selectByidYuncmMeetingRoom(roomId);
                logger.info("会议室信息+++++++++++++++++++"+room);
                if(room != null) {
                    if ("2".equals(room.getState())) {
                        String str[] = type.split(",");
                        for (int i = 0; i < str.length; i++) {
                            SysSetting setting = this.sysSetingService.findByKey("meetingroom.time");
                            String time = "";
                            if (setting != null) {
                                time = setting.getContent();
                            } else {
                                time = "0";
                            }
                            type = str[i];
                            if ("C8001".equals(type)) {
                                if (room != null) {
                                    Rooms rooms = new Rooms();
                                    rooms.setRoomName(room.getName());
                                    rooms.setLocation(room.getLocation());
                                    rooms.setState(room.getState());
                                    rooms.setQrCodeUrl(this.fileUploadService.selectTenementByFile(room.getQrCodeUrl()));
                                    map.put("C8001", rooms);
                                } else {
                                    map.put("C8001", null);
                                }
                            }
                            //获取当前时间最近的两个会议
                            List<YunmeetingConference> conferences = this.conferenceService.getNextConference(roomId, new Date());
                            if (conferences != null && conferences.size() != 0) {
                                //是否有正在进行中的会议
                                if (new Date().getTime() < format.parse(MeetingJudge.getTime(conferences.get(0).getTakeStartDate(), -Integer.parseInt(time))).getTime()) {
                                    if ("C8002".equals(type)) {
                                        if (conferences.size() >= 1) {
                                            map.put("C8002", getMeetingInfo(conferences.get(0), time, terminalId, tenantId));
                                        } else {
                                            map.put("C8002", null);
                                        }
                                    }
                                    if ("C8003".equals(type)) {
                                        if (conferences.size() >= 1) {
                                            map.put("C8003", getMeetingInfo(conferences.get(0), time, terminalId, tenantId));
                                        } else {
                                            map.put("C8003", null);
                                        }
                                    }

                                } else {
                                    if ("C8002".equals(type)) {
                                        if (conferences.size() >= 1) {
                                            map.put("C8002", getMeetingInfo(conferences.get(0), time, terminalId, tenantId));
                                        } else {
                                            map.put("C8002", null);
                                        }
                                    }
                                    if ("C8003".equals(type)) {
                                        if (conferences.size() == 2) {
                                            map.put("C8003", getMeetingInfo(conferences.get(1), time, terminalId, tenantId));
                                        } else {
                                            map.put("C8003", null);
                                        }
                                    }
                                }

                            } else {
                                if ("C8002".equals(type)) {
                                    map.put("C8002", null);
                                }
                                if ("C8003".equals(type)) {
                                    map.put("C8003", null);
                                }
                            }
                            //查询当天的全部会议列表
                            if ("C8005".equals(type)) {
                                List<YunmeetingConference> confs = this.conferenceService.getSameDayConference(roomId, new Date());
                                List<MeetingInfo> infos = new ArrayList<MeetingInfo>();
                                for (YunmeetingConference ce : confs) {
                                    infos.add(getMeetingInfo(ce, time, terminalId, tenantId));
                                }
                                if (infos.size() != 0) {
                                    map.put("C8005", infos);

                                } else {
                                    map.put("C8005", null);
                                }

                            }
                            //查询本周的会议列表
                            if ("C8006".equals(type)) {
                                CalendarUtil tt = new CalendarUtil();
                                String head = tt.getMondayOFWeek();
                                String tail = tt.getCurrentWeekday();
                                List<YunmeetingConference> confs = this.conferenceService.getThisWeekConference(roomId, sdf.parse(head), sdf.parse(tail));
                                List<MeetingInfo> infos = new ArrayList<MeetingInfo>();
                                for (YunmeetingConference ce : confs) {
                                    infos.add(getMeetingInfo(ce, time, terminalId, tenantId));
                                }
                                if (infos.size() != 0) {
                                    map.put("C8006", infos);
                                } else {
                                    map.put("C8006", null);
                                }
                            }
                            //查询当天的未开始会议列表
                            if ("C8007".equals(type)) {
                                List<YunmeetingConference> confs = this.conferenceService.getSameDayConference(roomId, new Date());
                                List<MeetingInfo> infos = new ArrayList<MeetingInfo>();
                                for (YunmeetingConference ce : confs) {
                                    if ("2".equals(ce.getState()) || "3".equals(ce.getState())) {
                                        infos.add(getMeetingInfo(ce, time, terminalId, tenantId));
                                    }
                                }
                                if (infos.size() != 0) {
                                    map.put("C8007", infos);
                                } else {
                                    map.put("C8007", null);
                                }

                            }
                            //是否存在二维码
                            if("C8004".equals(type)){
                                success = true;
                            }
                            //获取天气信息
                            if ("C9001".equals(type)) {
                                //获取终端所在城市
                                WeatherVo weatherVo = new WeatherVo();
                                if (StringUtils.isNotBlank(terminal.getCounty())) {
                                    weatherVo = configManagerService.getWeather(terminal.getCounty(), terminal.getCity());
                                } else {
                                    weatherVo = configManagerService.getWeather(terminal.getCity(), terminal.getProvince());
                                }
                                if (weatherVo != null) {
                                    map.put("C9001", weatherVo);
                                } else {
                                    map.put("C9001", null);
                                }
                            }
                        }
                        //获取二维码
                        if(success){
                            map.put("C8004",getQRCodeInfo(req,map,tenantId));
                        }
                    } else {
                        //会议室停用
                        map = getProgramInfo(req,type, room, roomId, terminal, tenantId);
                    }
                }else{
                    //未绑定会议室
                    map = getProgramInfos(type,terminal);
                }
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Success.getDescription(), null,"");
    }


    /**
     * 终端初始化获取模板路径
     * @return
     */
    @RequestMapping(value = "/obtainProgramMould",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Object obtainProgramMould(String terminalId,HttpServletRequest request,String tenantId) throws ParseException {
        InfoProgramTerminalMiddle middle = this.terminalService.getInfoProgramTerminalMiddleByTerminalId(terminalId);
        TerminalProgramCommandDto dto = null;
        if(middle != null) {
            if(middle.getProgramId() != null ) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                dto = this.terminalService.obtainProgramCommandById(middle.getProgramId(),tenantId);
                if (dto != null) {
                    if (dto.getStart() != null) {
                        Date date = sdf.parse(dto.getStart());
                        dto.setStart(sdf.format(date));
                    }
                    if (dto.getEnd() != null) {
                        Date date = sdf.parse(dto.getEnd());
                        dto.setEnd(sdf.format(date));
                    }
                    String url = syncProgramFile(middle.getProgramId(), request,0);
                    url = url+"&tenantId="+tenantId;
                    dto.setAttachmentUrl(url);
                }
            }
        }

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), dto);
    }


    /**
     * 节目目录下搜索.html文件
     * @param programRootPath 存储节目文件的根目录
     * @return html文件名称
     */
    private String findIndexPath(String programRootPath){
        File rootPath = new File(programRootPath);
        if(rootPath.exists()){
            String[] fileList = rootPath.list();
            for(int i=0; i<fileList.length; i++) {
                if(fileList[i].endsWith(".html")){
                    return fileList[i];
                }
            }
        }

        return "";
    }

    private String syncProgramFile(String programId, HttpServletRequest req,int reload){
        String path = req.getSession().getServletContext().getRealPath("/");

        String fileDir = path + "template"+ File.separator + programId;
        String newPath = fileDir + File.separator ;
        File filePath = new File(fileDir);
        logger.info("节目路径: " + filePath.toString());
        if(!LocalCacheUitl.containsKey(programId)){
            LocalCacheUitl.put(programId,programId);
        }
        String cachekey = (String) LocalCacheUitl.get(programId);

        synchronized (cachekey){
            if (!filePath.exists()) {
                List<BizImageRecorder> fileList = bizImageRecorderService.findByBizIDType(programId, "2");

                if (CollectionUtils.isNotEmpty(fileList) && fileList.size() == 1) {
                    SysAttachment sysAttachment = this.fileUploadService.selectByidFile(fileList.get(0).getImageId());
                    FileUploadUtil.syncProgramFile(newPath, filePath, sysAttachment);
                } else {
                    logger.error("publish: 获取节目文件失败, 找不到文件, 节目ID: {}", programId);
                }
            }
        }

        if(!filePath.exists()){
            return null;
        }

        String pageName = findIndexPath(newPath + "program");
        if(StringUtils.isBlank(pageName)){
            return null;
        }

        String pagePath = "";
        pagePath += req.getScheme() + "://" + req.getServerName();
        if(req.getServerPort() != 80){
            pagePath += ":" + req.getServerPort();
        }

        if(reload==1) {
            pagePath += "/template/" + programId + "/program/" + pageName;
        }else {
            pagePath = pagePath + "/system/program?programId=" + programId;
        }
        return pagePath;
    }

   public MeetingInfo getMeetingInfo(YunmeetingConference ce,String time,String terminalId,String tenantId){
       MeetingInfo info = new MeetingInfo();

       if("0".equals(ce.getIsPublic())){
           info.setConferenceName("未公开会议");
       }else {
           info.setConferenceName(ce.getConferenceName());
       }
       //获取部门信息
       info.setHostUnit(this.organizationService.selectOrganiztionById(ce.getHostUnit()).getOrgName());
       info.setEndTime(ce.getTakeEndDate());
       info.setStartTime(ce.getTakeStartDate());
       info.setFrontTime(time);
       info.setId(ce.getId());
//       info.setId(ce.getId());
//       info.setTenantId(tenantId);
//       info.setTerminalId(terminalId);
       //查询会议室配置信息
//       YuncmRoomReserveConf conf = this.yuncmMeetingService.selectYuncmRoomReserveConf();

       //查询用户信息获取会议预订人
       info.setCreater(this.userService.selectUserByUserId(ce.getOrganizerId()).getUserName());
       return info;
   }


   private String getRedisValue(String flag,String type){
       Set<String> set = new HashSet<String>();
       String str [] = flag.split(",");
       for (int i = 0; i < str.length; i++) {
           set.add(str[i]);
       }
       String s [] =type.split(",");
       for (int i = 0; i < s.length; i++) {
           set.add(s[i]);
       }
       String ss = "";
       for (String se : set){
           ss += se+",";
       }
       return ss;
   }


   public Map<String, Object> getProgramInfo(HttpServletRequest req,String type,YuncmMeetingRoom room,String roomId,InfoReleaseTerminal terminal,String tenantId){

       Map<String,Object> map = new HashMap<String,Object>();
       try {
           boolean success = false;
           String str[] = type.split(",");
           for (int i = 0; i < str.length; i++) {
               SysSetting setting = this.sysSetingService.findByKey("meetingroom.time");
               String time = "";
               if (setting != null) {
                   time = setting.getContent();
               } else {
                   time = "0";
               }
               type = str[i];
               if ("C8001".equals(type)) {
                   if (room != null) {
                       Rooms rooms = new Rooms();
                       rooms.setRoomName(room.getName());
                       rooms.setLocation(room.getLocation());
                       rooms.setState(room.getState());
                       rooms.setQrCodeUrl(this.fileUploadService.selectTenementByFile(room.getQrCodeUrl()));
                       map.put("C8001", rooms);
                   } else {
                       map.put("C8001", null);
                   }
               }
               //获取当前时间最近的两个会议
               List<YunmeetingConference> conferences = this.conferenceService.getNextConference(roomId, new Date());
               if (conferences != null && conferences.size() != 0) {
                   //是否有正在进行中的会议
                   if (new Date().getTime() > format.parse(MeetingJudge.getTime(conferences.get(0).getTakeStartDate(), -Integer.parseInt(time))).getTime()) {
                       if ("C8002".equals(type)) {
                           if (conferences.size() >= 1) {
                               map.put("C8002", getMeetingInfo(conferences.get(0), time,terminal.getId(),tenantId));
                           } else {
                               map.put("C8002", null);
                           }
                       }
                       if ("C8003".equals(type)) {
                           map.put("C8003", null);
                      }

                       //查询当天的全部会议列表
                       if ("C8005".equals(type)) {
                           List<MeetingInfo> infos = new ArrayList<MeetingInfo>();
                           infos.add(getMeetingInfo(conferences.get(0), time,terminal.getId(),tenantId));
                           if (infos.size() != 0) {
                               map.put("C8005", infos);
                           } else {
                               map.put("C8005", null);
                           }

                       }
                       //查询本周的会议列表
                       if ("C8006".equals(type)) {
                           List<MeetingInfo> infos = new ArrayList<MeetingInfo>();
                           infos.add(getMeetingInfo(conferences.get(0), time,terminal.getId(),tenantId));
                           if (infos.size() != 0) {
                               map.put("C8006", infos);
                           } else {
                               map.put("C8006", null);
                           }
                       }
                       //查询当天的未开始会议列表
                       if ("C8007".equals(type)) {
                           List<MeetingInfo> infos = new ArrayList<MeetingInfo>();
                           infos.add(getMeetingInfo(conferences.get(0), time,terminal.getId(),tenantId));
                           if (infos.size() != 0) {
                               map.put("C8007", infos);
                           } else {
                               map.put("C8007", null);
                           }

                       }
                   }

               } else {
                   if ("C8002".equals(type)) {
                       map.put("C8002", null);
                   }
                   if ("C8003".equals(type)) {
                       map.put("C8003", null);
                   }
               }
               if("C8004".equals(type)){
                   success = true;
               }
               //获取天气信息
               if ("C9001".equals(type)) {
                   //获取终端所在城市
                   WeatherVo weatherVo = new WeatherVo();
                   if (StringUtils.isNotBlank(terminal.getCounty())) {
                       weatherVo = configManagerService.getWeather(terminal.getCounty(), terminal.getCity());
                   } else {
                       weatherVo = configManagerService.getWeather(terminal.getCity(), terminal.getProvince());
                   }
                   if (weatherVo != null) {
                       map.put("C9001", weatherVo);
                   } else {
                       map.put("C9001", null);
                   }
               }
           }
            //获取二维码
           if(success){
               map.put("C8004",getQRCodeInfo(req,map,tenantId));
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       return map;
   }

    /**
     * 会议室删除状态
     * @return
     */
    public Map<String,Object> getProgramInfos(String type,InfoReleaseTerminal terminal){

        Map<String,Object> map = new HashMap<String,Object>();
        try {
            String str [] = type.split(",");
            for (int i= 0 ; i<str.length; i++){
                //获取天气信息
                if ("C9001".equals(type)) {
                    //获取终端所在城市
                    WeatherVo weatherVo = new WeatherVo();
                    if (StringUtils.isNotBlank(terminal.getCounty())) {
                        weatherVo = configManagerService.getWeather(terminal.getCounty(), terminal.getCity());
                    } else {
                        weatherVo = configManagerService.getWeather(terminal.getCity(), terminal.getProvince());
                    }
                    if (weatherVo != null) {
                        map.put("C9001", weatherVo);
                    } else {
                        map.put("C9001", null);
                    }
                }else {
                    map.put(str[i],null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return map;
    }

    /**
     * 获取二维码信息
     * @return
     */
    public QRCodeVo getQRCodeInfo(HttpServletRequest req, Map<String, Object> map,String tenantId){
        QRCodeVo vo = null;
        try {
            MeetingInfo info = null;
            if(null != map.get("C8002")){
                info = new MeetingInfo();
                info = (MeetingInfo) map.get("C8002");
            }
            if(info != null) {
                vo = new QRCodeVo();
                vo.setId(info.getId());
                vo.setTenantId(tenantId);
                //查询会议室配置信息
                YuncmRoomReserveConf conf = this.yuncmMeetingService.selectYuncmRoomReserveConf();
                if (conf != null) {
                    vo.setTimeStep(conf.getQrDuration());
                } else {
                    vo.setTimeStep(0);
                }
                //短地址映射
                Map<String,String> m = new HashMap<>();
                m.put("tenantId",tenantId);
                m.put("meetingId",vo.getId());
                m.put("timeStep",vo.getTimeStep()+"");
                String url = JSON.toJSONString(m);
                String[] urls = ShortUrlUtils.ShortUrl(url);
                RedisUtil.set(urls[0],url);
                RedisUtil.expire(urls[0],3600);
                ShortUrl shortUrl = new ShortUrl();
                shortUrl.setShortUrl(urls[0]);
                List<ShortUrl> shortUrls = shortUrlService.get(shortUrl);
                if(shortUrls==null||shortUrls.size()==0) {
                    shortUrl.setTenantId(tenantId);
                    shortUrl.setRealUrl(url);
                    shortUrlService.save(shortUrl);
                }
//                int port = req.getServerPort();
//                String portStr = ":"+port;
//                if(port==80){
//                    portStr = "";
//                }
//                String codeUrl = req.getScheme()+"://"+req.getServerName()+portStr+"/s/" + urls[0];
                String codeUrl = PropertyUtil.getWechatServer()+"/s/" + urls[0];
                logger.info("二维码短地址为："+codeUrl);
                vo.setQrCodeUrl(codeUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vo;
    }

    //同步节目
    @RequestMapping("/program")
    public void test(HttpServletRequest req, HttpServletResponse res, String programId, String tenantId) throws IOException {
        TenantContext.setTenantId(tenantId);
        String url = syncProgramFile(programId, req,1);
        res.sendRedirect(url);
    }





}
//        //获取当前会议
//        YunmeetingConference conference = this.conferenceService.getCurrentConference(roomId,new Date());
//        if(conference != null){
//            MeetingInfo info = new MeetingInfo();
//            info.setConferenceName(conference.getConferenceName());
//            info.setHostUnit(conference.getHostUnit());
//            info.setEndTime(conference.getTakeEndDate());
//            info.setStartTime(conference.getTakeStartDate());
//            //查询用户信息
//            SysUser sysUser = this.userService.selectUserByUserId(conference.getCreaterId());
//            info.setCreater(sysUser.getUserName());
//            showVo.setCurrent(info);
//        }