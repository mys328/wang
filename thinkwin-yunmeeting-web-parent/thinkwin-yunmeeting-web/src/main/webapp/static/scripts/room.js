'use strict';

var renameTag = 0; //用于区分是新建名称编辑 和 对之前组织机构的名称进行编辑
var imageObj ={
    imageState:0, //记录图片修改的状态
    imageSize:0,  //记录图片的大小
};
var oldName="";//记录编辑之前的名字
var searchKey=""; //记录搜索的关键字
var searchState=0; //记录搜索的状态
var zTreeObj;
$(function () {
    var page = {
        aid: 0, //记录区域id
        size: 15, //每页多少条
        page: 1, //当前页码
        subtotal: 0, //子区域下的会议室数
        total:0,// 会议室总数量
        treeId:"treeDemo_2",//记录点击的ztree的节点className  默认记录的是默认区域
    };
    var rooms = [];
    var areas = [];
    var areaId='';
    var isEdit = 0;
    var userState; //是否是付费用户
    //初始化分页控件
    var p = Pages();
    $('[data-toggle="popover"]').popover();
    p.callBack(callBack); //初始化分页器自定义回调函数
    getData();
    function getData() {
        //初始化区域
        selectAreaandRoom(function (data) {
            if (data.ifSuc == 1) {
                var area = data.data.roomArea;
                rooms = data.data.room;
                userState = data.data.userState;
                if(userState=="0"){ //免费用户
                    $(".isPay_false").show();
                    $(".isPay").hide();
                }else if(userState=="1"){ //付费用户
                    $(".isPay_false").hide();
                    $(".isPay").show();
                }
                page.total =rooms.total;
                if(data.data.roomTotal==0){ //是否需要禁用打印二维码按钮
                    $("#qrCode").attr({"disabled":"disabled"});
                }else {
                    $("#qrCode").removeAttr("disabled");
                }
                areas = [];
                for (var i = 0, l = area.length; i < l; i++) {
                    var num="";
                    if(area[i].number>0){
                        num=area[i].number;
                    }
                    if(area[i].isDefault=="1"){
                        page.aid=area[i].id;
                        areas[i] = { id: area[i].id, pId: 0, name: area[i].name, totalLabel: num,isDefault:area[i].isDefault,drag:false,isMove: false};
                    }else {
                        areas[i] = { id: area[i].id, pId: 0, name: area[i].name, totalLabel: num,isDefault:area[i].isDefault};
                    }
                }
                areas.unshift({ id: 0, name: "全部区域", open: true, isMove: false,drag:false, totalLabel: data.data.roomTotal,isDefault:"0"});
                page.treeId="treeDemo_2";
                zTreeObj = $.fn.zTree.init($("#treeDemo"), setting, areas);
                setRoom(rooms, 0);
            }
        });
    }
    function updateArea() {
        //初始化区域
        selectAreaandRoom(function (data) {
            if (data.ifSuc == 1) {
                var area = data.data.roomArea;
                page.total = data.data.roomTotal;
                if(page.total==0){
                    $("#qrCode").attr({"disabled":"disabled"});
                }else {
                    $("#qrCode").removeAttr("disabled");
                }
                areas = [];
                for (var i = 0, l = area.length; i < l; i++) {
                    var num="";
                    if(area[i].number>0){
                        num=area[i].number;
                    }
                    if(area[i].isDefault=="1"){
                        areas[i] = { id: area[i].id, pId: 0, name: area[i].name, totalLabel: num,isDefault:area[i].isDefault,drag:false,isMove: false};
                    }else {
                        areas[i] = { id: area[i].id, pId: 0, name: area[i].name, totalLabel: num,isDefault:area[i].isDefault};
                    }

                }
                areas.unshift({ id: 0, name: "全部区域", open: true, isMove: false,drag:false, totalLabel: page.total,isDefault:"0"});
                zTreeObj = $.fn.zTree.init($("#treeDemo"), setting, areas);
            }
        });
    }
    function searchData(searchKey) {
        searchAreaMeetingRoom(page.page,page.size,page.aid,searchKey,function(data){
            if (data.ifSuc == 1) {
                var area = data.data.roomArea;
                rooms = data.data.room;
                page.total = data.data.roomTotal;
                areas = [];
                for (var i = 0, l = area.length; i < l; i++) {
                    var num="";
                    if(area[i].number>0){
                        num=area[i].number;
                    }
                    if(area[i].isDefault=="1"){
                        areas[i] = { id: area[i].id, pId: 0, name: area[i].name, totalLabel: num,isDefault:area[i].isDefault,drag:false,isMove: false};
                    }else {
                        areas[i] = { id: area[i].id, pId: 0, name: area[i].name, totalLabel: num,isDefault:area[i].isDefault};
                    }
                }
                areas.unshift({ id: 0, name: "全部区域", open: true, isMove: false, totalLabel: page.total,isDefault:"0"});
                page.treeId="treeDemo_1";
                zTreeObj = $.fn.zTree.init($("#treeDemo"), setting, areas);
                setRoom(rooms, 0);
            }
        });
    }
    //设置会议室数据
    function setRoom(rooms, tag) {
        if (tag == 0) {
            //区分第一次触发 与 分页触发
            p.setCount(page.total);
            //会议室列表
            if (page.total > 15) {
                $('.list').addClass('haspage'); //控制高度的
                $('#page-Box').show();
            } else {
                $('.list').removeClass('haspage');
                $('#page-Box').hide();
            }
        }
        rooms.key=searchKey;
        if(rooms.list.length>=15&&$("#page-Box").css("display")=="block"){
            $('.list').css("height","calc(100% - 90px)");
        }else{
            $('.list').css("height","100%");
        };
        $('.list').html(soda(roomTpl, rooms));
        //获取滚动条宽度
        var width = $('.list')[0].offsetWidth - $('.list')[0].scrollWidth;
        $('.table-title').css("margin-right", width + 'px');
        if(rooms.list.length==0&&searchState==1){
            $(".nothing").html("搜索不到您需要的内容");
        }
        //会议室列表拖拽功能初始化
        var el = document.getElementById('roomBody');
        var sortable = Sortable.create(el, {
            onEnd: draggableOnEnd //会议室列表 拖拽结束后的回调
        });
    }
    //获取区域下的会议室
    function setAreaRoom(tag) {
        selectAreaMeetingRoom(page.page, page.size, page.aid, function (data) {
            if (data.ifSuc == 1) {
                rooms = data.data;
                page.total = data.data.total;
                if(page.aid==0){
                    page.treeId="treeDemo_1";
                }
                setRoom(rooms, tag);
            }else {
                notify("danger","服务器内部错误");
            }
        });
    }
    //获取搜索区域下的会议室
    function setSrarchAreaRoom(tag) {
        searchAreaMeetingRoom(page.page,page.size,page.aid,searchKey,function(data){
            if (data.ifSuc == 1) {
                if(page.aid==0){
                    rooms = data.data.room;
                    page.total = data.data.roomTotal;
                }else {
                    rooms = data.data;
                    page.total = data.data.total;
                }
                setRoom(rooms, 0);
            }
        });
    }
    //查询区域和会议室接口
    //http://api.yunmetting.com/meetingRoom/selectAreaandRoom
    function selectAreaandRoom(callBack) {

        fetchs.post('/meetingRoom/selectAreaandRoom',{} ,function (data) {
            callBack(data);
        })
    }

    //查询区域下的会议室接口
    //http://api.yunmetting.com/ meetingRoom / selectAreaMeetingRoom
    function selectAreaMeetingRoom(currentPage, pageSize, areaId, callBack) {
        var dataObj={
            currentPage:currentPage,
            pageSize:pageSize,
            areaId:areaId,
        };
        fetchs.post('/meetingRoom/selectAreaMeetingRoom',dataObj, function (data) {
            callBack(data);
        });
    }
    //会议室搜索接口
    //http://api.yunmetting.com/meetingRoom/searchMeetingRoom
    function searchMeetingRoom(currentPage, pageSize, searchKey, callBack) {
        var dataObj={
            currentPage:currentPage,
            pageSize:pageSize,
            searchKey:searchKey,
        };
        fetchs.post('/meetingRoom/searchMeetingRoom',dataObj, function (data) {
            callBack(data);
        });
    }
    //区域下的会议室搜索接口
    //http://api.yunmetting.com/meetingRoom/searchAreaMeetingRoom
    function searchAreaMeetingRoom(currentPage, pageSize, areaId, searchKey, callBack) {
        var dataObj={
            currentPage:currentPage,
            pageSize:pageSize,
            areaId:areaId,
            searchKey:searchKey,
        };

        fetchs.post('/meetingRoom/searchAreaMeetingRoom',dataObj, function (data) {
            callBack(data);
        });
    }

    //新建会议室接口
    //http://api.yunmeting.com/meetingRoom/insertMeetingRoom
    function insertMeetingRoom(insertRoomObj, callBack) {

        fetchs.post('/meetingRoom/insertMeetingRoom',insertRoomObj, function (data) {
            callBack(data);
        });
    }
    //修改会议室接口
    //http://api.yunmetting.com/meetingRoom/updateMeetingRoom
    function updateMeetingRoom(insertRoomObj, callBack) {
        fetchs.post('/meetingRoom/updateMeetingRoom',insertRoomObj, function (data) {
            callBack(data);
        });
    }
    //删除会议室接口
    //http://api.yunmetting.com/meetingRoom/deleteMeetingRoom
    function deleteMeetingRoom(id, callBack) {
        var dataObj={
            id:id,
        };
        fetchs.post('/meetingRoom/deleteMeetingRoom',dataObj, function (data) {
            callBack(data);
        });
    }
    //会议室停用接口
    //http://api.yunmetting.com/ meetingRoom/stopMeetingRoom
    function stopMeetingRoom(stopRoomObj, callBack) {
        fetchs.post('/meetingRoom/stopMeetingRoom',stopRoomObj, function (data) {
            callBack(data);
        });
    }
    //会议室启用接口
    // http://api.yunmetting.com/meetingRoom/startMeetingRoom
    function startMeetingRoom(id, callBack) {
        var dataObj={
            id:id,
        };
        fetchs.post('/meetingRoom/startMeetingRoom',dataObj, function (data) {
            callBack(data);
        });
    }
    //会议室拖动排序接口
    function mettingRoomSorting(nowRoomId, purposeRoomId, areaId, callBack) {
        var dataObj={
            nowRoomId:nowRoomId,
            purposeRoomId:purposeRoomId,
            areaId:areaId,
        };
        fetchs.post('/meetingRoom/mettingRoomSorting',dataObj, function (data) {
            callBack(data);
        });
    }
    //-----------------------会议室区域接口-------------------------
    //创建会议室区域
    //http://api.yunmetting.com/meetingRoom/insertYuncmRoomArea
    function insertYuncmRoomArea(name, callBack) {
        var dataObj = {
            name: name,
        };
        fetchs.post('/meetingRoom/insertYuncmRoomArea', dataObj, function (data) {
            callBack(data);
        });
    }
    //更改区域名称
    //http://api.yunmetting.com/meetingRoom/updateAreaName
    function updateAreaName(name, areaId, callBack) {
        var dataObj={
            name:name,
            areaId:areaId,
        };
        fetchs.post('/meetingRoom/updateAreaName',dataObj, function (data) {
            callBack(data);
        });
    }
    //删除区域
    //http://api.yunmetting.com/meetingRoom/deleteArea
    function deleteArea(areaId, callBack) {
        var dataObj={
            areaId:areaId,
        };
        fetchs.post('/meetingRoom/deleteArea',dataObj, function (data) {
            callBack(data);
        });
    }
    //会议室区域拖动排序接口
    function mettingRoomAreaSorting(nowRoomId, purposeRoomId, moveType, callBack) {
        var dataObj={
            nowRoomId:nowRoomId,
            purposeRoomId:purposeRoomId,
            moveType:moveType,
        };
        fetchs.post('/meetingRoom/mettingRoomAreaSorting',dataObj, function (data) {
            callBack(data);
        });
    }

    //下载全部会议室二维码接口
    //http://api.yunmetting.com/meetingRoom/downloadQrcode
    function downloadQrcode(areaId, callBack) {
        var _userInfo= JSON.parse(localStorage.getItem('userinfo'));
        var dataObj={
            areaId:areaId,
        };
        window.location='/meetingRoom/downloadQrcode?token='+  _userInfo.token;
        /*fetchs.post('/meetingRoom/downloadQrcode',dataObj, function (data) {
         callBack(data);
         });*/
    }
    //组织机构的单击事件
    function onClick(e, treeId, treeNode) {
        var zTree = $.fn.zTree.getZTreeObj('treeDemo');
        $(".on").removeClass("on");
        $(".dimBgc").show();
        var heightLight='#' + treeNode.tId + ' >div#heightLight';
        var aObj = $(heightLight);
        var treeNodeId="#"+treeNode.tId+" "+".dimBgc:first";
        $(treeNodeId).hide();
        aObj.addClass("on");
        page.aid = treeNode.id;
        page.page = 1;
        page.size = 15;
        page.treeId=treeNode.tId;
        p.resetpage();
        if(searchState==1){ //搜索状态下的点击
            setSrarchAreaRoom(0);
        }else {
            setAreaRoom(0);
        }
        // zTree.expandNode(treeNode); //打开/折叠节点
    }
    //返回当前页和每页多少条数据
    function callBack(currentPage, pageSize) {
        page.page = currentPage;
        page.size = pageSize;
        if(searchState==1){//搜索状态下的点击
            setSrarchAreaRoom(1);
        }else{
            setAreaRoom(1);
        }
    }
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
        var moveOrgId = treeNodes[0].id;
        var targetId = targetNode.id;
        if (targetNode.id == 0) {
            //如果目标节点是父节点,就取消
            return false;
        }
        var moveType;
        if (moveType == 'prev') {
            if(targetNode.isDefault=="1"){
                notify('danger', '不能移动到默认区域的前方');
                return false;
            }
            moveType = 0;
        } else if (moveType == 'next') {
            moveType = 1;
        }
        $.ajaxSetup({
            async : false
        });
        var confirmVal = true;
        //调用移动组织机构接口
        mettingRoomAreaSorting(moveOrgId, targetId, moveType, function (data) {
            if (data.ifSuc == 1) {
                notify('success', '移动成功');
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

        if (treeNode.name==""){
            if (renameTag == 0) {
                treeNode.name=oldName;
            }else {

                treeNode.name="新建区域";
            }
            zTreeObj .updateNode(treeNode);
        }
        if (renameTag == 0) {
            //更改区域名称
            updateAreaName(treeNode.name, treeNode.id, function (data) {
                if (data.ifSuc == 1) {
                    updateArea();
                    notify('success', '更改成功');
                }else {
                    return false;
                    notify('danger', '更改失败');
                }
                oldName="";
            });
        } else if (renameTag == 1) {
            //创建会议室区域
            insertYuncmRoomArea(treeNode.name, function (data) {
                renameTag = 0;
                if (data.ifSuc == 1) {
                    treeNode.id=data.data.id;
                    treeNode.isDefault=data.data.isDefault;
                    notify('success', '新建成功');
                    updateArea();
                }else {
                    return false;
                    notify('danger', '新建失败');
                }
            });
        }
        var treeNodeId="#"+treeNode.tId+" "+".dimBgc:first";
        if ($("#"+treeNode.tId).children().is('.on')==false){
            $(treeNodeId).show();
        }
    }
    //删除机构的回调函数
    function onRemove(e, treeId, treeNode) {
        //调用删除机构的接口
        deleteOrganization(treeNode.id, function (data) {
            console.log('--删除机构--');
        });
        console.log('删除成功');
    }

    //自定义添加...按钮
    function addDiyDom(treeId, treeNode) {
        var a='#' + treeNode.tId + ' > a';
        var aObj = $(a);
        var span= '#' + treeNode.tId + ' > span';
        var wrapObj = $(span);
        var div  = '<div id="heightLight" class="heightLight" ></div>';
        wrapObj.before(div);
        var heightLight='#' + page.treeId + ' >div#heightLight'
        var selectedObj = $(heightLight);
        var treeNodeId="#"+page.treeId+" "+".dimBgc:first";
        $(treeNodeId).hide();
        selectedObj.addClass("on");
        var editId = '"' + treeNode.id + '"';
        var menu = '';
        if (treeNode.level == 0) {
            menu = '<button id=\'partment\' class=\'dropdown-item editStyle\' onclick=\'didClickAddDepartment(' + editId + ')\'>新建区域</button>';
        } else {
            menu = '<button class=\'dropdown-item editStyle\' onclick=\'didClickRename(' + editId + ','+treeNode.tId+')\' )\'>重命名</button><button class=\'dropdown-item editStyle\'  data-id=\'' + treeNode.id + '\' data-name=\'' + treeNode.name + '\'  data-toggle=\'modal\' data-target=\'#deleteArea\'>删除</button>';
        }
        // console.log(treeNode);
        if ($('#diyBtn_' + treeNode.id).length > 0) return;
        var editStr = '<div class=\'dropdown\' id=\'dropdown\'  >  <div class=\'editBtn\' id=\'diyBtn_' + treeNode.id + '\'   data-toggle=\'dropdown\' ><a class=\'icon icon-organiz-more\'></a><div class=\'selectedRow\'></div></div><div class=\'dropdown-menu dropdown-menu-right edit-menu\' id=\'dropdown-menu\' aria-labelledby=\'diyBtn_' + treeNode.id + '\'   aria-haspopup=\'true\' aria-expanded=\'false\'>' + menu + '</div><div class=\'dimBgc\'></div>';
        if(treeNode.isDefault=="0"){
            aObj.after(editStr);
        }
        if(treeNode.totalLabel>0){
            var child='#' + treeNode.tId + ' > a span:nth-child(2)';
            var areaSpan = $(child);
            var totalLabel = "<label class='totalLabel'>" + treeNode.totalLabel + "<label>";
            areaSpan.after(totalLabel);
        }
    };
    //会议室列表 拖拽结束后的回调函数
    function draggableOnEnd(evt) {

        var areaId = page.aid;
        var oldIndex = evt.oldIndex + (page.page - 1) * page.size;
        var newIndex = evt.newIndex + (page.page - 1) * page.size;
        if(oldIndex==newIndex){
            return;
        }
        mettingRoomSorting(oldIndex, newIndex, areaId, function (data) {
            if (data.ifSuc == 1) {
                notify('success', '会议室排序成功');
            }else {
                notify('danger', '会议室排序失败');
            }
        });
    }

    //初始化,并配置ztree
    var setting = {
        edit: {
            enable: true,
            showRemoveBtn: false,
            showRenameBtn: false,
            drag: {
                prev: true,
                next: true,
                inner: false
            }
        },
        data: {
            simpleData: {
                enable: true
            }
        },
        callback: {
            beforeRename: beforeRename,
            onRename: onRename, //重命名后的事件回调
            onRemove: onRemove,
            beforeDrag: beforeDrag,
            beforeDrop: beforeDrop,
            onClick: onClick
        },
        view: {
            dblClickExpand: false,
            addDiyDom: addDiyDom,
            showTitle: false
        }
    };

    zTreeObj = $.fn.zTree.getZTreeObj('treeDemo');
    $("input.rename").maxLength=30;
    soda.filter('room.auth', function (auth) {
        switch (parseInt(auth)) {
            case 1:
                return '会议室管理员可预订';
                break;
            case 2:
                return '预订专员可预订';
                break;
            case 3:
                return '会议室管理员、预定专员可预定';
                break;
            default:
                return '所有人可预订';
                break;
        }
    });
    soda.filter('room.statu', function (statu) {
        switch (parseInt(statu)) {
            case 1:
                return '永久停用';
                break;
            case 2:
                return '正常启用';
                break;
            default:
                return '临时停用';
                break;
        }
    });

    var roomTpl = '\n  <table class="table table-hover list-rooms">\n    <tbody id="roomBody">\n      <tr ng-repeat="item in list" data-id="{{item.id}}">\n        <td>\n          <div class="thumb"><div class="imgBox"><img src="../static/images/huiyi-mini.png" ng-src="{{item.smallPicture}}"></div><div class="isAudit" ng-if="item.isAudit==1">需审核</div></div>\n          <h6><span ng-html="item.name|keylight:key" class="title"></span><span class="num" ng-html="item.persionNumber|keylight:key"></span><span class="people">\u4EBA</span></h6>\n          <p><span class="icon icon-room-address"></span>{{item.location}}</p>\n          <p><span class="icon icon-room-equipment"></span><span ng-html="item.deviceService|keylight:key"></span></p>\n        </td>\n        <td>{{item.reservationStatus|room.auth}}</td>\n        <td>{{item.state|room.statu}}<p class="stopDuration" ng-if="item.state==3">{{item.startTime|date:\'YYYY.M.D\'}}~{{item.endTime|date:\'YYYY.M.D\'}}</p></td>\n        <td>\n          <button class="btn btn-clear-primary btn-room-edit" data-id="{{item.id}}">\u4FEE\u6539</button>\n          <button class="btn btn-clear-primary" data-toggle="modal" data-target="#deleteModal" data-id="{{item.id}}" data-name="{{item.name}}">\u5220\u9664</button>\n          <button ng-if="item.state==2" class="btn btn-clear-primary" data-toggle="modal" data-target="#disableModal" data-id="{{item.id}}" data-name="{{item.name}}">\u505C\u7528</button>\n          <button ng-if="item.state!=2" class="btn btn-clear-primary" data-toggle="modal" data-target="#enabledModal" data-id="{{item.id}}" data-name="{{item.name}}">\u542F\u7528</button>\n        </td>\n      </tr>\n    </tbody>\n  </table>\n   <div ng-if="list.length<1" class="nothing">\u533A\u57DF\u4E0B\u6682\u65F6\u6CA1\u6709\u4F1A\u8BAE\u5BA4\uFF0C\u60A8\u53EF\u4EE5<a href="#"    class="addTitle">\u6DFB\u52A0\u4F1A\u8BAE\u5BA4</a></div>';

    // 删除会议室区域
    $('#deleteArea').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var id = button.data('id');
        var name = button.data('name');
        var modal = $(this);
        modal.find('span.room_name').text(name);
        modal.find('input.name1').val(id);
    });
    $('#deleteAreaForm').on('submit', function (event) {
        event.preventDefault();
        $('#deleteArea').modal('hide');
        var aID = eval('(' + '{' + $(this).serialize().replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
        //删除区域
        deleteArea(aID.id, function (data) {
            if (data.ifSuc == 1) {
                notify('success', '删除成功');
                page.treeId="treeDemo_2";
                getData();
            }else {
                notify('danger', '删除失败');
            }
        });
    });

    //下载二维码
    $('#printModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var area = areas.filter(function (item) {
            return item.id == page.aid;
        });
        // console.log(area);
        var modal = $(this);
        modal.find('.areaNmae').text(area[0].name);
        modal.find('input').val(area[0].id);
    });
    $('#downloadQrcode').on('submit', function (event) {
        event.preventDefault();
        $('#printModal').modal('hide');
        var data = eval('(' + '{' + $(this).serialize().replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
        downloadQrcode(data.id, function (data){
            console.log(data);
        })
    });
    // 删除会议室
    $('#deleteModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var id = button.data('id');
        var name = button.data('name');
        var modal = $(this);
        modal.find('span.room_name').text(name);
        modal.find('input').val(id);
    });
    $('#deleteForm').on('submit', function (event) {
        event.preventDefault();
        $('#deleteModal').modal('hide');
        var data = eval('(' + '{' + $(this).serialize().replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');

        deleteMeetingRoom(data.id, function (data) {
            if (data.ifSuc == 1) {
                notify('success', '删除成功');
                updateArea();
                setAreaRoom(0);
            }else {
                notify('danger', '删除失败');
            }
        });
    });
    //停用会议室
    $('#disableModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var id = button.data('id');
        if(id==undefined){  //阻止时间选择器的冒泡事件
            document.getElementsByName("breaking")[0].checked=false;
            return;
        }
        document.getElementsByName("breaking")[0].checked=false;
        var name = button.data('name');
        var modal = $(this);
        var currentDate = new Date(),
            sixDaysAfterNow = new Date().setDate(currentDate.getDate() + 6),
            date=new Date(sixDaysAfterNow);//获取6天后的时间戳
        $(".break_time_start").datepicker("setDate",currentDate);
        $(".break_time_end").datepicker("setDate",date);
        $(".break_time_start").css({"background":"#fff"});
        $(".break_time_end").css({"background":"#fff"});
        $(".break_time_start").removeAttr("disabled");
        $(".break_time_end").removeAttr("disabled");
        modal.find('span.room_id').text(id);
        modal.find('span.room_name').text(name);
    });


    $("input[name^='breaking']").click(function(){
        if(this.checked==true) {
            $(".break_time_start").datepicker("setDate","");
            $(".break_time_end").datepicker("setDate","");
            $(".break_time_start").css({"background":"#FAFAFA"});
            $(".break_time_end").css({"background":"#FAFAFA"});
            $(".break_time_start").attr('disabled',"true");
            $(".break_time_end").attr('disabled',"true");
        }else {
            var currentDate = new Date(),
                sixDaysAfterNow = new Date().setDate(currentDate.getDate() + 6),
                date=new Date(sixDaysAfterNow);//获取6天后的时间戳
            $(".break_time_start").datepicker("setDate",currentDate);
            $(".break_time_end").datepicker("setDate",date);
            $(".break_time_start").css({"background":"#fff"});
            $(".break_time_end").css({"background":"#fff"});
            $(".break_time_start").removeAttr("disabled");
            $(".break_time_end").removeAttr("disabled");
        }
    });
    $('#disableForm').on('submit', function (event) {
        event.preventDefault();
        $('#disableModal').modal('hide');
        var that = this;
        var id = $(this).find('span.room_id').text();
        var data = eval('(' + '{' + decodeURIComponent($(this).serialize(), true).replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
        var state = "";
        if (data.breaking == "1") {
            state = "1";
        } else {
            state = "3";
        }
        var stopRoomObj = {
            startTime: data.start,
            endTime: data.end,
            state: state,
            id: id,
            operReason: data.textarea
        };
        stopMeetingRoom(stopRoomObj, function (data) {
            that.reset();
            if (data.ifSuc == 1) {
                notify('success', '停用成功');
                setAreaRoom(0);
            }
        });
    });

    // 启用会议室
    $('#enabledModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var id = button.data('id');
        var name = button.data('name');
        var modal = $(this);
        modal.find('span.room_name').text(name);
        modal.find('input').val(id);
    });
    $('#enabledForm').on('submit', function (event) {
        event.preventDefault();
        $('#enabledModal').modal('hide');
        var data = eval('(' + '{' + $(this).serialize().replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
        //会议室启用接口
        startMeetingRoom(data.id, function (data) {
            if (data.ifSuc == 1) {
                notify('success', '启用成功');
                setAreaRoom(0);
            }
        });
    });

    var today = getNowDate();
    $('.input-daterange').datepicker({
        language: 'zh-CN',
        format: 'yyyy-mm-dd',
        startDate: today
    });
    // $('.input-daterange input').val(today)]

    //点击添加会议室
    $('.btn-add-room').on('click', function (event) {
        isAddRooms();
        event.stopPropagation();
    });
    $("body").on("click",".addTitle",function(event){
        isAddRooms();
        event.stopPropagation();
    });
    function isAddRooms() {
        fetchs.post('/meetingRoom/getAddRoomStatus',{}, function (data) {
            if(data.ifSuc==0){//会议室数量达到最大,不能添加
                notify('danger', data.msg);
                return;
            }else  if(data.ifSuc==1){
                $(".add-room-tittle").html("添加会议室");
                $(".btn-cancel").next().html("保存并继续");
                $(".btn-cancel").show();
                addMettingRooms();
            }else {//状态异常
                notify("danger",data.msg);
            }
        });
    }

    function  addMettingRooms() {
        isEdit = 0;  //  区分修改还是添加的标记
        var selectedHtml = "";
        for (var i = 1; i < areas.length; i++) {
            if(page.aid==0){
                $("#type-name").text(areas[1].name);
                $(".areaId").val(areas[1].id);
            }else if (areas[i].id == page.aid) {
                $("#type-name").text(areas[i].name);
                $(".areaId").val(page.aid);
            }
            selectedHtml += "<a class='dropdown-item'  id=" + areas[i].id+"  data-areaId=" + areas[i].id + " data-areaName="+areas[i].name+">"+areas[i].name+"</a>";
        }
        $("#roomform .area-type").html(selectedHtml);
        $("#roomform")[0].elements['limitMembers0'].checked = true;
        $('.add-room').addClass('show');
    }

    $('body').on('click', '#roomform .dropdown-item', function (e) {
        var areaId = $(this).attr("data-areaId");
        var areaName = $(this).attr("data-areaName");
        $("#type-name").text(areaName);
        $(".areaId").val(areaId);
    });

    //点击了空白区域
    $('body').on('click', function (e) {
        if (e.target != $('.add-room') && $('.add-room').find(e.target).length < 1 ) {
            $('.add-room').removeClass('show');
            if($(".btn-cancel").next().html()=="保存"){
                $('.btn-select-photo').show();
                $('.btn-change-photo').hide();
                $('.btn-delete-photo').hide();
                $("#roomform .form-group").removeClass('has-danger');
                $('.error-msg').text('');
                $('.photo img').attr('src', "../static/images/default@1x.png");
                imageObj={//置空图片对象
                    imageState:0, //记录图片修改的状态
                    imageSize:0,  //记录图片的大小
                };
                $('#roomform')[0].reset();
            }
        }
    });
    //点击修改会议室按钮
    $('body').on('click', '.btn-room-edit', function (e) {
        isEdit = 1;
        $("#roomform .form-group").removeClass('has-danger');
        $('.error-msg').text('');
        $(".add-room-tittle").html("修改会议室");
        $(".btn-cancel").hide();
        $(".btn-cancel").next().html("保存");


        //拿到当前会议室的内容 赋值给表单
        var id = $(this).data('id'); //拿到会议室id
        var room = rooms.list.filter(function (item) {
            return item.id == id;
        });
        $("#roomform select.form-control").html("");
        var selectedHtml = "";
        for (var i = 1; i < areas.length; i++) {
            if (room[0].areaId == areas[i].id) {
                $("#type-name").text(areas[i].name);
                if(page.aid==0){
                    areaId=room[0].areaId
                    $(".areaId").val(areaId);
                }else {
                    $(".areaId").val(page.aid);
                }

            }
            selectedHtml += "<a class='dropdown-item'  id=" + areas[i].id+"  data-areaId=" + areas[i].id + " data-areaName="+areas[i].name+">"+areas[i].name+"</a>";
        }
        $("#roomform .area-type").html(selectedHtml);

        var deviceArray=room[0].deviceServices;
        for (i in deviceArray) {
            switch (deviceArray[i]) {
                case '001':
                    $("#roomform")[0].elements['devices2'].checked = true;
                    break;
                case '002':
                    $("#roomform")[0].elements['devices3'].checked = true;
                    break;
                case '003':
                    $("#roomform")[0].elements['devices1'].checked = true;
                    break;
                case '004':
                    $("#roomform")[0].elements['devices0'].checked = true;
                    break;
                default:
                    return true;
            }
        }

        switch ( room[0].reservationStatus) {
            case '0':
                $("#roomform")[0].elements['limitMembers0'].checked = true;
                break;
            case '1':
                $("#roomform")[0].elements['limitMembers1'].checked = true;
                break;
            case '2':
                $("#roomform")[0].elements['limitMembers2'].checked = true;
                break;
            case '3': {
                $("#roomform")[0].elements['limitMembers1'].checked = true;
                $("#roomform")[0].elements['limitMembers2'].checked = true;
            }
                break;
            default:
                return true;
        }

        $("#roomform")[0].elements['room_name'].value = room[0].name;
        $("#roomform")[0].elements['room_capacity'].value = room[0].persionNumber ? room[0].persionNumber : "";
        $("#roomform")[0].elements['room_location'].value = room[0].location!="无"?room[0].location :" ";
        $("#roomform")[0].elements['audit'].checked =room[0].isAudit=="1"?true:false;
        $("#roomform")[0].elements['id'].value = id;
        $("#roomform")[0].elements['areaId'].value = page.aid;
        if (room[0].bigPicture){
            $("#room_image").attr("src",room[0].bigPicture);
            $('.btn-select-photo').hide();
            $('.btn-change-photo').show();
            $('.btn-delete-photo').show();
        }else {
            $('.photo img').attr('src', "../static/images/default@1x.png");
        }
        $('.add-room').addClass('show');
        e.stopPropagation();
    });
    //点击重新上传图片
    $('body').on('change', '.btn-change-photo .upload', function () {
        verifyImage($(this),1);
    });
    //点击选择图片
    $('body').on('change', '.btn-select-photo .upload', function () {
        verifyImage($(this),0);
    });
    //验证图片
    function verifyImage(self,type){
        imageObj.imageState=1;
        var file = self.get(0).files[0];
        var extStart = file.name.lastIndexOf(".");
        var ext = file.name.substring(extStart, file.name.length).toUpperCase();
        self.remove();
        var html = '<input class="change-photo-btn upload" type="file" accept="image/jpg,image/png,image/jpeg,image/bmp" style="display:none;">';
        if(type==1){
            $(".btn-change-photo .label").append(html) ;
        }else {
            $(".btn-select-photo .label").append(html) ;
        }

        if(ext != ".BMP" && ext != ".PNG" && ext != ".JPEG" && ext != ".JPG"){
            notify('danger','仅支持jpg、jpeg、png、bmp格式');
        }else{
            imageObj.imageSize=file.size;
            var size = file.size; //得到的是字节
            var maxSize = 5*1024*1024;
            if (size > maxSize) {
                notify("danger", "上传的图片大小不能大于5M");
            }else{
                var blob = URL.createObjectURL(file);
                imageObj.imageUrl=file;
                $('.photo img').attr('src', blob);
                $('.btn-select-photo').hide();
                $('.btn-change-photo').show();
                $('.btn-delete-photo').show();
            }
        }
    }
    //监听搜索框键盘事件
    $('.search input').on('keypress', function(e) {
        if (e.keyCode==13) {
            if($(this).val().length>0){
                $(".icon-search-del").show();
            }else{
                $(".icon-search-del").hide();
            }
            $('.input-search').removeClass('input-heighLight')
            $(this).blur();
            page.page = 1;
            page.size = 15;
            page.aid=0;
            searchKey=$('.search input').val();
            if(searchKey==""){
                searchState=0; //取消搜索状态
                getData();
            }else {
                searchState=1; //记录搜索状态
                searchData(searchKey);
            }
            e.preventDefault();//阻止默认事件
        };
    })
    $('#searchRoom').bind('focus','input',function () {
        $('.input-search').addClass('input-heighLight')
        // $('#del-searchList').hide();
    })
    $('#searchRoom').bind('blur','input',function () {
        $('.input-search').removeClass('input-heighLight')
    })

    $('.search input').bind('input propertychange', function() {
        if($(this).val().length>0){
            $(".icon-search-del").show();
        }else{
            $(".icon-search-del").hide();
        }
    });

    $(".icon-search-del").click(function () {
        $('.search input').val("");
        $(this).hide();
        searchKey="";
        searchState=0; //取消搜索状态
        getData();
    });
    //提交创建会议室表单
    $("#roomform").submit(function (e) {

        if ( e && e.stopPropagation ){ //处理兼容性问题
            //因此它支持W3C的stopPropagation()方法
            e.stopPropagation();
        } else{
            //否则，我们需要使用IE的方式来取消事件冒泡
            window.event.cancelBubble = true;
        }
        var $this = $(this);
        var $inputs = $(this).find('input[data-validate]');
        var isvalidate = false;
        $.when($inputs.each(function (el) {
            isvalidate = validate($(this));
            return isvalidate;
        })).then(function (ev) {
            if (isvalidate) {
                var data = eval('(' + '{' + decodeURIComponent($this.serialize(), true).replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
                if(data.areaId==0){
                   data.areaId= areaId;
                }
                var isAudit = "";
                if (!data.audit) {
                    isAudit = 0;
                } else {
                    isAudit = 1;
                }
                var deviceService = "";
                if (data.devices0 == "0") {
                    deviceService +="004,";
                }
                if (data.devices1 == "1") {
                    deviceService+="003,";
                }
                if (data.devices2 == "2") {
                    deviceService+="001,";
                }
                if (data.devices3 == "3") {
                    deviceService+="002,";
                }
                var roleId = "";
                if (data.limitMembers0 == "0") {
                    roleId+="0,";
                }
                if (data.limitMembers1 == "1") {
                    roleId+="1,";
                }
                if (data.limitMembers2 == "2") {
                    roleId+="2,";
                }
                var  insertRoomObj={
                    name: data.room_name,
                    areaId: data.areaId,
                    location: data.room_location,
                    deviceService: deviceService,
                    isAudit: isAudit,
                    roleId: roleId,
                    imageUrl: "",
                    imageSize:imageObj.imageSize,
                    persionNumber: data.room_capacity,
                    id:data.id,
                    file:"",
                };
                if(isEdit!=0){ //修改会议室模型
                    insertRoomObj.imageState = imageObj.imageState;
                }
                var imgSrc= $('.photo img').attr('src');
                if(imgSrc.indexOf("default@1x.png")>=0){
                    insertRoomObj.imageUrl="";
                    submitMeetingRoom(insertRoomObj);
                    return;
                }
                if(imageObj.imageState==0){ //图片不变
                    submitMeetingRoom(insertRoomObj);
                }else if(imageObj.imageState==1){ //图片修改
                    insertRoomObj.file=imageObj.imageUrl;
                    submitMeetingRoom(insertRoomObj);
                }
            }
        });
        e.preventDefault();
    });

    //删除/修改   checkBox
    $("input[name^='limitMembers']").click(function(){
        if($(this).val()==0){
            if(this.checked==true){
                document.getElementsByName("limitMembers1")[0].checked=false;
                document.getElementsByName("limitMembers2")[0].checked=false;
            }
        }else {
            if(this.checked==true){
                document.getElementsByName("limitMembers0")[0].checked=false;
            }
        }
    });
    function submitMeetingRoom(formData) {
        $("#save-btn").attr({"disabled":"disabled"});
        if (isEdit == 0) {
            //添加会议室
            fetchs.uploadMixture('/meetingRoom/insertMeetingRoomModify',formData,function (data) {
                if (data.ifSuc == 1) {
                    notify('success', '添加成功');
                    updateArea();
                    setAreaRoom(0);
                    $('.btn-select-photo').show();
                    $('.btn-change-photo').hide();
                    $('.btn-delete-photo').hide();
                    // $('.add-room').removeClass('show');
                    $('.photo img').attr('src', "../static/images/default@1x.png");
                    imageObj={//置空图片对象
                        imageState:0, //记录图片修改的状态
                        imageSize:0,  //记录图片的大小
                        imageName:"",  //记录图片的名称
                    };
                    $("#roomform")[0].reset(); //提交成功后,置空表格
                    $("#roomform")[0].elements['limitMembers0'].checked = true;
                } else {
                    notify('danger', data.msg);
                }
                $("#roomform .form-group").removeClass('has-danger');
                $('.error-msg').text('');
                $("#save-btn").removeAttr("disabled");
            })

        } else if (isEdit == 1) {
            //修改会议室
            fetchs.uploadMixture('/meetingRoom/updateMeetingRoomModify',formData,function (data) {
                if (data.ifSuc == 1) {
                    notify('success', '修改成功');
                    setAreaRoom(0);
                    updateArea();
                    $('.btn-select-photo').show();
                    $('.btn-change-photo').hide();
                    $('.btn-delete-photo').hide();
                    $('.add-room').removeClass('show');
                    $("#roomform .form-group").removeClass('has-danger');
                    $('.error-msg').text('');
                    $('.photo img').attr('src', "../static/images/default@1x.png");
                    imageObj={//置空图片对象
                        imageState:0, //记录图片修改的状态
                        imageSize:0,  //记录图片的大小
                        imageName:"",  //记录图片的名称
                    };
                    $("#roomform")[0].reset(); //提交成功后,置空表格
                } else {
                    notify('danger', data.msg);
                    setTimeout(gotoLogin,3000);
                }
                $("#save-btn").removeAttr("disabled");
            })
        }
    }

    //表单提交验证
    function rule(type, val) {
        switch (type) {
            case 'required':
                return val.trim() == '' ? '不能为空' : true;
                break;
            case 'location':
                return val.trim() == '' ? '' : !/^1(3|4|5|7|8)\d{9}$/.test(val) ? '手机号码格式不正确' : true;
                break;
            case 'capacity':
                return val.trim() == ''||val.trim() == '0' ? '容量不能为空' : !/^[0-9]{0,4}$/.test(val) ?'容量范围为1-9999':true;
                break;
            case 'name':
                return val.trim() == '' ? '会议室名称不能为空' : !/^[\u4e00-\u9fa5a-zA-Z0-9\\\-()（）]{0,31}$/.test(val) ? '会议室名称不能含有特殊字符' : true;
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
            $el.parents('form').find('p.error-msg').html("<i class='icon icon-error'></i><span>"+msg+"</span>");
        }else {
            $el.parent('.form-group').removeClass('has-danger');
            if($el.parents('form').find('.has-danger').length==0){
                // $el.parents('form').find('p.error-msg').html("<a class='icon icon-error'><span>"+msg+"</span></a>");
                return true;
            }
        }
    }
    function gotoLogin() {
        window.location.href="/system/loginpage";
    }
    function getNowDate() {
        var date = new Date();
        var seperator = '-';
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = '0' + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = '0' + strDate;
        }
        var currentdate = year + seperator + month + seperator + strDate;
        return currentdate;
    }

    //-------------------预定设置表单操作
    var bookingform = '' +
        '<form id="bookingform" enctype="multipart/form-data">'+
        '<div class="form-group row">'+
        '<label class="col-3 col-form-label">\u53EF\u9884\u8BA2\u65F6\u95F4</label>'+
        '<div class="col-9">'+
        '<div class="dropdown"  style="float:left">'+
        '<div class="form-control form-control-lg" id="reserveTimeStart" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true">' +
        '<span id="start-time">{{data.reserveTimeStart|date:"HH:mm"}}</span>'+
        '<i class="icon icon-organiz-down"></i>'+
        '<input name="reserveTimeStart" type="hidden" class="hidden-val" value="">' +
        '</div>'+
        '<div class="dropdown-menu start-select-box start-select-box-1" aria-labelledby="reserveTimeStart">' +
        '<a class="dropdown-item" name="6">06:00</a><a class="dropdown-item" name="7">07:00</a>'+
        '<a class="dropdown-item" name="8">08:00</a><a class="dropdown-item" name="9">09:00</a>'+
        '<a class="dropdown-item" name="10">10:00</a><a class="dropdown-item" name="11">11:00</a>'+
        '</div>'+
        '</div>'+
        '<span class="select-label" style="padding:0 24px">\u81F3</span>' +
        '<div class="dropdown" style="float:left">'+
        '<div class="form-control form-control-lg" id="reserveTimeEnd" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true">' +
        '<span id="end-time">{{data.reserveTimeEnd|date:"HH:mm"}}</span>'+
        '<i class="icon icon-organiz-down"></i>'+
        '<input name="reserveTimeEnd" type="hidden" class="hidden-val" value="">' +
        '</div>'+
        '<div class="dropdown-menu start-select-box start-select-box-2" aria-labelledby="reserveTimeEnd">' +
        '<a class="dropdown-item" name="17">17:00</a><a class="dropdown-item" name="18">18:00</a>'+
        '<a class="dropdown-item" name="19">19:00</a><a class="dropdown-item" name="20">20:00</a>'+
        '<a class="dropdown-item" name="21">21:00</a><a class="dropdown-item" name="22">22:00</a>'+
        '<a class="dropdown-item" name="23">23:00</a>'+
        '</div>'+
        '</div>' +
        '</div>'+
        '</div>'+
        '<div class="form-group row">' +
        '<label class="col-3 col-form-label">\u5355\u6B21\u4F1A\u8BAE\u6700\u5927\u65F6\u957F</label>' +
        '<div class="col-9">' +
        '<div class="dropdown" style="float:left">'+
        '<div class="form-control form-control-lg" id="meetingMaximum" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true">' +
        '<span id="end-time">{{data.meetingMaximum}}</span>'+
        '<i class="icon icon-organiz-down"></i>'+
        '<input name="meetingMaximum" type="hidden" class="hidden-val" value="{{data.meetingMaximum}}">' +
        '</div>'+
        '<div class="dropdown-menu start-select-box start-select-box-3" aria-labelledby="meetingMaximum">' +
        '<a class="dropdown-item" name="0">\u65E0\u9650\u5236</a><a class="dropdown-item" name="1">1</a>'+
        '<a class="dropdown-item" name="2">2</a><a class="dropdown-item" name="3">3</a>'+
        '<a class="dropdown-item" name="4">4</a><a class="dropdown-item" name="5">5</a>'+
        '<a class="dropdown-item" name="6">6</a><a class="dropdown-item" name="7">7</a>'+
        '<a class="dropdown-item" name="8">8</a>'+
        '</div>'+
        '</div>' +
        '<span class="select-label">\u5C0F\u65F6</span>' +
        '</div>' +
        '</div>' +
        '<div class="form-group row">' +
        '<label class="col-3 col-form-label">\u5355\u6B21\u4F1A\u8BAE\u6700\u5C0F\u65F6\u957F</label>' +
        '<div class="col-9">' +
        '<div class="dropdown" style="float:left">'+
        '<div class="form-control form-control-lg" id="meetingMinimum" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true">' +
        '<span id="end-time">{{data.meetingMinimum}}</span>'+
        '<i class="icon icon-organiz-down"></i>'+
        '<input name="meetingMinimum" type="hidden" class="hidden-val" value="{{data.meetingMinimum}}">' +
        '</div>'+
        '<div class="dropdown-menu start-select-box start-select-box-4" aria-labelledby="meetingMinimum">' +
        '<a class="dropdown-item" name="15">15</a><a class="dropdown-item" name="20">20</a>'+
        '<a class="dropdown-item" name="30">30</a><a class="dropdown-item" name="45">45</a>'+
        '<a class="dropdown-item" name="60">60</a>'+
        '</div>'+
        '</div>' +
        '<span class="select-label">\u5206\u949F</span>' +
        '</div>' +
        '</div>' +
        '<div class="form-group row">' +
        '<label class="col-3 col-form-label">\u4F1A\u8BAE\u53EF\u9884\u8BA2\u5468\u671F</label>' +
        '<div class="col-9">' +
        '<div class="dropdown" style="float:left">'+
        '<div class="form-control form-control-lg" id="reserveCycle" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true">' +
        '<span id="end-time">{{data.reserveCycle}}</span>'+
        '<i class="icon icon-organiz-down"></i>'+
        '<input name="reserveCycle" type="hidden" class="hidden-val" value="{{data.reserveCycle}}">' +
        '</div>'+
        '<div class="dropdown-menu start-select-box start-select-box-5" aria-labelledby="reserveCycle">' +
        '<a class="dropdown-item" name="0">\u65E0\u9650\u5236</a><a class="dropdown-item" name="60">1</a>'+
        '<a class="dropdown-item" name="3">3</a><a class="dropdown-item" name="7">7</a>'+
        '<a class="dropdown-item" name="15">15</a><a class="dropdown-item" name="30">30</a>'+
        '<a class="dropdown-item" name="60">60</a><a class="dropdown-item" name="90">90</a>'+
        '</div>'+
        '</div>' +
        '<span class="select-label">\u5929</span>' +
        '</div>' +
        '</div>' +
        '<div class="form-group row"ng-if="isPay==1"><label class="col-3 col-form-label">会议显示提前开始时间</label><div class="col-9"><div class="dropdown" style="float:left"><div class="form-control form-control-lg" id="meetingBeforeStart" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true"><span>{{data.beforeTime}}</span><i class="icon icon-organiz-down"></i><input name="minutes" type="hidden" class="hidden-val" value="0"></div><div class="dropdown-menu start-select-box start-select-box-6" aria-labelledby="meetingBeforeStart"><a class="dropdown-item" name="0">0</a><a class="dropdown-item" name="15">15</a><a class="dropdown-item" name="30">30</a><a class="dropdown-item" name="45">45</a><a class="dropdown-item" name="60">60</a></div></div><span class="select-label">分钟</span></div></div>'+

        '<div class="form-group row" ng-if="isPay==1">' +
        '<label class="col-3 col-form-label">签到二维码更新频率</label>' +
        '<div class="col-9">' +
        '<div class="dropdown" style="float:left">'+
        '<div class="form-control form-control-lg" id="qrDuration" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true">' +
        '<span id="end-time">{{data.qrDuration}}</span>'+
        '<i class="icon icon-organiz-down"></i>'+
        '<input name="qrDuration" type="hidden" class="hidden-val" value="{{data.qrDuration}}">' +
        '</div>'+
        '<div class="dropdown-menu start-select-box start-select-box-7" aria-labelledby="qrDuration">' +
        '<a class="dropdown-item" name="0">无限制</a><a class="dropdown-item" name="5">5</a>'+
        '<a class="dropdown-item" name="10">10</a><a class="dropdown-item" name="30">30</a><a class="dropdown-item" name="60">60</a>'+
        '</div>'+
        '</div>' +
        '<span class="select-label">秒</span><span class="no">防止复制二维码签到</span>' +
        '</div>' +
        '</div>' +
        '<div class="form-group row" ng-if="0"><label class="col-3 col-form-label">\u4F1A\u8BAE\u91CA\u653E\u8BBE\u7F6E</label>  <div class="col-9"><span class="no">\u8BE5\u7248\u672C\u6682\u4E0D\u652F\u6301</span></div></div>' +
        '<div class="form-group row"  ng-if="isPay==1"><label class="col-3 col-form-label">手动签到设置</label>  <div class="col-3"><label class="col custom-control custom-checkbox"><input class="custom-control-input signBox" name="signBox" type="checkbox" value="0" checked="checked"><span class="custom-control-indicator signControl"></span><span class="custom-control-description">开启手动签到</span> </label></div><span class="no signDesc">可关闭web、H5端手动签到功能，扫码签到需发布带二维码的节目至终端</span></div>' +
        '<div class="form-group row"><label class="col-3 col-form-label">\u4F1A\u8BAE\u53EF\u63D0\u524D\u5F00\u59CB\u65F6\u95F4</label><div class="col-9"><span class="no">\u8BE5\u7248\u672C\u6682\u4E0D\u652F\u6301</span></div></div>' +
        '<div class="form-group">  <button class="btn btn-lg btn-primary book-setting-btn" type="submit">保存</button></div></form>';
    $('.tab-business li a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        if ($(this).text() == '预订设置') {
            fetchs.post('/meetingRoom/selectMeetingRoomReserve',{}, function (result) {
                if(result.ifSuc==1){
                    result.data.beforeTime=result.data.minutes==null? 0:result.data.minutes;
                    result.isPay= userState;
                    $('#profile').html(soda(bookingform, result));
                    $('.start-select-box').on('click','a',function () {
                        $(this).addClass('select-bg-color').siblings().removeClass('select-bg-color');;
                        $(this).parent().parent().find('.form-control>span').text($(this).text());
                        $(this).parent().parent().find('.form-control .hidden-val').val($(this).text());
                        $('#bookingform .btn').attr('disabled', false);
                    });
                    $(".signBox").change(function() {
                        console.log( $(".signBox").is(':checked')?1:0);
                        $('#bookingform .btn').attr('disabled', false);
                    });
                    $('#bookingform .btn').attr('disabled', true);
                    $('input[name="reserveTimeStart"]').val(gettime(result.data.reserveTimeStart));
                    $('input[name="reserveTimeEnd"]').val(gettime(result.data.reserveTimeEnd));
                    $('input[name="minutes"]').val(result.data.beforeTime);
                    if(result.data.meetingMaximum==0){
                        $('#meetingMaximum span').text('无限制');
                    };
                    if(result.data.reserveCycle==0){
                        $('#reserveCycle span').text('无限制');
                    };
                    if(result.data.qrDuration==0){
                        $('#qrDuration span').text('无限制');
                    };
                    // if循环的另一种高效书写形式:
                    parseInt(result.data.signSet) == 0 && $('input[name="signBox"]').attr('checked',false);

                    $(".start-select-box-1").find("a.dropdown-item[name='"+new Date(result.data.reserveTimeStart).getHours()+"']").addClass('select-bg-color');
                    $(".start-select-box-2").find("a.dropdown-item[name='"+new Date(result.data.reserveTimeEnd).getHours()+"']").addClass('select-bg-color');
                    $(".start-select-box-3").find("a.dropdown-item[name='"+result.data.meetingMaximum+"']").addClass('select-bg-color');
                    $(".start-select-box-4").find("a.dropdown-item[name='"+result.data.meetingMinimum+"']").addClass('select-bg-color');
                    $(".start-select-box-5").find("a.dropdown-item[name='"+result.data.reserveCycle+"']").addClass('select-bg-color');
                    $(".start-select-box-6").find("a.dropdown-item[name='"+result.data.beforeTime+"']").addClass('select-bg-color');
                    $(".start-select-box-7").find("a.dropdown-item[name='"+result.data.qrDuration+"']").addClass('select-bg-color');
                    //预定设置表单提交
                    $('#bookingform').on('submit', function (event) {
                        event.preventDefault();
                        var $this = $(this);
                        var data = eval('(' + '{' + decodeURIComponent($this.serialize(), true).replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
                        var currentdate = getNowDate();
                        data.reserveTimeStart = currentdate + ' ' + data.reserveTimeStart+":"+"00";
                        data.reserveTimeEnd = currentdate + ' ' + data.reserveTimeEnd+":"+"00";
                        if(data.meetingMaximummeetingMaximum=="无限制"){
                            data.meetingMaximum=0;
                        };
                        if(data.reserveCycle=="无限制"){
                            data.reserveCycle=0;
                        };
                        if(data.qrDuration=="无限制"||!data.qrDuration){
                            data.qrDuration=0;
                        }
                        data.signSet = $(".signBox").is(':checked')?1:0;
                        fetchs.post('/meetingRoom/meetingRoomReserve', data, function (result) {
                            if (result.ifSuc == 1) {
                                notify('success', '保存成功');
                                $('#bookingform').change(function () {
                                    $('#bookingform .btn').attr('disabled', false);
                                });
                                $('#bookingform .btn').attr('disabled', true);
                            }else {
                                notify('danger', '保存失败');
                            }
                        });
                    });
                }else {
                    notify('danger', '请求异常');
                }
            });
        }
    });
});
function gettime(time){
    var date_time = new Date(time);
    date_time.setTime(time);
    var hour = date_time.getHours();
    var minute= date_time.getMinutes();
    if(hour<10){
        hour='0'+hour;
    };
    if(minute<10){
        minute='0'+minute;
    }
    var time_str =hour +':'+minute;
    return time_str;
}
//重命名
function didClickRename(editId,tId) {

    var node = zTreeObj.getNodeByParam('id', editId, null); //根据新的id找到新添加的节点
    oldName=node.name;
    var treeNodeId="#"+tId.id+" "+".dimBgc:first";
    $(treeNodeId).hide();
    zTreeObj.editName(node);
    document.getElementsByClassName("rename")[0].maxLength=20;
}
//添加区域
function didClickAddDepartment(editId) {
    //添加区域的同时,让区域处于可编辑状态
    var c = ++count;
    var node = zTreeObj.getNodeByParam('id', editId, null);
    var newNode = { id: c, name: '新建区域',isDefault:"0"};
    zTreeObj.addNodes(node, -1, newNode);
    var node1 = zTreeObj.getNodeByParam('id', c, null); //根据新的id找到新添加的节点
    renameTag = 1;
    var treeNodeId="#"+node1.tId+" "+".dimBgc:first";
    $(treeNodeId).hide();
    zTreeObj.editName(node1);
    document.getElementsByClassName("rename")[0].maxLength=20;
    document.getElementsByClassName("rename")[0].select();
}
//点击删除(图片框)
function delete_photo() {
    imageObj.imageState=2;
    $('.photo img').attr('src', "../static/images/default@1x.png");
    $('.btn-select-photo').show();
    $('.btn-change-photo').hide();
    $('.btn-delete-photo').hide();
}
//取消添加会议室
function btn_cancel() {
    $('.btn-select-photo').show();
    $('.btn-change-photo').hide();
    $('.btn-delete-photo').hide();
    $("#roomform .form-group").removeClass('has-danger');
    $('.error-msg').text('');
    $('.photo img').attr('src', "../static/images/default@1x.png");
    imageObj={//置空图片对象
        imageState:0, //记录图片修改的状态
        imageSize:0,  //记录图片的大小
        imageName:""  //记录图片的名称
    };
    $('#roomform')[0].reset();
    $('.add-room').removeClass('show');
}

//下载会议室二维码
function downloadQrcodeClick() {
    downloadQrcode(page.aid,function (data) {
        console.log(data);
    })
}
//# sourceMappingURL=room.js.map
