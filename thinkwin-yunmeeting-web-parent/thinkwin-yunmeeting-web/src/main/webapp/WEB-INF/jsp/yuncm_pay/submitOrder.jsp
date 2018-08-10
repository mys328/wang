<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html class="no-js" lang="">
  <head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="shortcut icon" type="image/x-icon" href="<%=basePath%>static/images/yunmeeting-logo.ico">
    <!-- Place favicon.ico in the root directory-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/common.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">
    <script src="<%=basePath%>static/plugin/underscore.min.js"></script>
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <link rel="stylesheet" href="<%=basePath%>static/styles/submitOrder.css">
  </head>
  <body><!--[if lt IE 10]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
    <nav class="navbar navbar-expand-lg navbar-light">
      <div class="d-flex flex-row justify-content-between align-items-center"><img class="d-inline-block" src="<%=basePath%>static/images/yunmeeting-logo.png"><span>官方电话：400-100-7909</span></div>
    </nav>
  <input type="hidden" value="${productId}" id="productId"><input type="hidden" value="${type}" id="ShowType">
    <div class="tab-content">
      <div class="content-top"><span class="step1"><span class="step-icon">1</span>提交订单</span><span class="step2"><span class="line"></span><span class="step-icon">2</span>等待付款</span><span class="step3"><span class="line"></span><span class="step-icon">3</span>支付完成</span></div>
      <div class="content">
        <div class="title">
          <span>购买企云会</span>
        </div>
        <div style="margin-left:24px;margin-top: 31px">
          <div class="row">
            <div class="col-left"><span style="height:40px;line-height:40px;">选择版本</span></div>
            <div class="col-right">
              <span id="version-show" style="display: none;margin-left: 16px;margin-right: 40px;font-size: 14px;color: #333;float: left"></span>
              <div class="dropdown" style="width: 280px;float: left;margin-right: 24px">
                <div class="form-control" id="selectVersion" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true"><span id="version-type">选择版本</span><span class="icon icon-organiz-down"></span></div>
                <div class="dropdown-menu" aria-labelledby="selectVersion"> </div>
              </div>
              <span id="ver-info" style="height: 40px;line-height: 40px;float: left;"> 版本介绍</span>
            </div>
            <%--<div class="col-3" style="height:40px;line-height:40px;"></div>--%>
          </div>
          <div class="row" style="margin-top:25px;">
            <div class="col-left">购买年限
              <%--<span style="height:40px;line-height:40px;">购买年限</span>--%>
            </div>
            <div class="col-right row" id="selectService">
              <%--<div class="col-4">--%>
                <%--<div class="buy-year">--%>
                  <%--<label class="discount">八折</label>--%>
                  <%--<labe class="year-title">3年/专业版（31-100人）</labe>--%>
                  <%--<p><sup>￥</sup>6720<span class="unit">8400</span></p>--%>
                <%--</div>--%>
              <%--</div>--%>
              <%--<div class="col-4">--%>
                <%--<div class="buy-year">--%>
                  <%--<label class="discount">八折</label>--%>
                  <%--<labe class="year-title">3年/专业版（31-100人）</labe>--%>
                  <%--<p><sup>￥</sup>6720<span class="unit">8400</span></p>--%>
                <%--</div>--%>
              <%--</div>--%>
              <%--<div class="col-4">--%>
                <%--<div class="buy-year">--%>
                  <%--<label class="discount">八折</label>--%>
                  <%--<labe class="year-title">3年/专业版（31-100人）</labe>--%>
                  <%--<p><sup>￥</sup>6720<span class="unit">8400</span></p>--%>
                <%--</div>--%>
              <%--</div>--%>
            </div>
          </div>
          <div style="margin-top:32px;">
            <h5 style="font-size: 16px">服务包选购</h5>
            <div class="row" style="margin-top:24px;height:32px;line-height:32px;">
              <div class="col-left"><span>会议室数量</span></div>
              <div class="col-right" style="height:32px;line-height:32px;">
                <div class="input-group" style="float:left;width:104px;">
                  <span class="input-group-btn" style="width:32px;height: 32px;line-height: 32px">
                    <button class="btn btn-secondary icon icon-fwb-deduct" id="subRoom" type="button" disabled="true"></button>
                  </span>
                  <input class="form-control" id="room-count" type="text" min="3" max="100" value="3" onKeypress="return (/[\d/g]/.test(String.fromCharCode(event.keyCode)))" style="ime-mode:Disabled">
                  <span class="input-group-btn" style="width:32px;height: 32px;line-height: 32px">
                    <button class="btn btn-secondary icon icon-fwb-add" id="addRoom" type="button"></button>
                  </span>
                </div>
                <span style="float:left;margin-left:8px;width: 14px;font-size: 14px;color: #333;">间</span>
                <span id="room-free-msg">前3间免费，每增加1间，150元/年。</span>
              </div>
            </div>
            <div class="row" style="margin-top:30px;height:40px;line-height:40px;">
              <div class="col-left"><span>存储空间</span></div>
              <div class="col-right" style="height:32px;line-height:32px;">
                <div class="input-group" style="float:left;width:104px;">
                    <span class="input-group-btn" style="width:32px;height: 32px;line-height: 32px">
                      <button class="btn btn-secondary icon icon-fwb-deduct" id="subMemonry" type="button" disabled="true"></button>
                    </span>
                  <input class="form-control" id="memonry-count" type="text" min="5" max="100" value="5" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')">
                  <span class="input-group-btn" style="width:32px;height: 32px;line-height: 32px">
                      <button class="btn btn-secondary icon icon-fwb-add" id="addMemonry" type="button"></button>
                    </span>
                </div><span style="float:left;margin-left:8px;width: 14px;font-size: 14px;color: #333;">G</span>
                <span id="space-free-msg">前5G免费，每增加5G，50元/年。</span>
              </div>
            </div>
            <div class="row" style="margin-top:30px;">
              <div class="col-left"></div>
              <div class="col-right">
                <%--<div class="row">--%>
                  <%--<div class="form-check col-4">--%>
                    <%--<label class="form-check-label">--%>
                      <%--<input class="form-check-input" type="checkbox">使用优惠券--%>
                    <%--</label>--%>
                  <%--</div>--%>
                  <%--<div class="col-8">--%>
                    <%--<input style="display:none;" id="coupon">--%>
                  <%--</div>--%>
                <%--</div>--%>
                <div style="margin-top:0px;"><span id="calculate-pirce">2800 x 3年 x 1 + 0元 + 0元 = 8400.00元</span></div>
                <div style="margin-top:32px;">
                  <button class="btn btn-lg btn-primary" id="submitOrder">提交订单</button><button class="btn btn-clear total">总计：<span id="total">8400.00元</span></button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <script src="<%=basePath%>static/scripts/submitOrder.js"></script>
  </body>
</html>