<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobExecutionVdo"%>
<%@page import="java.util.Date"%>
<%@page import="org.springframework.batch.core.StepExecution"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details.JobExecutionForm"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
	
<script>
	$(document).ready(function() {
		
		$("#jobExecutionContextDiv").hide();  // Hide the Job Execution Context box
		$("#jobParametersDiv").hide();  // Hide the Job Parameters box
		
		// Toggle visibility of steps when clicking the image button
		$("#stepsImage").click(
			function(event) {
				toggleDivisionVisibility($("#jobStepsDiv"), event.target);
			}
		);
		
		// Toggle visibility of parameters when clicking the image button
		$("#parametersImage").click(
			function(event) {
				toggleDivisionVisibility($("#jobParametersDiv"), event.target);
			}
		);
		
		// Toggle visibility of parameters when clicking the image button
		$("#jobExecutionContextImage").click(
			function(event) {
				toggleDivisionVisibility($("#jobExecutionContextDiv"), event.target);
			}
		);
	});
	
	
	/**
	 * If the division is currently visible, hide it, and vice-versa.
	 * divElement is the jQuery object to operate on.
	 * imageElemnt is the image element (plus/minus gif image).
	 */
	function toggleDivisionVisibility(divElement, imageElement) {
		var visible = !(divElement.is(":visible"));
		imageElement.src = (visible) ? "theme/images/wf_minus.gif" : "theme/images/wf_plus.gif";
		setElementVisibility(divElement, visible);
	}
	function setElementVisibility(element, visible) {
		if (visible) {
			$(element).show();
		} else {
 			$(element).hide();
		}
	}
</script>

	<c:set var="DATE_FORMAT" value="<%=WebConstants.DATE_TIME_MS_FORMAT_PATTERN %>"/>
	
	<%-- Only a user in the starting user or a superuser role can stop or restart a job --%>
	<c:set var="operationsDisabled" value="disabled"/>
	<c:if test="${job.userAllowedToStopAndRestartJob}">
		<c:set var="operationsDisabled" value=""/>
	</c:if>
	
	<%--
		Ensure we are working with a valid JobExecution.  We may not be if they entered an execution ID that was not found.
	 --%>
	<c:if test="${job.jobExecution != null}">
	
<%-- Execution statistics --%>	
	<div id="statsDiv">
	<table style="background: #f0f0f0; font-size: 12; font-weight: bold; border: thin double gray; padding: 5px;">
	<tr>
		<td style="padding-right:40px;">Book Name</td>
		<td colspan="3">${job.bookInfo.proviewDisplayName}</td>
	</tr>
	<tr>
		<td>Title ID</td>
		<td colspan="3">${job.bookInfo.titleId}</td>
	</tr>
		<tr>
		<td>Submitted By</td>
		<td colspan="3">${job.publishingStats.jobSubmitterName}</td>
	</tr>
	<tr>
		<td>Job Instance</td>
		<td style="padding-right:120px">
			<a href="<%=WebConstants.MVC_JOB_INSTANCE_DETAILS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${job.jobExecution.jobInstance.id}">${job.jobExecution.jobInstance.id}</a>
		</td>
		<td style="padding-right:30px;">Create Time</td>
		<td><fmt:formatDate value="${job.jobExecution.createTime}" pattern="${DATE_FORMAT}"/></td>
	</tr>
	<tr>
		<td>Job Execution</td>
		<td>${job.jobExecution.id}</td>
		<td>Start Time</td>
		<td><fmt:formatDate value="${job.jobExecution.startTime}" pattern="${DATE_FORMAT}"/></td>
	<tr>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>End Time</td>
		<td><fmt:formatDate value="${job.jobExecution.endTime}" pattern="${DATE_FORMAT}"/></td>
	</tr>
	<tr>
		<td>Running</td>
		<td>${job.jobExecution.running}</td>
		<td>Duration</td>
		<td>${job.duration}</td>
	</tr>
	<tr>
		<td>Job Status</td>
		<td>${job.jobExecution.status}</td>
		<c:choose>
		<c:when test="${job.jobRestartable}">
		<td><input type="button" value="Restart" ${operationsDisabled}
  				   onclick="location.href='<%=WebConstants.MVC_JOB_EXECUTION_JOB_RESTART%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${job.jobExecution.id}'"/> &nbsp;
  		</td>
  		<td>&nbsp;</td>
  		</c:when>
  		<c:when test="${job.jobStoppable}">
		<td><input type="button" value="Stop" ${operationsDisabled}
  				   onclick="location.href='<%=WebConstants.MVC_JOB_EXECUTION_JOB_STOP%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${job.jobExecution.id}'"/> &nbsp;
  		</td>
  		<td>&nbsp;</td>
  		</c:when>
  		<c:otherwise>
  		<td>&nbsp;</td>
  		<td>&nbsp;</td>
  		</c:otherwise>
  		</c:choose>
	</tr>
	</table>
	</div>
	<br/>
	
	<%-- JOB STEPS (in descending order) --%>
	<div class="job-details-table-label">
		<input id="stepsImage" type="image" src="theme/images/wf_minus.gif"/> Job Steps
	</div>
	<div id="jobStepsDiv" class="job-details-expand-div">
		<display:table id="step" name="job.steps" class="displayTagTable" cellpadding="3">
			<%	// Calculate how long (duration) it took to execute the step (milliseconds)
				StepExecution stepExecution = (StepExecution)pageContext.getAttribute("step");
				long executionDurationMs = -1; 
				if (stepExecution != null) {
					Date endTime = (stepExecution.getEndTime() != null) ? stepExecution.getEndTime() : new Date();
					executionDurationMs = JobSummary.getExecutionDuration(
														stepExecution.getStartTime(), endTime);
				}
			%>
			<display:setProperty name="paging.banner.onepage" value=" " />
	  		<display:setProperty name="basic.msg.empty_list">No job steps were found.</display:setProperty>
	  		<display:column title="Step Name" property="stepName" style="text-align:left"/>
	  		<display:column title="Step ID">
	  			<a href="<%=WebConstants.MVC_JOB_STEP_EXECUTION_DETAILS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${job.jobExecution.jobInstance.id}&<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${job.jobExecution.id}&<%=WebConstants.KEY_JOB_STEP_EXECUTION_ID%>=${step.id}">${step.id}</a>
	  		</display:column>
	  		<display:column title="Exit Code" property="exitStatus.exitCode"/>
	  		<display:column title="Start Time"><fmt:formatDate value="${step.startTime}" pattern="${DATE_FORMAT}"/></display:column>
	  		<display:column title="Duration"><%= JobSummary.getExecutionDuration(executionDurationMs)%></display:column>
	  		<display:column title="Exit Message" property="exitStatus.exitDescription" style="text-align:left"/>
		</display:table>
	</div>	
	<br/>
	
	<%-- JOB EXECUTION CONTEXT --%>
	<div class="job-details-table-label">
		<input id="jobExecutionContextImage" type="image" src="theme/images/wf_plus.gif"/> Job Execution Context
	</div>
	<br/>
	<div id="jobExecutionContextDiv" class="job-details-expand-div">
		<display:table id="jobExecutionContextMapEntry" name="job.jobExecutionContextMapEntryList" class="displayTagTable" cellpadding="3" style="text-align: left;">
	  		<display:setProperty name="basic.msg.empty_list">No job execution context entries were found.</display:setProperty>
	  		<display:setProperty name="paging.banner.onepage" value=" " />
	  		<display:column title="Name" property="key" style="width: 20%"/>
	  		<display:column title="Value" property="value"/>
		</display:table>
	</div>
	
	<%-- JOB PARAMETERS --%>
	<div class="job-details-table-label">
		<input id="parametersImage" type="image" src="theme/images/wf_plus.gif"/> Job Parameters
	</div>
	<div id="jobParametersDiv" class="job-details-expand-div">
		<display:table id="jobParameterMapEntry" name="job.jobParameterMapEntryList" class="displayTagTable" cellpadding="3" style="text-align: left;">
	  		<display:setProperty name="basic.msg.empty_list">No job parameters were found.</display:setProperty>
	  		<display:setProperty name="paging.banner.onepage" value=" " />
	  		<display:column title="Name" property="key" style="width: 20%"/>
	  		<display:column title="Value" property="value.value"/>
		</display:table>
	</div>

	</c:if> <%-- if there is a jobExecution defined --%>


