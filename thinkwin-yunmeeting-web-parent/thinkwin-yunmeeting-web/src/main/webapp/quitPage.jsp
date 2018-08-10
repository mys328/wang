<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>企云会-连接组织的智慧</title>
    <link rel="shortcut icon" type="image/x-icon" href="static/images/yunmeeting-logo.ico">
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
        span {
            text-align: center;
            font-size: 20px;
            color: #333;
            margin: 30px 0;
        }
        .text {
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
    <img src="static/images/yunmeeting-logo.png">
</div>
<div class="text">
    <span>您在当前企业下是离职状态,去加入新的企业吧！</span>
</div>
<button onclick="gotoLogin()">返回登录</button>
</body>
</html>