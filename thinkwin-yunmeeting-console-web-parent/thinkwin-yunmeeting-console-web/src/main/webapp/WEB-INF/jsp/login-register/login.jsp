<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录</title>
    <%@ include file="/WEB-INF/jsp/common/head.jsp"%>
    <link rel="shortcut icon" type="image/x-icon" href="<%=basePath%>static/images/yunmeeting-logo.ico">
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/login.css">
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/login-register-common.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">
    <script type="text/javascript" src="<%=basePath%>static/js/login-register/gVerify.js"></script>
</head>
<body>
<div class="form-group form" id="form">
    <%@ include file="/WEB-INF/jsp/common/login-logo.jsp"%>
    <div class="container" id="login_container"></div>
    <div class="account-login" id="account_login">
        <form loginForm="ngForm">
            <div class="form-group">
                <input id="phoneNumber"  class="form-control" type="tel" placeholder="请输入用户名"/>
            </div>
            <div class="form-group password-input">
                <input type="text" onfocus="this.type='password'" class="form-control" id="inputPassword"  placeholder="请输入密码"/>
            </div>
            <div class="form-group verify-input row">
                <div class="col-sm-6">
                    <input type="text" class="form-control" id="inputCode"  placeholder="请输入验证码"/>
                </div>
                <div class="col-sm-5" id="container">

                </div>
            </div>
            <div class="form-group warningBox">
                <i class="fa fa-exclamation-circle"></i>
                <label id="warmLabel"></label>
            </div>
            <div class="form-group">
                <input type="button" id="submitBtn"  class="btn btn-primary login-btn" value="登录" />
            </div>
            <div class="losed-psd">
                <%--<a href="javascript:void(0);" onclick="forget()">忘记密码?</a>--%>
            </div>
        </form>
    </div>
</div>
<%--加载器--%>
<%--<%@ include file="/WEB-INF/jsp/common/loader.jsp"%>--%>
</body>
<%--页面相关的js引用--%>
<script type="text/javascript"  src="<%=basePath%>static/js/common/formValidator.js"></script><%--表单验证--%>
<script type="text/javascript"  src="<%=basePath%>static/js/login-register/login.js"></script>
</html>
