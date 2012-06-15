<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.UserUtils" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="userFullName" value="<%=UserUtils.getAuthenticatedUserFullName()%>"/>

<div class="headerLeftSide">
	<span class="programTitle">THOMSON REUTERS <span style="color:orange">eBook Manager</span></span>  <span class="welcomeMessage"> - Welcome ${userFullName}</span>
</div>
<div class="headerRightSide">
	<ul class="navList">
		<li><a href="<%=WebConstants.MVC_BOOK_LIBRARY_LIST%>">LIBRARY</a></li>
		<li><a href="<%=WebConstants.MVC_PROVIEW_TITLES%>">PROVIEW LIST</a></li>
		<li><a href="<%=WebConstants.MVC_BOOK_AUDIT_LIST%>">AUDIT</a></li>
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
		
		<li><a href="<%=WebConstants.MVC_USER_PREFERENCES%>">PREFERENCES</a></li>
		
		<%-- Check if user has role to access Admin --%>
		<c:set var="showAdmin" value="false"/>
		<sec:authorize access="hasAnyRole('ROLE_SUPPORT,ROLE_SUPERUSER')">
			<c:set var="showAdmin" value="true"/>
		</sec:authorize>
		<c:if test="${showAdmin}">
			<li><a href="<%=WebConstants.MVC_ADMIN_MAIN%>">ADMIN</a></li>
		</c:if>
		<li><a href="<%=WebConstants.MVC_SUPPORT_PAGE_VIEW%>">SUPPORT LINKS</a></li>
		<li><a href="j_spring_security_logout">LOGOUT</a></li>
	</ul>
</div>
