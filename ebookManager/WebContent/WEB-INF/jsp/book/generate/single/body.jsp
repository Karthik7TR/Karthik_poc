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

  <script type="text/javascript" src="js/book/book-fields.js"></script>
  <script type="text/javascript">

  const VERSION_TYPE = {
      MAJOR : "<%=GenerateBookForm.Version.MAJOR.toString()%>",
      MINOR : "<%=GenerateBookForm.Version.MINOR.toString()%>",
      OVERWRITE : "<%=GenerateBookForm.Version.OVERWRITE.toString()%>"
  };
  const NOT_PUBLISHED = "Not published";

  function changeNewVersion(versionTypeSelection){
	 var newVersionType = $(versionTypeSelection).val();
	 var newVersion = "";

	 $("#nextMajorVersionGroup").hide();
	 $("#currentMajorVersionGroup").hide();

	 switch (newVersionType) {
         case VERSION_TYPE.OVERWRITE:
             newVersion = "${newOverwriteVersionNumber}";
             $("#currentMajorVersionGroup").show();
             break;
         case VERSION_TYPE.MINOR:
             newVersion = "${newMinorVersionNumber}";
             $("#currentMajorVersionGroup").show();
             break;
         case VERSION_TYPE.MAJOR:
             newVersion = "${newMajorVersionNumber}";
             $("#nextMajorVersionGroup").show();
             break;
     }
	 $('#newVersionNumber').text(newVersion);

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
	return checkCompleteFlag() && confirmValues() && submitForm(cmd);
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

  function checkPublishingCutoffDate() {
  	const  publishingCutOffDate = document.getElementById('publishingCutOffDate').innerHTML;
  	let confirmed = true;

	if (publishingCutOffDate !== '') {
		const publishingCutOffDateGreaterThanToday = document.getElementById('publishingCutOffDateGreaterThanToday').innerHTML;
		if (publishingCutOffDateGreaterThanToday === 'N') {
			alert('Cannot generate book: Publishing cut off date must be greater than today.');
			confirmed = false;
		} else {
			confirmed = confirm('Generate with Publishing cutoff date: ' + publishingCutOffDate);
		}
	}

	return confirmed;
  }

  const warningTypes = {
      EXISTED_ISBN: "existedIsbn",
      ISBN_SHOULD_BE_CHANGED_FOR_MAJOR_VERSION: "isbnShouldBeChangedForMajorVersion",
      SHOULD_BE_MAJOR: "shouldBeMajor",
      CONFIRMATION: "confirmation"
  };
  const warningMessages = {
      "existedIsbn": function (isbn) {
          return "\nWARNING: Current ISBN " + isbn + " has already been used to publish a book. ";
      },
      "isbnShouldBeChangedForMajorVersion": function () {
          return "Normally ISBN should be changed for major version.";
      },
      "shouldBeMajor": function (isbn, versionType) {
          return "\nWARNING: You are running a " + versionType + " version with a new ISBN number " + isbn + ". Are you sure this is not a MAJOR version?";
      },
      "confirmation": function () {
          return "\nDo you still want to continue?";
      }
  };

  function checkIsbn() {
      let confirmed = true;
      const isNewISBN = document.getElementById('isNewISBN').innerHTML === "Y";
      const isbn = document.getElementById('isbn').innerHTML;

      let message = "";
      const currentProviewVersion = "${versionNumber}";
      if (currentProviewVersion === NOT_PUBLISHED){
      		if (!isNewISBN) {
				message += warningMessages[warningTypes.EXISTED_ISBN](isbn);
			}
      } else {
          const isIsbnChanged = "${isbnChanged}" === "true";
          const isCurrentProviewVersionMinor = currentProviewVersion.split('.')[1] !== '0';
          const versionType = $('#newVersion option:selected').val();
		  if (isIsbnChanged) {
			  if (!isNewISBN) {
				  message += warningMessages[warningTypes.EXISTED_ISBN](isbn);
			  }
			  if ((versionType === VERSION_TYPE.MINOR) || (versionType === VERSION_TYPE.OVERWRITE && isCurrentProviewVersionMinor)) {
				  message += warningMessages[warningTypes.SHOULD_BE_MAJOR](isbn, versionType);
			  }
		  } else if (!isNewISBN && versionType === VERSION_TYPE.MAJOR) {
			  message += warningMessages[warningTypes.EXISTED_ISBN](isbn) + warningMessages[warningTypes.ISBN_SHOULD_BE_CHANGED_FOR_MAJOR_VERSION]()
		  }
	  }

      if (message !== "") {
          message += warningMessages[warningTypes.CONFIRMATION]();
          confirmed = confirm(message);
      }
      if (confirmed) {
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

	function groupValidation() {
		const versionType = $('#newVersion option:selected').val();
		var errorMessage = "${groupNextErrorMessage}";

		if (errorMessage && versionType === VERSION_TYPE.MAJOR) {
			alert(errorMessage);
			return false;
		}

		return true;
	}

	function confirmValues() {
		return checkVersion() && checkPublishingCutoffDate() && checkIsbn() && checkPilotBookStatus() && groupValidation();
	}

$(document).ready(function() {
	hideFields("${ book.sourceType }");
})
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
		  <tr class="cwbHideClass">
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
			<input id="groupButton" type="button" value="Create/Edit Group" onclick="submitEdit('<%=GenerateBookForm.Command.GROUP%>')" ${superUserPublisherPlusVisibility}/>
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
		  	<p id="usePublishingCutOffDate">${usePublishingCutOffDate}</p>
		  	<p id="isComplete">${isComplete}</p>
		 	<p id="isbn">${isbn}</p>
		  	<p id="publishingCutOffDateGreaterThanToday">${publishingCutOffDateGreaterThanToday}</p>
		  	<p id="pilotBookStatus">${pilotBookStatus}</p>
		  	<p id="isNewSUB">${isNewSUB}</p>
		 </div>

	</form:form>
  </c:when>
  <c:otherwise>
  	No book found
  </c:otherwise>
  </c:choose>
