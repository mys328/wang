//'use strict';
    var pay_state;//支付状态(該支付狀態必須放在準備函數外部)
$(function () {
    var order_id = $('#orderId').val();//订单id
    var proof_img_url='';
    var tableHtml = '\n     <table  class="table">\n            <thead>\n              <tr>\n                <th>产品名称</th>\n        <th>员工人数</th>\n         <th>会议室数</th>\n                <th>存储空间</th>\n                <th>有效时间</th>\n                <th> 服务价格（元）</th>\n              </tr>\n            </thead>\n            <tbody>\n              <tr>\n                <td><span>{{orderSubject}}</span></td>\n            <td><span>{{items[2].total}}人</span>    <span style="color: #888"  ng-if="items[2].giveaway&&orderType!=1">(含赠送{{items[2].giveaway}}人)</span></td>\n     <td><span>\n                  {{items[0].total}}间 </span>                 <span style="color: #888"  ng-if="items[0].free&&items[0].free!=0&&orderType!=1">(含免费{{items[0].free}}间<span ng-if="items[0].giveaway">,赠送{{items[0].giveaway}}间</span>)</span>\n                </td>\n                <td><span>\n                  {{items[1].total}}GB\n</span>                   <span style="color: #888"  ng-if="items[1].free&&items[1].free!=0&&orderType!=1">(含免费{{items[1].free}}GB<span ng-if="items[1].giveaway">,赠送{{items[1].giveaway}}GB</span>)</span>\n                </td>\n                <td>\n                  <span>{{rentStart|date:\'YYYY-MM-DD\'}}</span>\n                  \u81F3\n                  <span>{{rentEnd|date:\'YYYY-MM-DD\'}}</span>\n                </td>\n                <td><span>{{totalPrice}}</span></td>\n              </tr>\n            </tbody>\n          </table>';
    dataInit(order_id, function (result) {
        console.log(result);
        var table_list = result.data;
        if (result.ifSuc == 1) {
          countdown = setInterval(function () {
              lxfEndtime(Number(result.data.createTime)+86400000, '.countdown');
          }, 60);
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
          $('.free .content-table .table-box').html(soda(tableHtml, table_list));
          $('.order-number span:last-child').text(result.data.orderSn);
          $('.order-time .order-time-text').html(getMyDate(Number(result.data.createTime)));//订单预定的时间
          $('.order-number-text').text(result.data.orderSn);
          pay_state=result.data.status;//支付状态
            //页面数据初始化
          switch(pay_state){
                case 0:
                    $('.top-right').show();
                    $('.order-state-text').text('等待支付');
                    $('.pay-btn').text('立即支付');
                    $('.btn-up-photo').hide();
                    break;
                case 1:
                    $('.order-state-text').text('已完成');
                    $('.countdown').hide();
                    clearInterval(countdown);
                    break;
                case 2:
                    $('.order-state-text').text('已退款');
                    $('.top-right').hide();
                    clearInterval(countdown);
                    break;
                case 8:

                    proof_img_url =  result.data.certImageUrl;
                    $('.order-state-text').text('等待银行汇款');
                    $('.btn-up-photo').show();
                    $('.pay-btn').hide();
                    $('.top-right').show();
                    $('.top-right').remove('.pay-btn');
                    $('.proof-explain').show();
                    //查看凭证(初始化)
                    if(proof_img_url!=''&&proof_img_url!=null){
                        $('.check-proof').css({'display':'inline-block'});
                        $('.top-right .check-proof').show();
                        $('#previewModal .modal-body img').attr('src',proof_img_url);
                    }else{
                        $('.top-right .check-proof').hide();
                    };
                    break;
                case 99:
                    console.log(pay_state);
                    $('.order-state-text').text('交易关闭');
                    clearInterval(countdown);
                    break;
                default:
                    return true;
          }
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
    //立即支付
    $('.top-right button.pay-btn').click(function () {
        window.location.href = '/order/prePay?token='+fetchs.token+'&orderId='+order_id;
    });
    //取消支付
    $('.top-right button.cancel-order').click(function () {
        fetchs.post('/order/cancel', { 'orderId': order_id }, function (result) {
            if (result.ifSuc == 1) {
                $('.top-right').empty();
                clearInterval(countdown);
                $('.countdown').hide();
                $('.order-state-text').text('交易关闭');
                $('.proof-explain').hide();
                notify('success', '操作成功');
            }else if(result.code==-1){
                notify('danger','获取数据失败，请检查网络后重试');
            }else{
                notify('danger',result.msg);
            };
        });
    });
   /* //查看凭证(初始化)
    if(proof_img_url!=''){
        $('.check-proof').css({'display':'inline-block'});
        $('.top-right .check-proof').show();
        $('#previewModal .modal-body img').attr('src',proof_img_url);
    };*/
    //上传凭证
    $('.btn-up-photo input').on('change', function () {
        var formData = new FormData($('.imgUpload')[0]);
        var file = $(this).get(0).files[0];
        if (file) {
          var filepath = $(".btn-up-photo input[name='fileName']").val();
          var extStart = filepath.lastIndexOf(".");
          var ext = filepath.substring(extStart, filepath.length).toUpperCase();
          if (ext != ".BMP" && ext != ".PNG" && ext != ".JPEG" && ext != ".JPG") {
            notify('danger', '仅支持jpg、jpeg、png、bmp格式');
          } else {
            if (file.size > 5 * 1024 * 1024) {
              notify('danger', '上传的文件大小不能超过5M');
              return false;
            } else {
              //var blob = URL.createObjectURL(file); //将本地图片的路径转成可用的路径
              var reader = new FileReader();
              reader.readAsDataURL(file);
              reader.onload = function (e) {
                console.log(file); //图片流信息e.target.result
                fetchs.uploadMixture('/pay/updateCert',{'file':file,'orderId':order_id,}, function (result) {
                  console.log(result);
                  proof_img_url = result.data.fileUrl;
                  if (result.ifSuc == 1) {
                      notify('success','保存凭证成功');
                      $('.check-proof').css({'display': 'block'});
                      $('.top-right .check-proof').show();
                  }else if(result.code==-1){
                      notify('danger','获取数据失败，请检查网络后重试');
                  }else{
                      notify('danger','保存凭证失败');
                  };
                });
              };
            };
          }
        } else if (file == undefined) {
          $('.img-init').empty();
          $('.img-init').append('<img src="' + proof_img_url + '">');
        } else {
          $('.img-init').text('选择头像');
        }
    });
    //查看凭证
    $('.top-right .check-proof').click(function () {
      if(proof_img_url==''){
          $('.top-right .check-proof').hide();
      }else{
          $('.top-right .check-proof').show();
          $('#previewModal .modal-body img').attr('src',proof_img_url);
      };
    })
    //阻止浏览器回退
    if (window.history && window.history.pushState) {
        $(window).on('popstate', function () {
            window.history.pushState('forward', null, '#');
            window.history.forward(1);
        });
    }
    window.history.pushState('forward', null, '#'); //在IE中必须得有这两行
    window.history.forward(1);
});
var countdown;//倒计时
function lxfEndtime(endtime, $ele) {
  //var endtime = new Date(date).getTime(); //取结束日期(毫秒值)//
  var nowtime = new Date().getTime(); //今天的日期(毫秒值)
  var youtime = endtime - nowtime; //还有多久(毫秒值)
  var seconds = youtime / 1000; //秒
  var minutes = Math.floor(seconds / 60); //分
  var hours = Math.floor(minutes / 60); //小时
  var days = Math.floor(hours / 24); //天
  var CDay = days;
  var CHour = hours % 24;
  var CMinute = minutes % 60;
  var CSecond = Math.floor(seconds % 60); //"%"是取余运算，可以理解为60进一后取余数，然后只要余数。
  var c = new Date();
  var millseconds = c.getMilliseconds();
  var Cmillseconds = Math.floor(millseconds % 100);
  if (CSecond < 10) {
    //如果秒数为单数，则前面补零
    CSecond = "0" + CSecond;
  }
  if (CMinute < 10) {
    //如果分钟数为单数，则前面补零
    CMinute = "0" + CMinute;
  }
  if (CHour < 10) {
    //如果小时数为单数，则前面补零
    CHour = "0" + CHour;
  }
  /*   if(Cmillseconds<10) {//如果毫秒数为单数，则前面补零
      Cmillseconds="0"+Cmillseconds;
     }*/
  if (endtime <= nowtime) {
    $($ele).hide(); //如果结束日期小于当前日期就提示过期啦
    clearInterval(countdown);
    if(pay_state>8){
        $('.top-right').hide();
        $('.order-state-text').text('交易关闭');
    }
  } else {
    $($ele).html("请于<span>" + CHour + "</span>小时<span>" + CMinute + "</span>分钟<span>" + CSecond + "</span>秒内完成交易，逾期交易自动关闭。"); //<span class='timen'>"+Cmillseconds+"</span>
  }
}
//获得年月日 时 分 秒
function getMyDate(str) {
  var oDate = new Date(str),
      oYear = oDate.getFullYear(),
      oMonth = oDate.getMonth() + 1,
      oDay = oDate.getDate(),
      oHour = oDate.getHours(),
      oMin = oDate.getMinutes(),
      oSen = oDate.getSeconds(),
      oTime = oYear + '年' + getzf(oMonth) + '月' + getzf(oDay) + '日&nbsp&nbsp' + getzf(oHour) + ':' + getzf(oMin) ; //最后拼接时间+ ':' + getzf(oSen)
  return oTime;
};
//补0操作  
function getzf(num) {
  if (parseInt(num) < 10) {
    num = '0' + num;
  }
  return num;
}
//页面数据初始化
function dataInit(orderId, callBack) {
  fetchs.post('/order/get', { 'orderId': orderId }, function (data) {
    callBack(data);
  });
}
//# sourceMappingURL=unfilledOrder.js.map
