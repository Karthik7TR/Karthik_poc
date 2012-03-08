<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.UserUtils" %>

<c:set var="userRoles" value="<%=UserUtils.getUserRolesAsCsv()%>"/>

<div class="leftFooter">
	<b>&copy; 2012 Thomson Reuters</b>
	<span style="margin-left: 20px;">(${userRoles})</span>
</div>
<div class="rightFooter">
	<img src="theme/images/trlogo.gif"/>
</div>