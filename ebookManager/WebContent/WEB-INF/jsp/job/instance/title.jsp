<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<div class="pageTitle">Job Instance Details (${jobInstance.id})</div>
<span class="title-description">Steps from all attempted job executions.</span><br/>
<br/>
<input class="job-details-refresh-button" type="button" value="Refresh" onclick="location.href='<%=WebConstants.MVC_JOB_INSTANCE_DETAILS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${jobInstance.id}'">

