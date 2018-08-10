<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html class="no-js">
<head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>企业租户信息</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="shortcut icon" type="image/x-icon" href="<%=basePath%>static/images/yunmeeting-logo.ico">
    <link rel="stylesheet" href="<%=basePath%>static/styles/dashboard.css">
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <link rel="stylesheet" href="<%=basePath%>static/styles/pages.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/tenant.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">
</head>
<body><!--[if lt IE 10]><p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a
        href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p><![endif]-->
<div class="wrapper">
    <%@ include file="/WEB-INF/jsp/common/left.jsp"%>

    <div class="main-panel">
        <nav class="navbar fixed-top flex-row justify-content-between">
            <ul class="tab-business" role="tablist">
                <li class="nav-item"><a class="nav-link"></a></li>
            </ul>
            <%@ include file="/WEB-INF/jsp/common/top.jsp"%>
        </nav>
        <div class="content room">
            <div class="tab-content">
                <div class="tab-pane active" id="home" role="tabpanel">
                        <div class="top">
                            <div class="form-control input-search"><i class="icon icon-search"></i>
                                <input class="version-search" type="text" placeholder="搜索公司名称、联系电话、联系人" maxlength="50"><i class="icon icon-search-del del-searchList" style="display: none;"></i>
                            </div>
                            <%--<form class="search"><input class="form-control form-control-sm" id="search" type="search"--%>
                                                        <%--placeholder="搜索公司名称、联系电话、联系人"></form>--%>
                            <span class="log-count"></span>
                            <%--<div class="dropdown-box personnelType">--%>
                                <%--<button class="select-btn dropdown-toggle" id="personnelTypeMenu" type="button" value="">全部租户(49) </button>--%>
                                <%--<div class="dropdown-ul">--%>
                                    <%--<span class="span-list select-active span-list-0" name="0">全部租户(<i>49</i>)</span>--%>
                                    <%--<span class="span-list span-list-1" name="1">普通租户(<i>20</i>)</span>--%>
                                    <%--<span class="span-list span-list-2" name="2">内测租户(<i>29</i>)</span>--%>
                                <%--</div>--%>
                            <%--</div>--%>
                        </div>
                        <div class="list"></div>
                        <div id="pages">
                            <div class="page-Box" id="page-Box"></div>
                        </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="setBetaModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <div class="modal-title">设置租户类型</div>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
            </div>
            <div class="modal-body">
                <input name="id" type="hidden">
                <label>确定设置</label><span class="blue name"></span><span>为内测租户吗？</span>
            </div>
            <div class="modal-footer">
                <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
                <button class="btn btn-primary setBetaType" type="button">确定</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="errorModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <div class="modal-title">错误信息</div>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span
                        class="icon icon-close" aria-hidden="true"></span></button>
            </div>
            <div class="modal-body">
                <form>
                    <div class="form-group"><label class="form-control-label"><span class="name"></span></label></div>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="<%=basePath%>static/scripts/pages.js"></script>
<script src="<%=basePath%>static/scripts/tenant.js"></script>

</body>
</html>