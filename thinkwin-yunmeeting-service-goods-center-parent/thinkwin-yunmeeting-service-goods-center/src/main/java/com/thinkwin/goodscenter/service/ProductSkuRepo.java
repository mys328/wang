package com.thinkwin.goodscenter.service;

import com.thinkwin.goodscenter.vo.ProductSkuVo;

public interface ProductSkuRepo {
	ProductSkuVo getSkuByCode(String sku);
}
