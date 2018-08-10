<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/WEB-INF/jsp/common/common.jsp" %>
<script src="<%=basePath%>static/plugin/jquery.min.js"></script>
<script src="<%=basePath%>static/js/common/access.js"></script>
<link rel="shortcut icon" type="image/x-icon" href="<%=basePath%>static/images/yunmeeting-logo.ico">
<link rel="stylesheet" href="<%=basePath%>static/styles/dashboard.css">
<!-- endbuild-->
<script src="<%=basePath%>static/plugin/soda.min.js"></script>
<script src="<%=basePath%>static/plugin/moment.min.js"></script>
<script src="<%=basePath%>static/plugin/jquery.min.js"></script>
<script src="<%=basePath%>static/plugin/tether.min.js"></script>
<script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
<script src="<%=basePath%>static/scripts/config.js"></script>
<html>
<head>
    <title>企云会-连接组织的智慧</title>
</head>
<body>
    <font style="text-align: center "><h1>权限不足！</h1></font>
    <div class="modal fade" id="removeModal" tabindex="-1" role="dialog" aria-labelledby="removeModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header title-box">
                    <h5 class="modal-title" id="removeModalLabel">权限不足</h5>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                </div>
                <div class="modal-body"> <span>您的权限不足！请重新登录吧！！！</span></div>
                <div class="modal-footer">
                    <button class="btn btn-secondary" type="button" data-dismiss="modal">取消</button>
                    <button class="btn btn-primary remove-btn" type="button" data-dismiss="modal" onclick="tiaozhuan()">确定</button>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
