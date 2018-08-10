<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script src="<%=basePath%>static/js/common/mainTop.js"></script>
<style>
.icon-message-count {
    min-width: 14px;
    text-align: center;
    width:3px;
    height:3px;
    line-height: 14px;
    display: inline-block;
    position: relative;
    right: -13px;
    top: -44px;
    background: red;
    color: #fff;
    border: 3px solid red;
    border-radius:50%;
    padding: 7px;
    transform: scale(.4);
    font-size: 3px;
}
/*扫码绑定微信*/
#bindWechatQuick .bind-quick-mark{
    background:#ffffff;
    box-shadow:0 2px 24px 0 rgba(0,0,0,0.14);
    border-radius:2px;
    width:322px;
    height:372px;
    left:-125px;
    top:117%;
    padding: 17px 28px;
    box-sizing: border-box;
    margin: 0;
}
#bindWechatQuick .bind-quick-mark p{
    margin-bottom: 0;
    font-size: 14px;
    color: #333;
}
#bindWechatQuick .bind-quick-mark p:nth-child(1){
    margin-bottom: 0;
    line-height: 25px;
}
#bindWechatQuick .bind-quick-mark p:nth-child(2){
    margin-bottom: 18px;
}
#bindWechatQuick .bind-quick-mark::after{
    left: 161px;
    top: -8px;
    position: absolute;
    display: inline-block;
    width: 0;
    height: 0;
    vertical-align: middle;
    content: "";
    border-bottom: 8px solid #fff;
    border-right: 8px solid transparent;
    border-left: 8px solid transparent;
}
#bindWechatQuick .qr-code{
    width: 194px;
    padding: 16px 16px 0 16px;
    margin: 0 auto;
    border: 1px solid #1cb1f0;
    box-shadow: 0 8px 16px 0 rgba(20, 185, 255, 0.25);
    border-radius: 4px;
    background-color: #35baf3;
    color: #fff;
    text-align: center;
}
#bindWechatQuick .qr-code img{
    width: 162px;
    height: 162px;
    margin-bottom: 8px;
}
#bindWechatQuick .qr-code p{
    font-size: 14px;
    font-weight: 600;
    color: #fff;
    margin-bottom: 10px;
    line-height: 1.4;
}
#bindWechatQuick  .closed{
    line-height: normal;
    font-size: 14px;
    width: 28px;
    padding-left:0;
    margin: 24px auto 0;
    display: block;
    color:#35baf2;
}
#bindWechat{
    padding-left: 32px;
}
#bindWechat  .icon-yunmeetingswechat{
    font-size:27px;height:48px;line-height:48px;cursor: pointer;
}
</style>
<input type="hidden" value=0 id="pageType">
<div class="d-flex flex-row justify-content-between align-items-center">
    <a id="mainTopPoto" class="navbar-brand"></a>
    <ul class="nav">
        <li class="nav-item" name="首页">
            <a class="nav-link" href="javascript:void (0)" onclick="gotoMainPage1()">首页<span class="sr-only">(current)</span></a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="javascript:void(0);" onclick="gotoMyMeeting()">我的会议</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="javascript:void(0);" onclick="gotoMeetingReserve()">会议预订</a>
        </li>
        <li class="nav-item"  id="ConsoleLoginPageId" style="display: none">
            <a class="nav-link btn-authorize" href="javascript:void(0);" onclick="gotoConsoleLoginPage()">控制台</a>
        </li>
        <%-- <sec:authorize ifAnyGranted="主管理员,子管理员,会议室管理员">--%>
       <%-- </sec:authorize>--%>
        <li class="nav-item dropdown" id="bindWechatQuick">
            <a class="nav-link" id="bindWechat" href="javascript:void(0);"  data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="icon icon-yunmeetingswechat"></span></a>
            <div  class="dropdown-menu dropdown-menu-right bind-quick-mark" aria-labelledby="menu" id="bindWechatBox">
                <p>关注微信公众号，才可以收到会议提醒哟，</p>
                <p>更可享受微信快捷订会功能，赶紧试试吧！</p>
                <div class="qr-code">
                    <img src="">
                    <p class="company-name"></p>
                </div>
                <a class="closed" data-toggle="dropdown" href="#bindWechatBox" role="menu" aria-controls="addPerson">关闭</a>
            </div>
        </li>
        <li class="nav-item">
            <a class="nav-link infos" href="javascript:void(0);" style="cursor:default;font-size: 23px;"><span  class="icon icon-nav-dynamics" onclick="gotoViewMoredynamicPage()" style="height:48px;line-height:48px;cursor: pointer"><span id="redPoint" style="display: none" class="icon-message-count"></span></span></a>
        </li>
        <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" id="mainTop" href="javascript:void(0);" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"></a>
            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="menu">
                <a class="dropdown-item"  href="javascript:void(0);" onclick="personalData1()"><i class="icon icon-personaldata"></i>个人资料</a>
                <a class="dropdown-item"  href="javascript:void(0);" onclick="logout()"><i class="icon icon-exit"></i>退出登录</a>
            </div>
        </li>
    </ul>
</div>