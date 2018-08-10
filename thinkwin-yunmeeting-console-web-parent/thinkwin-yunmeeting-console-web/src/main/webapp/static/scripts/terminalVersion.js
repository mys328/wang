'use strict';

$(function () {
    var searchKey = ''; //搜索关键字
    var modalKey = ''; //模态框搜索关键字
    var modalPage = {
        page: 1,
        size: 15,
        id:"",
    };
    var pageObj = {
        page: 1,
        size: 15
    };
    var listObj = {
        listArray: [],
        listTotal: 0
    };
    var total;//总条数
    var file; //要上传的终端版本文件
    var timer = null;
    // 调用初始化数据接口位置
    $('body').on('shown.bs.tab', '#meetingShowNav a.nav-link', function (e) {
        if ($(this).attr('href') == '#terminalVersion') {
            //调用初始化数据接口位置
            console.log('1');
        }
    });
    //处理当改变窗口大小拖拽过高时,无法触发滚动加载更多,则重新刷新页面
    var resizeTimer = null;
    $(window).resize(function () {
        if (resizeTimer) clearTimeout(resizeTimer);
        resizeTimer = setTimeout(function () {
            var isReload =  Math.ceil(($(".tab-content").height()-150)/ 45+1) > pageObj.size ? true : false;
            if (searchKey != ''&& isReload){
                getList();
            } else if (searchKey == ''&& isReload){
                location.reload();
            }
        }, 1000);
    });
    var roomTpl = '\n <div class="middle" ng-if="array.length&&searchkey==\'\'">\n        <div class="version-Box">\n          <div class="beta-Box">\n            <div class="version">内测版本</div>\n            <div class="reminder" ng-if="alphaVer.length<1">暂未设置内测版本</div>\n    <div ng-repeat="item in alphaVer">\n              <div class="form-group" data-id="{{item.id}}">\n                <label class="version-Name">{{item.versionTitle}}</label><span class="version-beta">未发布</span><span class="btn-span btn-detail">详情</span>\n              </div>\n              <div class="beta-Box-left">\n                <div class="form-group">\n                  <label class="condition">版本编号   </label><span class="value">{{item.verNum}}</span>\n                </div>\n                <div class="form-group">\n                  <label class="condition">文件大小  </label><span class="value">{{item.size}}</span>\n                </div>\n                <div class="form-group"> \n                  <label class="condition">终端类型  </label><span class="value">{{item.terminalType}}</span>\n                </div>\n              </div>\n              <div class="beta-Box-right">\n                <div class="form-group"> \n                  <label class="condition">上传人员   </label><span class="value">{{item.creater}}</span>\n                </div>\n                <div class="form-group"> \n                  <label class="condition">上传时间   </label><span class="value">{{item.creatTime|dateTime: YYYY-MM-DD HH:mm}}</span>\n                </div>\n                <div class="button-box" data-id="{{item.id}}" data-num="{{item.verNum}}" data-name="{{item.versionTitle}}">\n                  <button class="btn btn-primary btn-sm btn-cancelBeta" >取消内测</button><button class="btn btn-primary btn-sm sendVersion">发布版本</button>\n                </div>\n              </div>\n            </div>\n          </div>\n          <div class="present-Box">\n            <div class="version">当前客户端版本 </div>\n            <div class="reminder" ng-if="releaseVer.length<1"> 暂未发布客户端版本</div>\n            <div ng-repeat="item in releaseVer">\n              <div class="form-group" data-id="{{item.id}}">\n                <label class="version-Name">{{item.versionTitle}}   </label><span class="version-current">已发布</span><span class="btn-span btn-detail" >详情</span>\n              </div>\n              <div class="beta-Box-left">\n                <div class="form-group">\n                  <label class="condition">版本编号   </label><span class="value">{{item.verNum}}</span>\n                </div>\n                <div class="form-group">\n                  <label class="condition">文件大小  </label><span class="value">{{item.size}}</span>\n                </div>\n                <div class="form-group"> \n                  <label class="condition">终端类型  </label><span class="value">{{item.terminalType}}</span>\n                </div>\n              </div>\n              <div class="beta-Box-right">\n                <div class="form-group"> \n                  <label class="condition">上传人员   </label><span class="value">{{item.creater}}</span>\n                </div>\n                <div class="form-group"> \n                  <label class="condition">上传时间   </label><span class="value">{{item.creatTime|dateTime: YYYY-MM-DD HH:mm}}</span>\n                </div>\n                <div class="form-group"> \n                  <label class="condition">更新数量   </label><span class="value">{{item.changeNum}}</span>\n                </div>\n              </div>\n            </div>\n          </div>\n        </div>\n      </div>\n      <div class="middle list-title" ng-if="array.length>0&&searchkey==\'\'">版本库</div>\n      <div class="list" ng-if="array.length>0">\n        <div class="scrollView" ng-class="searchkey!=\'\' ? \'searchList\' : \'\'">\n          <table class="table">\n            <tbody id="roomBody">\n              <tr> \n                <th class="table-1" data-toggle="tooltip" ><div>文件名称</div></th>\n                <th class="table-2" data-toggle="tooltip" ><div>版本编号</div></th>\n                <th class="table-3" data-toggle="tooltip" ><div>发布状态</div></th>\n                <th class="table-4" data-toggle="tooltip" ><div>文件大小</div></th>\n                <th class="table-5" data-toggle="tooltip" ><div>终端类型</div></th>\n                <th class="table-6" data-toggle="tooltip" ><div>上传人员</div></th>\n                <th class="table-7" data-toggle="tooltip" ><div>上传时间</div></th>\n                <th class="table-8" data-toggle="tooltip" ><div>更新数量</div></th>\n                <th class="table-9" data-toggle="tooltip" ><div>操作</div></th>\n              </tr>\n              <div class="listLine"></div>\n              <tr ng-repeat="item in array">\n                <td class="table-1" ><span ng-html="item.versionTitle|keylight:searchkey"></span></td>\n                <td class="table-2" ><span ng-html="item.verNum|keylight:searchkey"></span><span ng-if="item.reasleStatus==0&&item.verStatus==1" class="betaV">内测</span><span ng-if="item.reasleStatus==1" class="currentV">当前</span></td>\n                <td class="table-3"  ><span ng-if="item.reasleStatus==0">未发布</span><span ng-if="item.reasleStatus!=0">已发布</span></td>\n                <td class="table-4"  ><span>{{item.size|text}}</span></td>\n                <td class="table-5" ><span ng-html="item.terminalType|keylight:searchkey"></span></td>\n                <td class="table-6" ><span ng-html="item.creater|keylight:searchkey"></span></td>\n                <td class="table-7"  ><span>{{item.creatTime|dateTime: YYYY-MM-DD HH:mm}}</span></td>\n                <td class="table-8" ><span class="btn-span updateNum" data-id="{{item.id}}">{{item.changeNum}}</span></td>\n                <td class="table-9" data-toggle="tooltip" data-id="{{item.id}}" data-num="{{item.verNum}}" data-name="{{item.versionTitle}}">\n                   <span class="btn-span btn-detail">详情</span>\n                   <span class="btn-span btn-setBeta"  ng-if="item.reasleStatus==0&&item.verStatus==0">设为内测</span>\n                   <span class="btn-span btn-cancelBeta"  ng-if="item.reasleStatus==0&&item.verStatus==1">取消内测</span>\n                  <span class="btn-span btn-delete-version" ng-if="item.reasleStatus==0">删除</span>\n                </td>\n              </tr>\n            </tbody>\n          </table>\n          <div class="loading-bubbles" id="mine-loading">\n            <div class="bubble-container"> \n              <div class="bubble"></div>\n            </div>\n            <div class="bubble-container">\n              <div class="bubble"></div>\n            </div>\n            <div class="bubble-container">\n              <div class="bubble"></div>\n            </div>\n         </div>\n         </div>\n         </div>\n         <div ng-if="array.length<1&&searchkey==\'\'" class="nothing">还没有上传终端版本文件</div>\n         <div ng-if="array.length<1&&searchkey!=\'\'" class="nothing"><div>没有搜索到信息，换个条件试试？</div><div>您可以输入文件名称、版本编号、终端类型、上传人员等部分内容检索。</div></div>\n';

    var appendList = '\n    <table class="table">\n      <tr ng-repeat="item in verList" >\n        <td class="table-1" ><span ng-html="item.versionTitle|keylight:searchkey"></span></td>\n        <td class="table-2" ><span ng-html="item.verNum|keylight:searchkey"></span><span ng-if="item.reasleStatus==0&&item.verStatus==1" class="betaV">内测</span><span ng-if="item.reasleStatus==1" class="currentV">当前</span></td>\n        <td class="table-3" ><span ng-if="item.reasleStatus==0">未发布</span><span ng-if="item.reasleStatus!=0">已发布</span></td>\n        <td class="table-4" ><span>{{item.size}}</span></td>\n        <td class="table-5" ><span ng-html="item.terminalType|keylight:searchkey"></span></td>\n        <td class="table-6" ><span ng-html="item.creater|keylight:searchkey"></span></td>\n        <td class="table-7" ><span>{{item.creatTime|dateTime: YYYY-MM-DD HH:mm}}</span></td>\n        <td class="table-8" ><span class="btn-span updateNum" data-id="{{item.id}}">{{item.changeNum}}</span></td>\n        <td class="table-9" data-toggle="tooltip" data-id="{{item.id}}" data-num="{{item.verNum}}" data-name="{{item.versionTitle}}">\n           <span class="btn-span btn-detail">详情</span>\n           <span class="btn-span btn-setBeta"  ng-if="item.reasleStatus==0&&item.verStatus==0" >设为内测</span>\n           <span class="btn-span btn-cancelBeta"  ng-if="item.reasleStatus==0&&item.verStatus==1" >取消内测</span>\n          <span class="btn-span btn-delete-version" ng-if="item.reasleStatus==0" >删除</span>\n        </td>\n      </tr>\n    </table>\n  ';
    getList();
    function getList() {
        var size = Math.ceil(($(".tab-content").height()-150)/ 45+1);
        pageObj.size = size > 15 ? size : 15;
        pageObj.page = 1;
        var data = {'searchKey': searchKey,'currentPage': pageObj.page,'pageSize': pageObj.size,'hide':1 };
        fetchs.post('/displayTerminalVer/versionList', data, function (res){
            if(res.ifSuc==1){
                if(res.data){
                    initData(res.data);
                }
            }else  {
                notify('danger',res.msg);
                if(res.ifSuc==10){
                    goLoginPage();
                }
            }
        });
    }
    function initData(data){
        pageObj.page = 1;
        data.array =  listObj.listArray = data.verList.list;
        data.searchkey = searchKey;
        data.alphaVer = data.alphaVer==null?[]:[data.alphaVer];
        data.releaseVer =  data.releaseVer==null?[]:[data.releaseVer];
        if(data.recorde){
            $(".content .top .updateRecord").show();
        }
        $(".content .top .col-label.num").html(data.verList.total);
        total = parseInt(data.verList.total);
        $('body .version-content').html(soda(roomTpl, data));
        if(total>0){
           setEventListener();
        }
    }
    var detailTpl = '\n <div class="form-group form-group-title">\n      <h3 class="title">文件信息</h3>\n      <div class="dropdown" ng-if="reasleStatus==0">\n         <a class="nav-link dropdown-toggle" id="mainTop" href="javascript:void(0);" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">更多</a>\n        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="menu" data-id="{{id}}" data-name="{{versionTitle}}" data-num="{{verNum}}">\n          <a class="dropdown-item btn-cancelBeta" href="javascript:void(0);" ng-if="verStatus==1">取消内测</a>\n          <a class="dropdown-item btn-setBeta" href="javascript:void(0);" ng-if="verStatus==0">设为内测</a>\n          <a class="dropdown-item btn-delete-version" href="javascript:void(0);">删除</a>\n        </div>\n      </div>\n    </div>\n    <div class="form-group">\n      <div class="versionName">{{versionTitle}}</div>\n    </div>\n    <div class="form-group"><label class="condition">版本编号</label><span class="value">{{verNum}}</span>\n      <span class="value betaV" ng-if="verStatus==1">内测</span>\n      <span class="value currentV" ng-if="reasleStatus==1">当前</span>\n    </div>\n    <div class="form-group"><label class="condition">发布状态</label>\n      <span class="value" ng-if="reasleStatus==0">未发布</span>\n      <span class="value" ng-if="reasleStatus!=0">已发布</span>\n    </div>\n    <div class="form-group"><label class="condition">文件大小</label>\n      <span class="value">{{size}}</span>\n    </div>\n    <div class="form-group"> <label class="condition">终端类型</label>\n      <span class="value">{{terminalType}}</span>\n    </div>\n    <div class="form-group"><label class="condition">上传人员</label>\n      <span class="value">{{creater}}</span>\n    </div>\n    <div class="form-group"><label class="condition">上传时间</label><span class="value">{{creatTime|dateTime: YYYY-MM-DD HH:mm}}</span>\n    </div>\n    <div class="form-group"><label class="condition">更新数量</label><span class="value">{{changeNum}}</span>\n    </div>\n    <div class="form-group"><label class="record">更新记录</label>\n      <div class="condition condition-detail" ng-html="changeRecode"></div>\n    </div>\n  ';
    $('body').on('click', '.btn-detail', function (event) {
        event.stopPropagation();
        fetchs.post('/displayTerminalVer/getVersion',{"id":$(this).parent().data('id')}, function (res) {
            if(res.ifSuc==1){
                if(res.data){
                    $('.inner-box').html(soda(detailTpl, res.data));
                    $('body .version-info-box').addClass('show');
                }
            }else {
                notify('danger',res.msg);
                if(res.ifSuc==10){
                    goLoginPage();
                }
            }
        })
    });
    //查看更新记录
    $('body').on('click', '.updateRecord', function () {
       window.open("/gotoVersionsLogPage?token="+_userInfo.token+"&type="+1);
    })
    //隐藏
    $('body').bind("click", function (e) {
        var target = $(e.target);
        if (target.closest(".version-info-box").length == 0) {
            $("body .version-info-box").removeClass('show');
        }
    });
    //输入框改变事件
    $('.version-search').bind('input propertychange', function () {
        if ($(this).val().length > 0) {
            $(".del-searchList").show();
        } else {
            $(".del-searchList").hide();
        }
    });

    $('.search1').bind('input propertychange', function () {
        if ($(this).val().length > 0) {
            $(".del-updateList").show();
        } else {
            $(".del-updateList").hide();
        }
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

    $('.search1').bind('focus', function () {
        if ($(this).val().length > 0) {
            $(".del-updateList").show();
        } else {
            $(".del-updateList").hide();
        }
        $('.input-search').addClass('border-shadow');
    });
    //输入框失焦事件
    $('.version-search,.search1').bind('blur', function () {
        $('.input-search').removeClass('border-shadow');
    });

    //------取消搜索
    $(".del-searchList").click(function () {
        $('.version-search').val("");
        $(this).hide();
        searchKey = '';
        getList();
    });

    $(".del-updateList").click(function () {
        $('.search1').val("");
        $(this).hide();
        modalPage.page = 1;
        modalPage.size = 15;
        modalKey = '';
        getVerUpdateInfo();
    });

    //-------触发搜索
    $('body').on('keypress', '.version-search', function (e) {
        if (e.keyCode == 13) {
            searchKey = $('.version-search').val();
            pageObj.page = 1;
            getList();
            e.preventDefault(); //阻止默认事件
            $(this).blur();
        };
    });
    $('body').on('keypress', '.search1', function (e) {
        if (e.keyCode == 13) {
            modalKey = $('.search1').val();
            modalPage.page = 1;
            modalPage.size = 15;
            getVerUpdateInfo();
            e.preventDefault(); //阻止默认事件
            $(this).blur();
        };
    });
    //-------模态框隐藏的回调
    $("#updateNumModal").on("hide.bs.modal", function (e) {
        $('.search1').val("");
        $(".del-updateList").hide();
        modalPage.page = 1;
        modalPage.size = 15;
        modalPage.id="";
        modalKey = '';
    });
    //-------删除弹框
    $('body').on('click', '.btn-delete-version', function () {
        $("#delModal .version-num").html($(this).parent().data('num'));
        $("#delModal .version-name").html($(this).parent().data('name'));
        $("#delModal").find('input').val($(this).parent().data('id'));
        $("#delModal").modal("show");
    });
    //-------确认删除版本
    $('body').on('click', '.btn-version-delete', function () {
        var deleteId = $("#delModal").find('input').val();
        fetchs.post('/displayTerminalVer/delVersion',{'id':deleteId,'pageSize': pageObj.size},function(res){
            $("#delModal").modal("hide");
            if(res.ifSuc==1){
                if(res.data){
                    notify('success', res.msg);
                    if(searchKey!=""){
                       getList();
                    }else {
                       initData(res.data);
                    }
                }
            }else {
                notify('danger',res.msg);
                if(res.ifSuc==10){
                    goLoginPage();
                }else if(res.data){
                   if(searchKey!=""){
                        getList();
                   }else {
                        initData(res.data);
                   }
                }
            }
        });
    });
    //-------设置内测版本
    $('body').on('click', '.btn-setBeta', function () {
        console.log($(this).parent().data('name'));
        $("#setBeta .version-num").html($(this).parent().data('num'));
        $("#setBeta .version-name").html($(this).parent().data('name'));
        $("#setBeta").find('input').val($(this).parent().data('id'));
        $("#setBeta").modal("show");
    });

    //-------确认设置内测版本
    $('body').on('click', '.setBeta', function () {
        var id=$("#setBeta").find('input').val();
        setBetaVersion(id,1);
    });
    function setBetaVersion(id,status){
        fetchs.post('/displayTerminalVer/setVersion',{'id':id,'verStatus':status,'pageSize': pageObj.size},function(res){
            if(status==1){
                $("#setBeta").modal("hide");
            }else {
                $("#cancelBeta").modal("hide");
            }
            if(res.ifSuc==1){
                if(res.data){
                    if(searchKey!=""){
                        getList();
                    }else {
                        initData(res.data);
                    }
                    notify('success', res.msg);
                }
            }else {
                notify('danger',res.msg);
                if(res.ifSuc==10){
                    goLoginPage();
                }else if(res.data){
                    if(searchKey!=""){
                        getList();
                    }else {
                        initData(res.data);
                    }
                }

            }
        })
    }

    //-------取消内测版本
    $('body').on('click', '.btn-cancelBeta', function () {
        $("#cancelBeta .version-num").html($(this).parent().data('num'));
        $("#cancelBeta .version-name").html($(this).parent().data('name'));
        $("#cancelBeta").find('input').val($(this).parent().data('id'));
        $("#cancelBeta").modal("show");
    });
    //-------确认取消内测版本
    $('body').on('click', '.cancelBeta', function () {
        var id=$("#cancelBeta").find('input').val();
        setBetaVersion(id,0);
    });
    //-------发布版本
    $('body').on('click', '.sendVersion', function () {
        $("#sendVersion .version-num").html($(this).parent().data('num'));
        $("#sendVersion .version-name").html($(this).parent().data('name'));
        $("#sendVersion").find('input').val($(this).parent().data('id'));
        $("#sendVersion").modal("show");
    });

    $('body').on('click', '.btn-send', function () {
        var id=$("#sendVersion").find('input').val();
        fetchs.post('/displayTerminalVer/releaseVersion',{'id':id,'pageSize': pageObj.size},function(res){
          $("#sendVersion").modal("hide");
          if(res.ifSuc==1){
            initData(res.data);
            notify('success', res.msg);

          }else {
              notify('danger',res.msg);
              if(res.ifSuc==10){
                  goLoginPage();
              }else if(res.data){
                  initData(res.data);
              }
          }
        })
    });
    $('body').on('change', '.upload', function () {
        file = $(this).get(0).files[0];
        var extStart = file.name.lastIndexOf(".");
        var ext = file.name.substring(extStart, file.name.length).toUpperCase();
        $(this).remove();
        $(".uploadVersions .label").append( '<input class="uploadVersions upload" type="file" accept=".apk" style="display:none;">') ;
        if(ext != ".APK"){
            notify('danger','文件格式错误');
        }else{
             $("#table-loading").modal("show");
           fetchs.formDataUpload("/displayTerminalVer/uploadVerFile",file,function (res) {
               setTimeout(function () { $("#table-loading").modal("hide"); },1000);
               if(res.ifSuc==1){
                   if(res.data){
                       notify('success', res.msg);
                       if(searchKey!=""){
                           getList();
                       }else {
                           initData(res.data);
                       }
                   }
               }else {
                   notify('danger',res.msg);
                   if(res.ifSuc==10){
                       goLoginPage();
                   }
               }
           },[{'key':'pageSize','value': pageObj.size}]);
        }
    });
    //-------点击更新数量
    $('body').on('click', '.updateNum', function () {
        if(parseInt($(this).html())){
            modalPage.id=$(this).data('id');
            $("#updateNumModal").modal("show");
            getVerUpdateInfo();
        }
    });

    var updateList = '\n        <table class="table" ng-if="list.length>0">\n          <tr> \n            <th class="table-1" ><div>升级时间</div></th>\n            <th class="table-2"  ><div>终端标识</div></th>\n            <th class="table-3"  ><div>租户信息</div></th>\n            <th class="table-4"  ><div>联系人电话</div></th>\n            <th class="table-5" ><div>版本升级信息</div></th>\n          </tr>\n          <div class="listLine" ng-if="list.length>0"></div>\n          <tbody class="listBody">\n            <tr ng-repeat="item in list">\n              <td class="table-1"><span>{{item.createTime|dateTime: YYYY-MM-DD HH:mm}}</span></td>\n              <td class="table-2"><span ng-html="item.hardwareId|keylight:searchkey"></span></td>\n              <td class="table-3"><span ng-html="item.tenantName|keylight:searchkey"></span></td>\n              <td class="table-4" ><span ng-html="item.phoneNumber|keylight:searchkey"></span><span ng-html="item.userName|keylight:searchkey" class="contast"></span></td>\n              <td class="table-5"  ><span ng-html="item.lastVer|keylight:searchkey"></span><i class="icon icon-guide sign"></i><span >{{item.currentVer}}</span></td>\n            </tr>\n          </tbody>\n        </table>\n       <div ng-if="list.length<1&&searchkey!=\'\'" class="nothing"><div>没有搜索到信息，换个条件试试？</div><div>您可以输入终端标识、租户信息、联系人、原版本号等部分内容检索。</div>\n ';

    function getVerUpdateInfo(type) {
        var data={'id':modalPage.id,'searchKey': modalKey,'currentPage': modalPage.page, 'pageSize': modalPage.size};
        fetchs.post('/displayTerminalVer/getVerUpdateInfo',data,function(res){
            if(res.ifSuc==1){
                var listData = res.data.verTerminalList;
                if(type!=1){
                    p.setCount(parseInt(listData.total));
                }
                $("#updateNumModal .col-label.num").html(listData.total);
                listData.searchkey=modalKey;
                $(".table-update-list .update-list").html(soda(updateList, listData));
            }else {
                notify('danger',res.msg);
                if(res.ifSuc==10){
                    goLoginPage();
                }
            }
        });
    }
    //初始化分页控件
    var p = Pages();
    p.callBack(callBack);
    //返回当前页和每页多少条数据
    function callBack(currentPage, pageSize) {
        modalPage.page = currentPage;
        modalPage.size = pageSize;
        getVerUpdateInfo(1);
    }
    var scrollTimer = null;
    var isLoading = 1;
    function setEventListener() {
        document.getElementsByClassName('scrollView')[0].addEventListener('scroll', function (e) {
            if (scrollTimer) clearTimeout(scrollTimer);
            var target = e.currentTarget;
            var curScrollTop = target.scrollTop;
            var targetHeight = target.offsetHeight;
            var listCntHeight = document.getElementById('roomBody').offsetHeight;
            var isEnd;
            if ($(".loading-bubbles").is(":hidden")) {
                isEnd = curScrollTop + targetHeight >= listCntHeight;
            } else {
                isEnd = true;
            }
            if (isEnd) {
                if (listObj.listArray.length < total && isLoading==1) {
                    $(".loading-bubbles").show();
                    scrollTimer = setTimeout(function () {
                        ++pageObj.page;
                        var data = {'searchKey': searchKey,'currentPage':pageObj.page,'pageSize': pageObj.size,'hide':0 };
                        fetchs.post("/displayTerminalVer/versionList", data,function (res) {
                            if(res.ifSuc==1){
                                $(".loading-bubbles").hide();
                                if(res.data){
                                    if(res.data.verList.list.length==0){
                                        return  isLoading = 0;
                                    }
                                    isLoading = 1;
                                    var length = listObj.listArray.length;
                                    listObj.listArray = listObj.listArray.concat(res.data.verList.list);
                                    listObj.listArray = _.uniq(listObj.listArray, false, function (item) {
                                        return item.id;
                                    });
                                    var addArray = listObj.listArray.slice(length);
                                    if (addArray.length == 0) {
                                        return;
                                    }
                                    var html = $(".test").html(soda(appendList, { 'verList': addArray,'searchkey':searchKey})).find('tr');
                                    $("#roomBody").append(html);
                                }
                            }else {
                                --pageObj.page;
                                notify('danger',res.msg);
                                if(res.ifSuc==10){
                                    goLoginPage();
                                }
                            }
                        });
                    },500);
                }
            }
        });
    }
});
