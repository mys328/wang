'use strict';

$(function () {
  var order_id = $('#orderId').val();
  var pay_status =$('#payStatus').val();
    if(pay_status==2) {//支付成功
        $('.payment-success').show();
        $('.status-label').hide();
        $('.invoice-center').click(function () {
            window.location.href = "/commodity/orderManger?token=" + fetchs.token;
        })
        jump(5);
    }else if(pay_status==1||pay_status==0){//已创建订单、未支付
      $('.payment-success').hide();
      $('.status-label').hide();
      $('.payment-error').show();
      $('.payment-error-btn').on('click','a',function () {
          var link_name = $(this).attr('name');
          switch(link_name){
              case '1':
                  window.location.href='/order/prePay?token='+fetchs.token+'&orderId='+order_id;
                  break;
              case '2':
                  window.location.href= '/order/info?token='+fetchs.token+'&orderId='+order_id;
                  break;
              case '3':
                  window.location.href= "/gotoConsolePage?token="+fetchs.token;
                  break;
              default:
                  return true;
          }
      })
  }else{
      notify('danger','操作失败');
  }
  //5秒倒计时跳转页面
  function jump(count) {
        var start = window.setTimeout(function () {
            count--;
            if (count >= 0) {
                $("#count").text(count);
                jump(count);
            } else {
                clearTimeout(start);
                window.location.href= '/order/info?token='+fetchs.token+'&orderId='+order_id;
            }
        }, 1000);
    }
});