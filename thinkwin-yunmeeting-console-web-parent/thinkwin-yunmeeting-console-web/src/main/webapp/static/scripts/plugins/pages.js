(function(){
    'use strict';
    var Pages = function(element,options) {
        this.element = $(element);
        this.o = options;
        this.size = this.o.size;
        this.count = this.o.count;
        this.current = 1;
        this.total = 1;
        this.init();
    }
    Pages.prototype = {
        init: function() {
            if (this.element.find('.pages').length == 0){
                this.element.append(this.o.template);
                this.total = Math.ceil(this.count/this.size);
                this.element.find('.total').text('共'+this.total+'页,');
                if (this.total > 1) {
                    this.element.find('.next').prop('disabled', false);
                }
                this.bindEvent();
            }
        },
        bindEvent: function() {
            var self = this;
            this.element.on('click', $.proxy(this.click, this));
            this.element.find('.current').on('keyup',function(){
                this.value = this.value.replace(/[^\d]/g,'');
            });
            this.element.find('.current').on('keydown', function(event){
                if(event.keyCode == '13') {
                    self.goto(parseInt(this.value));
                }
            });
        },
        click: function(e) {
            if($(e.target).hasClass('prev')) {
                this.prev();
            }
            if($(e.target).hasClass('next')) {
                this.next();
            }

            if($(e.target).hasClass('dropdown-item')) {
                var size = $(e.target).data('size');
                this.element.find('.dropdown-toggle').text(size);
                this.current = 1;
                this.o.size = parseInt(size);
                this.size = parseInt(size);
                this.change();
            }
        },
        prev: function() {
            this.element.find('.next').prop('disabled', false)
            if (this.current >= 1) {
                this.current -= 1;
                this.update();
                this.change()
            }
        },
        next: function() {
            this.element.find('.prev').prop('disabled', false);
            if (this.current <= this.total) {
                this.current += 1;
                this.update();
                this.change()
            }
        },
        goto: function (page) {
            if (page == this.current) {
                return false;
            }
            if (page > this.total) {
                page = this.total;
                this.element.find('.current').val(page);
            }
            if (page <= 0){
                page = 1;
                this.element.find('.current').val(page);
            }
            this.current = page;
            this.update();
            this.change()
        },
        upcount: function(count){
            if (count > 0){
                this.count = count;
                this.total = Math.ceil(this.count/this.size);
                this.element.find('.current').val(this.current);
                this.element.find('.total').text('共'+this.total+'页,');
                if (this.total == 1) {
                    this.element.find('.prev').prop('disabled', true);
                    this.element.find('.next').prop('disabled', true);
                }else {
                    if (this.current == 1) {
                        this.element.find('.prev').prop('disabled', true);
                        this.element.find('.next').prop('disabled', false);
                    }else if (this.current == this.total) {
                        this.element.find('.prev').prop('disabled', false);
                        this.element.find('.next').prop('disabled', true);
                    }else {
                        this.element.find('.prev').prop('disabled', false);
                        this.element.find('.next').prop('disabled', false);
                    }
                }

            }
        },
        update: function () {
            this.element.find('.current').val(this.current);
            this.element.find('.total').text('共'+this.total+'页,');
            if (this.total == 1) {
                this.element.find('.prev').prop('disabled', true);
                this.element.find('.next').prop('disabled', true);
            }else {
                if (this.current == 1) {
                    this.element.find('.prev').prop('disabled', true);
                    this.element.find('.next').prop('disabled', false);
                }else if (this.current == this.total) {
                    this.element.find('.prev').prop('disabled', false);
                    this.element.find('.next').prop('disabled', true);
                }else {
                    this.element.find('.prev').prop('disabled', false);
                    this.element.find('.next').prop('disabled', false);
                }
            }
        },
        change: function () {
            if (this.o.change !== $.noop) {
                this.o.change(this.current, this.size);
            }
        },
        destroy: function(){
            this.element.html('');
            delete this.element.data().pages;
            return this;
        }
    }


    var Plugin = function(option) {
        var args = Array.apply(null, arguments);
        args.shift();
        var internal_return;
        this.each(function(){
            var $this = $(this),
                data = $this.data('pages'),
                options = typeof option === 'object' && option;
            if (!data){
                var opts = $.extend({}, $.fn.pages.defaults, options);
                data = new Pages(this, opts);
                $this.data('pages', data);
            }
            if (typeof option === 'string' && typeof data[option] === 'function'){
                internal_return = data[option].apply(data, args);
            }
        })
        if (internal_return === undefined ||internal_return instanceof Pages) return this;
        if (this.length > 1)
            throw new Error('Using only allowed for the collection of a single element (' + option + ' function)');
        else
            return internal_return;
    }
    $.fn.pages = Plugin;
    $.fn.pages.defaults = {
        class: '',
        count: 0,
        size: 15,
        current: 1,
        change: $.noop,
        template:'<div class="pages"><span>显示条数</span><div class="btn-group"><button type="button" class="btn dropdown-toggle" data-toggle="dropdown">15</button><div class="dropdown-menu"><a class="dropdown-item" href="#" data-size="15">15</a><a class="dropdown-item" href="#" data-size="30">30</a><a class="dropdown-item" href="#" data-size="50">50</a></div></div><span class="total">共1页，</span><span>到第</span><input type="text" class="current" value="1"/><span>页</span><button class="prev" disabled>上一页</button><button class="next" disabled>下一页</button></div>',
    }
})();