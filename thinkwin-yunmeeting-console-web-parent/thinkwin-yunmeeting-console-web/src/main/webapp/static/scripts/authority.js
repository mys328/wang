"use strict";

$(function () {

  soda.prefix('ng-');
  var page = {
    total: 0,
    size: 15,
    page: 1
  };
    var p = Pages();
    p.callBack(callBack);
    var currentId = ''; //当前操作的ID
    var searchKey = ''; //搜索关键字
    var operaType = 0; //操作类型 0-创建 1-修改
    var role = {
        createRole: 0, //创建权限
        editRole: 0, //修改权限
        delRole: 0 //删除权限
    }
    var roomTpl = '<div class="scroll-list" ng-if="data.length>0">  <p class="list-pie"></p> <table class="table">  <tbody ><tr> <th class="table-1"><span>权限标识</span></th> <th class="table-2"><span>权限名称</span></th> <th class="table-3"><span>权限url</span></th> <th class="table-4"><span>完成人员</span></th> <th class="table-5"><span>完成时间</span></th> <th class="table-6"><span>修改人员</span></th> <th class="table-7"><span>修改时间</span></th> <th class="table-8"><span>操作</span></th> </tr>  <tr ng-repeat="item in data"> <td class="table-1"><span data-toggle="tooltip" data-title="{{item.orgCode|text}}">{{item.orgCode|text}}</span></td> <td class="table-2"><span data-toggle="tooltip" data-title="{{item.orgName|text}}" ng-html="item.orgName|keylight:key"></span></td> <td class="table-3"><span data-toggle="tooltip" data-title="{{item.url|text}}" ng-html="item.url|keylight:key"></span></td> <td class="table-4"><span data-toggle="tooltip" data-title="{{item.createrId|text}}">{{item.createrId|text}}</span></td> <td class="table-5"><span data-toggle="tooltip" data-title="{{item.createTime|date:\'YYYY-MM-DD HH:mm\'}}">{{item.createTime|date:"YYYY-MM-DD HH:mm"}}</span></td> <td class="table-6"><span data-toggle="tooltip" data-title="{{item.modifyerId|text}}">{{item.modifyerId|text}}</span></td> <td class="table-7"><span data-toggle="tooltip" data-title="{{item.modifyTime|date:\'YYYY-MM-DD HH:mm\'}}">{{item.modifyTime|date:"YYYY-MM-DD HH:mm"}}</span></td> <td class="table-8"> <span ng-if="role.editRole == 1" class="btn-span" id="changeAuthority" data-id="{{item.permissionId}}" data-code="{{item.orgCode|text}}" data-name="{{item.orgName|text}}" data-url="{{item.url|text}}">修改</span> <span ng-if="role.delRole == 1" class="btn-span" data-toggle="modal" data-target="#delModal" data-id="{{item.permissionId}}">删除</span> </td> </tr> </tbody> </table> </div><div class="nothing" ng-if="data.length==0">暂无相关数据</div>';
    $('body').on('mouseenter', '[data-toggle="tooltip"]', function (e) {
        var text = $(this).data('title');
        var size = parseInt($(this).css('fontSize'));
        if ($(this).parent().width() < size * text.length) {
            $(this).tooltip({
                placement: function placement(tip, element) {

                    return window.scrollY + 50 < $(element).offset().top ? 'top' : 'bottom';
                },
                trigger: 'hover'
            });
            $(this).tooltip('toggle');
        }
    });
    //查询权限信息
    $('.create-btn').hide();
    fetchs.post('/saasRolePermission/findSaasPermissionsByParentId',{parentId:1005},function (res) {
        console.log(res);
        for (var i = 0; i<res.data.length; i++){
            var orgCode = res.data[i].orgCode;
            if(orgCode==1005001){
                role.createRole = 1; //创建权限
                $('.create-btn').show();
            }
            if(orgCode==1005002) {
                role.editRole = 1; //修改权限
            }
            if(orgCode==1005003) {
                role.delRole = 1; //删除权限
            }
        }
        getAutho(0);
    });

  function getAutho(tag) {
    // body...
      if(tag==0){
          //重新请求
          page.page = 1;
          p.resetpage();
      }
      var data = { condition: searchKey, currentPage: page.page, pageSize: page.size };
    fetchs.post('/saasPermission/selectSaasPermission', data, function (res) {
        if(res.ifSuc==1){
            if(res.data){
                page = {
                    total: res.data.total,
                    size: res.data.pageSize,
                    page: res.data.pageNum
                };
                if(tag==0){
                    p.setCount(res.data.total);
                }
                if(res.data.list){
                    $('.table-Box').html(soda(roomTpl, {data:res.data.list,key:searchKey,role:role}));
                }else {
                    p.setCount(0);
                    $('.table-Box').html(soda(roomTpl, {data:[],key:searchKey,role:role}));
                }
            }else {
                p.setCount(0);
                $('.table-Box').html(soda(roomTpl, {data:[],key:searchKey,role:role}));
            }
        }else {
            notify('danger','服务器异常');
            p.setCount(0);
            $('.table-Box').html(soda(roomTpl, {data:[],key:searchKey}));
        }
    });
  }
    //搜索
    //监听搜索框键盘事件
    $('#search').on('keypress', function (e) {
        if (e.keyCode == 13) {
            $('.input-search').removeClass('input-heighLight')
            $(this).blur();
            page.page = 1;
            page.size = 15;
            p.resetpage();
            searchKey = $(this).val();
            if(searchKey.length>0){
                //显示删除按钮
                $('#del-searchList').show();
            }else {
                $('#del-searchList').hide();
            }
            getAutho(0);
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
        searchKey = '';
        getAutho(0);
    })
  function callBack(pageCount, myPage) {
    //返回当前页和每页多少条数据
    page.page = pageCount;
    page.size = myPage;
    getAutho(1);
  }

  //创建权限点击事件
  $('body').on('click', '.create-btn', function (event) {
    $('.invoice-info-box').addClass('show');
    //赋值
    $('#id').val('');
    $('#name').val('');
    $('#url').val('');
    $('.title').text('创建权限');
    $('.countersign-btn').text('保存并继续');
    $('.countersign-btn').removeClass('save-btn');
    $('.cancel-btn').show();
    operaType = 0;
    event.stopPropagation();
  });
  //修改权限点击事件
  $('body').on('click', '#changeAuthority', function (event) {
    // body...
    //取值
    currentId =  $(this).data('id');
      //赋值
    $('#id').val($(this).data('code'));
    $('#name').val($(this).data('name'));
    $('#url').val($(this).data('url'));
    $('.invoice-info-box').addClass('show');
    $('.title').text('修改权限');
    $('.countersign-btn').text('保存');
    $('.countersign-btn').addClass('save-btn');
    $('.cancel-btn').hide();
    operaType = 1;
    event.stopPropagation();
  });
  $('#delModal').on('show.bs.modal', function (event) {
      var button = $(event.relatedTarget);
      var id = button.data('id');
      currentId = id;
  });
    //隐藏创建权限窗口
  $('body').on('click', function (event) {

    var value = event.target.classList;
    var is = $(event.target).hasClass('show-box');
    if (is == false) {
      $('.invoice-info-box').removeClass('show');
      $('input.show-box').val('');
      $('input.show-box').removeClass('error-box');
        $('#errormsg').text('');
        $('#errormsg').hide();
    }
  });
  //取消按钮点击
  $('body').on('click', '.cancel-btn', function () {
    $('.invoice-info-box').removeClass('show');
      $('input.show-box').val('');
      $('input.show-box').removeClass('error-box');
      $('#errormsg').text('');
      $('#errormsg').hide();

  });
  //保存并继续按钮点击
  $('body').on('click', '.countersign-btn', function () {
      $(this).attr('disabled',true);
    //判断输入是否为空
    if (isNull() == true) {
        $(this).attr('disabled',false);
      return;
    }
    var code = $('#id').val();
    var name = $('#name').val();
    var url = $('#url').val();
    $('#id').removeClass('error-box');
    $('#url').removeClass('error-box');
    $('#name').removeClass('error-box');
    //发起提交请求
      //发起提交请求
      if(operaType==0){
          //保存并继续
          fetchs.post('/saasPermission/saveSaasPermission',{permissionCode:code,permissionName:name,permissionUrl:url},function(res){
              $('.countersign-btn').attr('disabled',false);
              if(res.ifSuc==1){
                  getAutho(0);
                  $('#id').val('');
                  $('#url').val('');
                  $('#name').val('');
                  $('#errormsg').text('');
                  $('#errormsg').hide();
              }else {
                  $('#errormsg').text(res.msg);
                  $('#errormsg').show();
              }
          });
      }else if(operaType==1){
          //保存
          fetchs.post('/saasPermission/updateSaasPermission',{permissionId:currentId,permissionCode:code,permissionName:name,permissionUrl:url},function(res){
              $('.countersign-btn').attr('disabled',false);
              getAutho(1);
              if(res.ifSuc==1){
                  $('.invoice-info-box').removeClass('show');
                  notify('success','修改成功');
              }else {
                  notify('danger',res.msg);
              }
          });
      }
  });
    //删除权限
    $('#delAuth').on('click',function () {
        //删除请求
        $(this).attr('disabled',true);
        fetchs.post('/saasPermission/deleteSaasPermissionById',{permissionId:currentId},function (res) {
            $('#delAuth').attr('disabled',false);
            if(res.ifSuc==1){
                $('#delModal').modal('hide');
                notify('success','删除成功');
                getAutho(0);
            }else {
                notify('danger',res.msg);
            }
        });

    });
  //输入内容校验
  function isNull() {
    //判断输入是否为空
      var istrue = true;
    if($('#id').val().length==0){
        istrue = false;
        $('#id').addClass('error-box');
    }else {
        $('#id').removeClass('error-box');
    }
    if($('#name').val().length==0){
        $('#name').addClass('error-box');
        istrue = false;
    }else {
        $('#name').removeClass('error-box');
    }
    if($('#url').val().length==0){
        istrue = false;
        $('#url').addClass('error-box');
    }else {
        $('#url').removeClass('error-box');
    }
    return false;
  }
});
//# sourceMappingURL=authority.js.map
