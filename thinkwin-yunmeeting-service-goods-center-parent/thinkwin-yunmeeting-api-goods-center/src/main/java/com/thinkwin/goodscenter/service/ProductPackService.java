package com.thinkwin.goodscenter.service;

import com.thinkwin.goodscenter.vo.ProductPackVo;

import java.util.List;

public interface ProductPackService {
    List<ProductPackVo> getAllServicePlan();
    ProductPackVo selectServicePlanById(String id);
}