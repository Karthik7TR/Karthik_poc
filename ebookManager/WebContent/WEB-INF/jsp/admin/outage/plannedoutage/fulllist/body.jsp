<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<div class="centerSection">
	<c:set var="DATE_FORMAT" value="<%= WebConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	<a href="<%=WebConstants.MVC_ADMIN_OUTAGE_CREATE%>">Create Outage Schedule</a>
	<br/>
	<%-- Table of ContentType --%>
	<display:table id="<%= WebConstants.KEY_VDO %>" name="<%= WebConstants.KEY_OUTAGE %>" class="displayTagTable" cellpadding="2"
		requestURI="<%=WebConstants.MVC_ADMIN_OUTAGE_FULL_LIST%>"
		pagesize="20"
		partialList="false"
		defaultsort="2"
		defaultorder="descending">
	  <display:setProperty name="basic.msg.empty_list">No outage schedule was found.</display:setProperty>
	  <display:setProperty name="paging.banner.onepage" value=" " />
	  <display:column title="Outage Type" >
	  	System: ${ vdo.outageType.system } <br/>
	  	Sub-system: ${ vdo.outageType.subSystem }
	  </display:column>
	  <display:column title="Start Date/Time" sortable="true">
	  	<fmt:formatDate value="${vdo.startTime}" pattern="${DATE_FORMAT}"/>
	  </display:column>
	  <display:column title="End Date/Time" sortable="true">
	  	<fmt:formatDate value="${vdo.endTime}" pattern="${DATE_FORMAT}"/>
	  </display:column>
	  <display:column title="Reason" property="reason" />
	  <display:column title="System Impact Description" property="systemImpactDescription" />
	  <display:column title="Servers Impacted" property="serversImpacted" />
	  <display:column title="Notification Email Sent?" property="notificationEmailSent" />
	  <display:column title="All Clear Email Sent?" property="allClearEmailSent" />
	  <display:column title="Updated By" property="updatedBy" sortable="true" />
	  <display:column title="Last Updated" sortable="true">
	  	<fmt:formatDate value="${vdo.lastUpdated}" pattern="${DATE_FORMAT}"/>
	  </display:column>
	</display:table>
</div>