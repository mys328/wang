$(function() {
    //页面全局性数据初始化
    var pageObj={
        "totality":0,//全部数据总条数
        "dataList":[],
        "currentPage":1,//当前页面
        "pageSize":15,//分页条数
        "isloading":0,  // 0 不加载 1 加载
        "log_id":'',
        "currentProgramLog":"",
    }
    var listTpl = '<li ng-repeat="item in data" data-id="{{item.id}}"><p><span class="point"></span><span class="versions-name">版本V<i>{{item.programVersionNum}}</i>更新内容</span><span class="log-time">{{item.publishTime|dateTime}}发布</span></p><div class="log-con">{{item.publishNote}}</div></li><div ng-if="data.length<1" class="nothing">暂无节目版本更新内容</div>';
    var versionTpl = '<li ng-repeat="item in data" ><p><span class="point"></span><span class="versions-name">版本<i>{{item.title}}</i>更新内容</span></p><div class="log-con" ng-html="item.recode"></div></li><div ng-if="data.length<1" class="nothing">暂无节目版本更新内容</div>';
    var type = getQueryVariable("type"); //type : 1(版本更新记录)  2(节目更新记录)
    if(type==1){
        $(".title").html('终端版本更新内容');
        getVersion();
    }else if(type==2){
        $(".title").html("节目版本更新内容");
        getProgram();
    }
    //版本id存入
    $('body').on('click','.btn-default',function(){
        var current_id = $(this).data('id');
        $.each(pageObj.dataList,function (index,item) {
            if(item.id==current_id){
                pageObj.currentProgramLog = item;
                return pageObj;
            };
        });
        $("#synchronization .versions-name").html("V"+pageObj.currentProgramLog.programVersionNum);
        $('#synchronization .form-group textarea').val(pageObj.currentProgramLog.publishNote);
        $("#synchronization .form-group").removeClass('has-danger');
    })
    //修改
    $('body').on('click','.save-btn',function(){
        var textarea_val = $('#synchronization textarea').val();
        var log_id = $(this).data('id');
        !textarea_val? textarea_val=pageObj.currentProgramLog.publishNote:textarea_val;
        $('body .save-btn').attr("disabled",true);
        fetchs.post('/updateProgramVersion',{"publishNote":textarea_val,"programVersionId":pageObj.currentProgramLog.id,"ver":pageObj.currentProgramLog.ver},function(result){
            if(result.ifSuc==1){
                getProgram();
                notify('success','修改成功');
                $('body .save-btn').attr("disabled",false);
                $('#synchronization').modal('hide');
            }else{
                notify('danger',result.msg);
                if(result.ifSuc==10){
                    goLoginPage();
                }
            }
        })
    })
    //获取数据
    function getVersion(){
        fetchs.post('/syncProgramVer/getAllRecode',{},function(result){
            if(result.ifSuc==1){
                render(versionTpl,result.data.minVerList);
            }else{
                notify('danger',result.msg);
            }
        })
    }
    function getProgram(){
        fetchs.post('/syncProgramVer/getProgramVersionList',{},function(result){
            if(result.ifSuc==1){
                pageObj.dataList=pageObj.dataList.concat(result.data.list);
                render(listTpl,result.data.list);
            }else{
                notify('danger',result.msg);
            }
        })
    }
    function render(listTpl,dataList){
        $('body .list-box').html(soda(listTpl,{'data':dataList}));
    }
    // 时间格式化
    function momentTime(staTime){
        return moment(Number(staTime)).format('YYYY-MM-DD')+' '+moment(Number(staTime)).format('HH:mm');
    }
    // 获取url参数
    function getQueryVariable(variable) {
        var query = window.location.search.substring(1);
        var vars = query.split("&");
        for (var i=0;i<vars.length;i++) {
            var pair = vars[i].split("=");
            if(pair[0] == variable){return pair[1];}
        }
        return(false);
    }
})