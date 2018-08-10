'use strict';

(function () {

    //初始化订单参数对象
    var $product = {
        member: {
            num: 0, //数量
        },
        rooms: {
            num: 0,
        },
        spaces: {
            num: 0,
        },
        basePrice:0,
    };
    //购买时长
    var time={
        long:1,
        uom:0,  //0:年   1:月
    };

//初始化商品对象
    //1.会议室
    var roomsObj={
        free:0, //免费数量
        max:0,  //最大值
        min:0, //最小值
        step:10, //步长
        default:0, //默认值
        unitPrice:48,//基础价格
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
        max:0,  //最大值
        default:0,//默认值
        min:0, //最小值
        step:10, //步长
        unitPrice:48,//基础价格
        tiers:[
            {
                discount :95, //折扣值
                unit:20,  //购买的数量
            }
        ],
    }
    //3.内存空间
    var spaceObj={
        free:0, //免费数量
        max:0,  //最大值
        min:-1, //最小值
        default:0,//默认值
        step:10, //步长
        unitPrice:48,//基础价格
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

    //5.折扣类型
    var discountOption={
        peopleStyle:0,
        roomStyle:0, //0,跟随人员折扣 1.自身折扣 2.无折扣
        spaceStyle:0,
    }
    //6.第一次加载的标识
    var first={
        room:0,
        people:0,
    }
    //7.年限折扣模板
    var discountYears = '\n        <div  ng-repeat="item in data" class="rooms"  ng-class="$index==0 ? $index+\' select\': $index"><span ng-if="item.uom==0" class="dateUom" data-id="0">{{item.unit}}年</span><span ng-if="item.uom==1" class="dateUom" data-id="1">{{item.unit}}月</span>\n          <div class="selectedIcon"></div>\n          <div class="discountYears"  ng-if="item.discount<100"  data-discount="item.discount">{{item.discount}}折</div>\n        </div>';

    //8.订单总价格
    var totalPrice=0

    //是否使用0折优惠券
    var has0Discount = false;

    //9.标识当前页面 0: 升级， 1: 增容 2: 续费
    var index = $("#orderType").val();
    //10. 记录优惠券码
    var code="";
    if (index == 0) {
        //展示升级页面
        $('.content-serve').hide();
        $('.remaining').hide();
        $('.content-time .end').hide();
    } else if (index == 1) {
        //展示增容页面
        $('.title').html("购买服务增容");
        $(".content-time").hide();
        $(".content-showDiscount").hide();
        $(".tab-content .free .content-produce").css("borderBottom","0px");
    } else if (index == 2) {
        //展示续费页面
        $('.title').html("购买服务续费");
        $(".content-produce").hide();
    }
    //11.记录查询商品时返回的configId,在提交订单时传递
    var  configId = "";
    // -----------------查询所有商品及折扣信息-----------------
    getProductInfo();
    function getProductInfo() {
        fetchs.post('/commodity/getAll',{},function (result) {
            if(result.ifSuc==1){
                //定价配置
                configId = result.data.configId;
                //产品折扣信息
                var product = result.data.product;
                for(var i=0,len=product.length;i<len;i++){
                    if(product[i].sku=="100"){      //会议室数
                        roomsObj = product[i].config;
                        discountOption.roomStyle=product[i].discountOption;
                        if(!roomsObj.tiers || product[i].discountOption!=1){ //非空判断
                           roomsObj.tiers = [];
                        }
                        roomsObj.tiers.push({unit:roomsObj.free,discount:roomsObj.free+"间免费"});
                    }else if(product[i].sku=="101"){ //存储空间
                        spaceObj = product[i].config;
                        discountOption.spaceStyle=product[i].discountOption;
                        if(!spaceObj.tiers || product[i].discountOption!=1 ){
                            spaceObj.tiers = [];
                        }
                        spaceObj.tiers.push({unit:spaceObj.free,discount:spaceObj.free+"GB免费"});
                    }else if(product[i].sku=="102"){ //员工数量
                        peopleObj = product[i].config;
                        discountOption.peopleStyle=product[i].discountOption;
                        if(!peopleObj.tiers || product[i].discountOption!=1){
                            peopleObj.tiers = [];
                        }
                        peopleObj.tiers.push({unit:peopleObj.min,discount:peopleObj.min+"人起"});
                    }
                }
                //年限折扣信息
                if(index==0||index==2){
                    serviceTermDiscount = result.data.serviceTermDiscount;
                    $(".content-time .first").append(soda(discountYears, {data : serviceTermDiscount}));                 time.uom= serviceTermDiscount[0].uom; //初始化时间单位
                    time.long=serviceTermDiscount[0].unit; //初始化时长大小
                    //初始化购买清单里的时长
                    $(".value.duration").html(time.long+ (time.uom==0?"年":"月"));
                    $('.rooms').on("click",function(){
                        if (index != 1){
                            $(".rooms").removeClass("select");
                            $(this).addClass("select");
                            $(".value.duration").html($(".select .dateUom").html());
                            time.uom = $(".select .dateUom").data("id");
                            time.long=parseInt($(".select .dateUom").html());
                            calcPrice();
                        }
                    });
                    if(roomsObj.free>0){
                       $(".value.dfRoom").html("(含免费"+roomsObj.free+"间)");
                    }
                    if(spaceObj.free>0){
                        $(".value.dfSpace").html("(含免费"+spaceObj.free+"GB)");
                    }
                }else {
                    $(".value.dfRoom").hide();
                    $(".value.dfSpace").hide()
                }
                if(index==1||index==2){
                    //租户信息(现有的人数与会议室数量)
                    var info = result.data.tenantInfo;
                    $(".value.indate").html("至"+formatDate(Number(info.expireTime)));
                    $(".value.dfDate").html("(有效期至"+formatDate(Number(info.expireTime))+")");
                    if (index == 1){
                        $(".remaining>.time").html(info.surplusTime);
                        $(".value.duration").html(info.surplusTime);
                    }
                    var vos = info.vos;
                    // for(var i=0; i<vos.length; i++){
                    //     if(vos[i].displayName.indexOf(':')==vos[i].displayName.length-1||vos[i].displayName.indexOf('：')==vos[i].displayName.length-1){
                    //         vos[i].displayName = vos[i].displayName.substring(0,vos[i].displayName.length-1) + ' :';
                    //     }
                    // }
                    // $(".content-serve .key").eq(1).text(vos[0].displayName);
                    // $(".content-serve .key").eq(2).text(vos[1].displayName);
                    // $(".content-serve .key").eq(3).text(vos[2].displayName);

                    $(".content-serve .members").html(vos[0].qty+vos[0].uom);
                    $(".content-serve .rooms").html(vos[1].qty+vos[1].uom);
                    $(".content-serve .spaces").html(vos[2].qty+vos[2].uom);
                    if(index==1){
                       //只有人员的免费值与默认值相同
                       peopleObj.free = peopleObj.default=parseInt(vos[0].qty);
                       roomsObj.default=parseInt(vos[1].qty);
                       roomsObj.free = Math.max(roomsObj.default,roomsObj.free);
                       spaceObj.default=parseInt(vos[2].qty);
                       spaceObj.free = Math.max(spaceObj.default,spaceObj.free);
                    }else if(index==2){
                        $(".value.employNum").html(vos[0].qty + vos[0].uom);
                        $(".value.roomNum").html(vos[1].qty + vos[1].uom);
                        $(".value.spaceNum").html(vos[2].qty + vos[2].uom);
                        $product.member.num = parseInt(vos[0].qty);
                        $product.rooms.num = parseInt(vos[1].qty);
                        $product.spaces.num = parseInt(vos[2].qty);
                    }
                }
                //初始化商品初始化信息
                initSlider();
            }else {
                notify('danger','获取数据失败');
            }
        });
    }
    $(".discountIput").focus(function(){
        $(".error").hide();
    });
    $('.discountBtn').on('click', function () {

        if(!$(".discountIput").val()){ //为空时不校验
            $(".errorMsg").html("请输入优惠劵码");
            $(".error").show();
            return;
        }
        //点击了校验按钮
        if ($(".discountBtn").html() == "校验") {
            calcPrice(1);
        } else if ($(".discountBtn").html() == "取消") {
            $(".discountBtn").html("校验");
            $(".discountIput").attr("disabled", false);
            $(".discountIput").removeClass("disabled");
            $(".discountIput").val("");
            $(".correct").hide();
            $(".error").hide();
            code="";
            calcPrice(2);
        }
    });
    //提交订单
    $('.submit').on('click', function () {
        submitOrder();
    });
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
        var person_tips = '扩容员工人数费用为'+peopleObj.unitPrice+'元/年/人，'+peopleObj.min+'人起购，每次扩容'+peopleObj.step+'人起';
        $('.employee-range').sliderange({
            class: 'bwer',
            width: 820,
            default: peopleObj.default,//只有在增容的时候用到
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
                if (index == 0) {
                    //展示升级页面对应的操作
                    $('.peopleNum .actual').hide();
                    $product.member.num = value;
                    $(".value.employNum").html(value + "人");
                } else if (index == 1) {
                    //展示增容页面对应的操作
                    $product.member.num = value - peopleObj.free;
                    $(".value.employNum").html($product.member.num + "人");
                    console.log("展示增容页面" + value);
                }

                if(discountOption.roomStyle==0){
                    //如果会议室跟随人员折扣,则展示对应的折扣
                    if (!!point) {
                        $(".relevanceDiscount.room").css("color","#fa553c");
                        $(".relevanceDiscount.room").html("享"+point.discount+"折优惠");
                    } else {
                        $(".relevanceDiscount.room").css("color","#333333");
                        $(".relevanceDiscount.room").html("无折扣");
                    }
                }

                if(discountOption.spaceStyle==0){
                    //如果存储空间跟随人员折扣,则展示对应的折扣
                    if (!!point) {
                        $(".relevanceDiscount.space").css("color","#fa553c");
                        $(".relevanceDiscount.space").html("享"+point.discount+"折优惠");
                    } else {
                        $(".relevanceDiscount.space").css("color","#333333");
                        $(".relevanceDiscount.space").html("无折扣");
                    }
                }
                if(first.people==0){
                    first.people=1;
                }else {
                    calcPrice();
                }

            }
        });

        //间数控制
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
        var room_tip = '';
        if(discountOption.roomStyle==0){
            room_tip = '扩容会议室费用为'+roomsObj.unitPrice+'元/年/间，每次扩容最少'+roomsObj.step+'间，<b>折扣跟随所购员工人数的折扣执行</b>。'
        }else {
            room_tip = '扩容会议室费用为'+roomsObj.unitPrice+'元/年/间，每次扩容最少'+roomsObj.step+'间。'
        }

        $('.boardroom-range').sliderange({
            unit: '间',
            min: roomsObj.min!=-1?roomsObj.min:roomsObj.free,
            max: roomsObj.max,
            step: roomsObj.step,
            free:roomsObj.free!=-1?roomsObj.free:roomsObj.min,
            default: roomsObj.default,//只有在增容的时候用到
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
            points:roomsObj.tiers,
            change: function change(value,point) {
                if (index == 0) {
                    //展示升级页面
                    $product.rooms.num = value;
                    $(".value.roomNum").html(value + "间");
                } else if (index == 1) {
                    //展示增容页面
                    $product.rooms.num = value - roomsObj.free;
                    $(".value.roomNum").html($product.rooms.num + "间");
                }

                if(discountOption.roomStyle==1){//自身折扣时,进度条后边不展示折扣信息
                    $(".relevanceDiscount.room").hide();
                }else if(discountOption.roomStyle==2){  //无折扣
                    $(".relevanceDiscount.room").css("color","#333333");
                    $(".relevanceDiscount.room").html("无折扣");
                }
                if(first.room==0){
                    first.room=1;
                }else {
                    calcPrice();
                }
            }
        });

        //容量控制
        var space_tip = '';
        if(discountOption.spaceStyle==0){
            space_tip = '扩容存储空间的费用为'+spaceObj.unitPrice+'元/年/GB，每次扩容最少'+spaceObj.step+'GB，<b>折扣跟随所购员工人数的折扣执行</b>。'
        }else {
            space_tip = '扩容存储空间的费用为'+spaceObj.unitPrice+'元/年/GB，每次扩容最少'+spaceObj.step+'GB。'
        }
        $('.storage-range').sliderange({
            unit: 'GB',
            min: spaceObj.min!=-1?spaceObj.min:spaceObj.free,
            max: spaceObj.max,
            step: spaceObj.step,
            points: spaceObj.tiers,
            free:spaceObj.free!=-1?spaceObj.free:spaceObj.min,
            default: spaceObj.default,//只有在增容的时候用到
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
            tips: space_tip,
            change: function change(value,point) {
                console.log(value);
                if (index == 0) {
                    //展示升级页面
                    $product.spaces.num = value;
                    $(".value.spaceNum").html(value + "GB");
                } else if (index == 1) {
                    //展示增容页面
                    $product.spaces.num = value - spaceObj.free;
                    $(".value.spaceNum").html($product.spaces.num + "GB");
                }

                if(discountOption.spaceStyle==1){//自身折扣
                    $(".relevanceDiscount.space").hide();
                }else if(discountOption.spaceStyle==2){//无折扣
                    $(".relevanceDiscount.space").css("color","#333333");
                    $(".relevanceDiscount.space").html("无折扣");
                }
                calcPrice();
            }
        });
    }

    //计算价格接口
    function calcPrice(style) {
        //100会议室  101 存储空间 102员工数量
        var coupon="";
        if(style==1 || code.length){
           coupon = $(".discountIput").val();
        }
        var productParameter={
            "couponCode":coupon,
            "orderType":index,
            "uom":time.uom,//购买时长单位 0/1
            "serviceTerm": time.long,
            "orderLines":[
                {
                    "productSku":"102",
                    "qty":$product.member.num,
                },
                {
                    "productSku":"100",
                    "qty":$product.rooms.num,

                },
                {
                    "productSku":"101",
                    "qty":$product.spaces.num,
                },
            ]
        }
        loading(1);
        $.ajax({
            'type': 'POST',
            'url': '/order/calcPrice?token='+fetchs.token,
            'data': JSON.stringify(productParameter),
            'contentType': 'application/json',
            success:function(res){
                loading(0);
                if(res.ifSuc==1) {
                    $(".value.dfDate").html("(有效期至" + formatDate(Number(res.data.expireTime)) + ")");
                    totalPrice = res.data.payPrice;
                    $(".discountPrice").html(totalPrice + "元");
                    $(".price").html(res.data.listPrcie + "元");
                    $(".total").html(res.data.expression2);
                    if (index == 0) {
                        $(".unitPrice").html(res.data.expression1);
                    } else if (index == 1) {
                        if (parseFloat(totalPrice) > 0) {//处理增容页面0元无法提交订单
                            $('.submit').attr("disabled", false);
                            $(".submit").removeClass("disabled");
                        } else {
                            $('.submit').attr("disabled", "disabled");
                            $(".submit").addClass("disabled");
                        }
                        $(".unitPrice").html(res.data.expression1);
                    } else if (index == 2) {
                        $(".content-time .renew").html(res.data.expression1);
                    }
                    if (style == 2) { //点击取消优惠券的时候还原赠送的值
                        $(".value.dfMember").html(" ");
                        $(".value.dfMember").css("color", "#666666");
                        $(".value.dfSpace").css("color", "#666666");
                        $(".value.dfRoom").css("color", "#666666");
                        if (roomsObj.free > 0) {
                            $(".value.dfRoom").html("(含免费" + roomsObj.free + "间)");
                        } else {
                            $(".value.dfRoom").html(" ");
                        }
                        if (spaceObj.free > 0) {
                            $(".value.dfSpace").html("(含免费" + spaceObj.free + "GB)");
                        } else {
                            $(".value.dfSpace").html(" ");
                        }
                        $(".value.roomNum").html( $product.rooms.num + "间");
                        $(".value.spaceNum").html( $product.spaces.num + "GB");
                        $(".value.employNum").html( $product.member.num + "人");
                        var unit1 = time.uom == 0 ? "年" : "月";
                        $(".value.duration").html(time.long + unit1);
                    }
                    if ((index == 0 || index == 2)) {
                        if (res.data.couponValid) {//优惠券
                            code = $(".discountIput").val();
                            $(".discountBtn").html("取消");
                            $(".discountIput").attr("disabled", "disabled");
                            $(".discountIput").addClass("disabled");
                            //产品折扣信息
                            var info = res.data.couponInfo.products;
                            var infoStr = "";
                            if (res.data.couponInfo.couponType == "T") {
                                //特权优惠券
                                infoStr = "特权优惠券:";
                            } else if (res.data.couponInfo.couponType == "G") {
                                //赠送券
                                infoStr = "赠送券:";
                            } else if (res.data.couponInfo.couponType == "S") {
                                //时长优惠券
                                infoStr = "时长优惠券:";
                            }
                            if (res.data.couponInfo.couponType == "Z") {
                                //折扣优惠券
                                infoStr = "折扣优惠券:";
                            }
                            infoStr += "赠送";
                            if (info) {
                                for (var i = 0, len = info.length; i < len; i++) {
                                    if (info[i].qty > 0) {
                                        if (info[i].sku == "100") {//会议室数
                                            infoStr = "" + infoStr + info[i].qty + "间会议室,";
                                            var num = $product.rooms.num + info[i].qty;
                                            if (num > roomsObj.max) {
                                                num = roomsObj.max;
                                                $(".value.dfRoom").html("(超出上限)");
                                                $(".value.dfRoom").css("color", "#fa553c");
                                            } else {
                                                $(".value.dfRoom").css("color", "#666666");
                                                if (roomsObj.free > 0) {
                                                    $(".value.dfRoom").html("(含免费" + roomsObj.free + "间,赠送" + info[i].qty + "间)");
                                                } else {
                                                    $(".value.dfRoom").html("(赠送" + info[i].qty + "间)");
                                                }
                                            }
                                            $(".value.roomNum").html(num + "间");
                                        } else if (info[i].sku == "101") { //存储空间
                                            infoStr += info[i].qty + "GB存储空间,";
                                            var num = $product.spaces.num + info[i].qty;
                                            if (num > spaceObj.max) {
                                                num = spaceObj.max;
                                                $(".value.dfSpace").html("(超出上限)");
                                                $(".value.dfSpace").css("color", "#fa553c");
                                            } else {
                                                $(".value.dfSpace").css("color", "#666666");
                                                if (spaceObj.free > 0) {
                                                    $(".value.dfSpace").html("(含免费" + spaceObj.free + "GB,赠送" + info[i].qty + "GB)");
                                                } else {
                                                    $(".value.dfSpace").html("(赠送" + info[i].qty + "GB)");
                                                }

                                            }
                                            $(".value.spaceNum").html(num + "GB");
                                        } else if (info[i].sku == "102") { //员工数量
                                            infoStr += info[i].qty + "人员工人数,";
                                            var num = $product.member.num + info[i].qty;
                                            if (num > peopleObj.max) {
                                                num = peopleObj.max;
                                                $(".value.dfMember").html("(超出上限)");
                                                $(".value.dfMember").css("color", "#fa553c");
                                            } else {
                                                $(".value.dfMember").css("color", "#666666");
                                                if (peopleObj.free > 0) {
                                                    $(".value.dfMember").html("(含免费" + peopleObj.free + "人,赠送" + info[i].qty + "人)");
                                                } else {
                                                    $(".value.dfMember").html("(赠送" + info[i].qty + "人)");
                                                }
                                            }
                                            $(".value.employNum").html(num + "人");
                                        }
                                    }
                                }
                            }
                            if (res.data.couponInfo.serviceTerm.qty > 0) {
                                var unit0 = res.data.couponInfo.serviceTerm.uom == 0 ? "年" : "月";
                                infoStr += res.data.couponInfo.serviceTerm.qty + unit0 + "时长,";
                                $(".value.dfDate").html("(含赠送" + res.data.couponInfo.serviceTerm.qty + unit0 + " 有效期至" +formatDate(Number(res.data.expireTime))+ ")");
                                var unit1 = time.uom == 0 ? "年" : "月";
                                if (unit1 == "年" && unit0 == "年") {
                                    $(".value.duration").html(time.long + res.data.couponInfo.serviceTerm.qty + "年");
                                } else if (unit1 == "年" && unit0 == "月") {
                                    $(".value.duration").html(time.long + "年零" + res.data.couponInfo.serviceTerm.qty + "月");
                                } else if (unit1 == "月" && unit0 == "年") {
                                    $(".value.duration").html(res.data.couponInfo.serviceTerm.qty + "年零" + time.long + "月");
                                } else if (unit1 == "月" && unit0 == "月") {
                                    $(".value.duration").html(time.long + res.data.couponInfo.serviceTerm.qty + "月");
                                }
                            }

                            if (res.data.couponInfo.discount >= 0 && res.data.couponInfo.discount!=null) {
                                if(res.data.couponInfo.discount==0){
                                    has0Discount = true;
                                }
                                infoStr += res.data.couponInfo.discount + "折折扣,";
                            }
                            $(".discountInfo").html(infoStr.substr(0, infoStr.length - 1));
                            $(".error").hide();
                            $(".correct").show();
                        }else if(res.data.msg){
                            $(".correct").hide();
                            $(".errorMsg").html(res.data.msg);
                            $(".error").show();
                        }
                    }
                }else {
                    notify('danger', res.msg);
                }
            },
            error:function () {
                notify('danger',"网络连接失败，请检查网络");
            }
        });
    }
    //计算中
    function loading(type) {
        if(type==0){
            $(".content-bottom .loading").hide();
            $(".content-bottom .loaded").show();
        }else {
            $(".content-bottom .loading").show();
            $(".content-bottom .loaded").hide();
            $(".unitPrice").html("费用计算中...");
        }
    }

    $('[data-toggle="popover"]').each(function () {
        var element = $(this);
        element.popover({
            trigger: 'manual',
            placement: 'top', //top, bottom, left or right
            html: true,
            content: "赠送的员工人数、会议室数、存储空间，下次续费时将会计费",
        }).on("mouseenter", function () {
            var _this = this;
            $(this).popover("show");
            $(this).siblings(".popover").on("mouseleave", function () {
                $(_this).popover('hide');
            });
        }).on("mouseleave", function () {
            var _this = this;
            setTimeout(function () {
                if (!$(".popover:hover").length) {
                    $(_this).popover("hide")
                }
            }, 200);
        });
    });

    function formatDate(date) {
        var now=new Date(date);
        var year=now.getFullYear();
        var month=now.getMonth()+1;
        var date=now.getDate();
        if(month<10){
            month = "0"+month;
        }
        var date=now.getDate();
        if(date<10){
            date = "0"+date;
        }
        return year+"-"+month+"-"+date;
    }
    //提交订单接口
    function  submitOrder() {
        //100会议室  101 存储空间 102员工数量
        var order={
            "configId":configId,//定价配置id
            "orderType":index,//订单类型 升级--增容--续费
            "couponCode":code, //优惠券编码
            "uom":time.uom,        //购买时长单位 0/1
            "serviceTerm": time.long, //购买时长
            "orderLines":[       //对应的商品数量
                {
                    "productSku":"102",
                    "qty":$product.member.num,
                },
                {
                    "productSku":"100",
                    "qty":$product.rooms.num,

                },
                {
                    "productSku":"101",
                    "qty":$product.spaces.num,
                }
            ]
        }

        $.ajax({
            'type': 'POST',
            'url': '/order/new?token='+fetchs.token,
            'data': JSON.stringify(order),
            'contentType': 'application/json',
            success:function(res){
                if(res.ifSuc==1){
                    //拿到订单号,进行跳转
                    if(res.data.orderId !=null){
                        var orderId = res.data.orderId;
                        if(has0Discount==true){
                            notify('success','恭喜您，授权成功！');
                            setTimeout(function(){
                                window.location.href= '/commodity/orderManger?token='+fetchs.token;
                            },3000)
                        }else {
                            window.location.href='/order/prePay?token='+fetchs.token+'&orderId='+orderId;
                        }

                    }else {
                        notify('danger',"服务器异常");
                    }
                }else {
                    notify('danger',res.msg);
                }
            },
            error:function () {
                notify('danger',"网络连接失败，请检查网络");
            }
        });
    }
})();
//# sourceMappingURL=orderGroup.js.map
