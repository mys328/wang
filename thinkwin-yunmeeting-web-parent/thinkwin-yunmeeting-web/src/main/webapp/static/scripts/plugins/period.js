(function() {
    'use strict';
    var Period = function(element, options) {
        this.element = $(element);
        this.plugin = $('<div class="period-form"><div class="title">设置运行时段<span class="icon icon-close"></span></div><div class="content"></div><div class="footer"><span class="icon icon-error"></span><button class="btn btn-lg btn-primary btn-save-period">保存</button></div></div>');
        this.o = options;
        this.shown = false;
        this.added = this.o.added;
        this.period = {
            weekly: [],
            times: []
        }
        this.index = null;
        this.selected = '';
        this.weekly = [];
        this.o.class !== '' && this.plugin.addClass(this.o.class);
        this.bindEvents();
    }
    Period.prototype = {
        init: function(e) {
            // body...
        },
        show: function(e) {
            var self = this;
            this.plugin.appendTo(this.o.container);
            this.place();
            if($(e.target).hasClass('btn-add-period')){
                this.plugin.show();
                this.render();
                // 设置运行时段时间选择
                this.plugin.find('input[type="text"]').datetimepicker({
                    class: 'period-timepicker',
                    readonly: true,
                    container: '#taskModal .modal-body',
                    format: 'HH:mm',
                    datepicker: false,
                    step: 15,
                    range: true
                }).on('hidden.datetimepicker', function(evt, start, end){
                    self.plugin.find('.icon-error').text('').hide();
                    self.plugin.find('input[name="first"]').removeClass('is-invalid');
                    if ($(evt.target).attr('name') == 'first') {
                        if (new Date(end).getHours() < 23 || (new Date(end).getHours() == 23 && new Date(end).getMinutes() < 30)) {
                            var seconds = self.plugin.find('input[name="second"]').datetimepicker('getDate');
                            if (seconds.start && seconds.end && new Date(end).getTime() >= seconds.start.getTime()) {
                                self.plugin.find('input[name="second"]').datetimepicker('setTime',new Date(end+15*60*1000).format('HH:mm'),new Date(end+2*15*60*1000).format('HH:mm'))
                            }
                            self.plugin.find('input[name="second"]').datetimepicker('setOption', {
                                start: new Date(end).format('YYYY-MM-DD'),
                                min: new Date(end).format('YYYY-MM-DD HH:mm')
                            })
                            self.plugin.find('input[name="second"]').attr('placeholder', '点击选择时间').prop('disabled', false);
                        }else{
                            self.plugin.find('input[name="second"]').val('').attr('placeholder', '').prop('disabled', true);
                        }
                    }
                    self.tips();
                }).on('clear.datetimepicker', function(evt){
                    if ($(evt.target).attr('name') == 'first'){
                        self.plugin.find('input[name="second"]').datetimepicker('setDate', null, null);
                        self.plugin.find('input[name="second"]').datetimepicker('setOption', {
                            start: null,
                            min: null
                        });
                        self.plugin.find('input[name="second"]').val('').attr('placeholder', '').prop('disabled', true);
                    }
                    self.tips();
                });
                this.shown = true;
            }
            if ($(e.target).hasClass('btn-del-period')) {
                var index = $(e.target).parents('li').index();
                this.delete(index);
            }

            if ($(e.target).hasClass('btn-edit-period')) {
                this.index = $(e.target).parents('li').index();
                this.edit();
            }
            this.trigger('shown');
            return this;
        },
        hide: function(){
            this.plugin.hide();
            this.index = null;
            this.period = {
                weekly: [],
                times: []
            }
            this.shown = false;
            this.trigger('hidden');
            return this;
        },
        place: function() {
            var element = this.element,
                period = this.plugin,
                container = $(this.o.container),
                scrollTop =  scrollTop = this.o.container === 'body' ? $(document).scrollTop() : container.scrollTop(),
                offset = this.element.offset(),
                height = this.element.innerHeight(),
                width = this.element.innerWidth(),
                top = offset.top - container.offset().top ,
                left = offset.left - container.offset().left + 87;
            if (this.o.container !== 'body') {
                top += scrollTop;
            }
            if (offset.left < 0) {
                left -= offset.left;
            } else if (left + this.plugin.outerWidth() > container.width()) {

                left += width - this.plugin.outerWidth();
            } else {
                // Default to left
            }
            this.plugin.css({
                top: top,
                left: left
            });
            return this;
        },
        renderAdded: function() {
            var html = [];
            var selected = ''
            for (var i = 0; i < this.added.length; i++) {
                if (i == 0){
                    selected += this.added[i].weekly;
                } else {
                    selected += ','+this.added[i].weekly;
                }
                var item = '<li><p>';
                for (var j = 0; j < this.added[i].times.length; j++) {
                    item += '<b>'+this.added[i].times[j].startTime+'~'+this.added[i].times[j].endTime+'</b>'
                }
                item += '</p>';
                item += '<p>'+this.weekesc(this.added[i].weekly)+'</p>';
                item += '<p class="actions"><button class="btn btn-clear-primary btn-edit-period">修改</button><button class="btn btn-clear-primary btn-del-period">删除</button></p>'
                html.push(item)
            }
            this.selected = selected;
            if (this.selected.split(',').length == 7){
                this.element.find('.btn-add-period').hide()
            }
            if (html.length == 0) {
                this.element.find('.periods').html('<p>未添加运行时段，将默认全天开机，不添加时段也可以开启特别关机时段</p>')
            }else{
                this.element.find('.periods').html(html.join(''))
            }
        },
        render: function() {
            var html = [];
            html.push('<span>每周</span>');
            for (var i = 1; i < 8; i++) {
                var val = i==7 ? 0:i;
                var disabled = false;
                var checked = false;
                if (this.selected.indexOf(val.toString()) >= 0) {
                    disabled = true;
                }
                if (this.period.weekly.indexOf(val.toString()) >= 0) {
                    disabled = false;
                    checked = true;
                }
                if (this.selected == '' && i < 6) {
                    checked = true;
                    this.period.weekly = [1,2,3,4,5];
                }
                html.push('<label><input name="weekly" type="checkbox" value="'+val+'" '+(disabled ? 'disabled':'')+' '+(checked ? 'checked':'')+'><span>'+this.o.weekly[val]+'</span></label>')
            }
            this.plugin.find('.content').html('<div class="weeklys">'+html.join('')+'</div><input class="form-control" name="first" type="text" value="" placeholder="点击选择时间"><input class="form-control" name="second" type="text" value="" disabled><p class="tips"></p>');
            this.tips();
        },
        findIndex: function(array, id) {
            var index = -1;
            for (var i = 0; i < array.length; i++) {
                if (array[i].id == id) {
                    index = i;
                    break;
                }
            }
            return index;
        },

        bindEvents: function() {
            this.element.on('click', $.proxy(this.show, this));
            this.plugin.on('click', $.proxy(this.click, this));

            $(window).on('resize', $.proxy(this.place, this));

            $(document).on('mousedown', $.proxy(function(e) {
                if(this.shown && $(e.target).hasClass('icon-close') && this.plugin.find(e.target).length){

                    this.hide();
                }
            }, this))
        },
        click: function(e) {
            e.stopPropagation();
            if($(e.target).is('input[name="weekly"]')) {
                this.plugin.find('.icon-error').text('').hide();
                var weekly = [];
                this.plugin.find('input[name="weekly"]:checked').not(':disabled').each(function(){
                    weekly.push($(this).val())
                })
                this.period.weekly = weekly;
                this.tips()
            }
            if ($(e.target).hasClass('btn-save-period')) {
                this.add();
            }
        },
        add: function() {
            if (this.period.weekly.length == 0) {
                this.plugin.find('.icon-error').text('请选择每周几执行任务').show();
                return false
            }
            if (this.plugin.find('input[name="first"]').val() == '' && this.plugin.find('input[name="second"]').val() == '') {
                this.plugin.find('.icon-error').text('请选择至少一个时段').show();
                this.plugin.find('input[name="first"]').addClass('is-invalid');
                return false;
            }
            if (this.plugin.find('input[name="first"]').val() != '') {
                var first = this.plugin.find('input[name="first"]').val().split('~');
                this.period.times[0] = {
                    startTime: first[0],
                    endTime: first[1]
                }
                if (this.plugin.find('input[name="second"]').val() != '') {
                    var second = this.plugin.find('input[name="second"]').val().split('~');
                    this.period.times[1] = {
                        startTime: second[0],
                        endTime: second[1]
                    }
                }else {
                    this.period.times.splice(1, 1);
                }
            }else {
                if (this.plugin.find('input[name="second"]').val() != '') {
                    var second = this.plugin.find('input[name="second"]').val().split('~');
                    this.period.times[0] = {
                        startTime: second[0],
                        endTime: second[1]
                    }
                }else {
                    this.period.times.splice(1, 1);
                }
            }
            this.period.weekly = this.period.weekly.join(',');
            if (this.index == null) {
                this.added.push(this.period);
            }else {
                this.added[this.index] = this.period
            }
            this.added.sort(this.byweekly)
            this.change();
            this.hide();
            this.period = {
                weekly: [],
                times: []
            }
            this.index = null;
        },
        edit: function() {
            var self = this;
            this.period = {
                weekly: this.added[this.index].weekly.split(','),
                times: this.added[this.index].times
            };
            this.plugin.show();
            this.shown = true;
            this.render();

            // 设置运行时段时间选择
            this.plugin.find('input[type="text"]').datetimepicker({
                class: 'period-timepicker',
                readonly: true,
                container: '#taskModal .modal-body',
                format: 'HH:mm',
                datepicker: false,
                step: 15,
                range: true
            }).on('hidden.datetimepicker', function(evt, start, end){
                self.plugin.find('.icon-error').text('').hide();
                self.plugin.find('input[name="first"]').removeClass('is-invalid');
                if ($(evt.target).attr('name') == 'first') {
                    if (new Date(end).getHours() < 23 || (new Date(end).getHours() == 23 && new Date(end).getMinutes() < 30)) {
                        var seconds = self.plugin.find('input[name="second"]').datetimepicker('getDate');
                        if (seconds.start && seconds.end && new Date(end).getTime() >= seconds.start.getTime()) {
                            self.plugin.find('input[name="second"]').datetimepicker('setTime',new Date(end+15*60*1000).format('HH:mm'),new Date(end+2*15*60*1000).format('HH:mm'))
                        }
                        self.plugin.find('input[name="second"]').datetimepicker('setOption', {
                            start: new Date(end).format('YYYY-MM-DD'),
                            min: new Date(end).format('YYYY-MM-DD HH:mm')
                        })
                        self.plugin.find('input[name="second"]').attr('placeholder', '点击选择时间').prop('disabled', false);
                    }else{
                        self.plugin.find('input[name="second"]').val('').attr('placeholder', '').prop('disabled', true);
                    }
                }

                self.tips();
            }).on('clear.datetimepicker', function(evt){
                if ($(evt.target).attr('name') == 'first'){
                    self.plugin.find('input[name="second"]').datetimepicker('setDate', null, null);
                    self.plugin.find('input[name="second"]').datetimepicker('setOption', {
                        start: null,
                        min: null
                    });
                    self.plugin.find('input[name="second"]').val('').attr('placeholder', '').prop('disabled', true);
                }
                self.tips();
            });

            this.plugin.find('input[name="first"]').datetimepicker('setTime',this.period.times[0].startTime, this.period.times[0].endTime)
            if (this.period.times.length == 2){
                this.plugin.find('input[name="second"]').prop('disabled', false);
                this.plugin.find('input[name="second"]').datetimepicker('setTime',this.period.times[1].startTime,this.period.times[1].endTime);
            }
            this.tips();
        },
        delete: function(index) {
            this.added.splice(index, 1);
            this.change();
        },
        change: function(){
            this.renderAdded();
            if(this.o.change !== $.noop){
                this.o.change.call(this, this.added)
            }
            console.log(this.selected.split(','))
            if (this.selected.split(',').length == 7) {
                this.element.find('.btn-add-period').hide()
            }else{
                this.element.find('.btn-add-period').show()
            }
        },
        trigger: function(event) {
            this.element.trigger(event+'.period', [this, this.added])
        },
        byweekly: function(a, b){
            if (parseInt(a.weekly.split(',')[0]) == 0) {
                return true;
            } else if (parseInt(b.weekly.split(',')[0]) == 0) {
                return false;
            }else {
                return parseInt(a.weekly.split(',')[0]) > parseInt(b.weekly.split(',')[0])
            }
        },
        tips: function () {
            if (this.period.weekly == ''){
                this.plugin.find('p.tips').text('');
                return false;
            }
            var tips = this.weekesc(this.period.weekly);
            if (this.plugin.find('input[name="first"]').val() != '' && this.plugin.find('input[name="second"]').val() != ''){
                tips += '的 ' + this.plugin.find('input[name="first"]').val() +'，'+this.plugin.find('input[name="second"]').val() + ' 开机运行';
            }else if (this.plugin.find('input[name="first"]').val() != ''){
                tips += '的 ' + this.plugin.find('input[name="first"]').val() + ' 开机运行';
            }
            this.plugin.find('p.tips').text(tips)
        },
        weekesc: function(weekly){
            var esc = '';
            var weeks = [];
            if (typeof weekly === 'string') {
                weeks = weekly.split(',')
            }else{
                weeks = weekly;
            }
            if (weeks.join(',') == '1,2,3,4,5') {
                esc += '工作日';
            }else if (weeks.join(',') == '1,2,3,4,5,6,0') {
                esc += '每天'
            }else{
                if (weeks.length != 0) {
                    var strs = [];
                    for (var i = 0; i < weeks.length; i++) {
                        if (i == 0) {
                            strs.push('每周'+this.o.weekly[weeks[i]])
                        }else{
                            strs.push('周'+this.o.weekly[weeks[i]])
                        }
                    }
                    esc += strs.join('，');
                }
            }

            return esc;
        },
        setOption: function(options){
            this.o = $.extend({}, this.o, options);
            if (options.added) {
                this.added = this.o.added;
            }
            this.renderAdded();
            this.render();
        },
        update: function(data) {
            this.data = data;
            this.render();
        },
        reset: function(){
            this.hide();
            this.added = [];
            this.selected = '';
            this.renderAdded();
        },
        destroy: function(){
            this.plugin.remove();
            delete this.element.data().period;
            return this;
        }
    }
    var Plugin = function(option) {
        var args = Array.apply(null, arguments);
        args.shift();
        var internal_return;
        this.each(function(){
            var $this = $(this),
                data = $this.data('period'),
                options = typeof option === 'object' && option;
            if (!data){
                var opts = $.extend({}, $.fn.period.defaults, options);
                data = new Period(this, opts);
                $this.data('period', data);
            }
            if (typeof option === 'string' && typeof data[option] === 'function'){
                internal_return = data[option].apply(data, args);
            }
        })
        if (internal_return === undefined ||internal_return instanceof Period) return this;
        if (this.length > 1)
            throw new Error('Using only allowed for the collection of a single element (' + option + ' function)');
        else
            return internal_return;
    }
    $.fn.period = Plugin;
    $.fn.period.defaults = {
        class: '',
        added: [],
        weekly: ['日','一','二','三','四','五','六'],
        container: 'body',
        change: $.noop
    }
})();