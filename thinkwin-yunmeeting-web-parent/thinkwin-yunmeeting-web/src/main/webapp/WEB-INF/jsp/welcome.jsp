<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@taglib prefix="mvc" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title>SpringMVC<spring:message code="internationalisation" /></title>
</head>
<body>
Language: <a href="?lang=zh_CN"><spring:message code="language.cn" /></a> - <a href="?lang=en_US"><spring:message code="language.en" /></a>
<h2>
    <spring:message code="welcome" />
    <spring:message code="hello" />
    <spring:message code="auth.hello" />
    <spring:message code="mailesender.hello" />

    <spring:message code="test" />

</h2>
Locale: ${pageContext.response.locale }
</body>
</html>