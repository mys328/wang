package com.thinkwin.common;

import com.thinkwin.common.vo.statisticalAnalysisVo.DepStatisticalDataVo;

import java.util.Comparator;

/**
 *
 *部门统计排序工具类
 * User: yinchunlei
 * Date: 2017/10/30.
 * Company: thinkwin
 */
public class ComparatorDepStatisticalDataVo implements Comparator {
    public int paramType;//排序条件
    public int sortType;//排序类型


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

    public ComparatorDepStatisticalDataVo(int paramType, int sortType) {
        super();
        this.paramType = paramType;
        this.sortType = sortType;
    }

    public int compare(Object obj0, Object obj1) {
        DepStatisticalDataVo user0 = (DepStatisticalDataVo) obj0;
        DepStatisticalDataVo user1 = (DepStatisticalDataVo) obj1;
        int flag = 0;
        if (paramType == 1) {//默认按照会议时长排序
            if (sortType == 0) {//升序
                flag = user0.getMeetingHours().compareTo(user1.getMeetingHours());
            } else if (sortType == 1) {//降序
                flag = user1.getMeetingHours().compareTo(user0.getMeetingHours());
            }
        }else if (paramType == 0) {//按照组织会议数量排序
            if (sortType == 0) {//升序
                flag = user0.getNumberOfOrganizationalMeetings().compareTo(user1.getNumberOfOrganizationalMeetings());
            } else if (sortType == 1) {//降序
                flag = user1.getNumberOfOrganizationalMeetings().compareTo(user0.getNumberOfOrganizationalMeetings());
            }
        }else if (paramType == 2) {//按照参会人次排序
            if (sortType == 0) {//升序
                flag = user0.getNumberOfParticipants().compareTo(user1.getNumberOfParticipants());
            } else if (sortType == 1) {//降序
                flag = user1.getNumberOfParticipants().compareTo(user0.getNumberOfParticipants());
            }
        }else if (paramType == 3) {//按照为参会人数排序
            if (sortType == 0) {//升序
                flag = user0.getNumberOfAbsentParticipants().compareTo(user1.getNumberOfAbsentParticipants());
            } else if (sortType == 1) {//降序
                flag = user1.getNumberOfAbsentParticipants().compareTo(user0.getNumberOfAbsentParticipants());
            }
        }
        return flag;
    }
}
