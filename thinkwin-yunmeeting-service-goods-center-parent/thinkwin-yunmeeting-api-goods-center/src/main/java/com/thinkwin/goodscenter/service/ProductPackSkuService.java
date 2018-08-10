package com.thinkwin.goodscenter.service;

import com.thinkwin.common.dto.promotion.CapacityConfig;
import com.thinkwin.common.model.db.ProductPackSku;
import com.thinkwin.common.model.db.ProductSku;
import com.thinkwin.common.vo.ProductSkuDto;

import java.util.List;

/**
 * sku 版本
 */
public interface ProductPackSkuService {
    /*
       * 查询所有sku版本
       * */
    List<ProductPackSku> selectProductPackSkuList();

    /**
     * 获取所有商品sku
     * @return
     */
    List<ProductSkuDto> getAllSku();

    /**
     * 更新商品sku
     * @return
     */
    boolean updateProductSku(ProductSku productSku);

    /**
     * 更改商品详情描述
     * @param capacityConfigs
     * @return
     */
    boolean updateCommodityItem(List<CapacityConfig> capacityConfigs,Integer max);

}
