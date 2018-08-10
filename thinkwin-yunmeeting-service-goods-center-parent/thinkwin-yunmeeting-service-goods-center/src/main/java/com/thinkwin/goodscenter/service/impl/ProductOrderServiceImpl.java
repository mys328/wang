
package com.thinkwin.goodscenter.service.impl;

import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.vo.ProductSkuDto;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.goodscenter.dataview.ProductPackSkuView;
import com.thinkwin.goodscenter.dataview.ProductSkuView;
import com.thinkwin.goodscenter.mapper.*;
import com.thinkwin.goodscenter.service.ProductOrderService;
import com.thinkwin.goodscenter.service.ProductPackSkuService;
import com.thinkwin.goodscenter.vo.ProductPackVo;
import com.thinkwin.goodscenter.vo.ProductSpecVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/*
  *
  *  订单展示续费升级实现
  *  开发人员:daipengkai
  *  创建时间:2017/8/22
  *
  */
@Service("productOrderService")
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    ProductPackCategoryMapper productPackCategoryMapper;

    @Autowired
    ProductPackMapper productPackMapper;

    @Autowired
    ProductPackSkuMapper productPackSkuMapper;

    @Autowired
    ProductPackLineMapper productPackLineMapper;
    @Autowired
    CategoryItemMapper categoryItemMapper;

    @Autowired
    ProductMapper productMapper;
    @Autowired
    CommodityItemMapper commodityItemMapper;
    @Autowired
    UomDefMapper uomDefMapper;
    @Autowired
    ProductPackSkuService productPackSkuService;

    @Autowired
    ProductSkuViewMapper productSkuViewMapper;

    @Autowired
    ProductPackSkuViewMapper productPackSkuViewMapper;
    @Autowired
    ProductSkuMapper productSkuMapper;
    @Autowired
    ProductPackUpgradeRuleMapper productPackUpgradeRuleMapper;

    @Autowired
    SaasTenantService saasTenantService;

    @Override
    public Map<String, Object> selectOrderInformation(SaasTenant saasTenant) {
        Map<String, Object> objMap = new HashMap();
        List<ProductPackVo> packVos = new ArrayList<ProductPackVo>();
        Example example = new Example(ProductPack.class);
        example.setOrderByClause("sort_order");
        List<ProductPack> packs = this.productPackMapper.selectByExample(example);
        for(ProductPack pack : packs){
          //专业版本价格信息
          List<ProductPackSku> packSkus = this.productPackSkuMapper.selectCommodityYearPrice(pack.getPackageId());
          for (ProductPackSku packSku : packSkus) {
              Example itemExample = new Example(CategoryItem.class);
              Example.Criteria criteria = itemExample.createCriteria();
              //免费版本
              if(pack.getSortOrder() == 0){
                  //获取容量
                  ProductPackVo packVo = selectCommodityInfo(packSku);
                  List<String> list = new ArrayList<String>();
                          criteria.andEqualTo("categoryCode",pack.getCategoryCode());
                          List<CategoryItem> items = this.categoryItemMapper.selectByExample(itemExample);
                          for(CategoryItem item : items){
                              list.add(item.getItemName());
                          }
                  //排序
                  Collections.sort(list, new Comparator(){
                      @Override
                      public int compare(Object o1, Object o2) {
                          String stu1=(String)o1;
                          String stu2=(String)o2;
                          if(stu1.length()>stu2.length()){
                              return 1;
                          }else if(stu1.length()==stu2.length()){
                              return 0;
                          }else{
                              return -1;
                          }
                      }
                  });
                 String success = isCurrentVersion(saasTenant);
                 if(success.equals(packSku.getSku())){
                     packVo.setIsFront("1");
                 }else{
                     packVo.setIsFront("0");
                 }
                 packVo.setItems(list);
                 objMap.put("free",packVo);
             }else{
                 //专业版
                  //获取容量
                  ProductPackVo packVo = selectCommodityInfo(packSku);
                  List<String> list = new ArrayList<String>();
                          criteria.andEqualTo("categoryCode",pack.getCategoryCode());
                          List<CategoryItem> items = this.categoryItemMapper.selectByExample(itemExample);
                          for(CategoryItem item : items){
                              list.add(item.getItemName());
                          }
                      //排序
                      Collections.sort(list, new Comparator(){
                          @Override
                          public int compare(Object o1, Object o2) {
                              String stu1=(String)o1;
                              String stu2=(String)o2;
                              if(stu1.length()>stu2.length()){
                                  return 1;
                              }else if(stu1.length()==stu2.length()){
                                  return 0;
                              }else{
                                  return -1;
                              }
                          }
                      });
                  packVo.setItems(list);
                  String success = isCurrentVersion(saasTenant);
                  if(success.equals(packSku.getSku())){
                      packVo.setIsFront("1");
                  }else{
                      packVo.setIsFront("0");
                  }
                  packVos.add(packVo);
              }
          }
        }
        objMap.put("pay",packVos);
        return objMap;
    }

    @Override
    public Map<String, Object> selectOrderInformationModify(SaasTenant saasTenant) {
        Map<String,Object> map = new HashMap<String,Object>();
        List<ProductPackCategory> categorys = this.productPackCategoryMapper.selectAll();
        for(ProductPackCategory category : categorys){
            ProductPackVo vo = new ProductPackVo();
            List<String> list = new ArrayList<String>();
            /*Example example = new Example(CommodityItem.class);
            example.orderBy("sort_order");
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("categoryCode",category.getCategoryCode());*/
            List<CommodityItem> commodityItems = this.commodityItemMapper.selectCommodityItemAll(category.getCategoryCode());
            for(CommodityItem item : commodityItems){
                list.add(item.getItemName());
            }
            vo.setId(category.getCategoryCode());
            vo.setItems(list);
            if(category.getSortOrder() == 0){
                vo.setPrice("0");
                map.put("free",vo);
            }else{
                List<ProductSkuDto> dtos = this.productPackSkuService.getAllSku();
                for (ProductSkuDto dto : dtos){
                    if("102".equals(dto.getSku())){
                        vo.setPrice(dto.getPrice()+"");
                    }
                    if("100".equals(dto.getSku())){
                        vo.setRommPrice(dto.getPrice()+"");
                    }
                    if("101".equals(dto.getSku())){
                        vo.setSpacePrice(dto.getPrice()+"");
                    }
                }
                map.put("pay",vo);
            }
        }
        return map;
    }




    @Override
    public Map<String, Object> selectProductIdInfoAndList(String productId,SaasTenant saasTenant) {

        Map<String, Object> objMap = new HashMap();
        //获取升级或续费的套餐
        ProductPackSku packSku = this.productPackSkuMapper.selectByPrimaryKey(productId);
        List<ProductSpecVo> list = new ArrayList<ProductSpecVo>();
        //获取订单版本
        String pro = productId.substring(0,2);
        String pr = saasTenant.getBasePackageType().substring(0,2);
        if(pro.equals(pr)&& !"1000".equals(saasTenant.getBasePackageType())){
            ProductSpecVo vo = new ProductSpecVo();
            //获取人数信息
            Example lineExample = new Example(ProductPackLine.class);
            Example.Criteria criteria1 = lineExample.createCriteria();
            criteria1.andEqualTo("productPackId",packSku.getProductPackId());
            List<ProductPackLine> lines = this.productPackLineMapper.selectByExample(lineExample);
            //获取商品id
            List<ProductPackSku> packSkus = this.productPackSkuMapper.selectCommodityYearPrice(packSku.getProductPackId());
            vo.setId(packSkus.get(0).getSku());
            //获取套餐附带的会议室
            for(ProductPackLine line : lines){
                if(line.getSortOrder() == 0){
                    vo.setVersion("专业版（"+line.getQty()+"人）");
                }
                if(line.getSortOrder() == 1){
                    vo.setRoom(line.getQty()+"");
                }
                if(line.getSortOrder() == 2){
                    vo.setSpace(line.getQty()+"");
                }
            }
            list.add(vo);
        }else {
            //获取用户当前版本信息
            ProductPackSku packSku1 = this.productPackSkuMapper.selectByPrimaryKey(saasTenant.getBasePackageType());
            //获取当前版本以上的商品
            Example ruleExample = new Example(ProductPackUpgradeRule.class);
            Example.Criteria ruleCriteria = ruleExample.createCriteria();
            ruleCriteria.andEqualTo("productPack", packSku1.getProductPackId());
            List<ProductPackUpgradeRule> rules = this.productPackUpgradeRuleMapper.selectByExample(ruleExample);
            List<String> list1 = new ArrayList<String>();
            for (ProductPackUpgradeRule rule : rules) {
                list1.add(rule.getUpgradeTo());
            }

            //获取专业版版本列表去除较低的版本
            Example packExample = new Example(ProductPack.class);
            packExample.setOrderByClause("sort_order");
            Example.Criteria criteria = packExample.createCriteria();
            criteria.andNotEqualTo("sortOrder", 0).andEqualTo("status", 1).andIn("packageId", list1);
            List<ProductPack> packs = this.productPackMapper.selectByExample(packExample);

            for (ProductPack pack : packs) {

                //获取版本信息
                ProductSpecVo vo = new ProductSpecVo();
                Example example = new Example(ProductPackCategory.class);
                example.or().andEqualTo("categoryCode", pack.getCategoryCode());
                List<ProductPackCategory> category = this.productPackCategoryMapper.selectByExample(example);
                //获取人数信息
                Example lineExample = new Example(ProductPackLine.class);
                Example.Criteria criteria1 = lineExample.createCriteria();
                criteria1.andEqualTo("productPackId", pack.getPackageId());
                List<ProductPackLine> lines = this.productPackLineMapper.selectByExample(lineExample);
                //获取商品id
                List<ProductPackSku> packSkus = this.productPackSkuMapper.selectCommodityYearPrice(pack.getPackageId());
                vo.setId(packSkus.get(0).getSku());
                //获取套餐附带的会议室
                for (ProductPackLine line : lines) {
                    if (line.getSortOrder() == 0) {
                        vo.setVersion(category.get(0).getCategoryName() + "（" + line.getQty() + "人）");
                    }
                    if (line.getSortOrder() == 1) {
                        vo.setRoom(line.getQty() + "");
                    }
                    if (line.getSortOrder() == 2) {
                        vo.setSpace(line.getQty() + "");
                    }
                }
                list.add(vo);
            }

        }

        objMap.put("list",list);
        List<ProductSpecVo> specVos = selectProductPackSkuByIdInfo(productId,saasTenant);
        objMap.put("info",specVos);
        //获取单独套餐 会议室 空间 升级包
        Example lineExample = new Example(ProductPackLine.class);
        lineExample.setOrderByClause("sort_order");
        Example.Criteria criteria1 = lineExample.createCriteria();
        criteria1.andEqualTo("productPackId",packSku.getProductPackId());
        List<ProductPackLine> lines = this.productPackLineMapper.selectByExample(lineExample);
        for(ProductPackLine line : lines){
            Example example = new Example(ProductSku.class);
            example.setOrderByClause("sort_order");
            Example.Criteria criteria0 = example.createCriteria();
            criteria0.andEqualTo("status",1).andEqualTo("productId",line.getProductId());
            List<ProductSku> productSkus = this.productSkuMapper.selectByExample(example);
            if(line.getSortOrder() == 1){
                objMap.put("meetingId",productSkus.get(0).getSku());
            }
            if(line.getSortOrder() == 2){
                objMap.put("spaceId",productSkus.get(0).getSku());
            }
        }
        //获取租户付费时价格和到期时间
        if("1000".equals(saasTenant.getBasePackageType())){
            objMap.put("lastPrice","0");
            objMap.put("lastDay","");
        }else{
            ProductPackSku pack = this.productPackSkuMapper.selectByPrimaryKey(saasTenant.getBasePackageType());
            objMap.put("lastPrice",pack.getSalePrice());
            objMap.put("lastDay",saasTenant.getBasePackageExpir());
        }


        return objMap;
    }

    @Override
    public List<ProductSpecVo> selectProductByIdInfo(String productId,SaasTenant saasTenant) {
       return selectProductPackSkuByIdInfo(productId,saasTenant);
    }

    @Override
    public Map<String,Integer> selectCommodityByIdInfo(String suk) {

        Map<String, Integer> map = new HashMap();
        //查看当前商品
        ProductPackSku packSku = this.productPackSkuMapper.selectByPrimaryKey(suk);
        Example example = new Example(ProductPackLine.class);
        example.setOrderByClause("sort_order");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productPackId",packSku.getProductPackId());
        List<ProductPackLine> lines = this.productPackLineMapper.selectByExample(example);
        for(ProductPackLine line : lines){
            //人员
            if(line.getSortOrder() == 0){
                map.put("person",line.getQty());
            }
            //会议室
            if(line.getSortOrder() == 1){
                map.put("meetingRoom",line.getQty());
            }
            //磁盘空间
            if(line.getSortOrder() == 2){
                map.put("space",line.getQty());
            }
        }
        return map;
    }

    @Override
    public List<ProductSkuView> getProductSkuByCode(Collection<String> skus) {
        if(CollectionUtils.isEmpty(skus)){
            return null;
        }


        List<String> params = new ArrayList<String>();

        for(String sku : skus){
            if(StringUtils.isNotBlank(sku)){
                params.add(sku);
            }
        }

        if(CollectionUtils.isEmpty(params)){
            return null;
        }

        List<ProductSkuView> views = productSkuViewMapper.getProductSkuByCode(params);

        for(ProductSkuView view : views){
            view.setUomValue(StringUtils.split("/")[0]);
        }

        return views;
    }

    @Override
    public List<ProductPackSkuView> getProductPackByCode(Collection<String> skus) {
        if(CollectionUtils.isEmpty(skus)){
            return null;
        }

        List<String> params = new ArrayList<String>();

        for(String sku : skus){
            if(StringUtils.isNotBlank(sku)){
                params.add(sku);
            }
        }

        if(CollectionUtils.isEmpty(params)){
            return null;
        }

        return productPackSkuViewMapper.getProductPackSkuByCode(skus);
    }


    /**
     * 获取当前版本所对应的功能信息
     * @return
     */
   public ProductPackVo selectCommodityInfo(ProductPackSku packSku){

       List<String> list = new ArrayList<String>();
       ProductPackVo packVo = new ProductPackVo();
       packVo.setId(packSku.getSku());
       //商品价格
       packVo.setPrice(price(packSku.getSalePrice()+""));
       //获取商品详细信息
       Example example = new Example(ProductPackLine.class);
       example.setOrderByClause("sort_order");
       Example.Criteria criteria = example.createCriteria();
       criteria.andEqualTo("productPackId",packSku.getProductPackId());
       List<ProductPackLine> packLines = this.productPackLineMapper.selectByExample(example);
       //获取商品定义分类
       List<Product> products = this.productMapper.selectAll();
       for(ProductPackLine line : packLines){
           for(Product product : products) {
               if(line.getProductId().equals(product.getProductId())){
                  UomDef uomDef = this.uomDefMapper.selectByPrimaryKey(line.getProductUom());
                  String str = "";
                  str =product.getProductDesc()+line.getQty()+uomDef.getUomName();
                  list.add(str);
               }
           }
       }
       packVo.setFunctionList(list);
       return packVo;
   }


    /**
     * 根据版本id获取每年的价格
     * @return
     */
  public List<ProductSpecVo> selectProductPackSkuByIdInfo(String productId,SaasTenant saasTenant){


      ProductPackSku packSku = this.productPackSkuMapper.selectByPrimaryKey(productId);
      String product = packSku.getProductPackId();

      List<ProductSpecVo> specVos = new ArrayList<ProductSpecVo>();
      //获取订单版本
      String pro = productId.substring(0,2);
      String pr = saasTenant.getBasePackageType().substring(0,2);
      //判断用户是升级还是续费如果是当前版本或者免费版本则获取一到三年的价格
      if("1000".equals(saasTenant.getBasePackageType())||pro.equals(pr)) {
          //获取一到三年的价格和赠送的会议室空间数量
          Example skuExample = new Example(ProductPackSku.class);
          skuExample.setOrderByClause("sort_order DESC");
          Example.Criteria criteria = skuExample.createCriteria();
          criteria.andEqualTo("status", 1).andEqualTo("productPackId", product);
          List<ProductPackSku> skus = this.productPackSkuMapper.selectByExample(skuExample);
          for (ProductPackSku sku : skus) {
              ProductSpecVo specVo = new ProductSpecVo();
              specVo.setId(sku.getSku());
              specVo.setPrice(price(sku.getSalePrice()+""));
              specVo.setVersion(sku.getSkuDesc());
              specVo.setDiscount(sku.getDiscount());
              specVo.setDiscountTip(sku.getDiscountTip());
              specVos.add(specVo);
          }
      }else{
          //用户生级时获取一年的价格
          List<ProductPackSku> packSkus = this.productPackSkuMapper.selectCommodityYearPrice(product);
          for (ProductPackSku sku : packSkus) {
              ProductSpecVo specVo = new ProductSpecVo();
              specVo.setId(sku.getSku());
              specVo.setPrice(price(sku.getSalePrice()+""));
              specVo.setVersion(sku.getSkuDesc());
              specVo.setDiscount(sku.getDiscount());
              specVo.setDiscountTip(sku.getDiscountTip());
              specVos.add(specVo);
          }


      }
      return specVos;
  }

    /**
     * 获取当前版本信息
     * @param saasTenant
     * @return
     */
  public String isCurrentVersion(SaasTenant saasTenant){
       ProductPackSku sku = this.productPackSkuMapper.selectByPrimaryKey(saasTenant.getBasePackageType());
       List<ProductPackSku> packSkus = this.productPackSkuMapper.selectCommodityYearPrice(sku.getProductPackId());
      return packSkus.get(0).getSku();
  }


    /**
     * 根据sku查询sku信息
     * @param productId
     * @return
     */
    public ProductPackSku selectProductPackSkuBySku(String productId){
        if(StringUtils.isNotBlank(productId)){
            return productPackSkuMapper.selectByPrimaryKey(productId);
        }
        return null;
    }

    @Override
    public List<ProductSku> selectProductSkuInfo() {
       Example example = new Example(ProductSku.class);
       Example.Criteria criteria = example.createCriteria();
       criteria.andEqualTo("status",1);
       return this.productSkuMapper.selectByExample(example);

    }

    @Override
    public List<String> getFutureProductPack(String tenantId) {
        SaasTenant saasTenant = saasTenantService.selectByIdSaasTenantInfo(tenantId);

        if(saasTenant == null){
            return null;
        }

        ProductPackSku sku = productPackSkuMapper.selectByPrimaryKey(saasTenant.getBasePackageType());

        Example example = new Example(ProductPackUpgradeRule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productPack", sku.getProductPackId());

        List<String> skus = new ArrayList<String>();
        List<ProductPackUpgradeRule> rules = productPackUpgradeRuleMapper.selectByExample(example);
        for(ProductPackUpgradeRule rule : rules){
            skus.add(rule.getUpgradeTo());
        }

        return skus;
    }

    /**
     * 计算价格
     * @param salePrice
     * @return
     */
    public String price(String salePrice){
        String price = String.valueOf(salePrice);
        String str = "";
        String in = price.substring(price.length() - 2,price.length() - 1);
        if("0".equals(in)){
            str = price.substring(0,price.length() - 3);
        }else{
            str = price;
        }

        return str ;
    }
}

