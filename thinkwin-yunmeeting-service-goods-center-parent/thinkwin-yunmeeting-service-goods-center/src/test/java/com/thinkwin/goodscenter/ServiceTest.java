package com.thinkwin.goodscenter;

import com.thinkwin.goodscenter.dataview.ProductPackSkuView;
import com.thinkwin.goodscenter.dataview.ProductSkuView;
import com.thinkwin.goodscenter.mapper.ProductPackSkuViewMapper;
import com.thinkwin.goodscenter.mapper.ProductSkuViewMapper;
import com.thinkwin.goodscenter.service.ProductPackRepo;
import com.thinkwin.goodscenter.service.ProductPackService;
import com.thinkwin.goodscenter.service.ProductRepo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations="classpath:spring-config.xml")
public class ServiceTest {

	@Autowired
	ProductRepo productRepo;

	@Autowired
	ProductPackRepo productPackRepo;

	@Autowired
	ProductPackService productPackService;

	@Autowired
	ProductSkuViewMapper productSkuViewMapper;

	@Autowired
	ProductPackSkuViewMapper productPackSkuViewMapper;

	@Test
	public void testProductSkuMapper(){
		List<ProductPackSkuView> view = productPackSkuViewMapper.getProductPackSkuByCode(Arrays.asList("2000"));
		Assert.assertNotNull(view);
	}

	@Test
	public void testProduct(){
		List<ProductSkuView> view = productSkuViewMapper.getProductSkuByCode(Arrays.asList("100"));
		Assert.assertNotNull(view);
	}
}
