<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>忘记密码</title>
    <%@ include file="/WEB-INF/jsp/common/head.jsp"%>
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/forget.css">
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/login-register-common.css">
</head>
<body>
<div class="resetPassword">
    <%@ include file="/WEB-INF/jsp/common/login-logo.jsp"%>
    <form>
        <div class="form-title">忘记密码</div>
        <div class="form-group">
            <input class="col-12 form-control" type="text" id="forgetTel" name="tel" value="${phoneNumber}" placeholder="请输入手机号">
        </div>
        <div class="form-group input-group">
            <input class="col-7 form-control" type="text" id="forgetCode" placeholder="请输入验证码" name="code" >
            <input style="margin-left: 28px;" type="button" id="forgetSend" class="col-4 btn btn-secondary" value="获取验证码"/>
        </div>
        <div class="form-group">
            <input class="col-12 form-control" type="text" onfocus="this.type='password'" name="password" id="forgetPwd"  placeholder="请输入新密码" name="pwd" >
        </div>
        <div class="form-group">
            <input class="col-12 form-control" type="text" onfocus="this.type='password'" name="confirmPassWord" id="forgetPwdAgain" placeholder="请再次输入新密码">
        </div>
        <div class="warningBox">
            <i class="fa fa-exclamation-circle"></i>
            <label id="warmLabel"></label>
        </div>
        <button id="forgetSubmit" type="button" class="btn btn-primary form-footer">确认</button>
        <div class="goBack">
            <a href="/system/loginpage">返回登录</a>
        </div>
    </form>
</div>
</body>
<script src="<%=basePath%>static/js/login-register/forget.js"></script>
<script type="text/javascript"  src="<%=basePath%>static/js/common/formValidator.js"></script>
</html>
