<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditFilterForm"%>
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
			submitAuditFilterForm('<%=BookAuditFilterForm.FilterCommand.SEARCH%>');
		}
	});
});

<%-- Set up the jQuery TO and FROM date picker UI widget --%>
$(function() {
	$("#datepickerFrom").datepicker();
	$("#datepickerTo").datepicker();
});

<%-- Submit the row multi-select form with the command being used to indicate which operation initiated the submit. --%>
function submitAuditFilterForm(command) {
	$("#filterCommand").val(command);  // Set the form hidden field value for the operation discriminator
	$("#<%=BookAuditFilterForm.FORM_NAME%>").submit();	// POST the HTML form
}
</script>

<div class="header">Filters</div>
<form:form action="<%=WebConstants.MVC_BOOK_AUDIT_LIST_FILTER_POST %>"
		   commandName="<%=BookAuditFilterForm.FORM_NAME%>" method="post">
	<form:hidden path="filterCommand"/>
	
	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=BookAuditFilterForm.FORM_NAME%>">
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
		<form:input path="bookName"/>
	</div>	
	<div class="filterRow">	
		<label>Title ID:</label>
		<form:input path="titleId"/>
	</div>	
	<div class="filterRow">	
		<label>Book Definition ID:</label>
		<form:input path="bookDefinitionId"/>
	</div>	
	<div class="filterRow">	
		<label>User Name:</label>
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
		<label>Action:</label>
		<form:select path="action">
			<form:option label="ALL" value=""/>
			<form:option label="<%=BookAuditFilterForm.Action.CREATE.toString()%>" value="<%=BookAuditFilterForm.Action.CREATE.toString() %>"/>
			<form:option label="<%=BookAuditFilterForm.Action.DELETE.toString()%>" value="<%=BookAuditFilterForm.Action.DELETE.toString() %>"/>
			<form:option label="<%=BookAuditFilterForm.Action.EDIT.toString()%>" value="<%=BookAuditFilterForm.Action.EDIT.toString() %>"/>
			<form:option label="<%=BookAuditFilterForm.Action.RESTORE.toString()%>" value="<%=BookAuditFilterForm.Action.RESTORE.toString() %>"/>
		</form:select>
	</div>
	
	<div class="wildCard">Wildcard: %</div>
	
	<input id="auditFilterSearchButton" type="button" value="Search" onclick="submitAuditFilterForm('<%=BookAuditFilterForm.FilterCommand.SEARCH%>')"/>
	<input type="button" value="Reset" onclick="submitAuditFilterForm('<%=BookAuditFilterForm.FilterCommand.RESET%>')"/>
</form:form>

