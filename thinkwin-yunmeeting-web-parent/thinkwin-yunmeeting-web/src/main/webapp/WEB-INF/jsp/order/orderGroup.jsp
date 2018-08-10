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
  <script src="<%=basePath%>static/scripts/jump.js"></script>
  <link rel="stylesheet" href="<%=basePath%>static/styles/orderDetail.css">
  <link rel="stylesheet" href="<%=basePath%>static/styles/orderGroup.css">
  <link rel="stylesheet" href="<%=basePath%>static/styles/sliderange.css">
</head>
<body><!--[if lt IE 10]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
<nav class="navbar navbar-expand-lg navbar-light">
  <div class="d-flex flex-row justify-content-end align-items-center"><span>官方电话：400-100-7909</span></div>
</nav>
<input type="hidden" id="orderType" value="${type}">
<div class="status-label text-center"><span class="status-label-text"> <i>1</i>提交订单 </span><span class="status-label-line"></span><span class="status-label-text status-label-default"> <i>2</i>等待付款 </span><span class="status-label-line"></span><span class="status-label-text status-label-default"> <i>3</i>支付完成</span></div>
<div class="tab-content">
  <div class="content-serve">
    <div class="form-group row">
      <label class="col-form-label">当前服务</label>
      <label class="col-sm-2 version" onclick="didClickIntroduce()">专业版</label>
    </div>
    <div class="form-group row">
      <label class="col-form-label"></label>
      <div class="col-time">
        <label class="key">有效时间 :</label>
        <label class="value indate">2020-06-27</label>
      </div>
      <div class="col-serve" style="width: 235px">
        <label class="key" style="width: 75px">员工人数 :</label>
        <label class="value members">400人</label>
      </div>
      <div class="col-serve">
        <label class="key">会议室数 :</label>
        <label class="value rooms">24间        </label>
      </div>
      <div class="col-serve">
        <label class="key">存储空间 :</label>
        <label class="value spaces">25G</label>
      </div>
    </div>
  </div>
  <div class="free">
    <div class="title">购买企云会专业版<button class="btn clear  introduce" onclick="didClickIntroduce()">版本介绍</button></div>
    <div class="content-produce">
      <div class="form-group row peopleNum">
        <label class="col-form-label">员工人数</label>
        <div class="col employee-range"></div>
      </div>
      <div class="form-group row roomNum">
        <label class="col-form-label">会议室数</label>
        <div class="col boardroom-range"></div>
        <div class="relevanceDiscount room">无折扣</div>
      </div>
      <div class="form-group row space">
        <label class="col-form-label">存储空间</label>
        <div class="col storage-range"></div>
        <div class="relevanceDiscount space">无折扣</div>
      </div>
      <div class="form-group row last">
        <div class="remaining">
          <label class="col-form-label">剩余时长</label>
          <label class="value time">0天</label>
        </div>
        <label class="value unitPrice"> </label>
      </div>
    </div>
    <div class="content-time">
      <div class="form-group row first">
        <label class="col-form-label">购买时长</label>
        <%--<span class="rooms one select">1年--%>
        <%--<div class="selectedIcon"></div><div class="discountYears" data-discount="100">85折</div></span><span class="rooms two">2年--%>
        <%--<div class="selectedIcon"></div>--%>
        <%--<div class="discountYears" data-discount="100">85折</div></span><span class="rooms three">3年--%>
        <%--<div class="selectedIcon"></div>--%>
        <%--<div class="discountYears" data-discount="100">75折</div></span>--%>
      </div>
      <div class="form-group row end">
        <label class="value renew"></label>
      </div>
    </div>
    <div class="content-showDiscount"><div  class="col-form-label collapsed" data-toggle="collapse" href="#collapseExample" role="button" aria-expanded="false" aria-controls="collapseExample">使用优惠券<i class="icon icon-info-down"></i></div>
      <div class="collapse" id="collapseExample">
        <div class="form-group row">
          <label class="col-form-label"></label>
          <div class="col-sm-3"><input  class="form-control discountIput"  placeholder="请输入优惠券">
            <div class="describe">使用优惠券含折扣，时长折扣优惠将失效</div>
          </div><button class="btn btn-sm btn-primary discountBtn" type="button">校验</button>
          <div class="correct">
            <i class="icon icon-connected"></i>
            <label class="discountInfo"></label>
            <a class="icon icon-question" role="button" data-toggle="popover" data-original-title="" title=""></a>
          </div>
          <div class="error">
            <i class="icon icon-failed"></i>
            <label class="errorMsg">优惠码不正确，请填写真实的优惠码</label>
          </div>
        </div>
      </div>
    </div>
    <div class="content-list">
      <div class="form-group row">
        <label class="col-form-label">购买清单</label>
      </div>
      <div class="form-group row">
        <label class="col-form-label"></label>
        <div class="col-serve" style="margin-left: 15px">
          <label class="key">员工人数 :</label>
          <label class="value employNum"></label>
          <label class="value dfMember"></label>
        </div>
        <div class="col-serve">
          <label class="key">会议室数 :</label>
          <label class="value roomNum">0间</label>
          <label class="value dfRoom"></label>
        </div>
        <div class="col-serve">
          <label class="key">存储空间 :</label>
          <label class="value spaceNum">0G</label>
          <label class="value dfSpace"></label>
        </div>
      </div>
      <div class="form-group row">
        <label class="col-form-label"></label>
        <div class="col-sm-4">
          <label class="key">购买时长 :</label>
          <label class="value duration"></label>
          <label class="value dfDate"></label>
        </div>
      </div>
    </div>
    <div class="content-bottom">
      <div class="form-group row">
        <label class="col-form-label">总计费用</label>
        <div class="loading">
          <label class="blue">费用计算中...</label>
        </div>
        <div class="loaded">
          <label class="discountPrice">0元</label>
          <label class="price">0元</label>
        </div><button class="btn btn-lg btn-primary submit" type="button">提交订单</button>
      </div>
      <div class="form-group row">
        <label class="col-form-label"></label>
        <label class="loaded col-sm-8 total"></label>
      </div>
    </div>
  </div>
</div>
<script src="<%=basePath%>static/scripts/plugins/sliderange.js"></script>
<script src="<%=basePath%>static/scripts/orderGroup.js"></script>
</body>
</html>