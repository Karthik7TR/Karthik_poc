<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>

<c:choose>
    <c:when test="${combinedBookDefinition != null}">
        <div class="row">
            This will mark the combined book definition as deleted. You will not be able to edit, copy, or generate
            the combined book definition. A super user will be able to restore this combined book definition.
        </div>
        <div class="row">
            Are you sure you want to delete this combined book definition?
        </div>
        <div class="buttons">
            <button id="delete">Delete</button>
            <button onclick="location.href ='<%=WebConstants.MVC_COMBINED_BOOK_DEFINITION_VIEW%>?<%=WebConstants.KEY_ID%>=${combinedBookDefinition.id}';">
                Cancel
            </button>
        </div>
    </c:when>
    <c:otherwise>
        No Combined Book Definition found
    </c:otherwise>
</c:choose>
<jsp:include page="../../../common/dialog.jsp">
    <jsp:param name="title" value="Delete Combined Book Definition?"/>
    <jsp:param name="description" value="Are you sure you want to delete this combined book definition?"/>
    <jsp:param name="btnLabel" value="Delete"/>
    <jsp:param name="btnSelector" value="#delete"/>
    <jsp:param name="httpMethod" value="DELETE"/>
    <jsp:param name="url" value="${WebConstants.MVC_COMBINED_BOOK_DEFINITION_DELETE}?${WebConstants.KEY_ID}=${combinedBookDefinition.id}"/>
    <jsp:param name="redirectUrl" value="${WebConstants.MVC_COMBINED_BOOK_DEFINITION_VIEW}?${WebConstants.KEY_ID}=${combinedBookDefinition.id}"/>
    <jsp:param name="redirectUrlError" value="${WebConstants.MVC_COMBINED_BOOK_DEFINITION_DELETE_ERROR}?${WebConstants.KEY_ID}=${combinedBookDefinition.id}"/>
</jsp:include>