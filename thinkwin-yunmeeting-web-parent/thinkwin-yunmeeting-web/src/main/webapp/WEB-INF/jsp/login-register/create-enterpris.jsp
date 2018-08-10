<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2017/5/28
  Time: 15:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>企云会-连接组织的智慧</title>
    <%@ include file="/WEB-INF/jsp/common/head.jsp"%>
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/login-register-common.css">
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/create-enterpris.css">
</head>
<body>
<div class="create-enterpris">
    <%@ include file="/WEB-INF/jsp/common/login-logo.jsp"%>
    <div class="form-group">
        <input type="text" class="form-control form-control-lg col-12" maxlength="30"  placeholder="公司名称" id="inputCompany"/>
    </div>
    <div class="warningBox">
        <i class="icon icon-error"></i>
        <label id="warmLabel"></label>
    </div>
    <div class="form-group">
        <button id="save" class="btn btn-lg btn-primary col-12 create-btn">创建企业</button>
    </div>
    <div class="backLogin">
        <a href="/system/loginpage">返回登录</a>
    </div>
    <input id="uid" type="hidden" value="${uId}">
    <input id="userId" type="hidden" value="${userId}">
</div>
</body>
<script src="<%=basePath%>static/js/common/formValidator.js"></script>
<script src="<%=basePath%>static/js/login-register/create-enterpris.js"></script>
</html>
