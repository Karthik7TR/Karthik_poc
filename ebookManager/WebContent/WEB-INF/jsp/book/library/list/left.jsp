<!--
	Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryFilterForm"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>


  	<script>
		$(function() {
			$( "#datepickerFrom" ).datepicker();
			$( "#datepickerTo" ).datepicker();
		});
		
		function submitFilterForm(command) {
			$("#filterCommand").val(command);  // Set the form hidden field value for the operation discriminator
			$("#<%=BookLibraryFilterForm.FORM_NAME%>").submit();	// POST the HTML form
		}
		
	</script>

 	<h2>Filters</h2>

<form:form action="<%=WebConstants.MVC_BOOK_LIBRARY_FILTERED_POST%>"
			   commandName="<%=BookLibraryFilterForm.FORM_NAME%>" method="post">
	<form:hidden path="filterCommand"/>
	
	
	&nbsp;<form:label path="proviewDisplayName">Proview Display Name:</form:label><br>
	&nbsp;<form:input path="proviewDisplayName"/><br>
	&nbsp;<form:label path="titleId">Title ID:</form:label><br>
	&nbsp;<form:input path="titleId"/><br>
	&nbsp;<form:label path="isbn">ISBN:</form:label><br>
	&nbsp;<form:input path="isbn"/><br>
	&nbsp;<form:label path="materialId">Material ID:</form:label><br>
	&nbsp;<form:input path="materialId"/><br>
	&nbsp;<label>Last Edit Date:</label><br>
	&nbsp;&nbsp;<form:label path="from">From</form:label>
	&nbsp;&nbsp;&nbsp;<form:input id="datepickerFrom" path="fromString"/>
	&nbsp;&nbsp;<form:label path="to">To</form:label>
	&nbsp;&nbsp;&nbsp;<form:input id="datepickerTo" path="toString"/><br>
	&nbsp;Definition Status:<br>
	&nbsp;&nbsp;<form:radiobutton path="bookStatus" value="<%=BookLibraryFilterForm.BookDefStatus.ALL%>"/>All<br>
	&nbsp;&nbsp;<form:radiobutton path="bookStatus" value="<%=BookLibraryFilterForm.BookDefStatus.COMPLETE%>"/>Complete<br>
	&nbsp;&nbsp;<form:radiobutton path="bookStatus" value="<%=BookLibraryFilterForm.BookDefStatus.INCOMPLETE%>"/>InComplete<br>
			 
	
	
	<br><br>
	<input type="button" value="Search" onclick="submitFilterForm('<%=BookLibraryFilterForm.FilterCommand.SEARCH%>')"/>
	<input type="button" value="Reset" onclick="submitFilterForm('<%=BookLibraryFilterForm.FilterCommand.RESET%>')"/>
	
</form:form>
