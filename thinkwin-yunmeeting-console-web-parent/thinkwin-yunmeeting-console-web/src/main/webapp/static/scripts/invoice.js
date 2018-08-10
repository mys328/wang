"use strict";

$(function () {

  soda.prefix('ng-');
  var page = {
    total: 0,
    size: 15,
    page: 1
  };
    var userInfo = JSON.parse(localStorage.getItem('console-userinfo'));
    var role = 0;
  var currentOrder;
  var p = Pages();
  p.callBack(callBack);

    var roomTpl = ' <div class="listBox" ng-if="data.length>0"><div class="scroll-list"> <table class="table">  <tbody id="roomBody"><tr> <th class="table-1"><span>订单号</span></th> <th class="table-2"><span>联系电话</span></th> <th class="table-3"><span>联系人</span></th> <th class="table-4"><span>地区</span></th> <th class="table-5"><span>详细地址</span></th> <th class="table-6"><span>邮政编码</span></th> <th class="table-7"><span>操作</span></th> </tr>  <tr ng-repeat="item in data" > <td class="table-1"><span data-toggle="tooltip" data-title="{{item.orderId|text}}">{{item.orderId|text}}</span></td> <td class="table-2"><span data-toggle="tooltip" data-title="{{item.phoneNumber|text}}">{{item.phoneNumber|text}}</span></td> <td class="table-3"><span data-toggle="tooltip" data-title="{{item.name|text}}">{{item.name|text}}</span></td> <td class="table-4"><span data-toggle="tooltip" data-title="{{item.location|text}}">{{item.location|text}}</span></td> <td class="table-5"><span data-toggle="tooltip" data-title="{{item.detailedAddress|text}}">{{item.detailedAddress|text}}</span></td> <td class="table-6"><span data-toggle="tooltip" data-title="{{item.postalCode|text}}">{{item.postalCode|text}}</span></td> <td class="table-7"> <span ng-if="item.status == 0" class="btn-span" data-toggle="modal" data-target="#cerModal" data-id="{{item.orderId}}">发票信息</span> <span ng-if="item.status == 0 && role == 1" class="btn-span sure-btn" data-toggle="modal" data-target="#sureModal" data-id="{{item.orderId}}">开具发票</span> <span ng-if="item.status == 1">已开票</span> </td> </tr> </tbody> </table> </div></div><div ng-if="data.length<1" class="nothing">暂无可开发票订单</div>';
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
    //查询权限信息
    fetchs.post('/saasRolePermission/findSaasPermissionsByParentId',{parentId:1003},function (res) {
        console.log(res);
        for (var i = 0; i<res.data.length; i++){
            var orgCode = res.data[i].orgCode;
            if(orgCode==1003001){
                role = 1;
                break;
            }
        }
        getInvoiceInfo(0);
    });
  function getInvoiceInfo(tag) {
    // body...
      var data = { content: "", status: "", type: "0", currentPage: page.page, pageSize: page.size };

      fetchs.post('/invoice/selectOrderInvoiceByPage', data, function (res) {
          if(tag==0){
              page.total = res.data.total;
              p.setCount(res.data.total);
          }
          if(res){
              if(res.data && res.data.list){

                  $('.table-Box').html(soda(roomTpl, {data:res.data.list, role: role}));
              }else {
                  $('.table-Box').html(soda(roomTpl, {data:[], role: role}));
              }
          }else {
              $('.table-Box').html(soda(roomTpl, {data:[], role: role}));
          }
      });
  }


  function callBack(pageCount, myPage) {
    //返回当前页和每页多少条数据
    page.page = pageCount;
    page.size = myPage;
    getInvoiceInfo(1);
  }
  $('#cerModal').on('show.bs.modal', function (event) {
      //查询发票信息
      var button = $(event.relatedTarget);
      var id = button.data('id');
      var data = {id: id}
      fetchs.post('/invoice/selectOrderInvoiceById',data,function (res) {
          if(res.ifSuc==1){
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
  });
  $('#sureModal').on('show.bs.modal', function (event) {
      var button = $(event.relatedTarget);
      var id = button.data('id');
      currentOrder = id;
      var modal = $(this);
      modal.find('#orderId').text(id);
  });
  $('#sure').on('click',function (event) {
      $('#sureModal').modal('hide');
      //开具发票
      fetchs.post('/invoice/issueInvoice',{orderId:currentOrder},function (res) {
         if(res.ifSuc==1){
             notify('success','发票开具成功')
             getInvoiceInfo(1);
         }else {
             notify('danger',res.msg);
         }
      });
  })
});
