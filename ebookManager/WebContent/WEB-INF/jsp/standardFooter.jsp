<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.UserUtils" %>

<c:set var="userRoles" value="<%=UserUtils.getUserRolesAsCsv()%>"/>

<div class="leftFooter">
	<b>&copy; 2012 Thomson Reuters</b>
	<c:if test="${not fn:containsIgnoreCase('prod', environmentName) }">
		<span style="margin-left: 20px;">Environment: ${environmentName} &nbsp; ProView: ${proviewDomain} &nbsp; (${userRoles})</span>
	</c:if>
</div>
<div class="rightFooter">
	<img src="theme/images/trlogo.gif"/>
</div>