<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<script src="<%=basePath%>static/js/common/top.js"></script>
<ul class="nav">
    <%--<li class="nav-item"><a class="nav-link" href="javascript:void(0);"><span class="icon icon-question"></span></a></li>--%>
    <li class="nav-item dropdown">
        <a class="nav-link dropdown-toggle" id="menuTop" href="javascript:void(0);" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"></a>
        <div class="dropdown-menu" aria-labelledby="menu">
            <a class="dropdown-item" href="javascript:void(0);" onclick="logout()">退出登录</a>
        </div>
    </li>
</ul>
