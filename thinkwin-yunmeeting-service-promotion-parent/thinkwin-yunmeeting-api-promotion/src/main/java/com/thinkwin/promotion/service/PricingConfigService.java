package com.thinkwin.promotion.service;

import com.thinkwin.common.dto.promotion.PricingConfigDto;
import com.thinkwin.common.model.promotion.PricingConfig;

public interface PricingConfigService {
	/**
	 * 获取定价配置
     * @param conId 配置Id,默认不传值null，获取新的定价配置
	 * @return
	 */
	PricingConfig getPricingConfig(String conId);

	/**
	 * 获取定价配置
	 * @return
	 */
	PricingConfigDto getPricingConfig();

	/**
	 * 更新定价配置
	 * @param pricingConfig
	 * @return
	 */
	boolean updatePricingConfig(PricingConfigDto pricingConfig);

    /**
     * 使当前新的定价配置失效
     * @return
     */
    boolean updateLastPricingConfig();
}
