package com.thinkwin.serialnumber.service;

import java.util.Map;

/**
  *  获取序列号接口
  *
  *  开发人员:daipengkai
  *  创建时间:2017/6/30
  *
  */
public interface SerialNumberService {

    /**
     *  获取序列号接口
     * @param businessType 业务类型 需定义（包含：订单，优惠券，代理商订单 等）
     * @param agents 如果是一级代理商订单需要map的get为oneAgent，二级代理商get为 twoAgent（注：平台订单和优惠卷不需要传递）其他待添加
     * @return
     */
    Object generateSerialNumber (String businessType ,Map<String,String> agents);
}
