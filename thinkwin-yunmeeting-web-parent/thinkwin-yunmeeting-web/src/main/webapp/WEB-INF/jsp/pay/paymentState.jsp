<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html class="no-js" lang="">
  <head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="apple-touch-icon" href="<%=basePath%>static/images/apple-touch-icon.png">
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
    <!-- build:css static/styles/unfilledOrder.css-->
    <!-- build:css static/styles/paymentState.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/orderDetail.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/paymentState.css">
    <!-- endbuild-->
  </head>
  <body><!--[if lt IE 10]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
    <nav class="navbar navbar-expand-lg navbar-light">
      <div class="d-flex flex-row justify-content-end align-items-center">
        <span>官方电话：400-100-7909</span>
      </div>
    </nav>
    <div class="status-label text-center">
      <span class="status-label-text"> <i>1</i>提交订单 </span>
      <span class="status-label-line">—————</span>
      <span class="status-label-text"> <i>2</i>等待付款 </span>
      <span class="status-label-line">—————</span>
      <span class="status-label-text"> <i>3</i>支付完成</span>
    </div>
    <div class="tab-content">
      <div class="free text-center">
        <div class="payment-error"><img src="<%=basePath%>static/images/payment-error.png">
          <p class="payment-error-btn">
            <a class="btn btn-lg btn-primary"  href="#" name="1">重新支付</a>
            <a class="btn btn-lg btn-outline-info" href="#" name="2">查看订单</a>
            <a class="btn btn-lg btn-outline-info" href="#" name="3">返回企云会</a>
          </p>
          <div class="contect-us">
            <p>如有疑问，请联系我们</p>
            <p>电话：400-100-7909</p>
          </div>
        </div>
        <div class="payment-success"><img src="<%=basePath%>static/images/payment-success.png">
          <p class="state-label">您的订单已支付成功</p>
          <p class="count-down">
            <a class="invoice-center" href="#">您可以到订单中心-发票管理索取发票</a>
            <span id="count">5</span>
            S后跳转
          </p>
        </div>
      </div>
    </div>
    <input id="orderId" type="hidden" value="${model.orderId}" />
    <input id="payChannel" type="hidden" value="${model.channel}" />
    <input id="payStatus" type="hidden" value="${model.status}" />
    <input id="statusName" type="hidden" value="${model.statusName}" />
    <!-- build:js static/scripts/paymentState.js-->
    <script src="<%=basePath%>static/scripts/paymentState.js"></script>
    <!-- endbuilds-->
  </body>
</html>