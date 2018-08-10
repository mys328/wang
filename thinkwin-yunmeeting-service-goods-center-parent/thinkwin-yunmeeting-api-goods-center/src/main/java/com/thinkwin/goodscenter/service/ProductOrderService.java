package com.thinkwin.goodscenter.service;

import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.ProductPackSku;
import com.thinkwin.common.model.db.ProductSku;
import com.thinkwin.goodscenter.dataview.ProductPackSkuView;
import com.thinkwin.goodscenter.dataview.ProductSkuView;
import com.thinkwin.goodscenter.vo.ProductSpecVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
  *
  *  订单展示续费升级
  *  开发人员:daipengkai
  *  创建时间:2017/8/22
  *
  */
public interface ProductOrderService {


    /**
     * 查看订单展示详情
     * @return
     */
    Map<String,Object> selectOrderInformation(SaasTenant saasTenant);

    /**
     * 根据商品信息查看商品列表和详情
     * @param productId
     * @return
     */
    Map<String,Object> selectProductIdInfoAndList(String productId,SaasTenant saasTenant);

    /**
     * 根据商品信息查看商品列表和详情
     * @param productId
     * @return
     */
    List<ProductSpecVo> selectProductByIdInfo(String productId,SaasTenant saasTenant);

    /**
     * 根据套餐id 查询当前套餐的会议室 人数  和空间
     * @param suk
     * @return
     */
    Map<String,Integer> selectCommodityByIdInfo(String suk);

    /**
     * 获取商品sku信息
     * @param skus 商品sku列表
     * @return
     */
    List<ProductSkuView> getProductSkuByCode(Collection<String> skus);

    /**
     * 获取套餐sku信息
     * @param skus 套餐sku列表
     * @return
     */
    List<ProductPackSkuView> getProductPackByCode(Collection<String> skus);

    /**
     * 根据sku查询sku信息
     * @param productId
     * @return
     */
    ProductPackSku selectProductPackSkuBySku(String productId);

    /**
     * 查看增容套餐
     * @return
     */
    List<ProductSku> selectProductSkuInfo();

    /**
     * 获取租户可升级套餐
     * @param tenantId
     * @return
     */
    List<String> getFutureProductPack(String tenantId);


    Map<String,Object> selectOrderInformationModify(SaasTenant saasTenant);

}
