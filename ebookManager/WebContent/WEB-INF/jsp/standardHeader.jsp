<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.ModelUtils" %>

<c:set var="userFullName" value="<%=ModelUtils.getAuthenticatedUserFullName()%>"/>

<div style="color:white;"><b>&nbsp; eBook Manager</b> - Welcome ${userFullName}</div>