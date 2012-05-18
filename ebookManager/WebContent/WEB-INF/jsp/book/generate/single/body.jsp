<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

<html>
<head>

<body onload='clearRadioButtons()'>

  <script type="text/javascript">
 
  function clearRadioButtons(){
  	$("input:radio").attr("checked", false);
  }
  
  function changeMajorVersion(newVersion){
	  document.getElementById('newVersionNumber').innerHTML = newVersion;
	  document.getElementById('isMajorVersion').innerHTML = "Y";
  }
  
  function changeMinorVersion(newVersion){
	  document.getElementById('newVersionNumber').innerHTML = newVersion;
	  document.getElementById('isMajorVersion').innerHTML = "N";
  }
  
  function submitForm(cmd){
	$('#command').val(cmd);
  	$('#<%=GenerateBookForm.FORM_NAME%>').submit();
  	return true; 
  }
  
  function submitEdit(cmd){
	  return submitForm(cmd);
	  
  }
  
  function submitGenerate(cmd){
	
	var confirmed = checkCompleteFlag();
	
	if (confirmed){  
		confirmed=confirmValues();
		if (confirmed){
			confirmed = submitForm(cmd);
		}
		
	}
	
	return confirmed;
	
  }
  
  function checkCompleteFlag(){
	  var  completeFlag = document.getElementById("isComplete").innerHTML;
	  if (completeFlag=="false"){
		  alert("Cannot generate book: Book definition is not complete.");
		  return false;
	   }
	  else{
		  return true;
	  }
	  
  }
  
  function checkPublishingCutoffDate(){
	  
	  var  publishingCutOffDate = document.getElementById("publishingCutOffDate").innerHTML;
	  var confirmed = true;
			
		if (publishingCutOffDate!=""){
			
			var publishingCutOffDateGreaterOrEqualToday = document.getElementById("publishingCutOffDateGreaterOrEqualToday").innerHTML;
			
			if (publishingCutOffDateGreaterOrEqualToday=="N"){
				alert("Cannot generate book: Publishing cut off date must be greater or equal today.");
				confirmed = false;
			}
			else{
				confirmed = confirm("Generate with Publishing cutoff date: " + publishingCutOffDate);
			}
		}
		
		return confirmed;
  }
  
  
  function checkMaterialIdandIsbn(){
	  
	  var  confirmed = true;
	  var isMajorVersion = document.getElementById('isMajorVersion').innerHTML;
	  var isNewISBN = document.getElementById('isNewISBN').innerHTML;
	  var isNewMaterialId = document.getElementById('isNewMaterialId').innerHTML;
	  var isbn = document.getElementById('isbn').innerHTML
	  var materialId = document.getElementById('materialId').innerHTML
	   
	  
	  if (isMajorVersion == "Y"){
		
		  if(isNewISBN =="N" && isNewMaterialId=="N"){
			  alert("Cannot generate book: ISBN and Sub Material Number must be changed for major version.");
			  confirmed= false;
		  }
		  else{
		  	if(isNewISBN =="N"){
				alert("Cannot generate book: ISBN must be changed for major version.");
			  	confirmed= false;
		  	}
		  
		  	if(isNewMaterialId=="N"){
			  	alert("Cannot generate book: Sub Material Number must be changed for major version.");
			  	confirmed= false;
		  	}  	
		  }
	  }
	  if (confirmed){
		  confirmed = confirm("Generate with ISBN: " + isbn + ", Sub Material Number: " + materialId);
  	  }
	  return confirmed;
  }
  
  
  function checkVersion(){
	  
	  var confirmed = true;
	  var newVersion = document.getElementById('newVersionNumber').innerHTML
	  
	  if (newVersion==""){
		  alert("Cannot generate book: Version must be selected.");
		  confirmed = false;
	  }
	  else{
	  	confirmed = confirm("Generate with new version number: "+ newVersion);
	  }
	  
	  return confirmed;
  }
  
  
  function confirmValues(){
	  
	  var confirmed = checkVersion();
	 
	  if (confirmed){

		    confirmed = checkPublishingCutoffDate();
	
			if (confirmed){
				confirmed = checkMaterialIdandIsbn();
			}
	  }
	  
	  return confirmed;
  }
  
    
  </script>
  
 <c:choose>
 <c:when test="${book != null}">
 
	<form:form action="<%=WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW%>"
			   commandName="<%=GenerateBookForm.FORM_NAME%>" name="theForm" method="post">
			   
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
				<form:hidden path="<%=WebConstants.KEY_ID%>"/>
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
			  <form:radiobutton path="majorVersion" onclick='changeMinorVersion("${newMinorVersionNumber}")' value="false"/>Minor
			  <form:radiobutton path="majorVersion" onclick='changeMajorVersion("${newMajorVersionNumber}")' value="true"/>Major
			 </td>
		  </tr>
		  
		  <tr>
		  	<td>ProView Version Current:
		  	<td id="currentVersionNumber">${versionNumber}</td>
		  </tr>
		  
		  <tr>
		  	<td>ProView Version New:
		  	<td id="newVersionNumber"></td>
		  </tr>
		  
		</table>
		<div class="buttons">
			<input id="generateButton" type="button" value="Generate Book" onclick="submitGenerate('<%=GenerateBookForm.Command.GENERATE%>')" ${superPublisherPublisherplusVisibility} />
			<input id="editButton" type="button" value="Edit Book Definition" onclick="submitEdit('<%=GenerateBookForm.Command.EDIT%>')"/>
			<input id="editButton" type="button" value="Cancel" onclick="submitEdit('<%=GenerateBookForm.Command.CANCEL%>')" ${superPublisherPublisherplusVisibility} />
		</div>
		<div style="visibility: hidden"> 
		  	<p id="publishingCutOffDate">${publishingCutOffDate}</p>
		  	<p id="isNewISBN">${isNewISBN}</p>
		  	<p id="isNewMaterialId">${isNewMaterialId}</p>
		  	<p id="usePublishingCutOffDate">${usePublishingCutOffDate}</p>
		  	<p id="isComplete">${isComplete}</p>
		 	<p id="isbn">${isbn}</p>
		  	<p id="materialId">${materialId}</p>
		  	<p id="isMajorVersion">${isMajorVersion}</p>
		  	<p id="publishingCutOffDateGreaterOrEqualToday">${publishingCutOffDateGreaterOrEqualToday}</p>
		 </div>	
		
		<%-- Informational Messages area --%>
	    <c:if test="${infoMessage != null}">
	    <div class="infoMessageSuccess">
	    	${infoMessage}
	    </div>
	    <br/>
	    </c:if>
	    <%-- Error Messages area --%>
	    <c:if test="${errMessage != null}">
	    <div class="infoMessageError">
	    	${errMessage}
	    </div>
	    <br/>
	    </c:if>
		
	</form:form>
  </c:when>
  <c:otherwise>
  	No book found
  </c:otherwise>
  </c:choose>
  
</body>
</html>
