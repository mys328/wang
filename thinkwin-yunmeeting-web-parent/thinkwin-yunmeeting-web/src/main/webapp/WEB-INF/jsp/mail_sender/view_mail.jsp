<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>企云会-连接组织的智慧</title>
    <script type="text/javascript">
        function sendMail() {
            document.getElementById("mailSenderForm").submit();
        }
    </script>
</head>
<body>
<form id="mailSenderForm" action="http://localhost:8100/mailtest/sendermail">
    <input type="button" value="发送邮件" onclick="sendMail()">
</form>
</body>
</html>
