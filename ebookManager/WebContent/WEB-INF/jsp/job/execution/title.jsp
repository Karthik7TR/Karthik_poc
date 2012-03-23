<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<div class="pageTitle">Job Execution Details (${job.jobExecution.id})</div>
<span class="title-description">Steps and details for a single job run attempt (execution).</span><br/>
<br/>
<input class="job-details-refresh-button" type="button" value="Refresh" onclick="location.href='<%=WebConstants.MVC_JOB_EXECUTION_DETAILS%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${jobExecution.id}'">
