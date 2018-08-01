<!--
	Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

 	<script type="text/javascript">
		
 	function submitForm(cmd){
 			document.getElementById('submitStatus').innerHTML = "ProView request submitted... waiting for response.";
			$('#command').val(cmd);
			$('#<%=ProviewTitleForm.FORM_NAME%>').submit();
			return true; 
		}
	
 	 	function submitDelete(cmd){
			 
			 var confirmed = true;
			 
			 if (confirmed){
			 	confirmed = confirm("Are you sure to delete this title version?");
			 	if (confirmed){
					 submitForm(cmd);
			 	}
			 }
			 
			 return confirmed;
		 }
	
		
		
	</script>
	<form:form action="<%=WebConstants.MVC_PROVIEW_TITLE_DELETE%>"
			   commandName="<%=ProviewTitleForm.FORM_NAME%>" name="theForm" method="post">
	
		<table>		   
		<tr>
			<td id="titleId"><b>Title Id:</b></td> 
			<td>&nbsp;&nbsp;&nbsp;${titleId}</td>
		</tr>
		<tr>
			<td id="version"><b>Version:</b></td> 
			<td>&nbsp;&nbsp;&nbsp;${versionNumber}</td>
		</tr>
		<tr>
			<td id="status"><b>Status:</b></td>
			<td>&nbsp;&nbsp;&nbsp;${status}</td>
		</tr>
		<tr>
			<td><b>Comments (Optional)</b></td>
			<td><form:textarea path="comments" /></td>
		</tr>
		</table>

		<c:if test="${status == 'Removed'}">
            <div class="buttons">
                <input id="deleteButton" type="button" value="Delete" onclick="submitDelete('<%=ProviewTitleForm.Command.DELETE%>');"/>
            </div>
		</c:if>
		
		<td>
				<form:hidden path="titleId"/>
				<form:hidden path="version"/>
				<form:hidden path="status"/>
				<form:hidden path="lastUpdate"/>
				<form:hidden path="command"/>
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
