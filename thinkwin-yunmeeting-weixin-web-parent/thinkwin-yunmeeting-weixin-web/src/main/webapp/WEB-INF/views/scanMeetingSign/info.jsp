<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2018/7/17
  Time: 10:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
    <meta name="format-delection" content="telephone=no"/>
    <meta name="format-delection" content="email=no"/>
    <title>签到验证</title>
    <style type="text/css">
        *{margin: 0;padding: 0;}
        a{text-decoration: none;color: #000}
        ul,li,ol {list-style: none;}
        html,body {width: 100%;height: 100%;background:#fbfaff;}
        .box{width: 100%;height:100%;position: relative;}
        div,a,img {
            -webkit-tap-highlight-color: transparent;
            -webkit-touch-callout: none;
            -webkit-user-select: none;
            user-select:none;
        }
        .sec1{width: 100%;text-align: center;}
        .dv1 img{width: 122px;height:136px;margin-top: 60px;margin-bottom: 16px}
        .dv2{color: #a3b1cc;font-size: 14px;margin-bottom: 52px;}
        .dv3{color: #5c5c73;font-size: 18px;line-height: 1.4;text-align: center;margin-bottom: 30px;width:279px;display: inline-block;}
        .dv4 {width: 300px;text-align: left;display: inline-block;height: 150px;box-shadow: 0px 4px 13px 0px rgba(106,102,128,0.5)}
        .dv4 p{font-size: 18px;color: #5c5c73;margin-left: 20px;height: 44px;line-height: 44px;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;}
        .dv4 p img{width: 21px;height: 19px;margin-right: 15px;position: relative;top: 2px;}
        .dv5{font-size: 18px;color: #5c5c73;}
    </style>
</head>
<body>
<div class="box">
    <c:if test="${code == '1'}">
        <section class="sec1">
            <div class='dv1'><img src='/static/images/success@3x.png'></div>
            <div class='dv2'>恭喜您！签到成功</div>
            <div class='dv3'>${msg}</div>
            <div class='dv4'>
                <p style='margin-top: 9px;'>
                    <img src='/static/images/user@3x.png'>发起人:  ${orgName}</p >
                <p><img src='/static/images/alarm@3x.png'>时间:  ${data}</p >
                <p><img src='/static/images/location-copy@3x.png'>地点:  ${location}</p >
            </div>
        </section>
    </c:if>
    <c:if test="${code == '4'}">
        <section class="sec1">
            <div class='dv1'><img src='/static/images/success@3x.png'></div>
            <div class='dv2'>您已签到</div>
            <div class='dv3'>${msg}</div>
        </section>
    </c:if>
    <c:if test="${code != '1' && code != '4'}">
     <section class="sec1" id="sec2">
        <div class='dv1'><img src='/static/images/error@3x.png'></div>
        <div class='dv2'>抱歉！签到失败了</div>
        <div class='dv5'>${msg}</div>
    </section>
    </c:if>
</div>
</body>
</html>