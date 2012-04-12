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
});

<%-- Set up the jQuery TO and FROM date picker UI widget --%>
$(function() {
	$("#datepickerFrom").datepicker();
	$("#datepickerTo").datepicker();
});

<%-- Submit the row multi-select form with the command being used to indicate which operation initiated the submit. --%>
function submitJobSummaryFilterForm(command) {
	$("#filterCommand").val(command);  // Set the form hidden field value for the operation discriminator
	$("#<%=FilterForm.FORM_NAME%>").submit();	// POST the HTML form
}
</script>

<b>Job Filters</b><br/>
<br/>

<form:form action="<%=WebConstants.MVC_JOB_SUMMARY_FILTER_POST%>"
		   commandName="<%=FilterForm.FORM_NAME%>" method="post">
	<form:hidden path="filterCommand"/>
	
	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=FilterForm.FORM_NAME%>">
		<div class="errorBox">
	      <b><spring:message code="please.fix.errors"/>:</b><br/>
	      <form:errors path="*">
			<br/>
			<c:forEach items="${messages}" var="message">
				<span>${message}</span>
				<br/>
			</c:forEach>
		  </form:errors>
		  <br/>
	    </div>
	    <br/>
    </spring:hasBindErrors>
		   
	Proview Display Name:<form:input path="bookName"/><br/>
	Title ID:<form:input path="titleId"/><br/>
	Submitted By:<form:input path="submittedBy"/><br/>
	From Date:<form:input id="datepickerFrom" path="fromDateString"/><br/>
	To Date:<form:input id="datepickerTo" path="toDateString"/><br/>
	Job Status:<br/>
	<form:select path="batchStatus">
		<form:option label="ALL" value=""/>
		<form:option label="<%=BatchStatus.COMPLETED.toString()%>" value="<%=BatchStatus.COMPLETED.toString() %>"/>
		<form:option label="<%=BatchStatus.FAILED.toString() %>" value="<%=BatchStatus.FAILED.toString() %>"/>
		<form:option label="<%=BatchStatus.STARTED.toString() %>" value="<%=BatchStatus.STARTED.toString() %>"/>
		<form:option label="<%=BatchStatus.STOPPED.toString() %>" value="<%=BatchStatus.STOPPED.toString() %>"/>
		<form:option label="<%=BatchStatus.ABANDONED.toString() %>" value="<%=BatchStatus.ABANDONED.toString() %>"/>
		<form:option label="<%=BatchStatus.STARTING.toString() %>" value="<%=BatchStatus.STARTING.toString() %>"/>
		<form:option label="<%=BatchStatus.STOPPING.toString() %>" value="<%=BatchStatus.STOPPING.toString() %>"/>
		<form:option label="<%=BatchStatus.UNKNOWN.toString() %>" value="<%=BatchStatus.UNKNOWN.toString() %>"/>
	</form:select>
	<br/>
	<br/>
	<input id="jobFilterSearchButton" type="button" value="Search" onclick="submitJobSummaryFilterForm('<%=FilterForm.FilterCommand.SEARCH%>')"/>
	<input type="button" value="Reset" onclick="submitJobSummaryFilterForm('<%=FilterForm.FilterCommand.RESET%>')"/>
</form:form>

