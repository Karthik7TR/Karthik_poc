<!--
	Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->

<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm"%>
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
  					submitFilterForm('<%=ProviewGroupListFilterForm.FilterCommand.SEARCH%>');
  				}
  			});
  			
  			
  		});
		
		function submitFilterForm(command) {
			$("#filterCommand").val(command);  // Set the form hidden field value for the operation discriminator
			$("#<%=ProviewGroupListFilterForm.FORM_NAME%>").submit();	// POST the HTML form
		}
		
	</script>
	
	
<div class="header">Filters</div>

	
<form:form action="<%=WebConstants.MVC_PROVIEW_GROUP_LIST_FILTERED_POST%>"
			   commandName="<%=ProviewGroupListFilterForm.FORM_NAME%>" name="theForm" method="post">
	<form:hidden path="filterCommand"/>
	
	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=ProviewGroupListFilterForm.FORM_NAME%>">
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
		<form:input path="groupName"/>
	</div>
	<div class="filterRow">
		<label>Group ID:</label>
		<form:input path="proviewGroupID"/>
	</div>
	
	<div class="wildCard">Wildcard: %</div>
	
	<input id="searchButton" type="button" value="Search" onclick="submitFilterForm('<%=ProviewGroupListFilterForm.FilterCommand.SEARCH%>')"/>
	<input id="resetButton" type="button" value="Reset" onclick="submitFilterForm('<%=ProviewGroupListFilterForm.FilterCommand.RESET%>')"/>
	
	
	
</form:form>
