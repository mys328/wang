<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8"  %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv=”x-ua-compatible” content=”ie=10″ />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="shortcut icon" type="image/x-icon" href="<%=basePath%>static/images/yunmeeting-logo.ico">
    <link rel="stylesheet" href="<%=basePath%>static/styles/dashboard.css">
    <script src="<%=basePath%>static/plugin/underscore.min.js"></script>
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <link rel="stylesheet" href="<%=basePath%>static/styles/personnel.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/personnelOrg.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/statistical.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/selectTime.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/dateRange.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/statisticalPage.css">
  </head>
  <body><!--[if lt IE 10]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
    <div class="wrapper">
      <div class="sidebar">
        <div class="logo"><img src="<%=basePath%>static/images/yunmeeting-logo.png">
          <h4>用户企业</h4>
        </div>
        <%@ include file="/WEB-INF/jsp/common/left.jsp"%>
      </div>
      <div class="main-panel">
        <div class="main-panel-box">
          <nav class="navbar fixed-top navbar-expand-lg navbar-light">
            <div class="d-flex flex-row justify-content-between align-items-center">
              <ul class="tab-business statistical-tab" role="tablist">
                <li class="nav-link active" data-toggle="tab" href="#statistical" role="tab">统计分析</li>
              </ul>
              <%@ include file="/WEB-INF/jsp/common/top.jsp"%>
            </div>
          </nav>
          <div class="content">
            <div class="tab-content">
              <div class="tab-pane active" id="statistical" role="tabpanel">
                <div class="statistical-header">
                  <div class="date-box">
                    <div class="day-tab"><a class="btn btn-primary day-tab-active" id="seven">近7天</a><a class="btn btn-primary" id="thirty">近30天</a></div>
                    <div class="time-box">
                      <button class="icon icon-calendar-left" id="pre"></button><span class="date_title" id="date1"></span>
                      <button class="icon icon-calendar-right" id="next"></button>
                    </div>
                  </div>
                  <div class="meetting-type d-flex flex-row">
                    <div class="media"><span class="icon icon-statsmeetings d-flex align-self-center mr-3" style="color: #41adfa;"></span>
                      <div class="media-body">
                        <h5 class="mt-0">总会议数量</h5>
                        <p class="mb-0"></p>
                      </div>
                    </div>
                    <div class="media"><span class="icon icon-statstimes d-flex align-self-center mr-3" style="color: #ff7a5c;"></span>
                      <div class="media-body">
                        <h5 class="mt-0">总会议时长</h5>
                        <p class="mb-0"></p>
                      </div>
                    </div>
                    <div class="media"><span class="icon icon-statssignin d-flex align-self-center mr-3" style="color: #6694ff;"></span>
                      <div class="media-body">
                        <h5 class="mt-0">总签到率</h5>
                        <p class="mb-0"></p>
                      </div>
                    </div>
                    <div class="media"><span class="icon icon-statsrespond d-flex align-self-center mr-3" style="color: #38d981;"></span>
                      <div class="media-body">
                        <h5 class="mt-0">总会议响应率</h5>
                        <p class="mb-0"></p>
                      </div>
                    </div>
                  </div>
                  <div class="meetting-chart" id="main"></div>
                </div>
                <ul class="statistical-footer d-flex flex-row justify-content-around" role="tablist">
                  <li class="nav-link" data-toggle="tab" href="#meettingRoom" role="tab" aria-controls="meettingRoom" name="会议室统计">
                    <h2>会议室统计</h2>
                    <div class="link-text d-flex flex-row justify-content-between">
                      <div class="link-left">
                        <p class="link-label">会议室数量</p>
                        <p class="link-number"></p>
                      </div>
                      <div class="link-right">
                        <p class="link-label">使用率</p>
                        <p class="link-number"></p>
                      </div>
                    </div><i class="icon icon-kzt-mroom"></i>
                  </li>
                  <li class="nav-link" data-toggle="tab" href="#persons" role="tab" aria-controls="persons" name="人员统计">
                    <h2>人员统计</h2>
                    <div class="link-text d-flex flex-row justify-content-between">
                      <div class="link-left">
                        <p class="link-label">员工人数</p>
                        <p class="link-number"></p>
                      </div>
                      <div class="link-right">
                        <p class="link-label">未参会人数</p>
                        <p class="link-number"></p>
                      </div>
                    </div><i class="icon icon-personaldata"></i>
                  </li>
                  <li class="nav-link" data-toggle="tab" href="#department" role="tab" aria-controls="department" name="部门统计">
                    <h2>部门统计</h2>
                    <div class="link-text d-flex flex-row justify-content-between">
                      <div class="link-left">
                        <p class="link-label">部门数量</p>
                        <p class="link-number"></p>
                      </div>
                      <div class="link-right">
                        <p class="link-label">跨部门会议</p>
                        <p class="link-number"></p>
                      </div>
                    </div><i class="icon icon-organiz-unit"></i>
                  </li>
                </ul>
              </div>
              <div class="tab-pane import" id="meettingRoom" role="tabpanel">
                <div class="statistical-header">
                  <div class="date-box">
                    <div class="day-tab"><a class="btn btn-primary day-tab-active" id="room-seven">近7天</a><a class="btn btn-primary" id="room-thirty">近30天</a></div>
                    <div class="time-box">
                      <button class="icon icon-calendar-left" id="room-pre"></button><span class="date_title" id="room-date1"></span>
                      <button class="icon icon-calendar-right" id="room-next"></button>
                    </div>
                  </div>
                  <div class="room-screen d-flex flex-row">
                    <div class="dropdown area-down">
                      <div id="areatype" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true">
                        <span class="form-control placeholder-color" id="area" name="areaId" >全部区域</span>
                        <span class="icon icon-organiz-down"></span>
                      </div>
                      <div id="roomDropdownMenu" class="dropdown-menu area-type" aria-labelledby="areatype">
                        <%--  <span class="span-list select-active span-list-0" name="0">全部人员</span>
                          <span class="span-list span-list-1" name="1">已激活</span>
                          <span class="span-list span-list-2" name="2">未激活</span>
                          <span class="span-list span-list-3" name="3">已禁用</span>
                          <span class="span-list span-list-4" name="4">未分配部门</span>--%>
                      </div>
                    </div>
                    <div class="form-control input-search"><i class="icon icon-search"></i>
                      <input id="room-search" type="text" maxlength="100" placeholder="搜索会议室名称"><i class="icon icon-search-del" id="del-searchList"></i>
                    </div>
                    <label class="custom-control custom-checkbox">
                      <input class="custom-control-input" name="deviceService" type="checkbox" value="002"><span class="custom-control-indicator"></span><span class="custom-control-description">需审核会议室</span>
                    </label><span id="room-searchResult"></span>
                  </div>
                </div>
                <div class="hide-thead">
                  <table class="table">
                    <thead>
                    <tr>
                      <th class="table-1">会议室名称</th>
                      <th class="table-2">
                        <button class="sequenceBtn" data-type="1">会议数量</button>
                      </th>
                      <th class="table-3">
                        <button class="sequenceBtn" data-type="2">使用时间</button>
                      </th>
                      <th class="table-4">
                        <button class="sequenceBtn" data-type="3">闲置时长</button>
                      </th>
                      <th class="table-5">
                        <button class="sequenceBtn" data-type="4">使用率</button>
                      </th>
                      <th class="table-6">
                        <button class="sequenceBtn" data-type="5">审核未通过会议数量</button>
                      </th>
                    </tr>
                    </thead>
                  </table>
                </div>
                <div class="statistical-body d-flex flex-row justify-content-center align-items-center">
                  <div class="room-statistical-pie chart-shadow">
                    <p class="chart-label"><span></span><span> 会议数量占比</span></p>
                    <div class="chart-box chart-box-left" id="roomPieChart"></div>
                    <div style="display: none" class="nothing no-chart">暂无数据</div>
                  </div>
                  <div class="room-statistical-bar chart-shadow">
                    <p class="chart-label"><span></span><span>会议室使用率 <i style="font-size: 12px;font-style: normal">Top10</i></span></p>
                    <div class="chart-box chart-box-right" id="roomBarChart"></div>
                    <div style="display: none" class="nothing no-chart">暂无数据</div>
                  </div>
                </div>
                <div class="statistical-footer" id="roomTable">
                  <div class="room-table statical-table"></div>
                  <div id="page1">
                    <div class="page-Box" id="page-Box"> </div>
                  </div>
                </div>
              </div>
              <div class="tab-pane import" id="persons" role="tabpanel">
                <div class="statistical-header">
                  <div class="date-box">
                    <div class="day-tab"><a class="btn btn-primary day-tab-active" id="person-seven">近7天</a><a class="btn btn-primary" id="person-thirty">近30天</a></div>
                    <div class="time-box">
                      <button class="icon icon-calendar-left" id="person-pre"></button><span class="date_title" id="person-date1"></span>
                      <button class="icon icon-calendar-right" id="person-next"></button>
                    </div>
                  </div>
                  <div class="person-screen d-flex flex-row">
                    <div class="dropdown area-down org-wrapper"><%--data-toggle="personnel" data-type="4"--%>
                      <span class="form-control trigger">选择部门</span>
                      <span class="icon icon-organiz-down org-btn"></span>
                     <%-- <div id="persontype" class="touch">
                        &lt;%&ndash;<span class="form-control placeholder-color value" id="person" name="areaId">选择部门</span>
                        <span class="icon icon-organiz-down"></span>
                        <input name="organizer" type="hidden" data-validate="organizer" value="">&ndash;%&gt;
                      </div>--%>
                    </div>
                    <div class="form-control input-search"><i class="icon icon-search"></i>
                      <input id="person-search" type="text" maxlength="100" placeholder="搜索员工姓名"><i class="icon icon-search-del" id="del-searchPerson"></i>
                    </div><span id="person-searchResult"></span>
                  </div>
                </div>
                <div class="statistical-body">
                  <div class="hide-thead">
                    <table class="table">
                      <thead>
                      <tr>
                        <th class="table-1">员工姓名</th>
                        <th class="table-2">
                          <button class="sequenceBtn" data-type="1">会议数量(组织+参与)</button>
                        </th>
                        <th class="table-3">
                          <button class="sequenceBtn" data-type="0">会议时长</button>
                        </th>
                        <th class="table-4">
                          <button class="sequenceBtn" data-type=2>会议留言数</button>
                        </th>
                        <th class="table-5">
                          <button class="sequenceBtn" data-type="3">个人签到率</button>
                        </th>
                        <th class="table-6">
                          <button class="sequenceBtn" data-type="4">个人响应率</button>
                        </th>
                      </tr>
                      </thead>
                    </table>
                  </div>
                  <div class="person-table statical-table"></div>
                  <div id="page2">
                    <div class="page-Box" id="page-Box"></div>
                  </div>
                </div>
              </div>
              <div class="tab-pane import" id="department" role="tabpanel">
                <div class="statistical-header">
                  <div class="date-box">
                    <div class="day-tab"><a class="btn btn-primary day-tab-active" id="department-seven">近7天</a><a class="btn btn-primary" id="department-thirty">近30天</a></div>
                    <div class="time-box">
                      <button class="icon icon-calendar-left" id="department-pre"></button><span class="date_title" id="department-date1"></span>
                      <button class="icon icon-calendar-right" id="department-next"></button>
                    </div>
                  </div>
                </div>
                <div class="department-hide-thead">
                  <table class="table">
                    <thead>
                    <tr>
                      <th class="table-1">部门名称</th>
                      <th class="table-2">
                        <button class="sequenceBtn" data-type="0">组织会议数量</button>
                      </th>
                      <th class="table-3">
                        <button class="sequenceBtn" data-type="1">会议时长</button>
                      </th>
                      <th class="table-4">
                        <button class="sequenceBtn" data-type="2">参会人次</button>
                      </th>
                      <th class="table-5">
                        <button class="sequenceBtn" data-type="3">未参会人数</button>
                      </th>
                    </tr>
                    </thead>
                  </table>
                </div>
                <div class="statistical-body d-flex flex-row justify-content-center align-items-center">
                  <div class="department-statistical-pie chart-shadow">
                    <p class="chart-label"><span></span><span>部门会议时长占比</span></p>
                    <div class="chart-box chart-box-left" id="departmentPie" style="width: 364px;"></div>
                    <div style="display: none" class="nothing no-chart">暂无数据</div>
                  </div>
                  <div class="department-statistical-bar chart-shadow">
                    <p class="chart-label"><span></span><span>部门组织会议数量 <i style="font-size: 12px;font-style: normal">Top10</i></span></p>
                    <div class="chart-box chart-box-right" id="departmentBar"></div>
                    <div style="display: none" class="nothing no-chart">暂无数据</div>
                  </div>
                </div>
                <div class="statistical-footer">
                  <div class="department-table statical-table"></div>
                  <div id="page3">
                    <div class="page-Box" id="page-Box"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <script src="<%=basePath%>static/scripts/plugins/echarts.js"></script>
    <script src="<%=basePath%>static/scripts/dateRange.js"></script>
    <script src="<%=basePath%>static/scripts/pagesPro.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <script src="<%=basePath%>static/scripts/personnel.js"></script>
    <script src="<%=basePath%>static/scripts/personnelOrg.js"></script>
    <script src="<%=basePath%>static/scripts/statistical.js"></script>
    <script src="<%=basePath%>static/scripts/roomStatistical.js"></script>
    <script src="<%=basePath%>static/scripts/personStatistical.js"></script>
    <script src="<%=basePath%>static/scripts/departmentStatistical.js"></script>
  </body>
</html>