'use strict';

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

(function () {
  var Labels = function Labels(element,options){
    this.type="labels";//组件类型
    this.opt = $.extend({},$.fn.labels.defaults,options);//传入参数合并
    this.shown = false;//组件默认不显示
    this.element = $(element);//获取注册dom节点
    this.searchKey="";//搜索关键测词
    this.data="";//标签数据
    this.selected = [];//所有选中标签
    this.select=[];//当前选择标签
    this.add=[];//新建标签
    this.delete=[];//删除标签
    this.plugin = $('<div class="labels"><ul class="labels-list"></ul><p class="creat-label"><input class="form-control" type="text" maxlength="10" /><button class="btn btn-primary creat-label-btn">创建</button></p></div>');//标签容器模板
    var self = this;
    this.element.on('click', '.tags', function (e) {//组件初始化
      e.stopPropagation();
      self.init();
    });
    this.element.on('click', 'span.add i', function (event) {//删除标签
      event.stopPropagation();
      var item = $(this).parent('span').data();
      var index = _.findIndex(self.selected, item);
      self.delete=[];
      self.delete.push(item);
      self.trigger('delete');
    });
  } 
  Labels.prototype={
    //初始化
    init:function init(){
      var self=this;
      this.opt.init(this.element);
      this.trigger("init");
      if (!this.shown) {
        this.show();
      };
      !this.shown && this.element.on('click', $.proxy(this.show, this));
    },
    //显示
    show: function show(el){
      var self = this;
      var initial = [];
      this.shown = true;
      self.selected=[];
      this.element.append(this.plugin);
      this.place();
      this.opt.class && this.plugin.addClass(this.opt.class);
      if(self.opt.type==2){
        this.element.find('span[data-id].add').map(function (i, el) {
          initial.push($(el).data());
        });
        self.selected = initial;
      }
      this.fetch();
      this.bindEvent();
     /* this.opt.shown(this.element);*/
      this.trigger('shown');
    },
    //定位
    place:function place(){
      var element = this.element.offset(),
          scrollTop = this.opt.container == 'body' ? 0 : $(this.opt.container).scrollTop(),
          container = $(this.opt.container).offset();
      this.plugin.css({
        top: element.top - container.top + scrollTop,
        left: element.left - container.left
      });
    },
    //获取数据
    fetch:function fetch(){
      var self = this;
      fetchs.post("/selAssemblyPlatformProgrameLabels",{},function(result){
        if(result.ifSuc==1){
          self.data=result.data;
          self.rander();
        }else{
          notify("danger",result.msg);
        }
      })
    },
    //数据渲染
    rander:function rander(){
      var self = this;
      var notingTpl ='<div class="nothing" ng-if="list.length<1">暂时没有标签</div>';
      if(self.data.list.length){
        this.plugin.find('.labels-list').html('').append(soda(this.opt.template,{"data":self.data}));
      }else{
        this.plugin.find('.labels-list').html('').append(soda(notingTpl,{"data":self.data}));
      };
      if(self.selected){
        if (self.selected.length > 0) {
          $.each(self.selected, function (index,item) {
            if(item.id){
              self.plugin.find('li[data-id=' + item.id+ ']').addClass('active');
            }
          });
        };
      }
    },
    choose:function choose(arr,state){
      var self = this;
      if(state){
        self.selected.push(arr[0]);
        var tpl='<span class="add" data-id="' + arr[0].id +'" data-label-name="' + arr[0].labelName + '">' + arr[0].labelName + '<i class="icon icon-delete-personnel"></i></span>';
        self.element.find('.tags').before(tpl);
      }
    },
    deleted:function deleted(arr,state){
      var self = this;
      if(state){
        var index = _.findIndex(self.selected,arr[0]);
        self.selected.splice(index, 1);
        self.element.find('span.add[data-id="' + arr[0].id + '"]').hide().remove();
        self.plugin.find('li[data-id="' + arr[0].id + '"]').removeClass('active');
      }
    },
    created:function created(arr,state){
      var self = this;
      if(state){
        self.element.find(".creat-label-btn").attr("disabled",false);
        var tpl='<span class="add" data-id="' + arr[0].id +'" data-label-name="' + arr[0].labelName + '">' + arr[0].labelName + '<i class="icon icon-delete-personnel"></i></span>';
        self.element.find('.tags').before(tpl);
      }else{
        self.element.find(".creat-label-btn").attr("disabled",true);
      };
    },
    //事件绑定
    bindEvent:function bindEvent(){
      var self = this;
       //选择
      this.plugin.on('click', '.labels-list li', function () {
        var item= $(this).data();
        var index = _.findIndex(self.selected, item);
        var tpl = '<span class="add" data-id="' + item.id +'" data-label-name="' + item.labelName + '">' + item.labelName + '<i class="icon icon-delete-personnel"></i></span>';
        $(this).toggleClass('active');
        if(index < 0 && self.opt.type == 1) {//人员单选
          $(this).addClass('active').siblings().removeClass('active');
          self.selected=[];
          self.selected.push(item);
          self.trigger('selected');
          self.hide();
        } else if (index < 0 && self.opt.type == 2) {//人员多选
          self.select=[];
          self.select.push(item);
          self.trigger('selected');
        }else{
          self.delete=[];
          self.delete.push(item);
          self.trigger('unselect');
        }
      });
      this.plugin.on('click', '.creat-label .creat-label-btn', function (e) {
          e.stopPropagation();
          e.preventDefault();
          var newName =self.plugin.find(".creat-label input").val().trim();
          self.add=[];
          if(newName){
            var isTrue = !/^[\u4e00-\u9fa5a-zA-Z0-9\\\-()（）]{0,10}$/.test(newName)?  false : true ;
            if(isTrue){
                self.add.push({"id":"","labelName":newName});
                self.trigger('creatlabel');
                return true;
            }else {
                notify('danger', '标签名称不合法');
                return false;
            }
          }else{
            notify('danger', '标签名称不为空');
          }
      });      
      //组件隐藏
      $(window).on('resize', function () {
        self.place();
      });
      this.plugin.on('click', function (event) {
        event.stopPropagation();
      });
      $('body').on('click', function (e) {
        if (self.shown && self.element.find(e.target).length < 1) {
          self.hide();
        };
      });
      $('body .custom-checkbox').on('click', function (e) {
        if (self.shown && self.element.find(e.target).length < 1) {
          self.hide();
        }
      });
    },
    //隐藏
    hide:function hide(){
      var self = this;
      self.plugin.find(".creat-label input").val("");
      this.plugin.remove();
      this.shown = false;
      this.trigger('hidden');
    },
    //触发监听事件
    trigger:function trigger(event){
      this.element.trigger({
        type: event + ".bs.labels",
        selected: this.selected,
        select:this.select,
        delete:this.delete,
        add:this.add,
      });
    }
  };
  var labelsPlugin=function personsPlugin(option,arr,state){
    var args = Array.apply(null, arguments);
    args.shift();
    var internal_return;
    this.each(function () {
      var $this = $(this);
      var data = $this.data('labels');
      var options = (typeof option === 'undefined' ? 'undefined' : _typeof(option)) === 'object' && option;
      if (!data && /destroy|hide/.test(option)) return;
      if (!data) $this.data('labels', data = new Labels(this, options));
      if (typeof option === 'string' && typeof data[option] === 'function'){
        internal_return = data[option].apply(data,args);
      }
    });
    if (this.length > 1)
      throw new Error('Using only allowed for the collection of a single element (' + option + ' function)');
    else
      return internal_return;
  }
  $.fn.labels = labelsPlugin;
  $.fn.labels.defaults={
    class: null,
    type: 2, //单选 1 多选 2
    container: 'body',
    template: '<li class="labels-item" ng-repeat="item in data.list" data-id="{{item.id}}" data-label-name="{{item.labelName}}"><i class="icon icon-kzt-display"></i><span>{{item.labelName}}</span></li>',
    init: function init() {}, // 回调函数
    show: function show() {}, // 回调函数
    select: function select() {} // 回调函数
  };
})()