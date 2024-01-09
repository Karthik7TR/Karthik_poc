<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.UserUtils" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>


<h3><a href="<%= WebConstants.MVC_ADMIN_MAIN %>">Admin</a></h3>

<%-- Check if user has Super User role --%>
<c:set var="onlySuperUser" value="false"/>
<sec:authorize access="hasRole('ROLE_SUPERUSER')">
	<c:set var="onlySuperUser" value="true"/>
</sec:authorize>

<%-- Admin section only Super User can access --%>
<c:if test="${onlySuperUser}">
	Drop-down Menu
	<ul>
		<li><a href="<%= WebConstants.MVC_ADMIN_JURIS_CODE_VIEW %>">Jurisdiction</a></li>
		<li><a href="<%= WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW %>">Publish Type</a></li>
		<li><a href="<%= WebConstants.MVC_ADMIN_STATE_CODE_VIEW %>">States</a></li>
		<li><a href="<%= WebConstants.MVC_ADMIN_USER_PROFILE_VIEW %>">User Profiles</a></li>
	</ul>
	
	ProView
	<ul>
		<li><a href="<%= WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW %>">Keywords</a></li>
	</ul>
	
	Book Definition
	<ul>
		<li><a href="<%= WebConstants.MVC_ADMIN_BOOK_LOCK_LIST %>">Locks</a></li>
		<li><a href="<%= WebConstants.MVC_ADMIN_AUDIT_BOOK_LIST %>">Audit</a></li>
	</ul>
	<br/>
	<a href="<%= WebConstants.MVC_ADMIN_MISC %>">Miscellanous Configuration</a><br/>
	<br/>
	Split Metrics
	<ul>
		<li><a href="<%= WebConstants.MVC_ADMIN_DOCTYPE_METRIC_VIEW %>">Document Type Metrics</a></li>
	</ul>
</c:if>

Generator and Gatherer Switch
<ul>
	<li><a href="<%=WebConstants.MVC_ADMIN_START_GENERATOR%>">Start Generator and Gatherer</a></li>
	<li><a href="<%=WebConstants.MVC_ADMIN_STOP_GENERATOR%>">Stop Generator and Gatherer</a></li>
</ul>
<a href="javascript:void(0);" class="queue_clean">Clean Job Queue</a><br/>
<br/>
<a href="<%= WebConstants.MVC_ADMIN_JOB_THROTTLE_CONFIG %>">Job Throttle Configuration</a><br/>
<br/>
<a href="<%=WebConstants.MVC_ADMIN_OUTAGE_ACTIVE_LIST%>">Planned Outage List</a><br/>
<br/>
<a href="<%=WebConstants.MVC_ADMIN_OUTAGE_TYPE_LIST%>">Outage Types</a><br/>
<br/>
<a href="<%=WebConstants.MVC_ADMIN_QUALITY_REPORTS%>">Quality Reports</a><br/>
<br/>
<a href="<%=WebConstants.MVC_ADMIN_SUPPORT_VIEW%>">View Support Links</a>
