<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditFilterForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%--
	The filter form in the left side tile of the Active / Recent Jobs table.
 --%>
<script type="text/javascript" src="js/common-form.js"></script>
<script type="text/javascript" src="js/form-utils.js"></script>
<script>
$(document).ready(function() {
	<%-- Submit the filter form when ENTER key is pressed from within any input field. --%>
	$("form input").keyup(function(event) {
		if (event.keyCode == 13) {
        submitLeftFormAndBodyForm();
		}
	});
	<%-- Set up the timepicker TO and FROM date picker UI widget --%>
	$( "#datepickerFrom" ).datetimepicker({
		showSecond: true,
		timeFormat: 'HH:mm:ss'
	});
	$( "#datepickerTo" ).datetimepicker({
		hour: 23,
		minute: 59,
		second: 59,
		showSecond: true,
		timeFormat: 'HH:mm:ss'
	});
	$("#datepickerFrom, #datepickerTo").attr('autocomplete','off');
});
$(window).on('pageshow', function () {
    $('#proviewDisplayName').val('${ param.proviewDisplayName }');
    $('#titleId').val('${ param.titleId }');
    $('#bookDefinitionId').val('${ param.bookDefinitionId }');
    $('#submittedBy').val('${ param.submittedBy }');
    $('#datepickerFrom').val('${ param.fromDateString }');
    $('#datepickerTo').val('${ param.toDateString }');
    $('#action').val('${ param.action }');
});
</script>

<div class="header">Filters</div>
<form:form id="leftForm"
					 action="<%=WebConstants.MVC_BOOK_AUDIT_LIST %>"
					 modelAttribute="<%=BookAuditFilterForm.FORM_NAME%>"
					 method="get">
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
		<form:input path="proviewDisplayName" maxlength="4000"/>
	</div>	
	<div class="filterRow">	
		<label>Title ID:</label>
		<form:input path="titleId" maxlength="4000"/>
	</div>	
	<div class="filterRow">	
		<label>Book Definition ID:</label>
		<form:input path="bookDefinitionId" maxlength="19" onfocus="bookDefinitionIdField.addRestrictions(this)"/>
	</div>	
	<div class="filterRow">	
		<label>User ID:</label>
		<form:input path="submittedBy" maxlength="4000"/>
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
	
	<input type="button" value="Search" onclick="submitLeftFormAndBodyForm()"/>
	<input type="button" value="Reset" onclick="submitEmptyLeftFormAndBodyForm()"/>
</form:form>
