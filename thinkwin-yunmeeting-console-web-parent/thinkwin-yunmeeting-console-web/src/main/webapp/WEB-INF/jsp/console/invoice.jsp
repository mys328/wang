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
    <!-- build:css static/styles/index.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/invoice.css">
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
        <div class="content invoice">
          <div class="tab-content" style="height: 100%">
            <div class="table-Box" style="height: 100%"></div>
          </div>
          <div class="page-Box" id="page-Box"></div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="cerModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">发票信息</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <div class="show-invoice-info">
              <div class="flex">
                <label class="col-left">开具类型</label>
                <label class="col-right">个人</label>
              </div>
              <div class="flex">
                <label class="col-left">发票抬头</label>
                <label class="col-right">个人</label>
              </div>
              <div class="flex">
                <label class="col-left">发票类型</label>
                <label class="col-right">增值税普通发票</label>
              </div>
            </div>
            <div class="show-enterprise-common">
              <div class="flex">
                <label class="col-left">开具类型</label>
                <label class="col-right">企业</label>
              </div>
              <div class="flex">
                <label class="col-left">发票抬头</label>
                <label class="col-right" id="show-comname">北京盛科维科技发展有限公司</label>
              </div>
              <div class="flex">
                <label class="col-left">发票类型</label>
                <label class="col-right">增值税普通发票</label>
              </div>
              <div class="flex">
                <label class="col-left">税务证号</label>
                <label class="col-right" id="show-comtax">1110010101010</label>
              </div>
            </div>
            <div class="show-enterprise-special">
              <div class="flex">
                <label class="col-left">开具类型</label>
                <label class="col-right">企业</label>
              </div>
              <div class="flex">
                <label class="col-left">发票抬头</label>
                <label class="col-right" id="show-spename">北京盛科维科技发展有限公司</label>
              </div>
              <div class="flex">
                <label class="col-left">发票类型</label>
                <label class="col-right">增值税专用发票</label>
              </div>
              <div class="flex">
                <label class="col-left">税务证号</label>
                <label class="col-right" id="show-spetax">11000001111</label>
              </div>
              <div class="flex">
                <label class="col-left">开户银行</label>
                <label class="col-right" id="show-speBank">11000001111</label>
              </div>
              <div class="flex">
                <label class="col-left">开户账号</label>
                <label class="col-right" id="show-speacount">11000001111</label>
              </div>
              <div class="flex">
                <label class="col-left">注册地址</label>
                <label class="col-right" id="show-speaddress">北京市海淀区西三旗昌临813号京玺中韩文化创意园A区10号楼104</label>
              </div>
              <div class="flex">
                <label class="col-left">联系电话</label>
                <label class="col-right" id="show-spetel">11000001111</label>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="sureModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">开具发票提醒</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <label>确定为订单<span class="span" id="orderId">1234567</span>开具发票？</label>
          </div>
          <div class="modal-footer">
            <button class="btn cancel-btn" type="button" data-dismiss="modal">取消</button>
            <button class="btn countersign-btn" type="button" id="sure">开具发票</button>
          </div>
        </div>
      </div>
    </div>
    <script src="<%=basePath%>static/scripts/invoice.js"></script>
  </body>
</html>