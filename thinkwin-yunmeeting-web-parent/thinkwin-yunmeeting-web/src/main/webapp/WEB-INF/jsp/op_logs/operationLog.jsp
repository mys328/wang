<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html class="no-js">
<head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>

    <link rel="stylesheet" href="<%=basePath%>static/styles/dashboard.css">
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <link rel="stylesheet" href="<%=basePath%>static/styles/zTreeStyle.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/operationLog.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/pages.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">
</head>
<body><!--[if lt IE 10]><p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a
        href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p><![endif]-->
<div class="wrapper">
    <%@ include file="/WEB-INF/jsp/common/left.jsp"%>

    <div class="main-panel">
        <nav class="navbar fixed-top flex-row justify-content-between">
            <ul class="tab-business" role="tablist">
                <li class="nav-item"><a class="nav-link">操作日志</a></li>
            </ul>
            <%@ include file="/WEB-INF/jsp/common/top.jsp"%>
        </nav>
        <div class="content room">
            <div class="tab-content">
                <div class="tab-pane active" id="home" role="tabpanel">
                    <div class="left">
                        <div class="treeBox">
                            <ul class="ztree" id="treeDemo"></ul>
                        </div>
                    </div>
                    <div class="right">
                        <div class="top">
                            <form class="search">
                                <div class="form-control input-search">
                                    <i class="icon icon-search"></i>
                                    <input id="search" type="text" maxlength="100" placeholder="搜索操作者、内容、操作IP、操作时间">
                                    <i class="icon icon-search-del" id="del-searchList"></i>
                                </div>
                            </form>
                            <span class="log-count"></span>
                            <button class="btn btn-primary btn-sm clear-btn" data-toggle="modal"
                                    data-target="#deleteModal">清除记录
                            </button>
                        </div>
                        <div class="table-title">
                            <table class="table table-hover list-rooms">
                                <thead>
                                    <tr>
                                        <th class="table-1">业务类型</th>
                                        <th class="table-2">事件</th>
                                        <th class="table-3">日志内容</th>
                                        <th class="table-4">备注</th>
                                        <th class="table-5">IP</th>
                                        <th class="table-6">操作人</th>
                                        <th class="table-7">操作时间</th>
                                    </tr>
                                </thead>
                            </table>
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
</div>

<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <div class="modal-title">清除记录</div>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span
                        class="icon icon-close" aria-hidden="true"></span></button>
            </div>
            <div class="modal-body">
                <form>
                    <div class="form-group"><label class="form-control-label">确定要清除所有记录吗？</label></div>
                </form>
            </div>
            <div class="modal-footer">
                <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
                <button class="btn btn-lg btn-danger btn-clear" type="button">清除</button>
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
<script src="<%=basePath%>static/plugin/jquery.ztree.all.min.js"></script>
<script src="<%=basePath%>static/scripts/pages.js"></script>
<script src="<%=basePath%>static/scripts/operationLog.js"></script>
</body>
</html>