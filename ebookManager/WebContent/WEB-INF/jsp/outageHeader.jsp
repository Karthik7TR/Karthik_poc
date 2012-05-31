<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<c:if test="${fn:length(displayOutage) gt 0 }">
	<c:set var="DATE_FORMAT" value="<%= WebConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	<div id="outageMessageBox">
		<div id="outageMessageHeader">The following schedule indicates an outage for the eBook Generator.</div>
		<c:forEach items="${displayOutage}" var="plannedOutage" varStatus="status">
			<div class="outageMessage">
				Start: <fmt:formatDate value="${plannedOutage.startTime}" pattern="${DATE_FORMAT}"/> 
				End: <fmt:formatDate value="${plannedOutage.endTime}" pattern="${DATE_FORMAT}"/> 
				<c:if test="${plannedOutage.systemImpactDescription != null}">
					Reason: ${plannedOutage.systemImpactDescription}
				</c:if>
			</div>
		</c:forEach>
	</div>
</c:if>