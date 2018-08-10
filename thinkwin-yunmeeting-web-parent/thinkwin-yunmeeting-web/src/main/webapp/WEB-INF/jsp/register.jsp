<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--引入C标签库-->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page isELIgnored="false"%>
<html>
<head>
    <title>注册</title>
</head>
<body>
<div style="border: #cccccc solid 1px ;width: 25%;height: 25%;margin:15% auto;padding: 1%">
    <span>${message}</span>
    <form action="/yunmeeting/system/regist" method="post" class="form1">
        公司:<input name="tenantName" type="text" /><br/><br/>
        username:<input name="userName" type="text" /><br/><br/>
        password:<input name="password" type="password" /><br/><br/>
        <input type="submit" value="login">
    </form>
</div>
</body>
</html>
