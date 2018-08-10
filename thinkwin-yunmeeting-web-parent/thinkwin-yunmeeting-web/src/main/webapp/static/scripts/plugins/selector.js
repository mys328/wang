(function() {
    'use strict';
    var Selector = function(element, options) {
        this.element = $(element);
        this.isInput = this.element.is('input');
        this.input = this.isInput ? this.element : this.element.find('input');
        this.selector = $('<div class="selector"><div class="wrap"><div class="search"></div><div class="filter"></div><div class="result"></div></div><div class="examineListdiv"></div></div>');
        this.filter = this.selector.find('.filter');
        this.result = this.selector.find('.result');
        this.o = options;
        this.shown = false;
        this.selected = this.o.selected;
        this.data = [];
        this.o.class !== '' && this.selector.addClass(this.o.class);
        this.templates();
        this.renderSelected();
        this.renderFilter();
        this.bindEvents();
    }
    Selector.prototype = {
        init: function(e) {
            // body...
        },
        show: function(e) {
            if($(e.target).hasClass('icon-delete-personnel')){
                var data = $(e.target).parent('span').data();
                var key = this.findIndex(this.selected, data.id);
                $(e.target).parent('span').remove();
                this.selected.splice(key, 1);
                var index = this.findIndex(this.data, data.id);
                this.o.onSelect($(e.target), this.data[index], false, index);
                this.result.find('label').eq(index).click();
            }else{
                this.selector.appendTo(this.o.container);
                this.place();

                this.selector.show();
                this.shown = true;
                this.o.resources(this);
            }
            return this;
        },
        hide: function(){
            this.selector.hide();
            this.shown = false;
            this.trigger('hidden');
            return this;
        },
        place: function() {
            var container = $(this.o.container),
                scrollTop =  scrollTop = this.o.container === 'body' ? $(document).scrollTop() : container.scrollTop(),
                offset = this.element.offset(),
                height = this.element.innerHeight(),
                width = this.element.innerWidth(),
                top = offset.top - container.offset().top + height,
                left = offset.left - container.offset().left;
            if (this.o.container !== 'body') {
                top += scrollTop;
            }
            if (offset.left < 0) {
                left -= offset.left;
            } else if (left + this.selector.outerWidth() > container.width()) {

                left += width - this.selector.outerWidth();
            } else {
                // Default to left
            }
            // if (left + plugin.outerWidth() > container.outerWidth()) {
            //   left = element.offset().left - container.offset().left + element.outerWidth() - plugin.outerWidth();

            // }
            // if (top + plugin.outerHeight() > container.outerHeight()) {
            //   top = element.offset().top-container.offset().top + scrollTop + element.outerHeight() - plugin.outerHeight();
            // }
            this.selector.css({
                top: top,
                left: left
            });
            return this;
        },
        templates: function() {

        },
        render: function() {
            var i = 0;
            var html = [];
            for (var i = 0; i < this.data.length; i++) {
                var item = this.data[i];
                var selected = this.findIndex(this.selected, item.id);
                if(this.o.render !== $.noop){
                    var str = this.o.render(this, item, selected);
                    if (str !== undefined && str !== '') {
                        html.push(str);
                    }
                }else{

                    html.push('<label><input name="selector" type="radio" value="'+item.id+'"><span class="icon icon-tick"></span><span class="btn-binding">绑定</span><h3>'+item.name+'<span class="stopped">已停用</span></h3><p><span><i class="icon icon-xq-number"></i>'+item.capacity+'</span></p></label>')
                }
            }
            this.result.empty().append(html.join(''));
        },
        renderSelected: function(){
            this.element.find('[data-id]').remove();
            for (var i = 0; i < this.selected.length; i++) {
                if (this.o.renderSelected !== $.noop) {
                    var item = this.o.renderSelected(this.selected[i]);
                    this.element.find('.btn-add').before(item);
                } else {
                    this.element.find('.btn-add').before('<span data-id="'+this.selected[i].id+'"  class="btn-addspan">'+this.selected[i].name+'<i class="icon icon-delete-personnel"></i></span>');
                }
            }
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
        renderFilter: function() {
            var filter = this.o.filter();
            var html = [];
            if (filter === undefined || filter === ''){
                return false;
            }
            this.filter.show();
            for (var i = 0; i < filter.length; i++) {
                var item = filter[i];
                html.push('<label class="checkbox"><input name="filter" type="checkbox" value="'+item.key+'"><span class="icon">'+item.value+'</span></label>')
                if(this.o.renderFilter !== $.noop){
                    var str = this.o.renderFilter(item);
                    if (str !== undefined && str !== '') {
                        html[i] = str;
                    }
                }
            }
            this.filter.empty().append(html.join(''));
        },
        bindEvents: function() {
            if (this.isInput) {
                this.element.on('focus', $.proxy(function(evt){
                    $(evt.target).blur();
                    this.show(evt);
                }, this));
            }else{
                this.element.on('click', $.proxy(this.show, this));
            }
            this.selector.on('click', $.proxy(this.click, this));

            $(window).on('resize', $.proxy(this.place, this));

            $(document).on('mousedown', $.proxy(function(e) {
                if(this.shown && (!(this.element.is(e.target) || this.element.find(e.target).length || this.selector.is(e.target) ||
                    this.selector.find(e.target).length))){
                    this.hide();
                }
            }, this))
        },
        click: function(e) {
            e.stopPropagation();
            if($(e.target).is('input') && $(e.target).parents('.filter').length) {
                this.o.onFilter(this, $(e.target))
            }

            if($(e.target).is('input') && $(e.target).parents('.result').length) {
                var index = $(e.target).parent('label').index();
                if($(e.target).is(':checked')) {
                    this.o.onSelect(this, this.data[index], true, index);
                    this.input.val(this.data[index].name);
                    this.selected.push(this.data[index]);
                    if (this.o.renderSelected !== $.noop) {
                        var item = this.o.renderSelected(this.data[index]);
                        this.element.find('.btn-add').before(item);
                    } else {
                        this.element.find('.btn-add').before('<span data-id="'+this.data[index].id+'">'+this.data[index].name+'<i class="icon icon-delete-personnel"></i></span>');
                    }
                }else{
                    var eq = this.findIndex(this.selected, this.data[index].id)
                    this.element.find('[data-id="'+this.data[index].id+'"]').remove();
                    this.selected.splice(eq, 1);
                    this.o.onSelect(this, this.data[index], false, index);
                }
            }
        },
        setOption: function(options){
            this.o = $.extend({}, this.o, options);
            if (options.selected) {
                this.selected = this.o.selected;
            }
            this.renderSelected();
            this.renderFilter();
            this.render();
            // this.update();
        },
        trigger: function(event) {
            this.element.trigger(event+'.selector', [this, this.selected])
        },
        update: function(data) {
            this.data = data;
            this.render();
        },
        destroy: function(){
            this.selector.remove();
            delete this.element.data().selector;
            return this;
        }
    }
    var Plugin = function(option) {
        var args = Array.apply(null, arguments);
        args.shift();
        var internal_return;
        this.each(function(){
            var $this = $(this),
                data = $this.data('selector'),
                options = typeof option === 'object' && option;
            if (!data){
                var opts = $.extend({}, $.fn.selector.defaults, options);
                data = new Selector(this, opts);
                $this.data('selector', data);
            }
            if (typeof option === 'string' && typeof data[option] === 'function'){
                internal_return = data[option].apply(data, args);
            }
        })
        if (internal_return === undefined ||internal_return instanceof Selector) return this;
        if (this.length > 1)
            throw new Error('Using only allowed for the collection of a single element (' + option + ' function)');
        else
            return internal_return;
    }
    $.fn.selector = Plugin;
    $.fn.selector.defaults = {
        class: '',
        resources: $.noop,
        selected: [],
        renderSelected: $.noop,
        render: $.noop,
        templates: {
            selected: '',
            selector: ''
        },
        search: '',
        filter: $.noop,
        container: 'body',
        onSearch: $.noop,
        onFilter: $.noop,
        onSelect: $.noop
    }
})();