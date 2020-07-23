<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage.OutageForm"%>
<script type="text/javascript" src="js/shared/dateUtils.js"></script>
<script>
$(document).ready(function(){
	formatDates();
})
</script>
<%-- Check if there is a model to render, if not display error message --%>
<c:choose>
	<c:when test="${outage != null}">
		<form:form commandName="<%= OutageForm.FORM_NAME %>">
			<%-- Validation Error Message Presentation (if any) --%>
			<spring:hasBindErrors name="<%=OutageForm.FORM_NAME%>">
				<div class="errorBox">
			      <b><spring:message code="please.fix.errors"/>:</b><br/>
			      <form:errors path="*">
			      	<ul>
					<c:forEach items="${messages}" var="message">
						<li style="color: black">${message}</li>
					</c:forEach>
			      	</ul>
				  </form:errors>
				  <br/>
			    </div>
		    </spring:hasBindErrors>
		    
		    <%-- Informational messages - used to report status of job stop and/or restart operations. --%>
		    <c:if test="${fn:length(infoMessages) > 0}">
			 	<ul>
			 	<c:forEach items="${infoMessages}" var="message">
			 		<c:if test="${message.type == 'SUCCESS'}">
			 			<c:set var="cssStyle" value="color:darkgreen;"/>
			 		</c:if>
			 		 <c:if test="${message.type == 'FAIL' || message.type == 'ERROR'}">
			 			<c:set var="cssStyle" value="color:red;"/>
			 		</c:if>
					<li style="${cssStyle}">${message.text}</li>
				</c:forEach>
			 	</ul>
		    </c:if>	 
		    
			<form:hidden path="plannedOutageId" />
			<form:hidden path="startTimeString" />
			<form:hidden path="endTimeString" />
			Are you sure you want to delete this outage: 
			<div>
				Start Date/Time: <span class="toFormatDate"> ${ outage.startTime.toInstant() }</span>
			</div>
			<div>
				End Date/Time: <span class="toFormatDate"> ${ outage.endTime.toInstant() }</span>
			</div>
			<div class="buttons">
				<form:button id="delete">Delete</form:button>
				<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_OUTAGE_ACTIVE_LIST%>';">Cancel</button>
			</div>
		</form:form>
	</c:when>
	<c:otherwise>
		<div class="errorMessage">No outage found</div>
	</c:otherwise>
</c:choose>
