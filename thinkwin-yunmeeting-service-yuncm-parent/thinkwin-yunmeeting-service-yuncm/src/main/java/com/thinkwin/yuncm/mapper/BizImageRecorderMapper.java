package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.BizImageRecorder;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by yinchunlei on 2017/12/26.
 */
public interface BizImageRecorderMapper extends Mapper<BizImageRecorder> {
	List<BizImageRecorder> getImageByIds(@Param("ids")List<String> ids);


    List <BizImageRecorder> findByType(Map map);

    /**
     * 批量添加节目图片
     * @param lists
     * @return
     */
    public void batchAddBizImageRecorder(List<BizImageRecorder> lists);

    /**
     * 批量更新目图片
     * @param lists
     * @return
     */
    public void batchUpdateBizImageRecorder(List<BizImageRecorder> lists);

    /**
     * 批量物理删除节目图片
     * @param ids
     * @return
     */
    public void batchPhysicalDelBizImageRecorder(List<String> ids);


}
