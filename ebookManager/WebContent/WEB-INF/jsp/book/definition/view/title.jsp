<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<b>View eBook Definition</b><br/>
<br/>
<c:choose>

<c:when test="${book == null}">  <%-- if no book definition was found for the title ID --%>
<b style="color:red;">Book definition for Title ID &quot;${titleId}&quot; was not found.</b><br/>
</c:when>

<c:otherwise>	<%-- found the book --%>
Title ID: ${book.primaryKey.fullyQualifiedTitleId}<br/>
Name: ${book.bookName}<br/>
Definition Status: TODO
</c:otherwise>

</c:choose>