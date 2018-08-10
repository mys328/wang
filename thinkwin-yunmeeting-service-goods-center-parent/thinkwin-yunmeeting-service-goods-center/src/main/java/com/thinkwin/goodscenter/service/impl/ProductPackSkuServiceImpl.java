package com.thinkwin.goodscenter.service.impl;

import com.thinkwin.common.dto.promotion.CapacityConfig;
import com.thinkwin.common.model.db.CommodityItem;
import com.thinkwin.common.model.db.ProductPackSku;
import com.thinkwin.common.model.db.ProductSku;
import com.thinkwin.common.vo.ProductSkuDto;
import com.thinkwin.goodscenter.mapper.CommodityItemMapper;
import com.thinkwin.goodscenter.mapper.ProductPackSkuMapper;
import com.thinkwin.goodscenter.mapper.ProductSkuMapper;
import com.thinkwin.goodscenter.service.ProductPackSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("productPackSkuService")
public class ProductPackSkuServiceImpl implements ProductPackSkuService {

    @Autowired
    private ProductPackSkuMapper productPackSkuMapper;
    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    CommodityItemMapper commodityItemMapper;
    @Override
    public List<ProductPackSku> selectProductPackSkuList() {
        return productPackSkuMapper.selectAll();
    }

    @Override
    public List<ProductSkuDto> getAllSku() {
        return productSkuMapper.selectAllProductSku();
    }

    @Override
    public boolean updateProductSku(ProductSku productSku) {
        if(productSku!=null){
            int i = productSkuMapper.updateByPrimaryKeySelective(productSku);
            if(i>0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateCommodityItem(List<CapacityConfig> capacityConfigs,Integer max) {

        try {
            for (CapacityConfig config : capacityConfigs){
                //会议室数量
                int i = 0;
                if("100".equals(config.getSku())){
                    CommodityItem items = this.commodityItemMapper.selectByPrimaryKey("8cb75430059e11e88e97000c2933e4d7");
                    items.setItemName("会议室数：免费"+config.getQty()+"间,可扩容");
                    i = i + this.commodityItemMapper.updateByPrimaryKey(items);
                    CommodityItem Commoditys = this.commodityItemMapper.selectByPrimaryKey("cad61dc0059d11e88e97000c2933e4d7");
                    Commoditys.setItemName("会议室数："+config.getQty()+"间以下");
                    i = i + this.commodityItemMapper.updateByPrimaryKey(Commoditys);

                }
                //储存空间
                if("101".equals(config.getSku())){

                    CommodityItem items = this.commodityItemMapper.selectByPrimaryKey("8cb75450059e11e88e97000c2933e4d7");
                    items.setItemName("储存空间：免费"+config.getQty()+"G,可扩容");
                    i = i + this.commodityItemMapper.updateByPrimaryKey(items);

                    CommodityItem Commoditys = this.commodityItemMapper.selectByPrimaryKey("cad61ddf059d11e88e97000c2933e4d7");
                    Commoditys.setItemName("储存空间："+config.getQty()+"G以下");
                    i = i + this.commodityItemMapper.updateByPrimaryKey(Commoditys);

                }
                //员工人数
                if("102".equals(config.getSku())){

                    CommodityItem items = this.commodityItemMapper.selectByPrimaryKey("8cb7537c059e11e88e97000c2933e4d7");
                    items.setItemName("员工：支持最多"+max+"人");
                    i = i +  this.commodityItemMapper.updateByPrimaryKey(items);
                    CommodityItem Commoditys = this.commodityItemMapper.selectByPrimaryKey("cad61d3e059d11e88e97000c2933e4d7");
                    Commoditys.setItemName("员工："+config.getQty()+"人以下");
                    i = i +  this.commodityItemMapper.updateByPrimaryKey(Commoditys);
                }
                if(i > 5){
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
