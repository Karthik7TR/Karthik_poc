<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.springframework.batch.core.BatchStatus"%>
<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants.SortProperty"%>
<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobsummary.JobSummaryForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

<html>
  <head>
  	<link rel="stylesheet" href="theme/dashboard.css"/>
  	<link rel="stylesheet" href="theme/jquery.ui.all.css">
  	<link rel="stylesheet" href="theme/jquery.ui.datepicker.css">
  	<link rel="stylesheet" href="theme/jquery.ui.theme.css">
  	<link rel="stylesheet" href="theme/jquery.ui.core.css">
	<link rel="stylesheet" href="theme/jquery.ui.base.css">  	
  	
  	<script type="text/javascript" src="js/jquery.js"></script>
  	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
  	<script>
		$(function() {
			$( "#datepicker" ).datepicker();
		});
	</script>
	<title>Job Executions</title>
  </head>

  <body>
  	<c:set var="DATE_FORMAT" value="MM-dd-yy HH:mm:ss"/>
	<jsp:include page="stdHeader.jsp"/>
	<div class="majorDiv">

	<h2>Job Execution Summary</h2>

	<form:form action="<%=WebConstants.URL_JOB_SUMMARY%>"
			   commandName="<%=JobSummaryForm.FORM_NAME%>" name="theForm" method="post">
			   
		<%-- Error Message Presentation --%>
		<spring:hasBindErrors name="<%=JobSummaryForm.FORM_NAME%>">
			<div class="errorBox">
		      <b><spring:message code="please.fix.errors"/></b><br/>
		      <form:errors path="*">
		      	<ul>
				<c:forEach items="${messages}" var="message">
					<li style="color: black">${message}</li>
				</c:forEach>
		      	</ul>
			  </form:errors>
			  <br/>
		    </div>
		    <br/>
	    </spring:hasBindErrors>

<%-- Search Filter --%>	    
		<div>
		<!-- Pick the job name for which you want a summary -->
		<input type="submit" value="Search"/>
		&nbsp; Job Name
		<form:select path="jobName">
			<form:option label="ALL" value=""/>
			<form:options items="${jobNames}" itemLabel="label" itemValue="value"/>
		</form:select>

		&nbsp; Job Status
		<form:select path="status">
			<form:option label="ALL" value=""/>
			<form:option label="<%=BatchStatus.COMPLETED.toString()%>" value="<%=BatchStatus.COMPLETED.toString() %>"/>
			<form:option label="<%=BatchStatus.FAILED.toString() %>" value="<%=BatchStatus.FAILED.toString() %>"/>
			<form:option label="<%=BatchStatus.STARTED.toString() %>" value="<%=BatchStatus.STARTED.toString() %>"/>
			<form:option label="<%=BatchStatus.STOPPED.toString() %>" value="<%=BatchStatus.STOPPED.toString() %>"/>
			<form:option label="<%=BatchStatus.ABANDONED.toString() %>" value="<%=BatchStatus.ABANDONED.toString() %>"/>
			<form:option label="<%=BatchStatus.STARTING.toString() %>" value="<%=BatchStatus.STARTING.toString() %>"/>
			<form:option label="<%=BatchStatus.STOPPING.toString() %>" value="<%=BatchStatus.STOPPING.toString() %>"/>
			<form:option label="<%=BatchStatus.UNKNOWN.toString() %>" value="<%=BatchStatus.UNKNOWN.toString() %>"/>
		</form:select>
		
		&nbsp; Start Date <form:input id="datepicker" path="startDate"/>
		</div>
		<br/>
		
<%-- Determine the up or down arrow image to be added to currently sorted column header title --%>
		<c:choose>
			<c:when test="${paginatedList.ascendingSort}"><c:set var="sortImage" value="<img src='images/arrowup.gif' alt='(asc)'/>"/> </c:when>
			<c:otherwise><c:set var="sortImage" value="<img src='images/arrowdown.gif' alt='(desc)'/>"/></c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${paginatedList.sortCriterion == 'JOB_NAME'}"><c:set var="jobNameImageTag" value="${sortImage}"/></c:when>
			<c:when test="${paginatedList.sortCriterion == 'INSTANCE_ID'}"><c:set var="instanceIdImageTag" value="${sortImage}"/></c:when>
			<c:when test="${paginatedList.sortCriterion == 'BATCH_STATUS'}"><c:set var="batchStatusImageTag" value="${sortImage}"/></c:when>
			<c:when test="${paginatedList.sortCriterion == 'START_TIME'}"><c:set var="startTimeImageTag" value="${sortImage}"/></c:when>
			<c:when test="${paginatedList.sortCriterion == 'EXECUTION_TIME'}"><c:set var="durationImageTag" value="${sortImage}"/></c:when>
		</c:choose>

<%-- Table of job executions for a specific Job name --%>
		<display:table id="vdo" name="paginatedList" class="displayTagTable" cellpadding="2" 
					   requestURI="<%=WebConstants.URL_JOB_SUMMARY_PAGING%>"
					   sort="external">
		  <display:setProperty name="basic.msg.empty_list">No job executions were found.</display:setProperty>
	
		  <!-- display:column title="Job Name" property="jobInstance.jobName"/ -->
		  <display:column title="Job Name ${jobNameImageTag}" property="jobExecution.jobInstance.jobName" sortable="true" sortProperty="<%=SortProperty.JOB_NAME.toString()%>" style="text-align: left"/>
		  <display:column title="Instance ${instanceIdImageTag}" sortable="true" sortProperty="<%=SortProperty.INSTANCE_ID.toString()%>">
		  		<a href="<%=WebConstants.URL_JOB_INSTANCE_DETAILS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${vdo.jobExecution.jobInstance.id}">${vdo.jobExecution.jobInstance.id}</a>
		  </display:column>
		  <display:column title="Execution">
				<a href="<%=WebConstants.URL_JOB_EXECUTION_DETAILS_GET%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${vdo.jobExecution.id}">${vdo.jobExecution.id}</a>
		  </display:column>
		  <display:column title="Job Status ${batchStatusImageTag}" property="jobExecution.status" sortable="true" sortProperty="<%=SortProperty.BATCH_STATUS.toString()%>"/>
		  <display:column title="Step Status" property="lastStepBatchAndExitStatus.batchStatus"/>
		   <display:column title="Step Exit Code" property="lastStepBatchAndExitStatus.exitStatus.exitCode"/>
		  <display:column title="Start Time ${startTimeImageTag}" sortable="true" sortProperty="<%=SortProperty.START_TIME.toString()%>"><fmt:formatDate value="${vdo.jobExecution.startTime}" pattern="${DATE_FORMAT}"/></display:column>
		  <display:column title="Duration ${durationImageTag}" property="executionDuration" sortable="true" sortProperty="<%=SortProperty.EXECUTION_TIME.toString()%>"/>
		  
		</display:table>
	
<%-- Select for how may items (rows) per page to show --%>
		<br/>
		Items per page: 
		<c:set var="defaultItemsPerPage" value="<%=JobSummaryForm.DEFAULT_ITEMS_PER_PAGE%>"/>
		<form:select path="itemsPerPage" onchange="theForm.submit()">
			<form:option label="${defaultItemsPerPage}" value="${defaultItemsPerPage}"/>
			<form:option label="50" value="50"/>
			<form:option label="100" value="100"/>
			<form:option label="500" value="500"/>
		</form:select>
	</form:form>
	
	<%-- Hudson build information --%>
  	<div style="vertical-align: middle; width:98%" align="right">
		${environment} Build # @buildTag@ (@buildTime@)
  	</div>
	</div>
  </body>
</html>

