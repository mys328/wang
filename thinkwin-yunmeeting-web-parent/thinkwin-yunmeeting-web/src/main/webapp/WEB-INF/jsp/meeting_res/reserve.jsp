<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
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
    <link rel="stylesheet" href="<%=basePath%>/static/styles/common.css">
    <!-- endbuild-->
    <script src="<%=basePath%>/static/plugin/underscore.min.js"></script>
    <script src="<%=basePath%>/static/plugin/soda.min.js"></script>
    <script src="<%=basePath%>/static/plugin/moment.min.js"></script>
    <script src="<%=basePath%>/static/plugin/jquery.min.js"></script>
    <script src="<%=basePath%>/static/plugin/tether.min.js"></script>
    <script src="<%=basePath%>/static/plugin/bootstrap.min.js"></script>
    <script src="<%=basePath%>/static/scripts/config.js"></script>
    <!-- build:css static/styles/booking.css-->
    <link rel="stylesheet" href="<%=basePath%>/static/styles/booking.css">
    <link rel="stylesheet" href="<%=basePath%>/static/styles/detail.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/personnelOrg.css">
    <!-- endbuild-->
</head>
<body><!--[if lt IE 10]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade your browser</a> to improve your experience.</p><![endif]-->
<nav class="navbar navbar-expand-lg navbar-light">
    <%@ include file="/WEB-INF/jsp/common/mainTop.jsp"%>
</nav>
<div class="nav-tabs">
    <ul class="float-left">
        <li class="nav-item"><a>会议预订</a></li>
    </ul>
</div>
<div class="flex flex-row">
    <div class="sidebar" id="calendar">
        <div class="head">
            <input class="form-control form-control-sm" type="text" maxlength="10">
            <button class="btn btn-sm btn-primary btn-today">今日</button>
        </div>
        <div id="datepicker"></div>
        <div class="popular-room">
            <h6>常用会议室</h6>
            <ul></ul>
        </div>
    </div>
    <div class="main">
        <div class="filter">
            <form id="filterForm" class="filterForm">
                <div class="form-group flex">
                    <label>会议室人数</label>
                    <div class="col">
                        <label class="capacity">
                            <input class="revocable" name="personNum" type="checkbox" value="5"><span>5</span>
                        </label>
                        <label class="capacity">
                            <input class="revocable" name="personNum" type="checkbox" value="10"><span>10</span>
                        </label>
                        <label class="capacity">
                            <input class="revocable" name="personNum" type="checkbox" value="15"><span>15</span>
                        </label>
                        <label class="capacity">
                            <input class="revocable" name="personNum" type="checkbox" value="20"><span>20</span>
                        </label>
                        <label class="capacity">
                            <input class="revocable" name="personNum" type="checkbox" value="30"><span>30</span>
                        </label>
                        <label class="capacity">
                            <input class="revocable" name="personNum" type="checkbox" value="40"><span>40</span>
                        </label>
                        <label class="capacity">
                            <input class="revocable" name="personNum" type="checkbox" value="50"><span>50</span>
                        </label>
                    </div>
                </div>
                <div class="form-group flex">
                    <label>会议设备</label>
                    <div class="col">
                        <label class="custom-control custom-checkbox">
                            <input class="custom-control-input" name="deviceService" type="checkbox" value="004"><span class="custom-control-indicator"></span><span class="custom-control-description">扩音</span>
                        </label>
                        <label class="custom-control custom-checkbox">
                            <input class="custom-control-input" name="deviceService" type="checkbox" value="003"><span class="custom-control-indicator"></span><span class="custom-control-description">显示</span>
                        </label>
                        <label class="custom-control custom-checkbox">
                            <input class="custom-control-input" name="deviceService" type="checkbox" value="001"><span class="custom-control-indicator"></span><span class="custom-control-description">白板</span>
                        </label>
                        <label class="custom-control custom-checkbox">
                            <input class="custom-control-input" name="deviceService" type="checkbox" value="002"><span class="custom-control-indicator"></span><span class="custom-control-description">视频会议</span>
                        </label>
                    </div>
                </div>
                <div class="form-group flex">
                    <label>空闲时间</label>
                    <div class="col">
                        <div class="freetime">
                            <span class="value none">请选择时间</span>
                            <input name="staTime" type="hidden" value="">
                            <input name="endTime" type="hidden" value="">
                        </div>
                        <label class="area">区域</label>
                        <div class="dropdown area-down">
                            <div id="areatype" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true">
                                <span id="area" name="areaId" class="form-control placeholder-color">全部区域</span>
                                <span class="icon icon-organiz-down"></span>
                            </div>
                            <div class="dropdown-menu area-type" aria-labelledby="areatype">

                            </div>
                        </div>
                    </div>
                </div>
                <div class="form-group flex">
                    <label>会议室类型</label>
                    <div class="col">
                        <label class="type" style="margin-right: 16px">
                            <input class="revocable" name="isAudit" type="checkbox" value="0"><span>普通会议室</span>
                        </label><label class="type">
                            <input class="revocable" name="isAudit" type="checkbox" value="1"><span>需审核会议室</span>
                        </label>
                    </div>
                </div>
            </form>
        </div>
        <div id="timeline"></div>
    </div>
</div>
<div class="modal fade" id="deleteMeeting" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form>
                <div class="modal-header">
                    <div class="modal-title">删除会议</div>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
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
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-control-label break_tittle">您确定取消该会议吗？</label>
                        <input name="meetingId" type="hidden">
                        <input name="title" type="hidden">
                    </div>
                    <div class="form-group">
                        <textarea class="form-control break_textarea" placeholder="填写取消原因（选填），取消原因随通知发送给参会人" maxlength="200" name="cancelReason"></textarea>
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
                <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
            </div>
            <div class="modal-body signed"></div>
        </div>
    </div>
</div>
<script src="<%=basePath%>/static/plugin/fullcalendar.min.js"></script>
<script src="<%=basePath%>/static/plugin/zh-cn.min.js"></script>
<script src="<%=basePath%>/static/plugin/scheduler.min.js"></script>
<script src="<%=basePath%>/static/scripts/plugins/datepicker.js"></script>
<script src="<%=basePath%>/static/scripts/plugins/timerange.js"></script>
<script src="<%=basePath%>/static/scripts/plugins/boardroom.js"></script>
<%--<script src="<%=basePath%>static/scripts/personnel.js"></script>--%>
<script src="<%=basePath%>static/scripts/personnelOrg.js"></script>
<script src="<%=basePath%>/static/scripts/detail.js"></script>
<script src="<%=basePath%>/static/scripts/booking.js"></script>
</body>
</html>