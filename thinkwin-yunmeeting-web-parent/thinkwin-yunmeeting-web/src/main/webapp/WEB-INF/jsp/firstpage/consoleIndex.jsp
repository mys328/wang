<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2017/9/15
  Time: 13:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="shortcut icon" type="image/x-icon" href="<%=basePath%>static/images/yunmeeting-logo.ico">
    <link rel="stylesheet" href="<%=basePath%>static/styles/dashboard.css">
    <!-- endbuild-->
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">
</head>
<body>
    <div class="wrapper">
        <div class="sidebar">
            <div class="logo"><img src="<%=basePath%>static/images/yunmeeting-logo.png">
                <h4>用户企业</h4>
            </div>
            <%@ include file="/WEB-INF/jsp/common/left.jsp"%>
        </div>
        <div class="main-panel">
            <nav class="navbar fixed-top navbar-expand-lg navbar-light">
                <div class="d-flex flex-row justify-content-between align-items-center">
                    <ul class="tab-business" role="tablist">
                    </ul>
                    <%@ include file="/WEB-INF/jsp/common/top.jsp"%>
                </div>
            </nav>
            <div class="content directories" style="float: left;width: 100%;">
                    <img class="console-bg" style="margin: 88px auto 0;display: block;" src="<%=basePath%>static/images/console-index.png">
                    <p class="console-label" style="font-size:36px;color:#5c5c5c;text-align:center;margin-top: 60px;">欢迎使用企云会控制台</p>
            </div>
        </div>
    </div>
</body>
</html>
