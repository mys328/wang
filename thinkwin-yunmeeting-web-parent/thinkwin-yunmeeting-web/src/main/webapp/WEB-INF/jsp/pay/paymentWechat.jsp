<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <!-- build:css static/styles/unfilledOrder.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/orderDetail.css">
    <!-- build:css static/styles/paymentWechat.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/paymentWechat.css">
    <!-- endbuild-->
  </head>
  <body><!--[if lt IE 10]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
    <nav class="navbar navbar-expand-lg navbar-light">
      <div class="d-flex flex-row justify-content-between align-items-center">
        <img class="d-inline-block" src="<%=basePath%>static/images/yunmeeting-logo.png">
        <span>官方电话：400-100-7909</span></div>
    </nav>
    <div class="tab-content">
      <div class="free">
        <div class="title">微信支付</div>
        <div class="payment-con"> 
          <p class="payment-title">
            充值：
            <span class="order-version"></span>
            <span class="total"></span>
            <a class="other-payment" href="#">选择其他充值方式</a>
          </p>
          <div class="quick-mark-box">
            <div class="quick-mark-img">
              <img src="#" />
            </div>
          </div>
        </div>
      </div>
    </div>
    <input id="orderId" type="hidden" value="${model.orderId}" />
    <input id="imgUrl" type="hidden" value="${model.tradeNo}" />
    <!-- build:js static/scripts/paymentWechat.js-->
    <script src="<%=basePath%>static/scripts/paymentWechat.js"></script>
    <!-- endbuilds-->
  </body>
</html>