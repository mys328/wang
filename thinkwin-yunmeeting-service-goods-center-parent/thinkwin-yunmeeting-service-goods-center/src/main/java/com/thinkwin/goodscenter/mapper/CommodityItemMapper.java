package com.thinkwin.goodscenter.mapper;

import com.thinkwin.common.model.db.CommodityItem;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface CommodityItemMapper extends Mapper<CommodityItem> {

    /**
     * 查询商品列表
     * @param map
     * @return
     */
    public List<CommodityItem> selectCommodityItemAll(String categoryCode);


}