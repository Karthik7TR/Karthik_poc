<!--
	Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListFilterForm"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

<html>
<head>
  	<script>
  	
  		$(document).ready(function() {
  			<%-- Submit the filter form when ENTER key is pressed from within any input field. --%> 
  			$("form input").keyup(function(event) {
  				if (event.keyCode == 13) {
  					submitFilterForm('<%=ProviewListFilterForm.FilterCommand.SEARCH%>');
  				}
  			});
  			
  			
  		});
		
		function submitFilterForm(command) {
			$("#filterCommand").val(command);  // Set the form hidden field value for the operation discriminator
			$("#<%=ProviewListFilterForm.FORM_NAME%>").submit();	// POST the HTML form
		}
		
	</script>
	
	
<div class="header">Filters</div>

	
<form:form action="<%=WebConstants.MVC_PROVIEW_LIST_FILTERED_POST%>"
			   commandName="<%=ProviewListFilterForm.FORM_NAME%>" name="theForm" method="post">
	<form:hidden path="filterCommand"/>
	
	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=ProviewListFilterForm.FORM_NAME%>">
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
	
	<div class="wildCard">Wildcard: %</div>
	
	<input type="button" value="Search" onclick="submitFilterForm('<%=ProviewListFilterForm.FilterCommand.SEARCH%>')"/>
	<input type="button" value="Reset" onclick="submitFilterForm('<%=ProviewListFilterForm.FilterCommand.RESET%>')"/>
	
	
	
</form:form>
