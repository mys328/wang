<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html class="no-js" lang="">
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
      <link rel="stylesheet" href="<%=basePath%>static/styles/troubleRemoval.css">
  </head>
  <body><!--[if lt IE 10]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
      <nav class="navbar navbar-expand-lg navbar-light">
          <div class="d-flex flex-row justify-content-end align-items-center"><span>官方电话：400-100-7970</span></div>
      </nav>
    <div class="tab-content">
      <div class="free">
        <div class="title">软件更新故障排除</div>
        <div class="content-top">
          <div class="questiontitle">为保证软件更新正常，请确保在进行软件升级过程中网络通畅稳定，会议显示终端处于在线状态，如遇问题请根据实际情况进行排查。</div>
          <div class='questioncontent'>
          <div class='punctuation'>·</div>
          <div>问题：终端版本升级失败。</div>
          <div>原因：网络不稳定造成的下载或安装未完成。</div>
          <div>解决方案：检测查看网络环境是否正常，会议显示终端是否处于在线状态，企云会的其他功能是否可用。排除以上原因可再次重试升级。</div>
        </div>
        <div class='questioncontent'>
          <div class='punctuation'>·</div>
          <div>问题：更新后仍显示之前的版本号。</div>
          <div>原因：更新完成后未刷新状态。</div>
          <div>解决方案：可刷新页面查看是否是最新的版本。</div>
        </div>
        </div>
      </div>
    </div>
  </body>
</html>