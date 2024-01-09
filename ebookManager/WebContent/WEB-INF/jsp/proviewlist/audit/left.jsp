<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditFilterForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script type="text/javascript" src="js/form-utils.js"></script>
<script type="text/javascript">
	$(document).ready(function () {
		<%-- Submit the filter form when ENTER key is pressed from within any input field. --%>
		$("form input").keyup(function (event) {
			if (event.keyCode == 13) {
				submitLeftFormAndBodyForm();
			}
		});
		<%-- Set up the timepicker TO and FROM date picker UI widget --%>
		$("#datepickerFrom").datetimepicker({
			showSecond: true,
			timeFormat: 'HH:mm:ss'
		});
		$("#datepickerTo").datetimepicker({
			hour: 23,
			minute: 59,
			second: 59,
			showSecond: true,
			timeFormat: 'HH:mm:ss'
		});
		$("#datepickerFrom, #datepickerTo").attr('autocomplete', 'off');
	});
	$(window).on('pageshow', function () {
		$('#titleId').val('${ param.titleId }');
		$('#username').val('${ param.username }');
		$('#datepickerFrom').val('${ param.requestFromDateString }');
		$('#datepickerTo').val('${ param.requestToDateString }');
		$('#action').val('${ param.action }');
	});
</script>

<div class="header">Filters</div>
<form:form id="leftForm"
					 modelAttribute="<%=ProviewAuditFilterForm.FORM_NAME%>"
					 action="<%=WebConstants.MVC_PROVIEW_AUDIT_LIST%>"
					 method="get">
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
		<form:input path="titleId" maxlength="4000"/>
	</div>	
	<div class="filterRow">	
		<label>User Name:</label>
		<form:input path="username" maxlength="4000"/>
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
	<input type="button" value="Search" onclick="submitLeftFormAndBodyForm()"/>
	<input type="button" value="Reset" onclick="submitEmptyLeftFormAndBodyForm()"/>
</form:form>
