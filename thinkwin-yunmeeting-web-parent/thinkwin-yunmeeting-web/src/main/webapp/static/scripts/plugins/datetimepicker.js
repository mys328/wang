(function() {
    Date.prototype.countDaysInMonth = function () {
        return new Date(this.getFullYear(), this.getMonth() + 1, 0).getDate();
    };
    Date.prototype.format = function(string) {
        var o = {
            "M+": this.getMonth() + 1, //月份
            "D+": this.getDate(), //日
            "H+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        if(/(Y+)/.test(string)){
            string = string.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        }
        for (var k in o) {
            if (new RegExp("(" + k + ")").test(string)) {
                string = string.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)))
            }
        }
        return string;
    };
    var Datetimepicker = function(element, options) {
        $(element).data('datetimepicker', this);
        this.element = $(element);
        this.plugin = $('<div class="datetimepicker"><div class="calendars"><div class="header"></div><div class="starts"></div><div class="ends"></div></div><div class="times"><div class="header"></div><div class="starts"></div><div class="ends"></div></div></div>');
        this.calendars = this.plugin.find('.calendars');
        this.times = this.plugin.find('.times');
        this.shown = false;
        this.o = options;
        this.secondaryEvents = [];
        this.plugin.addClass(this.o.class);
        this.now = new Date();
        this.current = new Date(this.now.getFullYear(), this.now.getMonth(), this.now.getDate(), 0, 0, 0);
        if (this.element.is('input')) {

        }
        this.start = null;
        this.end = null;
        this.element.on('focus', $.proxy(function(evt){
            $(evt.target).blur();
            this.init();
        }, this));
    }
    Datetimepicker.prototype = {
        init: function() {
            this.plugin.appendTo(this.o.container);
            this.renderHeader();
            this.renderCalendars();
            this.renderTimes();
            this.scrollTime();
            this.bindEvent();
            this.place();
            this.update();
            this.attachSecondaryEvents();
            this.shown = true;
        },
        place: function() {
            var element = this.element,
                plugin = this.plugin,
                scrollTop =  this.o.container == 'body' ? 0 : $(this.o.container).scrollTop(),
                container = $(this.o.container),
                top = element.offset().top-container.offset().top + scrollTop,
                left = element.offset().left - container.offset().left;
            if (left + plugin.outerWidth() > container.outerWidth()) {
                left = element.offset().left - container.offset().left + element.outerWidth() - plugin.outerWidth();

            }
            if (top + plugin.outerHeight() > container.outerHeight()) {
                top = element.offset().top-container.offset().top + scrollTop + element.outerHeight() - plugin.outerHeight();
            }
            this.plugin.css({
                top: top,
                left: left
            });
        },
        renderHeader: function() {
            if (this.o.range) {
                this.calendars.find('.header').html('<input name="start" type="text" maxlength="10" placeholder="请选择日期"><span>-</span><input name="end" type="text" maxlength="10" placeholder="请选择日期"><button type="button" class="btn-today">今日</button><button type="button" class="btn-ensure">确定</button><button type="button" class="btn-clear">清空</button>');
                this.times.find('.header').html('<input type="text" maxlength="5" placeholder="请选择时间"><span class="duration"></span><span class="btn-clear">清除</span>')
            }else{
                this.calendars.find('.header').html('<input type="text" maxlength="10" placeholder="请选择日期"><button type="button" class="btn-today">今日</button>')
                this.times.find('.header').html('<input type="text" maxlength="5" placeholder="请选择时间">')
            }
            if (this.o.readonly) {
                // this.calendars.find('.header input').prop('readonly', true);
                this.times.find('.header input').prop('readonly', true);
            }
        },
        renderCalendars: function(){
            if (!this.o.datepicker) {
                this.calendars.hide();
                return this;
            }
            var calendars = {
                starts: '',
                ends: ''
            }

            calendars.starts = this.renderMonths(new Date(this.current.getFullYear(), this.current.getMonth(), 1, 0, 0, 0));
            if (this.current.getMonth() == 11) {
                calendars.ends = this.renderMonths(new Date(this.current.getFullYear()+1, 0, 1, 0, 0, 0))
            }else {
                calendars.ends = this.renderMonths(new Date(this.current.getFullYear(), this.current.getMonth()+1, 1, 0, 0, 0))
            }

            if (this.o.range && this.o.end) {
                calendars.ends = this.renderMonths(new Date(this.current.getFullYear(), this.current.getMonth(), 1, 0, 0, 0));
                if (this.current.getMonth() == 0) {
                    calendars.starts = this.renderMonths(new Date(this.current.getFullYear()-1, 11, 1, 0, 0, 0))
                }else {
                    calendars.starts = this.renderMonths(new Date(this.current.getFullYear(), this.current.getMonth()-1, 1, 0, 0, 0))
                }
            }

            this.calendars.find('.starts').html(calendars.starts);
            if (this.o.range) {
                this.calendars.find('.ends').html(calendars.ends);
                this.calendars.find('.starts .next').hide();
                this.calendars.find('.ends .prev').hide();
            }
            this.updateArrows();

        },
        renderMonths: function(start) {
            var i = 0;
            var table = '';
            var year = start.getFullYear();
            var month = start.getMonth();
            var options = this.o;
            if (start.getDay() === options.day) {
                start.setDate(start.getDate() - 7);
            }
            while (start.getDay() !== options.day) {
                start.setDate(start.getDate() - 1);
            }
            table += '<table><thead><tr>';
            table += '<th><span class="prev"><i class="icon icon-calendar-left"></i></span></th>';
            table += '<th colspan="5">'+year+'年'+(month+1)+'月</th>';
            table += '<th><span class="next"><i class="icon icon-calendar-right"></i></span></th>';
            table += '</tr><tr>'
            for (var j = 0; j < 7; j++) {
                table += '<th>'+ options.dates[options.language].days[(j+options.day)%7] + '</th>'
            }
            table += '</tr></thead>';
            table += '<tbody><tr>';
            while(i < 42) {
                i += 1;
                var className = [];
                if (i > 6 && start.getDay() === options.day) {
                    table += '</tr><tr>';
                }
                if (start.getMonth() < month) {
                    className.push('old')
                    if (options.range) {
                        className.push('disabled')
                    }
                }else if (start.getMonth() > month) {
                    className.push('new')
                    if (options.range) {
                        className.push('disabled')
                    }
                }else{
                    className.push('day');
                    if (this.start) {
                        if (start.toDateString() == this.start.toDateString()) {
                            className.push('active')
                        }
                    }
                    if (this.start && this.end) {
                        if (this.start.getTime() < start.getTime() && start.getTime() < this.end.getTime()) {
                            className.push('selected')
                        }
                        if (start.toDateString() == this.start.toDateString()) {
                            if (options.range) {
                                className.push('first')
                            }
                        }
                        if (start.toDateString() == this.end.toDateString()) {
                            if (options.range) {
                                className.push('active')
                                className.push('last')
                            }
                        }
                    }
                    if (start.toDateString() == this.now.toDateString()) {
                        className.push('today')
                    }
                    if (start.getTime() < this.parseDate(this.o.start).getTime()){
                        className.push('disabled')
                    }
                    if (start.getTime() > this.parseDate(this.o.end).getTime()){
                        className.push('disabled')
                    }

                }

                if (start.getTime() < this.parseDate(options.start).getTime()) {
                    className.push('disabled')
                }
                table += '<td class="'+ className.join(' ') +'" data-date="'+start.getTime()+'"><span>'+ start.getDate() +'</span></td>';

                start.setDate(start.getDate() + 1);
            }

            table += '</tbody></table>';

            return table;
        },
        renderTimes: function() {
            if (!this.o.timepicker) {
                this.times.hide()
                return this;
            }
            var self = this,i = 0,index = 0,h,m,
                options = this.o,
                timestamp = new Date(this.current.getFullYear(), this.current.getMonth(), this.current.getDate(), 0, 0, 0).getTime(),
                loops = (24*60)/options.step;
            var times = {
                starts: [],
                ends: []
            };
            if (this.o.range) {
                times.starts.push('<label><input name="starts" type="radio" value="0" disabled/><span>开始</span></label>')
                times.ends.push('<label><input name="ends" type="radio" value="0" disabled/><span>结束</span></label>')
            }
            while (i < loops) {
                var checked = {
                    start: false,
                    end: false
                }
                if (this.start) {
                    checked.start = timestamp == this.start.getTime()
                }
                if (this.end) {
                    checked.end = timestamp == this.end.getTime()
                }
                var disabled = {
                    start: false,
                    end: false
                }

                if (options.min) {
                    disabled.start = timestamp <= this.parseDate(options.min).getTime();
                    disabled.end = timestamp <= this.parseDate(options.min).getTime() + options.step*60*1000;
                }
                if (this.start) {
                    disabled.end = timestamp <= this.start.getTime();
                }

                if (i == 0) {
                    times.starts.push(self.renderItem('starts', timestamp, checked.start, disabled.start));
                }else if (i == loops-1) {
                    if (options.range) {
                        times.ends.push(self.renderItem('ends', timestamp, checked.end, disabled.end));
                    }else{
                        times.starts.push(self.renderItem('starts', timestamp, checked.start, disabled.start));
                    }
                }else{
                    if (options.range) {
                        times.ends.push(self.renderItem('ends', timestamp, checked.end, disabled.end));
                    }
                    times.starts.push(self.renderItem('starts', timestamp, checked.start, disabled.start));
                }
                timestamp += options.step*60*1000;
                i++;
            }
            this.times.find('.starts').html(times.starts.join(''));
            if (options.range) {
                this.times.find('.ends').html(times.ends.join(''));
            }

        },
        renderItem: function(type, timestamp, checked, disabled) {
            return '<label><input name="'+type+'" type="radio" value="'+timestamp+'" '+(checked ? 'checked': disabled ? 'disabled':'')+'/><span>'+new Date(timestamp).format('HH:mm')+'</span><span class="icon icon-tick"></span></label>'
        },
        scrollTime: function(){
            if (this.start) {

            }
            var times = this.now.format('HH:mm').split(':');
            var minutes = parseInt(times[0])*60 + parseInt(times[1])
            var height = this.times.find('.starts label').outerHeight();
            this.times.find('.ends').scrollTop(height*(Math.ceil(minutes/this.o.step)));
            this.times.find('.starts').scrollTop(height*(Math.ceil(minutes/this.o.step)));
        },
        updateArrows: function(){
            var start = this.o.start;
            var end = this.o.end;
            if (start && this.current.getFullYear() <= this.parseDate(start).getFullYear() && this.current.getMonth() <= this.parseDate(start).getMonth()){
                this.calendars.find('.prev').css({visibility: 'hidden'});
            }

            if (end && this.current.getFullYear() >= this.parseDate(end).getFullYear() && this.current.getMonth() >= this.parseDate(end).getMonth()) {
                this.calendars.find('.next').css({visibility: 'hidden'});
            }
        },
        applyEvents: function(evs){
            for (var i=0, el, ch, ev; i < evs.length; i++){
                el = evs[i][0];
                if (evs[i].length === 2){
                    ch = undefined;
                    ev = evs[i][1];
                }
                else if (evs[i].length === 3){
                    ch = evs[i][1];
                    ev = evs[i][2];
                }
                el.on(ev, ch);
            }
        },
        unapplyEvents: function(evs){
            for (var i=0, el, ev, ch; i < evs.length; i++){
                el = evs[i][0];
                if (evs[i].length === 2){
                    ch = undefined;
                    ev = evs[i][1];
                }
                else if (evs[i].length === 3){
                    ch = evs[i][1];
                    ev = evs[i][2];
                }
                el.off(ev, ch);
            }
        },
        bindEvent: function() {
            var self = this;
            // 上一个月
            this.calendars.off().on('click', '.prev', function(){
                self.prev()
            })
            // 下一个月
            this.calendars.on('click', '.next', function(evt){
                self.next()
            })
            this.calendars.on('blur', '.header input', function(evt){
                // if(self.o.readonly){
                //     return false;
                // }
                var reg = new RegExp(/^[1-9]\d{3}-([1-9]|0[1-9]|1[0-2])-([1-9]|0[1-9]|[1-2][0-9]|3[0-1])$/);
                var name = $(this).attr('name');

                if(!reg.test(this.value)){
                    if (name && name == 'end') {
                        if (this.end) {
                            this.value = self.end.format('YYYY-MM-DD')
                            this.value = ''
                        }else {
                            this.value = ''
                        }
                    }else {
                        if (this.start) {
                            this.value = self.start.format('YYYY-MM-DD')
                        }else {
                            this.value = ''
                        }
                    }
                } else {
                    var date = new Date(this.value).getTime();
                    if (name && name == 'start') {
                        if (self.end) {
                            if (date > self.end.getTime()) {
                                self.setDate(self.start.getTime(), self.end.getTime())
                            }else {
                                self.setDate(date, self.end.getTime())
                            }
                        }else {
                            self.setDate(date)
                        }
                    }else if (name && name == 'end') {
                        if (self.start) {
                            if (date < self.start.getTime()){
                                self.setDate(self.start.getTime(),self.end.getTime())
                            }else {
                                self.setDate(self.start.getTime(),date)
                            }
                        }else {
                            self.setDate(null,date)
                        }
                    }else {
                        if (new Date(this.value).getMonth() === parseInt(this.value.split('-')[1])) {
                            this.value = self.start.format('YYYY-MM-DD')
                        }else{
                            self.setDate(this.value)
                        }
                    }

                }
            }).on('keypress', function(evt){
                // if(self.o.readonly){
                //     return false;
                // }
                if(evt.keyCode == '13') {
                    $(evt.target).blur();
                }
                return (evt.keyCode>=48 && evt.keyCode<=57) || evt.keyCode == 45;
            })
            // 今天
            this.calendars.on('click', '.btn-today', function(evt){
                self.current = new Date(self.now.getFullYear(), self.now.getMonth(), 1, 0, 0, 0);
                if(self.o.range) {
                    self.setDate(new Date().setHours(0,0,0,0), new Date().setHours(0,0,0,0))
                }else {
                    var timestamp = Math.ceil(new Date().getTime()/(self.o.step*60*1000))*self.o.step*60*1000;
                    self.setDate(timestamp, timestamp);
                }

            })
            // 选择日期
            this.calendars.on('click', 'td', function(){
                self.select($(this))
            })
            // 清除
            this.calendars.on('click', '.btn-clear', function(evt){
                self.clear()
            })
            // 取消
            this.calendars.on('click', '.btn-cancel', function(){
                self.hide();
            })

            // 确定
            this.calendars.on('click', '.btn-ensure', function(evt){
                if (self.start && self.end) {
                    if (self.o.ensure !== $.noop) {
                        self.o.ensure(self.start, self.end)
                        self.element.val(self.start.format(self.o.format)+'~'+self.end.format(self.o.format))
                    }
                }
                self.hide()
            })

            // 选择时间
            this.times.off().on('click', 'input:radio', function(evt){
                var date = new Date(parseInt(this.value));
                if ($(this).attr('name') == 'starts') {
                    if (self.start) {
                        self.start.setHours(date.getHours(),date.getMinutes(),0)
                    }else{
                        self.start = date
                    }
                    if (self.o.range) {
                        if (self.end) {
                            if (self.start.getTime() >= self.end.getTime()) {
                                self.end = new Date(self.start.getTime() + self.o.step*60*1000)
                            }
                        }else{
                            self.end = new Date(self.start.getTime() + self.o.step*60*1000)
                        }
                    }
                }else{
                    if (self.end) {
                        self.end.setHours(date.getHours(),date.getMinutes(),0)
                    }else{
                        self.end = date
                    }
                    if (self.o.range) {
                        if (!self.start) {
                            self.start = new Date(self.end.getTime() - self.o.step*60*1000)
                        }
                    }
                }
                self.update();
            })
            this.times.on('blur', '.header input', function(evt){
                if(self.o.readonly){
                    return false;
                }
                var reg = new RegExp(/^(20|21|22|23|[1-9]|[0-1]\d):([1-9]|[0-5]\d)$/);
                if(!reg.test(this.value)){
                    this.value = self.start.format('HH:mm');
                } else {
                    self.setDate(this.value)
                }
            }).on('keypress', function(evt){
                if(self.o.readonly){
                    return false;
                }
                if(evt.keyCode == '13') {
                    $(evt.target).blur();
                }
                return evt.keyCode>=48 && evt.keyCode<=58;
            })
            // 选择日期
            this.times.on('click', '.btn-clear', function(evt){
                self.clear()
            })



            this.secondaryEvents = [
                [this.plugin, {
                    click: $.proxy(this.click, this)
                }],
                [$(window), {
                    resize: $.proxy(this.place, this)
                }],
                [$(document), {
                    mousedown: $.proxy(function(e){
                        var outside = !(this.element.is(e.target) || this.element.find(e.target).length || this.plugin.is(e.target) ||
                        this.plugin.find(e.target).length);
                        if(this.shown && outside){
                            this.hide();
                        }
                    }, this)
                }]
            ];
        },
        attachSecondaryEvents: function(){
            this.detachSecondaryEvents();
            this.applyEvents(this.secondaryEvents);
        },
        detachSecondaryEvents: function(){
            this.unapplyEvents(this.secondaryEvents);
        },
        prev: function() {
            var month = this.current.getMonth() - 1;
            if (month == -1) {
                this.current.setFullYear(this.current.getFullYear() - 1);
                month = 11;
            }
            this.current.setMonth(month);
            this.renderCalendars()
        },
        next: function() {
            var month = this.current.getMonth() + 1;
            if (month == 12) {
                this.current.setFullYear(this.current.getFullYear() + 1);
                month = 0;
            }
            this.current.setMonth(month);
            this.renderCalendars()
        },
        select: function(target) {
            if (!target.hasClass('disabled')) {
                var timestamp = parseInt(target.data('date'));
                if (target.hasClass('day')){
                    if (this.o.range) {
                        if (this.start && this.end){
                            if (this.start.getTime() == this.end.getTime()){
                                if (timestamp <= this.start.getTime()) {
                                    this.setDate(timestamp, this.start.getTime());
                                }else{
                                    this.setDate(this.start.getTime(), timestamp);
                                }
                            }else {
                                this.setDate(timestamp, timestamp);
                            }
                        }
                        if (this.start && !this.end) {
                            if (timestamp <= this.start.getTime()) {
                                this.setDate(timestamp, this.start.getTime());
                            }else{
                                this.setDate(this.start.getTime(), timestamp);
                            }
                        }
                        if (!this.start){
                            this.setDate(timestamp, null);
                        }
                    }else{
                        this.setDate(timestamp, null);
                    }
                }
                if (target.hasClass('old') || target.hasClass('new')) {
                    this.setDate(timestamp, null);
                }
            }
        },
        hide: function() {
            this.plugin.remove();
            this.detachSecondaryEvents();
            this.shown = false;
            this.trigger('hidden');
            return this;
        },
        clear: function(){
            this.start = null;
            this.end = null;
            this.calendars.find('.header input').val('');
            this.times.find('.header input').val('');
            this.times.find('span.duration').text('');
            if (this.element.is('input')) {
                this.element.val('');
            }else {
                this.element.find('span.value').addClass('none').text('请选择时间');
                this.element.find('input').eq(0).val('');
                this.element.find('input').eq(1).val('');
            }
            this.renderCalendars();
            this.renderTimes();
            this.trigger('clear');
        },
        parseDate: function(string) {
            var date = new Date();
            if (!isNaN(Number(string))) {
                date = new Date(parseInt(string));
            }else{
                string = string.replace(/-/g, '/');
                date = new Date(string);
            }
            return date;
        },
        setOption: function(opts){
            this.o = $.extend({}, this.o, opts);
            this.update();
        },
        update: function() {
            if (this.start) {
                if (!this.o.range) {
                    this.current = new Date(this.start.getFullYear(), this.start.getMonth(), this.start.getDate(), 0, 0, 0);
                }
                this.times.find('.header input').eq(0).val(this.start.format('HH:mm'))
                this.calendars.find('.header input').eq(0).val(this.start.format('YYYY-MM-DD'));
                if (this.o.ensure == $.noop) {
                    this.element.val(this.start.format(this.o.format))
                }
            }
            if (this.end) {
                this.times.find('.header input').eq(1).val(this.end.format('HH:mm'))
                this.calendars.find('.header input').eq(1).val(this.end.format('YYYY-MM-DD'));
            }
            if (this.start && this.end) {
                if (this.o.range) {
                    var duration = this.end.getTime()-this.start.getTime();
                    var hours = Math.floor(duration / (60 * 60 * 1000));
                    var minutes = Math.floor((duration - hours * (60 * 60 * 1000)) / (60 * 1000));
                    duration = (hours > 0 && minutes > 0) ? hours + '小时' + minutes + '分钟' : (hours > 0) ? hours + '小时' : minutes + '分钟';
                    this.times.find('.header input').val(this.start.format('HH:mm')+'~'+this.end.format('HH:mm'))
                    this.times.find('.header .duration').text('('+duration+')');
                    if (this.o.ensure == $.noop) {
                        this.element.val(this.start.format(this.o.format)+'~'+this.end.format(this.o.format))
                    }
                }
            }
            this.renderCalendars()
            this.renderTimes()
        },
        change: function() {
            if(this.o.change !== $.noop){
                this.o.change.call(this, this.start, this.end)
            }
        },
        setDate: function(start, end){
            if (start == null) {
                this.start = null;
            }else{
                var date = this.middled(start);

                if (this.start) {
                    this.start.setFullYear(date.getFullYear(), date.getMonth(), date.getDate())
                    if (!(date.getHours() == 0 && date.getMinutes() ==0)){
                        this.start.setHours(date.getHours(), date.getMinutes(), date.getSeconds())
                    }
                }else{
                    this.start = date
                }
            }
            if (end == null) {
                this.end = null
            }else{
                var date = this.middled(end);
                if (this.end) {
                    this.end.setFullYear(date.getFullYear(), date.getMonth(), date.getDate())
                }else{
                    this.end = date
                }
            }
            this.update();
        },
        setTime: function (start, end) {
            if (start == null) {
                this.start = null;
            }else{
                var date = new Date(this.current.getTime()).setHours(start.split(':')[0],start.split(':')[1],0);
                date = new Date(date);
                var min = this.parseDate(this.o.min).getTime();
                var start = this.parseDate(this.o.start).getTime();
                if (date.getTime() < start ) {
                    date = new Date(start);
                }
                if (date.getTime() < min) {
                    var num = min % (this.o.step*60*1000);
                    if (num == 0) {
                        date = new Date(min + this.o.step*60*1000);
                    }else{
                        date = new Date(min - num + this.o.step*60*1000);
                    }
                }
                if (this.start) {
                    this.start.setHours(date.getHours(), date.getMinutes(), date.getSeconds())
                }else{
                    this.start = date
                }
            }

            if (start == end) {
                this.start = null;
            }else{
                var date = new Date(this.current.getTime()).setHours(end.split(':')[0],end.split(':')[1],0);
                date = new Date(date);
                if (this.end) {
                    this.end.setHours(date.getHours(), date.getMinutes(), date.getSeconds())
                }else{
                    this.end = date
                }
            }
            this.update();
        },
        getDate: function () {
            return {
                start: this.start,
                end: this.end
            }
        },
        middled: function (time) {
            var date = this.parseDate(time);
            var min = this.parseDate(this.o.min).getTime();
            var max = this.parseDate(this.o.max).getTime();
            var start = this.parseDate(this.o.start).getTime();
            var end = this.parseDate(this.o.end).getTime();
            if (this.o.start && date.getTime() < start ) {
                date = new Date(start);
            }
            if (this.o.end && date.getTime() > end ) {
                date = new Date(end);
            }
            if (this.o.min && date.getTime() <= min) {
                var num = min % (this.o.step*60*1000);
                if (num == 0) {
                    date = new Date(min + this.o.step*60*1000);
                }else{
                    date = new Date(min - num + this.o.step*60*1000);
                }
            }
            if (this.o.max && date.getTime() >= max) {
                var num = max % (this.o.step*60*1000);
                if (num == 0) {
                    date = new Date(max - this.o.step*60*1000);
                }else{
                    date = new Date(max - num - this.o.step*60*1000);
                }
            }
            return date;
        },
        trigger: function(event) {
            if (event == 'clear') {
                this.element.trigger(event+'.datetimepicker');
            }else {
                if (this.o.range) {
                    if (this.start && this.end){
                        this.element.trigger(event+'.datetimepicker', [this.start.getTime(), this.end.getTime()])
                    }
                }else {
                    if (this.start) {
                        this.element.trigger(event+'.datetimepicker', [this.start.getTime()])
                    }
                }
            }
        },
        destroy: function(){
            this.plugin.hide().remove();
            delete this.element.data().datetimepicker;
            return this;
        }
    }

    var Plugin = function(option) {
        var args = Array.apply(null, arguments);
        args.shift();
        var internal_return;
        this.each(function(){
            var $this = $(this),
                data = $this.data('datetimepicker'),
                options = typeof option === 'object' && option;
            if (!data){
                var opts = $.extend({}, $.fn.datetimepicker.defaults, options);
                data = new Datetimepicker(this, opts);
                $this.data('datetimepicker', data);
            }
            if (typeof option === 'string' && typeof data[option] === 'function'){
                internal_return = data[option].apply(data, args);
            }
        })
        if (internal_return === undefined ||internal_return instanceof Datetimepicker) return this;
        if (this.length > 1)
            throw new Error('Using only allowed for the collection of a single element (' + option + ' function)');
        else
            return internal_return;
    }
    $.fn.datetimepicker = Plugin;
    $.fn.datetimepicker.defaults = {
        class: '',
        format: 'YYYY-MM-DD H:mm',
        start: null,
        end: null,
        min: null,
        max: null,
        day: 1,
        readonly: false,
        container: 'body',
        datepicker: true,
        timepicker: true,
        range: false,
        step: 60,
        until: '~',
        language: 'zh-cn',
        dates: {
            'zh-cn': {
                days: ['日','一','二','三','四','五','六'],
                months: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
                today: 'Today',
                clear: 'Clear',
                title: 'MM yyyy'
            }
        },
        clear: false,
        ensure: $.noop,
        change: $.noop
    }
    $(document).on('click', '[data-toggle="datetimepicker"]', function(e) {
        var $this = $(this);
        if ($this.data('datetimepicker'))
            return;
        e.preventDefault();
        // component click requires us to explicitly show it
        Plugin.call($this, 'init');
    })
})();