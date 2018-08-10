package com.thinkwin.goodscenter.service.impl;

import com.thinkwin.goodscenter.service.ProductPackRepo;
import com.thinkwin.goodscenter.service.ProductPackService;
import com.thinkwin.goodscenter.vo.ProductPackVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "productPackService")
public class DefaultProductPackService implements ProductPackService {

	@Autowired
	ProductPackRepo productPackRepo;

	@Override
	public List<ProductPackVo> getAllServicePlan() {
		return null;
	}

	@Override
	public ProductPackVo selectServicePlanById(String id) {
		return null;
	}
}
