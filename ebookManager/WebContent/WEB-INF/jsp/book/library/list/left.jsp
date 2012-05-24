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
	<!--[if lt IE 9]>
	<script>
	$(function() {
		var selectElement;
		
		$("select.autoSizeSelect")
		  .each(function() {
		    selectElement = $(this);
		    selectElement.data("origWidth", selectElement.outerWidth()) 
		  })
		  .bind("focusin", function(){
		    $(this).css("width", "auto");
		  })
		  .bind("blur", function(){
		    selectElement = $(this);
		    selectElement.css("width", selectElement.data("origWidth"));
		 });
	});
	</script>
	<![endif]-->
<div class="header">Filters</div>
 	
<form:form action="<%=WebConstants.MVC_BOOK_LIBRARY_FILTERED_POST%>"
			   commandName="<%=BookLibraryFilterForm.FORM_NAME%>" method="post">
	<form:hidden path="filterCommand"/>
	
	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=BookLibraryFilterForm.FORM_NAME%>">
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
		<label>ISBN:</label>
		<form:input path="isbn"/>
	</div>
	<div class="filterRow">
		<label>Sub Material Number:</label>
		<form:input path="materialId"/>
	</div>
	<div class="filterRow">
		<label>Last Edit From Date:</label>
		<form:input id="datepickerFrom" path="fromString"/>
	</div>
	<div class="filterRow">
		<label>Last Edit To Date:</label>
		<form:input id="datepickerTo" path="toString"/>
	</div>
	<div class="filterRow">
		<label>Definition Status:</label>
		<form:select path="action">
			<form:option label="ALL" value=""/>
			<form:option label="<%=BookLibraryFilterForm.Action.READY.toString()%>" value="<%=BookLibraryFilterForm.Action.READY.toString() %>"/>
			<form:option label="<%=BookLibraryFilterForm.Action.INCOMPLETE.toString()%>" value="<%=BookLibraryFilterForm.Action.INCOMPLETE.toString() %>"/>
			<form:option label="<%=BookLibraryFilterForm.Action.DELETED.toString()%>" value="<%=BookLibraryFilterForm.Action.DELETED.toString() %>"/>
		</form:select>
	</div>
	
	<div class="filterRow">
		<label>ProView Keyword:</label>
		<form:select class="autoSizeSelect" path="proviewKeyword" >
			<form:option path="proviewKeyword" value="">None</form:option>
			<c:forEach items="${keywordTypeCode}" var="keyword" varStatus="keywordStatus">
				<form:option  path="proviewKeyword" value="" disabled="true">${keyword.name}</form:option>
				<c:forEach items="${keyword.values}" var="keywordValue">
						<form:option path="proviewKeyword" value="${keywordValue.id}">&nbsp&nbsp&nbsp ${ keywordValue.name }</form:option>
				</c:forEach>
			</c:forEach>
		</form:select>
	</div>
	
	<div class="wildCard">Wildcard: %</div>

	<input type="button" value="Search" onclick="submitFilterForm('<%=BookLibraryFilterForm.FilterCommand.SEARCH%>')"/>
	<input type="button" value="Reset" onclick="submitFilterForm('<%=BookLibraryFilterForm.FilterCommand.RESET%>')"/>
	
	
	
</form:form>
