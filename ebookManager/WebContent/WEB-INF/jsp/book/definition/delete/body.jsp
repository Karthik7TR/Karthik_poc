<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.delete.DeleteBookDefinitionForm"%>

<script type="text/javascript">
$(document).ready(function() {
	$( "#dialog-confirm" ).dialog({
		autoOpen: false,
		resizable: false,
		height:260,
		width:500,
		modal: true,
		draggable:false,
		buttons: {
			"Delete": function() {
				$('#<%= DeleteBookDefinitionForm.FORM_NAME %>').submit();
			},
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		}
	});
	
	$('#save').click(function () {
		$( "#dialog-confirm" ).dialog( "open" );
	});
});
</script>

<%-- Check if there is a book model to render, if not don't display a bunch of unvalued labels. --%>
<c:choose>
	<c:when test="${book != null}">
		<%-- Check if book is already in the queue or scheduled --%>
		<c:choose>
			<c:when test="${!isInJobRequest}">
				<form:form commandName="<%= DeleteBookDefinitionForm.FORM_NAME %>" action="<%=WebConstants.MVC_BOOK_DEFINITION_DELETE%>" >
					<form:hidden path="id" />
					<form:hidden path="action" />
					<div class="errorDiv">
						<form:errors path="id" cssClass="errorMessage" />
					</div>
					<div class="row">
						<c:choose>
							<c:when test="${ book.publishedOnceFlag }">
								This will mark the eBook Definition as deleted. You will not be able to edit, copy, or generate the eBook Definition. A super 
								user will be able to restore this eBook Definition.
							</c:when>
							<c:otherwise>
								<span class="errorMessage">This will permanently delete the eBook Definition.</span>  You will not be able to restore this eBook Definition once it has been deleted.
							</c:otherwise>
						</c:choose>
					</div>
					<div class="row">
						Are you sure you want to delete this eBook Definition?  To delete, please type <b>"<%= WebConstants.CONFIRM_CODE_DELETE_BOOK %>"</b> in the
						Code field and the reason for the deletion in the comments.
					</div>
					<div class="row" style="margin:1em;">
						<form:label path="code">Code</form:label>
						<form:input path="code"/>
						<form:errors path="code" cssClass="errorMessage" />
						<div class="info" style="font-size:.7em;">
							*This field is case sensitive
						</div>
					</div>
					<div class="row" style="margin:1em;">
						<form:label path="comment">Comments</form:label>
						<div>
							<form:textarea path="comment"/>
							<form:errors path="comment" cssClass="errorMessage" />
						</div>
					</div>
				</form:form>
				<div class="buttons">
					<button id="save">Delete</button>
					<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${book.ebookDefinitionId}">Cancel</a>
				</div>
			</c:when>
			<c:otherwise>
			<span style="color:red;">This book is present in the job run queue, and thus cannot be deleted.<br/>
									 If you want to delete the definition, please remove it from the run queue.</span>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
	No eBook Definition found
	</c:otherwise>
</c:choose>


<div id="dialog-confirm" title="Delete eBook Definition?" style="display:hidden;" >
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Are you sure you want to delete this eBook Definition?</p>
</div>

