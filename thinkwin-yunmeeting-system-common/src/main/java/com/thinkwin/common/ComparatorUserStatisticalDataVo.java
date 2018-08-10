package com.thinkwin.common;

import com.thinkwin.common.vo.statisticalAnalysisVo.UserStatisticalDataVo;

import java.util.Comparator;

/**
 * 人员统计排序工具类
 * User: yinchunlei
 * Date: 2017/10/28.
 * Company: thinkwin
 */
public class ComparatorUserStatisticalDataVo  implements Comparator {
   public int paramType;
    public int sortType;


    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public int getParamType() {
        return paramType;
    }

    public void setParamType(int paramType) {
        this.paramType = paramType;
    }

    public ComparatorUserStatisticalDataVo(int paramType,int sortType) {
        super();
        this.paramType = paramType;
        this.sortType = sortType;
    }

    public int compare(Object obj0, Object obj1) {
        UserStatisticalDataVo user0=(UserStatisticalDataVo)obj0;
        UserStatisticalDataVo user1=(UserStatisticalDataVo)obj1;
        int flag = 0;
        if(paramType==0){//默认按照会议时长排序
            if(sortType==0){//升序
                flag = user0.getMeetingHours().compareTo(user1.getMeetingHours());
            }else if(sortType==1){//降序
                flag = user1.getMeetingHours().compareTo(user0.getMeetingHours());
            }
        }else if(paramType==1){//按照会议数量排序
            if(sortType==0){//升序
                flag = user0.getMeetingNum().compareTo(user1.getMeetingNum());
            }else if(sortType==1){//降序
                flag = user1.getMeetingNum().compareTo(user0.getMeetingNum());
            }
        }else if (paramType == 2) {//按照会议留言数排序
            if (sortType == 0) {//升序
                flag = user0.getConferenceMessageNumber().compareTo(user1.getConferenceMessageNumber());
            } else if (sortType == 1) {//降序
                flag = user1.getConferenceMessageNumber().compareTo(user0.getConferenceMessageNumber());
            }
        }else if (paramType == 3) {//按照个人签到率排序
            if (sortType == 0) {//升序
                flag = user0.getIndividualAttendanceRateNum().compareTo(user1.getIndividualAttendanceRateNum());
            } else if (sortType == 1) {//降序
                flag = user1.getIndividualAttendanceRateNum().compareTo(user0.getIndividualAttendanceRateNum());
            }
        }else if (paramType == 4) {//按照个人相应率排序
            if (sortType == 0) {//升序
                flag = user0.getIndividualCorrespondingRateNum().compareTo(user1.getIndividualCorrespondingRateNum());
            } else if (sortType == 1) {//降序
                flag = user1.getIndividualCorrespondingRateNum().compareTo(user0.getIndividualCorrespondingRateNum());
            }
        }
        return flag;
    }

}
