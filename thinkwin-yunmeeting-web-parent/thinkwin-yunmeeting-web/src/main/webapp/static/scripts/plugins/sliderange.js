(function() {
  'use strict';
  /*
   *  插件构造函数
   */
  var Sliderange = function(element, options) {
    // 初始变量
    this.index = 0;
    this.move = false;
    this.criticals = [];
    this.point = null;
    this.options = $.extend({}, $.fn.sliderange.defaults, options);
    this.element = $(element);
    this.plugin = $('<div class="sliderange">    <div class="sliderange-critical"></div>    <div class="sliderange-progress">        <div class="sliderange-track">            <div class="sliderange-section"></div>            <div class="sliderange-selected">              <div class="sliderange-default"></div>              <div class="section"></div>            </div>            <div class="sliderange-thumb"><span></span></div>        </div>        <div class="sliderange-number">            <span class="minus"></span>            <input type="text" />            <span class="plus"></span>            <span class="unit"></span>        </div>    </div>    <div class="sliderange-tips">        <div class="prompt"></div>        <div class="actual"></div>    </div></div>');
    this.render();
    this.bindEvent();
    this.val(Math.max(this.options.min, this.options.default, this.options.value))
  }
  Sliderange.prototype = {
    // 渲染
    render: function(data) {
      var self = this;
      this.element.find('.sliderange').length || this.element.html('').append(this.plugin);
      this.options.class && this.plugin.addClass(this.options.class);
      this.track = this.plugin.find(".sliderange-track"),
      this.selected = this.plugin.find(".sliderange-selected"),
      this.section = this.plugin.find(".sliderange-section"),
      this.default = this.plugin.find(".sliderange-default"),
      this.thumb = this.plugin.find(".sliderange-thumb"),
      this.critical = this.plugin.find(".sliderange-critical"),
      this.number = this.plugin.find(".sliderange-number"),
      this.tips = this.plugin.find(".sliderange-tips");
      this.track.css('width', this.options.width);
      this.minX = this.valueToX(Math.max(this.options.min, this.options.default))
      this.default.css('width', this.valueToX(this.options.default));
      var sections = [];
      $.each(this.options.section, function(index, item){
        var width = index === 0 ? item.width : item.width - self.options.section[index - 1].width;
        sections.push('<span style="width:' + width + 'px"><i>' + item.value + self.options.unit + "</i></span>")
      })
      this.section.html(sections.join(""));
      this.selected.find('.section').html(sections.join(""))
      this.options.points.sort(this.by('unit'));
      var criticals = [];
      $.each(this.options.points, function(index, item){
        var left = self.valueToX(item.unit);
        if (typeof item.discount == 'string') {
          criticals.push('<span style="left:' + left + 'px">' + item.discount + "</span>")
        }else{
          self.criticals.push({
            unit: item.unit,
            discount: item.discount,
            left: left
          })
          criticals.push('<span class="point" style="left:' + left + 'px">' + item.discount + "折</span>")
        }
      })

      this.critical.html(criticals.join(""));
      this.number.find('.unit').text(this.options.unit);
      this.tips.find(".prompt").css('width', this.options.width);
      this.options.tips && this.tips.find(".prompt").html(this.options.tips);
    },
    
    // 插件操作事件绑定
    bindEvent: function() {
      var self = this;
      // 拖动滑块
      this.thumb.off().on('mousedown', function(evt){
        $(this).addClass('moving')
        self.mousedown(evt)
      })

      // 点击滑条
      this.track.off().on('click', function(evt) {
        if ($(this).find(evt.target).length>0 && !$(evt.target).hasClass('sliderange-thumb')) {
          var diff = this.diff || 0;
          self.slide(self.relativeX(evt.clientX)- diff);
          self.toStep() && self.change()
        }
      })
      
      // 输入框
      this.number.find('input').off().on('blur', function(){
        if (this.value === '') {
          this.value = self.value;
        }
        if (parseInt(this.value) !== self.value) {
          self.val(parseInt(this.value))
        }
      }).on('keypress', function(event){
        var keyCode = event.keyCode || event.which;
        return (keyCode>=48 && keyCode<=57) || keyCode == 8 ||(keyCode>=96 && keyCode<=105);
      }).on('keyup', function(){
        this.value = this.value.replace(/[^\d]/g,'');
      })

      this.number.find('.plus').off().on('click', function(){
        !$(this).hasClass('disabled') && self.val(self.value+self.options.step)
      })

      this.number.find('.minus').off().on('click', function(){
        !$(this).hasClass('disabled') && self.val(self.value-self.options.step)
      })
    },
    mousedown: function(evt) {
      var self = this;
      this.diff = this.relativeX(evt.clientX) - this.thumb.position().left;
      $(document).off('mousemove.sliderange').off('mouseup.sliderange').off('selectstart.sliderange').on('mousemove.sliderange', function(evt){
        evt.preventDefault();
        self.mousemove(evt)
        return false;
      }).on('mouseup.sliderange', function(evt){
        self.mouseup(evt)
        self.thumb.removeClass('moving')
      }).on('selectstart.sliderange', function(){
        return false;
      })
    },
    mousemove: function(evt) {
      this.move = true;
      var diff = this.diff || 0;
      this.slide(this.relativeX(evt.clientX)- diff)
    },
    mouseup: function(evt) {
      $(document).off('mousemove.sliderange').off('mouseup.sliderange').off('selectstart.sliderange');;
      if (this.move) {
        this.toStep() && this.change();
        this.move = false;
      }
      
    },
    slide: function(x){
      this.number.find('span').removeClass('disabled')
      if (x <= this.minX) {
        x = this.minX;
        this.number.find('.minus').addClass('disabled')
      } else if (x >= this.options.width) {
        x = this.options.width
        this.number.find('.plus').addClass('disabled')
      }
      this.xToDiscount(x);
      this.thumb.css('left', x);
      this.selected.width(x);
      var value = this.xToValue(x);
      if (Math.ceil(value) - value < 0.0001) {
        value = Math.ceil(value);
      }else{
        value = parseInt(value);
      }
      this.update(value);
    },
    toStep: function() {
      if (this.value == this.preValue) {
        return false;
      }
      var initial = Math.max(this.options.min, this.options.default);
      var remainder = (this.value - initial) % this.options.step;
      if (0 !== remainder) {
        this.value += this.options.step - remainder;
        this.val(this.value);
        return false;
      } else {
        return true
      }
    },
    update: function(value){
      this.value = value;
      this.number.find('input').val(this.value);
      this.thumb.find('span').text(this.value+this.options.unit);
      this.tips.find('.actual').html('实际支付<b>'+(this.value-this.options.free)+'</b>'+this.options.unit);
    },
    relativeX: function(x) {
      return x - this.track.offset().left;
    },
    valueToX: function(value){
      var ladder = this.ladder(value, 'value')
      var x = (value - ladder.startV)/(ladder.endV-ladder.startV)*(ladder.endX-ladder.startX)+ladder.startX
      return x;
    },
    xToValue: function(x){
      var ladder = this.ladder(x, 'width');
      var value = (x - ladder.startX)/(ladder.endX-ladder.startX)*(ladder.endV-ladder.startV)+ladder.startV;
      return value;
    },
    xToDiscount: function(x) {
      this.point = null;
      this.critical.find('span.point').removeClass('attained');
      var index = -1, criticals = this.criticals;
      for (var i = criticals.length - 1; i >= 0; i--) {
        if (x >= criticals[i].left) {
          index = i;
          this.point = criticals[i];
          this.critical.find('span.point').eq(i).addClass('attained')
          break;
        }
      }
      return index;
    },
    ladder: function(value, type) {
      var self = this;
      var index = 0, section = this.options.section;
      for (var i = 0; i < section.length; i++) {
        if (value <= section[i][type]) {
          index = i;
          break;
        }
      }
      return {
        startX: index === 0 ? 0 : section[index-1].width,
        startV: index === 0 ? 0 : section[index-1].value,
        endX: section[index].width,
        endV: section[index].value
      }
    },
    by: function(property){
      return function(a, b) {
        return a[property] - b[property];
      }
    },
    val: function(value) {
      var max = this.xToValue(this.options.width);
      var min = Math.max(this.options.min, this.options.default);
      var remainder = (value - min) % this.options.step;
      if (value < min) {
        value = min;
      }else if (value > max) {
        value = max;
      }else{
        if (remainder !== 0) {
          value += this.options.step - remainder;
        }
      }
      this.slide(this.valueToX(value));
      this.update(value);
      if (this.preValue !== value) {
        this.change();
      }
    },
    change: function(){
      this.preValue = this.value;
      this.options.change && this.options.change.call(this, this.value, this.point);
    }
  }
  var sliderangePlugin = function(option) {
    return this.each(function() {
      var $this =$(this)
      var data = $this.data('sliderange');
      var options = typeof option === 'object' && option;
      if (!data && /destroy|hide/.test(option)) return
      if (!data) $this.data('sliderange', (data = new Sliderange(this, options)))
      if (typeof option == 'string') data[option]()
    })
  }
  $.fn.sliderange = sliderangePlugin;

  // 默认参数
  $.fn.sliderange.defaults = {
    class: null,
    width: 660,
    value: 0,
    default: 0,
    min: 0,
    max: 100,
    free: 0,
    unit: '人',
    section: [{
        value: 20,
        width: 340
    }, {
        value: 50,
        width: 500
    }, {
        value: 100,
        width: 660
    }],
    points: [],
    step: 1,
    tips: null,
    change: null,
    flag: null
  }
}());