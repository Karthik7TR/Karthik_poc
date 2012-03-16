<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue.QueueForm.Command"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue.QueueForm.DisplayTagSortProperty"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue.QueueForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="DATE_FORMAT" value="<%=WebConstants.DATE_TIME_FORMAT_PATTERN %>"/>

	<%-- Table of queued job requests (those that will run ASAP) --%>
	<display:table id="row" name="<%=WebConstants.KEY_PAGINATED_LIST%>"
				   requestURI="<%=WebConstants.MVC_JOB_QUEUE_PAGE_AND_SORT%>" class="displayTagTable"> 
	  <display:setProperty name="basic.msg.empty_list">No jobs are queued to run.</display:setProperty>
	  <display:column title="Book Name" property="book.fullBookName" sortable="true" sortProperty="<%=DisplayTagSortProperty.BOOK_NAME.toString()%>"/>
	  <display:column title="Title ID" property="book.titleId" sortable="true" sortProperty="<%=DisplayTagSortProperty.TITLE_ID.toString()%>"/>
	  <display:column title="Version &nbsp;" property="job.bookVersion" sortable="true" sortProperty="<%=DisplayTagSortProperty.BOOK_VERSION.toString()%>"/>
	  <display:column title="Priority &nbsp;" property="job.priority" sortable="true" sortProperty="<%=DisplayTagSortProperty.PRIORITY.toString()%>"/>
	  <display:column title="Submitted By" property="job.submittedBy" sortable="true" sortProperty="<%=DisplayTagSortProperty.SUBMITTED_BY.toString()%>"/>
	  <display:column title="Submitted At" sortable="true" sortProperty="<%=DisplayTagSortProperty.SUBMITTED_AT.toString()%>">
		<fmt:formatDate value="${row.job.submittedAt}" pattern="${DATE_FORMAT}"/>
	  </display:column>
	</display:table>


	