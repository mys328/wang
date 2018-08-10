'use strict';

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

(function () {
    var Boardroom = function Boardroom(element, options) {
        $(element).data('boardroom', this);
        this.element = $(element);
        this.boardroom = $('<div class="boardroom"><div class="wrapper"><div class="header"><label class="checkbox"><input name="filter" type="checkbox" value="004"><span class="icon">扩音</span></label><label class="checkbox"><input name="filter" type="checkbox" value="003"><span class="icon">显示</span></label><label class="checkbox"><input name="filter" type="checkbox" value="001"><span class="icon">白板</span></label><label class="checkbox"><input name="filter" type="checkbox" value="002"><span class="icon">视频会议</span></label></div><div class="results"></div></div><div class="occupy-info"></div></div>');
        this.shown = false;
        this.room = {};
        this.params = {
            deviceService: '',
            staTime: '',
            endTime: ''
        };
        this.o = options;
        var self = this;
        this.element.on('click', function (event) {
            self.init();
        });
    };
    Boardroom.prototype = {
        init: function init() {
            if (this.element.find('input').val() != '') {
                this.room = {
                    id: this.element.find('input').val()
                };
            }
            var timerange = this.element.parents('form').find('#timerange');
            if (timerange.length > 0) {
                this.params.staTime = moment(parseInt(timerange.children('input[name="start"]').val())).format('YYYY-MM-DD H:mm');
                this.params.endTime = moment(parseInt(timerange.children('input[name="end"]').val())).format('YYYY-MM-DD H:mm');
            }
            if ($('.detail').length > 0) {
                this.o.container = '.detail .wrap';
            }
            this.boardroom.appendTo(this.o.container);
            this.boardroom.find('.occupy-info').html('');
            this.renderData();
            this.bindEvent();
            this.place();
            this.shown = true;
            this._trigger('shown.bs.boardroom');
        },
        place: function place() {
            var element = this.element.offset(),
                scrollTop =  this.o.container == 'body' ? 0 : $(this.o.container).scrollTop(),
                container = $(this.o.container).offset();
            this.boardroom.css({
                top: element.top - container.top + scrollTop,
                left: element.left - container.left
            });
        },
        renderData: function renderData() {
            var self = this;
            fetchs.post('/meetingRoom/selectScreenAllMeetingRoom', self.params, function (res) {
                self.data = res.data;
                if (res.ifSuc == 1) {
                    var tpl = '\n          <label ng-repeat="item in data">\n            <input ng-if="item.isOccupy==1 && item.id == rid" name="boardroom" type="radio" value="{{item.id}}" checked disabled/>\n            <input ng-if="item.isOccupy==1 && item.id != rid" name="boardroom" type="radio" value="{{item.id}}" disabled/>\n            <input ng-if="item.isOccupy!=1 && item.id == rid" name="boardroom" type="radio" value="{{item.id}}" checked/>\n            <input ng-if="item.isOccupy!=1 && item.id != rid" name="boardroom" type="radio" value="{{item.id}}"/>\n            <span ng-if="item.isOccupy==0" class="icon icon-tick"></span>\n            <span ng-if="item.isOccupy==1" class="occupy">\u5360\u7528</span>\n        <div>   <span class="name">{{item.name}}</span><i class="auditIcon" ng-if="item.isAudit==1">需审核</i></div> \n            <span><i class="icon icon-xq-number"></i>{{item.persionNumber}}</span>\n            <span><i class="icon icon-room-address"></i>{{item.location}}</span>\n          </label>\n          <div ng-if="data.length<1" class="nothing">\u6CA1\u6709\u7B26\u5408\u6761\u4EF6\u7684\u4F1A\u8BAE\u5BA4</div>';
                    self.boardroom.find('.wrapper .results').html(soda(tpl, { data: res.data, rid: self.room.id }));
                }
            });
        },
        bindEvent: function bindEvent() {
            var self = this;
            // 选择会议室
            this.boardroom.on('click', 'input:radio', function (event) {
                    event.stopPropagation();
                    var name = $(this).parent('label').children('div').children('span.name').text().replace('需审核','');
                    var id = $(this).val();
                    self.room = {
                        id: id,
                        name: name
                    };
                    self.change();
                }

                // 会议室筛选
            );this.boardroom.on('click', '.header input', function (event) {
                    event.stopPropagation();
                    var filters = [];
                    self.boardroom.find('input:checkbox:checked').each(function () {
                        filters.push($(this).val());
                    });
                    self.params.deviceService = filters.join(',');
                    self.renderData();
                }

                //占用点击事件
            );this.boardroom.on('click', 'label', function (event) {
                if ($(this).children('input:disabled').length > 0) {
                    var index = $(this).index();
                    var tpl = '\n          <div class="header">\n            {{name}}\n            <span>{{roomVos.length}}\u4E2A\u5360\u7528</span>\n          </div>\n          <ul class="results">\n            <li ng-repeat="item in roomVos">\n              <span style="max-width: 100%;"><i class="icon icon-xq-time"></i>{{item.staDate|date:\'HH:mm\'}}~{{item.endDate|date:\'HH:mm\'}}</span>\n              <span><i class="icon icon-xq-organizer"></i>{{item.personName}}</span>\n            </li>\n          </ul>';
                    self.boardroom.find('.occupy-info').show().html(soda(tpl, self.data[index]));
                }else {
                    self.boardroom.find('.occupy-info').hide()
                }
            });

            this.boardroom.on('click', function (event) {
                event.stopPropagation();
            });

            $(window).on('resize', function () {
                self.place();
            });
            $('body').on('click', function (e) {
                if (self.shown && self.element.find(e.target).length < 1) {
                    self.boardroom.remove();
                    self.shown = false;
                    self._trigger('hidden.bs.boardroom');
                }
            });
        },
        update: function update(date) {
            this.o.date = date;
            this.renderData();
        },
        change: function change() {
            this.element.find('span.value').removeClass('none').text(this.room.name);
            this.element.find('input:hidden').val(this.room.id);
            this._trigger('change.bs.boardroom');
        },
        _trigger: function _trigger(event, date) {
            this.element.trigger({
                type: event,
                room: this.room
            });
        }
    };
    var boardroomPlugin = function boardroomPlugin(option) {
        var args = Array.apply(null, arguments);
        args.shift();
        var internal_return;
        this.each(function () {
            var $this = $(this),
                data = $this.data('boardroom'),
                options = (typeof option === 'undefined' ? 'undefined' : _typeof(option)) === 'object' && option;
            if (!data) {
                var opts = $.extend({}, defaults, options);
                data = new Boardroom(this, opts);
                $this.data('boardroom', data);
            }
            if (typeof option === 'string' && typeof data[option] === 'function') {
                internal_return = data[option].apply(data, args);
            }
        });
        if (internal_return === undefined || internal_return instanceof Boardroom) return this;
        if (this.length > 1) throw new Error('Using only allowed for the collection of a single element (' + option + ' function)');else return internal_return;
    };
    $.fn.boardroom = boardroomPlugin;
    var defaults = $.fn.boardroom.defaults = {
        start: '9:00',
        end: '21:00',
        container: 'body',
        timestamp: false,
        interval: 15 * 60 * 1000,
        date: false
    };

    $(document).on('click', '[data-toggle="boardroom"]', function (e) {
        var $this = $(this);
        if ($this.data('boardroom')) return;
        e.preventDefault();
        // component click requires us to explicitly show it
        boardroomPlugin.call($this, 'init');
    });
})();
