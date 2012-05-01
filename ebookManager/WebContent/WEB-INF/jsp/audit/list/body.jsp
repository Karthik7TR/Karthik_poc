<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditForm.DisplayTagSortProperty"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Table of job executions --%>
<display:table id="audit" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
			   requestURI="<%=WebConstants.MVC_BOOK_AUDIT_LIST_PAGE_AND_SORT%>"
			   sort="external">
  <display:setProperty name="basic.msg.empty_list">No book definition audits were found.</display:setProperty>
  <display:setProperty name="paging.banner.onepage" value=" " />
 	  <!-- The book name displayed in this column is what the name was when the job was run for this definition ID (it may be different now). -->
  <display:column title="Date/Time" sortable="true" sortProperty="<%=DisplayTagSortProperty.SUBMITTED_DATE.toString()%>"><fmt:formatDate value="${audit.lastUpdated}" pattern="<%=WebConstants.DATE_TIME_FORMAT_PATTERN %>"/></display:column>
  <display:column title="Book Definition ID" sortable="true" sortProperty="<%=DisplayTagSortProperty.BOOK_DEFINITION_ID.toString()%>" >
  	<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${audit.ebookDefinitionId}">${audit.ebookDefinitionId}</a>
  </display:column>
  <display:column title="Proview Display Name" property="proviewDisplayName" sortable="true" sortProperty="<%=DisplayTagSortProperty.BOOK_NAME.toString()%>"/>
  <display:column title="Title ID" property="titleId" sortable="true" sortProperty="<%=DisplayTagSortProperty.TITLE_ID.toString()%>" />
  <display:column title="User Name" property="updatedBy" sortable="true" sortProperty="<%=DisplayTagSortProperty.SUBMITTED_BY.toString()%>"/>
  <display:column title="Action" property="auditType" sortable="true" sortProperty="<%=DisplayTagSortProperty.ACTION.toString()%>"/>
  <display:column title="Comment" property="auditNote" />
</display:table>

