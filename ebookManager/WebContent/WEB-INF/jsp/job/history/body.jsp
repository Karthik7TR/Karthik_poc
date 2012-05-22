<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>


	<%-- Table of publishing stats for a specific book --%>
	
	<c:set var="DATE_FORMAT" value="<%=WebConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	
	<display:table id="<%= WebConstants.KEY_VDO %>" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
				   requestURI="<%=WebConstants.MVC_BOOK_JOB_HISTORY%>"
				   pagesize="20"
				   defaultsort="1"
				   defaultorder="descending">

	  <display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
	  <display:setProperty name="paging.banner.onepage" value=" " />
	  <display:column title="Date" sortable="true">
	  	<fmt:formatDate value="${vdo.jobSubmitTimestamp}" pattern="${DATE_FORMAT}"/>
	  </display:column>
	  <display:column title="Job Instance ID" sortable="true">
	  	<a href="<%=WebConstants.MVC_JOB_INSTANCE_DETAILS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${vdo.jobInstanceId}">${vdo.jobInstanceId}</a>
	  </display:column>
	  <display:column title="Version" property="bookVersionSubmitted" sortable="true"/>
	  <display:column title="Status" property="publishStatus" sortable="true"/>
	  <display:column title="Submitted By" property="jobSubmitterName" sortable="true"/>
	  <display:column title="Metrics" sortable="false">
	  	<a href="<%=WebConstants.MVC_BOOK_JOB_METRICS%>?<%=WebConstants.KEY_JOB_INSTANCE_ID%>=${vdo.jobInstanceId}">View Metrics</a>
	  </display:column>
	</display:table>
