<!--   页面简介
    获取jsp绝对路径
    1、jsp的写法：<%=request.getContextPath()%>
   2、El表达式的写法：${pageContext.request.contextPath}
-->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--引入C标签库-->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page isELIgnored="false"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path+"/";
    String basePath1 = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<link rel="shortcut icon" type="image/x-icon" href="<%=basePath%>static/images/yunmeeting-logo.ico">