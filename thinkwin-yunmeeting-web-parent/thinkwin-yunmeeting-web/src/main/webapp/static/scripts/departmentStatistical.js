$(function() {
  //--------------图表数据
    var dataList,option,myChart,colorList;//环形图
    var xAxisData,meetingNumber,percentList,options,myCharts;//柱状图
  //分页
  // let  p=DepartmentPages();
  // p.callBack(callBack);
  // p.setCount(20);
  var departmentPage  = 1;
  var departmentSize = 15;
  var p2= Pages("#page3");//创建分页器
  p2.callBack=function callBack(currentPage,page){ 
    departmentPage = currentPage;
    departmentSize = page;
    getDepStatisticalData(1);
  } ;
  // p2.setCount(50); //设置分页器请求的总数
  //会议室统计
  //默认开始结束时间和时间范围
  var key = ''; //搜索关键字
  var departmenttime = 7;
  var departmentstart = moment(new Date(new Date().getTime() - 1000*60*60*24*(departmenttime-1))).format('YYYY-MM-DD');
  var departmentend = moment(new Date()).format('YYYY-MM-DD');
  var departmenttype = ''; //部门统计type
  var departmentOrgId = '';
  var total = 0;
  var departmentData = [];
  // $('#department-next').hide();
  $('#department-next').attr({'disabled':'disabled'});
  var depdateRange2 = new pickerDateRange('department-date1', {
      isTodayValid:true ,
      startDate: departmentstart,
      endDate: departmentend,
      startToday: false,
      stopToday: true,
      needCompare: false,
      defaultText: ' ~ ',
      autoSubmit: false,
      inputTrigger: 'date',
      theme: 'ta',
      success: function (obj) {
          departmentstart = obj.startDate;
          departmentend = obj.endDate;
          $('#department-date1').text(moment(departmentstart).format('YYYY年MM月DD日') + ' ~ ' + moment(departmentend).format('YYYY年MM月DD日'))
          resetDepartmentType();
          var oldTime = new Date(departmentend).getTime();
          var now=new Date(departmentstart).getTime();
          departmenttime =parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
          var current = new Date(moment().format('YYYY-MM-DD')).getTime(); //当前时间戳
          if(current - new Date(departmentstart).getTime()>0){
              //当前时间晚于开始时间
              // $('#department-next').show();  
              $('#department-next').removeAttr('disabled');       
          }else {
              // $('#department-next').hide();
              $('#department-next').attr({'disabled':'disabled'});
          }
          if(current - new Date(departmentend).getTime()>0){
              // $('#department-next').show();
              $('#department-next').removeAttr('disabled'); 
          }else {
              // $('#department-next').hide();
              $('#department-next').attr({'disabled':'disabled'});
          }
          var oldTime = new Date(departmentend).getTime();
          var now=new Date(departmentstart).getTime();
          departmenttime =parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
          getDepStatisticalData(0);
      }
  });
    $('#department-date1').text(moment(departmentstart).format('YYYY年MM月DD日') + ' ~ ' + moment(departmentend).format('YYYY年MM月DD日'))

  $('body').on('click','#department-seven',function(){
    // $('#department-next').hide();
    $('#department-next').attr({'disabled':'disabled'});
    departmenttime = 7;
    resetDepartmentType();
    departmentstart = moment(new Date(new Date().getTime() - 1000*60*60*24*(departmenttime-1))).format('YYYY-MM-DD');
    departmentend = moment(new Date()).format('YYYY-MM-DD');
    resetdepartmentDateRange(departmentstart, departmentend);
    $('#department-date1').text(moment(new Date().getTime() - 1000*60*60*24*(departmenttime-1)).format('YYYY年MM月DD日') + ' ~ ' + moment(new Date()).format('YYYY年MM月DD日'))
    getDepStatisticalData(0);
  })
  $('body').on('click','#department-thirty',function(){
    // $('#department-next').hide();
    $('#department-next').attr({'disabled':'disabled'});
    departmenttime = 30;
    resetDepartmentType();
    departmentstart = moment(new Date(new Date().getTime() - 1000*60*60*24*(departmenttime-1))).format('YYYY-MM-DD');
    departmentend = moment(new Date()).format('YYYY-MM-DD');
    $('#department-date1').text(moment(new Date().getTime() - 1000*60*60*24*(departmenttime-1)).format('YYYY年MM月DD日') + ' ~ ' + moment(new Date()).format('YYYY年MM月DD日'))
    resetdepartmentDateRange(departmentstart, departmentend);
    getDepStatisticalData(0);
  })

  $('body').on('click', '#department-pre',function(){
      //计算时间间隔
      // var oldTime = new Date(departmentend).getTime();
      // var now=new Date(departmentstart).getTime();
      // departmenttime =parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
      var newEnd = new Date(new Date(departmentstart).getTime() - 1000*60*60*24);
      var newStart = new Date(new Date(departmentstart).getTime() - 1000*60*60*24*(departmenttime+1));
      var current = new Date(moment().format('YYYY-MM-DD')).getTime(); //当前时间戳
      if(current - new Date(newStart).getTime()>0){
          //当前时间晚于开始时间
          // $('#department-next').show();        
          $('#department-next').removeAttr('disabled'); 
      }
      if(current - new Date(newEnd).getTime()>0){
          // $('#department-next').show();
          $('#department-next').removeAttr('disabled'); 
      }
      departmentstart = moment(newStart).format('YYYY-MM-DD');
      departmentend = moment(newEnd).format('YYYY-MM-DD');
      resetdepartmentDateRange(departmentstart, departmentend);
      $('#department-date1').text(moment(departmentstart).format('YYYY年MM月DD日') + ' ~ ' + moment(departmentend).format('YYYY年MM月DD日'))
      resetDepartmentType();
      getDepStatisticalData(0);
          
  }); 
  $('body').on('click', '#department-next',function(){
      //计算时间间隔
      // var oldTime = new Date(departmentend).getTime();
      // var now=new Date(departmentstart).getTime();
      // departmenttime=parseInt((oldTime-now)/(1000 * 60 * 60 * 24));
      var newStart = new Date(new Date(departmentend).getTime() + 1000*60*60*24);
      var newEnd = new Date(new Date(departmentend).getTime() + 1000*60*60*24*(departmenttime+1));
      var current = new Date(moment().format('YYYY-MM-DD')).getTime(); //当前时间戳
      if(current - new Date(newStart).getTime()>0){
          //开始时间早于当前时间
          departmentstart = moment(newStart).format('YYYY-MM-DD');
      }else {
          departmentstart = moment(new Date()).format('YYYY-MM-DD');
          // $('#department-next').hide();   
          $('#department-next').attr({'disabled':'disabled'});     
      }
      if(current - new Date(newEnd).getTime()>0){
          departmentend = moment(newEnd).format('YYYY-MM-DD');
      }else {
          departmentend = moment(new Date()).format('YYYY-MM-DD');
          // $('#department-next').hide();
          $('#department-next').attr({'disabled':'disabled'});
      }
      resetdepartmentDateRange(departmentstart, departmentend);
      $('#department-date1').text(moment(departmentstart).format('YYYY年MM月DD日') + ' ~ ' + moment(departmentend).format('YYYY年MM月DD日'))
      resetDepartmentType();
      getDepStatisticalData(0);
  });

  function resetdepartmentDateRange(start, end) {
    $('.input_start:eq(3)').val(departmentstart);
    $('.input_end:eq(3)').val(departmentend);
      depdateRange2.calendar_endDate.setMonth(new Date(departmentstart).getMonth()+1, 1);
      depdateRange2.mOpts.endDate = depdateRange2.date2ymd(new Date(departmentend)).join('-');
      depdateRange2.show(false, depdateRange2);
      depdateRange2.init(0);
  }

  //监听搜索框键盘事件
  $('#department-search').on('keypress', function(e) {
    if (e.keyCode==13) {
      resetDepartmentType();
      $('.input-search').removeClass('input-heighLight');
      $(this).blur();
        key = $(this).val(); 
        if(key.length>0){
          $('#del-searchList').show();
          $('#department-searchResult').show();
        }else {
          $('#del-searchList').hide();
          $('#department-searchResult').hide();
        }
        getDepStatisticalData(0);
        return false;
    };
  });
  $('#department-search').bind('focus','input',function () {    
    $('.input-search').addClass('input-heighLight')
    $('#del-searchList').hide();
  })
  $('#department-search').bind('blur','input',function () {   
    $('.input-search').removeClass('input-heighLight')
  })
  $('#del-searchList').on('click',function(){
    $('#del-searchList').hide();
    $('#department-search').val('');
    key = '';
    $('#department-searchResult').hide();
    resetDepartmentType();
    getDepStatisticalData(0);
  })
  //环形图------------------------------
    function pieNoData() {
        var dataList = [{name:'无数据',value:'1'}];
        myChart.dispose();
        myChart = echarts.init(document.getElementById('departmentPie'));
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
  function depChartLeft(dataList,colorList) {
      myChart.dispose();
      myChart = echarts.init(document.getElementById('departmentPie'));
      var optionNo = {
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
              //formatter: "{b} <br/>时长占比：{d}%<br/>会议时长：{c}小时"
              formatter:function(item){
                  var time = duration(Number(item.value));
                  return '<span class="tool-title">'+item.name+'</span><br/><span class="tool-list">时长占比：'+item.percent+'%</span><br/><span class="tool-list">会议时长：'+time+'</span>'
              }
          },
          series: [
              {
                  name:'访问来源',
                  type:'pie',
                  radius: ['60.5%', '83%'],
                  avoidLabelOverlap: false,
                  hoverAnimation:true,
                  hoverOffset:4,
                  label: {
                      normal: {
                          show: false,
                          position: 'center',
                          formatter: '{b}\n\n{d}%'
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
                              return '{a|'+name+'}\n\n{b|'+Number(params.percent.toFixed(1))+'%}';
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
      myChart.setOption(optionNo);
      // setTimeout(function () {
          myChart.dispatchAction({
              type: 'highlight',
              name: dataList[0].name==null? '':dataList[0].name
          });
      // });
      myChart.on('mouseover', function (params) {
          if(params.name!=dataList[0].name){
              myChart.dispatchAction({
                  type: 'downplay',
                  name: dataList[0].name==null? '':dataList[0].name
              });
          };
      });
      myChart.on('mouseout', function (params) {
          myChart.dispatchAction({
              type: 'highlight',
              name: dataList[0].name==null? '':dataList[0].name
          });
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
                    //max:100,
                    interval:20,
                    boundaryGap:false,
                    axisLine: {
                        show:false,
                        lineStyle: {
                            color: '#757575'
                        }
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
                    data:['0','5','10','15','20'],
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
  function depChartRight(xAxisData,meetingNumber,percentList) {
      var echarts_width= $('.department-statistical-bar').width()+'px';
      $('#departmentBar').css({'width':echarts_width});
      var segments = 5;//y轴显示的段数
      if(xAxisData.length<segments){
          segments = xAxisData.length;
      };
      var maxValue= Math.ceil(Math.max.apply(null, meetingNumber)/segments)*segments;
      var intervalValue = Math.ceil(Math.max.apply(null, meetingNumber)/segments);
      if(maxValue==0){
          maxValue =20;
          intervalValue=5;
      };
      options = {
          colors: ['#38d981','#6694ff'],
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
                  return '<span class="tool-title">'+params.name+'</span><br/><span class="tool-list">会议数量：'+params.value+'</span><br/><span class="tool-list">数量占比：'+Number(percentList[params.dataIndex]).toFixed(1)+'%'+'</span>'
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
                  axisLabel: {
                      interval:0,
                      rotate: -45,
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
                  axisLine: {
                      lineStyle: {
                          color: '#e6e6e6'
                      },
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
                  interval:intervalValue,
                  max: maxValue,
                  axisLabel: {
                      fontSize:12,
                      color:'#999',
                      fontFamily: 'Microsoft YaHei',
                      formatter: '{value}'
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
                  barWidth: '30%',
                  itemStyle: {
                      normal: {
                          color: function(params) {
                              return colorList[params.dataIndex]
                          },
                          label: {
                              show: true,
                              position: 'top',
                              formatter: '{c}'
                              // formatter: '{b}\n{c} %'
                          }
                      }
                  },
                  data: meetingNumber
              }
          ]
      };
      myCharts.setOption(options);
  }
  $('body').on('shown.bs.tab','.statistical-footer li',function(e){
    if($(this).attr('name')=='部门统计'){
      myChart = echarts.init(document.getElementById('departmentPie'));
      myCharts = echarts.init(document.getElementById('departmentBar'));
      getDepStatisticalData(0);
    }
  })
  //获取用户信息 userId,token
  var _userInfo = JSON.parse(localStorage.getItem('userinfo'));
  if (_userInfo == null) {
       _userInfo = {
            userId: '',
            token: ''
       };
  };
  //获取部门统计数据
  function getDepStatisticalData(type) {
        fetchs.get('/getDepStatisticalData?token='+_userInfo.token+'&startTime='+departmentstart+'&endTime='+departmentend+'&paramType='+departmenttype+'&sortyType=1&orgId='+departmentOrgId+'&currentPage='+departmentPage+'&pageSize='+departmentSize,function (res) {
            console.log(res);
            if(res.ifSuc==1){
                if(type==0){
                    dataList = [];
                    xAxisData= [];
                    meetingNumber =[];
                    percentList = [];
                    var dataListValue = [];
                    var duration_chart_num =0;//会议时长
                    var meeting_chart_num = 0;//会议数量
                    var barList = res.data.statisticalAnalysisInfos.list;
                    if(res.data.dataList.length<=10){
                        for(var i=0;i<=res.data.dataList.length-1;i++){
                            dataList.push(res.data.dataList[i]);
                        };
                        for(var i=1;i<barList.length;i++){
                            xAxisData.push(barList[i].depName);
                            meetingNumber.push(barList[i].numberOfOrganizationalMeetings);
                            percentList.push((Number(barList[i].numberOfOrganizationalMeetings)/Number(barList[0].numberOfOrganizationalMeetings))*100);
                        };
                    }else{
                        for(var i=0;i<10;i++){
                            duration_chart_num +=Number(res.data.dataList[i].value);
                            dataList.push(res.data.dataList[i]);
                            dataListValue.push(res.data.dataList[i].value);
                        };
                        for(var i=1;i<=barList.length-1;i++){
                            if(i<11){
                                meeting_chart_num += Number(barList[i].numberOfOrganizationalMeetings);
                                xAxisData.push(barList[i].depName);
                                meetingNumber.push(barList[i].numberOfOrganizationalMeetings);
                                percentList.push((Number(barList[i].numberOfOrganizationalMeetings)/Number(barList[0].numberOfOrganizationalMeetings))*100);
                            }
                        };
                        var pie_other=Number(barList[0].meetingHours)-Number(duration_chart_num);
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
                                    'value':Number(barList[0].meetingHours)-Number(duration_chart_num)//会议时长
                                });
                            }
                        };
                        console.log(dataList);
                        /*meetingNumber[10]=Number(barList[0].numberOfOrganizationalMeetings)-meeting_chart_num;//会议数量
                        var other_num = meetingNumber[10];
                        percentList[10]=meetingNumber[10]/Number(barList[0].numberOfOrganizationalMeetings)*100;//数量占比
                        //当长度大于10时重新排序--柱状图
                        if(other_num!=0){
                            meetingNumber = meetingNumber.sort(function(a,b){return b-a});
                            percentList = percentList.sort(function(a,b){return b-a});
                        }else{
                            xAxisData[10]='其他';
                        };
                        for(var i=0;i<meetingNumber.length-1;i++){
                            if(meetingNumber[i]<=other_num&other_num!=0){
                                //console.log(i);‘其他’在重新排序的数组所在的位置
                                xAxisData.splice(i, 0, '其他');
                                break;
                            }
                        };*/
                        //console.log(meetingNumber);
                        //console.log(percentList);
                        //console.log(xAxisData);
                    };
                    var sum = 0;
                    for(var j = 0; j<dataList.length;j++){
                        sum += Number(dataList[j].value);
                    }
                    colorList = [];//背景色的数据
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
                    if(res.data.dataList.length>0){
                        // $('#roomBarChart').show();
                        // $('#roomPieChart').show();
                        // $('.no-chart').hide();
                        depChartLeft(dataList,colorList);
                        depChartRight(xAxisData,meetingNumber,percentList);
                    }else {
                        // $('#roomBarChart').hide();
                        pieNoData();
                        barNoData();
                        // $('#roomPieChart').hide();
                        // $('.no-chart').show();
                    }
                    //重置所有数据
                    if(res.data.statisticalAnalysisInfos){
                        p2.setCount(res.data.statisticalAnalysisInfos.total);
                        total = res.data.statisticalAnalysisInfos.total;
                    }else {
                        p2.setCount(0);
                        total = 0;
                    }
                }else if(type==2){
                    //排序重置分页
                    if(res.data.statisticalAnalysisInfos){
                        p2.setCount(res.data.statisticalAnalysisInfos.total);
                        total = res.data.statisticalAnalysisInfos.total;
                    }else {
                        p2.setCount(0);
                        total = 0;
                    }
                }
                // for(var i =0; i<res.data.statisticalAnalysisInfos.list.length; i++){
                //     res.data.statisticalAnalysisInfos.list[i].list = JSON.stringify(res.data.statisticalAnalysisInfos.list[i].nonParticipantUserInfo);
                // }
                departmentData = res.data.statisticalAnalysisInfos.list;
                $('.department-table').html(soda(temp, {data:res.data.statisticalAnalysisInfos.list,page:departmentPage}));
                // //会议数量
                // $('.department-meeting-num').popover({
                //     placement:"top",
                //     container:'body',
                //     trigger:'click',
                //     html:true,
                //     template: '<div class="popover popover-department-meeting" role="tooltip"><div class="popover-content"></div></div>',
                //     content:'<div class="department-popover-table"></div>'
                // })
                //未参会数量
                // $('.unmeeting-num').popover({
                //     placement:"left",
                //     container:'body',
                //     trigger:'click',
                //     html:true,
                //     template: '<div class="popover popover-unmeeting" role="tooltip"><div class="popover-content"></div></div>',
                //     content:'<div class="show-unmeeting"></div>'
                // })
            }else{

            };
            var ie6 = document.all;
            $('.main-panel-box').scroll(function(){
                var st = $(this).scrollTop();//document.body.scrollTop || document.documentElement.scrollTop
                console.log(st);
                if(st>=300){
                    $('.department-hide-thead').show();
                    if(total>15){
                        $('#department .statical-table').addClass('room-hasPage');
                        $('#page3').css('background-color','#f6f6f6');
                        $('#page3').show();
                    }
                }else {
                    $('.department-hide-thead').hide();
                    $('#page3').css('background-color','#fff');
                    $('#page3').hide();
                    $('#department .statical-table').removeClass('room-hasPage');
                };
            });
        })
    }
  //会议室统计表格
  var temp = ' <table class="table table-hover"> <thead> <tr> <th class="table-1">部门名称</th> <th class="table-2"><button class="sequenceBtn" data-type="0">组织会议数量</button></th> <th class="table-3"><button class="sequenceBtn" data-type="1">会议时长</button></th> <th class="table-4"><button class="sequenceBtn" data-type="2">参会人次</button></th> <th class="table-5"><button class="sequenceBtn" data-type="3">未参会人数</button></th> </tr> </thead> <tbody> <tr ng-repeat="item in data" ng-class="$index==0 ? \'all-tr\' : \'\'"> <td ng-class="$index==0 && page==1 || (item.hasChild==0) ? \'table-1\' : \'table-1 click-tr\'" data-show="0" data-mark="0" data-index="{{$index}}" data-currentid="{{item.depId}}"> <span ng-if="(item.hasChild==1)" class=\'icon icon-organiz-down hidden-child\'></span> <span ng-class="$index==0 && page==1 || (item.hasChild==0)? \'\' : \'click-tr\'" data-toggle="tooltip" data-title="{{item.depName}}">{{item.depName}}</span> </td> <td class="table-2"> <a tabindex="0" class="btn btn-lg btn-danger popover-button department-meeting-num" role="button" data-toggle="popover" data-placement="top" data-trigger="" data-orgid="{{item.depId}}"  data-content="">{{item.numberOfOrganizationalMeetings}}</a> </td> <td class="table-3"><span data-toggle="tooltip" data-title="{{item.meetingHours|formattime}}">{{item.meetingHours|formattime}}</span></td> <td class="table-4"><span data-toggle="tooltip" data-title="{{item.numberOfParticipants}}">{{item.numberOfParticipants}}次</span></td> <td class="table-5"> <a tabindex="0" class="btn btn-lg btn-danger popover-button unmeeting-num" role="button" data-toggle="popover" data-trigger="" data-index="{{$index}}" data-content="">{{item.numberOfAbsentParticipants}}人</a> </td> </tr> </tbody> </table><div ng-if="data.length<1" class="nothing">暂无相关内容</div> ';


  //排序
  $('body').on('click','#department .sequenceBtn',function(){
      departmenttype = $(this).data('type');
      resetDepartmentType(1);
      getDepStatisticalData(2);
      // alert(departmenttype);
  })
    //表格鼠标点击效果
    $('body').on('click','.department-table table tr',function () {
        $('.department-table table tr').removeClass('all-tr');
        $(this).addClass('all-tr');
    })

  function addChild (data,parentId,parentIndex) {
    // body...
      var html = '';
      for (var i = 0; i < data.length; i++) {
          data[i].list = JSON.stringify(data[i].nonParticipantUserInfo);
          var item = data[i];
          var childHtml = '<tr class="child-'+parentId+
              '"><td class="table-1"><span class="child-span" data-toggle="tooltip" data-title="'+
              item.depName+'">'+item.depName+
              '</span></td><td class="table-2"><a tabindex="0" class="btn btn-lg btn-danger popover-button child-meeting-num" role="button" data-toggle="popover" data-orgid="'+ item.depId +'" data-trigger=""  data-content="">'+
              +item.numberOfOrganizationalMeetings+
              '</a></td><td class="table-3"><span data-toggle="tooltip" data-title="'+
              formattime(item.meetingHours)+'">'+formattime(item.meetingHours)+
              '</span></td><td class="table-4"><span data-toggle="tooltip" data-title="'+
              item.numberOfParticipants+'">'+item.numberOfParticipants+'次'+
              '</span></td><td class="table-5"><a tabindex="0" class="btn btn-lg btn-danger popover-button child-unmeeting-num" role="button" data-toggle="popover" data-trigger=""  data-content="" data-parentindex="'+ parentIndex +'" data-index="'+ i +'">'+
              item.numberOfAbsentParticipants+'人'+
              '</a></td></tr>'
          html += childHtml;
      }
      return html;

  }
  $('body').on('click','td.click-tr',function () {
    // body...
    console.log();
    var currentId = $(this).data('currentid');
    var index = $(this).data('index');
    if($(this).data('mark')=='0'){
        //获取子部门下的数据
        var _this = this;
        fetchs.get('/getDepStatisticalData?token='+_userInfo.token+'&startTime='+departmentstart+'&endTime='+departmentend+'&paramType='+departmenttype+'&sortyType=1&orgId='+currentId,function (res) {
            console.log(res);
            if(res.ifSuc==1){
                if(res.data&&res.data.statisticalAnalysisInfos){
                    // for(var i =0; i<res.data.statisticalAnalysisInfos.list.length; i++){
                    //     res.data.statisticalAnalysisInfos.list[i].list = JSON.stringify(res.data.statisticalAnalysisInfos.list[i].nonParticipantUserInfo);
                    // }
                    //把子节点的数据给父节点
                    departmentData[index].childList = res.data.statisticalAnalysisInfos.list;
                    $(_this).parent().after(addChild(res.data.statisticalAnalysisInfos.list,currentId,index));
                    //会议数量
                    // $('.child-meeting-num').popover({
                    //     placement:"top",
                    //     container:'body',
                    //     trigger:'click',
                    //     html:true,
                    //     template: '<div class="popover popover-child-meeting" role="tooltip"><div class="popover-content"></div></div>',
                    //     content:'<div class="child-popover-table"></div>'
                    // });
                    //未参会会议数量
                    // $('.child-unmeeting-num').popover({
                    //     placement:"left",
                    //     container:'body',
                    //     trigger:'click',
                    //     html:true,
                    //     template: '<div class="popover popover-child-unmeeting" role="tooltip"><div class="popover-content"></div></div>',
                    //     content:'<div class="show-child-unmeeting"></div>'
                    // });
                    $(_this).data('mark','1');
                    $(_this).data('show','1');
                    $(_this).find('.icon-organiz-down').removeClass('hidden-child')
                }
            }else {
                notify('danger',res.msg);
            }

        })

    }else {
      var _class = '.child-'+currentId;
      if($(this).data('show')=='0'){
        //显示
        $(this).find('.icon-organiz-down').removeClass('hidden-child')
        $(this).parent().parent().find(_class).show();
        $(this).data('show','1');
      }else  {
        $(this).find('.icon-organiz-down').addClass('hidden-child')
        $(this).parent().parent().find(_class).hide();
        $(this).data('show','0');
      }
    }
    
  })
  //模态框html
  //会议模态框
  function popoverMeeting(data, _class){
    //取到X轴的Transform
    // var css = $(_class).css('transform');
    // var arr = css.split(',');
    // var x = 0;
    // console.log(arr);
    // if(arr.length>1){
    //     if(css.indexOf('matrix3d')>=0){
    //         if(arr.length>=4){
    //             css = arr[arr.length-4];
    //         }
    //     }else {
    //         css = arr[arr.length-2];
    //     }
    //   if(css.length>0){
    //     x = parseFloat(css);
    //   }else {
    //     x = 0;
    //   }
    // }else {
    //   x = 0;
    // }
    // //判断如果x轴Transform为负数，转为正数
    // if(x<0){
    //   x = 0-x;
    // }
    // //加上左边的距离
    // x += 234;
    // $(_class).css({'margin-left':x+'px'});
    //设置transform
    var meeting_html = '<div class="popover-table-title"><table class="table table-hover"><thead><tr><th class="table-1">会议名称</th><th class="table-2">会议时间</th><th class="table-3">组织者</th><th class="table-4">参会人数</th><th class="table-5">会议签到率</th><th class="table-6">会议响应率</th></tr></thead></table></div><div class="unmeeting-table"><table class="table table-hover"><tbody>';
    var showData = data;
    for(var i =0; i<showData.length; i++){
        var item = showData[i];
        meeting_html += '<tr>' +
        '<td class="table-1">' + 
        '<span data-toggle="tooltip" data-title="'+ item.meetingName +'">' + item.meetingName +
        '</span>' +
        '</td>' +
        '<td class="table-2">' + 
        '<span data-toggle="tooltip" data-title="'+ timeRange(item.startTime,item.endTime) +'">' + timeRange(item.startTime,item.endTime) +
        '</span>' +
        '</td>' +
        '<td class="table-3">' + 
        '<span data-toggle="tooltip" data-title="'+ '' +'">' + item.organizerName +
        '</span>' +
        '</td>' +
        '<td class="table-4">' + 
        '<span data-toggle="tooltip" data-title="'+ '' +'">' + item.numberOfParticipants +
        '</span>' +
        '</td>' +
        '<td class="table-5">' + 
        '<span data-toggle="tooltip" data-title="'+ '' +'">' + item.attendanceRate +
        '</span>' +
        '</td>' +
        '<td class="table-6">' + 
        '<span data-toggle="tooltip" data-title="'+ '' +'">' + item.meetingResponseRate +
        '</span>' +
        '</td>' +
        '</tr>';
    }
    meeting_html += '</tbody></table></div>';
    return meeting_html;
  }
  //设置弹出框点击事件
  $('body').on('click','.popover-button',function () {
      var arr = $('.popover');
      if(arr.length>1){
          for(var i = 0; i<arr.length-1; i++){
              $('.popover').eq(i).remove();
          }
      }
  })
    //点击组织会议数量弹出框
    //父级机构
  $('body').on('click','.department-meeting-num',function (event) {
      var orgId = $(this).data('orgid');
      var _this = $(this);
      if(orgId==null){
          orgId = '';
      }
        getMeetingInfo(orgId,_this,event);
  })
    //子级机构
    $('body').on('click','.child-meeting-num',function (event) {
        var orgId = $(this).data('orgid');
        var _this = $(this);
        if(orgId==null){
            orgId = '';
        }
        getMeetingInfo(orgId,_this,event);
    })
    //点击未参会人数弹出框
    //父级机构
    $('body').on('click','.unmeeting-num',function (event) {
        // var list = $(this).data('unlist');
        var _this = $(this);
        var index = $(this).data('index');
        var list = departmentData[index].nonParticipantUserInfo;
        var windowHeight =  $(document.body).height();
        var height = event.clientY;
        var place = 'left';
        if(windowHeight - height <=225){
            place = 'top';
        }
        if(list&&list.length>=1){
            var html = popoverUnMeeting(list, '.popover-unmeeting');
            // $('.popover-button').popover('dispose');
            setTimeout(function () {
                _this.popover({
                    placement:place,
                    container:'body',
                    trigger:'click',
                    html:true,
                    template: '<div class="popover popover-unmeeting" role="tooltip"><div class="popover-content"></div></div>',
                    content:html
                })
                _this.popover('show');
                //获取滚动条宽度
                var width = $('.unmeeting-table')[0].offsetWidth - $('.unmeeting-table')[0].scrollWidth;
                $('.popover-table-title').css("margin-right", width + 'px');
                // //计算table高度是否超过最大高度
                // var height = $('.popover-unmeeting .unmeeting-table table').height();
                // var maxHeight = $('.unmeeting-table').height();
                // if(height>maxHeight){
                //     $('.popover-table-title').css("width", "calc(100% - 8px)");
                // }
                // var css = '';
                // var transform = $('.popover-unmeeting').css('transform');
                // var window = $(document.body).width();
                // var arr = transform.split(',');
                // var x = 0;
                // if(arr.length>1){
                //     if(css.indexOf('matrix3d')>=0){
                //         if(arr.length>=4){
                //             css = arr[arr.length-4];
                //         }
                //     }else {
                //         css = arr[arr.length-2];
                //     }
                //     if(css.length>0){
                //         x = parseFloat(css);
                //     }else {
                //         x = 0;
                //     }
                // }else {
                //     x = 0;
                // }
                // if(x<0){
                //     //重新设置transform
                //     var new_x =  window - 305;
                //     var newtransform = transform.replace(x,new_x);
                //     $('.popover-unmeeting').css({'transform': newtransform});
                // }
            },10);
            // $('.popover-unmeeting').show();
        }else {
            // $('.popover-unmeeting').hide();
        }
    })
    $('body').on('click','.child-unmeeting-num',function (event) {
        // var list = $(this).data('unlist');
        var parentIndex = $(this).data('parentindex');
        var index = $(this).data('index');
        var windowHeight =  $(document.body).height();
        var height = event.clientY;
        var place = 'left';
        if(windowHeight - height <=225){
            place = 'top';
        }
        var list = departmentData[parentIndex].childList[index].nonParticipantUserInfo;
        var _this = $(this);
        if(list&&list.length>=1){
            var html = popoverUnMeeting(list, '.popover-unmeeting');
            // $('.popover-button').popover('dispose');
            setTimeout(function () {
                _this.popover({
                    placement:place,
                    container:'body',
                    trigger:'click',
                    html:true,
                    template: '<div class="popover popover-unmeeting" role="tooltip"><div class="popover-content"></div></div>',
                    content:html
                })
                _this.popover('show');
                //获取滚动条宽度
                var width = $('.unmeeting-table')[0].offsetWidth - $('.unmeeting-table')[0].scrollWidth;
                $('.popover-table-title').css("margin-right", width + 'px');
                // //计算table高度是否超过最大高度
                // var height = $('.popover-unmeeting .unmeeting-table table').height();
                // var maxHeight = $('.unmeeting-table').height();
                // if(height>maxHeight){
                //     $('.popover-table-title').css("width", "calc(100% - 8px)");
                // }
                // var css = '';
                // var transform = $('.popover-unmeeting').css('transform');
                // var window = $(document.body).width();
                // var arr = transform.split(',');
                // var x = 0;
                // if(arr.length>1){
                //     if(css.indexOf('matrix3d')>=0){
                //         if(arr.length>=4){
                //             css = arr[arr.length-4];
                //         }
                //     }else {
                //         css = arr[arr.length-2];
                //     }
                //     if(css.length>0){
                //         x = parseFloat(css);
                //     }else {
                //         x = 0;
                //     }
                // }else {
                //     x = 0;
                // }
                // if(x<0){
                //     //重新设置transform
                //     var new_x =  window - 305;
                //     var newtransform = transform.replace(x,new_x);
                //     $('.popover-unmeeting').css({'transform': newtransform});
                // }
            },10)
            // $('.popover-unmeeting').show();
        }else {
            // $('.popover-unmeeting').hide();
        }
    })
  $('body').on('click',function (event) {
      $('.popover-button').popover('dispose');
  })
    $('body').on('click','.popover',function (event) {
        event.stopPropagation();
    })
  //未参会人数模态框
  function popoverUnMeeting(data, _class){
      //取到X轴的Transform
      // var css = '';
      // var transform = $(_class).css('transform');
      // var arr = transform.split(',');
      // var x = 0;
      // if(arr.length>1){
      //     if(css.indexOf('matrix3d')>=0){
      //         if(arr.length>=4){
      //             css = arr[arr.length-4];
      //         }
      //     }else {
      //         css = arr[arr.length-2];
      //     }
      //     if(css.length>0){
      //         x = parseFloat(css);
      //     }else {
      //         x = 0;
      //     }
      // }else {
      //     x = 0;
      // }
      // //重新设置transform
      // var new_x =  x - 328;
      // var newtransform = transform.replace(x,new_x);
      // $(_class).css({'transform': newtransform});
    var unpass_html = '<div class="popover-table-title"><table class="table table-hover"><thead><tr><th class="table-1">未参会人数</th></tr></thead></table></div><div class="unmeeting-table"><table class="table table-hover"><tbody>';
    var showData = data;
    for(var i =0; i<showData.length; i++){
        var item = showData[i];
        unpass_html += '<tr>' +
        '<td class="table-1">' + 
        item.userName +
        '</span>' +
        '</td>' +
        '</tr>';
    }
    unpass_html += '</tbody></table></div>';
    return unpass_html;
  }
  //父级
  //获取会议数量，如果有数据就显示弹出框，无数据就不显示
  function getMeetingInfo(orgId, _this,target) {
      $('.popover-button').popover('dispose');
      fetchs.get('/getOrganizationalMeetingsInfo?startTime='+departmentstart+'&endTime='+departmentend+'&token='+_userInfo.token+'&orgId='+orgId,function (res) {
          console.log(res);
          if(res.ifSuc==1){
              if(res.data.length>=1){
                  //
                  var html = popoverMeeting(res.data,'.popover-department-meeting');
                  var height = target.clientY;
                  var place = 'top';
                  if(height<=450){
                      place = 'bottom';
                  }
                  _this.popover({
                      placement:place,
                      container:'body',
                      trigger:'click',
                      html:true,
                      template: '<div class="popover popover-department-meeting" role="tooltip"><div class="popover-content"></div></div>',
                      content:html
                  })
                  _this.popover('show');
                  //获取滚动条宽度
                  var width = $('.unmeeting-table')[0].offsetWidth - $('.unmeeting-table')[0].scrollWidth;
                  $('.popover-table-title').css("margin-right", width + 'px');
                  // //计算table高度是否超过最大高度
                  // var height = $('.popover-department-meeting .unmeeting-table table').height();
                  // var maxHeight = $('.unmeeting-table').height();
                  // if(height>maxHeight){
                  //     $('.popover-table-title').css("width", "calc(100% - 8px)");
                  // }

                  var css = $('.popover-department-meeting').css('transform');
                  var arr = css.split(',');
                  var x = 0;
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
                  $('.popover-department-meeting').css({'margin-left':x+'px'});
              }else {
                  // $('.popover-department-meeting').hide();
              }
          }else {
              // $('.popover-department-meeting').hide();
          }
      })
  }
  //未参会会议数量
  // $('body').on('shown.bs.popover','.unmeeting-num',function(e){
  //     var list = $(this).data('unlist');
  //     if(list&&list.length>=1){
  //         $('.show-unmeeting').html(popoverUnMeeting(list, '.popover-unmeeting'));
  //         $('.popover-unmeeting').show();
  //     }else {
  //         $('.popover-unmeeting').hide();
  //     }
  //
  // })

  //子级
  //会议数量
  // $('body').on('shown.bs.popover','.child-meeting-num',function(){
  //     var orgId = $(this).data('orgid');
  //     if(orgId==null){
  //         orgId = '';
  //     }
  //     // $('.popover-child-meeting').hide();
  //     fetchs.get('/getOrganizationalMeetingsInfo?startTime='+departmentstart+'&endTime='+departmentend+'&token='+_userInfo.token+'&orgId='+orgId,function (res) {
  //         console.log(res);
  //         if(res.ifSuc==1){
  //             if(res.data.length>=1){
  //                 $('.popover-child-meeting').show();
  //                 $('.child-popover-table').html(popoverMeeting(res.data,'.popover-child-meeting'));
  //             }else {
  //                 $('.popover-child-meeting').hide();
  //             }
  //         }else {
  //             $('.popover-child-meeting').hide();
  //         }
  //     })
  //
  // })
  //未参会会议数量
  // $('body').on('shown.bs.popover','.child-unmeeting-num',function(){
  //     var list = $(this).data('unlist');
  //     if(list&&list.length>=1){
  //         $('.show-child-unmeeting').html(popoverUnMeeting(list, '.popover-child-unmeeting'));
  //         $('.popover-child-unmeeting').show();
  //     }else {
  //         $('.popover-child-unmeeting').hide();
  //     }
  //   // $('.show-child-unmeeting').html(popoverUnMeeting([1,2]));
  // })

  //重置条件
  function resetDepartmentType(type){
    if(type){}else {
        departmenttype = 0; //重置排序条件
    }
    departmentSize = 15;
    departmentPage = 1;
    p2.resetpage();
  }

})