<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>

<script type="text/javascript">
		// Declare Global Variables
		var incrementVar = ${numberOfAuthors};
		var contentType = "";
		var publisher = "";
		var state = "";
		var pubType = "";
		var pubAbbr = "";
		var pubInfo = "";
		var jurisdiction = "";
		var contentTypeEnum = {NONE : 0, ANALYTICAL : 1, COURT_RULES: 2, SLICE_CODES : 3}; 
		
		
		// Function to create Fully Qualifed Title ID from the publisher options
		var updateTitleId = function() {
			var titleId = [];
			
			// Set up Title ID
			if(contentType == contentTypeEnum.ANALYTICAL) {
				if (pubInfo) {
					titleId.push(pubAbbr, pubInfo);
				} else {
					titleId.push(pubAbbr);
				};
			} else if(contentType == contentTypeEnum.COURT_RULES) {
				if (pubInfo) {
					titleId.push(state, pubType, pubInfo);
				} else {
					titleId.push(state, pubType);
				};
			} else if(contentType == contentTypeEnum.SLICE_CODES) {
				titleId.push(jurisdiction, pubInfo);
			};

			// Set up Namespace
			if (contentType != contentTypeEnum.NONE) {
				var fullyQualifiedTitleIdArray = [];
				var contentTypeAbbr = "";
				if(contentType == contentTypeEnum.ANALYTICAL) {
					contentTypeAbbr = "<%= WebConstants.KEY_ANALYTICAL_ABBR %>";
				} else if (contentType == contentTypeEnum.COURT_RULES) {
					contentTypeAbbr = "<%= WebConstants.KEY_COURT_RULES_ABBR %>";
				} else if(contentType == contentTypeEnum.SLICE_CODES) {
					contentTypeAbbr = "<%= WebConstants.KEY_SLICE_CODES_ABBR %>";
				} else {
					$('#titleId').val("");
					$('#titleIdBox').val("");
				}

				fullyQualifiedTitleIdArray.push(publisher, contentTypeAbbr, titleId.join("_"));
				
				var fullyQualifiedTitleId = fullyQualifiedTitleIdArray.join("/").toLowerCase();
				
				$('#titleId').val(fullyQualifiedTitleId);
				$('#titleIdBox').val(fullyQualifiedTitleId);
			} else {
				$('#titleId').val("");
				$('#titleIdBox').val("");
			};
		};
		
		// Function to determine which divs to show depending on the content type in Publisher Box
		var determineOptions = function() {
			$('#stateDiv').hide();
			$('#jurisdictionDiv').hide();
			$('#pubTypeDiv').hide();
			$('#pubAbbrDiv').hide();
			$('#publishDetailDiv').hide();
			
			if(contentType == contentTypeEnum.ANALYTICAL) {
				$('#pubAbbrDiv').show();
			} else if(contentType == contentTypeEnum.COURT_RULES) {
				$('#stateDiv').show();
				$('#pubTypeDiv').show();
			} else if(contentType == contentTypeEnum.SLICE_CODES) {
				$('#jurisdictionDiv').show();
			}
			
			if (contentType == contentTypeEnum.NONE) {
				$('#publishDetailDiv').hide();
			} else {
				$('#publishDetailDiv').show();
			}
		};
		
		// Add another author row
		var addAuthorRow = function() {
			var appendTxt = "<div class='row'>";
			appendTxt = appendTxt + "<input class=\"prefix\" id=\"authorInfo" + incrementVar + ".prefix\" name=\"authorInfo[" + incrementVar + "].prefix\" type=\"text\" title=\"prefix\"/>";
			appendTxt = appendTxt + "<input class=\"firstName\" id=\"authorInfo" + incrementVar + ".firstName\" name=\"authorInfo[" + incrementVar + "].firstName\" type=\"text\" title=\"first name\"/>";
			appendTxt = appendTxt + "<input class=\"middleName\" id=\"authorInfo" + incrementVar + ".middleName\" name=\"authorInfo[" + incrementVar + "].middleName\" type=\"text\" title=\"middle name\"/>";
			appendTxt = appendTxt + "<input class=\"lastName\" id=\"authorInfo" + incrementVar + ".lastName\" name=\"authorInfo[" + incrementVar + "].lastName\" type=\"text\" title=\"last name\"/>";
			appendTxt = appendTxt + "<input class=\"suffix\" id=\"authorInfo" + incrementVar + ".suffix\" name=\"authorInfo[" + incrementVar + "].suffix\" type=\"text\" title=\"suffix\"/>";
			appendTxt = appendTxt + "<input type=\"button\" value=\"Delete\" class=\"rdelete\" />";
			appendTxt = appendTxt + "</div>";
			$("#addHere").before(appendTxt);
			incrementVar = incrementVar + 1;
			
			textboxHint("authorName");
		};
		
		var setContentType = function(ct) {
			if (ct == "<%= WebConstants.KEY_ANALYTICAL %>") {
				contentType = contentTypeEnum.ANALYTICAL;
			} else if(ct == "<%= WebConstants.KEY_COURT_RULES %>") {
				contentType = contentTypeEnum.COURT_RULES;
			} else if (ct == "<%= WebConstants.KEY_SLICE_CODES%>") {
				contentType = contentTypeEnum.SLICE_CODES;
			} else {
				contentType = contentTypeEnum.NONE;
			}
		};
		
		var updateTOCorNORT = function(isTOC) {
			$("#displayTOC").hide();
			$("#displayNORT").hide();
			
			if(isTOC == "true") {
				$("#displayTOC").show();
				$("#nortFilterView").val("");
				$("#nortDomain").val("");
			} else {
				$("#displayNORT").show();
				$("#rootTocGuid").val("");
				$("#tocCollectionName").val("");
			}
		};
		
		var clearTitleInformation = function() {
			publisher = "";
			$('#publisher').val("");
			state = "";
			$('#state').val("");
			pubType = "";
			$('#pubType').val("");
			pubAbbr = "";
			$('#pubAbbr').val("");
			pubInfo = "";
			$('#pubInfo').val("");
			jurisdiction = "";
			$('#jurisdiction').val("");
		};
		
		$(document).ready(function() {
			<%-- Style buttons with jquery  --%>
			$( ".buttons input:submit,.buttons a,.buttons  button" ).button();
			
			<%-- Setup change handlers  --%>
			$('#contentType').change(function () {
				// Clear out information when content type changes
				clearTitleInformation();
				
				setContentType($(this).val());
				updateTitleId(contentType);
				determineOptions();
			});
			$('#publisher').change(function () {
				publisher = $(this).val();
				updateTitleId();
			});
			$('#state').change(function () {
				state = $(this).val();
				updateTitleId();
			});
			$('#jurisdiction').change(function () {
				jurisdiction = $(this).val();
				updateTitleId();
			});
			$('#pubType').change(function () {
				pubType = $(this).val();
				updateTitleId();
			});
			$('#pubAbbr').change(function () {
				pubAbbr = $(this).val();
				updateTitleId();
			});
			$('#pubInfo').change(function () {
				pubInfo = $(this).val();
				updateTitleId();
			});
			
			// Determine to show NORT or TOC fields
			$('input:radio[name=isTOC]').change(function () {
				updateTOCorNORT($(this).val());
			});
			
			$( "#accordion" ).accordion({
				fillSpace: true,
			});
			
			$(".rdelete").live("click", function () {
				var srow = $(this).parent();
				srow.css("background-color", "#F0F0F0");
				srow.fadeOut(500, function () { srow.remove(); });
			});
			
			// Initialize Global variables
			publisher = $('#publisher').val();
			state = $('#state').val();
			jurisdiction = $('#jurisdiction').val();
			pubType = $('#pubType').val();
			pubAbbr = $('#pubAbbr').val();
			pubInfo = $('#pubInfo').val();
			setContentType($('#contentType').val());
			
			// Setup view
			determineOptions();
			updateTitleId();
			updateTOCorNORT($('input:radio[name=isTOC]:checked').val());
			textboxHint("authorName");
		});
</script>

<div class="generateTitleID">
	<div>
		<form:label path="contentType" class="labelCol">Content Type</form:label>
		<form:select path="contentType" >
			<form:option value="" label="SELECT" />
			<form:options items="${contentTypes}" />
		</form:select>
		<form:errors path="contentType" cssClass="errorMessage" />
	</div>
	<div id="publishDetailDiv" style="display:none">
		<div>
			<form:label path="publisher" class="labelCol">Publisher</form:label>
			<form:select path="publisher" >
				<form:option value="" label="SELECT" />
				<form:options items="${publishers}" />
			</form:select>
			<div class="errorDiv">
				<form:errors path="publisher" cssClass="errorMessage" />
			</div>
		</div>
		<div id="stateDiv">
			<form:label path="state" class="labelCol">State</form:label>
			<form:select path="state" >
				<form:option value="" label="SELECT" />
				<form:options items="${states}" />
			</form:select>
			<div class="errorDiv">
				<form:errors path="state" cssClass="errorMessage" />
			</div>
		</div>
		<div id="jurisdictionDiv">
			<form:label path="jurisdiction" class="labelCol">Juris</form:label>
			<form:select path="jurisdiction" >
				<form:option value="" label="SELECT" />
				<form:options items="${jurisdictions}" />
			</form:select>
			<div class="errorDiv">
				<form:errors path="jurisdiction" cssClass="errorMessage" />
			</div>
		</div>
		<div id="pubTypeDiv">
			<form:label path="pubType" class="labelCol">Pub Type</form:label>
			<form:select path="pubType" >
				<form:option value="" label="SELECT" />
				<form:options items="${pubTypes}" />
			</form:select>
			<div class="errorDiv">
				<form:errors path="pubType" cssClass="errorMessage" />
			</div>
		</div>
		<div id="pubAbbrDiv">
			<form:label path="pubAbbr" class="labelCol">Pub Abbreviation</form:label>
			<form:input path="pubAbbr" maxlength="15"/>
			<div class="errorDiv">
				<form:errors path="pubAbbr" cssClass="errorMessage" />
			</div>
		</div>
		<div>
			<form:label path="pubInfo" class="labelCol">Pub Info</form:label>
			<form:input path="pubInfo" maxlength="40"/>
			<div class="errorDiv">
				<form:errors path="pubInfo" cssClass="errorMessage" />
			</div>
		</div>
	</div>
</div>
<div class="leftDefinitionForm">
	<div class="row">
		<form:label path="titleId" class="labelCol">Title ID</form:label>
		<input id="titleIdBox" type="text" disabled="disabled" />
		<form:hidden path="titleId"/>
		<div class="errorDiv">
			<form:errors path="titleId" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="nameLine1" class="labelCol">Name Line 1</form:label>
		<form:input path="nameLine1" />
		<div class="errorDiv">
			<form:errors path="nameLine1" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="nameLine2" class="labelCol">Name Line 2</form:label>
		<form:input path="nameLine2" />
		<div class="errorDiv">
			<form:errors path="nameLine2" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="nameLine3" class="labelCol">Name Line 3</form:label>
		<form:input path="nameLine3" />
		<div class="errorDiv">
			<form:errors path="nameLine3" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="nameLine4" class="labelCol">Name Line 4</form:label>
		<form:input path="nameLine4" />
		<div class="errorDiv">
			<form:errors path="nameLine4" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="copyright" class="labelCol">Copyright</form:label>
		<form:input path="copyright" />
		<div class="errorDiv">
			<form:errors path="copyright" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="copyrightPageText" class="labelCol">Copyright Page Text</form:label>
		<form:textarea path="copyrightPageText" />
		<div class="errorDiv">
			<form:errors path="copyrightPageText" cssClass="errorMessage" />
		</div>
	</div>
	
	<div class="row">
		<form:label path="materialId" class="labelCol">Material ID</form:label>
		<form:input path="materialId" />
		<div class="errorDiv">
			<form:errors path="materialId" cssClass="errorMessage" />
		</div>	</div>
	<div class="row">
		<label class="labelCol">TOC or NORT</label>
		<form:radiobutton path="isTOC" value="true" />TOC
		<form:radiobutton path="isTOC" value="false" />NORT
	</div>
	<div id="displayTOC" style="display:none">
		<div class="row">
			<form:label path="tocCollectionName" class="labelCol">TOC Collection</form:label>
			<form:input path="tocCollectionName" />
		<div class="errorDiv">
			<form:errors path="tocCollectionName" cssClass="errorMessage" />
		</div>
		</div>
		<div class="row">
			<form:label path="rootTocGuid" class="labelCol">Root TOC Guid</form:label>
			<form:input path="rootTocGuid" />
		<div class="errorDiv">
			<form:errors path="rootTocGuid" cssClass="errorMessage" />
		</div>
		</div>
	</div>
	<div id="displayNORT" style="display:none">
		<div class="row">
			<form:label path="nortDomain" class="labelCol">NORT Domain</form:label>
			<form:input path="nortDomain" />
			<div class="errorDiv">
				<form:errors path="nortDomain" cssClass="errorMessage" />
			</div>
		</div>
		<div class="row">
			<form:label path="nortFilterView" class="labelCol">NORT Filter View</form:label>
			<form:input path="nortFilterView" />
			<div class="errorDiv">
				<form:errors path="nortFilterView" cssClass="errorMessage" />
			</div>
		</div>
	</div>
	<div class="row">
		<label class="labelCol">Keywords</label>
		<div id="accordion">
			<h3><a href="#">Jurisdiction</a> <form:errors path="jurisdictionKeyword" cssClass="errorMessage" /></h3>
			<div>
				<form:checkboxes path="jurisdictionKeyword" items="${jurisdictions}" multiple="true" />
			</div>
			<h3><a href="#">Publisher</a> <form:errors path="publisherKeyword" cssClass="errorMessage" /></h3>
			<div>
				<form:checkboxes path="publisherKeyword" items="${publisherKeywords}" multiple="true" />
			</div>
			<h3><a href="#">Subject</a> <form:errors path="subjectKeyword" cssClass="errorMessage" /></h3>
			<div>
				<form:checkboxes path="subjectKeyword" items="${subjectKeywords}" multiple="true" />
			</div>
			<h3><a href="#">Type</a> <form:errors path="typeKeyword" cssClass="errorMessage" /></h3>
			<div>
				<form:checkboxes path="typeKeyword" items="${typeKeywords}" />
			</div>
		</div>
	</div>
</div>

<div class="rightDefinitionForm">
	<div id="authorName" class="row">
		<form:label path="authorInfo" class="labelCol">Author Information</form:label>
		<input type="button" onclick="addAuthorRow();" id="addAuthor" value="add" />
		<c:forEach items="${editBookDefinitionForm.authorInfo}" var="author" varStatus="aStatus">
			<div class="row">
				<form:input path="authorInfo[${aStatus.index}].prefix" title="prefix" class="prefix"  />
				<form:input path="authorInfo[${aStatus.index}].firstName"  title="first name" class="firstName" />
				<form:input path="authorInfo[${aStatus.index}].middleName"  title="middle name" class="middleName" />
				<form:input path="authorInfo[${aStatus.index}].lastName"   title="last name" class="lastName" />
				<form:input path="authorInfo[${aStatus.index}].suffix"  title="suffix" class="suffix" />
				<input type="button" value="Delete" class="rdelete" />
			</div>
		</c:forEach>
		<div id="addHere"></div>
	</div>
	<div class="row">
		<form:label path="additionalFrontMatterHeader1" class="labelCol">Additional Front Matter Header 1</form:label>
		<form:textarea path="additionalFrontMatterHeader1" />
		<div class="errorDiv">
			<form:errors path="additionalFrontMatterHeader1" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="additionalFrontMatterText1" class="labelCol">Additional Front Matter Text 1</form:label>
		<form:textarea path="additionalFrontMatterText1" />
		<div class="errorDiv">
			<form:errors path="additionalFrontMatterText1" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="additionalFrontMatterHeader2" class="labelCol">Additional Front Matter Header 2</form:label>
		<form:textarea path="additionalFrontMatterHeader2" />
		<div class="errorDiv">
			<form:errors path="additionalFrontMatterHeader2" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="additionalFrontMatterText2" class="labelCol">Additional Front Matter Text 2</form:label>
		<form:textarea path="additionalFrontMatterText2" />
		<div class="errorDiv">
			<form:errors path="additionalFrontMatterText2" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="publishDateText" class="labelCol">Publish Date Text</form:label>
		<form:input path="publishDateText" />
		<div class="errorDiv">
			<form:errors path="publishDateText" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="isbn" class="labelCol">ISBN</form:label>
		<form:input path="isbn" />
		<div class="errorDiv">
			<form:errors path="isbn" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="imageCollectionInformation" class="labelCol">Image Collection</form:label>
		<form:input path="imageCollectionInformation" />
		<div class="errorDiv">
			<form:errors path="imageCollectionInformation" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="currency" class="labelCol">Currency</form:label>
		<form:input path="currency" />
		<div class="errorDiv">
			<form:errors path="currency" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="autoUpdateSupport" class="labelCol">Auto Update Support</form:label>
		<form:radiobutton path="autoUpdateSupport" value="true" />True
		<form:radiobutton path="autoUpdateSupport" value="false" />False
		<div class="errorDiv">
			<form:errors path="autoUpdateSupport" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="searchIndex" class="labelCol">Search Index</form:label>
		<form:radiobutton path="searchIndex" value="true" />True
		<form:radiobutton path="searchIndex" value="false" />False
		<div class="errorDiv">
			<form:errors path="searchIndex" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="onePassSSOLinking" class="labelCol">One Pass SSO Linking</form:label>
		<form:radiobutton path="onePassSSOLinking" value="true" />True
		<form:radiobutton path="onePassSSOLinking" value="false" />False
		<div class="errorDiv">
			<form:errors path="onePassSSOLinking" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="keyCiteToplineFlag" class="labelCol">KeyCite Topline Flag</form:label>
		<form:radiobutton path="keyCiteToplineFlag" value="true" />True
		<form:radiobutton path="keyCiteToplineFlag" value="false" />False
		<div class="errorDiv">
			<form:errors path="keyCiteToplineFlag" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="isComplete" class="labelCol">Book Definition Status</form:label>
		<form:radiobutton path="isComplete" value="true" />Complete
		<form:radiobutton path="isComplete" value="false" />Incomplete
		<div class="errorDiv">
			<form:errors path="isComplete" cssClass="errorMessage" />
		</div>
	</div>
</div>

