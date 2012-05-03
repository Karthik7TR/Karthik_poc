<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary"%>
<%@page import="java.util.Date"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobExecutionVdo"%>
<%@page import="org.springframework.batch.core.StepExecution"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

<%--
	Displays the aggregated set of steps for all the executions of a specific job instance in descending start time order. 
--%>

<c:set var="DATE_FORMAT" value="<%=WebConstants.DATE_TIME_MS_FORMAT_PATTERN %>"/>

<div id="statsDiv">
<table style="background: #f0f0f0; font-size: 12; font-weight: bold; border: thin double gray; padding: 5px;">
<tr>
	<td style="padding-right:20px;">ProView Display Name</td>
	<td>${bookInfo.proviewDisplayName}</td>
</tr>
<tr>
	<td>Title ID</td>
	<td>${bookInfo.titleId}</td>
</tr>
</table>
</div>
<br/>

<%-- Job Steps table, aggregated from the same job instance (in descending start time order) --%>
<div class="job-details-table-label">
	Aggregated Steps from all executions
</div>
<div id="jobStepsDiv" class="job-details-expand-div">
	<display:table id="step" name="<%=WebConstants.KEY_JOB_STEP_EXECUTIONS%>" class="displayTagTable" cellpadding="3">
		<%	// Calculate how long (duration) it took to execute the step (milliseconds)
			StepExecution stepExecution = (StepExecution)pageContext.getAttribute("step");
			long executionDurationMs = -1; 
			if (stepExecution != null) {
				Date endTime = (stepExecution.getEndTime() != null) ? stepExecution.getEndTime() : new Date();
				executionDurationMs = JobSummary.getExecutionDuration(
													stepExecution.getStartTime(), endTime);
			}
		%>
  		<display:setProperty name="basic.msg.empty_list">No job steps were found.</display:setProperty>
  		<display:setProperty name="paging.banner.onepage" value=" " />
  		<display:column title="Step Name" property="stepName" style="text-align:left"/>
  		<display:column title="Exec ID">
  			<a href="<%=WebConstants.MVC_JOB_EXECUTION_DETAILS%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${step.jobExecutionId}">${step.jobExecutionId}</a>
  		</display:column>
  		<display:column title="Step ID">
  			<a href="<%=WebConstants.MVC_JOB_STEP_EXECUTION_DETAILS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${jobInstance.id}&<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${step.jobExecutionId}&<%=WebConstants.KEY_JOB_STEP_EXECUTION_ID%>=${step.id}">${step.id}</a>
  		</display:column>
  		<display:column title="Exit Code" property="exitStatus.exitCode"/>
  		<display:column title="Start Time"><fmt:formatDate value="${step.startTime}" pattern="${DATE_FORMAT}"/></display:column>
  		<display:column title="Duration"><%= JobSummary.getExecutionDuration(executionDurationMs)%></display:column>
  		<display:column title="Exit Message" property="exitStatus.exitDescription" style="text-align:left"/>
	</display:table>
</div>	


