<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->

<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsFilterForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsFilterForm.DisplayTagSortProperty"%>

<c:set var="defaultPageSize" value="<%=WebConstants.DEFAULT_PAGE_SIZE%>"/>

<script type="text/javascript" src="js/form-utils.js"></script>
<script type="text/javascript">
	const opp = "${ pageSize == null ? defaultPageSize : pageSize }";
	$(window).on('pageshow', function () {
		$('#objectsPerPage option[value=' + opp + ']').prop('selected', true);
	});
</script>

<%-- Select for how may items (rows) per page to show --%>
<form:form id="bodyForm"
					 action="<%=WebConstants.MVC_STATS%>"
					 modelAttribute="<%=PublishingStatsFilterForm.FORM_NAME%>"
					 method="get">
	<c:if test="${fn:length(paginatedList.list) != 0}">
		Items to display:
		<form:select path="objectsPerPage" onchange="submitLeftFormAndBodyForm()">
			<form:option label="${defaultPageSize}" value="${defaultPageSize}"/>
			<form:option label="50" value="50"/>
			<form:option label="100" value="100"/>
			<form:option label="150" value="150"/>
			<form:option label="300" value="300"/>
			<%-- Shows to MAX_INT.  Needs to get updated once number of books reach this amount --%>
			<form:option label="ALL" value="<%= Integer.MAX_VALUE %>"/>
		</form:select>
	</c:if>  <%-- if (table row count > 0) --%>
	<form:hidden path="sort" value="${ param.sort }"/>
	<form:hidden path="dir" value="${ param.dir }"/>
</form:form>

	<%-- Table of publishing stats for a specific book --%>
	<c:set var="DATE_FORMAT" value="<%=CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>

	<display:table id="stats" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2"
								 requestURI="<%=WebConstants.MVC_STATS%>"
								 sort="external">
	  <display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
	  <display:setProperty name="paging.banner.onepage" value=" " />
	  <display:column title="ProView Display Name" property="audit.proviewDisplayName" sortable="true" sortProperty="<%= DisplayTagSortProperty.PROVIEW_DISPLAY_NAME.toString() %>" />
	  <display:column title="Book Definition ID" property="ebookDefId" sortable="true" sortProperty="<%= DisplayTagSortProperty.EBOOK_DEFINITION_ID.toString() %>"/>
	  <display:column title="Job Submit Timestamp" property="jobSubmitTimestamp" sortable="true" sortProperty="<%= DisplayTagSortProperty.JOB_SUBMIT_TIMESTAMP.toString() %>"/>
	  <display:column title="Job Instance ID" sortable="true" sortProperty="<%= DisplayTagSortProperty.JOB_INSTANCE_ID.toString() %>">
	  	<a href="<%=WebConstants.MVC_JOB_INSTANCE_DETAILS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${stats.jobInstanceId}">${stats.jobInstanceId}</a>
	  </display:column>
	  <display:column title="Title ID" sortable="true" sortProperty="<%= DisplayTagSortProperty.TITLE_ID.toString() %>">
		  <c:choose>
			  <c:when test="${not empty stats.combinedBookDefinitionId}">
				  <a href="<%=WebConstants.MVC_COMBINED_BOOK_DEFINITION_VIEW%>?<%=WebConstants.KEY_ID%>=${stats.combinedBookDefinitionId}">${stats.audit.titleId}</a>
			  </c:when>
			  <c:otherwise>
				  <a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${stats.ebookDefId}">${ stats.audit.titleId }</a>
			  </c:otherwise>
		  </c:choose>
	  </display:column>
	  <display:column title="Book Version" property="bookVersionSubmitted" />
	  <display:column title="Publish Status" property="publishStatus" sortable="true" sortProperty="<%= DisplayTagSortProperty.PUBLISH_STATUS.toString() %>"/>
	  <display:column title="Book Size" property="bookSize" sortable="true" sortProperty="<%= DisplayTagSortProperty.BOOK_SIZE.toString() %>" />
	  <display:column title="Largest Doc Size" property="largestDocSize"  sortable="true" sortProperty="<%= DisplayTagSortProperty.LARGEST_DOC_SIZE.toString() %>"/>
	  <display:column title="Largest Image Size" property="largestImageSize" sortable="true" sortProperty="<%= DisplayTagSortProperty.LARGEST_IMAGE_SIZE.toString() %>" />
	  <display:column title="Largest Pdf Size" property="largestPdfSize" sortable="true" sortProperty="<%= DisplayTagSortProperty.LARGEST_PDF_SIZE.toString() %>" />
	  <display:column title="Metrics" sortable="false">
	  	<a href="<%=WebConstants.MVC_BOOK_JOB_METRICS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${stats.jobInstanceId}">View Metrics</a>
	  </display:column>
	</display:table>
	
	<div>
		The filter is applied to the results shown in the file.  The maximum amount of rows on the Excel file cannot exceed 65535.
	</div>
	<a id="excelExport" href="<%= WebConstants.MVC_STATS_DOWNLOAD %>">Download Excel</a>

