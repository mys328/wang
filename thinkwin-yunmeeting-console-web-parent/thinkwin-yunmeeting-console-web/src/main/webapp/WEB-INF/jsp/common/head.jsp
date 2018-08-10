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
    <%--css（cdn）--%>
<%--bootstrap.css-v4.0.0-alpha.6--%>
<link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css">
<%--FontAwesome.css 4.7.0 --%>
<%--FontAwesome.svg 4.7.0.--%>
    <%--js（cdn）--%>
<%--jueryv-v3.2.1--%>
<script type="text/javascript" src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
<%--bootstrap.js的依赖文件tether.js--%>
<script src="https://cdnjs.cloudflare.com/ajax/libs/tether/1.4.0/js/tether.min.js" integrity="sha384-DztdAPBWPRXSA/3eYEEUWrWCy7G5KFbe8fFjk5JAIxUYHKkDx6Qin1DkWx51bBrb" crossorigin="anonymous"></script>
<%--bootstrap.js-v4.0.0-alpha.6--%>
<script type="text/javascript" src="https://cdn.bootcss.com/bootstrap/4.0.0-alpha.6/js/bootstrap.min.js"></script>
