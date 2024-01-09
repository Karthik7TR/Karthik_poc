<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details.JobExecutionForm"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="pageTitle">Job Execution Details (${job.jobExecution.id})</div>
<span class="title-description">Steps and details for a single job run attempt (execution).</span><br/>
<br/>
<input class="job-details-refresh-button" type="button" value="Refresh" onclick="location.href='<%=WebConstants.MVC_JOB_EXECUTION_DETAILS%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${job.jobExecution.id}'">

<div id="searchJobExecution">
	<form:form action="<%=WebConstants.MVC_JOB_EXECUTION_DETAILS_POST%>"
				   	   commandName="<%=JobExecutionForm.FORM_NAME%>" name="theForm" method="post">
	  	Job Execution ID
	  	<form:input path="jobExecutionId"/>
	  	<input type="submit" value="Find"/> &nbsp;
	  	<%-- Error Message Presentation --%>
		<spring:hasBindErrors name="<%=JobExecutionForm.FORM_NAME%>">
			<div class="errorBox">
		      <form:errors path="*">
				<c:forEach items="${messages}" var="message">
					<div>${message}</div>
				</c:forEach>
			  </form:errors>
		    </div>
		</spring:hasBindErrors>
	</form:form>
</div>