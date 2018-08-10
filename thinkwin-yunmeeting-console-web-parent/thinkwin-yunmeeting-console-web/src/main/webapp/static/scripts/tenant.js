'use strict';

$(function () {
  var personalStatus='';
  var target;
  var userInfo = JSON.parse(localStorage.getItem('console-userinfo'));
  if (userInfo == null) {
    userInfo = {
      userId: '123',
      token: '123'
    };
  }
    // 过滤器：字符串截取
  soda.filter('num', function (baseNum, buyNum) {
      baseNum = parseInt(baseNum);
      buyNum = parseInt(buyNum);
      if((baseNum==null||baseNum==undefined||isNaN(baseNum))&&(buyNum==null||buyNum==undefined||isNaN(buyNum))){
          return 0;
      }
      if(baseNum==null||baseNum==undefined||isNaN(baseNum)){
          return buyNum;
      }
      if(buyNum==null||buyNum==undefined||isNaN(buyNum)) {
          return baseNum;
      }
      return buyNum+baseNum;
  });
  var roomTpl = '\n <div class="scroll-list" ng-if="list.length>0"><p class="list-pie"></p><table class="table list-rooms">\n    <tbody>\n ' +
      ' <tr> <th class="table-1"><span>租户信息</span></th> <th class="table-2"><span>租户类型</span></th>\n     <th class="table-3"><span>联系电话</span></th>\n        <th class="table-4"><span>联系人</span></th>\n        <th class="table-5"><span>企业授权</span></th>\n        <th class="table-6"><span>授权时间</span></th>\n        <th class="table-7"><span>会议室数量</span></th>\n        <th class="table-8"><span>存储空间</span></th>\n     </tr>\n ' +
      '     <tr ng-repeat="item in list" >\n        <td class="table-1"><span style="display:block" data-toggle="tooltip" data-title="{{item.tenantName}}" ng-html="item.tenantName|keylight:key"></span></td>\n        <td class="table-2"> <div class="dropdown-box changeType"> <button class="select-btn dropdown-toggle"  type="button" value=""><span ng-if="item.isInnerTest==1" >内测租户</span> <span ng-if="item.isInnerTest==0">普通租户</span> </button> <div class="dropdown-ul"> <span class="span-list span-list-1" name="1" ng-if="item.isInnerTest==1" data-id="{{item.id}}">普通租户</span> <span class="span-list span-list-2" name="2" ng-if="item.isInnerTest==0" data-id="{{item.id}}" data-name="{{item.tenantName}}">内测租户</span> </div> </div> </div></td>\n        <td class="table-3"><span data-toggle="tooltip" data-title="{{item.contactsTel}}" ng-html="item.contactsTel|keylight:key"> </span></td>\n        <td class="table-4"><span data-toggle="tooltip" data-title="{{item.contacts}}" ng-html="item.contacts|keylight:key"></span></td>\n          </span>\n        </td>\n        <td class="table-5"><span data-toggle="tooltip" data-title="{{item.basePackageType}}" ng-html="item.basePackageType"></span></td>\n        <td class="table-6"><span data-toggle="tooltip" data-title="{{item.basePackageExpir}}"  ng-html="item.basePackageExpir"></span></td>\n        <td class="table-7"><span data-toggle="tooltip" data-title="{{item.buyRoomNumTotal}}间" >{{item.buyRoomNumTotal}}间</span></td>\n  <td class="table-8"><span data-toggle="tooltip" data-id=8 data-title="{{item.buySpaceNumTotal}}{{item.basePackageSpacUnit}}">{{item.buySpaceNumTotal}}{{item.basePackageSpacUnit}}</span></td>\n     </tr>\n    </tbody>\n  </table>\n </div> <div ng-if="list.length<1" class="nothing">暂无租户信息<!--没有搜索到信息，换个条件试试？<br/> 您可以以输入公司名称、联系电话、联系人等部分内容检索。!--></div>';


    //<th class="table-9">登录次数</th>\n   <th class="table-10">最后登录时间</th>\n
    // <td class="table-9"><span>20次</span></td>\n  <td class="table-10"><span>2018-5-16 10:31 </span></td>\n
  var key = '';
  var page = {
    total: 0,
    size: 15,
    page: 1
  };

  //分页
  var p = Pages();
  p.callBack(callBack);
  getData(0);
  function getData(tag) {
    fetchs.get('/tenant/selectSaasTenantListByPage?content=' + key + '&currentPage=' + page.page + '&pageSize=' + page.size + '&token=' + userInfo.token+'&time='+ new Date().getTime(), function (res) {
      var searchKey = key;
      if (res.data){
          res.data.key = searchKey;
          page.total = res.data.total;
          if (tag == 0) {
              p.setCount(page.total);
          }
          if (page.total > 15) {
              $('.list').addClass('haspage');
          } else {
              $('.list').removeClass('haspage');
          }
          $('.log-count').text('租户数 ：' + page.total);
          if(res.data.list){
              for(var i = 0; i<res.data.list.length; i++){
                var item = res.data.list[i];
                if(item.buyRoomNumTotal==null){
                    item.buyRoomNumTotal = 0;
                }
                if(item.buySpaceNumTotal==null){
                    item.buySpaceNumTotal = 0;
                }
                if(item.basePackageSpacUnit==null){
                    item.basePackageSpacUnit = 'GB';
                }
                if(item.basePackageExpir!=null){
                    if(item.basePackageStart==null){
                       item.basePackageStart = Date.parse(new Date());
                    }
                    item.basePackageExpir = moment(item.basePackageStart).format('YYYY-MM-DD') + '至' + moment(item.basePackageExpir).format('YYYY-MM-DD');
                    // item.basePackageExpir = year-years + '-' + month + '-' + day + '至' + year + '-' + month + '-' + day
                }else {
                    if(item.basePackageType.indexOf('免费版')>=0){
                        item.basePackageExpir = '永久';
                    }else {
                        item.basePackageExpir = '';
                    }

                }

              }
              console.log(soda(roomTpl, res.data));
              $('.list').html(soda(roomTpl, res.data));
          }else {
              $('.list').html(soda(roomTpl, {data:[]}));
          }
      }else {
          $('.list').html(soda(roomTpl, {data:[]}));
          p.setCount(0);
      }

    });
  }

  function callBack(pageCount, myPage) {
    //返回当前页和每页多少条数据
    page.page = pageCount;
    page.size = myPage;
    getData(1);
  }

  //监听搜索框键盘事件
  $('.version-search').on('keypress', function (e) {
    if (e.keyCode == 13) {
      page.page = 1;
      page.size = 15;
      key = $(this).val();
      getData(0);
      e.preventDefault(); //阻止默认事件
      $(this).blur();
      return false;
    };
  });
    //------取消搜索
    $(".del-searchList").click(function () {
        $('.version-search').val("");
        $(this).hide();
        page.page = 1;
        page.size = 15;
        key = '';
        getData(0);
    });
    //输入框聚焦事件
    $('.version-search').bind('focus', function () {
        if ($(this).val().length > 0) {
            $(".del-searchList").show();
        } else {
            $(".del-searchList").hide();
        }
        $('.input-search').addClass('border-shadow');
    });
    //输入框失焦事件
    $('.version-search').bind('blur', function () {
        $('.input-search').removeClass('border-shadow');
    });
    //输入框改变事件
    $('.version-search').bind('input propertychange', function () {
        if ($(this).val().length > 0) {
            $(".del-searchList").show();
        } else {
            $(".del-searchList").hide();
        }
    });

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

   $('.personnelType').click(function (event) {
        stopBubble(event);
        $("tbody .dropdown-ul").hide();
        $('.personnelType .dropdown-ul').toggle();
   });

   $('body').bind("click", function (e) {
       var target = $(e.target);
       if (target.closest(".dropdown-box").length == 0) {
            $(".dropdown-box .dropdown-ul").hide();
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

    //阻止冒泡函数
    function stopBubble(e) {
        if (e && e.stopPropagation) {
            e.stopPropagation(); //w3c
        } else window.event.cancelBubble = true; //IE
    };
    //top区域下点击不同状态人员的事件
    $('.top .dropdown-ul').on('click', 'span', function () {
        $(this).addClass('select-active').siblings().removeClass('select-active');
        var type_val = $(this).text();
        $('#personnelTypeMenu').text(type_val);
        var state_name = $(this).attr('name');//人员筛选的状态码
        personalStatus=state_name;
    });

    //tbody区域下人员状态切换事件
    $('body').on('click', 'tbody .dropdown-ul .span-list-2', function () {
        $("#setBetaModal .name").html($(this).data('name'));
        $("#setBetaModal").find('input').val($(this).data('id'));
        $("#setBetaModal").modal("show");
    });
    $('body').on('click', '.setBetaType', function () {
        var id=$("#setBetaModal").find('input').val();
        setType(id,1);
    });

    $('body').on('click', 'tbody .dropdown-ul .span-list-1', function () {
        var id = $(this).data('id');
        setType(id);
    })

    function  setType(id,type) {
        if(type==1){
           $("#setBetaModal").modal("hide");
        }
        fetchs.post("/tenant/updateIsInnerTest",{"tenantId":id},function (res) {
            if(res.ifSuc==1) {
                notify('success', res.msg);
                getData(0);
            }else{
                notify('danger',res.msg);
            }
        })
    }
});
