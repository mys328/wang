package com.thinkwin.orders.service;

import com.thinkwin.common.model.orders.Address;

import java.util.List;

/**
 * 发票收货地址
 */
public interface AddressService {

    /*
    * 根据条件查询
    * */
    List<Address> selectAddressList(Address address);

    /*
    * 添加地址
    * */
    boolean insertAddress(Address address);

    /*
    * 修改地址
    * */
    boolean updateAddress(Address address);

    /*
    * 根据id查询
    * */
    Address selectAddressById(String id);
}
