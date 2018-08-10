package com.thinkwin.goodscenter.service.impl;

import com.thinkwin.goodscenter.mapper.ProductSkuMapper;
import com.thinkwin.goodscenter.service.ProductSkuRepo;
import com.thinkwin.goodscenter.vo.ProductSkuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "productSkuRepo")
public class DefaultProductSkuRepo implements ProductSkuRepo {

	@Autowired
	ProductSkuMapper productSkuMapper;

	@Override
	public ProductSkuVo getSkuByCode(String sku) {
		productSkuMapper.selectByPrimaryKey(sku);
		return null;
	}
}
