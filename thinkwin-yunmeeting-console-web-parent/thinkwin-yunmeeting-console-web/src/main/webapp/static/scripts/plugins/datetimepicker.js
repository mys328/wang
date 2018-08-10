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
    this.plugin = $('<div class="datetimepicker"><div class="calendars"><div class="header"><input type="text" maxlength="10"><button type="button">今日</button></div><div class="starts"></div><div class="ends"></div></div><div class="times"><div class="header"><input type="text" maxlength="5" placeholder="请选择时间"></div><div class="starts"></div><div class="ends"></div></div></div>');
    this.calendars = this.plugin.find('.calendars');
    this.times = this.plugin.find('.times');
    this.shown = false;
    this.options = options;
    this.now = new Date();
    this.current = new Date(this.now.getFullYear(), this.now.getMonth(), 1, 0, 0, 0);
    if (this.element.is('input')) {

    }
    this.start = new Date();
    this.end = null;
    this.element.on('focus', $.proxy(function(evt){
      $(evt.target).blur();
      this.init();
    }, this));
  }
  Datetimepicker.prototype = {
    init: function() {
      this.plugin.appendTo(this.options.container);
      this.renderCalendars();
      this.renderTimes();
      this.scrollTime();
      this.bindEvent();
      this.place();
      this.update();
      this.shown = true;
    },
    place: function() {
      var element = this.element,
          plugin = this.plugin,
        scrollTop =  this.options.container == 'body' ? 0 : $(this.options.container).scrollTop(),
        container = $(this.options.container),
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
    renderCalendars: function(){
      var d,day,
          i = 0,
          table = '',
          options = this.options,
          start = new Date(this.current.getFullYear(), this.current.getMonth(), 1, 12, 0, 0);
      if (start.getDay() === options.day) {
        start.setDate(start.getDate() - 7);
      }
      while (start.getDay() !== options.day) {
        start.setDate(start.getDate() - 1);
      }
      table += '<table><thead><tr>';
      table += '<th class="prev"><i class="icon icon-calendar-left"></i></th>';
      table += '<th colspan="5">'+this.current.getFullYear()+'年'+(this.current.getMonth()+1)+'月</th>';
      table += '<th class="next"><i class="icon icon-calendar-right"></i></th>';
      table += '</tr><tr>'
      for (var j = 0; j < 7; j++) {
        table += '<th>'+ options.dates[options.language].days[(j+options.day)%7] + '</th>'
      }
      table += '</tr></thead>';
      table += '<tbody><tr>';
      while(i < 42) {
        i += 1;
        var className = ['day'];
        if (i > 6 && start.getDay() === options.day) {
          table += '</tr><tr>';
        }
        if (start.getMonth() < this.current.getMonth()) {
          className.push('old')
        }else if (start.getMonth() > this.current.getMonth()) {
          className.push('new')
        }
        if (start.toDateString() == this.now.toDateString()) {
          className.push('today')
        }
        if (start.toDateString() == this.start.toDateString()) {
          className.push('active')
        }
        if (start.getTime() < this.parseDate(options.start).getTime()) {
          className.push('disabled')
        }
        table += '<td class="'+ className.join(' ') +'">'+ start.getDate() +'</td>';
        
        start.setDate(start.getDate() + 1);
      }

      table += '</tbody></table>';

      this.calendars.find('.starts').html(table);
      this.updateArrows();

    },
    renderTimes: function() {
      var i=0,index=0,h,m,
        options = this.options,
        timestamp = new Date(this.start.getFullYear(), this.start.getMonth(), this.start.getDate(), 0, 0, 0).getTime();
        end = timestamp + 24*60*60*1000;
      var times = '';
      while (timestamp < end) {
        i++;
        var time = new Date(timestamp).toTimeString().slice(0, 5);
        var checked = this.start.toTimeString().slice(0, 5) === time;
        var disabled = false;
        if (options.min) {
          disabled = timestamp <= this.parseDate(options.min).getTime();
        }
        if (checked) {index = i}
        times += '<label><input name="starts" type="radio" value="'+timestamp+'" '+(checked ? 'checked': disabled ? 'disabled':'')+'/><span>'+time+'</span><span class="icon icon-tick"></span></label>';
        timestamp += options.step*60*1000;
      }
      this.times.find('.starts').html(times);
      
    },
    scrollTime: function(){
      var times = this.start.format('HH:mm').split(':');
      var minutes = parseInt(times[0])*60 + parseInt(times[1])
      var height = this.times.find('.starts label').outerHeight();
      this.times.find('.starts').scrollTop(height*(Math.ceil(minutes/this.options.step)));
    },
    updateArrows: function(){
      var start = this.options.start;
      if (start && this.current.getFullYear() <= this.parseDate(start).getFullYear() && this.current.getMonth() <= this.parseDate(start).getMonth()){
        this.calendars.find('.prev').css({visibility: 'hidden'});
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
        var reg = new RegExp(/^[1-9]\d{3}-([1-9]|0[1-9]|1[0-2])-([1-9]|0[1-9]|[1-2][0-9]|3[0-1])$/);
        if(!reg.test(this.value)){
            this.value = self.start.format('YYYY-MM-DD')
        } else {
          if (new Date(this.value).getMonth() === parseInt(this.value.split('-')[1])) {
            this.value = self.start.format('YYYY-MM-DD')
          }else{
            self.setDate(this.value)
          }
        }
      }).on('keypress', function(evt){
        if(evt.keyCode == '13') {
          $(evt.target).blur();
        }
        return (evt.keyCode>=48 && evt.keyCode<=57) || evt.keyCode == 45;
      })
      // 今天
      this.calendars.on('click', '.header button', function(evt){
        self.setDate(new Date().format('YYYY-MM-DD'))
      })
      // 选择日期
      this.calendars.on('click', 'td', function(evt){
        self.select($(this))
      })
      
      // 选择时间
      this.times.off().on('click', 'input:radio', function(evt){
        self.setDate(this.value)
      })
      this.times.on('blur', '.header input', function(evt){
        var reg = new RegExp(/^(20|21|22|23|[1-9]|[0-1]\d):([1-9]|[0-5]\d)$/);
        if(!reg.test(this.value)){
          this.value = self.start.format('HH:mm');
        } else {
          self.setDate(this.value)
        }
      }).on('keypress', function(evt){
        if(evt.keyCode == '13') {
          $(evt.target).blur();
        }
        return evt.keyCode>=48 && evt.keyCode<=58;
      })

      this.plugin.on('click', function(evt){
        evt.stopPropagation();
      })
      $(window).off('resize.datetimepicker').on('resize.datetimepicker', function() {
        self.place();
      })
      $(document).off('mousedown.datetimepicker').on('mousedown.datetimepicker', function(e) {
        if (self.shown && !(self.element.is(e.target) || self.element.find(e.target).length || self.plugin.is(e.target) || self.plugin.find(e.target).length)) {
          self.hide();
        }
      })
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
      var dir, day, year, month, monthChanged, yearChanged;
      if (!target.hasClass('disabled')) {
        if (target.hasClass('day')){
          day = parseInt(target.text(), 10) || 1;
          year = this.current.getFullYear();
          month = this.current.getMonth();
          if (target.hasClass('old')) {
            if (month === 0) {
              month = 11;
              year = year - 1;
            } else {
              month = month - 1;
            }
          }
          if (target.hasClass('new')) {
            if (month === 11) {
              month = 0;
              year = year + 1;
            } else {
              month = month + 1;
            }
          }
          this.setDate(year+'-'+(month+1)+'-'+day);
          this.change()
        }
      }
    },
    hide: function() {
      this.plugin.remove();
      this.shown = false;
      this.trigger('hidden');
    },
    clear: function(){
      this.start = '';
      this.end = '';
      this.timerange.find('.header input').val('');
      this.timerange.find('span.duration').text('');
      this.element.find('span.value').addClass('none').text('请选择时间');
      this.element.find('input').eq(0).val('');
      this.element.find('input').eq(1).val('');
      this.renderTimes();
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
      this.options = $.extend({}, this.options, opts);
      this.update();
    },
    update: function() {
      this.times.find('.header input').val(this.start.format('HH:mm'))
      this.calendars.find('.header input').val(this.start.format('YYYY-MM-DD'));
      this.element.val(this.start.format('YYYY-MM-DD HH:mm'))
      this.renderCalendars()
      this.renderTimes()
    },
    change: function() {
      this.options.change && this.options.change.call(this, this.start, this.end);
    },
    setDate: function(string){
      var date = '',time='',dates,times;
      if (!isNaN(Number(string))) {
        string = parseInt(string);
      }else{
        if (string.split(' ').length == 1) {
          if (string.indexOf(':') > 0) {
            string = this.start.toDateString() +' '+ string;
          }else{
            string += ' '+this.start.toTimeString().slice(0,5);
          }
        }
        var min = this.parseDate(this.options.min).getTime();
        var start = this.parseDate(this.options.start).getTime();
        if (this.parseDate(string).getTime() < start ) {
          string = start;
        }
        if (this.parseDate(string).getTime() <= min) {
          var num = min % (this.options.step*60*1000);
          if (num == 0) {
            string = min + this.options.step*60*1000;
          }else{
            string = min - num + this.options.step*60*1000;
          }
        }
      }
      this.start = this.parseDate(string);
      this.current.setFullYear(this.start.getFullYear())
      this.current.setMonth(this.start.getMonth())
      this.update();
    },
    trigger: function(event, date) {
      this.element.trigger(event, this.start, this.end)
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
    format: 'YYYY-MM-DD H:mm',
    start: '6:00',
    end: '23:00',
    min: null,
    max: null,
    day: 1,
    container: 'body',
    datepicker: true,
    timepicker: false,
    range: true,
    step: 60,
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
    change: null
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