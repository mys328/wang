'use strict';

$(function () {
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        if ($(e.target).attr('href') == '#monitor') {

        } else if ($(e.target).attr('href') == '#switchgear') {
            getPlans()
        }
    });
    //==== 计划开关机 ====

    var switchgear = {
        data: [],
        keyword: ''
    };
    var plansParams = {
        word: '',
        state: ''
    };
    soda.discribe('period', '\n  <ul class="periods">\n    <li ng-repeat="period in item.periods">\n      <span>\n        <b ng-repeat="time in period.times">\n          {{time.startTime}}~{{time.endTime}}\n        </b>\n      </span>\n      <span>{{period.weekly|weekly}}</span>\n    </li>\n    <li ng-if="item.ifOpenDown == 1">\n      <span>\n        <i>{{item.downStartTime|date:\'YYYY-M-D HH:mm\'}}</i>\n      </span>\n      <span>\n        <i>{{item.downEndTime|date:\'YYYY-M-D HH:mm\'}}</i>\u8BE5\u65F6\u6BB5\u5173\u673A\n      </span>\n    </li>\n  </ul>');
    var planTpl = '\n  <div ng-repeat="item in data" ng-class="item.status == 0 ? \'task\':\'task running\'">\n    <div class="head">\n      <div class="title">\n        <h3 class="ellipsis" data-title="{{item.taskName}}" ng-html="item.taskName|keylight:keyword"></h3><span>{{item.status == 0 ? \'\u672A\u542F\u52A8\':\'\u8FD0\u884C\u4E2D\'}}</span>\n      </div>\n      <div class="runnings">\n        <a class="btn-show-terminals" data-id="{{item.id}}">\u6267\u884C\u7EC8\u7AEF\uFF1A{{item.terminalCount}}\u4E2A</a>\n        <div class="errors" ng-if="item.errorCount != 0">{{item.errorCount}}\u4E2A\u53D1\u9001\u5931\u8D25 \u8BF7<button class="btn btn-clear-primary btn-terminal-retry" data-id="{{item.id}}" data-batch="true" data-terminals="{{item.terminals}}">\u91CD\u8BD5</button></div>\n      </div>\n      <button class="btn btn-clear-primary btn-get-logs" data-id="{{item.id}}">\u67E5\u770B\u65E5\u5FD7</button>\n      <button class="btn btn-clear-primary" data-toggle="modal" data-target="#actionsModal" data-type="0" data-id="{{item.id}}" data-title="{{item.taskName}}" data-status="{{item.status}}">\u5220\u9664</button>\n      <button class="btn btn-clear-primary btn-edit-task" data-id="{{item.id}}">\u4FEE\u6539</button>\n      <button ng-if="item.status != 0" class="btn btn-clear-primary" data-toggle="modal" data-target="#actionsModal" data-type="2" data-id="{{item.id}}" data-title="{{item.taskName}}">\u505C\u6B62</button>\n      <button ng-if="item.status == 0" class="btn btn-clear-primary" data-toggle="modal" data-target="#actionsModal" data-type="1" data-id="{{item.id}}" data-title="{{item.taskName}}">\u542F\u52A8</button>\n    </div>\n    <ul ng-include="period:item"></ul>\n    <div class="terminals"></div>\n  </div>\n  <div class="nothing" ng-if="data.length == 0 && keyword == \'\'">\n    \u5F53\u524D\u8FD8\u6CA1\u6709\u8BA1\u5212\u5F00\u5173\u673A\u4EFB\u52A1\uFF0C\u60A8\u53EF\u4EE5<button class="btn btn-clear-primary" data-toggle="modal" data-target="#taskModal" data-backdrop="static">\u521B\u5EFA\u4EFB\u52A1</button>\n  </div>\n  <div class="nothing" ng-if="data.length == 0 && keyword != \'\'">\n    \u6CA1\u6709\u641C\u7D22\u5230\u4FE1\u606F\uFF0C\u6362\u4E2A\u6761\u4EF6\u8BD5\u8BD5\uFF1F<br/>\u60A8\u53EF\u4EE5\u8F93\u5165\u4EFB\u52A1\u540D\u79F0\u3001\u7EC8\u7AEF\u540D\u79F0\u7B49\u90E8\u5206\u5185\u5BB9\u68C0\u7D22\u3002\n  </div>';
    function getPlans() {
        if (hasTerminals) {
            fetchs.post('/planSwitch/getPlans', plansParams, function (res) {
                if (res.ifSuc == 1) {
                    switchgear.data = res.data.plans;
                    switchgear.keyword = plansParams.word;
                    var filter = [];
                    var filterstr = ['全部任务', '运行中', '未启动']
                    for (var i = 0; i < res.data.counts.length; i++) {
                        var num = res.data.counts[i];
                        var val = i==0 ? '':i==1?1:0;

                        if (val === plansParams.state) {
                            $('#switchgear .filter-dropdown .dropdown-toggle').text(filterstr[i]+'('+res.data.counts[i]+')')
                        }
                        filter.push('<a class="dropdown-item'+(val === plansParams.state ? ' active':'')+'" data-state="'+val+'" data-num="'+num+'">'+filterstr[i]+'('+num+')</a>')
                    }
                    $('#switchgear .filter-dropdown .dropdown-menu').html(filter.join(''));
                    $('.switchgear-table').html(soda(planTpl, switchgear));
                }
            });
        }else {
            $('.switchgear-table').html('<div class="nothing" id="nullData">当前还没有可监控的终端。<br>您可以根据<a href="../static/terminal_sales/terminal_sales.html"  target="view_window" style="text-decoration:underline">购买说明</a>，购买会议显示终端，<br>如果您已经购买，请根据<a style="color: #0AB2F5">购买说明-用户指南</a>进行终端的注册。</div>');
        }

    }
    // 搜索
    $('#switchgear #task-search').on('keypress', function (evt) {
        if (evt.keyCode == 13) {
            $(this).parent('.search-box').removeClass('border-shadow');
            $('#switchgear #task-search').blur();
            var key = this.value.trim();
            if (key != '') {
                $('#switchgear .btn-search-del').show();
                plansParams.word = key;
                switchgear.keyword = key;
                getPlans();
            } else {
                plansParams.word = key;
                switchgear.keyword = key;
                getPlans();
            }
        }
    });
    $('#switchgear #task-search').on('focus', function (evt) {
        $(this).parent('.search-box').addClass('border-shadow');
    });
    $('#switchgear #task-search').on('blur', function (evt) {
        $(this).parent('.search-box').removeClass('border-shadow');
    });
    $('#search').on('focus', function (evt) {
        $(this).parent('.search-box').addClass('border-shadow');
    });
    $('#search').on('blur', function (evt) {
        $(this).parent('.search-box').removeClass('border-shadow');
    });
    $('#switchgear .btn-search-del').on('click', function (evt) {
        $('#switchgear #task-search').val('');
        plansParams.word = '';
        switchgear.keyword = '';
        getPlans();
        $(this).hide();
    });
    // 下拉菜单
    $('#switchgear .filter-dropdown').on('click', '.dropdown-item', function () {
        if ($(this).data('num') == 0) {
            return false;
        }
        $('#switchgear .filter-dropdown .dropdown-item').removeClass('active');
        $(this).addClass('active');
        $('#switchgear .filter-dropdown .dropdown-toggle').text($(this).text());
        if (plansParams.state !== $(this).data('state')) {
            plansParams.state = $(this).data('state');
            getPlans();
        }
    });
    // 启动、停止、删除任务
    $('#actionsModal').on('show.bs.modal', function (e) {
        var data = $(e.relatedTarget).data();
        if(data) {
            var actions = ['删除', '启动', '停止', '清除'];
            $(this).find('.modal-title').text(actions[data.type] + '任务');
            $(this).find('.modal-body').html('确定要' + actions[data.type] + '<font color="#2fa2f5">' + data.title + '</font>' + '吗？');
            $(this).find('.modal-footer button').eq(1).text(actions[data.type]);
            $(this).find('.modal-footer button').eq(1).data({
                id: data.id,
                type: data.type
            });
            if (data.type == '0') {
                $(this).find('.modal-footer button').eq(1).data('status', data.status)
            }
            if (data.type == '1') {
                $(this).find('.modal-footer button').eq(1).addClass('btn-primary').removeClass('btn-danger');
            } else {
                $(this).find('.modal-footer button').eq(1).addClass('btn-danger').removeClass('btn-primary');
            }
            if (data.type == '3') {
                $(this).find('.modal-title').text('清除记录');
                $(this).find('.modal-body').html('确定要清除该开关机任务的全部日志记录吗？');
                $('.modal-backdrop').eq(0).removeClass('show');
                $('#logsModal').css('z-index', 1040);
                $(this).data('type', 3);
            }
        }else {
            $(this).find('.modal-footer button').eq(1).data('type', 4);
            $('.modal-backdrop').eq(0).removeClass('show');
            $('#taskModal').css('z-index', 1040);
            $(this).find('.modal-title').text('保存开关机任务');
            $(this).find('.modal-body').html('设置的特别关机时间大于10天，确定这是您想设置的时间吗？长时间关机，建议切断电源。');
            $(this).find('.modal-footer button').eq(1).text('确定保存').addClass('btn-danger').removeClass('btn-primary');
        }
    });
    $('#actionsModal').on('shown.bs.modal', function (e) {
        var data = $(e.relatedTarget).data();
        if(data && data.type == '3') {
            $('.modal-backdrop').eq(0).hide().addClass('show');
        }
    })

    $('#actionsModal').on('hide.bs.modal', function (e) {
        var data = $(this).data();
        $(this).find('.modal-title').text('');
        $(this).find('.modal-body').html('');
        if (data.type == 3){
            $('#logsModal').css('z-index', 1080);
            $('.modal-backdrop').eq(0).show();
            $('.modal-backdrop').eq(1).hide()
        }
        if (data.type == 4){
            $('#taskModal').css('z-index', 1080);
            $('.modal-backdrop').eq(0).show();
            $('.modal-backdrop').eq(1).hide()
        }
    });
    $('#actionsModal').on('hidden.bs.modal', function (e) {
        var data = $(this).data();
        if (data.type == 3){
            $('body').addClass('modal-open');
        }
        if (data.type == 4){
            $('body').addClass('modal-open');
        }
    });
    $('#actionsModal').on('click', '.btn-actions', function () {
        var data = $(this).data();
        if (data.type != 4){
            var api = ['/planSwitch/delete','/planSwitch/start','/planSwitch/stop','/planSwitch/clearLog']
            var params = {
                taskId: data.id
            }
            if (data.type == 0) {
                params.status = data.status
            }
            fetchs.post(api[data.type], params, function (res) {
                $('#actionsModal').modal('hide');
                if (res.ifSuc == 1){
                    if (data.type !== 3){
                        notify('success', '任务发送成功')
                    }
                } else {
                    var nums = res.data.length;
                    var terminals = res.data.splice(0,10);
                    if (res.code == 7001){
                        $('#feedbackModal .modal-title').text('启动任务失败反馈')
                        $('#feedbackModal .modal-body').html('<h4>启动任务失败</h4><p>由于终端<strong>'+terminals.join('、')+'</strong>等'+nums+'个终端正在运行其他任务。</p><p>请先停止终端正在运行的任务后重试。</p>')
                        $('#feedbackModal').modal('show')
                    }else if (res.code == 7002) {
                        $('#feedbackModal .modal-title').text('发送任务反馈')
                        $('#feedbackModal .modal-body').html('<h4>部分终端发送任务失败</h4><p>终端<strong>'+terminals.join('、')+'</strong>等'+nums+'个终端发送失败。</p><p>原因可能是终端已离线或网络故障。</p>')
                        $('#feedbackModal').modal('show')
                    }else {
                        notify('danger', res.msg)
                    }
                }
                if (data.type == 3){
                    getLogs()
                }else {
                    getPlans();
                }
            })
        }else {
            longTime = true;
            $('#actionsModal').modal('hide');
            $('#taskModal .btn-save-task').click();
        }

    });
    // 查看终端
    $('#switchgear').on('click', 'a', function (evt) {
        var data = $(this).data();
        var html = [];
        var parent = $(this).parents('.task');
        if (parent.find('.terminals').is(':hidden')) {
            // $('.switchgear-table').scrollTop(parent.position().top)
            $('.task .terminals').hide().html('');
            $('.popover').popover('dispose');
            fetchs.post('/planSwitch/getRunTerminals', { taskId:  data.id }, function (res) {
                if (res.ifSuc == 1){
                    var terminals = res.data.terminals;

                    for (var i = 0; i < terminals.length; i++) {
                        html.push('<div class="item'+(terminals[i].state == 0 ? ' fail':'')+'"'+(terminals[i].state == -1 ? ' data-title="'+terminals[i].taskName+'"':'')+'><h5>' + terminals[i].terminalName + '</h5>'+(terminals[i].meetingRoomName ? '<p>'+terminals[i].meetingRoomName+'</p>':'<p>未绑定会议室</p>')+(terminals[i].state == -2 ? '<p class="fail">离线</p>':terminals[i].state == 0?'<p><span style="color: #fa553c;">发送失败</span><button class="btn btn-clear-primary btn-terminal-retry" data-id="'+data.id+'" data-batch="false" data-terminals="'+terminals[i].id+'">重试</button></p>':terminals[i].state == -1?'<p class="fail">正在运行其他任务</p>':'' )+'</div>');
                    }
                    parent.find('.terminals').html('<div class="content">' + html.join('') + '</div>').show();
                    if (evt.pageY > $(window).height() / 2) {
                        parent.find('.terminals').addClass('top');
                    } else {
                        parent.find('.terminals').removeClass('top');
                    }
                }

            });
        }
    });
    $('body').on('click', function (e) {
        if (!$(e.target).hasClass('.btn-show-terminals') && !$(e.target).hasClass('.terminals') && !$('.task .terminals').find(e.target).length) {
            $('.task .terminals').hide().html('');
        }
    });

    $('.switchgear-table').on('click', '.btn-terminal-retry', function (e) {
        var data = $(this).data();
        fetchs.post('/planSwitch/retry', { taskId:  data.id, terminals: data.terminals }, function (res) {
            if (res.ifSuc == 1){
                notify('success', '任务发送成功');
                getPlans();
            } else {
                var nums = res.data.length;
                var terminals = res.data.splice(0,10);
                if (data.batch){
                    if (res.code == 7001){
                        $('#feedbackModal .modal-title').text('启动任务失败反馈')
                        $('#feedbackModal .modal-body').html('<h4>启动任务失败</h4><p>由于终端<strong>'+terminals.join('、')+'</strong>等'+nums+'个终端正在运行其他任务。</p><p>请先停止终端正在运行的任务后重试。</p>')
                        $('#feedbackModal').modal('show')
                    }else if (res.code == 7002) {
                        $('#feedbackModal .modal-title').text('发送任务反馈')
                        $('#feedbackModal .modal-body').html('<h4>部分终端发送任务失败</h4><p>终端<strong>'+terminals.join('、')+'</strong>等'+nums+'个终端发送失败。</p><p>原因可能是终端已离线或网络故障。</p>')
                        $('#feedbackModal').modal('show');
                        getPlans();
                    }else {
                        notify('danger', res.msg)
                        getPlans();
                    }
                }else {
                    notify('danger', '任务发送失败');
                    return false;
                }

            }
        });
    });

    // 查看日志
    var logsParams = {
        word: '',
        state: '',
        taskId: '',
        startTime: '',
        endTime: '',
        currentPage: 1,
        pageSize: 15

    }
    var logs = {
        data: [],
        keyword: ''
    };
    var showLogs = false;
    var logsTpl = '\n  <table>\n    <tbody><tr>\n      <th><span>\u8BB0\u5F55\u4FE1\u606F</span></th>\n      <th><span>\u72B6\u6001</span></th>\n      <th><span>\u6267\u884C\u65F6\u95F4</span></th>\n    </tr>\n    <tr ng-repeat="item in data">\n      <td ng-html="item.cmdContent|keylight:keyword"></td>\n      <td ng-class="item.runStatus == 1 ? \'\':\'fail\'">{{item.runStatus == 1 ? \'\u53D1\u9001\u6210\u529F\':\'\u53D1\u9001\u5931\u8D25\'}}</td>\n      <td>{{item.runTime|date:\'YYYY-MM-DD HH:mm\'}}</td>\n    </tr>\n  </tbody></table>\n  <div class="nothing" ng-if="data.length == 0 && keyword == \'\'">\n    \u5F53\u524D\u4EFB\u52A1\u65E0\u65E5\u5FD7\n  </div>\n  <div class="nothing" ng-if="data.length == 0 && keyword != \'\'">\n    \u6CA1\u6709\u641C\u7D22\u5230\u4FE1\u606F\uFF0C\u6362\u4E2A\u6761\u4EF6\u8BD5\u8BD5\uFF1F<br/>\u60A8\u53EF\u4EE5\u8F93\u5165\u7EC8\u7AEF\u540D\u79F0\u7B49\u90E8\u5206\u5185\u5BB9\u68C0\u7D22\u3002\n  </div>';
    function getLogs() {
        var html = [];
        fetchs.post('/planSwitch/getLogs', logsParams, function (res) {
            if (res.ifSuc == 1){
                var nums = 0;
                var counts = res.data.counts;
                var filtertext = '';
                $('#logsModal .filter-dropdown .dropdown-item').removeClass('active');
                $('#logsModal .filter-dropdown .dropdown-item').eq(0).data('num', counts.all).text('全部状态('+counts.all+')')
                $('#logsModal .filter-dropdown .dropdown-item').eq(1).data('num', counts.success).text('发送成功('+counts.success+')')
                $('#logsModal .filter-dropdown .dropdown-item').eq(2).data('num', counts.fail).text('发送失败('+counts.fail+')')
                if ('' === logsParams.state) {
                    filtertext = '全部状态('+counts.all+')';
                    nums = counts.all;
                    $('#logsModal .filter-dropdown .dropdown-item').eq(0).addClass('active');
                }else if (1 === logsParams.state){
                    filtertext = '发送成功('+counts.success+')';
                    nums = counts.success;
                    $('#logsModal .filter-dropdown .dropdown-item').eq(1).addClass('active');

                }else {
                    filtertext = '发送失败('+counts.fail+')';
                    nums = counts.fail;
                    $('#logsModal .filter-dropdown .dropdown-item').eq(2).addClass('active');

                }
                $('#logsModal .filter-dropdown .dropdown-toggle').text(filtertext);
                if(nums > 15){
                    $("#logsModal .bottom").show();
                    $("#logsModal .bottom").pages('upcount', nums, logsParams.currentPage);
                }else {
                    $("#logsModal .bottom").hide();
                    $("#logsModal .bottom").pages('reset');
                }
                if (res.data.logs != null){
                    logs.data = res.data.logs;
                }else {
                    logs.data = []
                }
                if (logs.data.length) {
                    $('.btn-clear-logs').prop('disabled', false)
                }else {
                    $('.btn-clear-logs').prop('disabled', true)
                }
                console.log(logs)
                $('#logsModal .scrollview').html(soda(logsTpl, logs))
                if (!showLogs) {
                    $('#logsModal').modal({
                        backdrop: 'static'
                    })
                }
            }else {
                notify('danger', res.msg);
                getPlans();
            }
        });
    }
    $('#switchgear').on('click', 'button.btn-get-logs', function () {
        var data = $(this).data();
        logsParams.taskId = data.id;
        $('#logsModal').find('.btn-clear-logs').data('id', data.id);
        getLogs()
    })
    $("#logsModal .bottom").pages({
        change:function(current,size){
            logsParams.currentPage = current;
            logsParams.pageSize = size;
            getLogs()
        },
        count: 0
    });
    $('#logsModal').on('show.bs.modal', function (e) {
        showLogs = true;
    });
    $('#logsModal').on('hidden.bs.modal', function (e) {
        showLogs = false
        $('#logsModal .scrollview').html('');
        $('#logsModal .search-input').val('');
        $('#logsModal .runtime').val('');
        $('#logsModal .runtime').datetimepicker('setDate', null, null)
        $('#logsModal .btn-search-del').hide();
        $("#logsModal .bottom").pages('reset');
        logsParams = {
            word: '',
            state: '',
            taskId: '',
            startTime: '',
            endTime: '',
            currentPage: 1,
            pageSize: 15

        }
        logs = {
            data: [],
            keyword: ''
        };
    });

    $('#logsModal .filter-dropdown .dropdown-item').on('click', function (e) {
        if ($(this).data('num') == 0) {
            return false;
        }
        $('#logsModal .filter-dropdown .dropdown-item').removeClass('active');
        $(this).addClass('active');
        $('#logsModal .filter-dropdown .dropdown-toggle').text($(this).text());
        if (logsParams.state !== $(this).data('state')) {
            logsParams.currentPage = 1;
            logsParams.state = $(this).data('state');
            getLogs();
        }
    });


    $('#logsModal').on('keypress', '.search-input', function (evt) {
        if (evt.keyCode == 13) {
            var key = this.value.trim();
            $(this).parent('.search-box').removeClass('border-shadow');
            $('#logsModal .search-input').blur();
            if (key != '') {
                $('#logsModal .btn-search-del').show();
                logsParams.word = key;
                logsParams.currentPage = 1;
                logs.keyword = key;
                getLogs();
            } else {
                logsParams.word = key;
                logsParams.currentPage = 1;
                logs.keyword = key;
                getLogs();
            }
        }
    });
    $('#logsModal').on('click', '.btn-search-del', function () {
        $('#logsModal .search-input').val('');
        logsParams.word = '';
        logsParams.currentPage = 1;
        logs.keyword = '';
        getLogs();
        $(this).hide();
    });
    $('#logsModal .runtime').datetimepicker({
        container: '#logsModal .modal-body',
        format: 'YYYY-MM-DD',
        timepicker: false,
        end: new Date().format('YYYY-MM-DD'),
        range: true,
        ensure: function (start, end) {
            logsParams.startTime = start.format('YYYY-MM-DD');
            logsParams.endTime = end.format('YYYY-MM-DD');
            getLogs();
        }
    }).on('clear.datetimepicker', function () {
        logsParams.startTime = '';
        logsParams.endTime = '';
        getLogs();
    })


    // 创建、修改任务

    var selectedTerminals = [];
    $('.switchgear-table').on('click', '.btn-edit-task', function () {
        var id = $(this).data('id');
        fetchs.post('/planSwitch/getPlan', {taskId: id}, function (res) {
            if (res.ifSuc == 1) {
                if (res.data.plan.id != null) {
                    tasks.taskId = res.data.plan.id;
                    tasks.taskName = res.data.plan.taskName;
                    selectedTerminals = res.data.plan.terminals;
                    tasks.periods = res.data.plan.periods != null ? res.data.plan.periods : [];
                    tasks.ifOpenDown = res.data.plan.ifOpenDown;
                    tasks.downStartTime =res.data.plan.downStartTime;
                    tasks.downEndTime = res.data.plan.downEndTime;
                    if (tasks.ifOpenDown == 1) {
                        tasks.downStartTime = new Date(res.data.plan.downStartTime).format('YYYY-MM-DD HH:mm:ss');
                        tasks.downEndTime = new Date(res.data.plan.downEndTime).format('YYYY-MM-DD HH:mm:ss');
                    }
                    tasks.option = 1;
                    $('#taskModal').find('.modal-title h3').text('修改开关机任务')
                    $('#taskModal').find('.btn-save-as-task').show();
                    $('#taskModal').modal({
                        backdrop: 'static'
                    })
                }else {
                    getPlans();
                    notify('danger', '任务状态发生变化，请待页面刷新后查看')
                }

                // $('#taskModal').modal('show');
            }
        });
    });
    $('#taskModal').on('show.bs.modal', function (e) {
        $(this).find('input[name="taskName"]').val(tasks.taskName);
        $('#taskModal .select-terminals').selector('setOption', {
            selected: selectedTerminals
        });
        $('.add-period').period('setOption', {
            added: tasks.periods
        });
        if (tasks.ifOpenDown == 1){
            $('#taskModal input[name="ifOpenDown"]').prop('checked', true);
            $('.special-time .timerange input').prop('disabled', false);
            $('.special-time input[name="downStartTime"]').datetimepicker('setDate', tasks.downStartTime);
            $('.special-time input[name="downEndTime"]').datetimepicker('setOption', {
                start: tasks.downStartTime.split(' ')[0],
                min: tasks.downStartTime
            });
            $('.special-time input[name="downEndTime"]').datetimepicker('setDate', tasks.downEndTime)
        }
        $('.btn-save-task').prop('disabled', false);
        $('.btn-save-as-task').prop('disabled', false);
    });
    $('#taskModal').on('hidden.bs.modal', function (e) {
        longTime = false;
        $(this).find('.modal-title h3').text('创建开关机任务')
        $(this).find('.btn-save-as-task').hide();
        $(this).find('input[name="taskName"]').val('');
        $(this).find('input[name="taskName"]').removeClass('is-invalid');
        $('#taskModal .select-terminals').selector('setOption', {
            selected: []
        });
        $(this).find('.select-terminals').removeClass('is-invalid')
        $('.add-period').period('setOption', {
            selected: []
        });
        $(this).find('.btn-add-period').show();
        $('#taskModal input[name="ifOpenDown"]').prop('checked', false);
        $('.special-time .timerange input').datetimepicker('setOption', {
            start: null,
            min: null
        });
        $('.special-time .timerange input').datetimepicker('clear');
        $('.special-time .timerange input').val('').attr('placeholder', '').prop("disabled", true);
        $('.special-time .timerange input').removeClass('is-invalid');
        $('.add-period').period('reset');
        selectedTerminals = [];
        tasks = {
            taskName: '',
            terminals: [],
            periods: [],
            ifOpenDown: 0,
            downStartTime: '',
            downEndTime: ''
        };
    });
    $('.btn-add-period').on('click', function(){
       $('.period-form .icon-error').hide();
     })
    var tasks = {
        taskName: '',
        terminals: '',
        periods: [],
        ifOpenDown: 0,
        downStartTime: '',
        downEndTime: ''
    };
    var longTime = false;
    var periodsTpl = '\n  <ul>\n    <li ng-repeat="period in periods">\n      <p>\n        <b ng-repeat="time in period.times">\n          {{time.startTime}}~{{time.endTime}}\n        </b>\n      </p>\n      <p>{{period.weekly|weekly}}</p>\n      <p class="actions">\n        <button class="btn btn-clear-primary btn-edit-period">\u4FEE\u6539</button>\n        <button class="btn btn-clear-primary btn-del-period">\u5220\u9664</button>\n      </p>\n    </li>\n  </ul>\n  <p ng-if="periods.length == 0">\u672A\u6DFB\u52A0\u8FD0\u884C\u65F6\u6BB5\uFF0C\u5C06\u9ED8\u8BA4\u5168\u5929\u5F00\u673A\uFF0C\u4E0D\u6DFB\u52A0\u65F6\u6BB5\u4E5F\u53EF\u4EE5\u5F00\u542F\u7279\u522B\u5173\u673A\u65F6\u6BB5</p>';
    // 保存任务
    function saveTask() {
        if (tasks.taskName == '') {
            $('input[name="taskName"]').addClass('is-invalid');
            notify('danger', '任务名称不能为空');
            return false;
        }else {
            if(!/^[\u4e00-\u9fa5a-zA-Z0-9_()（）]{1,50}$/.test(tasks.taskName)) {
                notify('danger', '任务名称不允许输入特殊字符');
                return false;
            }
        }

        if (selectedTerminals.length == 0) {
            $('#taskModal .select-terminals').addClass('is-invalid');
            notify('danger', '请至少选择一个终端');
            return false;
        }
        var terminals = [];
        for (var i = 0; i < selectedTerminals.length; i++) {
            terminals.push(selectedTerminals[i].id);
        }
        if (tasks.periods.length == '' && tasks.ifOpenDown == '') {
            notify('danger', '请设置运行时段或特别关机时段');
            return false;
        }

        if (tasks.ifOpenDown == 1 &&( tasks.downStartTime == '' || tasks.downEndTime == ''||tasks.downStartTime == null||tasks.downEndTime == null)) {
            $('#taskModal input[name="downStartTime"]').addClass('is-invalid');
            notify('danger', '请选择特别关机时段');
            return false;
        }

        if (tasks.ifOpenDown == 1 && tasks.downStartTime != '' && tasks.downEndTime != '') {
            var start = new Date(tasks.downStartTime.replace(/-/g, '/')).getTime();
            var end = new Date(tasks.downEndTime.replace(/-/g, '/')).getTime();
            if(!longTime && (end - start) >= 10*24*60*60*1000) {
                $('#actionsModal').data('type', 4);
                $('#actionsModal').modal('show');
                return false;
            }
        }
        tasks.terminals = terminals.join(',');
        tasks.periods = JSON.stringify(tasks.periods);

        console.log(tasks);
        $('.btn-save-task').prop('disabled', true);
        $('.btn-save-as-task').prop('disabled', true);
        if (tasks.taskId) {
            fetchs.post('/planSwitch/updatePlan', tasks, function (res) {
                if (res.ifSuc == 1){
                    $('#taskModal').modal('hide');
                    getPlans()
                } else {
                    var nums = res.data.length;
                    var terminals = res.data.splice(0,10);
                    if (res.code == 7001){
                        $('#taskModal').modal('hide');
                        $('#feedbackModal .modal-title').text('启动任务失败反馈')
                        $('#feedbackModal .modal-body').html('<h4>启动任务失败</h4><p>由于终端<strong>'+terminals.join('、')+'</strong>等'+nums+'个终端正在运行其他任务。</p><p>请先停止终端正在运行的任务后重试。</p>')
                        $('#feedbackModal').modal('show')
                    }else if (res.code == 7002) {
                        $('#taskModal').modal('hide');
                        $('#feedbackModal .modal-title').text('发送任务反馈')
                        $('#feedbackModal .modal-body').html('<h4>部分终端发送任务失败</h4><p>终端<strong>'+terminals.join('、')+'</strong>等'+nums+'个终端发送失败。</p><p>原因可能是终端已离线或网络故障。</p>')
                        $('#feedbackModal').modal('show')
                    }else {
                        notify('danger', res.msg)
                    }
                    tasks.periods = JSON.parse(tasks.periods);
                    $('.btn-save-task').prop('disabled', false);
                    $('.btn-save-as-task').prop('disabled', false);
                }
            });
        }else {
            fetchs.post('/planSwitch/addPlan', tasks, function (res) {
                if (res.ifSuc == 1){
                    $('#taskModal').modal('hide');
                    getPlans()
                }else {
                    $('.btn-save-task').prop('disabled', false);
                    $('.btn-save-as-task').prop('disabled', false);
                }
            });
        }
    }
    //聚焦清除错误高量
    $('input[name="taskName"]').focus(function(){
        $('input[name="taskName"]').removeClass('is-invalid');
    });
    $('.select-terminals').click(function(){
        $('.select-terminals').removeClass('is-invalid');
    });
    $('#taskModal').on('click', function (e) {
        // 开启特别关机时段勾选框
        if ($(e.target).attr('name') == 'ifOpenDown') {
            if ($(e.target).is(':checked')) {
                tasks.ifOpenDown = 1;
                $('.special-time .timerange input').attr('placeholder', '点击选择时间').prop("disabled", false);
            } else {
                tasks.ifOpenDown = 0;
                tasks.downStartTime = '';
                tasks.downEndTime = '';
                $('.special-time .timerange input').removeClass('is-invalid');
                $('.special-time .timerange input').attr('placeholder', '').prop("disabled", true);

            }
        }
        // 保存任务
        if ($(e.target).hasClass('btn-save-task')) {
            console.log(1);
            tasks.option = 1;
            saveTask();
        }
        // 另存为新任务
        if ($(e.target).hasClass('btn-save-as-task')) {
            console.log(2);
            tasks.option = 2;
            saveTask();
        }
    });
    // 任务名称输入
    $('#taskModal input[name="taskName"]').on('change', function (evt) {
        $(this).removeClass('is-invalid');
        tasks.taskName = $(this).val().trim();
    });
    // 选择终端
    $('#taskModal .select-terminals').selector({
        class: 'selector-terminals',
        container: '#taskModal .modal-body',
        added: [],
        resources: function resources(selector) {
            console.log(tasks);
            var taskId = tasks.taskId || '';
            fetchs.post('/planSwitch/queryTerminals', {taskId:taskId}, function (res) {
                if (res.ifSuc == 1 ){
                    if (res.data.terminals !=null){
                        selector.update(res.data.terminals);
                    }
                }
            });
        },
        renderSelected: function (item) {
            return '<span data-id="'+item.id+'">'+item.terminalName+'<i class="icon icon-delete-personnel"></i></span>'
        },
        render: function render(selector, item, selected) {
            return '<label><input name="selector" type="checkbox" value="' + item.id + '" ' + (selected >= 0 ? 'checked' : '') + '><span class="effect" '+(item.state == -1 ? (tasks.taskId && tasks.taskId == item.taskId) ? '':' data-title="正运行任务：'+item.taskName+'"':'')+'></span><div class="item"><h3>' + item.terminalName + '</h3>' + (item.meetingRoomName ? '<p>'+item.meetingRoomName+'</p>':'<p class="unbound">未绑定会议室</p>') + (item.state == -2 ? '<p class="fail">离线</p>': item.state == -1 ? (tasks.taskId && tasks.taskId == item.taskId) ? '':'<p class="fail">正在运行其他任务</p>':'') +'</div></label>';
        }
    }).on('hidden.selector', function (evt, selector, selected) {
        selectedTerminals = selected;
        $('#taskModal .select-terminals').removeClass('is-invalid');
    });
    // 设置运行时段
    $('.add-period').period({
        container: '.add-period',
        change: function change(periods) {
            $('.add-periods .periods').html(soda(periodsTpl, { periods: periods }));
        }
    }).on('hidden.period', function(evt, period, added){
        tasks.periods = added;
    })
    // 开启特别关机时段时间框
    $('.special-time .timerange input').datetimepicker({
        container: '#taskModal .modal-body',
        format: 'YYYY-MM-DD HH:mm',
        readonly: true,
        step: 15
    }).on('hidden.datetimepicker', function (evt, start) {
        $('.special-time .timerange input').removeClass('is-invalid');
        if ($(evt.target).attr('name') == 'downStartTime') {
            if ($('#taskModal input[name="downEndTime"]').val() == '') {
                $('#taskModal input[name="downEndTime"]').datetimepicker('setDate', new Date(start + 24 * 60 * 60 * 1000).getTime());
            } else {
                var end = new Date($('#taskModal input[name="downEndTime"]').val().replace(/-/g, '/')).getTime();
                if (new Date(start).getTime() >= end) {
                    $('#taskModal input[name="downEndTime"]').datetimepicker('setDate', new Date(start + 24 * 60 * 60 * 1000).getTime());
                }
            }
            $('#taskModal input[name="downEndTime"]').datetimepicker('setOption', {
                start: new Date(start).format('YYYY-MM-DD'),
                min: new Date(start).format('YYYY-MM-DD HH:mm')
            });
        } else {
            if ($('#taskModal input[name="downStartTime"]').val() == '') {
                $('#taskModal input[name="downStartTime"]').datetimepicker('setDate', new Date(start - 24 * 60 * 60 * 1000).getTime());
                $('#taskModal input[name="downEndTime"]').datetimepicker('setOption', {
                    start: new Date(start - 24 * 60 * 60 * 1000).format('YYYY-MM-DD'),
                    min: new Date(start - 24 * 60 * 60 * 1000).format('YYYY-MM-DD HH:mm')
                });
            }
        }
        tasks.downStartTime = $('#taskModal input[name="downStartTime"]').val() + ':00';
        tasks.downEndTime = $('#taskModal input[name="downEndTime"]').val() + ':00';
    })


    $('body').on('mouseenter', '.effect,.terminals .item', function () {
        var title = $(this).data('title');
        if (title){
            $(this).tooltip({
                title: title,
                placement: function (tip, element) {
                    return (window.scrollY+50 < $(element).offset().top) ? 'top':'bottom';
                },
                trigger: 'hover'
            })
            $(this).tooltip('toggle');
        }
    })
});