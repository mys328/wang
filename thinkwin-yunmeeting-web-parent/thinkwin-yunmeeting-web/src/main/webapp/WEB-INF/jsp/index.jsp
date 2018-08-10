<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html class="no-js" lang="">
<head>
    <meta charset="utf-8">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>企云会-连接组织的智慧</title>
    <%@include file="/WEB-INF/jsp/common/common.jsp" %>
    <link rel="apple-touch-icon" href="<%=basePath%>apple-touch-icon.png">
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
    <!-- build:css static/styles/index.css-->
    <link rel="stylesheet" href="<%=basePath%>static/styles/index.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/detail.css">
    <link rel="stylesheet" href="<%=basePath%>static/styles/personnelOrg.css">
    <!-- endbuild-->
</head>
<body><!--[if lt IE 10]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href='http://browsehappy.com/'>upgrade
    your browser</a> to improve your experience.</p><![endif]-->
<nav class="navbar navbar-expand-lg navbar-light">
    <%@ include file="/WEB-INF/jsp/common/mainTop.jsp" %>
</nav>
<div class="container-fluid" id="main">
    <div class="flex flex-row">
        <div class="counts company">
            <h6 id="company">2017年7月，北京盛科维企业会议统计</h6>
            <div class="stress"><span>会议室利用率</span><span class="ratio" id="meetingRoomUtilization">0</span></div>
            <div class="sub"><span>共开会议</span><span id="meetingTotalNum">0</span><span>场次</span></div>
            <div class="sub"><span>总会议时长</span><span id="meetingTotalTime">0</span><span>小时</span></div>
            <div class="sub"><span>总参会人数</span><span id="meetingTotalPeopleNum">0</span><span>人次</span></div>
        </div>
        <div class="counts personal">
            <h6 id="personal">7月个人会议统计</h6>
            <div class="stress"><span>您的会议总时长</span><span class="duration" id="personMeetingTotalTime">0</span></div>
            <div class="sub"><span>预订会议</span><span id="reserveMeetingNum">0</span><span>场次</span></div>
            <div class="sub"><span>参与会议</span><span id="ParticipateMeetingNum">0</span><span>场次</span></div>
            <div class="sub"><span>占总会议</span><span id="personMeetingProportion">0</span><span></span></div>
        </div>
        <div class="chart">
            <div class="pie" id="pie1">
                <svg width="126" height="126">
                    <circle class="idle active" cx="63" cy="63" r="52" transform="rotate(90 63 63)"
                            stroke-dashoffset="0"></circle>
                    <circle class="occupied" cx="63" cy="63" r="52" transform="rotate(90 63 63)"
                            stroke-dashoffset="326.72"></circle>
                </svg>
                <span>空闲<i id="meetingRoomEmptiest">0%</i></span>
                <p>会议室空闲、使用占比</p>
            </div>
            <div class="pie" id="pie2">
                <svg width="126" height="126">
                    <circle class="personal active" cx="63" cy="63" r="52" transform="rotate(90 63 63)"
                            stroke-dashoffset="326.72"></circle>
                    <circle class="company" cx="63" cy="63" r="52" transform="rotate(90 63 63)"
                            stroke-dashoffset="0"></circle>
                </svg>
                <span>个人<i id="personMeetingTimeProportion">0%</i></span>
                <p>个人会议总时长占比</p>
            </div>
        </div>
    </div>
    <div class="flex flex-row">
        <div class="meeting-plan">
            <h5>近期会议安排<a class="more" href="javascript:void(0);" onclick="gotoMeetingList()">查看更多</a></h5>
        </div>
        <div class="meeting-dynamics">
            <h5>近期会议动态<a class="more" href="javascript:void (0)">查看更多</a></h5>
            <div class="meeting-dynamics-body"></div>
        </div>
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
                    <button class="btn btn-danger" type="submit">删除</button>
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
                    <button class="btn btn-danger" type="submit">确定</button>
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
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-control-label">确定通过预订申请吗？</label>
                        <input name="id" type="hidden">
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-clear-secondary" type="button" data-dismiss="modal">取消</button>
                    <button class="btn btn-primary" type="submit">通过</button>
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
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close"><span class="icon icon-close" aria-hidden="true"></span></button>
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
                    <button class="btn btn-danger" type="submit">不通过</button>
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
<!-- build:js static/scripts/index.js-->
<script src="<%=basePath%>static/scripts/plugins/datepicker.js"></script>
<script src="<%=basePath%>static/scripts/plugins/timerange.js"></script>
<script src="<%=basePath%>static/scripts/plugins/boardroom.js"></script>
<%--<script src="<%=basePath%>static/scripts/personnel.js"></script>--%>
<script src="<%=basePath%>static/scripts/personnelOrg.js"></script>
<script src="<%=basePath%>static/scripts/index.js"></script>
<script src="<%=basePath%>static/scripts/detail.js"></script>
<!-- endbuild-->
</body>
</html>