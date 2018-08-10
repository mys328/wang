"use strict";

$(function () {

    soda.prefix('ng-');
    var page = {
      total: 0,
      page: 1,
      size: 15
    };
    var currentOrder = ''; //当前点击的订单号
    var role = 0; //是否有操作权限
    var p = Pages();
    p.callBack(callBack);


    var roomTpl = '<div class="listBox" ng-if="data.length>0"><div class="scroll-list"> <table class="table"> <tbody><tr> <th class="table-1"><span>订单号</span></th> <th class="table-2"><span>联系电话</span></th> <th class="table-3"><span>联系人</span></th> <th class="table-4"><span>地区</span></th> <th class="table-5"><span>详细地址</span></th> <th class="table-6"><span>邮政编码</span></th> <th class="table-7"><span>快递单号</span></th> <th class="table-8"><span>操作</span></th> </tr> <tr ng-repeat="item in data" > <td class="table-1"><span data-toggle="tooltip" data-title="{{item.orderId|text}}">{{item.orderId|text}}</span></td> <td class="table-2"><span data-toggle="tooltip" data-title="{{item.phoneNumber|text}}">{{item.phoneNumber|text}}</span></td> <td class="table-3"><span data-toggle="tooltip" data-title="{{item.name|text}}">{{item.name|text}}</span></td> <td class="table-4"><span data-toggle="tooltip" data-title="{{item.location|text}}">{{item.location|text}}</span></td> <td class="table-5"><span data-toggle="tooltip" data-title="{{item.detailedAddress|text}}">{{item.detailedAddress|text}}</span></td> <td class="table-6"><span data-toggle="tooltip" data-title="{{item.postalCode|text}}">{{item.postalCode|text}}</span></td> <td class="table-7"><span data-toggle="tooltip" data-title="{{item.expressTrackingNumber|text}}">{{item.expressTrackingNumber|text}}</span></td> <td class="table-8"> <span ng-if="item.status == 1 && role == 1" class="btn-span" data-id="{{item.orderId}}">邮寄发票</span> <span ng-if="item.status == 2">已寄出</span> </td> </tr> </tbody> </table> </div></div><div ng-if="data.length<1" class="nothing">暂无发票信息</div>';

    //查询权限信息
    fetchs.post('/saasRolePermission/findSaasPermissionsByParentId',{parentId:1004},function (res) {
        console.log(res);
        for (var i = 0; i<res.data.length; i++){
            var orgCode = res.data[i].orgCode;
            if(orgCode==1004001){
                role = 1;
                break;
            }
        }
        getOrderInvoice(0);
    });
  //请求数据
    function getOrderInvoice(tag) {
      var data = {
          content: '',
          type: 1,
          currentPage: page.page,
          pageSize: page.size
      }
        fetchs.post('/invoice/selectOrderInvoiceByPage',data,function (res) {
            if(res.ifSuc==1){
              if(tag==0){ //第一次请求设置分页
                  page.total = res.data.total;
                  p.setCount(page.total);
              }
              if(res.data && res.data.list){
                  $('.table-Box').html(soda(roomTpl, {data:res.data.list, role: role}));
              }else {
                  $('.table-Box').html(soda(roomTpl, {data:[], role: role}));
              }

            }else {
                $('.table-Box').html(soda(roomTpl, {data:[]}));
            }
        })
    }

  function callBack(pageCount, myPage) {
    //返回当前页和每页多少条数据
    page.page = pageCount;
    page.size = myPage;
    getOrderInvoice(1);
  }

    $('body').on('mouseenter', '[data-toggle="tooltip"]', function (e) {
        var text = $(this).data('title');
        var id = $(this).data('id');
        var size = parseInt($(this).css('fontSize'));
        if ($(this).parent().width()<size*text.length) {
            $(this).tooltip({
                placement: function (tip, element) {
                    return (window.scrollY+50 < $(element).offset().top) ? 'top':'bottom';
                },
                trigger: 'hover'
            })
            $(this).tooltip('toggle');
        }
    })


    //发票详情的表单显示
  $('body').on('click', '.btn-span', function (event) {
    var id = $(this).data('id');
    currentOrder = id;
    var data = {id: id}
    fetchs.post('/invoice/selectOrderInvoiceById',data,function (res) {
        if(res.ifSuc==1){
            $('.invoice-info-box').addClass('show');
            //判断开具类型是否为个人
            if(res.data.issueType==0){
              //个人
                $('.show-invoice-info').show();
                $('.show-enterprise-common').hide();
                $('.show-enterprise-special').hide();
            }else {
                //判断发票类型是否为普通发票
              if(res.data.invoiceType==0){
                //普通
                  $('#show-comname').text(res.data.header); //抬头
                  $('#show-comtax').text(res.data.taxNumber); //税务证号
                  $('.show-invoice-info').hide();
                  $('.show-enterprise-common').show();
                  $('.show-enterprise-special').hide();
              }else {
                //专用
                  $('#show-spename').text(res.data.header); //抬头
                  $('#show-spetax').text(res.data.taxNumber); //税务证号
                  $('#show-speBank').text(res.data.openingBank); //开户银行
                  $('#show-speacount').text(res.data.accountNumber); //开户账号
                  $('#show-speaddress').text(res.data.address); //注册地址
                  // $('#show-speaddress').text('北京市海淀区西三旗昌临813号京玺中韩文化创意园A区10号楼104');
                  $('#show-spetel').text(res.data.invoicePhone); //联系电话
                  $('.show-invoice-info').hide();
                  $('.show-enterprise-common').hide();
                  $('.show-enterprise-special').show();
              }
            }
        }
    })

    // event.stopPropagation();
  });
  $('body').on('click', '.invoice-info-box', function (event) {
      // var is = $(event.target).hasClass('dropdown-item');
      // if(is==false){
      //     is = $(event.target).hasClass('btn');
      // }
      // if(is==false){
      //     is = $(event.target).hasClass('form-control');
      // }
      // if(is==false){
      //     event.stopPropagation();
      // }
  });

  $('body').click(function (event) {
      // event.stopPropagation();
    var value = event.target.classList;
    var is = $(event.target).hasClass('dropdown-item');
    is = $(event.target).hasClass('send-div');
    // if(is==false){
    //     is = $(event.target).hasClass('btn');
    // }
    // if(is==false){
    //     is = $(event.target).hasClass('form-control');
    // }
    // if(is==false){
    //     is = $(event.target).hasClass('form-group');
    // }
    //   if(is==false){
    //       is = $(event.target).hasClass('invoice-info-box');
    //   }
    //   if(is==false){
    //       is = $(event.target).hasClass('inner-box');
    //   }
      if (is == false) {
          $('.invoice-info-box').removeClass('show');
          $('#comaddress').val('')
          $('#comaddress').removeClass('error-box');
      }
  });
  //取消-表单消失
  $('body').on('click', '.cancel-btn', function () {
    $('.invoice-info-box').removeClass('show');
  });
  //提交-表单消失
  $('body').on('click', '.countersign-btn', function () {
    //判断快递公司和快递单号是否填写
    if ($('#comaddress').val().length == 0) {
      $('#comaddress').addClass('error-box');
      // $('#error').show();
    } else {
      $('#comaddress').removeClass('error-box');
      // $('#error').hide();
      sendInovice();
    }
  });
  $('body').on('click', '#selectVersion', function (event) {
    // event.stopPropagation();
  });

  //邮寄发票
  function sendInovice() {
    var data = {
      orderId: currentOrder,
      expressTrackingNumber: $('#comaddress').val(),
      expressCompany: $('#version-type').text()?$('#version-type').text():'顺丰',
      expressGateUrl: 'http://www.sf-express.com/cn/sc/'
    };
    fetchs.post('/invoice/mailInvoice', data, function (res) {
      // body...
      if (res.ifSuc == 1) {
        notify('success','邮寄成功');
          $('#comaddress').val('')
          $('#comaddress').removeClass('error-box');
          $('.invoice-info-box').removeClass('show');
        // $('.invoice-info-box').removeClass('show');
        getOrderInvoice(1);
      } else {
          notify('danger','邮寄失败');
      }
    });
  }
});
