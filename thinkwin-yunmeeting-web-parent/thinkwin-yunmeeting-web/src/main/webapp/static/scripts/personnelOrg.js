'use strict';

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

(function () {
  'use strict';
  /*
   * @name Persons 人员组件
   * @description
   * 提供单位人员、人员多选、人员单选、单位多选、单位单选五种模式；同时还可限制人数
   * @event 事件
   * shown.bs.persons: 显示事件
   * delete.bs.persons: 删除事件
   * clear.bs.persons:清除事件
   * selected.bs.persons: 选择事件
   * unselect.bs.persons: 取消选择事件
   * hidden.bs.persons: 隐藏事件
     $(selector).on(events, function(event) {
       console.log(event.selected) 选中数据，跟随选择/取消选择变动
     })
   * @usage //人员组件调用
   * 部门类型
   * html <div id="selector"><span class="trigger"></span></div>
   * //trigger 触发按钮必须使用该class名
   * js注册
     $("selector").persons({
       class:'person', // 组件自定义类名
       type:3,         // 组件的类型  0 人员部门 1人员单选 2人员多选 3部门单选 4部门多选
       orgId:1,        // 组织id 初始值传入必须为 1
       container:'selector', // 组件显示的位置
       hierarchies:true, // 是否返回组件层级结构
       template:'<ul class="list groups">'+
                  '<li class="" ng-repeat="item in data.sysOrganizations" data-id="{{item.sysOrganization.id}}">'+
                    '<p data-leaf="{{item.leaf}}">'+
                      '<span class="icon icon-organiz-unit"></span>'+
                      '<span class="name" ng-html="item.sysOrganization.orgName|keylight:key"></span>'+
                    '</p>'+
                  '</li>'+
                '</ul>',// type:3 , 4
     });
    * 人员类型
    *  $("selector").persons({
         class:'person',
         type:2,
         orgId:1,
         identical:true, // 触发按钮和选择结果合并 使用 .trigger 中显示选择结果
         personsLimit:3, // 人员单选时的人员限制(管理设置单元有人员数量的限制)
         container:'selector',
         template:'<ul class="list groups">' +
                    '<li class="nopt" ng-repeat="item in data.sysOrganizations" data-id="{{item.sysOrganization.id}}"><p data-leaf="{{item.leaf}}"><span class="icon icon-organiz-unit"></span><span class="name" ng-html="item.sysOrganization.orgName|keylight:key"></span></p></li>' +
                  '</ul>' +
                  '<div class="line" ng-if="data.sysUser.length>0"></div>' +
                  '<ul class="list personnel">' +
                    '<li ng-repeat="item in data.sysUser" class="" data-id="{{item.id}}"><span class="nophoto small" ng-if="!item.smallPicture"><i>{{item.icon}}</i></span><img class="user-url" ng-if="item.smallPicture" src="{{item.smallPicture}}"><span class="name" ng-html="item.name|keylight:key"></span><span ng-if="item.participantStatus" class="conflict">(<i class="icon icon-error"></i>有会议冲突)</span></li>' +
                  '</ul>' // type:0 , 1 , 2
      });
     Template:  //type 1 (人员单选模板)
     '<ul class="list groups">' +
     '<li class="nopt" ng-repeat="item in data.sysOrganizations" data-id="{{item.sysOrganization.id}}"><p data-leaf="{{item.leaf}}"><span class="icon icon-organiz-unit"></span><span class="name" ng-html="item.sysOrganization.orgName|keylight:key"></span></p></li>' +
     '</ul>' +
     '<div class="line" ng-if="data.sysUser.length>0"></div>' +
     '<ul class="list personnel">' +
     '<li ng-repeat="item in data.sysUser" class="" data-id="{{item.id}}"><span class="nophoto small" ng-if="!item.smallPicture"><i>{{item.icon}}</i></span><img class="user-url" ng-if="item.smallPicture" src="{{item.smallPicture}}"><span class="name" ng-html="item.name|keylight:key"></span><span ng-if="item.participantStatus" class="conflict">(<i class="icon icon-error"></i>有会议冲突)</span></li>' +
     '</ul>'

   */
  var Persons = function Persons(element, options) {
    // 初始变量
    this.type = 'persons';
    this.opt = $.extend({}, $.fn.persons.defaults, options);
    this.shown = false;
    this.key = '';
    this.selected = [];
    this.delete=[];
    this.orgerId = null;
    this.add = [];
    this.organizer = null;
    this.bread = [{ id: 1, name: '全部' }];
    this.element = $(element);
    this.plugin = $('<div class="persons"><div class="search"><i class="icon icon-search"></i><input type="text" class="form-control form-control-lg" placeholder="搜索"><span class="icon icon-close btn-cancel-search" style="display: none;"></span></div><div class="bread"><ol><li data-id="1">全部</li></ol><p class="search-info" style="display: none;"></p><span class="btn-select-all" name="0">全选</span><span class="btn-eliminate-all" name="0">清除</span></div><div class="list-box"></div></div>');
    var self = this;
    this.element.on('click', '.trigger', function () {
      self.init();
    });
    this.element.on('click', 'span.add i', function (event) {
      event.stopPropagation();
      var item = $(this).parent('span').data();
      var index = _.findIndex(self.selected, item);
      self.delete=[];
      self.delete.push(item);
      self.selected.splice(index, 1);
      self.trigger('delete');
      $(this).parent('span.add').hide().remove();
      self.plugin.find('li[data-id="' + item.id + '"]').removeClass('active').find('p').removeClass('leaf');
      self.textChange();
      self.getNums();
    });
  };
  Persons.prototype = {
    // 初始化
    init: function init() {
      var self = this;
      this.bread = [{ id: 1, name: '全部' }];
      this.opt.init(this.element);
      this.trigger('init');
      if (!this.shown) {
        this.show();
      };
      !this.shown && this.element.on('click', $.proxy(this.show, this));
    },
    // 显示
    show: function show(data) {
      var self = this;
      var selected = [];
      this.shown = true;
      this.element.append(this.plugin);
      if(this.opt.hierarchies){
        this.element.find('.persons').css('width',this.element.outerWidth());
      }
      this.place();
      this.opt.class && this.plugin.addClass(this.opt.class);
      if([3].indexOf(self.opt.type) >= 0&&!self.opt.hierarchies) {
        self.plugin.find('span.btn-eliminate-all').show();
      }
      if([0,2,4].indexOf(self.opt.type) >= 0){
        self.plugin.find('span.btn-select-all').show();
      };
      if(self.opt.hierarchies&&this.selected.length<1){
        self.selected.push({'id':self.element.find('.trigger').attr("name")});
      };
      if([0,1, 2].indexOf(self.opt.type) >= 0){
          if(!self.opt.identical){
              this.element.find('span[data-id]').map(function (i, el) {
                  selected.push($(el).data());
              });
          }else{
              this.element.find('span.trigger').map(function (i, el) {
                  selected.push({"id":$(el).attr('name')});
              });
          }
          self.selected = selected;
      };
      if ($('.detail').length > 0) {
          this.orgerId = $('.detail #organizer input').val();
      }
      this.fetch(1);
      this.renderBread();
      this.bindEvent();
      this.opt.shown(this.element);
      this.trigger('shown');
    },
    // 定位
    place: function place() {
      var element = this.element.offset(),
          scrollTop = this.opt.container == 'body' ? 0 : $(this.opt.container).scrollTop(),
          container = $(this.opt.container).offset();
      this.plugin.css({
        top: element.top - container.top + scrollTop,
        left: element.left - container.left
      });
    },
    // 渲染数据
    render: function render(data) {
      var self = this;
      var key = self.key;
      $.map(data.sysOrganizations, function (item) {
          var name = key != '' ? item.sysOrganization.orgName.replace(new RegExp(key, 'g'), '<font color="#FA702">' + key + '</font>') : item.sysOrganization.orgName;
          item.sysOrganization.orgName = name;
          if(self.opt.type==0){
              item.org=1;
          }
          if([0,1, 2].indexOf(self.opt.type) >= 0){
              item.leaf= 'true';
          }
      });
      $.map(data.sysUser.list, function (item) {
          item.name = key != '' ? item.userName.replace(new RegExp(key, 'g'), '<font color="#FA702">' + key + '</font>') : item.userName;
          if (item.userName != null && item.userName.length > 2) {
              item.icon = item.userName.slice(-2);
          } else if (item.userName != null && item.userName.length == 2) {
              item.icon = item.userName.slice(-1);
          } else {
              item.icon = item.userName;
          }
          if(self.opt.type==0){
              item.org=0;
          }
      });
      var notingTpl ='<div class="persons-nothing" ng-if="data.sysOrganizations.length<1&&data.key==\'\'">暂时没有部门信息</div><div class="persons-nothing" ng-if="data.sysOrganizations.length<1&&data.key!=\'\'">没有搜索到信息</div>';
      if(data.sysOrganizations.length>0&&(self.opt.hierarchies||!self.opt.hierarchies)||data.sysUser.list.length>0){
        this.plugin.find('.list-box').html('').append(soda(this.opt.template, { data: { 'sysOrganizations': data.sysOrganizations, 'sysUser': data.sysUser.list} }));
      }else{
        this.plugin.find('.list-box').html('').append(soda(notingTpl, { data: { 'sysOrganizations': data.sysOrganizations, 'sysUser': data.sysUser.list,'key':self.key} }));
      }
      if (self.selected.length > 0) {
        $.map(self.selected, function (item) {
          self.plugin.find('li[data-id=' + item.id+ ']').addClass('active');
          self.plugin.find('li[data-id=' + item.id+ '] p').addClass('leaf');
        });
      };
      this.trigger('rendered');
      self.textChange();
    },
    // 面包线渲染
    renderBread: function renderBread() {
      var self = this;
      self.plugin.find('.bread ol').html('');
      $.map(self.bread, function (item) {
        self.plugin.find('.bread ol').append('<li data-id="' + item.id + '" data-len="5" data-placement="top" data-title="' + item.name + '">' + item.name.substring(5, 0) + '</li>');
      });
      this.trigger('renderBread');
    },
    select: function select(data) {},
    // 移除 隐藏
    hide: function hide(callback) {
      var self = this;
      if(self.opt.hierarchies){
          this.selected=[];
      }
      if (self.key) {
        self.key = '';
        self.plugin.find('.btn-cancel-search').hide();
        self.plugin.find('.search input').val('');
        self.plugin.find('.search-info').hide().html('');
        self.plugin.find('.bread ol').show();
        self.fetch(1);
      };
      this.plugin.remove();
      this.shown = false;
      this.trigger('hidden');
    },
    // 销毁
    destroy: function destroy() {
      var self = this;
      this.hide();
    },
    //搜索
    search: function search() {
      var self = this;
      var url='';
      var searchKey = encodeURI(self.key);
      var timerange = this.element.parents('form').find('#timerange');
      if (self.opt.type == 0&& timerange.children('input[name="start"]').val() != ''){
          var startTime = moment(parseInt(timerange.children('input[name="start"]').val())).format('YYYY-MM-DD H:mm');
          var endTime = moment(parseInt(timerange.children('input[name="end"]').val())).format('YYYY-MM-DD H:mm');
          var conferenceId = this.element.parents('form').find('input[name="conferenceId"]').val();
          url = '/search?userId=' + _userInfo.userId + '&startTime=' + startTime + '&endTime=' + endTime + '&searchParameter=' + searchKey + '&token=' + _userInfo.token;
          if (conferenceId != undefined) {
              url+='&conferenceId='+conferenceId;
          }
      }else {
          url= '/search?userId=' + _userInfo.userId + '&searchParameter=' + searchKey + '&token=' + _userInfo.token;
      }
      fetchs.get(url, function (data) {
          console.log(data);
          self.data = data.data;
          self.bread = [{ id: 1, name: '全部' }];
          self.render(self.data);
          self.renderBread();
      });
    },
    // 点击部门请求+初始化 获取数据
    fetch: function fetch(id) {
      var self = this;
      var url='';
      var timerange = this.element.parents('form').find('#timerange');
      if (self.opt.type == 0 && timerange.children('input[name="start"]').val() != ''){
          var startTime = moment(parseInt(timerange.children('input[name="start"]').val())).format('YYYY-MM-DD H:mm');
          var endTime = moment(parseInt(timerange.children('input[name="end"]').val())).format('YYYY-MM-DD H:mm');
          var conferenceId = this.element.parents('form').find('input[name="conferenceId"]').val();
          url = '/sysUsersByOrgId?userId=' + _userInfo.userId + '&startTime=' + startTime + '&endTime=' + endTime + '&orgId=' + id + '&token=' + _userInfo.token;
          if (conferenceId != undefined) {
              url+='&conferenceId='+conferenceId;
          }
      }else {
          url = '/sysUsersByOrgId?userId=' + _userInfo.userId +'&orgId=' +id+'&type=' + self.opt.type +'&max='+self.opt.max+'&token=' + _userInfo.token;
      }
      fetchs.get(url, function (data) {
          self.data = data.data;
          self.render(self.data);
      });
    },
    // 插件操作事件绑定
    bindEvent: function bindEvent() {
      var self = this;
      //选择
      this.plugin.on('click', '.list li', function () {
        var item = $(this).data();
        var name="";
        if($(this).find('span.name .conflict').length){
            name= $(this).find('span.name span:nth-child(1)').text();
        }else{
            name= $(this).find('span.name').text();
        }
        var index = _.findIndex(self.selected, item);
        var tpl = '<span class="add" data-id="' + item.id + '"' + (item.org != undefined ? 'data-org="' + item.org + '"' : '') + '>' + name + '<i class="icon icon-delete-personnel"></i></span>';
        if (self.opt.personsLimit && self.selected.length >= self.opt.personsLimit && index < 0) {
          return false;
        };
        if (self.opt.type == 0 && self.orgerId == item.id) {
            return false;
        }
        if(self.opt.type!==1){
            $(this).toggleClass('active').find('p').toggleClass('leaf');
        }
        if (index < 0 && [0, 4].indexOf(self.opt.type) >= 0) {//部门多选
          self.element.find('.trigger').before(tpl);
          self.selected.push(item);
          self.add.push(item);
          self.getNums();
        } else if (index < 0 && self.opt.type == 3) {//部门单选
          $(this).siblings().removeClass('active').find('p').removeClass('leaf');
          if(self.opt.hierarchies){
              var bread = $('#leftMenuTop h4').text();
              $.map(self.bread, function(v,i){
                  if (i!=0){
                      bread += '<span class="sign"></span>'+v.name;
                  }
              });
              bread += '<span class="sign"></span>'+name;
              self.element.find('.trigger').html(bread).attr({'name':item.id}).addClass('label-color');
          }else{
              self.element.find('.trigger').html(name).attr({ 'name': item.id }).addClass('label-color');
          }
          self.selected.splice(index, 1);
          self.selected.push(item);
          self.trigger('selected');
        } else if (index < 0 && self.opt.type == 1) {//人员单选
          $(this).addClass('active').siblings().removeClass('active');
          if(!self.opt.identical){
            self.element.find('.add').remove();
            self.element.find('.trigger').before(tpl);
          }else{//触发和显示合并
            self.element.find('.trigger').html(name);
            self.element.find('.trigger').attr('name',item.id);
            self.organizer = _.filter(self.data.sysUser.list,function(user) {
                return user.id == item.id;
            })[0];
          }
          self.selected.splice(index, 1);
          self.selected.push(item);
          self.trigger('selected');
        } else if (index < 0 && self.opt.type == 2) {//人员多选
          self.selected.push(item);
          self.element.find('.trigger').before(tpl);
          self.trigger('selected');
        } else {
          if(self.opt.type!==1){
            self.selected.splice(index, 1);
          }
          if (self.opt.type == 3) {
            self.element.find('.trigger').html('选择部门').attr({ 'name': '' }).removeClass('label-color');
          };
          if ([0,2,4].indexOf(self.opt.type) >= 0) {
            self.element.find('span[data-id="' + item.id + '"]').remove();
            self.getNums();
          };
          $(this).find('p').removeClass('leaf');
          self.trigger('unselect');
        }
        self.textChange();
      });
      // 面包线点击
      this.plugin.on('click', '.bread li', function () {
        var id = $(this).data('id');
        var index = $(this).index();
        self.bread.splice(index + 1, self.bread.length - index + 1);
        self.fetch(id);
        self.renderBread();
      });
      // 组织名称溢出显示label
      this.plugin.on('mouseover', '.bread li', function () {
        var title = $(this).data('title');
        if (title.length > 5 && $(this).css('font-size') != '0px') {
          $(this).find('.title-label').remove();
          $(this).append('<div class="title-label">' + title + '</div>');
          var cutWidth = (Number($('.title-label').outerWidth()) - Number($(this).outerWidth()) + 9) / 2;
          var top = $(this).offset().top - $(this).parents('.persons').offset().top - $('.title-label').outerHeight()-6;
          var left = $(this).offset().left - $(this).parents('.persons').offset().left - cutWidth;
          $('.title-label').css({ 'left': left + 'px', 'top': top + 'px' });
        }
      });
      this.plugin.on('mouseout', '.bread li,.bread li .title-label', function () {
        var title = $(this).data('title');
        if (title.length > 5&&title) {
          $(this).find('.title-label').remove();
        }
      });
      this.plugin.on('mouseover mouseout', '.bread li .title-label', function (event) {
        event.stopPropagation();
      });
      this.plugin.on('mouseout', '.bread li .title-label', function (event) {
        $(this).remove();
      });
      // 跳转到下一级
      this.plugin.on('click', '.groups p', function (event) {
        event.stopPropagation();
        if($(this).hasClass('leaf')){
          return false;
        }else{
          if(($(this).data('leaf') == true &&[3, 4].indexOf(self.opt.type) >= 0)||[0,1, 2].indexOf(self.opt.type) >= 0){
              var id = $(this).parent('li').data('id');
              var name = $(this).children('span.name').text();
              self.bread.push({ id: id, name: name });
              if (self.key) {
                  self.plugin.find('.search input').val('');
                  self.plugin.find('.btn-cancel-search').hide();
                  self.plugin.find('.btn-cancel-search').hide();
                  self.plugin.find('.search input').val('');
                  self.plugin.find('.search-info').hide().html('');
                  self.plugin.find('.bread ol').show();
                  self.key="";
              };
              self.fetch(id);
              self.renderBread();
          }
        }
      });
      // 搜索
      this.plugin.on('keydown', '.search input', function (event) {
        var key = $.trim($(this).val());
        if (event.keyCode == 13 && key != '') {
            event.preventDefault();
          if (!/[@#\$%\^&\*]+/g.test(key)) {
            self.key = key;
            self.search();
            self.plugin.find('.bread ol').hide();
            self.plugin.find('.btn-cancel-search').show();
            self.plugin.find('.search-info').show().html('搜索“<span style="color: #1896f0;">' + key + '</span>”的结果');
          } else {
            notify('danger', '请输入合法的字符');
          }
        }else if (event.keyCode == 13 && key == ''){
            event.preventDefault();
        }
      });
      // 取消搜索
      this.plugin.on('click', '.btn-cancel-search', function () {
        $(this).hide();
        self.plugin.find('.search input').val('');
        self.plugin.find('.search-info').hide().html('');
        self.plugin.find('.bread ol').show();
        var id = self.bread[self.bread.length - 1].id;
        self.key = '';
        self.fetch(id);
      });
      //搜索框清空初始化
      this.plugin.on('input propertychange','.search input',function (event) {
          if ($.trim($(this).val()).length <1) {
            event.preventDefault();
            self.plugin.find('.search input').val('');
            self.plugin.find('.search-info').hide().html('');
            self.plugin.find('.btn-cancel-search').hide();
            self.plugin.find('.bread ol').show();
            var id = self.bread[self.bread.length - 1].id;
            self.key = '';
            self.fetch(id);
          }
      });
      //清除
      this.plugin.on('click', '.btn-eliminate-all', function () {
        if (self.selected.length > 0) {
          self.plugin.find('li').removeClass('active');
          if (self.opt.type == 3) {
            self.element.find('.trigger').text('选择部门').attr({ 'name': '' }).removeClass('label-color');
          };
          if ([1, 4].indexOf(self.opt.type) >= 0) {
            $.map(self.selected, function (item) {
              self.element.find('span[data-id="' + item.id + '"]').remove();
            });
          };
          self.selected = [];
          self.bread = [{ id: 1, name: '全部' }];
          self.renderBread();
          if(self.key&&self.opt.type==3){
              self.search()
          }else{
              self.fetch(1);
          }
        };
        self.trigger('clear');
      });
      //全选
      this.plugin.on('click', '.btn-select-all', function () {
        var all = false;
        $(this).toggleClass(function (i, clas) {
          if (clas == 'btn-select-all') {
            $(this).text('全不选');
            all = true;
          } else {
            $(this).text('全选');
            all = false;
          }
          return 'yes';
        });
        self.plugin.find('.list li').not('.nopt').each(function (item) {
          var item = $(this).data();
          var name="";
          if($(this).find('span.name .conflict').length){
              name= $(this).find('span.name span:nth-child(1)').text();
          }else{
              name= $(this).find('span.name').text();
          }
          var index = _.findIndex(self.selected, item);
          var tpl = '<span class="add" data-id="' + item.id + '" ' + (item.org != undefined ? 'data-org="' + item.org + '"' : '') + '>' + name + '<i class="icon icon-close"></i></span>';
          if (self.orgerId == item.id) {
            return true;
          }
          if (($(this).attr('class')&&$(this).attr('class').indexOf('active') < 0)||!$(this).attr('class')) {
            if (all) {
              $(this).addClass('active').find('p').addClass('leaf');
              self.element.find('span.trigger').before(tpl);
              self.selected.push(item);
            }
          } else {
            if (!all) {
              self.element.find('span[data-id="' + item.id + '"]').remove();
              self.selected.splice(index, 1);
              $(this).removeClass('active').find('p').removeClass('leaf');
            }
          }
        });
        self.getNums();
      });
      $(window).on('resize', function () {
        self.place();
      });
      this.plugin.on('click', function (event) {
        event.stopPropagation();
      });
      //隐藏
      $('body').on('click', function (e) {
        if (self.shown && self.element.find(e.target).length < 1) {
          self.destroy();
        };
      });
      $('.personal-component-hiding').on('click', function (e) {
        if (self.shown && self.element.find(e.target).length < 1) {
          self.destroy();
        }
      });
    },
    // 获取参会人数
    getNums: function getNums() {
      var self = this;
      var data = {
        attendees: []
      };
      this.element.find('span[data-id]').each(function () {
        data.attendees.push({
          id: $(this).data().id,
          dep: $(this).data().org == undefined ? 0 : $(this).data().org,
          name: $(this).text()
        });
      });
      //self.element.find('.count').text('共' + self.selected.length + '人参会');
      fetchs.post('/meeting/getParticipantsNumber', { attendees: JSON.stringify(data) }, function (res) {
        if (res.ifSuc == 1) {
          self.element.find('span.count').text('共' + res.data + '人参会');
        } else {
          notify('danger', res.msg);
        }
      });
    },
    //全选按钮内容的联动
    textChange: function textChange() {
      var self = this;
      if (self.plugin.find('.list li.active').length&&self.plugin.find('.list li').length&&(self.plugin.find('.list li.active').length == self.plugin.find('.list li').length)) {
        self.plugin.find('.btn-select-all').addClass('yes');
        self.plugin.find('.btn-select-all').text('全不选');
      } else if([1,2].indexOf(self.opt.type) >= 0&&self.plugin.find('.list.personnel li.active').length&&self.plugin.find('.list.personnel li').length&&self.plugin.find('.list.personnel li.active').length==self.plugin.find('.list.personnel li').length){
        self.plugin.find('.btn-select-all').addClass('yes');
        self.plugin.find('.btn-select-all').text('全不选');
      }else{
        self.plugin.find('.btn-select-all').removeClass('yes');
        self.plugin.find('.btn-select-all').text('全选');
      };
    },
    // 触发监听事件
    trigger: function trigger(event) {
      this.element.trigger({
        type: event + ".bs.persons",
        selected: this.selected,
        add: this.add,
        organizer: this.organizer,
        delete:this.delete
      });
    }
  };
  var personsPlugin = function personsPlugin(option) {
    return this.each(function () {
      var $this = $(this);
      var data = $this.data('persons');
      var options = (typeof option === 'undefined' ? 'undefined' : _typeof(option)) === 'object' && option;
      if (!data && /destroy|hide/.test(option)) return;
      if (!data) $this.data('persons', data = new Persons(this, options));
      if (typeof option == 'string') data[option]();
    });
  };
  $.fn.persons = personsPlugin;
  // 默认参数
  $.fn.persons.defaults = {
    class: null,
    max: null,
    type: 0, //0 人员部门 1人员单选 2人员多选 3部门单选 4部门多选
    container: 'body',
    template: '<div class="name"><span ng-repeat="item in data">{{item.name}}</span></div>',
    init: function init() {}, // 回调函数
    shown: function shown() {}, // 回调函数
    select: function select() {} // 回调函数
  };
})();