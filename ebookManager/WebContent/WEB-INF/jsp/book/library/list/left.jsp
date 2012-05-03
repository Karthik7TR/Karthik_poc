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
  	
  		$(document).ready(function() {
  			<%-- Submit the filter form when ENTER key is pressed from within any input field. --%> 
  			$("form input").keyup(function(event) {
  				if (event.keyCode == 13) {
  					submitFilterForm('<%=BookLibraryFilterForm.FilterCommand.SEARCH%>');
  				}
  			});
  		});
  	
		$(function() {
			$( "#datepickerFrom" ).datepicker();
			$( "#datepickerTo" ).datepicker();
		});
		
		function submitFilterForm(command) {
			$("#filterCommand").val(command);  // Set the form hidden field value for the operation discriminator
			$("#<%=BookLibraryFilterForm.FORM_NAME%>").submit();	// POST the HTML form
		}
		
	</script>

<div class="header">Filters</div>
 	
<form:form action="<%=WebConstants.MVC_BOOK_LIBRARY_FILTERED_POST%>"
			   commandName="<%=BookLibraryFilterForm.FORM_NAME%>" method="post">
	<form:hidden path="filterCommand"/>
	
	
	ProView Display Name:<form:input path="proviewDisplayName"/><br>
	Title ID:<form:input path="titleId"/><br>
	ISBN:<form:input path="isbn"/><br>
	Material ID:<form:input path="materialId"/><br>
	<label>Last Edit Date:</label><br>
	From:<form:input id="datepickerFrom" path="fromString"/>
	To:<form:input id="datepickerTo" path="toString"/><br>
	Definition Status:<br>
	&nbsp;&nbsp;<form:radiobutton path="bookStatus" value="<%=BookLibraryFilterForm.BookDefStatus.ALL%>"/>All<br>
	&nbsp;&nbsp;<form:radiobutton path="bookStatus" value="<%=BookLibraryFilterForm.BookDefStatus.COMPLETE%>"/>Complete<br>
	&nbsp;&nbsp;<form:radiobutton path="bookStatus" value="<%=BookLibraryFilterForm.BookDefStatus.INCOMPLETE%>"/>InComplete<br>
			 
	
	<p><i style="color:grey">Wildcard: %</i></p>

	<input type="button" value="Search" onclick="submitFilterForm('<%=BookLibraryFilterForm.FilterCommand.SEARCH%>')"/>
	<input type="button" value="Reset" onclick="submitFilterForm('<%=BookLibraryFilterForm.FilterCommand.RESET%>')"/>
	
	
	
</form:form>
