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
    <link rel="stylesheet" href="<%=basePath%>static/styles/invoiceSend.css">
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
        <div class="content invoiceSend">
          <div class="tab-content" style="height: 100%">
            <div class="table-Box" style="height: 100%"></div>
          </div>
          <div class="page-Box" id="page-Box"></div>
          <div class="invoice-info-box send-div">
            <div class="inner-box send-div">
              <h3 class="send-div">邮寄发票信息</h3>
              <label class="title send-div">开票信息</label>
              <div class="send-div">
                <div class="show-invoice-info send-div">
                  <div class="flex send-div">
                    <label class="col-left send-div">开具类型</label>
                    <label class="col-right send-div">个人</label>
                  </div>
                  <div class="flex send-div">
                    <label class="col-left send-div">发票抬头</label>
                    <label class="col-right send-div">个人</label>
                  </div>
                  <div class="flex send-div">
                    <label class="col-left send-div">发票类型</label>
                    <label class="col-right send-div">增值税普通发票</label>
                  </div>
                </div>
                <div class="show-enterprise-common send-div">
                  <div class="flex send-div">
                    <label class="col-left send-div">开具类型</label>
                    <label class="col-right send-div">企业</label>
                  </div>
                  <div class="flex send-div">
                    <label class="col-left send-div">发票抬头</label>
                    <label class="col-right send-div" id="show-comname">北京盛科维科技发展有限公司</label>
                  </div>
                  <div class="flex send-div">
                    <label class="col-left send-div">发票类型</label>
                    <label class="col-right send-div">增值税普通发票</label>
                  </div>
                  <div class="flex send-div">
                    <label class="col-left send-div">税务证号</label>
                    <label class="col-right send-div" id="show-comtax">1110010101010</label>
                  </div>
                </div>
                <div class="show-enterprise-special send-div">
                  <div class="flex send-div">
                    <label class="col-left send-div">开具类型</label>
                    <label class="col-right send-div">企业</label>
                  </div>
                  <div class="flex send-div">
                    <label class="col-left send-div">发票抬头</label>
                    <label class="col-right send-div" id="show-spename">北京盛科维科技发展有限公司</label>
                  </div>
                  <div class="flex send-div">
                    <label class="col-left send-div">发票类型</label>
                    <label class="col-right send-div">增值税专用发票</label>
                  </div>
                  <div class="flex send-div">
                    <label class="col-left send-div">税务证号</label>
                    <label class="col-right send-div" id="show-spetax">11000001111</label>
                  </div>
                  <div class="flex send-div">
                    <label class="col-left send-div">开户银行</label>
                    <label class="col-right send-div" id="show-speBank">11000001111</label>
                  </div>
                  <div class="flex send-div">
                    <label class="col-left send-div">开户账号</label>
                    <label class="col-right send-div" id="show-speacount">11000001111</label>
                  </div>
                  <div class="flex send-div">
                    <label class="col-left send-div">注册地址</label>
                    <label class="col-right send-div" id="show-speaddress">北京市海淀区西三旗昌临813号京玺中韩文化创意园A区10号楼104</label>
                  </div>
                  <div class="flex send-div">
                    <label class="col-left send-div">联系电话</label>
                    <label class="col-right send-div" id="show-spetel">11000001111</label>
                  </div>
                </div>
              </div>
              <div class="form-group send-div">
                <label class="title send-div">快递公司</label>
                <div class="dropdown send-div">
                  <div class="form-control send-div" id="selectVersion" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true"><span class="send-div" id="version-type">顺丰</span><span class="icon icon-organiz-down send-div"></span></div>
                  <div class="dropdown-menu send-div" aria-labelledby="selectVersion"><a class="dropdown-item send-div" data-title="顺丰" data-id="顺丰">顺丰</a></div>
                </div>
              </div>
              <div class="form-group send-div">
                <label class="title send-div">快递单号</label>
                <input class="form-control send-div" id="comaddress" type="text" placeholder="请填写快递单号" maxlength="100">
              </div>
              <div class="form-group send-div">
                <button class="btn countersign-btn send-div" type="button">提交</button>
                <button class="btn  cancel-btn send-div" type="button" data-dismiss="modal">取消</button>
              </div>
            </div>
          </div>
        </div>
        <script src="<%=basePath%>static/scripts/invoiceSend.js"></script>
      </div>
    </div>
  </body>
</html>