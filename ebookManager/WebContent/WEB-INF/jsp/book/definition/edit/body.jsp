<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>

<script type="text/javascript">
	var warning = true;
	var unlockBook = function(async, onSuccess) {
		var bookdefinitionId = ${editBookDefinitionForm.bookdefinitionId}; 
		  $.ajax({
			  type: "POST",
			  url: "<%= WebConstants.MVC_BOOK_DEFINITION_UNLOCK %>",
			  data: { id: bookdefinitionId},
			  success: onSuccess,
			  async: async
			});
	};

	$(window).on('beforeunload', function (e) {
		if (warning) {
			let message = 'Are you sure you want to leave the page? Changes will not be saved.'
			e.returnValue = message;
			return message;
		}
	})
	
	$(window).on('unload', function () { unlockBook(false); });
	
	setInterval(function () { 
		let bookDefinitionId = ${editBookDefinitionForm.bookdefinitionId};
		$.post({
			url: "<%=WebConstants.MVC_BOOK_DEFINITION_LOCK_EXTEND%>",
			data: { id: bookDefinitionId}
		});
	}, <%= BookDefinitionLock.LOCK_TIMEOUT_SEC / 2 * 1000 %>);
	
	// Unlocks the book and redirects user to View Book Definition page.
	$(document).ready(function() {
		$('#cancel').click(function () {
			warning = false;
			if(window.activeSapRequest) {
				window.activeSapRequest.abort();
				delete window.activeSapRequest;
			}
			unlockBook(true, function(response) {
				  window.location = "<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${book.ebookDefinitionId}";
			});
	    });    
	});
</script>


<%-- Check if there is a book model to render, if not don't display a bunch of unvalued labels. --%>
<c:choose>
	<c:when test="${book != null}">
		<div class="bookDefinitionCRUD">
			<form:form commandName="<%= EditBookDefinitionForm.FORM_NAME %>" action="<%=WebConstants.MVC_BOOK_DEFINITION_EDIT%>" >
				<jsp:include page="../common/crudForm.jsp" />
				<div class="buttons">
					<div class="row">
						<form:label path="isComplete" class="labelCol">Book Definition Status</form:label>
						<form:radiobutton path="isComplete" value="true" />Ready
						<form:radiobutton path="isComplete" value="false" />Incomplete
						<div class="errorDiv">
							<form:errors path="isComplete" cssClass="errorMessage" />
						</div>
					</div>
					<form:button id="validate">Validate</form:button>
					<form:button id="confirm">Save</form:button>
					<button id="cancel" type="button">Cancel</button>
				</div>
			</form:form>
		</div>
	</c:when>
	<c:otherwise>
	No book found
	</c:otherwise>
</c:choose>


