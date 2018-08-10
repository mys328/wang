<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html class="no-js" lang="">
<head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <!-- Place favicon.ico in the root directory-->
    <!-- build:css static/styles/common.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/common.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">
    <!-- endbuild-->
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <!-- build:css static/styles/index.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/index.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/user.css">
    <!-- endbuild-->
</head>
<body style="overflow:auto;"><!--[if lt IE 10]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
<nav class="navbar navbar-expand-lg navbar-light">
    <%@ include file="/WEB-INF/jsp/common/mainTop.jsp"%>
</nav>
<div class="tab-pane" id="profile" role="tabpanel">
    <div class="wrap">
        <h4>个人资料</h4>
        <div class="content">
            <div class="rows avatar">
                <div class="row align-items-center">
                    <div class="labels">头像</div>
                    <div class="pic col">
                        <span class="nophoto biger"></span>
                        <div class="btn btn-lg btn-outline-primary change-photo">更换头像 <label class="change-photo-btn"><input class="change-photo-btn" type="file" accept="image/jpg,image/png,image/jpeg,image/bmp" style="
    display:none;"></label></div>
                        <a class="remove-photo" href="#" style="float: right;margin-top: 24px;color: #1896f0;display: none">删除头像</a>
                    </div>
                    <p class="help-block col">支持jpg、jpeg、png、bmp格式，且图片小于5M<span class="btn-change-photo">更换图片
                  <input class="change-photo-btn" type="file" accept="image/jpg,image/png,image/jpeg,image/bmp"></span></p>
                </div>
                <div class="cont">
                    <div class="collapse" id="changePhoto">
                        <div class="well">
                            <div class="cropper">
                                <div class="photo">
                                    <img id="image" src="<%=basePath%>static/images/default@1x.png">
                                </div>
                                <div class="preview">
                                    <span class="nophoto"></span>
                                </div>
                                <span class="img-size-label">100x100</span>
                            </div>
                            <div class="text-right"><a class="btn-cancel cancel-photo" data-toggle="collapse" href="#changePhoto">取消</a>
                                <button class="btn btn-lg btn-primary btn-save-photo" type="submit">保存</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="rows">
                <div class="row align-items-center">
                    <div class="labels">手机</div>
                    <p class="col"><i id="oldPhone"></i><a data-toggle="collapse" href="#changePhone">更换手机号</a></p>
                </div>
                <div class="cont">
                    <div class="collapse" id="changePhone">
                        <div class="well">
                            <form id="changePhoneForm" data-toggle="validator">
                                <div class="form-group new-phone">
                                    <input class="form-control form-control-lg" name="phone" type="text" placeholder="输入绑定的新手机号码" data-validate="phone">
                                </div>
                                <div class="form-group input-code">
                                    <input class="form-control form-control-lg" name="code" type="text" placeholder="验证码" data-validate="captcha" maxlength="6"><span>
                        <button class="btn btn-lg btn-outline-primary btn-get-code" type="button">获取验证码</button></span>
                                </div>
                                <div class="text-right"><a class="btn-cancel" data-toggle="collapse" href="#changePhone">取消</a><button class="btn btn-lg btn-primary" type="submit">确定</button>
                                </div>
                                <p class="error-msg"></p>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <div class="rows">
                <div class="row align-items-center">
                    <div class="labels">微信</div>
                    <p class="col"><i id="wechatNumber"> </i><a class="code-change" data-toggle="collapse" href="#unbindWeixin">解除绑定</a><a class="code-cancel" style="display: none"  data-toggle="collapse" href="#bindWeixin">取消</a></p>
                </div>
                <div class="cont">
                    <div class="collapse" id="unbindWeixin">
                        <div class="well">
                            <form id="changeWechatForm" data-toggle="validator">
                                <div class="form-group input-code">
                                    <input class="form-control form-control-lg" name="code" type="text" placeholder="验证码" data-validate="captcha" maxlength="6"><span>
                                    <button class="btn btn-lg btn-outline-primary btn-get-code" type="button">获取验证码</button></span>
                                </div>
                                <div class="text-right"><a class="btn-cancel" data-toggle="collapse" href="#unbindWeixin">取消</a><button class="btn btn-lg btn-primary" type="submit">确定</button>
                                </div>
                                <p class="error-msg"></p>
                            </form>
                        </div>
                    </div>
                    <div class="collapse" id="bindWeixin">
                        <div class="well">
                            <div class="code-img">
                                <img href="#">
                                <p>请使用微信扫描二维码，完成绑定</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="rows">
                <div class="row align-items-center">
                    <div class="labels">部门</div>
                    <p class="col"><i id="department"></i><span>需由管理员变更</span></p>
                </div>
            </div>
            <div class="line"></div>
            <form id="nameform" data-toggle="validator">
                <div class="rows nameform">
                    <div class="row align-items-center">
                        <div class="labels">姓名</div>
                        <div class="col">
                            <input class="form-control form-control-lg" name="username" type="text" maxlength="10" placeholder="请输入姓名" value="" data-validate="name">
                        </div>
                        <p class="error-msg error-msg-name"></p>
                    </div>
                </div>
                <div class="rows nameform">
                    <div class="row align-items-center">
                        <div class="labels">邮箱</div>
                        <div class="col">
                            <input maxlength="50" class="form-control form-control-lg" name="email" type="text" placeholder="请填写常用邮箱，用于接收会议通知" value="" data-validate="email">
                        </div>
                        <p class="error-msg error-msg-email"></p>
                    </div>
                </div>
                <div class="rows nameform">
                    <div class="cont">
                        <button class="btn btn-lg btn-primary" type="submit" disabled="true">保存</button>
                    </div>
                </div>
            </form>
        </div>
    </div><a class="icon icon-close btn-close" href="javascript:history.go(-1);"></a>
</div>
<!-- build:js static/scripts/user.js-->
<script src="<%=basePath%>static/plugin/cropper.min.js"></script>
<script type="text/javascript"  src="<%=basePath%>static/js/common/formValidator.js"></script><%--表单验证--%>
<script src="<%=basePath%>static/scripts/user.js"></script>
<!-- endbuild-->
</body>
</html>
