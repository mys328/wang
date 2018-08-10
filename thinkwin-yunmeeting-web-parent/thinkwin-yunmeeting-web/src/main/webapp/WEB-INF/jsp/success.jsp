<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--引入C标签库-->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page isELIgnored="false"%>
<html>
<head>
    <title>登录成功</title>
    <meta name="viewport" content="width=device-width,user-scalable=0">
    <style type="text/css">
        *{margin:0; padding:0}
        table{border:1px dashed #B9B9DD;font-size:12pt}
        td{border:1px dashed #B9B9DD;word-break:break-all; word-wrap:break-word;}
    </style>
</head>
<body>
<span>账号：${userLogin.userName}</span>
<span>姓名：${userLogin.userName}</span>
</body>
</html>