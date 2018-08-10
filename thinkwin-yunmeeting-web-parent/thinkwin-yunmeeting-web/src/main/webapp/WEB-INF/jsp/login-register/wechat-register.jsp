<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2017/5/28
  Time: 11:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>企云会-连接组织的智慧</title>
    <%@ include file="/WEB-INF/jsp/common/head.jsp"%>
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/login-register-common.css"><%--登陆注册公共样式--%>
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/wechat-register.css">
    <%--增加微信动态获取主域名  wn:17-09-14--%>
    <%--<%
        String host = request.getScheme() + "://"
                + request.getServerName();
    %>--%>
</head>
<body>
    <!-- 微信注册 -->
    <div class="weixin-register">
        <%@ include file="/WEB-INF/jsp/common/login-logo.jsp"%>
        <div id="register_container" class="register-QuickMark"></div>
        <div class="weixin-text">微信扫一扫免费开通企业云会议系统！</div>
        <div class="iphone-link">
            <a href="/system/registerpage">没有微信号？手机号注册</a>
        </div>
        <div class="warningBox">
            <i class="icon icon-error"></i>
            <label id="warmLabel">${info}</label>
        </div>
        <div class="backLogin">
            <a href="/system/loginpage">返回登录</a>
        </div>
    </div>
    <input type="hidden" id ="appId" value="${appId}"/>
    <input type="hidden" id ="callBackUrl" value="${callBackUrl}"/>
    <input type="hidden" id ="webSiteDomain" value="${webSiteDomain}"/>
</body>
<%--js--%>
<script src="<%=basePath%>static/js/common/wxRegister.js"></script><%--微信外部的js包--%>
<script src="<%=basePath%>static/js/login-register/wechat-login.js"></script><%--本地微信登陆的实例化js--%>
</html>
