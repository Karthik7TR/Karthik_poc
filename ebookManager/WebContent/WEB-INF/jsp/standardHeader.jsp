<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.ModelUtils" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>

<c:set var="userFullName" value="<%=ModelUtils.getAuthenticatedUserFullName()%>"/>

<div class="leftHeader">
	<span class="programTitle">eBook Manager</span> <span class="welcomeMessage"> - Welcome ${userFullName}</span>
</div>
<div class="rightHeader">
	<ul class="navList">
		<li><a href="">LIBRARY</a></li>
		<li><a href="">ACTIVE/RECENT JOBS</a></li>
		<li><a href="">CREATE eBOOK DEFINITION</a></li>
		<li><a href="">PREFERENCES</a></li>
		<li><a href="">SUPPORT</a></li>
		<li><a href="j_spring_security_logout">Logout</a></li>
	</ul>

</div>