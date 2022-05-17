<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>

<c:if test="${combinedBookDefinition != null}">
    <c:set var="readyToGenerate" value="${combinedBookDefinition.allBookDefinitionsExist()}"/>
    <c:set var="primaryBookExist" value="${combinedBookDefinition.primaryTitle.bookDefinition != null}"/>
    <c:set var="editBook" value="disabled"/>
    <sec:authorize access="hasAnyRole('ROLE_EDITOR, ROLE_PUBLISHER, ROLE_PUBLISHER_PLUS, ROLE_SUPERUSER')">
        <c:set var="editBook" value=""/>
    </sec:authorize>
    <c:set var="copyGenerateBook" value="disabled"/>
    <sec:authorize access="hasAnyRole('ROLE_PUBLISHER, ROLE_PUBLISHER_PLUS, ROLE_SUPERUSER')">
        <c:if test="${readyToGenerate}">
            <c:set var="copyGenerateBook" value=""/>
        </c:if>
    </sec:authorize>
    <c:set var="superUser" value="disabled"/>
    <sec:authorize access="hasRole('ROLE_SUPERUSER')">
        <c:set var="superUser" value=""/>
    </sec:authorize>
    <c:set var="editGroup" value="disabled"/>
    <sec:authorize access="hasAnyRole('ROLE_SUPERUSER, ROLE_PUBLISHER_PLUS')">
        <c:if test="${primaryBookExist}">
            <c:set var="editGroup" value=""/>
        </c:if>
    </sec:authorize>

    <div class="combined-book-definition-container">
        <display:table id="<%= WebConstants.KEY_VDO %>" name="combinedBookDefinition.sources" class="displayTagTable" cellpadding="2"
                       defaultsort="1"
                       defaultorder="ascending">
            <display:column title="Order">${vdo.sequenceNum + 1}</display:column>
            <display:column title="Source Type">${vdo.bookDefinition.sourceType}</display:column>
            <display:column title="Title ID"><a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${vdo.bookDefinition.ebookDefinitionId}">${vdo.bookDefinition.fullyQualifiedTitleId}</a></display:column>
            <display:column title="Primary Title">${ vdo.primarySource ? "&#10004;" : "" }</display:column>
            <display:column title="Deleted Book">${ vdo.bookDefinition.deletedFlag ? "&#10004;" : "" }</display:column>
        </display:table>
    </div>
    <div class="buttons">
        <c:choose>
            <c:when test="${combinedBookDefinition.deletedFlag}">
                <button type="button" ${superUser} onclick="location.href='<%=WebConstants.MVC_COMBINED_BOOK_DEFINITION_RESTORE%>?<%=WebConstants.KEY_ID%>=${combinedBookDefinition.id}'">Restore</button>
            </c:when>
            <c:otherwise>
                <button type="button" ${editBook} onclick="location.href='<%=WebConstants.MVC_COMBINED_BOOK_DEFINITION_EDIT%>?<%=WebConstants.KEY_ID%>=${combinedBookDefinition.id}'">Edit</button>
                <button type="button" onclick="alert('In development')">Copy</button>
                <c:choose>
                    <c:when test="${combinedBookDefinition.bookDefinitionDeletedFlag}">
                       <button type="button" disabled>Generate</button>
                    </c:when>
                    <c:otherwise>
                        <button type="button" ${copyGenerateBook} onclick="location.href='<%=WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW%>?<%=WebConstants.KEY_ID%>=${combinedBookDefinition.id}&<%=WebConstants.KEY_IS_COMBINED%>=true'">Generate</button>
                    </c:otherwise>
                </c:choose>
                <button type="button" ${editGroup} onclick="location.href='<%=WebConstants.MVC_GROUP_DEFINITION_EDIT%>?<%=WebConstants.KEY_ID%>=${combinedBookDefinition.id}&<%=WebConstants.KEY_IS_COMBINED%>=true'">Create/Edit Group</button>
                <button type="button" ${superUser} onclick="location.href='<%=WebConstants.MVC_COMBINED_BOOK_DEFINITION_DELETE%>?<%=WebConstants.KEY_ID%>=${combinedBookDefinition.id}'">Delete</button>
            </c:otherwise>
        </c:choose>
        <button type="button" onclick="alert('In development')">Audit Log</button>
        <button type="button" onclick="alert('In development')">Publishing Stats</button>
    </div>
</c:if>
