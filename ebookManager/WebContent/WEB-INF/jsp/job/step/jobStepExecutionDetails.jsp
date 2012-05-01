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


 <c:set var="DATE_FORMAT" value="<%=WebConstants.DATE_TIME_MS_FORMAT_PATTERN %>"/>

<%--
	Ensure we are working with a valid StepExecution.  We may not be if they entered an execution ID that was not found.
 --%>
<c:if test="${jobStepExecution != null}">

<%-- Execution statistics --%>
<%	// Calculate how long it took to execute the step (milliseconds)
	StepExecution stepExecutionObj = (StepExecution) request.getAttribute(WebConstants.KEY_JOB_STEP_EXECUTION);
	long executionMs = JobSummary.getExecutionDuration(stepExecutionObj.getStartTime(), stepExecutionObj.getEndTime());
%>	
<div id="statsDiv">
<table style="background: #f0f0f0; font-size: 12; font-weight: bold; border: thin double gray; padding: 5px;">
<tr>
	<td style="padding-right:80px;">Step Name</td>
	<td>${jobStepExecution.stepName}</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td>Book Name</td>
	<td colspan="3">${bookInfo.proviewDisplayName}</td>
</tr>
<tr>
	<td>Title ID</td>
	<td colspan="3">${bookInfo.titleId}</td>

</tr>
<tr>
	<td>Job Instance</td>
	<td>
		<a href="<%=WebConstants.MVC_JOB_INSTANCE_DETAILS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${jobInstance.id}">${jobInstance.id}</a>
	</td>
	<td style="padding-right:80px;">Start Time</td>
	<td><fmt:formatDate value="${jobStepExecution.startTime}" pattern="${DATE_FORMAT}"/></td>
<tr>
<tr>
	<td>Job Execution</td>
	<td><a href="<%=WebConstants.MVC_JOB_EXECUTION_DETAILS%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${jobStepExecution.jobExecutionId}">${jobStepExecution.jobExecutionId}</a>
	<td>End Time</td>
	<td><fmt:formatDate value="${jobStepExecution.endTime}" pattern="${DATE_FORMAT}"/></td>
<tr>
<tr>
	<td>Step ID</td>
	<td>${jobStepExecution.id}</td>
	<td>Duration</td>
	<td><%=JobSummary.getExecutionDuration(executionMs)%></td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
<tr>
<tr>
	<td>Commits</td>
	<td>${jobStepExecution.commitCount}</td>
	<td>Filter Count</td>
	<td>${jobStepExecution.filterCount}</td>
</tr>
<tr>
	<td>Rollbacks</td>
	<td>${jobStepExecution.rollbackCount}</td>
	<td>Read Skips</td>
	<td>${jobStepExecution.readSkipCount}</td>
</tr>
<tr>
	<td>Reads</td>
	<td>${jobStepExecution.readCount}</td>
	<td>Write Skips</td>
	<td>${jobStepExecution.writeSkipCount}</td>
</tr>
<tr>
	<td>Writes</td>
	<td>${jobStepExecution.writeCount}</td>
	<td>Process Skips</td>
	<td>${jobStepExecution.processSkipCount}</td>
</tr>	
<tr>
	<td>Job Status</td>
	<td>${jobStepExecution.status}</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
<tr>
<tr>
	<td>Exit Code</td>
	<td>${jobStepExecution.exitStatus.exitCode}</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
</tr>

<%-- Only display the exit message if there was indeed some error --%>
<c:if test="${not empty jobStepExecution.exitStatus.exitDescription}">
<tr>
	<td>Exit Message </td>
	<td colspan="3"  style="border-width:2;border-style:solid;border-color:red;">${jobStepExecution.exitStatus.exitDescription}</td>
</tr>
</c:if>
</table>
</div>
<br/>

<%-- Step Execution Context map entries (key/value pairs) --%>
<div id="stepExecutionContextDiv" class="job-details-expand-div">
	<display:table id="stepExecutionContextMapEntry" name="<%=WebConstants.KEY_JOB_STEP_EXECUTION_CONTEXT_MAP_ENTRIES %>" class="displayTagTable" cellpadding="3" style="text-align: left;">
  		<display:setProperty name="basic.msg.empty_list">No step execution context entries were found.</display:setProperty>
  		<display:setProperty name="paging.banner.onepage" value=" " />
  		<display:column title="Name" property="key" style="width: 20%"/>
  		<display:column title="Value" property="value"/>
	</display:table>
</div>

</c:if> <%-- if there is a step execution defined --%>


