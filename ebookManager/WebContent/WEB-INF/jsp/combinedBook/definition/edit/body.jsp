<%@ page
        import="com.thomsonreuters.uscl.ereader.mgr.web.controller.combinedBookDefinition.CombinedBookDefinitionForm" %>
<%@ page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:choose>
    <c:when test="${combinedBookDefinitionForm != null}">
        <form:form modelAttribute="<%=CombinedBookDefinitionForm.FORM_NAME %>"
                   action="<%=WebConstants.MVC_COMBINED_BOOK_DEFINITION_EDIT%>">
            <div class="combined-book-definition-container">
                <jsp:include page="../common/crudForm.jsp"/>
            </div>
            <div class="buttons">
                <form:button id="confirm">Save</form:button>
                <button type="button" onclick="location.href='<%=WebConstants.MVC_COMBINED_BOOK_DEFINITION_VIEW%>?<%=WebConstants.KEY_ID%>=${combinedBookDefinitionForm.id}';">
                    Cancel
                </button>
            </div>
        </form:form>
    </c:when>
    <c:otherwise>
        No combined book definition found
    </c:otherwise>
</c:choose>
