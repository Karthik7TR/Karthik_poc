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

Publishing History<br>


<%-- Table of job executions for a specific Job name --%>
	<display:table name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
				   requestURI="<%=WebConstants.MVC_BOOK_PUBLISHING_HISTORY%>"
				   pagesize="5"
				   partialList="true"
				   size="resultSize"
				   sort="external">
	  <display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
	  
	  <display:column title="Job Instance ID" property="jobInstanceId" sortable="true" style="text-align: left"/>
	  <display:column title="Audit ID" property="auditId" sortable="true" style="text-align: left"/>
	  <display:column title="Submit Time" property="jobSubmitTimestamp" sortable="true" style="text-align: left"/>
	  <display:column title="Submitter" property="jobSubmitterName" sortable="true" style="text-align: left"/>
	  <display:column title="Submitted Version" property="bookVersionSubmitted" sortable="true" style="text-align: left"/>
	  <display:column title="Publish Status" property="publishStatus" sortable="true" style="text-align: left"/>
	  <display:column title="Publish Start Time" property="publishStartTimestamp" sortable="true" style="text-align: left"/>
	  <display:column title="Publish End Time" property="publishEndTimestamp" sortable="true" style="text-align: left"/>
	  
	</display:table>
