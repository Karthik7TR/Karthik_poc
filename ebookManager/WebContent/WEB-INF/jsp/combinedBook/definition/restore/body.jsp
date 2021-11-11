<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>

<c:choose>
    <c:when test="${combinedBookDefinition != null}">
        <div class="row">
            Are you sure you want to restore this combined book definition?
        </div>
        <div class="buttons">
            <button id="restore">Restore</button>
            <button onclick="location.href ='<%=WebConstants.MVC_COMBINED_BOOK_DEFINITION_VIEW%>?<%=WebConstants.KEY_ID%>=${combinedBookDefinition.id}';">
                Cancel
            </button>
        </div>
    </c:when>
    <c:otherwise>
        No Combined Book Definition found
    </c:otherwise>
</c:choose>
<jsp:include page="../common/dialog.jsp">
    <jsp:param name="title" value="Restore Combined Book Definition?"/>
    <jsp:param name="description" value="Are you sure you want to restore this combined book definition?"/>
    <jsp:param name="btnLabel" value="Restore"/>
    <jsp:param name="btnId" value="#restore"/>
    <jsp:param name="httpMethod" value="POST"/>
    <jsp:param name="url" value="${WebConstants.MVC_COMBINED_BOOK_DEFINITION_RESTORE}?${WebConstants.KEY_ID}=${combinedBookDefinition.id}"/>
    <jsp:param name="redirectUrl" value="${WebConstants.MVC_COMBINED_BOOK_DEFINITION_VIEW}?${WebConstants.KEY_ID}=${combinedBookDefinition.id}"/>
</jsp:include>