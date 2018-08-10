package com.thinkwin.goodscenter.mapper;

import com.thinkwin.goodscenter.dataview.ProductPackSkuView;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Collection;
import java.util.List;

public interface ProductPackSkuViewMapper extends Mapper<ProductPackSkuView> {
	List<ProductPackSkuView> getProductPackSkuByCode(@Param("list") Collection<String> sku);
}
