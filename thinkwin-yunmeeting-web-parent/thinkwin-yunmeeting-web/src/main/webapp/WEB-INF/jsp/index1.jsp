<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">
    <script type="text/javascript" src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <title>测试iframe结构</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <script type="text/javascript">
        function chufaceshi(ob){
            //var userId = $("#userId").val();
            var token = $("#token").val();
            var id=$(ob).attr("id");
            var url=$("#url"+id).val();
            var oIframe=document.getElementById('iframe1');
            //oIframe.src = url+"?userId="+userId+"&token="+token;
            oIframe.src = url+"?token="+token;
        };


    </script>


</head>
<body>
<%@ include file="/WEB-INF/jsp/common/left.jsp"%>
<div style="float:left">

<div>企业名称：${tenantName}</div>
<div>企业logo：${companyLogo}</div>
<input type="hidden" id="userId" value="${userId}"/>
<input type="hidden" id="token" value="${token}"/>
<%--<c:forEach items="${list}" var="user">
    <tr>
        <td align = "center">
            <input id="${user.id}" type="button" value="${user.menuName}" onclick="chufaceshi(this)" />
            <input type="hidden" id="url${user.id}" value="${user.url}"/>
        </td><br/>
    </tr>
</c:forEach>--%>
</div>
<div style="float:right" >
<iframe id="iframe1" src="login.jsp" scrolling="auto" width="1130" height="630"></iframe>
</div>
<br/><br/><br/><br/>
<a onclick="gotoAddressListPage()">通讯录</a>
<br/><br/><br/><br/><br/><br/>
<a onclick="operationLog()">控制台</a><br/><br/><br/><br/><br/><br/>
<a onclick="gotoMainTopPage()">主Top页面</a>
<br/><br/><br/><br/><br/><br/>
<a onclick="testImportExcel()">导入测试</a><br/><br/><br/><br/><br/><br/>
<sec:authorize ifAnyGranted="系统管理员">hello： security页面标签测试</sec:authorize><br/>
<sec:authorize ifAnyGranted="系统管理员,普通员工,管理员">
        测试数据：：：：：
</sec:authorize>
<sec:authorize  ifAllGranted="系统管理员">
    哈哈哈
</sec:authorize>
<h1><a onclick="gotoMeetingPage()">会议列表</a></h1>

<h1><a onclick="gotoMeetingDynamicPage()">会议动态</a></h1>

<h1><a onclick="gotoSeletMeetingDynamicPage()">会议动态</a></h1>
<br/><br/><br/><br/><br/><br/>

<a onclick="gotoMeetingPages()">会议列表筛选测试</a>
<h1><a onclick="gotoMeetingRoom()">会议室</a></h1>

<h1><a onclick="gotoAdminPage()">企业设置</a></h1>
<script>
    function gotoAddressListPage() {
        var token = $("#token").val();
        window.location.href="/gotoAddressListPage?token="+token;
    }

    function gotoMainTopPage() {
        var token = $("#token").val();
        window.location.href="/gotoMainTopPage?token="+token;
    }
    function gotoMeetingRoom() {
        var token = $("#token").val();
        window.location.href="/meetingRoom/skipMeetingRoom?token="+token;
    }
    function operationLog() {
        var userInfo = JSON.parse(localStorage.getItem('userinfo'));

        var userId = userInfo.userId;
        var token = userInfo.token;
        window.location.href="/log/operationLog?userId="+userId+"&token="+token;
    }
    function testImportExcel() {
        var userId = $("#userId").val();
        var token = $("#token").val();
        window.location.href="/goImprotPage?userId="+userId+"&token="+token;
    }

    function gotoAdminPage() {
        var token = $("#token").val();
        window.location.href="/gotoEnterpriseSettingPage?token="+token;
    }
    function gotoMeetingPages() {
        var token = $("#token").val();
        window.location.href="/search/skipMeetingScreening?token="+token;
    }
    function gotoMeetingPage(){
        var token = $("#token").val();
        $.ajax({
            type:"post",
            dataType:"json",
            url:"/meeting/selectsettimemeeting",
            data:{
                token:token,
                start:"1502294400000"
            },
            success:function (data) {
                console.log(data);
            }
        });


        /*window.location.href="/meeting/selectmeetingdayview?token="+token;*/
    }
    function gotoMeetingDynamicPage(){
        var token = $("#token").val();

        $.ajax({
            type:"post",
            dataType:"json",
            url:"/dynamic/addmeetingdynamic",
            data:{
                token:token,
                conferenceId:"2",
                content:"fdfdfkdfjfkkdldfas",
                dynamicsType:"0"
            },
            success:function (data) {
                console.log(data);
            }
        });
        /*window.location.href="/meeting/selectmeetingdayview?token="+token;*/
    }
    function gotoSeletMeetingDynamicPage(){
        var token = $("#token").val();
        console.log(token);
        $.ajax({
            type:"post",
            dataType:"json",
            url:"/dynamic/selectrecentmeetingdynamic",
            data:{
                token:token,
                currentPage:1,
                pageSize:20,
                all:"0"
            },
            success:function (data) {
                console.log(data);
            }
        });
        /*window.location.href="/meeting/selectmeetingdayview?token="+token;*/
    }
</script>

</body>


</html>
