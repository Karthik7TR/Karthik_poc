<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsFilterForm"%>
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
			submitFilterForm('<%=PublishingStatsFilterForm.FilterCommand.SEARCH%>');
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
function submitFilterForm(command) {
	$("#filterCommand").val(command);  // Set the form hidden field value for the operation discriminator
	$("#<%=PublishingStatsFilterForm.FORM_NAME%>").submit();	// POST the HTML form
}
</script>

<div class="header">Filters</div>
<form:form action="<%=WebConstants.MVC_STATS_FILTER %>"
		   commandName="<%=PublishingStatsFilterForm.FORM_NAME%>" method="post">
	<form:hidden path="filterCommand"/>
	
	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=PublishingStatsFilterForm.FORM_NAME%>">
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
		<form:input path="proviewDisplayName" maxlength="4000"/>
	</div>	
	<div class="filterRow">	
		<label>Title ID:</label>
		<form:input path="titleId" maxlength="4000"/>
	</div>	
	<div class="filterRow">	
		<label>Book Definition ID:</label>
		<form:input path="bookDefinitionId" maxlength="19" onkeypress="return event.charCode == 37 || (event.charCode > 47 && event.charCode < 58)"/>
	</div>	
	<div class="filterRow">	
		<label>From Date:</label>
		<form:input id="datepickerFrom" path="fromDateString"/>
	</div>	
	<div class="filterRow">	
		<label>To Date:</label>
		<form:input id="datepickerTo" path="toDateString"/>
	</div>	
	
	<div class="wildCard">Wildcard: %</div>
	
	<input id="filterSearchButton" type="button" value="Search" onclick="submitFilterForm('<%=PublishingStatsFilterForm.FilterCommand.SEARCH%>')"/>
	<input type="button" value="Reset" onclick="submitFilterForm('<%=PublishingStatsFilterForm.FilterCommand.RESET%>')"/>
</form:form>

