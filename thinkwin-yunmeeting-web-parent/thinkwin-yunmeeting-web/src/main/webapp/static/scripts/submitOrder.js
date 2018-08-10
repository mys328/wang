$(function(){ 

    soda.prefix('ng-');
    var userInfo= JSON.parse(localStorage.getItem('userinfo'));
    if (userInfo==null) {
      userInfo = {
        userId:'123',
        token:'123'
      }
    };
    var price = 0.00; //价格
    var year = 1;
    var room = 3; //会议室数量
    var memonry = 5; //存储空间
    var minRoom = 3; //會議室免費數
    var minMemonry = 5;//存儲空間免費數
    var discount = 1; //折扣
    var selectPack = {}; //选择的套餐
    var roomSku = '100'; //会议室编号
    var memonrySku = '101'; //存储空间编号
    var lastPrice = 0; //之前版本剩餘金額
    var lastDay = 0; //之前版本剩餘天數
    var total = 0;

    var menu = `
    <span ng-repeat="item in data" class="dropdown-item" data-title="{{item.version}}" data-id="{{item.id}}" data-room="{{item.room}}" data-space="{{item.space}}">
      {{item.version}}
    </span>
    `;
    var sevice = `
      <span class="buy-year" ng-repeat="item in data"  data-id="{{item.id}}" data-price="{{item.price}}" data-discount="{{item.discount}}" data-title="{{item.version}}">
        <label class="discount" ng-if="item.discount != 1 && item.discount != null">{{item.discountTip}}</label>
        <label class="year-title">{{item.version}}</label>
        <span class="price">
          <sup>￥</sup>
          {{item.price}}
          <span class="unit" ng-if="item.discount != 1 && item.discount != null">{{item.originalPrice}}</span>
        </span>
        <span class="selectedIcon"></span>
      </span>
    `;

    //获取选择版本下的商品信息
    // var url = window.location.toString();
    // var productId = '';
    // var arr = url.split('?');
    // if(arr.length==2){
    //    var arr1 = arr[1].split('&');
    //    for (var i = 0; i<arr1.length; i++){
    //        if(arr1[i].indexOf('productId')>=0){
    //            var arr2 = arr1[i].split('=');
    //            productId = arr2[1];
    //            break;
    //        }
    //    }
    // }
    var productId = $('#productId').val();
    var showType = $('#ShowType').val(); // 0-續費 1-升級
    if(showType==0){
        $('#version-show').show();
        $('.dropdown').hide()
    }else {
        $('#version-show').hide();
        $('.dropdown').show()
    }
    fetchs.post('/commodity/selectProductIdInfoAndList',{'productId':productId},function (res) {
        if(res.ifSuc==1){
            if(res.data){
                roomSku = res.data.meetingId;
                memonrySku = res.data.spaceId;
                lastPrice = parseFloat(res.data.lastPrice);
                //當前日期
                // var date = new Date();
                // var years = date.getFullYear() + '-';
                // var month = date.getMonth()+1 + '-';
                // var day = date.getDate();
                 //到期時間
                if(res.data.lastDay!=null && res.data.lastDay.length!=0){
                    var oldTime=res.data.lastDay;
                    var now=new Date().getTime();
                    var time=parseInt((oldTime-now)/(1000 * 60 * 60 * 24))+1;
                    lastDay = time;
                    // var end = moment(res.data.lastDay).format('YYYY-MM-DD');
                    // lastDay = DateDiff(years+month+day,end);
                }else {
                    lastDay = 0;
                }

                for(var i = 0; i<res.data.list.length; i++){
                    var id = res.data.list[i].id;
                    if(id == productId){
                        room = parseInt(res.data.list[i].room);
                        minRoom = room;
                        memonry = parseInt(res.data.list[i].space);
                        minMemonry = memonry;
                        if(showType==0){
                            $('#version-show').text(res.data.list[i].version);
                        }else {
                            $('#version-type').text(res.data.list[i].version);
                        }
                        $('#room-free-msg').text("前"+room+"间免费，每增加1间，150元/年。")
                        $('#space-free-msg').text("前"+memonry+"G免费，每增加5G，50元/年。")
                        //設置會議室輸入框默認值和最小值
                        $('#room-count').val(room);
                        $('#room-count').attr('min',room);
                        $('#memonry-count').val(memonry);
                        $('#memonry-count').attr('min',memonry);
                        break;
                    }
                }
                for(var i =0; i<res.data.info.length; i++){
                    //設置原價
                    res.data.info[i].originalPrice = parseFloat(res.data.info[i].price) / parseFloat(res.data.info[i].discount);
                    if(i==0){
                        if(res.data.info[i].discount){
                            discount = parseFloat(res.data.info[i].discount);
                        }
                    }
                }
                $('.dropdown-menu').html(soda(menu,{data:res.data.list}));
                $('#selectService').html(soda(sevice,{data:res.data.info}));

                $('.buy-year').eq(0).addClass('select-version');
                price = parseFloat($('.buy-year').eq(0).data('price'));
                year = parseInt($('.buy-year').eq(0).data('title').split('年/')[0]);

                selectPack.productPackSku = ($('.buy-year').eq(0).data('id')).toString();
                selectPack.qty = 1;
                calculatePrice();
            }
        }

    })
    function selectProductInfo (id) {
      var data = {productId:id};

      fetchs.post('/commodity/selectProductPriceInfo',data,function (res) {
          if(res.ifSuc==1){
              if(res.data.length>0){
                  for(var i =0; i<res.data.length; i++){
                      //設置原價
                      res.data[i].originalPrice = parseFloat(res.data[i].price) / parseFloat(res.data[i].discount);
                      if(i==0 && res.data[i].discount){
                          discount = parseFloat(res.data[i].discount);
                      }
                  }
                  $('#selectService').html(soda(sevice,{data:res.data}));
                  $('.buy-year').eq(0).addClass('select-version');
                  price = parseFloat($('.buy-year').eq(0).data('price'));
                  year = parseInt($('.buy-year').eq(0).data('title').split('年/')[0]);
                  selectPack.productPackSku = ($('.buy-year').eq(0).data('id')).toString();
                  selectPack.qty = 1;
                  calculatePrice();
              }
          }else {

          }

      });
    }

    //點擊版本介紹
    $('#ver-info').on('click',function () {
        window.location.href="/commodity/orderExhibition?token="+  userInfo.token;
    })

    function calculatePrice(){
        if(showType==0){
            //續費不需要減去上一個版本的余額
            total = price + ((room-minRoom)*150 * year) + ((memonry-minMemonry)/5 *50 * year);
            var unitPrice = keepTwoDecimalFull(price) / keepTwoDecimalFull(year) / discount;
            var text = unitPrice + '元/年'  + ' x ' + year + '年' + ' x ' + discount + ' + ';
            if((room-minRoom)>0){
                 text +=  (room-minRoom) + '间 x ' + 150 + '元/年 x ' + year + '年 + ';
            }else {
                text += '0元 + ';
            }
            if((memonry-minMemonry)>0){
                text +=  (memonry-minMemonry)/5 + ' x ' + (50) + '元/5G/年 x ' + year + '年 = ';
            }else {
                text += '0元 = ';
            }
            text += keepTwoDecimalFull(total) + '元';
            $('#calculate-pirce').text(text);
            $('#total').text(keepTwoDecimalFull(total) + '元');
        }else {
            if(lastPrice==0||lastDay<=0){
                //上一版本剩餘金額為0不需要減去上一個版本的余額
                total = price + ((room-minRoom)*150 * year) + ((memonry-minMemonry)/5 *50 * year);
                var unitPrice = keepTwoDecimalFull(price) / keepTwoDecimalFull(year) / discount;
                var text = unitPrice + '元/年'  + ' x ' + year + '年' + ' x ' + discount + ' + ';
                if((room-minRoom)>0){
                    text +=  (room-minRoom) + '间 x ' + 150 + '元/年 x ' + year + '年 + ';
                }else {
                    text += '0元 + ';
                }
                if((memonry-minMemonry)>0){
                    text +=  (memonry-minMemonry)/5 + ' x ' + (50) + '元/5G/年 x ' + year + '年 = ';
                }else {
                    text += '0元 = ';
                }
                text += keepTwoDecimalFull(total) + '元';
                $('#calculate-pirce').text(text);
                $('#total').text(keepTwoDecimalFull(total) + '元');
            }else {
                //升级需要減去上一個版本的余額
                if(lastDay==0){
                    lastDay = 1;
                }
                var unitPrice = keepTwoDecimalFull(price) / keepTwoDecimalFull(year) / discount;  //一年的價格
                total = keepTwoDecimalFull(unitPrice)/365.0 * lastDay + ((room-minRoom)*150/365.0 * keepTwoDecimalFull(lastDay)) + ((memonry-minMemonry)/5 *50 /365.0 * keepTwoDecimalFull(lastDay))  - keepTwoDecimalFull(lastPrice)/365.00 * lastDay;
                var text = unitPrice+ '元/365天 x ';
                // if(lastDay/365<=1){
                    text +=  lastDay + '天 + '
                // }else {
                //     text += parseInt(lastDay/365) + '年 +'
                // }
                if((room-minRoom)>0){
                    text += (room-minRoom) + '间 x ' +  (150) + '元/年 x '  + lastDay + '天 +';
                }else {
                    text += '0元 + ';
                }
                if((memonry-minMemonry)>0){
                    text += (memonry-minMemonry)/5 + ' x ' + (50) + '元/5G/年 x ';
                    // if(lastDay/365<=1){
                        text += lastDay + '天 - '
                    // }else {
                    //     text += parseInt(lastDay/365) + '年 - '
                    // }
                }else {
                    text += '0元 - ';
                }
                text += lastPrice + '元/365天 x ';
                // if(lastDay/365<=1){
                    text += lastDay + '天 = ';
                // }else {
                //     text += parseInt(lastDay/365) + '年 = '
                // }
                text += keepTwoDecimalFull(total) + '元';
                $('#calculate-pirce').text(text);
                $('#total').text(keepTwoDecimalFull(total) + '元');
            }
        }

    }
    //四舍五入保留2位小数（不够位数，则用0替补）
    function keepTwoDecimalFull(num) {
        var result = parseFloat(num);
        if (isNaN(result)) {
            return false;
        }
        result = Math.round(num * 100) / 100;
        var s_x = result.toString();
        var pos_decimal = s_x.indexOf('.');
        if (pos_decimal < 0) {
            pos_decimal = s_x.length;
            s_x += '.';
        }
        while (s_x.length <= pos_decimal + 2) {
            s_x += '0';
        }
        return s_x;
    }
    function DateDiff(sDate1, sDate2) {  //sDate1和sDate2是yyyy-MM-dd格式

        var aDate, oDate1, oDate2, iDays;

        aDate = sDate1.split("-");

        oDate1 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0]);  //转换为yyyy-MM-dd格式

        aDate = sDate2.split("-");

        oDate2 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0]);

        iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60 / 24); //把相差的毫秒数转换为天数

        return iDays;  //返回相差天数

    }


    $('body').on('click','.buy-year',function(){
        var id = $(this).data('id');
        price = parseFloat($(this).data('price'));
        year = parseInt($(this).data('title').split('年/')[0]);
        discount = parseFloat($(this).data('discount'));
        $('.buy-year').removeClass('select-version');
        $(this).addClass('select-version')
        selectPack.productPackSku = ($(this).eq(0).data('id')).toString();
        selectPack.qty = 1;
        calculatePrice();
    });

  $('body').on('click','.dropdown-item' ,function(e){
      $('.dropdown-item').removeClass('select');
      $(this).addClass('select');
      var text = $(this).data('title');
      $('#version-type').text(text);
      selectProductInfo($(this).data('id'));
      $('#room-free-msg').text("前"+$(this).data('room')+"间免费，每增加1间，150元/年。")
      $('#space-free-msg').text("前"+$(this).data('space')+"G免费，每增加5G，50元/年。")
      room = parseInt($(this).data('room'));
      minRoom = room;
      memonry = parseInt($(this).data('space'));
      minMemonry = memonry;
      //設置會議室輸入框默認值和最小值
      $('#room-count').val(room);
      $('#room-count').attr('min',room);
      $('#memonry-count').val(memonry);
      $('memonry-count').attr('min',memonry);
      $('#subroom').attr('disabled',true);
      $('#subMemonry').attr('disabled', true);
  });

  //减少会议室数量
  $('#subRoom').on('click',function(){
     var number = parseInt($('#room-count').val());
     if(isNaN(number)){

     }else {
        number -= 1;
        if(number<100){
            $('#addRoom').attr('disabled',false);
        }
          $('#room-count').val(number.toString());
        if(number<=minRoom){
          //数量<=最小值时候不可点击
          $(this).attr('disabled', true);
          $('#room-count').val(minRoom);
          $('#room-count').removeClass('input-color')
        }else {
          $('#room-count').addClass('input-color')
        }
     }
      room = parseInt($('#room-count').val());
      calculatePrice();
  });
  //增加会议室数量
  $('#addRoom').on('click',function(){
     var number = parseInt($('#room-count').val());
     if(isNaN(number)){

     }else {
        number += 1;
        if(number>minRoom){
          $('#subRoom').attr('disabled', false);
          $('#room-count').addClass('input-color')
        }else {
          $('#room-count').removeClass('input-color')
        }
        $('#room-count').val(number.toString());
        if(number>=100){
            $('#addRoom').attr('disabled',true); //超過最大值不可點擊
            $('#room-count').val('100');
        }

     }
      room = parseInt($('#room-count').val());
      calculatePrice();
  });
  //输入框值的变化
  $('#room-count').on('change',function(){
      var number = parseInt($('#room-count').val());
      if(isNaN(number)||number<minRoom){
        $('#room-count').val(minRoom)
        $('#subRoom').attr('disabled', true);
        $('#room-count').removeClass('input-color')
     }else if(number>minRoom){
        // var abc = Math.ceil(number/3.0)*3;
        // $('#room-count').val(abc)
        $('#subRoom').attr('disabled', false);
        $('#room-count').addClass('input-color')
     }
     if(number>=100){
         $('#addRoom').attr('disabled',true);
         $('#room-count').val(100)
     }else {
         $('#addRoom').attr('disabled',false);
     }
      room = parseInt($('#room-count').val());
      calculatePrice();
  })
  //减少存储空间数量
  $('#subMemonry').on('click',function(){
     var number = parseInt($('#memonry-count').val());
     if(isNaN(number)){
     }else {
        number -= 5;
        if(number<100){
            $('#addMemonry').attr('disabled',false);
        }
          $('#memonry-count').val(number.toString());
        if(number<=minMemonry){
          //数量<=最小值时候不可点击
          $(this).attr('disabled', true);
          $('#memonry-count').val(minMemonry);
          $('#memonry-count').removeClass('input-color')
        }else {
          $('#memonry-count').addClass('input-color')
        }
     }
      memonry = parseInt($('#memonry-count').val());
      calculatePrice();
  });
  //增加存储空间数量
  $('#addMemonry').on('click',function(){
     var number = parseInt($('#memonry-count').val());
     if(isNaN(number)){

     }else {
        number += 5;
        if(number>minMemonry){
          $('#subMemonry').attr('disabled', false);
            $('#memonry-count').addClass('input-color')
        }else {
            $('#memonry-count').removeClass('input-color')
        }
        $('#memonry-count').val(number.toString());
        if(number>=100){
            $('#addMemonry').attr('disabled',true); //超過最大值不可點擊
            $('#memonry-count').val('100');
        }
     }
      memonry = parseInt($('#memonry-count').val());
      calculatePrice();
  });
  //输入框值的变化
  $('#memonry-count').on('change',function(){

      var number = parseInt($('#memonry-count').val());
      if(isNaN(number)||number<minMemonry){
        $('#memonry-count').val(minMemonry);
        $('#subMemonry').attr('disabled', true);
        $('#memonry-count').removeClass('input-color')
     }else if(number>5){
        var abc = Math.ceil(number/5.0)*5;
        $('#memonry-count').val(abc)
        $('#subMemonry').attr('disabled', false);
        $('#memonry-count').addClass('input-color')
     }
     if(number>=100){
         $('#addmemonry').attr('disabled',true);
         $('#memonry-count').val(100)
     }else {
         $('#addMemonry').attr('disabled',false);
     }
      memonry = parseInt($('#memonry-count').val());
      calculatePrice();

  })

  //复选框值的变化
  $('.form-check-input').on('change',function(){
    //判断是否已经打勾
    if($(this).prop('checked')==true){
      //选择了
      $('#coupon').show();
    }else {
      //未选择
      $('#coupon').hide();
    }
    console.log();

  })

    $('#submitOrder').on('click',function () {
        submitOrder();
    })

    //提交订单
    function submitOrder() {
        var arr = []
        arr[0] = selectPack;
        if(room>minRoom){
            arr.push({productSku:roomSku?roomSku:'100',qty:(room-minRoom)})
        }
        if(memonry>minMemonry){
            arr.push({productSku:memonrySku?memonrySku:'101',qty:(memonry-minMemonry)/5})
        }
        var data = {payPrice:keepTwoDecimalFull(total).toString(),orderLines:arr,orderType:0};
        console.log(JSON.stringify(data))
        $.ajax({
            'type': 'POST',
            'url': '/order/new?token='+userInfo.token,
            'data': JSON.stringify(data),
            'contentType': 'application/json',
            success:function(res){
                if(res.ifSuc==1){
                    var orderId = res.data.orderId;
                    if(orderId==null && orderId == undefined){
                        notify('danger','订单创建失败')
                        return;
                    }
                    window.location.href='/order/prePay?token='+userInfo.token+'&orderId='+orderId;
                    // window.location.href = '/order/info?token='+userInfo.token+'&orderId='+orderId;
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
    }
})