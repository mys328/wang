package com.thinkwin.common.utils;

import com.thinkwin.common.model.db.SysOrganization;
import com.thinkwin.common.model.db.YunmeetingConference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/6/4 0004.
 */
public class MeetingJudge {


    public static void main(String [] ags) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        //System.out.print( isThisWeek("2018-06-11"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str ="2018-06-11 12:30:00";
        String n = "10";
        Calendar   c   =   Calendar.getInstance();
        Integer i = +Integer.parseInt(n);
        c.setTime(formatter.parse(str));
        c.add(Calendar.MINUTE, i);
        String mDateTime=formatter.format(c.getTime());
        System.out.print(mDateTime);


    }


    private static  SimpleDateFormat fds = new SimpleDateFormat("yyyy-MM-dd");
    private static  final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 节目判断会议室是否和当前显示的有冲突
     *
     * @param conferences 当前的会议
     * @param conference  变更的会议
     * @param type        模板类型 会议室信息8001。当前节目8002。下一个节目8003。
     *                    会议二维码8004。 当天会议列表8005，本周的会议列表8006。
     *                    天气信息9001。
     *                    C8007	当天的会议列表显示未开始会议
     * @return
     */
    public static String jidgeMeetingState(List<YunmeetingConference> conferences, YunmeetingConference conference, String type,String time) throws ParseException {


        if(type != null) {
            String str[] = type.split(",");
            String flag = "";
            for (int i = 0; i < str.length; i++) {
                /**
                 * 当前节目
                 */
                if ("C8002".equals(str[i])) {
                    if (conferences.size() >= 1) {
                        YunmeetingConference ce = conferences.get(0);
                        boolean oo = timeJudge(ce, conference);
                        if (oo) {
                            flag += "C8002,";
                            if(new Date().getTime() <= format.parse(getTime(conferences.get(0).getTakeStartDate(), -Integer.parseInt(time))).getTime()){
                                flag += "C8003,";
                            }
                        }
                    }
                }
                /**
                 * 下前节目
                 */
                if ("C8003".equals(str[i])) {
                    if (conferences.size() >= 2) {
                        System.out.println(format.format(new Date().getTime()));
                        System.out.println(getTime(conferences.get(0).getTakeStartDate(), -Integer.parseInt(time)));
                        if(new Date().getTime() >= format.parse(getTime(conferences.get(0).getTakeStartDate(), -Integer.parseInt(time))).getTime()) {
                            YunmeetingConference ce = conferences.get(1);
                            boolean oo = timeJudge(ce, conference);
                            if (oo) {
                                flag += "C8003,";
                            }
                        }
                    }

                }
                /**
                 * 当天会议
                 */
                if ("C8005".equals(str[i])) {
                    boolean oo = isToday(conference.getTakeEndDate());
                    if (oo) {
                        flag +=  "C8005,";
                    }
                }
                /**
                 * 本周的会议
                 */
                if ("C8006".equals(str[i])) {
                    boolean oo = isThisWeek(fds.format(conference.getTakeStartDate()));
                    if (oo) {
                        flag +=  "C8006,";
                    }
                }

                /**
                 * 当天未开始的会议
                 */
                if ("C8007".equals(str[i])) {
                    boolean oo = isToday(conference.getTakeEndDate());
                    if (oo) {
                        flag +=  "C8007,";
                    }
                }

            }
            if("".equals(flag)){
                return "0";
            }
            return flag;
        }else {
            return "";
        }

    }



    /**
     * 节目判断会议室是否和当前显示的有冲突
     *
     * @param conferences 当前的会议
     * @param conference  变更的会议
     * @param type        模板类型 会议室信息8001。当前节目8002。下一个节目8003。
     *                    会议二维码8004。 当天会议列表8005，本周的会议列表8006。
     *                    天气信息9001。
     *                    C8007	当天的会议列表显示未开始会议
     * @return
     */
    public static String cancelJidgeMeetingState(List<YunmeetingConference> conferences, YunmeetingConference conference, String type) {


        if(type != null) {
            String str[] = type.split(",");
            String flag = "";
            for (int i = 0; i < str.length; i++) {
                /**
                 * 当前节目
                 */
                if ("C8002".equals(str[i])) {
                    if (conferences.size() >= 1) {
                        YunmeetingConference ce = conferences.get(0);
                        boolean oo = timeJudge(ce, conference);
                        if (oo) {
                            flag += "C8002,";
                        }
                    }
                }
                /**
                 * 下前节目
                 */
                if ("C8003".equals(str[i])) {
                    if (conferences.size() >= 2) {
                        YunmeetingConference ce = conferences.get(1);
                        boolean oo = timeJudge(ce, conference);
                        if (oo) {
                            flag += "C8003,";
                        }
                    }

                }
                /**
                 * 当天会议
                 */
                if ("C8005".equals(str[i])) {
                    boolean oo = isToday(conference.getTakeEndDate());
                    if (oo) {
                        flag +=  "C8005,";
                    }
                }
                /**
                 * 本周的会议
                 */
                if ("C8006".equals(str[i])) {
                    boolean oo = isThisWeek(fds.format(conference.getTakeStartDate()));
                    if (oo) {
                        flag +=  "C8006,";
                    }
                }

                /**
                 * 当天未开始的会议
                 */
                if ("C8007".equals(str[i])) {
                    boolean oo = isToday(conference.getTakeEndDate());
                    if (oo) {
                        flag +=  "C8007,";
                    }
                }

            }
            if("".equals(flag)){
                return "0";
            }
            return flag;
        }else {
            return "";
        }

    }





    /**
     * 是否为本天的时间
     * @param date
     * @return
     */
    public static boolean isToday(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        if (fmt.format(date).toString().equals(fmt.format(new Date()).toString())) {//格式化为相同格式
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断选择的日期是否是本周
     * @param time
     * @return
     */
    public static boolean isThisWeek(String time)  {

        CalendarUtil tt = new CalendarUtil();
        String head = tt.getMondayOFWeek();
        String tail =  tt.getCurrentWeekday();
        try {
            if (fds.parse(head).getTime() <= fds.parse(time).getTime() && fds.parse(time).getTime() <= fds.parse(tail).getTime()){
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 会议判断
     * @param ce 原会议
     * @param conference 变更的会议
     * @return
     */
    public static boolean timeJudge(YunmeetingConference ce ,YunmeetingConference conference){

        boolean bo = false;
        //id相同
        if(ce.getId().equals(conference.getId())){
            bo = true;
        }else{
            //不相同判断时间
            if (ce.getTakeStartDate().getTime() >= conference.getTakeStartDate().getTime()) {
                bo = true;
            }
        }
        return bo;
    }


    public static String getTime(Date date , Integer time){
        Calendar   c   =   Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mDateTime=formatter.format(c.getTime());
        return mDateTime;
    }







}
