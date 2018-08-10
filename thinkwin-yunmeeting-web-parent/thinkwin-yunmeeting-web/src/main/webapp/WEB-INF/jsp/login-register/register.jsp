<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>企云会-连接组织的智慧</title>
    <%@ include file="/WEB-INF/jsp/common/head.jsp"%>
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/login-register-common.css">
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/register.css">
</head>
<body>
<div class="register-form">
        <%@ include file="/WEB-INF/jsp/common/login-logo.jsp"%>
        <form class="form-horizontal">
            <div class="form-group weChat-logo" id="weChat-register"></div>
            <div class="form-group">
                <input class="col-12 form-control form-control-lg" type="text" id="inputCompany" placeholder="公司名称" maxlength="30"  name="company">
            </div>
            <div class="form-group">
                <input class="col-12 form-control form-control-lg" type="text" id="inputName" placeholder="姓名" maxlength="10" name="name">
            </div>
            <div class="form-group">
                <input class="col-12 form-control form-control-lg" type="tel" id="inputTel" placeholder="手机号" name="tel">
            </div>
            <div class="form-group">
                <input class="form-control form-control-lg get-code-text" type="text" id="inputCode" placeholder="验证码" name="code"><input type="button" id="registerCode" class="btn btn-lg btn-secondary get-code-btn" value="获取验证码"/>
            </div>
            <div class="form-group">
                <input class="col-12 form-control form-control-lg" type="text" onfocus="this.type='password'" id="inputPassword" placeholder="密码 8~20位数字、字母组合" name="pwd">
            </div>
            <div class="warningBox">
                <i class="icon icon-error"></i>
                <label id="warmLabel"></label>
            </div>
            <div class="form-group register" >
                <input id="registerBtn" type="button"  value="注册"  class="col-12 btn btn-lg btn-primary "/><%--type="submit"--%>
            </div>
            <div class="form-group terms-link">
                点击注册，表示您同意我们的
                <a target="_blank" href="/system/termspage">《服务条款》</a>
            </div>
            <div class="backLogin">
                <a href="/system/loginpage">返回登录</a>
            </div>
        </form>
    <input id="uid" type="hidden" value="${uId}">
    <input id="wechatName" type="hidden" value="${name}">
    <input id="wechatPhoto" type="hidden" value="${photo}">
</div>
</body>
<script src="<%=basePath%>static/js/login-register/register.js"></script>
<script src="<%=basePath%>static/js/common/formValidator.js"></script>
</html>



