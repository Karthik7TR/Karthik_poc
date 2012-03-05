<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="pageTitle">Generate eBook</div>
<c:choose>

<c:when test="${book == null}">  <%-- if no book definition was found for the title ID --%>
<div class="errorMessage"><b>Book definition for Title ID &quot;${titleId}&quot; was not found.</b></div><br/>
</c:when>

<c:otherwise>	<%-- found the book --%>
<div id="bookData">
	<div class="bookImage">
		<img alt="${book.bookName}" src="theme/images/cover.png">
	</div>
	<div class="titleData">
		Title ID: ${book.primaryKey.fullyQualifiedTitleId}<br/>
		Name: ${book.bookName}<br/>
	</div>
</div>
</c:otherwise>

</c:choose>