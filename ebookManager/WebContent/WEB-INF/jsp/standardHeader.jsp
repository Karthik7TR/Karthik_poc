<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.ModelUtils" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>

<c:set var="userFullName" value="<%=ModelUtils.getAuthenticatedUserFullName()%>"/>

<div style="color:white;">

<b>&nbsp; eBook Manager</b> - Welcome ${userFullName}
&nbsp; <a href="j_spring_security_logout">Logout</a>

</div>