'use strict';

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

/**
 * @name Personnel 人员组件
 * @description
 * 人员选择,提供单位人员、人员多选、人员单选、单位多选、单位单选五种模式
 * 同时还可限制人数
 *
 * @event
 * shown.bs.personnel: 显示事件
 * delete.bs.personnel: 删除事件
 * select.bs.personnel: 选择事件
 * unselect.bs.personnel: 取消选择事件
 * hidden.bs.personnel: 隐藏事件
 * $(selector).on(events, function(event) {
    console.log(event.initial) 每次显示初始数据
    console.log(event.selected) 选中数据，跟随选择/取消选择变动
    console.log(event.delete) 删除操作返回删除数据
    console.log(event.add) 选择操作返回数据
  })
 *
 * @usage
 *
 * ```html
 *  <!-- 单位人员 -->
 *  <div data-toggle="personnel" data-type="0">
 *    ...
 *    <div class="tags">
 *      <span data-id="1">小明</span>
 *      <span class="icon icon-add btn-add"></span>
 *    </div>
 *  <div>
 *
 *  <!-- 人员多选 -->
 *  <div data-toggle="personnel" data-type="1">
 *    ...
 *  <div>
 *
 *  <!-- 人员单选 -->
 *  <div data-toggle="personnel" data-type="2">
 *    ...
 *  <div>
 *
 *  <!-- 单位多选 -->
 *  <div data-toggle="personnel" data-type="3">
 *    ...
 *  <div>
 *
 *  <!-- 单位单选 -->
 *  <div data-toggle="personnel" data-type="4">
 *    ...
 *  <div>
 *
 *  <!-- 人数限制 -->
 *  <div data-toggle="personnel" data-max="3">
 *    ...
 *  <div>
 *
 * ```
 *
 */

+function ($) {
    'use strict';

    var _userInfo = JSON.parse(localStorage.getItem('userinfo'));
    var Personnel = function Personnel(element, options) {
        this.o = options;
        this.selected = [];
        this.initial = [];
        this.shown = false;
        this.add = [];
        this.delete = [];
        this.organizer = null;
        this.orgerId = null;
        this.key = '';
        this.bread = [{ id: 1, name: '全部' }];
        this.element = $(element);
        this.personnel = $('<div class="personnel"><div class="search"><input type="text" class="form-control form-control-lg" placeholder="搜索姓名"><span class="icon icon-close btn-cancel-search"></span></div><div class="bread"><ol></ol><p class="search-info"></p><span class="btn-select-all">全选</span></div><div class="cont"><ul class="list groups"></ul><div class="line"></div><ul class="list persons"></ul></div></div>');
        var self = this;
        this.element.on('click', 'span.btn-add, span.value', function() {
            self.init();
        });

        this.element.on('click', 'span i', function (event) {
            var item = $(this).parent('span').data();
            var index = _.findIndex(self.selected, item);
            self.delete = [item];
            self.selected.splice(index, 1);
            self.element.removeClass('most');
            self._trigger('delete.bs.personnel');
            event.stopPropagation();
            $(this).parent('span').hide().remove();
            self.personnel.find('li[data-id="'+item.id+'"]').removeClass('active');
            self.getNums();
        });
    };

    Personnel.prototype = {
        init: function init() {
            var self = this;
            var opts = this.element.data();
            this.o = $.extend({}, this.o, opts);
            if (!this.shown){
                this.show();
            }
        },
        show: function show() {
            var self = this;
            var selected = [];
            var initial = [];
            self.add = [];
            self.delete = [];
            self.shown = true;
            if ($('.detail').length > 0) {
                this.o.container = '.detail .wrap';
                this.orgerId = $('.detail #organizer input').val();
            }
            if (self.element.parents('#form-box').length > 0){
                this.o.container = '#form-box';
            }
            if ($('.personnel-wrap').length > 0) {
                this.o.container = '.personnel-wrap';
            }
            $(this.o.container).append(self.personnel);
            if ([0, 1, 3].indexOf(self.o.type) >= 0) {
                this.element.find('span[data-id]').map(function (i, el) {
                    selected.push($(el).data());
                    initial.push($(el).data());
                });
                self.selected = selected;
                self.initial = initial;
                this.personnel.find('.btn-select-all').show();
            }else {
                self.selected = [{ id: self.element.find('input:hidden').val() }];
            }
            this.bread = [{ id: 1, name: '全部' }];
            this.place();
            this.renderBread();
            this.fetch(1);
            this.bindEvent();
            this._trigger('shown.bs.personnel');
        },
        // 弹框定位
        place: function place() {
            var element = this.element.offset(),
                scrollTop =  this.o.container == 'body' ? 0 : $(this.o.container).scrollTop(),
                container = $(this.o.container).offset();
            this.height = this.element.outerHeight();
            this.personnel.css({
                top: element.top - container.top + scrollTop,
                left: element.left - container.left,
                marginTop: this.element.outerHeight()
            });
        },
        // 面包线渲染
        renderBread: function renderBread() {
            var self = this;
            self.personnel.find('.bread ol').html('');
            $.map(self.bread, function (item) {
                self.personnel.find('.bread ol').append('<li data-id="' + item.id + '">' + item.name + '</li>');
            });
        },
        // 数据渲染
        renderData: function renderData(data) {
            var self = this;
            var key = self.key;
            $('.personnel .persons').html('');
            $('.personnel .groups').html('');
            if (data.data){
                $.map(data.data.sysOrganizations, function (item) {
                    var name = key != '' ? item.sysOrganization.orgName.replace(new RegExp(key, 'g'), '<font color="#FA702">' + key + '</font>') : item.sysOrganization.orgName;
                    if ([1, 2].indexOf(self.o.type) < 0) {
                        $('.personnel .groups').append('<li class="' + (_.findIndex(self.selected, { id: item.sysOrganization.id }) >= 0 ? 'active' : '') + '" data-id="' + item.sysOrganization.id + '" ' + (self.o.type == 0 ? 'data-org="1"' : '') + '><p data-leaf="' + item.leaf + '"><span class="icon icon-organiz-unit"></span><span class="name">' + name + '</span></p></li>');
                    } else {
                        $('.personnel .groups').append('<li class="nopt" data-id="' + item.sysOrganization.id + '"><p data-leaf="' + item.leaf + '"><span class="icon icon-organiz-unit"></span><span class="name">' + name + '</span></p></li>');
                    }
                });
                if (data.data.sysOrganizations.length > 0 && data.data.sysUser.list.length > 0 && [3, 4].indexOf(self.o.type) < 0) {
                    $('.personnel .line').show();
                } else {
                    $('.personnel .line').hide();
                }
                if ([0, 1, 2].indexOf(self.o.type) >= 0) {
                    $.map(data.data.sysUser.list, function (item) {
                        var name = key != '' ? item.userName.replace(new RegExp(key, 'g'), '<font color="#FA702">' + key + '</font>') : item.userName;
                        var icon = '';
                        if (item.userName != null && item.userName.length > 2) {
                            icon = item.userName.slice(-2);
                        } else if (item.userName != null && item.userName.length == 2) {
                            icon = item.userName.slice(-1);
                        } else {
                            icon = item.userName;
                        }
                        if(item.photo){
                            $('.personnel .persons').append('<li class="' + (_.findIndex(self.selected, { id: item.id }) >= 0 ? 'active' : '') + '" data-id="' + item.id + '" ' + (self.o.type == 0 ? 'data-org="0"' : '') + '><img class="user-url" src=' + item.smallPicture +'><span class="name">' + name + '</span>'+(item.participantStatus ? '<span class="conflict"><i class="icon icon-error"></i>有会议冲突</span>':'')+'</li>');
                        }else {
                            $('.personnel .persons').append('<li class="' + (_.findIndex(self.selected, { id: item.id }) >= 0 ? 'active' : '') + '" data-id="' + item.id + '" ' + (self.o.type == 0 ? 'data-org="0"' : '') + '><span class="nophoto small"><i>' + icon + '</i></span><span class="name">' + name + '</span>'+(item.participantStatus ? '<span class="conflict"><i class="icon icon-error"></i>有会议冲突</span>':'')+'</li>');

                        }
                    });
                }
            }

        },
        // 事件绑定
        bindEvent: function bindEvent() {
            var self = this;
            // 面包线点击
            this.personnel.on('click', '.bread li', function () {
                var id = $(this).data('id');
                var index = $(this).index();
                self.bread.splice(index + 1, self.bread.splice.length - index + 1);
                self.fetch(id);
                self.renderBread();
            });
            // 跳转到下一级
            this.personnel.on('click', '.groups p', function (event) {
                event.stopPropagation();
                if ($(this).data('leaf') == true || [0,1,2].indexOf(self.o.type)>=0) {
                    var id = $(this).parent('li').data('id');
                    var name = $(this).children('span.name').text();
                    self.bread.push({ id: id, name: name });
                    self.fetch(id);
                    self.renderBread();
                }
            });
            // 选择/取消选择
            this.personnel.on('click', '.list li', function () {
                var item = $(this).data();
                var name = $(this).find('span.name').text();
                var index = _.findIndex(self.selected, item);
                var tpl = '\n          <span class="add" data-id="' + item.id + '" ' + (item.org != undefined ? 'data-org="' + item.org + '"' : '') + '>' + name + '<i class="icon icon-delete-personnel"></i>\n          </span>';
                if (self.o.max && self.selected.length >= self.o.max && index < 0) {
                    return false;
                }
                if (self.o.type == 2 && $(this).hasClass('active')) {
                    return false
                }
                if (self.o.type == 0 && self.orgerId == item.id) {
                    return false;
                }
                $(this).toggleClass('active');
                if(self.personnel.find('.list li.active').length == self.personnel.find('.list li').length) {
                    self.personnel.find('.btn-select-all').addClass('yes');
                    self.personnel.find('.btn-select-all').text('全不选')
                }else{
                    self.personnel.find('.btn-select-all').removeClass('yes');
                    self.personnel.find('.btn-select-all').text('全选')
                }
                if ([0, 1, 3].indexOf(self.o.type) < 0) {
                    self.element.find('span.value').text(name);
                    self.element.find('input:hidden').val(item.id);
                    self.personnel.find('.list li').not(this).removeClass('active');
                    self.selected.splice(0, 1);
                    if (self.o.type == 4){
                        var bread = $('#leftMenuTop h4').text();
                        $.map(self.bread, function(v,i){
                            if (i!=0){
                                bread += '>'+v.name;
                            }
                        });
                        bread += '>'+name;
                        self.element.find('span.value').text(bread);
                    }
                }
                if (index < 0) {
                    self.element.find('span.btn-add').before(tpl);
                    self._trigger('select.bs.personnel');
                    self.organizer = _.filter(self.data.sysUser.list,function(user) {
                        return user.id == item.id;
                    })[0];
                    self.selected.push(item);
                    self.element.find('span.count').text('共'+self.selected.length+'人参会');
                    self.add.push(item);
                } else {
                    self.element.find('span[data-id="' + item.id + '"]').remove();
                    self._trigger('unselect.bs.personnel');
                    if (_.findIndex(self.add, item) < 0) {
                        self.delete.push(item);
                    } else {
                        self.add = _.without(self.add, item);
                    }
                    self.selected.splice(index, 1);
                    self.element.find('span.count').text('共'+self.selected.length+'人参会');
                }
                if (self.o.max && self.selected.length >= self.o.max) {
                    self.element.addClass('most');
                } else {
                    self.element.removeClass('most');
                }
                if (self.element.height() != self.height) {
                    self.place();
                }
                self.getNums();
            });
            // 全选
            this.personnel.on('click', '.btn-select-all', function () {
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
                self.personnel.find('.list li').not('.nopt').each(function (el) {
                    var item = $(this).data();
                    var name = $(this).find('span.name').text();
                    var index = _.findIndex(self.selected, item);
                    var tpl = '\n          <span data-id="' + item.id + '" ' + (item.org != undefined ? 'data-org="' + item.org + '"' : '') + '>' + name + '<i class="icon icon-close"></i>\n          </span>';
                    if(self.orgerId == item.id){
                        return true;
                    }
                    if ($(this).attr('class').indexOf('active') < 0) {
                        if (all) {
                            $(this).addClass('active');
                            self.element.find('span.btn-add').before(tpl);
                            self.selected.push(item);
                        }
                    } else {
                        if (!all) {
                            self.element.find('span[data-id="' + item.id + '"]').remove();
                            self.selected.splice(index, 1);
                            $(this).removeClass('active');
                        }
                    }
                    if (self.o.max && self.selected.length >= self.o.max) {
                        self.element.addClass('most');
                        return false;
                    } else {
                        self.element.removeClass('most');
                    }
                });
                self.getNums();
            });

            // 搜索
            this.personnel.on('keydown', '.search input', function (event) {
                var key = $.trim($(this).val());

                if (event.keyCode == 13 && key != '') {
                    if (!/[@#\$%\^&\*]+/g.test(key)) {
                        self.key = key;
                        self.search();
                        self.personnel.find('.bread ol').hide();
                        self.personnel.find('.btn-cancel-search').show();
                        self.personnel.find('.search-info').show().html('搜索“<span>' + key + '</span>”的结果');
                    } else {
                        notify('danger', '请输入合法的字符');
                    }
                }
            });
            // 取消搜索
            this.personnel.on('click', '.btn-cancel-search', function () {
                $(this).hide();
                self.personnel.find('.search input').val('');
                self.personnel.find('.search-info').hide().html('');
                self.personnel.find('.bread ol').show();
                var id = self.bread[self.bread.length - 1].id;
                self.key = '';
                self.fetch(id);
            });
            this.personnel.on('click', function (event) {
                event.stopPropagation();
            });
            $('body').on('click', function (e) {
                if (self.shown && self.element.find(e.target).length < 1) {
                    $('.btn-cancel-search').hide()
                    self.personnel.find('.search input').val('');
                    self.personnel.find('.search-info').hide().html('');
                    self.personnel.find('.bread ol').show();
                    self.key = '';
                    self.shown = false;
                    self._trigger('hidden.bs.personnel');
                    self.add = [];
                    self.delete = [];
                    self.personnel.remove();
                }
                if (self.shown && self.element.find(e.target).length >= 0 && [0, 1, 3].indexOf(self.o.type) >= 0) {
                    if (!$(e.target).hasClass('btn-add')) {
                        $('.btn-cancel-search').hide()
                        self.personnel.find('.search input').val('');
                        self.personnel.find('.search-info').hide().html('');
                        self.personnel.find('.bread ol').show();
                        self.key = '';
                        self.shown = false;
                        self._trigger('hidden.bs.personnel');
                        self.add = [];
                        self.delete = [];
                        self.personnel.remove();
                    }
                }
            });
            //通讯录人员组建隐藏 段威杰
            $('.personal-component-hiding').on('click', function (e) {
                if (self.shown && self.element.find(e.target).length < 1) {
                    $('.btn-cancel-search').hide()
                    self.personnel.find('.search input').val('');
                    self.personnel.find('.search-info').hide().html('');
                    self.personnel.find('.bread ol').show();
                    self.key = '';
                    self.shown = false;
                    self._trigger('hidden.bs.personnel');
                    self.add = [];
                    self.delete = [];
                    self.personnel.remove();
                }
                if (self.shown && self.element.find(e.target).length >= 0 && [0, 1, 3].indexOf(self.o.type) >= 0) {
                    if (!$(e.target).hasClass('btn-add')) {
                        $('.btn-cancel-search').hide()
                        self.personnel.find('.search input').val('');
                        self.personnel.find('.search-info').hide().html('');
                        self.personnel.find('.bread ol').show();
                        self.key = '';
                        self.shown = false;
                        self._trigger('hidden.bs.personnel');
                        self.add = [];
                        self.delete = [];
                        self.personnel.remove();
                    }
                }
            });
            //通讯录选中框隐藏人员组件 段威杰
            $('td div.custom-control.custom-checkbox').on('click', function (e) {
                if (self.shown && self.element.find(e.target).length < 1) {
                    $('.btn-cancel-search').hide()
                    self.personnel.find('.search input').val('');
                    self.personnel.find('.search-info').hide().html('');
                    self.personnel.find('.bread ol').show();
                    self.key = '';
                    self.shown = false;
                    self._trigger('hidden.bs.personnel');
                    self.add = [];
                    self.delete = [];
                    self.personnel.remove();
                }
                if (self.shown && self.element.find(e.target).length >= 0 && [0, 1, 3].indexOf(self.o.type) >= 0) {
                    if (!$(e.target).hasClass('btn-add')) {
                        $('.btn-cancel-search').hide()
                        self.personnel.find('.search input').val('');
                        self.personnel.find('.search-info').hide().html('');
                        self.personnel.find('.bread ol').show();
                        self.key = '';
                        self.shown = false;
                        self._trigger('hidden.bs.personnel');
                        self.add = [];
                        self.delete = [];
                        self.personnel.remove();
                    }
                }
            });
            $(window).on('resize', function () {
                self.place();
            });
        },
        _trigger: function _trigger(event) {
            this.element.trigger({
                type: event,
                initial: this.initial,
                selected: this.selected,
                delete: this.delete,
                organizer: this.organizer,
                add: this.add
            });
        },
        // 获取参会人数
        getNums: function(){
            var self = this;
            var data = {
                attendees: []
            };
            this.element.find('span[data-id]').each(function () {
                data.attendees.push({
                    id: $(this).data().id,
                    dep: $(this).data().org == undefined ? 0: $(this).data().org,
                    name: $(this).text()
                });
            });
            fetchs.post('/meeting/getParticipantsNumber',{attendees:JSON.stringify(data)}, function (res) {
                if (res.ifSuc == 1){
                    self.element.find('span.count').text('共'+res.data+'人参会')
                }else {
                    notify('danger', res.msg);
                }
            });

        },
        // 搜索请求
        search: function search() {
            var self = this;
            var timerange = this.element.parents('form').find('#timerange');
            if (self.o.type == 0 && timerange.children('input[name="start"]').val() != ''){
                var startTime = moment(parseInt(timerange.children('input[name="start"]').val())).format('YYYY-MM-DD H:mm');
                var endTime = moment(parseInt(timerange.children('input[name="end"]').val())).format('YYYY-MM-DD H:mm');
                var conferenceId = this.element.parents('form').find('input[name="conferenceId"]').val();
                var url = '/search?userId=' + _userInfo.userId + '&startTime=' + startTime + '&endTime=' + endTime + '&searchParameter=' + self.key + '&token=' + _userInfo.token;

                if (conferenceId != undefined) {
                    url+='&conferenceId='+conferenceId;
                }

                fetchs.get(url, function (data) {
                    self.data = data.data;
                    self.renderData(data);
                });

            }else {
                fetchs.get('/search?userId=' + _userInfo.userId + '&searchParameter=' + self.key + '&token=' + _userInfo.token, function (data) {
                    self.data = data.data;
                    self.renderData(data);
                });
            }

        },
        // 点击部门请求
        fetch: function fetch(id) {
            var self = this;
            var timerange = this.element.parents('form').find('#timerange');
            if (self.o.type == 0 && timerange.children('input[name="start"]').val() != ''){
                var startTime = moment(parseInt(timerange.children('input[name="start"]').val())).format('YYYY-MM-DD H:mm');
                var endTime = moment(parseInt(timerange.children('input[name="end"]').val())).format('YYYY-MM-DD H:mm');
                var conferenceId = this.element.parents('form').find('input[name="conferenceId"]').val();
                var url = '/sysUsersByOrgId?userId=' + _userInfo.userId + '&startTime=' + startTime + '&endTime=' + endTime + '&orgId=' + id + '&token=' + _userInfo.token;

                if (conferenceId != undefined) {
                    url+='&conferenceId='+conferenceId;
                }

                fetchs.get(url, function (data) {
                    self.data = data.data;
                    self.renderData(data);
                });
            }else {
                fetchs.get('/sysUsersByOrgId?userId=' + _userInfo.userId + '&orgId=' + id + '&token=' + _userInfo.token, function (data) {
                    self.data = data.data;
                    self.renderData(data);
                });
            }

        }
    };
    function personnelPlugin(option) {
        var args = Array.apply(null, arguments);
        args.shift();
        var internal_return;
        this.each(function() {
            var $this = $(this),
                data = $this.data('personnel'),
                options = typeof option === 'object' && option;
            if (!data) {
                var opts = $.extend({}, $.fn.personnel.defaults, options);
                data = new Personnel(this, opts);
                $this.data('personnel', data);
            }
            if (typeof option === 'string' && typeof data[option] === 'function') {
                internal_return = data[option].apply(data, args);
            }
        })
        if (internal_return === undefined || internal_return instanceof Personnel) return this;
        if (this.length > 1)
            throw new Error('Using only allowed for the collection of a single element (' + option + ' function)');
        else
            return internal_return;
    }
    $.fn.personnel = personnelPlugin;
    $.fn.personnel.defaults = {
        max: null, // number: 默认null不限人数
        type: 0, // number: 默认0单位人员、1人员多选、2人员单选、3单位多选、4单位单选
        container: 'body'
    }
    $(document).on('click', '[data-toggle="personnel"]', function(e) {
        var $this = $(this);
        if ($this.data('personnel'))
            return;
        e.preventDefault();
        // component click requires us to explicitly show it
        if ($(e.target)[0].tagName == 'I'){
            $(e.target).parent().remove();
        }
        personnelPlugin.call($this, 'init');
    });
    $(window).on('load', function () {
        $('[data-toggle="personnel"]').each(function () {
            var $this = $(this);
            personnelPlugin.call($this);
        });
    })
}(jQuery);