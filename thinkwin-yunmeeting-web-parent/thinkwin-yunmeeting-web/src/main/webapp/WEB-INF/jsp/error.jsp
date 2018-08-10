<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="shortcut icon" type="image/x-icon" href="<%=basePath%>static/images/yunmeeting-logo.ico">
    <style>
        .logo{
            text-align: center;
            font-size:28px;
            color:#696969;
        }
        .logo i{
            color:#1896f0;font-size:45px;
            vertical-align: middle;
        }
        .text {
            margin: 30px 0;
        }
        span {
            text-align: center;
            font-size: 20px;
            color: #333;
            margin: 30px 0;
        }
        button {
            width: 120px;
            height: 40px;
            background-color: #2da8fa;
            color: #fff;
            font-size: 14px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        button:focus{
            outline: none;
        }
    </style>
    <script>
        function gotoLogin() {
            window.location.href="/system/loginpage";
        }
    </script>
</head>
<body style="text-align: center">
<div class="logo">
    <img src="<%=basePath%>static/images/yunmeeting-logo.png">
</div>
<div class="text">
    <span>您的请求被服务器拒绝了，请重新登录！</span>
</div>
<button onclick="gotoLogin()">返回登录</button>
</body>
</html>