<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm"%>


<script>
function submitForm(cmd)
{
	$('#command').val(cmd);
	$('#<%=ViewBookDefinitionForm.FORM_NAME%>').submit();
	return true;
}
</script>
<style type="text/css">
	.labelCol { text-align:right; font-weight: bold;}
</style>

<%-- Check if there is a book model to render, if not don't display a bunch of unvalued labels. --%>
<c:if test="${book != null}">
<c:if test="${isInJobRequest}">
	<div style="color:red;">Note: This book definition is already in the job run queue, and thus cannot be edited.</div>
</c:if>
<div style="font-family:Arial">
	<table style="font-family:Arial">
	<tr>
		<td class="labelCol">Title ID<td>
		<td>${book.titleId}</td>
		<td class="labelCol">&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="labelCol">Name<td>
		<td>${book.proviewDisplayName}</td>
		<td class="labelCol">&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="labelCol">Copyright<td>
		<td>${book.copyright}</td>
		<td class="labelCol">&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="labelCol">Material ID<td>
		<td>${book.materialId}</td>
		<td class="labelCol">&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="labelCol">Author(s)<td>
		<td><c:forEach var="author" items="${book.authors}">
				${author.fullName}<br/>
			</c:forEach>
		</td>
		<td class="labelCol">&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="labelCol">TOC Collection<td>
		<td>${book.tocCollectionName}</td>
		<td class="labelCol">&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="labelCol">Root TOC GUID<td>
		<td>${book.rootTocGuid}</td>
		<td class="labelCol">&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="labelCol">DOC Collection<td>
		<td>${book.docCollectionName}</td>
		<td class="labelCol">&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="labelCol">NORT Domain<td>
		<td>${book.nortDomain}</td>
		<td class="labelCol">&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="labelCol">NortFilter View<td>
		<td>${book.nortFilterView}</td>
		<td class="labelCol">&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="labelCol">ISBN<td>
		<td>${book.isbn}</td>
		<td class="labelCol">&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	</table>
	
	<%-- Action buttons for the displayed book definition --%>
	<br/>
	<form:form name="theForm" commandName="<%=ViewBookDefinitionForm.FORM_NAME%>"
			   action="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_POST%>">
		<form:hidden path="command"/>
		<form:hidden path="<%=WebConstants.KEY_ID%>"/>
		<c:if test="${!isInJobRequest}">
			<input type="submit" ${buttonVisibility} value="Edit" onclick="submitForm('<%=ViewBookDefinitionForm.Command.EDIT%>')"/>
		</c:if>
		<input type="submit" ${buttonVisibility} value="Copy" onclick="submitForm('<%=ViewBookDefinitionForm.Command.COPY%>')"/>
		<input type="submit" value="Generate" onclick="submitForm('<%=ViewBookDefinitionForm.Command.GENERATE%>')"/>
		<input type="submit" ${buttonVisibility} value="Delete" onclick="submitForm('<%=ViewBookDefinitionForm.Command.DELETE%>')"/>
		<input type="submit" value="Audit Log" onclick="submitForm('<%=ViewBookDefinitionForm.Command.AUDIT_LOG%>')"/>
		<input type="submit" value="Publishing History" onclick="submitForm('<%=ViewBookDefinitionForm.Command.BOOK_PUBLISHING_HISTORY%>')"/>
	</form:form>
</div>
</c:if>	<%-- If there is any book to display --%>
