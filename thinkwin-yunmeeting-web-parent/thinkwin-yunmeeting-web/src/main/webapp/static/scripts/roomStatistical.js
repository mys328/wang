$(function() {
  // body...
  //-------------------------------------
  //分页
  // let  p=RoomPages();
  // p.callBack(callBack);
  // p.setCount(20);
  var p= Pages("#page1");//创建分页器
  var roomPage = 1;
  var pageSize = 15;
  var total = 0;
  p.callBack=function callBack(currentPage,page){

      roomPage = currentPage;
    pageSize = page;
    getDate(initList,2);
  } ;
  // p.setCount(50); //设置分页器请求的总数
  //会议室统计
  //默认开始结束时间和时间范围
  var key = ''; //搜索关键字
  var roomtime = 7;
  var roomstart = moment(new Date(new Date().getTime() - 1000*60*60*24*(roomtime-1))).format('YYYY-MM-DD');
  var roomend = moment(new Date()).format('YYYY-MM-DD');
  var selectAear = ''; //搜索区域
  var needReview = ''; //需审核
  var sequence_type = '';  //排序条件
  var selectList;//区域筛选
    //-----环形图柱状图数据
    var dataType;//数据类型 0 刷选和搜索触发 1 表格排序  2 分页操作
    var initList;
    var myChart,myCharts;
    var dataList=[];
    var colorList=[];
    var xAxisData,durationList,percentList;
    var option,options;
    //---表格数据
    var table_data;
  $('#room-next').attr({'disabled':'disabled'});
  var roomdateRange2 = new pickerDateRange('room-date1', {
      isTodayValid:true ,
      startDate: roomstart,
      endDate: roomend,
      startToday: false,
      stopToday: true,
      needCompare: false,
      defaultText: ' ~ ',
      autoSubmit: false,
      inputTrigger: 'date',
      theme: 'ta',
      success: function (obj) {
          roomstart = obj.startDate;
          roomend = obj.endDate;
          $('#room-date1').text(moment(roomstart).format('YYYY年MM月DD日') + ' ~ ' + moment(roomend).format('YYYY年MM月DD日'))
          resetType();
          var oldTime = new Date(roomend).getTime();
          var now=new Date(roomstart).getTime();
          roomtime =parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
          var current = new Date(moment().format('YYYY-MM-DD')).getTime(); //当前时间戳
          if(current - new Date(roomstart).getTime()>0){
              //当前时间晚于开始时间
              $('#room-next').removeAttr('disabled');
          }else {
              $('#room-next').attr({'disabled':'disabled'});
          }
          if(current - new Date(roomend).getTime()>0){
              $('#room-next').removeAttr('disabled');
          }else {
              $('#room-next').attr({'disabled':'disabled'});
          }
          var oldTime = new Date(roomend).getTime();
          var now=new Date(roomstart).getTime();
          roomtime =parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
          getDate(initList,0);
      }
  });

  $('body').on('click','#room-seven',function(){
    $('#room-next').attr({'disabled':'disabled'});
    roomtime = 7;
    resetType();
    roomstart = moment(new Date(new Date().getTime() - 1000*60*60*24*(roomtime-1))).format('YYYY-MM-DD');
    roomend = moment(new Date()).format('YYYY-MM-DD');
    resetRoomDateRange(roomstart, roomend);
    $('#room-date1').text(moment(new Date().getTime() - 1000*60*60*24*(roomtime-1)).format('YYYY年MM月DD日') + ' ~ ' + moment(new Date()).format('YYYY年MM月DD日'))
    getDate(initList,0);
      // $('.input_start').val(start);
    // $('.input_end').val(end);
    // dateRange1.calendar_endDate.setMonth(new Date(start).getMonth()+1, 1);
    // dateRange1.mOpts.endDate = dateRange1.date2ymd(new Date(end)).join('-');
    // dateRange1.show(false, dateRange1);
    // dateRange1.init(0);
  })
  $('body').on('click','#room-thirty',function(){
    $('#room-next').attr({'disabled':'disabled'});
    roomtime = 30;
    resetType();
    roomstart = moment(new Date(new Date().getTime() - 1000*60*60*24*(roomtime-1))).format('YYYY-MM-DD');
    roomend = moment(new Date()).format('YYYY-MM-DD');
    $('#room-date1').text(moment(new Date().getTime() - 1000*60*60*24*(roomtime-1)).format('YYYY年M月D日') + ' ~ ' + moment(new Date()).format('YYYY年M月D日'))
    resetRoomDateRange(roomstart, roomend);
      getDate(initList,0);
  })

  $('body').on('click', '#room-pre',function(){
      //计算时间间隔
      // var oldTime = new Date(roomend).getTime();
      // var now=new Date(roomstart).getTime();
      // roomtime =parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
      var newEnd = new Date(new Date(roomstart).getTime() - 1000*60*60*24);
      var newStart = new Date(new Date(roomstart).getTime() - 1000*60*60*24*(roomtime+1));
      var current = new Date(moment().format('YYYY-MM-DD')).getTime(); //当前时间戳
      if(current - new Date(newStart).getTime()>0){
          //当前时间晚于开始时间
          $('#room-next').removeAttr('disabled');
      }
      if(current - new Date(newEnd).getTime()>0){
          $('#room-next').removeAttr('disabled');
      }
      roomstart = moment(newStart).format('YYYY-MM-DD');
      roomend = moment(newEnd).format('YYYY-MM-DD');
      resetRoomDateRange(roomstart, roomend);
      $('#room-date1').text(moment(roomstart).format('YYYY年MM月DD日') + ' ~ ' + moment(roomend).format('YYYY年MM月DD日'))
      resetType();
      getDate(initList,0);
      // $('.input_start').val(start);
      // $('.input_end').val(end);
      // dateRange1.calendar_endDate.setMonth(new Date(start).getMonth()+1, 1);
      // dateRange1.mOpts.endDate = dateRange1.date2ymd(new Date(end)).join('-');
      // dateRange1.show(false, dateRange1);
      // dateRange1.init(0);

  });
    $('#room-date1').text(moment(roomstart).format('YYYY年MM月DD日') + ' ~ ' + moment(roomend).format('YYYY年MM月DD日'))

    $('body').on('click', '#room-next',function(){
      //计算时间间隔
      // var oldTime = new Date(roomend).getTime();
      // var now=new Date(roomstart).getTime();
      // roomtime=parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
      var newStart = new Date(new Date(roomend).getTime() + 1000*60*60*24);
      var newEnd = new Date(new Date(roomend).getTime() + 1000*60*60*24*(roomtime+1));
      var current = new Date(moment().format('YYYY-MM-DD')).getTime(); //当前时间戳
      if(current - new Date(newStart).getTime()>0){
          //开始时间早于当前时间
          roomstart = moment(newStart).format('YYYY-MM-DD');
      }else {
          roomstart = moment(new Date()).format('YYYY-MM-DD');
          $('#room-next').attr({'disabled':'disabled'});
      }
      if(current - new Date(newEnd).getTime()>0){
          roomend = moment(newEnd).format('YYYY-MM-DD');
      }else {
          roomend = moment(new Date()).format('YYYY-MM-DD');
          $('#room-next').attr({'disabled':'disabled'});
      }
      resetRoomDateRange(roomstart, roomend);
      $('#room-date1').text(moment(roomstart).format('YYYY年MM月DD日') + ' ~ ' + moment(roomend).format('YYYY年MM月DD日'))
      resetType();
      getDate(initList,0);
      // $('.input_start').val(start);
      // $('.input_end').val(end);
      // dateRange1.calendar_endDate.setMonth(new Date(start).getMonth()+1, 1);
      // dateRange1.mOpts.endDate = dateRange1.date2ymd(new Date(end)).join('-');
      // dateRange1.show(false, dateRange1);
      // dateRange1.init(0);
  });

  function resetRoomDateRange(start, end) {
    $('.input_start:eq(1)').val(roomstart);
    $('.input_end:eq(1)').val(roomend);
      roomdateRange2.calendar_endDate.setMonth(new Date(roomstart).getMonth()+1, 1);
      roomdateRange2.mOpts.endDate = roomdateRange2.date2ymd(new Date(roomend)).join('-');
      roomdateRange2.show(false, roomdateRange2);
      roomdateRange2.init(0);
  }
  //区域筛选
    $('body').on('click', '#roomDropdownMenu span', function () {
        $(this).addClass('select-active').siblings().removeClass('select-active');
        var type_val = $(this).text();
        var areaId= $(this).data('id');
        $('#area').text(type_val);
        $('#area.form-control').css({'color':'#333'});
        selectAear = areaId;
        if($(this).data('id')==0){
            $('#area.form-control').css({'color':'#d1d1d1'});
            selectAear = '';
        };
        getDate(initList,0);
    })
  //监听搜索框键盘事件
  $('#room-search').on('keypress', function(e) {
    if (e.keyCode==13) {
        resetType();
        $('.input-search').removeClass('input-heighLight');
        $(this).blur();
        key = $(this).val();
        if(key.length>0){
          $('#del-searchList').show();
          // $('#room-searchResult').show();
        }else {
          $('#del-searchList').hide();
          // $('#room-searchResult').hide();
        };
        getDate(initList,0);
        return false;
    };
  });
  $('#room-search').bind('focus','input',function () {
    $('.input-search').addClass('input-heighLight')
    $('#del-searchList').hide();
  })
  $('#room-search').bind('blur','input',function () {
    $('.input-search').removeClass('input-heighLight')
  })
  $('#del-searchList').on('click',function(){
    $('#del-searchList').hide();
    $('#room-search').val('');
    key = '';
    // $('#room-searchResult').hide();
    resetType();
    getDate(initList,0);
  })
  //监听需审核会议室事件
  $('.custom-control-input').on('change',function(){
    resetType();
    if($(".custom-control-input").is(':checked')==true){
      console.log('select');
      needReview = '1';
    }else {
      console.log('unselect');
      needReview = '';
    }
    getDate(initList,0);
  })
//环形图----------------------------------
    function pieNoData() {
      var dataList = [{name:'无数据',value:'1'}];
        myChart.dispose();
        myChart = echarts.init(document.getElementById('roomPieChart'));
        var optionNo = {
            tooltip: false,
            series: [
                {
                    name:'访问来源',
                    type:'pie',
                    radius: ['60.5%', '83%'],
                    silent:true,
                    label: {
                        normal: {
                            show: true,
                            position: 'center',
                            textStyle: {
                                fontSize: '16',
                                fontWeight: '100',
                                color: '#333',
                                fontFamily: 'Helvetica Neue, PingFang SC, Microsoft Yahei, "微软雅黑", STXihei, sans-serif;',
                            },
                            formatter: '{b}'
                        },
                    },
                    labelLine: {
                        normal: {
                            show: true
                        }
                    },
                    data:dataList
                }
            ],
            color:['#eee']
        };
        myChart.setOption(optionNo);
    }
function chartLeft(dataList,colorList) {
    myChart.dispose();
    myChart = echarts.init(document.getElementById('roomPieChart'));
    option = {
        tooltip: {
            trigger: 'item',
            position: function (pt) {
                return  [pt[0] + 20, pt[1] + 20];
            },
            backgroundColor :'#fff',
            borderColor :'#6694ff',
            borderWidth:'1',
            textStyle :{
                color:'#333',
                fontFamily: 'Helvetica Neue, PingFang SC, Microsoft Yahei, "微软雅黑", STXihei, sans-serif;',
            },
            padding: [8, 12],
            //formatter: "{b} <br/>数量占比：{d}%<br/>会议数量：{c}"
            formatter:function(item){
                return  '<span class="tool-title">'+item.name+'</span><br/><span class="tool-list">数量占比：'+item.percent.toFixed(1)+'%</span><br/><span class="tool-list">会议数量：'+item.value+'</span>'
            }
        },
        series: [
            {
                name:'访问来源',
                type:'pie',
                center:['50%','50%'],
                radius: ['60.5%', '83%'],
                avoidLabelOverlap: false,
                hoverAnimation:true,
                hoverOffset:4,
                label: {
                    normal: {
                        show: false,
                        position: 'center',
                        //formatter: '{b}\n\n{d}%'
                    },
                    emphasis: {
                        show: true,
                        textStyle: {
                            color:'#676b73',
                            fontSize: '16',
                            fontWeight: '100',
                            fontFamily: 'Microsoft YaHei',
                        },
                        formatter: function(params){
                            var name = params.name==null? '': params.name;
                            if(params.name.length>5){
                                name = params.name.substring(0,5)+'...';
                            };
                            return '{a|'+name+'}\n\n{b|'+ params.percent.toFixed(1) +'%}';
                        },
                        /*formatter: [
                            '{a|这段文本采用样式a}',
                            '{b|这段文本采用样式b}这段用默认样式{x|这段用样式x}'
                        ].join('\n\n'),*/
                        rich: {
                            a: {
                                fontSize: 14,
                                fontFamily: 'Microsoft YaHei',
                            },
                            b: {
                                fontSize: 24,
                                fontFamily: 'Microsoft YaHei',
                            },
                        }
                    }
                },
                labelLine: {
                    normal: {
                        show: true
                    }
                },
                data:dataList
            }
        ],
        color:colorList
    };
    myChart.setOption(option);
    // setTimeout(function () {
        myChart.dispatchAction({
            type: 'highlight',
            name: dataList[0].name
        });
    // });
    myChart.on('mouseover', function (params) {
        if(params.name!=dataList[0].name){
            myChart.dispatchAction({
                type: 'downplay',
                name: dataList[0].name
            })
        }
    });
    myChart.on('mouseout', function (params) {
        myChart.dispatchAction({
            type: 'highlight',
            name: dataList[0].name,
        })
    });
}
//柱状图-----------------------------------
    function barNoData() {
        var optionNo = {
            color: ['#3398DB'],
            tooltip : false,
            grid: {
                left: '3%',
                right: '4%',
                bottom: '15%',
                top:'10%',
                containLabel: true
            },
            xAxis : [
                {
                    type : 'category',
                    axisLine: {
                        show:false,
                        lineStyle: {
                            color: '#757575'
                        },
                    },
                    axisLabel: {
                        fontSize:12,
                        color:'#999',
                        fontFamily: 'Microsoft YaHei',
                    },
                    nameTextStyle:{
                        fontSize:12,
                        color:'#757575',
                        fontFamily: 'Microsoft YaHei',
                    },
                    axisTick: {
                        alignWithLabel: true
                    },
                    /* axisLabel:{
                     rotate:0,
                     },*/
                    data : []
                }
            ],
            yAxis : [
                {
                    type : 'category',
                    interval:20,
                    boundaryGap:false,
                    axisLine: {
                        show:false,
                        lineStyle: {
                            color: '#757575'
                        }
                    },
                    axisLabel: {
                        fontSize:12,
                        color:'#999',
                        fontFamily: 'Microsoft YaHei',
                    },
                    nameTextStyle:{
                        fontSize:12,
                        color:'#757575',
                        fontFamily: 'Microsoft YaHei',
                    },
                    axisPointer:{
                        show:false
                    },
                    axisTick:{
                        show:false
                    },
                    splitLine: {
                        show: true,
                        lineStyle: {
                            color: '#e6e6e6',
                            width: 1
                        }
                    },
                    data:['0%','25%','50%','75%','100%'],
                }
            ],
            series : [
                {
                    name:'百分比',
                    type:'bar',
                    max:100,
                    barWidth: '50%',
                    data:[]
                }
            ]
        };
        var echarts_width= $('.room-statistical-bar').width()+'px';
        $('#roomBarChart').css({'width':echarts_width});
        myCharts.setOption(optionNo);
    }
function chartRight() {
    options = {
        color: ['#3398DB'],
        tooltip : {
            backgroundColor :'#fff',
            borderColor :'#6694ff',
            borderWidth:'1',
            textStyle :{
                color:'#333',
                fontFamily: 'Helvetica Neue, PingFang SC, Microsoft Yahei, "微软雅黑", STXihei, sans-serif;',
            },
            padding: [8, 12],
            formatter:function(params){
                var time = duration(Number(durationList[params.dataIndex]));
                return '<span class="tool-title">'+params.name+'</span><br/><span class="tool-list">使用率：'+Number(params.value).toFixed(1)+'%</span><br/><span class="tool-list">使用时长：'+time+'</span>'
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '20%',
            top:'10%',
            containLabel: true
        },
        xAxis : [
            {
                type : 'category',
                axisLine: {
                    lineStyle: {
                        color: '#e6e6e6'
                    },
                },
                axisLabel: {
                    fontSize:12,
                    color:'#999',
                    fontFamily: 'Microsoft YaHei',
                },
                axisLabel:{
                    rotate:-45,
                    interval:0,
                    color:'#757575',
                    fontFamily: 'Microsoft YaHei',
                    formatter:function (params) {
                        console.log(params);
                        var name = params==null? '': params;
                        if(params.length>5){
                            name = params.substring(0,5)+'...';
                        };
                       return name
                    }
                },
                nameTextStyle:{
                    fontSize:12,
                    color:'#757575',
                    fontFamily: 'Microsoft YaHei',
                },
                axisTick: {
                    alignWithLabel: true
                },
                data : xAxisData
            }
        ],
        yAxis : [
            {
                type : 'value',
                max:100,
                axisLabel: {
                    fontSize:12,
                    color:'#999',
                    fontFamily: 'Microsoft YaHei',
                    formatter: '{value} %'
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
                axisPointer:{
                    show:false
                },
                axisTick:{
                    show:false
                },
                splitLine: {
                    show: true,
                    lineStyle: {
                        color: '#e6e6e6',
                        width: 1
                    }
                },
            }
        ],
        series : [
            {
                name:'百分比',
                type:'bar',
                max:100,
                barWidth: '50%',
                itemStyle: {
                    normal: {
                        color: function(params) {
                            return colorList[params.dataIndex]
                        },
                        label: {
                            show: true,
                            position: 'top',
                            //formatter: '{c} %'
                            formatter:function(item){
                                return Number(item.value).toFixed(1)+'%'
                            }
                        }
                    }
                },
                data:percentList
            }
        ]
    };
    var echarts_width= $('.room-statistical-bar').width()+'px';
    $('#roomBarChart').css({'width':echarts_width});
    myCharts.setOption(options);
}
  $('body').on('shown.bs.tab','.statistical-footer li',function(e){
    if($(this).attr('name')=='会议室统计'){
        myChart = echarts.init(document.getElementById('roomPieChart'));
        myCharts = echarts.init(document.getElementById('roomBarChart'));
        //滚动悬浮效果
        var ie6 = document.all;
        $('.main-panel-box').scroll(function(){
              var st = $(this).scrollTop();//document.body.scrollTop || document.documentElement.scrollTop
              console.log(st);
              //console.log($('#roomTable').offset().top);//parseInt($('#roomTable').offset().top)
              if(st>=276){
                $('.hide-thead').show();
                if(total>15){
                    $('.statical-table').addClass('room-hasPage');
                    $('#page1').css('background-color','#f6f6f6');
                    $('#page1').show();
                }
              }else {
                $('.hide-thead').hide();
                $('#page1').css('background-color','#fff');
                $('#page1').hide();
                $('.statical-table').removeClass('room-hasPage');
              };
          });
        getDate(initList,0,4);
    }
  })
    var selectHtml= ' <span class="span-list" ng-repeat = "item in data" data-id={{item.id}} data-state={{item.deleteState}}>{{item.name}}</span> ';
    //获取数据
    function getDate(initList,dataType,selectshow){
        initList  = {//初始化参数
            'staTime':roomstart,
            'endTime':roomend,
            'areaId':selectAear,
            'type': sequence_type,
            'searchKey':key,
            'isAudit':needReview,
            //分页参数
            'currentPage':roomPage,
            'pageSize':pageSize,
        };
        console.log(initList);
        fetchs.post('/meetingStatistics/getMeetingRoomStatisticsDetails',initList,function (result) {
            console.log(result);
            if(result.ifSuc==1){
                result.data.area.unshift({createrId:'',name:'全部区域',id:'0'});
                selectList = result.data.area;
                table_data = result.data.statistics;
                if(dataType==0){
                    if(key == '' && needReview == '' && selectAear == ''){
                        //无搜索
                        $('#room-searchResult').hide();
                    }else {
                        $('#room-searchResult').show();
                        if(result.data && result.data.total){
                            $('#room-searchResult').text('共'+result.data.total+'个结果');
                        }else {
                            $('#room-searchResult').text('共0个结果');
                        }

                    }
                    if(selectshow==4){
                        $('body #roomDropdownMenu').html(soda(selectHtml,{data:selectList}));
                        $('body').find('.span-list:nth-child(1)').addClass('select-active');
                    }
                    dataList = [];
                    xAxisData= [];
                    durationList =[];
                    percentList = [];
                    var dataListValue=[];
                    var meeting_total_num=0;//其他的会议量
                    var meeting_chart_num =0;//会议室前十会议数量
                    var duration_chart_num = 0;
                    var percent_chart_num = 0;
                    if(result.data.dataList.length<=10){
                        dataList = result.data.dataList;
                        xAxisData = result.data.xAxisData;
                        durationList = result.data.durationList;
                        percentList = result.data.percentList;
                    }else{
                        for(var i = 0;i<=result.data.dataList.length-1;i++){
                                if(i<10){
                                    meeting_chart_num += Number(result.data.dataList[i].value);
                                    duration_chart_num += Number(result.data.durationList[i]);
                                    percent_chart_num += Number(result.data.percentList[i]);
                                    dataList.push(result.data.dataList[i]);
                                    dataListValue.push(result.data.dataList[i].value);
                                    xAxisData.push(result.data.xAxisData[i]);
                                    durationList.push(result.data.durationList[i]);
                                    percentList.push(Number(result.data.percentList[i].split('%')[0]));
                                };
                        };
                       /* dataList[10]=Object({
                            'itemStyle':'',
                            'name': '其他',
                            'value':result.data.statistics[0].meetingTotalNum-meeting_chart_num
                        });*/
                        var pie_other=result.data.statistics[0].meetingTotalNum-Number(meeting_chart_num);
                        //--环形图数据排序
                        for(var i=0;i<dataListValue.length-1;i++){
                            if(Number(dataListValue[i])<=pie_other&pie_other!=0){
                                console.log(i);
                                dataList[10]='';
                                dataList.splice(i,0,{
                                    'itemStyle':'',
                                    'name': '其他',
                                    'value':pie_other//会议时长
                                });
                                dataList.pop();
                                break;
                            }else{
                                dataList[10]=Object({
                                    'itemStyle':'',
                                    'name': '其他',
                                    'value':pie_other//会议时长
                                });
                            }
                        };
                        console.log(dataList);
                       /* durationList[10]=Number(result.data.statistics[0].meetingUseTime)-duration_chart_num;
                        var other_duration = durationList[10];
                        percentList[10]=durationList[10]/(result.data.statistics[0].meetingFreeTime+result.data.statistics[0].meetingUseTime)*100;
                        //当长度大于10时重新排序--柱状图
                        if(other_duration!=0){
                            durationList = durationList.sort(function(a,b){return b-a});
                            percentList = percentList.sort(function(a,b){return b-a});
                        }else{
                            xAxisData[10]='其他';
                        };
                        for(var i=0;i<durationList.length-1;i++){
                            if(durationList[i]<=other_duration&other_duration!=0){
                                //console.log(i);‘其他’在重新排序的数组所在的位置
                                xAxisData.splice(i, 0, '其他');
                                break;
                            }
                        };*/
                        console.log(durationList);
                        console.log(percentList);
                        console.log(xAxisData)
                    };
                    console.log(dataList);
                    colorList = [];//背景色的数据
                    var sum = 0;
                    for(var j = 0; j<dataList.length;j++){
                        sum += Number(dataList[j].value);
                    }

                    for(var i = 0;i<=dataList.length-1;i++){
                        if(dataList.length>1 && Number(dataList[0].value) != sum){
                            dataList[i].itemStyle =Object({normal:{borderWidth:2,borderColor:"#fff"}});
                        }else {
                            dataList[i].itemStyle =Object({normal:{borderWidth:0,borderColor:"#fff"}});
                        }
                        if(i % 2==0){//偶数
                            colorList.push('#38d981');
                        }else{//奇数
                            colorList.push('#6694ff');
                        };
                    };
                    if(result.data.dataList.length>0){
                        // $('#roomBarChart').show();
                        // $('#roomPieChart').show();
                        chartLeft(dataList,colorList);
                        chartRight(xAxisData,durationList,percentList);
                        // $('.no-chart').hide();
                    }else {
                        // $('#roomBarChart').hide();
                        pieNoData();
                        barNoData();
                        // $('#roomPieChart').hide();
                        // $('.no-chart').show();
                    }
                    if(result.data.total){
                        p.setCount(result.data.total);//初始化分页
                        total = result.data.total;
                    }else {
                        p.setCount(0);
                        total = 0;
                        $('#page1').css('background-color','#fff');
                    }

                }else if(dataType==1){
                    if(result.data.total){
                        p.setCount(result.data.total);//初始化分页
                        total = result.data.total;
                    }else {
                        p.setCount(0);
                        total = 0;
                        $('#page1').css('background-color','#fff');
                    }
                }else if(dataType==2){
                    //分页的操作

                }
                table_data = result.data.statistics;
                if(table_data==null){
                    table_data = [];
                }
                $('.room-table').html(soda(temp, {data:table_data,searchkey:key,page:roomPage}));
                // $('.meeting-num').popover({
                //     placement:"top",
                //     container:'body',
                //     trigger:'click',
                //     html:true,
                //     template: '<div class="popover popover-meeting" role="tooltip"><div class="popover-content"></div></div>',
                //     content:'<div class="show-table"></div>'
                // })
                // //审核未通过会议数量
                // $('.unpass-num').popover({
                //     placement:"left",
                //     container:'body',
                //     trigger:'click',
                //     html:true,
                //     template: '<div class="popover popover-unpass" role="tooltip"><div class="popover-content"></div></div>',
                //     content:'<div class="show-unpass"></div>'
                // })
            }
        })
    }
  //会议室统计表格
  var temp = ' <table class="table table-hover"> <thead> <tr> <th class="table-1">会议室名称</th> <th class="table-2"><button class="sequenceBtn" data-type="1">会议数量</button></th> <th class="table-3"><button class="sequenceBtn" data-type="2">使用时间</button></th> <th class="table-4"><button class="sequenceBtn" data-type="3">闲置时长</button></th> <th class="table-5"><button class="sequenceBtn" data-type="4">使用率</button></th> <th class="table-6"><button class="sequenceBtn" data-type="5">审核未通过会议数量</button></th> </tr> </thead> <tbody> <tr ng-repeat="item in data" data-room-id={{item.roomId}} ng-class="$index==0? \'all-tr\' : \'\'"> <td class="table-1"><span data-toggle="tooltip" data-title="{{item.name}}" ng-html="item.name|keylight:searchkey"></span></td> <td class="table-2"> <a tabindex="0" class="btn btn-lg popover-button meeting-num" role="button" data-toggle="popover" data-trigger="focus" title="" data-content="">{{item.meetingTotalNum}}</a> </td> <td class="table-3"><span data-toggle="tooltip" data-title="{{item.meetingUseTime|formattime}}">{{item.meetingUseTime|formattime}}</span></td> <td class="table-4"><span data-toggle="tooltip" data-title="{{item.meetingFreeTime|formattime}}">{{item.meetingFreeTime|formattime}}</span></td> <td class="table-5"><span data-toggle="tooltip" data-title="">{{item.meetingRoomUtilization}}</span></td> <td class="table-6"> <a tabindex="0" class="btn btn-lg popover-button unpass-num" role="button" data-toggle="popover" data-trigger="focus" title="" data-content="">{{item.notPassMeetingNum}}</a> </td> </tr> </tbody> </table> <div ng-if="data.length<1 && searchkey==\'\'" class="nothing">暂无相关内容</div> <div ng-if="data.length<1 && searchkey!=\'\'" class="nothing">搜索不到您需要的内容</div>';
  //排序
  $('body').on('click','#meettingRoom .sequenceBtn',function(){
      sequence_type = $(this).data('type');
      console.log(sequence_type);
      resetType(1);
      getDate(initList,1);
  })
    //表格鼠标点击效果
    $('body').on('click','.room-table table tr',function () {
        $('.room-table table tr').removeClass('all-tr');
        $(this).addClass('all-tr');
    })
    //设置弹出框点击事件
    $('body').on('click','.popover-button',function () {
        var arr = $('.popover');
        if(arr.length>1){
            for(var i = 0; i<arr.length-1; i++){
                $('.popover').eq(i).remove();
            }
        }
    })
    //会议数量点击事件
    $('body').on('click','.meeting-num',function (event) {
        var list = {
            'currentPage':'1',
            'pageSize':'15',
            'roomId':$(this).parent().parent().data('roomId'),
            'type':'4',
            'staTime':roomstart,
            'endTime':roomend
        };
        var _this = $(this);
        var height = event.clientY;
        var place = 'top';
        if(height<=450){
            place = 'bottom';
        }
        $('.meeting-num').popover('dispose');
        fetchs.post('/meetingStatistics/getMeetingDetails',list,function (result) {
            console.log(result);
            if(result.ifSuc==1){

                if(result.data.length<1){
                    // $('.popover-meeting').hide();
                }else{
                    // $('.popover-meeting').show();
                    var showData = result.data;
                    var meeting_html = '<div class="popover-table-title"><table class="table table-hover"><thead><tr><th class="table-1">会议名称</th><th class="table-2">会议时间</th><th class="table-3">会议时长</th><th class="table-4">预订人</th><th class="table-5">会议签到率</th><th class="table-6">会议响应率</th></tr></thead></table></div><div class="unmeeting-table"><table class="table table-hover"><tbody>';
                    for(var i =0; i<showData.length; i++){
                        meeting_html += '<tr>' +
                            '<td class="table-1">' +
                            '<span data-toggle="tooltip" data-title="'+ '' +'">' + showData[i].name +
                            '</span>' +
                            '</td>' +
                            '<td class="table-2">' +
                            '<span data-toggle="tooltip" data-title="'+ timeRange(showData[i].staTime,showData[i].endTime) +'">' + timeRange(showData[i].staTime,showData[i].endTime)+
                            '</span>' +
                            '</td>' +
                            '<td class="table-3">' +
                            '<span data-toggle="tooltip" data-title="'+ '' +'">' + showData[i].timeLength+
                            '</span>' +
                            '</td>' +
                            '<td class="table-4">' +
                            '<span data-toggle="tooltip" data-title="'+ '' +'">' + showData[i].reserveName+
                            '</span>' +
                            '</td>' +
                            '<td class="table-5">' +
                            '<span data-toggle="tooltip" data-title="'+ '' +'">' + showData[i].signRate+
                            '</span>' +
                            '</td>' +
                            '<td class="table-6">' +
                            '<span data-toggle="tooltip" data-title="'+ '' +'">' + showData[i].answerRate+
                            '</span>' +
                            '</td>' +
                            '</tr>';
                    }
                    meeting_html += '</tbody></table></div>';
                    _this.popover({
                        placement:place,
                        container:'body',
                        trigger:'click',
                        html:true,
                        template: '<div class="popover popover-meeting" role="tooltip"><div class="popover-content"></div></div>',
                        content:meeting_html
                    })
                    _this.popover('show');

                    //获取滚动条宽度
                    var width = $('.unmeeting-table')[0].offsetWidth - $('.unmeeting-table')[0].scrollWidth;
                    $('.popover-table-title').css("margin-right", width + 'px');
                    // //计算table高度是否超过最大高度
                    // var height = $('.popover-meeting .unmeeting-table table').height();
                    // var maxHeight = $('.unmeeting-table').height();
                    // if(height>maxHeight){
                    //     $('.popover-table-title').css("width", "calc(100% - 8px)");
                    // }

                    //设置transform
                    //取到X轴的Transform
                    var css = $('.popover-meeting').css('transform');
                    var arr = css.split(',');
                    var x = 0;
                    console.log(arr);
                    if(arr.length>1){
                        css = arr[arr.length-2];
                        if(css.length>0){
                            x = parseFloat(css);
                        }else {
                            x = 0;
                        }
                    }else {
                        x = 0;
                    }
                    //判断如果x轴Transform为负数，转为正数
                    if(x<0){
                        x = 0-x;
                    }
                    //加上左边的距离
                    x += 234;
                    $('.popover-meeting').css({'margin-left':x+'px'});
                }
            }
        })
    })
    $('body').on('click','.unpass-num',function (event) {
        $(this).parent('tr').data('roomId');
        var list = {
            'currentPage':'1',
            'pageSize':'15',
            'roomId':$(this).parent().parent().data('roomId'),
            'type':'0',
            'staTime':roomstart,
            'endTime':roomend
        };
        var _this = $(this);
        $('.unpass-num').popover('dispose');
        fetchs.post('/meetingStatistics/getMeetingDetails',list,function (result) {
            console.log(result);
            if(result.ifSuc==1){
                var showData = result.data;
                if(showData.length<1){
                    // $('.popover-unpass').hide();
                }else{
                    // $('.popover-unpass').show();
                    var unpass_html = '<div class="popover-table-title"><table class="table table-hover"><thead><tr><th class="table-1">审核未通过会议</th><th class="table-2">会议时间</th><th class="table-3">预订人</th></tr></thead></table></div><div class="unmeeting-table"><table class="table table-hover"><tbody>';
                    for(var i =0; i<showData.length; i++){
                        unpass_html += '<tr>' +
                            '<td class="table-1">' +
                            '<span data-toggle="tooltip" data-title="'+ '' +'">' + showData[i].name +
                            '</span>' +
                            '</td>' +
                            '<td class="table-2">' +
                            '<span data-toggle="tooltip" data-title="'+ timeRange(showData[i].staTime,showData[i].endTime) +'">' +timeRange(showData[i].staTime,showData[i].endTime) +
                            '</span>' +
                            '</td>' +
                            '<td class="table-3">' +
                            '<span data-toggle="tooltip" data-title="'+ '' +'">' + showData[i].reserveName +
                            '</span>' +
                            '</td>' +
                            '</tr>';
                    }
                    unpass_html += '</tbody></table></div>'
                    //审核未通过会议数量
                    _this.popover({
                        placement:"left",
                        container:'body',
                        trigger:'click',
                        html:true,
                        template: '<div class="popover popover-unpass" role="tooltip"><div class="popover-content"></div></div>',
                        content:unpass_html
                    })
                    _this.popover('show');
                    //获取滚动条宽度
                    var width = $('.unmeeting-table')[0].offsetWidth - $('.unmeeting-table')[0].scrollWidth;
                    $('.popover-table-title').css("margin-right", width + 'px');
                    // //计算table高度是否超过最大高度
                    // var height = $('.popover-unpass .unmeeting-table table').height();
                    // var maxHeight = $('.unmeeting-table').height();
                    // if(height>maxHeight){
                    //     $('.popover-table-title').css("width", "calc(100% - 8px)");
                    // }
                }
            }
        });
    })
    $('body').on('click',function (event) {
        $('.popover-button').popover('dispose');
    })
    $('body').on('click','.popover',function (event) {
        event.stopPropagation();
    })
  // $('body').on('shown.bs.popover','.meeting-num',function(){
  //
  // })
  // $('body').on('shown.bs.popover','.unpass-num',function(){
  //     $(this).parent('tr').data('roomId');
  //     var list = {
  //         'currentPage':'1',
  //         'pageSize':'15',
  //         'roomId':$(this).parent().parent().data('roomId'),
  //         'type':'0',
  //         'staTime':roomstart,
  //         'endTime':roomend
  //     };
  //     fetchs.post('/meetingStatistics/getMeetingDetails',list,function (result) {
  //         console.log(result);
  //         var showData = result.data;
  //         if(result.ifSuc==1){
  //             if(showData.length<1){
  //                 $('.popover-unpass').hide();
  //             }else{
  //                 $('.popover-unpass').show();
  //                 var unpass_html = '<table class="table table-hover"><thead><tr><th class="table-1">审核未通过会议</th><th class="table-2">会议时间</th><th class="table-3">预订人</th></tr></thead><tbody>';
  //                 for(var i =0; i<showData.length; i++){
  //                     unpass_html += '<tr>' +
  //                         '<td class="table-1">' +
  //                         '<span data-toggle="tooltip" data-title="'+ '' +'">' + showData[i].name +
  //                         '</span>' +
  //                         '</td>' +
  //                         '<td class="table-2">' +
  //                         '<span data-toggle="tooltip" data-title="'+ timeRange(showData[i].staTime,showData[i].endTime) +'">' +timeRange(showData[i].staTime,showData[i].endTime) +
  //                         '</span>' +
  //                         '</td>' +
  //                         '<td class="table-3">' +
  //                         '<span data-toggle="tooltip" data-title="'+ '' +'">' + showData[i].reserveName +
  //                         '</span>' +
  //                         '</td>' +
  //                         '</tr>';
  //                 }
  //                 unpass_html += '</tbody></table>'
  //                 $('.show-unpass').html(unpass_html);
  //             }
  //         }
  //     });
  //
  // })

  //重置条件
  function resetType(type){
      if(type){
      }else {
          sequence_type = ''; //重置排序条件
      }
    pageSize = 15;
      roomPage = 1;
    p.resetpage();
  }
  //--鼠标滚动表格头部悬浮

})