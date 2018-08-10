$(function(){
  //默认开始结束时间和时间范围
  var jurisdiction;//用户权限
  var time = 7;
  var start = moment(new Date(new Date().getTime() - 1000*60*60*24*(time-1))).format('YYYY-MM-DD');
  var end = moment(new Date()).format('YYYY-MM-DD');
    // $('#next').hide();
  $('#next').attr({'disabled':'disabled'});
  //tab切换
  $('ul li[data-toggle="tab"]').on('shown.bs.tab', function(e){
    $('li[data-toggle="tab"]').removeClass('active');
  });
  $('body').on('shown.bs.tab','.statistical-footer li[data-toggle="tab"]',function(){
    if(jurisdiction==0){//无权限
      $('#statistical').addClass('active');
      $('.tab-pane.import').removeClass('active');
      notify('danger','当前版本不支持这项功能，请联系企业管理员升级授权');
    }else{//有权限
      $('.statistical-tab').empty();
      $('.statistical-tab').append('<li class="nav-link" data-toggle="tab" href="#statistical" role="tab" aria-expanded="true">统计分析</li><i class="icon icon-guide"></i><li class="nav-link active" data-toggle="tab" href="'+$(this).attr('href')+'" role="tab">'+$(this).attr('name')+'</li>');
    }
  })
  $('body').on('shown.bs.tab','.statistical-tab li',function(e){
    if($(this).text()=='统计分析'){
      $('.statistical-tab').empty();
      $('.statistical-tab').append('<li class="nav-link active" data-toggle="tab" href="#statistical" role="tab" aria-expanded="true">统计分析</li>');
    }
  });
  $('.day-tab').on('click','a.btn',function(e){
    $(this).addClass('day-tab-active').siblings().removeClass('day-tab-active');
  })
  //时间插件-------------------------------------------
  var dateRange1 = new pickerDateRange('date1', {
      isTodayValid:true ,
      startDate: start,
      endDate: end,
      startToday: false,
      stopToday: true,
      needCompare: false,
      defaultText: ' ~ ',
      autoSubmit: false,
      inputTrigger: 'date',
      theme: 'ta',
      success: function (obj) {
          start = obj.startDate;
          end = obj.endDate;
          $('#date1').text(moment(start).format('YYYY年MM月DD日') + ' ~ ' + moment(end).format('YYYY年MM月DD日'))
          getDate(start,end);
          var oldTime = new Date(end).getTime();
          var now=new Date(start).getTime();
          time =parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
          var newEnd = new Date(end).getTime();
          var newStart = new Date(start).getTime();
          var current = new Date(moment().format('YYYY-MM-DD')).getTime(); //当前时间戳
          if(current - newStart>0){
              //当前时间晚于开始时间
              // $('#next').show();  
              $('#next').removeAttr('disabled');      
          }else {
            // $('#next').hide();
            $('#next').attr({'disabled':'disabled'});
          }
          if(current - newEnd>0){
              // $('#next').show();
              $('#next').removeAttr('disabled');
          }else {
            // $('#next').hide();
            $('#next').attr({'disabled':'disabled'});
          }
      }
  });
    $('#date1').text(moment(new Date(new Date().getTime() - 1000*60*60*24*(time-1))).format('YYYY年MM月DD日') + ' ~ ' + moment(new Date()).format('YYYY年MM月DD日'));

    $('body').on('click','#seven',function(){
    time = 7;
    start = moment(new Date(new Date().getTime() - 1000*60*60*24*(time-1))).format('YYYY-MM-DD');
    end = moment(new Date()).format('YYYY-MM-DD');
    // $('#next').hide();
    $('#next').attr({'disabled':'disabled'});
    resetDateRange(start, end);
    $('#date1').text(moment(new Date().getTime() - 1000*60*60*24*(time-1)).format('YYYY年MM月DD日') + ' ~ ' + moment(new Date()).format('YYYY年MM月DD日'))
    // $('.input_start').val(start);
    // $('.input_end').val(end);
    // dateRange1.calendar_endDate.setMonth(new Date(start).getMonth()+1, 1);
    // dateRange1.mOpts.endDate = dateRange1.date2ymd(new Date(end)).join('-');
    // dateRange1.show(false, dateRange1);
    // dateRange1.init(0);
  })
  $('body').on('click','#thirty',function(){
    time = 30;
    start = moment(new Date(new Date().getTime() - 1000*60*60*24*(time-1))).format('YYYY-MM-DD');
    end = moment(new Date()).format('YYYY-MM-DD');
    // $('#next').hide();
    $('#next').attr({'disabled':'disabled'});
    $('#date1').text(moment(new Date().getTime() - 1000*60*60*24*(time-1)).format('YYYY年MM月DD日') + ' ~ ' + moment(new Date()).format('YYYY年MM月DD日'))
    resetDateRange(start, end);
  })

  $('body').on('click','#pre', function(){
      //计算时间间隔
      // var oldTime = new Date(end).getTime();
      // var now=new Date(start).getTime();
      // time =parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
      var newEnd = new Date(new Date(start).getTime() - 1000*60*60*24);
      var newStart = new Date(new Date(start).getTime() - 1000*60*60*24*(time+1));
      var current = new Date(moment().format('YYYY-MM-DD')).getTime(); //当前时间戳
      if(current - new Date(newStart).getTime()>0){
          //当前时间晚于开始时间
          // $('#next').show();
         $('#next').removeAttr('disabled');      
      }
      if(current - new Date(newEnd).getTime()>0){
          // $('#next').show();
          $('#next').removeAttr('disabled');
      }
      start = moment(newStart).format('YYYY-MM-DD');
      end = moment(newEnd).format('YYYY-MM-DD');
      resetDateRange(start, end);
      $('#date1').text(moment(start).format('YYYY年MM月DD日') + ' ~ ' + moment(end).format('YYYY年MM月DD日'))
      // $('.input_start').val(start);
      // $('.input_end').val(end);
      // dateRange1.calendar_endDate.setMonth(new Date(start).getMonth()+1, 1);
      // dateRange1.mOpts.endDate = dateRange1.date2ymd(new Date(end)).join('-');
      // dateRange1.show(false, dateRange1);
      // dateRange1.init(0);
  }); 
  $('body').on('click','#next', function(){
      //计算时间间隔
      // var oldTime = new Date(end).getTime();
      // var now=new Date(start).getTime();
      // var time=parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
      var newStart = new Date(new Date(end).getTime() + 1000*60*60*24);
      var newEnd = new Date(new Date(end).getTime() + 1000*60*60*24*(time+1));
      var current = new Date(moment().format('YYYY-MM-DD')).getTime(); //当前时间戳
      if(current - new Date(newStart).getTime()>0){
          //开始时间早于当前时间
          start = moment(newStart).format('YYYY-MM-DD');
      }else {
          start = moment(new Date()).format('YYYY-MM-DD');
          // $('#next').hide();
          $('#next').attr({'disabled':'disabled'});
      }
      if(current - new Date(newEnd).getTime()>0){
          end = moment(newEnd).format('YYYY-MM-DD');
      }else {
          end = moment(new Date()).format('YYYY-MM-DD');
          // $('#next').hide();
          $('#next').attr({'disabled':'disabled'});
      }
      resetDateRange(start, end);
      $('#date1').text(moment(start).format('YYYY年MM月DD日') + ' ~ ' + moment(end).format('YYYY年MM月DD日'))
      // $('.input_start').val(start);
      // $('.input_end').val(end);
      // dateRange1.calendar_endDate.setMonth(new Date(start).getMonth()+1, 1);
      // dateRange1.mOpts.endDate = dateRange1.date2ymd(new Date(end)).join('-');
      // dateRange1.show(false, dateRange1);
      // dateRange1.init(0);
  });

  function resetDateRange(start, end) {
    $('.input_start:eq(0)').val(start);
    $('.input_end:eq(0)').val(end);
    dateRange1.calendar_endDate.setMonth(new Date(start).getMonth()+1, 1);
    dateRange1.mOpts.endDate = dateRange1.date2ymd(new Date(end)).join('-');
    dateRange1.show(false, dateRange1);
    dateRange1.init(0);
    getDate(start,end);
  }
  //echarts 数据加载-----------------------------
  var myChart = echarts.init(document.getElementById('main'));
  var date = [];//x轴的时间数据
  var meetingNumber;//会议数量
  var durationList;//会议时长
  var tool_durationList;
  var segments = 5;//y轴方向上的段数
  var option;//echarts数据结构
  getDate(start,end);//获取数据
  function getDate(start,end){
       console.log(start,end);
       fetchs.post('/meetingStatistics/getStatisticsDetails',{'staTime':start,"endTime":end},function(res){// 真实接口
          //页面数据赋值
          if(res.ifSuc==1){
            //顶部
             $('.meetting-type .media:nth-child(1) .media-body p.mb-0').text(res.data.meetingNum);//会议总数量
             $('.meetting-type .media:nth-child(2) .media-body p.mb-0').text(duration(res.data.meetingTotalTime));//会议总时长res.data.meetingTotalTime
             $('.meetting-type .media:nth-child(3) .media-body p.mb-0').text(res.data.signRate);// 签到率
             $('.meetting-type .media:nth-child(4) .media-body p.mb-0').text(res.data.replyRate);//响应率
              //底部
             $('.statistical-footer .nav-link:nth-child(3) .link-left .link-number').text(res.data.orgTotalNum);// 部门数量
             $('.statistical-footer .nav-link:nth-child(3) .link-right .link-number').text(res.data.crossDepartmentMeeting);// 跨部门会议
              $('.statistical-footer .nav-link:nth-child(2) .link-left .link-number').text(res.data.userTotalNum+"人");// 员工人数
              $('.statistical-footer .nav-link:nth-child(2) .link-right .link-number').text(res.data.numberOfAbsentParticipants+"人");// 未参会人数
              $('.statistical-footer .nav-link:nth-child(1) .link-left .link-number').text(res.data.roomTota);// 会议室数量
              $('.statistical-footer .nav-link:nth-child(1) .link-right .link-number').text(res.data.roomUseRate);//会议室使用率
              jurisdiction = res.data.jurisdiction;
              meetingNumber='';//会议数量
              durationList = [];//会议时长
              option = '';
              date=[];//x轴的时间数据
              meetingNumber = res.data.meettingNumber;
              //时长的数数组的长度和段数比较
             // durationList = res.data.meettingDuration;
              tool_durationList = res.data.meettingDuration;
              for (var i = 0; i <res.data.meettingTime.length; i++) {
                  var hour = Number(duration(res.data.meettingDuration[i]).split('小时')[0]);
                  var minute = Number(duration(res.data.meettingDuration[i]).split('小时')[1].split('分钟')[0]);
                  var current_duration = Number(hour)+Number(minute/60);
                  durationList.push(Number(current_duration));
                  date.push(moment(Number(res.data.meettingTime[i])).format('YYYY年MM月DD日'));
              };
              if(durationList.length<segments){
                  segments = durationList.length;
              };
              chartShow(durationList,meetingNumber,date,tool_durationList);
          }
      })
  }
    function chartShow(durationList,meetingNumber,date,tool_durationList) {
        var interval_a =Math.ceil(Math.max.apply(null, meetingNumber)/segments);
        var  max_a = Math.ceil(Math.max.apply(null, meetingNumber)/segments)*segments;
        if(max_a<=5){
          interval_a=1;
          max_a = 5;
        };
        var interval_b =Math.ceil(Math.max.apply(null, durationList)/segments);//间隔
        var  max_b = Math.ceil(Math.max.apply(null, durationList)/segments)*segments;//最大值
        if(max_b<=5){
            interval_b=1;
            max_b = 5;
        };
        option = {
            tooltip: {
                trigger: 'axis',
               /* position: function (pt) {
                    return  [pt[0] - 100, pt[1] - 100];
                },*/
                backgroundColor :'#fff',
                borderColor :'#6694ff',
                borderWidth:'1',
                textStyle :{
                    color:'#333',
                    fontFamily: 'Microsoft Yahei',
                },
                padding: [8, 12],
                formatter: function(params) {
                    var res ='<span class="tool-title">'+ params[0].name+'</span><br/>';
                    res+='<span class="tool-list"><i style="width: 8px;transform: scale(0.5);margin-right: 4px;">'+params[0].marker+'</i>'+params[0].seriesName+' : '+params[0].value+'</span></br>';
                    res +='<span class="tool-list"><i style="width: 8px;transform: scale(0.5);margin-right: 4px;">'+params[1].marker+'</i>'+params[1].seriesName+' : '+duration(tool_durationList[params[0].dataIndex])+'</span>';
                    return res;
                }
            },
            grid: {
                left: '1%',
                right: '1%',
                bottom: '6%',
                containLabel: true,
            },
            legend: {
                left: '0%',
                color:'#333',
                itemGap:48,
                itemWidth:16,
                itemHeight:-16,
                data:[{
                    name:'会议数量',
                    textStyle:{
                        fontSize:14,
                        color:'#333',
                        fontFamily: 'Microsoft YaHei',
                    },
                    icon:'pin',//格式为'image://+icon文件地址'，其中image::后的//不能省略image://./images/icon1.png
                    iconStyle:{
                        type:'solid',
                        borderColor:'#fff',
                        borderWidth :5,
                        borderType :'solid',
                    }
                },
                    {
                        name:'会议时长',
                        textStyle:{
                            fontSize:14,
                            color:'#333',
                            fontFamily: 'Microsoft YaHei',
                            icon:'icon icon-kzt-mroom'//格式为'image://+icon文件地址'，其中image::后的//不能省略
                        },
                        icon:'pin',
                    }],
                     selectedMode:false,
                    /* formatter: function (name,i) {
                         console.log(name);
                         var len='';
                         if(name=="会议数量"){
                             len = '<i class="icon icon-kzt-mroom"></i>';
                         }else{
                             len = '<i class="icon icon-kzt-mroom"></i>';
                         };
                         return len+ name;
                     }*/
            },
            title: {
                show:false,
                left: 'center',
                text: '大数据量面积图',
            },
            toolbox: {
                show:false,
                feature: {
                    dataZoom: {
                        yAxisIndex: 'none'
                    },
                    restore: {},
                    saveAsImage: {}
                }
            },
            axisPointer:{
                z:1,
                type:'line',
                lineStyle:{
                    color:'#6694FF'
                },
            },
            xAxis: {
                axisLine: {
                    lineStyle: {
                        color: '#e6e6e6'
                    },
                },
                axisLabel:{
                 //rotate:-30,
                    color:'#757575',
                    fontSize:12,
                    fontFamily: 'Microsoft YaHei',
                    formatter: function(value, index){
                        return value.substring(value.indexOf('年')+1);
                    }
                },
                nameTextStyle:{
                    fontSize:12,
                    color:'#757575',
                    fontFamily: 'Microsoft YaHei',
                },
                // type: 'category',
                boundaryGap: false,
                data: date,
                axisTick :{
                    show:true,
                    interval:0,
                    length: 6,
                    /*inside:true,*/
                    secondTick: {
                        show: false,//是否显示二级刻度
                        interval: null,//二级刻度间间隔大小
                        length: 3,//二级刻度线的长度
                        // lineStyle：{ //刻度线的线条样式
                        // }
                    }
                },
            },
            yAxis:
                [{
                    name: '会议数量',
                    type: 'value',
                    interval:interval_a,
                    max: max_a,
                    axisLabel: {
                        fontSize:12,
                        color:'#757575',
                        fontFamily: 'Microsoft YaHei',
                    },
                    nameTextStyle:{
                        fontSize:12,
                        color:'#757575',
                        fontFamily: 'Microsoft YaHei',
                    },
                    axisLine: {
                        show:false,
                        lineStyle: {
                            color: '#757575'
                        }
                    },
                    splitLine: {
                        show: true,
                        lineStyle: {
                            color: '#e6e6e6',
                            width: 1
                        }
                    },
                    axisPointer:{
                        show:false
                    },
                    axisTick:{
                        show:false
                    },
                    boundaryGap: false//[0, '100%']
                },
                    {
                        name: '会议时长(h)',
                        // nameLocation: 'end',
                        interval:interval_b,
                        max: max_b,
                        min:0,
                        type: 'value',
                        axisLabel: {
                            fontSize:12,
                            color:'#757575',
                            formatter: '{value} h'
                        },
                        // inverse: true,
                        axisLine: {
                            show:false,
                            lineStyle: {
                                color: '#757575'
                            }
                        },
                        axisPointer:{
                            show:false
                        },
                        axisTick:{
                            show:false,
                        },
                        splitLine: {
                            show: true,
                            lineStyle: {
                                color: '#e6e6e6',
                                width: 1
                            }
                        },
                        boundaryGap: [0, '100%'],
                        scale:'h'
                    }],
            series: [
                {
                    name:'会议数量',
                    type:'line',
                    smooth:true,
                    symbol: 'circle',
                    symbolSize:6,
                    sampling: 'average',
                    itemStyle: {
                        normal: {
                            color: '#27a1f2',
                        },
                        emphasis: {
                            type:'solid',
                            borderColor:'#fff',
                            borderWidth :2,
                            borderType :'solid',
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowOffsetY: 0,
                            shadowColor: '#41adfa'
                        }
                    },
                    markLine:{
                        lineStyle:{
                            normal:{
                                color:'red'
                            }
                        }
                    },
                    data: meetingNumber
                },
                {
                    name:'会议时长',
                    type:'line',
                    yAxisIndex:1,
                    smooth:true,
                    symbol: 'circle',
                    symbolSize:6,
                    sampling: 'average',
                    itemStyle: {
                        normal: {
                            color: '#ff7a5c',
                        },
                        emphasis: {
                            type:'solid',
                            borderColor:'#fff',
                            borderWidth :2,
                            borderType :'solid',
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowOffsetY: 0,
                            shadowColor: '#ff7a5c'
                        }
                    },
                    data: durationList
                }
            ]
        };
        myChart.setOption(option);
    }
})