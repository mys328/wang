<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2017/5/28
  Time: 13:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>操作成功</title>
    <%@ include file="/WEB-INF/jsp/common/head.jsp"%>
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/login-register-common.css"><%--登陆注册公共样式--%>
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/succeed.css">
</head>
<body>
    <!-- 注册成功 -->
    <div class="succeed">
        <%@ include file="/WEB-INF/jsp/common/login-logo.jsp"%>
        <div class="succeed-logo">
            <i class="fa fa-check-circle-o"></i>
        </div>
        <div class="succeed-text">
            ${hintMessage}
        </div>
        <button id="backLogin" class="btn btn-primary col-12" type="button">
            ${buttonMessage}（<span id="count">3</span>s）
        </button>
    </div>
</body>
<script src="<%=basePath%>static/js/login-register/succeed.js"></script>
</html>
