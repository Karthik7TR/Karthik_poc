<%--
	Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
--%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

  <script type="text/javascript">
  function changeNewVersion(versionTypeSelection){
	 var newVersionType = versionTypeSelection.options[versionTypeSelection.selectedIndex].value;
	 var newVersion = "";
	 var isMajorVersion;
	 var isSplitBook = document.getElementById('isSplitBook').innerHTML;
	 var disableTitleFromSplit = document.getElementById('disableTitleFromSplit').innerHTML;
	 $("#nextMajorVersionGroup").hide();
	 $("#currentMajorVersionGroup").hide();
	 
	 if (newVersionType == "OVERWRITE"){
		 newVersion = '${newOverwriteVersionNumber}';
		 isMajorVersion = "N";
		 $("#currentMajorVersionGroup").show();
	 }
	 else if (newVersionType == "MINOR"){
		 newVersion = '${newMinorVersionNumber}';
		 isMajorVersion = "N";
		 $("#currentMajorVersionGroup").show();
	 }
	 else if (newVersionType == "MAJOR"){
		 newVersion = '${newMajorVersionNumber}';
		 isMajorVersion = "Y";
		 $("#nextMajorVersionGroup").show();
	 }
	 else if (newVersionType == ""){
		 newVersion = "";
		 isMajorVersion = "N";
	 }
	 
	 if (newVersion > 1.0 && isSplitBook == "true" && disableTitleFromSplit == "true") {
		 $("#splitWarning").show();
	 } else {
		 $("#splitWarning").hide();
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
			alert("Cannot generate book: Current ISBN " + isbn + " has already been used to publish a book. ISBN must be changed for major version.");
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
  
  
	function checkSplitStatus() {

		var confirmed = true;
		var isSplitBook = document.getElementById('isSplitBook').innerHTML;
		var disableTitleFromSplit = document.getElementById('disableTitleFromSplit').innerHTML;

		if (isSplitBook == "true" && disableTitleFromSplit == "true") {
			confirmed = confirm("You are about to generate a split book with minor/major update. ProView notes migration enhancements are still in process.  Be aware customers might lose their annotations and/or notes if the split location(s) has changed from the previous book version.");
		}

		return confirmed;
	}
	
	function groupValidation() {
		var confirmed = true;
		var isMajorVersion = document.getElementById('isMajorVersion').innerHTML;
		var errorMessage = "${groupNextErrorMessage}";

		if (errorMessage && isMajorVersion == "Y") {	
			alert(errorMessage);
			confirmed= false;
		}

		return confirmed;
	}

	function confirmValues() {

		var confirmed = checkVersion();

		if (confirmed) {
			confirmed = checkPublishingCutoffDate();
			if (confirmed) {
				confirmed = checkIsbn();
				if (confirmed) {
					confirmed = checkPilotBookStatus();
					if (confirmed) {
						confirmed = checkSplitStatus();
						if (confirmed) {
							confirmed = groupValidation();
						}
					}
				}
			}
		}

		return confirmed;
	}
	
$(document).ready(function() {
})
  </script>
  
 <c:choose>
 <c:when test="${book != null}">
 
 	<div id="splitWarning" class="infoMessageWarning" style="display:none">
 		You are about to generate a split book with minor/major update. ProView notes migration enhancements are still in process.  Be aware customers might lose their annotations and/or notes if the split location(s) has changed from the previous book version.
 	</div>
 
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
		
		<c:if test="${not empty book.groupName}">
		<div id="currentMajorVersionGroup" style="display:none;">
		<c:choose>
			<c:when test="${groupCurrentPreview != null}">
				<div>Group Detail</div>
				<div class="dynamicRow">
					<label>Group Id</label>
					<span class="field">${groupCurrentPreview.groupId}</span>
				</div>
				<div class="dynamicRow">
					<label>Group Name</label>
					<span class="field">${groupCurrentPreview.name}</span>
				</div>
				<div class="dynamicRow">
					<label>Group Version</label>
					<span class="field">${groupCurrentPreview.groupVersion}</span>
				</div>
				<div class="dynamicRow">
					<label>Group Type</label>
					<span class="field">${groupCurrentPreview.type}</span>
				</div>
				<div class="dynamicRow">
					<label>Head Title</label>
					<span class="field">${groupCurrentPreview.headTitle}</span>
				</div>
				<div class="expandingBox">
				<c:forEach items="${ groupCurrentPreview.subGroupInfoList }" var="subGroup" varStatus="count">
					<c:if test="${not empty subGroup.heading }">
						<div class="dynamicRow">
							<label>Subgroup ${count.index + 1} Heading:</label>
							<span class="field"> ${ subGroup.heading }</span>
						</div>
					</c:if>
					<c:forEach items="${ subGroup.titles }" var="title">
						<div class="dynamicRow">
							<label>eBook Title ID:</label>
							<span class="field"> ${ title }</span>
						</div>
					</c:forEach>
				</c:forEach>
				</div>
			</c:when>
			<c:when test="${empty errMessage}">
				Group already in ProView
			</c:when>
		</c:choose>
		</div>
		
		<div id="nextMajorVersionGroup" style="display:none;">
			<c:choose>
				<c:when test="${not empty groupNextErrorMessage}">
					<div class="infoMessageError">
				    	${groupNextErrorMessage}
				    </div>
				    <br/>
				</c:when>
				<c:when test="${groupNextPreview != null}">
					<div>Group Detail</div>
					<div class="dynamicRow">
						<label>Group Id</label>
						<span class="field">${groupNextPreview.groupId}</span>
					</div>
					<div class="dynamicRow">
						<label>Group Name</label>
						<span class="field">${groupNextPreview.name}</span>
					</div>
					<div class="dynamicRow">
						<label>Group Version</label>
						<span class="field">${groupNextPreview.groupVersion}</span>
					</div>
					<div class="dynamicRow">
						<label>Group Type</label>
						<span class="field">${groupNextPreview.type}</span>
					</div>
					<div class="dynamicRow">
						<label>Head Title</label>
						<span class="field">${groupNextPreview.headTitle}</span>
					</div>
					<div class="expandingBox">
					<c:forEach items="${ groupNextPreview.subGroupInfoList }" var="subGroup" varStatus="count">
						<c:if test="${not empty subGroup.heading }">
							<div class="dynamicRow">
								<label>Subgroup ${count.index + 1} Heading:</label>
								<span class="field"> ${ subGroup.heading }</span>
							</div>
						</c:if>
						<c:forEach items="${ subGroup.titles }" var="title">
							<div class="dynamicRow">
								<label>eBook Title ID:</label>
								<span class="field"> ${ title }</span>
							</div>
						</c:forEach>
					</c:forEach>
					</div>
				</c:when>
				<c:when test="${empty errMessage}">
					Group already in ProView
				</c:when>
			</c:choose>
		</div>
		</c:if>
		
		<%-- Warning Messages area --%>
	    <c:if test="${warningMessage != null}">
	    <div class="infoMessageWarning">
	    	${warningMessage}
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
	    
		<div class="buttons">
			<c:if test="${empty errMessage}">
				<input id="generateButton" type="button" value="Generate Book" onclick="submitGenerate('<%=GenerateBookForm.Command.GENERATE%>')" ${superPublisherPublisherplusVisibility} />
			</c:if>
			<input id="editButton" type="button" value="Edit Book Definition" onclick="submitEdit('<%=GenerateBookForm.Command.EDIT%>')" ${superPublisherPublisherplusVisibility}/>
			<input id="groupButton" type="button" value="Create/Edit Group" onclick="submitEdit('<%=GenerateBookForm.Command.GROUP%>')" ${superUserVisibility}/>
			<input id="editButton" type="button" value="Cancel" onclick="submitEdit('<%=GenerateBookForm.Command.CANCEL%>')" ${superPublisherPublisherplusVisibility} />
		</div>
		
		<%-- Informational Messages area --%>
	    <c:if test="${infoMessage != null}">
	    <div class="infoMessageSuccess">
	    	${infoMessage}
	    </div>
	    <br/> 
	    </c:if>
	    
		<div style="visibility: hidden"> 
		  	<p id="publishingCutOffDate">${publishingCutOffDate}</p>
		  	<p id="isNewISBN">${isNewISBN}</p>
			<p id="isMajorVersion"></p>
		  	<p id="usePublishingCutOffDate">${usePublishingCutOffDate}</p>
		  	<p id="isComplete">${isComplete}</p>
		 	<p id="isbn">${isbn}</p>
		  	<p id="publishingCutOffDateGreaterOrEqualToday">${publishingCutOffDateGreaterOrEqualToday}</p>
		  	<p id="pilotBookStatus">${pilotBookStatus}</p>
		  	<p id="isSplitBook">${isSplitBook}</p>
		  	<p id="disableTitleFromSplit">${disableTitleFromSplit}</p>
		  	<p id="isNewSUB">${isNewSUB}</p>
		 </div>	
		
	</form:form>
  </c:when>
  <c:otherwise>
  	No book found
  </c:otherwise>
  </c:choose>
