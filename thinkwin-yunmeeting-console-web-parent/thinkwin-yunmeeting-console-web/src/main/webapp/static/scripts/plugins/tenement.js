/**
 * Created by dell on 2018/7/16.
 */
(function(){
    'use strict';
    var Tenement = function Tenement(element,options) {
        this.element = $(element);
        this.o = $.extend({},$.fn.tenement.defaults,options);//传入参数合并
        this.plugin=$('<ul class="tenement"></ul>');
        this.selected=[];
        this.searchKey;
        var self = this;
        this.element.on('focus', '.select-custom', function (e) {//组件初始化
            e.stopPropagation();
            var that =$(this);
            self.init();
            self.element.find(".select-custom").removeClass("error-box");
            self.element.parents("#selectLssee").find(".warningBox").hide();
            setTimeout(function () {
                self.searchKey=that.val().trim();
                if(self.searchKey!=""){
                    self.fetch();
                }
            },1000)
        });
        this.element.on('keyup','.select-custom',function(e){
            e.stopPropagation();
            var that =$(this);
            setTimeout(function () {
                self.searchKey=that.val().trim();
                if(self.searchKey!=""){
                    self.fetch();
                }else{
                    self.element.find(".select-custom").attr({"name":""}).val("");
                    self.hide();
                }
            })
        });
    }
    Tenement.prototype = {
        init:function(){
            var self=this;
            self.selected=[];
        },
        bindEvent: function(){
            var self = this;
            this.plugin.on('click','.dropdown-item',function(event){
                self.selected=[];
                var item= $(this).data();
                var index = _.findIndex(self.selected, item);
                if(index < 0){
                  self.selected.push(item);
                  self.hide();
                }
            });
        },
        fetch:function fetch() {
            var self = this;
            fetchs.post("/getAllTenants",{'seachKey':self.searchKey},function(result){
                self.element.append(self.plugin);
                self.plugin.empty();
                if(result.ifSuc==1){
                    self.data=result.data;
                    self.rander(result.data);
                    self.place();
                    self.o.class && self.plugin.addClass(self.o.class);
                    self.bindEvent();
                }else{
                    notify("danger",result.msg);
                }
            })
        },
        rander:function rander(res) {
            var self=this;
            this.plugin.append(soda(self.o.template,res));
        },
        //定位
        place:function place(){
            var self=this;
            var element = this.element.offset(),
                scrollTop = this.o.container == 'body' ? 0 : $(this.o.container).scrollTop(),
                container = $(this.o.container).offset();
            self.plugin.css({
                top: element.top - container.top + scrollTop,
                left: element.left - container.left
            });
        },
        hide: function(){
            var self = this;
            this.plugin.remove();
            this.trigger('hidden');
        },
        //触发监听事件
        trigger:function trigger(event){
            this.element.trigger({
                type: event + ".bs.tenement",
                selected: this.selected,
            });
        }
    }
    var Plugin = function(option){
        var args = Array.apply(null, arguments);
        args.shift();
        var internal_return;
        this.each(function(){
            var $this = $(this),
                data = $this.data('Tenement'),
                options = typeof option === 'object' && option;
            if (!data){
                var opts = $.extend({}, $.fn.tenement.defaults, options);
                data = new Tenement(this, opts);
                $this.data('Tenement', data);
            }
            if (!data && /destroy|hide/.test(option)) return;
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
    $.fn.tenement = Plugin;
    $.fn.tenement.defaults = {
        class: '',
        container: 'body',
        template:'<li class="dropdown-item" data-id="{{item.id}}" data-name="{{item.tenantName}}" ng-repeat="item in list">{{item.tenantName}}</li><div class="nothing" ng-if="list.length<1">无搜索结果</div>',//data-toggle="tooltip" data-title=""
    }
})();