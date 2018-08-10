<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<script src="<%=basePath%>static/js/common/top.js"></script>
<ul class="nav">
    <li class="nav-item"><a  href="<%=basePath%>static/pdf/handbook.pdf"  target="view_window"><span class="icon icon-question handbook"></span></a></li>
    <li class="nav-item dropdown">
        <a class="nav-link dropdown-toggle" id="menuTop" href="javascript:void(0);" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"></a>
        <div class="dropdown-menu" aria-labelledby="menu">
            <a class="dropdown-item" href="javascript:void(0);" onclick="personalData()"><i class="icon icon-personaldata"></i>个人资料</a>
            <a class="dropdown-item" href="javascript:void(0);" onclick="logoutConsole()"><i class="icon icon-IDcard"></i>退出控制台</a>
            <a class="dropdown-item" href="javascript:void(0);" onclick="logout()"><i class="icon icon-exit"></i>退出登录</a>
        </div>
    </li>
</ul>
