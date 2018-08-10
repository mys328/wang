package com.thinkwin.goodscenter.mapper;

import com.thinkwin.common.model.db.ProductPackSku;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProductPackSkuMapper extends Mapper<ProductPackSku> {


    List<ProductPackSku> selectCommodityYearPrice(String sku);
}