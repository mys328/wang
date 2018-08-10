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
    //初始化分页
    var p = Pages();
    p.callBack(callBack);
  var operaType = 0; //操作类型 0-创建用户 1-修改用户 2-重置密码
  var searchKey = ''; //搜索关键字
  var currentId = '';
    var role = {
        createRole: 0, //创建权限
        editRole: 0, //修改权限
        delRole: 0, //删除权限
        authRole: 0, //授权权限
        resetRole: 0 //重置密码权限
    }
    // 自定义sodajs前缀
    soda.prefix('ng-');
    soda.filter('nosys',function (roles) {
        if(roles==null) return true;
        for (var i =0; i<roles.length; i++){
            if(roles[i].code==0){
                return false;
            }
        }
        return true;
    })
    soda.filter('roles',function (roles) {
        if(roles==null) return '';
        if(roles.length==0) return '';
        var text = '';
        for (var i =0; i<roles.length; i++){
          text += roles[i].name + ' '
        }
        return text;
    });
    var roomTpl = '<div class="scroll-list" ng-if="data.length>0">  <p class="list-pie"></p> <table class="table"> <tbody> <tr> <th class="table-1"><span>用户名</span></th> <th class="table-2"><span>姓名</span></th> <th class="table-3"><span>职位</span></th> <th class="table-4"><span>角色</span></th> <th class="table-5"><span>操作</span></th> </tr>  <tr ng-repeat="item in data"> <td class="table-1"><span data-toggle="tooltip" data-title="{{item.email|text}}" ng-html="item.email|keylight:key"></span></td> <td class="table-2"><span data-toggle="tooltip" data-title="{{item.userName|text}}" ng-html="item.userName|keylight:key"></span></td> <td class="table-3"><span data-toggle="tooltip" data-title="{{item.position|text}}">{{item.position|text}}</span></td> <td class="table-4" ><span data-toggle="tooltip" data-title="{{item.rolesName|roles}}">{{item.rolesName|roles}}</span></td> <td class="table-5"> <span ng-if="role.editRole == 1" class="btn-span" id="changeAuthorize" data-id="{{item.id}}" data-username="{{item.email|text}}" data-name="{{item.userName|text}}" data-position="{{item.position|text}}" data-pwd="" data-surepwd="">修改</span> <span ng-if="role.delRole == 1 && item.rolesName|nosys" class="btn-span" data-toggle="modal" data-target="#delModal" data-id="{{item.id}}">删除</span> <span ng-if="role.authRole == 1" class="btn-span" data-toggle="modal" data-target="#authorizeModal" data-id="{{item.id}}" data-name="{{item.userName|text}}" data-position="{{item.position|text}}">分配角色</span> <span ng-if="role.resetRole == 1" class="btn-span" id="resetPwd" data-id="{{item.id}}" data-username="{{item.email|text}}" data-name="{{item.userName|text}}" data-position="{{item.position|text}}" data-pwd="" data-surepwd="">重置密码</span> </td> </tr> </tbody> </table> </div><div class="nothing" ng-if="data.length==0">暂无相关数据</div> ';
    //查询权限信息
    $('.create-btn').hide();
    fetchs.post('/saasRolePermission/findSaasPermissionsByParentId',{parentId:1007},function (res) {
        console.log(res);
        for (var i = 0; i<res.data.length; i++){
            var orgCode = res.data[i].orgCode;
            if(orgCode==1007001){
                role.createRole = 1; //创建权限
                $('.create-btn').show();
            }
            if(orgCode==1007002) {
                role.editRole = 1; //修改用户
            }
            if(orgCode==1007003) {
                role.delRole = 1; //删除用户
            }
            if(orgCode==1007004) {
                role.authRole = 1; //授权角色
            }
            if(orgCode==1007005) {
                role.resetRole = 1; //重置密码
            }
        }
        getInvoiceInfo(0);
    });

  function getInvoiceInfo(tag) {
    // body...
      if(tag==0){
          //重新请求
          page.page = 1;
          p.resetpage();
      }
      var data = { condition: searchKey, currentPage: page.page, pageSize: page.size };
      fetchs.post('/saasUser/selectSaasUserList', data, function (res) {
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
              $('.table-Box').html(soda(roomTpl, {data:[],key:searchKey,role:role}));
          }
      });
  }
  function callBack(pageCount, myPage) {
    //返回当前页和每页多少条数据
    page.page = pageCount;
    page.size = myPage;
    getInvoiceInfo(1);
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
            getInvoiceInfo(0);
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
        getInvoiceInfo(0);
    })

    var authList = ' <div class="list-group"> <button type="button" ng-repeat="item in data" ng-class="type == 0 ? \'list-group-item  unauth-item\' : \'list-group-item auth-item\'" data-id="{{item.roleId}}" data-index="{{$index}}">{{item.roleName|text}}</button> </div>';
    var unAuthArr = []; //未授权列表
    var authArr = []; //已授权列表
    var selectUnAuthArr = []; //选择未授权列表
    var selectAuthArr = []; //选择已授权列表
    var times = null;
    //列表单击事件
    $('body').on('click','.list-group-item',function(event){
        clearTimeout(times);
        var _this = $(this);
        times = setTimeout(function(){
            var id = _this.data('id');
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
                            if(selectUnAuthArr[i].roleId!=item.roleId){
                                selectUnAuthArr.push(item);
                                break;
                            }
                        }
                    }

                }else {
                    //授权
                    var item = authArr[index];
                    if(selectAuthArr.length==0){
                        selectAuthArr.push(item);
                    }else {
                        for(var i=0;i<selectAuthArr.length;i++){
                            if(selectAuthArr[i].roleId!=item.roleId){
                                selectAuthArr.push(item);
                                break;
                            }
                        }
                    }

                }
            }
        },300);
    })
    //双击事件
    $('body').on('dblclick','.list-group-item',function (event) {
        clearTimeout(times);
        var id = $(this).data('id');
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
                    if(authArr[k].roleId!=item.roleId){
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
            if(unAuthArr.length==0){
                unAuthArr.push(item);
            }else {
                for(var k=0;k<unAuthArr.length;k++){
                    if(unAuthArr[k].roleId!=item.roleId){
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
        if(type==0){
            //选中未授权的添加到已授权
            for(var i =0; i<selectUnAuthArr.length;i++){
                var item= selectUnAuthArr[i];
                //添加到授权列表
                if(authArr.length==0){
                    authArr.push(item);
                }else {
                    for(var k=0;k<authArr.length;k++){
                        if(authArr[k].roleId!=item.roleId){
                            authArr.push(item);
                            break;
                        }
                    }
                }
                //从未授权列表移除
                for(var j =0;j<unAuthArr.length;j++){
                    if(unAuthArr[j].roleId==item.roleId){
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
                        if(authArr[k].roleId!=item.roleId){
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
                        if(unAuthArr[k].roleId!=item.roleId){
                            unAuthArr.push(item);
                            break;
                        }
                    }
                }
                //从已授权列表移除
                for(var j =0;j<authArr.length;j++){
                    if(authArr[j].roleId==item.roleId){
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
                if(unAuthArr.length==0){
                    unAuthArr.push(item);
                }else {
                    for(var k=0;k<unAuthArr.length;k++){
                        if(unAuthArr[k].roleId!=item.roleId){
                            unAuthArr.push(item);
                            break;
                        }
                    }
                }
            }
            //从未授权列表移除
            authArr = [];
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
            permissions.push(authArr[i].roleId);
        }
        // if(permissions.length==0){
        //     $('#error').text('请添加授权角色');
        //     $('#error').show();
        //     return;
        // }
        $('#error').hide();
        var data = {
            userId: currentId,
            roleIds: permissions
        }
        fetchs.post('/saasUserRole/saveSaasUserRole',data,function (res) {
            console.log(res);
            if(res.ifSuc==1){
                //成功
                notify('success','授权成功');
                $('#authorizeModal').modal('hide');
                getInvoiceInfo(1);
            }else {
                $('#error').show();
                $('#error').text(res.msg);
            }
        });
    });
    //查询未授权权限列表
    function selectUnAuth() {
        fetchs.post('/saasUserRole/findUserRolesUnAuth',{userId:currentId},function (res) {
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
        fetchs.post('/saasUserRole/findUserRolesByUserId',{userId:currentId},function (res) {
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
    //授权
    $('#authorizeModal').on('show.bs.modal',function (event) {
        var button = $(event.relatedTarget);
        var id = button.data('id');
        currentId = id;
        var name = button.data('name');
        var position = button.data('position');
        //赋值
        $('.role-id').text('姓名：'+name);
        $('.role-name').text('职位：'+position);
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

  //创建用户点击事件
  $('body').on('click', '.create-btn', function (event) {
    $('.invoice-info-box').addClass('show');
    //赋值
    $('#userName').val('');
    $('#name').val('');
    $('#position').val('');
    $('.resetpwd').attr("readOnly", false);
    $('#pwd').val('');
    $('#surepwd').val('');
    $('.invoice-info-box').addClass('show');
    $('.title').text('创建用户');
    $('.create').show();
    $('.countersign-btn').text('保存并继续');
    $('.countersign-btn').removeClass('save-btn');
    $('.cancel-btn').show();
    operaType = 0;
    event.stopPropagation();
  });
  //修改用户点击事件
  $('body').on('click', '#changeAuthorize', function (event) {
    // body...
    //取值
      currentId = $(this).data('id');

      //赋值
    $('.resetpwd').attr("readOnly", false);
    $('#userName').val($(this).data('username'));
    $('#name').val($(this).data('name'));
    $('#position').val($(this).data('position'));
    $('.invoice-info-box').addClass('show');
    $('.title').text('修改用户');
    $('.create').hide();
    $('.countersign-btn').text('保存');
    $('.countersign-btn').addClass('save-btn');
    $('.cancel-btn').hide();
    operaType = 1;
    event.stopPropagation();
  });
  //重置密码点击事件
  $('body').on('click', '#resetPwd', function (event) {
    // body...
    //取值
      currentId = $(this).data('id');

      //赋值
    $('#userName').val($(this).data('username'));
    $('#name').val($(this).data('name'));
    $('#position').val($(this).data('position'));
    $('.resetpwd').attr("readOnly", true);
    $('#pwd').val($(this).data('pwd'));
    $('#surepwd').val($(this).data('surepwd'));
    $('.invoice-info-box').addClass('show');
    $('.title').text('重置密码');
    $('.create').show();
    $('.countersign-btn').text('保存');
    $('.countersign-btn').addClass('save-btn');
    $('.cancel-btn').hide();
    operaType = 2;
    event.stopPropagation();
  });
  //删除用户事件
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
      if (isNull() == false) {
          $(this).attr('disabled',false);
          return;
      }
      if(operaType==0){ //创建
          var user = {userName:$('#name').val(),
              email: $('#userName').val(),
              position: $('#position').val(),
              password: $('#pwd').val(),
              passwordConfirm: $('#surepwd').val()};
          //发起提交请求
          fetchs.post('/saasUser/saveSaasUser',user,function(res){
              $('.countersign-btn').attr('disabled',false);
              if(res.ifSuc==1){
                  getInvoiceInfo(0);
                  $('#userName').val('');
                  $('#name').val('');
                  $('#position').val('');
                  $('#pwd').val('');
                  $('#surepwd').val('');
                  $('#errormsg').text('');
                  $('#errormsg').hide();
              }else {
                  $('#errormsg').text(res.msg);
                  $('#errormsg').show();
              }

          });
      }else if(operaType==1){ //修改信息
          var updateuser = {userId: currentId,
              userName:$('#name').val(),
              email: $('#userName').val(),
              position: $('#position').val()};
          //发起提交请求
          fetchs.post('/saasUser/updateSaasUser',updateuser,function(res){
              $('.countersign-btn').attr('disabled',false);
              getInvoiceInfo(1);
              if(res.ifSuc==1){
                  $('.invoice-info-box').removeClass('show');
                  notify('success','修改成功');
              }else {
                  notify('danger',res.msg);
              }
          });
      }else if(operaType==2){ //重置密码
          var resetpwd = {userId: currentId,
              password: $('#pwd').val(),
              passwordConfirm: $('#surepwd').val()};
          //发起提交请求
          fetchs.post('/saasUser/updateSaasUserPassword',resetpwd,function(res){
              $('.countersign-btn').attr('disabled',false);
              if(res.ifSuc==1){
                  $('.invoice-info-box').removeClass('show');
                  notify('success','重置成功');
              }else {
                  notify('danger',res.msg);
              }
          });
      }

  });

  //删除用户
  $('#delUser').on('click',function () {
      //删除请求
      $(this).attr('disabled',true);
      fetchs.post('/saasUser/deleteSaasUserByUserId',{userId:currentId},function (res) {
          $('#delUser').attr('disabled',false);
          if(res.ifSuc==1){
              $('#delModal').modal('hide');
              notify('success','删除成功');
              getInvoiceInfo(0);
          }else {
              notify('danger',res.msg);
          }
      });

  });
    $('body').on('mouseenter', '[data-toggle="tooltip"]', function (e) {
        var text = $(this).data('title');
        var size = parseInt($(this).css('fontSize'));
        if ($(this).parent().width() < size * text.length) {
            $(this).tooltip({
                placement: function placement(tip, element) {

                    // return 'left';
                    // return 'left';
                    return window.scrollY + 50 < $(element).offset().top ? 'top' : 'bottom';
                },
                trigger: 'hover'
            });
            $(this).tooltip('toggle');
        }
    });
  //输入内容校验
  function isNull() {
    //判断输入是否为空
    var istrue = true;
    if(operaType==0){
      //创建用户，判断所有输入项
        if($('#userName').val().length==0){
            //用户名为空
            istrue = false;
            $('#userName').addClass('error-box');
        }else {
            //用户名格式是否正确
            if(!isEmail($('#userName').val())){
                istrue = false;
                $('#userName').addClass('error-box');
                $('#errormsg').text('用户名格式不正确');
                $('#errormsg').show();
            }else {
                $('#errormsg').text('');
                $('#errormsg').hide();
                $('#userName').removeClass('error-box');
            }
        }
        if($('#name').val().length==0){
          //姓名为空
          istrue = false;
          $('#name').addClass('error-box');
        }else {
            $('#name').removeClass('error-box');
        }
        if($('#position').val().length==0){
          //职位为空
          istrue = false;
          $('#position').addClass('error-box');
        }else {
            $('#position').removeClass('error-box');
        }
        if($('#pwd').val().length==0){
          //登录密码为空
            istrue = false;
            $('#pwd').addClass('error-box');
        }else {
          $('#pwd').removeClass('error-box');
        }
        if($('#surepwd').val().length==0){
          //确认密码为空
          istrue = false;
          $('#surepwd').addClass('error-box');
        }else {
          $('#surepwd').removeClass('error-box');
        }
        if(istrue){
            if($('#pwd').val() != $('#surepwd').val()){
                istrue = false;
                $('#errormsg').text('登录密码和确认密码不一致');
                $('#errormsg').show();
            }else {
                $('#errormsg').text('');
                $('#errormsg').hide();
            }
        }
        return istrue;
    }
    if(operaType==1){
      //修改用户
        if($('#userName').val().length==0){
            //用户名为空
            istrue = false;
            $('#userName').addClass('error-box');
        }else {
            //用户名格式是否正确
            if(!isEmail($('#userName').val())){
                istrue = false;
                $('#userName').addClass('error-box');
                $('#errormsg').text('用户名格式不正确');
                $('#errormsg').show();
            }else {
                $('#errormsg').text('');
                $('#errormsg').hide();
                $('#userName').removeClass('error-box');
            }
        }
        if($('#name').val().length==0){
            //姓名为空
            istrue = false;
            $('#name').addClass('error-box');
        }else {
            $('#name').removeClass('error-box');
        }
        if($('#position').val().length==0){
            //职位为空
            istrue = false;
            $('#position').addClass('error-box');
        }else {
            $('#position').removeClass('error-box');
        }
        return istrue;

    }
    if(operaType==2){
      //重置密码
        if($('#pwd').val().length==0){
            //登录密码为空
            istrue = false;
            $('#pwd').addClass('error-box');
        }else {
            $('#pwd').removeClass('error-box');
        }
        if($('#surepwd').val().length==0){
            //确认密码为空
            istrue = false;
            $('#surepwd').addClass('error-box');
        }else {
            $('#surepwd').removeClass('error-box');
        }
        if($('#pwd').val() != $('#surepwd').val()){
            istrue = false;
            $('#errormsg').text('登录密码和确认密码不一致');
            $('#errormsg').show();
        }else {
            $('#errormsg').text('');
            $('#errormsg').hide();
        }
        return istrue;
    }
  }
});
