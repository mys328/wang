'use strict';

$(document).ready(function () {
    // /*删除标签背景*/
    $(".nav-item").removeClass("flagBackground");
    /*初始化标签背景*/
    $(".nav-item:eq(1)").addClass("flagBackground");
    var userInfo = JSON.parse(localStorage.getItem('userinfo'));
    if (userInfo == null) {
        userInfo = {
            userId: '123',
            token: '123'
        };
    }
    var pageType = $("#pageType").val(); //0-日视图 1-列表
    var url = window.location.toString();
    var arr = url.split('?');
    if(arr.length==2){
       var arr1 = arr[1].split('&');
       for (var i = 0; i<arr1.length; i++){
           if(arr1[i].indexOf('type')>=0){
               var arr2 = arr1[i].split('=');
               pageType = arr2[1];
               break;
           }
       }
    }
    var events = [];
    var reserveCycle = 0; //预定周期
    selectMeetingRoomReserve();
    $('#dates').datepicker({
        language: 'zh-CN',
        format: 'yyyy-mm-dd',
        todayHighlight: true,
        templates: {
            leftArrow: '<i class="icon icon-calendar-left"></i>',
            rightArrow: '<i class="icon icon-calendar-right"></i>'
        },
        beforeShowDay: function (date) {
            if (events.indexOf(moment(date).format('YYYY-MM-DD')) >= 0) {
                return {classes: 'hasevent'};
            } else {
                return {classes: ''};
            }
        }
    }).on('changeDate', function (ev) {
        $('.popover').popover('hide');
        $('#agenda').fullCalendar('gotoDate', ev.date);
        $('#calendar input').val(moment(ev.date).format('YYYY-MM-DD'));
        if (popoverElement != undefined) $('.popover').popover('hide');
        if(moment(ev.date).format('YYYY-MM-DD') == moment().format('YYYY-MM-DD')){
            fetchs.post('/meeting/selectmeetingdayview', {start: moment(ev.date).format('x')}, function (res) {
               events = res.data;
               $('#dates').datepicker('update', moment(ev.date).format('YYYY-MM-DD'));
            });
        }
    }).on('changeMonth', function(ev) {
        $('.popover').popover('hide');
        $('#agenda').fullCalendar('gotoDate', ev.date);
        $('#calendar input').val(moment(ev.date).format('YYYY-MM-DD'));

        fetchs.post('/meeting/selectmeetingdayview', {start: moment(ev.date).format('x')}, function (res) {
            events = res.data;
            $('#dates').datepicker('update', moment(ev.date).format('YYYY-MM-DD'));
            // $('#dates').datepicker('update', ' ');
        });
    });

    $('#calendar input').val(moment().format('YYYY-MM-DD'));
    $('#calendar input').on('keypress', function (event) {
        var date = $(this).val().trim();
        if (event.keyCode == '13' && date != '') {
            if (moment(date).isValid()) {
                $('#dates').datepicker('setDate', moment(date).format('YYYY-MM-DD'));
            } else {
                console.log('请输入合法有效的时间');
            }
        }
    });
    fetchs.post('/meeting/selectmeetingdayview', {start: moment().format('x')}, function (res) {
        events = res.data;
        $('#dates').datepicker('update', ' ');
    });
    $('.btn-today').on('click', function () {
        $('#calendar input').val(moment().format('YYYY-MM-DD'));

        $('#dates').datepicker('setDate', moment().format('YYYY-MM-DD'));
    });

    // 会议室预订设置信息
    function selectMeetingRoomReserve() {
        fetchs.post('/meetingRoom/selectMeetingRoomReserve', {}, function (res) {
            if (res.ifSuc == 1) {
                reserveCycle = res.data.reserveCycle;
                $.fn.detail.defaults = {
                    min: res.data.meetingMinimum,
                    max: res.data.meetingMaximum,
                    cycle: res.data.reserveCycle
                }
                var start = moment(res.data.reserveTimeStart).format('H:mm');
                var end = moment(res.data.reserveTimeEnd).format('H:mm');
                $.fn.timerange.defaults.start = start;
                $.fn.timerange.defaults.end = end;
                $.fn.timerange.defaults.cycle = res.data.reserveCycle;
                agendaOptons.selectConstraint = {
                    start: start,
                    end: end
                }
                $('#agenda').fullCalendar(agendaOptons)
            }
        })
    }

    var agendaOptons = {
        header: {
            left: 'title',
            center: '',
            right: '' // agendaDay,agendaWeek,month
        },
        locale: 'zh-cn',
        timezone: 'local',
        titleFormat: 'YYYY年M月D日 周dd',
        columnFormat: ' ',
        allDaySlot: false,
        defaultView: 'agendaDay',
        scrollTime: '12:00:00',
        snapDuration: '00:15:00',
        nowIndicator: true,
        slotLabelFormat: ['H:mm'],
        selectable: true,
        selectHelper: true,
        selectOverlap: false,
        slotEventOverlap: false,
        height: $('body').innerHeight() - 120,
        eventClick: function(events, event) {
            // $('.popover').not($(event.currentTarget)).popover('hide')
            popoverElement = $(event.currentTarget);
        },
        selectAllow: function(select) {
            if (select.start.isBefore(moment().add(8, 'hours'))) {
                return false;
            } else if (reserveCycle != 0 && select.start.isAfter(moment().add(reserveCycle,'days').format('YYYY-MM-DD'),'day')){
                return false;
            } else {
                return true; // or false
            }
        },
        selectConstraint: {
            start: '7:00',
            end: '21:00'
        },
        select: function(start, end, event) {
            if (navigator.userAgent.indexOf('Firefox') >= 0) {
                $('.popover').popover('hide');
            }
            var obj = {
                start: start.format('x'),
                end: end.format('x'),
            };
            var newBtn = $('<div class="btn-new">新建会议</div>');
            newBtn.data(obj);
            newBtn.css({
                top: $(event.target).parents('.fc-event').position().top + $(event.target).parents('.fc-event').outerHeight() - 52,
                left: 0
            });
            $(event.target).parents('.fc-event-container').append(newBtn);
        },
        eventLimit: true,
        events: function(start, end, timezone, callback) {
            fetchs.post('/meeting/selectsettimemeeting', {start: start.format('x'),all:0}, function (res) {
                var events = [];
                if (res.ifSuc == 1) {
                    events = res.data;
                    if(res.msg=="1"){//判断当前用户是否有审核会议权限(勿删)
                      //显示
                      $("#verify").show();
                    }else {
                       $("#verify").hide();
                    }
                    getAllEventsTotal(events);
                } else {
                    notify('danger', res.msg)
                }
                callback(events);
            })
        },
        eventRender: function(event, element) {
            var start = moment(event.start).format('H:mm');
            var end = moment(event.end).format('H:mm');
            event.before = moment().isBefore(event.start);
            event.after = moment().isBefore(event.end);
            var tpl = '\n        <div class="ellipsis fc-content" data-title="' + (event.meetingSubject != undefined ? event.meetingSubject : '') + '">\n          <span class="times">' + start + '~' + end + '</span>\n          <span>' + (event.meetingSubject != undefined ? event.meetingSubject : '') + '</span>\n        </div>\n        <div class="fc-bg"></div>';
            $(element).html(tpl);
            if (event.state == 0) {
                $(element).addClass('partake')
            }
            var tpl = '\n        <div ng-class="state == 0 ? \'meeting-info partake\':\'meeting-info\'">\n          <h6 data-id="{{meetingId}}">{{meetingSubject}}</h6>\n          <p>\u65F6\u95F4\uFF1A{{start|date:\'H:mm\'}}~{{end|date:\'H:mm\'}}</p>\n          <p>\u5730\u70B9\uFF1A{{location}}</p>\n          <div class="user">\n            <img class="user-url" ng-if="photo" src="{{smallPicture}}"><span ng-if="!photo || photo==\'\'" class="nophoto">{{name|name}}</span>\n            <span class="name">{{name}}</span>\n            <span ng-if="isReservationPerson == 0">{{phone}}</span>\n          </div>\n          <div class="handles" ng-if="isReservationPerson!=0 && before">\n            <button ng-if="before" class="btn-meeting-edit" data-id="{{meetingId}}">\u4FEE\u6539\u4F1A\u8BAE</button>\n            <button ng-if="after" data-toggle="modal" data-target="#cancelMeeting" data-id="{{meetingId}}" data-title="{{meetingSubject}}">\u53D6\u6D88\u4F1A\u8BAE</button>\n          </div>\n        </div>';
            element.popover({
                placement: 'top',
                html: true,
                content: soda(tpl, event),
                trigger: 'click'
            });

        },
        viewRender: function(view, element) {
            var start = view.options.selectConstraint.start.split(':')[0];
            var end = view.options.selectConstraint.end.split(':')[0];
            var date = $('#agenda').fullCalendar('getDate');
            if (date.isBefore(moment().format('YYYY-MM-DD'),'day') || (reserveCycle != 0 && date.isAfter(moment().add(reserveCycle-1,'days').format('YYYY-MM-DD'),'day'))) {
                $('.fc-slats tr').addClass('fc-busy')
            }else if (date.isAfter(moment().format('YYYY-MM-DD'),'day')) {
                $('.fc-slats tr').each(function() {
                    if ($(this).index() < parseInt(start) * 2 || $(this).index() > parseInt(end) * 2 - 1) {
                        $(this).addClass('fc-busy')
                    }
                })
            }else{
                if (start < parseInt(moment().format('H'))) {
                    start = parseInt(moment().format('H'));
                }
                $('.fc-slats tr').each(function() {
                    if ($(this).index() < parseInt(start) * 2 || $(this).index() > parseInt(end) * 2 - 1) {
                        $(this).addClass('fc-busy')
                    }
                })
            }
            refreshClock()
        }
    }


    function getAllEventsTotal(evnts) {
        $('span.statistics').remove();
        var num = evnts.length;
        var hours = 0;
        var minutes = 0;
        $.map(evnts, function (item) {
            var duration = moment.duration(moment(item.end).diff(moment(item.start)));
            minutes += duration.asMinutes();
        })
        hours = Math.floor(minutes / 60);
        minutes = minutes % 60;
        $('.fc-header-toolbar .fc-left').append('<span class="statistics">（' + num + '个会议，用时' + hours + '小时' + minutes + '分钟）</span>')
    }

    function refreshClock() {
        $('.fc-now-indicator-arrow').text(moment().format('H:mm'))
        setTimeout(refreshClock, 1000)
    }
    
    var popoverElement;
    $('body').on('click',function(e) {
        if (popoverElement && $(e.target).parents('a.fc-event').length === 0) {
            $('a.fc-event').popover('hide')
        }else{
            $('a.fc-event').not(popoverElement).popover('hide')
        }
    });
    var userinfos = null
    fetchs.get('/getSysUserInfoByUserId?token=' + _userInfo.token, function (res) {
        userinfos = res.data;
    });
    $('body').on('click', '.btn-new', function () {
        var data = $(this).data();
        data.type = 1;
        data.organizer = {
            id: userinfos.id,
            name: userinfos.userName
        }
        data.attendees = [{
            id: userinfos.id,
            name: userinfos.userName,
            dep: 0
        }]
        data.department = {
            id: userinfos.orgId,
            name: userinfos.orgName
        }
        $(this).detail('show', data);
    })
    //我的列表
    $('#btn-list').on('click', function () {
        $('#myMeeting').show();
        $('#mine').hide();
        $('.input-search').show();
        pageType = 1;
        $('#pageType').val(pageType);
        $(this).removeClass('unselected-btn');
        $(this).addClass('selected-btn');
        $('.btn-calendar').removeClass('selected-btn');
        $('.btn-calendar').addClass('unselected-btn');
        getMeeting(0);
    });
    $('.btn-calendar').on('click', function () {
        $('#myMeeting').hide();
        $('#mine').show();
        $('.input-search').hide();
        pageType = 0;
        $('#pageType').val(pageType);
        $(this).removeClass('unselected-btn');
        $(this).addClass('selected-btn');
        $('#btn-list').removeClass('selected-btn');
        $('#btn-list').addClass('unselected-btn');
    });

    soda.prefix('ng-');



    soda.filter('statu', function (statu) {
        if (statu == null) statu = '';
        switch (statu.toString()) {
            case '2': {
                return '已预约';
                break;
            }
            case '4': {
                return '已结束';
                break;
            }
            case '5': {
                return '已取消';
                break;
            }
            case '1': {
                return '待审批';
                break;
            }
            case '0':{
                return '未通过';
            }
            default: {
                return '';
                break;
            }
        }
    });

    soda.filter('name', function (name) {
        if(name==null) return '';
        if (name.length == 0) {
            return '';
        } else if (name.length <= 2) {
            return name;
        }
        return name.substring(name.length - 2, name.length);
    });

    var currentTab = 0;
    var sevenDays = 0; //最近七天的
    var future = 0; //未来的会议
    var last = 0; //过去的会议
    var myoper = 0; //我组织的
    var attend = 0; //我参加的
    var lastSelected = 0; //上次选中的时间 默认最近7天 1：未来的会议 2：过去的会议
    var lastType = ''; //上次选中的分类
    var isOrg = 0; //是否是我组织我参加的
    var myMeetings = new Array(); //我的会议
    var page = {
        pageNum: 1, //页数
        pageSize: 15 //每页数据数
    };
    var history = 0; //是否有历史数组 0-没有，1-有
    var key = '';
    var firstload = true;
    $('#pageType').val(pageType);
    //监听我审核的会议点击事件
    // $('#verify').on('click',function () {
    //     var type = $('#pageType').val();
    //     window.location.href="/search/skipMeetingScreeningVerify?token="+  _userInfo.token + "&type=" + pageType;
    // })
    if(pageType == 1){
        //默认显示列表
        $('#myMeeting').show();
        $('#mine').hide();
        $('.input-search').show();
        $('#btn-list').removeClass('unselected-btn');
        $('#btn-list').addClass('selected-btn');
        $('.btn-calendar').removeClass('selected-btn');
        $('.btn-calendar').addClass('unselected-btn');
        getMeeting(0);
    }else {
        //默认显示日视图
        $('#myMeeting').hide();
        $('.input-search').hide();
        $('#mine').show();
        $('.btn-calendar').removeClass('unselected-btn');
        $('.btn-calendar').addClass('selected-btn');
        $('#btn-list').removeClass('selected-btn');
        $('#btn-list').addClass('unselected-btn');
        selectMeetingRoomReserve();
    }
    // $('#mine-loading').hide();

    //鼠标悬停效果
    $('.mouse').mousemove(function (event) {
        var id = $(this).data('id');
        if (id < 3) {
            //时间分类
            if (lastSelected == id) {//选中的和当前悬停的是同一个,没有悬停效果

            } else {
                $(this).addClass('hover');
                $('#' + id).show();
                $('#' + id).css('color', '#cccccc');
            }
        } else {
            //我审核的会议 或者组织参与分类
            if (id == 3 | id == 4) {
                if (lastType == id) {//选中的和当前悬停的是同一个,没有悬停效果

                } else {
                    $(this).addClass('hover');
                    $('#' + id).show();
                    $('#' + id).css('color', '#cccccc');
                }
            }
        }
    });
    //鼠标移走效果
    $('.mouse').mouseleave(function (event) {
        var id = $(this).data('id');
        if (id < 3) {
            //时间分类
            if (lastSelected == id) {//选中的和当前悬停的是同一个,没有悬停效果

            } else {
                $(this).removeClass('hover');
                $('#' + id).hide();
                $('#' + id).css('color', '#27a1f2');
            }
        } else {
            //我审核的会议 或者组织参与分类
            if (id == 3 | id == 4) {
                if (lastType == id) {//选中的和当前悬停的是同一个,没有悬停效果

                } else {
                    $(this).removeClass('hover');
                    $('#' + id).hide();
                    $('#' + id).css('color', '#27a1f2');
                }
            }
        }
    });
    //鼠标点击效果
    $('.mouse').on('click', function (event) {
        //重置页码
        page.pageNum = 1;
        var id = $(this).data('id');
        $(this).removeClass('hover');
        $('#' + id).css('color', '#27a1f2');
        $('#' + id).hide();
        if (id < 3) {
            //时间分类
            if (lastSelected.length != 0) {
                $('#' + lastSelected).hide();
            }
            $('#' + id).show();
            lastSelected = id;
        } else {
            //我审核的会议 或者组织参与分类
            if (id == 3 | id == 4) {
                if (lastType.length != 0 && lastType != id) {
                    //如果上次选中的和这次选中的不是同一个类型，上次的取消选中
                    $('#' + lastType).hide();
                    $('#' + id).show();
                    lastType = id;
                } else if (lastType == id) {
                    //如果上次选中的和本次选中的相同，取消本次选中的
                    $('#' + lastType).hide();
                    lastType = '';
                    //恢复悬停效果
                    $(this).addClass('hover');
                    $('#' + id).show();
                    $('#' + id).css('color', '#cccccc');
                } else {
                    $('#' + id).show();
                    lastType = id;
                }
            }
        }
        getMeeting(0);
    });

    //会议列表显示
    //最近7天
    var sevenTpl = '  \n  <div ng-if="data.length!=0" ng-repeat="meeting in data">\n    <div class="header">\n      <span class="day">{{meeting.date}}<span class="count">{{meeting.num}}</span></span>\n    </div>\n    <ul>\n    <li ng-repeat="item in meeting.meeting" ng-class="item.isOrg == 0 ? \'started\':\'\'">\n      <div class="col-3 float-left">\n        <span class="times" ng-html="item.takeStartDate|period:item.takeEndDate"></span>\n      </div>\n      <div class="col-9 float-right" >\n        <div class="col-12 height">\n          <div class="col-7 float-left">\n            <h6 ng-class="item.state == 5 || item.state == 0 ? \' title cancel-title \' : \' title \'" data-toggle="tooltip" data-id="{{item.id}}" data-title="{{item.conferenceName}}" ng-html="item.conferenceName|keylight:key"></h6>\n            <p ng-if="item.address.length!=0"><i class="icon icon-room-address"></i><span ng-html="item.address|keylight:key"></span></p>\n          </div>\n          <div class="col-4 float-right" >\n            <span class="organizer"  ng-if="item.userName!=null">\n              <img class="user-url" ng-if="item.userNameUrl" src="{{item.userNameUrl}}"><span ng-if="!item.userNameUrl" ng-class="item.userName ? \'nophoto\':\'notext\'" ng-html="item.userName|name"></span> <span ng-html="item.userName|keylight:key"></span>             </span>\n            <span ng-if="item.state != 2 && item.state != 3 &&item.state != 0" ng-class="item.state == \'1\' ? \'green-color\':\'gray-color\'">{{item.state|statu}}</span>\n        <span ng-if="item.state == 0" ng-class="item.state == \'0\' ? \'gray-color\' : \'green-color\'">{{item.state|statu}}</span>\n      </div>          \n        </div>\n        <div>\n          <span ng-repeat="person in item.personsVos" ng-if="$index<4" class="person">{{person.userName}}</span>\n        <span ng-if="item.personsVos!=null | item.personsVos.length!=0" class="person-count">\u5171{{item.count}}\u4EBA\u53C2\u4F1A</span>\n   <span class="icon icon-question"  ng-if="item.state === \'0\'" data-toggle="notpass-reason" data-title="{{item.auditWhy}}"></span>\n    </div>\n      </div>\n    </li>\n  </ul>\n  </div>\n  <div class="nothing" ng-if="data.length==0 & history==0">\u60A8\u8FD8\u6CA1\u6709\u4F1A\u8BAE\u8BB0\u5F55\uFF0C\u8BF7\u4ECE<span class="gotobooking">会议预订</span>\u5F00\u59CB\u5427!</div>\n  <div class="nothing" ng-if="data.length==0 & history==1 & key.length==0" class="nothing" style="height:100%">\u6682\u65F6\u6CA1\u6709\u4F1A\u8BAE</div>\n  <div class="nothing" ng-if="data.length==0 & history==1 & key.length!=0" class="nothing" style="height:100%">\u6CA1\u6709\u641C\u7D22\u5230\u4FE1\u606F,\u6362\u4E2A\u6761\u4EF6\u8BD5\u8BD5\uFF1F<br>\u60A8\u53EF\u4EE5\u8F93\u5165\u4F1A\u8BAE\u540D\u79F0\u3001\u7EC4\u7EC7\u8005\u3001\u4E3B\u529E\u5355\u4F4D\u3001\u4F1A\u8BAE\u5185\u5BB9\u7B49\u90E8\u5206\u5185\u5BB9\u68C0\u7D22\u3002</div>\n  ';

    var meetinglist = '  \n  <div ng-if="data.length!=0">  \n    <ul>\n    <li ng-repeat="item in data" ng-class="item.isOrg == 0 ? \'started\':\'\'">\n      <div class="col-3 float-left">\n        <span class="times" ng-html="item.takeStartDate|period:item.takeEndDate"></span>\n      </div>\n      <div class="col-9 float-right" >\n        <div class="col-12 height">\n          <div class="col-7 float-left">\n            <h6 ng-class="item.state == 5 || item.state == 0 ? \' title cancel-title \' : \' title \'" data-toggle="tooltip"  data-id="{{item.id}}" data-title="{{item.conferenceName}}" ng-html="item.conferenceName|keylight:key"></h6>\n            <p ng-if="item.address.length!=0"><i class="icon icon-room-address"></i><span ng-html="item.address|keylight:key"></span></p>\n          </div>\n          <div class="col-4 float-right" >\n            <span class="organizer" ng-if="item.userName!=null">\n              <img class="user-url" ng-if="item.userNameUrl" src="{{item.userNameUrl}}"><span ng-if="!item.userNameUrl" ng-class="item.userName ? \'nophoto\':\'notext\'" ng-html="item.userName|name"></span> <span ng-html="item.userName|keylight:key"></span>             </span>\n    <span ng-if="item.state != 2 && item.state != 3 &&item.state != 0" ng-class="item.state == \'1\' ? \'green-color\':\'gray-color\'">{{item.state|statu}}</span>\n        <span ng-if="item.state == 0" ng-class="item.state == \'0\' ? \'gray-color\' : \'green-color\'" >{{item.state|statu}}</span>\n          </div>          \n        </div>\n        <div>\n          <span ng-repeat="person in item.personsVos" ng-if="$index<4" class="person">{{person.userName}}</span>\n        <span ng-if="item.personsVos!=null | item.personsVos.length!=0" class="person-count">\u5171{{item.count}}\u4EBA\u53C2\u4F1A</span>\n    <span class="icon icon-question"  ng-if="item.state === \'0\'" data-toggle="notpass-reason" data-title="{{item.auditWhy}}"></span>\n      </div>\n      </div>\n  </li>\n  </ul>\n  </div>\n  <div class="nothing" ng-if="data.length==0 & history==0">\u60A8\u8FD8\u6CA1\u6709\u4F1A\u8BAE\u8BB0\u5F55\uFF0C\u8BF7\u4ECE<span class="gotobooking">会议预订</span>\u5F00\u59CB\u5427!</div>\n  <div class="nothing" ng-if="data.length==0 & history==1 & key.length==0" class="nothing" style="height:100%">\u6682\u65F6\u6CA1\u6709\u4F1A\u8BAE</div>\n  <div class="nothing" ng-if="data.length==0 & history==1 & key.length!=0" class="nothing" style="height:100%">\u6CA1\u6709\u641C\u7D22\u5230\u4FE1\u606F,\u6362\u4E2A\u6761\u4EF6\u8BD5\u8BD5\uFF1F<br>\u60A8\u53EF\u4EE5\u8F93\u5165\u4F1A\u8BAE\u540D\u79F0\u3001\u7EC4\u7EC7\u8005\u3001\u4E3B\u529E\u5355\u4F4D\u3001\u4F1A\u8BAE\u5185\u5BB9\u7B49\u90E8\u5206\u5185\u5BB9\u68C0\u7D22\u3002</div>\n  ';

    //搜索框监听事件
    //监听搜索框键盘事件
    $('#search').on('keypress', function (e) {
        if (e.keyCode == 13) {
            $('.input-search').removeClass('input-heighLight')
            $(this).blur();
            page.page = 1;
            page.size = 15;
            key = $(this).val();
            if(key.length>0){
                //显示删除按钮
                $('#del-searchList').show();
            }else {
                $('#del-searchList').hide();
            }
            getMeeting(0);
            return false;
        }
        ;
    });
    $('#search').bind('focus','input',function () {
        $('.input-search').addClass('input-heighLight');
        $('#del-searchList').hide();
    })
    $('#search').bind('blur','input',function () {
        $('.input-search').removeClass('input-heighLight');
        if(key.length>0){
            //显示删除按钮
            $('#del-searchList').show();
        }else {
            $('#del-searchList').hide();
        }
    })
    $('#del-searchList').on('click',function () {
        $('#del-searchList').hide();
        $('#search').val('');
        key = '';
        getMeeting(0);
    })
    function getMeeting(type) {
        //type：0-获取新数据 1-加载更多

        if (lastType.length == 0) {
            isOrg = 0;
        } else if (lastType == 3) {
            //我组织的
            isOrg = 1;
        } else if (lastType == 4) {
            //我参加的
            isOrg = 2;
        }

        fetchs.post('/search/selectMyMeeting', {
            userId: +userInfo.userId,
            meetingType: lastSelected,
            myType: isOrg,
            currentPage: page.pageNum,
            pageSize: page.pageSize,
            searchKey: key,
            token: userInfo.token
        }, function (res) {

            if (type == 0) {
                if(res.msg=="1"){//判断当前用户是否有审核会议权限(勿删)
                    //显示
                    $("#verify").show();
                }else {
                    $("#verify").hide();
                }
                if (lastSelected == 0) {
                    if (res.data) {
                        myMeetings = res.data.conference;
                        if(key.length>0){
                            $('#searchResult').show();
                            $('#searchResult').text('共搜索到'+res.data.recent+'个会议')
                        }else {
                            $('#searchResult').hide();
                        }
                    } else {
                        myMeetings = [];
                        $('#searchResult').hide();
                    }
                    // if (myMeetings.length < page.pageSize) {
                    //     $('#mine-loading').hide();
                    // } else {
                    //     $('#mine-loading').show();
                    // }
                    if (firstload) {
                        firstload = false;
                        if (parseInt(res.data.recent)==0 && parseInt(res.data.future)==0 && parseInt(res.data.formerly)==0  && key.length == 0) {
                            history = 0;
                        } else {
                            history = 1;
                        }
                    }

                } else {
                    if (res.data) {
                        myMeetings = res.data.meeting.list;
                        if(key.length>0){
                            $('#searchResult').show();
                            $('#searchResult').text('共搜索到'+res.data.meeting.total+'个会议')
                        }else {
                            $('#searchResult').hide();
                        }
                    } else {
                        myMeetings = [];
                        $('#searchResult').hide();
                    }
                    // $('#mine-loading').hide();
                    // if (myMeetings.length < page.pageSize) {
                    //     $('#mine-loading').hide();
                    // } else {
                    //     $('#mine-loading').show();
                    // }

                }
                if (res.data) {
                    $('#sevenDays').html(res.data.recent);
                    $('#future').html(res.data.future);
                    $('#last').html(res.data.formerly);
                    $('#myoper').html(res.data.myOrgan);
                    $('#attend').html(res.data.myJoin);
                } else {
                    $('#sevenDays').html(0);
                    $('#future').html(0);
                    $('#last').html(0);
                    $('#myoper').html(0);
                    $('#attend').html(0);
                }

            } else {
                isloading = false;
                if(15*page.pageNum>myMeetings.length){
                    $('#mine-loading').hide();
                }else {
                    $('#mine-loading').show();
                }
                if (lastSelected == 0) {
                    if (res.data) {
                        for (var i = 0; i < res.data.conference.length; i++) {
                            myMeetings.push(res.data.conference[i]);
                        }
                        ;
                    }
                } else {
                    if (res.data) {
                        for (var i = 0; i < res.data.meeting.list.length; i++) {
                            myMeetings.push(res.data.meeting.list[i]);
                        }
                    }

                }
            }
            if (lastSelected == 0) {
                //如果显示最近7天
                $('.meeting-plan').html(soda(sevenTpl, {data: myMeetings, key: key, history: history}));
            } else {
                $('.meeting-plan').html(soda(meetinglist, {data: myMeetings, key: key, history: history}));
            }
        });
    }
    var isloading = false;
    $('.right').scroll(function () {
        if ($('.right').height()+$('.right')[0].scrollTop >= $('.right')[0].scrollHeight-10) {

            if (15*page.pageNum>myMeetings.length) {
                $('#mine-loading').hide();
            } else {
                if(isloading==false){
                    isloading =true;
                    page.pageNum += 1;
                    $('#mine-loading').show();
                    getMeeting(1);
                }

            }

        }
    });

    //加载更多
    $('#mine-loading').on('click', function () {
    });
    $('#review-loading').on('click', function () {
    });

    $('body').on('mouseenter', '[data-toggle="tooltip"]', function (e) {
        var text = $(this).text();
        if(text==null||text==""){
            return;
        }
        var size = parseInt($(this).css('fontSize'));
        if ($(this).width() < size * text.length) {
            $(this).css('color', '#1d98f0');
            $(this).tooltip({
                placement: function placement(tip, element) {
                    return window.scrollY + 50 < $(element).offset().top ? 'top' : 'bottom';
                },
                trigger: 'hover'
            });
            $(this).tooltip('toggle');
        }
    });

    $('body').on('mouseenter', '[data-toggle="notpass-reason"]', function (e) {
        if($(this).data('title')==null||$(this).data('title')==""){
            return;
        }
        $(this).data('title',$(this).data('title').toString());
        $(this).css('color', '#1d98f0');
        $(this).tooltip({
            placement: function placement(tip, element) {
                return 'left';
            },
            trigger: 'hover'
        });
        $(this).tooltip('toggle');
    });

    $('body').on('mouseleave', '[data-toggle="notpass-reason"]', function (e) {
        $(this).css('color', '#7e868c');
    });


    $('body').on('mouseleave', '[data-toggle="tooltip"]', function (e) {
        $(this).css('color', '#333');
    });

    $('body').on('click', '.gotobooking', function () {
        console.log('去预定会议');
        var start = new Date(Math.ceil(new Date().getTime() / (30 * 60 * 1000)) * 30 * 60 * 1000).getTime();
        var end = start + 30 * 60 * 1000;
        var data = {};
        data.type = 1;
        data.start =start;
        data.end = end;
        data.organizer = {
            id: userinfos.id,
            name: userinfos.userName
        }
        data.attendees = [{
            id: userinfos.id,
            name: userinfos.userName,
            dep: 0
        }]
        data.department = {
            id: userinfos.orgId,
            name: userinfos.orgName
        }
        $(this).detail('show', data)
        // window.location.href = "/gotomeetingreservepage?token=" + _userInfo.token;
    });
    // 点击新建会议按钮外部区域,此按钮消失
    $('body').on('mousedown', function(e){
        if(!$(e.target).hasClass('btn-new')){
            $('.btn-new').remove();
        }
    });
    // $("#gotoMeetingScreeningVerify").on('click',function () {
    //     fetchs.get('/search/skipMeetingScreeningVerify?token='+_userInfo.token,function () {
    //
    //     })
    // })


});