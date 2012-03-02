<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<div class="pageTitle">Job Execution Details (${jobExecution.id})</div>
<br/>
<input class="job-details-refresh-button" type="button" value="Refresh" onclick="location.href='<%=WebConstants.MVC_JOB_EXECUTION_DETAILS%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${jobExecution.id}'">
