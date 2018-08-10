<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html class="no-js" lang="">
<head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="apple-touch-icon" href="apple-touch-icon.png">
    <!-- Place favicon.ico in the root directory-->
    <!-- build:css static/styles/common.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/common.css">
    <!-- endbuild-->
    <script src="<%=basePath%>static/plugin/underscore.min.js"></script>
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <!-- build:css static/styles/index.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/index.css">
    <!-- endbuild-->
    <style type="text/css">
        .form-control, .btn.btn-lg.btn-block.btn-primary {
            height: 44px;
        }

        .form-control::-webkit-input-placeholder {
            color: #d1d1d1;
            opacity: 1;
        }
        .form-control:-ms-input-placeholder {
            color: #d1d1d1;
            opacity: 1;
        }
        .form-control::placeholder {
            color: #d1d1d1;
            opacity: 1;
        }
        a,a:focus, a:hover{
            color: #1896f0;
        }
    </style>
</head>
<body>
<!--[if lt IE 10]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
<nav class="navbar navbar-expand-lg navbar-light">
    <%@ include file="/WEB-INF/jsp/common/mainTop.jsp"%>
</nav>
<div class="tab-pane" id="authorize" role="tabpanel">
    <h2>登录控制台</h2>
    <p>为保障企业信息安全，请再次输入密码进入控制台</p>
    <div class="wrap">
        <form id="authorizeForm">
            <img class="company-logo" src="">
            <h6></h6>
            <input class="form-control form-control-lg"  name="password" type="password" placeholder="请输入登录密码" autofocus="autofocus">
            <div class="text-right"><a href="javascript:void(0);" onclick="forget()">忘记密码？</a></div>
            <p class="error-msg"></p>
            <button class="btn btn-lg btn-block btn-primary" type="submit">登录</button>
            <div class="text-center">现在不想登录了？<a class="btn-close" href="javascript:history.go(-1)">关闭</a></div>
        </form>
    </div>
</div>
<input type="hidden" value="${companyLogo}" id="companyLogo">
<input type="hidden" value="${companyName}" id="companyName">
<!-- build:js static/scripts/index.js-->
<script src="<%=basePath%>static/scripts/index.js"></script>
<script>
    $(function(){
        /*删除标签背景*/
        $(".nav-item").removeClass("flagBackground");
        /*初始化标签背景*/
        $(".nav-item:eq(3)").addClass("flagBackground");
    })
</script>
<!-- endbuild-->
</body>
</html>