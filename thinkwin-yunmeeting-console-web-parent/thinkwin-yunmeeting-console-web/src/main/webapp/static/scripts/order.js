"use strict";

$(function () {

  soda.prefix('ng-');
  var page = {
    total: 0,
    size: 15,
    page: 1
  };

  var currNum = '';
    var userInfo = JSON.parse(localStorage.getItem('console-userinfo'));
    var role = 0;
    var  currOrder = '';
    var currType = ''; //当前点击的数据支付方式
    var remark='';
    var p = Pages();
    p.callBack(callBack);

    /**
     0: 未支付
     1: 已付款
     2: 已退款
     3: 支付失败
     4: 开票中
     5: 已开票
     8: 待银行汇款
     99: 已取消
     */
    /**
     * 1 微信
     * 2 支付宝
     * 3 网银支付
     * 4 银行汇款
     */
    soda.filter('state',function(state){
        state = state.toString();
        switch(state){
            case '0':{
                return '未支付'
            }
            case '1':{
                return '已付款'
            }
            case '2':{
                return '已退款'
            }
            case '3':{
                return '支付失败'
            }
            case '4':{
                return '开票中'
            }
            case '5':{
                return '已开票'
            }
            case '8':{
                return '待银行汇款'
            }
            case '99':{
                return '已取消'
            }
            default:{
                return '';
            }
        }
    })
    soda.filter('discount',function (discount) {
        if(discount==null) return '';
        if(discount == '1') return '';
        return discount;
    })
    //付款方式是银行汇款，付款状态是已支付或待银行汇款时候显示操作按钮
    var roomTpl = ' <div class="listBox" ng-if="data.length>0"><div class="scroll-list"> <table class="table"> <tbody><tr> <th class="table-1"><span>订单号</span></th> <th class="table-2"><span>公司名称</span></th> <th class="table-3"><span>购买服务</span></th> <th class="table-4"><span>支付状态</span></th> <th class="table-5"><span>支付时间</span></th> <th class="table-6"><span>优惠折扣</span></th> <th class="table-7"><span>支付方式</span></th> <th class="table-8"><span>支付金额</span></th> <th class="table-9"><span>操作</span></th> </tr><tr ng-repeat="item in data" > <td class="table-1"><span  data-title="{{item.orderNum|text}}">{{item.orderNum|text}}</span></td> ' +
        '<td class="table-2"><span  data-title="{{item.tenantName|text}}">{{item.tenantName|text}}</span></td>' +
        ' <td class="table-3"><span  data-title="{{item.skuName|text}}">{{item.skuName|text}}</span></td>' +
        ' <td class="table-4"><span  data-title="{{item.payStatus|text}}">{{item.payStatus|text}}</span></td> ' +
        ' <td class="table-5"><span  data-title="{{item.payTime|date:\'YYYY-MM-DD HH:mm:ss\'}}">{{item.payTime|date:"YYYY-MM-DD HH:mm:ss"}}</span></td> ' +
        ' <td class="table-6 Discountcontent"></td> ' +
        ' <td class="table-7"><span  data-title="{{item.payType|text}}">{{item.payType|text}}</span></td>' +
        ' <td class="table-8"><span data-title="{{item.payPrice|text}}">{{item.payPrice|text}}</span></td>' +
        ' <td class="table-9"><span ng-if="item.channel == 4 && (item.status == 0 ||item.status == 1 || item.status == 8) && item.certificatePath" class' +
        '="btn-span" data-toggle="modal" data-target="#cerModal" data-id="{{item.orderNum}}" data-order="{{item.orderId}}" data-cer=' +
        '"{{item.certificatePath}}">查看凭证</span> <span ng-if="role == 1 && ((item.channel == 4 && (item.status == 0 || item.status ==' +
        ' 8)) || ((item.channel == 1 || item.channel == 2 || item.channel == 3) && (item.status == 0)) )" class="btn-span sure-btn" data-' +
        'toggle="modal" data-target="#sureModal" data-id="{{item.orderNum}}" data-order="{{item.orderId}}" data-paytype="{{item.channel}}"' +
        ' data-company="{{item.tenantName|text}}" data-price="{{item.payPrice|text}}">确认到账</span><span class="btn-span sure-btn"  data-tog' +
        'gle="modal" data-target="#remarkModal"  data-remark="{{item.remark}}" data-order="{{item.orderId}}" ng-if="role == 1 && (item.status ' +
        '==1||item.status ==2||item.status ==4||item.status ==5)">查看备注</span> </td> </tr> </tbody> </table> </div></div> <div ng-if="data.length<1" class="nothing">暂无订单</div>';
    //查询权限信息
    fetchs.post('/saasRolePermission/findSaasPermissionsByParentId',{parentId:1002},function (res) {
        console.log(res);
        for (var i = 0; i<res.data.length; i++){
            var orgCode = res.data[i].orgCode;
            if(orgCode==1002001){
                role = 1;
                break;
            }
        }
        //获取数据
        getOrderInfo(0)
    });

    function getOrderInfo(tag) {
      var data = {
          currentPage:page.page,
          pageSize:page.size,
          ran: Math.random()
      }
        fetchs.qyh_get('/getAllOrderListInfo',data,function(res){
            if(res.data){
                if(tag==0){
                    page.total = res.data.total;
                    p.setCount(res.data.total);
                }
                if(res.data.list){
                    $('.table-Box').html(soda(roomTpl, {data:res.data.list, role: role}));
                    for (var i = 0, len = res.data.list.length; i < len; i++) {
                        if(res.data.list[i].orderDiscount!=null){
                        if(res.data.list[i].orderDiscount.skuDiscount!=null||res.data.list[i].orderDiscount.discount!=null||res.data.list[i].orderDiscount.couponDiscount!=null){
                            var str='<span data-toggle="tooltip" class="examine"><span>查看</span><div class="examinediv">'
                            if( res.data.list[i].orderDiscount.skuDiscount!=null){
                                for (var a = 0, len1 = res.data.list[i].orderDiscount.skuDiscount.length; a < len1; a++) {
                                        if( res.data.list[i].orderDiscount.skuDiscount[a].sku==102) {
                                            str+='<div><span class="namediv">员工人数折扣：</span>满'+res.data.list[i].orderDiscount.skuDiscount[a].unit+'人打<span class="discount">'+res.data.list[i].orderDiscount.skuDiscount[a].discount+'折</span></div>' ;
                                        }
                                        if(res.data.list[i].orderDiscount.skuDiscount[a].sku==100){
                                            if(res.data.list[i].orderDiscount.skuDiscount[a].unit==-1){
                                                str+='<div><span class="namediv">会议室数折扣：</span><span class="discount">'+res.data.list[i].orderDiscount.skuDiscount[a].discount+'折</span></div>'
                                            }else{
                                                str+='<div><span class="namediv">会议室数折扣：</span>满'+res.data.list[i].orderDiscount.skuDiscount[a].unit+'间打<span class="discount">'+res.data.list[i].orderDiscount.skuDiscount[a].discount+'折</span></div>'
                                            }

                                        }
                                        if(res.data.list[i].orderDiscount.skuDiscount[a].sku==101){
                                            if(res.data.list[i].orderDiscount.skuDiscount[a].unit==-1){
                                                str+='<div><span class="namediv">存储空间折扣：</span><span class="discount">'+res.data.list[i].orderDiscount.skuDiscount[a].discount+'折</span></div>'
                                            }else{
                                                str+='<div><span class="namediv">存储空间折扣：</span>满'+res.data.list[i].orderDiscount.skuDiscount[a].unit+'GB打<span class="discount">'+res.data.list[i].orderDiscount.skuDiscount[a].discount+'折</span></div>'
                                            }
                                        }
                                    }
                                 }
                            if( res.data.list[i].orderDiscount.discount!=null){
                                    str+='<div><span class="namediv">时长折扣：</span><span class="discount">'+res.data.list[i].orderDiscount.discount+'折</span></div>';
                                }
                           if(res.data.list[i].orderDiscount.couponDiscount!=null){
                                if (null != res.data.list[i].orderDiscount.couponDiscount.discount) {
                                     str += '<div><span class="namediv">优惠券折扣：</span><span class="discount">' + res.data.list[i].orderDiscount.couponDiscount.discount + '折</span></div>';
                                        }
                                if (res.data.list[i].orderDiscount.couponDiscount.products!=null||res.data.list[i].orderDiscount.couponDiscount.serviceTermInfo!=null) {
                                    str += '<div style="line-height: 23px;margin-bottom: 12px;"><span class="namediv" style="float: left">优惠券赠送：</span><span  class="giveas" >';
                                    if (res.data.list[i].orderDiscount.couponDiscount.products!=null) {
                                        for (var a = 0, len1 = res.data.list[i].orderDiscount.couponDiscount.products.length; a < len1; a++) {
                                            if (res.data.list[i].orderDiscount.couponDiscount.products[a].sku == 102 && res.data.list[i].orderDiscount.couponDiscount.products[a].qty != 0) {
                                                str += '<span>' + res.data.list[i].orderDiscount.couponDiscount.products[a].qty + '人员工人数、</span>';
                                            }
                                            if (res.data.list[i].orderDiscount.couponDiscount.products[a].sku == 100 && res.data.list[i].orderDiscount.couponDiscount.products[a].qty != 0) {
                                                str += '<span>' + res.data.list[i].orderDiscount.couponDiscount.products[a].qty + '间会议室、</span>';
                                            }
                                            if (res.data.list[i].orderDiscount.couponDiscount.products[a].sku == 101 && res.data.list[i].orderDiscount.couponDiscount.products[a].qty != 0) {
                                                str += '<span>' + res.data.list[i].orderDiscount.couponDiscount.products[a].qty + 'GB存储空间、</span>';
                                            }
                                        }
                                    }
                                    if (res.data.list[i].orderDiscount.couponDiscount.serviceTermInfo != null && res.data.list[i].orderDiscount.couponDiscount.serviceTermInfo.qty != 0) {
                                        str += '<span>' + res.data.list[i].orderDiscount.couponDiscount.serviceTermInfo.qty;
                                        if (res.data.list[i].orderDiscount.couponDiscount.serviceTermInfo.uom == 0) {
                                            str += '年'
                                        } else {
                                            str += '月'
                                        }
                                        str += '时长、</span>';
                                    }
                                    str += '</span></div>';
                                }
                            }
                            str+='</div>';
                            $(".Discountcontent").eq(i).html(str)
                            str='';
                        }
                    }
                    }

                    $('body').on('mouseenter', '[data-toggle="tooltip"]', function () {
                        var basic=  $(this).find(".giveas").text();
                        var reg=/、$/gi;
                        basic=basic.replace(reg,"");
                        $(this).find(".giveas").text(basic);

                        var valBoxHeight =$(this).find('.examinediv').outerHeight();
                        var windowHeight =$(document.body).height() - $(this).offset().top-56;
                        if(valBoxHeight>windowHeight){
                            $(this).find('.examinediv').css('top',-valBoxHeight+15+'px');
                        }
                        $(this).find(".examinediv").show();
                    });
                    $('body').on('mouseleave', '[data-toggle="tooltip"]',function () {
                        $(this).find(".examinediv").hide()
                    });
                }else {
                    $('.table-Box').html(soda(roomTpl, {data:[], role: role}));
                }

            }else {
                $('.table-Box').html(soda(roomTpl, {data:[], role: 0}));
                if(tag==0){
                    p.setCount(0);
                }
            }

        })

    }

  function callBack(pageCount, myPage) {
    //返回当前页和每页多少条数据
    page.page = pageCount;
    page.size = myPage;
    getOrderInfo(1);
  }
    $('#cerModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var src = button.data('cer');
        $('#cer').attr('src',src);
    });
    $('#sureModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var id = button.data('id');
        currNum = id;
        currOrder = button.data('order');
        var company = button.data('company');
        var price = button.data('price');
        var payType = button.data('paytype');
        currType = payType;
        var modal = $(this);
        if(payType!=4){
            modal.find('#inputOderNum').show();
            if(payType==2){
                modal.find('.orderNum-label').text('支付宝交易号：');
                modal.find('#orderNum').attr('placeholder','请输入支付宝交易号');
            }else if(payType==1){
                modal.find('.orderNum-label').text('微信转账单号：');
                modal.find('#orderNum').attr('placeholder','请输入微信转账单号');
            }else if(payType==3){
                modal.find('.orderNum-label').text('网银交易单号：');
                modal.find('#orderNum').attr('placeholder','请输入网银交易单号');
            }else {
                modal.find('#inputOderNum').hide();
            }

        }else {
            modal.find('#inputOderNum').hide();
        }

        modal.find('#orderId').text(id);
        modal.find('#company').text(company);
        modal.find('#price').text(price);
        modal.find('#textremark').val("");
    });
    $('#remarkModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        currOrder = button.data('order');
        remark = button.data('remark');
        var modal = $(this);
        modal.find('#textremarkinfo').val(remark);

    });

    $('#sure').on('click',function () {
        var orderNum = $('#orderNum').val();
        var remark = $('#textremark').val();
        if(orderNum.length==0 && currType != 4 && currType.length != 0){
            $('#orderNum').addClass('error-box');
            return;
        }
        $('#sureModal').modal('hide');
        $('#orderNum').removeClass('error-box');
        var data = {
            orderNum:currNum,
            orderId:currOrder,
            payMentNo:orderNum,
            remark:remark
        };
        fetchs.post('/confirmationOfAccount',data,function (res) {
            if(res.ifSuc==1){
                $('body').append('<div id="surenotify" class="alert" role="alert">确认中...(5s)</div>');
                var num = 5;
                var time = setInterval(function(){
                    num--;
                    if(num==0){
                        clearInterval(time);
                        $('.alert').alert('close');
                        notify('success','确认到账成功');
                        getOrderInfo(1);
                        return;
                    }
                    $('#surenotify').text('确认中...('+num+'s)');
                }, 1000);
            }else {
                notify('danger',res.msg);
            }
        })
    })
    $('#save').on('click',function () {
        var orderNum = $('#orderNum').val();
        var remark = $('#textremarkinfo').val();
        var data = {
            orderId:currOrder,
            remark:remark
        };
        $('#remarkModal').modal('hide');
        fetchs.post('/editRemarks',data,function (res) {
            if(res.ifSuc==1){
                notify('success','保存成功');
                getOrderInfo(1);
                return;
            }else {
                notify('danger',res.msg);
            }
        })
    })
});
