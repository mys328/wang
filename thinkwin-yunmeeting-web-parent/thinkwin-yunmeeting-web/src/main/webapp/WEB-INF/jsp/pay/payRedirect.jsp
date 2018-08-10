<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<body>
<c:if test="${not empty model.payForm}">
    ${model.payForm}
</c:if>
</body>
</html>