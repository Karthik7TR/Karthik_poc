<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="display" uri="http://displaytag.sf.net/el" %>

<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>

<c:if test="${combinedBookDefinition != null}">
    <div class="combined-book-definition-container">
        <display:table id="<%= WebConstants.KEY_VDO %>" name="combinedBookDefinition.sources" class="displayTagTable" cellpadding="2"
                       defaultsort="1"
                       defaultorder="ascending">
            <display:column title="Order">${vdo.sequenceNum + 1}</display:column>
            <display:column title="Source Type">${vdo.bookDefinition.sourceType}</display:column>
            <display:column title="Title ID">${vdo.bookDefinition.fullyQualifiedTitleId}</display:column>
            <display:column title="Primary Title">${ vdo.primarySource ? "&#10004;" : "" }</display:column>
        </display:table>
    </div>
    <div class="buttons">
        <button type="button" onclick="location.href='<%=WebConstants.MVC_COMBINED_BOOK_DEFINITION_EDIT%>?<%=WebConstants.KEY_ID%>=${combinedBookDefinition.id}';">Edit</button>
        <button type="button" onclick="alert('In development')">Copy</button>
        <button type="button" onclick="alert('In development')">Generate</button>
        <button type="button" onclick="alert('In development')">Delete</button>
        <button type="button" onclick="alert('In development')">Audit Log</button>
        <button type="button" onclick="alert('In development')">Publishing Stats</button>
    </div>
</c:if>
