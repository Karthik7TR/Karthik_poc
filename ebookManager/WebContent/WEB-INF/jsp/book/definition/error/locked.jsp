<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ page import="java.util.*"%>

<%-- Check if there is a book model to render, if not don't display a bunch of unvalued labels. --%>
<c:choose>
	<c:when test="${book != null}">
		<div>Book Definition is currently being edited by ${ bookDefinitionLock.fullName } (${ bookDefinitionLock.username }).</div>
		
		<div>Book Definition has been locked since <fmt:formatDate value="${bookDefinitionLock.checkoutTimestamp}" type="both" /> server time. </div>
		<% 	Date date = new java.util.Date();
			pageContext.setAttribute("date", date); %>
		<c:set var="currentDateTime" value="${date}" />
		<div>Current server time is <fmt:formatDate value="${ currentDateTime }" type="both" /></div>
		
	</c:when>
	<c:otherwise>
	No book definition found
	</c:otherwise>
</c:choose>


