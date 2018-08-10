'use strict';

$(function () {
  var get_pay_state;//支付狀態
  var order_id = $('#orderId').val();
  var tradeNo = $('#imgUrl').val();
  fetchs.post('/order/get', {'orderId':order_id}, function (result) {
    console.log(result);
    if(result.ifSuc==1){
        $('.order-version').text(result.data.orderSubject);
        $('.total').text(result.data.payPrice+'元');
    }else if(result.code==-1){
        notify('danger','获取数据失败，请检查网络后重试');
    }else{
        notify('danger','获取数据失败');
    };
  });
  $('.quick-mark-img img').attr('src','/pay/qrcode?token='+fetchs.token+'&tradeNo='+tradeNo);
  $('.payment-title .other-payment').click(function () {
      window.location.href='/order/prePay?token='+fetchs.token+'&orderId='+order_id;
  })
  get_pay_state=setInterval(function () {
     getState();
  },2000);
  //获取支付状态
  function getState() {
      fetchs.post('/order/payStatus',{'orderId':order_id},function (result) {
          console.log(result);
          if(result.statusCode==1){
             clearInterval(get_pay_state);
             window.location.href= '/pay/status?token='+fetchs.token+'&orderId='+order_id;
          }
      })
  }
});
//# sourceMappingURL=paymentWechat.js.map
