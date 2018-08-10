$(function () {
    /*删除标签背景*/
    $(".nav-item").removeClass("flagBackground");
    /*初始化标签背景*/
    $(".nav-item:eq(2)").addClass("flagBackground");
    var roomFilters = {
        staTime: '',
        endTime: '',
        roomId: '',
        currentPage: 1,
    }
    var currentareaId = '';
    // 会议室时间
    $('.freetime').timerange({
        container: '.main',
        dateweek: false,
        timestamp: false,
        datepicker: false
    }).on('hidden.bs.timerange', function (event) {
        getRooms();
    })


    // 左侧日历
    var reserveCycle = 0; //预定周期
    $('#datepicker').datepicker({
        language: 'zh-CN',
        format: 'yyyy-mm-dd',
        todayHighlight: true,
        minViewMode: 0,
        maxViewMode: 0,
        templates: {
            leftArrow: '<i class="icon icon-calendar-left"></i>',
            rightArrow: '<i class="icon icon-calendar-right"></i>'
        },
        beforeShowDay: function (date) {
            if (moment(date).isBefore(moment().subtract(1, 'days'))) {
                return {classes: 'disabled'};
            } else if (reserveCycle != 0 && moment(date).isAfter(moment().add(reserveCycle - 1, 'days'))) {
                return {classes: 'disabled'};
            }
        }
    }).on('changeDate', function (ev) {
        $('.popover').popover('hide');
        $('.freetime').timerange('update',moment(ev.date).format('YYYY-MM-DD'));
        $('.freetime').timerange('clear');
        $('#timeline').fullCalendar('gotoDate', ev.date);
        $('#calendar input').val(moment(ev.date).format('YYYY-MM-DD'));
    }).on('changeMonth', function (ev) {
        $('.popover').popover('hide');
        $('#timeline').fullCalendar('gotoDate', ev.date);
        $('#calendar input').val(moment(ev.date).format('YYYY-MM-DD'));
        $('#datepicker').datepicker('update', moment(ev.date).format('YYYY-MM-DD'));
    });
    $('#datepicker').datepicker('setDate', moment().format('YYYY-MM-DD'));
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
    // 会议室区域
    fetchs.post('/meetingRoom/selectAreaAndDevice', {}, function (res) {
        $('.area-type').html('<a class="dropdown-item area-item" data-id="">全部区域</a>');
        $.map(res.data.roomArea, function (item) {
            $('.area-type').append('<a class="dropdown-item area-item" data-id="' + item.id + '">' + item.name + '</a>')
        })
        // $('#area')
    })
    $('body').on('click','.area-item',function (e) {
        var id = $(this).data('id');
        if(id.length==0){
            $('#area').addClass('placeholder-color');
        }else {
            $('#area').removeClass('placeholder-color');
        }
        $('#area').text($(this).text());
        currentareaId = id;
        getRooms();
    })
    // 日历输入框
    $('#calendar input').val(moment().format('YYYY-MM-DD'));
    $('#calendar input').on('keypress', function (event) {
        var date = $(this).val().trim();
        if (event.keyCode == '13' && date != '') {
            if (moment(date).isValid()) {
                $('#datepicker').datepicker('setDate', moment(date).format('YYYY-MM-DD'));
            } else {
                notify('danger', '请输入合法有效的时间');
            }
        }
    })

    // 今日按钮
    $('.btn-today').on('click', function () {
        $('#calendar input').val(moment().format('YYYY-MM-DD'));
        $('#datepicker').datepicker('setDate', moment().format('YYYY-MM-DD'));
    })
    // 时间线视图
    var slatsWidth = 70;
    var resourceObjects = [];
    var selectId = null;
    var timelineOptons = {
        defaultDate: moment(),
        header: false,
        locale: 'zh-cn',
        timezone: 'local',
        width: 1680,
        resourceAreaWidth: 276,
        height: $('body').innerHeight() - $('.filter').outerHeight() - 98,
        defaultView: 'timelineDay',
        selectable: true,
        selectOverlap: function (event) {
            return event.state == '2';
        },
        slotEventOverlap: false,
        scrollTime: moment().subtract(1, 'hours').format('HH:mm:ss'),
        snapDuration: '00:15:00',
        slotLabelFormat: ['H:mm'],
        minTime: '6:00',
        maxTime: '24:00',
        minTime: '6:00',
        windowResize: function () {
            slatsWidth = $('.fc-slats td').width()*2;
            if (navigator.userAgent.indexOf("Firefox")>0) {
                slatsWidth+=2;
            }
            if (navigator.userAgent.indexOf("Trident")>0 ) {
                slatsWidth+=1;
            }
        },
        selectAllow: function (select) {

            selectId = select.resourceId;
            if (select.start.isBefore(moment().add(8, 'hours'))) {
                return false;
            } else if (reserveCycle != 0 && select.start.isAfter(moment().add(reserveCycle,'days').format('YYYY-MM-DD'),'day')){
                return false;
            } else {

                return true;
            }
        },
        unselect: function(event, view) {
            $('.fc-highlight-times').remove();
        },
        select: function (start, end, event, view, resource) {
            if (navigator.userAgent.indexOf('Firefox') >= 0) {
                $('.popover').popover('hide');
            }
            var min = parseInt(timelineOptons.minTime.split(':')[0]);
            var hours = start.hour();
            var minutes = start.minute();
            var space = (end.format('x') - start.format('x'))/(15*60*1000);
            var left = ((hours-min)*4+minutes/15+space/2)*slatsWidth/4;
            //添加dome节点(时间段标签)
            if ($('.fc-time-area [data-resource-id="'+resource.id+'"]').find('.fc-highlight-times').length == 0) {
                $('.fc-time-area [data-resource-id="'+resource.id+'"] td > div').prepend('<div class="fc-highlight-times"></div>')
            }
            $('.fc-time-area [data-resource-id="'+resource.id+'"]').find('.fc-highlight-times').css({left: left+'px'}).text(start.format('H:mm')+'~'+end.format('H:mm'))
            //动态添加会议选定时间标签
            var index = $('.fc-time-area [data-resource-id="'+resource.id+'"]').index();
            if (index == 0) {
                $('.fc-time-area [data-resource-id="'+resource.id+'"]').find('.fc-highlight-times').addClass('under')
            }else if($('.fc-time-area [data-resource-id="'+resource.id+'"]').offset().top<375){ //当模块向上滑动到第一个时,时间也处理为向下显示
                $('.fc-time-area [data-resource-id="'+resource.id+'"]').find('.fc-highlight-times').addClass('under')
            }
            var obj = {
                start: start.format('x'),
                end: end.format('x'),
                location: {
                    id: resource.id,
                    name: resource.name
                }
            };
            var newBtn = $('<div class="btn-new">新建会议</div>');
            newBtn.data(obj);
            var selectTarget = null;
            if (selectId) {
                selectTarget = $('tr[data-resource-id="'+selectId+'"]').find('.fc-highlight');
            }else {
                if($(event.target).hasClass('fc-highlight')){
                    selectTarget = $(event.target)
                }else {
                    selectTarget = $(event.target).find('.fc-highlight');
                }
            }
            newBtn.css({
                top: selectTarget.position().top + selectTarget.outerHeight() - 52,
                left: selectTarget.position().left + selectTarget.outerWidth()
            });
            //预定会议的按钮位置显示  段
            var $wd =selectTarget.offset().left+selectTarget.outerWidth();
            var $body_wd =$('body').width();
            if($body_wd-$wd<=168){
                if(selectTarget.outerWidth()>168){
                    newBtn.css({
                        left:selectTarget.position().left+(selectTarget.outerWidth()-168)
                    })
                }else {
                    newBtn.css({
                        left:selectTarget.position().left-167
                    })
                }
            }
            selectTarget.parents('tr[data-resource-id]').find('.fc-event-container').append(newBtn);
            event.stopPropagation();
        },
        resourceLabelText: '共8个会议室',
        resources: function (callback) {
            callback(resourceObjects)
        },
        resourceRender: function (resource, label, body) {
            var used = _.indexOf(roomId, resource.id) >= 0 ? 'thumb used':'thumb';
            var tmpl = '\n      <div class="room-info">\n        <div class="' + used + '">\n       <div class="imgBox">  <img src="' + (resource.smallPicture != '' ? resource.smallPicture : 'static/images/huiyi-mini.png') + '">\n  </div>        ' + (resource.isAudit == 1 ? '<span>需审核</span>' : '') + '\n        </div>\n        <h6>' + resource.name + '</h6>\n        <span class="btn-booking">\u9884<br/>\u8BA2</span>\n        <p><span class="icon icon-xq-number"></span>' + resource.persionNumber + '人</p>\n        <p><span class="icon icon-room-equipment"></span>' + resource.deviceService + '</p>\n      </div>';
            label.children('div').html(tmpl);
            var tpl = '\n        <div class="room-popover-info">\n          <img src="{{bigPicture == \'\' ? \'static/images/huiyi.png\':bigPicture}}">\n          <h6>{{name}}<span>{{persionNumber}}人</span></h6>\n          <p><span class="icon icon-room-address"></span>{{location}}</p>\n          <p><span class="icon icon-room-equipment"></span>{{deviceService}}</p>\n          <div ng-if="isAudit == 1" class="audit">\n            <span>\u9700\u5BA1\u6838</span>\n          </div>\n        </div>';
            label.find('.thumb').popover({
                container: '.main',
                placement: 'top',
                html:true,
                content: soda(tpl,resource),
                trigger: 'click'
            });
            label.find('.btn-booking').data({
                location: {
                    id: resource.id,
                    name: resource.name
                }
            });
        },
        events: function (start, end, timezone, callback) {
            fetchs.post('/meeting/selectsettimemeeting', {start: start.format('x'), all:1}, function (res) {
                var events = [];
                if (res.ifSuc == 1) {
                    events = res.data;
                } else {
                    notify('danger', res.msg)
                }
                callback(events);
            })
        },
        eventClick: function (calEvent, event, view) {
            popoverElement = $(event.currentTarget);
        },
        eventRender: function (event, element, view) {
            var start = moment(event.start).format('H:mm');
            var end = moment(event.end).format('H:mm');
            event.before = moment().isBefore(event.start);
            event.after = moment().isBefore(event.end);
            var tpl='';
            if(event.isPublic==0 ){
                tpl = '\n        <div class="ellipsis fc-content" data-title="非公开会议">\n          <span class="times">' + start + '~' + end + '</span>\n     <div>     <span>非公开会议</span>\n   </div>\n        <div class="fc-bg"></div>';
            }else {
                tpl = '\n        <div class="ellipsis fc-content" data-title="' + (event.meetingSubject != undefined ? event.meetingSubject : '') + '">\n          <span class="times">' + start + '~' + end + '</span>\n     <div>     <span>' + event.meetingSubject + '</span>\n          <span class="organizer">' + event.name + '<br/>' + event.phone + '</span>\n        </div>\n        <div class="fc-bg"></div>';
            }
            if (event.state == '2'){
                $(element).html('<div class="ellipsis fc-content" data-title="'+ start + '~' + end +' 已有待审批的会议"><span class="times">' + start + '~' + end + ' 已有待审批的会议</span></div>').addClass('fc-event-auditing');
            }else {
                $(element).html(tpl);
            }
            if (event.state == 0) {
                // $(element).addClass('partake')
            }
            var tpl = '\n        <div ng-class="state == 0 ? \'meeting-info\':\'meeting-info \'">\n          <h6 ng-if="isPublic==1" data-id="{{isSelectDeteils == 1 ? meetingId:\'\'}}">{{meetingSubject}}</h6>\n     <h6 ng-if="isPublic==0">非公开会议</h6>\n      <p>\u65F6\u95F4\uFF1A{{start|date:\'H:mm\'}}~{{end|date:\'H:mm\'}}</p>\n          <p>\u5730\u70B9\uFF1A{{location}}</p>\n       <div ng-if="isPublic==1" >   <div class="user">\n            <img class="user-url" ng-if="smallPicture" src="{{smallPicture}}"><span ng-if="!smallPicture || smallPicture==\'\'" class="nophoto">{{name|name}}</span>\n            <span class="name">{{name}}</span>\n            <span ng-if="isReservationPerson==0">{{phone}}</span>\n          </div>\n          <div class="handles" ng-if="isReservationPerson==1">\n            <button ng-if="before" class="btn-meeting-edit" data-id="{{meetingId}}">\u4FEE\u6539\u4F1A\u8BAE</button>\n            <button ng-if="after" data-toggle="modal" data-target="#cancelMeeting" data-id="{{meetingId}}" data-title="{{meetingSubject}}">\u53D6\u6D88\u4F1A\u8BAE</button>\n     </div>     </div>\n        </div>';
            if (event.state != '2'){
                element.popover({
                    placement: function(evt) {
                        var po = $('body').width()-360<getLeft(element[0])-element.parents('.fc-scroller').scrollLeft();
                        return po ? 'left':'top';
                    },
                    html: true,
                    content: soda(tpl, event),
                    trigger: 'click'
                });
            }
        },
        viewRender: function (view, element) {
            var date = $('#timeline').fullCalendar('getDate');
            var min = parseInt(view.options.minTime.split(':')[0]);
            var max = parseInt(view.options.maxTime.split(':')[0]);
            if (date.isBefore(moment().format('YYYY-MM-DD'),'day')) {
                $('.fc-slats td').addClass('fc-busy');
                $('.fc-chrono th').addClass('fc-busy');
            }
            $('.fc-chrono th').eq(0).addClass('fc-busy');
            $('.fc-slats td').eq(0).addClass('fc-busy');
            $('.fc-slats td').eq(1).addClass('fc-busy');
            $('.fc-chrono th').eq(-1).addClass('fc-busy');
            $('.fc-slats td').eq(-1).addClass('fc-busy');
            $('.fc-slats td').eq(-2).addClass('fc-busy');
            if (reserveCycle != 0 && date.isAfter(moment().add(reserveCycle-1,'days').format('YYYY-MM-DD'),'day')) {
                $('.fc-slats td').addClass('fc-busy');
                $('.fc-chrono th').addClass('fc-busy');
            }
            if (moment().isSame(date.format('YYYY-MM-DD'), 'day') && min < parseInt(moment().format('H'))) {
                $('.fc-slats td').each(function() {
                    if ($(this).index() < (parseInt(moment().format('H'))-min)*2) {
                        $(this).addClass('fc-busy')
                    }
                })
                $('.fc-chrono th').each(function(){
                    if ($(this).index()*2 < (parseInt(moment().format('H'))-min)*2) {
                        $(this).addClass('fc-busy')
                    }
                })
            }
            slatsWidth = $('.fc-slats td').width()*2;
            if (navigator.userAgent.indexOf("Firefox")>0) {
                slatsWidth+=2;
            }
            if (navigator.userAgent.indexOf("Trident")>0 ) {
                slatsWidth+=1;
            }
            if ($('.now-time-line').length<1) {
                $('.fc-time-area .fc-scroller-canvas').append('<div class="now-time-line"></div>');
            }
            refreshClock()
            $('.fc-unselectable .fc-scroller').on('scroll', function(e){
                if (popoverElement) {
                    popoverElement.popover('hide');
                    popoverElement = null
                }
                if ($(this).scrollTop()+$(this).height()>=$(this)[0].scrollHeight) {
                    if ($(this).find('.loader').length < 1) {
                        if (hasmore) {
                            $(this).append('<div class="loader"><span></span><span></span><span></span></div>');
                            loadmore($(this))
                        }
                    }
                }
                var times = moment().format('H:m').split(':');
                var indicatorLeft = slatsWidth*(parseInt(times[0])-min+parseInt(times[1])/60);
                if (times[0]<min || times[0]>max) {
                    $('.now-time-line').css({
                        position: 'fixed',
                        left: $(this).offset().left,
                        top: $(this).offset().top
                    })
                    $('.fc-widget-header .now-time-line').css({
                        position: 'fixed',
                        left: $(this).offset().left,
                        top: $(this).offset().top - 36
                    })
                }else {
                    if (indicatorLeft <= $(this).scrollLeft()) {
                        $('.now-time-line').css({
                            left: $(this).scrollLeft()
                        })
                        $('.fc-widget-header .now-time-line').css({
                            position: 'fixed',
                            left: $(this).offset().left,
                            top: $(this).offset().top - 36
                        })
                    }else{
                        $('.now-time-line').css({
                            position: 'absolute',
                            left: indicatorLeft,
                            top: 0
                        })
                    }
                }

            })
        },
        schedulerLicenseKey: 'CC-Attribution-NonCommercial-NoDerivatives'
    };
    var popoverElement;
    $('body').on('click', function(e) {
        if (popoverElement && $(e.target).parents('a.fc-event').length === 0) {
            $('a.fc-event').popover('hide');
        }else{

            $('a.fc-event').not(popoverElement).popover('hide')
        }
        if (popoverElement && popoverElement.hasClass('thumb') && $(e.target).parents('.thumb').length === 0) {
            $('.thumb').popover('hide')
        }else{
            $('.thumb').not(popoverElement).popover('hide')
        }
    });

    $('body').on('click', '.thumb', function(e){
        popoverElement = $(this);
    })
    // 会议人数、会议类型点击选择事件
    $('.capacity input,.type input').on('click', function () {
        var name = $(this).attr('name');
        if ($('input[name="' + name + '"]:checked').length == 2) {
            $('input[name="' + name + '"]:checked').not($(this))[0].checked = false;
        }
        getRooms();
    });
    // 会议设备点击事件
    $('input[name="deviceService"]').on('click', function () {
        getRooms();
    })

    // 区域点击事件
    // $('#area').on('change', function () {
    //     getRooms();
    // })
    var hasmore = false;
    function getRooms(callback) {
        var serializes = $('#filterForm').serializeArray();
        var params = {};
        for (var item in serializes) {
            if (serializes[item].name == 'start' || serializes[item].name == 'end') {
                params[serializes[item].name] = parseInt(serializes[item].value)
            } else {
                if (params[serializes[item].name]) {
                    params[serializes[item].name] = params[serializes[item].name] + ',' + serializes[item].value;
                } else {
                    params[serializes[item].name] = serializes[item].value;
                }
            }
        }
        if (!params.personNum) {
            params.personNum = ''
        }
        if (!params.deviceService) {
            params.deviceService = ''
        }
        if (!params.isAudit) {
            params.isAudit = ''
        }
        var initial = _.isEqual(params, {staTime: "", endTime: "", personNum:"",deviceService:"",isAudit:""});
        roomFilters.currentPage = 1;
        roomFilters.areaId = currentareaId;
        roomFilters = $.extend({},roomFilters,params)
        fetchs.post('/meetingRoom/screeningMettingRoom', roomFilters, function (res) {

            if (res.data.meetingRoom.length == 0) {
                if (!initial) {
                    $('#timeline').html('<div class="nothing">没有符合条件的会议室</div>')
                } else {
                    $('#timeline').html('<div class="nothing">没有会议室可以预订，<br/>请管理员去控制台添加吧！</div>')
                }

            } else {
                resourceObjects = res.data.meetingRoom;
                timelineOptons.resourceLabelText = '共有' + res.data.roomTotal + '个会议室';
                $('#timeline').html('');
                $('#timeline').fullCalendar('destroy');
                timelineOptons.defaultDate = $('#datepicker').datepicker('getDate');
                $('#timeline').fullCalendar(timelineOptons)
                $('.fc-resource-area  .fc-cell-text').html('共有&nbsp;<span style="color: #fc6f4c">' + res.data.roomTotal + '</span>&nbsp;个会议室')
            }
        })
    }
    function loadmore(ele){
        roomFilters.currentPage = roomFilters.currentPage+1;
        fetchs.post('/meetingRoom/mettingRoomAreaSorting', roomFilters, function(res) {
            if (res.ifSuc == 1) {
                $.each(res.data, function(index, item){
                    if(index == 0) {
                        $('#timeline').fullCalendar('addResource', item, true);
                    }else{
                        $('#timeline').fullCalendar('addResource', item);
                    }
                })
                ele.find('.loader').remove();
                if (res.data.length == 0 || res.data.length < 15) {
                    hasmore = false;
                }
            }
        })
    }
    // 常用会议室
    var roomId = [];
    fetchs.post('/meetingRoom/selectUseFrequencyMany',{},function(res){
        if (res.ifSuc == 1) {
            var tpl = '<li ng-repeat="item in data" data-id="{{item.id}}">{{item.name}}<span class="icon icon-tick"></span></li>';
            $('.popular-room ul').html(soda(tpl, {data: res.data}))
        }
    })
    $('.popular-room').on('click', 'li', function () {
        var id = $(this).data('id');
        var index = _.indexOf(roomId, id);
        if (index < 0) {
            roomId.push(id)
            $(this).addClass('checked');
        }else{
            roomId.splice(index, 1)
            $(this).removeClass('checked');
        }
        roomFilters.roomId = roomId.join(',');
        getRooms();
    })
    getRooms();
    var userinfos = null
    fetchs.get('/getSysUserInfoByUserId?token=' + _userInfo.token, function (res) {
        userinfos = res.data;
    })
    $('body').on('click', '.btn-new', function(e){
        var data = $(this).data();
        var self = $(this);
        fetchs.post('/meeting/selectuserrole', {roomId:data.location.id}, function(res){
            if (res.ifSuc == 1 && res.data.isCreate == 1) {
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
                //新建会议的时候拿到是否是付费用户状态
                //存储的付费状态信息与获取到的不一致时,从新赋值
                if(res.data.tenantType && fetchs.status != res.data.tenantType){
                    var tenantType ={'status':res.data.tenantType};
                    localStorage.setItem('tenantType',JSON.stringify(tenantType));
                    fetchs.status= tenantType.status;
                }
                self.detail('show',data);
            }else if(res.ifSuc == 1 && res.data.isCreate == 0){
                notify('danger', '您没有该会议室的预订权限')
            }else{
                notify('danger', res.msg)
            }
        })
    })
    $('body').on('click', '.btn-booking', function (e) {
        var data = $(this).data();
        var self = $(this);
        fetchs.post('/meeting/selectuserrole', {roomId:data.location.id}, function(res){
            if (res.ifSuc == 1 && res.data.isCreate == 1) {
                data.type = 1;
                data.start = new Date(Math.ceil(new Date().getTime() / (15 * 60 * 1000)) * 15 * 60 * 1000).getTime();
                data.end = data.start + 15 * 60 * 1000;
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
                self.detail('show', data)
            }else if(res.ifSuc == 1 && res.data.isCreate == 0){
                notify('danger', '您没有该会议室的预订权限')
            }else{
                notify('danger', res.msg)
            }
        })
    })
    var timeLineText = '收起筛选';
    function refreshClock() {
        var min = parseInt($('#timeline').fullCalendar('option', 'minTime').split(':')[0]);
        var max = parseInt($('#timeline').fullCalendar('option', 'maxTime').split(':')[0]);
        var times = moment().format('H:m:s').split(':');
        var left = slatsWidth*(parseInt(times[0])-min+parseInt(times[1])/60);

        if (times[0]<min || times[0]>max) {
            $('.now-time-line').css({
                position: 'fixed',
                left: $('.fc-unselectable .fc-scroller').offset().left,
                top: $('.fc-unselectable .fc-scroller').offset().top
            })
            $('.fc-widget-header .now-time-line').css({
                position: 'fixed',
                left: $('.fc-unselectable .fc-scroller').offset().left,
                top: $('.fc-unselectable .fc-scroller').offset().top - 36
            })
        }else {
            if ($('.fc-unselectable .fc-scroller').scrollLeft() < left) {
                $('.now-time-line').css({
                    position: 'absolute',
                    left: left,
                    top: 0
                })
            }
        }
        $('.fc-widget-header .now-time-line').html('<span>'+moment().format('H:mm')+'<i>'+timeLineText+'</i></span>')
        setTimeout(refreshClock, (60-parseInt(times[2]))*1000)
    }
    function hoverTimeline() {
        var times = moment().format('H:m:s').split(':');
        var min = parseInt($('#timeline').fullCalendar('option', 'minTime').split(':')[0]);
        var max = parseInt($('#timeline').fullCalendar('option', 'maxTime').split(':')[0]);
        var left = slatsWidth*(parseInt(times[0])-min+parseInt(times[1])/60);
        if (times[0]<min || times[0]>max) {
            left = 0;
        }else {
            if ($('.fc-unselectable .fc-scroller').scrollLeft()-70 > left) {
                $('.fc-widget-header .now-time-line').css({
                    position: 'fixed',
                    left: $('.fc-unselectable .fc-scroller').offset().left,
                    top: $('.fc-unselectable .fc-scroller').offset().top - 36
                })
            }else{
                $('.fc-widget-header .now-time-line').css({
                    position: 'fixed',
                    left: $('.fc-unselectable .fc-scroller').offset().left+(left-$('.fc-unselectable .fc-scroller').scrollLeft()),
                    top: $('.fc-unselectable .fc-scroller').offset().top - 36
                })
            }
        }

    }
    setTimeout(function () {
        if (!localStorage.getItem('timeline')) {
            hoverTimeline()
            $('.now-time-line span').addClass('hover');
            setTimeout(function(){
                $('.now-time-line span').removeClass('hover');
            }, 1000);
            localStorage.setItem('timeline', true)
        }
    },1000)
    $('body').on('mouseover', '.now-time-line span', hoverTimeline)

    $('.filter').collapse('show');
    $('body').on('click', '.now-time-line span', function(e){
        var times = moment().format('H:m:s').split(':');
        var min = parseInt($('#timeline').fullCalendar('option', 'minTime').split(':')[0]);
        var left = slatsWidth*(parseInt(times[0])-min+parseInt(times[1])/60);
        $('.now-time-line').css({
            position: 'absolute',
            left: left,
            top: 0
        })
        $('.filter').collapse('toggle');
    })
    $('.filter').on('hidden.bs.collapse', function () {
        timelineOptons.height = $('body').innerHeight() - 98;
        $('#timeline').fullCalendar('option', 'height', $('body').innerHeight() - 98);
        $('.now-time-line i').text('展开筛选');
        timeLineText = '展开筛选';
    })
    $('.filter').on('show.bs.collapse', function () {
        timelineOptons.height = $('body').innerHeight() - 201 - 98;
        $('#timeline').fullCalendar('option', 'height', $('body').innerHeight() - 201 - 98);
        //201为$('.filter').innerHeight()，解决IE11兼容问题改成死值
        $('.now-time-line i').text('收起筛选');
        timeLineText = '收起筛选';
    })
    // 点击新建会议按钮外部区域,此按钮消失
    $('body').on('mousedown', function(e){
        if(!$(e.target).hasClass('btn-new')){
            $('.btn-new').remove();
            selectId = null;;
        }
    });
    function getLeft(e){
        var offset=e.offsetLeft;
        if(e.offsetParent!=null) offset+=getLeft(e.offsetParent);
        return offset;
    }
});