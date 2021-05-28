<!--
	Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->

<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupForm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

<script type="text/javascript" src="js/form-utils.js"></script>
<script type="text/javascript">

	$(document).ready(function () {
		<%-- Submit forms when ENTER key is pressed from within any input field. --%>
		$("form input").keyup(function (event) {
			if (event.keyCode == 13) {
				submitLeftFormAndBodyForm();
			}
		});
	});

	$(window).on('pageshow', function() {
		$('#groupFilterName').val('${ param.groupFilterName }');
		$('#groupFilterId').val('${ param.groupFilterId }');
	});

</script>

<div class="header">Filters</div>

<form:form
		id="leftForm"
		modelAttribute="<%=ProviewGroupForm.FORM_NAME%>"
		action="<%=WebConstants.MVC_PROVIEW_GROUPS%>"
		method="get">

	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=ProviewGroupForm.FORM_NAME%>">
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
		<label>Group Name:</label>
		<form:input path="groupFilterName"/>
	</div>
	<div class="filterRow">
		<label>Group ID:</label>
		<form:input path="groupFilterId"/>
	</div>
	
	<div class="wildCard">Wildcard: %</div>
	
	<input id="searchButton" type="button" value="Search" onclick="submitLeftFormAndBodyForm()"/>
	<input id="resetButton" type="button" value="Reset" onclick="submitEmptyLeftFormAndBodyForm()"/>

</form:form>
