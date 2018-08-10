package com.thinkwin.goodscenter.mapper;

import com.thinkwin.common.model.db.ProductSku;
import com.thinkwin.common.vo.ProductSkuDto;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProductSkuMapper extends Mapper<ProductSku> {
    /**
     * 查询所有商品信息
     * @return
     */
    List<ProductSkuDto> selectAllProductSku();


}