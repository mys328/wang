<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>企云会-连接组织的智慧</title>
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
            <input class="col-12 form-control form-control-lg" type="text" id="forgetTel" name="tel" value="${phoneNumber}" placeholder="手机号">
        </div>
        <div class="form-group">
            <input class="form-control form-control-lg get-code-text" type="text" id="forgetCode" placeholder="验证码" name="code" /><input type="button" id="forgetSend" class="btn btn-lg btn-secondary get-code-btn" value="获取验证码"/>
        </div>
        <div class="form-group">
            <input class="col-12 form-control form-control-lg" type="text" onfocus="this.type='password'" name="password" id="forgetPwd"  placeholder="密码 ８~20位数字、字母组合" name="pwd" >
        </div>
        <div class="form-group">
            <input class="col-12 form-control form-control-lg" type="text" onfocus="this.type='password'" name="confirmPassWord" id="forgetPwdAgain" placeholder="再次输入">
        </div>
        <div class="warningBox">
            <i class="icon icon-error"></i>
            <label id="warmLabel"></label>
        </div>
        <button id="forgetSubmit" type="button" class="btn btn-lg btn-primary form-footer">确定</button>
        <div class="goBack">
            <a href="/system/loginpage">返回登录</a>
        </div>
    </form>
</div>
</body>
<script src="<%=basePath%>static/js/login-register/forget.js"></script>
<script type="text/javascript"  src="<%=basePath%>static/js/common/formValidator.js"></script>
</html>
