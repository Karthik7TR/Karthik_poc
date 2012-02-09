<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.SortProperty"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

Book Library List Body<br>


<%-- Table of job executions for a specific Job name --%>
	<display:table id="vdo" name="paginatedList" class="displayTagTable" cellpadding="2" 
				   requestURI="<%= WebConstants.MVC_BOOK_LIBRARY_LIST_PAGING%>"
				   pagesize="5"
				   partialList="true"
				   size="resultSize"
				   sort="external">
	  <display:setProperty name="basic.msg.empty_list">No book definitions were found.</display:setProperty>
	  <display:column title="Select">
	  	<input type="checkbox" name="id" value="${vdo.fullyQualifiedTitleId}" />
	  </display:column>
	  <display:column title="Book Name" property="bookName" sortable="true" sortName="BOOK_NAME" style="text-align: left"/>
	  <display:column title="Title ID" property="fullyQualifiedTitleId" sortable="true" sortName="author" style="text-align: left"/>
	</display:table>
