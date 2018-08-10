package com.thinkwin.yuncm.service;

import com.thinkwin.common.model.db.BizImageRecorder;

import java.util.List;

/*
 * 类说明：
 * @author lining 2018/5/15
 * @version 1.0
 *
 */
public interface BizImageRecorderService {

    public List<BizImageRecorder> findByBizIDType(String bizId, String type);

    public List<BizImageRecorder> findByType(String type);

    public List<BizImageRecorder> findByType(String type1,String type2);

    /**
     * 批量添加节目背景图片
     * @param lists
     * @return
     */
    public void batchAddBizImageRecorder(List<BizImageRecorder> lists);

    /**
     * 批量更新节目背景图片
     * @param lists
     * @return
     */
    public void batchUpdateBizImageRecorder(List<BizImageRecorder> lists);

    /**
     * 批量物理删除节目背景图片
     * @param ids
     * @return
     */
    public void batchPhysicalDelBizImageRecorder(List<String> ids);

}
