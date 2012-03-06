<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.UserUtils" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>

<c:set var="userFullName" value="<%=UserUtils.getAuthenticatedUserFullName()%>"/>

<div class="leftHeader">
	<span class="programTitle">THOMSON REUTERS</span> <span class="programTitle" style="color:orange">eBook Manager</span> <span class="welcomeMessage"> - Welcome ${userFullName}</span>
</div>
<div class="rightHeader">
	<ul class="navList">
		<li><a href="<%=WebConstants.MVC_BOOK_LIBRARY_LIST%>">LIBRARY</a></li>
		<li><a href="<%=WebConstants.MVC_JOB_SUMMARY%>">JOBS</a></li>
		<li><a href="<%=WebConstants.MVC_BOOK_DEFINITION_CREATE%>">CREATE eBOOK DEFINITION</a></li>
		<li><a href="<%=WebConstants.MVC_PREFERENCES%>">PREFERENCES</a></li>
		<li><a href="<%=WebConstants.MVC_SUPPORT%>">SUPPORT</a></li>
		<li><a href="j_spring_security_logout">Logout</a></li>
	</ul>
</div>