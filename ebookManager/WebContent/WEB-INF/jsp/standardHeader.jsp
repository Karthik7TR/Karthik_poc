<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.UserUtils" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="userFullName" value="<%=UserUtils.getAuthenticatedUserFullName()%>"/>

<div class="leftHeader">
	<span class="programTitle">THOMSON REUTERS</span> <span class="programTitle" style="color:orange">eBook Manager</span> <span class="welcomeMessage"> - Welcome ${userFullName}</span>
</div>
<div class="rightHeader">
	<ul class="navList">
		<li><a href="<%=WebConstants.MVC_BOOK_LIBRARY_LIST%>">LIBRARY</a></li>
		<li><a href="<%=WebConstants.MVC_JOB_SUMMARY%>">JOBS</a></li>
		<li><a href="<%=WebConstants.MVC_JOB_QUEUE%>">QUEUED</a></li>
		
		<%-- Check if user has role to access create book definition --%>
		<c:set var="showCreate" value="false"/>
		<sec:authorize access="hasAnyRole('ROLE_PUBLISHER,ROLE_SUPERUSER,ROLE_PUBLISHER_PLUS')">
			<c:set var="showCreate" value="true"/>
		</sec:authorize>
		<c:if test="${showCreate}">
			<li><a href="<%=WebConstants.MVC_BOOK_DEFINITION_CREATE%>">CREATE BOOK</a></li>
		</c:if>

		<%-- Check if user has role to access Admin --%>
		<c:set var="showAdmin" value="false"/>
		<sec:authorize access="hasRole('ROLE_SUPERUSER')">
			<c:set var="showAdmin" value="true"/>
		</sec:authorize>
		<c:if test="${showAdmin}">
			<li><a href="<%=WebConstants.MVC_ADMIN_MAIN%>">ADMINISTRATION</a></li>
		</c:if>
		<li><a href="j_spring_security_logout">Logout</a></li>
	</ul>
</div>
