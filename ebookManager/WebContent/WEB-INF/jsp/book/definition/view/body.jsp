<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm"%>


<script>
function submitForm(cmd)
{
	$('#command').val(cmd);
	theForm.submit();
	return true;
}
</script>
<style type="text/css">
	.labelCol { text-align:right; font-weight: bold;}
</style>

<%-- Check if there is a book model to render, if not don't display a bunch of unvalued labels. --%>
<c:if test="${book != null}">
<table>
<tr>
	<td class="labelCol">Title ID<td>
	<td>${book.primaryKey.fullyQualifiedTitleId}</td>
	<td class="labelCol">&nbsp;</td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td class="labelCol">Name<td>
	<td>${book.bookName}</td>
	<td class="labelCol">&nbsp;</td>
	<td>&nbsp;</td>
</tr>

<tr>
	<td class="labelCol">Major Version<td>
	<td>${book.majorVersion}</td>
	<td class="labelCol">&nbsp;</td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td class="labelCol">Minor Version<td>
	<td>${book.minorVersion}</td>
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
	<td><c:forEach var="author" items="${book.authorList}">
			${author}<br/>
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
	<td class="labelCol">Content Type<td>
	<td>${book.contentType}</td>
	<td class="labelCol">&nbsp;</td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td class="labelCol">Content Subtype<td>
	<td>${book.contentSubtype}</td>
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
<br/>
<%-- Action buttons for the displayed book definition --%>
<form:form name="theForm" commandName="<%=ViewBookDefinitionForm.FORM_NAME%>"
		   action="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_POST%>">
	<form:hidden path="command"/>
	<form:hidden path="<%=WebConstants.KEY_TITLE_ID%>"/>
	<input type="submit" value="Edit" onclick="submitForm('<%=ViewBookDefinitionForm.Command.EDIT%>')"/>
	<input type="submit" value="Generate" onclick="submitForm('<%=ViewBookDefinitionForm.Command.GENERATE%>')"/>
	<input type="submit" value="Delete" onclick="submitForm('<%=ViewBookDefinitionForm.Command.DELETE%>')"/>
	<input type="submit" value="Audit Log" onclick="submitForm('<%=ViewBookDefinitionForm.Command.AUDIT_LOG%>')"/>
	<input type="submit" value="Job History" onclick="submitForm('<%=ViewBookDefinitionForm.Command.JOB_HISTORY%>')"/>
</form:form>
</c:if>	<%-- If there is any book to display --%>
