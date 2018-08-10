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
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="shortcut icon" type="image/x-icon" href="<%=basePath%>static/images/yunmeeting-logo.ico">
    <!-- Place favicon.ico in the root directory-->
    <!-- build:css static/styles/dashboard.css-->
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
    <!-- build:css static/styles/role.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/role.css">
    <!-- endbuild-->
  </head>
  <body><!--[if lt IE 10]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
    <div class="wrapper">
      <%@ include file="/WEB-INF/jsp/common/left.jsp"%>
      <div class="main-panel">
        <nav class="navbar fixed-top flex-row justify-content-between">
          <ul class="tab-business" role="tablist">
            <li class="nav-item"></li>
          </ul>
          <%@ include file="/WEB-INF/jsp/common/top.jsp"%>
        </nav>
        <div class="content">
          <div class="tab-content" style="height: 100%">
            <div class="top">
              <div class="form-control input-search"><i class="icon icon-search"></i>
                <input id="search" type="text" placeholder="搜索角色名称"><i class="icon icon-search-del" id="del-searchList"></i>
              </div>
              <button class="btn btn-primary btn-sm create-btn" data-toggle="modal" data-target="#deleteModal">创建角色</button>
            </div>
            <div class="table-Box"></div>
          </div>
          <div class="page-Box" id="page-Box"></div>
        </div>
        <div class="invoice-info-box show-box">
          <div class="inner-box show-box">
            <h3 class="show-box title">创建角色</h3>
            <div class="form-group show-box">
              <label class="show-box">角色ID</label>
              <input class="show-box form-control" id="roleid" type="text" placeholder="请填写角色ID" maxlength="100">
            </div>
            <div class="form-group show-box">
              <label class="show-box">角色名称</label>
              <input class="show-box form-control" id="rolename" type="text" placeholder="请填写角色名称" maxlength="100">
            </div>
            <div class="form-group show-box">
              <span id="errormsg"></span>
            </div>
            <button class="show-box btn btn-clear-secondary cancel-btn" type="button" data-dismiss="modal">取消</button>
            <button class="show-box btn btn-primary countersign-btn" type="button">保存并继续  </button>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="authorizeModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">分配权限</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <div class="role-info">
              <label class="role-id">角色ID：100388</label>
              <label class="role-name">角色名称：财务人员</label>
            </div>
            <div class="role-operate">
              <div class="un-authorize">
                <label>未授权</label>
                <div class="ul" id="unauthorize"></div>
              </div>
              <div class="operate">
                <label>操作</label>
                <div class="ul">
                  <div class="operate-btn" data-type="0" id="select-right">选中添加到右边</div>
                  <div class="operate-btn" data-type="1" id="all-right">全部添加到右边</div>
                  <div class="operate-btn" data-type="2" id="select-left">选中添加到左边</div>
                  <div class="operate-btn" data-type="3" id="all-left">全部添加到左边</div>
                </div>
              </div>
              <div class="authorize">
                <label>已授权</label>
                <div class="ul" id="authorize"></div>
              </div>
              <label id="error"></label>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn btn-primary" id="submitAuth" type="button">确定</button>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="delModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">删除角色</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <label>确定删除该角色吗？</label>
          </div>
          <div class="modal-footer">
            <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn btn-danger" id="delRole" type="button">删除</button>
          </div>
        </div>
      </div>
    </div>
    <script src="<%=basePath%>static/scripts/role.js"></script>
  </body>
</html>