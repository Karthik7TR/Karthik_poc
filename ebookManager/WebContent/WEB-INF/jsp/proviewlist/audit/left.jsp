<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditFilterForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%--
	The filter form in the left side tile of the Active / Recent Jobs table.
 --%>
<script>
$(document).ready(function() {
	<%-- Submit the filter form when ENTER key is pressed from within any input field. --%> 
	$("form input").keyup(function(event) {
		if (event.keyCode == 13) {
			submitAuditFilterForm('<%=ProviewAuditFilterForm.FilterCommand.SEARCH%>');
		}
	});
	
	<%-- Set up the timepicker TO and FROM date picker UI widget --%>
	$( "#datepickerFrom" ).datetimepicker({
		showSecond: true,
		timeFormat: 'hh:mm:ss'
	});
	$( "#datepickerTo" ).datetimepicker({
		hour: 23,
		minute: 59,
		second: 59,
		showSecond: true,
		timeFormat: 'hh:mm:ss'
	});
});

<%-- Submit the row multi-select form with the command being used to indicate which operation initiated the submit. --%>
function submitAuditFilterForm(command) {
	$("#filterCommand").val(command);  // Set the form hidden field value for the operation discriminator
	$("#<%=ProviewAuditFilterForm.FORM_NAME%>").submit();	// POST the HTML form
}
</script>

<div class="header">Filters</div>
<form:form action="<%=WebConstants.MVC_PROVIEW_AUDIT_LIST_FILTER_POST %>"
		   commandName="<%=ProviewAuditFilterForm.FORM_NAME%>" method="post">
	<form:hidden path="filterCommand"/>
	
	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=ProviewAuditFilterForm.FORM_NAME%>">
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
	
	<div class="filterRow">	
		<label>Title ID:</label>
		<form:input path="titleId"/>
	</div>	
	<div class="filterRow">	
		<label>User Name:</label>
		<form:input path="username"/>
	</div>	
	<div class="filterRow">	
		<label>From Date:</label>
		<form:input id="datepickerFrom" path="requestFromDateString"/>
	</div>	
	<div class="filterRow">	
		<label>To Date:</label>
		<form:input id="datepickerTo" path="requestToDateString"/>
	</div>	
	<div class="filterRow">	
		<label>Action:</label>
		<form:select path="action">
			<form:option label="ALL" value=""/>
			<form:option label="<%=ProviewAuditFilterForm.Action.DELETE.toString()%>" value="<%=ProviewAuditFilterForm.Action.DELETE.toString() %>"/>
			<form:option label="<%=ProviewAuditFilterForm.Action.PROMOTE.toString()%>" value="<%=ProviewAuditFilterForm.Action.PROMOTE.toString() %>"/>
			<form:option label="<%=ProviewAuditFilterForm.Action.REMOVE.toString()%>" value="<%=ProviewAuditFilterForm.Action.REMOVE.toString() %>"/>
		</form:select>
	</div>
	
	<div class="wildCard">Wildcard: %</div>
	
	<input id="auditFilterSearchButton" type="button" value="Search" onclick="submitAuditFilterForm('<%=ProviewAuditFilterForm.FilterCommand.SEARCH%>')"/>
	<input type="button" value="Reset" onclick="submitAuditFilterForm('<%=ProviewAuditFilterForm.FilterCommand.RESET%>')"/>
</form:form>

