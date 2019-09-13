<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditForm.DisplayTagSortProperty"%>
<%@ page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditForm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript">
    function changePageSize(){
        $('#<%=ProviewAuditForm.FORM_NAME%>').submit();
    }
</script>

<form:form action="<%=WebConstants.MVC_PROVIEW_AUDIT_LIST_PAGE_AND_SORT%>"
           commandName="<%=ProviewAuditForm.FORM_NAME%>" name="form" method="post">

    Items per page:
    <form:select path="objectsPerPage" onchange="changePageSize()">
        <form:option label="20" value="20"/>
        <form:option label="50" value="50"/>
        <form:option label="100" value="100"/>
        <form:option label="250" value="250"/>
        <form:option label="ALL" value="<%= Integer.MAX_VALUE %>"/>
    </form:select>

</form:form>

<display:table id="audit" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2"
               requestURI="<%=WebConstants.MVC_PROVIEW_AUDIT_LIST_PAGE_AND_SORT%>"
               sort="external"
               pagesize="${pageSize}">
  <display:setProperty name="basic.msg.empty_list">No proview audits were found.</display:setProperty>
  <display:setProperty name="paging.banner.onepage" value=" " />
  <display:column title="Action Submit Date/Time" sortable="true" sortProperty="<%=DisplayTagSortProperty.REQUEST_DATE.toString()%>">
    <fmt:formatDate value="${audit.requestDate}" pattern="<%=CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>
  </display:column>
  <display:column title="Title ID" property="titleId" sortable="true" sortProperty="<%=DisplayTagSortProperty.TITLE_ID.toString()%>" />
  <display:column title="Book Version" property="bookVersion" sortable="true" sortProperty="<%=DisplayTagSortProperty.BOOK_VERSION.toString()%>"/>
  <display:column title="ProView Last Update" sortable="true" sortProperty="<%=DisplayTagSortProperty.BOOK_LAST_UPDATED.toString()%>">
    <fmt:formatDate value="${audit.bookLastUpdated}" pattern="<%=CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>
  </display:column>
  <display:column title="User Name" property="username" sortable="true" sortProperty="<%=DisplayTagSortProperty.USERNAME.toString()%>"/>
  <display:column title="Action" property="proviewRequest" sortable="true" sortProperty="<%=DisplayTagSortProperty.PROVIEW_REQUEST.toString()%>"/>
  <display:column title="Comment" property="auditNote" />
</display:table>

