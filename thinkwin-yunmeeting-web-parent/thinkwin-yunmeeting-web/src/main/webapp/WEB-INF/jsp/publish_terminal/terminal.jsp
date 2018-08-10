<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>

    <link rel="stylesheet" href="<%=basePath%>static/styles/dashboard.css">
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <link rel="stylesheet" href="<%=basePath%>static/styles/zTreeStyle.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/pages.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/terminal.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/switchgear.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/cityLayout.css">
  </head>
  <body><!--[if lt IE 10]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
    <div class="wrapper">
      <%@ include file="/WEB-INF/jsp/common/left.jsp"%>
      <div class="main-panel">
        <nav class="navbar fixed-top navbar-expand-lg navbar-light">
          <div class="d-flex flex-row justify-content-between align-items-center">
            <ul class="tab-business" role="tablist">
              <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#monitor" role="tab">终端监控</a></li>
              <%--<li class="nav-item"><a class="nav-link" data-toggle="tab" href="#freetime" role="tab">闲时播放设置</a></li>--%>
              <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#switchgear" role="tab">计划开关机</a></li>
            </ul>
            <%@ include file="/WEB-INF/jsp/common/top.jsp"%>
          </div>
          <div class="CheckUpdatesdiv">
            <span class="CheckUpdateContent"></span>
            <span class="CheckUpdateDetails">查看更新内容</span>
            <span class="CheckUpdatebutton"  >更新节目</span>
            <span class="icon icon-close"></span>
          </div>
        </nav>
        <div class="content terminal">
          <div class="tab-content">
            <div class="tab-pane active" id="monitor" role="tabpanel">
              <div class="table-top">
                <div class="search-box"><i class="icon icon-search"></i><i class="icon icon-search-del btn-search-del"></i>
                  <input class="form-control search-input" id="searchterminal" type="search" maxlength="50" placeholder="搜索终端名称、版本、节目等">
                </div>
                <div class="dropdown filter-dropdown">
                  <button class="dropdown-toggle" data-toggle="dropdown"></button>
                  <div class="dropdown-menu"><a class="dropdown-item active" data-filter="">全部设备 (<span class="equipmentNum"></span>)</a><a class="dropdown-item" data-filter="1">在线 (<span class="equipmentonline"></span>)</a><a class="dropdown-item" data-filter="0">离线 (<span class="equipmentoffline"></span>)</a></div>
                </div>
                <div class="btn-refresh btn-pandect"><i class="icon icon-refresh"></i><span>刷新</span></div>
                <button class="btn btn-primary btn-pandect" data-toggle="modal" data-target="#pandectModal">实时总览</button>
                <button class="btn btn-primary btn-conceal" data-toggle="modal" data-target="#remote" id="remotebatch">远程重启</button>
                <button class="btn btn-primary btn-conceal publish-program" data-toggle="modal">发布节目</button>
                <button class="btn btn-primary btn-conceal interMessage" data-toggle="modal" data-target="#inter-cut">插播消息</button>
              </div>
              <div class="table terminal-table">
                <div class="table-header">
                  <table>
                    <colgroup>
                      <col>
                      <col>
                      <col>
                      <col>
                      <col>
                      <col>
                    </colgroup>
                    <thead>
                      <tr>
                        <th>
                          <label class="checkbox" id="selectAll">
                            <input name="selectAll" type="checkbox" value="0"><span class="icon"></span>
                          </label>
                        </th>
                        <th>终端名称</th>
                        <th>终端状态</th>
                        <th>版本</th>
                        <th>当前节目</th>
                        <th>操作</th>
                      </tr>
                    </thead>
                  </table>
                </div>
                <div class="table-body"></div>
                <div class="page-Box" id="page-Box"></div>
              </div>
              <div class="detail">
                <div class="title">终端详情</div>
                <div class="dropdown more-dropdown">
                  <button class="dropdown-toggle" data-toggle="dropdown"  id="terminalDropdown">更多<i class="icon icon-info-down toggle-color"></i></button>
                  <div class="dropdown-menu"><a class="dropdown-item dropdownprograms publish-details-program" data-toggle="modal">发布节目</a><a class="dropdown-item dropdowndelete" data-toggle="modal" data-target="#delete">删除节目</a><a class="dropdown-item interMessage" data-toggle="modal" data-target="#inter-cut" >插播消息</a><a class="dropdown-item dropdown-livePicture" data-toggle="modal" data-target="#livePictureModal">实时画面</a><a class="dropdown-item" data-toggle="modal" data-target="#remote"  id="remotesingle">远程重启</a></div>
                </div>
                <div class="form-group">
                  <label>终端名称</label>
                  <input class="form-control form-control-lg" type="text" placeholder="填写终端名称，限30个字符" name="name" maxlength="30" id="particularsName">
                </div>
                <div class="form-group">
                  <label>绑定会议室</label>
                  <input class="form-control form-control-lg" type="text" placeholder="点击绑定会议室" name="boardroom"  id="particularsRoom">
                </div>
                <div class="form-group">
                  <label>终端位置<span class="Citytext">用于节目中显示当地天气信息</span></label>
                  <div class="col-right dorpdown">
                    <input id="address-city" class="form-control form-control-lg" style="background: #fff" type="text" placeholder="请选择省市区（必选）"  readonly unselectable='on' >
                    <div class="dorpdown-menu" aria-labelledby="selectCity">               </div>
                  </div>
                </div>
                <div class="form-group row">
                  <label>终端标识</label>
                  <p  id="particularsHardwareId"></p>
                </div>
                <div class="form-group row">
                  <label>终端类型</label>
                  <p><a id="particularstype"></a><span id="particularsresolution"></span></p>
                </div>
                <div class="form-group row">
                <label>终端背景</label>
                <p style='position: relative;top:2px;'>
                  <span>请上传小于5M的图片，推荐尺寸1920×1080</span>
                  <span class="btn-select-photo">上传
                     <label class="change-photo-btn label">
                       <input type="file" accept="image/jpg,image/png,image/jpeg,image/bmp">
                     </label>
                  </span>
                  <span class="btn-delete-photo">删除</span>
                  <span class="btn-change-photo">重新上传
                     <label class="change-photo-btn label">
                       <input type="file" accept="image/jpg,image/png,image/jpeg,image/bmp">
                     </label>
                  </span>
                </p>
                <div class="bg"><img src="" id="particularsbg"></div>
              </div>
                <div class="text-right">
                  <button class="btn btn-lg btn-primary" type="submit"  id="particularsSave">保存</button>
                </div>
              </div>

            </div>
            <div class="tab-pane" id="freetime" role="tabpanel"></div>
            <div class="tab-pane" id="switchgear" role="tabpanel">
              <div class="table-top">
                <div class="search-box"><i class="icon icon-search"></i><i class="icon icon-search-del btn-search-del"></i>
                  <input class="form-control search-input" id="task-search" type="search" maxlength="100" placeholder="搜索任务名称、终端名称">
                </div>
                <div class="dropdown filter-dropdown">
                  <button class="dropdown-toggle" data-toggle="dropdown">全部任务(0)</button>
                  <div class="dropdown-menu"><a class="dropdown-item active" data-state="">全部任务 (0)</a><a class="dropdown-item" data-state="1">运行中 (0)</a><a class="dropdown-item" data-state="0">未启动 (0)</a></div>
                </div>
                <button class="btn btn-primary btn-new-task" data-toggle="modal" data-target="#taskModal" data-backdrop="static" style="width: 96px">创建任务</button>
              </div>
              <div class="switchgear-table"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade inter-cut" id="inter-cut" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <div class="modal-title">插播消息</div>
              <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
            </div>
            <div class="modal-body">
              <div class="print-qrcode">
                <div class="form-group"><label class='inter-cutlabel'>播放时长：</label>
                  <div class="inter-cutinput">
                    <input class="form-control form-control-lg" id="comname" type="text" placeholder="请输入时长" maxlength="5"><div class="inter-cutline"></div>
                    <div class="dropdown area-down show inter-cutselect">
                    <div class="dropdown area-down show inter-cutselect">
                      <div id="areatype"  data-toggle="dropdown" aria-expanded="true" aria-haspopup="true">
                        <span id="area" name="areaId" class="form-control placeholder-color" data-id="123"></span>
                        <span class="icon icon-organiz-down"></span>
                      </div>
                      <div class="dropdown-menu area-type areatypeclick" aria-labelledby="areatype" id="arealist">
                      </div>
                    </div>
                </div>
                </div>
                </div>
                <div class="form-group">
                    <label class='inter-cutlabel'>播放速度：</label>
                    <div class="dropdown area-down show areatype1">
                      <div id="areatype1" data-toggle="dropdown" aria-expanded="true" aria-haspopup="true">
                      <span id="area1" name="areaId" class="form-control placeholder-color" data-id="123"></span>
                      <span class="icon icon-organiz-down"></span>
                      </div>
                      <div class="dropdown-menu area-type1 areatypeclick" aria-labelledby="areatype1" id="arealist1">
                      </div>
                    </div>
                </div>
                <div class="form-group">
                  <textarea class="form-control form-control-lg" id="inter-cuttextarea" placeholder="请输入插播内容" maxlength="500"></textarea>
                </div>
                <div class="inter-cuterrordiv"><i class="icon icon-error"></i><span class="inter-cuterror"></span></div>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
              <button class="btn btn-primary inter-cutbutton" type="button">插播</button>
            </div>
          </div>
        </div>
      </div>
    <div class="modal fade remote" id="remote" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">远程重启</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <div class="print-qrcode">
              <div class="form-group"><div class='remotefont'>确定批量重启所选终端吗？</div></div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn restart" type="button" >重启</button>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade delete" id="delete" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">删除节目</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <div class="print-qrcode">
              <div class="form-group"><div class='remotefont'>确定删除<span class="deletename"></span>的当前节目吗？</div></div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn delete btn-danger" type="button" id="deleteprogrambtn">删除</button>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="upgradeVersions" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">升级显示终端版本</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <div class="print-qrcode"><div class='versionsdiv'><span>当前版本：<span class="versionsPresent"></span></span><span><i class="icon icon-calendar-right"></i></span><span>最新版本：<a style='color:#09a7f0;' class="versionsNewest"></a></span></div>
                 <div class='versionsposition'>
                   <div class='versionscontent'>
                        <div><span class="versionsNewest"></span>更新提示：</div>
                        <div  class="versionshtml"></div>
                    </div>
                    <div class='contentmore'>更多</div>
                </div>
                 <div class='versionsstate'>
                    <div class='defeatedstate upgradeDefeated'>
                        <div class='defeatedtitle'>
                            <i class="icon icon-failure"></i>升级失败
                        </div>
                        <div class='defeatedclick'>
                            <a class="troublePage" style="color: #09a7f0;">软件更新故障排除</a>
                            <a class="defeatedupgrade" style="color: #09a7f0;">再次升级</a>
                        </div>
                    </div>
                    <div class='defeatedtitle upgradeSuccess'>
                        <i class="icon icon-success"></i>升级成功
                    </div>
                    <div class="upgradeCourse">
                        <div  class='defeatedtitle coursetitle'>正在安装，请稍等</div>
                        <div class='progressdiv'><progress value="40" max="100"></progress></div>
                    </div>
                     <div class="upgradeInitial">
                         <div  class='defeatedtitle coursetitle'>正在下载，请稍等</div>
                         <div class='progressdiv'><progress value="0" max="100"></progress></div>
                     </div>
                </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn btn-primary upgradebutton" type="button">现在升级</button>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade livePictureModal" id="livePictureModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">变更管理员</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <div class="loading">
              <div class="spinner"><span></span><span></span><span></span><span></span><span></span></div>
              <p>正在获取实时画面，请耐心等候…</p>
            </div>
            <div class="livepreview"></div>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade programsModal" id="programsModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
          <div class="modal-header">

            <a class=" back-step" href="#step1" role="tab" data-toggle="tab">
              <i class="icon icon-calendar-left" style="margin-right: 12px"></i>选择节目
            </a>
            <ul class="tab-title tab-text" style="display: none">
              选择节目
            </ul>
            <ul class="ztree-title tab-business tab-title" role="tablist">
              <li class="nav-link active" data-toggle="tab" href="#step1" role="tab" data-toggle="tab" data-type="0">节目模板</li>
              <li class="nav-link" data-toggle="tab" href="#step3" role="tab" data-toggle="tab" data-type="1">定制模板</li>
            </ul>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body tab-content">
            <div class="tab-pane active" id="step1">
              <div class="programs-filter"></div>
              <div class="programs-list slideprograms"></div>
            </div>
            <div class="tab-pane programs-list" id="step3"></div>
            <div class="tab-pane" id="step2">
              <div class="programs-form">
                <div class="flex">
                  <div class="lable">已选节目</div>
                  <div class="col">
                    <span class="programcheckedName"></span><span id="programcheckedPreview" data-name="">预览</span>
                  </div>
                </div>
                <div class="flex">
                  <div class="lable">已选终端</div>
                  <div class="col">
                    <p>已有播放计划、离线的终端不能发布新节目，需删除节目后重新发布</p>
                    <div class="select-terminals" id="select-terminals"><span class="icon icon-add-personnel btn-add"></span></div>
                  </div>
                </div>
                <div class="flex">
                  <div class="lable">播放时间</div>
                  <div class="col">
                    <div class="timerange">
                      <input class="form-control" name="downStartTime" type="text" value="" id="playstartTinme"><span>至</span><input class="form-control" name="downEndTime" type="text" value="" id="playednTinme">
                    </div><label class="checkbox"><input name="selectAll" type="checkbox" value="0" id="long-termbtn"><span class="icon">长期播放</span>
                    </label>
                  </div>
                </div>
              </div>
              <div class="bottom">
                <button class="btn btn-primary" id="Publishimmediately">立即发布</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="pandectModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">
              <h3>实时画面总览</h3>
            </div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body tab-content">
            <div class="tab-pane active">
              <div class="programs-list">
                <ul>
                </ul>
                <div class="nothing"style="display:none" id="programsNothing">暂无在线终端</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="CheckUpdateModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
              <div class="loading">
                <div class="spinner"><span></span><span></span><span></span><span></span><span></span></div>
                <p>正在节目更新，请耐心等候…</p>
              </div>
          </div>
      </div>
    <div class="modal fade" id="programshadeModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog modal-lg" role="document">
        <div class="loading">
          <div class="spinner"><span></span><span></span><span></span><span></span><span></span></div>
          <p>正在发布节目，请耐心等候…</p>
        </div>
      </div>
    </div>
    <div class="modal fade" id="feedbackModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">发送任务反馈</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body">
            <div class="print-qrcode">
              <div class="form-group feedbacktitle">部分终端发布节目失败</div>
              <div class="form-group">
                <div class="feedbackdiv"></div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-primary feedbackbtn" type="button" style="height: 40px;">知道了</button>
          </div>
        </div>
      </div>
    </div>
    <div class="programPreviewbjdiv">
        <div class="programPreviewbj"></div>
        <div class="programPreview">
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true" style="font-size: 15px"></span></button>
            <img src="" class="programPreviewImg">
        </div>
    </div>

    <div class="modal fade" id="taskModal" tabindex="-1" role="dialog" aria-hidden="true">
     <div class="modal-dialog modal-lg" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <div class="modal-title">
            <h3>创建开关机任务</h3>
          </div>
          <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
        </div>
        <div class="modal-body">
          <div class="task-form">
            <div class="flex">
              <div class="lable">任务名称</div>
              <div class="col">
                <input class="form-control" name="taskName" type="text" maxlength="50" value="" placeholder="请输入任务名称">
              </div>
            </div>
            <div class="flex">
              <div class="lable">选择终端</div>
              <div class="col">
                <div class="select-terminals"><span class="icon icon-add-personnel btn-add"></span></div>
              </div>
            </div>
            <div class="flex add-periods">
              <div class="lable">运行时段</div>
              <div class="col add-period">
                <div class="periods">
                  <p>未添加运行时段，将默认全天开机，不添加时段也可以开启特别关机时段</p>
                </div>
                <button class="btn btn-clear-primary btn-add-period">添加运行时段</button>
              </div>
            </div>
            <div class="flex">
              <div class="lable">高级设置</div>
              <div class="col special-time">
                <div>
                  <label class="checkbox">
                    <input name="ifOpenDown" type="checkbox" value="0"><span class="icon"></span>开启特别关机时段
                  </label>
                </div>
                <div class="timerange">
                  <input class="form-control" name="downStartTime" type="text" value="" disabled><span>至</span>
                  <input class="form-control" name="downEndTime" type="text" value="" disabled><span>该时段关机</span>
                </div>
              </div>
            </div>
          </div>
          <div class="bottom">
            <button class="btn btn-clear-secondary btn-save-as-task">另存新任务</button>
            <button class="btn btn-primary btn-save-task" style="width:120px;height: 40px;">保存</button>
          </div>
        </div>
      </div>
    </div>
    </div>
    <div class="modal fade" id="logsModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <div class="modal-title">
            <h3>查看日志</h3>
          </div>
          <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
        </div>
        <div class="modal-body logs">
          <div class="table-top">
            <div class="search-box"><i class="icon icon-search"></i><i class="icon icon-search-del btn-search-del"></i>
              <input class="form-control search-input" id="search" type="search" maxlength="100" placeholder="搜索终端名称"  autocomplete="off">
            </div>
            <input id="runtime" class="form-control runtime" type="text" placeholder="选择执行时间">
            <div class="dropdown filter-dropdown">
              <button class="dropdown-toggle" data-toggle="dropdown">全部任务(22)</button>
              <div class="dropdown-menu">
                <a href="javascript:void(0)" class="dropdown-item active" data-state="">全部状态(0)</a>
                <a href="javascript:void(1)" class="dropdown-item" data-state="1">发送成功(0)</a>
                <a href="javascript:void(2)" class="dropdown-item" data-state="0">发送失败(0)</a>
              </div>
            </div>
            <button class="btn btn-primary btn-clear-logs" data-toggle="modal" data-target="#actionsModal" data-type="3">清除记录</button>
          </div>
          <div class="table logs-table">
            <div class="scrollview">
            </div>
          </div>
          <div class="bottom">

          </div>
        </div>
      </div>
    </div>
  </div>
    <div class="modal fade" id="actionsModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">任务</div>
            <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
          </div>
          <div class="modal-body" style="line-height: 30px"></div>
          <div class="modal-footer">
            <button class="btn btn-lg btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
            <button class="btn btn-lg btn-actions btn-primary" type="button" style="height: 40px;">启动</button>
          </div>
        </div>
      </div>
    </div>
    <!-- build:js static/scripts/terminal.js-->
    <script src="<%=basePath%>static/scripts/terminal.js"></script>
    <script src="<%=basePath%>static/scripts/plugins/datetimepicker.js"></script>
    <script src="<%=basePath%>static/scripts/plugins/datepicker.js"></script>
    <script src="<%=basePath%>static/scripts/plugins/selector.js"></script>
    <script src="<%=basePath%>static/scripts/pages.js"></script>
    <script src="<%=basePath%>static/scripts/plugins/pages.js"></script>
    <script src="<%=basePath%>static/scripts/plugins/period.js"></script>
    <script src="<%=basePath%>static/scripts/switchgear.js"></script>
    <script src="<%=basePath%>static/scripts/cityselect.js"></script>
  <!-- endbuild-->
  </body>
</html>