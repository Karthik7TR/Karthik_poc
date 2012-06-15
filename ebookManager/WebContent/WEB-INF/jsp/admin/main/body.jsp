<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.UserUtils" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>

<%-- Check if user has Super User role --%>
<c:set var="onlySuperUser" value="false"/>
<sec:authorize access="hasRole('ROLE_SUPERUSER')">
	<c:set var="onlySuperUser" value="true"/>
</sec:authorize>

<div id="adminMain">
	<%-- Admin section only Super User can access --%>
	<c:if test="${onlySuperUser}">
		<div>
			<h3>Drop-down Menu</h3>
			<div class="description">Used in Book Definition Title Id generation</div>
			<div class="buttons">
				<a href="<%= WebConstants.MVC_ADMIN_JURIS_CODE_VIEW %>">Jurisdiction</a>
				<a href="<%= WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW %>">Publish Type</a>
				<a href="<%= WebConstants.MVC_ADMIN_STATE_CODE_VIEW %>">States</a>
			</div>
		</div>
		
		<div>
			<h3>ProView</h3>
			<div class="description">Options used in ProView</div>
			<div class="buttons">
				<a href="<%= WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW %>">Keywords</a>
			</div>
		</div>
		
		<div>
			<h3>Book Definition</h3>
			<div class="buttons">
				<a href="<%= WebConstants.MVC_ADMIN_BOOK_LOCK_LIST %>">Locks</a>
			</div>
		</div>
		
		<div>
			<h3>Miscellaneous Configuration</h3>
			<div class="buttons">
				<a href="<%= WebConstants.MVC_ADMIN_MISC %>">Miscellaneous Configuration</a>
			</div>
		</div>	
	</c:if>
	
	
	<div>
		<h3>Job Throttling</h3>
		<div class="buttons">
			<a href="<%= WebConstants.MVC_ADMIN_JOB_THROTTLE_CONFIG %>">Throttle Configuration</a>
		</div>
	</div>
	
	<div>
		<h3>Generator and Gatherer Switch</h3>
		<div class="buttons">
			<a href="<%=WebConstants.MVC_ADMIN_START_GENERATOR%>">Start All Book Generation</a>
			<a href="<%=WebConstants.MVC_ADMIN_STOP_GENERATOR%>">Stop All Book Generation</a>
		</div>
	</div>
	
	<div >
	<h3>Planned Outages</h3>
	<div class="buttons">
		<a href="<%=WebConstants.MVC_ADMIN_OUTAGE_ACTIVE_LIST%>">Active Outage List</a>
		<a href="<%=WebConstants.MVC_ADMIN_OUTAGE_TYPE_LIST%>">Outage Types</a>
	</div>
</div>

<div>
	<h3>Support Links</h3>
	<div class="buttons">
		<a href="<%=WebConstants.MVC_ADMIN_SUPPORT_VIEW%>">View Support Links</a>
	</div>
</div>
</div>

