<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage.OutageForm"%>

<script type="text/javascript" src="js/shared/dateUtils.js"></script>
<script>
$(document).ready(function() {
	var timeOffset = 300000;
	
	var startDateTextBox = $('#startDatetimepicker');
	var endDateTextBox = $('#endDatetimepicker');
	if (startDateTextBox.val().length !== 0) {
		startDateTextBox.val(formatDateFromUTCString(startDateTextBox.val()));
	}
	if (endDateTextBox.val().length !== 0) {
		endDateTextBox.val(formatDateFromUTCString(endDateTextBox.val()));
	}
	
	<%-- Set up the timepicker TO and FROM date picker UI widget --%>
	const currentDate = new Date();
	$( "#startDatetimepicker" ).datetimepicker({
		showSecond: true,
		timeFormat: 'HH:mm:ss',
		defaultValue: formatDateFromUTCString(currentDate.toUTCString()),
		minDateTime: currentDate,
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
	        } else {
	        	endDateTextBox.datetimepicker('setDate',(new Date(offsetMilliseconds)));
	        }
	    }
	});
	const minEndDate = new Date(currentDate.getTime() + timeOffset);
	$( "#endDatetimepicker" ).datetimepicker({
		showSecond: true,
		timeFormat: 'HH:mm:ss',
		defaultValue: formatDateFromUTCString(minEndDate.toUTCString()),
		minDateTime: minEndDate,
		afterInject: function() {
			$(".ui-datepicker-current").hide();
		},
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
	        } else {
	        	startDateTextBox.datetimepicker('setDate',(new Date(offsetMilliseconds)));
	        }
	    }
	});
	$("#startDatetimepicker, #endDatetimepicker").attr('autocomplete','off');
	
	$("#save").click(function(event) {
		$(event.target).attr("disabled", true);
		var confirmation = true;
		var startDate = $('#startDatetimepicker').datetimepicker('getDate');
		if(startDate < new Date()) {
			confirmation = confirm("The planned outage will start immediately.  This will stop all currently running jobs.  Are you sure?");
		}
		
		if(confirmation) {
			validateDate("#startDatetimepicker");
			validateDate("#endDatetimepicker");
			$("#<%= OutageForm.FORM_NAME %>").submit();
		} else {
			$(event.target).attr("disabled", false);
		}
	});
	
	startDateTextBox.val(fixIEHiddenSymbols(startDateTextBox.val()));
	endDateTextBox.val(fixIEHiddenSymbols(endDateTextBox.val()));
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
