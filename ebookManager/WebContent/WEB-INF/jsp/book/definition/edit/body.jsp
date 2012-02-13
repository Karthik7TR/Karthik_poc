<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>


<style type="text/css">
	.labelCol { text-align:right; font-weight: bold;}
</style>

<%-- Check if there is a book model to render, if not don't display a bunch of unvalued labels. --%>
<c:if test="${book != null}">

<form:form commandName="<%= EditBookDefinitionForm.FORM_NAME %>"
	action="<%=WebConstants.MVC_BOOK_DEFINITION_EDIT_POST%>" >
	<table>
		<tr>
			<td class="labelCol">Title ID<td>
			<td> ${editBookDefinitionForm.titleId} </td>
		</tr>
		<tr>
			<td class="labelCol">Name<td>
			<td><form:input path="bookName" /></td>
		</tr>
		<tr>
			<td class="labelCol">Major Version<td>
			<td><form:input path="majorVersion" /></td>
		</tr>
		<tr>
			<td class="labelCol">Minor Version<td>
			<td><form:input path="minorVersion" /></td>
		</tr>
	</table>
</form:form>
</c:if>


