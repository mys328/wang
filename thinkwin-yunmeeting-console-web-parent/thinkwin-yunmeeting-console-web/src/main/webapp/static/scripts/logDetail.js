'use strict';
var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };
(function(){
  var Details = function Details(element,options){
    //参数初始化
    this.type="details";//组件类型
    this.opt=$.extend({},$.fn.details.defaults,options);
    this.element = $(element);//获取注册dom节点
    this.searchKey='';//搜索关键词
    this.dataObj={};
    this.pageObj={
      currentPage:1,
      pageSize:15,
      total:0,
    };
    this.eventtype="";
    this.currentItem="";
    this.plugin=$('<div class="log-details-box"></div>');//模板容器
    this.template = '';
    this.zTreeObj = '';
    this.labelsList=[];
  }
  //原型注册事件
  Details.prototype={
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
      this.shown = true;
      self.searchKey='';
      this.labelsList=[];
      var searchType={//搜索的placeholder
          placeholderCon:""
      };
      var sameTpl='<div class="top"><span class="user-name"></span><button class="btn btn-primary btn-sm btn-closed">返回</button></div>';
      var searchTpl='<div class="top"><div class="form-control input-search"><i class="icon icon-search"></i><input id="search" type="text" maxlength="100" placeholder="{{placeholderCon}}" ><i class="icon icon-search-del" id="del-searchList"></i></div><span class="col-label customer-name">错误日志：</span><span class="col-label customer-num"></span></div>'
      if(self.opt.type==0){//全部错误日志
        sameTpl+='<div class="logs-box"><div class="logs-left"><div class="treeBox"><ul class="ztree" id="treeDemo"></ul></div></div><div class="logs-right">'+searchTpl+'<div class="list-box"></div></div></div>';
          searchType.placeholderCon="搜索租户名称、终端名称、事件、日志内容";
      }else{//租户错误日志
        sameTpl+=searchTpl+'<div class="list-box"></div>';
          searchType.placeholderCon="搜索终端名称、业务类型、事件、日志内容"
      };
      $('body').append(this.plugin.html(soda(sameTpl,searchType)));
      if(self.opt.type==0) $.ajaxSetup({async : false});
      this.search();
      if(self.opt.type==0) self.ztree();
      this.place();
      this.bindEvent();
      this.opt.class && this.plugin.addClass(this.opt.class);
      this.trigger('shown');
    },
    //获取数据
    fetch:function fetch(state){
      var self = this;
      var url,options;
      self.pageObj.currentPage = 1;
      self.pageObj.pageSize = 15;
      if(self.opt.type==0){//全部错误日志
        url = "/tenant/getAllTerminalLog";
        options={"eventType":self.eventtype,"word":self.searchKey,"currentPage":self.pageObj.currentPage,"pageSize":self.pageObj.pageSize};
      }else{//单个租户错误日志
        url = "/tenant/getTenantTerminalLog";
        options={"tenantId":self.opt.userId,"word":self.searchKey,"currentPage":self.pageObj.currentPage,"pageSize":self.pageObj.pageSize};
      };
      fetchs.post(url,options,function(result){
        if(result.ifSuc==1){
          self.dataObj = result.data;
          self.labelsList[0] = { id: 0, pId: "", name: '全部终端错误日志', open: 'true', type: '' };//ztree 数据赋值
          $.each(result.data.types,function(index,item){
              self.labelsList[index + 1]= { id: item.id, pId: item.parentId, name: item.name, open: 'false', type: item.type };
          });
          self.pageObj.total=result.data.total;
          self.dataObj.type=self.opt.type;
          self.rander(state);
        }else{
          notify('danger',result.msg)
        }
        if(self.opt.type==0) $.ajaxSetup({async : true});
      })
    },
    //分页请求
    page:function page(){
          var self = this;
          var url,options;
          if(self.opt.type==0){//全部错误日志
              url = "/tenant/getAllTerminalLog";
              options={"eventType":self.eventtype,"word":self.searchKey,"currentPage":self.pageObj.currentPage,"pageSize":self.pageObj.pageSize};
          }else{//单个租户错误日志
              url = "/tenant/getTenantTerminalLog";
              options={"tenantId":self.opt.userId,"word":self.searchKey,"currentPage":self.pageObj.currentPage,"pageSize":self.pageObj.pageSize};
          };
          self.plugin.parents("body").find(".tooltip").remove();
          fetchs.post(url,options,function(result){
              if(result.ifSuc==1){
                  self.dataObj = result.data;
                  self.plugin.parents("body").find(".tooltip").remove();
                  if(self.pageObj.total>15){
                      if(self.opt.type==0) self.plugin.find(".scroll-list").css({"height":"calc(100% - 56px)"});
                      else if(self.opt.type==1) self.plugin.find(".scroll-list").css({"height":"calc(100% - 56px)"});
                  }else{
                      self.plugin.find(".scroll-list").css({"height":"calc(100% - 0px)"});
                  }
                  result.data.searchKey=self.searchKey;
                  result.data.type=self.opt.type;
                  var tabelTpl = '<div class="scroll-list"><p class="list-pie"></p><table class="table"><tbody><tr><th class="table-1" data-toggle="tooltip"><span ng-if="type==0">租户名称</span><span ng-if="type==1">终端名称</span></th><th class="table-2" data-toggle="tooltip"><span  ng-if="type==1">业务类型</span><span ng-if="type==0">终端名称</span></th><th class="table-3" data-toggle="tooltip" data-title="事件"><span>事件</span></th><th class="table-4" data-toggle="tooltip" data-title="错误时间"><span>错误时间</span></th><th class="table-5" data-toggle="tooltip" data-title="日志内容"><span>日志内容</span></th><th class="table-6" data-toggle="tooltip" data-title="操作"><span>操作</span></th></tr><tr ng-repeat="item in logs"><td class="table-1" data-toggle="tooltip" ng-if="type==1" data-title="{{item.terminalName}}"><span ng-html="item.terminalName|keylight:searchKey"></span></td><td class="table-1" data-toggle="tooltip" ng-if="type==0" data-title="{{item.tenantName}}"><span ng-html="item.tenantName|keylight:searchKey"></span></td><td class="table-2" data-toggle="tooltip" ng-if="type==0" data-title="{{item.terminalName}}"><span ng-html="item.terminalName|keylight:searchKey"></span></td><td class="table-2" data-toggle="tooltip" ng-if="type==1" data-title="{{item.eventname}}"><span ng-html="item.eventname|keylight:searchKey"></span></td><td class="table-3" data-toggle="tooltip" data-title="{{item.businessname}}"><span ng-html="item.businessname|keylight:searchKey"></span></td><td class="table-4" data-toggle="tooltip" data-title="{{item.businesstime|date:\'YYYY-MM-DD HH:mm\'}}"><span>{{item.businesstime|date:"YYYY-MM-DD HH:mm"}}</span></td><td class="table-5" data-toggle="tooltip" data-title="{{item.content}}"><span ng-html="item.content|keylight:searchKey"></span></td><td class="table-6" data-toggle="tooltip" data-title="查看"><span class="btn-view-log btn-span" data-id="{{item.id}}">查看</span></td></tr></tbody></table></div><div class="nothing" ng-if="!total&&!searchKey">暂无错误日志</div><div class="nothing" ng-if="!total&&searchKey">没有搜索到信息，换个条件试试？</br>您可以输入终端名称、业务类型、事件、日志内容等部分内容检索。</div>';
                  self.plugin.find(".tabel-box").empty().html(soda(tabelTpl,self.dataObj));
                  self.plugin.find(".bottom").pages('upcount',result.data.total);
              }else{
                  notify('danger',result.msg)
              }
          })
      },
    //搜索
    search:function search(state){
      var self=this;
      if(state==1){
         this.page();
      }else {
         this.fetch();
      }
    },
    //渲染
    rander:function rander(){
      var self=this;
      var tabelTpl = '<div class="tabel-box" ng-if="total"><div class="scroll-list"><p class="list-pie"></p><table class="table"><tbody><tr><th class="table-1" data-toggle="tooltip"><span ng-if="type==0">租户名称</span><span ng-if="type==1">终端名称</span></th><th class="table-2" data-toggle="tooltip"><span  ng-if="type==1">业务类型</span><span ng-if="type==0">终端名称</span></th><th class="table-3" data-toggle="tooltip" data-title="事件"><span>事件</span></th><th class="table-4" data-toggle="tooltip" data-title="错误时间"><span>错误时间</span></th><th class="table-5" data-toggle="tooltip" data-title="日志内容"><span>日志内容</span></th><th class="table-6" data-toggle="tooltip" data-title="操作"><span>操作</span></th></tr><tr ng-repeat="item in logs"><td class="table-1" data-toggle="tooltip" ng-if="type==1" data-title="{{item.terminalName}}"><span ng-html="item.terminalName|keylight:searchKey"></span></td><td class="table-1" data-toggle="tooltip" ng-if="type==0" data-title="{{item.tenantName}}"><span ng-html="item.tenantName|keylight:searchKey"></span></td><td class="table-2" data-toggle="tooltip" ng-if="type==0" data-title="{{item.terminalName}}"><span ng-html="item.terminalName|keylight:searchKey"></span></td><td class="table-2" data-toggle="tooltip" ng-if="type==1" data-title="{{item.eventname}}"><span ng-html="item.eventname|keylight:searchKey"></span></td><td class="table-3" data-toggle="tooltip" data-title="{{item.businessname}}"><span ng-html="item.businessname|keylight:searchKey"></span></td><td class="table-4" data-toggle="tooltip" data-title="{{item.businesstime|date:\'YYYY-MM-DD HH:mm\'}}"><span>{{item.businesstime|date:"YYYY-MM-DD HH:mm"}}</span></td><td class="table-5" data-toggle="tooltip" data-title="{{item.content}}"><span ng-html="item.content|keylight:searchKey"></span></td><td class="table-6" data-toggle="tooltip" data-title="查看"><span class="btn-view-log btn-span" data-id="{{item.id}}">查看</span></td></tr></tbody></table></div></div><div class="nothing" ng-if="!total&&!searchKey">暂无错误日志</div><div class="nothing" ng-if="!total&&searchKey">没有搜索到信息，换个条件试试？</br>您可以输入终端名称、业务类型、事件、日志内容等部分内容检索。</div><div class="bottom innerPageBox"></div>';//
      self.dataObj.searchKey = self.searchKey;
      if(self.opt.type==0){
        this.plugin.find(".user-name").html("错误日志");
      }else{
        this.plugin.find(".user-name").html(self.dataObj.tenantName);
      }
      this.plugin.find(".customer-num").html(self.dataObj.total+"个");
      this.plugin.find(".list-box").empty().html(soda(tabelTpl,self.dataObj));
      if(self.pageObj.total>15){
          this.plugin.find(".bottom").show();
          if(self.opt.type==0) this.plugin.find(".scroll-list").css({"height":"calc(100% - 56px)"});
          else if(self.opt.type==1) this.plugin.find(".scroll-list").css({"height":"calc(100% - 56px)"});
          this.plugin.find(".bottom").pages({
              change:function(current,size){
                  self.pageObj.currentPage = current;
                  self.pageObj.pageSize = size;
                  self.search(1);
              },
              count: 0
          });
          this.plugin.find(".bottom").pages('upcount',self.pageObj.total);
      }else{
          this.plugin.find(".scroll-list").css({"height":"calc(100% - 0px)"});
          this.plugin.find(".bottom").hide();
      }
    },
    //ztree插件
    ztree:function ztree(){
      var self=this;
      var setting = {
        edit: {
          enable: true,
          showRemoveBtn: false,
          showRenameBtn: false,
          drag: {
            prev: false,
            next: false,
            inner: false,
            isCopy: false,
            isMove: false
          }
        },
        data: {
          simpleData: {
            enable: true
          }
        },
        callback: {
          onClick: onClick
        },
        view: {
          dblClickExpand: false,
          open: false,
          showLine: false,
          showTitle: false,
          addDiyDom: addDiyDom
        }
      };
      var lastSelectedNode;
      zTreeObj = $.fn.zTree.init(self.plugin.find('#treeDemo'),setting,self.labelsList);
      //设置默认第一个选中
      var aObj = $('#treeDemo_1' + ' >div#heightLight');
      aObj.addClass("on");
      lastSelectedNode = aObj;
      //左侧点击事件
      function onClick(e, treeId, treeNode) {
        var zTree = $.fn.zTree.getZTreeObj('treeDemo');
        if (lastSelectedNode.length > 0) {
          lastSelectedNode.removeClass("on");
        }
        var aObj = $('#' + treeNode.tId + ' >div#heightLight');
        aObj.addClass("on");
        lastSelectedNode = aObj;
        zTree.expandNode(treeNode);
        if (treeNode.pId==="") {//一级节点
          self.eventtype = '';
        } else {
          self.eventtype = treeNode.type;
        }
        self.pageObj.currentPage = 1;
        self.pageObj.pageSize = 15;
        self.clearSearch();
        self.plugin.find(".bottom").pages('destroy');
        self.fetch();
      };
      function addDiyDom(treeId, treeNode) {
        var wrapObj = $('#' + treeNode.tId + ' > span');
        var div = '<div id="heightLight" class="heightLight" ></div>';
        wrapObj.before(div);
      }
    },
    //隐藏
    hide:function hide(){
      var self = this;
      this.plugin.remove();
      this.plugin.find(".bottom").pages('destroy');
    },
    //位置
    place:function place(){
      var element = this.element.offset(),
      scrollTop = this.opt.container == 'body' ? 0 : $(this.opt.container).scrollTop(),
      container = $(this.opt.container).offset();
      this.plugin.css({
        top: element.top - container.top + scrollTop,
        left: element.left - container.left
      });
    },
    clearSearch:function clearSearch() {
      var self=this;
      self.plugin.find(".icon-search-del").hide();
      self.plugin.find('.input-search input').val("");
      self.searchKey="";
    },
    //事件绑定
    bindEvent:function bindEvent(){
      var self = this;
      //返回
      this.plugin.on('click', '.btn-closed', function () {
        self.hide();
      });
      //查看日志
      this.plugin.on('click', '.btn-view-log', function () {
        var current_id = $(this).data("id");
        $.each(self.dataObj.logs,function(item,ele){
          if(ele.id==current_id){
            self.currentItem = ele
            return true;
          }
        })
        $("#viewLog .modal-body").empty().html(self.currentItem.errorLogContent);
        $("#viewLog").modal("show");
      });
      //搜索
      this.plugin.on("keypress",".top .input-search #search",function(e){
        var that=$(this);
        if(e.keyCode==13){
          e.preventDefault();
          self.searchKey=that.val().trim();
          self.search();
        }
      });
      this.plugin.on("input propertychange","#search",function() {
        if($(this).val().length>0){
          self.plugin.find(".icon-search-del").show();
        }else{
          self.plugin.find(".icon-search-del").hide();
        }
      });
      this.plugin.on('focus','.input-search input',function () {
        self.plugin.find('.input-search').addClass('border-shadow');
      })
      this.plugin.on('blur','.input-search input',function () {
        self.plugin.find('.input-search').removeClass('border-shadow');
      })
      this.plugin.on('click','.icon-search-del',function () {
        self.clearSearch();
        self.search();
      });
      //表格的移除省略号提示语效果
      $('body').on('mouseenter', '[data-toggle="tooltip"]', function (e) {
          var text = $(this).data('title');
          var id = $(this).data('id');
          var size = parseInt($(this).css('fontSize'));
          if ($(this).width() < size * text.length) {
              $(this).tooltip({
                  placement: function placement(tip, element) {
                      if (id == '6' || id == '5') {
                          return 'left';
                      }
                      return window.scrollY + 50 < $(element).offset().top ? 'top' : 'bottom';
                  },
                  trigger: 'hover'
              });
              $(this).tooltip('toggle');
          }
      });
    },
    //触发监听事件
    trigger:function trigger(event){
      this.element.trigger({
        type: event + ".bs.details",
      });
    }
  }
  //传入参数处理
  var detailsPlugin =function detailsPlugin(option,arr,state){
    var args = Array.apply(null, arguments);
    args.shift();
    var internal_return;
    this.each(function () {
      var $this = $(this);
      var data = $this.data('details');
      var options = (typeof option === 'undefined' ? 'undefined' : _typeof(option)) === 'object' && option;
      if (!data && /destroy|hide/.test(option)) return;
      if (!data) $this.data('details', data = new Details(this, options));
      if (typeof option === 'string' && typeof data[option] === 'function'){
        internal_return = data[option].apply(data,args);
      }
    });
    if (this.length > 1)
      throw new Error('Using only allowed for the collection of a single element (' + option + ' function)');
    else
      return internal_return;
  };
  $.fn.details=detailsPlugin;
  //组件默认参数
  $.fn.details.defaults={
      class:null,
      type:null,
      container:'body',
      template:''
  };
})();