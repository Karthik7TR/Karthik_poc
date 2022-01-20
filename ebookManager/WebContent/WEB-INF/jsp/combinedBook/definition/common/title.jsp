<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>

<c:choose>
    <c:when test="${combinedBookDefinition == null}">
        <div class="errorMessage"><b>Combined book definition was not found.</b></div><br/>
    </c:when>
    <c:when test="${combinedBookDefinition.primaryTitle.bookDefinition == null}">
        <div class="errorMessage">Primary book definition was deleted.</div>
    </c:when>
    <c:otherwise>
        <c:set var="primaryTitle" value="${combinedBookDefinition.primaryTitle.bookDefinition}"/>
        <div id="bookData">
            <div class="bookImage">
                <img alt="${fn:escapeXml(primaryTitle.proviewDisplayName)}" src="<%= CoreConstants.MVC_COVER_IMAGE %>?imageName=${ primaryTitle.coverImage }">
            </div>
            <div class="titleData">
                <div><label>Title ID:</label> ${primaryTitle.fullyQualifiedTitleId}</div>
                <div><label>ProView Display Name:</label> ${fn:escapeXml(primaryTitle.proviewDisplayName)}</div>
                <div class="bookStatus"><label>Definition Status:</label> ${combinedBookDefinition.deletedFlag ? "Deleted": primaryTitle.bookStatus}</div>
            </div>
        </div>
    </c:otherwise>
</c:choose>
