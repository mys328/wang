//终端错误日志
$(function() {
  var self;//查看按钮 .btn-view节点
  var that;//查看全部错误日志节点
  var pageObj={
    searchKey:"",
    data:'',
    currentPage:1,
    pageSize:15,
  }
  //SPA-tab切换
  $('body').on('shown.bs.tab','#meetingShowNav a.nav-link',function(e){
    if(self) self.details("hide");//注销租户日志页
    if(that) that.details("hide");//注销全部错误日志页
    if($(this).attr('href')=='#terminalCustomer'){
        $('#terminalCustomer #search').val("");
        pageObj.searchKey="";
        $("#terminalCustomer .icon-search-del").hide();
        getData();
        $("#terminalCustomer .bottom#pages").pages({
            change:function(current,size){
                pageObj.currentPage = current;
                pageObj.pageSize = size;
                getData();
            },
            count: 0
        });

    }
  })
  //表格模板
  var userLogTpl ='<div class="tabel-box" ng-if="list.length>0&&total"><div class="scroll-list"><p class="list-pie"></p> <table class="table"> <tbody> <tr> <th class="table-1" data-toggle="tooltip" data-title="租户信息"><span>租户信息</span></th><th class="table-2" data-toggle="tooltip" data-title="节目租户类型"><span>节目租户类型</span></th> <th class="table-3" data-toggle="tooltip" data-title="联系电话"><span>联系电话</span></th> <th class="table-4" data-toggle="tooltip" data-title="联系人"><span>联系人</span></th> <th class="table-5" data-toggle="tooltip" data-title="企业授权"><span>企业授权</span></th> <th class="table-6" data-toggle="tooltip" data-title="授权时间"><span>授权时间</span></th> <th class="table-7" data-toggle="tooltip" data-title="终端数量"><span>终端数量</span></th> <th class="table-8" data-toggle="tooltip" data-title="会议室数量"><span>会议室数量</span></th> <th class="table-9" data-toggle="tooltip" data-title="近一月日志"><span>近一月日志</span></th> </tr> <tr ng-repeat="item in list"> <td class="table-1" data-toggle="tooltip" data-title="{{item.tenantName}}"><span ng-html="item.tenantName|keylight:searchKey"></span></td> <td class="table-2"> <div class="dropdown-box changeType"> <button class="select-btn dropdown-toggle"  type="button" value=""><span ng-if="item.isCustomizedTenant==1" >定制节目租户</span> <span ng-if="item.isCustomizedTenant==0">非定制节目租户</span> </button> <div class="dropdown-ul"> <span class="dropdown-item span-list span-list-1" name="1" ng-if="item.isCustomizedTenant==1" data-id="{{item.id}}">设定非定制节目租户</span> <span class="dropdown-item span-list span-list-2" name="2" ng-if="item.isCustomizedTenant==0" data-id="{{item.id}}">设定定制节目租户</span> </div> </div> </div></td> <td class="table-3" data-toggle="tooltip" data-title="{{item.contactsTel}}"><span ng-html="item.contactsTel|keylight:searchKey"></span></td> <td class="table-4" data-toggle="tooltip" data-title="{{item.contacts}}"><span ng-html="item.contacts|keylight:searchKey"></span></td> <td class="table-5" data-toggle="tooltip" data-title="{{item.basePackageType}}"><span>{{item.basePackageType}}</span></td> <td class="table-6" data-toggle="tooltip" data-title="{{item.basePackageStart|logTime}}至{{item.basePackageExpir|logTime}}"><span>{{item.basePackageStart|logTime}}至{{item.basePackageExpir|logTime}}</span></td> <td class="table-7" data-toggle="tooltip" data-title="{{item.terminalCount}}"><span class="btn-span terminal-number" data-id="{{item.id}}" data-number="{{item.terminalCount}}">{{item.terminalCount}}台</span></td> <td class="table-8" data-toggle="tooltip" data-title="{{item.buyRoomNumTotal}}"><span>{{item.buyRoomNumTotal}}间</span></td> <td class="table-9" data-toggle="tooltip" data-title="查看日志"><span class="btn-span btn-view" data-id="{{item.id}}">查看日志</span></td> </tr> </tbody> </table> </div> </div><div class="nothing" ng-if="list.length==0&&!total&&!searchKey">暂无错误日志</div><div class="nothing" ng-if="list.length==0&&!total&&searchKey">没有搜索到信息，换个条件试试？</br>您可以输入租户名称、联系电话、联系人等部分内容检索。</div>';
  //获取数据
  function getData(status){
    fetchs.post('/tenant/selectSaasTenantListByPage',{"content":pageObj.searchKey,"currentPage":pageObj.currentPage,"pageSize":pageObj.pageSize,"option":"1","platformProgrameLabeId":pageObj.aid},function(result){
      if(result.ifSuc==1){
        result.data.searchKey=pageObj.searchKey;
        $(".customer-num").empty().html(result.data.total);
        $("#terminalCustomer .tabel-out-box").html(soda(userLogTpl,result.data));
        if(result.data.total>15){
            $("#terminalCustomer .bottom#pages").show();
            $("body #terminalCustomer .bottom#pages").pages('upcount', result.data.total);
        }else{
            $("#terminalCustomer .bottom#pages").hide();
        }
      }else{
        notify('danger',result.msg)
      }
    });
  }
  //搜索
  $('#terminalCustomer #search').keypress(function (e) {
    if (e.keyCode==13) {
      e.preventDefault();
      pageObj.currentPage = 1;
      pageObj.pageSize = 15;
      pageObj.searchKey=$('#terminalCustomer #search').val().trim();
      getData();
      $(this).blur();
    };
  });
  $('#terminalCustomer .input-search input').bind('input propertychange', function() {
    if($(this).val().length>0){
      $(".icon-search-del").show();
    }else{
      $(".icon-search-del").hide();
    }
  });
  $('#terminalCustomer .input-search input').bind('focus',function () {
    $('.input-search').addClass('border-shadow');
  })
  $('#terminalCustomer .input-search input').bind('blur',function () {
    $('.input-search').removeClass('border-shadow');
  })
  $("#terminalCustomer .icon-search-del").click(function () {
    $('#terminalCustomer .input-search input').val("");
    $(this).hide();
    pageObj.searchKey=$('#terminalCustomer #search').val().trim();
    getData();
  });
  //查看租户日志 组件调用
  $("body").on("click",".btn-view",function(){
    self = $(this);
    self.details({
      class:"user-log",
      type:"1",
      userId:self.data("id"),
      container:"body",
      template:"",
    });
    self.details("show");
  })
  //查看全部错误日志  组件调用
  $("body").on("click",".veiw-error-log",function(){
    that = $(this);
    that.details({
      class:"all-logs",
      type:"0",
      container:"body",
      template:"",
    });
    that.details("show");
  })
  //终端设备
    $('body').on('click','.terminal-number',function (event) {
      var self = $(this);
      var height = event.clientY;
      var place = 'top';
      if(height<=450){
          place = 'bottom';
      }
      $('.terminal-number').popover('dispose');
      if($(this).data("number")&&$(this).data("id")){
          fetchs.post('/tenant/getTerminalList',{"tenantId":$(this).data("id")},function (result) {
              if(result.ifSuc==1){
                  if(result.data.length<1){
                      $('.popover-meeting').popover('hide');
                  }else{
                      var terminalTpl = '<div class="popover-table-title"><table class="table table-hover"><thead><tr><th class="table-1">终端标识</th><th class="table-2">终端类型</th><th class="table-3">注册时间</th></tr></thead></table></div><div class="unmeeting-table"><table class="table table-hover"><tbody><tr ng-repeat="item in data"><td class="table-1"><span data-toggle="tooltip" data-title="{{item.terminalId|text}}">{{item.terminalId|text}}</span></td><td class="table-2"><span data-toggle="tooltip" data-title="{{item.terminalType|text}}">{{item.terminalType|text}}</span></td><td class="table-3"><span data-toggle="tooltip" data-title="{{item.registrationTime|date:\'YYYY-MM-DD HH:mm\'}}">{{item.registrationTime|date:\'YYYY-MM-DD HH:mm\'}}</span></td></tr></tbody></table></div>';
                      self.popover({
                          placement:place,
                          container:'body',
                          trigger:'click',
                          html:true,
                          template: '<div class="popover popover-meeting" role="tooltip"><div class="popover-content"></div></div>',
                          content:soda(terminalTpl,result)
                      })
                      self.popover('show');
                      //获取滚动条宽度
                      var width = $('.unmeeting-table')[0].offsetWidth - $('.unmeeting-table')[0].scrollWidth;
                      $('.popover-table-title').css("margin-right", width + 'px');
                      //计算table高度是否超过最大高度
                      var height = $('.popover-meeting .unmeeting-table table').height();
                      var maxHeight = $('.unmeeting-table').height();
                      if(height>maxHeight){
                          $('.popover-table-title').css("width", "calc(100% - 8px)");
                      }
                  }
              }
          })
      }
    })
    $('body').on('click',function (event) {
        $('.popover-meeting').popover('hide');
        $('.popover-button').popover('dispose');
    })
    $('body').on('click','.popover-meeting',function (event) {
        event.stopPropagation();
    })
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


    $('body').on('click', '.changeType', function (event) {
        stopBubble(event);
        var target=$(this).find(".dropdown-ul")
        if(target.is(":hidden")){
            $('.dropdown-ul').hide();
            target.show();
        }else {
            $('.dropdown-ul').hide();
        }
    });
    $('body').bind("click", function (e) {
        var target = $(e.target);
        if (target.closest(".dropdown-box").length == 0) {
            $(".dropdown-box .dropdown-ul").hide();
        }
    });
    $('.personnelType').click(function (event) {
        stopBubble(event);
        $("tbody .dropdown-ul").hide();
        $('.personnelType .dropdown-ul').toggle();
    });
    function  setType(id) {
        fetchs.post("/tenant/updateIsCustomizedTenant",{"tenantId":id},function (res) {
            if(res.ifSuc==1) {
                notify('success', res.msg);
                getData();
            }else{
                notify('danger',res.msg);
            }
        })
    }
    //阻止冒泡函数
    function stopBubble(e) {
        if (e && e.stopPropagation) {
            e.stopPropagation(); //w3c
        } else window.event.cancelBubble = true; //IE
    };
    $('body').on('mouseenter', '[data-toggle="tooltip"]', function (e) {
        var text = $(this).data('title');
        var id = $(this).data('id');
        var size = parseInt($(this).css('fontSize'));
        if ($(this).parent().width() < size * text.length) {
            $(this).tooltip({
                placement: function placement(tip, element) {
                    if (id == 7) {
                        return 'left';
                    }
                    // return 'left';
                    return window.scrollY + 50 < $(element).offset().top ? 'top' : 'bottom';
                },
                trigger: 'hover'
            });
            $(this).tooltip('toggle');
        }
    });
    /*//top区域下点击不同状态人员的事件
    $('.top .dropdown-ul').on('click', 'span', function () {
        $(this).addClass('select-active').siblings().removeClass('select-active');
        var type_val = $(this).text();
        $('#personnelTypeMenu').text(type_val);
        var state_name = $(this).attr('name');//人员筛选的状态码
        personalStatus=state_name;
    });*/
    //tbody区域下人员状态切换事件
    $('body').on('click', 'tbody .dropdown-ul .span-list', function () {
        var id = $(this).data('id');
        setType(id);
    });

})