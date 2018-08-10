package com.thinkwin.common.vo.publish;

import java.io.Serializable;

/**
 * User:wangxilei
 * Date:2018/6/6
 * Company:thinkwin
 */
public class PlanCountVo implements Serializable{
    private static final long serialVersionUID = -5819785422817787560L;
    private int allCount;
    private int startCount;
    private int noStartCount;

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public int getStartCount() {
        return startCount;
    }

    public void setStartCount(int startCount) {
        this.startCount = startCount;
    }

    public int getNoStartCount() {
        return noStartCount;
    }

    public void setNoStartCount(int noStartCount) {
        this.noStartCount = noStartCount;
    }
}
