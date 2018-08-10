$(function() {
    var listTpl ='<table><colgroup> <col> <col> <col> <col> <col> <col> </colgroup>'+
        '<tbody>'+
        '<tr ng-repeat="item in terminals" style="height: 72px;">'+
        '<td> '+
        '<label class="checkbox">'+
        ' <input name="item" type="checkbox" value="0"  data-id="{{item.id}}" data-name="{{item.name}}" data-filtration="{{item.program ? item.program.id:null}}"  data-status="{{item.status}}" ><span class="icon"></span>'+
        ' </label>'+
        '</td>'+
        '<td>'+
        ' <p  ng-html="item.name|keylight:keyword"></p>'+
        '  <p ng-if="item.meetingRoom"><span  ng-html="item.meetingRoom ? item.meetingRoom.name:null|keylight:keyword" class="overflowdiv"></span></p>'+
        '  <p ng-if="!item.meetingRoom" style="color:#aaaaaa;"><span>未绑定会议室</span></p>'+
        '  </td>'+
        ' <td>{{item.status == 0 ? "离线":"在线"}}</td>'+
        ' <td>'+
        '   <span class="version" ng-if="item.version"  ng-html="item.version|keylight:keyword"></span>'+
        '    <button class="btn btn-clear-primary btn-upgrad" ng-if="item.upgradable"  data-toggle="modal" data-target="#upgradeVersions"  data-name="{{item.version}}"  data-id="{{item.id}}"><i class="icon icon-upgrade"></i></button>'+
        '    </td>'+
        '   <td>'+
        '    <p ng-if="item.program" ><a ng-html="item.program ? item.program.name:null|keylight:keyword" class="programName"></a> <em class="programstartTime"  ng-if="item.status == 1" >{{item.program? item.program.status == 1 ? "（正在播放）":""+item.program.startTime+"":null}}</em><span class="btn-delete"  data-toggle="modal" data-target="#delete"  data-id="{{item.id}}" data-name="{{item.name}}">删除</span></p>'+
        '    <p ng-if="!item.program&&!item.message" style="color:#aaaaaa;">暂无节目</p>'+
        '    <p ng-if="item.message"  class="terminalsmessagediv"><i class="icon icon-notice"></i><span class="terminalsmessage">{{item.message ? item.message.content : null}}</span><span>{{item.message? item.message.status == 0 ? "（正在发送）":item.message.status==2 ? "（未送达）":"":null}}<i  ng-if="item.message&&item.message.status==1 " class="icon icon-success"></i><i  ng-if="item.message&&item.message.status==2 " class="icon icon-error"></i></span></p>'+
        '   </td>'+
        '   <td>'+
        '    <button class="btn btn-clear-primary btn-detail"  data-id="{{item.id}}" data-name="{{item.name}}" data-status="{{item.status}}" data-program="{{item.program}}">详情修改</button>'+
        '     <button class="btn btn-clear-primary livePictureModal"   ng-if="item.status == 1" data-id="{{item.id}}" data-name="{{item.name}}" data-toggle="modal" data-target="#livePictureModal">实时画面</button>'+
        '    <button class="btn btn-clear-primary" style="color:#aaaaaa;"  ng-if="item.status == 0">实时画面</button>'+
        ' </td>'+
        ' </tr>'+
        '</tbody>'+
        '</table>'+
        '<div ng-if="!terminals" class="nothing"id="nullData">当前还没有可监控的终端。<br>您可以根据<a href="../static/terminal_sales/terminal_sales.html"  target="view_window" style="text-decoration:underline">购买说明</a>，购买会议显示终端，<br>如果您已经购买，请根据<a style="color: #0AB2F5">购买说明-用户指南</a>进行终端的注册。</div>'+
        '<div ng-if="!terminals" class="nothing" id="result" style="display:none">没有搜索到信息，换个条件试试？<br>您可以输入终端名称、会议室、版本、节目内容检索。</div>';
    var params = {
        word: '',
        status: '',
        pageNum: 1,
        pageSize: 15
    };
    var programsparams = {
        word: '',
        status:1,
        pageNum: 1,
        pageSize: 500
    };
    var roomdata;
    var getRoomTerminalsparams = {
        meetingRoomId: '',
        status: ''
    };
    var queryTerminalsparams = {
        name: '',
        roomId: '',
        terminalId: '',
        picture: null,
        updateBackground:'',
        province:'',
        city:'',
        county:''
    };
    var terminalId={
        terminalId:''
    };
    var meetingRoomId;
    var sendScreenshotCommandparams={
        requestId:'',
        ids: []
    };
    var refreshScreenshotparams={
        requestId:'',
        ids: []
    };
    var refreshScreenshottime;
    var queryUpgradetime;
    var selectedlist={
        terminals: [],
        terminalsname:[]
    };
    var imageObj ={
        imageState:0, //记录图片修改的状态
        imageSize:0,  //记录图片的大小
    };
    var broadcastMessageparams= {
        "terminals": [],
        "message": {
            "length": 1,
            "unit": 3,
            "speed": 2,
            "content": "临时通知"
        }
    };
    var upgradeId={
        id:''
    };
    var getPrograms={
        tagId:'',
        pageSize:12,
        pageNum:1
    };
    var customPage = 1;//定制节目页码
    var customSize = 16;//定制节目每页条数
    var customList = [];//定制节目list
    var customTotal = 0;//定制节目总数
    var publishProgram={
        terminals: [],
        programId: "1836e74789a24e928c1ae5b295b2c8fc",
        start: "2018-6-9 10:05",
        end: "2018-6-9 11:05",
        standing: true,
        idle: false
    };
    var filtrationchecked={
        terminals: []
    };
    var meetingRoomchecked={
        meetingRoom:[]
    };
    var refreshTerminalProgramStatus={
        requestId:''
    };
    var refreshTerminalTimeout;
    var programaddlist={
        programlist:'',
        programtotal: ''
    };
    var isCustomTenant = '';//是否为定制租户
    var filterStatus='';
    var defaultBackgroundUrl='';
    //初始化分页控件
    var p = Pages();
    p.callBack(callBack); //初始化分页器自定义回调函数
    //返回当前页和每页多少条数据
    function callBack(currentPage, pageSize) {
        params.pageNum = currentPage;
        params.pageSize = pageSize;
        getDate();
    }
    function formatDateTime(inputTime) {
        var date = new Date(inputTime);
        var y = date.getFullYear();
        var m = date.getMonth() + 1;
        m = m < 10 ? ('0' + m) : m;
        var d = date.getDate();
        d = d < 10 ? ('0' + d) : d;
        var h = date.getHours();
        h = h < 10 ? ('0' + h) : h;
        var minute = date.getMinutes();
        minute = minute < 10 ? ('0' + minute) : minute;
        return y + '-' + m + '-' + d+' '+h+':'+minute;
    };
    //获取列表
    function getDate() {
        fetchs.post('/terminal/queryTerminals',params,function(res) {
            if(res.ifSuc == 1) {
                $('#selectAll').find('input').prop('checked', false);
                res.data.keyword= params.word;
                if( params.status==1){
                    $('.terminal .filter-dropdown').find('.dropdown-toggle').data('num',res.data.stats.online).html('在线('+res.data.stats.online+')');
                }else if(params.status===0){
                    $('.terminal .filter-dropdown').find('.dropdown-toggle').data('num',res.data.stats.offline).html('离线('+res.data.stats.offline+')');
                }else{
                    $('.terminal .filter-dropdown').find('.dropdown-toggle').data('num', res.data.stats.total).html('全部设备('+res.data.stats.total+')')
                }
                $('#monitor .filter-dropdown .dropdown-item').eq(0).data('num',res.data.stats.total).text('全部设备('+res.data.stats.total+')');
                $('#monitor .filter-dropdown .dropdown-item').eq(1).data('num', res.data.stats.online).text('在线('+res.data.stats.online+')');
                $('#monitor .filter-dropdown .dropdown-item').eq(2).data('num', res.data.stats.offline).text('离线('+res.data.stats.offline+')');
                $('.table .table-body').html(soda(listTpl, res.data));
                $('.table .programstartTime').each(function () {
                    if(!isNaN($(this).html())){
                        $(this).html('（'+formatDateTime( parseInt($(this).html()))+' 播放）')
                    }
                });
                $('.btn-conceal').hide();
                if(res.data.terminals==null){
                    $('.btn-pandect').hide();
                    $('#selectAll').find('input').attr("disabled","disabled");
                }else{
                    $('.btn-pandect').show();
                    $('#selectAll').find('input').removeAttr("disabled");
                }
            }else{
                notify("danger", res.msg);
            }
            terminalId.terminalId='';
        })
    };
    window.hasTerminals = false;
    //初始分页列表
    function setgetDate() {
        fetchs.post('/terminal/queryTerminals',params,function(res) {
            if(res.ifSuc == 1) {
                $('#selectAll').find('input').prop('checked', false);
                if(params.status===''){
                    p.setCount(res.data.stats.total);
                }else{
                    if(params.status==1){
                        p.setCount(res.data.stats.online);
                    }else if(params.status==0){
                        p.setCount(res.data.stats.offline);
                    }
                }
                res.data.keyword= params.word;
                if( params.status==1){
                    $('.terminal .filter-dropdown').find('.dropdown-toggle').data('num',res.data.stats.online).html('在线('+res.data.stats.online+')');
                }else if(params.status===0){
                    $('.terminal .filter-dropdown').find('.dropdown-toggle').data('num',res.data.stats.offline).html('离线('+res.data.stats.offline+')');
                }else{
                    $('.terminal .filter-dropdown').find('.dropdown-toggle').data('num', res.data.stats.total).html('全部设备('+res.data.stats.total+')')
                }
                $('#monitor .filter-dropdown .dropdown-item').eq(0).data('num',res.data.stats.total).text('全部设备('+res.data.stats.total+')');
                $('#monitor .filter-dropdown .dropdown-item').eq(1).data('num', res.data.stats.online).text('在线('+res.data.stats.online+')');
                $('#monitor .filter-dropdown .dropdown-item').eq(2).data('num', res.data.stats.offline).text('离线('+res.data.stats.offline+')');
                $('.table .table-body').html(soda(listTpl, res.data));
                $('.table .programstartTime').each(function () {
                    if(!isNaN($(this).html())){
                        $(this).html('（'+formatDateTime( parseInt($(this).html()))+' 播放）')
                    }
                });
                $('.btn-conceal').hide();
                if(res.data.terminals==null){
                    $('.btn-pandect').hide();
                    if(params.status === '' && params.word === '' && params.pageNum == 1) {
                        hasTerminals = false;
                        $('.btn-new-task').prop('disabled', true);
                    }
                    $('#selectAll').find('input').attr("disabled","disabled");
                }else{
                    $('.btn-pandect').show();
                    $('.btn-new-task').prop('disabled', false);
                    hasTerminals = true;
                    $('#selectAll').find('input').removeAttr("disabled");
                }
            }else{
                notify("danger", res.msg);
            }
        })
    };
    //获取会议室列表
    function getroomDate() {
        fetchs.post('/terminal/getRoomTerminals', getRoomTerminalsparams, function (res) {
            if(res.ifSuc == 1) {
                roomdata = res.data;
            }else{
                notify("danger", res.msg);
            }
        })
    };
    //检查节目是否有更新
    function checkProgramVerDate() {
        fetchs.post('/syncProgramVer/checkProgramVer',queryTerminalsparams, function (res) {
            if(res.ifSuc == 1) {
                $('.CheckUpdateContent').html(res.msg);
                if( res.code!=1000) {
                    if(res.code==1003||res.code==1002){
                        $('.CheckUpdateDetails').hide();
                    }else{
                        $('.CheckUpdateDetails').show();
                    };
                    $('body .main-panel').css("padding-top","98px");
                    $('.fixed-top').css("height","98px");
                    $('.CheckUpdatesdiv').show();
                }
            }else{
                notify("danger", res.msg);
            }
        })
    };
    //获取插播消息配置数据
    function getBroadcastConfigDate() {
        fetchs.post('/terminal/getBroadcastConfig',queryTerminalsparams, function (res) {
            var str='';
            var str1='';
            for (var i = 0, len = res.data.length.length; i < len; i++) {
                if( res.data.length[i].isDefault==1){
                    $("#area") .attr("data-id", res.data.length[i].code);
                    $("#area").html(res.data.length[i].name);
                }
                str+='<a class="dropdown-item area-item" data-id="'+res.data.length[i].code+'"> '+res.data.length[i].name+'</a>';
            }
            $("#arealist").html(str);
            for (var i = 0, len = res.data.speed.length; i < len; i++) {
                if( res.data.length[i].isDefault==1){
                    $("#area1").attr("data-id", res.data.speed[i].code);
                    $("#area1").html(res.data.speed[i].name);
                }
                str1+='<a class="dropdown-item area-item" data-id="'+res.data.speed[i].code+'"> '+res.data.speed[i].name+'</a>';
            }
            $("#arealist1").html(str1);
        })
    };
    setgetDate();
    getroomDate();
    checkProgramVerDate();
    //列表滑动显示删除
    $('.table').on('mouseover',"tr",function(e){
        $(this).find(".btn-delete").show();
    });
    $('.table').on('mouseout',"tr",function(e){
        $(this).find(".btn-delete").hide();
    });
    $(document).on('focus', 'input[readonly]', function () {
        this.blur();
    });
    $('.table').on('click',"tr",function(evt){
        if(!$(evt.target).hasClass('btn-clear-primary')&&!$(evt.target).hasClass('btn-delete')&&!$(evt.target).hasClass('checkbox')&&!$(evt.target).hasClass('btn-upgrad') &&$('.btn-upgrad').find(evt.target).length < 1&&$('.btn-clear-primary').find(evt.target).length < 1&&$('.btn-delete').find(evt.target).length < 1&&$('.checkbox').find(evt.target).length < 1){
            if ($(this).find('input').prop('checked')) {
                $(this).find('input').prop('checked',false);
            } else {
                $(this).find('input').prop('checked',true);
            };
            var checklist=0;
            var datalist=0;
            $("tbody tr input[name='item']").each(function () {
                datalist++;
                if ($(this).prop('checked')) {
                    checklist++;
                }
            })
            if(checklist==datalist){
                $('#selectAll').find('input').prop('checked', true);
            }else{
                $('#selectAll').find('input').prop('checked', false);
            }
            if(checklist!=0){
                $('.btn-conceal').show();
            }else{
                $('.btn-conceal').hide();
            }
        }
    });
    //刷新事件
    $('.btn-refresh').on('click', function(evt) {
        $(this).find('i').addClass("refreshtransform");
        setTimeout(function () {
            $('.btn-refresh').find('i').removeClass("refreshtransform");
        }, 170);
        getDate();
    });
// 搜索终端列表
    $('#monitor #searchterminal').on('keypress', function(evt) {
        if(evt.keyCode == 13){
            var key = this.value.trim();
            $('#searchterminal').blur();
            if (key != '') {
                $('#monitor .btn-search-del').show();
                params.word = key;
            }else{
                $('#monitor .btn-search-del').hide();
                params.word='';
            }
            fetchs.post('/terminal/queryTerminals',params,function(res) {
                if(res.ifSuc == 1) {
                    $('#selectAll').find('input').prop('checked', false);
                    p.setCount(res.data.stats.total);
                    res.data.keyword= params.word;
                    if( params.status==1){
                        $('.terminal .filter-dropdown').find('.dropdown-toggle').data('num',res.data.stats.online).html('在线('+res.data.stats.online+')');
                    }else if(params.status===0){
                        $('.terminal .filter-dropdown').find('.dropdown-toggle').data('num',res.data.stats.offline).html('离线('+res.data.stats.offline+')');
                    }else{
                        $('.terminal .filter-dropdown').find('.dropdown-toggle').data('num', res.data.stats.total).html('全部设备('+res.data.stats.total+')')
                    }
                    $('#monitor .filter-dropdown .dropdown-item').eq(0).data('num',res.data.stats.total).text('全部设备('+res.data.stats.total+')');
                    $('#monitor .filter-dropdown .dropdown-item').eq(1).data('num', res.data.stats.online).text('在线('+res.data.stats.online+')');
                    $('#monitor .filter-dropdown .dropdown-item').eq(2).data('num', res.data.stats.offline).text('离线('+res.data.stats.offline+')');
                    $('.table .table-body').html(soda(listTpl, res.data));
                    $('.table .programstartTime').each(function () {
                        if(!isNaN($(this).html())){
                            $(this).html('（'+formatDateTime( parseInt($(this).html()))+' 播放）')
                        }
                    });
                    $('.btn-conceal').hide();
                    $('#nullData').css('display','none');
                    $('#result').css('display','block');
                    if(res.data.terminals==null){
                        $('.btn-pandect').hide();
                        $('#selectAll').find('input').attr("disabled","disabled");
                    }else{
                        $('.btn-pandect').show();
                        $('#selectAll').find('input').removeAttr("disabled");
                    }
                }else{
                    notify("danger", res.msg);
                }
            })
        }
    });
    $('#monitor .btn-search-del').on('click', function(evt) {
        $('#monitor #searchterminal').val('');
        params.word = '';
        params.pageNum=1;
        setgetDate();
        $(this).hide();
    });
    $('#searchterminal').on('focus', function (evt) {
        $(this).parent('.search-box').addClass('border-shadow');
    });
    $('#searchterminal').on('blur', function (evt) {
        $(this).parent('.search-box').removeClass('border-shadow');
    });
// 下拉筛选
    $('#monitor .filter-dropdown .dropdown-item').on('click', function(){
        if ($(this).data('num') == 0) {
            return false;
        }
        $('#monitor .filter-dropdown .dropdown-item').removeClass('active');
        $(this).addClass('active');
        $('#monitor .filter-dropdown .dropdown-toggle').text($(this).text());
        if (params.status !== $(this).data('filter')) {
            params.status = $(this).data('filter');
            filterStatus=$(this).data('filter');
            params.pageNum=1;
            setgetDate();
        }
    });
    var circulation = 1;
    var circulationlist=1;
// 终端实时画面
    var livePicturedata={
        id: '',
        name: ''
    };
    $('body').on('click', '.livePictureModal', function () {
        livePicturedata.id=$(this).data('id');
        livePicturedata.name=$(this).data('name');
    });
    $('#livePictureModal').on('show.bs.modal', function(e){
        $('.detail').removeClass('show');
        circulationlist=1;
        var data={
            id: '',
            name: ''
        };
        if(livePicturedata.id==undefined|| livePicturedata.id==null|| livePicturedata.id==''){
            data.name=queryTerminalsparams.name;
            data.id=queryTerminalsparams.terminalId;
        }else{
            data = $(e.relatedTarget).data();
        }
        $(this).find('.modal-title').text(data.name);
        $("body").find(".modal-backdrop").eq(1).css("z-index",9998);
        sendScreenshotCommandparams.ids=data.id.split(",");
        refreshScreenshotparams.ids=data.id.split(",");
        sendScreenshotCommandparams.requestId='';
        if(sendScreenshotCommandparams.ids!=''){
            fetchs.post('/terminal/sendScreenshotCommand',sendScreenshotCommandparams,function(res) {
                refreshScreenshotparams.requestId=res.data.requestId;
                function realMethod(){
                    if(circulationlist==2){
                        return ;
                    }
                    fetchs.post('/terminal/refreshScreenshot',refreshScreenshotparams,function(res) {
                        if(res.ifSuc == 1) {
                            if (res.data.terminals[0].pictureUrl != null && res.data.terminals[0].pictureUrl != '') {
                                circulationlist == 2;
                                $('#livePictureModal').find('.loading').hide();
                                $('#livePictureModal').find('.livepreview').html('<img src="' + res.data.terminals[0].pictureUrl + '">').show();
                            } else {
                                circulationlist == 1;
                                setTimeout(function () {
                                    realMethod();
                                }, 3000)
                            }
                        }else{
                            setTimeout( function(){
                                $('#livePictureModal').modal('hide');
                            },500);
                            notify("danger", res.msg);
                            getDate();
                        }
                    })
                }
                realMethod();
            })
        }
    });
    $('#livePictureModal').on('shown.bs.modal', function (e) {
        if( $('.modal-backdrop').length==2){
            $('.modal-backdrop').eq(0).hide().addClass('show');
        }
    });
    $('#livePictureModal').on('hide.bs.modal', function(e) {
         $('#pandectModal').css('z-index', 1080);
         $('.modal-backdrop').eq(0).show();
         $('.modal-backdrop').eq(1).hide();
     });
    $('#livePictureModal').on('hidden.bs.modal', function(e){
        circulationlist=2;
        sendScreenshotCommandparams.requestId='';
        livePicturedata.id=''
        $(this).find('.loading').show();
        $(this).find('.livepreview').html('').hide();
    });
// 终端实时总览
    $('#pandectModal').on('show.bs.modal', function(e){
        sendScreenshotCommandparams.requestId='';
        sendScreenshotCommandparams.ids='';
        circulation=1;
        fetchs.post('/terminal/sendScreenshotCommand',sendScreenshotCommandparams,function(res) {
            if(res.ifSuc == 1) {
                refreshScreenshotparams.requestId=res.data.requestId;
                refreshScreenshotparams.ids='';
                $('.loadingdiv').show();
                $('.loadingerrordiv').hide();
                function realMethod(){
                    if(circulation==2){
                        return ;
                    }
                    fetchs.post('/terminal/refreshScreenshot',refreshScreenshotparams,function(res) {
                        if(res.ifSuc == 1) {
                            var aa=0;
                            if($('.traverselist').length==0){
                                var str='';
                                for (var i = 0, len = res.data.terminals.length; i < len; i++) {
                                    if(res.data.terminals[i].pictureUrl==""||res.data.terminals[i].pictureUrl==null){
                                        aa++;
                                    }
                                    str+= '<li class="traverselist">';
                                    str+= '<h6 class="smallPicturediv" data-name="' + res.data.terminals[i].pictureUrl + '" data-programName="' + res.data.terminals[i].programName + '"> <img src="' + res.data.terminals[i].smallPictureUrl + '">'
                                    if(res.data.terminals[i].programName!=''){
                                        str+=  '<span class="programNamediv"><span>'+ res.data.terminals[i].programName + '</span></span>'
                                    }
                                    str+= '</h6><h6 class="loadingdiv"><span class="icon icon-changeimg"></span><span class="shadediv"></span><span class="icon-refresh1"><img src="../static/images/loading2.png"></span></h6>'
                                    str+='</h6><h6 class="loadingerrordiv"><span class="icon icon-failpicture"></span><span class="loadingerrortext">加载失败，请<a  data-id="' + res.data.terminals[i].terminalId + '">重试</a></span></h6><p class="pandectlistname">' + res.data.terminals[i].terminalName + '</p>' +
                                        '<p class="pandectlistroom overflowdiv">' + res.data.terminals[i].roomName + '</p>';
                                    str+= '</li>';
                                }
                                $('#pandectModal ul.list-box').html(str);
                                for (var i = 0, len = res.data.terminals.length; i < len; i++) {
                                    if(res.data.terminals[i].pictureUrl!=""&&res.data.terminals[i].pictureUrl!=null){
                                        $('.traverselist').eq(i).find(".smallPicturediv").show();
                                        $('.traverselist').eq(i).find(".loadingdiv").hide();
                                        $('.traverselist').eq(i).find(".smallPicturediv img").attr("src",res.data.terminals[i].smallPictureUrl);
                                        $('.traverselist').eq(i).find(".smallPicturediv").attr("data-name",res.data.terminals[i].pictureUrl);

                                    }else{
                                        $('.traverselist').eq(i).find(".smallPicturediv").hide();
                                        $('.traverselist').eq(i).find(".loadingdiv").show();
                                    }
                                }
                            }else{
                                for (var i = 0, len = res.data.terminals.length; i < len; i++) {
                                    if(res.data.terminals[i].pictureUrl==""||res.data.terminals[i].pictureUrl==null){
                                        aa++;
                                    }
                                    if(res.data.terminals[i].pictureUrl!=""&&res.data.terminals[i].pictureUrl!=null){
                                        $('.traverselist').eq(i).find(".smallPicturediv").show();
                                        $('.traverselist').eq(i).find(".loadingdiv").hide();
                                        $('.traverselist').eq(i).find(".smallPicturediv img").attr("src",res.data.terminals[i].smallPictureUrl);
                                        $('.traverselist').eq(i).find(".smallPicturediv").attr("data-name",res.data.terminals[i].pictureUrl);
                                    }else{
                                        $('.traverselist').eq(i).find(".smallPicturediv").hide();
                                        $('.traverselist').eq(i).find(".loadingdiv").show();
                                    }
                                }
                            }
                            if(aa==0){
                                circulation=2;
                            }else{
                                setTimeout( function() {
                                    realMethod();
                                },3000)
                            }
                        }else{
                            notify("danger", res.msg);
                            getDate();
                        }
                    })
                }
                realMethod();
            }else{
                if(res.msg=='暂无在线终端。'){
                    $('#programsNothing').show();
                }else{
                    notify("danger", res.msg);
                }
            }
        })
        setTimeout( function(){
            circulation=2;
            refreshScreenshotparams.requestId='';
            $(".traverselist").each(function(){
                console.log($(this).find("img").attr('src'));
                if($(this).find("img").attr('src')!=undefined&&$(this).find("img").attr('src')!=''&&$(this).find("img").attr('src')!=null&&$(this).find("img").attr('src')!="null"){
                    $(this).find('.smallPicturediv').show();
                    $(this).find('.loadingdiv').hide();
                    $(this).find('.loadingerrordiv').hide();
                }else{
                    $(this).find('.smallPicturediv').hide();
                    $(this).find('.loadingdiv').hide();
                    $(this).find('.loadingerrordiv').show();
                }
            });
        },2*60*1000);
    });
    $('#pandectModal').on('hidden.bs.modal', function(e){
        $('#pandectModal ul.list-box').html('');
        $('#programsNothing').hide();
    });
//终端地址管理
    init_city_select($("#address-city"));
// 终端详情修改展示
    $('.table-body').on('click', '.btn-detail', function(evt) {
        terminalId.terminalId=$(this).data('id');
        queryTerminalsparams.terminalId= $(this).data('id');
        queryTerminalsparams.name= $(this).data('name');
        if($(this).data('status')==1){
            $('#terminalDropdown').show();
        }else{
            $('#terminalDropdown').hide();
        }
        if($(this).data('program')==null){
            $('.dropdowndelete').hide();
            $('.dropdownprograms').show();
        }else{
            $('.dropdowndelete').show();
            $('.dropdownprograms').hide();
        }
        fetchs.post('/terminal/get',terminalId,function(res) {
            meetingRoomchecked.meetingRoom=[]
            $('#particularsName').val(res.data.name);
            if(res.data.meetingRoom!=null){
                $('#particularsRoom').val(res.data.meetingRoom.name);
                meetingRoomId= res.data.meetingRoom.id;
                meetingRoomchecked.meetingRoom.push({
                    id : res.data.meetingRoom.id,
                    name:res.data.meetingRoom.name
                })
            }else{
                $('#particularsRoom').val('');
            }
            $('input[name="boardroom"]').selector('setOption', {
                selected: meetingRoomchecked.meetingRoom
            });
            $('#particularsHardwareId').html(res.data.hardwareId);
            $('#particularstype').html(res.data.type);
            if(res.data.resolution!=null) {
                $('#particularsresolution').html("（"+res.data.resolution+"）");
            }else{
                $('#particularsresolution').html("");
            }
            if(res.data.backgroundUrl!=null&&res.data.backgroundUrl!="") {
                $('.btn-select-photo').hide();
                $('.btn-change-photo').show();
                $('.btn-delete-photo').show();
                $('#particularsbg').attr("src",res.data.backgroundUrl);
            }else{
                $('.btn-select-photo').show();
                $('.btn-change-photo').hide();
                $('.btn-delete-photo').hide();
                if(res.data.defaultBackgroundUrl!=null&&res.data.defaultBackgroundUrl!=""){
                    $('#particularsbg').attr("src",res.data.defaultBackgroundUrl);
                }else{
                    $('#particularsbg').attr("src",'/static/images/default-bg@1x.png');
                }
            }
            defaultBackgroundUrl=res.data.defaultBackgroundUrl;
            if(res.data.county==null&&res.data.province==null){
                $('#address-city').val('');
            }else{
                if(res.data.county==null){
                    $('#address-city').val(res.data.province + '-' + res.data.city );
                }else{
                    $('#address-city').val(res.data.province + '-' + res.data.city + '-' + res.data.county);
                }
            }
        });
        $('.detail').addClass('show');
        evt.stopPropagation();
    });
//关闭终端详情修改侧边框
    $('body').on('click', function(evt) {
        if(!$(evt.target).hasClass('detail') &&!$(evt.target).hasClass('programsModal')&&!$(evt.target).hasClass('inter-cut')&&!$(evt.target).hasClass('remote')&&!$(evt.target).hasClass('delete')&&!$(evt.target).hasClass('livePictureModal') && $('.detail').find(evt.target).length < 1 && $('.programsModal').find(evt.target).length < 1&& $('.inter-cut').find(evt.target).length < 1&& $('.remote').find(evt.target).length < 1&& $('.delete').find(evt.target).length < 1&& $('.livePictureModal').find(evt.target).length < 1&& $('.programPreviewbjdiv').find(evt.target).length < 1 ) {
            $('.examineListdiv').hide();
            $('.detail').removeClass('show');
            if(!$(evt.target).hasClass('programsModal') && $('.programsModal').find(evt.target).length < 1) {
                queryTerminalsparams.terminalId = '';
                queryTerminalsparams.name = '';
            }
        }
    });
//验证终端背景图片
    function verifyImage(self){
        var file = self.get(0).files[0];
        var extStart = file.name.lastIndexOf(".");
        var ext = file.name.substring(extStart, file.name.length).toUpperCase();
        var reader = new FileReader;
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
                    if (size > maxSize) {
                        notify("danger", "请上传小于5M的图片");
                        $('.btn-select-photo').show();
                        $('.btn-change-photo').hide();
                        $('.btn-delete-photo').hide();
                    }else{
                        var blob = URL.createObjectURL(file);
                        queryTerminalsparams.picture=file;
                        $('#particularsbg').attr('src', blob);
                        $('.btn-select-photo').hide();
                        $('.btn-change-photo').show();
                        $('.btn-delete-photo').show();
                    }
                }
            };
            image.src = evt.target.result;
        };
        reader.readAsDataURL(file);
        self.val('');//上传同一张图片清除上一张信息
    }
    $('.btn-select-photo input').on('change', function () {
        verifyImage($(this));
        queryTerminalsparams.updateBackground="0";
    });
    $('.btn-change-photo input').on('change', function () {
        verifyImage($(this));
        queryTerminalsparams.updateBackground="0";
    });
    $('.btn-delete-photo').on('click', function(evt) {
        $('.btn-select-photo').show();
        $('.btn-change-photo').hide();
        $('.btn-delete-photo').hide();
        if(defaultBackgroundUrl!=null&&defaultBackgroundUrl!=""){
            $('#particularsbg').attr('src', defaultBackgroundUrl);
        }else{
            $('#particularsbg').attr("src",'/static/images/default-bg@1x.png');
        }
        queryTerminalsparams.picture=null;
        queryTerminalsparams.updateBackground="1";
    });
//保存修改终端详情
    $('#particularsSave').on('click', function(evt) {
        var arr = $('#address-city').val().split('-');
        if(arr.length==3){
            queryTerminalsparams.province = arr[0];
            queryTerminalsparams.city = arr[1];
            queryTerminalsparams.county = arr[2];
        }else if(arr.length==2){
            queryTerminalsparams.province = arr[0];
            queryTerminalsparams.city = arr[1];
            queryTerminalsparams.county = '';
        }else if(arr.length==1){
            queryTerminalsparams.province = arr[0];
            queryTerminalsparams.city = '';
            queryTerminalsparams.county = '';
        }else {
            queryTerminalsparams.province = '';
            queryTerminalsparams.city = '';
            queryTerminalsparams.county = '';
        }
        queryTerminalsparams.name= $('#particularsName').val();
        if(queryTerminalsparams.picture==null){
            delete queryTerminalsparams.picture;
        }
        if(  queryTerminalsparams.province==''){
            notify("danger", "终端位置不能为空");
        }else{
            fetchs.uploadMixture('/terminal/updateTerminal', queryTerminalsparams, function (res) {
                if(res.ifSuc == 1) {
                    notify("success", "保存成功");
                    $('.detail').removeClass('show');
                    getDate();
                }else{
                    notify("danger", res.msg);
                }
            })
        }
    });
//终端列表全选效果
    $('body').on('click','#selectAll',function () {
        if ($(this).find('input').prop('checked')) {
            $("tbody tr input[name='item']").prop('checked', true);
            $('.btn-conceal button').attr('disabled', true);
        } else {
            $("tbody tr input[name='item']").prop('checked', false);
        };
        var checklist=0;
        var datalist=0
        $("tbody tr input[name='item']").each(function () {
            datalist++;
            if ($(this).prop('checked')) {
                checklist++;
            }
        })
        if(checklist!=0){
            $('.btn-conceal').show();
        }else{
            $('.btn-conceal').hide();
        }
    });
//终端列表的单个人员批量操作
    $('body').on('click','tbody tr input',function (event) {
        var checklist=0;
        var datalist=0
        $("tbody tr input[name='item']").each(function () {
            datalist++;
            if ($(this).prop('checked')) {
                checklist++;
            }
        })
        if(checklist==datalist){
            $('#selectAll').find('input').prop('checked', true);
        }else{
            $('#selectAll').find('input').prop('checked', false);
        }
        if(checklist!=0){
            $('.btn-conceal').show();
        }else{
            $('.btn-conceal').hide();
        }
    });
//获取批量勾选终端列表封装方法
    function listchecked() {
        selectedlist.terminals.splice(0,selectedlist.terminals.length);
        if(terminalId.terminalId!=''){
            selectedlist.terminals.push(terminalId.terminalId);
        }else {
            $("tbody tr input[name='item']").each(function () {
                if ($(this).prop('checked')) {
                    if(selectedlist.terminals.indexOf($(this).data('id'))==-1){
                        selectedlist.terminals.push($(this).data('id'));
                    }
                }
            })
        }
    }
//批量远程重启终端
    $('.restart').on('click', function() {
        listchecked();
        fetchs.post('/terminal/rebootTerminal', selectedlist, function (res) {
            $('.detail').removeClass('show');
            if(res.ifSuc == 1) {
                getDate()
                $('#remote').modal('hide');
            }else{
                notify("danger", res.msg);
            }
        })
    });
//区分是否批量终端展示提示信息
    $('#remotebatch').on('click', function() {
        $('.remotefont').html('确定批量重启所选终端吗？');
    });
    $('#remotesingle').on('click', function() {
        $('.remotefont').html('确定重启所选终端吗？');
    });
//插播消息input聚焦事件
    $("#comname").focus(function(){
        $(".inter-cutinput").css('border','1px solid #d1d1d1');
        $(".inter-cuterrordiv").css('opacity',0);
    });
    $("#inter-cuttextarea").focus(function(){
        $("#inter-cuttextarea").css('border','1px solid #d1d1d1')
        $(".inter-cuterrordiv").css('opacity',0);
    });
//插播消息
    $('.interMessage').on('click', function() {
        getBroadcastConfigDate();
        listchecked();
        $('.areatypeclick').on('click','a',function (event) {
            $(this).parent().prev().find('.form-control').text($(this).text());
            $(this).parent().prev().find('.form-control').attr("data-id", $(this).data('id'));
        })
    });
    $('.inter-cutbutton').on('click', function() {
        if($("#comname").val()==''){
            $(".inter-cutinput").css('border','1px solid #f74555')
        }else{
            $(".inter-cutinput").css('border','1px solid #d1d1d1')
        }
        if($("#inter-cuttextarea").val()==''){
            $("#inter-cuttextarea").css('border','1px solid #f74555')
        }else{
            $("#inter-cuttextarea").css('border','1px solid #d1d1d1')
        }
        if($("#comname").val()==''){
            $(".inter-cuterrordiv").css('opacity',1);
            $(".inter-cuterror").html("请输入播放时长");
        }else if($("#inter-cuttextarea").val()==''){
            $(".inter-cuterrordiv").css('opacity',1);
            $(".inter-cuterror").html("请输入插播内容");
        }else{
            $(".inter-cuterrordiv").css('opacity',0);
            broadcastMessageparams.terminals=selectedlist.terminals;
            broadcastMessageparams.message.unit=$("#area") .attr("data-id");
            broadcastMessageparams.message.speed=$("#area1") .attr("data-id");
            broadcastMessageparams.message.content= $("#inter-cuttextarea").val();
            broadcastMessageparams.message.length=$("#comname").val();
            $.ajax({
                type:"POST",
                url:"/terminal/broadcastMessage?userId="+fetchs.userId+"&token="+fetchs.token,
                dataType:"json",
                contentType:"application/json",
                data:JSON.stringify(broadcastMessageparams),
                success:function(data){
                    $('.detail').removeClass('show');
                    if(data.ifSuc == 1) {
                        $('#inter-cut').modal('hide');
                        $('#inter-cuttextarea').val('');
                        $('#comname').val('');
                        getDate()
                    }else{
                        notify("danger", data.msg);
                    }
                }
            });
        }
    });
    $('#comname').on('keyup', function() {
        $(this).val($(this).val().match(/^[1-9]{1}[0-9]*$/));
    });
//删除终端节目
    $('.table .table-body').on('click','.btn-delete', function() {
        $('.remotefont').html('确定删除<span class="deletename"></span>的当前节目吗？');
        terminalId.terminalId=$(this).data('id');
        $('.deletename').html($(this).data('name'));
    });
    $('#deleteprogrambtn').on('click', function() {
        fetchs.post('/terminal/deleteTerminalProgram', terminalId, function (res) {
            $('.detail').removeClass('show');
            if(res.ifSuc == 1) {
                $('#delete').modal('hide');
                getDate();
            }else{
                notify("danger", res.msg);
                $('#delete').modal('hide');
                getDate();
            }
        })
    });
//获取最新版本内容
    $('.table-body').on('click', '.btn-upgrad', function(evt) {
        var versionsPresent=$(this).data('name');
        terminalId.terminalId=$(this).data('id');
        fetchs.post('/terminal/getLatestUpdateLog',terminalId, function (res) {
            if(res.ifSuc == 1) {
                $('.versionsPresent').html(versionsPresent);
                $('.versionsNewest').html(res.data.appVersion);
                $('.versionshtml').html(res.data.updateDesc);
            }else{
                notify("danger", res.msg);
            }
        })
    });
//升级版本
    $('.upgradebutton').click(function () {
        fetchs.post('/terminal/updateApp',terminalId, function (res) {
            if(res.ifSuc == 1) {
                function queryUpgradeMethod() {
                    function setprogress(progressdiv) {
                        if(progressdiv=="aa"){
                            var current= $('.upgradeInitial progress').val();
                            if(current>=39){
                                current=39;
                            }
                            $('.upgradeInitial  progress').val(current+1);

                        }else{
                            var current1= $('.upgradeCourse progress').val();
                            if(current1>=99){
                                current1=99;
                            }
                            $('.upgradeCourse  progress').val(current1+1);
                        }
                    }
                    upgradeId.id=terminalId.terminalId;
                    fetchs.post('/terminal/queryUpgradeStatus', upgradeId, function (res) {
                        if (res.ifSuc == 1) {
                            $(".upgradebutton").parent().hide();
                            $('.versionsstate').show();
                            if (res.data == null) {
                                var timer = setTimeout(setprogress('aa'),200);
                                queryUpgradetime = window.setTimeout(queryUpgradeMethod, 2000);
                                $('.upgradeInitial').show();
                                $('.upgradeSuccess').hide();
                                $('.upgradeDefeated').hide();
                                $('.upgradeCourse').hide();
                            } else {
                                if (res.data.result != null) {
                                    $('.upgradeInitial').hide();
                                    if (res.data.result == 1) {
                                        $('.upgradeCourse  progress').val(100);
                                        setTimeout( function() {
                                            $('.upgradeSuccess').show();
                                            $('.upgradeDefeated').hide();
                                            $('.upgradeInitial').hide();
                                            $('.upgradeCourse').hide();
                                        },3000);
                                    } else {
                                        $('.upgradeDefeated').show();
                                        $('.upgradeSuccess').hide();
                                        $('.upgradeInitial').hide();
                                        $('.upgradeCourse').hide();
                                    }
                                    window.clearInterval(queryUpgradetime);
                                } else {
                                    if (res.data.status == 1) {
                                        $('.upgradeInitial').show();
                                        $('.upgradeDefeated').hide();
                                        $('.upgradeSuccess').hide();
                                        $('.upgradeCourse').hide();
                                        var timer = setTimeout(setprogress('aa'),200);
                                    }else if(res.data.status == -1){
                                        $('.upgradeInitial').show();
                                        $('.upgradeDefeated').hide();
                                        $('.upgradeCourse').hide();
                                        $('.upgradeSuccess').hide();
                                        $('.upgradeInitial  progress').val(0);
                                    } else {
                                        $('.upgradeCourse').show();
                                        $('.upgradeInitial').hide();
                                        $('.upgradeDefeated').hide();
                                        $('.upgradeSuccess').hide();
                                        var timer = setTimeout(setprogress('bb'),200);
                                    }
                                    queryUpgradetime = window.setTimeout(queryUpgradeMethod,2000);
                                }
                            }

                        } else {
                            $('.upgradeCourse').hide();
                            $('.upgradeInitial').hide();
                            $('.upgradeSuccess').hide();
                            $('.upgradeDefeated').show();
                        }
                    })
                }
                queryUpgradeMethod();
            }else {
                notify("danger", res.msg);
            }
        })
    });
    $('.defeatedupgrade').click(function () {
        fetchs.post('/terminal/updateApp',terminalId, function (res) {
            if(res.ifSuc == 1) {
                function queryUpgradeMethod() {
                    function setprogress(progressdiv) {
                        if(progressdiv=="aa"){
                            var current= $('.upgradeInitial progress').val();
                            if(current>=39){
                                current=39;
                            }
                            $('.upgradeInitial  progress').val(current+1);

                        }else{
                            var current1= $('.upgradeCourse progress').val();
                            if(current1>=99){
                                current1=99;
                            }
                            $('.upgradeCourse  progress').val(current1+1);
                        }
                    }
                    upgradeId.id=terminalId.terminalId;
                    fetchs.post('/terminal/queryUpgradeStatus', upgradeId, function (res) {
                        if (res.ifSuc == 1) {
                            $(".upgradebutton").parent().hide();
                            $('.versionsstate').show();
                            if (res.data == null) {
                                var timer = setTimeout(setprogress('aa'),200);
                                queryUpgradetime = window.setTimeout(queryUpgradeMethod, 2000);
                                $('.upgradeInitial').show();
                                $('.upgradeSuccess').hide();
                                $('.upgradeDefeated').hide();
                                $('.upgradeCourse').hide();
                            } else {
                                if (res.data.result != null) {
                                    $('.upgradeInitial').hide();
                                    if (res.data.result == 1) {
                                        $('.upgradeCourse  progress').val(100);
                                        setTimeout( function() {
                                            $('.upgradeSuccess').show();
                                            $('.upgradeDefeated').hide();
                                            $('.upgradeInitial').hide();
                                            $('.upgradeCourse').hide();
                                        },3000);
                                    } else {
                                        $('.upgradeDefeated').show();
                                        $('.upgradeSuccess').hide();
                                        $('.upgradeInitial').hide();
                                        $('.upgradeCourse').hide();
                                    }
                                    window.clearInterval(queryUpgradetime);
                                } else {
                                    if (res.data.status == 1) {
                                        $('.upgradeInitial').show();
                                        $('.upgradeDefeated').hide();
                                        $('.upgradeSuccess').hide();
                                        $('.upgradeCourse').hide();
                                        var timer = setTimeout(setprogress('aa'),200);
                                    }else if(res.data.status == -1){
                                        $('.upgradeInitial').show();
                                        $('.upgradeDefeated').hide();
                                        $('.upgradeCourse').hide();
                                        $('.upgradeSuccess').hide();
                                        $('.upgradeInitial  progress').val(0);
                                    } else {
                                        $('.upgradeCourse').show();
                                        $('.upgradeInitial').hide();
                                        $('.upgradeDefeated').hide();
                                        $('.upgradeSuccess').hide();
                                        var timer = setTimeout(setprogress('bb'),200);
                                    }
                                    queryUpgradetime = window.setTimeout(queryUpgradeMethod,2000);
                                }
                            }
                        } else {
                            $('.upgradeCourse').hide();
                            $('.upgradeInitial').hide();
                            $('.upgradeSuccess').hide();
                            $('.upgradeDefeated').show();
                        }
                    })
                }
                queryUpgradeMethod();
            }else {
                notify("danger", res.msg);
            }
        })
    });
    $('#upgradeVersions').on('hidden.bs.modal', function(e){
        window.clearInterval(queryUpgradetime);
        $('.upgradeInitial progress').val('0');
        $('.upgradeCourse progress').val('40');
        $(".upgradebutton").parent().show();
        $('.versionsstate').hide();
        $('.upgradeSuccess').hide();
        $('.upgradeDefeated').hide();
        $('.upgradeInitial').hide();
        $('.upgradeCourse').hide();
    });
//节目同步更新
    $('.CheckUpdatesdiv .icon-close').on('click',function(){
        $('.CheckUpdatesdiv').hide();
        $('.main-panel').css("padding-top","56px");
        $('.fixed-top').css("height","56px");
    });
    $('.CheckUpdatebutton').on('click',function(){
        fetchs.post('/syncProgramVer/checkProgramVer',queryTerminalsparams, function (res) {
            if(res.ifSuc == 1) {
                if( res.code==1000) {
                    notify("success", res.msg);
                    ('.main-panel').css("padding-top","56px");
                    $('.fixed-top').css("height","56px");
                    $('.CheckUpdatesdiv').hide();
                }else {
                    //$('#CheckUpdateModal').modal('show');
                    fetchs.post('/syncProgramVer/upgradeProgramVer',terminalId, function (res) {
                        if(res.ifSuc == 1) {
                            $('.main-panel').css("padding-top","56px");
                            $('.fixed-top').css("height","56px");
                            $('.CheckUpdatesdiv').hide();
                            /*  setTimeout( function(){
                                  $('#CheckUpdateModal').modal('hide');
                              },500);*/
                            notify("success", res.msg);
                        }else{
                            /*   setTimeout( function(){
                                   $('#CheckUpdateModal').modal('hide');
                               },500);*/
                            notify("danger", res.msg);
                        }
                    })
                }
            }else{
                notify("danger", res.msg);
            }
        })
    });
// 节目列表和设置终端的切换
    $('#programsModal').on('shown.bs.tab', 'a[data-toggle="tab"]',function (e) {
        $('#programsModal input[name="downStartTime"]').datetimepicker('setDate',new Date(Math.ceil(new Date().getTime() / (15 * 60 * 1000)) * 15 * 60 * 1000).getTime());
        $('#programsModal input[name="downEndTime"]').datetimepicker('setDate', new Date(Math.ceil(new Date().getTime() / (15 * 60 * 1000)) * 15 * 60 * 1000).getTime()+ 30 * 24 * 60 * 60 * 1000);
        filtrationchecked.terminals=[];
        if(queryTerminalsparams.terminalId==undefined||queryTerminalsparams.terminalId==null||queryTerminalsparams.terminalId==''){
            $("tbody tr input[name='item']").each(function () {
                if ($(this).prop('checked')) {
                    var str=$(this).data();
                    if($(this).data('filtration')==''&&$(this).data('status')==1){
                        if(filtrationchecked.terminals.indexOf($(this).data('id'))==-1){
                            filtrationchecked.terminals.push({
                                id : str.id,
                                name:str.name
                            })
                        }
                    }
                }
            });
        }else{
            filtrationchecked.terminals.push({
                id : queryTerminalsparams.terminalId,
                name:queryTerminalsparams.name
            })
        }
        $('.select-terminals').selector('setOption', {
            selected: filtrationchecked.terminals
        });
        var ids = [];
        for (var i = 0; i < filtrationchecked.terminals.length; i++) {
            ids.push(filtrationchecked.terminals[i].id);
        }
        publishProgram.terminals=ids;
        $('.programcheckedName').html($(this).data('name'));
        $('#programcheckedPreview').attr("data-name", $(this).data('url'));
        publishProgram.programId=$(this).data('id');
        $(e.target).removeClass('active');
        if($(e.target).hasClass('back-step')){//节目详情
            if(isCustomTenant){ //定制租户
                $(".ztree-title").show();
            }else{
                $(".tab-text").show();
            }
            $(e.target).css('visibility','hidden');
        }else{//节目列表
            $(".ztree-title,.tab-text").hide();
            $('#programsModal').find('.modal-title h3').text('设置终端');
            $('#programsModal').find('.back-step').css('visibility','visible');
        }
    });
// 节目时间段展示
    $('#programsModal input').datetimepicker({
        container: '#programsModal .tab-pane.active',
        step: 15,
        format: 'YYYY-MM-DD HH:mm',
        readonly: true
    }).on('hidden.datetimepicker', function (evt, start) {
        if ($(evt.target).attr('name') == 'downStartTime') {
            if ($('input[name="downEndTime"]').val() == '') {
                $('input[name="downEndTime"]').datetimepicker('setDate', new Date(start + 24 * 60 * 60 * 1000).format('YYYY-MM-DD HH:mm'));
            } else {
                var end = new Date($('input[name="downEndTime"]').val().replace(/-/g, '/')).getTime();
                if (start >= end) {
                    $('input[name="downEndTime"]').datetimepicker('setDate', new Date(start + 24 * 60 * 60 * 1000).format('YYYY-MM-DD HH:mm'));
                }
            }
            $('input[name="downEndTime"]').datetimepicker('setOption', {
                start: new Date(start).format('YYYY-MM-DD'),
                min: new Date(start).format('YYYY-MM-DD HH:mm')
            });
        } else {
            if ($('input[name="downStartTime"]').val() == '') {
                $('input[name="downStartTime"]').datetimepicker('setDate', new Date(start - 24 * 60 * 60 * 1000).format('YYYY-MM-DD HH:mm'));
                $('input[name="downEndTime"]').datetimepicker('setOption', {
                    start: new Date(start - 24 * 60 * 60 * 1000).format('YYYY-MM-DD'),
                    min: new Date(start - 24 * 60 * 60 * 1000).format('YYYY-MM-DD HH:mm')
                });
            }
        }
        publishProgram.start = $('input[name="downStartTime"]').val() + ':00';
        publishProgram.end = $('input[name="downEndTime"]').val() + ':00';
    });
    // 普通节目定制节目点击切换
    $("body").on("shown.bs.tab",".ztree-title .nav-link",function (e) {
        if($(this).data("type")==0){//普通节目
            getPrograms.tagId='';
            getLabels(terminalId);
            $("body .back-step").attr("href","#step1");
        }else{//定制节目
            customPage=1;
            customSize=16;
            getCustomPrograms();
        }
    })
//节目模态窗展示事件和节目内点击事件
    var labelTpl='<button ng-repeat="item in tags" data-id="{{item.id}}">{{item.name}}</button>'
    var programTpl = '<ul class="list-box"><li ng-repeat="item in programs"><a href="#step2" role="tab" data-toggle="tab" data-name="{{item.name}}"  data-id="{{item.programId}}"  data-url="{{item.photoUrl}}"><div></div><img src="{{item.photoUrlSmall}}"> <h1></h1><p class="overflowdiv">{{item.name}}</p></a><button class="rogrampreviewButton" data-name="{{item.photoUrl}}">预览</button></li></ul><div class="nothing" ng-if="programs.length<1">暂无节目信息</div>';
    $("body").on("click",".publish-program,.publish-details-program",function () {
        getDataInit();
    })
    function getDataInit(){
        filtrationchecked.terminals=[];
        $('#step1').addClass('active');
        $('#step2').removeClass('active');
        $('#long-termbtn').prop('checked', false);
        $('#playstartTinme').attr('disabled',false);
        $('#playednTinme').attr('disabled',false);
        $('#programsModal input[name="downStartTime"]').datetimepicker('setDate',new Date(Math.ceil(new Date().getTime() / (15 * 60 * 1000)) * 15 * 60 * 1000).getTime());
        $('#programsModal input[name="downEndTime"]').datetimepicker('setDate', new Date(Math.ceil(new Date().getTime() / (15 * 60 * 1000)) * 15 * 60 * 1000).getTime()+ 30 * 24 * 60 * 60 * 1000);
        $('#programsModal input[name="downEndTime"]').datetimepicker('setOption', {
            min: $('input[name="downStartTime"]').val()
        });
        getPrograms.tagId='';
        getLabels(terminalId,1);
    }
    //获取标签list
    function getLabels(terminalId,init) {
        fetchs.post('/terminal/getProgramTags',terminalId, function (res) {
            if(res.ifSuc == 1){
                if(init&&init==1){
                    isCustomTenant= res.data.isCustomTenant;//true 定制租户  false 普通租户
                    if(isCustomTenant){
                        $(".ztree-title").show();
                        $(".tab-text").hide();
                    }else{
                        $(".ztree-title").hide();
                        $(".tab-text").show();
                    };
                };
                if(!res.data.tags) res.data.tags=[];
                res.data.tags.unshift({'id':"", 'name':"全部"});
                $('.programs-filter').html(soda(labelTpl,res.data));
                if(!init&&init!=1&&getPrograms.tagId) $('body button[data-id='+getPrograms.tagId+']').addClass('active');
                else $('.programs-filter button:first-child').addClass('active');
                getPrograms.pageNum=1;
                getPrograms.pageSize=12;
                if(isCustomTenant){
                    $(".ztree-title li").removeClass('active');
                    $(".ztree-title li:first-child").addClass("active");
                }
                getProgramList(getPrograms,init);
                scrollList();
            }else{
                notify("danger", res.msg);
            }
        });
    }
    //获取普通节目list
    function getProgramList(getPrograms,init) {
        fetchs.post('/terminal/getPrograms',getPrograms, function (res) {
            if(res.ifSuc == 1) {
                programaddlist.programlist=res.data.programs;
                programaddlist.programtotal=res.data.pageInfo.total;
                $('#programsModal .slideprograms').html(soda(programTpl,res.data));
                if(init&&init==1){
                    $("body #programsModal").modal("show");
                };
            }else{
                notify("danger", res.msg);
                return false;
            }
        });
    }
    //定制节目list
    function getCustomPrograms() {
        fetchs.post('/terminal/getDingZhiProgram',{"pageSize":16, "pageNum":1}, function (res) {
            if(res.ifSuc == 1) {
                customList=res.data.programs;
                customTotal=res.data.pageInfo.total;
                if(res.code==1&&res.code){
                    $('#programsModal').modal('hide');
                    setTimeout(function(){
                        notify('danger',res.msg);
                    },600);
                    return false;
                };
                $('#step3').html(soda(programTpl,res.data));
                $("body .back-step").attr("href","#step3");
                scrollCustomList();
            }else{
                notify("danger", res.msg);
            }
        });
    }
    $('#programsModal').on('hidden.bs.modal', function(e){
        params.status=filterStatus;
        $('#programsModal').find('.modal-title h3').text('选择节目');
        $('#programsModal').find('.back-step').css('visibility','hidden');
    });
    $('#Publishimmediately').on('click',function(){
        if($('#long-termbtn').prop('checked')){
            publishProgram.standing=true;
        }else{
            publishProgram.standing=false;
        }
        publishProgram.start=$('#playstartTinme').val()+':00';
        publishProgram.end=$('#playednTinme').val()+':00';
        if($('#select-terminals').children().length<=1){
            notify("danger", '未选择终端');
        }else{
            fetchs.post('/terminal/publishProgram',publishProgram, function (res) {
                $('.detail').removeClass('show');
                if(res.ifSuc == 1) {
                    $('#programsModal').modal('hide');
                    if(res.data.requestId!=null){
                        $('#programshadeModal').modal('show');
                        refreshTerminalProgramStatus.requestId= res.data.requestId;
                        function refreshTerminal() {
                            fetchs.post('/terminal/refreshTerminalProgramStatus',refreshTerminalProgramStatus, function (res) {
                                if(res.ifSuc == 1) {
                                    if(res.data.errMsg==''){
                                        setTimeout( function(){
                                            $('#programshadeModal').modal('hide');
                                            notify("success", '节目发布成功');
                                            getDate();
                                        },500);
                                    }else{
                                        if(res.data.errMsg==null){
                                            refreshTerminalTimeout = window.setTimeout(refreshTerminal,3000);
                                        }else{
                                            var str='';
                                            var str1='';
                                            for (var i = 0, len = res.data.errMsg.length; i < len; i++) {
                                                for (var a = 0, len1 = res.data.errMsg[i].terminals.length; a < len1; a++) {
                                                    str1 += res.data.errMsg[i].terminals[a]+'、';
                                                }
                                                str1=str1.substring(0,str1.length-1);
                                                str += '<div class="feedbackfontdiv">终端<span class="feedbackfont">'+str1+'</span>发布失败。</div></div><div>'+res.data.errMsg[i].msg+'</div>';
                                            }
                                            $('.feedbackdiv').html(str);

                                            window.clearInterval(queryUpgradetime);
                                            setTimeout( function(){
                                                $('#programshadeModal').modal('hide');
                                                $('#feedbackModal').modal('show');
                                            },500);
                                            params.pageSize = 15;
                                            params.status = '';
                                            getDate();
                                        }
                                    }
                                }else{
                                    notify("danger", res.msg);
                                }
                            })
                        }
                        refreshTerminal();
                    }else{
                        if(res.data.errMsg!=null){
                            var str='';
                            var str1='';
                            for (var i = 0, len = res.data.errMsg.length; i < len; i++) {
                                for (var a = 0, len1 = res.data.errMsg[i].terminals.length; a < len1; a++) {
                                    str1 += res.data.errMsg[i].terminals[a]+'、';
                                }
                                str1=str1.substring(0,str1.length-1);
                                str += '<div class="feedbackfontdiv">终端<span class="feedbackfont">'+str1+'</span>发布失败。</div></div><div>'+res.data.errMsg[i].msg+'</div>';
                            }
                            $('.feedbackdiv').html(str);
                            setTimeout( function(){
                                $('#feedbackModal').modal('show');
                            },500);
                        }else{
                            params.status='';
                            getDate();
                        }
                    }
                }else{
                    notify("danger", res.msg);
                }
            })
        }
    });
    $('#long-termbtn').on('click',function(){
        if($('#long-termbtn').prop('checked')){
            $('#playstartTinme').val('');
            $('#playednTinme').val('');
            $('#playstartTinme').attr('disabled',true);
            $('#playednTinme').attr('disabled',true);
        }else{
            $('#programsModal input[name="downStartTime"]').datetimepicker('setDate',new Date(Math.ceil(new Date().getTime() / (15 * 60 * 1000)) * 15 * 60 * 1000).getTime());
            $('#programsModal input[name="downEndTime"]').datetimepicker('setDate', new Date(Math.ceil(new Date().getTime() / (15 * 60 * 1000)) * 15 * 60 * 1000).getTime()+ 30 * 24 * 60 * 60 * 1000);
            $('#playstartTinme').attr('disabled',false);
            $('#playednTinme').attr('disabled',false);
        }
    });
//节目设置终端预览
    $('#programcheckedPreview').on('click',function(){
        $('.modal-backdrop').eq(1).show();
        $('.modal-backdrop').eq(0).hide();
        $('.programPreviewbjdiv').show();
        $('.programPreviewImg').attr("src",$(this).attr("data-name"));
    });
    $('.feedbackbtn').on('click',function(){
        $('#feedbackModal').modal('hide');
    });
//节目绑定终端弹框
    $('#select-terminals').selector({
        class: 'selector-terminals',
        container: '#programsModal .tab-pane.active',
        resources: function(selector) {
            fetchs.post('/terminal/queryTerminals',programsparams,function(res) {
                if(res.ifSuc == 1) {
                    //发布节目选择终端
                    if (res.data.terminals !=null){
                        selector.update(res.data.terminals);
                    }
                }else{
                    notify("danger", res.msg);
                }
            })

        },
        renderSelected: function (item) {
            return '<span data-id="'+item.id+'">'+item.name+'<i class="icon icon-delete-personnel"></i></span>'
        },
        render: function(selector, item,selected) {
            var str=''
            if(item.program==null){
                str+='<label><input name="selector"  type="checkbox" value="' + item.id + '" ' + (selected >= 0 ? 'checked' : '') + '><span class="effect"></span><div class="item"><h3>'+item.name+'</h3>' ;
                if(item.meetingRoom!=null){
                    str+= '<p>'+item.meetingRoom.name+'</p>'
                }else{
                    str+= '<p>未绑定会议室</p>'
                }
                str+='</div></label>';
                return str;
            }else{
                return '<div></div>';
            }
        }
    }).on('hidden.selector', function (evt, selector, selected) {
        var ids = [];
        for (var i = 0; i < selected.length; i++) {
            ids.push(selected[i].id);
        }
        publishProgram.terminals=ids;
    });
    //修改详情选择会议室
    $('input[name="boardroom"]').selector({
        container: '.detail',
        selected: [],
        resources: function(selector) {
            fetchs.post('/terminal/getRoomTerminals', getRoomTerminalsparams, function (res) {
                if(res.ifSuc == 1) {
                    selector.update(res.data);
                }else{
                    notify("danger", res.msg);
                }
            })
        },
        render: function(selector, item,selected) {
            return '<label><input name="selector"  type="radio" value="' + item.id + '" ' + (selected >= 0 ? 'checked' : '' ) + '><span class="icon icon-tick"></span>'+(item.terminals ? '<span class="btn-binding">绑定</span>':'')+'<h3><span  class="'+(item.status!=2 ? "siteoverflowdiv1":"siteoverflowdiv") +'">'+item.name+'</span>'+(item.status!=2 ? '<span class="stopped">已停用</span>':'')+'</h3>'+(item.location ? '<p><span class="siteoverflowdiv "><i class="icon icon-xq-site"></i>'+item.location+'</span></p>':'<p><span class="siteoverflowdiv "><i class="icon icon-xq-site"></i>无</span></p>')+'</label>'
        },
        renderFilter: function(item) {
            // body...
        },
        filter: function(){
            var data = [{
                key: 1,
                value: '已绑定'
            },
                {
                    key: 0,
                    value: '未绑定'
                }];
            return data;
        },
        onFilter: function(selector) {
            if($('.filter').find('input').eq(0).is(':checked')){
                getRoomTerminalsparams.status=1;
                if($('.filter').find('input').eq(0).is(':checked')&&$('.filter').find('input').eq(1).is(':checked')){
                    getRoomTerminalsparams.status='';
                }
            } else  if($('.filter').find('input').eq(1).is(':checked')){
                getRoomTerminalsparams.status=0;
            }else{
                getRoomTerminalsparams.status='';
            }
            fetchs.post('/terminal/getRoomTerminals', getRoomTerminalsparams, function (res) {
                selector.update(res.data);
            })
            $('.examineListdiv').hide();
        },
        onSelect: function(ele, index) {
            queryTerminalsparams.roomId=index.id;
            var str=''
            if( index.terminals!=null){
                str+='<div class="filter">' +
                    '<span class="examinename">'+index.name+'</span>' +
                    '<span class="examineListnum">终端'+index.terminals.length+'个</span>' +
                    '</div>' +
                    '<div class="examineList">' ;
                for (var i = 0, len = index.terminals.length; i < len; i++) {
                    str += '<div><span class="icon icon-kzt-display"></span><span class="examineListname">'+index.terminals[i].name+'</span></div>'
                }
                str += '</div>';
                $('.examineListdiv').html(str);
                $('.examineListdiv').show();

            }else{
                $('.examineListdiv').hide();
            }
            meetingRoomchecked.meetingRoom=[];
            meetingRoomchecked.meetingRoom.push({
                id : index.id,
                name:index.name
            })
            $('input[name="boardroom"]').selector('setOption', {
                selected: meetingRoomchecked.meetingRoom
            });
        }
    });
//节目列表预览
    $('.programPreview .close').on('click',function(){
        $('.programPreviewbjdiv').hide();
        $('.modal-backdrop').eq(0).show();
        $('.modal-backdrop').eq(1).hide();
    });
    $('.programPreviewbj').on('click',function(){
        $('.programPreviewbjdiv').hide();
        $('.modal-backdrop').eq(0).show();
        $('.modal-backdrop').eq(1).hide();
    });
    //节目预览
    $('body').on('click', '.rogrampreviewButton', function(evt) {
        $('.modal-backdrop').eq(1).show();
        $('.modal-backdrop').eq(0).hide();
        $('.programPreviewbjdiv').show();
        $('.programPreviewImg').attr("src",$(this).data('name'));
    });
//切换节目标签事件
    $('.programs-filter').on('click', 'button', function(evt) {
        $(this).siblings().removeClass("active");
        $(this).addClass('active');
        getPrograms.tagId= $(this).data('id');
        getPrograms.pageNum=1;
        getProgramList(getPrograms);
    });
//实时总览内交互事件
    $('body').on('click', '.smallPicturediv', function() {
        sendScreenshotCommandparams.ids='';
        $('#livePictureModal').modal('show');
        $('#livePictureModal').find('.loading').hide();
        $('#livePictureModal').find('.modal-title').text($(this).attr("data-programName"));
        $('#livePictureModal').find('.livepreview').html('<img src="' + $(this).attr("data-name") + '">').show();
        $('.modal-backdrop').eq(0).removeClass('show');
        $('#pandectModal').css('z-index', 1040);
    });
    $('body').on('mouseover', '.smallPicturediv', function() {
        $(this).find('.programNamediv').show();
    });
    $("body").on('mouseout', '.smallPicturediv', function() {
        $(this).find('.programNamediv').hide();
    });
    $('#pandectModal').on('click', 'a', function() {
        sendScreenshotCommandparams.ids=$(this).data('id');
        var parents=$(this).parents('.traverselist');
        parents.find('.loadingdiv').show();
        parents.find('.loadingerrordiv').hide();
        circulation=1;
        sendScreenshotCommandparams.requestId=refreshScreenshotparams.requestId;
        fetchs.post('/terminal/sendScreenshotCommand',sendScreenshotCommandparams,function(res) {
            refreshScreenshotparams.requestId=res.data.requestId;
            refreshScreenshotparams.ids=sendScreenshotCommandparams.ids;
            function realMethod(){
                if(circulation==2){
                    return ;
                }
                fetchs.post('/terminal/refreshScreenshot',refreshScreenshotparams,function(res) {
                    if(res.ifSuc == 1) {
                        var aa=0;
                        if($('.traverselist').length==0){
                            var str='';
                            for (var i = 0, len = res.data.terminals.length; i < len; i++) {
                                if(res.data.terminals[i].pictureUrl==""||res.data.terminals[i].pictureUrl==null){
                                    aa++;
                                }
                                str+= '<li class="traverselist">';

                                str+= '<h6 class="smallPicturediv" data-name="' + res.data.terminals[i].pictureUrl + '"  data-programName="' + res.data.terminals[i].programName + '"><img src="' + res.data.terminals[i].smallPictureUrl + '">'
                                if(res.data.terminals[i].programName!=''){
                                    str+=  '<span class="programNamediv"><span>'+ res.data.terminals[i].programName + '</span></span>'
                                }
                                str+= '</h6><h6 class="loadingdiv"><span class="icon icon-changeimg"></span><span class="shadediv"></span><span class="icon-refresh1"><img src="../static/images/loading2.png"></span></h6>'

                                str+='</h6><h6 class="loadingerrordiv"><span class="icon icon-failpicture"></span><span class="loadingerrortext">加载失败，请<a  data-id="' + res.data.terminals[i].terminalId + '">重试</a></span></h6><p class="pandectlistname">' + res.data.terminals[i].terminalName + '</p>' +
                                    '<p class="pandectlistroom overflowdiv">' + res.data.terminals[i].roomName + '</p>';
                                str+= '</li>';
                            }
                            $('#pandectModal ul.list-box').html(str);
                            for (var i = 0, len = res.data.terminals.length; i < len; i++) {
                                if(res.data.terminals[i].pictureUrl!=""&&res.data.terminals[i].pictureUrl!=null){
                                    parents.find(".smallPicturediv").show();
                                    parents.find(".loadingdiv").hide();
                                    parents.find(".smallPicturediv img").attr("src",res.data.terminals[i].pictureUrl);
                                    parents.find(".smallPicturediv").attr("data-name",res.data.terminals[i].pictureUrl);
                                }else{
                                    parents.find(".smallPicturediv").hide();
                                    parents.find(".loadingdiv").show();
                                }
                            }
                        }else{
                            for (var i = 0, len = res.data.terminals.length; i < len; i++) {
                                if(res.data.terminals[i].pictureUrl==""||res.data.terminals[i].pictureUrl==null){
                                    aa++;
                                }
                                if(res.data.terminals[i].pictureUrl!=""&&res.data.terminals[i].pictureUrl!=null){
                                    parents.find(".smallPicturediv").show();
                                    parents.find(".loadingdiv").hide();
                                    parents.find(".smallPicturediv img").attr("src",res.data.terminals[i].pictureUrl);
                                    parents.find(".smallPicturediv").attr("data-name",res.data.terminals[i].pictureUrl);
                                }else{
                                    parents.find(".smallPicturediv").hide();
                                    parents.find(".loadingdiv").show();
                                }
                            }
                        }
                        if(aa==0){
                            circulation=2;
                        }else{
                            setTimeout( function() {
                                realMethod();
                            },3000)
                        }
                    }else{
                        notify("danger", res.msg);
                    }
                })
            }
            setTimeout( function() {
                realMethod();
            },3000)
        })
        setTimeout( function(){
            circulation=2;
            refreshScreenshotparams.requestId='';
            if(parents.find("img").attr('src')!=undefined&&parents.find("img").attr('src')!=''&&parents.find("img").attr('src')!=null&&parents.find("img").attr('src')!="null"){
                parents.find('.smallPicturediv').show();
                parents.find('.loadingdiv').hide();
                parents.find('.loadingerrordiv').hide();
            }else{
                parents.find('.smallPicturediv').hide();
                parents.find('.loadingdiv').hide();
                parents.find('.loadingerrordiv').show();
            }
        },2*60*1000);
    });
//节目列表滚动加载
    function getAddressList() {
        fetchs.post('/terminal/getPrograms',getPrograms, function (res) {
            if(res.ifSuc == 1) {
                programaddlist.programlist= programaddlist.programlist.concat(res.data.programs);
                programaddlist.programtotal=res.data.pageInfo.total;
                for (var i = 0, len = res.data.programs.length; i < len; i++) {
                    str+='<li>' +
                        '<a href="#step2" role="tab" data-toggle="tab" data-name="'+res.data.programs[i].name+'"  data-id="'+res.data.programs[i].programId+'"  data-url="'+res.data.programs[i].photoUrl+'">'+
                        '<div></div>'+
                        '<img src="'+res.data.programs[i].photoUrl+'">'+
                        '<h1></h1>'+
                        '<p class="overflowdiv">'+res.data.programs[i].name+'</p>'+
                        '</a>'+
                        '<button class="rogrampreviewButton" data-name="'+res.data.programs[i].photoUrl+'">预览</button>'+
                        '</li>';
                }
                $('.programs-list ul').html($('.programs-list ul').html()+str);
            }else{
                notify("danger", res.msg);
            }
        })
    };
    //滚动加载 -- 普通节目
    function  scrollList() {
        //滚动加载
        document.getElementsByClassName('slideprograms')[0].addEventListener('scroll',function(e){
            var target = e.currentTarget;
            var curScrollTop = target.scrollTop;
            var targetHeight = target.offsetHeight;
            var listCntHeight = $("#programsModal .programs-list ul").height()+25;
            var isEnd = curScrollTop + targetHeight === listCntHeight;
            console.log(isEnd)
            if(isEnd) {
                if(programaddlist.programlist.length<programaddlist.programtotal){
                    if(getPrograms.pageNum>=(programaddlist.programtotal/getPrograms.pageSize)){
                        return false;
                    };
                    getPrograms.pageNum += 1;
                    getAddressList();
                }
            }
        })
    };
    //滚动加载 -- 定制节目
    function  scrollCustomList() {
        document.getElementById('step3').addEventListener('scroll',function(e){
            var target = e.currentTarget;
            var curScrollTop = target.scrollTop;
            var targetHeight = target.offsetHeight;
            var listCntHeight = $("#step3 ul.list-box").height()+25;
            var isEnd = curScrollTop + targetHeight >= listCntHeight;
            if(isEnd) {
                if(customList.length<customTotal){
                    if(customPage>=(customTotal/customSize)){
                        return false;
                    };
                    customPage += 1;
                    fetchs.post('/terminal/getDingZhiProgram',{tagId:'', pageSize:customSize, pageNum:customPage}, function (res) {
                        if(res.ifSuc == 1) {
                            customList= customList.concat(res.data.programs);
                            customTotal=res.data.pageInfo.total;
                            $('#step3').html(soda(programTpl,{'programs':customList}));
                        }else{
                            notify("danger", res.msg);
                        }
                    })
                }
            }
        })
    };
//查看版本更新记录
    $('body').on('click', '.contentmore', function () {
        window.open("/syncProgramVer/gotoVersionsLogPage?token="+_userInfo.token+"&type="+1);
});
//查看节目更新记录
$('body').on('click', '.CheckUpdateDetails', function () {
    window.open("/syncProgramVer/gotoVersionsLogPage?token="+_userInfo.token+"&type="+2);
})
//查看更新故障排除页面
$('body').on('click', '.troublePage', function () {
    window.open("/syncProgramVer/gototroublePage?token="+_userInfo.token);
})
});