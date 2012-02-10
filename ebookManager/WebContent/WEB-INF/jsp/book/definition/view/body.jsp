<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style type="text/css">
	.labelCol { text-align:right; font-weight: bold;}
</style>

<c:if test="${book != null}">
<table>
<tr>
	<td class="labelCol">Title ID<td>
	<td>${book.primaryKey.fullyQualifiedTitleId}</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>
<tr>
	<td class="labelCol">Name<td>
	<td>${book.bookName}</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>

<tr>
	<td class="labelCol">Major Version<td>
	<td>${book.majorVersion}</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>
<tr>
	<td class="labelCol">Minor Version<td>
	<td>${book.minorVersion}</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>
<tr>
	<td class="labelCol">Copyright<td>
	<td>${book.copyright}</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>
<tr>
	<td class="labelCol">Material ID<td>
	<td>${book.materialId}</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>
<tr>
	<td class="labelCol">Author(s)<td>
	<td><c:forEach var="author" items="${book.authorList}">
			${author}<br/>
		</c:forEach>
	</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>
<tr>
	<td class="labelCol">TOC Collection<td>
	<td>${book.tocCollectionName}</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>
<tr>
	<td class="labelCol">Root TOC GUID<td>
	<td>${book.rootTocGuid}</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>
<tr>
	<td class="labelCol">DOC Collection<td>
	<td>${book.docCollectionName}</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>
<tr>
	<td class="labelCol">NORT Domain<td>
	<td>${book.nortDomain}</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>
<tr>
	<td class="labelCol">NortFilter View<td>
	<td>${book.nortFilterView}</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>
<tr>
	<td class="labelCol">Content Type<td>
	<td>${book.contentType}</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>
<tr>
	<td class="labelCol">Content Subtype<td>
	<td>${book.contentSubtype}</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>
<tr>
	<td class="labelCol">ISBN<td>
	<td>${book.isbn}</td>
	<td class="labelCol">col3</td>
	<td>col4</td>
</tr>
</table>
</c:if>
