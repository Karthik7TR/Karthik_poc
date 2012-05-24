<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.FilterForm"%>
<%@page import="org.springframework.batch.core.BatchStatus"%>
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
			submitJobSummaryFilterForm('<%=FilterForm.FilterCommand.SEARCH%>');
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
function submitJobSummaryFilterForm(command) {
	$("#filterCommand").val(command);  // Set the form hidden field value for the operation discriminator
	$("#<%=FilterForm.FORM_NAME%>").submit();	// POST the HTML form
}
</script>

<div class="header">Filters</div>
<form:form action="<%=WebConstants.MVC_JOB_SUMMARY_FILTER_POST%>"
		   commandName="<%=FilterForm.FORM_NAME%>" method="post">
	<form:hidden path="filterCommand"/>
	
	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=FilterForm.FORM_NAME%>">
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
		<label>ProView Display Name:</label>
		<form:input path="proviewDisplayName"/>
	</div>
	<div class="filterRow">
		<label>Title ID:</label>
		<form:input path="titleId"/>
	</div>
	<div class="filterRow">
		<label>Submitted By:</label>
		<form:input path="submittedBy"/>
	</div>
	<div class="filterRow">
		<label>From Date:</label>
		<form:input id="datepickerFrom" path="fromDateString"/>
	</div>
	<div class="filterRow">
		<label>To Date:</label>
		<form:input id="datepickerTo" path="toDateString"/>
	</div>
	<div class="filterRow">
		<label>Job Status:</label>
		<form:select path="batchStatus" multiple="true" size="7">
			<form:option label="<%=BatchStatus.STARTED.toString() %>" value="<%=BatchStatus.STARTED.toString() %>"/>
			<form:option label="<%=BatchStatus.FAILED.toString() %>" value="<%=BatchStatus.FAILED.toString() %>"/>
			<form:option label="<%=BatchStatus.COMPLETED.toString()%>" value="<%=BatchStatus.COMPLETED.toString() %>"/>
			<form:option label="<%=BatchStatus.STOPPED.toString() %>" value="<%=BatchStatus.STOPPED.toString() %>"/>
			<form:option label="<%=BatchStatus.STARTING.toString() %>" value="<%=BatchStatus.STARTING.toString() %>"/>
			<form:option label="<%=BatchStatus.STOPPING.toString() %>" value="<%=BatchStatus.STOPPING.toString() %>"/>
			<form:option label="<%=BatchStatus.ABANDONED.toString() %>" value="<%=BatchStatus.ABANDONED.toString() %>"/>
		</form:select>
	</div>
	
	<div class="wildCard">Wildcard: %</div>
	
	<input id="jobFilterSearchButton" type="button" value="Search" onclick="submitJobSummaryFilterForm('<%=FilterForm.FilterCommand.SEARCH%>')"/>
	<input type="button" value="Reset" onclick="submitJobSummaryFilterForm('<%=FilterForm.FilterCommand.RESET%>')"/>
</form:form>

