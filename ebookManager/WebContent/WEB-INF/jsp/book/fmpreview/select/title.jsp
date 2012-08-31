<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<div class="pageTitle">Front Matter Preview</div>
<c:choose>

<c:when test="${book == null}">  <%-- if no book definition was found for BookDefinitionId --%>
<div class="errorMessage"><b>Book definition was not found.</b></div><br/>
</c:when>

<c:otherwise>	<%-- found the book --%>
<div id="bookData">
	<div class="bookImage">
		<img alt="${fn:escapeXml(book.proviewDisplayName)}" src="<%= WebConstants.MVC_COVER_IMAGE %>?imageName=${ book.coverImage }">
	</div>
	<div class="titleData">
		<div><label>Title ID: </label> <a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?id=${book.ebookDefinitionId}">${book.fullyQualifiedTitleId}</a></div>
		<div><label>ProView Display Name: </label> ${fn:escapeXml(book.proviewDisplayName)}</div>
		<div class="bookStatus"><label>Current Status: </label> ${book.bookStatus}</div>
	</div>
</div>
</c:otherwise>

</c:choose>