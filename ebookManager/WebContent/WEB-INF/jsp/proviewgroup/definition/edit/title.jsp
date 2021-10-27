<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>

<div class="pageTitle">Create/Edit Group Definition</div>
<c:choose>

<c:when test="${book == null}"> 
 <%-- if no book definition was found for BookDefinitionId --%>
</c:when>
<c:when test="${allProviewTitleInfo == 0}">  
 <%-- if no titles are found on ProView --%>
</c:when>
<c:otherwise>	<%-- found the book --%>
<div id="bookData">
	<div class="bookImage">
		<img alt="${fn:escapeXml(book.proviewDisplayName)}" src="<%= CoreConstants.MVC_COVER_IMAGE %>?imageName=${ book.coverImage }">
	</div>
	<div class="titleData">
		<div><label>Title ID:</label> ${book.fullyQualifiedTitleId}</div>
		<div><label>ProView Display Name:</label> ${fn:escapeXml(book.proviewDisplayName)}</div>
	</div>
</div>
</c:otherwise>

</c:choose>