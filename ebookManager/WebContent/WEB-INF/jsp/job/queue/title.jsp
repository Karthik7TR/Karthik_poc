<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<div class="pageTitle">Queued Jobs</div>
<span class="title-description">Jobs in line to run as soon as possible.</span><br/>
<br/>
<input class="job-details-refresh-button" type="button" value="Refresh" onclick="location.href='<%=WebConstants.MVC_JOB_QUEUE%>'">
