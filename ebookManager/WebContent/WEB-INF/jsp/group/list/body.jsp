<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list.GroupForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list.GroupForm.DisplayGroupSortProperty"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Select for how may items (rows) per page to show --%>
<c:if test="${fn:length(paginatedList.list) != 0}">
  <form:form id="itemCountForm" action="<%=WebConstants.MVC_BOOK_GROUP_CHANGE_ROW_COUNT%>"
		     commandName="<%=GroupForm.FORM_NAME%>" method="post">
	Items to display: 
	<c:set var="defaultItemsPerPage" value="<%=PageAndSort.DEFAULT_ITEMS_PER_PAGE%>"/>
	<form:select path="objectsPerPage" onchange="submit()">
		<form:option label="${defaultItemsPerPage}" value="${defaultItemsPerPage}"/>
		<form:option label="50" value="50"/>
		<form:option label="100" value="100"/>
		<form:option label="150" value="150"/>
		<form:option label="300" value="300"/>
		<%-- Shows to MAX_INT.  Needs to get updated once number of books reach this amount --%>
		<form:option label="ALL" value="<%= Integer.MAX_VALUE %>"/>
	</form:select>
  </form:form>
</c:if>  <%-- if (table row count > 0) --%>	

<%-- Table of job executions --%>
<display:table id="group" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
			   requestURI="<%=WebConstants.MVC_BOOK_GROUP_LIST_PAGE_AND_SORT%>"
			   sort="external">
  <display:setProperty name="basic.msg.empty_list">No book definition groups were found.</display:setProperty>
  <display:setProperty name="paging.banner.onepage" value=" " />
  <display:column title="ProView Display Name" property="proviewDisplayName" sortable="true" sortProperty="<%=DisplayGroupSortProperty.PROVIEW_DISPLAY_NAME.toString()%>"/>
  <display:column title="Title ID" property="titleId" sortable="true" sortProperty="<%=DisplayGroupSortProperty.TITLE_ID.toString()%>" />
  <display:column title="Group Name" property="groupName" sortable="true" sortProperty="<%=DisplayGroupSortProperty.GROUP_NAME.toString()%>"/>
  <display:column title="Book Version" property="bookVersion"/> 
  <display:column title="Action" sortable="false" media="html">
	  			<a href="<%=WebConstants.MVC_GROUP_BOOK_ALL_VERSIONS%>?<%=WebConstants.KEY_ID%>=${group.bookDefinitionId}">View Group details</a>
  </display:column>   
 
</display:table>
