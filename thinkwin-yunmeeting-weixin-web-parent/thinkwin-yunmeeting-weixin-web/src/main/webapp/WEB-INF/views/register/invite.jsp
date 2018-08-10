<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="en">
<head>
 	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
	<meta name="format-delection" content="telephone=no"/>
	<meta name="format-delection" content="email=no"/>
	<link rel="stylesheet" type="text/css" href="/static/css/register/style.css">
	<title>注册</title>
</head>
<body>
<div class="box invite">
    <div class="verify">
        <img class="logo" src="/assets/images/logo.png">
        <div class="nameDiv">
            <input type="text" placeholder="姓名" class="nameInput" maxlength="10">
        </div>
        <div class="departmentDiv">
            <p>${tenantName}</p>
        </div>
        <div class="phoneDiv">
            <input type="text" placeholder="手机号" onkeyup="this.value=this.value.replace(/\D/g,'')" class="phoneInput" maxlength="11">
        </div>
        <div class="verifyDiv">
            <input type="text"  placeholder="验证码" onkeyup="this.value=this.value.replace(/\D/g,'')" class="verifyInput">
        </div>
        <input type="button" id="bth" class="bth" value="获取验证码">
        <div class="pwdDiv">
            <input type="password" placeholder="密码 8 ~ 20位数字、字母组合"  class="passwordInput">
        </div>
        <div class="errorAlert"><img src="/static/images/wx-error.png"><span>手机号不能为空</span></div>
        <div class="sign">下一步</div>
        <input type="hidden" value="${openId}" id="openId" />
        <input type="hidden" value="${tenantId}" id="tenantId"/>
        <input type="hidden" value="${tenantName}" id="tenantName"/>
    </div>
</div>
</body>
<script src="/static/js/jquery-1.8.3.js"></script>
<script src="/static/js/register/invite.js"></script>
</html>