<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/5/24
  Time: 15:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>http://www.baidu.com</title>

</head>
<body>
<input type="hidden" id="qq" value="${url}">



<script>
    window.onload = function () {

        var aa=document.getElementById("qq").value;
        console.log(aa);
        window.location.href= aa;
    }


</script>
</body>
</html>
