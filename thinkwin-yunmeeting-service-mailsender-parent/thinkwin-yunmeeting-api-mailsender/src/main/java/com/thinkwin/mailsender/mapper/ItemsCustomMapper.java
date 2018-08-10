package com.thinkwin.mailsender.mapper;

import java.util.List;

import com.thinkwin.mailsender.po.ItemsCustom;
import com.thinkwin.mailsender.po.ItemsCustomQueryVo;



/**
 * Author:
 * Time  : 16-8-23 下午1:52
 */
public interface ItemsCustomMapper {

	List<ItemsCustom> getAllItemsLikeName(ItemsCustomQueryVo itemsCustomQueryVo) throws Exception;
}
