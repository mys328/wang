<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html class="no-js" lang="">
<head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="apple-touch-icon" href="apple-touch-icon.png">
    <!-- Place favicon.ico in the root directory-->
    <!-- build:css static/styles/common.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/common.css">
    <!-- endbuild-->
    <script src="<%=basePath%>static/plugin/underscore.min.js"></script>
    <script src="<%=basePath%>static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>static/scripts/config.js"></script>
    <script src="<%=basePath%>static/scripts/jump.js"></script>
    <!-- build:css static/styles/meeting.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/meeting.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/detail.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/iconfont.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/personnelOrg.css">
    <!-- endbuild-->

</head>
<body><!--[if lt IE 10]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade
    your browser</a> to improve your experience.</p><![endif]-->
<nav class="navbar navbar-expand-lg navbar-light">
    <%@ include file="/WEB-INF/jsp/common/mainTop.jsp" %>
</nav>
<input type="hidden" value="${type}" id="pageType">
<div class="nav-tabs">
    <ul class="float-left">
        <li class="nav-item"><a class="active" href="javascript:void(0);" onclick="gotoMeetingScreening()">我的会议</a></li>
        <li class="nav-item" id="verify" style="display: none"><a href="javascript:void(0);" onclick="gotoMeetingScreeningVerify()">我审核的</a>
        </li>
    </ul>
    <div class="float-right">
        <span id="searchResult"></span>
        <div class="form-control input-search">
            <i class="icon icon-search"></i>
            <input id="search" type="text" maxlength="100" placeholder="在所有会议中搜索">
            <i class="icon icon-search-del" id="del-searchList"></i>
        </div>
        <div class="btn-group" data-toggle="buttons">
            <button class="btn btn-sm btn-calendar select-btn" type="button">日</button>
            <%--<button class="btn btn-sm btn-primary" type="button">月</button>--%>
            <%--<button class="btn btn-sm btn-primary" type="button">年</button>--%>
        </div>
        <button class="btn btn-sm btn-primary unselected-btn" id="btn-list" type="button">列表</button>
    </div>
</div>
<div class="tab-content">
    <div class="tab-pane active" id="mine" role="tabpanel">
        <div class="flex flex-row">
            <div id="calendar">
                <div class="head">
                    <input class="form-control form-control-sm" type="text" maxlength="10">
                    <button class="btn btn-sm btn-primary btn-today">今日</button>
                </div>
                <div id="dates"></div>
            </div>
            <div id="agenda"></div>
        </div>
    </div>
    <div class="tab-pane" id="myMeeting">
        <div class="left">
            <div class="meetingfilter">
                <div>
                    <ul>
                        <li class="mouse" data-id="0"><a>最近七天的<span class="count" id="sevenDays">0</span><span
                                class="icon icon-tick" id="0" style="display:block;"></span></a></li>
                        <li class="mouse" data-id="1"><a>未来的会议<span class="count" id="future">0</span><span
                                class="icon icon-tick" id="1"></span></a></li>
                        <li class="mouse" data-id="2"><a>过去的会议<span class="count" id="last">0</span><span
                                class="icon icon-tick" id="2"></span></a></li>
                    </ul>
                </div>
                <div class="opers">
                    <ul>
                        <li class="mouse" data-id="3"><a><span class="oper-span"> </span>我组织的<span class="count"
                                                                                                   id="myoper">0</span><span
                                class="icon icon-tick" id="3"></span></a></li>
                        <li class="mouse" data-id="4"><a><span class="att-span"></span>我参加的<span class="count"
                                                                                                 id="attend">0</span><span
                                class="icon icon-tick" id="4"></span></a></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="right">
            <div class="meeting-plan"></div>
            <div class="loading-bubbles" id="mine-loading">
                <div class="bubble-container">
                    <div class="bubble"></div>
                </div>
                <div class="bubble-container">
                    <div class="bubble"></div>
                </div>
                <div class="bubble-container">
                    <div class="bubble"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="deleteMeeting" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form>
                <div class="modal-header">
                    <div class="modal-title">删除会议</div>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span
                            class="icon icon-close" aria-hidden="true"></span></button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-control-label">确定删除该会议吗？</label>
                        <input name="meetingId" type="hidden">
                        <input name="title" type="hidden">
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
                    <button class="btn btn-lg btn-danger" type="submit">删除</button>
                </div>
            </form>
        </div>
    </div>
</div>
<div class="modal fade" id="cancelMeeting" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content room_break">
            <form>
                <div class="modal-header">
                    <div class="modal-title">取消会议</div>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span
                            class="icon icon-close" aria-hidden="true"></span></button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-control-label break_tittle">您确定取消该会议吗？</label>
                        <input name="meetingId" type="hidden">
                        <input name="title" type="hidden">
                    </div>
                    <div class="form-group">
                        <textarea class="form-control break_textarea" placeholder="填写取消原因（选填），取消原因随通知发送给参会人"
                                  maxlength="200" name="cancelReason"></textarea>
                    </div>
                </div>
                <div class="modal-footer break_footer">
                    <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
                    <button class="btn btn-lg btn-danger" type="submit">确定</button>
                </div>
            </form>
        </div>
    </div>
</div>
<div class="modal fade" id="directAuditedMeeting" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form>
                <div class="modal-header">
                    <div class="modal-title">会议审核</div>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span
                            class="icon icon-close" aria-hidden="true"></span></button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-control-label">确定通过预订申请吗？</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
                    <button class="btn btn-lg btn-primary" type="submit">通过</button>
                    <input name="mettingLocation" type="hidden">
                    <input name="meetingId" type="hidden">
                </div>
            </form>
        </div>
    </div>
</div>
<div class="modal fade" id="auditedMeeting" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form>
                <div class="modal-header">
                    <div class="modal-title">会议审核</div>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span
                            class="icon icon-close" aria-hidden="true"></span></button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-control-label">该会议时间内还有其他预定申请,若审核通过,其他会议将不通过审核,确定通过预定申请吗?</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
                    <button class="btn btn-lg btn-primary" type="submit">通过</button>
                    <input name="mettingLocation" type="hidden">
                    <input name="meetingId" type="hidden">
                </div>
            </form>
        </div>
    </div>
</div>
<div class="modal fade" id="unauditedMeeting" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form>
                <div class="modal-header">
                    <div class="modal-title">会议审核</div>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span
                            class="icon icon-close" aria-hidden="true"></span></button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-control-label">请选择不通过预订申请的原因</label>
                        <label class="radio">
                            <input name="reason" type="radio" value="会议室已被预订" checked><span>会议室已被预订</span>
                        </label>
                        <label class="radio">
                            <input name="reason" type="radio" value="会议规格不符"><span>会议规格不符</span>
                        </label>
                        <label class="radio">
                            <input name="reason" type="radio" value="会议室暂时关闭"><span>会议室暂时关闭</span>
                        </label>
                        <label class="radio">
                            <input name="reason" type="radio" value="其他"><span>其他</span>
                        </label>
                        <textarea class="form-control auditReson_textarea" maxlength="200" name="reason1"></textarea>
                        <input name="mettingLocation" type="hidden">
                        <input name="meetingId" type="hidden">
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
                    <button class="btn btn-lg btn-danger" type="submit">不通过</button>
                </div>
            </form>
        </div>
    </div>
</div>
<div class="modal fade" id="signinMeeting" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form>
                <div class="modal-header">
                    <div class="modal-title">会议签到</div>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-control-label">您确定现在签到吗？</label>
                        <input name="meetingId" type="hidden">
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
                    <button class="btn btn-primary" type="submit">签到</button>
                </div>
            </form>
        </div>
    </div>
</div>
<div class="modal fade" id="signedMeeting" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <div class="modal-title">签到统计</div>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span
                        class="icon icon-close" aria-hidden="true"></span></button>
            </div>
            <div class="modal-body signed"></div>
        </div>
    </div>
</div>
<script src="<%=basePath%>static/plugin/fullcalendar.min.js"></script>
<script src="<%=basePath%>static/plugin/zh-cn.min.js"></script>
<script src="<%=basePath%>static/scripts/plugins/datepicker.js"></script>
<script src="<%=basePath%>static/scripts/plugins/timerange.js"></script>
<script src="<%=basePath%>static/scripts/plugins/boardroom.js"></script>
<%--<script src="<%=basePath%>static/scripts/personnel.js"></script>--%>
<script src="<%=basePath%>static/scripts/personnelOrg.js"></script>
<script src="<%=basePath%>static/scripts/detail.js"></script>
<script src="<%=basePath%>static/scripts/meeting.js"></script>
</body>
</html>