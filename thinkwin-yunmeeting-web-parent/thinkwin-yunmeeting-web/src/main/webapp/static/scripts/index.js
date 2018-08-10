'use strict';
function gotoMeetingScreeningVerify() {
    window.location.href = "/search/skipMeetingScreeningVerify?token=" + _userInfo.token+ "&type=" + 3;
}
function gotoMeetingList() {
    window.location.href="/search/skipMeetingScreening?token="+  _userInfo.token + "&type=" + 1;
}
$(function () {
    /*删除标签背景*/
    $(".nav-item").removeClass("flagBackground");
    /*初始化标签背景*/
    $(".nav-item:eq(0)").addClass("flagBackground");
    var planTpl = '\n  <ul ng-repeat="item in data.data.recentMeeting">\n    <li  ng-class="item.organizer == 0 ? \'org\':\'\'">\n      <span class="times" ng-html="item.start|period:item.end"></span>\n      <span ng-class="item.state == 1 ? \'statu started\':\'statu\'">{{item.state == 1 ? \'\u8FDB\u884C\u4E2D\':\'\u672A\u5F00\u59CB\'}}</span>\n      <h6 class="" data-id="{{item.conferenceId}}" data-title="{{item.title}}">{{item.title}}  <p><i class="icon icon-xq-site"></i>{{item.location}}</p></h6>\n     \n    </li>\n  </ul>\n  <div ng-if="data.data.recentMeeting.length<1 && data.msg!=\'近期没有会议安排\'" class="nothing">\u60A8\u8FD8\u6CA1\u6709\u4F1A\u8BAE\u8BB0\u5F55\uFF0C\u8BF7\u4ECE<a href="javascript:void(0);" onclick="gotoMeetingReserve()">\u4F1A\u8BAE\u9884\u8BA2</a>\u5F00\u59CB\u5427\uFF01</div><div ng-if="data.msg==\'近期没有会议安排\'" class="nothing">{{data.msg}}</div>';
    function isNull(value) {
        return value != null ? value : 0;
    }

    var index = function index() {
        this.init();
    }
    index.prototype = {
        init:function init() {
        },
        reload:function () {
            fetchs.post('/dynamic/selectrecentmeetingdynamic', {}, function (res) {
                res.recent = true;
                $('.meeting-dynamics-body').html(soda(dynamicsTpl, res));
            });
        }
    }
    var userInfo = JSON.parse(localStorage.getItem('userinfo'));
    if (userInfo == null) {
        userInfo = {
            userId: '123',
            token: '123'
        };
    };
    var reserveCycle = 0; //预定周期
    // 会议室预定设置信息
    fetchs.post('/meetingRoom/selectMeetingRoomReserve', {}, function (res) {
        if (res.ifSuc == 1) {
            reserveCycle = res.data.reserveCycle;
            $('#datepicker').datepicker('setDate', moment().format('YYYY-MM-DD'));
            $.fn.detail.defaults = {
                min: res.data.meetingMinimum,
                max: res.data.meetingMaximum,
                cycle: res.data.reserveCycle
            }
            var start = moment(res.data.reserveTimeStart).format('H:mm')
            var end = moment(res.data.reserveTimeEnd).format('H:mm')
            $('.freetime').timerange('setopts', {
                start: start,
                end: end
            })
            timelineOptons.selectConstraint = {
                start: start,
                end: end
            }
            timelineOptons.minTime = moment(res.data.reserveTimeStart).subtract(1,'h').format('H:mm');
            timelineOptons.maxTime = moment(res.data.reserveTimeEnd).add(1,'h').format('H:mm');
            if (timelineOptons.maxTime == '0:00'){
                timelineOptons.maxTime = '24:00';
            }
            $.fn.timerange.defaults.start = start;
            $.fn.timerange.defaults.end = end;
            $.fn.timerange.defaults.cycle = res.data.reserveCycle;
        }
    })
    fetchs.get('/getUserTenantInfo?token=' + _userInfo.token + '&userId=' + _userInfo.userId, function (res) {
        //获取当前年月
        var date = new Date();
        var year = date.getFullYear() + '年';
        var month = date.getMonth() + 1;
        $('#personal').text(month + '月个人会议统计');
        if (res.ifSuc == 1) {
            if (res.data) {
                if (res.data.tenantName != null) {

                    $('#company').text(year + month + '月，' + res.data.tenantName + '企业会议统计');
                } else {
                    $('#company').text('');
                }
            } else {
                $('#company').text('');
            }
        } else {
            $('#company').text('');
        }
    });

    var meetingData = {};
    fetchs.post('/home/meetingstatistics?userId=', {userId: userInfo.userId, token: userInfo.token}, function (res) {

        if (res.ifSuc == 1) { // meetingRoomUtilization// 会议室利用率
            // personMeetingProportion //个人会议占比
            // meetingTotalNum //会议总数
            // meetingTotalTime //会议总时长
            // personMeetingTotalTime //个人会议总时长
            // meetingTotalPeopleNum //会议总人数（总参会人次）
            // reserveMeetingNum //预定会议总数
            // participateMeetingNum //参与会议总数
            meetingData = res.data;
            //会议室利用率
            $('#meetingRoomUtilization').html(res.data.meetingRoomUtilization == null ? '0%' : isNull(res.data.meetingRoomUtilization));
            //个人会议占比
            $('#personMeetingProportion').html(res.data.personMeetingProportion == null ? '0%' : isNull(res.data.personMeetingProportion));
            //会议总数
            $('#meetingTotalNum').html(isNull(res.data.meetingTotalNum));
            //会议总时长
            $('#meetingTotalTime').html(isNull(res.data.meetingTotalTime));
            //个人会议总时长
            $('#personMeetingTotalTime').html(res.data.personMeetingTotalTime == null ? '0分钟' : isNull(res.data.personMeetingTotalTime));
            //会议总人数
            $('#meetingTotalPeopleNum').html(isNull(res.data.meetingTotalPeopleNum));
            //预定会议总数
            $('#reserveMeetingNum').html(isNull(res.data.reserveMeetingNum));
            //参与会议总数
            $('#ParticipateMeetingNum').html(isNull(res.data.participateMeetingNum));
            var roomempty = (1 - (res.data.meetingUserOf > 1 ? 1 : res.data.meetingUserOf)) * 100;
            $('#meetingRoomEmptiest').text(parseInt(roomempty) + '%');
            var persontime = (res.data.meetingPersonaLength > 1 ? 1 : res.data.meetingPersonaLength) * 100;
            $('#personMeetingTimeProportion').text(parseInt(persontime) + '%');
            pie($('#pie1'), (roomempty),1);
            pie($('#pie2'), (persontime),2);
        }else {
            notify('danger',"数据统计查询失败");
        }
     }

    );
    //近期会议
    fetchs.post('/meeting/selectrecentmeeting', {}, function (res) {
        if (res.ifSuc == 1) {
            if (res.data.isAuthstr == 1 && res.data.authstrNum != 0) {
                $('.meeting-plan a.more').before('<a href="javascript:void(0);" onclick="gotoMeetingScreeningVerify();">您有' + res.data.authstrNum + '条需审核会议</a>')
            }
            $('.meeting-plan').append(soda(planTpl, {data: res}));
            if (res.msg!='近期没有会议安排' && !res.data.recentMeeting) {
                $('.meeting-plan .more').hide();
            }
        } else {
            notify('danger',"近期会议查询失败");
        }
    });
    // 自定义sodajs前缀
    soda.prefix('ng-');
    //名字灰色显示
    soda.filter('sysname', function (string, word) {
        if (string == null || word == null) return string;
        if (string.length == 0 || word.length == 0) return string;
        var reg = word.replace(/([\.|\*|\$|\*|\(|\)|\+])/g, "\\" + "$1");
        return word == '' ? string : string.toString().replace(new RegExp(reg), '<font color="#7e838c">' + word + '</font>');
    });
    var dynamicsTpl = '\n  <div class="notices" ng-repeat="item in data">\n    <h6>\n      <a class="title" href="#" data-id="{{item.id}}" title="{{item.title}}">\n        {{item.title}}\n      </a>\n    </h6>\n    <ul>\n      <li ng-repeat="item in item.dynamics">\n        <img class="user-url nophoto" ng-if="item.smallPicture && item.sys == 0" src="{{item.smallPicture}}"><span ng-if="item.sys == 0 && !item.smallPicture" class="nophoto small {{item.name|charcode}}"><i>{{item.name|slice:-2}}</i></span>\n        <span ng-if="item.sys == 1" class="icon icon-systeminfo"></span>\n        <span class="timeago">{{item.timeago|timeago}}</span>\n        <p>\n          <span class="name" ng-if="item.sys == 0">{{item.name}}</span>\n          <span class="name" ng-if="item.sys == 0">\uFF1A</span>\n  <span  ng-if="item.sys == 0">{{item.message}}</span>        <span  ng-if="item.sys != 0" ng-html="item.message|sysname:item.name"></span></p>\n      </li>\n    </ul>\n  </div>\n  <div ng-if="data.length<1" class="nothing">近期没有会议动态</div>';

    //近期会议动态查询
    fetchs.post('/dynamic/selectrecentmeetingdynamic', {}, function (res) {
        if(res.ifSuc==1){
            res.recent = true;
            if (res.data.length<1) {
                $('.meeting-dynamics .more').hide();
            }
            $('.meeting-dynamics-body').html(soda(dynamicsTpl, res));
        }else {
            notify("danger","近期会议动态查询失败");
        }
    });

    function pie(ele, ratio,tag) {
        var $circle = ele.find('circle');
        if(tag==1){
            var i = 326.72 * (100-ratio) / 100;
            $circle.eq(0).attr('stroke-dashoffset',  i);
            $circle.eq(1).attr('stroke-dashoffset', 326.72 + i);
        }else {
            var i = 326.72 * ratio / 100;
            $circle.eq(0).attr('stroke-dashoffset', 326.72 + i);
            $circle.eq(1).attr('stroke-dashoffset', i);
        }
    }

    $('.pie circle').on('click', function () {
        var ratio = parseInt($(this).attr('stroke-dashoffset'));
        var clas = $(this).attr('class');
        var category = {idle: '空闲', occupied: '使用', personal: '个人', company: '企业'};
        if (ratio > 326.72) {
            ratio = Math.round((ratio - 326.72) / 326.72 * 100);
        } else {
            ratio = Math.round((326.72 - ratio) / 326.72 * 100);
        }
        $(this).parents('.pie').find('circle').removeClass('active');
        var text = category[clas];
        if (text == undefined) {
            var str = $(this).parents('.pie').find('span').text();
            console.log(str);
            if (str.indexOf('空闲') >= 0) {
                text = '使用';
                var value = $(this).parents('.pie').find('i').text();
                value = value.split("%");
                ratio = 100 - parseInt(value);
            } else if (str.indexOf('使用') >= 0) {
                text = '空闲';
                var value = $(this).parents('.pie').find('i').text();
                value = value.split("%");
                ratio = 100 - parseInt(value);
            } else if (str.indexOf('个人') >= 0) {
                text = '企业';
                var value = $(this).parents('.pie').find('i').text();
                value = value.split("%");
                ratio = 100 - parseInt(value);
            } else if (str.indexOf('企业') >= 0) {
                text = '个人';
                var value = $(this).parents('.pie').find('i').text();
                value = value.split("%");
                ratio = 100 - parseInt(value);
            }
        }
        $(this).parents('.pie').find('span').html(text + '<i>' + ratio + '%</i>');
        $(this).addClass('active');
        $(this).parent('svg')[0].appendChild($(this).prev('circle')[0]);
    });
    $('.btn-show-profile').on('click', function (event) {
            event.preventDefault();
            $('.home').hide();
            fetchs.get('/setting/querypersonalinfo', function (res) {
                $('.profile').show();
            });
        }
        // 关闭个人资料
    );
    $('.profile span.icon-close').on('click', function (event) {
        $('.profile').hide();
        $('.home').show();
    });


    $('.meeting-dynamics .more').click(function () {
        location.href = "/dynamic/viewmoredynamicpage?token=" + userInfo.token;
    });
    $('#authorizeForm input').on('keypress', function () {
        $(this).parents('form').find('p.error-msg').html('');
    })
    $('#authorizeForm').on('submit', function (event) {
            event.preventDefault();
            var self = $(this);
            var data = eval('(' + '{' + $(this).serialize().replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
            if (data.password == '') {
                $(this).find('p.error-msg').html('<i class="icon icon-error"></i>请输入密码');
                return false;
            }
            fetchs.get('/administratorLoginConsole?password=' + data.password + '&token=' + _userInfo.token, function (res) {
                if (res.ifSuc == 1) {
                    window.location.href = "/gotoConsolePage?token=" + _userInfo.token;
                } else {
                    self.find('p.error-msg').html('<i class="icon icon-error"></i>' + res.msg);
                }
            });
        }
    );

    $('body').on('mouseenter', '[data-toggle="tooltip"]', function (e) {
        var text = $(this).text();
        var size = parseInt($(this).css('fontSize'));
        if ($(this).width() < size * text.length) {
            $(this).tooltip({
                placement: function placement(tip, element) {
                    return window.scrollY + 50 < $(element).offset().top ? 'top' : 'bottom';
                },
                trigger: 'hover'
            });
            $(this).tooltip('toggle');
        }
    });
    var company_logo = $('#companyLogo').val();
    if(company_logo==''||company_logo==null){
        company_logo = 'static/images/yunmeeting.png';
    };
    var company_name = $('#companyName').val();
    if(company_name==''||company_name==null){
        company_name = '企云会';
    };
    $('#authorizeForm img').attr('src',company_logo);
    $('#authorizeForm h6').text(company_name);
    //标示动画是否执行
    // $(".container-fluid").scroll(function(){
    //     var targetTop = $(this).scrollTop();
    //
    //     if (targetTop >= 190){
    //         $(".meeting-dynamics").addClass("dynamics-fixed");
    //         $(".meeting-dynamics").css("top",(targetTop+180)+"px");
    //     }else {
    //         $(".meeting-dynamics").removeClass("dynamics-fixed");
    //         $(".meeting-dynamics").css("top","0");
    //     }
    // });
});