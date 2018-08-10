<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@include file="/WEB-INF/jsp/common/common.jsp" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会</title>
    <link rel="apple-touch-icon" href="apple-touch-icon.png">
    <!-- build:css static/styles/dashboard.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/dashboard.css">
    <!-- endbuild-->
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <!-- build:css static/styles/versionsLog.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/versionsLog.css">
    <!-- endbuild-->
  </head>
  <body>
  <div class="wrapper">
    <nav class="navbar navbar-expand-lg navbar-light">
      <div class="d-flex flex-row justify-content-end align-items-center"><span>官方电话：400-100-7970</span></div>
    </nav>
    <div class="tab-content">
      <div class="free">
        <div class="title"></div>
        <ul class="list-box">
        </ul>
        <div class="loading-bubbles" id="mine-loading">
          <div class="bubble-container">
            <div class="bubble"></div>
          </div>
          <div class="bubble-container">
            <div class="bubble"></div>
          </div>
          <div class="bubble-container">
            <div class="bubble"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="modal fade" id="synchronization" tabindex="-1" role="dialog" tabindex="-1" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <div class="modal-title">节目版本信息</div>
          <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
        </div>
        <div class="modal-body">
          <label class="form-control-label">节目版本编号<span class="versions-name"></span></label>
          <div class="form-group">
            <textarea class="form-control" placeholder="请输入版本更新内容" maxlength="1000" name="cancelReason"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
          <button class="btn btn-primary save-btn" type="submit">保存</button>
        </div>
      </div>
    </div>
  </div>
    <!-- build:js static/scripts/versionsLog.js-->
    <script src="<%=basePath%>static/scripts/versionsLog.js"></script>
    <!-- endbuild -->
  </body>
</html>