<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2017/5/28
  Time: 14:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>企云会-连接组织的智慧</title>
    <%@ include file="/WEB-INF/jsp/common/head.jsp"%>
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/login-register-common.css">
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/bind-account.css">
</head>
<body>
<div id="binding-account">
    <%@ include file="/WEB-INF/jsp/common/login-logo.jsp"%>
    <div class="form-group">
        <input type="tel" class="form-control form-control-lg" id="inputTel" placeholder="请输入手机号"/>
    </div>
    <div class="form-group input-group">
        <input type="text" class="col-7 form-control form-control-lg" id="inputCode" placeholder="请输入验证码"/>
        <input type="button" id="bindCode" class="col-4 btn btn-lg btn-secondary code" value="获取验证码"/>
    </div>
    <div class="warningBox">
        <i class="icon icon-error"></i>
        <label id="warmLabel"></label>
    </div>
    <div class="form-group">
        <button id="bindBtn" class="btn btn-lg btn-primary col-12">立即绑定</button>
    </div>
    <div class="backLogin">
        <a href="/system/loginpage">返回登录</a>
    </div>
    <input id="uid" type="hidden" value="${uId}">
</div>
</body>
<script src="<%=basePath%>static/js/common/formValidator.js"></script>
<script src="<%=basePath%>static/js/login-register/bind-account.js"></script>
</html>
