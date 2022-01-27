<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditFilterForm.DisplayTagSortProperty"%>
<%@ page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditFilterForm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="defaultPageSize" value="<%=WebConstants.DEFAULT_PAGE_SIZE%>"/>

<script type="text/javascript" src="js/form-utils.js"></script>
<script type="text/javascript">
	const opp = "${ pageSize == null ? defaultPageSize : pageSize }";
	$(window).on('pageshow', function () {
		$('#objectsPerPage option[value=' + opp + ']').prop('selected', true);
	});
</script>

<c:if test="${fn:length(paginatedList.list) != 0}">
	<form:form id="bodyForm"
						 action="<%=WebConstants.MVC_BOOK_AUDIT_LIST%>"
						 modelAttribute="<%=BookAuditFilterForm.FORM_NAME%>"
						 method="get">
	Items to display: 
	<form:select path="objectsPerPage" onchange="submitLeftFormAndBodyForm()">
		<form:option label="${defaultPageSize}" value="${defaultPageSize}"/>
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
<display:table id="audit" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
			   requestURI="<%=WebConstants.MVC_BOOK_AUDIT_LIST%>"
			   sort="external">
  <display:setProperty name="basic.msg.empty_list">No book definition audits were found.</display:setProperty>
  <display:setProperty name="paging.banner.onepage" value=" " />
 	  <!-- The book name displayed in this column is what the name was when the job was run for this definition ID (it may be different now). -->
  <display:column title="Date/Time" sortable="true" sortProperty="<%=DisplayTagSortProperty.SUBMITTED_DATE.toString()%>"><fmt:formatDate value="${audit.lastUpdated}" pattern="<%=CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/></display:column>
  <display:column title="Book Definition ID" sortable="true" sortProperty="<%=DisplayTagSortProperty.BOOK_DEFINITION_ID.toString()%>" >
  	<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${audit.ebookDefinitionId}">${audit.ebookDefinitionId}</a>
  </display:column>
  <display:column title="ProView Display Name" property="proviewDisplayName" sortable="true" sortProperty="<%=DisplayTagSortProperty.BOOK_NAME.toString()%>"/>
  <display:column title="Title ID" property="titleId" sortable="true" sortProperty="<%=DisplayTagSortProperty.TITLE_ID.toString()%>" />
  <display:column title="User Name" property="updatedBy" sortable="true" sortProperty="<%=DisplayTagSortProperty.SUBMITTED_BY.toString()%>"/>
  <display:column title="Action" property="auditType" sortable="true" sortProperty="<%=DisplayTagSortProperty.ACTION.toString()%>"/>
  <display:column title="Comment" property="auditNote" />
</display:table>
