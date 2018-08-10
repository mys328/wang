<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="description" content="">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>会议显示管理</title>
  <link rel="apple-touch-icon" href="apple-touch-icon.png">
  <!-- Place favicon.ico in the root directory-->
  <!-- build:css static/styles/dashboard.css-->
  <%@include file="/WEB-INF/jsp/common/common.jsp" %>
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
  <!-- build:css static/styles/programManagement.css-->
  <link rel="stylesheet" href="<%=basePath%>static/styles/programManagement.css">
  <link rel="stylesheet" href="<%=basePath%>static/styles/terminalLog.css">
  <link rel="stylesheet" href="<%=basePath%>static/styles/zTreeStyle.css">
  <link rel="stylesheet" href="<%=basePath%>static/styles/label.css">
  <link rel="stylesheet" href="<%=basePath%>static/styles/tenement.css">
  <link rel="stylesheet" href="<%=basePath%>static/styles/terminalVersion.css">
  <link rel="stylesheet" href="<%=basePath%>static/styles/logDetail.css">
  <!-- endbuild-->
</head>
<body><!--[if lt IE 10]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
<div class="wrapper">
  <%@ include file="/WEB-INF/jsp/common/left.jsp"%>
  <div class="main-panel">
    <nav class="navbar fixed-top navbar-expand-lg navbar-light">
      <div class="d-flex flex-row justify-content-between ">
        <ul class="tab-business" id="meetingShowNav" role="tablist"> <a class="nav-link active" data-toggle="tab" href="#terminalVersion" role="tab">终端版本管理</a><a class="nav-link" data-toggle="tab" href="#eventManaging" role="tab">节目管理</a><a class="nav-link" data-toggle="tab" href="#terminalCustomer" role="tab">终端客户</a></ul>
        <%@ include file="/WEB-INF/jsp/common/top.jsp"%>
      </div>
    </nav>
    <div class="content program-show">
      <div class="tab-content">
        <div class="active tab-pane" id="terminalVersion" role="tabpanel" aria-expanded="false">
          <div class="top">
            <div class="form-control input-search"><i class="icon icon-search"></i>
              <input class="version-search" type="text" placeholder="搜索文件名称、版本编号等"  maxlength="50"><i class="icon icon-search-del del-searchList"></i>
            </div><span class="col-label name">文件数:</span><span class="col-label num">   </span>
            <div class="btn btn-primary btn-sm uploadVersions" data-toggle="modal">上传版本文件 <label class="uploadVersions label"><input class="uploadVersions upload" type="file" accept=".apk" style="
    display:none;"></label></div>
            <button class="btn btn-primary btn-sm updateRecord" data-toggle="modal" data-target="#updateRecord">全部更新记录</button>
          </div>
          <div class="version-content"></div>
          <div class="test"></div>
          <form class="version-info-box" data-toggle="validator" method="post" enctype="multipart/form-data">
            <div class="inner-box"></div>
          </form>
          <div class="modal fade" id="setBeta" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <div class="modal-title">设置内测版本</div>
                  <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
                </div>
                <div class="modal-body">
                  <input name="id" type="hidden">
                  <label>确定设置版本为</label><span class="blue version-num">1.1.2</span><span>的更新文件</span><span class="blue version-name"></span><span>为终端内测版本吗？</span>
                </div>
                <div class="modal-footer">
                  <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
                  <button class="btn btn-primary setBeta" type="button">设为内测</button>
                </div>
              </div>
            </div>
          </div>
          <div class="modal fade" id="cancelBeta" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <div class="modal-title">取消内测</div>
                  <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
                </div>
                <div class="modal-body">
                  <input name="id" type="hidden">
                  <label>确定取消版本为</label><span class="blue version-num">1.1.2</span><span>的更新文件</span><span class="blue version-name"></span><span>?</span>
                </div>
                <div class="modal-footer">
                  <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
                  <button class="btn btn-danger cancelBeta" type="button">确定</button>

                </div>
              </div>
            </div>
          </div>
          <div class="modal fade" id="delModal" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <div class="modal-title">删除版本文件</div>
                  <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
                </div>
                <div class="modal-body">
                  <input name="id" type="hidden">
                  <label>确定删除版本为</label><span class="blue version-num">1.1.2</span><span>的更新文件</span><span class="blue version-name"></span><span>?</span>
                </div>
                <div class="modal-footer">
                  <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
                  <button class="btn btn-danger btn-version-delete" type="button">删除</button>
                </div>
              </div>
            </div>
          </div>
          <div class="modal fade" id="table-loading" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static" data-animation="false">
            <div class="modal-dialog" role="document">
              <div class="modal-content">
                    <span class="load1 table-loading">
                      <div class="rect1"></div>
                      <div class="rect2"></div>
                      <div class="rect3"></div>
                      <div class="rect4"></div>
                      <div class="rect5"></div>
                    </span>
                <span class="load1 table-text">
                      正在上传,请耐心等待...
                    </span>
              </div>
            </div>
          </div>
          <div class="modal fade" id="sendVersion" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <div class="modal-title">发布版本</div>
                  <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
                </div>
                <div class="modal-body">
                  <input name="id" type="hidden">
                  <label>确定发布版本为</label><span class="blue version-num">1.1.2</span><span>的更新文件</span><span class="blue version-name"></span><span>为客户最新终端版本吗？</span>
                </div>
                <div class="modal-footer">
                  <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
                  <button class="btn btn-primary btn-send" type="button">发布</button>
                </div>
              </div>
            </div>
          </div>
          <div class="modal fade" id="updateNumModal" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static">
            <div class="modal-dialog" role="document">
              <div class="modal-content">
                <div class="update-header">
                  <div class="form-control input-search"><i class="icon icon-search"></i>
                    <input class="search1" type="text" placeholder="搜索终端标识、租户信息等"  maxlength="50"><i class="icon icon-search-del del-updateList"></i>
                  </div><span class="col-label name">更新数量 :</span><span class="col-label num">816 </span>
                  <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
                </div>
                <div class="modal-body">
                  <div class="table-update-list">
                    <div class="update-list"></div>
                  </div>
                </div>
                <div class="modal-footer">
                  <div class="page-Box" id="page-Box"></div>
                </div>
              </div>
            </div>
          </div>
          <div class="bottom"></div>
        </div>

        <div class="tab-pane" id="eventManaging" role="tabpanel" aria-expanded="false">
          <form class="program-form" id="formBox"></form>
          <div class="managing-left">
            <ul class="ztree-title tab-business" role="tablist">
              <a class="nav-link active" data-toggle="tab" href="#" role="tab" data-type="0">节目标签</a>
              <a class="nav-link" data-toggle="tab" href="#" role="tab" data-type="1">定制节目</a>
            </ul>
            <ul class="ztree" id="treeDemo"></ul>
            <button class="btn btn-primary add-label" id="addLabel" type="button">
              <i class="icon icon-add-personnel"></i>添加标签
            </button>
          </div>
          <div class="managing-top">
            <div class="form-control input-search"><i class="icon icon-search"></i>
              <input id="search" type="text" placeholder="搜索节目名称"><i class="icon icon-search-del" id="del-searchList"></i>
            </div><span class="versions-info">版本：<i></i></span>
            <div class="btns-box" id="appendTag">
              <button class="btn btn-primary btn-sm append-btn tags" data-toggle="modal" data-target="#addProgramLabel">添加节目标签</button><button class="btn btn-primary btn-sm delete-btn">删除节目</button><button class="btn btn-primary btn-sm update-log">更新日志</button><label class="btn btn-primary btn-sm update update-btn">上传节目<input type="file" name="myfile" value="" accept="application/zip,application/x-zip,application/x-zip-compressed"></label><button class="btn btn-primary btn-sm test-btn" data-toggle="modal" data-target="#closedBeta">进行内测</button><button class="btn btn-primary btn-sm synchronization-btn" data-toggle="modal" data-target="#synchronization">同步到租户</button>
            </div>
          </div>
          <div class="managing-right"><div class="program-box"></div></div>
        </div>
        <div class="tab-pane" id="terminalCustomer" role="tabpanel" aria-expanded="false">
          <div class="top">
            <div class="form-control input-search"><i class="icon icon-search"></i>
              <input id="search" type="text" placeholder="搜索租户名称、联系电话、联系人" maxlength="100"><i class="icon icon-search-del" id="del-searchList"></i>
            </div><span class="col-label customer-name">客户数:</span><span class="col-label customer-num"></span>
            <button class="btn btn-primary btn-sm veiw-error-log" data-toggle="modal" data-target="#updateRecord">查看全部错误日志</button>
          </div>
          <div class="tabel-out-box"></div>
          <div class="innerPageBox bottom page-Box" id="pages"></div>
          <div class="innerPageBox bottom page-Box" id="pagesDetail"></div>
        </div>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="deleteProgram" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <div class="modal-title">删除节目</div>
        <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
      </div>
      <div class="modal-body">
        <label>确定删除所选中的节目吗？</label>
      </div>
      <div class="modal-footer">
        <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
        <button class="btn btn-danger btn-affirm-delete" type="button" data-dismiss="modal">删除</button>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="closedBeta" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <div class="modal-title">进行内测</div>
        <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
      </div>
      <div class="modal-body">
        <label>确定内测当前节目吗？</label>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" type="button" data-dismiss="modal">取消</button>
        <button class="btn btn-primary btn-affirm-test"  type="button" data-dismiss="modal">进行内测</button>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="previewModal" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static">
  <div class="modal-dialog" role="document">
    <div class="modal-content proof-box">
      <button class="close" type="button" data-dismiss="modal" aria-label="Close">
        <div class="icon icon-close" aria-hidden="true"></div>
      </button>
      <div class="modal-body"><span class="loader-text">正在生成效果图，请稍等</span>
        <iframe id="iframeShow" src=""></iframe>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="deleteLabel" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <form id="deleteLabelForm">
        <div class="modal-header">
          <div class="modal-title">删除标签</div>
          <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
        </div>
        <div class="modal-body" style="padding: 21px 0 27px;">
          <label class="form-control-label" style="margin-bottom:0;">确定要删除<span class="label-name"></span>吗？
            <input class="name1" name="id" type="hidden" value="cb56fc57dda346e59d7abd416dd95a5f">
          </label>
        </div>
        <div class="modal-footer">
          <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
          <button class="btn btn-lg btn-danger" type="submit">删除</button>
        </div>
      </form>
    </div>
  </div>
</div>
<div class="modal fade" id="selectLssee" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <div class="modal-title">请选择定制节目指定的租户</div>
        <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
      </div>
      <div class="modal-body" id="custom">
        <input class="form-control form-control-lg select-custom" type="text" name="" maxlength="200" />
      </div>
      <p class="warningBox">
        <i class="icon icon-error"></i><label id="warmLabel">请选择节目指定租户</label>
      </p>
      <div class="modal-footer">
        <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
        <label class="btn btn-primary customUpload">上传节目<input type="file" name="myfile" value="" accept="application/zip,application/x-zip,application/x-zip-compressed"></label>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="synchronization" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <div class="modal-title">节目版本信息</div>
        <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
      </div>
      <div class="modal-body">
        <label class="form-control-label">节目版本编号<span class="versions-name"></span></label>
        <textarea class="form-control" placeholder="请输入版本更新内容" ；length="1000" name="cancelReason"></textarea>
      </div>
      <div class="modal-footer">
        <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
        <button class="btn btn-lg btn-primary publish-btn">同步到租户</button>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="viewLog" tabindex="-1" role="dialog"  aria-hidden=”true” data-backdrop="static">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <div class="modal-title">错误信息</div>
        <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
      </div>
      <div class="modal-body" style="word-wrap: break-word;height: 347px;overflow-y: auto;padding:0;font-size:14px;margin: 18px 0 13px;">暂无日志内容</div>
    </div>
  </div>
</div>
<script src="<%=basePath%>static/plugin/jquery.ztree.all.min.js"></script>
<script src="<%=basePath%>static/scripts/label.js"></script>
<script src="<%=basePath%>static/scripts/plugins/pages.js"></script>
<script src="<%=basePath%>static/scripts/plugins/tenement.js"></script>
<script src="<%=basePath%>static/scripts/logDetail.js"></script>
<script src="<%=basePath%>static/scripts/programManagement.js"></script>
<script src="<%=basePath%>static/scripts/terminalVersion.js"></script>
<script src="<%=basePath%>static/scripts/terminalLog.js"></script>
</body>
</html>