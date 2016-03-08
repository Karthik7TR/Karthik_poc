<!--
	Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm"%>
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
			$('#<%=ProviewGroupListFilterForm.FORM_NAME%>').submit();
  			return true; 
  		}
		
 		 function submitDelete(){
 			 var confirmed = confirm("Are you sure to Delete?");
 			 if (confirmed){
 				 submitForm();
 			 }
 			 
 			 return confirmed;
 		 }
		
		
	</script>

<body>

  
 
	<form:form action="<%=WebConstants.MVC_PROVIEW_GROUP_BOOK_DELETE%>"
			   commandName="<%=ProviewGroupListFilterForm.FORM_NAME%>" name="theForm" method="post">
	
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
		
		<div class="buttons">
			<input id="deleteButton" type="button" <c:if test="${isComplete == true}"><c:out value="disabled='disabled'"/></c:if> value="Delete" onclick="submitDelete()"/>
			<input id="cancelButton" type="button"  <c:if test="${isComplete == true}"><c:out value="disabled='disabled'"/></c:if>  onclick=" location.href ='<%=WebConstants.MVC_PROVIEW_GROUP_BOOK_VERSIONS%>?<%=WebConstants.KEY_GROUP_BY_VERSION_ID%>=${groupIdByVersion}';" value="Cancel"/>		
		</div>
		
		<td>
				<form:hidden path="bookDefinitionId" value="${bookDefinitionId}"/>
				<form:hidden path="proviewGroupID" value="${proviewGroupID}"/>
		  		<form:hidden path="groupVersion" value="${groupVersion}"/>
				<form:hidden path="groupIds" value="${groupIds}"/>
		  		<form:hidden path="groupIdByVersion" value="${groupIdByVersion}"/>	
		  		<form:hidden path="groupName" value="${groupName}"/>
		  		<form:hidden path="groupOperation" value="${groupOperation}"/>		
				
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
	