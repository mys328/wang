<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html class="no-js" lang="">
<head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="apple-touch-icon" href="<%=basePath%>apple-touch-icon.png">
    <!-- Place favicon.ico in the root directory-->
    <!-- build:css static/styles/common.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/common.css">
    <!-- endbuild-->
    <script src="<%=basePath%>static/plugin/underscore.min.js"></script>
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <!-- build:css static/styles/index.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/index.css">
    <!-- endbuild-->
</head>
<body><!--[if lt IE 10]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade
    your browser</a> to improve your experience.</p><![endif]-->
<nav class="navbar navbar-expand-lg navbar-light">
    <%@ include file="/WEB-INF/jsp/common/mainTop.jsp" %>
    <%--<div class="d-flex flex-row justify-content-between align-items-center"><a class="navbar-brand" href="#"><img class="d-inline-block align-top" src="static/images/logo@2x.png">企云会</a>
        <ul class="nav">
            <li class="nav-item active"><a class="nav-link" href="/">首页<span class="sr-only">(current)</span></a></li>
            <li class="nav-item"><a class="nav-link" href="/meeting.html">我的会议</a></li>
            <li class="nav-item"><a class="nav-link" href="/booking.html">会议预定</a></li>
            <li class="nav-item"><a class="nav-link btn-authorize" target="_blank" href="authorize.html">控制台</a></li>
            <li class="nav-item"><a class="nav-link" href="#"><span class="icon icon-notice"></span></a></li>
            <li class="nav-item dropdown"><a class="nav-link dropdown-toggle" id="menu" href="#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="nophoto">管理</span><span>系统管理员</span></a>
                <div class="dropdown-menu" aria-labelledby="menu"><a class="dropdown-item" href="user.html"><i class="icon icon-my"></i>个人资料</a><a class="dropdown-item" href="/logout"><i class="icon icon-logout"></i>退出登录</a></div>
            </li>
        </ul>
    </div>--%>
</nav>
<div class="container-fluid" id="main">
    <div class="row">
        <div class="col counts company">
            <h6>2017年7月，北京盛科维企业会议统计</h6>
            <div class="stress"><span>会议室利用率</span><span class="ratio" id="meetingRoomUtilization">0</span></div>
            <div class="sub"><span>共开会议</span><span id="meetingTotalNum">0</span><span>场次</span></div>
            <div class="sub"><span>总会议时长</span><span id="meetingTotalTime">0</span><span>小时</span></div>
            <div class="sub"><span>总参会人数</span><span id="meetingTotalPeopleNum">0</span><span>人次</span></div>
        </div>
        <div class="col counts personal">
            <h6>7月个人会议统计</h6>
            <div class="stress"><span>您的会议总时长</span><span class="duration" id="personMeetingTotalTime">0</span></div>
            <div class="sub"><span>预订会议</span><span id="reserveMeetingNum">0</span><span>场次</span></div>
            <div class="sub"><span>参与会议</span><span id="ParticipateMeetingNum">0</span><span>场次</span></div>
            <div class="sub"><span>占总会议</span><span id="personMeetingProportion">0</span><span></span></div>
        </div>
        <div class="col chart">
            <div class="pie" id="pie1">
                <svg width="126" height="126">
                    <circle class="idle active" cx="63" cy="63" r="52" transform="rotate(90 63 63)"
                            stroke-dashoffset="522.752"></circle>
                    <circle class="occupied" cx="63" cy="63" r="52" transform="rotate(90 63 63)"
                            stroke-dashoffset="196.032"></circle>
                </svg>
                <span>空闲<i id="meetingRoomEmptiest">0%</i></span>
                <p>会议室空闲、使用占比</p>
            </div>
            <div class="pie" id="pie2">
                <svg width="126" height="126">
                    <circle class="personal active" cx="63" cy="63" r="52" transform="rotate(90 63 63)"
                            stroke-dashoffset="490.08"></circle>
                    <circle class="company" cx="63" cy="63" r="52" transform="rotate(90 63 63)"
                            stroke-dashoffset="163.36"></circle>
                </svg>
                <span>个人<i id="personMeetingTimeProportion">0%</i></span>
                <p>个人会议总时长占比</p>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-8 meeting-plan">
            <h5>近期会议安排<a href="#">您有5条需审核会议</a><a class="more" href="/meeting.html">查看更多</a></h5>
        </div>
        <div class="col-4 meeting-dynamics">
            <h5>近期会议动态<a class="more" href="javascript:void (0)">查看更多</a></h5>
        </div>
    </div>
</div>
<!-- build:js static/scripts/index.js-->
<script src="<%=basePath%>static/scripts/index.js"></script>
<!-- endbuild-->
</body>
</html>