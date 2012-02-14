<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>


<style type="text/css">
	.labelCol { text-align:right; font-weight: bold;}
</style>


<form:form commandName="<%= EditBookDefinitionForm.FORM_NAME %>"
	action="<%=WebConstants.MVC_BOOK_DEFINITION_CREATE_POST%>" >
	<table>
		<tr>
			<td class="labelCol">Title ID<td>
			<td><form:input path="titleId" /></td>
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
		<tr>
			<td class="labelCol">Copyright<td>
			<td><form:input path="copyright" /></td>
		</tr>
		<tr>
			<td class="labelCol">Material ID<td>
			<td><form:input path="materialId" /></td>
		</tr>
		<tr>
			<td class="labelCol">Authors<td>
			<td><form:input path="authorInfo" /></td>
		</tr>
		<tr>
			<td class="labelCol">Root TOC Guid<td>
			<td><form:input path="rootTocGuid" /></td>
		</tr>
		<tr>
			<td class="labelCol">Doc Collection Name<td>
			<td><form:input path="docCollectionName" /></td>
		</tr>
		<tr>
			<td class="labelCol">TOC Collection Name<td>
			<td><form:input path="tocCollectionName" /></td>
		</tr>
		<tr>
			<td class="labelCol">NORT Domain<td>
			<td><form:input path="nortDomain" /></td>
		</tr>
		<tr>
			<td class="labelCol">NORT Filter View<td>
			<td><form:input path="nortFilterView" /></td>
		</tr>
		<tr>
			<td class="labelCol">Content Type<td>
			<td><form:input path="contentType" /></td>
		</tr>
		<tr>
			<td class="labelCol">Content Subtype<td>
			<td><form:input path="contentSubtype" /></td>
		</tr>
		<tr>
			<td class="labelCol">Cover Image<td>
			<td><form:input path="coverImage" /></td>
		</tr>
		<tr>
			<td class="labelCol">ISBN<td>
			<td><form:input path="isbn" /></td>
		</tr>
		<tr>
			<td class="labelCol">Material ID Embedded In Doc Text<td>
			<td><form:input path="materialIdEmbeddedInDocText" /></td>
		</tr>
	</table>
</form:form>


