<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="pageTitle">eBook Job History Metrics</div>
<c:choose>

<c:when test="${book == null}">  
<div class="errorMessage"><b>Book definition was not found.</b></div><br/>
</c:when>

<c:otherwise>	<%-- found the book --%>
<div id="bookData">
	<div class="bookImage">
		<img alt="${book.proviewDisplayName}" src="theme/images/cover.png">
	</div>
	<div class="titleData">
		Title ID: ${book.titleId}<br/>
		Name: ${book.proviewDisplayName}<br/>
	</div>
</div>
</c:otherwise>

</c:choose>