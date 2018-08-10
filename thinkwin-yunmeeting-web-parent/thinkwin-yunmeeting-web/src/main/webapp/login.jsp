<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--引入C标签库-->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page isELIgnored="false"%>
<html>
<head>
    <title>企云会-连接组织的智慧</title>
    <script type="text/javascript">
        function fromSubmit(){
            $("#userForm").submit();
        }
    </script>
</head>
    <body>
        <div style="border: #cccccc solid 1px ;width: 25%;height: 25%;margin:15% auto;padding: 1%">
            <span>${message}</span>
            <form id="userForm"action="logincheck" method="post"  class="form1">
                username:<input id="username" name=j_username type="text" /><br/><br/>
                password:<input id="password" name=j_password type="password" /><br/><br/>
                <input type="submit" value="login" onclick="fromSubmit()">
               <%-- <input type="button" value="微信登录" onclick="wechatLogin()">--%>
            </form>
            </div>
        </div>
    </body>
</html>
