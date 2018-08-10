<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
	<meta name="format-delection" content="telephone=no"/>
	<meta name="format-delection" content="email=no"/>
	  <link rel="stylesheet" href="/static/fonts/demo.css">
    <link rel="stylesheet" href="/static/fonts/iconfont.css">
    <link rel="stylesheet" type="text/css" href="/static/css/scanMeeting/style.css">
    <link href="/static/css/scanMeeting/mobiscroll.animation.css" rel="stylesheet" type="text/css" />
    <link href="/static/css/scanMeeting/mobiscroll.widget.css" rel="stylesheet" type="text/css" />
    <link href="/static/css/scanMeeting/mobiscroll.widget.ios.css" rel="stylesheet" type="text/css" />
    <link href="/static/css/scanMeeting/mobiscroll.scroller.css" rel="stylesheet" type="text/css" />
    <link href="/static/css/scanMeeting/mobiscroll.scroller.ios.css" rel="stylesheet" type="text/css" />
    <!--日历-->
    <link rel="stylesheet" href="/static/css/scanMeeting/layer.css"><!--layer弹框-->
	<title>企云会-连接组织的智慧</title>
</head>
<body>
	<div class="box">
		<div class="meetingname">
		    <input type="text" placeholder="请输入会议名称(必填)" id="meetname" maxlength="50">
		</div>
		<div class="newbox">
	      <div class="site">
		    <p class="iconfont icon-xq-place createmeetingroomicon" style="font-size: 14.5px;"></p>
		    <p class="newname">地点</p>
		    <p class="createmeetingroomName">${meetingRoomName}</p>
		  </div>
		  <div class="starttime">
		    <p class="iconfont icon-xq-time createmeetingroomicon"></p>
		    <p class="newname">开始时间</p>
		    <input type="text" class="outinput" id="kaitime">
		  </div>
		  <div class="endtime">
		    <p class="iconfont icon-xq-time createmeetingroomicon"></p>
		    <p class="newname">结束时间</p>
		    <input type="text" class="outinput" id="endtime">
		  </div>
      <input type="hidden" value="${tenantId}" id="tenantId"/>
      <input type="hidden" value="${meetingRoomId}" id="meetingRoomId" />
      <input type="hidden" value="${unionId}" id="unionid"/>
		</div>
		<div class="createbth">
			<p>确定</p>
		</div>
	</div>
</body>
<script type='text/javascript' src='/static/js/jquery-1.8.3.js'></script>

<script src="/static/js/scanMeeting/mobiscroll.core.js"></script>
<script src="/static/js/scanMeeting/mobiscroll.widget.js"></script>
<script src="/static/js/scanMeeting/mobiscroll.scroller.js"></script>
<script src="/static/js/scanMeeting/mobiscroll.util.datetime.js"></script>
<script src="/static/js/scanMeeting/mobiscroll.datetimebase.js"></script>
<script src="/static/js/scanMeeting/mobiscroll.widget.ios.js"></script>
<script src="/static/js/scanMeeting/mobiscroll.i18n.zh.js"></script>
<script type="text/javascript" src="/static/js/scanMeeting/layer.m.js"></script>
<script type="text/javascript">
    /*获取当前时间的整点*/
    function CurentTime() {
        var now = new Date();
        var year = now.getFullYear();       //年
        var month = now.getMonth() + 1;     //月
        var day = now.getDate();            //日

        var hh = now.getHours();            //时
        var mm = now.getMinutes();          //分

        var clock = year + "-";

        if(month < 10)
            clock += "0";

        clock += month + "-";

        if(day < 10){ clock += "0";}
        clock += day + " ";


        if(mm<=30){
            mm=30;
            if(hh < 10){clock += "0";}
            clock += hh + ":";
        }
        else{
            hh = hh+1;
            if(hh < 10){clock += "0";}
            clock += hh + ":";
            mm = 0;
            clock += '0';
        }
        clock += mm;


        return(clock);
    }
    var CurentTime=CurentTime();
    $("#kaitime").val(CurentTime);

    function CurentTime1() {
        var now = new Date();

        var year = now.getFullYear();       //年
        var month = now.getMonth() + 1;     //月
        var day = now.getDate();            //日

        var hh = now.getHours();            //时
        var mm = now.getMinutes();          //分

        var clock = year + "-";

        if(month < 10)
            clock += "0";

        clock += month + "-";

        if(day < 10){ clock += "0";}
        clock += day + " ";


        if(mm<=30){
            mm=0;
            hh = hh+1;
            if(hh < 10){clock += "0";}
            clock += hh + ":";
            clock += "0";
        }
        else{
            hh = hh+1;
            if(hh < 10){clock += "0";}
            clock += hh + ":";
            mm = 30;
        }
        clock += mm;
        return(clock);
    }
    var CurentTime1=CurentTime1();
    $("#endtime").val(CurentTime1);

    //选择开始时间
    $(function () {
        function CurentTime(time) {
            var now = new Date(time);

            var year = now.getFullYear();       //年
            var month = now.getMonth() + 1;     //月
            var day = now.getDate();            //日

            var hh = now.getHours();            //时
            var mm = now.getMinutes();          //分

            var clock = year + "-";

            if(month < 10)
                clock += "0";

            clock += month + "-";
            if(day < 10){ clock += "0";}
            clock += day + " ";
            if(hh < 10){clock += "0";}
            clock += hh + ":";
            if(mm < 10){clock += "0";}
            clock += mm;
            return(clock);
        }
        var currYear = (new Date()).getFullYear();// 获取年
        var currMonth = (new Date()).getMonth(); // 获取月
        var currDay = (new Date()).getDate(); // 获取日
        var currHour = (new Date()).getHours(); // 获取时
        var currMinutes = (new Date()).getMinutes(); // 获取分
        var opt={};
        opt.date = {preset : 'date'};
        opt.datetime = {preset : 'datetime',minDate:new Date(currYear, currMonth, currDay, currHour, currMinutes)};
        opt.time = {preset : 'time'};
        opt.default = {
            preset: 'datetime', //日期，可选：date\datetime\time\tree_list\image_text\select
            theme: 'ios', //皮肤样式，可选：default\android\android-ics light\android-ics\ios\jqm\sense-ui\wp light\wp
            display: 'bottom', //显示方式 ，可选：modal\inline\bubble\top\bottom
            mode: 'scroller', //日期选择模式，可选：scroller\clickpick\mixed
            lang:'zh',
            dateOrder: 'yymmdd', //面板中日期排列格式
            startYear:currYear, //开始年份
            endYear:currYear + 100, //结束年份
            dateFormat : 'yy-mm-dd', // 日期格式
            timeFormat: 'HH:ii',
            timeWheels: 'HHii',
            onSelect: function (value, inst) {
                var endtime = $("#endtime").val();
                if (new Date(Date.parse(endtime.replace(/-/g, "/"))).getTime() - new Date(Date.parse(value.replace(/-/g, "/"))).getTime()<1800000) {
                    var d = new Date(new Date(Date.parse(value.replace(/-/g, "/"))).getTime()+1800000);    //根据时间戳生成的时间对象
                    Date.prototype.format = function(format) {
                        var date = {
                            "M+": this.getMonth() + 1,
                            "d+": this.getDate(),
                            "h+": this.getHours(),
                            "m+": this.getMinutes(),
                            "s+": this.getSeconds(),
                            "q+": Math.floor((this.getMonth() + 3) / 3),
                            "S+": this.getMilliseconds()
                        };
                        if (/(y+)/i.test(format)) {
                            format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
                        }
                        for (var k in date) {
                            if (new RegExp("(" + k + ")").test(format)) {
                                format = format.replace(RegExp.$1, RegExp.$1.length == 1
                                    ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
                            }
                        }
                        return format;
                    }
                    endtime =d.format('yyyy-MM-dd hh:mm');
                    $("#endtime").val(endtime);
                }
            }
        };
        var optDateTime = $.extend(opt['datetime'], opt['default']);
        $("#kaitime").mobiscroll(optDateTime)
    });
    //选择结束时间
    $(function () {
        function CurentTime(time) {
            var now = new Date(time);

            var year = now.getFullYear();       //年
            var month = now.getMonth() + 1;     //月
            var day = now.getDate();            //日

            var hh = now.getHours();            //时
            var mm = now.getMinutes();          //分

            var clock = year + "-";

            if(month < 10)
                clock += "0";

            clock += month + "-";
            if(day < 10){ clock += "0";}
            clock += day + " ";
            if(hh < 10){clock += "0";}
            clock += hh + ":";
            if(mm < 10){clock += "0";}
            clock += mm;
            return(clock);
        }
        var currYear = (new Date()).getFullYear();// 获取年
        var currMonth = (new Date()).getMonth(); // 获取月
        var currDay = (new Date()).getDate(); // 获取日
        var currHour = (new Date()).getHours(); // 获取时
        var currMinutes = (new Date()).getMinutes(); // 获取分
        var opt={};
        opt.date = {preset : 'date'};
        opt.datetime = {preset : 'datetime',minDate:new Date(currYear, currMonth, currDay, currHour, currMinutes)};
        opt.time = {preset : 'time'};
        opt.default = {
            preset: 'datetime', //日期，可选：date\datetime\time\tree_list\image_text\select
            theme: 'ios', //皮肤样式，可选：default\android\android-ics light\android-ics\ios\jqm\sense-ui\wp light\wp
            display: 'bottom', //显示方式 ，可选：modal\inline\bubble\top\bottom
            mode: 'scroller', //日期选择模式，可选：scroller\clickpick\mixed
            lang:'zh',
            dateOrder: 'yymmdd', //面板中日期排列格式
            startYear:currYear, //开始年份
            endYear:currYear + 100, //结束年份
            dateFormat : 'yy-mm-dd', // 日期格式
            timeFormat: 'HH:ii',
            timeWheels: 'HHii',
            onSelect: function (value, inst) {
                var endtime='';
                var starttime = $("#kaitime").val();
                if (new Date(Date.parse(value.replace(/-/g, "/"))).getTime() - new Date(Date.parse(starttime.replace(/-/g, "/"))).getTime() <900000) {
                    var d = new Date(new Date(Date.parse(starttime.replace(/-/g, "/"))).getTime()+1800000);    //根据时间戳生成的时间对象
                    Date.prototype.format = function(format) {
                        var date = {
                            "M+": this.getMonth() + 1,
                            "d+": this.getDate(),
                            "h+": this.getHours(),
                            "m+": this.getMinutes(),
                            "s+": this.getSeconds(),
                            "q+": Math.floor((this.getMonth() + 3) / 3),
                            "S+": this.getMilliseconds()
                        };
                        if (/(y+)/i.test(format)) {
                            format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
                        }
                        for (var k in date) {
                            if (new RegExp("(" + k + ")").test(format)) {
                                format = format.replace(RegExp.$1, RegExp.$1.length == 1
                                    ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
                            }
                        }
                        return format;
                    }
                    endtime =d.format('yyyy-MM-dd hh:mm');
                    $("#endtime").val(endtime);
                }
            }
        };
        var optDateTime = $.extend(opt['datetime'], opt['default']);
        $("#endtime").mobiscroll(optDateTime)
    });

    $(".createbth").click(function () {
        if($("#meetname").val()==""){
            layer.open({
                content: '<div class="alertDiv"><div class="alert-icon iconfont icon-false"></div><div class="hint">信息填写不完整</div></div>'
                ,skin: 'msg'
                ,shade: 0
                ,time: 2 //2秒后自动关闭
            });
        }else{
            $.ajax({
                url:"/wechat/scanMeeting/reserveMeeting",
                type:"post",
                data:{tenantId:$('#tenantId').val(),meetingRoomId:$('#meetingRoomId').val(),unionId:$('#unionid').val(),title:$('#meetname').val(),start:$('#kaitime').val(),end:$('#endtime').val()},
                success:function (e) {
                    if(e.ifSuc==1){
                        layer.open({
                            content: '<div class="alertDiv"><div class="alert-icon iconfont icon-true"></div><div class="hint">预订成功</div></div>'
                            ,skin: 'msg'
                            ,shade: 0
                            ,time: 2 //2秒后自动关闭
                            ,end:function () {
                                location.href=e.data.url;
                            }
                        });
                    }else if(e.ifSuc==10){
                        layer.open({
                            content: '<div class="alertDiv1"><div class="alert-icon iconfont icon-false"></div><div class="hint">手机号不在当前企业下</div></div>'
                            ,skin: 'msg'
                            ,shade: 0
                            ,time: 2 //2秒后自动关闭
                        });
                    }else{
                        layer.open({
                            content: '<div class="alertDiv1"><div class="alert-icon iconfont icon-false"></div><div class="hint">'+e.msg+'</div></div>'
                            ,skin: 'msg'
                            ,shade: 0
                            ,time: 2 //2秒后自动关闭
                        });
                    }
                }
            });
        }
    })

</script>
</html>