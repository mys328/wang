'use strict';

(function () {
  //商品总折扣单价
  var product_discountUnitPrice;
  //商品总原始单价
  var product_unitPrice;
  //商品总折扣价
  var product_discountTotalPrice;
  //商品总原始价格
  var product_totalPrice;
  var endTime = 0; //时长

  //初始化订单参数对象
  var product = {
    member: {
        discount: 100, //折扣
        basePrice:0,
        num: 50 //数量
    },
    rooms: {
      discount: 100, //折扣
      basePrice:0,
      num: 3
    },
    spaces: {
      discount: 100, //折扣
      basePrice:0,
      num: 5
    },
  };
  //购买时长
  var time=1;
  //购买时长单位
  var time_uom=0;
  //时长折扣
  var time_discount = 100;

  var person_first = true;
  var room_firt = true;
  var space_first = true;
  var time_first = true;

//初始化商品对象
  //1.会议室
  var roomsObj={
    free:3, //免费数量
    max:1000,  //最大值
    min:3, //最小值
    step:10, //步长
    default:0, //默认值
    unitPrice:48,//基础价格
    discountOption:0, //折扣类型 0-跟随员工折扣 1-自定义折扣 2-无折扣
    tiers:[
      {
        discount :95, //折扣值
         unit:20,  //购买的数量
      }
    ],
  }
  //2.人数
  var peopleObj={
      free:0, //免费数量
      max:2000,  //最大值
      default:0,//默认值
      min:50, //最小值
      step:10, //步长
      unitPrice:48,//基础价格
      tiers:[
          {
              discount :95, //折扣值
              unit:100,  //购买的数量
          },
          {
              discount :90, //折扣值
              unit:200,  //购买的数量
          },
          {
              discount :85, //折扣值
              unit:400,  //购买的数量
          },
          {
              discount :75, //折扣值
              unit:500,  //购买的数量
          }
      ],
  }
  //3.内存空间
  var spaceObj={
      free:5, //免费数量
      max:100,  //最大值
      min:5, //最小值
      default:0,//默认值
      step:10, //步长
      unitPrice:48,//基础价格
      discountOption:0, //折扣类型 0-跟随员工折扣 1-自定义折扣 2-无折扣
      tiers:[
          {
              discount :95, //折扣值
              unit:20,  //购买的数量
          }
      ],
  };

  //4.购买时长折扣对象
   var serviceTermDiscount=[{
       discount: 85, //折扣
       unit: 2,  //购买时长
   }];

    //输入框只能输入数字
    $("body").on('keydown',"input[type='int']",function(e){
        var k = (typeof e.which == "number") ? e.which : e.keyCode;
        console.log(k);
        if((k<48 || (k<96&&k>57) || k>105) && k!=8){
            return false;
        }
        if(e.shiftKey) {
            return false;
        }
        return true;
    });
    $("body").on('keyup',"input[type='int']",function(e){
        var str = $(this).val();
        $(this).val(str.replace(/[^\d]/g,''));
    });

    //排序
    function by(property){
        return function(a, b) {
            return a[property] - b[property] > 0;
        }
    }
  getProductInfo();   // 查询所有商品及折扣信息

  function getProductInfo() {
     fetchs.post('/promotion/getPricesettingProviewData',{},function (result) {
      if(result.ifSuc==1){
         console.log(result);
         //年限折扣信息
         if(index==0||index==2){
             serviceTermDiscount = result.data.serviceTermDiscount;
             var yearDiscount = [];
             var monthDiscount = [];
             for(var i = 0; i <serviceTermDiscount.length; i++){
                 if(serviceTermDiscount[i].uom==1){
                     monthDiscount.push(serviceTermDiscount[i]);
                 }else {
                     yearDiscount.push(serviceTermDiscount[i]);
                 }
             }
             //根据unit字段排序
             yearDiscount = yearDiscount.sort(by('unit'));
             monthDiscount = monthDiscount.sort(by('unit'))
             serviceTermDiscount = monthDiscount.concat(yearDiscount);

             for(var i=0;i<serviceTermDiscount.length;i++){
                 var uom = '';
                 if(serviceTermDiscount[i].uom==0){
                     uom = '年';
                 }else {
                     uom = '月';
                 }
                 if(serviceTermDiscount[i].discount==100){
                     var html = '<span class="rooms" data-index="'+i+'" data-discount="'+serviceTermDiscount[i].discount+'">'+serviceTermDiscount[i].unit+uom+'<div class="selectedIcon"></div></span>';
                     $('.content-time .first').append(html);
                 }else {
                     var html = '<span class="rooms" data-index="'+i+'" data-discount="'+serviceTermDiscount[i].discount+'">'+serviceTermDiscount[i].unit+uom+'<div class="selectedIcon"></div>'+
                         '<div class="discountYears">'+serviceTermDiscount[i].discount+'折</div></span>';
                     $('.content-time .first').append(html);
                 }

             }
             //第一个时长默认选中
             restoreStyle(serviceTermDiscount[0]);
             $('.rooms').removeClass('select');
             $('.content-time .first .rooms').eq(0).addClass('select');
         }
         if(index==0||index==1){
             //产品折扣信息
             var product = result.data.skuDiscount;
             for(var i=0,len=product.length;i<len;i++){
                 if(product[i].sku=="100"){      //会议室数
                     roomsObj = product[i].config;
                     roomsObj.discountOption = product[i].discountOption;
                     console.log("room");
                     console.log(roomsObj);
                 }else if(product[i].sku=="101"){ //存储空间

                     spaceObj = product[i].config;
                     spaceObj.discountOption = product[i].discountOption;
                     console.log("space");
                     console.log(spaceObj);
                 }else if(product[i].sku=="102"){ //员工数量
                     console.log("people");
                     peopleObj = product[i].config;
                     console.log(peopleObj);
                 }
             }
          }

          if(index==1||index==2){
            //租户信息(现有的人数与会议室数量)
            var info = result.data.tenantInfo;
            var dataFormat= moment(Number(info.expireTime)).format('YYYY-MM-DD');
            $(".value.indate").html(dataFormat);
            $(".value.dfDate").html("(有效期至"+dataFormat+")");

            var now = new Date();
            var now_new = Date.parse(now.toDateString());
            var remainingTime = Math.round((info.expireTime-now_new) / (1000 * 60 * 60 * 24));
            if(remainingTime>=365){
                remainingTime = parseInt(remainingTime/365)+"年零"+remainingTime%365+"天";
            }else {
                remainingTime = remainingTime+"天";
            }
            $(".remaining>.time").html(remainingTime);
            $(".value.duration").html(remainingTime);
            var vos = info.vos;
            $(".content-serve .members").html(vos[0].qty+vos[0].uom);
            $(".content-serve .rooms").html(vos[1].qty+vos[1].uom);
            $(".content-serve .spaces").html(vos[2].qty+vos[2].uom);
            if(index==1){
                peopleObj.free=peopleObj.default=vos[0].qty;
                roomsObj.free=roomsObj.default=vos[1].qty;
                spaceObj.free=spaceObj.default=vos[2].qty;
            }else if(index==2){
                $(".value.employNum").html(vos[0].qty + "人");
                $(".value.roomNum").html(vos[1].qty + "间");
                $(".value.spaceNum").html(vos[2].qty + "GB");
            }
         }
         //初始化商品初始化信息
         initSlider();
      }else {
          notify('danger','获取数据失败');
      }
     });
  }

  //标识当前页面 0: 升级， 1: 增容 2: 续费
  var index = $("#orderType").val();
  index=0;
  if (index == 0) {
    //展示升级页面
    $('.content-serve').hide();
    $('.remaining').hide();
    $('.content-time .end').hide();
  } else if (index == 1) {
    //展示增容页面
    $('.title').html("购买服务增容");
    $(".unitPrice").hide();
    $(".content-time").hide();
    $(".content-showDiscount").hide();
  } else if (index == 2) {
    //展示续费页面
    $('.title').html("购买服务续费");
    $(".content-produce").hide();
  }

  $('body').on('click', function (e) {
    if (index != 1){

      if (e.target.className == "rooms") {
        restoreStyle(serviceTermDiscount[$(e.target).data('index')]);
        $('.rooms').removeClass('select');
        $(e.target).addClass('select');

      }

    }

    // if (e.target.className == "discountBtn") {
    //   //点击了校验按钮
    //   if ($(".discountBtn").html() == "校验") {
    //
    //     console.log("优惠券");
    //     console.log( $(".discountIput").val());
    //     // calcPrice($(".discountIput").val());
    //     // $(".discountBtn").html("取消");
    //     // $(".discountIput").attr("disabled", "disabled");
    //     // $(".discountIput").addClass("disabled");
    //     // $(".correct").show();
    //     // $(".error").show();
    //   } else if ($(".discountBtn").html() == "取消") {
    //
    //     $(".discountBtn").html("校验");
    //     $(".discountIput").attr("disabled", false);
    //     $(".discountIput").removeClass("disabled");
    //     $(".discountIput").val("");
    //     $(".correct").hide();
    //     $(".error").hide();
    //   }
    // }
    //提交订单
    if (e.target.className == "submit") {
        submitOrder();
    }
  });

  //选中图标的展示效果
  function restoreStyle(tag1) {
      time_first = false;
      var uom = '';
      var year = new Date().getFullYear(); //年
      var month = new Date().getMonth()+1; //月
      var day = new Date().getDate();
      if(tag1.uom==0){
          uom = '年';
          year+=tag1.unit;
          // day -= 1; //有效期截止到时长后的上一天
      }else {
          uom = '月';
          month+=tag1.unit;
          // day -= 1;
          if(month-12>0){
              month -= 12; //如果月份超过12，年份加一
              year += parseInt((month-12)/12)+1;
          }
      }
      var stringTime = year+'/'+month+'/'+day;

      var date = Date.parse(stringTime);
      // date -= 24*60*60*1000;
      endTime = date - 24*60*60*1000;


      // if(day<=0){
      //     //如果天小于1，月份推算到上一个月最后一天
      //     // day = 1;
      //     month -= 1;
      //     if(month==2){
      //         //判断是否为闰年
      //         if((year%4==0 && year%100!=0)||(year%100==0 && year%400==0)){
      //             day = 29;
      //         }else {
      //             day = 28;
      //         }
      //     }else if([1,3,5,7,8,10,12].indexOf(month)>=0){
      //         //31天
      //         day = 31;
      //     }else {
      //         day = 30;
      //     }
      // }
      // if(month<=0){
      //     //如果月份小于1，年份推算到上一年最后一天
      //     year -= 1;
      //     month = 12;
      //     day = 31;
      // }

      // if(month<10){
      //     month = '0'+month;
      // }
      // if(day<10){
      //     day = '0'+day;
      // }

      var end_time = year+'-'+month+'-'+day;
      end_time = moment(endTime).format('YYYY-MM-DD');
      $(".value.duration").html(tag1.unit+uom+'<label class="dfDate">（有效期至'+end_time+'）</label>');
      time = tag1.unit;
      time_uom = tag1.uom;
      time_discount = tag1.discount;
      //计算价格
      calcPrice();
  }
  //本地计算价格公式, 暂时保留!
  // var unitPriceHtml = '\n  <div class="table">\n  ' +
  //     ' <span ng-if="member.num!=0">48元/年/人 x {{member.num}}人</span>\n' +
  //     ' <span ng-if="discount!=100">x {{discount/100}}</span>\n     ' +
  //     ' <span ng-if="rooms.num!=3">+150元/年/间 x {{rooms.num-3}}间</span>\n' +
  //     ' <span ng-if="rooms.num!=3 && discount!=100">x {{discount/100}}\n </span>\n ' +
  //     ' <span ng-if="spaces.num!=5">+10元/年/GB x {{spaces.num-5}}GB</span>  \n     ' +
  //     ' <span>= {{basePrice}}元/年</span>\n   ' +
  //     ' </table>';

  // function basePrice(argument) {

    // var count = (48 * product.member.num + (product.rooms.num - 3) * 150) * product.discount / 100 + (product.spaces.num - 5) * 10;
    // product.basePrice = count;
    // $('.unitPrice').html(soda(unitPriceHtml, product));
  // }

  //初始化拖拽控制器方法
  function initSlider(){
      if(roomsObj.discountOption==1){
          $(".relevanceDiscount.room").hide();
      }else {
          $(".relevanceDiscount.room").show();
          if(roomsObj.discountOption==2){
              $(".relevanceDiscount.room").css("color","#333333");
              $(".relevanceDiscount.room").html("无折扣");
          }
      }
      if(spaceObj.discountOption==1){
          $(".relevanceDiscount.space").hide();
      }else {
          $(".relevanceDiscount.space").show();
          if(spaceObj.discountOption==2){
              $(".relevanceDiscount.space").css("color","#333333");
              $(".relevanceDiscount.space").html("无折扣");
          }
      }
      //人员控制
      var person_min = 0;
      var person_middle = 0;
      if(peopleObj.max>=1500){
          person_min = 500;
          person_middle = 1000;
      }else if(peopleObj.max>=300&&peopleObj.max<1500){
          if((peopleObj.max*0.5)%100 >=50){
              person_middle = (parseInt((peopleObj.max*0.5)/100) + 1)*100;
          }else {
              person_middle = (parseInt((peopleObj.max*0.5)/100))*100;
          }
          if((peopleObj.max*0.25)%100 >=50){
              person_min = (parseInt((peopleObj.max*0.25)/100) + 1)*100;
          }else {
              person_min = (parseInt((peopleObj.max*0.25)/100))*100;
          }
      }
      peopleObj.tiers.push({'discount':peopleObj.min+'人起','unit':peopleObj.min});
      var person_tips = '扩容员工人数费用为'+peopleObj.unitPrice+'元/年/人，'+peopleObj.min+'人起购，每次扩容'+peopleObj.step+'人起';
      $('.employee-range').sliderange({
          class: 'bwer',
          width: 820,
          min: peopleObj.min!=-1?peopleObj.min:peopleObj.free,
          free:peopleObj.free!=-1?peopleObj.free:0,
          max: peopleObj.max,
          step: peopleObj.step,
          section: [{
              value: person_min,
              width: 500
          }, {
              value: person_middle,
              width: 660
          }, {
              value: peopleObj.max,
              width: 820
          }],
          points: peopleObj.tiers,
          tips: person_tips,
          change: function change(value, point) {
              person_first = false;
              if(point){
                  product.member.discount = point.discount;
              }
              if (index == 0) {
                  //展示升级页面对应的操作
                  product.member.num = value;
                  $(".value.employNum").html(value + "人");
              } else if (index == 1) {
                  //展示增容页面对应的操作
                  product.member.num = value - peopleObj.free;
                  $(".value.employNum").html(product.member.num + "人");
                  console.log("展示增容页面" + value);

              }

              if (index==0||index==1){  //展示对应的折扣
                  if(roomsObj.discountOption==0){
                      if (!!point) {
                          product.rooms.discount = point.discount;
                          $(".relevanceDiscount.room").css("color","#fa553c");
                          $(".relevanceDiscount.room").html("享"+product.rooms.discount+"折优惠");

                      } else {
                          product.rooms.discount = 100;
                          $(".relevanceDiscount.room").css("color","#333333");
                          $(".relevanceDiscount.room").html("无折扣");
                      }
                  }
                  if(spaceObj.discountOption==0){
                      if (!!point) {
                          product.spaces.discount = point.discount;
                          $(".relevanceDiscount.space").css("color","#fa553c");
                          $(".relevanceDiscount.space").html("享"+product.spaces.discount+"折优惠");
                      } else {
                          product.spaces.discount = 100;
                          $(".relevanceDiscount.space").css("color","#333333");
                          $(".relevanceDiscount.space").html("无折扣");
                      }
                  }
              }
              calcPrice(0,0);
          }
      });

      //间数控制
      var room_tip = '';
      var points = [];
      if(roomsObj.discountOption==1){
          points = roomsObj.tiers;
      }
      if(roomsObj.discountOption==0){
          room_tip = '扩容会议室费用为'+roomsObj.unitPrice+'元/年/间，每次扩容最少'+roomsObj.step+'间，<b>折扣跟随所购员工人数的折扣执行</b>。'
      }else {
          room_tip = '扩容会议室费用为'+roomsObj.unitPrice+'元/年/间，每次扩容最少'+roomsObj.step+'间。'
      }
      points.push({'discount':roomsObj.free+'间免费','unit':roomsObj.free});
      var room_min = 0;
      var room_middle = 0;
      if(roomsObj.max>=350){
          room_min = 50;
          room_middle = 200;
      }else if(roomsObj.max>=100&&roomsObj.max<350){
          if((roomsObj.max*0.2)%10 >=5){
              room_middle = (parseInt((roomsObj.max*0.2)/10) + 1)*10;
          }else {
              room_middle = (parseInt((roomsObj.max*0.2)/10))*10;
          }
          if((roomsObj.max*0.05)%10 >=5){
              room_min = (parseInt((roomsObj.max*0.05)/10) + 1)*10;
          }else {
              room_min = (parseInt((roomsObj.max*0.05)/10))*10;
          }
      }
      $('.boardroom-range').sliderange({
          unit: '间',
          min: roomsObj.min!=-1?roomsObj.min:roomsObj.free,
          max: roomsObj.max,
          free:roomsObj.free!=-1?roomsObj.free:roomsObj.min,
          step: roomsObj.step,
          section: [{
              value: room_min,
              width: 340
          }, {
              value: room_middle,
              width: 500
          }, {
              value: roomsObj.max,
              width: 660
          }],
          tips: room_tip,
          points: points,
          change: function change(value, point) {
              room_firt = false;
              console.log(value);
              if(roomsObj.discountOption==1&&point){
                  product.rooms.discount = point.discount;
              }
              if (index == 0) {
                  //展示升级页面
                  product.rooms.num = value;
                  $(".value.roomNum").html(value + '间<label class="dfRoom">（含免费'+roomsObj.free+'间）</label>');
                  calcPrice(0,0);
              } else if (index == 1) {
                  //展示增容页面
                  $(".content-list .dfRoom").hide();
                  product.rooms.num = value - roomsObj.free;
                  $(".value.roomNum").html(product.rooms.num + "间");

              }
          }
      });

      //容量控制
      var space_tip = '';
      var space_points = [];
      if(spaceObj.discountOption==1){
          space_points = spaceObj.tiers;
      }
      if(spaceObj.discountOption==0){
          space_tip = '扩容存储空间的费用为'+spaceObj.unitPrice+'元/年/GB，每次扩容最少'+spaceObj.step+'GB，<b>折扣跟随所购员工人数的折扣执行</b>。'
      }else {
          space_tip = '扩容存储空间的费用为'+spaceObj.unitPrice+'元/年/GB，每次扩容最少'+spaceObj.step+'GB。'
      }
      space_points.push({'discount':spaceObj.free+'GB免费','unit':spaceObj.free});
      $('.storage-range').sliderange({
          unit: 'GB',
          min: spaceObj.min!=-1?spaceObj.min:spaceObj.free,
          max: spaceObj.max,
          free:spaceObj.free!=-1?spaceObj.free:spaceObj.min,
          step: spaceObj.step,
          section: [{
              value: 20,
              width: 340
          }, {
              value: 50,
              width: 500
          }, {
              value: spaceObj.max,
              width: 660
          }],
          points: space_points,
          tips: space_tip,
          change: function change(value, point) {
              space_first = false;
              console.log(value);
              if(roomsObj.discountOption==1&&point){
                  product.spaces.discount = point.discount;
              }
              if (index == 0) {
                  //展示升级页面
                  product.spaces.num = value;
                  $(".value.spaceNum").html(value + 'GB<label class="dfRoom">（含免费'+spaceObj.free+'GB）</label>');
                  calcPrice(0,0);
              } else if (index == 1) {
                  //展示增容页面
                  $(".content-list .dfSpace").hide();
                  product.spaces.num = value - spaceObj.free;
                  $(".value.spaceNum").html(product.spaces.num + "GB");

              }
          }
      });
  }

  //计算价格接口
  function calcPrice(coupon,type) {
      if(person_first||room_firt||space_first||time_first){
          return;
      }
      console.log(product);

      //计算单价

      //员工价格
      var person_price = (product.member.num)*product.member.discount*peopleObj.unitPrice/100;
      //会议室价格
      var room_price = (product.rooms.num-roomsObj.free)*product.rooms.discount*roomsObj.unitPrice/100;
      //存储空间价格
      var speace_price = (product.spaces.num-spaceObj.free)*product.spaces.discount*spaceObj.unitPrice/100;
      //折扣单价
      product_discountUnitPrice = person_price+room_price+speace_price;

      //原始单价
      product_unitPrice = (product.member.num)*peopleObj.unitPrice+
          (product.rooms.num)*roomsObj.unitPrice+
          (product.spaces.num)*spaceObj.unitPrice;


      //显示公式
      var text = '';
      //员工人数
      text+= peopleObj.unitPrice+'元/年/人×'+product.member.num+'人';
      if(product.member.discount<100){
          //折扣
          text += '×'+Math.floor(product.member.discount)/100;
      }
      //会议室数
      if(product.rooms.num-roomsObj.free>0){
          text += ' + '+roomsObj.unitPrice+'元/年/间×'+(product.rooms.num-roomsObj.free)+'间';
          if(product.rooms.discount<100){
              //折扣
              text += '×'+Math.floor(product.rooms.discount)/100;
          }
      }

      //存储空间
      if(product.spaces.num-spaceObj.free>0){
          text += ' + '+spaceObj.unitPrice+'元/年/GB×'+(product.spaces.num-spaceObj.free)+'GB';
          if(product.spaces.discount<100){
              //折扣
              text += '×'+Math.floor(product.spaces.discount)/100;
          }
      }

      text += ' = '+keepTwoDecimalFull(product_discountUnitPrice)+'元/年'
      $('.content-produce .loaded label').text(text);


      //计算总价
      var timeLength = 0;
      if(time_uom==0){
          //年
          timeLength = time;
          product_discountTotalPrice = product_discountUnitPrice*time*time_discount/100;
          product_totalPrice = product_unitPrice*time;
      }else {
          //月
          timeLength = parseInt((endTime-Date.parse(new Date()))/(1000 * 60 * 60 * 24))+2;
          product_discountTotalPrice = product_discountUnitPrice*timeLength*time_discount/100/365;
          product_totalPrice = product_unitPrice*timeLength/365;
      }
      $('.content-bottom .discountPrice').text(keepTwoDecimalFull(product_discountTotalPrice)+'元');
      $('.content-bottom .price').text(keepTwoDecimalFull(product_totalPrice)+'元');
      var uom = '';
      var total_text = '';
      if(time_uom==0) {
          uom = '年';
          total_text = keepTwoDecimalFull(product_discountUnitPrice)+'元/年×'+time+uom;
      }else {
          uom = '月';
          total_text = keepTwoDecimalFull(product_discountUnitPrice)+'元/年×('+ timeLength+'/365)年';
      }

      if(time_discount<100){
          total_text += '×'+Math.floor(time_discount)/100;
      }
      total_text += ' = '+keepTwoDecimalFull(product_discountTotalPrice)+'元';
      $('.content-bottom .total').text(total_text);

      //计算折扣
      // submitOrder();
      $('#discountValue').text('');


      //100会议室  101 存储空间 102员工数量
    //   var productParameter={
    //      "coupon":coupon,
    //      "orderType":index,
    //      "serviceTerm": time,
    //      "orderLines":[
    //          {
    //            "productSku":"100",
    //            "qty":product.member.num,
    //          },
    //          {
    //            "productSku":"101",
    //            "qty":product.rooms.num,
    //
    //          },
    //          {
    //            "productSku":"102",
    //            "qty":product.spaces.num,
    //          }
    //      ]
    //   }
    // loading(1,type);
    //
    //  $.ajax({
    //       'type': 'POST',
    //       'url': '/order/calcPrice?token='+fetchs.token,
    //       'data': JSON.stringify(productParameter),
    //       'contentType': 'application/json',
    //       success:function(res){
    //           loading(0,type);
    //           if(res.ifSuc==1){
    //             console.log(res.data);
    //           }else {
    //               notify('danger',res.msg);
    //           }
    //       },
    //       error:function (XMLHttpRequest,textStatus,errorThrown) {
    //           loading(0,type);
    //           notify('danger',"网络连接失败，请检查网络");
    //       }
    //   });
  }
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
  //计算中
  function loading(type, type1) {
      if(type==0){
          if(type1==0){
              $(".content-produce .loading").hide();
              $(".content-produce .loaded").show();
          }
          $(".content-bottom .loading").hide();
          $(".content-bottom .loaded").show();
      }else {
          $(".content-bottom .loading").show();
          $(".content-bottom .loaded").hide();
          if(type1==0){
              $(".content-produce .loading").show();
              $(".content-produce .loaded").hide();
          }
      }
  }
  $('body').on('keyup','#previewPrice',function (e) {
      var k = (typeof e.which == "number") ? e.which : e.keyCode;
      if(k==13){
          submitOrder();
      }
  })

  //提交订单接口
  function  submitOrder() {
      //计算折扣
      var previewPrice = $('#previewPrice').val();
      if($('#previewPrice').val()==''){
          // $('.discount-title').hide();
          $('#discountValue').text('');
          return;
      }

      var price = 0;
      if(product_discountUnitPrice*time==0){
          price = 0;
      }else {
          if(time_uom==0){
              price = (previewPrice/(product_discountUnitPrice*time));
          }else {
              var timeLength = parseInt((endTime-Date.parse(new Date())) / (1000 * 60 * 60 * 24))+2;
              price = (previewPrice/(product_discountUnitPrice*timeLength/365));
          }
      }

      // $('.discount-title').show();
      if(price*100>=100||price==0){
          $('#discountValue').text(parseInt(price*100)+'%');
      }else {
          $('#discountValue').text(keepTwoDecimalFull(price*100)+'%');
      }



      //100会议室  101 存储空间 102员工数量
      // var coupon=$(".discountIput").val();
      // var order={
      //     "orderType":index,//订单类型 升级--增容--续费
      //     "couponCode":coupon, //优惠券编码
      //     "uom":"year",        //购买市场单位 year/month
      //     "serviceTerm": time, //购买时长
      //     "orderLines":[       //对应的商品数量
      //         {
      //             "productSku":"100",
      //             "qty":product.member.num,
      //         },
      //         {
      //             "productSku":"101",
      //             "qty":product.rooms.num,
      //
      //         },
      //         {
      //             "productSku":"102",
      //             "qty":product.spaces.num,
      //         }
      //     ]
      // }
      //
      // $.ajax({
      //     'type': 'POST',
      //     'url': '/order/new?token='+fetchs.token,
      //     'data': JSON.stringify(order),
      //     'contentType': 'application/json',
      //     success:function(res){
      //         if(res.ifSuc==1){
      //             //拿到订单号,进行跳转
      //             if(res.data.orderId !=null){
      //                 var orderId = res.data.orderId;
      //                 window.location.href='/order/prePay?token='+fetchs.token+'&orderId='+orderId;
      //             }else {
      //                 notify('danger',"服务器异常");
      //             }
      //         }else {
      //             notify('danger',res.msg);
      //         }
      //     },
      //     error:function (XMLHttpRequest,textStatus,errorThrown) {
      //         notify('danger',"网络连接失败，请检查网络");
      //     }
      // });
  }
})();
