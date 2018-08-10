'use strict';

$(function () {
    var dynamics = {
        data: [],
        keyword: ''
    };
    // 自定义sodajs前缀
    soda.prefix('ng-');
    //名字灰色显示
    soda.filter('searchKey', function (item, word) {
        var string = item.message;
        var name = item.name;
        if(word.length==0){ //没有搜索
            if(string.indexOf(name)==0 && item.sys == 1){
                var result = string.slice(name.length);
                return '<font color="#7e838c">' + name + '</font>' + result;
            }
            return string;
        }
        if(name!=null&&name!=''){
            if(name.indexOf(word)>=0 && item.sys == 1){
                //名字里包含搜索关键字
                var temp = string.slice(name.length);
                var reg = word.replace(/([\.|\*|\$|\*|\(|\)|\+])/g, "\\" + "$1");
                var nameTemp = name.split(word);
                var name1 = nameTemp[0];
                var name2 = nameTemp[1];
                var nameStr = '<font color="#7e838c">'+ name1+'</font>'+'<font color="#FA702">'+word+'</font>'+'<font color="#7e838c">'+ name2+'</font>'
                var result = word == '' ? temp : temp.toString().replace(new RegExp(reg, 'g'), '<font color="#FA702">' + word + '</font>');
                return nameStr + result;
            }
        }
        if (string == null || word == null) return string;
        if (string.length == 0 || word.length == 0) return string;
        var reg = word.replace(/([\.|\*|\$|\*|\(|\)|\+])/g, "\\" + "$1");
        var result = word == '' ? string : string.toString().replace(new RegExp(reg, 'g'), '<font color="#FA702">' + word + '</font>');
        if(string.indexOf(name)==0 && item.sys == 1) {
            var temp = result.slice(name.length);
            return '<font color="#7e838c">' + name + '</font>' + temp;
        }
        return result;
    });
    var dynamicsTpl = '\n  <div class="notices" ng-repeat="item in data">\n    <h6>\n      <a href="#" data-id="{{item.id}}" data-toggle="tooltip" title="{{item.title}}" ng-html="item.title|keylight:keyword"></a>\n      <a ng-class="item.dateState==1 ? \'btn-show-more collapsed\':\'btn-show-more no-more collapsed\'" data-toggle="collapse" href="#{{\'notic\'+item.id}}">{{item.dynamics.length}}</a>\n      <span class="times" ng-html="item.start|period:item.end"></span>\n    </h6>\n    <ul >\n      <li ng-repeat="item in item.dynamics" ng-if="item.timeago > nowTime">\n        <img class="user-url nophoto" ng-if="item.smallPicture && item.sys == 0" src="{{item.smallPicture}}"><span ng-if="item.sys == 0 && !item.smallPicture" class="nophoto small {{item.name|charcode}}"><i>{{item.name|slice:-2}}</i></span>\n        <span ng-if="item.sys == 1" class="icon icon-systeminfo"></span>\n        <span class="timeago">{{item.timeago|timeago}}</span>\n        <p><span class="name" ng-if="item.sys == 0" ng-html="item.name|keylight:keyword"></span><span class="name" ng-if="item.sys == 0">\uFF1A</span><span ng-html="item|searchKey:keyword"></span></p>\n      </li>\n    </ul>\n   <ul id="{{\'notic\'+item.id}}" class="collapse">\n      <li ng-repeat="item in item.dynamics" ng-if="item.timeago <= nowTime">\n        <span ng-if="item.sys == 0" class="nophoto small {{item.name|charcode}}"><i>{{item.name|slice:-2}}</i></span>\n        <span ng-if="item.sys == 1" class="icon icon-systeminfo"></span>\n        <span class="timeago">{{item.timeago|timeago}}</span>\n        <p><span class="name" ng-if="item.sys == 0" ng-html="item.name|keylight:keyword"></span><span class="name" ng-if="item.sys == 0">\uFF1A</span><span  ng-html="item|searchKey:keyword"></span></p>\n      </li>\n    </ul>\n</div>\n  <div ng-if="data.length<1" class="nothing">{{keyword ? \'\u641C\u7D22\u4E0D\u5230\u60A8\u9700\u8981\u7684\u5185\u5BB9\':\'\u8FD8\u6CA1\u6709\u4F1A\u8BAE\u52A8\u6001\'}}</div>';
    var reserveCycle = 0; //预定周期
    // 会议室预定设置信息
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
    });
    var params = {
        all: 1,
        currentPage: 1
        // 会议动态初始化数据
    };fetchs.post('/dynamic/selectrecentmeetingdynamic', params, function (res) {
        if (res.ifSuc == 1) {
            dynamics.data = res.data;
            var timestamp=new Date().getTime();
            console.log(timestamp);
            //获取7天前的日期
            dynamics.nowTime=timestamp-(1000 * 60 * 60 * 24*7);
            $('#dynamics .result').html(soda(dynamicsTpl, dynamics));
            if (res.data.length == 15) {
                hasmore = true;
            }
        } else {
            $('#dynamics .result').html(soda(dynamicsTpl, dynamics));
        }
    });
    // 搜索会议动态
    $('body').on('keypress', '#dynamics input', function (event) {
            var keyword = $(this).val().trim();
            if (event.keyCode == '13') {
                params.search = keyword;
                $('#dynamics .title span').css('display', 'inline-block');
                fetchs.post('/dynamic/selectrecentmeetingdynamic', params, function (res) {
                    if (res.ifSuc == 1) {
                        dynamics.keyword = keyword;
                        dynamics.data = res.data;
                        $('#dynamics .result').html(soda(dynamicsTpl, dynamics));
                        if (res.data.length == 15) {
                            hasmore = true;
                        }else if(res.data.length<=0){
                            $(".nothing").html("搜索不到您需要的内容");
                        }
                    }else{
                        dynamics.keyword = keyword;
                        $('#dynamics .result').html(soda(dynamicsTpl, dynamics));
                    }
                });
                $(this).blur();
            }
        }
        // 取消搜索
    );
    $('#dynamics .title span').on('click', function () {
            $('#dynamics input').val('');
            dynamics.keyword = '';
            params.currentPage = 1;
            delete params.search;
            $(this).removeAttr('style');
            fetchs.post('/dynamic/selectrecentmeetingdynamic', params, function (res) {
                if (res.ifSuc == 1) {
                    dynamics.data = res.data;
                    $('#dynamics .result').html(soda(dynamicsTpl, dynamics));
                    if (res.data.length == 15) {
                        hasmore = true;
                    }
                } else {
                    $('#dynamics .result').html(soda(dynamicsTpl, dynamics));
                }
            });
        }
        // 滚动底部加载更多
    );
    var hasmore = false; // 是否有更多
    $('#dynamics .wrap').on('scroll', function () {
        if ($(this).scrollTop() + $(this).outerHeight() >= $(this)[0].scrollHeight) {
            console.log(0);
            if ($('#dynamics .result').find('.loader').length < 1) {
                if (hasmore) {
                    $('#dynamics .result').append('<div class="loader"><span></span><span></span><span></span></div>');
                    loadmore($('#dynamics .result'));
                }
            }
        }
    });
    function loadmore(ele) {
        params.currentPage = params.currentPage + 1;
        fetchs.post('/dynamic/selectrecentmeetingdynamic', params, function (res) {
            if (res.ifSuc == 1) {
                dynamics.data.push.apply(dynamics.data, res.data);
                $('#dynamics .result').html(soda(dynamicsTpl, dynamics));
                ele.find('.loader').remove();
                if (res.data.length == 0 || res.data.length < 15) {
                    hasmore = false;
                }
            }
        });
    }

});