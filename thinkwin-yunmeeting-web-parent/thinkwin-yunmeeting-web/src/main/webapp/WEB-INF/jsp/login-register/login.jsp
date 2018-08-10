<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>企云会-连接组织的智慧</title>
    <%@ include file="/WEB-INF/jsp/common/head.jsp"%>
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/login.css">
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/login-register-common.css">
    <%--增加微信动态获取主域名  wn:17-09-14--%>
    <%--<%
        String host = request.getScheme() + "://"
                + request.getServerName();
    %>--%>
</head>
<body>
<div class="form" id="form">
    <%@ include file="/WEB-INF/jsp/common/login-logo.jsp"%>
    <ul id="login-nav" class="nav nav-tabs">
        <li class="nav-item" id="wechatLogin">
            <a class="nav-link login-active" name="wechat">微信登录</a><%--<spring:message code="auth.login.wechat" />--%>
        </li>
        <li class="nav-item">
            <a class="nav-link" name="account">账号登录</a>
        </li>
    </ul>
    <div id="login_container"></div>
    <div class="account-login" id="account_login" style="display: none;">
        <input type="hidden" id ="appId" value="${appId}"/>
        <input type="hidden" id ="callBackUrl" value="${callBackUrl}"/>
        <input type="hidden" id ="webSiteDomain" value="${webSiteDomain}"/>
        <form loginForm="ngForm">
            <div class="form-group phone-group">
                <input id="phoneNumber"  class="form-control form-control-lg" type="tel" placeholder="请输入手机号"/>
            </div>
            <div class="form-group password-input">
                <input type="text" onfocus="this.type='password'" class="form-control form-control-lg" id="inputPassword"  placeholder="请输入密码"/>
            </div>
            <div class="losed-psd">
                    <span class="reminder">
                        <i class="fa fa-exclamation-circle"></i>
                        <label class="reminder-text"></label>
                    </span>
                <a href="javascript:void(0);" onclick="forget()">忘记密码?</a>
            </div>
            <div class="warningBox">
                <i class="icon icon-error"></i>
                <label id="warmLabel"></label>
            </div>
            <div class="form-group">
                <button  type="button" id="submitBtn" class="btn btn-lg btn-primary login-btn">登录</button>
            </div>
            <div class="register-link">
                还没有账号？
                <a href="/system/wechatregisterpage">注册新账号</a>
            </div>
        </form>
    </div>
</div>
<%--加载器--%>
<%--<%@ include file="/WEB-INF/jsp/common/loader.jsp"%>--%>
</body>
<%--页面相关的js引用--%>
<script type="text/javascript"  src="<%=basePath%>static/js/common/wxLogin.js"></script><%--微信外部的js包--%>
<script type="text/javascript"  src="<%=basePath%>static/js/login-register/wechat-login.js"></script><%--本地微信登陆的实例化js--%>
<script type="text/javascript"  src="<%=basePath%>static/js/common/formValidator.js"></script><%--表单验证--%>
<script type="text/javascript"  src="<%=basePath%>static/js/login-register/login.js"></script>

</html>
