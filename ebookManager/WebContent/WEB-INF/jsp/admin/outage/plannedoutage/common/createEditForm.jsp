<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage.OutageForm"%>

<script>
$(document).ready(function() {
	var timeOffset = 300000; // in milliseconds.
	var timeOffset2 = 7200000;
	
	<%-- Set up the timepicker TO and FROM date picker UI widget --%>
	$( "#startDatetimepicker" ).datetimepicker({
		showSecond: true,
		timeFormat: 'hh:mm:ss',
		minDateTime: new Date(),
		onClose: function(dateText, inst) {
			var milliseconds = new Date(dateText).getTime();
			var offsetMilliseconds = milliseconds + timeOffset;
			
	        var endDateTextBox = $('#endDatetimepicker');
	        if (endDateTextBox.val() != '') {
	            var offsetStartDate = new Date(offsetMilliseconds);
	            var testEndDate = new Date(endDateTextBox.val());
	            
	            <%-- Change end date/time if it is within the offset time --%>
	            if (offsetStartDate > testEndDate) {
	            	endDateTextBox.datetimepicker('setDate',(new Date(offsetMilliseconds)));
	            }
	        }
	        else {
	        	// Default set to 2 hours after start date/time if end date is not populated
	            endDateTextBox.datetimepicker('setDate',(new Date(milliseconds + timeOffset2)));
	        }
	    }
	});
	
	$( "#endDatetimepicker" ).datetimepicker({
		showSecond: true,
		timeFormat: 'hh:mm:ss',
		minDateTime: new Date(new Date().getTime() + timeOffset),
		onClose: function(dateText, inst) {
			var milliseconds = new Date(dateText).getTime();
			var offsetMilliseconds = milliseconds - timeOffset;
			
	        var startDateTextBox = $('#startDatetimepicker');
	        if (startDateTextBox.val() != '') {
	            var testStartDate = new Date(startDateTextBox.val());
	            var offsetEndDate = new Date(offsetMilliseconds);
	            
	            <%-- Change start date/time if it is within the offset time --%>
	            if (testStartDate > offsetEndDate) {
	            	startDateTextBox.datetimepicker('setDate',(new Date(offsetMilliseconds)));
	            }
	        }
	        else {
	        	// Default set to 2 hours before end date/time if start date is not populated
	        	startDateTextBox.datetimepicker('setDate',(new Date(milliseconds - timeOffset2)));
	        }
	    }
	});
	
	$("#save").click(function() {
		var confirmation = true;
		var startDate = $('#startDatetimepicker').datetimepicker('getDate');
		if(startDate < new Date()) {
			confirmation = confirm("The planned outage will start immediately.  This will stop all currently running jobs.  Are you sure?");
		}
		
		if(confirmation) {
			$("#<%= OutageForm.FORM_NAME %>").submit();
		}
	});
});
</script>
<form:form id="<%= OutageForm.FORM_NAME %>" commandName="<%= OutageForm.FORM_NAME %>">
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
    
    <%-- Informational messages --%>
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
	<div class="row">
		<form:label path="outageTypeId">Outage Type</form:label>
		<form:select path="outageTypeId">
			<form:option value="">SELECT</form:option>
			<c:forEach items="${outageType}" var="type">
				<form:option value="${type.id}">System: ${type.system} Sub-system: ${type.subSystem}</form:option>
			</c:forEach>
		</form:select>
	</div>
	<div class="row">
		<form:label path="startTimeString">Start Date/Time</form:label>
		<form:input id="startDatetimepicker" path="startTimeString"/>
	</div>
	<div class="row">
		<form:label path="endTimeString">End Date/Time</form:label>
		<form:input id="endDatetimepicker" path="endTimeString"/>
	</div>
	<div class="row">
		<form:label path="reason">Reason</form:label>
		<form:textarea path="reason"/>
	</div>
	<div class="row">
		<form:label path="systemImpactDescription">System Impact Description: shown to users (Optional)</form:label>
		<form:textarea path="systemImpactDescription"/>
	</div>
	<div class="row">
		<form:label path="serversImpacted">Servers Impacted (Optional)</form:label>
		<form:input path="serversImpacted"/>
	</div>
	<div class="buttons">
		<form:button type="button" id="save">Save</form:button>
		<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_OUTAGE_ACTIVE_LIST%>';">Cancel</button>
	</div>
</form:form>
