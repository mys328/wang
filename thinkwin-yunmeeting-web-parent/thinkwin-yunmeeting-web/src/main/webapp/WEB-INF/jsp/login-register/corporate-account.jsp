<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2017/5/28
  Time: 14:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>企云会-连接组织的智慧</title>
    <%@ include file="/WEB-INF/jsp/common/head.jsp"%>
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/login-register-common.css">
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/corporate-account.css">
</head>
<body>
    <%--未绑定微信账号--%>
    <div class="bind-account">
        <%@ include file="/WEB-INF/jsp/common/login-logo.jsp"%>
        <p class="unorganized">您当前未加入任何企业</p>
        <p class="invite-join" id="joinText"></p>
        <div class="form-group">
            <a class="btn btn-lg btn-primary col-12 open" id="openBtn"></a>
        </div>
        <div class="form-group">
            <a class="btn btn-lg btn-primary col-12 bind" id="bindBtn"></a>
        </div>
    </div>
    <input id="isRegistState" type="hidden" value="${isRegist}">
    <input id="uid" type="hidden" value="${uId}">
</body>
<script>
    $(function(){
        var _isRegist =$("#isRegistState").val();
        var _uid=$("#uid").val();
        if(_isRegist==1){
            $("#joinText").text("请让管理员邀请加入或创建一个新企业");
            $("#openBtn").text("创建一个新企业");
            $("#openBtn").attr("href",'/system/createenterprispage?uId='+_uid);
            $("#bindBtn").text("返回登录");
            $("#bindBtn").attr("href",'/system/loginpage');
        }else{
            $("#joinText").text("请让管理员邀请加入或开通企云会会议系统");
            $("#openBtn").text("开通企云会会议系统");
            $("#openBtn").attr("href",'/system/registerpage?uId='+_uid);
            $("#bindBtn").text("绑定已有账号");
            $("#bindBtn").attr("href",'/system/bindaccountpage?uId='+_uid);
        }
    })
</script>
</html>
