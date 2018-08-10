<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
 	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
	<meta name="format-delection" content="telephone=no"/>
	<meta name="format-delection" content="email=no"/>
	<link rel="stylesheet" type="text/css" href="/static/css/register/style.css">
	<title>验证码</title>
</head>
<body>
<div class="box inviteCode">
    <img class="logo" src="/assets/images/logo.png">
    <input type="hidden" value="${openId}" id="openId" />
    <input type="hidden" value="${tenantId}" id="tenantId"/>

    <div class="invitationDiv">请输入邀请码</div>
    <div class="ipt-box-nick mb15-nick">
        <input type="tel" readonlyunselectable="on"
               maxlength="6" onkeyup="this.value=this.value.replace(/[^\d]/g,'') " class="ipt-real-nick"/>
        <div class="ipts-box-nick">
            <div class="ipt-fake-box">
                <input type="text" style="margin-left: 0">
                <input type="text" >
                <input type="text" >
                <input type="text" >
                <input type="text" >
                <input type="text" style="margin-right: 0">
            </div>
        </div>
    </div>
    <div class="invitationerrorDiv"><img src="/static/images/wx-error.png"><span></span></div>
    <button class="forbidden" id="confirmbtn">确定</button>
</div>
<script src="/static/js/jquery-1.8.3.js"></script>
<script src="/static/js/register/invite.js"></script>
</body>
</html>