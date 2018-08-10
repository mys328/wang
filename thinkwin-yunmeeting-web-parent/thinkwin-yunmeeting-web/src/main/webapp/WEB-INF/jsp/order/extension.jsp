<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html class="no-js" lang="">
<head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="apple-touch-icon" href="<%=basePath%>apple-touch-icon.png">
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
    <link rel="stylesheet" href="<%=basePath%>static/styles/extension.css">
</head>
<body><!--[if lt IE 10]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
<nav class="navbar navbar-expand-lg navbar-light">
    <div class="d-flex flex-row justify-content-end align-items-center"><span>官方电话：400-100-7909</span></div>
</nav>
<div class="tab-content">
    <div class="content-top"><span class="step1"><span class="step-icon">1</span>提交订单</span><span class="step2"><span class="line"></span><span class="step-icon">2</span>等待付款</span><span class="step3"><span class="line"></span><span class="step-icon">3</span>支付完成</span></div>
    <div class="content">
        <div class="content-title"><span>购买服务包</span></div>
        <div style="margin-left:24px;margin-right: 24px">
            <div class="row" style="margin-top:32px;margin-left:0;margin-right: 0px"><span class="title">会议室数量</span><span class="rooms one">3间</span><span class="rooms two">5间  </span><span class="rooms three">10间</span>
                <div class="col-2" style="padding: 0px">
                    <div style="width:160px;height:40px;line-height:40px;">
                        <div class="input-group" style="float:left;width:124px;"><span class="input-group-btn" style="width:40px; height: 40px;line-height: 40px">
                    <button class="btn btn-secondary icon icon-fwb-deduct" id="subRoom" type="button" disabled="true"></button></span>
                            <input class="form-control" id="room-count" style="width:44px;" type="text" min="0" max="100" value="0" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"><span class="input-group-btn" style="width:40px;height: 40px;line-height: 40px">
                    <button class="btn btn-secondary icon icon-fwb-add" id="addRoom" type="button"></button></span>
                        </div><span style="float:left;margin-left: 16px">间                </span>
                    </div>
                </div>
            </div>
            <div class="row" style="margin-top:16px;margin-left:0px;margin-right: 0px;padding-left: 96px;height: 19px;line-height: 19px"><span style="font-size: 14px;color: #757575">每增加1间，150元/年。</span></div>
            <div class="row" style="margin-top:24px;margin-left:0;margin-right: 0px"><span class="title">存储空间</span><span class="space one">5G</span><span class="space two">10G </span><span class="space three">15G</span>
                <div class="col-2" style="padding: 0px">
                    <div style="width:160px;height:40px;line-height:40px;">
                        <div class="input-group" style="float:left;width:130px;"><span class="input-group-btn" style="width:40px;height: 40px;line-height: 40px;">
                    <button class="btn btn-secondary icon icon-fwb-deduct" id="subMemonry" type="button" disabled="true"></button></span>
                            <input class="form-control" id="memonry-count" style="width:44px;" type="text" min="0" max="100" value="0" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"><span class="input-group-btn" style="width:40px;height: 40px;line-height: 40px">
                    <button class="btn btn-secondary icon icon-fwb-add" id="addMemonry" type="button"></button></span>
                        </div><span style="float:left;margin-left: 16px">G                </span>
                    </div>
                </div>
            </div>
            <div class="row" style="margin-top:16px;margin-left:0px;margin-right: 0px;padding-left: 96px;height: 19px;line-height: 19px"> <span style="font-size: 14px;color: #757575">每增加5G，50元/年。        </span></div>
            <div class="row" style="margin:0px;">
                <div style="margin-left:96px;margin-right: 24px;padding: 0px">
                    <%--<div class="row" style="margin-top:10px;">--%>
                        <%--<div class="form-check col-8">--%>
                            <%--<label class="form-check-label">--%>
                                <%--<input class="form-check-input" type="checkbox">使用优惠券--%>
                                <%--<input class="discount">--%>
                            <%--</label>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <div class="row" style="margin: 0px">
                        <div style="margin-top:39px;margin-left:0px;margin-right: 0px;font-size: 16px;color: #333">会议室价格：</div>
                        <div class="roomPrice" style="margin-top:39px;"><span>0元</span></div>
                    </div>
                    <div class="row" style="margin: 0px">
                        <div style="margin-top:23px;margin-left:0px;margin-right: 0px;font-size: 16px;color: #333">存储空间价格：</div>
                        <div class="spacePrice" style="margin-top:23px;"><span>0元     </span></div>
                    </div>
                    <div style="margin-top:31px;margin-left:0px;">
                        <button class="btn btn-lg btn-primary submitOrder" disabled>提交订单</button><span class="total">总计：<span id="total">0元</span></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<input type="hidden" value="${room}" id="roomNo">
<input type="hidden" value="${space}" id="spaceNo">
<script src="<%=basePath%>static/scripts/extension.js"> </script>
</body>
</html>