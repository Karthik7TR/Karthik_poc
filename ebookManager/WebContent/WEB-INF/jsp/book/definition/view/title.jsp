<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
.titleData {
	margin-top:10px;
	margin-left:10px;
	font-family:Arial;
}
</style>
<div class="pageTitle">View eBook Definition</div>
<c:choose>

<c:when test="${book == null}">  <%-- if no book definition was found for the title ID --%>
<b style="color:red;">Book definition for Title ID &quot;${titleId}&quot; was not found.</b><br/>
</c:when>

<c:otherwise>	<%-- found the book --%>
<div class="titleData">
Definition Status: TODO
</div>
</c:otherwise>

</c:choose>