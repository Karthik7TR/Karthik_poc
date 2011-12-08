<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="java.util.Date"%>
<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.JobExecutionVdo"%>
<%@page import="org.springframework.batch.core.StepExecution"%>
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
  	<script type="text/javascript" src="js/jquery.js"></script>
	<title>Step Execution Details</title>
	
	<script>
	$(document).ready(function() {
		
		// Toggle visibility of execution context section when clicking the image button
		$("#stepExecutionContextImage").click(
			function(event) {
				showHideDivision($("#stepExecutionContextDiv"), event.target);
			}
		);
	});
	
	/**
	 * divJqo is the jQuery object to operate on.
	 * imageElemnt is the image element (plus/minus gif image).
	 */
	function showHideDivision(divJqo, imageElement) {
		var showIt = !(divJqo.is(":visible"));
		if (showIt) {
			imageElement.src = "images/wf_minus.gif";
		} else {
			imageElement.src = "images/wf_plus.gif";
		}
		showElement(divJqo, showIt);
	}
	function showElement(element, showIt) {
		if (showIt) {
			$(element).show();
		} else {
			$(element).hide();
		}
	}
	</script>
  </head>
  

  <body>
  	<c:set var="DATE_FORMAT" value="MM-dd-yy HH:mm:ss.SSS"/>
  	<jsp:include page="stdHeader.jsp"/>
	<div class="majorDiv">
	
	<h2>Step ${stepExecution.id} Execution Details</h2>
  	
	<%--
		Ensure we are working with a valid StepExecution.  We may not be if they entered an execution ID that was not found.
	 --%>
	<c:if test="${stepExecution != null}">
	
<%-- Execution statistics --%>
	<%	// Calculate how long it took to execute the step (milliseconds)
		StepExecution stepExecutionObj = (StepExecution) request.getAttribute(WebConstants.KEY_STEP_EXECUTION);
		long executionMs = JobExecutionVdo.getExecutionDurationMs(stepExecutionObj.getStartTime(), stepExecutionObj.getEndTime());
	%>	
	<div id="statsDiv">
	<table style="background: #f0f0f0; font-size: 12; font-weight: bold; border: thin double gray; padding: 5px;">
	<tr>
		<td>Book</td>
		<td colspan="3">${jobInstance.jobParameters.parameters.bookTitle} &nbsp; (${jobInstance.jobParameters.parameters.bookCode})</td>
	</tr>
	
	<tr>
		<td>Job Name</td>
		<td colspan="3">${jobInstance.jobName}</td>
	<tr>
	<tr>
		<td width="22%">Step Name</td>
		<td width="30%">${stepExecution.stepName}</td>
		<td width="22%">Start Time</td>
		<td><fmt:formatDate value="${stepExecution.startTime}" pattern="${DATE_FORMAT}"/></td>
	</tr>
	<tr>
		<td>Instance ID</td>
		<td>
			<a href="<%=WebConstants.URL_JOB_INSTANCE_DETAILS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${jobInstance.id}">${jobInstance.id}</a>
		</td>
		<td>End Time</td>
		<td><fmt:formatDate value="${stepExecution.endTime}" pattern="${DATE_FORMAT}"/></td>
	<tr>
	<tr>
		<td>Execution ID</td>
		<td><a href="<%=WebConstants.URL_JOB_EXECUTION_DETAILS_GET%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${stepExecution.jobExecutionId}">${stepExecution.jobExecutionId}</a>
		<td>Duration</td>
		<td><%=JobExecutionVdo.getExecutionDuration(executionMs)%></td>
	<tr>
	<tr>
		<td>Step ID</td>
		<td>${stepExecution.id}</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	<tr>
	<tr>
		<td>Commits</td>
		<td>${stepExecution.commitCount}</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>Rollbacks</td>
		<td>${stepExecution.rollbackCount}</td>
		<td>Filter Count</td>
		<td>${stepExecution.filterCount}</td>
	</tr>
	<tr>
		<td>Reads</td>
		<td>${stepExecution.readCount}</td>
		<td>Read Skips</td>
		<td>${stepExecution.readSkipCount}</td>
	</tr>
	<tr>
		<td>Writes</td>
		<td>${stepExecution.writeCount}</td>
		<td>Write Skips</td>
		<td>${stepExecution.writeSkipCount}</td>
	</tr>	
	<tr>
		<td>Status</td>
		<td>${stepExecution.status}</td>
		<td>Process Skips</td>
		<td>${stepExecution.processSkipCount}</td>

	<tr>
	<tr>
		<td>Exit Code</td>
		<td>${stepExecution.exitStatus.exitCode}</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>Exit Message </td>
		<td colspan="3">${stepExecution.exitStatus.exitDescription}</td>
	</tr>
	</table>
	</div>
	<br/>
	
<%-- Step Execution Context map entries (key/value pairs) --%>
	<div class="details-table-label">
		<input id="stepExecutionContextImage" type="image" src="images/wf_minus.gif"/>Step Execution Context
	</div>
	
	<div id="stepExecutionContextDiv" class="details-expand-div">
		<display:table id="stepExecutionContextMapEntry" name="<%=WebConstants.KEY_STEP_EXECUTION_CONTEXT_MAP_ENTRIES %>" class="displayTagTable" cellpadding="3" style="text-align: left;">
	  		<display:setProperty name="basic.msg.empty_list">No step execution context entries were found.</display:setProperty>
	  		<display:column title="Name" property="key" style="width: 20%"/>
	  		<display:column title="Value" property="value"/>
		</display:table>
	</div>
	
	</c:if> <%-- if there is a jobExecution defined --%>
	</div>  <%-- majorDiv --%>
  	
  </body>
</html>

