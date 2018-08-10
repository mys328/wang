'use strict';

$(function () {
  $('#select-right').text('选中添加到右边>>');
  $('#all-right').text('全部添加到右边>>');
  $('#select-left').text('<<选中添加到左边');
  $('#all-left').text('<<全部添加到左边');
  soda.prefix('ng-');
  var page = {
    total: 0,
    size: 15,
    page: 1
  };
    var p = Pages();
    p.callBack(callBack);
  var currentId = ''; //当前操作的ID
  var currentRoleId = ''; //当前操作的角色ID
  var searchKey = ''; //搜索关键字
  var operaType = 0; //操作类型 0-创建 1-修改
    var role = {
        createRole: 0, //创建权限
        editRole: 0, //修改权限
        delRole: 0, //删除权限
        authRole: 0 //授权权限
    }
    var roomTpl = ' <div class="scroll-list" ng-if="data.length>0"> <p class="list-pie"></p> <table class="table">  <tbody ><tr> <th class="table-1"><span>角色ID</span></th> <th class="table-2"><span>角色名称</span></th> <th class="table-3"><span>操作</span></th> </tr><tr ng-repeat="item in data"> <td class="table-1"><span data-toggle="tooltip" data-title="{{item.orgCode|text}}">{{item.orgCode|text}}</span></td> <td class="table-2"><span data-toggle="tooltip" data-title="{{item.roleName|text}}" ng-html="item.roleName|keylight:key"></span></td> <td class="table-3"> <span ng-if="role.authRole == 1" class="btn-span" data-toggle="modal" data-target="#authorizeModal" data-id="{{item.roleId}}" data-roleid="{{item.orgCode}}" data-rolename="{{item.roleName}}">授权</span> <span ng-if="role.editRole == 1" class="btn-span" id="changeAuthorize" data-id="{{item.roleId}}" data-roleid="{{item.orgCode}}" data-rolename="{{item.roleName}}">修改</span> <span ng-if="role.delRole == 1 && item.orgCode != 0" class="btn-span" data-toggle="modal" data-target="#delModal" data-id="{{item.roleId}}">删除</span> </td> </tr> </tbody> </table> </div> <div class="nothing" ng-if="data.length==0">暂无相关数据</div>';
    //查询权限信息
    $('.create-btn').hide();
    fetchs.post('/saasRolePermission/findSaasPermissionsByParentId',{parentId:1006},function (res) {
        console.log(res);
        for (var i = 0; i<res.data.length; i++){
            var orgCode = res.data[i].orgCode;
            if(orgCode==1006001){
                role.createRole = 1; //创建角色
                $('.create-btn').show();
            }
            if(orgCode==1006002) {
                role.authRole = 1; //授权权限
            }
            if(orgCode==1006003) {
                role.editRole = 1; //修改角色
            }
            if(orgCode==1006004) {
                role.delRole = 1; //删除角色

            }
        }
        getroles(0);
    });

  function getroles(tag) {
    // body...
      if(tag==0){
          //重新请求
          page.page = 1;
          p.resetpage();
      }
      var data = { condition: searchKey, currentPage: page.page, pageSize: page.size };
      fetchs.post('/saasRole/findAllSaasRoles', data, function (res) {
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
              $('.list').html(soda(roomTpl, {data:[],key:searchKey,role:role}));
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
            getroles(0);
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
        getroles(0);
    })
  var authList = ' <div class="list-group"> <button type="button" ng-repeat="item in data" ng-class="type == 0 ? \'list-group-item  unauth-item\' : \'list-group-item auth-item\'" data-id="{{item.permissionId}}" data-code="{{item.orgCode}}" data-index="{{$index}}">{{item.orgName|text}}</button> </div>';
    var unAuthArr = []; //未授权列表
    var authArr = []; //已授权列表
    var selectUnAuthArr = []; //选择未授权列表
    var selectAuthArr = []; //选择已授权列表
    var times = null;
     //列表单击事件
    $('body').on('click','.list-group-item',function(event){
        $('#error').hide();
        $('#error').text('');
        clearTimeout(times);
        var _this = $(this);
        times = setTimeout(function () {
            var id = _this.data('id');
            var code = _this.data('code');
            var name = _this.data('name');
            var index = _this.data('index');
            console.log(index);
            // item = JSON.stringify(item);
            if(_this.hasClass('selected')==true){
                //取消选中
                _this.removeClass('selected');
                if(_this.hasClass('unauth-item')==true){
                    //未授权
                    var item = unAuthArr[index];
                    var i = selectUnAuthArr.indexOf(item);
                    selectUnAuthArr.splice(i,1);
                }else {
                    //授权
                    var item = authArr[index];
                    var i = selectAuthArr.indexOf(item);
                    selectAuthArr.splice(i,1);
                }
            }else {
                //选中
                _this.addClass('selected');
                if(_this.hasClass('unauth-item')==true){
                    //未授权
                    var item = unAuthArr[index];
                    if(selectUnAuthArr.length==0){
                        selectUnAuthArr.push(item);
                    }else {
                        for(var i=0;i<selectUnAuthArr.length;i++){
                            if(selectUnAuthArr[i].permissionId!=item.permissionId){
                                selectUnAuthArr.push(item);
                                break;
                            }
                        }
                    }

                }else {
                    //授权
                    var item = authArr[index];
                    if((code.toString().indexOf('1005')==0 || item.orgCode == 1006002) && currentRoleId == 0){
                        //如果权限以1005开头，不可取消
                        $('#error').show();
                        $('#error').text('该权限不可取消授权');
                        _this.removeClass('selected');
                        return;
                    }

                    if(selectAuthArr.length==0){
                        selectAuthArr.push(item);
                    }else {
                        for(var i=0;i<selectAuthArr.length;i++){
                            if(selectAuthArr[i].permissionId!=item.permissionId){
                                selectAuthArr.push(item);
                                break;
                            }
                        }
                    }

                }
            }
        },300);
    });
    //双击事件
    $('body').on('dblclick','.list-group-item',function (event) {
        $('#error').hide();
        $('#error').text('');
        clearTimeout(times);
        console.log($(this).roleId);
        var id = $(this).data('id');
        var code = $(this).data('code');
        var name = $(this).data('name');
        var index = $(this).data('index');
        console.log(index);
        if($(this).hasClass('unauth-item')==true){
            //未授权
            var item = unAuthArr[index];
            //添加到授权列表
            if(authArr.length==0){
                authArr.push(item);
            }else {
                for(var k=0;k<authArr.length;k++){
                    if(authArr[k].permissionId!=item.permissionId){
                        authArr.push(item);
                        break;
                    }
                }
            }
            //从未授权列表移除
            unAuthArr.splice(index,1);
        }else {
            //授权
            var item = authArr[index];
            //添加到未授权列表
            var is= (code.toString().indexOf('1005')==0 || item.orgCode == 1006002) && currentRoleId == 0;
            if(is == true){
                //如果权限以1005开头，不可取消
                $('#error').show();
                $('#error').text('该权限不可取消授权');
                $(this).removeClass('selected');
                return;
            }
            $('#error').hide();
            $('#error').text('');
            if(unAuthArr.length==0){
                unAuthArr.push(item);
            }else {
                for(var k=0;k<unAuthArr.length;k++){
                    if(unAuthArr[k].permissionId!=item.permissionId){
                        unAuthArr.push(item);
                        break;
                    }
                }
            }
            //从已授权列表移除
            authArr.splice(index,1);
        }
        //重新渲染列表
        $('#unauthorize').html(soda(authList, {data:unAuthArr,type:0}));
        $('#authorize').html(soda(authList, {data:authArr,type:1}));
    });
    //授权取消授权
    $('.operate .operate-btn').on('click',function (event) {
        var type = $(this).data('type');
        $('#error').hide();
        $('#error').text('');
        if(type==0){
            //选中未授权的添加到已授权
            for(var i =0; i<selectUnAuthArr.length;i++){
                var item= selectUnAuthArr[i];
                //添加到授权列表
                if(authArr.length==0){
                    authArr.push(item);
                }else {
                    for(var k=0;k<authArr.length;k++){
                        if(authArr[k].permissionId!=item.permissionId){
                            authArr.push(item);
                            break;
                        }
                    }
                }
                //从未授权列表移除
                for(var j =0;j<unAuthArr.length;j++){
                    if(unAuthArr[j].permissionId==item.permissionId){
                        unAuthArr.splice(j,1);
                        break;
                    }
                }
            }

        }else if(type==1){
            //所有未授权的添加到已授权
            for(var i =0; i<unAuthArr.length;i++){
                var item= unAuthArr[i];
                //添加到授权列表
                if(authArr.length==0){
                    authArr.push(item);
                }else {
                    for(var k=0;k<authArr.length;k++){
                        if(authArr[k].permissionId!=item.permissionId){
                            authArr.push(item);
                            break;
                        }
                    }
                }
            }
            //从未授权列表移除
            unAuthArr = [];
        }else if(type==2){
            //选中已授权的添加到未授权
            for(var i =0; i<selectAuthArr.length;i++){
                var item= selectAuthArr[i];
                //添加到未授权列表
                if(unAuthArr.length==0){
                    unAuthArr.push(item);
                }else {
                    for(var k=0;k<unAuthArr.length;k++){
                        if(unAuthArr[k].permissionId!=item.permissionId){
                            unAuthArr.push(item);
                            break;
                        }
                    }
                }
                //从已授权列表移除
                for(var j =0;j<authArr.length;j++){
                    if(authArr[j].permissionId==item.permissionId){
                        authArr.splice(j,1);
                        break;
                    }
                }
            }
        }else if(type==3){
            //所有已授权的添加到未授权
            for(var i =0; i<authArr.length;i++){
                var item= authArr[i];
                //添加到授权列表
                if(unAuthArr.length==0 && ((item.orgCode.toString().indexOf('1005')<0 && item.orgCode != 1006002) || currentRoleId != 0)){
                    unAuthArr.push(item);
                }else {
                    for(var k=0;k<unAuthArr.length;k++){
                        if(unAuthArr[k].permissionId!=item.permissionId && ((item.orgCode.toString().indexOf('1005')<0 && item.orgCode != 1006002) || currentRoleId != 0)){
                            unAuthArr.push(item);
                            break;
                        }
                    }
                }
            }
            //从未授权列表移除
            var temArr = [];
            for(var i = 0; i < authArr.length; i++){
                var item= authArr[i];
                if((item.orgCode.toString().indexOf('1005')==0 || item.orgCode == 1006002) && currentRoleId == 0){
                    temArr.push(authArr[i]);
                }
            }
            authArr = temArr;
        }
        //重新渲染列表
        $('#unauthorize').html(soda(authList, {data:unAuthArr,type:0}));
        $('#authorize').html(soda(authList, {data:authArr,type:1}));
        //清空选择元素
        selectAuthArr = [];
        selectUnAuthArr = [];
    });
    //权限分配
    $('#submitAuth').on('click',function(event){
        var permissions = [];
        for(var i=0;i<authArr.length;i++){
            permissions.push(authArr[i].permissionId);
        }
        // if(permissions.length==0){
        //     $('#error').text('请添加授权权限');
        //     $('#error').show();
        //     return;
        // }
        $('#error').hide();
        var data = {
            roleId: currentId,
            permissionIds: permissions
        }
        fetchs.post('/saasRolePermission/updateSaasRolePermission',data,function (res) {
            console.log(res);
            if(res.ifSuc==1){
                //成功
                notify('success','授权成功');
                $('#authorizeModal').modal('hide');
                getroles(1);
            }else {
                $('#error').show();
                $('#error').text(res.msg);
            }
        });
    });
  //查询未授权权限列表
    function selectUnAuth() {
        fetchs.post('/saasRolePermission/findSaasPermissionsUnAuth',{roleId:currentId},function (res) {
            console.log(res);
            if(res.ifSuc==1){
                if(res.data){
                    unAuthArr = res.data;
                }else {
                    unAuthArr = [];
                }

            }else {
                unAuthArr = [];
            }
            $('#unauthorize').html(soda(authList, {data:unAuthArr,type:0}));
        });
    }
    //查询已授权权限列表
    function selectAuth() {
        fetchs.post('/saasRolePermission/selectSaasPermissionsByRoleId',{roleId:currentId},function (res) {
            console.log(res);
            if(res.ifSuc==1){
                if(res.data){
                    authArr = res.data;
                }else {
                    authArr = [];
                }

            }else {
                authArr = [];
            }
            $('#authorize').html(soda(authList, {data:authArr,type:1}));
        });
    }

  function callBack(pageCount, myPage) {
    //返回当前页和每页多少条数据
    page.page = pageCount;
    page.size = myPage;
    getroles(1);
  }

  //创建权限点击事件
  $('body').on('click', '.create-btn', function (event) {
    $('.invoice-info-box').addClass('show');
    $('#roleid').val($(this).data('roleid'));
    $('#rolename').val($(this).data('rolename'));
    $('.title').text('创建角色');
    $('.countersign-btn').text('保存并继续');
    $('.countersign-btn').removeClass('save-btn');
    $('.cancel-btn').show();
    operaType = 0;
    event.stopPropagation();
  });
  //修改权限点击事件
  $('body').on('click', '#changeAuthorize', function (event) {
    // body...
    //取值
    currentId =  $(this).data('id'),
        //赋值

    $('#roleid').val($(this).data('roleid'));
      if($('#roleid').val()==0){
          $('#roleid').attr("readOnly", true);
      }else {
          $('#roleid').attr("readOnly", false);
      }
    $('#rolename').val($(this).data('rolename'));
    $('.title').text('修改角色');
    $('.countersign-btn').text('保存');
    $('.countersign-btn').addClass('save-btn');
    $('.cancel-btn').hide();
    $('.invoice-info-box').addClass('show');
    operaType = 1;
    event.stopPropagation();
  });
    //删除事件
    $('#delModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var id = button.data('id');
        currentId = id;
    });
    //授权
    $('#authorizeModal').on('show.bs.modal',function (event) {
        var button = $(event.relatedTarget);
        var id = button.data('id');
        currentId = id;
        var roleid = button.data('roleid');
        var rolename = button.data('rolename');
        //赋值
        $('.role-id').text('角色ID：'+roleid);
        $('.role-name').text('角色名称：'+rolename);
        currentRoleId = roleid;
        selectUnAuth();
        selectAuth();

    })
    //隐藏授权
    $('#authorizeModal').on('hide.bs.modal',function (event) {
        //清空数据
        selectAuthArr = [];
        selectUnAuthArr = [];
        authArr = [];
        unAuthArr = [];
        //重新渲染列表
        $('#unauthorize').html(soda(authList, {data:unAuthArr,type:0}));
        $('#authorize').html(soda(authList, {data:authArr,type:1}));
        $('#error').hide();
    })
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
      var roleid = $('#roleid').val();
      var rolename = $('#rolename').val();
      $('#roleid').removeClass('error-box');
      $('#rolename').removeClass('error-box');
      //发起提交请求
      if(operaType==0){
        //保存并继续
          fetchs.post('/saasRole/saveSaasRole',{roleCode:roleid,roleName:rolename},function(res){
              $('.countersign-btn').attr('disabled',false);
              if(res.ifSuc==1){
                  getroles(0);
                  $('#roleid').val('');
                  $('#rolename').val('');
                  $('#errormsg').text('');
                  $('#errormsg').hide();
              }else {
                  $('#errormsg').text(res.msg);
                  $('#errormsg').show();
              }
          });
      }else if(operaType==1){
        //保存
          fetchs.post('/saasRole/updateSaasRole',{roleId:currentId,roleCode:roleid,roleName:rolename},function(res){
              $('.countersign-btn').attr('disabled',false);
              getroles(1);
              if(res.ifSuc==1){
                  $('.invoice-info-box').removeClass('show');
                  notify('success','修改成功');
              }else {
                  notify('danger',res.msg);
              }
          });
      }
  });
    //删除角色
    $('#delRole').on('click',function () {
        //删除请求
        $(this).attr('disabled',true);
        fetchs.post('/saasRole/deleteSaasRole',{roleId:currentId},function (res) {
            $('#delRole').attr('disabled',false);
            if(res.ifSuc==1){
                $('#delModal').modal('hide');
                notify('success','删除成功');
                getroles(0);
            }else {
                notify('danger',res.msg);
            }
        });

    });
  //输入内容校验
  function isNull() {
    //判断输入是否为空
    if ($('#roleid').val().length == 0 && $('#rolename').val().length == 0) {
      $('#roleid').addClass('error-box');
      $('#rolename').addClass('error-box');
      return true;
    }
    $('#roleid').removeClass('error-box');
    $('#rolename').removeClass('error-box');
    if ($('#roleid').val().length == 0) {
      $('#roleid').addClass('error-box');
      return true;
    }

    if ($('#rolename').val().length == 0) {
      $('#rolename').addClass('error-box');
      return true;
    }
  }
    $('body').on('mouseenter', '[data-toggle="tooltip"]', function (e) {
        var text = $(this).data('title');
        var size = parseInt($(this).css('fontSize'));
        if ($(this).parent().width() < size * text.length) {
            $(this).tooltip({
                placement: function placement(tip, element) {

                    // return 'left';
                    return window.scrollY + 50 < $(element).offset().top ? 'top' : 'bottom';
                },
                trigger: 'hover'
            });
            $(this).tooltip('toggle');
        }
    });
});
