'use strict';

$(function () {
  soda.filter('source', function (source) {
    if (source === 'PC') {
      return "../static/images/PC.png";
    } else if (source === 'Android') {
      return "../static/images/android.png";
    } else if (source === 'iPhone') {
      return "../static/images/iphone.png";
    }
  });
  var userInfo = JSON.parse(localStorage.getItem('userinfo'));
  if (userInfo == null) {
    userInfo = {
      userId: '123',
      token: '123'
    };
  };
  var areaTpl = '\n    <div class="treeBox">\n       <ul id="treeDemo" class="ztree">\n           \n       </ul>\n    </div>\n  ';
  var roomTpl = '\n  <table class="table table-hover list-rooms">\n    <tbody>\n      <tr ng-repeat="item in list">\n        <td class="table-1"><span data-toggle="tooltip" data-title="{{item.businessname}}">{{item.businessname}}</span></td>\n        <td class="table-2"><span data-toggle="tooltip" data-title="{{item.eventname}}">{{item.eventname}}</span></td>\n        <td class="table-3"><span data-toggle="tooltip" data-title="{{item.content}}" ng-html="item.content|keylight:key"></span></td>\n        <td class="table-4">\n          <span>\n            <button ng-if="item.result | item.result.length!=0" class="btn btn-clear-primary" data-toggle="modal" data-target="#errorModal" data-id="{{item.id}}">\u67E5\u770B</button>\n          </span>\n        </td>\n        <td class="table-5"><span data-toggle="tooltip" data-title="{{item.ip}}"><i ng-class="item.source== \'PC\' ? \'icon icon-room-equipment\' : \'icon icon-phone\'"></i><span ng-html="item.ip|keylight:key"></span></span></td>\n        <td class="table-6"><span data-toggle="tooltip" data-title="{{item.operator}}" ng-html="item.operator|keylight:key"></span></td>\n        <td class="table-7"><span data-toggle="tooltip" data-id=7 data-title="{{item.operatedate}}" ng-html="item.operatedate|keylight:key"></span></td>\n      </tr>\n    </tbody>\n  </table>\n  <div ng-if="list.length<1" class="nothing">\u6682\u65F6\u6CA1\u6709\u65E5\u5FD7</div>';
  var logs = [];
  var types = [];
  var key = '';
  var page = {
    businesstype: '',
    eventtype: '', //事件类型
    total: 0,
    size: 15,
    page: 1
  };

  //分页
  var p = Pages();
  p.callBack(callBack);

  var zTreeObj = '';

  //获取数据
  getData(0);
  getNodes();

  function getData(tag) {
    console.log(userInfo);
    key = $('#search').val();
    console.log(key);
    fetchs.get('/log/selectSysLogListByPage?userId=' + userInfo.userId + '&businesstype=' + page.businesstype + '&eventtype=' + page.eventtype + '&content=' + key + '&currentPage=' + page.page + '&pageSize=' + page.size + '&token=' + userInfo.token, function (res) {
      var searchKey = $('#search').val();
      if (res.data){
          res.data.key = searchKey;
          page = {
              businesstype: page.businesstype,
              eventtype: page.eventtype,
              total: res.data.total,
              size: res.data.pageSize,
              page: res.data.pageNum
          };
          // console.log('个数'+page.total);
          if (tag == 0) {
              p.setCount(page.total);
          }

          if (page.total > 15) {
              $('.list').addClass('haspage');
          } else {
              $('.list').removeClass('haspage');
          }
          $('.list').html(soda(roomTpl, res.data));

          //获取滚动条宽度
          var width = $('.list')[0].offsetWidth - $('.list')[0].scrollWidth;
          $('.table-title').css("margin-right", width + 'px');

          logs = res.data.list;
          console.log(res);
          $('.log-count').text('共' + page.total + '个日志');
          if (res.data.list.length == 0) {
              $('.clear-btn').attr('disabled', true);
          } else {
              $('.clear-btn').attr('disabled', false);
          }
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
  $('#search').on('keypress', function (e) {
    if (e.keyCode == 13) {
      $('.input-search').removeClass('input-heighLight')
      $(this).blur();
      page.page = 1;
      page.size = 15;
      p.resetpage();
      key = $(this).val();
        if(key.length>0){
            //显示删除按钮
            $('#del-searchList').show();
        }else {
            $('#del-searchList').hide();
        }
      getData(0);
      return false;
    };
  });
    $('#search').bind('focus','input',function () {
        $('.input-search').addClass('input-heighLight')
        $('#del-searchList').hide();
    })
    $('#search').bind('blur','input',function () {
        $('.input-search').removeClass('input-heighLight')
    })
    $('#del-searchList').on('click',function () {
        $('#del-searchList').hide();
        $('#search').val('');
        key = '';
        getData(0);
    })
  //点击查看按钮事件
  $('#errorModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget);
    var id = button.data('id');
    var modal = $(this);
    fetchs.get('/log/selectSysLogById?userId=' + userInfo.userId + '&id=' + id + '&token=' + userInfo.token, function (res) {
      console.log(res.data);
      var name = res.data.result;
      console.log(name);
      console.log(modal);
      modal.find('span.name').text(name);
    });
  });
  //初始化,并配置ztree
  function getNodes() {
    fetchs.get('/log/selectSysLogTypeList?userId=' + userInfo.userId + '&token=' + userInfo.token, function (res) {
      types = res.data;
      if (res.data) {
        var array = new Array();
        array[0] = { id: 0, pId: 0, name: '全部日志', open: 'true', type: '' };
        for (var i = 0; i < res.data.length; i++) {
          var data = res.data[i];
          array[i + 1] = { id: data.id, pId: data.parentId, name: data.name, open: 'false', type: data.type };
        };
        zNodes(array);
        //设置默认第一个选中
        var aObj = $('#treeDemo_1' + ' >div#heightLight');
        aObj.addClass("on");
        lastSelectedNode = aObj;
      };
    });
  }
  function zNodes(zNodes) {
    var setting = {
      edit: {
        enable: true,
        showRemoveBtn: false,
        showRenameBtn: false,

        drag: {
          prev: false,
          next: false,
          inner: false,
          isCopy: false,
          isMove: false
        }
      },
      data: {
        simpleData: {
          enable: true
        }
      },
      callback: {
        onClick: onClick
      },
      view: {
        dblClickExpand: false,
        open: false,
        showLine: false,
        showTitle: false,
        addDiyDom: addDiyDom
      }
    };
    zTreeObj = $.fn.zTree.init($('#treeDemo'), setting, zNodes);
  }

  //树结构点击事件
  //上次选择的node
  var lastSelectedNode = '';
  function addDiyDom(treeId, treeNode) {
    var wrapObj = $('#' + treeNode.tId + ' > span');
    var div = '<div id="heightLight" class="heightLight" ></div>';
    wrapObj.before(div);
  }

  function onClick(e, treeId, treeNode) {
    var zTree = $.fn.zTree.getZTreeObj('treeDemo');
    if (lastSelectedNode.length > 0) {
      lastSelectedNode.removeClass("on");
    }
    var aObj = $('#' + treeNode.tId + ' >div#heightLight');
    aObj.addClass("on");
    lastSelectedNode = aObj;

    zTree.expandNode(treeNode);
    console.log(treeNode);
    page.businesstype = treeNode.type;
    if (treeNode.pId == 0) {
      //一级节点
      page.eventtype = '';
    } else {
      page.eventtype = treeNode.type;
    }
    page.page = 1;
    page.size = 15;
    p.resetpage();
    getData(0);
  }

  $('.btn-clear').on('click', function (btn) {
    $('#deleteModal').modal('hide');
    clearData();
  });

  function clearData() {
    fetchs.get('/log/deleteSysLogList?userId=' + userInfo.userId + '&businesstype=' + page.businesstype + '&eventtype=' + page.eventtype + '&token=' + userInfo.token, function (res) {
      if (res.ifSuc == 1) {
        //成功
        $('.list').removeClass('haspage');
        $('.page-Box').hide();
        $('.list').html(soda(roomTpl, ''));
        notify('success', res.msg);
        $('.clear-btn').attr('disabled', true);
        $('.log-count').text('共' + 0 + '个日志');
      } else {
        //失败
        notify('danger', res.msg);
      }
    });
  }

  $('body').on('mouseenter', '[data-toggle="tooltip"]', function (e) {
    var text = $(this).data('title');
    var id = $(this).data('id');
    var size = parseInt($(this).css('fontSize'));
    if ($(this).parent().width() < size * text.length) {
      $(this).tooltip({
        placement: function placement(tip, element) {
          // console.log(window.scrollX)
          // console.log($(element).offset().right)
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
});
//# sourceMappingURL=operationLog.js.map
