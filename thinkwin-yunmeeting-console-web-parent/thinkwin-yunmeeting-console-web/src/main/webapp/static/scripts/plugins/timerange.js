(function() {
  var Timerange = function(element, options) {
    $(element).data('timerange', this);
    this.element = $(element);
    this.timerange = $('<div class="timerange"><div class="times"><div class="header"><input type="text" placeholder="请选择时间"/><span class="duration"></span><span class="btn-clear">清除</span></div><div class="starts"></div><div class="ends"></div></div></div>');
    this.calendar = $('<div class="calendar"></div>');
    this.start = 0;
    this.end = 0;
    this.shown = false;
    this.o = options;
    
    var self = this;
    this.element.on('click', function(event) {
      self.init();
    });
  }
  Timerange.prototype = {
    init: function() {
      var start = this.element.find('input').eq(0).val();
      var end = this.element.find('input').eq(1).val();
      start = !isNaN(start) ? parseInt(start) : start;
      end = !isNaN(end) ? parseInt(end) : end;
      if (this.start == 0 && this.end == 0 && moment(start).isValid() && moment(end).isValid()) {
        this.start = new Date(start).getTime();
        this.end = new Date(end).getTime();
        this.o.date = moment(start).format('YYYY-MM-DD');
      }
      this.timerange.appendTo(this.o.container);
      this.renderCalendar();
      this.renderTimes();
      this.place();
      this.shown = true;
      this._trigger('shown.bs.timerange');
    },
    place: function() {
      var element = this.element.offset(),
        container = $(this.o.container).offset();
      this.timerange.css({
        top: element.top-container.top,
        left: element.left-container.left
      });
    },
    renderCalendar: function(){
      var self = this;
      if (this.o.datepicker) {
        this.calendar.html('<div class="head"><input type="text" maxlength="10"><button>今日</button></div><div class="datepicker"></div>');
        this.timerange.prepend(this.calendar);
        this.calendar.children('.datepicker').datepicker({
          language: 'zh-CN',
          format: 'yyyy-mm-dd',
          todayHighlight: true,
          startDate: moment().format('YYYY-MM-DD'),
          minViewMode: 0,
          maxViewMode: 0,
          templates: {
            leftArrow: '<i class="icon icon-back"></i>',
            rightArrow: '<i class="icon icon-right"></i>'
          }
        }).on('changeDate',function(ev) {
          self.calendar.find('.head input').val(moment(ev.date).format('YYYY-MM-DD'))
          self.update(moment(ev.date).format('YYYY-MM-DD'))
        })
        this.calendar.children('.datepicker').datepicker('setDate', this.o.date)
        this.calendar.find('.head input').val(this.o.date)
      }
    },
    renderTimes: function() {
      var date,start,end;
      start = isNaN(this.start) ? parseInt(this.start) : new Date(this.start).getTime();
      end = isNaN(this.end) ? parseInt(this.end) : new Date(this.end).getTime();
      if (!this.o.date || new Date(this.o.date).toDateString() == new Date().toDateString()) {
        this.o.date = moment().format('YYYY-MM-DD');
        date = moment().format('YYYY-MM-DD');
        start = new Date(date+' '+ new Date(Math.ceil(new Date().getTime()/this.o.interval)*this.o.interval).toTimeString().slice(0,5)).getTime();
      }else{
        date = this.o.date;
        start = new Date(date+' '+this.o.start).getTime();
      }
      end = new Date(date+' '+this.o.end).getTime();
      var timestamp = start;
      this.timerange.find('.starts').html('<label><input name="starts" type="radio" value="" disabled/><span>开始</span></label>');
      this.timerange.find('.ends').html('<label><input name="ends" type="radio" value="" disabled/><span>结束</span></label>');
      while(timestamp <= end){
        var time = new Date(timestamp).toTimeString().slice(0,5);
        if (timestamp != end) {
          this.timerange.find('.starts').append('<label><input name="starts" type="radio" value="'+timestamp+'" '+(timestamp==this.start ? 'checked': '')+'/><span>'+time+'</span><span class="icon icon-tick"></span></label>');
        }
        if (timestamp != start) {
          var checked = time==this.end;
          var disabled = this.start!='' && timestamp <= this.start;
          this.timerange.find('.ends').append('<label><input name="ends" type="radio" value="'+timestamp+'" '+(timestamp==this.end ? 'checked': disabled ? 'disabled':'')+'/><span>'+time+'</span><span class="icon icon-tick"></span></label>');
        }
        timestamp+=this.o.interval;
      }
      this.bindEvent();
    },
    bindEvent: function() {
      var self = this;
      var starts = this.timerange.find('.starts');
      var ends = this.timerange.find('.ends');
      // 时间选择
      this.timerange.on('click', 'input:radio', function(event) {
        var index = $(this).parent('label').index();
        var value = $(this).val();
        if ($(this).parents().is('.starts')) {
          self.start = parseInt(value);
          ends.children('label').eq(index).prevAll().children('input').attr('disabled', true);
          ends.children('label').eq(index-1).nextAll().children('input').attr('disabled', false);
          if (self.end == '') {
            ends.find('input').eq(index)[0].checked = true;
            self.end = parseInt(ends.find('input').eq(index).val());
            ends.scrollTop(36*index);
          }else{
            if (index>ends.find('input:checked').parent('label').index()) {
              ends.find('input').eq(index)[0].checked = true;
              self.end = parseInt(ends.find('input').eq(index).val());
              ends.scrollTop(36*index);
            }
          }
        }else{
          self.end = parseInt(value);
          if (self.start == '') {
            starts.find('input').eq(1)[0].checked = true;
            self.start = parseInt(starts.find('input').eq(1).val());
          }
        }
        self.change();
      })
      // 清除
      this.timerange.on('click', '.btn-clear', function(){
        self.start = '';
        self.end = '';
        self.timerange.find('.header input').val('');
        self.timerange.find('span.duration').text('');
        self.element.find('span.value').addClass('none').text('请选择时间');
        self.element.find('input').eq(0).val('');
        self.element.find('input').eq(1).val('');
        self.renderTimes();
      })

      //今日
      this.calendar.on('click', '.head button', function(){
        self.update(moment().format('YYYY-MM-DD'))
        self.calendar.children('.datepicker').datepicker('setDate', moment().format('YYYY-MM-DD'))
      })

      // 日期输入框回车事件
      this.calendar.on('keypress', '.head input', function(){
        var date = $(this).val().trim();
        if(event.keyCode == '13' && date != ''){
          if (moment(date).isValid()) {
            self.update(moment(date).format('YYYY-MM-DD'))
            self.calendar.children('.datepicker').datepicker('setDate', moment(date).format('YYYY-MM-DD'))
          }else{
            notify('danger', '请输入合法有效的时间');
          }
        }
      })
      this.timerange.on('click', function(event) {
        event.stopPropagation();
      })
      $(window).on('resize', function() {
        self.place();
      })
      $('body').on('click', function(e) {
        if (self.shown && self.element.find(e.target).length<1) {
          self.timerange.remove();
          self.shown = false;
          self._trigger('hidden.bs.timerange');
        }
      })
    },
    update: function(date) {
      this.o.date = date;
      if (this.start != '' && this.end != '') {
        this.start = new Date(date+' '+moment(this.start).format('H:mm')).getTime();
        this.end = new Date(date+' '+moment(this.end).format('H:mm')).getTime();
        this.change();
      }
      this.renderTimes();
    },
    change: function() {
      var duration = this.end - this.start;
      var week = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'][new Date(this.start).getDay()];
      var times = moment(this.start).format('H:mm')+'~'+moment(this.end).format('H:mm');
      var hours = Math.floor(duration/(60*60*1000));
      var minutes = Math.floor((duration-hours*(60*60*1000))/(60*1000));
      duration = (hours>0 && minutes>0) ? hours+'小时'+minutes+'分钟' : (hours>0) ? hours+'小时' : minutes+'分钟';
      this.timerange.find('.header input').val(times);
      this.timerange.find('span.duration').text(duration);
      if (this.o.dateweek) {
        this.element.find('span.value').removeClass('none').text(times+' '+moment(this.o.date).format('YYYY年M月D日')+' （'+week+'） '+duration);
      }else{
        this.element.find('span.value').removeClass('none').text(times+' '+duration);
      }
      if (this.o.timestamp) {
        this.element.find('input').eq(0).val(this.start);
        this.element.find('input').eq(1).val(this.end);
      }else{
        this.element.find('input').eq(0).val(moment(this.start).format('YYYY-MM-DD H:mm'));
        this.element.find('input').eq(1).val(moment(this.end).format('YYYY-MM-DD H:mm'));
      }
      
      this._trigger('change.bs.timerange');
    },
    _trigger: function(event, date) {
      if (this.start==''&& this.end=='') {
        return false;
      }
      this.element.trigger({
        type: event,
        start: this.start,
        end: this.end
      })
    }
  }
  var timerangePlugin = function(option) {
    var args = Array.apply(null, arguments);
    args.shift();
    var internal_return;
    this.each(function(){
      var $this = $(this),
        data = $this.data('timerange'),
        options = typeof option === 'object' && option;
      if (!data){
        var opts = $.extend({}, defaults, options);
        data = new Timerange(this, opts);
        $this.data('timerange', data);
      }
      if (typeof option === 'string' && typeof data[option] === 'function'){
        internal_return = data[option].apply(data, args);
      }
    })
    if (internal_return === undefined ||internal_return instanceof Timerange) return this;
    if (this.length > 1)
      throw new Error('Using only allowed for the collection of a single element (' + option + ' function)');
    else
      return internal_return;
  }
  $.fn.timerange = timerangePlugin;
  var defaults = $.fn.timerange.defaults = {
    start: '9:00',
    end: '21:00',
    container: 'body',
    dateweek: true,
    timestamp: true,
    datepicker: true,
    interval: 15*60*1000,
    date: false
  }
  $(document).on('click', '[data-toggle="timerange"]', function(e) {
    var $this = $(this);
    if ($this.data('timerange'))
      return;
    e.preventDefault();
    // component click requires us to explicitly show it
    timerangePlugin.call($this, 'init');
  })
})();