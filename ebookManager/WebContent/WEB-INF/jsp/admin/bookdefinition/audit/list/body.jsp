<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookaudit.AdminAuditFilterForm"%>

<form:form action="<%=WebConstants.MVC_ADMIN_AUDIT_BOOK_SEARCH%>"
			   commandName="<%=AdminAuditFilterForm.FORM_NAME%>" method="post">

	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=AdminAuditFilterForm.FORM_NAME%>">
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
    </spring:hasBindErrors>
	
	<div class="filterRow">
		<label>ProView Display Name:</label>
		<form:input path="proviewDisplayName"/>

		<label>Title ID:</label>
		<form:input path="titleId"/>

		<label>ISBN:</label>
		<form:input path="isbn"/>
		<label class="wildCard">Wildcard: %</label>
		<input type="submit" value="Search" />
	</div>
</form:form>

<div class="centerSection">
	<c:set var="DATE_FORMAT" value="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	<c:choose>
		<c:when test="${publishingStats != null}">
			<div id="auditListLabel">Only up to 100 results are displayed:</div>
			<table id="auditList" class="displayTagTable">
				<thead>
					<tr>
						<th>ProView Display Name</th>
						<th>Title ID</th>
						<th>ProView Version</th>
						<th>ISBN</th>
						<th class="sortable sorted order2">Generate Date/Time</th>
						<th>Edit?</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${publishingStats}" var="record" varStatus="status">
						<c:set var="rowClass" value="odd" />
						<c:if test="${status.index % 2 == 0 }">
							<c:set var="rowClass" value="even" />
						</c:if>
						<tr class="${rowClass}">
							<td><a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${record.audit.ebookDefinitionId}">${record.audit.proviewDisplayName}</a></td>
							<td><a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${record.audit.ebookDefinitionId}">${record.audit.titleId}</a></td>
							<td>${record.bookVersionSubmitted } </td>
							<td>${record.audit.isbn }</td>
							<td><fmt:formatDate value="${record.jobSubmitTimestamp}" pattern="${DATE_FORMAT}"/></td>
							<td><a href="<%=WebConstants.MVC_ADMIN_AUDIT_BOOK_MODIFY_ISBN%>?<%=WebConstants.KEY_ID%>=${record.audit.auditId}">ISBN</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:otherwise>
			<div id="auditListLabel">Please search for Publish records</div>
		</c:otherwise>
	</c:choose>
</div>