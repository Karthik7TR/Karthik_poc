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

<body>

  <script type="text/javascript">
 
  function changeNewVersion(versionTypeSelection){
	 var newVersionType = versionTypeSelection.options[versionTypeSelection.selectedIndex].value;
	 var newVersion;
	 var isMajorVersion;
	 
	
	 if (newVersionType == "OVERWRITE"){
		 newVersion = '${newOverwriteVersionNumber}';
		 isMajorVersion = "N";
	 }
	 else if (newVersionType == "MINOR"){
		 newVersion = '${newMinorVersionNumber}';
		 isMajorVersion = "N";
	 }
	 else if (newVersionType == "MAJOR"){
		 newVersion = '${newMajorVersionNumber}';
		 isMajorVersion = "Y";
	 }
	 else if (newVersionType == ""){
		 newVersion = "";
		 isMajorVersion = "N";
	 }
	 
	 
	 document.getElementById('newVersionNumber').innerHTML = newVersion;
	 document.getElementById('isMajorVersion').innerHTML = isMajorVersion;
	 
	 return true;
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
  
  
  function checkIsbn(){
	  
	  var confirmed = true;
	  var isNewISBN = document.getElementById('isNewISBN').innerHTML;
	  var isbn = document.getElementById('isbn').innerHTML;
	  var isMajorVersion = document.getElementById('isMajorVersion').innerHTML;
		
	   
	  if (isMajorVersion == "Y"){
	  	if(isNewISBN =="N"){
			alert("Cannot generate book: ISBN must be changed for major version.");
		  	confirmed= false;
	  	}
	  }
	  if (confirmed){
		  confirmed = confirm("Generate with ISBN: " + isbn);
  	  }
	  return confirmed;
  }
  
  
  function checkVersion(){
	  
	  var confirmed = true;
	  var newVersion = document.getElementById('newVersionNumber').innerHTML;
	  
	  if (newVersion == ""){
		  alert("Cannot generate book: Version must be selected.");
		  confirmed = false;
	  }
	  else{
	  	confirmed = confirm("Generate with new version number: "+ newVersion);
	  }
	  
	  return confirmed;
  }
  
	function checkPilotBookStatus(){
  
		var confirmed = true;
		var pilotBookStatus = document.getElementById('pilotBookStatus').innerHTML;
		
		if (pilotBookStatus=="IN_PROGRESS"){
			confirmed = confirm("You are about to generate a pilot book without notes migration. Customers will not see their annotations and/or notes.  Once the migration CSV file exists, please regenerate the book after updating the Notes Migration to 'True'.");
		}
		
		return confirmed;
	}
  
  
  function confirmValues(){
	  
	  var confirmed = checkVersion();
	 
	  if (confirmed){
		    confirmed = checkPublishingCutoffDate();
			if (confirmed){
				confirmed = checkIsbn();
				if (confirmed){
					confirmed = checkPilotBookStatus();
				}
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
				<form:hidden path="newMajorVersion"/>
				<form:hidden path="newMinorVersion"/>
				<form:hidden path="newOverwriteVersion"/>
				<form:hidden path="<%=WebConstants.KEY_ID%>"/>
			</td>
		</tr>
		  
		  <tr>
		  	<td>ProView Status Current:</td>
		  	<td id="bookStatusInProview">${bookStatusInProview}</td>
		  </tr>
		  
		  <tr>
		  	<td>ProView Version Current:</td>
		  	<td id="currentVersionNumber">${versionNumber}</td>
		  </tr>
		
		  <tr>
		  	<td>ProView Version New:</td>
		  	<td id="newVersionNumber"></td>
		  </tr>
		  <tr>
		  	<td>Novus Stage:</td>
		  	<td>
		  		<c:choose>
					<c:when test="${ book.finalStage == true }">
						Final Stage
					</c:when>
					<c:otherwise>
						<span style="color:red;font-weight: bold;">Review Stage</span>
					</c:otherwise>
				</c:choose>
			</td>
		  </tr>
		  <tr>
		  	<td>Generate Version Type:&nbsp;</td>  <%-- Indicates which launch queue to place job request on --%>
			<td>
			  	<form:select path="newVersion" onchange="changeNewVersion(this)">
			  		<form:option label="Select version" value=""/>
					<c:if test="${overwriteAllowed == 'Y'}">
						<form:option label="<%=GenerateBookForm.Version.OVERWRITE.toString()%>" value="<%=GenerateBookForm.Version.OVERWRITE.toString()%>"/>
					</c:if>
					<form:option label="<%=GenerateBookForm.Version.MINOR.toString()%>" value="<%=GenerateBookForm.Version.MINOR.toString()%>"/>
					<form:option label="<%=GenerateBookForm.Version.MAJOR.toString()%>" value="<%=GenerateBookForm.Version.MAJOR.toString()%>"/>
				</form:select>
			 </td>
		  </tr>
		  
		  <tr>
			<td>Job Priority:&nbsp;</td>  <%-- Indicates which launch queue to place job request on --%>
			<td>
			  <form:select path="highPriorityJob">
			    <form:option label="NORMAL" value="false"/>
				<form:option label="HIGH" value="true"/>
			  </form:select>
			 </td>
		  </tr>
		  
		
		  
		
		</table>
		<div class="buttons">
			<input id="generateButton" type="button" value="Generate Book" onclick="submitGenerate('<%=GenerateBookForm.Command.GENERATE%>')" ${superPublisherPublisherplusVisibility} />
			<input id="editButton" type="button" value="Edit Book Definition" onclick="submitEdit('<%=GenerateBookForm.Command.EDIT%>')" ${superPublisherPublisherplusVisibility}/>
			<input id="editButton" type="button" value="Cancel" onclick="submitEdit('<%=GenerateBookForm.Command.CANCEL%>')" ${superPublisherPublisherplusVisibility} />
		</div>
		<div style="visibility: hidden"> 
		  	<p id="publishingCutOffDate">${publishingCutOffDate}</p>
		  	<p id="isNewISBN">${isNewISBN}</p>
			<p id="isMajorVersion"></p>
		  	<p id="usePublishingCutOffDate">${usePublishingCutOffDate}</p>
		  	<p id="isComplete">${isComplete}</p>
		 	<p id="isbn">${isbn}</p>
		  	<p id="publishingCutOffDateGreaterOrEqualToday">${publishingCutOffDateGreaterOrEqualToday}</p>
		  	<p id="pilotBookStatus">${pilotBookStatus}</p>
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
