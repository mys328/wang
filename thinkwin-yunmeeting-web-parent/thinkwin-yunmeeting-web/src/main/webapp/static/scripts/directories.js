'use strict';
var zTreeObj;
var pageObj = {
    aid:"1", //记录组织id
    size:15, //每页多少条
    page: 1, //当前页码
    total:0, //记录总条数
    orgName:"",
};
var userStatus ="1";//记录用户点击的状态
var category=0; //操作类别  0:默认点击, 1:搜索状态  2.点击人员状态 3.点击搜索状态下的人员状态
var searchKey=""; //记录搜索的关键字
var userNumInfo={};//记录机构下的不同状态的人员数量
var personnels = []; //记录机构下的人员数组
var oldName="";//记录编辑之前的名字
var zTree = $.fn.zTree.getZTreeObj('treeDemo'); //拿到初始化的ztree
var renameTag = 0; //用于区分是新建名称编辑 和 对之前组织机构的名称进行编辑
var $tenantId;//租户id
$(function () {
  //初始化分页控件
  var p = Pages();
  p.callBack(pageCallBack); //初始化分页器自定义回调函数
  //初始化,并配置ztree
  var setting = {
    edit: {
      enable: true,
      showRemoveBtn: false,
      showRenameBtn: false,
      drag: {
        prev: true,
        next: true,
        inner: true
      }
    },
    data: {
      simpleData: {
        enable: true
      }
    },
    callback: {
      onExpand: didClickExpand,
      beforeRename: beforeRename,
      onRename: onRename, //重命名后的事件回调
      beforeRemove: beforeRemove, //删除机构前的事件回调
      onRemove: onRemove,
      beforeDrag: beforeDrag,
      beforeDrop: beforeDrop,
      onClick: onClick,
    },
    view: {
      dblClickExpand: false,
      addDiyDom: addDiyDom,
      showTitle: false
    }
  };
  $("input.rename").maxLength=20;
  //拖拽前的回调函数 
  function beforeDrag(treeId, treeNodes) {
    for (var i = 0, l = treeNodes.length; i < l; i++) {
      if (treeNodes[i].drag === false) {
        return false;
      }
    }
    return true;
  }
  //拖拽放下前的回调
  function beforeDrop(treeId, treeNodes, targetNode, moveType) {
    if (targetNode.level== 0) {
          //如果目标节点是父节点,就取消
          return false;
    }
    var moveOrgId = treeNodes[0].id;
    var parentId = targetNode.id;
    var moveType;
    if (moveType == 'inner') {
      moveType = 2;
    } else if (moveType == 'prev') {
      moveType = 1;
    } else if (moveType == 'next') {
      moveType = 3;
    }
    $.ajaxSetup({
       async : false
    });
    var confirmVal = true;
    organiztionMove( moveOrgId, parentId, moveType, function (data) {
       if (data.ifSuc == 1) {
           notify("success","移动成功");
       }else {
           confirmVal = false;
           notify('danger', '移动失败');
       }
       $.ajaxSetup({
            async : true,
       });
    });
    return confirmVal;
  }
//点击ztree 展开节点的回调函数
  function didClickExpand(event, treeId, treeNode) {
     // $("ul.ztree").scrollLeft($("ul.ztree")[0].scrollWidth);
  }
  //修改名称前的回调函数
  function beforeRename(treeId, treeNode, newName, isCancel) {
      //可以在这判断名称是否合法
      var isTrue = !/^[\u4e00-\u9fa5a-zA-Z0-9\\\-()（）]{0,30}$/.test(newName) ?  false : true ;
      if(isTrue){
          return true;
      }else {
          notify('danger', '区域名称不合法');
          return false;
      }
  }
  //修改名称后的回调函数
  function onRename(event, treeId, treeNode, isCancel) {
      if(treeNode.name==oldName&&renameTag == 0){
          return;
      }
      if (treeNode.name==""){
          if (renameTag == 0) {
              treeNode.name=oldName;
              zTreeObj .updateNode(treeNode);
              return false;
          }else {
              var node = zTreeObj.getNodeByParam('id', treeNode.pId, null);
              if(node.isParent==true){
                  treeNode.name = "新建部门"+(node.children.length+1);
              }else {
                  treeNode.name = "新建部门";
              }
          }
          zTreeObj .updateNode(treeNode);
      }
      if (renameTag == 0) {
          updateOrganiztion(treeNode.id,treeNode.name, function (data) {
              if (data.ifSuc == 1) {
                  notify("success","修改成功");

              }else {
                  treeNode.name=oldName;
                  zTreeObj .updateNode(treeNode);
                  notify("danger",data.msg);
              }
              oldName="";
          });
      } else if (renameTag == 1){
          saveOrganization(treeNode.name, treeNode.pId, function (data) {
              if (data.ifSuc == 1) {
                  renameTag = 0;
                  var node = zTreeObj.getNodeByParam('id', treeNode.pId, null);
                  zTreeObj.removeNode(treeNode, false);
                  var newNode = { id: data.code, name:treeNode.name };
                  zTreeObj.addNodes(node, -1, newNode);
                  notify("success","添加成功");
              }else {
                  var node = zTreeObj.getNodeByParam('id', treeNode.id, null);
                  zTreeObj.removeNode(node, true); //第二个参数  是否要触发删除子机构的回调函数
                  notify("danger",data.msg);
              }
          });
      }
      var treeNodeId="#"+treeNode.tId+" "+".dimBgc:first";
      if ($("#"+treeNode.tId).children().is('.on')==false){
          $(treeNodeId).show();
      }
  }
  //删除前的回调函数
  function beforeRemove(treeId, treeNode) {
  }
  //删除组织机构的回调函数
  function onRemove(e, treeId, treeNode) {
    //调用删除机构的接口
  }
  //组织机构的单击事件  
  function onClick(e, treeId, treeNode) {
    var zTree = $.fn.zTree.getZTreeObj('treeDemo');
    $(".on").removeClass("on");
    $(".dimBgc").show();
    $("span.title").text(treeNode.name);
    //取消搜索状态
    $('#searchKepress').val("");
    searchKey="";
    category=0;
    if(treeNode.level==0){//点击了根节点(控制批量操作按钮的显示)
         $(".batch-insert").show();
         $(".span-list-4").show();
    }else{
         $(".batch-insert").hide();
        $(".span-list-4").hide();
    }
    var heightLight='#' + treeNode.tId + ' >div#heightLight';
    var aObj = $(heightLight);
    var treeNodeId="#"+treeNode.tId+" "+".dimBgc:first";
    $(treeNodeId).hide();
    aObj.addClass("on");
    //初始化分页器数据
    pageObj.aid=treeNode.id;
    pageObj.page = 1;
    pageObj.size = 15;
    pageObj.orgName=treeNode.name;
    p.resetpage();
    //初始化用户状态数据
    userStatus="1";
    $(".select-active").removeClass("select-active");
    $(".span-list-0").addClass("select-active");
    getAddress(1);
    $('.right.tab-content .tab-pane').removeClass('active');
    $('#addPerson').addClass('active');
    $('.justify-content-center a.nav-link').removeClass('ul-tab');
    $('.btn-box').hide();
    $('#selectAll input').prop('checked',false);
    $('.addPersonInfo #addDepartment .trigger').attr("name",pageObj.aid==''||pageObj.aid==null? '':pageObj.aid);
}
  //自定义添加...按钮
  function addDiyDom(treeId, treeNode) {
    var a='#' + treeNode.tId + ' > a';
    var aObj = $(a);
    var span='#' + treeNode.tId + ' > span';
    var wrapObj = $(span);
    var div = '<div id="heightLight" class="heightLight" ></div>';
    wrapObj.before(div);
    var selectedObj = $('#' + "treeDemo_1" + ' >div#heightLight');
    var editId = '"' + treeNode.id + '"';
    var name= '"' + treeNode.name + '"';
    var menu = '';
    if (treeNode.level == 0) {
      menu = '<button id=\'partment\' class=\'dropdown-item editStyle\' onclick=\'didClickAddDepartment(' + editId + ','+treeNode.tId+')\'>添加子部门</button>';
    } else {
      menu = '<button  class=\'dropdown-item editStyle\' onclick=\'didClickAddDepartment(' + editId + ')\'>添加子部门</button><button class=\'dropdown-item editStyle\' onclick=\'didClickRename(' + editId + ','+treeNode.tId+')\')\'>修改名称</button><button class=\'dropdown-item editStyle\'  onclick=\'didClickDeletedDepartment(' + editId + ','+name+')\'>删除</button>';
    }
    if ($('#diyBtn_' + treeNode.id).length > 0) return;
    var editStr = '<div class=\'dropdown\' id=\'dropdown\' onclick=\'didClickEdit(' + editId + ')\' >  <div class=\'editBtn\' id=\'diyBtn_' + treeNode.id + '\' title=\'' + treeNode.name + '\'  data-toggle=\'dropdown\' ><a class=\'icon icon-organiz-more\'></a><div class=\'selectedRow\'></div></div><div class=\'dropdown-menu dropdown-menu-right\' id=\'dropdown-menu\' aria-labelledby=\'diyBtn_' + treeNode.id + '\'   aria-haspopup=\'true\' aria-expanded=\'false\'>' + menu + '</div><div class=\'dimBgc\'></div>';
    aObj.after(editStr);
    //选中效果
    if(treeNode.level==0){
      selectedObj.addClass("on");
      var treeNodeId="#"+treeNode.tId+" "+".dimBgc:first";
      $(treeNodeId).hide();
    }
  };
  //初始化请求组织机构树的接口
  var arrM = new Array();
  getAddress(0);
  //获取组织机构信息的方法
  function getAddress(tag){
      getAddressList(pageObj.aid,function(data){
          if (data.ifSuc==1) {
              var result=data.data.sysOrganizations;
              var sysUser=data.data.sysUser.list;
              dataList = sysUser;
              userNumInfo =data.data.userNumInfo;
              setSpanList(userNumInfo);
              pageObj.total=userNumInfo.userTotalNum;
              var personnels=[];
              arrM=[];
              var rootName="";
              for (var i=0, l=result.length; i<l; i++){
                  if(result[i].parentId==0){
                      rootName=result[i].orgName;
                      pageObj.orgName=rootName;
                      arrM[i] = {id:result[i].id,pId:result[i].parentId,name:result[i].orgName,open:true};
                  }else {
                      arrM[i] = {id:result[i].id,pId:result[i].parentId,name:result[i].orgName};
                  }
              }
              for (var i = 0; i < sysUser.length; i++) {
                if(sysUser[i].smallPicture==""||sysUser[i].smallPicture==null){
                    sysUser[i].nophoto =1;
                }
                  personnels[i]=  {
                      "id": sysUser[i].id,
                      "name": sysUser[i].userName,
                      "phone": sysUser[i].phoneNumber,
                      "sector": sysUser[i].orgName,
                      "email": sysUser[i].email,
                      "jobs": sysUser[i].position,
                      "smallPicture":sysUser[i].smallPicture ,
                      "inPicture":sysUser[i].inPicture,
                      "isShow":sysUser[i].status==3? true:false,
                      "roleId":sysUser[i].roleId,
                      'roleName':sysUser[i].roleName,
                      'nophoto':sysUser[i].nophoto,
                      'status':sysUser[i].status
                  }
              }
              if(tag==0){
                  p.setCount(pageObj.total);
                  setMemberList({list:personnels});
                  $("span.title").text(rootName);
                  $.fn.zTree.init($("#treeDemo"), setting, arrM);
              }else if(tag==1){
                  p.setCount(pageObj.total);
                  setMemberList({list:personnels});
              }else if(tag==2){
                  if(arrM.length>0){
                      var node = zTreeObj.getNodeByParam("id",pageObj.aid,null);
                      var newNodes =  arrM;
                      zTreeObj.addNodes(node,0,newNodes);
                  }
                  // p.setCount(pageObj.total);
                  // setMemberList({list:personnels});
              }else if(tag==3){  //分页器触发的请求
                  setMemberList({list:personnels});
              }
          }
      });
  }
  //设置人员状态信息
  function setSpanList(info){
        $(".span-list-0 >i").text(info.userTotalNum);
        $(".span-list-1 >i").text(info.activatedNum);
        $(".span-list-2 >i").text(info.noActivatedNum);
        $(".span-list-3 >i").text(info.disableNum);
        $(".span-list-4 >i").text(info.noDistributionDepartmentNum);
        $('#dropdownMenu').text($('.select-active').text()); //初始化按钮的值
    }
//查询全部组织机构接口
  function getAddressList(orgId, callBack) {
        var dataObj={
            currentPage:pageObj.page,
            pageSize:pageObj.size,
            orgId:orgId,
        };
        fetchs.qyh_get('/getAddressListStructure',dataObj,function (data) {
            callBack(data);
        })
    }
  //创建组织机构树
   zTreeObj = $.fn.zTree.init($('#treeDemo'), setting, arrM);
  var treeObj = $.fn.zTree.getZTreeObj('treeDemo');
  //返回当前页和每页多少条数据
  function pageCallBack(currentPage, page) {
      pageObj.page = currentPage;
      pageObj.size = page;
      if(category==0){ //默认状态下触发区域分页
         getAddress(3);
      }else if(category==1){//搜索状态下触发的分页
          searchData(1);
      }else if(category==2||category==3){//获取状态下触发的分页
          getUsersByStatus(1);
      }
  }
  //表格数据加载
  function setMemberList(data) {
      if(data.list.length>=15&&$("#page-Box").css("display")=='block'){
          $(".directories .right .table-box .table-content").css("height","calc(100% - 56px)");
      }else{
          $(".directories .right .table-box .table-content").css("height","100%");
      }
    data.key=searchKey;
    $('.table-box .table-content').html(soda(addressTpl, data));

    $('.btn-box').hide();
    $('.table tr div#selectAll input[name=\'checkname\']').prop('checked', false);
    //搜索结果为空时显示
    if(data.list==undefined){
        if(category==1){
            $(".nothing").html("没有搜索到信息，换个条件试试？<br/>您可以输入人员名称、全拼、电话号码、邮箱等部分内容检索。");
        }
    }else{
        if(data.list.length==0&&category==1){
            $(".nothing").html("没有搜索到信息，换个条件试试？<br/>您可以输入人员名称、全拼、电话号码、邮箱等部分内容检索。");
        }
    }
  }
  //表格加载之后数据操作---------------------------------
  $.ajaxSettings.traditional = true;
  var _userInfo = JSON.parse(localStorage.getItem('userinfo'));
  var isShow = 0;
  var current_user_role='';
  //var isForbid = 0;//人员禁用
  //回车搜索
    $('#searchKepress').keypress(function (e) {
      if (e.keyCode==13) {
          pageObj.page = 1;
          pageObj.size = 15;
          searchKey=$('#searchKepress').val();
          if(searchKey==""){
              category=0; //取消搜索状态
          }else {
              category=1; //记录搜索状态
          }
          searchData(0);
          e.preventDefault();//阻止默认事件
          $(this).blur();
      };
    });
    $('.search-box input').bind('input propertychange', function() {
        if($(this).val().length>0){
            $(".icon-search-del").show();
        }else{
            $(".icon-search-del").hide();
        }
    });
    $('.search-box input').bind('focus',function () {
        $('.search-box').addClass('border-shadow');
        $(".icon-search-del").hide();
    })
    $('.search-box input').bind('blur',function () {
        $('.search-box').removeClass('border-shadow');
    })
    $(".icon-search-del").click(function () {
        $('.search-box input').val("");
        $(this).hide();
        pageObj.page = 1;
        pageObj.size = 15;
        searchKey=$('#searchKepress').val();
        category=0; //去下搜索状态
        searchData(0);
    });
    //全选效果
    $('body').on('click','#selectAll',function () {
        if ($(this).find('input').prop('checked')) {
            $('.table tr div input[name=\'checkname\']').prop('checked', true);
            $('.btn-box').show();
            $('.btn-box button').attr('disabled', true);
        } else {
            $('.table tr div input[name=\'checkname\']').prop('checked', false);
            $('.btn-box').hide();
        };
    });
    //表格的单个人员批量操作
    $('body .btn-box button').attr('disabled', true); //按钮禁用初始化
    $('body').on('click','.table tr div',function (event) {
        getUserRole();//获取当前用户的角色
        $('.table tbody tr').removeClass('trActive');
        savelist(function (result) {
            if (result.length <=0) {
                $('.btn-box').hide();
                $('body .btn-box button').attr('disabled', true);
            }else{
                if(result.length>=dataList.length){
                    $('.table tr div#selectAll input[name=\'checkname\']').prop('checked', true);
                }else{
                    $('.table tr div#selectAll input[name=\'checkname\']').prop('checked', false);
                }
                $('.btn-box').show();
                $('body .btn-box button').attr('disabled', false);
            };
            if (tag == 1) {
                $('.btn-box button:nth-child(2)').text('禁用');
            } else {
                $('.btn-box button:nth-child(2)').text('启用');
            }
            tag = 0;
        });
        $('#form-box').removeClass('form-box-show');
        stopBubble(event);
    });
    //批量禁用启用
    $('body').on('click','.btn-box button:nth-child(2)', function () {
        var forbidden_text = $(".btn-box button:nth-child(2)").text();
        var type;
        if (forbidden_text == "禁用") {
            type = 3;
            tag = 0;
        } else {
            type = 1;
            tag =1;
        }
        savelist(function (result,role_type,status_obj) {
            if(role_type==1){
                notify('danger','您没有此操作权限');
                role_type=0;
            }else{
                var removeList = { 'userIds': result, 'state': type ,'orgId':pageObj.aid};
                fetchs.post('/people/changepeoplestate', removeList, function (res) {
                    if (res.ifSuc == 1) {
                        if (forbidden_text == "禁用") {
                            operation("forbidden");
                            $(".btn-box button:nth-child(2)").text('启用');
                            // userNumInfo.activatedNum = userNumInfo.activatedNum-status_obj.activated ;//激活
                            // userNumInfo.noActivatedNum = userNumInfo.noActivatedNum-status_obj.noactive//未激活
                            // userNumInfo.noDistributionDepartmentNum = userNumInfo.noDistributionDepartmentNum -status_obj.unabsorbed;//未分配
                            // userNumInfo.disableNum = userNumInfo.disableNum+status_obj.activated+status_obj.noactive+status_obj.unabsorbed ;//禁用
                            // setSpanList(userNumInfo);
                        } else {
                            operation("startUsing");
                            $(".btn-box button:nth-child(2)").text('禁用');
                            // userNumInfo.activatedNum =userNumInfo.activatedNum + status_obj.activated ;//激活
                            // userNumInfo.noActivatedNum = userNumInfo.noActivatedNum + status_obj.noactive//未激活
                            // userNumInfo.noDistributionDepartmentNum = userNumInfo.noDistributionDepartmentNum + status_obj.unabsorbed;//未分配
                            // userNumInfo.disableNum =userNumInfo.disableNum-status_obj.activated-status_obj.noactive-status_obj.unabsorbed ;//禁用
                            // setSpanList(userNumInfo);
                        }
                        //批量禁用之后的条件刷选请求
                        setSpanList(res.data);
                    }else if(res.ifSuc == 2){
                        notify('danger','企业管理员不可禁用');
                    }else if(res.ifSuc == 3){
                        notify('danger',res.msg);
                    }else {
                        if(res.code==-1){
                            notify('danger','网络异常，请检查网络后重试');
                        }else{
                            notify('danger',res.msg);
                        }
                    }
                });
            }
            tag = 0;
        });
    });
    //批量移除
    $('body').on('click','#removeModal .remove-btn', function () {
        savelist(function (data,role_type) {
            if(data.length==0){
                data.push(currentTr.id);
            };
            fetchs.post('/people/removepeople', {'userIds': data }, function (result) {
                if (result.ifSuc == 1) {
                    //$('#removeModal').modal('show')
                    operation("removeMember");
                    notify('success','移除成功');
                    getAddress(1);
                    $('.btn-box').hide();
                    $('#selectAll input').prop('checked',false);
                } else if(result.ifSuc == 2){
                    notify('danger','企业管理员不可移除');
                }else if(result.ifSuc == 3){
                    notify('danger',result.msg);
                }else{
                    if(result.code==-1){
                        notify('danger','网络异常，请检查网络后重试');
                    }else{
                        notify('danger',result.msg);
                    }
                }
            });
        });
    });
    //批量移除的权限校验
    $('body').on('click','.btn-box button:nth-child(3)',function () {
        savelist(function (data,role_type) {
            if(role_type==1){
                notify('danger','您没有此操作权限');
                role_type=0;
            }else{
                $('#removeModal .modal-body').text('确定移除当前所选人员吗？');
                $('#removeModal').modal('show');
            }
        })
    })
    //单击个人显示右侧的表单
    var $current_tr;
    var currentOrgName;
    $('body').on('click','.table tbody tr',function (event) {
        $current_tr = $(this);
        $(this).siblings().removeClass('trActive');
        $(this).removeClass('trActive').addClass('trActive');
        $(".dropdown-box .dropdown-ul").hide();
        isShow = 0;
        var current_id = $(this).data('trList');
        $.each(dataList,function (item) {
            if($(this)[0].id==current_id){
                currentTr = $(this)[0];
                return currentTr;
            };
        });
        $('.savePersonInfo .form-group').removeClass('has-danger');
        $('.savePersonInfo .error-msg').empty();
        $('.imgUpload').hide();
        $('.addPersonInfo').hide();
        $('.savePersonInfo').show();//修改人员表单显示
        //修改人员表单赋值
        if(currentTr.nophoto==1){//.photo==null||currentTr.photo==''
            $('.person-logo').hide();
            var bg= $current_tr.find('td:nth-child(2) .nophoto').css('background-color');
            var name = $current_tr.find('td:nth-child(2) .nophoto').text();
            $('.img-message').before('<span class="form-nophoto person-logo" style="background-color:' +bg+'">'+name+'</span>');
        }else{
            $('.form-top span.form-nophoto').remove();
            $('.person-logo').show().attr('src',currentTr.bigPicture);
        }
        if(currentTr.roleName==undefined||currentTr.roleName==null||currentTr.roleName==''){
            $('.img-message p:first-child span:nth-child(2)').hide();
        }else{
            $('.img-message p:first-child span:nth-child(2)').html('<i class="icon icon-IDcard"></i>'+currentTr.roleName+'')
        };
        $('.person-name').text(currentTr.userName);
        $('.phone-number').text(currentTr.phoneNumber);
        $('#formGroupName').val(currentTr.userName);
        $('#formGroupEmail').val(currentTr.email);
        currentOrgName = currentTr.catalog.split('>');
        var bread = '';
        $.map(currentOrgName, function(v,i){
            if(currentOrgName.length>1){
                bread += '<span class="sign" data-toggle="tooltip" data-title="'+v+'">'+v.substring(5, 0)+'</span>';
            }else if(currentOrgName.length=1){
                bread += currentOrgName[0];
            }
        });
        $('body #addDepartmentChange .trigger').html(bread==''||bread==null? '部门':bread).attr({'label':currentTr.orgName,'name':currentTr.orgId});
        $('#formGroupSex').text(currentTr.sex!==1? '男':'女');
        var sex_state = currentTr.sex!==1? '男':'女';
        $(".savePersonInfo").find("a.dropdown-item").removeClass('select-bg-color');
        $(".savePersonInfo").find("a.dropdown-item[name='"+sex_state+"']").addClass('select-bg-color');
        $('#formGroupPosition').val(currentTr.position);
        var isBan = $(this).find('td:nth-child(2)').find('#forbidState').css('display');
        if (isBan == 'inline-block') {
            $('.img-message p:nth-child(3)').html('<span style="color:#f74555;">已禁用</span>');
            $('.more-btn .dropdown-list a:nth-child(1)').attr('name', '3');
        }else if(currentTr.status==2){
            $('.img-message p:nth-child(3)').html('<span style="color:#07bf5a;">未激活</span>');
            $('.more-btn .dropdown-list a:nth-child(1)').attr('name', '2');
        }else {
            $('.img-message p:nth-child(3)').html('<span style="color:#07bf5a;">已激活</span>');
            $('.more-btn .dropdown-list a:nth-child(1)').attr('name', '1');
        };
        $('.savePersonInfo .save-btn').attr('disabled', true);
        $('.savePersonInfo input.form-control').focus(function () {
            $('.savePersonInfo .save-btn').attr('disabled', false);
        });
        $('.savePersonInfo select').focus(function(){
            $('.savePersonInfo .save-btn').attr('disabled', false);
        })
        $('#form-box').addClass('form-box-show');
        //stopBubble(event);
    });
    $('body').on('shown.bs.personnel','form.savePersonInfo',function(){
        $('.savePersonInfo .save-btn').attr('disabled', false);
    })
    $('body').on('shown.bs.personnel','form.addPersonInfo',function(){
        $('.addPersonInfo .save-btn').attr('disabled', false);
    })
  function searchData(tag) {
      var searchObj={
          seachCondition:searchKey,
          parentId:pageObj.aid,
          currentPage:pageObj.page,
          pageSize:pageObj.size,
      };
    fetchs.qyh_get('/seachUserByCondition', searchObj ,function (data) {
        if (data.ifSuc==1) {
            if (data.data.sysUser!=null) {
                var sysUser = data.data.sysUser.list;
                dataList = sysUser;
                userNumInfo = data.data.userNumInfo;
                setSpanList(userNumInfo);
                $('#dropdownMenu').text($('.select-active').text()); //初始化按钮的值
                pageObj.total = userNumInfo.userTotalNum;
                if (tag == 0) { //初始化搜索分页器总数
                    p.setCount(pageObj.total);
                }
                var personnels = [];
                for (var i = 0; i < sysUser.length; i++) {
                    if (sysUser[i].smallPicture == "" || sysUser[i].smallPicture == null) {
                        sysUser[i].nophoto =1;
                    }
                    personnels[i] = {
                        "id": sysUser[i].id,
                        "name": sysUser[i].userName,
                        "phone": sysUser[i].phoneNumber,
                        "sector": sysUser[i].orgName,
                        "email": sysUser[i].email,
                        "jobs": sysUser[i].position,
                        "smallPicture": sysUser[i].smallPicture,
                        "inPicture":sysUser[i].inPicture,
                        "isShow": sysUser[i].status == 3 ? true : false,
                        "roleId":sysUser[i].roleId,
                        'roleName':sysUser[i].roleName,
                        'nophoto':sysUser[i].nophoto,
                        'status':sysUser[i].status
                    }
                }
            }else {
                userNumInfo = data.data.userNumInfo;
                setSpanList(userNumInfo);
                p.setCount(0);
            }
            setMemberList({list:personnels});
            $('.btn-box').hide();
            $('#selectAll input').prop('checked',false);
        }
    });
  }
  $('.top ul a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
    $('a[data-toggle="tab"]').removeClass('active');
    $(this).addClass('ul-tab').parent().siblings().find('a').removeClass('ul-tab');
    if ($(this).attr('name') == '批量导入') {
      fetchs.post('/getUserNum',{},function (result) {
          var userImportNum =  result.data.userMaixNum-result.data.userNowNum;//人员上限
          $('.user-max').text(result.data.userMaixNum);
          $('.user-min').text(userImportNum<=0? 0 :userImportNum);
          $('.up-limit').show();
          $('.file-name').hide();
          $('.repetition').hide();
          $('.import-table').hide();
          $('.file').html('上传文件<input id="upFile" onchange="againUp()" type="file" value="上传文件" name="myfile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,text/csv,application/vnd.ms-excel">');
          if(userImportNum<=0){
              $('body #upFile').attr('type','text');
              $('.file').css({'color':'#959595'});
          }else{
              $('body #upFile').attr('type','file');
              $('.file').css({'color':'#1896f0'});
          }
      });
    }
    if ($(this).attr('name') == '邀请人员') {
      fetchs.post('/people/invitepeople', {},function (result) {
        if (result.ifSuc == 1) {
            $tenantId = result.data.tenantId;
            $('.qr-code img').attr('src', result.data.qrcodePath);
            $('#codeText').val(result.data.invitationCode);
            $('.company-name').text(result.data.companyName);//公司名称显示
            $('.open-link-btn').text($("#wechatHost").val()+'/system/checkInviteCode?tenantId='+result.data.tenantId+'&code='+md5(result.data.invitationCode));
        } else {
            if(result.code==-1){
                notify('danger','网络异常，请检查网络后重试');
            }
        }
      });
    };
    if ($(this).attr('name') == '新增人员') {
      $(this).removeClass('ul-tab');
      $('.imgUpload').show();
      $('.addPersonInfo').show();
      $('.savePersonInfo').hide();
      var orgIdInit =$('#addDepartment input[name="organizer"]').val();
      addPersonLimit();
      $('#form-box').addClass('form-box-show');
    };
  });
    //新增人员上限
    var addDepartName;
    function addPersonLimit(){
       fetchs.post('/getUserNum',{'orgId':pageObj.aid},function (result) {
           if(result.ifSuc==1){
               var userImportNum =  result.data.userMaixNum-result.data.userNowNum;//人员上限
               if(userImportNum<=0){
                   $('#form-box').removeClass('form-box-show');
                   notify('danger','公司人员已达上限');
               }else{
                   if (isShow == 0) {
                       $('#form-box').addClass('form-box-show');
                   }
                   isShow = 1;
                   addDepartName = '';
                   addDepartName =result.data.catalog.split('>');
                   var bread = '';
                   $.map(addDepartName, function(v,i){
                       if(addDepartName.length>1){
                           bread += '<span class="sign" data-toggle="tooltip" data-title="'+v+'">'+v.substring(5, 0)+'</span>';
                       }else if(addDepartName.length=1){
                           bread += addDepartName[0];
                       }
                   });
                   $('#form-box').addClass('form-box-show');
                   $('.addPersonInfo #addDepartment .trigger').html(bread).attr({'name':pageObj.aid==''||pageObj.aid==null? '1':pageObj.aid,'label':addDepartName[addDepartName.length-1]});
               }
           }
        })
    }
    //无人员时操作添加按钮
    $('body').on('click','a.nothing-addperson',function(event){
        $('.savePersonInfo').hide();
        $('.imgUpload').show();
        $('.addPersonInfo').show();
        addPersonLimit();
        $('#form-box').addClass('form-box-show');
    })
  //新增人员的表单操作
  addPersonFormOperation();
  //------邀请人员操作开始----------
  $('#codeBtn').click(function (event) {// 刷新邀请码
      event.stopPropagation();
      fetchs.post('/setting/refreshinvitecode',{}, function (result) {
          if (result.ifSuc == 1) {
              $('#codeText').val(result.data.inviteCode);
              $('.open-link-btn').text($("#wechatHost").val()+'/system/checkInviteCode?tenantId='+$tenantId+'&code='+md5(result.data.inviteCode));
          } else {
              notify('danger','刷新邀请码失败，请重试');
          }
      });
  });
  //点击完成复制
  $('#copyBtn').click(function () {
    $('.textarea-text').empty();
    var link_box_text = $('.link-box-top').text()+$('.link-box-bottom').text();
    $('.textarea-text').text(link_box_text);
    var _node = '.textarea-text';
    copy(_node);
  });
  //------邀请人员操作结束------
  //下拉框点击效果
  $('#dropdownMenu').click(function (event) {
    var userInfo = JSON.parse(localStorage.getItem('userinfo'));
    stopBubble(event);
    $('.dropdown-ul').toggle();
  });
  $('body').bind("click", function (e) {
    var target = $(e.target);
    if (target.closest(".dropdown-box").length == 0) {
      $(".dropdown-box .dropdown-ul").hide();
    }
  });
 //区域下点击不同状态人员的事件
  $('.dropdown-ul').on('click', 'span', function () {
    $(this).addClass('select-active').siblings().removeClass('select-active');
    var type_val = $(this).text();
    $('#dropdownMenu').text(type_val);
    var state_name = $(this).attr('name');//人员筛选的状态码
     userStatus=state_name;
     if(category==1||category==3){
       category=3;
     }else {
       category=2;
     }
      pageObj.size=15;
      pageObj.page=1;
      p.resetpage();
     getUsersByStatus(0);
  });
  //根据人员状态获取人员的接口
  function getUsersByStatus(tag){
    var stateObj={};
    if (category==3){ //搜索状态下点击人员状态操作
        stateObj ={
            userStatus:userStatus,
            orgId:pageObj.aid,
            currentPage:pageObj.page,
            pageSize:pageObj.size,
            seachCondition:searchKey,
        }
    }else {
        stateObj={
            userStatus:userStatus,
            orgId:pageObj.aid,
            currentPage:pageObj.page,
            pageSize:pageObj.size,
        }
    }
    // var findNum=".span-list-"+userStatus+" >i";
    // var total = parseInt($(findNum).text());
      fetchs.qyh_get('/getUsersByStatus',stateObj,function (data) {
          if (data.ifSuc==1) {
              var personnels = [];
              if(data.data.sysUser!=null){
                  var sysUser=data.data.sysUser.list;
                  dataList = sysUser;
                  pageObj.total=data.data.userNumInfo.userTotalNum;
                  //拿到总数量
                  if(tag==0){//设置分页
                      p.setCount(pageObj.total);
                  }
                  for (var i = 0; i < sysUser.length; i++) {
                      if (sysUser[i].smallPicture == "" || sysUser[i].smallPicture == null) {
                          sysUser[i].nophoto =1;
                      }
                      personnels[i] = {
                          "id": sysUser[i].id,
                          "name": sysUser[i].userName,
                          "phone": sysUser[i].phoneNumber,
                          "sector": sysUser[i].orgName,
                          "email": sysUser[i].email,
                          "jobs": sysUser[i].position,
                          "smallPicture": sysUser[i].smallPicture,
                          "inPicture":sysUser[i].inPicture,
                          "isShow": sysUser[i].status==3? true:false,
                          "roleId":sysUser[i].roleId,
                          'roleName':sysUser[i].roleName,
                          'nophoto':sysUser[i].nophoto,
                          'status':sysUser[i].status
                      }
                  }
              }else {
                  pageObj.total=0;
                  if(tag==0){//设置分页
                      p.setCount(pageObj.total);
                  }
              }
              setMemberList({list:personnels});
              $('.btn-box').hide();
              $('#selectAll input').prop('checked',false);
          }
      })
  }
  var tag = 0; //本地操作标识
  //获取表格数据
  var dataList = []; //所有的数据
  var currentTr = {}; //选中单个tr的数据
  soda.prefix('ng-');
  //表格结构初始化
  var addressTpl = '<table class="table table-hover list-personnels"><tbody name="1">' +
                  '<tr ng-repeat="item in list" data-tr-list="{{item.id}}"  data-tr-status="{{item.status}}" data-tr-role="{{item.roleId}}"  data-tr-show="{{item.isShow}}">' +
                  '<td><div class="custom-control custom-checkbox"><input class="custom-control-input" type="checkbox" name="checkname"><span class="custom-control-indicator"></span></div></td>' +
                  '<td data-toggle="tooltip" data-title="{{item.name}}"><span ng-if="item.nophoto" class="nophoto {{item.name|charcode}}">{{item.name|slice:-2}}</span><img ng-if="item.smallPicture" src="{{item.smallPicture}}"><span ng-html="item.name|keylight:key"></span><span id="forbidState" class="ban{{item.isShow}}">(\u7981\u7528)</span></td>' +
                  '<td data-toggle="tooltip" data-title="{{item.phone}}"><span ng-html="item.phone|keylight:key"></span></td>' +
                  '<td data-toggle="tooltip" data-title="{{item.sector}}"><span ng-if="item.sector">{{item.sector}}</span></td>' +
                  '<td data-toggle="tooltip" data-title="{{item.email}}"><span ng-html="item.email|keylight:key"></span></td>' +
                  '<td data-id="5" data-toggle="tooltip" data-title="{{item.jobs}}"><span ng-if="item.jobs">{{item.jobs}}</span></td></tr></tbody>' +
                  '</table>' +
                  '<div ng-if="list.length<1" class="nothing">部门暂时没有人员，您可以<a class="nothing-addperson" href="#" name="2">新增人员</a></div>';
  //获取表格数据
  $('a.closed[data-toggle="tab"]').click(function(){
      $('.top ul a[data-toggle="tab"]').removeClass('ul-tab');
      $('.import-table').hide();
      $('.repetition').hide();
      $('.file-name').hide();
  })
  //修改个人信息的表单
  personInfoFormOperation();
  //修改人员信息表单操作
    $('body').click(function (e) {
        if(e.target != $('#form-box')&& $('#form-box').find(e.target).length < 1){
            if ($(e.target).parents('tbody').attr('name')== 1&&$(e.target).parents('tbody').length==1) {// && $('#form-box').find(e.target).length < 1
                return false;
            };
            if($(e.target).attr('name')==2){
                return false;
            };
            $('#form-box').removeClass('form-box-show');
        }
    });
  function personInfoFormOperation() {
    $('#form-box').click(function (event) {
      stopBubble(event);
      $(".more-btn .dropdown-list").hide();
    });
    //表单按钮阻止事件冒泡
    $('.form-top #dropdownMenu1').click(function (event) {
      getUserRole();//获取当前用户的角色
      $('.dropdown-list').toggle();
        if($('.more-btn .dropdown-list').css('display')=='block'){
            $('#dropdownMenu1').addClass('toggle-color');
            $('#dropdownMenu1 i').addClass('toggle-color');
        }else{
            $('#dropdownMenu1').removeClass('toggle-color');
            $('#dropdownMenu1 i').removeClass('toggle-color');
        }
        stopBubble(event);
      var isBan = $('.img-message p:nth-child(3) span').text();
      switch (isBan) {
        case '已禁用':
          $('.more-btn .dropdown-list a:nth-child(1)').html('<i class="icon icon-lock"></i><span>启用</span>');
          break;
        case '已激活':
          $('.more-btn .dropdown-list a:nth-child(1)').html('<i class="icon icon-lock"></i><span>禁用</span>');
          break;
        case '未激活':
          $('.more-btn .dropdown-list a:nth-child(1)').html('<i class="icon icon-lock"></i><span>禁用</span>');
          break;
        default:
          return true;
      };
       $('.savePersonInfo .save-btn').attr('disabled',false);
    });
    $('body').bind("click", function (e) {
      var target = $(e.target);
      if (target.closest(".more-btn").length == 0) {
        $(".more-btn .dropdown-list").hide();
      }
    });
    $('.dropdown-list').on('click', 'a', function () {
      $('.dropdown-list').hide();
    });
    //保存个人信息表单提交
    var banType;//禁用、启用、删除的状态值
    //单个人员的禁用
    $('.more-btn .dropdown-list a:nth-child(1)').click(function () {
        $('#dropdownMenu1').removeClass('toggle-color');
        $('#dropdownMenu1 i').removeClass('toggle-color');
        var forbidden_text = $(this).find('span').text();
        if ($(this).find('span').text() == '启用') {
          $(this).attr('name', '1');
          banType = 1;
        } else {
          $(this).attr('name', '3');
          banType = 3;
        }
        if(current_user_role>=currentTr.roleId){
            notify('danger','您没有此操作权限');
        }else{
            fetchs.post('/people/changepeoplestate', {'userIds':currentTr.id,'state':banType}, function (result) {
                    if (result.ifSuc == 1) {
                        if (forbidden_text == "禁用") {
                            operation("forbidden");
                            $('.img-message p:nth-child(3)').html('<span style="color:#f74555;">已禁用</span>');
                        } else {
                            operation("startUsing");
                            $('.img-message p:nth-child(3)').html('<span style="color:#07bf5a;">已激活</span>');
                        }
                        getAddress(3);
                    }else if(result.ifSuc == 2){
                        notify('danger','企业管理员不可禁用');
                    }else if(result.ifSuc == 3){
                        notify('danger',result.msg);
                    }else {
                        if(result.code==-1){
                            notify('danger','网络异常，请检查网络后重试');
                        }else{
                            notify('danger',result.msg);
                        }
                    }
                });
        }
    });
    //单个人员的删除
    $('.dropdown-list a:nth-child(2)').click(function(){
        $('#dropdownMenu1').removeClass('toggle-color');
        $('#dropdownMenu1 i').removeClass('toggle-color');
        if(current_user_role>=currentTr.roleId){//用户的角色id>当前选中的人员角色id
            notify('danger','您没有此操作权限');
        }else{
            $('#form-box').removeClass('form-box-show');
            $('#removeModal .modal-body').text('确定移除当前人员吗？');
            $('#removeModal').modal('show');
        }
    });
      //新增人员组件调用
      $("#addDepartment").persons({//人员组件调用
          class:'person',
          type:3,
          orgId:1,
          container:'#addDepartment',
          hierarchies:true,//人员组件层级结构返回
          template:'<ul class="list groups"><li class="" ng-repeat="item in sysOrganizations" data-id="{{item.id}}" data-org-name="{{item.orgName}}"><p data-leaf="{{item.leaf}}"><span class="icon icon-organiz-unit"></span><span class="name" ng-html="item.orgName|keylight:key"></span></p></li></ul>',
      });
      //修改人员信息 组件调用
      $("#addDepartmentChange").persons({//人员组件调用
          class:'person',
          type:3,
          orgId:1,
          container:'#addDepartmentChange',
          hierarchies:true,//人员组件层级结构返回
          template:'<ul class="list groups"><li class="" ng-repeat="item in sysOrganizations" data-id="{{item.id}}" data-org-name="{{item.orgName}}"><p data-leaf="{{item.leaf}}"><span class="icon icon-organiz-unit"></span><span class="name" ng-html="item.orgName|keylight:key"></span></p></li></ul>',
      });
      //修改部门
      $("#addDepartmentRecompose").persons({//人员组件调用
          class:'person',
          type:3,
          orgId:1,
          container:'#addDepartmentRecompose',
          hierarchies:true,//人员组件层级结构返回
          template:'<ul class="list groups"><li class="" ng-repeat="item in sysOrganizations" data-id="{{item.id}}" data-org-name="{{item.orgName}}"><p data-leaf="{{item.leaf}}"><span class="icon icon-organiz-unit"></span><span class="name" ng-html="item.orgName|keylight:key"></span></p></li></ul>',
      });
      //人员组件隐藏取值
      $('#addDepartmentChange').on('hidden.bs.persons',function(obg){
          $('.savePersonInfo .save-btn').attr('disabled', false);
      });
      //取消选择
      $('#addDepartmentChange,#addDepartment').on('unselect.bs.persons',function(obg){
          if(currentOrgName){
              $("#addDepartmentChange .trigger").html(currentOrgName[0]).attr({"name":'1','label':currentOrgName[0]});
          }
          if(addDepartName){
              $("#addDepartment .trigger").html(addDepartName[0]).attr({"name":'1','label':addDepartName[0]});
          };
      });
      $('#addDepartmentRecompose').on('unselect.bs.persons',function(obg){
          $("#addDepartmentRecompose .trigger").html('请选择部门').attr("name",'');
      })

      $("#addDepartmentChange,#addDepartment").on('selected.bs.persons',function (obj) {
          var bread = '';
          var currentBread;
          var selected_id  = obj.selected[0].id;
          $.each(obj.dataList,function (index,item) {
              if(item.id==selected_id){
                  currentBread = item.departmentLevel;
                  return currentBread;
              };
          });
          var companyName =$('#leftMenuTop h4').text();
          $.map(currentBread, function(v,i){
              if(i==0){
                  bread = '<span class="sign" data-toggle="tooltip" data-title="'+companyName+'">'+companyName.substring(5, 0)+'</span>';
              }else{
                  bread += '<span class="sign" data-toggle="tooltip" data-title="'+v.orgName+'">'+v.orgName.substring(5, 0)+'</span>';
              }
          });
          bread+='<span class="sign" data-toggle="tooltip" data-title="'+obj.selected[0].orgName+'">'+String(obj.selected[0].orgName).substring(5, 0)+'</span>';
          $("#addDepartmentChange .trigger,#addDepartment .trigger").html(bread).attr({'name':obj.selected[0].id,'label':obj.selected[0].orgName}).addClass('label-color');
      })
      $("#addDepartmentRecompose").on('selected.bs.persons',function (obj) {
          var bread = '';
          var currentBread;
          var companyName =$('#leftMenuTop h4').text();
          var selected_id  = obj.selected[0].id;
          $.each(obj.dataList,function (index,item) {
              if(item.id==selected_id){
                  currentBread = item.departmentLevel;
                  return currentBread;
              };
          });
          $.map(currentBread, function(v,i){
              if(i==0){
                  bread = '<span class="sign" data-toggle="tooltip" data-title="'+companyName+'">'+companyName.substring(5, 0)+'</span>';
              }else{
                  bread += '<span class="sign" data-toggle="tooltip" data-title="'+v.orgName+'">'+v.orgName.substring(5, 0)+'</span>';
              }
          });
          bread+='<span class="sign" data-toggle="tooltip" data-title="'+obj.selected[0].orgName+'">'+String(obj.selected[0].orgName).substring(5, 0)+'</span>';
          $("#addDepartmentRecompose .trigger").html(bread).attr({'name':obj.selected[0].id,'label':obj.selected[0].orgName}).addClass('label-color');
      })
    $('.savePersonInfo').on('validator', function (event, data) {
      var orgid = $('#addDepartmentChange .trigger').attr("name");
      var department = $('#addDepartmentChange .trigger').attr('label');
      var $sex =$('#formGroupSex').text();
      var person_new_info = {
        'id': currentTr.id,
        'sex': $sex !== '女' ? 0 : 1,
        'position': data.position,
        'email': data.email,
        'name': data.name,
        'phone':currentTr.phoneNumber,
        'orgId': orgid==''? currentTr.orgId:orgid,
        'department': department
      };
      //修改人员表单提交
      fetchs.post('/people/updatepeople', person_new_info, function (result) {
          if(result.ifSuc==1){
              $('#form-box').removeClass('form-box-show');
              notify('success','保存成功');
              searchKey=$('#searchKepress').val();
              if(searchKey==""){
                  category=0; //取消搜索状态
                  getAddress(1);
              }else {
                  category=1; //记录搜索状态
                  searchData(0);
              }
              var userInfo = JSON.parse(localStorage.getItem('userinfo'));
              if(person_new_info.id==userInfo.userId){
                  setTimeout(function(){
                      window.location.reload();
                  },3000)
              }
          }else{
              if(result.code==-1){
                  notify('danger','网络异常，请检查网络后重试');
              }else{
                  notify('danger',result.msg);
              }
          }
      });
    });
  }
  //人员表单操作
  function addPersonFormOperation() {
    $('#form-box').click(function (event) {
      stopBubble(event);
    });
   /* var file;//上传文件的信息
    var imgFile = '';//头像的流
    var blob;
    var reader;*/
    /*$('.btn-change-photo input').on('change', function () {
      var formData = new FormData($('.imgUpload')[0]);
      file = $(this).get(0).files[0];
      if (file) {
          var filepath = $(".btn-change-photo input[name='fileName']").val();
          var extStart = filepath.lastIndexOf(".");
          var ext = filepath.substring(extStart, filepath.length).toUpperCase();
          if(ext != ".BMP" && ext != ".PNG" && ext != ".JPEG" && ext != ".JPG"){
              notify('danger','仅支持jpg、jpeg、png、bmp格式');
          }else{
              if (file.size > 5*1024*1024) {
                  notify('danger','上传的文件大小不能超过5M');
                  return false;
              }else{
                  $('.img-init').empty();
                  blob = URL.createObjectURL(file);//将本地图片的路径转成可用的路径
                  $('.img-init').append('<img src="' + blob + '">');
                  reader = new FileReader();
                  reader.readAsDataURL(file);
                  reader.onload = function (e) {
                      imgFile = file;
                  };
              };
          }
      }else if (file == undefined) {
         $('.img-init').empty();
          if(blob==""){
              $('.img-init').text('选择头像');
          }else{
              $('.img-init').append('<img src="' + blob + '">');
          }
      }else{
         $('.img-init').text('选择头像');
      }
    });*/
    //取消清空表单
    $('.cancel-btn').click(function () {
      //file = '';
      //$('#form-box form.imgUpload .img-init').text('选择头像');
      $('#form-box form.addPersonInfo')[0].reset();
      $('#form-box form.addPersonInfo .error-msg').empty();
      $('#form-box form.addPersonInfo .form-group').removeClass('has-danger');
      $('#form-box').removeClass('form-box-show');
    });
    //保存个人信息表单提交
    $('.addPersonInfo').on('validator', function (event, data) {
      var files = $('.btn-change-photo input').attr('name');
      var orgname = $('#addDepartment .trigger').attr('label');
      var add_sex=$('#addPersonSex').text();
        var new_user_info = {
            'name':data.name,
            'phone':data.phone,
            'email':data.email,
            'position':data.position,
            'sex':add_sex !== '女'? 0:1,
            'orgId':$('#addDepartment .trigger').attr('name'),
            'department':orgname,
            //'fileName':logobase64==undefined||logobase64==''? '':logobase64,
            'file':''//imgFile
        };
     /* if(file==undefined||file ==''){
          new_user_info.img = '';
          new_user_info.size = '';
      }else{
          new_user_info.img = file.name;
          new_user_info.size = file.size;
      };*/
      fetchs.uploadMixture('/people/addpeopleModify',new_user_info,function (result) {
          if(result.ifSuc==1){
              $('.addPersonInfo')[0].reset(); //添加成功后表单重置
             // $('.img-init').empty();
             // $('.img-init').text('选择头像');
             /// imgFile ='';
              //blob ='';
              //file = '';
              $('.addPersonInfo .error-msg').empty();
              notify('success','保存成功')
              getAddress(1);
          }else if(result.ifSuc==0){
              if (result.code==6014){
                  $('.addPersonInfo .form-group[name="phone"]').addClass('has-danger');
                  $('.addPersonInfo').find('p.error-msg').html('<i class="icon icon-error"></i>手机号已注册');
              }else if(result.code==-1){
                  $('.addPersonInfo').find('p.error-msg').html('<i class="icon icon-error"></i>网络异常，请检查网络后重试')
              }else if(result.code==6012){
                  $('.addPersonInfo').find('p.error-msg').html('<i class="icon icon-error"></i>'+result.msg+'');
              }else{
                  $('.addPersonInfo').find('p.error-msg').html('<i class="icon icon-error"></i>'+result.msg+'');
              };
          }else{
              $('.addPersonInfo').find('p.error-msg').html('<i class="icon icon-error"></i>网络异常');
          }
      })
    });
  }
    //新增人员的人员组件隐藏
    $('.personnel-stop-propagation').click(function (e) {
        e.stopPropagation();
    })
    $('.btn-box button:nth-child(1)').click(function () {
        $("#addDepartmentRecompose .trigger").html('请选择部门').attr('name','').removeClass('label-color');
    })
    //批量修改部门
    $('#reviseModal .modal-footer button:last-child').on('click', function (){
        var orgname = $('#addDepartmentRecompose .trigger').attr('label');
        var orgid = $('#addDepartmentRecompose .trigger').attr('name');
        if(orgid==''||orgid==null){
            $('#reviseModal').modal('hide');
            return false;
        }
        savelist(function (data) {
            fetchs.post('/people/movepeople', {'userIds': data ,'orgId':orgid,'orgName':orgname}, function (result) {
                if (result.ifSuc == 1) {
                    $('#reviseModal').modal('hide');
                    notify('success','修改部门成功');
                    getAddress(1);
                    $('.btn-box').hide();
                    $('#selectAll input').prop('checked',false);
                }else if(result.ifSuc == 3){
                    notify('danger',result.msg);
                }else {
                    $('#reviseModal').modal('hide');
                    if(result.code==-1){
                        notify('danger','网络异常，请检查网络后重试');
                    }else{
                        notify('danger',result.msg);
                    }
                }
            });
        })
    })
    //表单提交的提交事件和输入框点击事件验证初始化
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
    //表单的性别选择
    $('.sex-btn').on('click',function (event) {
        stopBubble(event);
        $(this).parent().find('.start-select-box').toggle();
    })
    $('.start-select-box').on('click','a',function () {
        $(this).parent().hide();
        $(this).addClass('select-bg-color').siblings().removeClass('select-bg-color');;
        $(this).parent().parent().find('.form-control>span').text($(this).text());
        $(this).parent().parent().find('.form-control .hidden-val').val($(this).text());
        $('.savePersonInfo .save-btn').attr('disabled', false);
    })
    $('.wrapper,form').bind("click", function (e) {
        var target = $(e.target);
        if (target.closest(".start-select-box").length == 0) {
            $(".start-select-box").hide();
        }
    });
  //阻止冒泡函数
  function stopBubble(e) {
    if (e && e.stopPropagation) {
      e.stopPropagation(); //w3c
    } else window.event.cancelBubble = true; //IE
  };
  //复制功能函数
  function copy(textarea_node) {
    var copyText = $(textarea_node).select();
    document.execCommand('Copy');
    notify('success','复制成功');
  };
  //获取当前用户的角色
  function getUserRole() {
      fetchs.get('/getCurrentUserRoleId?token='+fetchs.token,function (result) {
          if(result!=''||result!=undefined||result!=null){
              current_user_role = result;
          }
      });
  }
  //获取人员的自定义id与禁用按钮状态存入数组
  function savelist(callBack) {
    var trtList = $('.table tbody tr');
    var idList = [];
    // var activated = [];//已激活的状态
    // var noactive=[]; //未激活的状态
    // var banlist=[]; //禁用
    // var unabsorbed = [];//未分配部门
    var role_type=0;//有权限
    $.each(trtList, function (index, ele) {
      var _checked = $(this).find('div input[name=\'checkname\']').prop('checked');
      var urser_id;
      var user_role;
      var user_status;
      if (_checked) {
        var state = $(this).find('td:nth-child(2) span#forbidState').css('display');
        urser_id = $(this).data('trList');
        user_role = $(this).data('trRole');//当前选中的用户角色
        user_status = $(this).data('trStatus');
        if (state == 'none') {
          tag = 1;
        };
        idList.push(urser_id);
        // switch(user_status){
        //     case 1:
        //         return activated.push(user_status);
        //         break;
        //     case 2:
        //         return noactive.push(user_status);
        //         break;
        //     case 3:
        //         return banlist.push(user_status);
        //         break;
        //     case 4:
        //         return unabsorbed.push(user_status);
        //         break;
        //     default:
        //         return true;
        // }
        if(current_user_role>=user_role){
            role_type = 1;//无权限
        };
      };
    });
    /*var tr_status = {
        'activated':activated.length,
        'noactive':noactive.length,
        'banlist':banlist.length,
        'unabsorbed':unabsorbed.length
    };*/
    callBack(idList,role_type);//,tr_status
  };
  //负责本地批量操作
  function operation(isTag,num) {
    var trtList = $('.table tbody tr');
    $.each(trtList, function (index, ele) {
      var _checked = $(this).find('div input[name=\'checkname\']').prop('checked');
      if (_checked) {
        if (isTag == "forbidden") {
          //点击了禁用按钮
          tag = 0;
          $(this).find('td:nth-child(2) span#forbidState').css('display', 'inline-block');
          $(this).find('td:nth-child(2) span#forbidState').removeClass('banfalse');
          $(this).find('td:nth-child(2) span#forbidState').addClass('bantrue');
        } else if (isTag == "startUsing") {
          //点击了启用按钮
          tag = 0;
          $(this).find('td:nth-child(2) span#forbidState').css('display', 'none');
        } else if (isTag == "removeMember") {
          //点击了移除按钮
          $(this).remove();
        }
      };
    });
  }
  //表格的移除省略号提示语效果
  $('body').on('mouseenter', '[data-toggle="tooltip"]', function (e) {
        var text = $(this).data('title');
        var id = $(this).data('id');
        var size = parseInt($(this).css('fontSize'));
        if ($(this).width() < size * text.length) {
            $(this).tooltip({
                placement: function placement(tip, element) {
                    if (id == '6'||id=='5') {
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
//上传文件
function againUp() {
    var filepath = $(".file input#upFile[name='myfile']").val();
    var extStart = filepath.lastIndexOf(".");
    var ext = filepath.substring(extStart, filepath.length).toUpperCase();
    if(ext != ".CSV" && ext != ".XLSX" && ext != ".XLS"){
        notify('danger','文件格式错误，请下载标准模板');
        $('.file').html('上传文件<input id="upFile" onchange="againUp()" type="file" value="上传文件" name="myfile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,text/csv,application/vnd.ms-excel">');
    }else{
        var $name = $('#upFile').attr('name');
        $('.import-table').hide();
        $('.table-loading').fadeIn(1500);
        setTimeout(function () {
            $('.table-loading').fadeOut(1500, function () {
                getUpData($name);
            });
        }, 2000);
    }
}
var fileData = '<table class="table table-hover list-personnels"><tbody>' +
                '<tr ng-repeat="item in data">' +
                    '<td data-toggle="tooltip" data-title="{{item.userNum}}"><span ng-if="item.userNum">{{item.userNum}}</span></td>' +
                    '<td data-toggle="tooltip" data-title="{{item.depar}}"><span ng-if="item.depar">{{item.depar}}</span></td>' +
                    '<td data-toggle="tooltip" data-title="{{item.position}}"><span ng-if="item.position">{{item.position}}</span></td>' +
                    '<td data-toggle="tooltip" data-title="{{item.name}}"><span ng-if="item.name">{{item.name}}</span></td>' +
                    '<td data-toggle="tooltip" data-title="{{item.sex}}"><span ng-if="item.sex">{{item.sex}}</span></td>' +
                    '<td data-toggle="tooltip" data-title="{{item.phoneNum}}"><span ng-if="item.phoneNum">{{item.phoneNum}}</span></td>' +
                    '<td data-toggle="tooltip" data-id="6" data-title="{{item.email}}"><span ng-if="item.email">{{item.email}}</span></td>' +
                '</tr>' +
                '</tbody></table>';
//获取上传文件的数据
var isCover ='2'; //默认导入的数据覆盖
var import_list='';//需要导入的数据
function getUpData($name) {
  fetchs.fileupload('/importPreviewExcel','0','#myfile',function(result){
      var repetition = regrRepetition(result.data); //上传是否有重复的手机号 1表示有 0表示没有
      var personLimit = $('.user-min').text();//人员上限
      if (result.ifSuc == 0) {
          notify('danger', '文件格式错误，请下载标准模板');
          $('.file').html('上传文件<input id="upFile" onchange="againUp()" type="file" value="上传文件" name="myfile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,text/csv,application/vnd.ms-excel">');
      } else {
          if (repetition.data.length < 1) {
              if(repetition.empty==1){
                  notify('danger','上传数据必填项不能为空');
              }else{
                  notify('danger', '文件为空，请重新填写后上传');
              };
              $('.file').html('上传文件<input id="upFile" onchange="againUp()" type="file" value="上传文件" name="myfile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,text/csv,application/vnd.ms-excel">');
          } else if (repetition.data.length > personLimit) {
              notify('danger', '大于导入的最大上限，请重新填写后上传');
              $('.file').html('上传文件<input id="upFile" onchange="againUp()" type="file" value="上传文件" name="myfile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,text/csv,application/vnd.ms-excel">');
          }else{
              if(repetition.empty==1){
                  notify('danger','上传数据必填项不能为空');
                  $('.file').html('上传文件<input id="upFile" onchange="againUp()" type="file" value="上传文件" name="myfile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,text/csv,application/vnd.ms-excel">');
              }else{
                  if(repetition.state==1){
                      notify('danger','上传数据的手机号有重复');
                      $('.file').html('上传文件<input id="upFile" onchange="againUp()" type="file" value="上传文件" name="myfile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,text/csv,application/vnd.ms-excel">');
                  }else if(repetition.state==0){
                      import_list = repetition.data;
                      $('.import-table .table-con').empty();
                      var flie_name = document.querySelector('#upFile').files['0'].name;
                      $('.import-table').show();
                      $('.up-limit').hide();
                      $('.repetition').show();
                      $('.file-name').show();
                      $('.repetition .custom-control-input').prop('checked',true);
                      $('.file-name i:nth-child(1)').text(''+ flie_name + '');
                      $('.file-name i:nth-child(2)').text('(共' + repetition.data.length + '条)');
                      $('.file').html('重新上传<input id="upFile" onchange="againUp()" type="file" value="上传文件" name="myfile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,text/csv,application/vnd.ms-excel">');
                      $('.file span').text('重新上传');
                      $(".import-table .table-con").html(soda(fileData, repetition));
                  }
              }
          }
      }
      //上传是否覆盖的状态
      $(".repetition span").click(function () {
          var isCoverState = $(this).find("input[name='checkname']").prop("checked");
          if (isCoverState) {
              isCover ='2';//覆盖
          } else {
              isCover ='1';//不覆盖
          }
      });
  })
};
//开始导入
//result.data.selctStatus = isCover
$('body').on('click','.repetition button.import-btn',function () {
    fetchs.fileimport('/importExcel',isCover,import_list,function(res){
        if(res.ifSuc==1){
            $('#loadModal').modal('show');
            setTimeout(function(){
                $('#loadModal').modal('hide');
            },1500)
            setTimeout(function(){
                notify('success','导入成功');
            },3000);
            setTimeout(function(){
                window.location.reload();
            },5000)
        }else if(res.ifSuc==0){
            notify('danger',res.msg);
        }else{
            notify('danger','res.msg');
        }
    })
})
//下载模板
$('#myfile a.template-link').attr('href','/upload/ downloadTemplate?token='+fetchs.token+'')
//上传手机号查重
function regrRepetition(filephone) {
  var obj = {};
  var arr= [];
  var file_state = {};
  file_state.state=1;//默认手机号不重复
  file_state.empty=1;//默认为空
  file_state.data=arr;
  $.each(filephone, function (i, el) {
      var file_depar = $(this)[0].depar;
      var file_name = $(this)[0].name;
      var file_phone = $(this)[0].phoneNum;
      var file_userNum = $(this)[0].userNum;
      var file_sex = $(this)[0].sex;
      var file_position = $(this)[0].position;
      var file_email = $(this)[0].email;
      if(file_depar!==''&&file_depar!==null&&file_depar!=='null'){
          if(file_name!==''&&file_name!==null&&file_name!=='null'){
              if(file_phone!==''&&file_phone!==null&&file_phone!=='null'){
                  if (!obj[$(this)[0].phoneNum]) {
                      obj[$(this)[0].phoneNum] = true;
                      arr.push($(this)[0]);
                      file_state.state = 0; //没有重复
                  } else {
                      file_state.state = 1; //重复
                  }
                  file_state.empty =0;//不为空
              }else{
                  if(file_depar==null&&file_name==null&&file_phone==null&&file_position==null&&file_email==null&&file_sex==null&&file_userNum==null){
                      file_state.empty =0;
                  }else{
                      file_state.empty=1;//必填项有空值
                      return false;
                  }
              }
          }else{
              file_state.empty=1;//必填项有空值
              return false;
          }
      }else{
          if(file_depar==null&&file_name==null&&file_phone==null&&file_position==null&&file_email==null&&file_sex==null&&file_userNum==null){
              file_state.empty =0;
          }else{
              file_state.empty=1;//必填项有空值
              return false;
          }
      }
  });
  return file_state;
}
//表单提交验证
function rule(type, val) {
  switch (type) {
    case 'required':
      return val.trim() == '' ? '不能为空' : true;
      break;
    case 'department':
      return val.trim() == '' ? '部门不能为空' : true;
      break;
    case 'phone':
      return val.trim() == '' ? '手机号码不能为空' : !isPhone(val) ? '手机号码格式不正确' : true;
      break;
    case 'email':
      return val.trim() == '' ? '邮箱不能为空' : !isEmail(val)? '邮箱格式不正确' : true;
      break;
    case 'name':
      return val.trim() == '' ? '姓名不能为空' : !isName(val)? '名字格式不正确' : true;
    default:
      return true;
  }
}
//表单验证的提示信息
function validate($el) {
  var val = $el.val();
  var type = $el.data('validate');
  var msg = rule(type, val);
  if (msg != true) {
    $el.parent('.form-group').addClass('has-danger');
    $el.parents('form').find('p.error-msg').html('<i class="icon icon-error"></i>'+msg);
    return false;
  } else {
    $el.parent('.form-group').removeClass('has-danger');
    $el.parents('form').find('p.error-msg').text('');
    return true;
  }
}
//------------------------ztree增删改事件和对应的接口------------------------------  
//点击了编辑按钮
function didClickEdit(editId) {
}

//重命名
function didClickRename(editId,tId) {
  var node = zTreeObj.getNodeByParam('id', editId, null); //根据新的id找到新添加的节点

  var treeNodeId="#"+tId.id+" "+".dimBgc:first";
  $(treeNodeId).hide();

  oldName=node.name;
  zTreeObj.editName(node);
  document.getElementsByClassName("rename")[0].maxLength=20;
}
//添加子机构
function didClickAddDepartment(editId,tID) {
  //添加部分的同时,让部分处于可编辑状态
  var c =new Date().getTime();
  var node = zTreeObj.getNodeByParam('id', editId, null);
  // 判断当前父级节点有没有子节点,有的话先展开,后添加新节点
  // if (!node.isParent || node.children) {
    //如果有子节点,就不再次请求接口 就不触发onclick事件
    //直接添加部门
    var name="新建部门";
    if(node.isParent==true){
        var length= node.children.length+1;
        name ="新建部门"+length;
    }else {
        name ="新建部门";
    }
    var newNode = { id: c, name: name };
    zTreeObj.addNodes(node, -1, newNode);
    var node1 = zTreeObj.getNodeByParam('id', c, null); //根据新的id找到新添加的节点
    var treeNodeId="#"+node1.tId+" "+".dimBgc:first";
    $(treeNodeId).hide();
    zTreeObj.editName(node1);
    renameTag = 1;
    document.getElementsByClassName("rename")[0].maxLength=20;
    document.getElementsByClassName("rename")[0].select();
  // } else {
  //   //先展开
  //     pageObj.aid = node.id;
  //     var dataObj={
  //         currentPage:pageObj.page,
  //         pageSize:pageObj.size,
  //         orgId:pageObj.aid,
  //     };
  //     fetchs.post('/getAddressListStructure',dataObj,function (data) {
  //         if (data.ifSuc==1) {
  //             var result=data.data.sysOrganizations;
  //             var arrM=[];
  //             for (var i=0, l=result.length; i<l; i++) {
  //                 arrM[i] = {id:result[i].sysOrganization.id,pId:result[i].sysOrganization.parentId,name:result[i].sysOrganization.orgName,isParent:result[i].leaf};
  //             }
  //             if(arrM.length>0){
  //                 var node = zTreeObj.getNodeByParam("id",pageObj.aid,null);
  //                 var newNodes =  arrM;
  //                 zTreeObj.addNodes(node,0,newNodes);
  //             }
  //             //后添加部门
  //             var newNode = { id: c, name: '新建部门' };
  //             zTreeObj.addNodes(node, -1, newNode);
  //             var node1 = zTreeObj.getNodeByParam('id', c, null); //根据新的id找到新添加的节点
  //             zTreeObj.editName(node1);
  //             renameTag = 1;
  //             document.getElementsByClassName("rename")[0].maxLength=30;
  //             document.getElementsByClassName("rename")[0].select();
  //         }
  //     })
  // }
}
function getMembers(id,callBack){ //获取机构下的人员
    var Obj={
        currentPage:1,
        pageSize:15,
        orgId:id,
    };
    fetchs.post('/getAddressListStructure',Obj,function (data) {
        callBack(data);
    })
}
//删除子机构 
function didClickDeletedDepartment(editId,name) {
   getMembers(editId, function (data) {
     if (data.ifSuc == 1) {
        var sysLength = data.data.sysUser.list.length;
        if (sysLength > 0) {
           notify('danger',"部门下还有人员,不能删除");
          return false; //返回false将不删除子节点
    }else {
          $('#deleteOrga').find('span.room_name').text(name);
          $('#deleteOrga').find('input.name1').val(editId);
          $('#deleteOrga').modal('show');
           }
      }
    });
}
// 删除会议室区域
$('#deleteOrgaForm').on('submit', function (event) {
    event.preventDefault();
    $('#deleteOrga').modal('hide');
    var aID = eval('(' + '{' + $(this).serialize().replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
    //删除区域
    deleteOrganization(aID.id, function (data) {
        if (data.ifSuc == 1) {
            if(data.data==false){
                notify('danger',data.msg);
            }else{
                var node = zTreeObj.getNodeByParam('id', aID.id, null);
                zTreeObj.removeNode(node, true); //第二个参数  是否要触发删除子机构的回调函数
                notify('success',"删除成功");
            }
        }else{
            notify('danger',"删除失败");
        }
    });
});
//组织机构移动接口
function organiztionMove( moveOrgId, parentId, moveType, callBack) {
  var  moveObj={
      moveOrgId:moveOrgId,
      parentId:parentId,
      moveType:moveType,
  }
  fetchs.post('/organiztionMove',moveObj, function (data) {
    callBack(data);
  })
}
//组织机构添加接口
function saveOrganization(orgName, parentId, callBack) {
   var saveObj={
       orgName:orgName,
       parentId:parentId,
  }
  fetchs.post('/saveOrganization',saveObj, function (data) {
         callBack(data);
  })
}
//组织机构删除接口
function deleteOrganization(organizationId, callBack) {
  fetchs.post('/deleteOrganizationById',{organizationId:organizationId}, function (data) {
    callBack(data);
  })
}
//修改组织机构的功能接口
function updateOrganiztion(id, orgName, callBack) {
   var updateObj={
       id:id,
       orgName:orgName,
   }
  fetchs.post('/updateOrganiztion',updateObj, function (data) {
    callBack(data);
  })
}