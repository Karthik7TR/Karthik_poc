<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsForm.DisplayTagSortProperty"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort"%>

<%-- Select for how may items (rows) per page to show --%>
	<c:if test="${fn:length(paginatedList.list) != 0}">
	  <form:form id="itemCountForm" action="<%=WebConstants.MVC_STATS_CHANGE_ROW_COUNT%>"
			     commandName="<%=PublishingStatsForm.FORM_NAME%>" method="post">
		Items to display: 
		<c:set var="defaultItemsPerPage" value="<%=PageAndSort.DEFAULT_ITEMS_PER_PAGE%>"/>
		<form:select path="objectsPerPage" onchange="submit()">
			<form:option label="${defaultItemsPerPage}" value="${defaultItemsPerPage}"/>
			<form:option label="50" value="50"/>
			<form:option label="100" value="100"/>
			<form:option label="150" value="150"/>
			<form:option label="300" value="300"/>
			<%-- Shows to MAX_INT.  Needs to get updated once number of books reach this amount --%>
			<form:option label="ALL" value="<%= Integer.MAX_VALUE %>"/>
		</form:select>
	  </form:form>
	</c:if>  <%-- if (table row count > 0) --%>	
	
	<%-- Table of publishing stats for a specific book --%>
	
	<c:set var="DATE_FORMAT" value="<%=CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>

	<display:table id="<%= WebConstants.KEY_VDO %>" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
		requestURI="<%=WebConstants.MVC_STATS_PAGE_AND_SORT%>"
		sort="external">
	  <display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
	  <display:setProperty name="paging.banner.onepage" value=" " />
	  
	  <display:column title="jobSubmitTimestamp" property="jobSubmitTimestamp" sortable="true" sortProperty="<%= DisplayTagSortProperty.JOB_SUBMIT_TIMESTAMP.toString() %>"/>
	  <display:column title="jobInstanceId" sortable="true" sortProperty="<%= DisplayTagSortProperty.JOB_INSTANCE_ID.toString() %>">
	  	<a href="<%=WebConstants.MVC_JOB_INSTANCE_DETAILS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${vdo.jobInstanceId}">${vdo.jobInstanceId}</a>
	  </display:column>
	  <display:column title="titleId" sortable="true" sortProperty="<%= DisplayTagSortProperty.TITLE_ID.toString() %>">
	  	<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${vdo.ebookDefId}">${ vdo.audit.titleId }</a>
	  </display:column>
	  <display:column title="Book Version" property="bookVersionSubmitted" />
	  <display:column title="publishStatus" property="publishStatus" sortable="true" sortProperty="<%= DisplayTagSortProperty.PUBLISH_STATUS.toString() %>"/>
	  <display:column title="bookSize" property="bookSize" sortable="true" sortProperty="<%= DisplayTagSortProperty.BOOK_SIZE.toString() %>" />
	  <display:column title="largestDocSize" property="largestDocSize"  sortable="true" sortProperty="<%= DisplayTagSortProperty.LARGEST_DOC_SIZE.toString() %>"/>
	  <display:column title="largestImageSize" property="largestImageSize" sortable="true" sortProperty="<%= DisplayTagSortProperty.LARGEST_IMAGE_SIZE.toString() %>" />
	  <display:column title="largestPdfSize" property="largestPdfSize" sortable="true" sortProperty="<%= DisplayTagSortProperty.LARGEST_PDF_SIZE.toString() %>" />
	  <display:column title="Metrics" sortable="false">
	  	<a href="<%=WebConstants.MVC_BOOK_JOB_METRICS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${vdo.jobInstanceId}">View Metrics</a>
	  </display:column>
	</display:table>
	
	<div>
		The filter is applied to the results shown in the file.  The maximum amount of rows on the Excel file cannot exceed 65535.
	</div>
	<a href="<%= WebConstants.MVC_STATS_DOWNLOAD %>">Download Excel</a>

