<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
	<meta name="format-delection" content="telephone=no"/>
	<meta name="format-delection" content="email=no"/>
	<link rel="stylesheet" type="text/css" href="/static/css/register/style.css">
	<title>成功</title>
</head>
<body>
<div class="box">
    <img class="logo" src="/assets/images/logo.png">
    <div class="alert-icon"><img src="/static/images/wx-ture.png"></div>
    <div class="alert-message">绑定成功！<br/>今后可以用微信快速登录</div>
    <a href="javascript:WeixinJSBridge.call('closeWindow');" class="weui-btn weui-btn_primary"><div class="sign">确定</div></a>
    <div class="alert-hint">您可以在微信中预订、查询会议，或者在浏览器中登录<a href="http://www.yunmeeting.com" >http://www.yunmeetings.com</a>使用。</div>
</div>
</body>
</html>