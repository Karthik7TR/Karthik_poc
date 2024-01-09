<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>

<c:set var="SOME_PARTS_HAVE_INCONSISTENT_STATUS_OR_ABSENT" value="<%=CoreConstants.ERROR_SIGN_SOME_PARTS_HAVE_INCONSISTENT_STATUS_OR_ABSENT %>"/>
<c:set var="SOME_PARTS_ARE_IN_FINAL_SOME_IN_REVIEW_STATE" value="<%=CoreConstants.ERROR_SIGN_SOME_PARTS_ARE_IN_FINAL_SOME_IN_REVIEW_STATE %>"/>
<div>
    Legend:
    <ul style="list-style: none">
        <li>
            <c:out value="${SOME_PARTS_ARE_IN_FINAL_SOME_IN_REVIEW_STATE}"/> - Some parts are in <b>Final</b> state and some are in <b>Review</b>.
        </li>
        <li>
            <c:out value="${SOME_PARTS_HAVE_INCONSISTENT_STATUS_OR_ABSENT}"/> - Some parts are absent or in <b>Removed</b> or <b>Cleanup</b> state.
        </li>
    </ul>
</div>
