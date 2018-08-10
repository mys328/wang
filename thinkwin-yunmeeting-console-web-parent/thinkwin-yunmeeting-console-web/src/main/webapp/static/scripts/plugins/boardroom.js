(function() {
  var Boardroom = function(element, options) {
    $(element).data('boardroom', this);
    this.element = $(element);
    this.boardroom = $('<div class="boardroom"><div class="wrap"><div class="header"><label class="checkbox"><input name="filter" type="checkbox" value="004"><span class="icon">扩音</span></label><label class="checkbox"><input name="filter" type="checkbox" value="003"><span class="icon">显示</span></label><label class="checkbox"><input name="filter" type="checkbox" value="001"><span class="icon">白板</span></label><label class="checkbox"><input name="filter" type="checkbox" value="002"><span class="icon">视频会议</span></label></div><div class="result"></div></div></div>');
    this.shown = false;
    this.room = {},
    this.filters = '',
    this.o = options;
    var self = this;
    this.element.on('click', function(event) {
      self.init();
    });
  }
  Boardroom.prototype = {
    init: function() {
      if (this.element.find('input').val() != '') {
        this.room = {
          id: this.element.find('input').val()
        }
      }
      this.boardroom.appendTo(this.o.container);
      this.renderData();
      this.bindEvent();
      this.place();
      this.shown = true;
      this._trigger('shown.bs.boardroom');
    },
    place: function() {
      var element = this.element.offset(),
        container = $(this.o.container).offset();
      this.boardroom.css({
        top: element.top-container.top,
        left: element.left-container.left
      });
    },
    renderData: function() {
      var self = this;
      self.boardroom.find('.result').html('');
      fetchs.get('/meetingRoom/mettingRoomAreaSorting?deviceService='+self.filters, function(res) {
        self.data = res.data;
        if (res.data.length == 0) {
          self.boardroom.find('.result').html('<div class="nothing">没有符合条件的会议室</div>')
        }
        $.map(self.data, function(item) {
          self.boardroom.find('.result').append('<label><input name="boardroom" type="radio" value="'+item.id+'" '+((item.id == self.room.id)? 'checked':'')+'/><span class="icon icon-tick"></span><span class="name">'+item.name+'</span><span><i class="icon icon-xq-number"></i>20</span><span><i class="icon icon-room-address"></i>'+item.location+'</span></label>')
        })
      });
      
    },
    bindEvent: function() {
      var self = this;
      // 选择会议室
      this.boardroom.on('click', 'input:radio', function(event) {
        var name = $(this).parent('label').children('span.name').text();
        var id = $(this).val();
        self.room = {
          id,
          name 
        }
        self.change();
      })
      // 会议室筛选
      this.boardroom.on('click', '.header input', function(event) {
        event.stopPropagation()
        var filters = []
        self.boardroom.find('input:checkbox:checked').each(function(){
          filters.push($(this).val());
        })
        self.filters = filters.join(',');
        self.renderData();
      })
      this.boardroom.on('click', function(event) {
        event.stopPropagation();
      })
      $(window).on('resize', function() {
        self.place();
      })
      $('body').on('click', function(e) {
        if (self.shown && self.element.find(e.target).length<1) {
          self.boardroom.remove();
          self.shown = false;
          self._trigger('hidden.bs.boardroom');
        }
      })
    },
    update: function(date) {
      this.o.date = date;
      this.renderData();
    },
    change: function() {
      this.element.find('span.value').text(this.room.name);
      this.element.find('input:hidden').val(this.room.id);
      this._trigger('change.bs.boardroom');
    },
    _trigger: function(event, date) {
      this.element.trigger({
        type: event,
        room: this.room
      })
    }
  }
  var boardroomPlugin = function(option) {
    var args = Array.apply(null, arguments);
    args.shift();
    var internal_return;
    this.each(function(){
      var $this = $(this),
        data = $this.data('boardroom'),
        options = typeof option === 'object' && option;
      if (!data){
        opts = $.extend({}, defaults, options);
        data = new Boardroom(this, opts);
        $this.data('boardroom', data);
      }
      if (typeof option === 'string' && typeof data[option] === 'function'){
        internal_return = data[option].apply(data, args);
      }
    })
    if (internal_return === undefined ||internal_return instanceof Boardroom) return this;
    if (this.length > 1)
      throw new Error('Using only allowed for the collection of a single element (' + option + ' function)');
    else
      return internal_return;
  }
  $.fn.boardroom = boardroomPlugin;
  var defaults = $.fn.boardroom.defaults = {
    start: '9:00',
    end: '21:00',
    container: 'body',
    timestamp: false,
    interval: 15*60*1000,
    date: false
  }

  $(document).on('click', '[data-toggle="boardroom"]', function(e) {
    var $this = $(this);
    if ($this.data('boardroom'))
      return;
    e.preventDefault();
    // component click requires us to explicitly show it
    boardroomPlugin.call($this, 'init');
  })
})();