<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="apple-touch-icon" href="apple-touch-icon.png">
    <!-- Place favicon.ico in the root directory-->
    <!-- build:css static/styles/dashboard.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/dashboard.css">
    <!-- endbuild-->
    <script src="<%=basePath%>static/plugin/underscore.min.js"></script>
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <!-- build:css static/styles/admin.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/personnelOrg.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/admin.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">
    <!-- endbuild-->
  </head>
  <body><!--[if lt IE 10]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
    <div class="wrapper">
      <div class="sidebar">
        <div class="logo"><img src="<%=basePath%>static/images/yunmeeting.png">
          <h4>用户企业</h4>
        </div>
    <%--    <ul class="nav flex-column">
          <li class="nav-item"><a class="nav-link" href="admin.jsp"><i class="icon icon-settings"></i><span>企业设置</span></a></li>
          <li class="nav-item"><a class="nav-link" href="directories.html"><i class="icon icon-addressbook"></i><span>通讯录管理</span></a></li>
          <li class="nav-item"><a class="nav-link" href="room.html"><i class="icon icon-home"></i><span>会议室管理</span></a></li>
          <li class="nav-item"><a class="nav-link" href="#"><i class="icon icon-text"></i><span>订单管理</span></a></li>
          <li class="nav-item"><a class="nav-link" href="statistical.html"><i class="icon icon-rank"></i><span>统计分析</span></a></li>
          <li class="nav-item"><a class="nav-link" href="operationLog.html"><i class="icon icon-edit"></i><span>操作日志</span></a></li>
        </ul>--%>
        <%@ include file="/WEB-INF/jsp/common/left.jsp"%>
      </div>
      <div class="main-panel">
        <nav class="navbar fixed-top navbar-expand-lg navbar-light">
          <div class="d-flex flex-row justify-content-between align-items-center">
            <ul class="tab-business" role="tablist">
              <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#home" role="tab">企业基本信息</a></li>
              <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#profile" role="tab" id="setmanagetime">管理设置</a></li>
            </ul>
            <%@ include file="/WEB-INF/jsp/common/top.jsp"%>
           <%-- <ul class="nav">
              <li class="nav-item"><a class="nav-link" href="#"><span class="icon icon-question"></span></a></li>
              <li class="nav-item dropdown"><a class="nav-link dropdown-toggle" id="menu" href="#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="nophoto">管理</span><span>系统管理员</span></a>
                <div class="dropdown-menu" aria-labelledby="menu"><a class="dropdown-item btn-show-profile" data-toggle="tab" href="#profile" role="tab"><i class="icon icon-my"></i>个人资料</a><a class="dropdown-item" href="/logout"><i class="icon icon-logout"></i>退出登录</a></div>
              </li>
            </ul>--%>
          </div>
        </nav>
        <div class="content personnel-wrap" class="content personnel-wrap" style="min-width: 1125px;">
          <div class="tab-content">
            <div class="tab-pane active" id="home" role="tabpanel">
              <div class="company-info">
                <form id="companyForm">
                  <div class="form-group company-logo">
                    <label>公司LOGO图片</label>
                    <div class="logo"><img id="logo" src="<%=basePath%>static/images/yunmeeting.png"><span class="btn btn-outline-primary btn-select-photo"><span id="sctp">上传图片</span>
                      <label><input type="file" accept="image/jpg,image/png,image/jpeg,image/bmp" style="
    display:none;"></label>
                       </span></div>
                    <div class="collapse" id="selectPhoto">
                      <p class="help-block">
                         支持jpg、jpeg、png、bmp格式，且图片小于5M<span class="btn-change-photo">更换图片
                          <input type="file" accept="image/jpg,image/png,image/jpeg,image/bmp"></span></p>
                      <div class="cropper">
                        <div class="photo"><img id="image" src="<%=basePath%>static/images/yunmeeting.png"></div>
                        <div class="preview"></div>
                        <div class="preview-label">100 x 100</div>
                      </div>
                      <div class="text-right"><a class="btn-cancel" data-toggle="collapse" href="#selectPhoto">取消</a>
                        <button class="btn btn-lg btn-primary btn-save-photo" id="changeLogo">保存</button>
                      </div>
                    </div>
                  </div>
                  <div class="form-group">
                    <label>公司名称</label>
                    <input class="form-control form-control-lg" id="comname" type="text" placeholder="请输入公司名称" maxlength="30">
                  </div>
                  <div class="form-group">
                    <label>行业类型</label>
                    <div class="dropdown">
                      <div class="form-control form-control-lg" id="comtype" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true">
                        <span id="type-name" class="placeholder-color">请选择行业类型</span>
                        <span class="icon icon-organiz-down"></span>
                      </div>
                      <div class="dropdown-menu company-type" aria-labelledby="comtype">

                      </div>
                    </div>


                  </div>
                  <div class="form-group">
                    <label>详细地址</label>
                    <input class="form-control form-control-lg" id="comaddress" type="text" placeholder="请输入详细地址，例如北京海淀区学清路810号XXX大厦" maxlength="200">
                  </div>
                  <div class="form-group des-form">
                    <label>公司简介</label>
                    <textarea class="form-control form-control-lg" id="description" placeholder="请输入公司简介" maxlength="200"></textarea>
                    <span class="description-length">（0/200字）</span>
                  </div>
                  <div id="error">
                    <label><i class="icon icon-error"></i>&nbsp;</label><span id="errormsg"></span>
                  </div>
                  <button class="btn btn-primary" id="savecomInfo" type="submit">保存</button>
                </form>
              </div>
              <div class="invite">
                <h5>邀请</h5>
                <p>将二维码发送给同事，同事可微信扫码后，用邀请码快速加入企业。</p>
                <div class="qr-code"><img id="rcode" src="<%=basePath%>static/images/code.png"><span id="companyName"></span></div>
                <div class="invite-code">邀请码<input class="form-control form-control-lg" id="inviteCode" type="text" value="768910" readonly><button id="icon-refresh"><i class="icon icon-refresh"></i>刷新</button>
                </div>
                <div class="pricing">当前授权：<span id="pricing">免费版</span><button class="btn btn-lg btn-outline btn-custom" id="buyVersion"></button><span id="endTime"></span>
                </div>
                <div class="version-count">
                  <div><span class="icon icon-tick"></span><span id="personCount"></span></div>
                  <div><span class="icon icon-tick"></span><span id="roomCount"></span></div>
                  <div><span class="icon icon-tick"></span><span id="capacityCount"></span></div>
                </div>
              </div>
              <div class="rows company-dissolution">
                <h3>解散企业</h3>
                <p> <span>一旦解散了企业，企业内所有的数据都将会被永久删除，并且无法撤回，请谨慎操作！</span><a class="dissolution-link" href="#dissolution" data-toggle="collapse" aria-expanded="false">解散企业</a></p>
                <div class="collapse" id="dissolution" aria-expanded="true">
                  <div id="dissolutionForm">
                    <div class="form-group">
                      <input class="form-control form-control-lg" name="code" type="text" placeholder="请输入手机验证码" data-validate="captcha" maxlength="6"><button class="btn btn-lg btn-outline-primary btn-get-code" type="button" id="btn-get-code1">获取验证码</button><p class="error-msg"></p>
                    </div>
                    <div class="save-bts">
                      <button class="btn btn-lg btn-outline-primary btn-save">解散企业</button><button class="btn btn-lg btn-primary btn-cancel" data-toggle="collapse" href="#dissolution">取消</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="tab-pane" id="profile" role="tabpanel">
              <div class="managers keeper">
                <button class="btn btn-lg btn-outline" data-toggle="modal" data-target="#alterModal">变更管理员</button>
                <h5>主管理员</h5>
                <p id="mainAdmin"></p>
              </div>
              <div class="managers" id="subManager"><%-- data-toggle="personnel" data-type="1" data-max="3"--%>
                <h5>子管理员</h5>
                <span class="info">管理员拥有控制台管理权限，最多可设置三个管理员</span>
                <%--<div class="tags" id="sonAdministrator"><span class="icon icon-add-personnel btn-add"></span></div>--%>
                <div class="tags" id="sonAdministrator"><span class="icon icon-add-personnel btn-add trigger"></span></div>
              </div>
              <div class="managers" id="roomManger"><%-- data-toggle="personnel" data-type="1"--%>
                <h5>会议室管理员</h5>
                <span class="info">会议室管理员拥有会议室管理权限，负责审批特殊会议室的预订申请</span>
                <div class="tags" id="boardroommanagers"><span class="icon icon-add-personnel btn-add trigger"></span></div>
              </div>
              <div class="managers" id="roomBooker"><%-- data-toggle="personnel" data-type="1"--%>
                <h5>会议室预订专员</h5>
                <span class="info">可对会议室设置预订权限，指定只有会议预订专员才能预订会议室</span>
                <div class="tags" id="commissioners"><span class="icon icon-add-personnel btn-add trigger"></span></div>
              </div>
              <div class="managers" id="terminalManager"><%-- data-toggle="personnel" data-type="1"--%>
                <h5>会议显示终端管理员</h5>
                <span class="info">可对会议显示终端进行注册及管理,最多可设置三个管理员</span>
                <div class="tags" id="terminals"><span class="icon icon-add-personnel btn-add trigger"></span></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="alterModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">变更管理员</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <div class="tab-content">
              <div class="tab-pane active" id="step1">
                <div class="step"><i class="icon icon-step"></i>验证手机号<span id="phone">系统会向13590009999发送验证码</span></div>
                <div class="form-group input-code">
                  <input class="form-control form-control-lg" id="input-code1" name="code" type="text" placeholder="验证码" maxlength="6" data-validate="captcha"><span>
                    <button class="btn btn-lg btn-custom btn-outline btn-get-code" id="code1" type="button">获取验证码</button></span>
                </div>
                <div class="error-div">
                  <span class="error"><i class="icon icon-error"></i>&nbsp;<span class="msg">验证码不能为空</span></span>
                </div>
                <div class="text-right">
                  <button class="btn btn-lg btn-clear" type="button" data-dismiss="modal">取消</button><a class="btn btn-lg btn-primary" id="next" role="tab1" data-toggle="tab">下一步</a>
                </div>
              </div>
              <div class="tab-pane" id="step2">
                <div class="step"><i class="icon icon-step1"></i>绑定新管理员</div>
                <div class="form-group">
                  <input class="form-control form-control-lg" id="phoneNum" name="code" type="text" placeholder="新管理员的登录手机号"  data-validate="captcha">
                </div>
                <div class="form-group input-code">
                  <input class="form-control form-control-lg" id="input-code2" name="code" type="text" placeholder="验证码" maxlength="6" data-validate="captcha"><span>
                    <button class="btn btn-lg btn-custom btn-outline btn-get-code" id="code2" type="button">获取验证码</button></span>
                </div>
                <div class="error-div">
                  <span class="error"><i class="icon icon-error"></i>&nbsp;<span class="msg">验证码不能为空</span></span>
                </div>
                <div class="text-right">
                  <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
                  <button class="btn btn-lg btn-primary" id="submit-phoneNum" type="button">确定</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- build:js static/scripts/admin.js-->
    <script src="<%=basePath%>static/plugin/cropper.min.js"></script>
    <%--<script src="<%=basePath%>static/scripts/personnel.js"></script>--%>
    <script src="<%=basePath%>static/scripts/personnelOrg.js"></script>
    <script src="<%=basePath%>static/scripts/admin.js"></script>
    <!-- endbuild-->
  </body>
</html>