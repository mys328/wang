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
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <script src="<%=basePath%>static/scripts/jump.js"></script>
    <!-- build:css static/styles/room.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/zTreeStyle1.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/pages.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/room.css">
    <!-- endbuild-->
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
              <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#home" role="tab">会议室台账</a></li>
              <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#profile" role="tab">预订设置</a></li>
            </ul>
            <%@ include file="/WEB-INF/jsp/common/top.jsp"%>
          </div>
        </nav>
        <div class="content room">
          <div class="tab-content">
            <div class="tab-pane active" id="home" role="tabpanel">
              <div class="top">
                <form class="search">
                  <div class="form-control input-search">
                    <i class="icon icon-search"></i>
                    <input type="search"  placeholder="搜索名称、区域、容量、设备" id="searchRoom">
                    <i class="icon icon-search-del" id="del-searchList"></i>
                  </div>
                </form>
                <button class="btn btn-primary" id="qrCode" data-toggle="modal" data-target="#printModal">打印二维码</button>
                <button class="btn btn-primary btn-add-room">添加会议室</button>
              </div>
              <div class="left">
                <div class="treeBox">
                  <ul class="ztree" id="treeDemo"></ul>
                </div>
              </div>
              <div class="right">
                <div class="table-title">
                  <table class="table table-hover list-rooms">
                    <thead>
                    <tr>
                      <th>会议室</th>
                      <th>操作权限</th>
                      <th>状态</th>
                      <th>操作</th>
                    </tr>
                    </thead>
                  </table>
                </div>
                <div class="list"></div>
                <div class="page-Box" id="page-Box"></div>
              </div>
            </div>
            <div class="tab-pane" id="profile" role="tabpanel"></div>
          </div>
          <div class="add-room"> 
            <h6 class="add-room-tittle">添加会议室</h6>
            <form id="roomform">
              <div class="form-group">
                <label>会议室名称</label>
                <input class="form-control form-control-lg" type="text" placeholder="填写会议室名称，限20个字符" name="room_name" data-validate="name" maxlength='20' >
              </div>
              <div class="form-group">
                <label>会议室区域</label>
                <div class="dropdown">
                  <div class="form-control form-control-lg" id="areaType" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true">
                    <span id="type-name"></span>
                    <span class="icon icon-organiz-down"></span>
                    <input name="areaId" type="hidden" class="areaId">
                  </div>
                  <div class="dropdown-menu area-type" aria-labelledby="areaType">
                  </div>
                </div>
                <%--<select class="form-control" name="room_select"></select>--%>
              </div>
              <div class="form-group">
                <label>容量</label>
                <input class="form-control form-control-lg"  placeholder="填写会议室可容纳的人数，例如20" name="room_capacity" data-validate="capacity">
              </div>
              <div class="form-group">
                <label>物理位置</label>
                <input class="form-control form-control-lg" type="text" placeholder="选填，可填写会议室的具体所在位置，如A座101室" name="room_location" maxlength="100">
              </div>
              <div class="form-group row">
                <label>会议设备</label>
                <label class="col custom-control custom-checkbox">
                  <input class="custom-control-input" type="checkbox" value="0" name="devices0"><span class="custom-control-indicator"></span><span class="custom-control-description">扩音</span>
                </label>
                <label class="col custom-control custom-checkbox">
                  <input class="custom-control-input" type="checkbox" value="1" name="devices1"><span class="custom-control-indicator"></span><span class="custom-control-description">显示</span>
                </label>
                <label class="col custom-control custom-checkbox">
                  <input class="custom-control-input" type="checkbox" value="2" name="devices2"><span class="custom-control-indicator"></span><span class="custom-control-description">白板</span>
                </label>
                <label class="col custom-control custom-checkbox">
                  <input class="custom-control-input" type="checkbox" value="3" name="devices3"><span class="custom-control-indicator"></span><span class="custom-control-description">视频会议</span>
                </label>
              </div>
              <div class="form-group row isPay">
                <label>预订需审核</label>
                <label class="custom-control custom-checkbox">
                  <input class="custom-control-input" type="checkbox" name="audit" value="1"><span class="custom-control-indicator"></span>
                </label>
                <p class="tips">勾选此项后，该会议室预订申请需经会议室管理员审批后使用</p>
              </div>
              <div class="form-group row isPay">
                <label>预订权限<a class="icon icon-question" tabindex="0" data-toggle="popover" data-trigger="focus" data-placement="top" data-content="由企业管理员在企业设置-&gt;管理设置中设置角色人员"></a></label>
                <label class="col custom-control custom-checkbox">
                  <input class="custom-control-input" type="checkbox" value="0" name="limitMembers0"><span class="custom-control-indicator"></span><span class="custom-control-description">全部人员</span>
                </label>
                <label class="col custom-control custom-checkbox">
                  <input class="custom-control-input" type="checkbox" value="1" name="limitMembers1"><span class="custom-control-indicator"></span><span class="custom-control-description">会议室管理员</span>
                </label>
                <label class="col custom-control custom-checkbox">
                  <input class="custom-control-input" type="checkbox" value="2" name="limitMembers2"><span class="custom-control-indicator"></span><span class="custom-control-description">预订专员</span>
                </label>
              </div>
              <div class="form-group row isPay_false">
                <label class="labelGray">预订需审核</label>
                <p class="tips">免费版本不支持此功能，请联系企业管理员购买专业版</p>
              </div>
              <div class="form-group row isPay_false">
                <label class="labelGray">预订权限</label>
                <p class="tips">免费版本不支持此功能，请联系企业管理员购买专业版</p>
              </div>
              <div class="form-group row pictureBox">
                <div class="room-picture">会议室图片</div>
                <p class="tips">请上传小于5M的图片，推荐尺寸400×300</p><span class="btn-delete-photo" onclick="delete_photo()">删除</span><span class="btn-change-photo">重新上传
                  <label class="change-photo-btn label"><input class="change-photo-btn upload" type="file" accept="image/jpg,image/png,image/jpeg,image/bmp" style="display:none;"></label></span>
                <div class="col-12">
                  <div class="photo"><img id="room_image" src="<%=basePath%>static/images/default@1x.png" name="room_image"><span class="btn-select-photo"><span class="icon icon-addimg"></span>选择图片
                     <label class="change-photo-btn label"><input class="change-photo-btn upload" type="file" accept="image/jpg,image/png,image/jpeg,image/bmp" style="display:none;"></label>
                      <input name="id" type="hidden"></span></div>
                </div>
              </div>
              <p class="error-msg"></p>
              <div class="text-right"><a class="btn-cancel" href="#" onclick="btn_cancel()">取消</a><button class="btn btn-lg btn-primary saveBtn" id="save-btn" type="submit">保存并继续</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="printModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content qrcodeModal">
          <form id="downloadQrcode">
          <div class="modal-header">
            <div class="modal-title">打印二维码</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <div class="print-qrcode"><span class="ribbon">样例</span>
              <h6 class="areaNmae">第一会议室</h6><img src="<%=basePath%>static/images/code.png">
              <p>临时会议预订，请扫描二维码即扫即订</p>
            </div>
            <p class="print-qrcode-tips">打印会议室二维码，可张贴在会议室门口，即扫即订</p>
            <input name="id" type="hidden">
          </div>
          <div class="modal-footer">
            <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn btn-lg btn-primary"  type="submit">下载全部</button>
          </div>
          </form>
        </div>
      </div>
    </div>
    <div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <form id="deleteForm">
            <div class="modal-header">
              <div class="modal-title">删除会议室</div>
              <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
            </div>
            <div class="modal-body" style="padding: 21px 0 27px">
              <%--<div class="form-group">--%>
                <label class="form-control-label" style="margin-bottom: 0">确定删除<span class="room_name"></span>吗？</label>
                <input name="id" type="hidden">
             <%-- </div>--%>
            </div>
            <div class="modal-footer">
              <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
              <button class="btn btn-lg btn-danger" type="submit">删除</button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <div class="modal fade" id="deleteArea" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <form id="deleteAreaForm">
            <div class="modal-header">
              <div class="modal-title">删除区域</div>
              <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label class="form-control-label">删除<span class="room_name"></span>,区域下的会议室会被一起删除，确认删除吗？</label>
                <input class="name1" name="id" type="hidden">
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
              <button class="btn btn-lg btn-danger" type="submit">删除</button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <div class="modal fade" id="enabledModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <form id="enabledForm">
            <div class="modal-header">
              <div class="modal-title">启用会议室</div>
              <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label class="form-control-label">确定启用<span class="room_name"></span>吗？</label>
                <input name="id" type="hidden">
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
              <button class="btn btn-lg btn-primary" type="submit">启用          </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <div class="modal fade" id="disableModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content room_break">
          <form id="disableForm">
            <div class="modal-header">
              <div class="modal-title">停用会议室</div>
              <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label class="form-control-label break_tittle">确定停用<span class="room_name">第一会议室</span>吗？</label><span class="room_id" style="display:none"></span>
                <div class="input-daterange input-group" id="datepicker">
                  <input class="break_time_start" type="text" name="start"><span class="break_zhi">至</span>
                  <input class="break_time_end" type="text" name="end"><span class="break_zhi">停用</span>
                </div>
                <label class="custom-control custom-checkbox input_break">
                  <input class="custom-control-input" type="checkbox" name="breaking" value="1"><span class="custom-control-indicator"></span><span class="custom-control-description">永久停用</span>
                </label>
              </div>
              <div class="form-group">
                <textarea class="form-control break_textarea" placeholder="停用原因" maxlength="200" name="textarea"></textarea>
              </div>
            </div>
            <div class="modal-footer break_footer">
              <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
              <button class="btn btn-lg btn-primary" type="submit">停用</button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <!-- build:js static/scripts/room.js-->
    <script src="<%=basePath%>static/plugin/jquery.ztree.all.min.js"></script>
    <script src="<%=basePath%>static/plugin/Sortable.min.js"></script>
    <script src="<%=basePath%>static/scripts/pages.js"></script>
    <script src="<%=basePath%>static/scripts/plugins/datepicker.js"></script>
    <script src="<%=basePath%>static/scripts/room.js"></script>
    <!-- endbuild-->
  </body>
</html>