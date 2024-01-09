<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookaudit.AdminAuditFilterForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort.SortProperty"%>

<script type="text/javascript" src="js/form-utils.js"></script>
<script type="text/javascript">
	const opp = "${ pageSize == null ? defaultPageSize : pageSize }";
	$(window).on('pageshow', function() {
		$('#objectsPerPage option[value=' + opp + ']').prop('selected', true);
	});

</script>

<c:if test="${fn:length(paginatedList.list) != 0}">
	<form:form id="itemCountForm" action="<%=WebConstants.MVC_ADMIN_AUDIT_CHANGE_ROW_COUNT%>"
			   commandName="<%=AdminAuditFilterForm.FORM_NAME%>" method="post">

		<c:set var="defaultPageSize" value="<%=WebConstants.DEFAULT_PAGE_SIZE%>"/>

		Items per page:
		<form:select path="objectsPerPage" onchange="submit()">
			<form:option label="${ defaultPageSize }" value="${ defaultPageSize }"/>
			<form:option label="50" value="50"/>
			<form:option label="100" value="100"/>
			<form:option label="250" value="250"/>
			<form:option label="ALL" value="<%= Integer.MAX_VALUE %>"/>
		</form:select>
		<br>
	</form:form>
</c:if>  <%-- if (table row count > 0) --%>

<form:form action="<%=WebConstants.MVC_ADMIN_AUDIT_BOOK_LIST%>"
		   commandName="<%=AdminAuditFilterForm.FORM_NAME%>" method="get">

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
		<form:input path="proviewDisplayName" maxlength="4000"/>

		<label>Title ID:</label>
		<form:input path="titleId" maxlength="4000"/>

		<label>ISBN:</label>
		<form:input path="isbn" maxlength="4000"/>
		<label class="wildCard">Wildcard: %</label>
		<input type="submit" name="submit" value="Search" />
		<input type="submit" name="submit" value="Reset" />
	</div>

	<form:hidden path="sort" value="${ param.sort }"/>
	<form:hidden path="dir" value="${ param.dir }"/>
	<display:table id="adminAudit" name="<%=WebConstants.KEY_PAGINATED_LIST%>"
				   class="displayTagTable" cellpadding="2"
				   requestURI="<%=WebConstants.MVC_ADMIN_AUDIT_BOOK_LIST%>"
				   export="true"
				   sort="external">

		<display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
		<display:setProperty name="paging.banner.onepage" value=" " />
		<display:column title="ProView Display Name" sortable="true" sortProperty="<%=SortProperty.PROVIEW_DISPLAY_NAME.toString() %>" >
			<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${adminAudit.audit.ebookDefinitionId}">${adminAudit.audit.proviewDisplayName}</a>
		</display:column>
		<display:column title="Title ID" sortable="true"  sortProperty="<%=SortProperty.TITLE_ID.toString() %>">
			<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${adminAudit.audit.ebookDefinitionId}">${adminAudit.audit.titleId}</a>
		</display:column>
		<display:column title="ProView Version"  sortable="true" sortProperty="<%=SortProperty.PROVIEW_VERSION.toString() %>" >
			${adminAudit.bookVersionSubmitted}
		</display:column>
		<display:column title="ISBN"  sortable="true"  sortProperty="<%=SortProperty.ISBN.toString() %>">
			${adminAudit.audit.isbn}
		</display:column>

		<display:column title="Last Definition Edit Date/Time" sortable="true" sortProperty="<%=SortProperty.GENERATE_DATE_TIME.toString() %>" ><fmt:formatDate value="${adminAudit.jobSubmitTimestamp}" pattern="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>
		</display:column>

		<display:column title="Edit?"  sortable="true"  sortProperty="<%=SortProperty.ISBN.toString() %>"><a href="<%=WebConstants.MVC_ADMIN_AUDIT_BOOK_MODIFY_ISBN%>?<%=WebConstants.KEY_ID%>=${adminAudit.audit.auditId}">ISBN</a>
		</display:column>

	</display:table>

</form:form>
