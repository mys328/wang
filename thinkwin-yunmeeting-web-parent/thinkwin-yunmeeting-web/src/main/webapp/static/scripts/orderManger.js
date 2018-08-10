$(function(){
    $('.icon-close').on('click',function(){
      $('.top-div').hide();
    })
    $('#buyNow').on('click',function(){
      alert('立即续费');
    });
    var userInfo = JSON.parse(localStorage.getItem('userinfo'));
    //版本过期
    $('#version-info').addClass('overinfo');
    $('#version-state').addClass('overdue');
    $('#user').text(userInfo.userName);
    personstatistics();
    var roomTpl = '<div class="listBox"> <table class="table">\n    <thead>\n      <tr>\n        <th>订单号</th>\n        <th>产品名称</th>\n        <th>有效时间</th>\n        <th>订单状态</th>\n        <th>订单金额</th>\n        <th>操作</th>\n      </tr>\n    </thead>\n    <tbody id="roomBody">\n      <tr ng-repeat="item in list" >\n        <td><span class="orderNumber" onclick="didClickOrderNumber(\'{{item.orderId}}\')">{{item.orderSn}}</span></td>\n        <td>{{item.orderSubject}}</td>\n        <td>{{item.rentStart|date:\'YYYY-MM-DD\'}}~{{item.rentEnd|date:\'YYYY-MM-DD\'}}</td>\n        <td>{{item.statusName}}</td>\n        <td>{{item.totalPrice}}\u5143</td>\n        <td>\n          <span ng-if="item.status==0" class="clear-primary" onclick="didClickPayment(\'{{item.orderId}}\',\'{{item.orderSource}}\')">\u4ED8\u6B3E</span>\n          <span ng-if="item.status==0||item.status==8" class="clear-primary" data-toggle="modal" data-target="#cancelOrder" data-id="{{item.orderId}}" data-name="{{item.orderSubject}}">\u53D6\u6D88</span>\n        </td>\n      </tr>\n    </tbody>\n  </table>\n  <span class="reminder">待付款订单请于24小时内完成支付，逾期订单自动关闭。</span>\n  </div>\n   ';

    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        if ($(e.target).attr('href') == '#update') {

        } else if ($(e.target).attr('href') == '#invoice') {
            selectInvoice();
            selectAddress();
            getInvoice();
        }
        console.log($(e.target).attr('href'));
        // console.log(e.relatedTarget);
    });

    //发票信息
    var invoiceId = ''; //发票信息ID
    var issueType = '0'; //开具类型
    var invoiceTitle = ''; //发票抬头
    var invoiceType = '0'; //发票类型
    var invoiceTax = '';  //税务证号
    var invoicebank = ''; //开户银行
    var invoiceAcount = ''; //开户账号
    var resignAddress = ''; //注册地址
    var tel = ''; //联系电话
    var invoiceInfo = {};

    var addInvoiceInfo = false; //是否添加了发票信息
    var addInvoiceAddress = false; //是否添加了邮寄地址
    var data = {};

    //没有填写发票信息
    $('.null-invoice').show();
    //个人发票信息
    $('.show-invoice-info').hide();
    //企业普通发票信息
    $('.show-enterprise-common').hide();
    //企业增值发票信息
    $('.show-enterprise-special').hide();

    // selectInvoice();
    function selectInvoice(){
      fetchs.post('/invoice/selectInvoiceList',data,function(res){
        if(res.ifSuc==1&&res.data){
          //添加了发票信息
          addInvoiceInfo = true;
          invoiceId = res.data.id;
          issueType = res.data.issueType;
          invoiceTitle = res.data.header;
          invoiceType = res.data.invoiceType;
          invoiceTax = res.data.taxNumber;
          invoicebank = res.data.openingBank;
          invoiceAcount = res.data.accountNumber;
          resignAddress = res.data.address;
          tel = res.data.phoneNumber;
          if(issueType.toString() === '0'){
            showPersonnel();
          }else {
            if(invoiceType.toString() === '0'){
              showCommon();
            }else {
              showSpecial();
            }
          }
        }else {
          addInvoiceInfo = false;
          //添加发票
          //没有填写发票信息
          $('.null-invoice').show();
          $('#none-info').show(); //隐藏立即添加的按钮标签
          $('#reminder-info').hide(); //显示提示标签
          $('#form-info').hide(); //显示form表单
          $('#personnel-invoice').show(); //显示个人发票
          $('#enterprise-invoice').hide();
          $('#value-added').hide();
          //个人发票信息
          $('.show-invoice-info').hide();
          //企业普通发票信息
          $('.show-enterprise-common').hide();
          //企业增值发票信息
          $('.show-enterprise-special').hide();
          $('#enterprise-issue').prop('checked',false);
          $('#personnel-issue').prop('checked', true);
          issueType = '0';
          $('#special').prop('checked',false);
          $('#common').prop('checked', true);
          invoiceType = '0';
        }
      });
    }

    //发票管理
    //添加发票信息
    $('#add-info').on('click',function(){
        $('#none-info').hide();
        $('#reminder-info').show();
        $('#form-info').show();
    })

    //修改个人发票信息
    $('#change-personnel').on('click',function(){
      //个人发票信息
      $('.show-invoice-info').hide();
      //企业普通发票信息
      $('.show-enterprise-common').hide();
      //企业增值发票信息
      $('.show-enterprise-special').hide();

      $('.null-invoice').show();
      $('#none-info').hide();
      $('#reminder-info').show();
      $('#form-info').show();
      $('#personnel-invoice').show();
      $('#enterprise-invoice').hide();
      $('#enterprise-issue').prop('checked',false);
      $('#personnel-issue').prop('checked', true);
      issueType = '0';
      $('#common').prop('checked',true);
      $('#special').prop('checked', false);
      invoiceType = '0';
    })
    //修改企业普通发票
    $('#change-common').on('click',function(){
      //传值
      $('#enterprise-name').val(invoiceTitle);
      $('#enterprise-number').val(invoiceTax);
      $('#enterprise-issue').prop('checked',true);
      $('#personnel-issue').prop('checked', false);
      issueType = '1';
      $('#common').prop('checked',true);
      $('#special').prop('checked', false);
      invoiceType = '0';
      //个人发票信息
      $('.show-invoice-info').hide();
      //企业普通发票信息
      $('.show-enterprise-common').hide();
      //企业增值发票信息
      $('.show-enterprise-special').hide();
      $('.null-invoice').show(); //显示添加div
        $('#none-info').hide(); //隐藏立即添加的按钮标签
        $('#reminder-info').show(); //显示提示标签
        $('#form-info').show(); //显示form表单
        $('#personnel-invoice').hide(); //隐藏个人表单
        $('#enterprise-invoice').show(); //显示企业表单
        $('#value-added').hide(); //隐藏企业增值专用发票
    })
    //修改企业增值发票
    $('#change-special').on('click',function(){
      //传值
      $('#enterprise-name').val(invoiceTitle);
      $('#enterprise-number').val(invoiceTax);
      $('#bank-open').val(invoicebank);
      $('#bank-acount').val(invoiceAcount);
      $('#resign-address').val(resignAddress);
      $('#com-tel').val(tel);
      $('#enterprise-issue').prop('checked',true);
      $('#personnel-issue').prop('checked', false);
      issueType = '1';
      $('#special').prop('checked',true);
      $('#common').prop('checked', false);
      invoiceType = '1';
      //个人发票信息
      $('.show-invoice-info').hide();
      //企业普通发票信息
      $('.show-enterprise-common').hide();
      //企业增值发票信息
      $('.show-enterprise-special').hide();

      $('.null-invoice').show(); //显示添加div
      $('#none-info').hide(); //隐藏立即添加的按钮标签
      $('#reminder-info').show(); //显示提示标签
      $('#form-info').show(); //显示form表单
      $('#personnel-invoice').hide(); //隐藏个人表单
      $('#enterprise-invoice').show(); //显示企业表单
      $('#value-added').show();
    })

    //开具类型选择
    $('.form-check-input').on('click',function(){
      var temp = $(this).prop('value');
      var name = $(this).prop('name');
      if(name === 'issue-type'){
        //开具类型
        if(temp === '1'){
          //开具类型为企业
          $('#personnel-invoice').hide();
          $('#enterprise-invoice').show();
          $('#value-added').hide();
        }else {
          //开具类型为个人
          $('#personnel-invoice').show();
          $('#enterprise-invoice').hide();
          $('#common').prop('checked',true);
          $('#special').prop('checked', false);
          invoiceType = '0';
        }
        issueType = temp;
      }else if(name === 'invoice-type'){
        //发票类型
        if(temp === '1'){
          //发票类型为增值税专用发票
          $('#value-added').show();
        }else {
          $('#value-added').hide();
        }
        invoiceType = temp;
      }
    })

    //显示个人发票
    function showPersonnel(){
      $('.null-invoice').hide();
      $('.show-invoice-info').show();
      $('.show-enterprise-common').hide();
      //企业增值发票信息
      $('.show-enterprise-special').hide();

    }
    //显示企业普通发票
    function showCommon(){
      $('.null-invoice').hide();
      $('.show-invoice-info').hide();
      $('.show-enterprise-common').show();
      //企业增值发票信息
      $('.show-enterprise-special').hide();

      //企业增值普通发票
      $('#show-comname').text(invoiceTitle); //发票抬头
      $('#show-comtax').text(invoiceTax); //税务证号
    }
    //显示企业专用发票
    function showSpecial(){
      $('.null-invoice').hide();
      $('.show-invoice-info').hide();
      $('.show-enterprise-common').hide();
      //企业增值发票信息
      $('.show-enterprise-special').show();

      //企业增值专用发票
      $('#show-spename').text(invoiceTitle); //发票抬头
      $('#show-spetax').text(invoiceTax); //税务证号
      $('#show-speBank').text(invoicebank); //开户银行
      $('#show-speacount').text(invoiceAcount); //开户账号
      $('#show-speaddress').text(resignAddress); //注册地址
      $('#show-spetel').text(tel); //联系电话
    }
    //提交发票信息按钮事件
    $('#submit-info').on('click',function(){
      //验证表单
      if(checkInvoiceInfo() == true){
        //验证通过，提交发票信息
        if(invoiceId.length==0){
          //添加
          fetchs.post('/invoice/insertInvoice',invoiceInfo,function(res){
              if(res.ifSuc == 1){
                  //修改成功
                  notify('success','添加成功');
                  selectInvoice();
                  $('#enterprise-name').val('');
                  $('#enterprise-number').val('');
                  $('#bank-open').val('');
                  $('#bank-acount').val('');
                  $('#resign-address').val('');
                  $('#com-tel').val('');
                  $('#enterprise-issue').prop('checked',false);
                  $('#personnel-issue').prop('checked', true);
                  issueType = '0';
                  $('#special').prop('checked',false);
                  $('#common').prop('checked', true);
                  invoiceType = '0';
              }else {
                  if(res.code == -1){
                      notify('danger','网络连接失败，请检查网络后重试')
                  }else {
                      notify('danger',res.code);
                  }
              }
          });
        }else {
          //修改
          invoiceInfo['id'] = invoiceId;
          fetchs.post('/invoice/updateInvoice',invoiceInfo,function(res){
              if(res.ifSuc == 1){
                  //修改成功
                  notify('success','修改成功');
                  selectInvoice();
                  $('#enterprise-name').val('');
                  $('#enterprise-number').val('');
                  $('#bank-open').val('');
                  $('#bank-acount').val('');
                  $('#resign-address').val('');
                  $('#com-tel').val('');
                  $('#enterprise-issue').prop('checked',false);
                  $('#personnel-issue').prop('checked', true);
                  issueType = '0';
                  $('#special').prop('checked',false);
                  $('#common').prop('checked', true);
                  invoiceType = '0';
              }else {
                  if(res.code == -1){
                      notify('danger','网络连接失败，请检查网络后重试')
                  }else {
                      notify('danger',res.code);
                  }
              }
          })
        }
      }
    })
    //验证发票信息
    function checkInvoiceInfo(){
      if(issueType === '1'){
        //开具类型为企业
        var isSuc = true;
        //判断发票抬头是否为空
        if($('#enterprise-name').val().length == 0){
          $('#enterprise-name').addClass('error-border');
          isSuc = false;
        }else {
          $('#enterprise-name').removeClass('error-border');
        }
        invoiceInfo['issueType'] = "1";
        invoiceInfo['header'] = $('#enterprise-name').val();
        //判断税务证号
        if ($('#enterprise-number').val().length == 0) {
          $('#enterprise-number').addClass('error-border');
          isSuc = false;
        }else {
          $('#enterprise-number').removeClass('error-border');
        }
        invoiceInfo['taxNumber'] = $('#enterprise-number').val();
        //判断发票类型
        if (invoiceType == '1'){
          //增值税专用发票
          invoiceInfo['invoiceType'] = "1";
          //判断开户银行
          if($('#bank-open').val().length == 0) {
            $('#bank-open').addClass('error-border');
            isSuc = false;
          }else {
            $('#bank-open').removeClass('error-border');
          }
          invoiceInfo['openingBank'] = $('#bank-open').val();
          //判断开户账号
          if($('#bank-acount').val().length == 0){
            $('#bank-acount').addClass('error-border');
            isSuc = false;
          }else {
            $('#bank-acount').removeClass('error-border');
          }
          invoiceInfo['accountNumber'] = $('#bank-acount').val();
          //判断注册地址
          if($('#resign-address').val().length == 0){
            $('#resign-address').addClass('error-border');
            isSuc = false;
          }else {
            $('#resign-address').removeClass('error-border');
          }
          invoiceInfo['address'] = $('#resign-address').val();
          //判断联系电话
          if($('#com-tel').val().length == 0){
            $('#com-tel').addClass('error-border');
            isSuc = false;
          }else {
            $('#com-tel').removeClass('error-border');
          }
          invoiceInfo['phoneNumber'] = $('#com-tel').val();
        }else {
          invoiceInfo['invoiceType'] = "0";
        }
        return isSuc;
      }else {
        invoiceInfo['issueType'] = "0";
        return true;  
      }
    }
    //取消填写发票信息按钮事件
    $('#clear-info').on('click',function(){
        $('.invoice-info .form-control').removeClass('error-border');
        selectInvoice();
        $('#enterprise-name').val('');
        $('#enterprise-number').val('');
        $('#bank-open').val('');
        $('#bank-acount').val('');
        $('#resign-address').val('');
        $('#com-tel').val('');
        $('#enterprise-issue').prop('checked',false);
        $('#personnel-issue').prop('checked', true);
        issueType = '0';
        $('#special').prop('checked',false);
        $('#common').prop('checked', true);
        invoiceType = '0';
      // if(addInvoiceInfo == true) {
      //   if(issueType == '0'){
      //     showPersonnel();
      //   }else {
      //     if(invoiceType == '0'){
      //       showCommon();
      //     }else {
      //       showSpecial();
      //     }
      //   }
      // }else {
      //   //没有填写发票信息
      //   $('.null-invoice').show();
      //   //个人发票信息
      //   $('.show-invoice-info').hide();
      //   //企业普通发票信息
      //   $('.show-enterprise-common').hide();
      //   //企业增值发票信息
      //   $('.show-enterprise-special').hide();
      //   $('#form-info').hide();
      //   $('#reminder-info').hide();
      //   $('#none-info').show();
      // }
      
    })



    //发票寄送地址管理
    var addressId = '';
    var addressName = ''; //收件人姓名
    var addressCity = ''; //收件人所在地区
    var province = ''; //省
    var city = '';  //市
    var county = ''; //县
    var addressDetail = ''; //收件人详细地址
    var addressCode = ''; //收件人邮编
    var addressTel = ''; //收件人联系电话
    var addressData = {}; //地址信息
    init_city_select($("#address-city"));
    //查询地址信息
    var temp = {};

    // selectAddress();
    function selectAddress(){
      fetchs.post('/invoice/selectAddressList',temp,function(res){
      if(res.ifSuc==1&&res.data){
        addInvoiceAddress = true;
        addressId = res.data.id;
        addressName = res.data.name;
        province = res.data.province;
        city = res.data.city;
        county = res.data.county;
        addressCity = province + '-' + city + '-' + county;
        addressDetail = res.data.detailedAddress;
        addressCode = res.data.postalCode;
        addressTel = res.data.phoneNumber;

        showAddress();
      }else {
        addInvoiceAddress = false;
        //显示添加
          //赋值
          $('#address-name').val('');
          $('#address-city').val('');
          $('#address-detail').val('');
          $('#address-code').val('');
          $('#address-tel').val('');
          $('.none-invoice-address').show();
        $('.show-invoice-address').hide();
        $('#none-address').show();
        $('#form-address').hide();

      }
    });
    }
  
    //显示地址
    function showAddress(){
      $('.none-invoice-address').hide();
      $('.show-invoice-address').show();
      $('#show-addressName').text(addressName);
      $('#show-addressCity').text(addressCity);
      $('#show-addressDetail').text(addressDetail);
      $('#show-addressCode').text(addressCode);
      $('#show-addressTel').text(addressTel);
    }    

    //添加地址
    $('#add-address').on('click',function(){
      $('#none-address').hide();
      $('#form-address').show();
    })
    //修改地址
    $('#changeAddress').on('click',function(){
      $('.none-invoice-address').show();
      $('.show-invoice-address').hide();
      $('#none-address').hide();
      $('#form-address').show();

      //赋值
      $('#address-name').val(addressName);
      $('#address-city').val(addressCity);
      $('#address-detail').val(addressDetail);
      $('#address-code').val(addressCode);
      $('#address-tel').val(addressTel);  
    })
    //保存地址
    $('#submit-address').on('click',function(){
      if(checkAddress() == true){
        if (addressId.length == 0) {
          fetchs.post('/invoice/insertAddress',addressData,function(){
            selectAddress();
          });
        }else {
          addressData['id'] = addressId;
          fetchs.post('/invoice/updateAddress',addressData,function(){
            selectAddress();
          })
        }
             
      }
    })
    //取消添加/修改
    $('#clear-address').on('click',function(){
        $('.invoice-address .form-control').removeClass('error-border');
        $('#errormsg').hide();
      if(addInvoiceAddress == true){
        showAddress();
      }else {
        //显示添加
          //赋值
          $('#address-name').val('');
          $('#address-city').val('');
          $('#address-detail').val('');
          $('#address-code').val('');
          $('#address-tel').val('');
          $('.none-invoice-address').show();
        $('.show-invoice-address').hide();
        $('#none-address').show();
        $('#form-address').hide();

      }
    })
    //校验地址信息
    function checkAddress(){
      var isSuc = true;
      if($('#address-name').val().length == 0){
        isSuc = false;
        $('#address-name').addClass('error-border');
      }else {
        $('#address-name').removeClass('error-border');
      }
      addressData['name'] = $('#address-name').val();
      if ($('#address-city').val().length == 0) {
        isSuc = false;
        $('#selectCity').addClass('error-border');
      }else {
        $('#selectCity').removeClass('error-border');
      }
      // addressCity = $('#address-city').val();
      var arr = $('#address-city').val().split('-');
      if(arr.length==3){
        province = arr[0];
        city = arr[1];
        county = arr[2];
      }else if(arr.length==2){
        province = arr[0];
        city = arr[1];
        county = '';
      }else if(arr.length==1){
        province = arr[0];
        city = '';
        county = '';
      }else {
        province = '';
        city = '';
        county = '';
      }
      addressData['province'] = province;
      addressData['city'] = city;
      addressData['county'] = county;
      addressData['street'] = '';
      if ($('#address-detail').val().length == 0) {
        isSuc = false;
        $('#address-detail').addClass('error-border');
      }else {
        $('#address-detail').removeClass('error-border');
      }
      addressData['detailedAddress'] = $('#address-detail').val();
      addressData['postalCode'] = $('#address-code').val();
      if($('#address-tel').val().length == 0){
        isSuc = false;
        $('#address-tel').addClass('error-border');
      }else {
          if(isPhone($('#address-tel').val())==false){
              isSuc = false;
              $('#errormsg').show();
              $('#address-tel').addClass('error-border');
          }else {
              addressData['phoneNumber'] = $('#address-tel').val();
              $('#errormsg').hide();
              $('#address-tel').removeClass('error-border');
          }
      }


      return isSuc;
    }
    //验证输入内容是否是手机号
    function isPhone (string) {
        var pattern = /^(13[0-9]|14[5-9]|15[012356789]|166|17[0-8]|18[0-9]|19[8-9])[0-9]{8}$/;
        if (pattern.test(string)) {
            return true;
        }
        return false;
    };
    function getInvoice(){
        var data = {currentPage:1,pageSize:10}
        fetchs.post('/order/invoiceRequest',data,function (res) {
            if(res.ifSuc==1&&res.data!=null){
                $('.get-invoice').html(soda(invoice,res.data));
            }else {
                $('.get-invoice').html(soda(invoice,{list:[]}));
            }
        })

    }

    $('body').on('click','#getInvoice',function(e){
        //检测发票信息
        if(addInvoiceInfo==true && addInvoiceAddress==true){
          //可以索取
          var order = $(this).data('id'); //订单号
          var id = $(this).data('orderid'); //索取发票需要传入的id
          if (order) {
              var data = {'orderId':id,'orderSN':order};
              fetchs.post('/invoice/requestInvoice',data,function (res) {
                  if(res.ifSuc==1){
                      //索取成功
                      var msg = '订单号为'+order+'的发票申请已受理，快递将在7个工作日内发出，请注意查收'
                      notify1('success',msg);
                  }else {
                      var msg = '订单号为'+order+'的发票申请失败，请重新申请'
                      notify('danger',msg);
                  }
                  getInvoice();
              });
          }
        }else {
          if(addInvoiceInfo==false&&addInvoiceAddress==true){
            notify('danger','请完善发票信息后再索取发票')
          }else if(addInvoiceInfo==true&&addInvoiceAddress==false){
            notify('danger','请完善寄送地址后再索取发票')
          }else{
            notify('danger','请完善发票信息及寄送地址后再索取发票')
          }
        }
    })

    //发票索取
    var invoice = '\n        <table class="table" ng-if="list.length>0">\n          <thead>\n            <tr>\n              <th>\u8BA2\u5355\u53F7</th>\n              <th>\u521B\u5EFA\u65F6\u95F4</th>\n              <th>\u8D2D\u4E70\u670D\u52A1</th>\n              <th>\u8BA2\u5355\u91D1\u989D</th>\n              <th>\u64CD\u4F5C</th>\n            </tr>\n          </thead>\n          <tbody>\n            <tr ng-repeat="item in list">\n              <td><span>{{item.orderSn}}</span></td>\n              <td><span>{{item.createTime|date:\'YYYY-MM-DD HH:mm\'}}</span></td>\n              <td><span>{{item.orderSubject}}</span></td>\n              <td><span ng-html="item.payPrice + \'\u5143\'"></span></td>\n              <td>\n                <span ng-if="item.status==4">\u5F00\u7968\u4E2D</span>\n                <span class="change-btn" ng-if="item.status==1" id="getInvoice" data-id="{{item.orderSn}}" data-orderId="{{item.orderId}}">\u7D22\u53D6</span>\n                <span ng-if="item.status==5">\n                  <span style="font-size: 14px">\u5DF2\u5F00\u7968</span>\n                  <span class="change-btn" style="margin-left:16px;" data-toggle="modal" data-target="#orderModal" data-id="{{item.orderSn}}">\u67E5\u770B\u5FEB\u9012\u5355\u53F7</span>\n                </span>\n              </td>\n            </tr>\n          </tbody>\n        </table>\n        <div class="none" ng-if="list.length<1">\u60A8\u8FD8\u6CA1\u6709\u53EF\u4EE5\u5F00\u7968\u7684\u8BA2\u5355</div>';
    //点击查看快递单号按钮事件
    var url = 'http://www.sf-express.com/cn/sc/';
    $('#orderModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var id = button.data('id');
        var modal = $(this);""
        var data = {'id':id};
        fetchs.post('/invoice/selectOrderInvoiceById',data,function (res) {
            if(res.ifSuc==1&&res.data){
                var company = res.data.expressCompany!=null?res.data.expressCompany :'顺丰';
                var text = '您的快递单号为'+res.data.expressTrackingNumber+'，承运公司为'+company+'，您可前往快递公司官网，查看物流状态';
                $('#gotowebsite').attr('disabled',false);
                if(res.data.expressTrackingNumber==null) {
                    text = '您的发票还未寄出，请耐心等待'
                    $('#gotowebsite').attr('disabled', true);
                }
                    modal.find('#msg').text(text);
                url = res.data.expressGateUrl;
            }else {
                notify('danger','快递单号获取失败');
            }
        });


    })
    $('#gotowebsite').on('click',function () {
        $('#orderModal').modal('hide');
        if(url==null||url.length==0){
            url = 'http://www.sf-express.com/cn/sc/';
        }
        window.open(url);
    })

   function personstatistics() {
     fetchs.post('/orderauth/personstatistics',{}, function (result) {
         if(result.ifSuc==1) {
             var data = result.data;
             var category =  ['免费版','专业版', '专业版', '专业版' ];
             var version =data.version; //获取当前版本
             $("#version-info.overinfo").html(category[parseInt(version)]);
             if(data.validity==0){//拿到有效期的状态
                 $("#version-time").html("永久");
                 $(".status0").show();
             }else {
                 //进行日期判断,看是否已经过期
                 var timestamp=new Date().getTime();
                 if(timestamp>data.validity){ //已经过期
                    $(".overinfo").css("color","#959595");
                    $("#version-time").html(formatDate(data.validity));
                    $(".status3").show();
                    $(".overdue").show();
                    $(".top-div").show();
                 }else{ //老用户,还没有过期
                     var timestamp=new Date().getTime();
                     var time=parseInt((data.validity-timestamp) / (1000 * 60 * 60 * 24));
                     if(time==7){
                         $(".top-div").html('<span>您的企业当前版本有效期还剩7天，过期后将无法使用，请及时续费。<a id="buyNow">立即续费</a></span><span class="icon icon-close"></span>');
                         $(".top-div").show();
                     }else if(time==30){
                         $(".top-div").html('<span>您的企业当前版本有效期还剩30天，过期后将无法使用，请及时续费。<a id="buyNow">立即续费</a></span><span class="icon icon-close"></span>');
                         $(".top-div").show();
                     }
                    $("#version-time").html(formatDate(data.validity));
                    //如果是最高版本,显示增容,续费
                   $(".status1,.status4").show();
                 }
             }
             //判断是否为免费版增加高亮
             if(version==0){
                 $(".version0").addClass("selected");
                 $(".introduce0").show();
             }else{
                 $(".version1").addClass("selected");
                 $(".introduce1").show();
             }

             //剩余人数
             if (data.residuePersonNum <= 0) {
                 $('#pie1 span i').parent().parent().addClass('none-space');
                 $('#pie1 span i').parent().parent().html('已用完');
             } else {
                 $('#pie1 span i').html(isNull(data.residuePersonNum));
             }
             //员工总人数
             $('#pie1 .total').html(isNull(data.totalPersonNum + "人"));
             //已激活人数
             $('#pie1 .activated').html(isNull(data.activePersonNum + "人"));
             //未激活人数
             $('#pie1 .inactive').html(isNull(data.notActivePersonNum + "人"));
             //剩余会议室间数
             if (data.freeRooms <= 0) {
                 $('#pie2 span i').parent().parent().addClass('none-space')
                 $('#pie2 span i').parent().parent().html('已用完');
             } else {
                 $('#pie2 span i').html(isNull(data.freeRooms));
             }
             //会议室总数
             $('#pie2 .roomTotal').html(isNull(data.totalRooms) + "间");
             //已使用数量
             $('#pie2 .roomUsed').html(isNull(data.usedRooms) + "间");
             //剩余空间容量
             if (data.freeSpace <= 0) {
                 $('#pie3 span i').parent().parent().addClass('none-space')
                 $('#pie3 span i').parent().parent().html('已用完');
             } else {
                 if (data.freeSpace >= 1024) {
                     //如果赠送流量大于1G,则显示为G
                     var gb_Flow = data.freeSpace / 1024;
                     //toFixed(1);四舍五入保留一位小数
                     var flow = gb_Flow.toFixed(1);
                     $('#pie3 span i').html(isNull(flow));
                     $('#pie3 span .unit').html(isNull("G"));
                 } else {
                     $('#pie3 span i').html(isNull(data.freeSpace));
                 }
             }
             //总空间容量
             $('#pie3 .totalSpace').html(getFlow(isNull(data.totalSpace)));
             //已使用空间容量
             $('#pie3 .usedSpace').html(getFlow(isNull(data.usedSpace)));

             //存储空间的比例
             pie($('#pie1'), data.residuePersonNum / data.totalPersonNum);
             pie($('#pie2'), data.freeRooms / data.totalRooms);
             pie($('#pie3'), data.freeSpace / data.totalSpace);
         }
     });
   }

    function pie(ele, ratio) {
        var $circle = ele.find('circle');
        var i = 439.8 * (1-ratio);
        if(ratio<0.2){//已用超过百分之80的时候,剩余量变为红色
            $circle.eq(1).removeClass("active").addClass("drain");
        }
        // $circle.eq(0).attr('stroke-dashoffset',i);
        $circle.eq(1).attr('stroke-dashoffset',439.8 + i);
    }
    function isNull(value){
        return value != null ? value : 0;
    }
    function getFlow(flowVlueBytes){
        var flow = "";
        //如果赠送流量小于1G.则显示为MB
        if(flowVlueBytes< 1024){
           flow = flowVlueBytes + 'M';
        }else if(flowVlueBytes >= 1024){
           //如果赠送流量大于1G,则显示为G
            var gb_Flow = flowVlueBytes/1024;
            //toFixed(1);四舍五入保留一位小数
            flow = gb_Flow.toFixed(1)+'G';
        }
        return flow;
    }
    function formatDate(date) {
      var now=new Date(date);
      var year=now.getFullYear();
      var month=now.getMonth()+1;
      if(month<10){
          month = "0"+month;
      }
      var date=now.getDate();
      if(date<10){
          date = "0"+date;
      }
      return year+"-"+month+"-"+date;
    }
    $("body").on("click",".icon-close",function(){
       $('.top-div').hide();
    });

// 查询订单
    searchOrder();
    function searchOrder(){
        $.ajax({
            'type': 'POST',
            'url': '/order/query?token='+fetchs.token,
            'data': JSON.stringify({"status":[0,1,8],"pageSize":100}),
            'contentType': 'application/json',
            success:function(res){
                if(res.ifSuc==1){
                  if(res.data!=null){
                     $('#orderList').html(soda(roomTpl, res.data));
                     for(var i=0,j=res.data.list.length;i<j;i++){
                         if(res.data.list[i].status==0){//查询到有未完成的订单
                           $('#orderReminder').find('input.name1').val(res.data.list[i].orderId);
                           $("#orderReminder").modal('show');
                         }
                     }
                  }else{
                      $('#orderList').html("");
                  }
                }else {
                    notify('danger',res.msg);
                }
            },
            error:function () {
                data = {
                    ifSuc:0,
                    code:-1,
                    msg:"网络连接失败，请检查网络",
                }
            }
        });
    }

    $('#orderReminderForm').on('submit', function (event) {
        event.preventDefault();
        var data = eval('(' + '{' + $(this).serialize().replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
        window.open('/order/info?token='+fetchs.token+'&orderId='+data.id);
        $("#orderReminder").modal('hide');
    });

//取消订单接口
    function cancelOrder(orderId){
        $.ajax({
            'type': 'GET',
            'url': '/order/cancel?token='+fetchs.token+'&orderId='+orderId,
            'contentType': 'application/json',
            success:function(res){
                if(res.ifSuc==1){
                    //调用查询订单
                    searchOrder();
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
                notify('danger',data.msg);
            }
        });
        $('#cancelOrder').modal('hide');
    }
    // 取消订单
    $('#cancelOrder').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var id = button.data('id');
        var name = button.data('name');
        var modal = $(this);
        modal.find('span.room_name').text(name);
        modal.find('input').val(id);
    });
    $('#cancelOrderForm').on('submit', function (event) {
        event.preventDefault();
        var data = eval('(' + '{' + $(this).serialize().replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
        cancelOrder(data.id);
    });
})

 //点击了订单号进到详情页
 function didClickOrderNumber(number){//点击订单号,跳转到订单详情页面
   window.open('/order/info?token='+fetchs.token+'&orderId='+number);
   // console.log(number);
   // console.log("点击了订单号");
   //打开一个新页
 }
function didClickPayment(number,payChannel){//点击付款,跳转到付款页面
   window.open('/order/prePay?token='+fetchs.token+'&orderId='+number);
}