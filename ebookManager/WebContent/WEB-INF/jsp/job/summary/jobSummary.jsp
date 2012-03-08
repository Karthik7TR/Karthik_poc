<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryForm.JobCommand"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.FilterForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.PageAndSort"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.PageAndSort.DisplayTagSortProperty"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="org.springframework.batch.core.BatchStatus"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script>
$(document).ready(function() {
	$('#selectAll').click(function () {
		$(this).parents('#<%= WebConstants.KEY_VDO %>').find(':checkbox').attr('checked', this.checked);
	});
});

function submitJobSummaryForm(command) {
	$("#jobCommand").val(command);  // Set the form hidden field value for the operation discriminator
	$("#<%=JobSummaryForm.FORM_NAME%>").submit();	// POST the HTML form
}
</script>

<c:set var="DATE_FORMAT" value="<%=WebConstants.DATE_TIME_FORMAT_PATTERN %>"/>

<form:form action="<%=WebConstants.MVC_JOB_SUMMARY_JOB_OPERATION%>"
		   commandName="<%=JobSummaryForm.FORM_NAME%>" name="theForm" method="post">
	<form:hidden path="jobCommand"/>
	
	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=JobSummaryForm.FORM_NAME%>">
		<div class="errorBox">
	      <b><spring:message code="please.fix.errors"/>:</b><br/>
	      <form:errors path="*">
	      	<ul>
			<c:forEach items="${messages}" var="message">
				<li style="color: black">${message}</li>
			</c:forEach>
	      	</ul>
		  </form:errors>
		  <br/>
	    </div>
    </spring:hasBindErrors>

    <%-- Informational messages - used to report status of job stop and/or restart operations. --%>
 	<ul>
 	<c:forEach items="${infoMessages}" var="message">
 		<c:if test="${message.type == 'SUCCESS'}">
 			<c:set var="cssStyle" value="color:darkgreen;"/>
 		</c:if>
 		 <c:if test="${message.type == 'FAIL' || message.type == 'ERROR'}">
 			<c:set var="cssStyle" value="color:red;"/>
 		</c:if>
		<li style="${cssStyle}">${message.text}</li>
	</c:forEach>
 	</ul>

	<%-- Table of job executions --%>
	<c:set var="selectAllElement" value="<input type='checkbox' id='selectAll' value='false' />"/>
	<display:table id="vdo" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
				   requestURI="<%=WebConstants.MVC_JOB_SUMMARY_PAGE_AND_SORT%>"
				   sort="external">
	  <display:setProperty name="basic.msg.empty_list">No job executions were found.</display:setProperty>
	  <display:column title="${selectAllElement}"  style="text-align: center">
  		<form:checkbox path="jobExecutionIds" value="${vdo.jobExecution.id}"/>
  	  </display:column>
	  <display:column title="Book Name" sortable="true" sortProperty="<%=DisplayTagSortProperty.BOOK_NAME.toString()%>" style="text-align: left">
	  	<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_TITLE_ID%>=${vdo.bookInfo.titleId}">${vdo.bookInfo.bookName}</a>
	  </display:column>
	  <display:column title="Title ID" property="bookInfo.titleId" sortable="true" sortProperty="<%=DisplayTagSortProperty.TITLE_ID.toString()%>"style="text-align: left"/>
	  <display:column title="Inst &nbsp;" sortable="true" sortProperty="<%=DisplayTagSortProperty.JOB_INSTANCE_ID.toString()%>">
	  	<a href="<%=WebConstants.MVC_JOB_INSTANCE_DETAILS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${vdo.jobExecution.jobInstance.id}">${vdo.jobExecution.jobInstance.id}</a>
	  </display:column>
	  <display:column title="Exec &nbsp;" sortable="true" sortProperty="<%=DisplayTagSortProperty.JOB_EXECUTION_ID.toString()%>">
		<a href="<%=WebConstants.MVC_JOB_EXECUTION_DETAILS%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${vdo.jobExecution.id}">${vdo.jobExecution.id}</a>
	  </display:column>
	  <display:column title="Job Status" property="jobExecution.status" sortable="true" sortProperty="<%=DisplayTagSortProperty.BATCH_STATUS.toString()%>"/>
	  <display:column title="Start Time" sortable="true" sortProperty="<%=DisplayTagSortProperty.START_TIME.toString()%>"><fmt:formatDate value="${vdo.jobExecution.startTime}" pattern="${DATE_FORMAT}"/></display:column>
	  <display:column title="Duration" property="executionDuration"/>
	</display:table>

	<%-- Only display row related UI controls if some rows are present. --%>	
	<c:if test="${fn:length(paginatedList.list) != 0}">
	
	<%-- STOP and RESTART buttons are only available to a user in a SUPERUSER role --%> 
	<c:set var="jobOperationsDisabled" value="disabled"/>
	<sec:authorize access="hasRole('ROLE_SUPERUSER')">
		<c:set var="isDisabled" value=""/>
	</sec:authorize>
	<input type="button" ${jobOperationsDisabled} value="Stop Job" onclick="submitJobSummaryForm('<%=JobCommand.STOP_JOB%>')"/> &nbsp;
	<input type="button" ${jobOperationsDisabled} value="Restart Job" onclick="submitJobSummaryForm('<%=JobCommand.RESTART_JOB%>')"/>
	</c:if>  <%-- if rowCount > 0 --%>
</form:form>

<br/>
<%-- Select for how may items (rows) per page to show --%>
<form:form action="<%=WebConstants.MVC_JOB_SUMMARY_CHANGE_ROW_COUNT%>"
		   commandName="<%=JobSummaryForm.FORM_NAME%>" method="post">
	Items to display: 
	<c:set var="defaultItemsPerPage" value="<%=PageAndSort.DEFAULT_ITEMS_PER_PAGE%>"/>
	<form:select path="objectsPerPage" onchange="submit()">
		<form:option label="${defaultItemsPerPage}" value="${defaultItemsPerPage}"/>
		<form:option label="50" value="50"/>
		<form:option label="100" value="100"/>
		<form:option label="250" value="250"/>
	</form:select>
</form:form>