<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.PageAndSortForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.PageAndSortForm.DisplayTagSortProperty"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.JobListForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="org.springframework.batch.core.BatchStatus"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>


  	<c:set var="DATE_FORMAT" value="MM/dd/yy HH:mm:ss"/>


	<form:form action="<%=WebConstants.MVC_JOB_LIST_POST%>"
			   commandName="<%=JobListForm.FORM_NAME%>" name="theForm" method="post">
			   
<%-- Table of job executions for a specific Job name --%>
		<display:table id="vdo" name="paginatedList" class="displayTagTable" cellpadding="2" 
					   requestURI="<%=WebConstants.MVC_JOB_LIST_PAGE_AND_SORT%>"
					   sort="external">
		  <display:setProperty name="basic.msg.empty_list">No job executions were found.</display:setProperty>
	
		  <%-- display:column title="Job Name ${jobNameImageTag}" property="jobExecution.jobInstance.jobName" sortable="true" sortProperty="<%=SortProperty.JOB_NAME.toString()%>" style="text-align: left"/ --%>
		  <%-- No need to display the job name since it will always be the same.  The book code discriminates between which book the job will create. --%>
		  <display:column title="Book Name" property="bookName" sortable="true" sortProperty="<%=DisplayTagSortProperty.BOOK_NAME.toString()%>" style="text-align: left"/>
		  <display:column title="Title ID" property="fullyQualifiedTitleId" sortable="true" sortProperty="<%=DisplayTagSortProperty.TITLE_ID.toString()%>"style="text-align: left"/>
		  <display:column title="Job ID" sortable="true" sortProperty="<%=DisplayTagSortProperty.JOB_INSTANCE_ID.toString()%>">
		  		<a href="<%=WebConstants.MVC_AFTER_LOGOUT%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${vdo.jobExecution.jobInstance.id}">${vdo.jobExecution.jobInstance.id}</a> / ${vdo.jobExecution.id}
		  </display:column>

		  <display:column title="Job Status" property="jobExecution.status" sortable="true" sortProperty="<%=DisplayTagSortProperty.BATCH_STATUS.toString()%>"/>
		  <display:column title="Start Time" sortable="true" sortProperty="<%=DisplayTagSortProperty.START_TIME.toString()%>"><fmt:formatDate value="${vdo.jobExecution.startTime}" pattern="${DATE_FORMAT}"/></display:column>
		  
		</display:table>
	
<%-- Select for how may items (rows) per page to show --%>
		<br/>
		
	</form:form>

	<form:form name="pageAndSortFormTPH" 
			   commandName="<%=PageAndSortForm.FORM_NAME%>"
			   action="<%=WebConstants.MVC_JOB_LIST_ITEMS%>"
			   method="post">
		Items per page: 
		<c:set var="defaultItemsPerPage" value="<%=PageAndSortForm.DEFAULT_ITEMS_PER_PAGE%>"/>
		<form:select path="itemsPerPage" onchange="submit()">
			<form:option label="${defaultItemsPerPage}" value="${defaultItemsPerPage}"/>
			<form:option label="50" value="50"/>
			<form:option label="100" value="100"/>
			<form:option label="500" value="500"/>
		</form:select>
	</form:form>

