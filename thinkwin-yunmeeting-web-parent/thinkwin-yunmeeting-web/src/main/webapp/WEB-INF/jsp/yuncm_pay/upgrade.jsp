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
    <script src="<%=basePath%>static/plugin/underscore.min.js"></script>
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <link rel="stylesheet" href="<%=basePath%>static/styles/upgrade.css">
  </head>
  <body><!--[if lt IE 10]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
    <nav class="navbar navbar-expand-lg navbar-light">
      <div class="navbarlogo"><img src="../../static/images/yunmeeting-logo.png"></div>
      <div class="d-flex flex-row justify-content-end align-items-center"><span>官方电话：400-100-7909</span></div>
    </nav>
    <div class="tab-content">
      <div class="goods-show">
        <div class="free">
          <div class="title title1"> <span>免费版</span></div>
          <div class="price-center row">
            <div class="price-div col-12">
              <div class="special-lower price" id="free-version"><sup>￥</sup><span id="freeprice"></span><span class="unit">/人/年</span></div>
            </div>
          </div>
          <ul class="version-info" id="free-info">

          </ul>
        </div>
        <div class="specialty">
          <div class="title"> <span>专业版 </span></div>
          <div class="price-center row" id="special">
            <div class="price-div spepri col-12">
              <div class="special-lower price">
                <sup>￥</sup><span style="color: #000" id="payprice"></span><span class="unit">/人/年</span>
              </div>
              <div class="specialtytitle">人数越多优惠越多，更有年限折上折</div>
            </div>
          </div>
          <ul class="version-info" id="pay-info">
          </ul>
          <h6 id="room-note">注：会议室超出按<span id="rommPrice"></span>元/间/年收费</h6>
          <h6 id="memory-note">存储空间超出按<span id="spacePrice"></span>元/G/年收费    </h6>
        </div>
      </div>

    </div>
    <script src="<%=basePath%>static/scripts/upgrade.js"></script>
  </body>
</html>