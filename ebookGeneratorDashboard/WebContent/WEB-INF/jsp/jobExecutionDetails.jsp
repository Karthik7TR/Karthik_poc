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
<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobexecution.JobExecutionForm"%>
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
	<title>Job Execution Details</title>
	
	<script>
	$(document).ready(function() {
		
		$("#jobExecutionContextDiv").hide();  // Hide the Job Execution Context section
		
		// Toggle visibility of steps when clicking the image button
		$("#stepsImage").click(
			function(event) {
				showHideDivision($("#jobStepsDiv"), event.target);
			}
		);
		
		// Toggle visibility of parameters when clicking the image button
		$("#parametersImage").click(
			function(event) {
				showHideDivision($("#jobParametersDiv"), event.target);
			}
		);
		
		// Toggle visibility of parameters when clicking the image button
		$("#jobExecutionContextImage").click(
			function(event) {
				showHideDivision($("#jobExecutionContextDiv"), event.target);
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
	<c:set var="DATE_FORMAT" value="MM/dd/yy HH:mm:ss.SSS"/>
  	<jsp:include page="stdHeader.jsp"/>
	<div class="majorDiv">
	
	<h2>Job Execution ${jobExecution.id} Details</h2>
	

	<form:form action="<%=WebConstants.URL_JOB_EXECUTION_DETAILS_POST%>"
			   	   commandName="<%=JobExecutionForm.FORM_NAME%>" name="theForm" method="post">
			   	   
<%-- Error Message Presentation --%>
		<spring:hasBindErrors name="<%=JobExecutionForm.FORM_NAME%>">
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
	    
  		Job Execution ID &nbsp; <form:input path="jobExecutionId"/> &nbsp;
  		<input type="submit" value="Find"/> &nbsp;
  		<input type="button" value="Refresh"
  			onclick="location.href='<%=WebConstants.URL_JOB_EXECUTION_DETAILS_GET%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${jobExecution.id}'"/>
	</form:form>

	<%--
		Ensure we are working with a valid JobExecution.  We may not be if they entered an execution ID that was not found.
	 --%>
	<c:if test="${jobExecution != null}">
	
<%-- Execution statistics --%>	
	<div id="statsDiv">
	<table style="background: #f0f0f0; font-size: 12; font-weight: bold; border: thin double gray; padding: 5px;">
	<tr>
		<td>Book Name</td>
		<td colspan="3">${vdo.bookName}</td>
	</tr>
	<tr>
		<td>Title ID</td>
		<td colspan="3">${vdo.fullyQualifiedTitleId}</td>
	</tr>
	<tr>
		<td width="25%">Job Name</td>
		<td width="35%">${jobExecution.jobInstance.jobName}</td>
		<td>Create Time</td>
		<td><fmt:formatDate value="${jobExecution.createTime}" pattern="${DATE_FORMAT}"/></td>
	</tr>
	<tr>
		<td>Job Instance</td>
		<td>
			<a href="<%=WebConstants.URL_JOB_INSTANCE_DETAILS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${jobExecution.jobInstance.id}">${jobExecution.jobInstance.id}</a>
		</td>
		<td>Start Time</td>
		<td><fmt:formatDate value="${jobExecution.startTime}" pattern="${DATE_FORMAT}"/></td>
	</tr>
	<tr>
		<td>Job Execution</td>
		<td>${jobExecution.id}</td>
		<td>End Time</td>
		<td><fmt:formatDate value="${jobExecution.endTime}" pattern="${DATE_FORMAT}"/></td>
	<tr>
	<tr>
		<td>Running</td>
		<td>${jobExecution.running}</td>
		<td>Duration</td>
		<td>${vdo.executionDuration}</td>
	</tr>
	<tr>
		<td>Job Status</td>
		<td>${jobExecution.status}</td>
		<c:choose>
		<c:when test="${vdo.restartable}">
		<td><input type="button" value="Restart"
  				   onclick="location.href='<%=WebConstants.URL_JOB_RESTART%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${jobExecution.id}'"/> &nbsp;
  		</td>
  		<td>&nbsp;</td>
  		</c:when>
  		<c:when test="${vdo.stoppable}">
		<td><input type="button" value="Stop"
  				   onclick="location.href='<%=WebConstants.URL_JOB_STOP%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${jobExecution.id}'"/> &nbsp;
  		</td>
  		<td>&nbsp;</td>
  		</c:when>
  		<c:otherwise>
  		<td>&nbsp;</td>
  		<td>&nbsp;</td>
  		</c:otherwise>
  		</c:choose>
	</tr>
	<tr>
		<td>Last Step Status</td>
		<td>${vdo.lastStepBatchAndExitStatus.batchStatus}</td>
		<td>&nbsp;</td>
  		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>Last Step Exit Code</td>
		<td>${vdo.lastStepBatchAndExitStatus.exitStatus.exitCode}</td>
		<td>&nbsp;</td>
  		<td>&nbsp;</td>
	</tr>
	</table>
	</div>
	<br/>
	
<%-- JOB STEPS (in descending order) --%>
	<div class="details-table-label">
		<input id="stepsImage" type="image" src="images/wf_minus.gif"/>Job Steps
	</div>
	<div id="jobStepsDiv" class="details-expand-div">
		<display:table id="step" name="vdo.steps" class="displayTagTable" cellpadding="3">
			<%	// Calculate how long it took to execute the step (milliseconds)
				StepExecution stepExecutionObj = (StepExecution)pageContext.getAttribute("step");
				long executionDurationMs = -1; 
				if (stepExecutionObj != null) {
					executionDurationMs = JobExecutionVdo.getExecutionDurationMs(
						stepExecutionObj.getStartTime(), stepExecutionObj.getEndTime());
				}
			%>
	  		<display:setProperty name="basic.msg.empty_list">No job steps were found.</display:setProperty>
	  		<display:column title="Step Name" property="stepName" style="text-align:left"/>
	  		<display:column title="Step ID">
	  			<a href="<%=WebConstants.URL_STEP_EXECUTION_DETAILS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${jobExecution.jobInstance.id}&<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${jobExecution.id}&<%=WebConstants.KEY_STEP_EXECUTION_ID%>=${step.id}">${step.id}</a>
	  		</display:column>
	  		<display:column title="Status" property="status"/>
	  		<display:column title="Exit Code" property="exitStatus.exitCode"/>
	  		<display:column title="Start Time"><fmt:formatDate value="${step.startTime}" pattern="${DATE_FORMAT}"/></display:column>
	  		<display:column title="Duration"><%= JobExecutionVdo.getExecutionDuration(executionDurationMs)%></display:column>
	  		<display:column title="Exit Message" property="exitStatus.exitDescription" style="text-align:left"/>
		</display:table>
	</div>	
	<br/>
	
<%-- JOB PARAMETERS --%>
	<div class="details-table-label">
		<input id="parametersImage" type="image" src="images/wf_minus.gif"/>Job Parameters
	</div>
	
	<div id="jobParametersDiv" class="details-expand-div">
		<display:table id="jobParameterMapEntry" name="vdo.jobParameterMapEntryList" class="displayTagTable" cellpadding="3" style="text-align: left;">
	  		<display:setProperty name="basic.msg.empty_list">No job parameters were found.</display:setProperty>
	  		<display:column title="Name" property="key" style="width: 20%"/>
	  		<display:column title="Value" property="value.value"/>
		</display:table>
	</div>
	<br/>
	
<%-- JOB EXECUTION CONTEXT --%>
	<div class="details-table-label">
		<input id="jobExecutionContextImage" type="image" src="images/wf_plus.gif"/>Job Execution Context
	</div>
	
	<div id="jobExecutionContextDiv" class="details-expand-div">
		<display:table id="jobExecutionContextMapEntry" name="vdo.jobExecutionContextMapEntryList" class="displayTagTable" cellpadding="3" style="text-align: left;">
	  		<display:setProperty name="basic.msg.empty_list">No job execution context entries were found.</display:setProperty>
	  		<display:column title="Name" property="key" style="width: 20%"/>
	  		<display:column title="Value" property="value"/>
		</display:table>
	</div>
	
	</c:if> <%-- if there is a jobExecution defined --%>
	</div>  <%-- majorDiv --%>
  	
  </body>
</html>

