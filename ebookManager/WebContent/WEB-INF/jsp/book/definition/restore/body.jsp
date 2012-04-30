<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.delete.DeleteBookDefinitionForm"%>

<%-- Check if there is a book model to render, if not don't display a bunch of unvalued labels. --%>
<c:choose>
	<c:when test="${book != null}">
		<form:form commandName="<%= DeleteBookDefinitionForm.FORM_NAME %>" action="<%=WebConstants.MVC_BOOK_DEFINITION_RESTORE%>" >
			<form:hidden path="id" />
			<form:hidden path="action" />
			<div class="errorDiv">
				<form:errors path="id" cssClass="errorMessage" />
			</div>
			<div class="row">
				Are you sure you want to restore this eBook Definition? Please put your explanation in the comments.
			</div>
			<div class="row" style="margin:1em;">
				<form:label path="comment">Comments</form:label>
				<div>
					<form:textarea path="comment"/>
					<form:errors path="comment" cssClass="errorMessage" />
				</div>
			</div>
			<div class="buttons">
				<form:button id="save">Restore</form:button>
				<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${book.ebookDefinitionId}">Cancel</a>
			</div>
		</form:form>
	</c:when>
	<c:otherwise>
	No eBook Definition found
	</c:otherwise>
</c:choose>

