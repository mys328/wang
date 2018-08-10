package com.thinkwin.pay.mapper;

import com.thinkwin.pay.model.Payment;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface PaymentMapper extends Mapper<Payment> {

	Payment selectPaymentByIdLocked(@Param("orderId")String orderId);

	Payment getPaymentById(@Param("orderId")String orderId);

	int updatePaymentById(@Param("payment")Payment payment);

}