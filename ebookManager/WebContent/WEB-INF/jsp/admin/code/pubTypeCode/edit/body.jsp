<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.publishtypecode.PublishTypeCodeForm"%>

<%-- Check if there is a model to render, if not display error message --%>
<c:choose>
	<c:when test="${pubTypeCode != null}">
		<jsp:include page="../common/createEditForm.jsp" />
	</c:when>
	<c:otherwise>
		<div class="errorMessage">No Publish Type Code found</div>
	</c:otherwise>
</c:choose>
