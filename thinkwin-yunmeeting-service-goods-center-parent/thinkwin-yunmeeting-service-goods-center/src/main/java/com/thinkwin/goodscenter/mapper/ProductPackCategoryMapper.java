package com.thinkwin.goodscenter.mapper;

import com.thinkwin.common.model.db.ProductPackCategory;
import tk.mybatis.mapper.common.Mapper;

public interface ProductPackCategoryMapper extends Mapper<ProductPackCategory> {

    /**
     * 查看商品版本
     * @param packageId 商品id
     * @return
     */
    ProductPackCategory selectByProductPackCategoryInfo(String packageId);
}