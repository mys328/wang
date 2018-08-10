<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/jsp/common/common.jsp" %>
<!DOCTYPE html>
<html class="no-js" lang="">
<head>
  <meta charset="utf-8">
  <meta name="description" content="">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>企云会-连接组织的智慧</title>
  <link rel="apple-touch-icon" href="apple-touch-icon.png">
  <!-- Place favicon.ico in the root directory-->
  <!-- build:css static/styles/common.css-->
  <link rel="stylesheet" href="<%=basePath%>static/styles/common.css">
  <!-- endbuild-->
  <script src="<%=basePath%>static/plugin/underscore.min.js"></script>
  <script src="<%=basePath%>static/plugin/soda.min.js"></script>
  <script src="<%=basePath%>static/plugin/moment.min.js"></script>
  <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
  <script src="<%=basePath%>static/plugin/tether.min.js"></script>
  <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
  <script src="<%=basePath%>static/scripts/config.js"></script>
  <!-- build:css static/styles/orderDetail.css-->
  <link rel="stylesheet" href="<%=basePath%>static/styles/orderDetail.css">
  <!-- endbuild-->
</head>
<body><!--[if lt IE 10]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
<nav class="navbar navbar-expand-lg navbar-light">
  <div class="d-flex flex-row justify-content-end align-items-center"><span>官方电话：400-100-7909</span></div>
</nav>
<div class="tab-content">
  <div class="free">
    <div class="title">订单</div>
    <div class="content-top">
      <div class="top-left">
        <div class="order-number"><span>订单编号</span><span class="order-number-text"></span></div>
        <div class="order-time" style="display:flex;"><span>订购时间</span>
          <div class="time-box">
            <p class="order-time-text"></p>
            <p class="countdown"></p>
          </div>
        </div>
        <div class="order-state"><span>订单状态</span>
          <div class="order-state-box"><span class="order-state-text"></span>
            <div class="proof-explain">请上传汇款凭证，以便我们尽快确认您的付款，如有任何问题请及时与我们联系。<br/>如需其他支付方式，请取消此订单，重新下单。</div>
          </div>
        </div>
      </div>
      <div class="top-right">
        <button class="btn btn-lg btn-primary pay-btn">立即支付</button>
        <form id="myfile" style="display: inline-block;" enctype="multipart/form-data"><span class="btn btn-lg btn-primary btn-up-photo">上传凭证
              <input type="file" accept="image/jpg,image/png,image/jpeg,image/bmp" name="fileName"></span></form>
        <button class="btn btn-lg btn-outline-info cancel-order">取消订单</button>
        <a class="check-proof text-center" href="#" data-toggle="modal" data-target="#previewModal">查看汇款凭证</a>
      </div>
    </div>
    <div class="content-table">
      <div class="title order-ditail">订单详情</div>
      <div class="table-box">
      </div>
    </div>
    <div class="content-footer">
      <p>如有疑问，请联系我们</p>
      <p>电话：400-100-7909    </p>
    </div>
  </div>
</div>
<div class="modal fade" id="previewModal" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static">
  <div class="modal-dialog" role="document">
    <div class="modal-content proof-box">
      <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
      <div class="modal-body"><img src=""></div>
    </div>
  </div>
<input id="orderId" type="hidden" value="${model.orderId}" />
<!-- build:js static/scripts/orderDetail.js--></div>

<script src="<%=basePath%>static/scripts/orderDetail.js"></script>
<!-- endbuilds-->
</body>
</html>