<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<div class="pageTitle">Restore Book Definition</div>
<c:choose>

<c:when test="${book == null}">  <%-- if no book definition was found for BookDefinitionId --%>
<div class="errorMessage"><b>Book definition was not found.</b></div><br/>
</c:when>

<c:otherwise>	<%-- found the book --%>
<div id="bookData">
	<div class="bookImage">
		<img alt="${book.proviewDisplayName}" src="<%= WebConstants.MVC_COVER_IMAGE %>?imageName=${ book.coverImage }">
	</div>
	<div class="titleData">
		<div><label>Title ID:</label> ${book.fullyQualifiedTitleId}</div>
		<div><label>ProView Display Name:</label> ${book.proviewDisplayName}</div>
		<div class="bookStatus"><label>Current Status:</label> ${book.bookStatus}</div>
	</div>
</div>
</c:otherwise>

</c:choose>