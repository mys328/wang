package com.thinkwin.promotion.service.impl;

import com.alibaba.fastjson.JSON;
import com.thinkwin.common.dto.promotion.PricingConfigDto;
import com.thinkwin.common.dto.promotion.SkuPricingConfig;
import com.thinkwin.common.model.db.ProductSku;
import com.thinkwin.common.model.promotion.PricingConfig;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.goodscenter.service.ProductPackSkuService;
import com.thinkwin.promotion.constant.PromotionConstant;
import com.thinkwin.promotion.mapper.PricingConfigMapper;
import com.thinkwin.promotion.service.PricingConfigService;
import com.thinkwin.service.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service("pricingConfigService")
public class PricingConfigServiceImpl implements PricingConfigService {
	@Autowired
	PricingConfigMapper pricingConfigMapper;
	@Autowired
	ProductPackSkuService productPackSkuService;

	@Override
	public PricingConfig getPricingConfig(String conId) {
		Example ex = new Example(PricingConfig.class);
		if(conId!=null&&!"".equals(conId)){
			ex.createCriteria().andEqualTo("id",conId);
		}else{
			ex.setOrderByClause("create_time desc");
		}
		List<PricingConfig> pricingConfigs = pricingConfigMapper.selectByExample(ex);
		if(null!=pricingConfigs && pricingConfigs.size()>0){
//			return JSON.parseObject(pricingConfigs.get(0).getContent(), PricingConfigDto.class);
			return pricingConfigs.get(0);
		}
		return null;
	}

	@Override
	public PricingConfigDto getPricingConfig() {
		Example ex = new Example(PricingConfig.class);
		ex.setOrderByClause("create_time desc");
		List<PricingConfig> pricingConfigs = pricingConfigMapper.selectByExample(ex);
		if(null!=pricingConfigs && pricingConfigs.size()>0){
			PricingConfig config = pricingConfigs.get(0);
			PricingConfigDto pricingConfigDto = JSON.parseObject(config.getContent(), PricingConfigDto.class);
			pricingConfigDto.setId(config.getId());
			return pricingConfigDto;
		}
		return null;
	}

	@Override
	public boolean updatePricingConfig(PricingConfigDto pricingConfig) {
		boolean result = false;
		if(null != pricingConfig){
			String content = JSON.toJSONString(pricingConfig);
			PricingConfig pc = new PricingConfig();
			pc.setCreateTime(new Date());
			pc.setId(CreateUUIdUtil.Uuid());
			pc.setContent(content);
			pc.setCreaterId(TenantContext.getUserInfo().getUserId());
			pc.setStatus(PromotionConstant.PricingConfigStatus.ENABLE);
			int insert = pricingConfigMapper.insert(pc);
			if(insert>0){
				//同步更新商品库sku价格
				List<SkuPricingConfig> skuDiscount = pricingConfig.getSkuDiscount();
				if(skuDiscount!=null&&skuDiscount.size()>0){
					ProductSku sku = new ProductSku();
					for (SkuPricingConfig con : skuDiscount) {
						sku.setSku(con.getSku());
						sku.setListPrice(new BigDecimal(con.getConfig().getUnitPrice()));
						sku.setSalePrice(new BigDecimal(con.getConfig().getUnitPrice()));
						productPackSkuService.updateProductSku(sku);
					}
				}
				result = true;
			}
		}
		return result;
	}

    /**
     * 使当前新的定价配置失效
     *
     * @return
     */
    @Override
    public boolean updateLastPricingConfig() {
        PricingConfig pricingConfig=getPricingConfig(null);
        if(null!=pricingConfig){
            pricingConfig.setStatus(PromotionConstant.PricingConfigStatus.DISABLE);
           int f= pricingConfigMapper.updateByPrimaryKey(pricingConfig);
           return (f>0)? true: false;
        }
        return false;
    }
}
