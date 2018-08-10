'use strict';

$(function () {
  var get_pay_state;//获取支付狀態
  var order_id = $('#orderId').val();//订单id
  var tableHtml = '<table class="table"><tbody><tr><td style="width:100px;color:#757575;padding-left: 0"><span style="margin-left: 16px;">购买清单</span></td><td style="width:330px">员工人数：<span style="color:#333333;">{{items[2].total}}人</span><span  ng-if="items[2].giveaway&&orderType!=1">(含赠送{{items[2].giveaway}}人)</span></td><td>会议室数：<span style="color:#333333;">{{items[0].total}}间<span ng-if="items[0].free&&items[0].free!=0&&orderType!=1">(含免费{{items[0].free}}间<span ng-if="items[0].giveaway&&items[0].giveaway!=0">,赠送{{items[0].giveaway}}间</span>)</span></span></td><td>存储空间：<span style="color:#333333;">{{items[1].total}}GB<span ng-if="items[1].free&&items[1].free!=0&&orderType!=1">(含免费{{items[1].free}}GB<span ng-if="items[1].giveaway&&items[1].giveaway!=0">,赠送{{items[1].giveaway}}GB</span>)</span></span></td></tr><tr><td></td><td>购买时长：<span style="color:#333333;"><span id="timeLong"></span>(<span ng-if="ServiceTermInfo">含赠送{{ServiceTermInfo.qty}}年,</span>有效期至{{rentEnd|date:\'YYYY-MM-DD\'}})</span></td><td></td></tr></tbody></table>';
  //页面数据初始化
  dataInit(order_id, function (result) {
    console.log(result);
    console.log(result);
    var table_list = result.data;
    if (result.ifSuc == 1) {
        if(table_list.items.length==1&&table_list.items[0].productName=='存储空间'){
            for(var i=0;i<1;i++){
                var table_total=table_list.items[0].total;
                var table_extra=table_list.items[0].extra;
                table_list.items[1] ={
                    'total':table_total,
                    'extra':table_extra
                };
                table_list.items[0].total='';
                table_list.items[0].extra='';
            }
        };
        $('.order-number').text(result.data.orderSn);
        $('.free .content-table').html(soda(tableHtml,table_list));
        $('.total-number').text('' + result.data.payPrice + '元');
        var str;
        if(result.data.days&&result.data.serviceTerm){
            str= result.data.serviceTerm+'年零'+result.data.days+'天';
        }else{
            if(result.data.days){
                str= result.data.days+'天';
            }
            if(result.data.serviceTerm){
                if(result.data.uom==0){
                    str=result.data.serviceTerm+'年';
                }else{
                    if(str=result.data.serviceTerm>12){
                        str=parseInt(result.data.serviceTerm/12)+'年零'+result.data.serviceTerm%12+'月';
                    }else{
                        str=result.data.serviceTerm+'月';
                    }
                }
            }
        }
        $('#timeLong').text(str);


    }else if(result.code==-1){
        notify('danger','获取数据失败，请检查网络后重试');
    }else if(result.ifSuc == 0){
        if(result.msg=="订单不存在"){
            notify('danger','该订单不存在');
        }
    }else{
        notify('danger','获取数据失败');
    };
  });
  //支付类型的效果
  var payment_state = 2; //订单支付的类型状态
  $('body .payment').on('click', 'a.text-center', function () {
    payment_state = $(this).attr('name');
    $(this).addClass('payment-active').siblings().removeClass('payment-active');
    $('.bank-pay-info').hide();
    if (payment_state == 4) {
      $('.bank-pay-info').show();
    }
  });
  //立即支付
  $('.pay-btn-box .pay-btn').click(function () {
      $.ajax({
          url: "/order/pay?userId="+fetchs.userId+"&orderId="+order_id+"&payChannel="+payment_state+"&token="+fetchs.token,
          async: false,
          success:function(result){
              if (result.ifSuc =='1') {
                  if(payment_state == 4){
                      window.location.href='/order/info?token='+fetchs.token+'&orderId='+ order_id;
                  }else if(result.data.tradeNo!==undefined||result.data.tradeNo!==''||result.data.tradeNo!==null) {
                      $('#paymentConfirmation').modal('show');
                      window.open('/pay/payOrder?token=' + fetchs.token + '&orderId=' + order_id + '&tradeNo=' + result.data.tradeNo)//或者$(form).submit()
                  };
              }else if(result.code==-1){
                  notify('danger','获取数据失败，请检查网络后重试');
              }else{
                  notify('danger','获取数据失败');
              };
          },
          error: function (result) {
              notify('danger','操作失败');
          }
      })
  });
  //遇到问题按钮触发
  $('.trouble-btn').click(function () {
    checkPayState(order_id, function (result) {
      console.log(result);
      if (result.statusCode == 1) {
          window.location.href= '/pay/status?token='+fetchs.token+'&orderId='+order_id;
      }else if(result.code==-1){
          notify('danger','获取数据失败，请检查网络后重试');
      }else if(result.statusCode == 0){
          window.location.href= '/pay/status?token='+fetchs.token+'&orderId='+order_id;
      }else if(result.statusCode == 99){
          window.location.href= '/pay/status?token='+fetchs.token+'&orderId='+order_id;
      }else{
          notify('danger','操作失败');
      };
    });
  });
  //支付成功的按钮触发
  $('.success-btn').click(function () {
    checkPayState(order_id, function (result) {
      console.log(result);
      if (result.statusCode == 1) {
        window.location.href= '/pay/status?token='+fetchs.token+'&orderId='+order_id;
      }else if(result.code==-1){
          notify('danger','获取数据失败，请检查网络后重试');
      }else if(result.statusCode == 0){
          window.location.href= '/pay/status?token='+fetchs.token+'&orderId='+order_id;
      }else if(result.statusCode == 99){
          window.location.href= '/pay/status?token='+fetchs.token+'&orderId='+order_id;
      }else{
          notify('danger','操作失败');
      };
    });
  });

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
//页面数据初始化
function dataInit(orderId, callBack) {
  fetchs.post('/order/get', { 'orderId': orderId }, function (data) {
    callBack(data);
  });
}
//查询订单支付状态
function checkPayState(orderId, callBack) {
  fetchs.post('/order/payStatus', { 'orderId': orderId }, function (data) {
    callBack(data);
  }); /*.fail(function(){
        callBack(500);
      });*/
};
//# sourceMappingURL=payOrder.js.map
