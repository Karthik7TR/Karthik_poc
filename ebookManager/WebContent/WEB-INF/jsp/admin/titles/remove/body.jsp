<!--
	Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.proview.ProviewTitleForm"%>
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
			$('#<%=ProviewTitleForm.FORM_NAME%>').submit();
  			return true; 
  		}
		
 		 function submitRemove(){
 			 var confirmed = confirm("Are you sure to remove this title version?");
 			 if (confirmed){
 				 submitForm();
 			 }
 		 }
		
		
	</script>

<body>

  
 
	<form:form action="<%=WebConstants.MVC_ADMIN_KEYWORD_PROVIEW_TITLE_REMOVE%>"
			   commandName="<%=ProviewTitleForm.FORM_NAME%>" name="theForm" method="post">
	
		<table>		   
		<tr>
			<td id="titleId">Title Id: ${titleId}</td>
		</tr>
		<tr>
			<td id="version">Version Number: ${versionNumber}</td>
		</tr>
		<tr>
			<td id="status">Status: ${status}</td>
		</tr>
		</table>
		
		<div class="buttons">
			<input id="removeButton" type="button" value="Remove" onclick="submitRemove();"/>
		</div>
		
		<td>
				<form:hidden path="titleId"/>
				<form:hidden path="version"/>
				<form:hidden path="status"/>
		</td>
		
		<%-- Informational Messages area --%>
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
	