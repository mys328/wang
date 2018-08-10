<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>定价配置</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="shortcut icon" type="image/x-icon" href="<%=basePath%>static/images/yunmeeting-logo.ico">
    <!-- Place favicon.ico in the root directory-->
    <!-- build:css static/styles/dashboard.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/dashboard.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">
    <!-- endbuild-->
    <script src="<%=basePath%>static/plugin/underscore.min.js"></script>
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <!-- build:css static/styles/pricesetting.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/pricesetting.css">
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
        <div class="content">
          <div class="header">
            <button class="btn btn-primary btn-sm save-btn" data-toggle="modal" data-target="#saveModal" disabled>保存配置</button>
            <button class="btn btn-primary btn-sm preview-btn">预览效果</button>
          </div>
          <div class="tab-content">
            <form>
              <div class="price-free">
                <div class="title">免费版配置</div>
                <div class="price-setting">
                  <div class="price-input row"><span class="col-label">员工数量</span>
                    <input class="col-form" id="free-personnumber" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="col-unit">人</span>
                  </div>
                  <div class="price-input row"><span class="col-label">会议室数</span>
                    <input class="col-form" id="free-roomnumber" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="col-unit">间</span>
                  </div>
                  <div class="price-input row"><span class="col-label">存储空间</span>
                    <input class="col-form" id="free-spacenumber" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="col-unit">GB</span>
                  </div>
                </div>
              </div>
              <div class="person-count">
                <div class="title">员工数量</div>
                <div class="price-setting parent-flex">
                  <div class="wrap-flex">
                    <div class="child-flex">
                      <div class="price-input row"><span class="col-label">单价</span>
                        <input class="col-form" id="person-price" placeholder="请输入数值" type="float" data-max="99999" data-min="0"><span class="col-unit">元/年/人</span>
                      </div>
                      <div class="price-input row"><span class="col-label">起购数量</span>
                        <input class="col-form" id="person-buynumber" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="col-unit">人</span>
                      </div>
                    </div>
                    <div class="child-flex">
                      <div class="price-input row"><span class="col-label">数量上限</span>
                        <input class="col-form" id="person-maxnumber" placeholder="请输入数值" type="int" data-max="99999" data-min="300"><span class="col-unit">人</span>
                      </div>
                      <div class="price-input row"><span class="col-label">单位步长</span>
                        <input class="col-form" id="person-stepnumber" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="col-unit">人</span>
                      </div>
                    </div>
                  </div>
                  <div class="column-flex">
                    <div class="price-input discount-set"><span class="col-label">折扣设置</span><span class="col-button" data-toggle="modal" data-target="#personDiscount">修改档位</span></div>
                    <div class="wrap-flex discount-info" id="person-discount"></div>
                  </div>
                </div>
              </div>
              <div class="room-count">
                <div class="title">会议室数</div>
                <div class="price-setting parent-flex">
                  <div class="wrap-flex">
                    <div class="child-flex">
                      <div class="price-input row"><span class="col-label">单价</span>
                        <input class="col-form" id="room-price" placeholder="请输入数值" type="float" data-max="99999" data-min="0"><span class="col-unit">元/年/间</span>
                      </div>
                      <div class="price-input row"><span class="col-label">免费数量</span>
                        <input class="col-form" id="room-freenumber" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="col-unit">间</span>
                      </div>
                    </div>
                    <div class="child-flex">
                      <div class="price-input row"><span class="col-label">数量上限</span>
                        <input class="col-form" id="room-maxnumber" placeholder="请输入数值" type="int" data-max="99999" data-min="100"><span class="col-unit">间</span>
                      </div>
                      <div class="price-input row"><span class="col-label">单位步长</span>
                        <input class="col-form" id="room-stepnumber" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="col-unit">间</span>
                      </div>
                    </div>
                  </div>
                  <div class="column-flex">
                    <div class="price-input discount-set row" style="margin-top:7px;"><span class="col-label">折扣设置</span>
                      <div class="form-check form-check-inline">
                        <input class="form-check-input" id="roomRadio1" type="radio" name="roomRadioOptions" value="0"><label class="form-check-label" for="roomRadio1">跟随员工人数打折</label>
                      </div>
                      <div class="form-check form-check-inline">
                        <input class="form-check-input" id="roomRadio2" type="radio" name="roomRadioOptions" value="1"><label class="form-check-label" for="roomRadio2">会议室数档位折扣</label>
                      </div>
                      <div class="form-check form-check-inline">
                        <input class="form-check-input" id="roomRadio3" type="radio" name="roomRadioOptions" value="2"><label class="form-check-label" for="roomRadio3">无折扣</label>
                      </div>
                    </div>
                    <div class="wrap-flex discount-info">
                      <div class="price-input discount-set"><span class="col-button" data-toggle="modal" data-target="#roomDiscount">修改档位</span></div>
                    </div>
                    <div class="wrap-flex discount-info" id="room-discount">         </div>
                  </div>
                </div>
              </div>
              <div class="storage-space">
                <div class="title">存储空间</div>
                <div class="price-setting parent-flex">
                  <div class="wrap-flex">
                    <div class="child-flex">
                      <div class="price-input row"><span class="col-label">单价</span>
                        <input class="col-form" id="space-price" placeholder="请输入数值" type="float" data-max="99999" data-min="0"><span class="col-unit">元/年/GB</span>
                      </div>
                      <div class="price-input row"><span class="col-label">免费数量</span>
                        <input class="col-form" id="space-freenumber" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="col-unit">GB</span>
                      </div>
                    </div>
                    <div class="child-flex">
                      <div class="price-input row"><span class="col-label">数量上限</span>
                        <input class="col-form" id="space-maxnumber" placeholder="请输入数值" type="int" data-max="99999" data-min="100"><span class="col-unit">GB</span>
                      </div>
                      <div class="price-input row"><span class="col-label">单位步长</span>
                        <input class="col-form" id="space-stepnumber" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="col-unit">GB</span>
                      </div>
                    </div>
                  </div>
                  <div class="column-flex">
                    <div class="price-input discount-set row" style="margin-top:7px;"><span class="col-label">折扣设置</span>
                      <div class="form-check form-check-inline">
                        <input class="form-check-input" id="spaceRadio1" type="radio" name="spaceRadioOptions" value="0"><label class="form-check-label" for="spaceRadio1">跟随员工人数打折</label>
                      </div>
                      <div class="form-check form-check-inline">
                        <input class="form-check-input" id="spaceRadio2" type="radio" name="spaceRadioOptions" value="1"><label class="form-check-label" for="spaceRadio2">存储空间档位折扣</label>
                      </div>
                      <div class="form-check form-check-inline">
                        <input class="form-check-input" id="spaceRadio3" type="radio" name="spaceRadioOptions" value="2"><label class="form-check-label" for="spaceRadio3">无折扣</label>
                      </div>
                    </div>
                  </div>
                  <div class="wrap-flex discount-info">
                    <div class="price-input discount-set"><span class="col-button" data-toggle="modal" data-target="#spaceDiscount">修改档位</span></div>
                  </div>
                  <div class="wrap-flex discount-info" id="space-discount"></div>
                </div>
              </div>
              <div class="time-length"> 
                <div class="title">时长</div>
                <div class="price-setting parent-flex">
                  <div class="wrap-flex">
                    <div class="price-input discount-set"><span class="col-button" data-toggle="modal" data-target="#timeDiscount">修改档位</span></div>
                  </div>
                  <div class="wrap-flex" id="time-discount" style="margin-top:11px;"></div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="saveModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">保存定价配置</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body"><span>确定保存当前定价配置吗？</span></div>
          <div class="modal-footer">
            <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn btn-primary" type="button">保存</button>
          </div>
        </div>
      </div>
    </div>
    <div class="change-discount modal fade" id="personDiscount" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">员工数量档位设置</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <div class="parent-flex">
              <ul class="discount-stall">
                <li class="row"><span class="col-label">满</span>
                  <input class="col-form stall-number" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="col-label">人打</span>
                  <input class="col-form discount-number" placeholder="请输入数值" type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract">-</span>
                </li>
              </ul>
            </div><span class="btn-span addDiscount">添加档位</span>
          </div>
          <div class="modal-footer">
            <button class="btn btn-primary" type="button">修改</button>
          </div>
        </div>
      </div>
    </div>
    <div class="change-discount modal fade" id="roomDiscount" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">会议室数档位设置</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <div class="parent-flex">
              <ul class="discount-stall">
                <li class="row"><span class="col-label">满</span>
                  <input class="col-form stall-number" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="col-label">间打</span>
                  <input class="col-form discount-number" placeholder="请输入数值" type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract">-</span>
                </li>
              </ul>
            </div><span class="btn-span addDiscount">添加档位</span>
          </div>
          <div class="modal-footer">
            <button class="btn btn-primary" type="button">修改</button>
          </div>
        </div>
      </div>
    </div>
    <div class="change-discount modal fade" id="spaceDiscount" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">存储空间档位设置</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <div class="parent-flex">
              <ul class="discount-stall">
                <li class="row"><span class="col-label">满</span>
                  <input class="col-form stall-number" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="col-label">GB打</span>
                  <input class="col-form discount-number" placeholder="请输入数值" type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract">-</span>
                </li>
              </ul>
            </div><span class="btn-span addDiscount">添加档位</span>
          </div>
          <div class="modal-footer">
            <button class="btn btn-primary" type="button">修改</button>
          </div>
        </div>
      </div>
    </div>
    <div class="change-discount modal fade" id="timeDiscount" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">时长档位设置</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <ul class="discount-stall parent-flex">
              <li class="row">
                <input class="col-form stall-number" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="dropdown-toggle dropdownMenuButton" data-type="0">年</span><span class="col-label">打</span>
                <input class="col-form discount-number" placeholder="请输入数值" type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract">-</span>
              </li>
            </ul><span class="btn-span addDiscount">添加档位</span>
          </div>
          <div class="modal-footer">
            <button class="btn btn-primary" type="button">修改</button>
          </div>
        </div>
      </div>
    </div>
  <div class="timelength-down dropdown-menu"><span class="dropdown-item" data-type="0">年</span><span class="dropdown-item" data-type="1">月</span></div>
  <script src="<%=basePath%>static/scripts/pricesetting.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
  </body>
</html>