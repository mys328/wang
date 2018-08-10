package com.thinkwin.orders.service.impl;

import com.thinkwin.common.model.orders.Address;
import com.thinkwin.orders.mapper.AddressMapper;
import com.thinkwin.orders.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("addressService")
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> selectAddressList(Address address) {
        return this.addressMapper.select(address);
    }

    @Override
    public boolean insertAddress(Address address) {
        boolean success = false;
        if(address != null){
            Integer flag =  this.addressMapper.insert(address);
            if(flag > 0){
                success=true;
            }
        }
        return success;
    }

    @Override
    public boolean updateAddress(Address address) {
        boolean success = false;
        if(address != null){
            Integer flag =  this.addressMapper.updateByPrimaryKey(address);
            if(flag > 0){
                success=true;
            }
        }
        return success;
    }

    @Override
    public Address selectAddressById(String id) {
        return addressMapper.selectByPrimaryKey(id);
    }
}
