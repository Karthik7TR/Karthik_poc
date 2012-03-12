<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

<html>
<head>

<body onload='changeVersion("${newMinorVersionNumber}")'>

  <script type="text/javascript">
  	
  function changeVersion(newVersion){
	  document.getElementById('newVersionNumber').innerHTML = newVersion;
  }
  
  function submitForm(cmd)
  {
  	$('#command').val(cmd);
  	theForm.submit();
  	return true;
  }
  
  function checkPublishingCutoffDate(){
	  
	  	var usePublishingCutOffDate = document.getElementById("usePublishingCutOffDate").innerHTML; 
		
		if (usePublishingCutOffDate=="Y"){
			var  publishingCutOffDate = document.getElementById("publishingCutOffDate").innerHTML;
			
			if (publishingCutOffDate==null)
				{
					alert("Publishing cut off date is required to publish this book. Please edit the definition to enter it.");
					return false;
				}
		}
	  return true;
  }
  
  function confirmValues(){
	  var newVersion = document.getElementById('newVersionNumber').innerHTML
	  var confirmed = confirm("Generate with new version number: "+ newVersion);
	  
	  if (confirmed){
		
		confirmed = checkPublishingCutoffDate();
		
		if (confirmed){
			confirmed = confirm("Generate with Publishing cutoff date: " + publishingCutOffDate);
		
			if (confirmed){
				var isbn =document.getElementById("isbn").innerHTML;
				var  materialId=document.getElementById("materialId").innerHTML;
				confirmed=confirm("Generate with ISBN: " + isbn + ", Material Id: " + materialId);
			}
		}
			
	  }
	  
	  return confirmed;
  }
  
    
  </script>
  
  
  <div class="majorDiv">
	
	<form:form action="<%=WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW%>"
			   commandName="<%=GenerateBookForm.FORM_NAME%>" name="theForm" method="post" onsubmit='return confirmValues();'>
			   
		<%-- Validation error Message Presentation --%>
		<spring:hasBindErrors name="<%=GenerateBookForm.FORM_NAME%>">
			<div class="errorBox">
		      <b><spring:message code="please.fix.errors"/></b><br/>
		      <form:errors path="*">
		      	<ul>
				<c:forEach items="${messages}" var="message">
					<li style="color: black">${message}</li>
				</c:forEach>
		      	</ul>
			  </form:errors>
			  <br/>
		    </div>
		    <br/>
	    </spring:hasBindErrors>
	    
	    <table>
		<tr>
			<td>
				<form:hidden path="fullyQualifiedTitleId"/>
				<form:hidden path="command"/>
			</td>
		</tr>
		<tr>
			<td>Priority:&nbsp;</td>  <%-- Indicates which launch queue to place job request on --%>
			<td>
			  <form:select path="highPriorityJob">
			    <form:option label="Normal" value="false"/>
				<form:option label="High" value="true"/>
			  </form:select>
			 </td>
		  </tr>
		  
		  <tr>
		  	<td>Version:&nbsp;</td>  <%-- Indicates which launch queue to place job request on --%>
			<td>
			  <form:radiobutton path="majorVersion" onclick='changeVersion("${newMinorVersionNumber}")' value="false"/>Minor
			  <form:radiobutton path="majorVersion" onclick='changeVersion("${newMajorVersionNumber}")' value="true"/>Major
			 </td>
		  </tr>
		  
		  <tr>
		  	<td>Proview Version Current:
		  	<td id="currentVersionNumber">${versionNumber}</td>
		  </tr>
		  
		  <tr>
		  	<td>Proview Version New:
		  	<td id="newVersionNumber"></td>
		  </tr>
		  <div style="visibility: hidden"> 
		  	<text id="publishingCutOffDate">${publishingCutOffDate}</text>
		  	<text id="isbn">${isbn}</text>
		  	<text id="materialId">${materialId}</text>
		  	<text id="usePublishingCutOffDate">${usePublishingCutOffDate}</text>
		  </div>	
		</table>
		<br/>
		<input id="generateButton" type="submit" value="Generate" ${generateButtonVisibility} />
		<input type="button" value="Edit Book Definition" onclick="submitForm('<%=ViewBookDefinitionForm.Command.EDIT%>')"/>
		
		
		<%-- Informational Messages area --%>
	    <c:if test="${infoMessage != null}">
	    <div style="background: lightgreen; padding: 5px 5px 5px 5px;">
	    	${infoMessage}
	    </div>
	    <br/>
	    </c:if>
	    <%-- Error Messages area --%>
	    <c:if test="${errMessage != null}">
	    <div style="background: orange; padding: 5px 5px 5px 5px;">
	    	${errMessage}
	    </div>
	    <br/>
	    </c:if>
		
	</form:form>
  </div>
</body>
</html>
