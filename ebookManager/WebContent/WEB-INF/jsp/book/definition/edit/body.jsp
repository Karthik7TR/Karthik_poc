<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>

<%-- Check if there is a book model to render, if not don't display a bunch of unvalued labels. --%>
<c:choose>
	<c:when test="${book != null}">
		<%-- Check if book is already in the queue or scheduled --%>
		<c:choose>
			<c:when test="${!isInJobRequest}">
				<div class="bookDefinitionCRUD">
					<form:form commandName="<%= EditBookDefinitionForm.FORM_NAME %>" action="<%=WebConstants.MVC_BOOK_DEFINITION_EDIT%>" >
						<jsp:include page="../common/crudForm.jsp" />
						<div class="buttons">
							<form:button id="validate">Validate</form:button>
							<form:button id="save">Save</form:button>
							<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${book.ebookDefinitionId}">Cancel</a>
						</div>
					</form:form>
				</div>
			</c:when>
			<c:otherwise>
			The book definition is scheduled or in the queue.  If you want to edit the definition, please remove it from the schedule or queue.
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
	No book found
	</c:otherwise>
</c:choose>


