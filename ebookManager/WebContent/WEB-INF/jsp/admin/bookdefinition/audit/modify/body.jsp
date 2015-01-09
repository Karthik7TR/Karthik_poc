<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookaudit.AdminAuditRecordForm"%>

<%-- Check if there is a model to render, if not display error message --%>
<c:choose>
	<c:when test="${audit != null}">
		<form:form commandName="<%= AdminAuditRecordForm.FORM_NAME %>">
			<form:hidden path="auditId" />
			<form:hidden path="bookDefinitionId" />
			<form:hidden path="proviewDisplayName"/>
			<form:hidden path="isbn"/>
			<form:hidden path="titleId"/>
			Are you sure you want to modify ISBN ${audit.isbn} for Title ID ${audit.titleId}?
			<br>
			<div class="buttons">
				<button type="submit" name="editParam" value="EditIsbnAll">Modify All matching Title ID and ISBN</button>
				<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_AUDIT_BOOK_LIST%>';">Cancel</button>
			</div>
		</form:form>
	</c:when>
	<c:otherwise>
		<div class="errorMessage">No Book Audit found</div>
	</c:otherwise>
</c:choose>
