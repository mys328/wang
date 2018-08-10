<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
	<head>
		<meta charset="UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
		<meta name="format-delection" content="telephone=no"/>
		<meta name="format-delection" content="email=no"/>
		<link rel="stylesheet" href="/static/fonts/demo.css">
	    <link rel="stylesheet" href="/static/fonts/iconfont.css">
	    <link rel="stylesheet" href="/static/css/scanMeeting/style.css">
		<title>企云会-连接组织的智慧</title>
	</head>
<body>
	<div class="box">
		<div class='meetingRoomDiv'>
			<div class='leftDiv'>

					<c:if test="${not empty room.getImgPath()}">
					<img src="${room.getImgPath()}" class='meetingRoomPicture'/>
				</c:if>
				<c:if test="${empty room.getImgPath()}">
					<img src="/static/images/scanMeeting/default-room.png" class='meetingRoomPicture'/>
				</c:if>
				<c:if test="${room.meetingRoomStatus == 'free'}">
					<img src='/static/images/scanMeeting/free@3x.png' class='meetingRoomstate'>
				</c:if>
				<c:if test="${room.meetingRoomStatus == 'busy'}">
					<img src='/static/images/scanMeeting/starting@3x.png' class='meetingRoomstate'>
				</c:if>
			</div>
			<div class='rightDiv'>
				<div class="meetingRoomName">${room.getMeetingRoomName()}</div>
				<div class="meetingRoomcapacity">容量${room.getCapacity()}人</div>
				<p>
					<c:if test="${room.hasMicrophone == 1}">
						<span class='bor1'>扩音</span>
					</c:if>
					<c:if test="${room.hasDisplay == 1}">
						<span class='bor2'>显示</span>
					</c:if>
					<c:if test="${room.hasVedioMeeting == 1}">
						<span class='bor3'>视频</span>
					</c:if>
                    <c:if test="${room.hasWhiteboard == 1}">
						<span class='bor3'>白板</span>
					</c:if>
				</p>
			</div>
		</div>
        <div class="scanMeeting">
		<c:if test="${room.meetingList.size() == 0}">
			   <div class="notmeeting">
			     	<div class="iconfont icon-waitcup blankBj"></div>
			     	 <span>今天暂无会议安排</span>
				</div>
		</c:if>
		<c:if test="${room.meetingList.size() != 0}">
        <div class="meetinglistAll">
			  <c:forEach var="meeting" items="${room.getMeetingList()}">
					<div class="meetinglistDiv">
						<div class="meetinglisticon">
							<div class="iconfont icon-ewm-meeting"></div>
						</div>
						<div class="meetinglisttopDiv">
							<span class="meetingTime">${meeting.getBeginTime()}--${meeting.getEndTime()}</span>
							<span class="iconfont icon-xq-user personnel"></span>
							<span class="personnelName"> ${meeting.getCreateUser()}</span>
						</div>
						<div class="meetinglistbottomDiv">
							<span> ${meeting.getDurasion()}</span>
						</div>
					</div>
		    </c:forEach>
        </div>
		</c:if>
		</div>
		<div class="bottomDiv">
			<span>共${room.meetingList.size()}个会议</span>
            <a href="/wechat/scanMeeting/meetingPage?tenantId=${tenantId}&meetingRoomId=${meetingRoomId}"><div>立即预订</div></a>
		</div>
	</div>
</body>
</html>