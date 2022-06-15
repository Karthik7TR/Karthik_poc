<!--
	Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->

<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitlesReportFilterForm"%>
<%@ page import="com.thomsonreuters.uscl.ereader.deliver.service.ProviewStatus" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

<script type="text/javascript" src="js/form-utils.js"></script>
<script type="text/javascript">
	$(document).ready(function () {
		<%-- Submit the filter form when ENTER key is pressed from within any input field. --%>
		$("form input").keyup(function (event) {
			if (event.keyCode == 13) {
				submitLeftFormAndBodyForm();
			}
		});
	});

	$(window).on('pageshow', function() {
		$('#proviewDisplayName').val('${ param.proviewDisplayName }');
		$('#titleId').val('${ param.titleId }');
		$('#minVersions').val('${ param.minVersions }');
		$('#maxVersions').val('${ param.maxVersions }');
	});
</script>

<div class="header">Filters</div>

<form:form
		id="leftForm"
		modelAttribute="<%=ProviewTitlesReportFilterForm.FORM_NAME%>"
		action="<%=WebConstants.MVC_PROVIEW_TITLES_REPORT%>"
		method="get">

	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=ProviewTitlesReportFilterForm.FORM_NAME%>">
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
		<label>Min Total versions:</label>
		<form:input path="minVersions"/>
	</div>
	<div class="filterRow">
		<label>Max Total versions:</label>
		<form:input path="maxVersions"/>
	</div>
	<div class="filterRow">
		<label>Status:</label>
		<form:select path="status">
			<form:option label="ALL" value=""/>
			<form:option label="<%=ProviewStatus.Review.name()%>" value="<%=ProviewStatus.Review.name() %>"/>
			<form:option label="<%=ProviewStatus.Final.name()%>" value="<%=ProviewStatus.Final.name() %>"/>
			<form:option label="<%=ProviewStatus.Removed.name()%>" value="<%=ProviewStatus.Removed.name() %>"/>
		</form:select>
	</div>
	
	<div class="wildCard">Wildcard: %</div>
	
	<input type="button" value="Search" onclick="submitLeftFormAndBodyForm()"/>
	<input type="button" value="Reset" onclick="submitEmptyLeftFormAndBodyForm()"/>
</form:form>
