<%--
  Created by IntelliJ IDEA.
  User: duanwejie
  Date: 2017/5/27
  Time: 13:25
  引用外部类库，js库，在所有页面都要用到的部分，抽离到该文件
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<![endif]–>
<!–[if IE 10]>--%>
<meta http-equiv=”x-ua-compatible” content=”ie=10″ />
<meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
<%--<![endif]–>--%>
<%--
    针对页面动态获取css、js路径的方法
    <%=basePath%>
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%--国际化码--%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page isELIgnored="false"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path+"/";
    String basePath1 = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>
<link rel="shortcut icon" type="image/x-icon" href="<%=basePath%>static/images/yunmeeting-logo.ico">
<link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">
<link rel="stylesheet" href="<%=basePath%>static/styles/dashboard.css">
<script src="<%=basePath%>static/plugin/jquery.min.js"></script>

