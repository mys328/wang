<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html class="no-js">
  <head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="apple-touch-icon" href="apple-touch-icon.png">
    <!-- Place favicon.ico in the root directory-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/dashboard.css">
    <script src="<%=basePath%>static/plugin/underscore.min.js"></script>
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <script src="<%=basePath%>static/scripts/jump.js"></script>
    <link rel="stylesheet" href="<%=basePath%>static/styles/cityLayout.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/orderManger.css">
  </head>
  <body><!--[if lt IE 10]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
    <div class="wrapper">
      <%@ include file="/WEB-INF/jsp/common/left.jsp"%>
      <div class="main-panel">
        <nav class="navbar fixed-top navbar-expand-lg navbar-light">
          <div class="d-flex flex-row justify-content-between align-items-center">
            <ul class="tab-business" role="tablist">
              <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#update" role="tab">授权升级</a></li>
              <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#invoice" role="tab">发票管理</a></li>
            </ul>
            <%@ include file="/WEB-INF/jsp/common/top.jsp"%>
         <%--   <ul class="nav">
              <li class="nav-item"><a class="nav-link" href="#"><span class="icon icon-question"></span></a></li>
              <li class="nav-item dropdown"><a class="nav-link dropdown-toggle" id="menu" href="#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="nophoto">管理</span><span>系统管理员</span></a>
                <div class="dropdown-menu" aria-labelledby="menu"><a class="dropdown-item btn-show-profile" data-toggle="tab" href="#profile" role="tab"><i class="icon icon-personaldata"></i>个人资料</a><a class="dropdown-item" href="/logout"><i class="icon icon-exit"></i>退出登录</a></div>
              </li>
            </ul>--%>
          </div>
        </nav>
        <div class="content">
          <div class="tab-content">
            <div class="tab-pane active" id="update" role="tabpanel">
              <div class="top-div"><span>您的企业当前版本已过期，功能已不可用，请续费。<a id="buyNow">立即续费</a></span><span class="icon icon-close"></span></div>
              <div class="top-div">2017.12.1~2018.2.28期间，是企云会推广期。从2018.3.1日起，会进行订单改版，价格上调。客户在推广期下的订单不受改版价格的影响。</div>
              <div class="center-div">
                <div class="main-center">
                  <p class="center-title"> <span id="user">系统管理员</span><span>，欢迎使用企云会 </span><span id="version-info"></span><span id="version-state">已过期</span><span>，有效期至：</span><span id="version-time" class="version-time"></span></p>
                </div>
                <div class="right-center"></div>
              </div>
              <div class="col chart">
                <div class="pie" id="pie1">员工人数
                  <svg width="166" height="166">
                    <circle class="members active" cx="82" cy="44" r="70" transform="rotate(90 63 63)" stroke-dashoffset="0"></circle>
                    <circle class="members" cx="82" cy="44" r="70" transform="rotate(90 63 63)" stroke-dashoffset="439.8"></circle>
                  </svg><span>剩余人数<div><i></i><a class="unit">人</a></div></span>
                  <div class="pie-descript">
                    <p>员工总人数：<a class="total">0人</a></p>
                    <p>已激活人数：<a class="activated">0人</a></p>
                    <p>未激活人数：<a class="inactive">0人  </a></p>
                  </div>
                </div>
                <div class="pie" id="pie2">会议室数
                  <svg width="166" height="166">
                    <circle class="rooms active" cx="82" cy="44" r="70" transform="rotate(90 63 63)" stroke-dashoffset="0"></circle>
                    <circle class="rooms" cx="82" cy="44" r="70" transform="rotate(90 63 63)" stroke-dashoffset="439.8"></circle>
                  </svg><span>剩余<div><i></i><a class="unit">间</a></div></span>
                  <div class="pie-descript">
                    <p>会议室总数：<a class="roomTotal">0间</a></p>
                    <p>已使用数量：<a class="roomUsed">0间  </a></p>
                  </div>
                </div>
                <div class="pie" id="pie3">存储空间
                  <svg width="166" height="166">
                    <circle class="space active" cx="82" cy="44" r="70" transform="rotate(90 63 63)" stroke-dashoffset="0"></circle>
                    <circle class="space" cx="82" cy="44" r="70" transform="rotate(90 63 63)" stroke-dashoffset="439.8"></circle>
                  </svg><span>剩余空间<div><i></i><a class="unit">M </a></div></span>
                  <div class="pie-descript">
                    <p>总存储空间：<a class="totalSpace">0G</a></p>
                    <p>已使用空间：<a class="usedSpace">0M</a></p>
                  </div>
                </div>
                <div class="pie" id="pie4">当前版本
                  <div id="receptacle">
                    <p class="version0">免费版</p>
                    <p class="version1">专业版</p>
                    <div class="btn-box">
                      <button class="btn btn-lg btn-primary status1" type="text" onclick="didClickExpansion()">增容</button>
                      <button class="btn btn-lg btn-primary status0" type="text" onclick="didClickUpgrade()">升级专业版</button>
                      <button class="btn btn-lg btn-primary status3" type="text" onclick="didClickExtend()">立即续费</button>
                      <button class="btn btn-lg btn-primary status4" type="text" onclick="didClickExtend()">续费</button>
                    </div>
                  </div>
                  <div class="introduce0" onclick="didClickIntroduce()">(版本介绍)</div>
                  <div class="introduce1" onclick="didClickIntroduce()">(版本介绍)</div>
                </div>
              </div>
              <div class="bottom-div">
                <div class="list" id="orderList"></div>
              </div>
            </div>
            <div class="tab-pane" id="invoice" role="tabpanel">
              <div class="invoice-info">
                <div class="title">发票信息管理</div>
                <div class="null-invoice"><span id="none-info">您还没有添加发票信息，请<span class="add-btn" id="add-info">立即添加</span></span><span id="reminder-info">请完善发票信息<span>（发票信息仅限一条，请您慎重填写）</span></span>
                  <div class="form" id="form-info">
                    <div class="form-group row input-form">
                      <label class="col-left col-form-label">开具类型</label>
                      <div class="col-right">
                        <div class="form-check form-check-inline">
                          <label class="form-check-label">
                            <input class="form-check-input" type="radio" name="issue-type" id="personnel-issue" value="0" checked>&nbsp;&nbsp;个人
                          </label>
                        </div>
                        <div class="form-check form-check-inline">
                          <label class="form-check-label">
                            <input class="form-check-input" type="radio" name="issue-type" id="enterprise-issue" value="1">&nbsp;&nbsp;企业
                          </label>
                        </div>
                      </div>
                    </div>
                    <div id="personnel-invoice">
                      <div class="form-group row input-form">
                        <label class="col-left col-form-label">发票抬头</label>
                        <div class="col-right">
                          <label>个人</label>
                        </div>
                      </div>
                      <div class="form-group row input-form">
                        <label class="col-left col-form-label">发票类型</label>
                        <div class="col-right">
                          <label class="personnel-invoice">增值税普通发票</label>
                        </div>
                      </div>
                    </div>
                    <div id="enterprise-invoice">
                      <div class="form-group row input-form">
                        <label class="col-left col-form-label">发票抬头</label>
                        <div class="col-right">
                          <input class="form-control" id="enterprise-name" type="text" placeholder="请填写您营业执照上的全称（必填）">
                        </div>
                      </div>
                      <div class="form-group row input-form">
                        <label class="col-left col-form-label">发票类型</label>
                        <div class="col-right">
                          <div class="form-check form-check-inline">
                            <label class="form-check-label">
                              <input class="form-check-input" type="radio" name="invoice-type" id="common" value="0" checked>&nbsp;&nbsp;增值税普通发票
                            </label>
                          </div>
                          <div class="form-check form-check-inline">
                            <label class="form-check-label">
                              <input class="form-check-input" type="radio" name="invoice-type" id="special" value="1">&nbsp;&nbsp;增值税专用发票
                            </label>
                          </div>
                        </div>
                      </div>
                      <div class="form-group row enterprise-number input-form">
                        <label class="col-left col-form-label">税务证号</label>
                        <div class="col-right">
                          <input class="form-control" id="enterprise-number" type="text" maxlength="500" placeholder="请填写您税务登记证上的编号（必填）" aria-describedby="note">
                          <span class="" id="note">请与贵公司财务人员核实并填写准确的税务登记证号，以免影响您发票后续的使用</span>
                        </div>
                      </div>
                      <div id="value-added">
                        <div class="form-group row input-form">
                          <label class="col-left col-form-label">开户银行</label>
                          <div class="col-right">
                            <input class="form-control" id="bank-open" type="text" maxlength="500" placeholder="请填写您开户许可证上的开户银行（必填）">
                          </div>
                        </div>
                        <div class="form-group row input-form">
                          <label class="col-left col-form-label">开户账号</label>
                          <div class="col-right">
                            <input class="form-control" id="bank-acount" type="text" maxlength="500" placeholder="请填写您开户许可证上的银行账户（必填）">
                          </div>
                        </div>
                        <div class="form-group row resign-address input-form">
                          <label class="col-left col-form-label">注册地址</label>
                          <div class="col-right">
                            <textarea class="form-control" id="resign-address" maxlength="500" placeholder="请填写您营业执照上的注册地址（必填）"></textarea>
                          </div>
                        </div>
                        <div class="form-group row com-tel input-form">
                          <label class="col-left col-form-label">联系电话</label>
                          <div class="col-right">
                            <input class="form-control" id="com-tel" type="text" maxlength="500" placeholder="请填写您公司有效的联系电话（必填）">
                          </div>
                        </div>
                      </div>
                    </div>
                    <div>
                      <button class="btn btn-submit" id="submit-info" type="button">提交</button><button class="btn btn-clear" id="clear-info" type="button">取消</button>
                    </div>
                  </div>
                </div>
                <div class="show-invoice-info">
                  <div class="row show">
                    <label class="col-left">开具类型</label>
                    <label class="col-right">个人</label>
                  </div>
                  <div class="row show">
                    <label class="col-left">发票抬头</label>
                    <label class="col-right">个人</label>
                  </div>
                  <div class="row show">
                    <label class="col-left">发票类型</label>
                    <label class="col-right">增值税普通发票<span class="change-btn" id="change-personnel">修改发票信息</span></label>
                  </div>
                </div>
                <div class="show-enterprise-common">
                  <div class="row show">
                    <label class="col-left">开具类型</label>
                    <label class="col-right">企业</label>
                  </div>
                  <div class="row show">
                    <label class="col-left">发票抬头</label>
                    <label class="col-right" id="show-comname">北京盛科维科技发展有限公司</label>
                  </div>
                  <div class="row show">
                    <label class="col-left">发票类型</label>
                    <label class="col-right">增值税普通发票</label>
                  </div>
                  <div class="row show">
                    <label class="col-left">税务证号</label>
                    <label class="col-right"><span id="show-comtax"> </span><span class="change-btn" id="change-common">修改发票信息</span></label>
                  </div>
                </div>
                <div class="show-enterprise-special">
                  <div class="row show">
                    <label class="col-left">开具类型</label>
                    <label class="col-right">企业</label>
                  </div>
                  <div class="row show">
                    <label class="col-left">发票抬头</label>
                    <label class="col-right" id="show-spename">北京盛科维科技发展有限公司</label>
                  </div>
                  <div class="row show">
                    <label class="col-left">发票类型</label>
                    <label class="col-right">增值税专用发票</label>
                  </div>
                  <div class="row show">
                    <label class="col-left">税务证号</label>
                    <label class="col-right" id="show-spetax">11000001111</label>
                  </div>
                  <div class="row show">
                    <label class="col-left">开户银行</label>
                    <label class="col-right" id="show-speBank">11000001111</label>
                  </div>
                  <div class="row show">
                    <label class="col-left">开户账号</label>
                    <label class="col-right" id="show-speacount">11000001111</label>
                  </div>
                  <div class="row show">
                    <label class="col-left">注册地址</label>
                    <label class="col-right" id="show-speaddress">北京市海淀区西三旗昌临813号京玺中韩文化创意园A区10号楼104</label>
                  </div>
                  <div class="row show">
                    <label class="col-left">联系电话</label>
                    <label class="col-right"><span id="show-spetel">11000001111</span><span class="change-btn" id="change-special">修改发票信息</span></label>
                  </div>
                </div>
              </div>
              <div class="invoice-address">
                <div class="title">发票寄送地址管理</div>
                <div class="none-invoice-address"><span id="none-address">您还没有添加发票寄送地址，请<span class="add-btn" id="add-address">立即添加</span></span>
                  <div class="form" id="form-address">
                    <div class="form-group row">
                      <label class="col-left col-form-label">收件人姓名</label>
                      <div class="col-right">
                        <input class="form-control" id="address-name" type="text" placeholder="长度不超过25个字符（必填）" maxlength="25">
                      </div>
                    </div>
                    <div class="form-group row">
                      <label class="col-left col-form-label">所在地区</label>
                      <div class="col-right dorpdown">
                        <div class="form-control city_input" readonly="readonly" id="selectCity" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true">
                          <input id="address-city" type="text" placeholder="请选择省市区（必选）" readonly="readonly"><span class="icon icon-organiz-down"> </span>
                        </div>
                        <div class="dorpdown-menu" aria-labelledby="selectCity">               </div>
                      </div>
                    </div>
                    <div class="form-group row textarea">
                      <label class="col-left col-form-label">详细地址</label>
                      <div class="col-right">
                        <textarea class="form-control" id="address-detail" type="text" maxlength="500" placeholder="建议您如实填写详细收货地址，例如街道名称，门牌号码，楼层和房间号等信息（必填）"></textarea>
                      </div>
                    </div>
                    <div class="form-group row">
                      <label class="col-left col-form-label">邮政编码</label>
                      <div class="col-right">
                        <input class="form-control" id="address-code" type="text" maxlength="500" placeholder="如果邮政编码不清楚，请填写000000">
                      </div>
                    </div>
                    <div class="form-group row">
                      <label class="col-left col-form-label">手机号码</label>
                      <div class="col-right">
                        <input class="form-control" id="address-tel" type="text" maxlength="500" placeholder="请填写常用手机号">
                        <span id="errormsg"><i class="icon icon-error"></i><span>手机号格式不正确</span></span>
                      </div>
                    </div>
                    <div>
                      <button class="btn btn-submit" id="submit-address" type="button">保存</button><button class="btn btn-clear" id="clear-address" type="button">取消</button>
                    </div>
                  </div>
                </div>
                <div class="show-invoice-address">
                  <table class="table">
                    <thead>
                      <tr>
                        <th>收件人姓名</th>
                        <th>所在地区</th>
                        <th>详细地址</th>
                        <th>邮政编码</th>
                        <th>手机号码</th>
                        <th>操作</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td><span id="show-addressName"></span></td>
                        <td><span id="show-addressCity"></span></td>
                        <td><span id="show-addressDetail"></span></td>
                        <td><span id="show-addressCode"></span></td>
                        <td><span id="show-addressTel"></span></td>
                        <td><span class="change-btn" id="changeAddress">修改</span></td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
              <div class="invoice-ask">
                <div>
                  <div class="title">发票索取</div>
                  <div class="get-invoice"></div>
                </div>
                <div>
                  <div class="reminder-label">温馨提示：</div>
                  <div class="reminder-span"> <span>1.请仔细核对发票信息及寄送地址后再索取发票。</span><br><span>2.发票寄送采取快递到付形式。</span></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="orderModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">查看快递单号</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <form>
              <div class="form-group">
                <label class="form-control-label" id="msg"></label>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn btn-lg btn-primary" id="gotowebsite" type="button">快递官网</button>
          </div>
        </div>
      </div>
    </div>
   <div class="modal fade" id="cancelOrder" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <form id="cancelOrderForm">
          <div class="modal-header">
            <div class="modal-title">取消订单</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label class="form-control-label">产品名称:<span class="room_name"></span></label>
              <input class="name1" name="id" type="hidden">
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn btn-lg btn-danger" type="submit">确定</button>
          </div>
        </form>
      </div>
    </div>
   </div>

  <div class="modal fade" id="orderReminder" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <form id="orderReminderForm">
          <div class="modal-header">
            <div class="modal-title">未完成订单提醒</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label class="form-control-label">您有一个未完成订单,请先完成订单或取消订单<span class="room_name"></span></label>
              <input class="name1" name="id" type="hidden">
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn btn-lg btn-primary" type="submit">查看订单</button>
          </div>
        </form>
      </div>
    </div>
  </div>
    <script src="<%=basePath%>static/scripts/cityselect.js"></script>
    <script src="<%=basePath%>static/scripts/orderManger.js"></script>
  </body>
</html>