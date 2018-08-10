<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2017/9/26
  Time: 16:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会-连接组织的智慧</title>
    <%@ include file="/WEB-INF/jsp/common/head.jsp"%>
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/login-register-common.css">
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/checkInviteCode.css">
</head>
<body>
<div class="register-form">
    <%@ include file="/WEB-INF/jsp/common/login-logo.jsp"%>
    <form class="form-horizontal">
        <div class="form-group weChat-logo" id="weChat-register"></div>
        <div class="form-group">
            <input class="col-12 form-control form-control-lg" type="text" id="inputName" placeholder="姓名" maxlength="10" name="userName">
        </div>
        <div class="form-group">
            <input class="col-12 form-control form-control-lg" style="box-shadow: none;border-color:rgba(0, 0, 0, 0.15);background-color: #f0f3f8;" type="text" id="inputCompany" placeholder="公司名称" maxlength="50"  name="tenantName" readonly="true">
        </div>
        <div class="form-group">
            <input class="col-12 form-control form-control-lg" type="tel" id="inputTel" placeholder="手机号" name="phoneNumber" maxlength="11">
        </div>
        <div class="form-group">
            <input class="form-control form-control-lg get-code-text" type="text" id="inputCode" placeholder="验证码" name="code"><input type="button" id="registerCode" class="btn btn-lg btn-secondary get-code-btn" value="获取验证码"/>
        </div>
        <div class="form-group">
            <input class="col-12 form-control form-control-lg" type="text" onfocus="this.type='password'" id="inputPassword" placeholder="密码 8~20位数字、字母组合" name="password">
        </div>
        <p class="error-msg warningBox">
            <i class='icon icon-error'></i>
            <label id="warmLabel"></label>
        </p>
        <div class="form-group register" >
            <button id="registerBtn" type="submit" class="col-12 btn btn-lg btn-primary ">下一步</button>
        </div>
    </form>
    <input id="success" type="hidden" value="${isSuc}">
    <input id="companyName" type="hidden" value="${tenantName}">
    <input id="tenantId" type="hidden" value="${tenantId}">
    <div class="modal fade" id="deleteOrga" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <form id="deleteOrgaForm">
                    <div class="modal-header">
                        <div class="modal-title">邀请失败</div>
                    </div>
                    <div class="modal-body" style="padding: 21px 0 27px">
                        <label class="form-control-label" style="margin-bottom:0;">邀请链接已失效，请联系企业管理员再次索取。</label>
                    </div>
                    <div class="modal-footer">
                        <a class="btn btn-lg btn-primary" href="<%=basePath%>system/loginpage">确定</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
<script src="<%=basePath%>static/js/common/formValidator.js"></script>
<script src="<%=basePath%>static/js/login-register/checkInviteCode.js"></script>
</html>
