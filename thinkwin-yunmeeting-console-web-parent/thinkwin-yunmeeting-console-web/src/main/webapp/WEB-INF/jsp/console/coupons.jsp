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
    <link rel="apple-touch-icon" href="<%=basePath%>apple-touch-icon.png">
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
    <!-- build:css static/styles/coupons.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/datetimepicker.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/coupons.css">
    <!-- endbuild-->
  </head>
  <body><!--[if lt IE 10]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
    <div class="wrapper">
      <%@ include file="/WEB-INF/jsp/common/left.jsp"%>
      <div class="main-panel">
        <nav class="navbar fixed-top navbar-expand-lg navbar-light">
          <div class="d-flex flex-row justify-content-between align-items-center">
            <ul class="tab-business"> <a class="nav-link" data-toggle="tab" href="#list" role="tab">优惠券管理</a></ul>
            <%@ include file="/WEB-INF/jsp/common/top.jsp"%>
          </div>
        </nav>
        <div class="content">
          <div class="tab-content">
            <div class="tab-pane active" id="list" role="tabpanel">
              <div class="top">
                <div class="form-control input-search"><i class="icon icon-search"></i>
                  <input id="search" type="text" placeholder="搜索编号、名称、优惠值"><i class="icon icon-search-del" id="del-searchList"></i>
                </div>
                <button class="btn btn-primary btn-sm create-btn" data-toggle="modal" data-target="#deleteModal">特权优惠券</button>
              </div>
              <div class="table-head-box">
                <table class="table table-head">
                  <thead>
                    <tr>
                      <th class="table-1" data-toggle="tooltip" data-title="编号"><span>编号</span></th>
                      <th class="table-2" data-toggle="tooltip" data-title="优惠券名称"><span>优惠券名称</span></th>
                      <th class="table-3" data-toggle="tooltip" data-title="类型"><span>类型</span></th>
                      <th class="table-4" data-toggle="tooltip" data-title="优惠值"><span>优惠值</span></th>
                      <th class="table-5" data-toggle="tooltip" data-title="使用限制"><span>使用限制</span></th>
                      <th class="table-6" data-toggle="tooltip" data-title="有效时间"><span>有效时间</span></th>
                      <th class="table-7" data-toggle="tooltip" data-title="发放方式"><span>发放方式</span></th>
                      <th class="table-8" data-toggle="tooltip" data-title="发放状态"><span>发放状态</span></th>
                      <th class="table-9" data-toggle="tooltip" data-title="使用状态"><span>使用状态</span></th>
                      <th class="table-10" data-toggle="tooltip" data-title="操作"><span>操作</span></th>
                    </tr>
                  </thead>
                </table>
              </div>
              <div id="listBox">
                <div class="table-body"></div>
              </div>
              <div class="page-Box" id="page-Box"></div>
            </div>
            <div class="tab-pane" id="details" role="tabpanel"></div>
          </div>
        </div>
        <form class="invoice-info-box" data-toggle="validator" method="post" enctype="multipart/form-data">
          <div class="inner-box">
            <h3 class="title">创建特权优惠券</h3>
            <div class="form-group">
              <label>名称</label><input class="form-control" type="text" name="ticketName" placeholder="推荐输入活动相关名称" maxlength="50" data-validate="ticketName">
            </div>
            <div class="form-group">
              <label>有效时间</label><input class="form-control time" type="text" name="startTime" placeholder="开始时间" data-validate="startTime"><span class="time-text">至</span><input class="form-control time" type="text" name="endTime" placeholder="结束时间" data-validate="endTime">
            </div>
            <div class="form-group">
              <label class="vlaue-label">优惠值</label>
              <div class="control-group">
                <div class="value-list"><span>赠送</span><input class="form-control" type="int" placeholder="无优惠" name="person" data-id="" data-validate="person" data-max="999" data-min="1"><span>个</span><span>员工数</span>
                </div>
                <div class="value-list"><span>赠送</span><input class="form-control" type="int" placeholder="无优惠" name="room" data-validate="room" data-max="999" data-min="1"><span>间</span><span>会议室</span>
                </div>
                <div class="value-list"><span>赠送</span><input class="form-control" type="int" placeholder="无优惠" name="space" data-validate="space" data-max="999" data-min="1"><span>GB</span><span>存储空间</span>
                </div>
                <div class="value-list"><span>赠送</span><input class="form-control" type="int" placeholder="无优惠" name="duration" data-validate="duration" data-max="999" data-min="1"><span>年</span><span>时长</span>
                </div>
                <div class="value-list"><span>赠送</span><input class="form-control" type="int" placeholder="无优惠" name="discount" data-validate="discount" data-max="99" data-min="0"><span>折</span><span>折扣</span><span class="label-value">0-99的整数</span>
                </div>
              </div>
            </div>
            <p class="error-msg"></p>
            <button class="btn btn-primary publish-btn form-btn" type="submit">发布</button><button class="btn btn-clear-secondary save-btn form-btn" type="button">保存</button>
          </div>
        </form>
      </div>
    </div>
    <div class="modal fade" id="delModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">删除优惠券</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <label>确定删除所选优惠券吗？</label>
          </div>
          <div class="modal-footer">
            <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn btn-danger btn-affirm-delete" type="button">删除</button>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="disabledModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">使失效</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <label>确定使所选优惠券失效吗？</label>
          </div>
          <div class="modal-footer">
            <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button><button class="btn btn-danger btn-affirm-invalidate" type="button">使失效</button>
          </div>
        </div>
      </div>
    </div>
    <script src="<%=basePath%>static/scripts/plugins/datetimepicker.js"></script>
    <script src="<%=basePath%>static/scripts/coupons.js"></script>
  </body>
</html>