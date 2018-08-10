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
    <!-- build:css static/styles/orderDetail.css-->
    <link rel="stylesheet" href="<%=basePath%>/static/styles/orderDetail.css">
    <link rel="stylesheet" href="<%=basePath%>/static/styles/payOrder.css">
    <!-- endbuild-->
</head>
<body><!--[if lt IE 10]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
<nav class="navbar navbar-expand-lg navbar-light">
    <div class="d-flex flex-row justify-content-end align-items-center"><span>官方电话：400-100-7909</span></div>
</nav>
<div class="status-label text-center"><span class="status-label-text"> <i>1</i>提交订单 </span><span class="status-label-line"></span><span class="status-label-text"> <i>2</i>等待付款 </span><span class="status-label-line"></span><span class="status-label-text status-label-default"> <i>3</i>支付完成</span></div>
<div class="tab-content">
    <div class="free">
        <div class="title">订单详情<span class="order-number-box">(<i class="order-number"></i>)</span></div>
        <div class="content-table">
        </div>
        <div class="payment">
            <i class="payment-label" style="color: #757575;">支付方式</i>
            <a class="different text-center payment-active" name="2">
                <img src="<%=basePath%>static/images/PayPal.png">
            </a>
            <a class="text-center" name="1">
                <img src="<%=basePath%>static/images/WeChat.png">
            </a>
            <%--<a class="text-center" name="3">网银支付</a>--%>
            <a class="text-center" name="4">银行汇款</a>
        </div>
        <div class="bank-pay-info">
            <p>银行转账支付</p>
            <p>公司名称：<span>北京盛科维科技有限公司</span></p>
            <p>银行账号：<span>01090 30290 01201 05615 886</span></p>
            <p>开户银行：<span>北京银行知春路支行</span></p>
            <p>我们提供公对公转账支持，请在转账备注中注明订单号
                <span class="order-number"></span>
                ，我们在收到您的转账后<br/>对该订单进行处理。
            </p>
        </div>
        <div class="pay-btn-box">
            <a class="btn btn-primary pay-btn" href="#">立即支付</a>
            <span class="total-label">总计：</span>
            <span class="total-number"></span>
        </div>
    </div>
</div>
<input id="orderDetailId" type="hidden" val="" name="订单id">
<div class="modal fade" id="paymentConfirmation" tabindex="-1" role="dialog" aria-labelledby="removeModalLabel" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header title-box">
                <h5 class="modal-title" id="removeModalLabel">支付遇到问题？</h5>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true" style="font-size: 14px"></span></button>
            </div>
            <div class="modal-body"> <span>请先进行在线支付，成功后自动完成服务授权。</span></div>
            <div class="modal-footer"><a class="trouble-btn">支付遇到问题</a>
                <button class="btn btn-primary success-btn" type="button" aria-label="Close">支付成功</button>
            </div>
        </div>
    </div>
</div>
<input id="orderId" type="hidden" value="${model.orderId}" />
<input id="orderStatus" type="hidden" value="${model.status}" />
<input id="statusName" type="hidden" value="${model.statusName}" />
<!-- build:js static/scripayOrder.jsl.js-->
<script src="<%=basePath%>static/scripts/payOrder.js"></script>
<!-- endbuilds-->
</body>
</html>