<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">
<style type="text/css">
    .active-left {
        background-color: #dde2eb;
        /*border-left: 4px solid #2da8fa;*/
    }
    .select-left {
        background-color: #2da8fa;
        width: 4px;
        height: 50px;
        position: absolute;
    }
</style>

<div class="sidebar">
    <div class="logo"><img src="<%=basePath%>static/images/yunmeeting.png"><h5 style="margin-top: 10px;font-size: 18px;color: #222">企云会综合管理平台</h5></div>
    <ul class="nav flex-column" id="leftMenu">
        <%--<li class="nav-item"><a class="nav-link" style="cursor: pointer" onclick="gotoTenantPage()" ><i--%>
                <%--class="icon icon-kzt-settings"></i><span>企业租户信息</span></a>--%>
        <%--</li>--%>
        <%--<li class="nav-item"><a class="nav-link" style="cursor: pointer" onclick="gotoOrderPage()">--%>
                <%--<i class="icon icon-kzt-staff"></i><span>订单信息管理</span></a>--%>
        <%--</li>--%>
        <%--<sec:authorize ifAnyGranted="系统管理员,管理员,财务人员">--%>
            <%--<li class="nav-item"><a class="nav-link" style="cursor: pointer" onclick="gotoInvoicePage()">--%>
                    <%--<i class="icon icon-kzt-mroom"></i><span>发票开票管理</span></a>--%>
            <%--</li>--%>
        <%--</sec:authorize>--%>
        <%--<sec:authorize ifAnyGranted="系统管理员,管理员,运营人员">--%>
            <%--<li class="nav-item"><a class="nav-link" style="cursor: pointer" onclick="gotoInvoiceSendPage()">--%>
                <%--<i class="icon icon-kzt-order"></i><span>发票寄票管理</span></a>--%>
            <%--</li>--%>
        <%--</sec:authorize>--%>

        <%--<sec:authorize ifAnyGranted="系统管理员">--%>
            <%--<li class="nav-item"><a class="nav-link" style="cursor: pointer" onclick="gotoPermissionPage()">--%>
                <%--<i class="icon icon-kzt-order"></i><span>权限管理</span></a>--%>
            <%--</li>--%>
        <%--</sec:authorize>--%>
        <%--<sec:authorize ifAnyGranted="系统管理员">--%>
            <%--<li class="nav-item"><a class="nav-link" style="cursor: pointer" onclick="gotoRolePage()">--%>
                <%--<i class="icon icon-kzt-order"></i><span>角色管理</span></a>--%>
            <%--</li>--%>
        <%--</sec:authorize>--%>
        <%--<sec:authorize ifAnyGranted="系统管理员">--%>
            <%--<li class="nav-item"><a class="nav-link" style="cursor: pointer" onclick="gotoUserPage()">--%>
                <%--<i class="icon icon-kzt-order"></i><span>用户管理</span></a>--%>
            <%--</li>--%>
        <%--</sec:authorize>--%>
        <%--
        <li class="nav-item"><a class="nav-link" onclick="gotoTenantPage()" ><i
                class="icon icon-settings"></i><span>企业租户配置</span></a>
        </li>
        <li class="nav-item"><a class="nav-link" href="">
            <i class="icon icon-settings"></i><span>平台参数配置</span></a>
        </li>
        <li class="nav-item"><a class="nav-link active" href="">
            <i class="icon icon-settings"></i><span>数据库域配置</span></a>
        </li>--%>

    </ul>
</div>

<script src="<%=basePath%>static/js/common/left.js"></script>
