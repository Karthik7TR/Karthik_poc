<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.JobListForm.JobCommand"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.FilterForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.PageAndSort"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.PageAndSort.DisplayTagSortProperty"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.JobListForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="org.springframework.batch.core.BatchStatus"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script>
$(document).ready(function() {
	$('#selectAll').click(function () {
		$(this).parents('#<%= WebConstants.KEY_VDO %>').find(':checkbox').attr('checked', this.checked);
	});
});
function submitChangeObjectsPerPage() {
	submitJobListForm('<%=JobCommand.CHANGE_OBJECTS_PER_PAGE%>');
}
function submitJobListForm(command) {
	$("#jobCommand").val(command);  // Set the form hidden field value for the operation discriminator
	$("#<%=JobListForm.FORM_NAME%>").submit();	// POST the HTML form
}
</script>

 <c:set var="DATE_FORMAT" value="MM/dd/yy HH:mm:ss"/>

<form:form action="<%=WebConstants.MVC_JOB_LIST_POST%>"
		   commandName="<%=JobListForm.FORM_NAME%>" name="theForm" method="post">
	<form:hidden path="jobCommand"/>
	
	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=JobListForm.FORM_NAME%>">
		<div class="errorBox">
	      <b><spring:message code="please.fix.errors"/>:</b><br/>
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
	
	<%-- Table of job executions --%>
	<c:set var="selectAllElement" value="<input type='checkbox' id='selectAll' value='false' />"/>
	<display:table id="vdo" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
				   requestURI="<%=WebConstants.MVC_JOB_LIST_PAGE_AND_SORT%>"
				   sort="external">
	  <display:setProperty name="basic.msg.empty_list">No job executions were found.</display:setProperty>
	  <display:column title="${selectAllElement}"  style="text-align: center">
  		<form:checkbox path="jobExecutionIds" value="${vdo.jobExecution.id}"/>
  	  </display:column>
	  <display:column title="Book Name" property="bookName" sortable="true" sortProperty="<%=DisplayTagSortProperty.BOOK_NAME.toString()%>" style="text-align: left"/>
	  <display:column title="Title ID" property="fullyQualifiedTitleId" sortable="true" sortProperty="<%=DisplayTagSortProperty.TITLE_ID.toString()%>"style="text-align: left"/>
	  <display:column title="Inst" sortable="true" sortProperty="<%=DisplayTagSortProperty.JOB_INSTANCE_ID.toString()%>">
	  		<a href="<%=WebConstants.MVC_JOB_INSTANCE_DETAIL%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${vdo.jobExecution.jobInstance.id}">${vdo.jobExecution.jobInstance.id}</a>
	  </display:column>
	  <display:column title="Exec" sortable="true" sortProperty="<%=DisplayTagSortProperty.JOB_EXECUTION_ID.toString()%>">
	  		<a href="<%=WebConstants.MVC_JOB_EXECUTION_DETAIL%>?<%=WebConstants.KEY_JOB_EXECUTION_ID%>=${vdo.jobExecution.id}">${vdo.jobExecution.id}</a>
	  </display:column>
	  <display:column title="Job Status" property="jobExecution.status" sortable="true" sortProperty="<%=DisplayTagSortProperty.BATCH_STATUS.toString()%>"/>
	  <display:column title="Start Time" sortable="true" sortProperty="<%=DisplayTagSortProperty.START_TIME.toString()%>"><fmt:formatDate value="${vdo.jobExecution.startTime}" pattern="${DATE_FORMAT}"/></display:column>
	  <display:column title="Duration" property="executionDuration"/>
	</display:table>
	
	<%-- Select for how may items (rows) per page to show --%>
	Rows per page: 
	<c:set var="defaultItemsPerPage" value="<%=PageAndSort.DEFAULT_ITEMS_PER_PAGE%>"/>
	<form:select path="objectsPerPage" onchange="submitChangeObjectsPerPage()">
		<form:option label="${defaultItemsPerPage}" value="${defaultItemsPerPage}"/>
		<form:option label="50" value="50"/>
		<form:option label="100" value="100"/>
		<form:option label="500" value="500"/>
	</form:select>
	&nbsp;
	
	<%-- Operational buttons --%> 
	<%-- sec:authorize access="hasRole('ROLE_SUPERUSER')" --%>
		<input type="button" value="Stop Job" onclick="submitJobListForm('<%=JobCommand.STOP_JOB%>')"/> &nbsp;
		<input type="button" value="Restart Job" onclick="submitJobListForm('<%=JobCommand.RESTART_JOB%>')"/>
	
</form:form>

