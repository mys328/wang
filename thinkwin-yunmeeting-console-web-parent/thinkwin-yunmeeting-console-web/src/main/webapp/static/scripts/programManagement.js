var zTreeObj;
var oldName="";//记录编辑之前的名字
var zTree = $.fn.zTree.getZTreeObj('treeDemo'); //拿到初始化的ztree
var renameTag = 0; //用于区分是新建名称编辑 和 对之前组织机构的名称进行编辑
$(function(){
    //节目管理  数据
    var pageObj = {
        "searchKey":"",//搜索关键字
        "aid":"", //标签id
        "tId":"",
        "tagList":[],//标签数据
        "currentTagNumber":"",//当前标签下节目数量
        "tags":[],//格式化标签list数据
        "programList":[],//节目数据
        "dataList":[],//节目累加数据
        "currentPage":1,//当前页
        "pageSize":0,//分页条数
        "initProgramTotal":0,//全部节目总数
        "nowVersionNum":"",//当前版本号
        "ver":"",//版本管理锁标志
        "currentProgram":"",//当前选择的节目数据
        "delete_type":0,//删除节目的状态 0 批量 1 删除单个
        "program_id":[],//已选中的节目id
        "batchOperationState":'', //0草稿 1内测 2已同步
        "searchState":0,//0未搜索 1搜索状态
        "pageDataState":0,//0数据不累加 1数据累加
        "programTpl":'',//节目模板
        "programBox":'',
    };
    var tab_type=0;
    var imageObj ={//记录图片的大小
        imageSize:0,
        imageUrl:'',
        file:""
    };
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
            onRename: onRename, //重命名后的事件回调
            beforeRemove: beforeRemove, //删除机构前的事件回调
            onRemove: onRemove,//删除标签的回调函数
            beforeDrag: beforeDrag,//拖拽前的回调函数
            beforeDrop: beforeDrop,//拖拽放下前的回调
            onClick: onClick,//点击标签的回调
        },
        view: {
            dblClickExpand: false,
            addDiyDom: addDiyDom,
            showTitle: false
        }
    };
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
        if (targetNode.id== 0&&targetNode.name=="全部标签") {//如果目标节点是全部标签，排序失败
            return false;
        };
        var moveOrgId = treeNodes[0].id;
        var parentId = targetNode.id;
        var moveType;
        if (moveType == 'next') {
            moveType = 2;
        } else if (moveType == 'prev') {
            moveType = 1;
        } else if (moveType == 'inner') {
            moveType = 3;
            return false;
        }
        $.ajaxSetup({
            async : false
        });
        var confirmVal = true;
        organiztionMove( moveOrgId, parentId, moveType, function (data) {
            if (data.ifSuc== 1) {
                notify("success","移动成功");
            }else {
                notify("danger",data.msg);
                confirmVal = false;
                if(data.ifSuc==10){
                    goLoginPage();
                }
            }
            $.ajaxSetup({
                async : true
            });
        });
        return confirmVal;
    }
    //修改名称后的回调函数
    function onRename(event, treeId, treeNode,isCancel) {
        if(treeNode.name==oldName&&renameTag == 0){
            return;
        };
        if(treeNode.name==""){
            if (renameTag == 0) {
                treeNode.name=oldName;
                zTreeObj.updateNode(treeNode);
                renameTag == 1;
            }if(renameTag == 1){
                var node = zTreeObj.getNodeByParam('id', treeNode.id, null);
                zTreeObj.removeNode(node, true);
                renameTag == 0;
            }
            //zTreeObj .updateNode(treeNode);
            return false;
        }else{
            var isTrue = !/^[\u4e00-\u9fa5a-zA-Z0-9\\\-()（）\_]{0,10}$/.test(treeNode.name.trim())?  false : true ;
            if(isTrue){
                if(renameTag == 0) {//修改标签
                    updateOrganiztion(treeNode.id,treeNode.name,treeNode.ver,function (data) {
                        if (data.ifSuc == 1) {
                            notify("success","修改成功");
                        }else {
                            treeNode.name=oldName;
                            zTreeObj .updateNode(treeNode);
                            notify("danger",data.msg);
                            if(data.ifSuc==10){
                                goLoginPage();
                            }
                        }
                        oldName="";
                        getAddressList();
                    });
                }else if(renameTag==1){
                    saveOrganization(treeNode.name, function (data) {
                        if (data.ifSuc == 1) {
                            renameTag = 0;
                            var node = zTreeObj.getNodeByParam('id', treeNode.pId, null);
                            zTreeObj.removeNode(treeNode, false);
                            var newNode = { id: data.data.newLabelId, name:treeNode.name };
                            zTreeObj.addNodes(node, -1, newNode);
                            notify("success","添加成功");
                            btnUsingState($("#addLabel"),false);
                        }else {
                            renameTag = 0;
                            btnUsingState($("#addLabel"),false);
                            var node = zTreeObj.getNodeByParam('id', treeNode.id, null);
                            zTreeObj.removeNode(node, true); //第二个参数  是否要触发删除子机构的回调函数
                            notify("danger",data.msg);
                        }
                        getAddressList();
                    });
                }
            }else{
                notify('danger', '输入内容不能包含特殊字符');
                if(renameTag == 0) {//修改标签
                    treeNode.name=oldName;
                    zTreeObj.updateNode(treeNode);
                }else if(renameTag==1){
                    var node = zTreeObj.getNodeByParam('id', treeNode.id, null);
                    zTreeObj.removeNode(node, true); //第二个参数  是否要触发删除子机构的回调函数
                    btnUsingState($("#addLabel"),false);
                    return false;
                }
            }
        };
        var treeNodeId="#"+treeNode.tId+" "+".dimBgc:first";
        if ($("#"+treeNode.tId).children().is('.on')==false){
            $(treeNodeId).show();
        }
    }
    //删除标签前的回调函数
    function beforeRemove(treeId, treeNode) {
    }
    //删除标签的回调函数
    function onRemove(e, treeId, treeNode) {
    }
    //组织机构的单击事件
    function onClick(e, treeId, treeNode) {
        var zTree = $.fn.zTree.getZTreeObj('treeDemo');
        $(".on").removeClass("on");
        //取消搜索状态 新修改的
        $('#search').val("");
        $('#del-searchList').hide();
        pageObj.programList=[];
        pageObj.dataList=[];
        pageObj.program_id=[];
        pageObj.searchKey="";
        pageObj.pageDataState=0;
        pageObj.aid=treeNode.id;
        pageObj.tId = treeNode.tId;
        $("#"+treeNode.tId+" "+".dimBgc:first").hide();
        $('#' + treeNode.tId + ' >div#heightLight').addClass("on");
        $('body #eventManaging .program-box').empty();
        //计算当前标签下总数
        if(pageObj.aid){
            $.each(pageObj.tagList,function(item,ele){
                if(ele.id==pageObj.aid){
                    pageObj.currentTagNumber=ele.platformProgrameNum;
                }
            });
        }else{
            pageObj.currentTagNumber=pageObj.initProgramTotal;
        }
        getAddressList(0,1);
    }
//自定义更多按钮
    function addDiyDom(treeId, treeNode) {
        var heightLightTpl = '<div id="heightLight" class="heightLight" ></div>';
        $('#' + treeNode.tId + ' > span').before(heightLightTpl);
        var editId = '"'+treeNode.id+ '"';
        var name= '"'+treeNode.name + '"';
        var menu ='<button class=\'dropdown-item editStyle\' onclick=\'didClickRename(' + editId + ','+treeNode.tId+')\')\'>重命名</button><button class=\'dropdown-item editStyle\'  onclick=\'didClickDeletedDepartment(' + editId + ','+name+')\'>删除</button>';
        // if($('#diyBtn_' + treeNode.id).length > 0) return;
        var editStr = '<div class=\'dropdown\' id=\'dropdown\' onclick=\'didClickEdit(' + editId + ')\' >  <div class=\'editBtn\' id=\'diyBtn_' + treeNode.id + '\'  data-toggle=\'dropdown\' ><a class=\'icon icon-organiz-more\'></a><div class=\'selectedRow\'></div></div><div class=\'dropdown-menu dropdown-menu-right\' id=\'dropdown-menu\' aria-labelledby=\'diyBtn_' + treeNode.id + '\'   aria-haspopup=\'true\' aria-expanded=\'false\'>' + menu + '</div><div class=\'dimBgc\'></div>';
        if(treeNode.id!==0){
            if(!tab_type){
                $('#'+treeNode.tId+'>a').after(editStr);
            }
        }
        $('#'+treeNode.tId).attr({"data-toggle":"tooltip","data-title":treeNode.name+"  "+treeNode.totalLabel});
        if(treeNode.totalLabel>=0){
            var child='#' + treeNode.tId + ' > a span:nth-child(2)';
            var areaSpan = $(child);
            var totalLabel = "<label class='totalLabel'>" + treeNode.totalLabel +"<label>";
            areaSpan.after(totalLabel);
        }
        //选中效果
        if(!pageObj.aid||pageObj.aid==""){
            $('#'+"treeDemo_1"+'>div#heightLight').addClass("on");
            var treeNodeId="#"+treeNode.tId+" "+".dimBgc:first";
            $(treeNodeId).hide();
        }
    };
    //初始化请求组织机构树的接口
    var arrM = [];
    //节目模板
    pageObj.programTpl='<div class="program-box"><div class="list-body">' +
        '<div class="program" data-id="{{item.id}}" ng-repeat="item in dataList">'+
        '<div class="shade program-leave"></div>'+
        '<div class="img-box">' +
        '<img  src="{{item.photoUrl}}">'+
        '<button class="btn btn-primary btn-sm preview-btn" data-id="{{item.id}}"  data-ver="{{item.ver}}" style="display: none;">预览</button>'+
        '<span class="program-state" ng-if="item.programStatus==0" data-state="{{item.programStatus}}">草稿</span>'+
        '<span class="program-state" ng-if="item.programStatus==2" data-state="{{item.programStatus}}">内测</span>'+
        '</div>'+
        '<p>' +
        '<span class="headline" ng-html="item.programName|keylight:searchKey"></span>' +
        '<span class="versions-num" ng-if="item.programVersionNum">V{{item.programVersionNum}}</span>' +
        '<span class="custom-control custom-checkbox" data-isTrue="{{item.isSelected}}">'+
        '<input class="custom-control-input" type="checkbox" name="checkname"><span class="custom-control-indicator"></span></span>' +
        '</p>'+
        '</div>' +
        '<div ng-if="dataList.length<1&&searchKey" class="nothing">搜索不到您需要的内容</div><div ng-if="dataList.length<1&&!searchKey" class="nothing">还没有上传节目文件</div></div></div>'
    var scrollTpl='<div class="program" data-id="{{item.id}}" ng-repeat="item in programList">'+
        '<div class="shade program-leave"></div>'+
        '<div class="img-box">' +
        '<img  src="{{item.photoUrl}}">'+
        '<button class="btn btn-primary btn-sm preview-btn" data-id="{{item.id}}" data-ver="{{item.ver}}" style="display: none;">预览</button>'+
        '<span class="program-state" ng-if="item.programStatus==0" data-state="{{item.programStatus}}">草稿</span>'+
        '<span class="program-state" ng-if="item.programStatus==2" data-state="{{item.programStatus}}">内测</span>'+
        '</div>'+
        '<p>' +
        '<span class="headline" ng-html="item.programName|keylight:searchKey"></span>' +
        '<span class="versions-num" ng-if="item.programVersionNum">V{{item.programVersionNum}}</span>' +
        '<span class="custom-control custom-checkbox" data-isTrue="{{item.isSelected}}">'+
        '<input class="custom-control-input" type="checkbox" name="checkname"><span class="custom-control-indicator"></span></span>' +
        '</p>'+
        '</div>';
//查询全部标签接口
    function getAddressList(isScroll,tag) {//isScroll 是否滚动 tag 是否更新列表
        if(isScroll != 1){
            getPageSize();
        }
        fetchs.post('/selPlatformProgrameLabels',{'platformProgrameLabelId':pageObj.aid,'type':tab_type,"currentPage":pageObj.currentPage,"pageSize":pageObj.pageSize},function(result){
            if(result.ifSuc==1){
                var length = pageObj.dataList.length;
                pageObj.program_id=[];//重新渲染后清空已选择的节目
                pageObj.tagList=result.data.platformProgrameLabes; //标签list
                pageObj.programList= result.data.platformProgrames.list; //节目list
                pageObj.dataList=pageObj.dataList.concat(result.data.platformProgrames.list)
                pageObj.dataList= _.uniq(pageObj.dataList, false, function (item) {
                    return item.id;
                });
                pageObj.programList =  pageObj.dataList.slice(length);
                pageObj.initProgramTotal=result.data.platformProgrameTotalNum;//全部标签下节目总条数
                pageObj.batchOperationState=result.data.batchOperationState;
                pageObj.nowVersionNum=result.data.nowVersionNum;//当前版本号
                pageObj.ver=result.data.ver;//状态锁
                if(tag!=1){//未点击标签
                    pageObj.tags = [];
                    if(pageObj.tagList.length){
                        if(tab_type){
                            for (var i=0, l=pageObj.tagList.length; i<l; i++){
                                pageObj.tags[i]={"id":pageObj.tagList[i].id,"name":pageObj.tagList[i].labelName,"totalLabel":pageObj.tagList[i].platformProgrameNum,"ver":pageObj.tagList[i].ver,"isMove": false,drag:false}
                            };
                            pageObj.tags.unshift({"id":0,"name":"全部节目","totalLabel":result.data.platformProgrameTotalNum,"isMove": false,drag:false});
                        }else{
                            for (var i=0, l=pageObj.tagList.length; i<l; i++){
                                pageObj.tags[i]={"id":pageObj.tagList[i].id,"name":pageObj.tagList[i].labelName,"totalLabel":pageObj.tagList[i].platformProgrameNum,"ver":pageObj.tagList[i].ver}
                            };
                            pageObj.tags.unshift({"id":0,"name":"全部标签","totalLabel":result.data.platformProgrameTotalNum,"isMove": false,drag:false});
                        }
                    }else{
                        if(tab_type){
                            pageObj.tags.unshift({"id":0,"name":"全部节目","totalLabel":pageObj.programList.length,"isMove": false,drag:false});
                        }else{
                            pageObj.tags.unshift({"id":0,"name":"全部标签","totalLabel":pageObj.programList.length,"isMove": false,drag:false});
                        }
                    };
                    $.fn.zTree.init($("#treeDemo"), setting,pageObj.tags);
                    if(pageObj.tId&&pageObj.aid!=""){//已选择的节点
                        $(".on").removeClass("on");
                        $("#"+pageObj.tId+" "+".dimBgc:first").hide();
                        $('#' + pageObj.tId + ' >div#heightLight').addClass("on");
                    };
                    pageObj.currentTagNumber=result.data.platformProgrameTotalNum;
                };
                if(!tab_type) $("#addLabel").show();
                programRander(isScroll);
            }else{
                notify('danger',result.msg);
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
            $.ajaxSetup({async : true});
        })
    }
    //创建组织机构树
    zTreeObj = $.fn.zTree.init($('#treeDemo'), setting, arrM);
    var treeObj = $.fn.zTree.getZTreeObj('treeDemo');
    //添加标签
    $('body .add-label').on('click',function(e){
        didClickAddDepartment();
    })
    //节目的渲染
    function programRander(isScroll){
        if(pageObj.nowVersionNum){
            $('.versions-info').show();
            btnUsingState($(".update-log"),false);
            $('.versions-info i').html("V"+pageObj.nowVersionNum);
        }else {
            $('.versions-info').hide();
            btnUsingState($(".update-log"),true);
        }
        $('.synchronization-btn').hide();
        if(pageObj.initProgramTotal>0){
            switch(pageObj.batchOperationState){
                case "0"://已编辑后
                    $('.test-btn').show().html("进行内测").attr("disabled",false);
                    break;
                case "1"://已内测
                    $('.test-btn').hide();
                    $('.synchronization-btn').show();
                    break;
                case "2"://已发布（不含草稿和内测状态下的）
                    $('.test-btn').show().html("已同步节目").attr("disabled",true);
                    $('.synchronization-btn').hide();
                    break;
                default:
            }
        }else{
            $('.test-btn').show().html("进行内测");
            $('body .synchronization-btn').hide().attr('disabled',false).text('同步到租户');
            btnUsingState($(".test-btn"),true);
        };
        if (isScroll){
            $('body .program-box .list-body').append(soda(scrollTpl,pageObj));
        }else {
            $('body .managing-right').html(soda(pageObj.programTpl,pageObj));
        }
        scrollList();
        var istrue_id = $('body .custom-checkbox[data-istrue="true"]').parents('.program');
        var isTrue = $('body .custom-checkbox');
        $.each(istrue_id,function(index,ele){
            var iscover = remove(pageObj.program_id,ele);
            if(iscover){
                pageObj.program_id.push($(this).attr('data-id'));
            }
        })
        $.each(isTrue,function(item){
            if($(this).data('istrue')){
                $(this).find('input[name="checkname"]').prop("checked",true);
            }
        })
        $('.btns-box .append-btn,.btns-box .delete-btn').hide();
    }
    function  scrollList() {
        //滚动加载 document.getElementsByClassName('scrollView')[0].addEventListener('scroll', function (e) {
        document.getElementsByClassName('program-box')[0].addEventListener('scroll',function(e){
            var target = e.currentTarget;
            var curScrollTop = target.scrollTop;
            var targetHeight = target.offsetHeight;
            var listCntHeight = $(".managing-right .program-box .list-body").height()+ 16;
            var isEnd = curScrollTop + targetHeight === listCntHeight;
            if(isEnd) {
                if(pageObj.dataList.length<pageObj.currentTagNumber){
                    if(pageObj.currentPage>=(pageObj.currentTagNumber/pageObj.pageSize)){
                        return false;
                    }
                    pageObj.currentPage += 1;
                    if(pageObj.searchState){
                        searchPage(1)
                    }else{
                        getAddressList(1);
                    }
                }
            }
        })
    }
    //处理当改变窗口大小拖拽过高时,无法触发滚动加载更多,则重新刷新页面
    var resizeTimer = null;
    $(window).resize(function () {
        if (resizeTimer) clearTimeout(resizeTimer);
        resizeTimer = setTimeout(function () {
            pageObj.pageDataState = 0;
            var $width = $(".managing-right").outerWidth();
            var $height = $(".managing-right").outerHeight();
            var row = Math.ceil($width / 211);
            var col = Math.ceil($height / 167);
            var page_size = row * col;
            if (page_size > pageObj.pageSize) {
                if (pageObj.searchState) {
                    searchPage()
                } else {
                    getAddressList();
                };
            }
        },1000);
    });
    //SPA-tab切换
    $('body').on('shown.bs.tab','#meetingShowNav a.nav-link',function(e){
        if($(this).attr('href')=='#eventManaging'){
            tabInitData();
            $('body #eventManaging .program-box').empty();
            getPageSize();
            getAddressList();
        }
    })
    $(".ztree-title").on("click","a.nav-link",function (e) {
        clearSearch();
        var $type = $(this).data("type");
        $('body #eventManaging .program-box').empty();
        $("#treeDemo").empty();
        $.ajaxSetup({async : false});
        if($type==1){//定制节目
            $("#addLabel,.update-log").hide();
            tab_type=1;
            /*pageObj.aid="";
             pageObj.pageSize=0;*/
            tabInitData();
            getPageSize();
            getAddressList();
        }else if($type==0){//普通节目
            tabInitData();
            tab_type=0;
            $(".update-log").show();
            getPageSize();
            getAddressList();
        };
    })
    //tab数据初始化
    function tabInitData() {
        pageObj.program_id=[];
        pageObj.pageDataState=0;
        pageObj.currentPage=1;
        pageObj.dataList=[];
        pageObj.aid="";
        pageObj.pageSize=0;
    }
    //获取分页条数
    function  getPageSize() {
        var $width = $(".managing-right").outerWidth();
        var $height = $(".managing-right").outerHeight();
        var row = Math.ceil($width/211);
        var col = Math.ceil($height/167);
        var page_size = row*col;
        if(row*col>12){
            pageObj.pageSize=page_size;
        }else{
            pageObj.pageSize=12;
        }
        pageObj.currentPage=1;
        pageObj.dataList=[];
    }
//搜索
    $('#eventManaging #search').keypress(function (e) {
        if (e.keyCode==13) {
            e.preventDefault();
            e.stopPropagation();
            getPageSize();
            pageObj.searchKey=$('#search').val().trim();
            pageObj.searchState=1;
            pageObj.pageDataState=0;
            pageObj.dataList=[];
            searchPage();
        };
    });
    //节目搜索
    function  searchPage(isScroll) {
        if(isScroll != 1){
            pageObj.dataList=[];
            getPageSize();
        }
        fetchs.post('/selPlatformProgrames',{"seachKey":pageObj.searchKey,"platformProgrameLabeId":pageObj.aid==0? "":pageObj.aid,"type":tab_type,"currentPage":pageObj.currentPage,"pageSize":pageObj.pageSize},function(result){
            if(result.ifSuc==1){
                var length = pageObj.dataList.length;
                pageObj.programList=result.data.list;
                pageObj.dataList=pageObj.dataList.concat(result.data.list);
                pageObj.dataList= _.uniq(pageObj.dataList, false, function (item) {
                    return item.id;
                });
                pageObj.programList =  pageObj.dataList.slice(length);
                programRander(isScroll);
                $('#eventManaging #search').blur();
            }else{
                notify('danger',result.msg);
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
        });
    }
    // 确定删除标签
    $('#deleteLabelForm').on('submit', function (event) {
        event.preventDefault();
        $('#deleteLabel').modal('hide');
        var aID = eval('(' + '{' + $(this).serialize().replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
        //删除区域
        deleteOrganization(aID.id, function (result) {
            if (result.ifSuc == 1) {
                if(result.data==false){
                    notify('danger',result.msg);
                }else{
                    var node = zTreeObj.getNodeByParam('id', aID.id, null);
                    zTreeObj.removeNode(node, true); //第二个参数  是否要触发删除子机构的回调函数
                    notify('success',"删除成功");
                    if(aID.id===pageObj.aid){
                        pageObj.aid="";
                    };
                }
            }else{
                notify('danger',result.msg);
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
            getAddressList();
        });
    });
    //搜索框响应
    $('#eventManaging .input-search input').bind('input propertychange', function() {
        if($(this).val().length>0){
            $(".icon-search-del").show();
        }else{
            $(".icon-search-del").hide();
        }
    });
    $('#eventManaging .input-search input').bind('focus',function () {
        $('.input-search').addClass('border-shadow');
    })
    $('#eventManaging .input-search input').bind('blur',function () {
        $('.input-search').removeClass('border-shadow');
    })
    $("#eventManaging .icon-search-del").click(function () {
        $('#eventManaging .input-search input').val("");
        $(this).hide();
        pageObj.searchState=0;
        pageObj.searchKey=$('#eventManaging #search').val().trim();
        getAddressList();
    });
//单个节目滑入点击操作
    $('body').on('mouseenter','.program',function(e){
        $(this).find('.shade').addClass('program-hover');
        $(this).find('.shade').removeClass('program-leave');
        $(this).find('.preview-btn,.custom-checkbox').show();
    })
    $('body').on('mouseleave','.program',function(e){
        $(this).find('.shade').removeClass('program-hover');
        $(this).find('.shade').addClass('program-leave');
        $(this).find('.preview-btn').hide();
        if($(this).find('.custom-checkbox input[name="checkname"]').prop("checked")==false){
            $(this).find('.custom-checkbox').hide();
        }else{
            $(this).find('.custom-checkbox').show();
        }
    });
    //选择节目
    $('body').on('click','.custom-checkbox',function(e){
        stopBubble(e);
        var current_id = $(this).parents('.program').data('id');
        var isCoverState = $(this).find("input[name='checkname']").prop("checked");
        if (isCoverState) {//取消选中
            $(this).find("input[name='checkname']").prop("checked",false);
            $.each(pageObj.program_id,function(item,ele){
                if(ele==current_id){
                    pageObj.program_id.splice(item,1);
                }
            })
        } else {//选中
            $(this).find("input[name='checkname']").prop("checked",true);
            var iscover = remove(pageObj.program_id,current_id)
            if(iscover){
                pageObj.program_id.push($(this).parents('.program').data('id'));
            }
        }
        if(pageObj.program_id.length>0){
            if(tab_type){
                $('.btns-box .delete-btn').show();
            }else{
                $('.btns-box .append-btn,.btns-box .delete-btn').show();
            }
        }else{
            $('.btns-box .append-btn,.btns-box .delete-btn').hide();
        }
    })
//节目详情
    $('body').bind("click", function (e) {
        if (e.target != $('#formBox')&&e.target!=$('.tags')[0]&&$('#formBox').find(e.target).length < 1&&e.target != $('.program')){
            $("#formBox").removeClass('form-show');
        }
    });
    $('body').on('keypress','#formBox input',function(e){
        if(e.keyCode==13){
            e.preventDefault();
        }
    })
    var formTpl ='<div class="form-box"> ' +
        '<h6 class="tittle">节目详情</h6> ' +
        '<div class="dropdown more-btn"> ' +
        '<button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">更多<i class="icon icon-info-down"></i></button> ' +
        '<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1"> ' +
            '<li class="dropdown-item" role="presentation" ng-if="!currentProgram.programVersionNum"><a role="menuitem" tabindex="-1" href="#"><form id="reuploadFile" enctype="multipart/form-data" style="display: inline-block;"><label class="update reupload">重新上传节目<input type="file" name="myfile" accept="application/zip,application/x-zip,application/x-zip-compressed"></label></form></a></li> ' +
            '<li class="dropdown-item delete" role="presentation"><a role="menuitem" tabindex="-1" href="#" >删除节目</a></li> ' +
        '</ul> ' +
        '</div> ' +
        '<div class="form-group"> ' +
        '<label>节目名称</label> ' +
        '<input class="program-title form-control form-control-lg" type="text" placeholder="" name="program_name" data-validate="name" maxlength="50" value="{{currentProgram.programName}}"> ' +
        '</div> ' +
        '<div class="form-group"> ' +
        '<label>节目标签</label> ' +
        '<div class="program-mark" id="markTags"><span class="add" data-type="{{currentProgram.type}}" ng-repeat="ele in currentProgram.labelIds" data-id="{{ele.labelId}}" data-label-name="{{ele.labelName}}">{{ele.labelName}}<i ng-if="!currentProgram.type" class="icon icon-delete-personnel"></i></span><span ng-if="!currentProgram.type" class="icon icon-add-personnel btn-add tags"></span></div> ' +
        '</div> ' +
        '<div class="form-group"> ' +
        '<label>节目版本</label><span class="versions-label" ng-if="!currentProgram.programVersionNum">未发布</span><span class="versions-label" ng-if="currentProgram.programVersionNum">V{{currentProgram.programVersionNum}}</span> ' +
        '</div> ' +
        '<div class="form-group"> ' +
        '<label>上传信息</label> ' +
        '<div class="update-info"> ' +
        '<label>上传人员</label><span class="update-user">{{currentProgram.creater|text}}</span> ' +
        '<label>上传时间</label><span class="update-time" ng-if="currentProgram.creatTime">{{currentProgram.creatTime|dateTime}}</span> ' +
        '</div> ' +
        '</div> ' +
        '<div class="form-group picture-box"> ' +
        '<label class="picture-label">节目效果图</label> ' +
        '<p class="tips">请上传小于5M的图片，限定尺寸1920×1080</p> ' +
        '<label class="btn-change-photo">重传图片 ' +
        '<input type="file" accept="image/jpg,image/png,image/jpeg,image/bmp"> ' +
        '</label> ' +
        '<div class="col-12"> ' +//<img id="programImage" ng-if="!currentProgram.photoUrlBig" src="static/images/huiyi.png" name="programImage">ng-if="currentProgram.photoUrlBig"
        '<div class="photo"><img id="programImage" src="{{currentProgram.photoUrlBig}}" name="programImage"></div> ' +
        '</div> ' +
        '</div> ' +
        '<button class="btn btn-lg btn-primary save-btn" id="saveBtn" type="button">保存</button></div>';
    //节目详情赋值
    $('body').on('click','.program',function(e){
        stopBubble(e);
        var current_id = $(this).data('id');
        $.each(pageObj.dataList,function (index,item) {
            if(item.id==current_id){
                pageObj.currentProgram = item;
                return pageObj;
            };
        });
        if (tab_type) pageObj.currentProgram.type=1;
        $('body #formBox').empty().addClass('form-show').html(soda(formTpl,{"currentProgram":pageObj.currentProgram}));
        $('body').find("#dropdownMenu1").dropdown();
        //详情更多按钮下拉菜单的切换
        $('body .more-btn .dropdown-menu').on('click','li',function(e){
            $('.more-btn').removeClass('show');
        });
        $('#formBox').bind("click",function(e){
            if(e.target!=$('body .more-btn')&&e.target!=$('body .dropdown-menu li')&&$('body .more-btn').find(e.target).length<1&&$('body .more-btn').hasClass('show')){
                $('body .more-btn').removeClass('show');
            }
        })
        imageObj.imageUrl="";
        //组件调用----节目详情
        $("body #markTags").labels({
            class:"tag",
            type:2,
            container:'#markTags',
            template:'<li class="labels-item" ng-repeat="item in data.list" data-id="{{item.id}}" data-label-name="{{item.labelName}}"><i class="icon icon-kzt-display"></i><span>{{item.labelName}}</span></li>'
        });
    })
    //删除节目----节目详情
    $('body').on('click',".more-btn ul li.delete a",function(e){
        e.stopPropagation();
        $('#deleteProgram .modal-body label').html('确定删除节目<span style="color:#0990f0">'+pageObj.currentProgram.programName+'</span>吗？');
        $('#deleteProgram').modal('show');
        pageObj.delete_type=1;
    })
    //点击重传图片----节目详情
    $('body').on('change',".btn-change-photo input",function (e) {
        verifyImage($(this));
    });
    //重新上传节目----节目详情 定制节目
    $("body").on('change','.reupload input[type="file"]',function (e) {
        e.preventDefault();
        verifyFile($(this),2,[{"key":"platformProgrameId","value":pageObj.currentProgram.id},{"key":"ver","value":pageObj.currentProgram.ver}]);
    })
    //修改节目保存
    $('body').on('click',".save-btn",function(e){
        var name_programe = $('input[name="program_name"]').val().trim();
        var formData = {
            "platformProgrameId":pageObj.currentProgram.id,
            "programeName": name_programe==""? pageObj.currentProgram.programName:name_programe,
            "file":!imageObj.imageUrl? "":imageObj.imageUrl,
            "ver":pageObj.currentProgram.ver,//节目锁
        }
        btnUsingState($(this),true);
        editLabel(formData,function(result){
            btnUsingState($('body .save-btn'),false);
            if(result.ifSuc==1){
                if(result.data.success){
                    notify('success','节目保存成功');
                    $("#formBox").removeClass('form-show');
                    pageObj.pageDataState=0;
                    clearSearch();
                }else{
                    notify('danger','操作失败');
                };
            }else{
                notify('danger',result.msg)
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
            getAddressList();
        })
    })

    //选择标签----节目详情
    $("body").on("selected.bs.labels","#markTags",function(e){
        var data={
            "platformProgrameLabelId":e.select[0].id,
            "platformProgrameIds":pageObj.currentProgram.id
        };
        creatLabel(data,function(result){
            if(result.ifSuc==1){
                if(result.data.success){
                    $("body #markTags").labels("choose",e.select,result.data.success);
                    searchState();
                    pageObj.program_id=[];
                }else{
                    notify("danger","操作失败");
                }
            }else{
                notify("danger",result.msg);
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
        });
    });
    //删除单个标签----节目详情
    $("body").on("delete.bs.labels","#markTags",function(e){
        deleLabel({"platformProgrameId":pageObj.currentProgram.id,"platformProgrameLabelId":e.delete[0].id},function(result){
            if(result.ifSuc==1){
                if(result.data.success){
                    $("body #markTags").labels("deleted",e.delete,result.data.success);
                    if(!e.selected.length){
                        $("body #markTags").labels("hide");
                    }
                    searchState();
                }else{
                    notify("danger","操作失败");
                }
            }else{
                notify("danger",result.msg);
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
        })
    });
    //取消选择----节目详情
    $("body").on("unselect.bs.labels"," #markTags",function(e){
        deleLabel({"platformProgrameId":pageObj.currentProgram.id,"platformProgrameLabelId":[e.delete[0].id]},function(result){
            if(result.ifSuc==1){
                if(result.data.success){
                    $("body #markTags").labels("deleted",e.delete,result.data.success);
                    if(!e.selected.length){
                        $("body #markTags").labels("hide");
                    }
                    searchState();
                }else{
                    notify("danger","操作失败");
                }
            }else{
                notify("danger",result.msg);
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
        })
    })
    //创建标签----节目详情
    $("body").on("creatlabel.bs.labels","#markTags",function(e){
        //if(e.add[0].length){
        var data={
            "platformProgrameLabelName":e.add[0].labelName,
            "platformProgrameIds":pageObj.currentProgram.id
        };
        creatLabel(data,function(result){
            if(result.ifSuc==1){
                if(result.data.success){
                    e.add[0].id=result.data.newLabelId;
                    $("body #markTags").labels("created",e.add,result.data.success);
                    $("body #markTags").labels("hide");
                    searchState();
                }else{
                    notify("danger","操作失败")
                }
            }else{
                notify("danger",result.msg);
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
        });
        // }
    });
//批量操作
    //标签组件调用
    $("body #appendTag").labels({
        class:"tag",
        type:1,
        container:'#appendTag',
        template:'<li class="labels-item" ng-repeat="item in data.list" data-id="{{item.id}}" data-label-name="{{item.labelName}}"><i class="icon icon-kzt-display"></i><span>{{item.labelName}}</span></li>'
    })
    //添加标签----批量操作
    $("body #appendTag").on("selected.bs.labels",function(e){
        var data={
            "platformProgrameLabelId":e.selected[0].id,
            "platformProgrameIds":pageObj.program_id
        };
        creatLabel(data,function(result){
            if(result.ifSuc==1){
                if(result.data.success){
                    notify("success","标签批量添加成功");
                }else{
                    notify("danger","操作失败")
                }
            }else{
                notify("danger",result.msg);
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
            searchState();
        })
    })
    //创建标签-----批量操作
    $("body #appendTag").on("creatlabel.bs.labels",function(e){
        var data={
            "platformProgrameLabelName":e.add[0].labelName,
            "platformProgrameIds":pageObj.program_id
        };
        creatLabel(data,function(result){
            if(result.ifSuc==1){
                if(result.data.success){
                    notify("success","标签批量添加成功");
                    $("body #appendTag").labels("hide");
                    searchState();
                    pageObj.program_id=[];
                }else{
                    notify("danger","操作失败")
                }
            }else{
                notify("danger",result.msg);
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
        })
    });
    //批量删除节目
    $('.btns-box .delete-btn').on('click',function(e){
        $('#deleteProgram .modal-body label').html('确定删除所选中的节目吗？')
        $('#deleteProgram').modal('show');
        pageObj.delete_type=0;
    })
    //确认删除
    $("body").on('click','.btn-affirm-delete',function(){
        $("body .btn-danger").attr("disabled",false);
        if(!pageObj.delete_type){
            deleteProgram(pageObj.program_id);
        }else{
            var single_id=[];
            single_id.push(pageObj.currentProgram.id);
            deleteProgram(single_id);
        }
    })
    //节目的预览
    $("body").on('click','.preview-btn',function(e){
        stopBubble(e);
        var program_url;
        fetchs.post("/platformProgramePreview",{"platformProgrameId":$(this).data("id"),"ver":$(this).data("ver")},function (result) {
            if(result.ifSuc==1){
                program_url=result.data.sysAttachmentUrl;
                $("#previewModal").modal("show");
                $('#iframeShow').hide();
                $('.loader-text').show();
                setTimeout(function(){
                    $('.loader-text').hide();
                    $('#iframeShow').stop().fadeIn(500).attr('src',program_url);
                },2000);
            }else{
                notify("danger",result.msg);
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
        })
    });
    //更新日志
    $("body").on("click",".btns-box .update-log",function (e) {
        gotoPage(2);
    });
    function gotoPage(type) {
        $.ajax({
            url: "/checkUserRole?userId="+fetchs.userId+"&token="+fetchs.token,
            async: false,
            success:function(result){
                if (result.ifSuc==1) {
                    window.open("/gotoVersionsLogPage?token="+_userInfo.token+"&type="+type);
                }else{
                    notify('danger',result.msg);
                    if(result.ifSuc==10){
                        goLoginPage();
                    }
                };
            },
            error: function (result) {
                var data = {
                    ifSuc: 0,
                    code: -1,
                    msg: "网络连接失败，请检查网络"
                };
                if(result.msg){
                    notify('danger',result.msg);
                }else{
                    notify('danger',data.msg);
                }
            }
        })
    };
    //节目上传
    $("body").on('change',".btns-box .update-btn input",function (e) {
        e.preventDefault();
        var self= $(this);
        verifyFile($(this),1);
    });
    //定制节目上传
    $("body").on('change',"#selectLssee .customUpload input",function (e) {
        var $id= $("#custom .select-custom").attr("name");
        var $name=$("#custom .select-custom").val().trim();
        verifyFile($(this),3,[{"key":"programName","value":$name},{"key":"tenantId","value":$id}]);
    })
    //节目上传类型判断
    $("body").on("click",".update-btn",function () {
        if(tab_type){
            $("#selectLssee").modal("show");
            $("#custom").tenement("hide");
            $("#selectLssee .select-custom").attr({"name":""}).val("").removeClass("error-box");
            $("#selectLssee .warningBox").hide();
            return false;
        }
    })
    $("body").on("click",".customUpload",function(){
        var $input = $("#selectLssee .select-custom");
        if($input.val()!==''&&$input.data("name")!==""){//已选择租户
            $input.removeClass("error-box");
            $("#selectLssee .warningBox").hide();
        }else{//未选择租户
            $input.addClass("error-box");
            $("#selectLssee .warningBox").show();
            return false;
        }
    })
    //选择租户 组件注册
    $("#custom").tenement({
        class:'custom-box',
        container:'#custom',
    })
    $("#custom").on("hidden.bs.tenement",function (e) {
        if(e.selected.length){
          $("#custom .select-custom").attr("name",e.selected[0].id).val(e.selected[0].name);
          if($("#custom .select-custom").hasClass('error-box')){
            $("#custom .select-custom").removeClass('error-box')
            $(".warningBox").hide();
          }
        }
    })
    //节目的内测
    $("body").on('click','.btn-affirm-test',function(){
        indoorTest();
    })
    //节目的同步到租户
    $('body .synchronization-btn').on('click',function(){
        $('#synchronization textarea').val("").removeClass('has-danger');
        $('#synchronization .publish-btn').attr("disabled",false);
        var newVersionNum= (Number(pageObj.nowVersionNum)+1).toFixed(1)
        $("#synchronization .versions-name").html("V"+newVersionNum);
        //文本框隐藏
        if (tab_type) $('#synchronization textarea').hide();
        else $('#synchronization textarea').show();
    })
    $("body").on('click','#synchronization .publish-btn',function(){
        if(tab_type){
            publishProgram();
        }else{
            var versionsText = $('#synchronization textarea').val();//版本内容
            if(!versionsText){
                $('#synchronization textarea').addClass('has-danger');
            }else{
                $('#synchronization textarea').removeClass('has-danger');
                publishProgram(versionsText);
            }
        }
    })
    //判断是否为搜索状态
    function searchState() {
        if(pageObj.searchKey){
            searchPage();
        }else{
            getAddressList();
        }
    }
//-----------方法的封装
    //清除搜索框
    function clearSearch() {
        $('#search').val("");
        $('#del-searchList').hide();
        pageObj.searchKey="";
    }
//获取数据
    //按钮的启用禁用
    function btnUsingState($this,type){
        $this.attr("disabled",type);
    }
//删除标签-节目详情
    function deleLabel(data,callBack){
        fetchs.post("/delPlatformProgrameAndLabelmiddle",data,function(data){
            callBack(data);
        });
    };
//创建标签
    function creatLabel(data,callBack){
        fetchs.post("/createdPlatformProgrameLabel",data,function(data){
            callBack(data);
        });
    };
//编辑节目
    function editLabel(data,callBack){
        fetchs.uploadMixture("/updatePlatformProgrames",data,function(data){
            callBack(data);
        });
    }
//删除节目
    function deleteProgram(deleteList){
        $("body .btn-danger").attr("disabled",true);
        fetchs.post('/delPlatformProgrames',{"platformProgrameIds":[deleteList]},function(result){
            $("body .btn-danger").attr("disabled",false);
            if(result.ifSuc==1){
                if(result.data.success){
                    notify('success','删除节目成功');
                    clearSearch();
                    pageObj.program_id=[];
                }else{
                    notify("danger","操作失败");
                }
            }else{
                notify('danger',result.msg);
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
            getAddressList();
        })
    }
//内测
    function indoorTest() {
        btnUsingState($(".btn-affirm-test"),true);
        fetchs.post('/platformProgrameBeta',{"ver":pageObj.ver,type:tab_type},function(result) {
            btnUsingState($(".btn-affirm-test"),false);
            if(result.ifSuc==1){
                if(result.data.success){
                    notify('success','内测节目成功');
                    pageObj.program_id=[];
                    clearSearch();
                }else{
                    notify("danger","操作失败");
                }
            }else{
                notify('danger',result.msg);
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
            getAddressList();
        })
    }
//同步到租户
    function publishProgram(versionsText) {
        var data={
            "oldProgramVersionNum":pageObj.nowVersionNum,
            "nextProgramVersionNum":(Number(pageObj.nowVersionNum)+1).toFixed(1),
            "publishNote":versionsText,
            "ver":pageObj.ver,
            "type":tab_type
        };
        if(tab_type) delete data.publishNote;
        else data.publishNote=versionsText;
        $("body .publish-btn").attr('disabled',true);
        fetchs.post('/platformProgrameSynchronization',data,function(result) {
            $("#synchronization").modal("hide");
            if(result.ifSuc==1){
                if(result.data.success){
                    notify('success','同步节目到租户成功');
                    pageObj.program_id=[];
                    clearSearch();
                }else{
                    notify("danger","操作失败");
                }
            }else{
                notify('danger',result.msg);
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
            getAddressList();
        })
    }
//数组去重
    function remove(arr, item) {
        var result=1;  //不重复
        $.each(arr,function(index,element){
            if(element==item){
                result=0;//重复
            }
        });
        return result;
    }
//阻止冒泡
    function stopBubble(e) {
        if (e && e.stopPropagation) {
            e.stopPropagation(); //w3c
        } else window.event.cancelBubble = true; //IE
    };
//时间格式化
    function range(dateTime) {
        return moment(Number(dateTime)).format('YYYY-MM-DD')+' '+moment(Number(dateTime)).format('HH:mm');
    }
//验证图片
    function verifyImage(self){
        var file = self.get(0).files[0];
        var extStart = file.name.lastIndexOf(".");
        var ext = file.name.substring(extStart, file.name.length).toUpperCase();
        var reader = new FileReader;
        if(ext != ".BMP" && ext != ".PNG" && ext != ".JPEG" && ext != ".JPG"){
            notify('danger','图片格式不正确');
            return false;
        };
        reader.onload = function (evt) {
            var image = new Image();
            image.onload = function(){
                var img_width = this.width;
                var img_height = this.height;
                if(ext != ".BMP" && ext != ".PNG" && ext != ".JPEG" && ext != ".JPG"){
                    notify('danger','图片格式不正确');
                }else{
                    imageObj.imageSize=file.size;
                    var size = file.size; //得到的是字节
                    var maxSize = 5*1024*1024;
                    if(img_width!=1920&&img_height!=1080){
                        notify("danger", "请保证图片像素为1920x1080");
                    }else{
                        if (size > maxSize) {
                            notify("danger", "图片不能大于5M");
                        }else{
                            var blob = URL.createObjectURL(file);
                            $('.photo img').attr('src', blob);
                            imageObj.imageUrl=file;
                        }
                    }
                }
            };
            image.src = evt.target.result;
        };
        reader.readAsDataURL(file);
        self.val('');//上传同一张图片清除上一张信息
    }
    //验证文件包
    function verifyFile(self,uploadState,data) {
        if(self){
            var file = self.get(0).files[0];
            self.remove();
            var $inputTpl= '<input type="file" name="myfile" value="" accept="application/zip,application/x-zip,application/x-zip-compressed">';
            switch(uploadState) {
                case 1://上传节目(普通节目)
                    $("body .btns-box .update-btn").append($inputTpl);
                    break;
                case 2://重新上传(普通节目)
                    $("body .reupload").append($inputTpl);
                    break;
                case 3://上传(定制节目)
                    $("body #selectLssee .customUpload").append($inputTpl);
                    break;
                default:
            }
            imageObj.program_file="";
            if(file&&self.val()){
                var extStart = file.name.lastIndexOf(".");
                var ext = file.name.substring(extStart, file.name.length).toUpperCase();
                if(tab_type) $("#selectLssee").modal("hide");
                if(ext != ".ZIP"){
                    setTimeout(function(){notify('danger','文件格式不正确');},500);
                    self.attr("disabled",false);
                }else{
                    imageObj.program_file="";
                    imageObj.program_file=file;
                    self.attr("disabled",true);
                    if(imageObj.program_file){
                        fetchs.formDataUpload("/addPlatformPrograme",imageObj.program_file,function (result) {
                            self.attr("disabled",false);
                            if(result.ifSuc==1){
                                if(result.data.success){
                                    notify("success","上传成功");
                                    clearSearch();
                                }else{
                                    notify("success","操作失败");
                                }
                            }else{
                                notify("danger",result.msg);
                                if(result.ifSuc==10){
                                    goLoginPage();
                                }
                            }
                            getAddressList();
                        },data)
                    }
                }
            };
        };
    }
})

//------------------------ztree增删改事件和对应的接口------------------------------  
//点击了编辑按钮
function didClickEdit(editId) {}
//标签重命名
function didClickRename(editId,tId) {
    var node = zTreeObj.getNodeByParam('id', editId, null); //根据新的id找到新添加的节点
    $("#"+tId.id+" "+".dimBgc:first").hide();
    oldName=node.name;
    renameTag=0;
    zTreeObj.editName(node);
    document.getElementsByClassName("rename")[0].maxLength=10;
}
//添加标签
function didClickAddDepartment() {
    //添加部分的同时,让部分处于可编辑状态
    var creatTime =new Date().getTime();
    var newNode = { id: creatTime, name: "新建标签"};
    zTreeObj.addNodes(null, -1, newNode);
    var node1 = zTreeObj.getNodeByParam('id', creatTime, null); //根据新的id找到新添加的节点
    $("#"+node1.tId+" "+".dimBgc:first").hide();
    zTreeObj.editName(node1);
    renameTag = 1;
    document.getElementsByClassName("rename")[0].maxLength=10;
    document.getElementsByClassName("rename")[0].select();
}
//删除标签
function didClickDeletedDepartment(editId,name) {
    $('#deleteLabel').find('span.label-name').text(name);
    $('#deleteLabel').find('input.name1').val(editId);
    $('#deleteLabel').modal('show');
}
//标签移动接口（标签列表）
function organiztionMove( moveOrgId, parentId, moveType, callBack) {
    fetchs.post('/platformProgrameLabelsort',{"sortPlatformProgrameLabelId":moveOrgId,"platformProgrameLabelId":parentId,"sortType":moveType}, function (data) {
        callBack(data);
    })
}
//标签添加接口（标签列表）
function saveOrganization(orgName,callBack) {
    fetchs.post('/createdPlatformProgrameLabel',{"platformProgrameLabelName":orgName}, function (data) {
        callBack(data);
    })
}
//标签删除接口（标签列表）
function deleteOrganization(organizationId, callBack) {
    fetchs.post('/delPlatformProgrameLabels',{"platformProgrameLabeIds":[organizationId]}, function (data) {
        callBack(data);
    })
}
//修改标签名称的接口（标签列表）
function updateOrganiztion(id, orgName,ver, callBack) {
    fetchs.post('/updatePlatformProgrameLabel',{"platformProgrameLabelId":[id],"platformProgrameLabelName":orgName,"ver":ver}, function (data) {
        callBack(data);
    })
}