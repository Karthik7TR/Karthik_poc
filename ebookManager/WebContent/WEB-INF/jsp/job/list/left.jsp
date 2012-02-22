<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.FilterForm"%>
<%@page import="org.springframework.batch.core.BatchStatus"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<b>Job Filters</b><br/>
<br/>

<form:form action="<%=WebConstants.MVC_JOB_LIST_FILTER_POST%>"
		   commandName="<%=FilterForm.FORM_NAME%>" method="post">
		   
Title ID <form:input path="titleId"/><br/>

From Date <form:input path="fromDateString"/><br/>

To Date <form:input path="toDateString"/><br/>

Job Status<br/>
<form:select path="batchStatus">
	<form:option label="ALL" value=""/>
	<form:option label="<%=BatchStatus.COMPLETED.toString()%>" value="<%=BatchStatus.COMPLETED.toString() %>"/>
	<form:option label="<%=BatchStatus.FAILED.toString() %>" value="<%=BatchStatus.FAILED.toString() %>"/>
	<form:option label="<%=BatchStatus.STARTED.toString() %>" value="<%=BatchStatus.STARTED.toString() %>"/>
	<form:option label="<%=BatchStatus.STOPPED.toString() %>" value="<%=BatchStatus.STOPPED.toString() %>"/>
	<form:option label="<%=BatchStatus.ABANDONED.toString() %>" value="<%=BatchStatus.ABANDONED.toString() %>"/>
	<form:option label="<%=BatchStatus.STARTING.toString() %>" value="<%=BatchStatus.STARTING.toString() %>"/>
	<form:option label="<%=BatchStatus.STOPPING.toString() %>" value="<%=BatchStatus.STOPPING.toString() %>"/>
	<form:option label="<%=BatchStatus.UNKNOWN.toString() %>" value="<%=BatchStatus.UNKNOWN.toString() %>"/>
</form:select>
<br/>
<br/>
<input type="submit" value="GO"/>
<input type="submit" value="CLEAR"/>

</form:form>
