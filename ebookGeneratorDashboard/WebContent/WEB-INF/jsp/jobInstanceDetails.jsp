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

<%--
	Displays the aggregated set of steps for all the executions of a specific job instance in descending start time order. 
--%>
<html>
  <head>
  	<link rel="stylesheet" href="theme/dashboard.css"/>
	<title>Job Instance Details</title>
  </head>
  
  <body>
	<c:set var="DATE_FORMAT" value="MM-dd-yy HH:mm:ss.SSS"/>
  	<jsp:include page="stdHeader.jsp"/>
	<div class="majorDiv">
	
	<h2>Job Instance ${jobInstance.id} Details</h2>

	<div id="statsDiv">
	<table style="background: #f0f0f0; font-size: 12; font-weight: bold; border: thin double gray; padding: 5px;">
	<tr>
		<td width="20%">Job Name</td>
		<td width="30%">${jobInstance.jobName}</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td width="20%">Job Instance</td>
		<td width="30%">${jobInstance.id}</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	</table>
	</div>
	<br/>
	
<%-- Job Steps table, aggregated from the same job instance (in descending start time order) --%>
	<div class="details-table-label">
		Aggregated Steps from all executions
	</div>
	<div id="jobStepsDiv" class="details-expand-div">
		<display:table id="step" name="<%=WebConstants.KEY_STEP_EXECUTIONS%>" class="displayTagTable" cellpadding="3">
			<%	// Calculate how long (duration) it took to execute the step (milliseconds)
				StepExecution stepExecution = (StepExecution)pageContext.getAttribute("step");
				long executionDurationMs = -1; 
				if (stepExecution != null) {
					executionDurationMs = JobExecutionVdo.getExecutionDurationMs(
						stepExecution.getStartTime(), stepExecution.getEndTime());
				}
			%>
	  		<display:setProperty name="basic.msg.empty_list">No job steps were found.</display:setProperty>
	  		<display:column title="Step Name" property="stepName" style="text-align:left"/>
	  		<display:column title="Exec ID">
	  			<a href="<%=WebConstants.URL_JOB_EXECUTION_DETAILS_GET%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${step.jobExecutionId}">${step.jobExecutionId}</a>
	  		</display:column>
	  		<display:column title="Step ID">
	  			<a href="<%=WebConstants.URL_STEP_EXECUTION_DETAILS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${jobInstance.id}&<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${step.jobExecutionId}&<%=WebConstants.KEY_STEP_EXECUTION_ID%>=${step.id}">${step.id}</a>
	  		</display:column>
	  		<display:column title="Status" property="status"/>
	  		<display:column title="Exit Code" property="exitStatus.exitCode"/>
	  		<display:column title="Start Time"><fmt:formatDate value="${step.startTime}" pattern="${DATE_FORMAT}"/></display:column>
	  		<display:column title="Duration"><%= JobExecutionVdo.getExecutionDuration(executionDurationMs)%></display:column>
	  		<display:column title="Exit Message" property="exitStatus.exitDescription" style="text-align:left"/>
		</display:table>
	</div>	
	<br/>
	</div>  <!-- majorDiv -->
  	
  </body>
</html>

