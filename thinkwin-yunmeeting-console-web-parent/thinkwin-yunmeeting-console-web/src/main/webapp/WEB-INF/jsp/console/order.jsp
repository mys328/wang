<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    <link rel="stylesheet" href="<%=basePath%>static/styles/order.css">
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
        <div class="content order">
          <div class="tab-content" style="height: 100%">
            <div class="table-Box" style="height: 100%"></div>
          </div>
          <div class="page-Box" id="page-Box"></div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="cerModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content img-content">
          <div class="modal-header img-header">
            <div class="modal-title"> </div>
          </div>
          <div class="modal-body img-body">
            <div class="img"><img id="cer"></div>
            <div class="img-close">
              <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" style="color: white;" aria-hidden="true"></span></button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="sureModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <div class="modal-title">订单信息</div>
          <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close " aria-hidden="true"></span></button>
        </div>
        <div class="modal-body">
          <div class="row">
            <label class="col-left">订单号：</label>
            <label class="col-right" id="orderId">1237566</label>
          </div>
          <div class="row">
            <label class="col-left">公司名称：</label>
            <label class="col-right" id="company">1234578</label>
          </div>
          <div class="row">
            <label class="col-left">汇款金额：</label>
            <label class="col-right" id="price">12345.00元</label>
          </div>
          <div class="row" id="inputOderNum">
            <label class="orderNum-label">支付宝交易号</label>
            <input class="form-control orderNum-input" id="orderNum" type="text" placeholder="请填写支付宝交易号">
          </div>
          <div class="row">
            <textarea placeholder="请输入到账备注信息" id="textremark" class="textremark"  maxlength="500"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn cancel-btn" type="button" data-dismiss="modal">取消</button>
          <button class="btn countersign-btn" type="button" id="sure">确认到账</button>
        </div>
      </div>
    </div>
  </div>
    <div class="modal fade" id="remarkModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <div class="modal-title">订单备注</div>
              <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close " aria-hidden="true"></span></button>
            </div>
            <div class="modal-body">
            <div class="row">
              <textarea placeholder="请输入到账备注信息" id="textremarkinfo" class="textremarkinfo" maxlength="500"></textarea>
            </div>
          </div>
            <div class="modal-footer">
              <button class="btn cancel-btn" type="button" data-dismiss="modal">取消</button>
              <button class="btn countersign-btn" type="button" id="save">保存</button>
            </div>
          </div>
         </div>
    </div>
    <script src="<%=basePath%>static/scripts/order.js"></script>
  </body>
</html>