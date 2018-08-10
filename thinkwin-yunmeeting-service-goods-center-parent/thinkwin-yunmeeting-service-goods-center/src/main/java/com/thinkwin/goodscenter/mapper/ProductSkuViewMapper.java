package com.thinkwin.goodscenter.mapper;

import com.thinkwin.goodscenter.dataview.ProductSkuView;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Collection;
import java.util.List;

public interface ProductSkuViewMapper extends Mapper<ProductSkuView> {
	List<ProductSkuView> getProductSkuByCode(@Param("list") Collection<String> skus);
}
