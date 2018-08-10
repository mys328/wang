<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/jsp/common/common.jsp" %>
<!DOCTYPE html>
<html class="no-js" lang="">
  <head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会</title>
    <link rel="apple-touch-icon" href="apple-touch-icon.png">
    <!-- Place favicon.ico in the root directory-->
    <!-- build:css static/styles/common.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/common.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">

    <!-- endbuild-->
    <script src="<%=basePath%>static/plugin/underscore.min.js"></script>
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <link rel="stylesheet" href="<%=basePath%>static/styles/orderDetail.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/orderGroup.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/sliderange.css">
  </head>
  <body><!--[if lt IE 10]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
  <nav class="navbar navbar-expand-lg navbar-light">
    <div class="d-flex flex-row justify-content-between align-items-center"><img class="d-inline-block" src="<%=basePath%>static/images/yunmeeting-logo.png"><span>官方电话：400-100-7909</span></div>
  </nav>
    <input type="hidden" id="orderType" value="${type}">
    <%--<div class="status-label text-center"><span class="status-label-text"> <i>1</i>提交订单 </span><span class="status-label-line">—————</span><span class="status-label-text"> <i>2</i>等待付款 </span><span class="status-label-line">—————</span><span class="status-label-text"> <i>3</i>支付完成</span></div>--%>
    <div class="tab-content">
      <div class="content-serve">
        <div class="form-group row">
          <label class="col-form-label">当前服务</label>
          <label class="col-sm-2 version">专业版</label>
        </div>
        <div class="form-group row">
          <label class="col-form-label"></label>
          <div class="col-sm-3">
            <label class="key">有效时间:</label>
            <label class="value indate">2020-06-27</label>
          </div>
          <div class="col-sm-2">
            <label class="key">员工人数:</label>
            <label class="value members">400人</label>
          </div>
          <div class="col-sm-2">
            <label class="key">会议室数:</label>
            <label class="value rooms">24间        </label>
          </div>
          <div class="col-sm-2">
            <label class="key">存储空间:</label>
            <label class="value spaces">25G</label>
          </div>
        </div>
      </div>
      <div class="free">
        <div class="title">订单预览效果</div>
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
            <%--<div class="loading">--%>
              <%--<label>费用计算中...</label>--%>
            <%--</div>--%>
            <div class="loaded">
              <label></label>
            </div>
            <div class="remaining">
              <label class="col-form-label">剩余时长</label>
              <label class="value time">2年零107天</label>
            </div>
            <label class="value unitPrice"> </label>
          </div>
        </div>
        <div class="content-time">
          <div class="form-group row first">
            <label class="col-form-label">购买时长</label>

          </div>
          <div class="form-group row end">
            <label class="value renew">15744.00元/年x2年x0.85=26764.80元</label>
          </div>
        </div>
        <%--<div class="content-showDiscount"><div  class="col-form-label collapsed" data-toggle="collapse" href="#collapseExample" role="button" aria-expanded="false" aria-controls="collapseExample">使用优惠券<i class="icon icon-info-down"></i></div>--%>
          <%--<div class="collapse" id="collapseExample">--%>
            <%--<div class="form-group row">--%>
              <%--<label class="col-form-label"></label>--%>
              <%--<div class="col-sm-3"><input  class="form-control discountIput"  placeholder="请输入优惠券">--%>
                <%--<div class="describe">使用优惠券含折扣，时长折扣优惠将失效</div>--%>
              <%--</div><button class="discountBtn" type="button">校验</button> --%>
              <%--<div class="correct">--%>
                 <%--<i class="icon icon-kzt-settings"></i>--%>
                <%--<label>特权优惠券：赠送11间会议室，10GB存储空间，1年时长</label>--%>
              <%--</div>--%>
              <%--<div class="error">--%>
                 <%--<i class="icon icon-kzt-settings"></i>--%>
                <%--<label>优惠码不正确，请填写真实的优惠码</label>--%>
              <%--</div>--%>
            <%--</div>--%>
          <%--</div>--%>
        <%--</div>--%>
        <div class="content-list">
          <div class="form-group row">
            <label class="col-form-label">购买清单</label>
          </div>
          <div class="form-group row">
            <label class="col-form-label"></label>
            <div class="col-row">
              <label class="key">员工人数：</label>
              <label class="value employNum">410人</label>
            </div>
            <div class="col-row">
              <label class="key">会议室数：</label>
              <label class="value roomNum">3间
                <label class="dfRoom">(含3间免费)</label>
              </label>
            </div>
            <div class="col-row">
              <label class="key">存储空间：</label>
              <label class="value spaceNum">5G
                <label class="dfSpace">(含5G免费)  </label>
              </label>
            </div>
          </div>
          <div class="form-group row">
            <label class="col-form-label"></label>
            <div class="col-row">
              <label class="key">选择年份：</label>
              <label class="value duration">
                <label class="dfDate"></label>
              </label>
            </div>
          </div>
        </div>
        <div class="content-bottom">
          <div class="form-group row">
            <label class="col-form-label">总计费用</label>
            <%--<div class="loading">--%>
              <%--<label class="blue">费用计算中...</label>--%>
            <%--</div>--%>
            <div class="loaded">
              <label class="discountPrice"></label>
              <label class="price"></label>
            </div>
            <div class="form-group budget row">
              <label class="label">预算金额</label>
              <input class="col-form" placeholder="输入金额" type="int" maxlength="12" id="previewPrice">
              <label class="unit">元</label>
              <label class="discount-title">折扣约</label>
              <label id="discountValue"></label>
            </div>
            <button class="submit" type="button">计算折扣</button>
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