'use strict';



  /*删除标签背景*/
    $(".nav-item").removeClass("flagBackground");
  /*初始化标签背景*/
    $(".nav-item:eq(1)").addClass("flagBackground");

  soda.prefix('ng-');

  soda.filter('statu', function (statu) {
      if (statu==null) statu = '';
    switch (statu) {
      case '1':
      case 1:
        {
          return '等待审批中';
          break;
        }
      case '2':
      case 2:
        {
          return '审批通过';
          break;
        }
      case '0':
      case 0:
        {
          return '审批未通过';
          break;
        }
      default:
        {
          return '';
          break;
        }
    }
  });
  soda.filter('name', function (name) {
    if(name==null) return '';
    if (name.length == 0) {
      return '';
    } else if (name.length <= 2) {
      return name;
    }
    return name.substring(name.length - 2, name.length);
  });

  var userInfo = JSON.parse(localStorage.getItem('userinfo'));
  if (userInfo == null) {
    userInfo = {
      userId: '123',
      token: '123'
    };
  };
  var toreview = 0; //待审批的
  var nopass = 0; //未通过
  var passed = 0; //已通过
  var reviewMeetings = new Array(); //我审核的会议
  // var opers = new Array();
  var reviews = new Array();
  var page = {
    pageNum: 1, //页数
    pageSize: 15 //每页数据数
  };
  var key = '';
  $('#review-loading').hide();

  var verifypageType = $('#verifypageType').val();
  if(verifypageType==3){
      // var  _this = $('.mouse')[0];
      // console.log(_this);
      // $('.toreview-class').removeClass('hover');
      $('#2').css('color', '#27a1f2');
      $('#2').hide();
      if (reviews.indexOf(2) < 0) {
          $('#2').show();
          reviews.push(2);
      }
  }
  getReviewMeeting(0); //0-获取新数据 1-加载更多

    $('.active').on('click',function () {
        window.location.reload();
    });

  //鼠标悬停效果
  $('.mouse').mousemove(function (event) {
    var id = $(this).data('id');
    if (reviews.indexOf(id) < 0) {
      //如果没有选中显示鼠标经过的效果
      $(this).addClass('hover');
      $('#' + id).show();
      $('#' + id).css('color', '#cccccc');
    }
  });
  //鼠标移走效果
  $('.mouse').mouseleave(function (event) {
    var id = $(this).data('id');
    $(this).removeClass('hover');
    if (reviews.indexOf(id) < 0) {
      //如果没有选中设置鼠标移走的效果
      $('#' + id).css('color', '#27a1f2');
      $('#' + id).hide();
    }
  });
  //鼠标点击效果
  $('.mouse').on('click', function (event) {
    //重置页码
    page.pageNum = 1;
    var id = $(this).data('id');
    $(this).removeClass('hover');
    $('#' + id).css('color', '#27a1f2');
    $('#' + id).hide();
    if (reviews.indexOf(id) < 0) {
      $('#' + id).show();
      reviews.push(id);
    } else {
      $('#' + id).hide();
      var index = reviews.indexOf(id);
      reviews.splice(index, 1);
      //恢复悬停效果
      $(this).addClass('hover');
      $('#' + id).show();
      $('#' + id).css('color', '#cccccc');
    }
    console.log(reviews);

    getReviewMeeting(0);
  });

  //监听搜索框键盘事件
  $('#search').on('keypress', function (e) {
    if (e.keyCode == 13) {
      $('.input-search').removeClass('input-heighLight')
      $(this).blur();
      page.page = 1;
      page.size = 15;
      key = $(this).val();
        if(key.length>0){
            //显示删除按钮
            $('#del-searchList').show();
        }else {
            $('#del-searchList').hide();
        }
      getReviewMeeting(0);
      return false;
    };
  });

    $('#search').bind('input propertychange', function() {
        if($(this).val().length>0){
            $('#del-searchList').show();
        }else{
            $('#del-searchList').hide();
        }
    });
  $('#search').bind('focus','input',function () {
    $('.input-search').addClass('input-heighLight')
    // $('#del-searchList').hide();
  })
  $('#search').bind('blur','input',function () {
    $('.input-search').removeClass('input-heighLight')
  })
    $('#del-searchList').on('click',function () {
        $('#del-searchList').hide();
        $('#search').val('');
        key = '';
        getReviewMeeting(0);
    })
  //我审核的会议
    var reviewTpl = '\n      <ul ng-if="data.length>0">\n        <li ng-repeat="item in data" ng-class="item.state === \'0\'? \'notpass\':\'\'">\n          <div class="col-3 float-left">\n            <span class="times" ng-html="item.takeStartDate|period:item.takeEndDate"></span>\n          </div>\n          <div class="col-9 float-right">\n            <div class="col-12 height">\n              <div class="col-6 float-left">\n                <h6 class="title" data-toggle="tooltip"  data-id="{{item.id}}" data-title="{{item.conferenceName}}" ng-html="item.conferenceName|keylight:key"></h6>\n                <p ng-if="item.address!=null & item.address.length!=0"><i class="icon icon-room-address"></i><span ng-html="item.address|keylight:key"></span></p>\n              </div>\n              <div class="col-6 float-right">\n                <span class="organizer">\n                  <img class="user-url" ng-if="item.userNameUrl" src="{{item.userNameUrl}}"><span ng-if="!item.userNameUrl"  ng-class="item.userName ? \'nophoto\':\'notext\'" ng-html="item.userName|name"></span> <span ng-html="item.userName|keylight:key"></span>\n                </span>\n                <span class="statu">\n                  <span ng-html="item.state|statu"></span>\n                  <br>\n                  <span class="icon icon-question" style="font-size: 14px;color:#BBC4CC" ng-if="item.state === \'0\'" data-toggle="notpass-reason" data-title="{{item.auditWhy}}"></span>\n                </span>\n\n              </div>          \n            </div>\n            <div ng-if="item.personsVos!=null | item.personsVos.length!=0">\n              <span ng-repeat="person in item.personsVos" ng-if="$index<4" class="person">{{person.userName}}</span>\n              <span ng-if="item.persons | item.persons.length!=0" class="person-count">\u5171{{item.count}}\u4EBA\u53C2\u4F1A</span>\n            </div>\n          </div>\n      </li>\n    </ul>\n    <div ng-if="data.length==0 & key.length==0" class="nothing">\u6682\u65F6\u6CA1\u6709\u4F1A\u8BAE</div>\n    <div ng-if="data.length==0  & key.length!=0" class="nothing" style="height:100%">\u6CA1\u6709\u641C\u7D22\u5230\u4FE1\u606F,\u6362\u4E2A\u6761\u4EF6\u8BD5\u8BD5\uFF1F<br>\u60A8\u53EF\u4EE5\u8F93\u5165\u4F1A\u8BAE\u540D\u79F0\u3001\u7EC4\u7EC7\u8005\u3001\u4E3B\u529E\u5355\u4F4D\u3001\u4F1A\u8BAE\u5185\u5BB9\u7B49\u90E8\u5206\u5185\u5BB9\u68C0\u7D22\u3002</div>\n  ';

    var isloading = false;
    $('.right').scroll(function () {
        if ($('.right').height()+$('.right')[0].scrollTop >= $('.right')[0].scrollHeight-10) {

            if (15*page.pageNum>reviewMeetings.length) {
                $('#review-loading').hide();
            } else {
                if(isloading==false){
                    isloading =true;
                    page.pageNum += 1;
                    $('#review-loading').show();
                    getReviewMeeting(1);
                }

            }

        }
    });
    var reserveCycle = 0; //预定周期
    // 会议室预定设置信息
    fetchs.post('/meetingRoom/selectMeetingRoomReserve', {}, function (res) {
        if (res.ifSuc == 1) {
            reserveCycle = res.data.reserveCycle;
            $('#datepicker').datepicker('setDate', moment().format('YYYY-MM-DD'));
            $.fn.detail.defaults = {
                min: res.data.meetingMinimum,
                max: res.data.meetingMaximum,
                cycle: res.data.reserveCycle
            }
            var start = moment(res.data.reserveTimeStart).format('H:mm')
            var end = moment(res.data.reserveTimeEnd).format('H:mm')
            $('.freetime').timerange('setopts', {
                start: start,
                end: end
            })
            timelineOptons.selectConstraint = {
                start: start,
                end: end
            }
            timelineOptons.minTime = moment(res.data.reserveTimeStart).subtract(1,'h').format('H:mm');
            timelineOptons.maxTime = moment(res.data.reserveTimeEnd).add(1,'h').format('H:mm');
            if (timelineOptons.maxTime == '0:00'){
                timelineOptons.maxTime = '24:00';
            }
            $.fn.timerange.defaults.start = start;
            $.fn.timerange.defaults.end = end;
            $.fn.timerange.defaults.cycle = res.data.reserveCycle;
        }
    })
//加载更多
  $('#mine-loading').on('click', function () {});
  $('#review-loading').on('click', function () {});

  function getReviewMeeting(type) {
    // 审核状态，3：显示全部类型，2：待审批，1：已通过，0：未通过，（为空显示全部）
    var auditType = 3;
    if (reviews.length == 0 | reviews.length == 3) {
      //数组长度为0表示没有选择 长度为3表示选择全部，都查询全部类型
      auditType = 3;
    } else if (reviews.length == 1) {
      //长度为1表示选择一项
      auditType = reviews[0];
    } else {
      //长度为2表示选择其中两项
      if (reviews.indexOf(2) >= 0 && reviews.indexOf(1) >= 0 && reviews.indexOf(0) < 0) {
        //数组是[2,1] 表示选择待审批、已通过
        auditType = 4;
      } else if (reviews.indexOf(2) >= 0 && reviews.indexOf(0) >= 0 && reviews.indexOf(1) < 0) {
        //数组是[2,0] 表示选择待审批、未通过
        auditType = 5;
      } else if (reviews.indexOf(1) >= 0 && reviews.indexOf(0) >= 0 && reviews.indexOf(2) < 0) {
        //数组是[1,0] 表示选择已通过、未通过
        auditType = 6;
      }
    }
    fetchs.post('/search/selectAuditMeeting', { userId: userInfo.userId, auditType: auditType, currentPage: page.pageNum, pageSize: page.pageSize, searchKey: key, token: userInfo.token }, function (res) {
      if (res.data.meeting.length < page.pageSize) {
        $('#review-loading').hide();
      } else {
        $('#review-loading').show();
      }
      if (type == 0) {
        reviewMeetings = res.data.meeting;
        $('#toreview').html(res.data.pending); //待审核的
        $('#passed').html(res.data.pass); //已通过
        $('#nopass').html(res.data.notPass); //未通过
          if(key.length>0){
              $('#searchResult').show();
              var count = 0;
              if(auditType==0){
                //未通过的
                  count = parseInt(res.data.notPass);
              }else if(auditType==1){
                //已通过
                  count = parseInt(res.data.pass);
              }else if(auditType==2){
                  //待审核
                  count = parseInt(res.data.pending);
              }else if(auditType==3){
                  //全部
                  count = parseInt(res.data.notPass)+parseInt(res.data.pass)+parseInt(res.data.pending);
              }else if(auditType==4){
                  //待审批、已通过
                  count = parseInt(res.data.pass)+parseInt(res.data.pending);
              }else if(auditType==5){
                  //待审批、未通过
                  count = parseInt(res.data.notPass)+parseInt(res.data.pending);
              }else if(auditType==6){
                  //已通过、未通过
                  count =  parseInt(res.data.notPass)+parseInt(res.data.pass)
              }
              count = count!=NaN ? count:0;
              $('#searchResult').text('共搜索到'+count+'个会议')
          }else {
              $('#searchResult').hide();
          }
      } else {
        isloading = false;
        for (var i = 0; i < res.data.meeting.length; i++) {
          reviewMeetings.push(res.data.meeting[i]);
        };
        if(15*page.pageNum>reviewMeetings.length){
          $('#review-loading').hide();
        }else {
          $('.review-meeting').show();
        }
      }
      $('.review-meeting').html(soda(reviewTpl, { data: reviewMeetings,key:key }));
    });
  }

  $('body').on('mouseenter', '[data-toggle="tooltip"]', function (e) {
    var text = $(this).text();
      if(text==null||text==""){
          return;
      }
    var size = parseInt($(this).css('fontSize'));
    if ($(this).width() < size * text.length) {
      $(this).css('color', '#1d98f0');
      $(this).tooltip({
        placement: function placement(tip, element) {
          return window.scrollY + 50 < $(element).offset().top ? 'top' : 'bottom';
        },
        trigger: 'hover'
      });
      $(this).tooltip('toggle');
    }
  });
  $('body').on('mouseleave', '[data-toggle="tooltip"]', function (e) {
    $(this).css('color', '#333');
  });

  $('body').on('mouseenter', '[data-toggle="notpass-reason"]', function (e) {
    if($(this).data('title')==null||$(this).data('title')==""){
       return;
    }
      $(this).data('title',$(this).data('title').toString());
      $(this).css('color', '#1d98f0');
    $(this).tooltip({
      placement: function placement(tip, element) {
        return 'left';
      },
      trigger: 'hover'
    });
    $(this).tooltip('toggle');
  });
  $('body').on('mouseleave', '[data-toggle="notpass-reason"]', function (e) {
    $(this).css('color', '#7e868c');
  });

//# sourceMappingURL=verify.js.map
