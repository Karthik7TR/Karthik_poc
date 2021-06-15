<%@ page
        import="com.thomsonreuters.uscl.ereader.mgr.web.controller.combinedBookDefinition.CombinedBookDefinitionForm" %>
<%@ page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<form:form modelAttribute="<%=CombinedBookDefinitionForm.FORM_NAME %>"
           action="<%=WebConstants.MVC_COMBINED_BOOK_DEFINITION_CREATE%>">
    <div class="combined-book-definition-container">
        <jsp:include page="../common/crudForm.jsp"/>
    </div>
    <div class="buttons">
        <form:button id="confirm">Save</form:button>
        <button type="button" onclick="location.href ='<%=WebConstants.MVC_BOOK_LIBRARY_LIST%>';">Cancel</button>
    </div>
</form:form>
