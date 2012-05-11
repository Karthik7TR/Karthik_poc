<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.UserUtils" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>

<%-- Check if user has Super User role --%>
<c:set var="onlySuperUser" value="false"/>
<sec:authorize access="hasRole('ROLE_SUPERUSER')">
	<c:set var="onlySuperUser" value="true"/>
</sec:authorize>

<%-- Admin section only Super User can access --%>
<c:if test="${onlySuperUser}">
	<div class="centerSection">
		<h3>Drop-down Menu</h3>
		<div class="buttons">
			<a href="<%= WebConstants.MVC_ADMIN_JURIS_CODE_VIEW %>">Jurisdiction</a>
			<a href="<%= WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW %>">Publish Type</a>
			<a href="<%= WebConstants.MVC_ADMIN_STATE_CODE_VIEW %>">States</a>
		</div>
	
	</div>
	
	<div class="centerSection">
		<h3>ProView</h3>
		<div class="buttons">
			<a href="<%= WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW %>">Keywords</a>
			<a href="<%=WebConstants.MVC_ADMIN_KEYWORD_PROVIEW_TITLES%>">Titles</a>
		</div>
	</div>
	
	<div class="centerSection">
		<h3>Book Definition</h3>
		<div class="buttons">
			<a href="<%= WebConstants.MVC_ADMIN_BOOK_LOCK_LIST %>">Locks</a>
		</div>
	</div>
	
</c:if>


<div class="centerSection">
	<h3>Job Throttling</h3>
	<div class="buttons">
		<a href="<%= WebConstants.MVC_ADMIN_JOB_THROTTLE_CONFIG %>">Throttle Configuration</a>
	</div>
</div>

<div class="centerSection">
	<h3>Stop Generator and Gatherer</h3>
	<div class="buttons">
		<a href="<%=WebConstants.MVC_ADMIN_STOP_GENERATOR%>">Stop All Book Generation</a>
	</div>
</div>
