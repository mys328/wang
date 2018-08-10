'use strict';
var spaceTime=0;  //记录距离有效期到期还有多少天
var roomPrice=0;  //记录会议室间数价格
var spacePrice=0;  //记录选取的容量的价格
var spaceNum=0;
var roomNum=0;
$(function () {
    fetchs.post('/orderauth/selectversionsinfo',{}, function (result) {
        if(result.ifSuc==1) {
         var oldTime=result.data.expirationTime;
         var now=new Date().getTime();
         var time=parseInt((oldTime-now)/(1000 * 60 * 60 * 24))+1;
         if(time>0){
           spaceTime = time;
         }
        }
    });


    //点击了空白区域
    $('body').on('click', function (e) {
        if (e.target.className == "form-check-input") {
            if (e.target.checked == true) {
                $(".discount").show();
            } else {
                $(".discount").hide();
            }
        }

        if (e.target.className == "rooms one") {
            restoreStyle(0, 0);
            $(".rooms.one").addClass("select");
            $(".rooms.one").html('3间<span class="selectedIcon"></span>');
            var price="<span>"+priceHtml(0,3)+"</span>";
            $(".roomPrice").html(price);
        }

        if (e.target.className == "rooms two") {
            restoreStyle(0, 0);
            $(".rooms.two").addClass("select");
            $(".rooms.two").html('5间<span class="selectedIcon"></span>');
            var price="<span>"+priceHtml(0,5)+"</span>";
            $(".roomPrice").html(price);
        }

        if (e.target.className == "rooms three") {
            restoreStyle(0, 0);
            $(".rooms.three").addClass("select");
            $(".rooms.three").html('10间<span class="selectedIcon"></span>');
            var price="<span>"+priceHtml(0,10)+"</span>";
            $(".roomPrice").html(price);
        }

        if (e.target.className == "space one") {
            restoreStyle(1, 0);
            $(".space.one").addClass("select");
            $(".space.one").html('5G<span class="selectedIcon"></span>');
            var price="<span>"+priceHtml(1,5)+"</span>";
            $(".spacePrice").html(price);
        }

        if (e.target.className == "space two") {
            restoreStyle(1, 0);
            $(".space.two").addClass("select");
            $(".space.two").html('10G<span class="selectedIcon"></span>');
            var price="<span>"+priceHtml(1,10)+"</span>";
            $(".spacePrice").html(price);
        }

        if (e.target.className == "space three") {
            restoreStyle(1, 0);
            $(".space.three").addClass("select");
            $(".space.three").html('15G<span class="selectedIcon"></span>');
            var price="<span>"+priceHtml(1,15)+"</span>";
            $(".spacePrice").html(price);
        }
    });

    //减少会议室数量
    $('#subRoom').on('click', function () {
        restoreStyle(0, 1);
        var number = parseInt($('#room-count').val());
        var price="";
        if (isNaN(number)) {} else {
            number -= 1;
            $('#addRoom').attr('disabled', false);
            if (number <= 0) {
                //数量<=3时候不可点击
                $(this).attr('disabled', true);
                $('#room-count').val('0');
                priceHtml(0,0);
                price="<span>0元</span>";
            }else  {
                $('#room-count').val(number.toString());
                price="<span>"+priceHtml(0,number)+"</span>";
            }
            $(".roomPrice").html(price);
        }
    });
    //增加会议室数量
    $('#addRoom').on('click', function () {
        restoreStyle(0, 1);
        var number = parseInt($('#room-count').val());
        if (isNaN(number)) {} else {
            number += 1;
            $('#subRoom').attr('disabled', false);
            if(number >= 100){
               $('#addRoom').attr('disabled', true);
               $('#room-count').val('100');
               var price="<span>"+priceHtml(0,100)+"</span>";
               $('#room-count').val(100);
            }else {
               var price="<span>"+priceHtml(0,number)+"</span>";
               $('#room-count').val(number.toString());
            }
            $(".roomPrice").html(price);
        }
    });
    //输入框值的变化
    $('#room-count').on('change', function () {
        restoreStyle(0, 1);
        var number = parseInt($('#room-count').val());
        var price="";
        if (isNaN(number) || number <= 0) {
            $('#room-count').val('0');
            $('#subRoom').attr('disabled', true);
            $('#addRoom').attr('disabled', false);
            priceHtml(0,0);
            price="<span>0元</span>";
        } else if (number >=100){
            $('#subRoom').attr('disabled', false);
            $('#addRoom').attr('disabled', true);
            $('#room-count').val('100');
            price="<span>"+priceHtml(0,100)+"</span>";
        }else {
            $('#subRoom').attr('disabled', false);
            $('#addRoom').attr('disabled', false);
            price="<span>"+priceHtml(0,number)+"</span>";
        }
        $(".roomPrice").html(price);
    });
    //减少存储空间数量
    $('#subMemonry').on('click', function () {
        $('#memonry-count').addClass("btn-selected");
        restoreStyle(1, 1);
        var number = parseInt($('#memonry-count').val());
        if (isNaN(number)) {} else {
            number -= 5;
            $('#addMemonry').attr('disabled', false);
            var price="";
            if (number <= 0) {
                //数量<=5时候不可点击
                $(this).attr('disabled', true);
                $('#memonry-count').val('0');
                priceHtml(1,0)
                price="<span>0元</span>";
            }else {
                $('#memonry-count').val(number.toString());
                price="<span>"+priceHtml(1,number)+"</span>";
            }
            $(".spacePrice").html(price);
        }
    });
    //增加存储空间数量
    $('#addMemonry').on('click', function () {
        restoreStyle(1, 1);
        var number = parseInt($('#memonry-count').val());
        if (isNaN(number)) {} else {
            number += 5;
            $('#subMemonry').attr('disabled', false);
            if(number >= 100){
                $('#addMemonry').attr('disabled', true);
                $('#memonry-count').val(100);
                var price="<span>"+priceHtml(1,100)+"</span>";
            }else {
                $('#memonry-count').val(number.toString());
                var price="<span>"+priceHtml(1,number)+"</span>";
            }
            $(".spacePrice").html(price);
        }
    });
    //输入框值的变化
    $('#memonry-count').on('change', function () {
        restoreStyle(1, 1);
        $('#memonry-count').addClass("btn-selected");
        var number = parseInt($('#memonry-count').val());
        var price="";
        if (isNaN(number) || number <5) {
            $('#memonry-count').val('0');
            $('#subMemonry').attr('disabled', true);
            $('#addMemonry').attr('disabled', false);
            priceHtml(1,0);
            price="<span>0元</span>";
        } else if(number>=100){
           $('#memonry-count').val('100');
           $('#subMemonry').attr('disabled', false);
           $('#addMemonry').attr('disabled', true);
            price="<span>"+priceHtml(1,100)+"</span>";
        }else {
            var abc = Math.ceil(number / 5.0) * 5;
            $('#memonry-count').val(abc);
            $('#addMemonry').attr('disabled', false);
            $('#subMemonry').attr('disabled', false);
            price="<span>"+priceHtml(1,number)+"</span>";
        }
        $(".spacePrice").html(price);
    });
    
    function restoreStyle(tag1, tag2) {
        //隐藏点击效果
        if (tag1 == 0) {
            $(".rooms .selectedIcon").remove();
            $(".rooms").removeClass("select");
            if (tag2 == 0) {
                $('#room-count').val(0);
                $('#room-count').removeClass("btn-selected");
            } else {
                $('#room-count').addClass("btn-selected");
            }
        } else {
            $(".space .selectedIcon").remove();
            $(".space").removeClass("select");
            if (tag2 == 0) {
                $('#memonry-count').val(0);
                $('#memonry-count').removeClass("btn-selected");
            } else {
                $('#memonry-count').addClass("btn-selected");
            }
        }
    }
    function priceHtml(tag,num) {
      var price="";
      if(tag==0){//返回会议室价格的计算公式
        roomNum=num;
        roomPrice=num*150/365*spaceTime;
        price=num+"间×150元/间/365天×"+spaceTime+"天="+roomPrice.toFixed(2)+"元";
      }else {//返回存储空间价格的计算公式
        spaceNum=num;
        spacePrice=num/5*50/365*spaceTime;
        price=num+"G/5G×50元/365天×"+spaceTime+"天="+spacePrice.toFixed(2)+"元";
      }
      var total=(roomPrice+spacePrice).toFixed(2)+"元";
      $("#total").html(total);
      if(roomNum>0||spaceNum>0){
          $('.submitOrder').attr('disabled',false);
      }else {
          $('.submitOrder').attr('disabled',true);
      }
      return price;
    }

    //提交订单
    $("body").on("click",".submitOrder",function(){

        var roomSku=$("#roomNo").val(); //会议室商品编码
        var memonrySku=$("#spaceNo").val();//空间商品编码
        var totalPrice=(roomPrice+spacePrice).toFixed(2);
        // var total = price*discount + ((room-minRoom)*150) + ((memonry-minMemonry)/5*50);
        var arr = [];
        if(roomNum==0&&spaceNum==0){
            return;
        }
        if(roomNum>0){//会议室数量
            arr.push({productSku:roomSku?roomSku:'100',qty:roomNum})
        }
        if(spaceNum>=5){//空间大小
            arr.push({productSku:memonrySku?memonrySku:'101',qty:spaceNum/5})
        }
        var data = {payPrice:totalPrice.toString(),orderLines:arr,orderType:1};
        $.ajax({
            'type': 'POST',
            'url': '/order/new?token='+fetchs.token,
            'data': JSON.stringify(data),
            'contentType': 'application/json',
            success:function(res){
                if(res.ifSuc==1){
                    //拿到订单号,进行跳转
                  if(res.data.orderId !=null){
                    var orderId = res.data.orderId;
                    window.location.href='/order/prePay?token='+fetchs.token+'&orderId='+orderId;
                    }else {
                      notify('danger',"请求异常");
                  }
                }else {
                    notify('danger',res.msg);
                }
            },
            error:function (XMLHttpRequest,textStatus,errorThrown) {
                data = {
                    ifSuc:0,
                    code:-1,
                    msg:"网络连接失败，请检查网络"
                }
            }
        });
    })

});
//# sourceMappingURL=extension.js.map
