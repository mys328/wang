<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8"  %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="shortcut icon" type="image/x-icon" href="<%=basePath%>static/images/yunmeeting-logo.ico">
    <link rel="stylesheet" href="<%=basePath%>static/styles/dashboard.css">
    <!-- endbuild-->
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/plugins/md5.min.js"></script>
    <link rel="stylesheet" href="<%=basePath%>static/styles/zTreeStyle.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/personsDepartments.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/directories.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/pages.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">
    <!-- endbuild-->
    <%
      String host = request.getScheme() + "://"
              + request.getServerName()+":" + request.getServerPort();
    %>
  </head>
  <body><!--[if lt IE 10]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
    <div class="wrapper">
      <div class="sidebar">
        <div class="logo"><img src="<%=basePath%>static/images/yunmeeting-logo.png">
          <h4>用户企业</h4>
        </div>
        <%@ include file="/WEB-INF/jsp/common/left.jsp"%>
      </div>
      <div class="main-panel">
        <nav class="navbar fixed-top navbar-expand-lg navbar-light">
          <div class="d-flex flex-row justify-content-between align-items-center">
            <ul class="tab-business" role="tablist">
              <li class="nav-item"><a class="nav-link">通讯录管理</a></li>
            </ul>
            <%@ include file="/WEB-INF/jsp/common/top.jsp"%>
          </div>
        </nav>
        <div class="content directories">
          <div class="tab-content">
            <div class="tab-pane active" id="home" role="tabpanel">
              <div class="top"><span class="title"></span>
                <ul class="nav justify-content-center" role="tablist">
                  <li class="nav-item  batch-insert"><a class="nav-link" data-toggle="tab" href="#importBox" role="tab" aria-controls="importBox" name="批量导入">批量导入</a></li>
                  <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#addPerson" role="tab" aria-controls="addPerson" name="新增人员">新增人员</a></li>
                  <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#inviteBox" role="tab" aria-controls="inviteBox" name="邀请人员">邀请人员</a></li>
                </ul>
              </div>
              <div class="left">
                  <p class="title">组织机构</p>
                  <div class="treeBox">
                    <ul class="ztree" id="treeDemo"></ul>
                  </div>
              </div>
              <div class="right tab-content">
                <div class="tab-pane import" id="importBox" role="tabpanel"><a class="icon icon-close closed" data-toggle="tab" href="#addPerson" role="tab" aria-controls="addPerson"></a>
                  <div class="import-top">
                    <form id="myfile" enctype="multipart/form-data">
                      <p class="update-label">
                        <span>1. 请按照我们提供的标准模板填写信息</span>
                        <a class="template-link" href="#">下载标准模板</a></p>
                      <p class="update-label upBtn">
                        <span>2. 请上传填写好的文件</span>
                        <span class="file-name" style="margin-right:32px;display:none"><i></i><i></i></span>
                        <a class="file"></a>
                      </p>
                    </form>
                    <p class="up-limit">帐号上限<i class="user-max"></i>人，还可以导入<i class="user-min"></i>人，超出部分将无法导入，支持csv或xlsx格式。</p>
                    <p class="repetition"> <span class="custom-control custom-checkbox">
                        <input class="custom-control-input" type="checkbox" name="checkname"><span class="custom-control-indicator"></span></span>手机号信息相同时，覆盖原有信息
                      <button class="btn btn-primary import-btn">导入</button>
                    </p>
                  </div>
                  <div class="import-table">
                    <div class="table-head">
                      <table class="table">
                        <thead>
                          <tr>
                            <th>员工编号</th>
                            <th>部门</th>
                            <th>职位</th>
                            <th>姓名</th>
                            <th>性别</th>
                            <th>手机</th>
                            <th>邮箱</th>
                          </tr>
                        </thead>
                      </table>
                    </div>
                    <div class="table-con"></div>
                  </div>
                  <div class="load1 table-loading">
                    <div class="rect1"></div>
                    <div class="rect2"></div>
                    <div class="rect3"></div>
                    <div class="rect4"></div>
                    <div class="rect5"></div>
                  </div>
                </div>
                <div class="tab-pane active" id="addPerson" role="tabpanel">
                  <div class="table-box" id="tableBox">
                    <div class="table-top">
                      <div class="search-box">
                        <i class="icon icon-search" id="search-btn"></i>
                        <i class="icon icon-search-del"></i>
                        <input maxlength="50" class="form-control search-input" id="searchKepress" type="text" placeholder="搜索名称、全拼、电话号码、邮箱">
                      </div>
                      <div class="dropdown-box">
                        <button class="select-btn dropdown-toggle" id="dropdownMenu" type="button" value=""> </button>
                        <div class="dropdown-ul">
                          <span class="span-list select-active span-list-0" name="0">全部人员(<i></i>)</span>
                          <span class="span-list span-list-1" name="1">已激活(<i></i>)</span>
                          <span class="span-list span-list-2" name="2">未激活(<i></i>)</span>
                          <span class="span-list span-list-3" name="3">已禁用(<i></i>)</span>
                          <span class="span-list span-list-4" name="4">未分配部门(<i></i>)</span>
                        </div>
                      </div>
                      <div class="btn-group btn-box" role="group" aria-label="Basic example">
                        <button class="btn btn-primary" type="button" data-toggle="modal" data-target="#reviseModal">修改部门</button><button class="btn btn-primary" type="button">禁用</button><button class="btn btn-primary" type="button">移除</button>
                      </div>
                    </div>
                    <div class="table-header">
                      <table class="table">
                        <thead>
                          <tr>
                            <th>
                              <div class="custom-control custom-checkbox" id="selectAll">
                                <input class="custom-control-input" type="checkbox" name="checkname"><span class="custom-control-indicator"></span>
                              </div>
                            </th>
                            <th>姓名</th>
                            <th>手机</th>
                            <th>部门</th>
                            <th>邮箱</th>
                            <th>职位</th>
                          </tr>
                        </thead>
                      </table>
                    </div>
                    <div class="table-content"></div>
                  </div>
                  <div class="page-Box" id="page-Box"></div>
                </div>
                <div class="tab-pane invite" id="inviteBox" role="tabpanel"><span role="tablist"><a class="icon icon-close closed nav-link" data-toggle="tab" href="#addPerson" role="tab" aria-controls="addPerson"></a></span>
                  <p class="invite-title">将二维码发送给同事，同事可微信扫码后，用邀请码快速加入企业。</p>
                  <div class="qr-code"><img src="#">
                    <p class="company-name"></p>
                  </div>
                  <div class="invite-code">邀请码<input class="form-control form-control-lg" id="codeText" type="text" value="" readonly><button id="codeBtn"><i class="icon icon-refresh"></i>刷新</button>
                  </div>
                  <p class="copy">或者复制以下邀请信息发送给同事
                    <button id="copyBtn">复制</button>
                  </p>
                  <div class="link-box"><div class="link-box-top" style="margin-bottom: 0px">公司从今日起启动企云会管理我们的会议，会议效率是组织效率的公因数，让我们一起科学的管理会议。</div><div class="link-box-bottom">请在浏览器打开连接,加入企业:<i class="open-link-btn" style="color: #2c9be8;word-wrap: break-word;"></i></div>
                    <textarea class="textarea-text"></textarea>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="form-box-style personal-component-hiding" id="form-box">
            <form class="imgUpload" enctype="multipart/form-data">
              <h2 class="addperson-title" >新增人员</h2>
              <%--<div class="form-group btn-change-photo">
                <span class="img-init">选择头像</span>
                <input type="file" accept="image/jpg,image/png,image/jpeg,image/bmp" name="fileName">
              </div>--%>
            </form>
            <form class="addPersonInfo" data-toggle="validator" method="post" enctype="multipart/form-data">
              <div class="form-group">
                <input class="form-control form-control-lg" type="text" placeholder="姓名"  name="name" data-validate="name" maxlength="10">
              </div>
              <div class="form-group">
                <input class="form-control form-control-lg" type="text" placeholder="手机号" name="phone" data-validate="phone">
              </div>
              <div class="form-group">
                <input class="form-control form-control-lg" type="text" placeholder="邮箱"  name="email" data-validate="email" maxlength="50">
              </div>
              <%--<div id="addDepartment" class="form-group form-control-lg form-control col personnel-stop-propagation" data-toggle="personnel" data-type="4">
                   <span class="value">部门</span>
                   <input name="organizer" type="hidden" data-validate="organizer" value="" >
             </div>--%>
              <div id="addDepartment" class="form-group form-control" style="padding: 0;position: relative;">
                <span class="trigger input-control" style="width: 100%;display: inline-block;padding: 0.625rem 0.625rem;">部门</span>
                <input name="organizer" type="hidden" data-validate="organizer" value="">
              </div>
              <div class="form-group dropdown">
                  <div class="form-control form-control-lg sex-btn" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true">
                    <span id="addPersonSex">男</span>
                    <i class="icon icon-organiz-down"></i>
                    <input name="sex" type="hidden" class="hidden-val" value="">
                  </div>
                  <div class="dropdown-menu start-select-box" aria-labelledby="addSex">
                    <a class="dropdown-item" name="boy">男</a><a class="dropdown-item" name="lady">女</a>
                  </div>
              </div>
              <div class="form-group">
                <input class="form-control form-control-lg" type="text" placeholder="职位"  name="position" data-validate="position" maxlength="20">
              </div>
              <div class="warningBox">
                <i class="fa fa-exclamation-circle">图标</i>
                <label id="warmLabel">提示性信息</label>
              </div>
              <p class="error-msg"></p>
              <div class="form-group addperson-btns">
                <button type="button" class="btn btn-lg btn-primary cancel-btn">取消</button>
                <button type="submit" class="btn btn-lg btn-primary save-btn">保存并继续</button>
              </div>
            </form>
            <form class="savePersonInfo" data-toggle="validator">
              <div class="form-group form-top">
                <img class="person-logo" src="">
                <div class="img-message">
                  <p>
                    <span class="person-name"></span>
                    <span>
                      <i class="icon icon-group"></i>管理员
                    </span>
                  </p>
                  <p>
                    <i class="icon icon-phone"></i>
                    <i class="phone-number"></i>
                  </p>
                  <p></p>
                </div>
                <div class="dropdown more-btn">
                  <button class="btn btn-secondary" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    更多
                    <i class="icon icon-info-down"></i>
                  </button>
                  <div class="dropdown-menu dropdown-list" aria-labelledby="dropdownMenu1">
                    <a class="dropdown-item" href="#" name=""></a>
                    <a class="dropdown-item" href="#" name="删除">
                      <i class="icon icon-delete"></i>移除
                    </a>
                  </div>
                </div>
              </div>
              <div class="form-group">
                <label for="formGroupExampleInput">姓名</label>
                <input class="form-control form-control-lg" type="text" id="formGroupName" placeholder="请输入姓名" value="" name="name" data-validate="name" maxlength="10">
              </div>
              <div class="form-group">
                <label for="formGroupExampleInput">邮箱</label>
                <input class="form-control form-control-lg" type="text" id="formGroupEmail" placeholder="请输入邮箱" value="" name="email" data-validate="email" maxlength="50">
              </div>
              <div class="form-group">
                <label for="formGroupExampleInput">部门</label>
                <%-- <div id="addDepartmentChange" class="form-control form-control-lg personnel-stop-propagation" data-toggle="personnel" data-type="4">
                    <span class="value">部门</span>
                    <input name="organizer" type="hidden" data-validate="organizer" value="">
                  </div>--%>
                <div id="addDepartmentChange" class="form-control" style="padding: 0;position: relative;">
                  <span class="trigger input-control" style="width: 100%;display: inline-block;padding: 0.625rem 0.625rem;">请选择部门</span>
                  <input name="organizer" type="hidden" data-validate="organizer" value="">
                </div>
              </div>
              <div class="form-group dropdown">
                <label for="formGroupExampleInput">性别</label>
                <div class="form-control form-control-lg sex-btn" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true">
                  <span id="formGroupSex"></span>
                  <i class="icon icon-organiz-down"></i>
                  <input name="sex" type="hidden" class="hidden-val" value="">
                </div>
                <div class="dropdown-menu start-select-box" aria-labelledby="formGroupSex">
                  <a class="dropdown-item" name="男">男</a><a class="dropdown-item" name="女">女</a>
                </div>
              </div>
              <div class="form-group">
                <label for="formGroupExampleInput">职位</label>
                <input class="form-control form-control-lg" type="text" id="formGroupPosition" placeholder="请输入职位" value="" name="position" data-validate="position" maxlength="20">
              </div>
              <p class="error-msg"></p>
              <div class="form-group">
                <button type="submit" class="btn btn-lg btn-primary save-btn">保存</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade personal-component-hiding" id="reviseModal" tabindex="-1" role="dialog" aria-labelledby="reviseModalLabel" aria-hidden="true" data-backdrop="static">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header title-box">
            <h5 class="modal-title" id="reviseModalLabel">修改部门</h5>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <%-- <div class="modal-body">
            <button class="department-input" placeholder="请选择部门">请选择部门</button>
          </div>
          <div id="addDepartmentRecompose" class="form-control form-control-lg personnel-stop-propagation" style="margin: 24px 0 31px 0;" data-toggle="personnel" data-type="4">
            <span class="value">请选择部门</span>
            <input name="organizer" type="hidden" data-validate="organizer" value="">
          </div>--%>
          <div id="addDepartmentRecompose" class="modal-body" style="margin: 24px 0 31px 0;padding:0;position: relative;">
            <span class="trigger input-control department-input" style="width: 100%;display: inline-block;padding: 0.625rem 0.625rem;">请选择部门</span>
            <input name="organizer" type="hidden" data-validate="organizer" value="">
          </div>
          <div class="modal-footer">
            <button class="btn btn-lg btn-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn btn-lg btn-primary" type="button">确定</button>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="removeModal" tabindex="-1" role="dialog" aria-labelledby="removeModalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header title-box">
            <h5 class="modal-title" id="removeModalLabel">移除人员</h5>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body" style="padding:21px 0  27px"></div>
          <div class="modal-footer">
            <button class="btn btn-lg btn-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn btn-lg btn-primary remove-btn" type="button" data-dismiss="modal" aria-label="Close">移除</button>
          </div>
        </div>
      </div>
    </div>
  <div class="modal fade" id="deleteOrga" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <form id="deleteOrgaForm">
          <div class="modal-header">
            <div class="modal-title">删除部门</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body" style="padding: 21px 0 27px">
            <%--<div class="form-group">--%>
              <label class="form-control-label" style="margin-bottom:0;">确定要删除<span class="room_name"></span>吗？</label>
              <input class="name1" name="id" type="hidden">
            <%--</div>--%>
          </div>
          <div class="modal-footer">
            <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn btn-lg btn-danger" type="submit">删除</button>
          </div>
        </form>
      </div>
    </div>
  </div>
    <div class="modal fade" id="loadModal" tabindex="-1" role="dialog" aria-labelledby="loadModalLabel" aria-hidden="true" data-backdrop="static">
      <div class="modal-dialog" role="document" style="margin: 125px auto;">
        <div class="modal-content" style="padding: 55px 0.5rem;" >
          <div class="modal-body" style="padding: 15px 154px;line-height:60px;">
            <div class="load1">
              <div class="rect1"></div>
              <div class="rect2"></div>
              <div class="rect3"></div>
              <div class="rect4"></div>
              <div class="rect5"></div>
            </div><span style="float:left;">导入中，请稍侯…</span>
          </div>
        </div>
      </div>
    </div>
    <input type="hidden" id ="wechatHost" value="<%=host%>"/>
    <script src="<%=basePath%>static/plugin/underscore.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.ztree.all.min.js"></script>
    <script src="<%=basePath%>static/scripts/pages.js"></script>
    <script src="<%=basePath%>static/scripts/personsDepartments.js"></script>
    <script type="text/javascript" src="<%=basePath%>static/js/common/formValidator.js"></script><%--表单验证--%>
    <script src="<%=basePath%>static/scripts/directories.js"></script>
  </body>
</html>