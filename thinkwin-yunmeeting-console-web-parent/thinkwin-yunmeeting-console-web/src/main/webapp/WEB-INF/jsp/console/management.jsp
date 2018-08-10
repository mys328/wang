<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title></title>
    <link rel="apple-touch-icon" href="apple-touch-icon.png">
    <!-- Place favicon.ico in the root directory-->
    <!-- build:css static/styles/dashboard.css-->
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="stylesheet" href="<%=basePath%>static/styles/pages.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/dashboard.css">
    <!-- endbuild-->
    <script src="<%=basePath%>static/plugin/underscore.min.js"></script>
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <script src="<%=basePath%>static/scripts/pages.js"></script>
    <link rel="stylesheet" href="<%=basePath%>static/styles/management.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/label.css">
  </head>
  <body><!--[if lt IE 10]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
    <div class="wrapper">
      <%@ include file="/WEB-INF/jsp/common/left.jsp"%>
      <div class="main-panel">
        <nav class="navbar fixed-top navbar-expand-lg navbar-light">
          <div class="d-flex flex-row justify-content-between align-items-center">
            <ul class="tab-business" id="managementNav" role="tablist"><a class="nav-link" data-toggle="tab" href="#systemConfig" role="tab">系统配置</a><a class="nav-link active" data-toggle="tab" href="#operationConfig" role="tab">业务配置</a></ul>
            <ul class="nav">
              <li class="nav-item"><a class="nav-link" href="#"><span class="icon icon-question"></span></a></li>
              <li class="nav-item dropdown"><a class="nav-link dropdown-toggle" id="menu" href="#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="nophoto">管理</span><span>系统管理员</span></a>
                <div class="dropdown-menu" aria-labelledby="menu"><a class="dropdown-item" href="/logout"><i class="icon icon-exit"></i>退出登录</a></div>
              </li>
            </ul>
          </div>
        </nav>
        <div class="content management">
          <div class="tab-content">
            <div class="tab-pane" id="systemConfig" role="tabpanel" aria-expanded="false">
              <div class="nothing">暂无可配置项</div>
            </div>
            <div class="active tab-pane" id="operationConfig" role="tabpanel" aria-expanded="false"> 
              <div class="operation-content">
                <div class="nothing">暂无可配置项</div>
                <div class="content-box weatherConfig">
                  <div class="left">
                    <div class="form-group title">会议显示终端节目管理天气组件配置</div>
                    <div class="form-group desc">用于带天气节目获取当地天气数据</div>
                  </div>
                  <div class="right">
                    <button class="btn btn-outline-primary set-weather" type="button">修改配置</button>
                  </div>
                </div>
                <div class="content-box terminalBackgrounp">
                  <div class="left">
                    <div class="form-group title">会议显示默认终端背景图</div>
                    <div class="form-group desc">用于租户已注册终端显示列表</div>
                  </div>
                  <div class="right">
                    <button class="btn btn-outline-primary set-image" type="button">修改配置   </button>
                  </div>
                </div>
                <div class="content-box appConfig">
                  <div class="left">
                    <div class="form-group title">app下载链接配置</div>
                    <div class="form-group desc">租户扫描此二维码下载终端管理APP，完成终端的注册</div>
                  </div>
                  <div class="right">
                    <button class="btn btn-outline-primary set-app" type="button">修改配置</button>
                  </div>
                </div>
                <form class="config-box" data-toggle="validator" method="post" enctype="multipart/form-data">
                  <div class="inner-box weather">
                    <div class="form-group">
                      <h3>节目管理天气组件配置</h3>
                    </div>
                    <div class="form-group">
                      <label class="title">天气组件key值</label><input class="form-control form-control-lg" type="text" placeholder="填写key值" name="weather" data-validate="name" maxlength="500">
                      <button class="btn btn-primary save save-weather" type="button">修改配置</button>
                    </div>
                  </div>
                  <div class="inner-box image">
                    <div class="form-group">
                      <h3>会议显示默认终端背景图</h3>
                    </div><div class="form-group row pictureBox">
                    <span class="picture-title">默认终端背景图</span>
                    <p class="tips">请上传小于5M的图片，限定尺寸1920x1080</p>
                    <span class="btn-change-photo">重新上传
<label class="change-photo-btn label"><input class="change-photo-btn upload" type="file" accept="image/jpg,image/png,image/jpeg,image/bmp" style="display:none;"></label></span>
                    <div class="col-12">
                      <div class="photo"><img id="bgc_image"  src="<%=basePath%>static/images/default-bg@1x.png" onerror="this.src='<%=basePath%>static/images/default-bg@1x.png'">
                      </div>
                    </div>
                    <button class="btn btn-primary save save-image" type="button">保 存</button>
                  </div>
                 </div>
                 <div class="inner-box appDownload">
                    <div class="form-group">
                      <h3>app下载链接配置</h3>
                    </div>
                    <div class="form-group">
                      <label class="title">app下载地址</label><input class="form-control form-control-lg" type="text" placeholder="填写接口url" name="app" data-validate="name" maxlength="500">
                      <button class="btn btn-primary save save-app" type="button">修改配置</button>
                    </div>
                  </div>
                 <div class="warningBox">
                    <i class="icon icon-error"></i>
                    <label id="warmLabel">配置项的值不能为空</label>
                 </div>
                </form>
              </div>
            </div>
          </div>
        </div>
        <script src="<%=basePath%>static/scripts/label.js"></script>
        <script src="<%=basePath%>static/scripts/management.js"></script>
      </div>
    </div>
  </body>
</html>