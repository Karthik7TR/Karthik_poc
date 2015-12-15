<!--
	Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list.GroupListFilterForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

<html>
<head>
 	<script type="text/javascript">
		
 		function submitForm(){
 			document.getElementById('submitStatus').innerHTML = "ProView request submitted... waiting for response."; 
			$('#<%=GroupListFilterForm.FORM_NAME%>').submit();
  			return true; 
  		}
		
 		 function submitPromote(){
 			 var confirmed = confirm("Are you sure to promote this title version to Final?");
 			 if (confirmed){
 				 submitForm();
 			 }
 			 
 			 return confirmed;
 		 }
		
		
	</script>

<body>
  
 
	<form:form action="<%=WebConstants.MVC_PROVIEW_GROUP_PROMOTE%>"
			   commandName="<%=GroupListFilterForm.FORM_NAME%>" name="theForm" method="post">
	
		<table>		   
		<tr>
			<td id="groupName"><b>Group Name:</b></td> 
			<td>&nbsp;&nbsp;&nbsp;${groupName}</td>
		</tr>
		<tr>
			<td id="status"><b>Group Status:</b></td> 
			<td>&nbsp;&nbsp;&nbsp;${groupStatus}</td>
		</tr>
		<tr>
			<td><b>Comments (Optional)</b></td>
			<td><form:textarea path="comments" /></td>
		</tr>
		</table>
		
		<div id="operationButtons" class="buttons">
			<input name="promoteButton" type="button" <c:if test="${isComplete == true}"><c:out value="disabled='disabled'"/></c:if>  value="Promote" onclick="submitPromote() "/>
			<button id="cancelButton" type="button"  <c:if test="${isComplete == true}"><c:out value="disabled='disabled'"/></c:if>  onclick=" location.href ='<%=WebConstants.MVC_GROUP_BOOK_ALL_VERSIONS%>?<%=WebConstants.KEY_ID%>=${bookDefinitionId}';">Cancel</button>
		</div>
		
		<td>
				<form:hidden path="bookDefinitionId" value="${bookDefinitionId}"/>
				<form:hidden path="proviewGroupID" value="${proviewGroupID}"/>
		  		<form:hidden path="groupVersion" value="${groupVersion}"/>
				<form:hidden path="groupName" />			
				
		</td>
		
		<%-- Informational Messages area --%>
		<div id="submitStatus" style="background: yellow;">
		</div>
	    <c:if test="${infoMessage != null}">
	    <div style="background: lightgreen;">
	    	${infoMessage}
	    </div>
	    <br/>
	    </c:if>
	    <%-- Error Messages area --%>
	    <c:if test="${errMessage != null}">
	    <div style="background: red;">
	    	${errMessage}
	    </div>
	    <br/>
	    </c:if>
		
	</form:form>
  
  
</body>
</html>
	