$(function() {
  // body...
  //-------------------------------------
  // let  p=Pages();
  // p.callBack(callBack);
  // p.setCount(20);
  var p1= Pages("#page2");//创建分页器
  $('#page2').show();
  $('#page2').css('background-color','#fff');
  $('#persons .hide-thead').show();
  var personPage = 1;
  var personSize = 15;
  p1.callBack=function callBack(currentPage,page){ 
    personPage = currentPage;
    personSize = page;
    getpersonStatisticalData(1);
  } ;
  //会议室统计
  //默认开始结束时间和时间范围
  var personkey = ''; //搜索关键字
  var persontime = 7;
  var personstart = moment(new Date(new Date().getTime() - 1000*60*60*24*(persontime-1))).format('YYYY-MM-DD');
  var personend = moment(new Date()).format('YYYY-MM-DD');
  var personorgId = ''; //部门id
  var persontype = '0'; //人员统计排序type
  var dataType = '';
  // $('#person-next').hide();

  $('#person-next').attr({'disabled':'disabled'});
  var persondateRange2 = new pickerDateRange('person-date1', {
      isTodayValid:true ,
      startDate: personstart,
      endDate: personend,
      startToday: false,
      stopToday: true,
      needCompare: false,
      defaultText: ' ~ ',
      autoSubmit: false,
      inputTrigger: 'date',
      theme: 'ta',
      success: function (obj) {
          personstart = obj.startDate;
          personend = obj.endDate;
          $('#person-date1').text(moment(personstart).format('YYYY年MM月DD日') + ' ~ ' + moment(personend).format('YYYY年MM月DD日'))
          resetPersonType();
          var oldTime = new Date(personend).getTime();
          var now=new Date(personstart).getTime();
          persontime =parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
          var current = new Date(moment().format('YYYY-MM-DD')).getTime(); //当前时间戳
          if(current - new Date(personstart).getTime()>0){
              //当前时间晚于开始时间
              // $('#person-next').show();   
              $('#person-next').removeAttr('disabled');      
          }else {
              // $('#person-next').hide();
              $('#person-next').attr({'disabled':'disabled'});
          }
          if(current - new Date(personend).getTime()>0){
              // $('#person-next').show();
              $('#person-next').removeAttr('disabled'); 
          }else {
              // $('#person-next').hide();
              $('#person-next').attr({'disabled':'disabled'});
          }
          var oldTime = new Date(personend).getTime();
          var now=new Date(personstart).getTime();
          persontime =parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
          //获取数据
          getpersonStatisticalData(0);
      }
  });
    $('#person-date1').text(moment(personstart).format('YYYY年MM月DD日') + ' ~ ' + moment(personend).format('YYYY年MM月DD日'))

  $('body').on('click','#person-seven',function(){
    // $('#person-next').hide();
    $('#person-next').attr({'disabled':'disabled'});
    persontime = 7;
    resetPersonType();
    personstart = moment(new Date(new Date().getTime() - 1000*60*60*24*(persontime-1))).format('YYYY-MM-DD');
    personend = moment(new Date()).format('YYYY-MM-DD');
    resetpersonDateRange(personstart, personend);
    $('#person-date1').text(moment(new Date().getTime() - 1000*60*60*24*(persontime-1)).format('YYYY年MM月DD日') + ' ~ ' + moment(new Date()).format('YYYY年MM月DD日'))
      //获取数据
      getpersonStatisticalData(0);
  })
  $('body').on('click','#person-thirty',function(){
    // $('#person-next').hide();
    $('#person-next').attr({'disabled':'disabled'});
    persontime = 30;
    resetPersonType();
    personstart = moment(new Date(new Date().getTime() - 1000*60*60*24*(persontime-1))).format('YYYY-MM-DD');
    personend = moment(new Date()).format('YYYY-MM-DD');
    $('#person-date1').text(moment(new Date().getTime() - 1000*60*60*24*(persontime-1)).format('YYYY年MM月DD日') + ' ~ ' + moment(new Date()).format('YYYY年MM月DD日'))
    resetpersonDateRange(personstart, personend);
      //获取数据
      getpersonStatisticalData(0);
  })

  $('body').on('click', '#person-pre',function(){
      //计算时间间隔
      // var oldTime = new Date(personend).getTime();
      // var now=new Date(personstart).getTime();
      // persontime =parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
      var newEnd = new Date(new Date(personstart).getTime() - 1000*60*60*24);
      var newStart = new Date(new Date(personstart).getTime() - 1000*60*60*24*(persontime+1));
      var current = new Date(moment().format('YYYY-MM-DD')).getTime(); //当前时间戳
      if(current - new Date(newStart).getTime()>0){
          //当前时间晚于开始时间
          // $('#person-next').show();        
          $('#person-next').removeAttr('disabled'); 
      }
      if(current - new Date(newEnd).getTime()>0){
          // $('#person-next').show();
          $('#person-next').removeAttr('disabled'); 
      }
      personstart = moment(newStart).format('YYYY-MM-DD');
      personend = moment(newEnd).format('YYYY-MM-DD');
      resetpersonDateRange(personstart, personend);
      $('#person-date1').text(moment(personstart).format('YYYY年MM月DD日') + ' ~ ' + moment(personend).format('YYYY年MM月DD日'))
      resetPersonType();
      //获取数据
      getpersonStatisticalData(0);
          
  }); 
  $('body').on('click', '#person-next',function(){
      //计算时间间隔
      // var oldTime = new Date(personend).getTime();
      // var now=new Date(personstart).getTime();
      // persontime=parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
      var newStart = new Date(new Date(personend).getTime() + 1000*60*60*24);
      var newEnd = new Date(new Date(personend).getTime() + 1000*60*60*24*(persontime+1));
      var current = new Date(moment().format('YYYY-MM-DD')).getTime(); //当前时间戳
      if(current - new Date(newStart).getTime()>0){
          //开始时间早于当前时间
          personstart = moment(newStart).format('YYYY-MM-DD');
      }else {
          personstart = moment(new Date()).format('YYYY-MM-DD');
          // $('#person-next').hide();
          $('#person-next').attr({'disabled':'disabled'});        
      }
      if(current - new Date(newEnd).getTime()>0){
          personend = moment(newEnd).format('YYYY-MM-DD');
      }else {
          personend = moment(new Date()).format('YYYY-MM-DD');
          // $('#person-next').hide();
          $('#person-next').attr({'disabled':'disabled'});
      }
      resetpersonDateRange(personstart, personend);
      $('#person-date1').text(moment(personstart).format('YYYY年MM月DD日') + ' ~ ' + moment(personend).format('YYYY年MM月DD日'))
      resetPersonType();
      //获取数据
      getpersonStatisticalData(0);
  });

  function resetpersonDateRange(start, end) {
    $('.input_start:eq(2)').val(personstart);
    $('.input_end:eq(2)').val(personend);
      persondateRange2.calendar_endDate.setMonth(new Date(personstart).getMonth()+1, 1);
      persondateRange2.mOpts.endDate = persondateRange2.date2ymd(new Date(personend)).join('-');
      persondateRange2.show(false, persondateRange2);
      persondateRange2.init(0);
  }

  //监听搜索框键盘事件
  $('#person-search').on('keypress', function(e) {
    if (e.keyCode==13) {
      resetPersonType();
      $('.input-search').removeClass('input-heighLight');
      $(this).blur();
        personkey = $(this).val();
        if(personkey.length>0){
          $('#del-searchList').show();
        }else {
          $('#del-searchList').hide();
        }
        //获取数据
        getpersonStatisticalData(0);
        return false;
    };
  });
  $('#person-search').bind('focus','input',function () {    
    $('.input-search').addClass('input-heighLight')
    $('#del-searchList').hide();
  })
  $('#person-search').bind('blur','input',function () {   
    $('.input-search').removeClass('input-heighLight')
  })
  $('#del-searchList').on('click',function(){
    $('#del-searchList').hide();
    $('#person-search').val('');
      personkey = '';
    $('#person-searchResult').hide();
    resetPersonType();
      //获取数据
      getpersonStatisticalData(0);
  })
    //获取用户信息 userId,token
    var _userInfo = JSON.parse(localStorage.getItem('userinfo'));
    if (_userInfo == null) {
        _userInfo = {
            userId: '',
            token: ''
        };
    };

    $('body').on('shown.bs.tab','.statistical-footer li',function(e){
        if($(this).attr('name')=='人员统计'){
            getpersonStatisticalData(0);
        }
    })
    //获取人员统计数据 type：0-新加载 1-分页
    function getpersonStatisticalData(type) {
        var url = encodeURI('/getUserStatisticalData?token='+_userInfo.token+'&endTime='+personend+'&startTime='+personstart+'&orgId='+personorgId+'&queryCriteria='+personkey+'&paramType='+persontype+'&sortType=1&currentPage='+personPage+'&pageSize='+personSize);
        if(type==1){
            var url = encodeURI('/getUserStatisticalData?token='+_userInfo.token+'&endTime='+personend+'&startTime='+personstart+'&orgId='+personorgId+'&queryCriteria='+personkey+'&paramType='+persontype+'&sortType=1&currentPage='+personPage+'&pageSize='+personSize+'&customLogo='+dataType);
        }
        fetchs.get(url,function (res) {
           if(res.ifSuc==1){
               if(type==0){
                   if(res.data){
                       if(res.data.statisticalAnalysisInfos.total){
                           if(personkey!='' || personorgId != ''){
                               $('#person-searchResult').show();
                               $('#person-searchResult').text('共'+res.data.statisticalAnalysisInfos.total+'个搜索结果');
                           }else {
                               $('#person-searchResult').hide();
                           }
                           if(res.data.statisticalAnalysisInfos.total>15){
                               $('.person-table').addClass('has-page');
                               $('#page2').css('background-color','#f6f6f6');
                               $('#page2').show();
                           }else {
                               $('.person-table').removeClass('has-page');
                               $('#page2').css('background-color','#fff');
                               $('#page2').hide();;
                           }
                           p1.setCount(res.data.statisticalAnalysisInfos.total);

                       }else {
                           if(personkey!=''){
                               $('#person-searchResult').show();
                               $('#person-searchResult').text('共'+0+'个搜索结果');
                           }else {
                               $('#person-searchResult').hide();
                           }
                           p1.setCount(0);
                           $('#page2').css('background-color','#fff');
                           $('.person-table').removeClass('has-page');
                           $('#page2').hide();
                       }
                   }else {
                       if(personkey!=''){
                           $('#person-searchResult').show();
                           $('#person-searchResult').text('共'+0+'个搜索结果');
                       }else {
                           $('#person-searchResult').hide();
                       }
                       p1.setCount(0);
                       $('#page2').css('background-color','#fff');
                       $('.person-table').removeClass('has-page');
                       $('#page2').hide();
                   }
               } else {

               }
               if(res.data){
                   dataType = res.data.customLogo;
                   if(res.data.statisticalAnalysisInfos){
                       $('.person-table').html(soda(temp, {data:res.data.statisticalAnalysisInfos.list,searchkey:personkey}))
                   }else {
                       $('.person-table').html(soda(temp, {data:[],searchkey:personkey}))
                   }
               }else {
                   $('.person-table').html(soda(temp, {data:[],searchkey:personkey}))
               }
               // //会议数量
               // $('.person-meeting-num').popover({
               //     placement:"right",
               //     container:'body',
               //     trigger:'click',
               //     html:true,
               //     template: '<div class="popover popover-person-meeting" role="tooltip"><div class="popover-content"></div></div>',
               //     content:'<div class="person-meeting-table"></div>'
               // })
           }else {
               notify('danger',res.msg);
           }

        });
    }



  //会议室统计表格
  var temp = ' <table class="table table-hover"> <thead> <tr> <th class="table-1">员工姓名</th> <th class="table-2"><button class="sequenceBtn" data-type="1">会议数量（组织+参与）</button></th> <th class="table-3"><button class="sequenceBtn" data-type="0">会议时长</button></th> <th class="table-4"><button class="sequenceBtn" data-type="2">会议留言数</button></th> <th class="table-5"><button class="sequenceBtn" data-type="3">个人签到率</button></th> <th class="table-6"><button class="sequenceBtn" data-type="4">个人响应率</button></th> </tr> </thead> <tbody> <tr ng-repeat="item in data" ng-class="$index==0 ? \'all-tr\': \'\'"> <td class="table-1"> <span data-toggle="tooltip" data-title="{{item.userName}}" ng-html="item.userName|keylight:searchkey"></span> </td> <td class="table-2"><span> <a tabindex="0" class="btn btn-lg btn-danger popover-button person-meeting-num" role="button" data-toggle="popover" data-trigger="focus" data-userid="{{item.userId}}" data-content="">{{item.meetingNum}}  ({{item.organizationalMeetingNum}}+{{item.participationConferenceNum}})</a> </span></td> <td class="table-3"><span data-toggle="tooltip" data-title="{{item.meetingHours|formattime}}">{{item.meetingHours|formattime}}</span></td> <td class="table-4"><span data-toggle="tooltip" data-title="">{{item.conferenceMessageNumber}}条</span></td> <td class="table-5"><span data-toggle="tooltip" data-title="">{{item.individualAttendanceRate}}</span></td> <td class="table-6"><span data-toggle="tooltip" data-title="">{{item.individualCorrespondingRate}}</span></td> </tr> </tbody> </table> <div ng-if="data.length<1 && searchkey==\'\'" class="nothing">暂无相关内容</div> <div ng-if="data.length<1 && searchkey!=\'\'" class="nothing">搜索不到您需要的内容</div>';

  //排序
  $('body').on('click','#persons .sequenceBtn',function(){
      persontype = $(this).data('type');
      resetPersonType(1);
      getpersonStatisticalData(0);
  })

    //表格鼠标点击效果
    $('body').on('click','.person-table table tr',function () {
        $('.person-table table tr').removeClass('all-tr');
        $(this).addClass('all-tr');
    })
    //设置弹出框点击事件
    $('body').on('click','.person-meeting-num',function () {
        var arr = $('.popover');
        if(arr.length>1){
            for(var i = 0; i<arr.length-1; i++){
                $('.popover').eq(i).remove();
            }
        }
        var userId = $(this).data('userid');
        var _this = $(this);
        $('.person-meeting-num').popover('dispose');
        //获取会议数量，获取到后如果有数据就显示弹出框，无数据不显示
        fetchs.get('getConferenceNumInfo?token='+_userInfo.token+'&userId='+userId+'&startTime='+personstart+'&endTime='+personend,function (res) {
            if(res.ifSuc==1){
                if(res.data){
                    var showData = res.data;
                    if(showData.length>0){
                        // $('.popover-person-meeting').show();
                        var meeting_html = '<div class="popover-table-title"><table class="table table-hover"><thead><tr><th class="table-1">会议名称</th><th class="table-2">会议时间</th><th class="table-3">组织者</th><th class="table-4">参会人数</th></tr></thead></table></div><div class="unmeeting-table"><table class="table table-hover"><tbody>';
                        for(var i =0; i<showData.length; i++){
                            var item = showData[i];
                            var time = timeRange(item.meetingStartTime,item.meetingEndTime);
                            meeting_html += '<tr>' +
                                '<td class="table-1">' +
                                '<span data-toggle="tooltip" data-title="'+ item.meetingName +'">' + item.meetingName +
                                '</span>' +
                                '</td>' +
                                '<td class="table-2">' +
                                '<span data-toggle="tooltip" data-title="'+ time +'">' + time +
                                '</span>' +
                                '</td>' +
                                '<td class="table-3">' +
                                '<span data-toggle="tooltip" data-title="'+ '' +'">' + item.organizerName +
                                '</span>' +
                                '</td>' +
                                '<td class="table-4">' +
                                '<span data-toggle="tooltip" data-title="'+ '' +'">' + item.participantsNum +
                                '</span>' +
                                '</td>' +
                                '</tr>';
                        }
                        meeting_html += '</tbody></table></div>'
                        //会议数量
                        _this.popover({
                            placement:"right",
                            container:'body',
                            trigger:'click',
                            html:true,
                            template: '<div class="popover popover-person-meeting" role="tooltip"><div class="popover-content"></div></div>',
                            content:meeting_html
                        })
                        _this.popover('show');
                        //获取滚动条宽度
                        var width = $('.unmeeting-table')[0].offsetWidth - $('.unmeeting-table')[0].scrollWidth;
                        $('.popover-table-title').css("margin-right", width + 'px');
                        // //计算table高度是否超过最大高度
                        // var height = $('.popover-person-meeting .unmeeting-table table').height();
                        // var maxHeight = $('.unmeeting-table').height();
                        // if(height>maxHeight){
                        //     $('.popover-table-title').css("width", "calc(100% - 8px)");
                        // }
                    }else {
                    }
                }else {
                }
            }else {
            }
        });
    })
    $('body').on('click',function (event) {
        $('.popover-button').popover('dispose');
    })
    $('body').on('click','.popover',function (event) {
        event.stopPropagation();
    })
  $('body').on('shown.bs.popover','.person-meeting-num',function(){
      //修改弹出框的宽度
    //取到X轴的Transform
    var css = $('.popover-person-meeting').css('transform');
    var arr = css.split(',');
    var width = 0;
    console.log(arr);
    if(arr.length>1){
        if(css.indexOf('matrix3d')>=0){
            if(arr.length>=4){
                css = arr[arr.length-4];
            }
        }else {
            css = arr[arr.length-2];
        }
      if(css.length>0){
        width = parseFloat(css);
      }else {
        width = 0;
      }
    }else {
      width = 0;
    }
    //判断如果x轴Transform为负数，转为正数
    if(width<0){
      width = 0-width;
    }
    //加上左边的距离
    width += 80;
    $('.popover-person-meeting').css({'width':'calc(100% - '+width+'px)'});
    $('.popover-person-meeting').css({'max-width':'calc(100% - '+width+'px)'});

  })

  //重置条件
  function resetPersonType(type){
      if(type){}else {
          persontype = 0; //重置排序条件
      }
    personSize = 15;
    personPage = 1;
    p1.resetpage();
  }
  //部门单选
  $(".org-wrapper").persons({
    class:'person',
    type:3,
    max:'',
    hierarchies:false,//组织单选显示默认层级
    orgId:1,
    container:'.org-wrapper',
    template:'<ul class="list groups"><li class="" ng-repeat="item in data.sysOrganizations" data-id="{{item.sysOrganization.id}}"><p data-leaf="{{item.leaf}}"><span class="icon icon-organiz-unit"></span><span class="name" ng-html="item.sysOrganization.orgName|keylight:key"></span></p></li></ul>',
  });
  //选择部门
  $('.org-wrapper').on('hidden.bs.persons', function (e){
        console.log(e.type);
        if(e.selected.length>0){
            personorgId = e.selected[0].id;
            $('#persontype span.value').removeClass('placeholder-color');
        }else {
            personorgId = '';
            $('#persontype span.value').addClass('placeholder-color');
            $('#persontype span.value').text('选择部门');
        }
        if(personorgId==''||personorgId==null){
            personorgId = '';
        };
        getpersonStatisticalData(0);
    });
})