$(function () {
  var start = new Date();
  start.setHours(0,0,0,0);
   $('input.time').datetimepicker({
        start: start.format('YYYY-MM-DD HH:mm'),
        step: 15,
        container: '.invoice-info-box .inner-box',
        change: function(start, end){
        }
    }).on('hidden', function(evt, start, end){
        if ($(evt.target).attr('name') == 'startTime') {
            var end = new Date($('[name="endTime"]').val().replace(/-/g, '/')).getTime();
            $('input[name="endTime"]').datetimepicker('setOption',{
                start: start.format('YYYY-MM-DD HH:mm'),
                min: start.format('YYYY-MM-DD HH:mm')
            });
            if (start.getTime() >= end) {
                $('input[name="endTime"]').datetimepicker('setDate',new Date(start.getTime() + 30*24*60*60*1000).format('YYYY-MM-DD HH:mm'));
            }
        }
    })
  var searchKey = ""; //记录搜索的关键字
  //分页参数
  var page = {
    total: 0,
    size: 15,
    page: 1
  };
  var p = Pages();
  p.resetpage();
  p.callBack(callBack);
  function callBack(pageCount, myPage) {
    //返回当前页和每页多少条数据
    page.page = pageCount;
    page.size = myPage;
    getList(searchKey,1);
  }
  var dataList;
  var category = 0; //取消搜索状态
  var roomTpl = '\n  <table class="table"><tbody id="roomBody">' +
      '<tr ng-repeat="item in data.list" data-id="{{item.id}}" data-type="{{item.couponType}}">' +
      '<td class="table-1" data-toggle="tooltip" data-title="{{item.batchNum}}"><span ng-html="item.batchNum|keylight:data.searchkey"></span></td>' +
      '<td class="table-2" data-toggle="tooltip" data-title="{{item.couponName}}"><span ng-html="item.couponName|keylight:data.searchkey"></span></td>' +
      '<td class="table-3" data-toggle="tooltip" data-title=""><span ng-if="item.couponType==\'T\'">特权券</span></td>'+
      '<td class="table-4" data-toggle="tooltip" data-title="\u67E5\u770B\u4F18\u60E0"><span class="btn-span btn-view" data-id="{{item.id}}">\u67E5\u770B\u4F18\u60E0</span></td>' +
      '<td class="table-5" data-toggle="tooltip" data-title="\u9650\u7528{{item.limit}}\u5F20"><span ng-if="item.limit!=0">\u9650\u7528{{item.limit}}\u5F20</span><span ng-if="item.limit==0">\u65E0\u9650\u5236</span></td>' +
      '<td class="table-6" data-toggle="tooltip" data-title="{{item.effectiveTime|dateTime}}\u81F3{{item.expireTime | dateTime}}"><span>{{item.effectiveTime|dateTime}}</span>\u81F3<br/><span>{{item.expireTime | dateTime}}</span></td>' +
      '<td class="table-7" data-toggle="tooltip" data-title="{{item.modeName}}"><span ng-if="item.issueMode&&item.status!=0">指定发放</span><span ng-if="item.status==0">无</span><br/><span ng-if="item.status" class="creater-name">{{item.createName}}</span></td>' +
      '<td class="table-8" data-toggle="tooltip" data-title="{{item.issuedQty}}/{{item.totalQty}}"><span ng-if="item.status">{{item.issuedQty}}/{{item.totalQty}}</span><span ng-if="item.status==0">未发放</span></td>' +
      '<td class="table-9" data-toggle="tooltip" data-title="{{item.usedQty}}/{{item.totalQty}}"><span ng-if="item.status">{{item.usedQty}}/{{item.totalQty}}</span><span ng-if="item.status==0">无</span></td>' +
      '<td class="table-10" data-toggle="tooltip"><span class="btn-span btn-edit" ng-if="item.status==0">\u7F16\u8F91\u5E76\u53D1\u653E</span><span class="btn-span btn-delete" ng-if="item.status==0">\u5220\u9664</span><a class="btn-span btn-detail" ng-if="item.status!=0&&item.status!=2" data-toggle="tab" href="#details" role="tab" data-id="{{item.id}}">详情</a><span class="btn-span btn-copy" ng-if="item.status==1&&item.usedQty==0" data-id="{{item.couponCode}}">复制<textarea ng-if="item.status==1&&item.usedQty==0" class="copy-code">{{item.couponCode}}</textarea></span><span class="btn-span btn-invalidate" ng-if="item.status==1&&item.usedQty==0">使失效</span><span class="btn-span invalidate" ng-if="item.status==2">已失效</span></td></tr></tbody></table>' +
      '<div ng-if="data.list.length<1&&data.searchkey==\'\'" class="nothing">\u6682\u65F6\u6CA1\u6709\u4F18\u60E0\u5238\uFF0C\u60A8\u53EF\u4EE5\u521B\u5EFA\u4F18\u60E0\u5238</div>\n  ' +
      '<div ng-if="data.list.length<1&&data.searchkey!=\'\'" class="nothing">没有搜索到信息，换个条件试试？<br/>您可以输入编号、名称、优惠值等部分内容检索。</div>';
  getList(searchKey);
  function getList(searchKey, status) {
    var parameter = {
      'word': $.trim(searchKey),
      'currentPage':page.page,
      'pageSize':page.size
    };
    fetchs.post('/promotion/coupon/query',parameter,function (result) {
      if (result.ifSuc ==1) {
        if (searchKey) {
          result.data.searchkey = $.trim(searchKey);
        } else {
            result.data.searchkey = '';
        };
        if(status!=1){
          p.setCount(result.data.total);
        }
        dataList =result.data;
        $('body .table-body').html(soda(roomTpl, {'data':result.data}));
      }
    });
  }
  //创建
  $('body').on('click', '.create-btn', function (event) {
    event.stopPropagation();
    start = new Date();
    start.setHours(0,0,0,0);
    $('.invoice-info-box')[0].reset();
    $('form .save-btn').attr('disabled', false);
    $('form .publish-btn').attr('disabled', false);
    $('input[name="startTime"]').datetimepicker('setDate',new Date(start.getTime()).format('YYYY-MM-DD HH:mm'));
    $('input[name="endTime"]').datetimepicker('setOption',{
      start: start.format('YYYY-MM-DD HH:mm'),
      min: start.format('YYYY-MM-DD HH:mm')
    });
    $('input[name="endTime"]').datetimepicker('setDate',new Date(start.getTime() + 30*24*60*60*1000).format('YYYY-MM-DD HH:mm'));
    $('.invoice-info-box .title').attr('name','');
    $('body .invoice-info-box').addClass('show');
  });
  //修改
  var current = '';
  var formValue;
  //显示
  $('body').on('click', '.btn-edit', function (event) {
    event.stopPropagation();
    $('.invoice-info-box')[0].reset();
    $('form .save-btn').attr('disabled', false);
    $('form .publish-btn').attr('disabled', false);
    var current_id = $(this).parents('tr').data('id');
    $.each(dataList.list, function (item) {
      if ($(this)[0].id == current_id) {
        current = $(this)[0];
          if(typeof(current.discountConfig)!=='object'){
              current.discountConfig= $.parseJSON(current.discountConfig);
          };
        return current;
      };
    });
    formValue = [];
    for (var i = 0; i < current.discountConfig.giveaway.length; i++) {
        formValue.push(current.discountConfig.giveaway[i].qty);
    };
    formValue.push(current.discountConfig.extraServiceTerm);
    formValue.push(current.discountConfig.discount);
    $('.form-control[name="ticketName"]').val(current.couponName);
    $('.invoice-info-box .title').attr('name',current_id);
    $('.form-control[name="startTime"]').val(range(current.effectiveTime,1));
    $('.form-control[name="endTime"]').val(range(current.expireTime,1));
    $('input[name="startTime"]').datetimepicker('setDate',range(current.effectiveTime,1));
    $('input[name="endTime"]').datetimepicker('setDate',range(current.expireTime,1));
    var $input = $('.control-group input.form-control');
    $.each($input, function (index) {
      $(this).val(formValue[index]);
    });
    $('body .invoice-info-box').addClass('show');
  });
  //时间格式化
  function range(dateTime,num) {
    if(num){//1是修改 0是创建
        return moment(Number(dateTime)).format('YYYY-MM-DD') + ' ' + moment(Number(dateTime)).format('HH:mm');
    }else{
        return moment(Number(dateTime)).format('YYYY-MM-DD') + ' ' + '00:00';//moment(Number(dateTime)).format('HH:mm')
    }
  }
  //隐藏
  $('body').bind("click", function (e) {
    var target = $(e.target);
    if (target.closest(".invoice-info-box").length == 0) {
      $(".invoice-info-box .error-msg").html('');
      $(".invoice-info-box .form-group").removeClass('has-danger');
      $("body .invoice-info-box").removeClass('show');
      $('invoice-info-box input').removeClass('error-box');
    }
  });
  //输入
  $("body").on('keydown', "input[type='int']", function (e) {
    var k = typeof e.which == "number" ? e.which : e.keyCode;
    if ((k < 48 || k < 96 && k > 57 || k > 105) && k != 8) {
      return false;
    }
    if (e.shiftKey) {
      return false;
    }
    return true;
  });
  $("body").on('keyup', "input[type='int']", function (e) {
    var str = $(this).val();
    $(this).val(str.replace(/[^\d]/g, ''));
  });
  //失焦之后判断并赋值
  $("body").on('blur', "input[type='int']", function (e) {
    //取到input最大最小值
    var max = $(this).data('max');
    var min = $(this).data('min');
    //取到input值
    var value = $(this).val();
    //如果值为空边框标红
    if (value == '' || value.length == 0) {
      $(this).val($(this).data('value'));
      return;
    }
    if (Number(value)==0) {
        $(this).val(0);
    }
    //判断是否为数字格式
    if (parseInt(value) != NaN) {
      //如果大于最大值，值改为最大值
      if (parseInt(value) > parseInt(max)) {
        $(this).val(max);
      }
      //如果小于最小值，值改为最小值
      if (parseInt(value) < parseInt(min)) {
        $(this).val(min);
      }
    } else {
      $(this).val($(this).data('value'));
    }
    if ($(this).data('defalut') != $(this).val()) {
      $('.header .save-btn').attr('disabled', false);
    }
  });
  //保存
  $('body').on('click', '.save-btn', function () {
    var name = $('input[name="ticketName"]');
    var start = $('input[name="startTime"]');
    var ends = $('input[name="endTime"]');
    var discounts = $('.value-list input.form-control');
    var dis = 1; //全为空
    $.each(discounts, function () {
      if ($(this).val()) {
        //有值
        dis = 0;
        return false;
      } else {
        dis = 1;
      }
    });
    if (validate(name) && validate(start) && validate(ends)) {
      if(new Date(ends.val()).getTime()<new Date().getTime()){
          $("body .invoice-info-box .error-msg").html('<i class="icon icon-error"></i>结束时间不能小于当前时间');
        return;
      }else{
          $("body .invoice-info-box .error-msg").html('');
      };
      if (dis) {
        $(this).parents('form').find('p.error-msg').html('<i class="icon icon-error"></i>至少输入一项优惠值');
      } else {
        $('form .save-btn').attr('disabled', true);
        $(this).parents('form').find('p.error-msg').html();
        var person = $('input[name=person]').val();
        var room = $('input[name=room]').val();
        var space = $('input[name=space]').val();
        var duration = $('input[name=duration]').val();
        var discount = $('input[name=discount]').val();
        var argument = {};
        var currentId='';
        currentId = $('.invoice-info-box .title').attr('name');
        argument = {
          "id": currentId? currentId : '',
          "couponType": current.couponType ? current.couponType : 'T', //特权券为T，赠送券为G，时长券为S，折扣券为Z
          "discountConfig": {
            "giveaway": [
                {"sku":"102","name":"员工人数","qty":person,"uom":"个" },
                { "sku":"100","name":"会议室","qty":room,"uom": "间" },
                {"sku":"101","name":"存储空间","qty":space,"uom": "GB" }
            ],
            "extraServiceTerm": duration,
            "discount":discount
          },
          "effectiveTime": new Date(Date.parse(start.val().replace(/-/g,"/"))).getTime(),
          "expireTime": new Date(Date.parse(ends.val().replace(/-/g,"/"))).getTime(),
          "totalQty": current.totalQty ? current.totalQty : 1, //默认特权券1
          "couponName": name.val(),
          "isRelease": 0 //0保存，1发布
        };
        fetchs.post('/promotion/coupon/new',{'data':JSON.stringify(argument)}, function (result) {
          console.log(result);
          if (result.ifSuc == 1) {
            $('.invoice-info-box')[0].reset();
            $('.invoice-info-box').removeClass('show');
            notify('success', '保存成功');
            if(searchKey){
              $('#search').val('');
              searchKey =$('#search').val();
            };
            page.page = 1;
            page.size = 15;
            p.resetpage();
            getList(searchKey);// 刷新列表
          }else{
            $('form .save-btn').attr('disabled', false);
            notify('danger',result.msg);
          }
        });
      }
    };
  });
  $('body input[name="ticketName"]').bind('keydown',function(event){
      if (event.keyCode == 13) {
        event.preventDefault();
      }
  })
  //发布
  $('.invoice-info-box').on('validator', function (event, data) {
    if(new Date(data.endTime).getTime()<new Date().getTime()){
        $("body .invoice-info-box .error-msg").html('<i class="icon icon-error"></i>结束时间不能小于当前时间');
        return;
    }else{
        $("body .invoice-info-box .error-msg").html('');
    };
    if (data.discount == "" && data.duration == "" && data.person == "" && data.room == "" && data.space == "") {
      $(this).find('p.error-msg').html('<i class="icon icon-error"></i>至少输入一项优惠值');
    } else {
      $('form .publish-btn').attr('disabled', true);
      var argument = {};//特权券为T，赠送券为G，时长券为S，折扣券为Z
      var currentId='';
      currentId = $('.invoice-info-box .title').attr('name');
      argument = {
          "id":currentId? currentId : "",
          "couponType":current.couponType ? current.couponType : "T",
          "discountConfig":{
              "giveaway":[
                  {"sku":"102","name":"员工人数","qty":data.person,"uom":"个" },
                  { "sku":"100","name":"会议室","qty":data.room,"uom": "间" },
                  {"sku":"101","name":"存储空间","qty":data.space,"uom": "GB" }
              ],
              "extraServiceTerm":data.duration,
              "discount":data.discount
          },
          "effectiveTime":new Date(Date.parse(data.startTime.replace(/-/g,"/"))).getTime(),
          "expireTime": new Date(Date.parse(data.endTime.replace(/-/g,"/"))).getTime(),
          "totalQty": current.totalQty ? current.totalQty : 1, //默认特权券1
          "couponName": data.ticketName,
          "isRelease": 1 //0保存，1发布
      };
      fetchs.post('/promotion/coupon/new', {'data':JSON.stringify(argument)}, function (result) {
        if (result.ifSuc == 1) {
          $('.invoice-info-box')[0].reset();
          $('.invoice-info-box').removeClass('show');
          notify('success', '发布成功');
          if(searchKey){
              $('#search').val('');
              searchKey =$('#search').val();
          };
          page.page = 1;
          page.size = 15;
          p.resetpage();
          getList(searchKey); // 刷新列表
        }else{
          $('form .publish-btn').attr('disabled', false);
          notify('danger',result.msg);
          if(result.code =='-1'){
              getList(searchKey);
          }
        }
      });
    }
  });
  $('form[data-toggle="validator"]').on('submit', function (event) {
    event.preventDefault();
    var $this = $(this);
    var $inputs = $(this).find('input[data-validate]');
    var isvalidate = false;
    $.when($inputs.each(function (el) {
      isvalidate = validate($(this));
      return isvalidate;
    })).then(function (ev) {
      if (isvalidate) {
        var data = eval('(' + '{' + decodeURIComponent($(this).serialize(), true).replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
        $this.trigger('validator', data);
      }
    });
  });
  $('form[data-toggle="validator"] button[type="submit"]').on('button.bs.validator', function () {
    validate($(this));
  });
  //表单验证的提示信息
  function validate($el) {
    var val = $el.val();
    var type = $el.data('validate');
    var msg = rule(type, $.trim(val));
    if (msg != true) {
      $el.parent('.form-group').addClass('has-danger');
      $el.parents('form').find('p.error-msg').html('<i class="icon icon-error"></i>' + msg);
      return false;
    } else {
      $el.parent('.form-group').removeClass('has-danger');
      $el.parents('form').find('p.error-msg').text('');
      return true;
    }
    return msg;
  }
  //表单验证
  function rule(type, val) {
    switch (type) {
      case 'ticketName':
        return val.trim() == '' ? '名称不能为空' : !/^[A-Za-z0-9_\.\·\-\(\)\（\）\u4e00-\u9fa5]+$/.test(val) ? '名称不能含有特殊字符' : true;
        break;
      case 'startTime':
        return val.trim() == '' ? '时间不能为空' : !/\d{4}(\-|\/|.)\d{1,2}\1\d{1,2}/.test(val) ? '时间格式不正确' : true;
        break;
      case 'endTime':
        return val.trim() == '' ? '时间不能为空' : !/\d{4}(\-|\/|.)\d{1,2}\1\d{1,2}/.test(val) ? '时间格式不正确' : true;
        break;
      default:
        return true;
    }
  }
  //tab切换   (新增js)
  $('body a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
    $('body a[data-toggle="tab"]').removeClass('active');
  });
  $('body').on('shown.bs.tab', 'a[data-toggle="tab"]', function () {
    if ($(this).text() == '优惠券管理') {
      $('.tab-business').empty();
      $('.tab-business').append('<a class="nav-link" data-toggle="tab" href="#list" role="tab" aria-expanded="true">优惠券管理</a>');
    } else {
      $('.tab-business').empty();
      $('.tab-business').append('<a class="nav-link link-hover" data-toggle="tab" href="#list" role="tab" aria-expanded="true">优惠券管理</a><i class="icon icon-guide"></i><a class="nav-link" data-toggle="tab" href="' + $(this).attr('href') + '" role="tab">优惠券详情</a>');
    }
  });
  //搜索
  $('body').on('keypress', '#search', function (e) {
    if (e.keyCode == 13) {
      e.preventDefault(); //阻止默认事件
      page.page = 1;
      page.size = 15;
      searchKey = $('#search').val();
      p.resetpage();
      getList(searchKey);
      $(this).blur();
    };
  });
  $('#search').bind('input propertychange', function () {
    if ($(this).val().length > 0) {
      $(".icon-search-del").show();
    } else {
      searchKey = $('#search').val();
      getList(searchKey);
      $(".icon-search-del").hide();
    }
  });
  $('#search').bind('focus', function () {
    searchKey = $('#search').val();
    $('.input-search').addClass('border-shadow');
    if(searchKey==''){
      $(".icon-search-del").hide();
    };
  });
  $('#search').bind('blur', function () {
    $('.input-search').removeClass('border-shadow');
  });
  //取消搜索
  $(".icon-search-del").click(function () {
    $('#search').val("");
    $(this).hide();
    page.page = 1;
    page.size = 15;
    searchKey = $('#search').val();
    searchKey = '';
    p.resetpage();
    getList(searchKey);
  });
  //点击复制
  $('body').on('click', '.btn-copy', function (event) {
    var copyText = $(this).find('textarea').select();
    document.execCommand('Copy');
    notify('success', '成功复制到剪切板');
  });
  //查看优惠值
  var currentTr = '';
  var valuesTpl = '<div class="values-box"><p ng-repeat="item in data.giveaway" ng-if="item.qty">{{item.qty}}{{item.uom}}{{item.name}}</p><p ng-if="data.extraServiceTerm">{{data.extraServiceTerm}}年时长</p><p ng-if="data.discount>=0">{{data.discount}}折折扣</p></div>';
  $('body').on('mouseenter', '#list .btn-view', function () {
    var current_id = $(this).data('id');
    $.each(dataList.list, function (item) {
      if ($(this)[0].id == current_id) {
        if(typeof($(this)[0].discountConfig)!=='object'){
            $(this)[0].discountConfig=$.parseJSON($(this)[0].discountConfig)
        };
        currentTr = $(this)[0].discountConfig;
        if(currentTr.discount==null){
            currentTr.discount = -1;
        };
        return currentTr;
      };
    });
    console.log(currentTr);
    $(this).append(soda(valuesTpl, { 'data': currentTr }));
    var valBoxHeight =$(this).find('.values-box').outerHeight();
    var windowHeight =$(document.body).height() - $(this).offset().top;
    if(valBoxHeight>windowHeight){
        $(this).find('.values-box').css('top',-valBoxHeight+'px');
    }
  });
  $('body').on('mouseleave', '.btn-view', function () {
    $('.values-box').remove();
  });
  //使优惠券失效
  var invalidateId = [];
  $('body').on('click', '.btn-invalidate', function () {
    $("#disabledModal").modal("show");
    invalidateId.length = 0;
    invalidateId.push($(this).parents('tr').data("id"));
  });
  $('body').on('click', '.btn-affirm-invalidate', function () {
    fetchs.post('/promotion/coupon/invalidate', { 'id': invalidateId }, function (res) {
      $("#disabledModal").modal("hide");
      if (res.ifSuc == 1) {
        //使失效成功，刷新列表
        notify('success','使失效成功');
        getList(searchKey);
      }else{
        notify('danger',res.msg);
        if(res.code =='-1'){
            getList(searchKey);
        }
      }
    });
  });
  //删除优惠券
  var deleteId = [];
  $('body').on('click', '.btn-delete', function () {
    $("#delModal").modal("show");
    deleteId.length = 0;
    deleteId.push($(this).parents('tr').data("id"));
  });
  $('body').on('click', '.btn-affirm-delete', function () {
    fetchs.post('/promotion/coupon/delete', { 'id': deleteId }, function (res) {
        $("#delModal").modal("hide");
        if (res.ifSuc == 1) {
        //删除成功，刷新列表
        notify('success', '删除成功');
        getList(searchKey);
      }else{
        notify('danger',res.msg);
        if(res.code =='-1'){//多人操作的并发
           getList(searchKey);
        }
      }
    });
  });
  //详情
  var detailTpl = '' +
      ' <div class="batch-details">' +
          '<p class="title">\u4F18\u60E0\u5238\u4FE1\u606F</p>\n      ' +
          '<div class="batch">\n' +
            '<p>{{data.couponName}}</p>' +
            '<div class="item-box"><label>\u7C7B\u578B</label><span class="span-right" ng-if="data.couponType==\'T\'">特权券</span></div>' +
            '<div class="item-box"><label>\u53D1\u653E\u72B6\u6001</label><span class="span-right">{{data.issuedQty}}/{{data.totalQty}}</span></div>' +
            '<div class="item-box"><label>\u53D1\u653E\u65B9\u5F0F</label><span class="span-right" ng-if="data.issueMode==1">指定发放</span><span class="span-right" ng-if="!data.issueMode">无</span></div>' +
            '<div class="item-box"><label>\u4F7F\u7528\u9650\u5236</label><span ng-if="data.limit">\u9650\u7528{{data.limit}}<span class="span-right">\u5F20</span></span></div>' +
            '<br/><div class="item-box"><label>\u4F18\u60E0\u503C</label><span class="span-right btn-span btn-view">\u67E5\u770B\u4F18\u60E0</span></div>' +
            '<div class="item-box"><label>\u4F7F\u7528\u72B6\u6001</label><span class="span-right">{{data.usedQty}}/{{data.totalQty}}</span></div>' +
            '<div class="item-box"><label>\u6709\u6548\u65F6\u95F4</label><span>{{data.effectiveTime|dateTime}}</span>\u81F3<span class="span-right">{{data.expireTime| dateTime}}</span></div>' +
          '</div>'+
        '</div>'+
        '<div class="batch-list">' +
          '<p class="title">\u5355\u4E00\u4F18\u60E0\u5238</p>' +
          '<table class="table table-head">' +
          '<thead>\n' +
            '<tr> \n ' +
              '<th class="table-1" data-toggle="tooltip" data-title="\u4F18\u60E0\u5238\u7801"><span>\u4F18\u60E0\u5238\u7801</span></th><th class="table-2" data-toggle="tooltip" data-title="\u9886\u53D6\u65B9"><span>\u9886\u53D6\u65B9</span></th>' +
              '<th class="table-3"data-toggle="tooltip" data-title="\u4F7F\u7528\u60C5\u51B5"><span>\u4F7F\u7528\u60C5\u51B5</span></th><th class="table-4" data-toggle="tooltip" data-title="\u4F7F\u7528\u79DF\u6237"><span>\u4F7F\u7528\u79DF\u6237</span></th>' +
              '<th class="table-5" data-toggle="tooltip" data-title="\u4F7F\u7528\u65F6\u95F4"><span>\u4F7F\u7528\u65F6\u95F4</span></th><th class="table-6" data-toggle="tooltip" data-title="\u64CD\u4F5C"><span>\u64CD\u4F5C</span></th>' +
            '</tr>' +
          '</thead>' +
          '<tbody>' +
            '<tr ng-repeat="item in data.items">' +
              '<td class="table-1" data-toggle="tooltip" data-title="{{item.couponCode}}"><span ng-if="item.couponCode">{{item.couponCode}}</span><span ng-if="!item.couponCode">--</span></td>' +
              '<td class="table-2" data-toggle="tooltip" data-title="{{item.obtainedBy}}"><span ng-if="item.obtainedBy">{{item.obtainedBy}}</span><span ng-if="!item.obtainedBy">--</span></td>' +
              '<td class="table-3" data-toggle="tooltip" data-title="{{item.statusName}}"><span ng-if="item.status==1">已使用</span><span ng-if="item.status!=1">未使用</span></td>' +
              '<td class="table-4" data-toggle="tooltip" data-title="{{item.usedBy}}"><span ng-if="item.usedBy">{{item.usedBy}}</span><span ng-if="!item.usedBy">--</span></td>' +
              '<td class="table-5" data-toggle="tooltip" data-title="{{item.usedAt | dateTime}}"><span ng-if="item.usedAt">{{item.usedAt | dateTime}}</span><span ng-if="!item.usedAt">--</span></td>' +
              '<td class="table-6" data-toggle="tooltip"><span class="btn-span btn-copy" ng-if="item.status==0" data-id="{{item.couponCode}}">\u590D\u5236<textarea class="copy-code">{{item.couponCode}}</textarea></span><span class="btn-span invalidate" ng-if="item.status!=0">--</span></td>' +
            '</tr>' +
          '</tbody>' +
       '</table>' +
      '</div>';
  //优惠券详情
  $("body").on("shown.bs.tab", 'a[data-toggle="tab"]', function () {
    if ($(this).text() == "详情") {
      $('a[data-toggle="tab"]').removeClass('active');
      var detailsArgument = {
        'id': $(this).parents('tr').data('id'),
        'pageNum': page.page,
        'pageSize': page.size
      };
      fetchs.post('/promotion/coupon/get',detailsArgument, function (result) {
        if (result.ifSuc==1) {
          $('#details').html(soda(detailTpl, { 'data': result.data }));
          //查看优惠值
          var valuesTpl= '<div class="values-box"><p ng-repeat="item in data.giveaway" ng-if="item.qty">{{item.qty}}{{item.uom}}{{item.name}}</p><p ng-if="data.extraServiceTerm">{{data.extraServiceTerm}}年时长</p><p ng-if="data.discount>=0">{{data.discount}}折折扣</p></div>';
          var values = '';
          if($.parseJSON(result.data.discountConfig)){
              values=$.parseJSON(result.data.discountConfig);
              if(values.discount==null){
                  values.discount=-1;
              }
          }
          $('body').on('mouseenter','.batch .btn-view',function(){
            $('.values-box').remove();
            $(this).append(soda(valuesTpl,{'data':values}));
          })
          $('body').on('mouseleave','.btn-view',function(){
            $('.values-box').remove();
          });
        }else{
            notify('danger',result.msg);
        }
      });
    }
  });
  //表格的移除省略号提示语效果
  $('body').on('mouseenter', '[data-toggle="tooltip"]', function (e) {
    var text = $(this).data('title');
    var id = $(this).data('id');
    var size = parseInt($(this).css('fontSize'));
    if ($(this).width() < size * text.length) {
      $(this).tooltip({
        placement: function placement(tip, element) {
          if (id == '6' || id == '5') {
            return 'left';
          }
          return window.scrollY + 50 < $(element).offset().top ? 'top' : 'bottom';
        },
        trigger: 'hover'
      });
      $(this).tooltip('toggle');
    }
  });
});
