<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="pageTitle">View eBook Definition</div>
<c:choose>

<c:when test="${book == null}">  <%-- if no book definition was found for BookDefinitionId --%>
<div class="errorMessage"><b>Book definition was not found.</b></div><br/>
</c:when>

<c:otherwise>	<%-- found the book --%>
<div id="bookData">
	<div class="bookImage">
		<img alt="${book.proviewDisplayName}" src="theme/images/cover.png">
	</div>
	<div class="titleData">
		Title ID: ${book.fullyQualifiedTitleId}<br/>
		Name: ${book.proviewDisplayName}<br/>
		<div class="bookStatus">Current Status: ${book.bookStatus}</div>
	</div>
</div>
</c:otherwise>

</c:choose>