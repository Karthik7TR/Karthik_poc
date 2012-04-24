<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookdefinitionlock.BookDefinitionLockForm"%>

<%-- Check if there is a model to render, if not display error message --%>
<c:choose>
	<c:when test="${bookDefinitionLock != null}">
		<form:form commandName="<%= BookDefinitionLockForm.FORM_NAME %>">
			<form:hidden path="bookDefinitionLockId" />
			<form:hidden path="bookDefinitionId" />
			<form:hidden path="checkoutTimestamp"/>
			<form:hidden path="username"/>
			<form:hidden path="fullName"/>
			Are you sure you want to delete Book Definition lock for: ${bookDefinitionLock.ebookDefinition.fullyQualifiedTitleId}
			<br>
			Make sure ${ bookDefinitionLock.fullName } (${ bookDefinitionLock.username }) is not editing the Book Definition before deleting the lock.
			<div class="buttons">
				<form:button id="delete">Delete</form:button>
				<a href="<%=WebConstants.MVC_ADMIN_BOOK_LOCK_LIST%>">Cancel</a>
			</div>
		</form:form>
	</c:when>
	<c:otherwise>
		<div class="errorMessage">No Book Definition Lock found</div>
	</c:otherwise>
</c:choose>
