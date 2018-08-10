'use strict';
var selectMore=0;//区分该会议时间内还有其他预定申请
var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) {
    return typeof obj;
} : function (obj) {
    return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj;
};
(function () {
    //名字灰色显示
    soda.filter('sysname', function (string, word) {
        if (string == null || word == null) return string;
        if (string.length == 0 || word.length == 0) return string;
        var reg = word.replace(/([\.|\*|\$|\*|\(|\)|\+])/g, "\\" + "$1");
        return word == '' ? string : string.toString().replace(new RegExp(reg), '<font color="#7e838c">' + word + '</font>');
    });


    var detailTmpl = '\n <div class="wrap">\n   <div class="content">\n      <div class="title">{{type==0 ? "会议详情": type==2? "修改会议" : "会议预订"}}<span ng-if="type==0">预订人:{{bookeder.name}}&nbsp;&nbsp;&nbsp;&nbsp;{{createTime|date:\'YYYY.M.D&nbsp;&nbsp;H:mm\'}}</span></div>\n        <div class="result">\n          <form id="detailForm">\n            <div class="flex">\n              <div class="label">主题</div>\n              <div ng-if="type==0" class="col">\n                <h5>{{title}}\n </h5>\n     <span ng-if="isPublic==0" class="icon icon-lock"></span>\n                <span ng-replace="status|status"></span>              <p class="contents">{{contents}}</p>\n              </div>\n              <div ng-if="type!=0" class="col">\n                <input class="form-control form-control-lg" name="title" type="text" placeholder="填写会议主题" value="{{title}}" maxlength="50">\n                <input ng-if="type==2" name="conferenceId" type="hidden"  value="{{conferenceId}}">\n                <input name="clientType" type="hidden"  value="0">\n                <label class="checkbox">\n                  <input name="isPublic" type="checkbox" value="0" ng-checked="{{isPublic == 0 ? true:undefined}}"><span class="icon"><a class=" standardfont">非公开会议</a></span>\n                </label>\n              </div>\n            </div>\n            <div class="flex">\n              <div class="label">时间</div>\n              <div ng-if="type==0" class="col">\n                <div class="pop">\n                  <span class="icon icon-xq-time"></span>\n                  <span class="value">{{start|date:\'H:mm\'}}~{{end|date:\'H:mm\'}} {{start|duration:end}}</span>\n                </div>\n              </div>\n              <div ng-if="type!=0" class="col">\n                <div id="timerange" class="pop" data-toggle="timerange">\n                  <span class="icon icon-xq-time"></span>\n                  <span class="value">{{start|date:\'H:mm\'}}~{{end|date:\'H:mm\'}} {{start|duration:end}}</span>\n                  <input name="start" type="hidden" value="{{start}}">\n                  <input name="end" type="hidden" value="{{end}}">\n                </div>\n                <p>\n                  <span ng-if="opt.cycle!=0">可预订最近{{opt.cycle}}天的会议</span>\n                  <span ng-if="opt.cycle!=0 && opt.max!=0">，</span>\n                  <span ng-if="opt.max!=0">会议最大时长为{{opt.max}}小时</span>\n                </p>\n              </div>\n            </div>\n            <div class="flex">\n              <div class="label">地点</div>\n              <div ng-if="type==0" class="col">\n                <div class="pop">\n                  <span class="icon icon-xq-site"></span>\n                  <span class="value">{{location.name}}</span>\n                </div>\n              </div>\n              <div ng-if="type!=0" class="col">\n                <div class="pop place" data-toggle="boardroom">\n                  <span class="icon icon-xq-site"></span>\n                  <span class="value none" ng-if="location.name == \'\'">\u8BF7\u9009\u62E9\u5730\u70B9</span>\n                  <span ng-if="location.name != \'\'" class="value">{{location.name}}</span>\n                  <input name="location" type="hidden" value="{{location.id}}">\n                </div>\n              </div>\n            </div>\n            <div class="flex">\n              <div class="label">组织者</div>\n              <div ng-if="type==0" class="col">\n                <div class="pop">\n                  <span class="icon icon-xq-organizer"></span>\n                  <span class="value">{{organizer.name}}</span>\n                </div>\n              </div>\n              <div ng-if="type!=0" class="col">\n                <div id="organizer" class="pop" data-toggle="personnel" data-type="2">\n                  <span class="icon icon-xq-organizer"></span>\n                  <span class="trigger" name="{{organizer.id}}">{{organizer.name}}</span>\n                  <input name="organizer" type="hidden" value="{{organizer.id}}">\n                </div>\n                <p>组织者默认为预订人，可修改</p>\n              </div>\n            </div>\n            <div class="flex" style="min-height:56px">\n              <div class="label">主办单位</div>\n              <div ng-if="type==0" class="col">\n                <div class="pop">\n                  <span class="icon icon-organiz-unit"></span>\n                  <span class="value">{{department.name}}</span>\n                </div>\n              </div>\n              <div ng-if="type!=0" class="col">\n                <div id="department" class="pop">\n                  <span class="icon icon-organiz-unit"></span>\n                  <span class="value">{{department.name}}</span>\n                  <input name="department" type="hidden" value="{{department.id}}">\n                </div>\n                <p>主办单位默认为组织者所在部门，不支持手动修改</p>\n              </div>\n            </div>\n            <div class="flex">\n              <div class="label">参会人</div>\n              <div ng-if="type==0" class="col screening">\n                <input type="radio" id="all" name="screening" checked/>\n                <label for="all"><i></i>全部（{{screening.all}}）</label>\n                <input type="radio" id="accept" name="screening" />\n                <label for="accept"><i></i>接受（{{screening.accept}}）</label>\n                <input type="radio" id="interim" name="screening"/>\n                <label for="interim"><i></i>暂定（{{screening.interim}}）</label>\n                <input type="radio" id="reject" name="screening"/>\n                <label for="reject"><i></i>拒绝（{{screening.reject}}）</label>\n                <input type="radio" id="noreply" name="screening"/>\n                <label for="noreply"><i></i>未回复（{{screening.noreply}}）</label>\n                <div class="tags">\n                  <span ng-class="item.isDimisson == 1 ? \'all quit\':\'all\'" ng-repeat="item in attendees">{{item.name}}</span>\n                  <span ng-repeat="item in allAttendees" ng-class="(item.statu == 1 ? \'accept\': item.statu == 2 ? \'interim\': item.statu == 3 ? \'noreply\': \'reject\')+(item.isDimisson == 1 ? \' quit\':\'\')">{{item.name}}</span>\n                </div>\n              </div>\n              <div ng-if="type!=0" class="col tags" id="addPersons" data-toggle="personnel" data-type="0">\n                <span ng-repeat="item in attendees" data-id="{{item.id}}" data-org="{{item.dep}}" ng-class="$index > 0 ? item.isDimisson == 1 ? \'quit\':\'add\' : \'\'">{{item.name}}<i class="icon icon-delete-personnel" ng-if="$index>0"></i></span>\n                <span class="icon icon-add-personnel btn-add trigger"></span>\n                <span class="count">共{{allAttendees.length ? allAttendees.length: 1}}人参会</span></div>\n            </div>\n            <div ng-if="type!=0" class="flex">\n              <div class="label">会议内容</div>\n              <div class="col">\n                <textarea name="contents" class="form-control" placeholder="填写会议内容" maxlength="300">{{contents}}</textarea>\n              </div>\n            </div>\n            <div ng-if="type!=0" class="flex">\n              <div class="label">通知</div>\n              <div class="col">\n                <label class="checkbox">\n                  <input name="notice" type="checkbox" value="1" checked disabled><span class="icon"><a class=" standardfont">微信</a></span>\n                </label>\n                <label class="checkbox" ng-if="isPay==1">\n                  <input name="notice" type="checkbox" value="2" ng-checked="{{notice == \'1,2\'?true:undefined}}"><span class="icon"><a class=" standardfont">邮件</a></span>\n                </label>\n              </div>\n            </div>\n            <div ng-if="type!=0" class="flex">\n              <div class="label">提醒</div>\n              <div class="col">\n                <label class="checkbox">\n                  <input name="remind" type="checkbox" value="0" ng-checked="{{reminds[0]}}"><span class="icon"><a class=" standardfont">准时</a></span>\n                </label>\n                <label class="checkbox">\n                  <input name="remind" type="checkbox" value="15" ng-checked="{{reminds[1]}}"><span class="icon"><a class=" standardfont">提前15分钟</a></span>\n                </label>\n                <label class="checkbox">\n                  <input name="remind" type="checkbox" value="1h" ng-checked="{{reminds[2]}}"><span class="icon"><a class=" standardfont">提前1小时</a></span>\n                </label>\n                <label class="checkbox">\n                  <input name="remind" type="checkbox" value="2h" ng-checked="{{reminds[3]}}"><span class="icon"><a class=" standardfont">提前2小时</a></span>\n                </label>\n                <label class="checkbox">\n                  <input name="remind" type="checkbox" value="1d" ng-checked="{{reminds[4]}}"><span class="icon"><a class=" standardfont">提前1天</a></span>\n                </label>\n              </div>\n            </div>\n            <div ng-if="type!=0" class="flex">\n              <div class="label"></div>\n              <div class="col">\n                <button ng-if="type==1" class="btn btn-lg btn-primary" type="submit">立即预订</button>\n                <button ng-if="type==2" class="btn btn-lg btn-primary save-submit" type="submit" disabled>保存</button><button ng-if="type==2" class="btn btn-lg btn-clear-secondary btn-cancel-edit" type="button">取消</button>\n              </div>\n            </div>\n            <div ng-if="type==0 && (userRole==2 || userRole==3 || userRole==5 || userRole==7) && (status==2 || status==3)" class="flex responds">\n              <div class="label">会议响应</div>\n              <div ng-repeat="item in allAttendees" class="col" ng-if="item.id == userinfo.userId">\n                <button ng-if="item.statu==1" class="btn btn-sm btn-primary" type="button" data-respond="1" disabled>您已接受</button>\n                <button ng-if="item.statu!=1" class="btn btn-sm btn-primary" type="button" data-respond="1">接受</button>\n                <button ng-if="item.statu==2" class="btn btn-sm btn-primary" type="button" data-respond="2" disabled>您已暂定</button>\n                <button ng-if="item.statu!=2" class="btn btn-sm btn-primary" type="button" data-respond="2">暂定</button>\n                <button ng-if="item.statu==0" class="btn btn-sm btn-primary" type="button" data-respond="0" disabled>您已拒绝</button>\n                <button ng-if="item.statu!=0" class="btn btn-sm btn-primary" type="button" data-respond="0">拒绝</button>\n              </div>\n            </div>\n            <div ng-if="(status==3 || status==4 || status==5) && type==0" class="flex">\n              <div class="label"></div>\n              <div class="col">\n                <button class="btn btn-clear-primary" type="button" data-toggle="modal" data-target="#signedMeeting">本次会议，参会人数共{{screening.all}}人，已签到{{signin.yes}}人</button>\n    </div>\n            </div>\n          </form>\n          <div ng-if="type==0" class="actions">\n            <button ng-if="isShowSign==1" type="button" class="btn-meeting-signin" data-toggle="modal" data-target="#signinMeeting" data-id="{{conferenceId}}" data-title="{{title}}">签到</button>\n            <button ng-if="signStatus==1" type="button" class="btn-meeting-signin" disabled>已签到</button>\n            <button ng-if="(userRole==3 || userRole==4 || userRole==5 || userRole==6) && (status==1 || status==2)" type="button" class="btn-meeting-edit">修改会议</button>\n            <button ng-if="(userRole==3 || userRole==4 || userRole==5 || userRole==6) && status!=0 && status!=4 && status!=5" type="button" data-toggle="modal" data-target="#cancelMeeting" data-id="{{conferenceId}}" data-title="{{title}}">取消会议</button>\n            <button ng-if="(userRole==3 || userRole==4 ||userRole==5 || userRole==6) && (status==0 || status==5)" type="button" data-toggle="modal" data-target="#deleteMeeting" data-id="{{conferenceId}}" data-title="{{title}}">删除会议</button>\n            <button ng-if="(userRole==1 || userRole==4 || userRole==5 || userRole==7) && status==1" type="button"  data-target="#auditedMeeting" class="auditedMeeting"  data-id="{{conferenceId}}"   data-time="{{start}}"  data-end="{{end}}" data-location="{{location.name}}"  data-roomname="{{title}}" data-roomid="{{location.id}}">审核通过</button>\n            <button ng-if="(userRole==1 || userRole==4 || userRole==5 || userRole==7) && status==1" type="button"  data-target="#unauditedMeeting" class="unauditedMeeting" data-id="{{conferenceId}}" data-location="{{location.name}}"  data-time="{{start}}" data-end="{{end}}">审核不通过</button>\n          </div>\n        </div>\n     <div class="meeting-dynamics">  <div ng-if="type==0" class="notices">\n         <h6>会议动态<span>{{dynamics.length}}</span></h6>\n          <ul>\n            <li ng-repeat="item in dynamics">\n        <span ng-if="item.sys == 0 && item.photo" class="nophoto " style="background-color: #fff"><img style="width:100%; height: 100%;vertical-align: bottom;border-radius: 50%;" src="{{item.photo}}"></span>    <span ng-if="item.sys == 0 && !item.photo" class="nophoto small {{item.name|charcode}}"><i>{{item.name|slice:-2}}</i></span>\n                    <span ng-if="item.sys == 1" class="icon icon-systeminfo"></span>\n              <span class="timeago">{{item.timeago|timeago}}</span>\n              <p><span class="name" ng-if="item.sys == 0" ng-html="item.name|keylight:keyword"></span><span class="name" ng-if="item.sys == 0">\uFF1A</span><span class="message" ng-if="item.sys ==0">{{item.message}}</span> <span class="message" ng-if="item.sys != 0" ng-html="item.message|sysname:item.name"></span></p>\n            </li>\n          </ul>\n        </div>\n  </div>      <div ng-if="type==0" class="leave-word">\n          <div class="textArea" contenteditable="true" type="text"  maxlength="200">请输入留言，按 Enter 快速发布</div>\n<p class="publish-box"><button class="btn btn-primary">发布</button></p></div>\n      </div>\n    </div>\n  </div>\n  <a class="icon icon-close btn-close"></a>';
    var Detail = function Detail(element, options, data) {
        this.o = options;
        this.initial = {};
        this.data = {
            title: '', // 会议主题
            type: 0, // 0:详情、1:新建、2:编辑
            clientType: 0,
            isPublic: 1,
            start: 0, // 开始时间
            end: 0, // 结束时间
            location: { // 会议地点
                id: '',
                name: ''
            },
            organizer: { // 会议组织者
                id: '',
                name: ''
            },
            department: { // 主办单位
                id: '',
                name: ''
            },
            attendees: [],
            screening: {
                all: 0,
                accept: 0,
                interim: 0,
                reject: 0,
                noreply: 0
            },
            signin: {
                yes: 0,
                no: 0
            },
            dynamics: [],
            contents: '', // 会议内容
            notice: '1', // 1:微信、2:&邮件、1,2:微信&邮件
            remind: '' //
        };
        this.screening = {
            all: 0,
            accept: 0,
            interim: 0,
            reject: 0,
            noreply: 0
        };
        this.shown = false;
        this.element = $(element);
        this.detail = $('<div class="detail floating"></div>');
        this.params = {
            meetingSubject: '',
            isPublic: 1,
            start: '',
            end: '',
            resourceId: '',
            meetingOrganizer: '',
            meetingHostUnit: '',
            userIds: [],
            meetingContent: '',
            informTime: '',
            informType: ''
        };
        this.leaveword = '';
        this.init();
    };
    Detail.prototype = {
        init: function init() {
            var self = this;
            this.element.on('click', function (event) {
                self.show();
            });
        },
        show: function (data) {
            this.initial = data;
            this.data = $.extend({}, this.data, data);
            this.data.isPay=fetchs.status;
            this.data.userinfo = JSON.parse(localStorage.getItem('userinfo'));
            $('body').append(this.detail);
            this.renderData()
            this.bindEvent();
        },
        renderData: function () {
            var self = this;
            if (this.data.type == 0) {
                var screening = _.countBy(this.data.allAttendees, function (item) {
                    return item.statu == 1 ? 'accept' : item.statu == 2 ? 'interim' : item.statu == 3 ? 'noreply' : 'reject'
                });
                screening.all = this.data.allAttendees.length;
                var signin = _.countBy(this.data.allAttendees, function(item) {
                    return (item.signTime == null || item.signTime==0) ? 'no': 'yes'
                });
                var user = _.filter(this.data.allAttendees,function(item){return item.id == self.data.userinfo.userId});
                if (user.length != 0){
                    signin.user = user[0].signTime; //获取签到时间(判断是否已经签到)
                }
                var reminds = [undefined,undefined,undefined,undefined,undefined];
                if (this.data.remind.indexOf('0')>=0) {
                    reminds[0] = true;
                }
                if (this.data.remind.indexOf('15')>=0) {
                    reminds[1] = true;
                }
                if (this.data.remind.indexOf('1h')>=0) {
                    reminds[2] = true;
                }
                if (this.data.remind.indexOf('2h')>=0) {
                    reminds[3] = true;
                }
                if (this.data.remind.indexOf('1d')>=0) {
                    reminds[4] = true;
                }
                this.data.reminds = reminds;
                this.data.screening = $.extend({}, this.screening, screening);
                this.data.signin = $.extend({}, this.data.signin, signin);
                this.detail.find('button[data-target="#signedMeeting"]');
            }
            if (this.data.type == 2) {
                var reminds = [undefined,undefined,undefined,undefined,undefined];
                if (this.data.remind.indexOf('0')>=0) {
                    reminds[0] = true;
                }
                if (this.data.remind.indexOf('15')>=0) {
                    reminds[1] = true;
                }
                if (this.data.remind.indexOf('1h')>=0) {
                    reminds[2] = true;
                }
                if (this.data.remind.indexOf('2h')>=0) {
                    reminds[3] = true;
                }
                if (this.data.remind.indexOf('1d')>=0) {
                    reminds[4] = true;
                }
                this.data.reminds = reminds;
            }
            this.data.opt = this.o;
            // 状态测试
            // this.data.userRole="7";
            // this.data.status="1";
            this.detail.html(soda(detailTmpl, this.data));
        },
        bindEvent: function bindEvent() {
            var self = this;
            // 关闭
            this.detail.on('click', '.btn-close', function () {
                    self.data.type = 0;
                    self.detail.remove();
                    $('.float-right').show();
                }
            );
            // 修改会议
            this.detail.on('click', '.actions .btn-meeting-edit', function () {
                self.data.type = 2;
                self.data.isPay=fetchs.status;
                self.detail.html(soda(detailTmpl, self.data));
                $("#addPersons").persons({
                    class:'person',
                    type:0,
                    orgId:1,
                    selected:[{"id":""}],
                    container:'#addPersons',
                    template:'<ul class="list groups">' +
                    '<li class="" ng-repeat="item in data.sysOrganizations" data-id="{{item.sysOrganization.id}}"  data-org="{{item.org}}"><p data-leaf="{{item.leaf}}"><span class="icon icon-organiz-unit"></span><span class="name" ng-html="item.sysOrganization.orgName|keylight:key"></span></p></li>' +
                    '</ul>' +
                    '<div class="line" ng-if="data.sysUser.length>0"></div>' +
                    '<ul class="list personnel">' +
                    '<li ng-repeat="item in data.sysUser" class="" data-id="{{item.id}}" data-org="{{item.org}}"><span class="nophoto small" ng-if="!item.smallPicture"><i>{{item.icon}}</i></span><img class="user-url" ng-if="item.smallPicture" src="{{item.smallPicture}}"><span class="name"><span style="display: inline-block; min-width: 60px;" ng-html="item.name|keylight:key"></span><span ng-if="item.participantStatus" class="conflict"><i class="icon icon-error"></i>有会议冲突</span></span></li>' +
                    '</ul>'
                });
                $("#organizer").persons({
                    class:'person',
                    type:1,
                    orgId:1,// 触发按钮和选择结果合并 使用 .trigger 中显示选择结果
                    identical:true,
                    container:'#addPersons',
                    template:'<ul class="list groups">' +
                    '<li class="nopt" ng-repeat="item in data.sysOrganizations" data-id="{{item.sysOrganization.id}}"><p data-leaf="{{item.leaf}}"><span class="icon icon-organiz-unit"></span><span class="name" ng-html="item.sysOrganization.orgName|keylight:key"></span></p></li>' +
                    '</ul>' +
                    '<div class="line" ng-if="data.sysUser.length>0"></div>' +
                    '<ul class="list personnel">' +
                    '<li ng-repeat="item in data.sysUser" class="" data-id="{{item.id}}"><span class="nophoto small" ng-if="!item.smallPicture"><i>{{item.icon}}</i></span><img class="user-url" ng-if="item.smallPicture" src="{{item.smallPicture}}"><span class="name"><span style="display: inline-block; min-width: 56px;" ng-html="item.name|keylight:key"></span><span ng-if="item.participantStatus" class="conflict"><i class="icon icon-error"></i>有会议冲突</span></span></li>' +
                    '</ul>'
                });
            });
            // 取消编辑会议
            this.detail.on('click', '.btn-cancel-edit', function () {
                    self.data.type = 0;
                    self.detail.html(soda(detailTmpl, self.data));
                    // self.detail.remove();
                }
            );

            // 修改会议按钮disabled
            this.detail.on('change', 'input, textarea', function(){
                self.detail.find('button[type="submit"]')[0].disabled = false;
            })
            this.detail.on('shown.bs.persons', '#organizer,.tags', function (event) {
                self.detail.find('button[type="submit"]')[0].disabled = false;
            })
            this.detail.on('delete.bs.persons', '#organizer,.tags', function (event) {
                self.detail.find('button[type="submit"]')[0].disabled = false;
            })
            this.detail.on('shown.bs.timerange', '#timerange', function (event) {
                self.detail.find('button[type="submit"]')[0].disabled = false;
            })
            this.detail.on('shown.bs.boardroom', '.place', function (event) {
                self.detail.find('button[type="submit"]')[0].disabled = false;
            })
            // 会议响应
            this.detail.on('click', '.responds button', function () {
                    var replyState = $(this).data('respond');
                    var $this = $(this)
                    $this.attr("disabled","disabled");
                    fetchs.post('/meeting/replymeeting', {
                        meetingId: self.data.conferenceId,
                        replyState: replyState
                    }, function (res) {
                        if(res.ifSuc == 1){
                            self.detail.find('.responds button').removeAttr('disabled');
                            $this.attr("disabled","disabled");
                            delete res.data.conferenceId;
                            self.data = $.extend({}, self.data, res.data);
                            self.renderData();
                        }else{
                            $this.removeAttr('disabled');
                        }
                    });
                }

                // 留言
            );
            this.detail.on('keyup', '.leave-word .textArea', function (event) {
                var msg = $(this).text().trim();
                if(msg.length>200){
                    self.leaveword=msg=msg.slice(0,200);
                    $('.leave-word .textArea').text(msg);
                    $('.leave-word .textArea').select();
                    var sel = window.getSelection();
                    var range = document.createRange();
                    range.collapse(false);
                    sel.removeAllRanges();
                    sel.addRange(range);
                }
            });
            this.detail.on('keydown', '.leave-word .textArea', function (event) {
                    var msg = $(this).text().trim();
                    if (event.keyCode == '13' && msg != '') {
                            $(this).blur();
                            fetchs.post('/meeting/addmeetingdynamic', {
                                conferenceId: self.data.conferenceId,
                                content: msg
                            }, function (res) {
                                if (res.ifSuc == 1) {
                                    notify('success', '留言成功');
                                    self.leaveword = '';
                                    self.data = $.extend({}, self.data, res.data);
                                    self.renderData();
                                    reboard();
                                    var dynamicsTpl = '\n  <div ng-if="type==0" class="notices">\n         <h6>会议动态<span>{{dynamics.length}}</span></h6>\n          <ul>\n            <li ng-repeat="item in dynamics">\n        <span ng-if="item.sys == 0 && item.photo" class="nophoto " style="background-color: #fff"><img style="width:100%; height: 100%;vertical-align: bottom;border-radius: 50%;" src="{{item.photo}}"></span>    <span ng-if="item.sys == 0 && !item.photo" class="nophoto small {{item.name|charcode}}"><i>{{item.name|slice:-2}}</i></span>\n                    <span ng-if="item.sys == 1" class="icon icon-systeminfo"></span>\n              <span class="timeago">{{item.timeago|timeago}}</span>\n              <p><span class="name" ng-if="item.sys == 0" ng-html="item.name|keylight:keyword"></span><span class="name" ng-if="item.sys == 0">\uFF1A</span><span class="message" ng-if="item.sys ==0">{{item.message}}</span> <span class="message" ng-if="item.sys != 0" ng-html="item.message|sysname:item.name"></span></p>\n            </li>\n          </ul>\n        </div>\n';
                                    $('.meeting-dynamics').html(soda(dynamicsTpl, self.data));
                                }
                            });

                        return false;
                    }
                }
            );
            this.detail.on('focus','.leave-word .textArea',function() {

                if (self.leaveword != ''){
                    $(this).text(self.leaveword).css({'color':'#333'});
                } else {
                    if($(this).text()=='请输入留言，按 Enter 快速发布'){
                        $(this).text('').css({'color':'#333'});
                    }
                }

            });
            this.detail.on('blur','.leave-word .textArea',function() {
                if($(this).text()==''){
                    self.leaveword="";
                    $(this).text('请输入留言，按 Enter 快速发布').css({'color':'#aaa'});
                } else {
                    self.leaveword = $(this).text();
                    $(this).text('请输入留言，按 Enter 快速发布').css({'color':'#aaa'});
                }
            });
            $('body').on('click',function(e) {
                if (e.target != $('.leave-word')&&e.target!=$('.leave-word .textArea')&& $('.leave-word').find(e.target).length < 1) {
                    $('.publish-box').hide();
                    $('.detail .leave-word .textArea').css({'background-color':'#f6f6f6'})
                }else{
                    $('.publish-box').show();
                    $('.detail .leave-word .textArea').css({'background-color':'#fff'})
                }
            });
            this.detail.on('click', '.leave-word button', function (event) {
                var msg = self.leaveword.trim();
                if(msg!='请输入留言，按 Enter 快速发布'){
                    if(msg.replace(/(^s*)|(s*$)/g, "").length ==0){
                        notify('danger','发送失败，留言不能为空');
                    }else {
                        fetchs.post('/meeting/addmeetingdynamic', {
                            conferenceId: self.data.conferenceId,
                            content: msg
                        }, function (res) {
                            if (res.ifSuc == 1) {
                                self.leaveword = '';
                                notify('success', '留言成功');
                                self.data = $.extend({}, self.data, res.data);
                                self.renderData();
                                reboard();
                                var dynamicsTpl = '\n  <div ng-if="type==0" class="notices">\n         <h6>会议动态<span>{{dynamics.length}}</span></h6>\n          <ul>\n            <li ng-repeat="item in dynamics">\n        <span ng-if="item.sys == 0 && item.photo" class="nophoto " style="background-color: #fff"><img style="width:100%; height: 100%;vertical-align: bottom;border-radius: 50%;" src="{{item.photo}}"></span>    <span ng-if="item.sys == 0 && !item.photo" class="nophoto small {{item.name|charcode}}"><i>{{item.name|slice:-2}}</i></span>\n                    <span ng-if="item.sys == 1" class="icon icon-systeminfo"></span>\n              <span class="timeago">{{item.timeago|timeago}}</span>\n              <p><span class="name" ng-if="item.sys == 0" ng-html="item.name|keylight:keyword"></span><span class="name" ng-if="item.sys == 0">\uFF1A</span><span class="message" ng-if="item.sys ==0">{{item.message}}</span> <span class="message" ng-if="item.sys != 0" ng-html="item.message|sysname:item.name"></span></p>\n            </li>\n          </ul>\n        </div>\n';
                              $('.meeting-dynamics').html(soda(dynamicsTpl, self.data));
                            }
                        });
                    }
                }
            })
            // 预订会议
            this.detail.on('submit', '#detailForm', function (e) {
                    e.preventDefault();
                    var form = $(this);
                    var params = $(this).serializeArray();
                    var data = {
                        notice: 1
                    };
                    // if (self.data.type == 1) {
                    //   data.notice = 1
                    // }
                    data.attendees = [];
                    for (var item in params) {
                        if (params[item].name == 'start' || params[item].name == 'end' || params[item].name == 'clientType') {
                            data[params[item].name] = parseInt(params[item].value);
                        } else if (params[item].name == 'location' || params[item].name == 'organizer' || params[item].name == 'department') {
                            var name = self.detail.find('input[name="' + params[item].name + '"]').prev('span.value').text();
                            data[params[item].name] = {
                                id: params[item].value,
                                name: name
                            };
                        } else {
                            if (data[params[item].name]) {
                                data[params[item].name] = data[params[item].name] + ',' + params[item].value;
                            } else {
                                data[params[item].name] = params[item].value;
                            }
                        }
                    }
                    self.detail.find('span[data-id]').each(function () {
                        data.attendees.push({
                            id: $(this).data().id,
                            dep: $(this).data().org,
                            name: $(this).text()
                        });
                    });
                    if (!data.isPublic) {
                        data.isPublic = 1
                    }
                    if (data.title == '') {
                        notify('danger', '请填写会议主题');
                        return false;
                    }
                    if (!data.start) {
                        notify('danger','请选择时间')
                        return false;
                    }
                    if (data.location.id == '') {
                        notify('danger','请选择地点')
                        return false;
                    }
                    if (data.start < new Date().getTime()) {
                        notify('danger', '会议开始时间不能早于当前时间');
                        return false;
                    }
                    if (self.o.min != 0 && (data.end - data.start) / (60 * 1000) < self.o.min) {
                        notify('danger', '会议时长不能小于' + self.o.min + '分钟');
                        return false;
                    }
                    if (self.o.max != 0 && (data.end - data.start) / (60 * 60 * 1000) > self.o.max) {
                        notify('danger', '会议时长不能大于' + self.o.max + '小时');
                        return false;
                    }
                    // if(self.data.type == 2){
                    //   var difference = {
                    //     conferenceId: self.data.conferenceId
                    //   };
                    //   _.each(data, function(value, key){
                    //     if (_.isObject(value)) {
                    //       if (!_.isEqual(self.data[key], value)) {
                    //         difference[key] = value
                    //       }
                    //     }else if (_.isArray(value)) {
                    //       if (!_.isEqual(self.data[key], value)) {
                    //         difference[key] = value
                    //       }
                    //     }else{
                    //       if (self.data[key] != value) {
                    //         difference[key] = value
                    //       }
                    //     }
                    //   })
                    //   data = difference
                    // }
                    form.find('button[type="submit"]').attr("disabled", "disabled");
                    fetchs.post('/meeting/addorupdatemeeting', {data: JSON.stringify(data)}, function (res) {
                        if (res.ifSuc == 1) {
                            if (self.data.type == 1) {
                                notify('success', '预订成功');
                            }
                            var id = res.data;
                            self.data.type = 0;

                            fetchs.post('/meeting/selectmeetingdetails', {meetingId: res.data}, function (res) {
                                    if (res.ifSuc == 1) {
                                        self.data = $.extend({}, self.data, res.data);
                                        self.data.conferenceId = id;
                                        self.renderData();
                                        reboard();
                                        reverify();
                                        // var event = {
                                        //   meetingSubject: res.data.title,
                                        //   start: res.data.start,
                                        //   end: res.data.end,
                                        //   location: res.data.location.name,
                                        //   meetingId: id,
                                        //   name: res.data.bookeder.name,
                                        //   phone:"13146226688",
                                        //   photo:"http://10.10.11.56/yunmeeting/M00/00/09/gLOFmf4w-AdADPAAPwB8QSiyU926.jpg",
                                        //   resourceId:"75778302ead644ee8398e548d5161d7d",

                                        //   state: res.data.status
                                        // }
                                    }else {
                                        notify('danger',res.msg);
                                    }
                                }
                                // self.data = this.data = $.extend({}, self.data, data);
                                // self.data.type = 0;
                                // self.data.conferenceId = res.data;
                                // self.detail.html(soda(detailTmpl, self.data));
                            );
                        } else {
                            notify('danger', res.msg);
                            form.find('button[type="submit"]').removeAttr("disabled");
                        }
                    });
                }
            );
            // 取消会议
            $('#cancelMeeting form').unbind('submit').on('submit', function (e) {
                    $('#cancelMeeting form').find('button[type="submit"]')[0].disabled = true;
                    e.preventDefault();
                    var data = eval('(' + '{' + $(this).serialize().replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
                    fetchs.post('/meeting/cancelmeeting', data, function (res) {
                        if (res.ifSuc == 1) {
                            $('#cancelMeeting form')[0].reset();
                            $('#cancelMeeting').modal('hide');
                            self.data = $.extend({}, self.data, res.data);
                            self.data.status = 5;
                            self.detail.html(soda(detailTmpl, self.data));
                            reboard();
                            reverify();
                        } else {
                            notify('danger', res.msg);
                        }
                    });
                }
                // 签到统计
            );
            // 签到
            $('#signinMeeting form').unbind('submit').on('submit', function(e){
                e.preventDefault();
                var data = eval('(' + '{'+$(this).serialize().replace(/&/g,'",').replace(/=/g,':"')+'"}' + ')');
                fetchs.post('/meeting/meetingsign', data, function(res){
                    if (res.ifSuc==1) {
                        $('#signinMeeting form')[0].reset();
                        $('#signinMeeting').modal('hide');
                        self.data = $.extend({}, self.data, res.data);
                        self.data.signin = {
                            yes:0,
                            no:0
                        }
                        self.renderData();
                    }else{
                        notify('danger', res.msg)
                    }
                })
            });
            //审核未通过
            $('#unauditedMeeting form').unbind('submit').on('submit', function (event) {
                event.preventDefault();
                if($(".auditReson_textarea").val().trim()==""&&$(".auditReson_textarea").is(":hidden")!=true){
                    $(".auditReson_textarea").addClass("reson_danger");
                    return;
                }else {
                    $(".reson_danger").removeClass("reson_danger");
                }
                var data = eval('(' + '{' + decodeURIComponent($(this).serialize(), true).replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
                data.reason = data.reason !="其他"?data.reason:data.reason1;
                isAudit(data,0);
            });
            //审核通过的提示弹框
            $('#directAuditedMeeting form').unbind('submit').on('submit', function (event) {
                event.preventDefault();
                var data = eval('(' + '{' + decodeURIComponent($(this).serialize(), true).replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
                isAudit(data,2);
            });
            //同时间段有其他会议时的确认提示审核弹框
            $('#auditedMeeting form').unbind('submit').on('submit', function (event) {
                event.preventDefault();
                var data = eval('(' + '{' + decodeURIComponent($(this).serialize(), true).replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
                isAudit(data,2);
            });
            //会议审核接口
            function isAudit(data,state) {
                fetchs.post('/meeting/auditmeeting', {
                    meetingId: data.meetingId,
                    reason: data.reason,
                    auditState:state,
                    address:data.mettingLocation,
                }, function (res) {
                    if(res.ifSuc == 1){
                        self.data = $.extend({}, self.data, res.data);
                        if(state==0){
                            $('#unauditedMeeting form')[0].reset();
                            $('#unauditedMeeting').modal('hide');
                            self.detail.html(soda(detailTmpl, self.data));
                        }else if(state==2){
                            if(selectMore){
                                $('#auditedMeeting').modal('hide');
                            }else {
                                $('#directAuditedMeeting').modal('hide');
                            }
                            self.detail.html(soda(detailTmpl, self.data));
                        }
                        reboard();
                        reverify();
                    }else {
                        if(state==0){
                            $('#unauditedMeeting form')[0].reset();
                            $('#unauditedMeeting').modal('hide');
                            $(".auditReson_textarea").val("");
                            $(".auditReson_textarea").hide();
                            $(".auditReson_textarea").attr("placeholder","");
                        }else if(state==2){
                            if(selectMore){
                                $('#auditedMeeting').modal('hide');
                            }else {
                                $('#directAuditedMeeting').modal('hide');
                            }
                        }
                        notify("danger",res.msg);
                    }
                });
            }
            //审核不通过modal隐藏时,重置表格内容
            $('#unauditedMeeting').on('hide.bs.modal', function () {
                $(".auditReson_textarea").val("");
                $(".auditReson_textarea").hide();
                $('#unauditedMeeting form')[0].reset();
                $(".auditReson_textarea").attr("placeholder","");
            })
            $('#signedMeeting').on('show.bs.modal', function (event) {
                var data = self.data.allAttendees;
                var signin = self.data.signin;
                var modal = $(this);
                var tpl = '\n          <h6>\u5DF2\u7B7E\u5230\uFF08{{signin.yes}}\uFF09</h6>\n          <ul>\n            <li ng-repeat="item in data" ng-if="item.signTime">\n     <span ng-if="item.photo" class="nophoto small" style="background-color: #fff"><img style="width:100%; height: 100%;vertical-align: bottom;border-radius: 50%;" src="{{item.photo}}"></span>          <span class="nophoto small bgc1" ng-if="!item.photo"><i>{{item.name|slice:-2}}</i></span>\n              <span class="itemName">{{item.name}}</span> <span>{{item.signTime|date:\'H:mm\'}}</span>\n            </li>\n          </ul>\n          <h6>\u672A\u7B7E\u5230\uFF08{{signin.no}}\uFF09</h6>\n          <p>\n            <span ng-repeat="item in data" ng-if="!item.signTime">{{item.name}}<i ng-if="item.statu==0">(\u5DF2\u62D2\u7EDD)</i></span>\n          </p>';
                modal.find('.signed').html(soda(tpl, {data: data, signin: signin}));
                }
            );
            // 变更组织者
            this.detail.on('hidden.bs.persons', '#organizer', function (event) {
                if (event.organizer) {
                    self.detail.find('#department span.value').text(event.organizer.orgName);
                    self.detail.find('#department input').val(event.organizer.orgId);
                    self.detail.find('#addPersons span[data-id="'+event.organizer.id+'"]').remove();
                    self.detail.find('#addPersons span[data-id]').append('<i class="icon icon-delete-personnel"></i>').addClass('add');
                    self.detail.find('#addPersons.tags').prepend('<span data-id="'+event.organizer.id+'" data-org="0">'+event.organizer.userName+'</span>');
                    $('input[name="organizer"]').val(event.selected[0].id);
                    getNums();
                }
            });
            this.detail.find("#addPersons").persons({
                class:'person',
                type:0,
                orgId:1,
                container:'#addPersons',
                template:'<ul class="list groups">' +
                '<li class="" ng-repeat="item in data.sysOrganizations" data-id="{{item.sysOrganization.id}}"  data-org="{{item.org}}"><p data-leaf="{{item.leaf}}"><span class="icon icon-organiz-unit"></span><span class="name" ng-html="item.sysOrganization.orgName|keylight:key"></span></p></li>' +
                '</ul>' +
                '<div class="line" ng-if="data.sysUser.length>0"></div>' +
                '<ul class="list personnel">' +
                '<li ng-repeat="item in data.sysUser" class="" data-id="{{item.id}}" data-org="{{item.org}}"><span class="nophoto small" ng-if="!item.smallPicture"><i>{{item.icon}}</i></span><img class="user-url" ng-if="item.smallPicture" src="{{item.smallPicture}}"><span class="name"><span style="display: inline-block; min-width: 60px;" ng-html="item.name|keylight:key"></span><span ng-if="item.participantStatus" class="conflict"><i class="icon icon-error"></i>有会议冲突</span></span></li>' +
                '</ul>'
            });
            this.detail.find("#organizer").persons({
                class:'person',
                type:1,
                orgId:1,
                identical:true,
                container:'#addPersons',
                template:'<ul class="list groups">' +
                '<li class="nopt" ng-repeat="item in data.sysOrganizations" data-id="{{item.sysOrganization.id}}"><p data-leaf="{{item.leaf}}"><span class="icon icon-organiz-unit"></span><span class="name" ng-html="item.sysOrganization.orgName|keylight:key"></span></p></li>' +
                '</ul>' +
                '<div class="line" ng-if="data.sysUser.length>0"></div>' +
                '<ul class="list personnel">' +
                '<li ng-repeat="item in data.sysUser" class="" data-id="{{item.id}}"><span class="nophoto small" ng-if="!item.smallPicture"><i>{{item.icon}}</i></span><img class="user-url" ng-if="item.smallPicture" src="{{item.smallPicture}}"><span class="name"><span style="display: inline-block; min-width: 60px;" ng-html="item.name|keylight:key"></span><span ng-if="item.participantStatus" class="conflict"><i class="icon icon-error"></i>有会议冲突</span></span></li>' +
                '</ul>'
            });
        }
    };
    function getNums() {
        var self = this;
        var data = {
            attendees: []
        };
        $('body').find("#addPersons").find('span[data-id]').each(function () {
            data.attendees.push({
                id: $(this).data().id,
                dep: $(this).data().org == undefined ? 0 : $(this).data().org,
                name: $(this).text()
            });
        });
        fetchs.post('/meeting/getParticipantsNumber', { attendees: JSON.stringify(data) }, function (res) {
            if (res.ifSuc == 1) {
                $('body').find("#addPersons").find('span.count').text('共' +res.data+ '人参会');
            } else {
                notify('danger', res.msg);
            }
        });
    }
    var detailPlugin = function detailPlugin(option) {
        var args = Array.apply(null, arguments);
        args.shift();
        var internal_return;
        this.each(function () {
            var $this = $(this),
                data = $this.data('detail'),
                options = (typeof option === 'undefined' ? 'undefined' : _typeof(option)) === 'object' && option;
            if (!data) {
                var opts = $.extend({}, $.fn.detail.defaults, options);
                data = new Detail(this, opts);
                $this.data('detail', data);
            }
            if (typeof option === 'string' && typeof data[option] === 'function') {
                internal_return = data[option].apply(data, args);
            }
        });
        if (internal_return === undefined || internal_return instanceof Detail) return this;
        if (this.length > 1) throw new Error('Using only allowed for the collection of a single element (' + option + ' function)'); else return internal_return;
    };
    $.fn.detail = detailPlugin;
    $.fn.detail.defaults = {
        max: 0, // 单次会议最大时长
        min: 15, // 单次会议最小时长
        cycle: 0 // 可预订周期
    };
    $(document).on('click', '[data-toggle="detail"]', function (e) {
            var $this = $(this);
            if ($this.data('detail')) return;
            e.preventDefault();
            // component click requires us to explicitly show it
            detailPlugin.call($this, 'show');
        }
        // 删除会议
    );
    // 会议签到
    $('#signinMeeting').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var id = button.data('id');
        var modal = $(this);
        modal.find('input[name="meetingId"]').val(id);
    })
    $('#deleteMeeting').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var id = button.data('id');
        var title = button.data('title');
        var modal = $(this);
        modal.find('input[name="meetingId"]').val(id);
        modal.find('input[name="title"]').val(title);
    });
    $('#deleteMeeting form').on('submit', function (e) {
        e.preventDefault();
        $('#deleteMeeting form').find('button[type="submit"]')[0].disabled = true;
        var data = eval('(' + '{' + $(this).serialize().replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
        fetchs.post('/meeting/cancelmeeting', data, function (res) {
            if (res.ifSuc == 1) {
                $('#cancelMeeting form')[0].reset();
                $('#cancelMeeting').modal('hide');
                gotoMyMeeting();
            } else {
                notify('danger', res.msg);
            }
            $('#deleteMeeting form').find('button[type="submit"]')[0].disabled = false;
        });
    });
    $('body').on('click', '.review-meeting h6,.meeting-plan h6,.meeting-info h6,.notices a[data-id]', function (e) {
            e.preventDefault();
            var id = $(this).data('id');
            var self = $(this);
            if (!!id){
                fetchs.post('/meeting/selectmeetingdetails', {meetingId: id}, function (res) {
                    if (res.ifSuc == 1) {
                        res.data.conferenceId = id;
                        self.detail('show', res.data);
                        $('.float-right').hide();
                    }else {
                        $('.float-right').hide();
                        notify('danger', res.msg)
                    }
                });
            }

        }
    );
    // 取消会议
    $('#cancelMeeting').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var id = button.data('id');
        var title = button.data('title');
        var modal = $(this);
        modal.find('input[name="meetingId"]').val(id);
        modal.find('input[name="title"]').val(title);
        $('#cancelMeeting form').find('button[type="submit"]')[0].disabled = false;
        if (button.parents('.popover').length > 0) {

            $('#cancelMeeting form').unbind('submit').on('submit', function (e) {
                e.preventDefault();
                $('#cancelMeeting form').find('button[type="submit"]')[0].disabled = true;
                var data = eval('(' + '{' + $(this).serialize().replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
                fetchs.post('/meeting/cancelmeeting', data, function (res) {
                    if (res.ifSuc == 1) {
                        $('#cancelMeeting form')[0].reset();
                        $('#cancelMeeting').modal('hide');
                        reboard();
                        reverify();
                    } else {
                        notify('danger', res.msg);
                    }
                    $(".break_textarea").val("");
                });
            });
        }
    });

    $('#cancelMeeting').on('show.bs.modal', function (event) {
        $(".break_textarea").val("");
    });

    // 刷新日历看板
    function reboard(){
        if($('#timeline').length>0){
            $('#timeline').fullCalendar('refetchEvents')
        }
        if($('#agenda').length>0){
            $('#agenda').fullCalendar('refetchEvents')
        }
    }

    function reverify() { //刷新会议审核页面
        try
        {
            if(typeof(eval(getReviewMeeting))=="function")
            {
                getReviewMeeting(0);
            }
        }catch(e)
        {

        }
    }
    //会议审核通过点击事件
    $('body').on('click', '.auditedMeeting', function (event) {//点击了会议审核不通过按钮
        var button = $(".auditedMeeting");
        var end =button.data('end');
        var now=new Date().getTime();
        var location = button.data('location');
        if(now>end){//如果当前时间大于开始时间(超时)
            notify("danger","会议已过期，可通知预订人处理该会议");
            return;
        }else {
            var id = button.data('id');
            var auditObj={
                title : button.data('roomname'),
                end :  button.data('end'),
                time : button.data('time'),
                id : button.data('id'),
                roomId:button.data('roomid')
            }
            //分别给两个弹框赋值
            var modal = $("#auditedMeeting");
            modal.find('input[name="meetingId"]').val(id);
            modal.find('input[name="mettingLocation"]').val(location);

            var modal1 = $("#directAuditedMeeting");
            modal1.find('input[name="meetingId"]').val(id);
            modal1.find('input[name="mettingLocation"]').val(location);
            //在此处做判断
            selectmoreauditmeeting(auditObj);
        }
    });
    function selectmoreauditmeeting (auditObj) {
        fetchs.post('/meeting/selectmoreauditmeeting', {
            meetingId:auditObj.id,
            meetingName:auditObj.title,
            roomId:auditObj.roomId,
            start:auditObj.time,
            end:auditObj.end,
        }, function (res) {
          if(res.ifSuc == 1){
            if(res.data==0){ //区分该会议时间内还有其他预定申请,若审核通过,其他会议将不通过审核.
                 selectMore=0;
                $("#directAuditedMeeting").modal("show");
             }else if(res.data==1){
                selectMore=1;
                $("#auditedMeeting").modal("show");
             }
                reboard();
            }else {
                notify("danger",res.msg);
            }
        });
    }
    //会议审核不通过点击事件
    $('body').on('click', '.unauditedMeeting', function (event) {//点击了会议审核通过按钮
        var button = $(".unauditedMeeting");
        var start =  button.data('time');
        var end = button.data('end');
        var now=new Date().getTime();
        if(now>end){//如果当前时间大于开始时间(超时)
            notify("danger","会议已过期，可通知预订人处理该会议");
            return;
        }else {
            var id = button.data('id');
            var location =  button.data('location');
            var modal = $("#unauditedMeeting");
            modal.find('input[name="meetingId"]').val(id);
            modal.find('input[name="mettingLocation"]').val(location);
            $("#unauditedMeeting").modal("show");
        }
    });
    //会议审核不通过弹框中的单选按钮点击事件
    $("body").on("click",".radio",function(event){
        $(".reson_danger").removeClass("reson_danger");
        if(event.currentTarget.childNodes[1].defaultValue=="其他"){
            $(".auditReson_textarea").show();
            $(".auditReson_textarea").attr("placeholder","请填写其他原因");
        }else{
            $(".auditReson_textarea").val("");
            $(".auditReson_textarea").hide();
            $(".auditReson_textarea").attr("placeholder","");
        }
        event.stopPropagation();
    });
    //修改会议
    $('body').on('click', '.popover .btn-meeting-edit', function (e) {
        e.preventDefault();
        var id = $(this).data('id');
        var self = $(this);
        fetchs.post('/meeting/selectmeetingdetails', {meetingId: id}, function (res) {
            if (res.ifSuc == 1) {
                res.data.conferenceId = id;
                res.data.type = 2;
                //新建会议的时候拿到是否是付费用户状态
                //存储的付费状态信息与获取到的不一致时,从新赋值
                if(res.data.tenantType && fetchs.status != res.data.tenantType){
                    var tenantType ={'status':res.data.tenantType};
                    localStorage.setItem('tenantType',JSON.stringify(tenantType));
                    fetchs.status= tenantType.status;
                    res.data.isPay= tenantType.status;
                }else {
                    res.data.isPay=fetchs.status;
                }
                self.detail('show', res.data);
            }else {
                notify('danger',res.msg);
            }
        });
    });
})();
