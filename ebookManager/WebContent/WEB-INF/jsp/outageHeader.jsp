<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>

<c:if test="${fn:length(displayOutage) gt 0 }">
	<c:if test="${ dismissOutage != true }">
		<script type="text/javascript">
		$(document).ready(function() {
			$('#dismiss').click(function () {
				$.post("<%= WebConstants.MVC_DISMISS_OUTAGE %>", function() {
						$("#outageMessageBox").slideUp("slow");
					});
			});
		});
		</script>
		<c:set var="DATE_FORMAT" value="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>
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
			<button id="dismiss">Dismiss</button>
		</div>
	</c:if>
</c:if>