<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>

<script type="text/javascript">
		// Declare Global Variables
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
				}
			} else if(contentType == contentTypeEnum.COURT_RULES) {
				if (pubInfo) {
					titleId.push(state, pubType, pubInfo);
				} else {
					titleId.push(state, pubType);
				}
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
			}
		};
		
		// Function to determine which divs to show depending on the content type.
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
		
		var setContentType = function(ct) {
			if (ct == "<%= WebConstants.KEY_ANALYTICAL %>") {
				contentType = contentTypeEnum.ANALYTICAL;
			} else if(ct == "<%= WebConstants.KEY_COURT_RULES %>") {
				contentType = contentTypeEnum.COURT_RULES;
			} else if (ct == "<%= WebConstants.KEY_SLICE_CODES%>") {
				contentType = contentTypeEnum.SLICE_CODES;
			} else {
				contentType = contentTypeEnum.NONE;
			};
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
			$( ".buttons input:submit,.buttons a,.buttons  button" ).button();
			
			// Setup change handlers
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
			
			$('input:radio[name=TOCorNORT]').change(function () {
				
			});
			
			$( "#accordion" ).accordion({
				collapsible: true,
				fillSpace: true,
				active:0
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
			<form:errors path="publisher" cssClass="errorMessage" />
		</div>
		<div id="stateDiv">
			<form:label path="state" class="labelCol">State</form:label>
			<form:select path="state" >
				<form:option value="" label="SELECT" />
				<form:options items="${states}" />
			</form:select>
			<form:errors path="state" cssClass="errorMessage" />
		</div>
		<div id="jurisdictionDiv">
			<form:label path="jurisdiction" class="labelCol">Juris</form:label>
			<form:select path="jurisdiction" >
				<form:option value="" label="SELECT" />
				<form:options items="${jurisdictions}" />
			</form:select>
			<form:errors path="jurisdiction" cssClass="errorMessage" />
		</div>
		<div id="pubTypeDiv">
			<form:label path="pubType" class="labelCol">Pub Type</form:label>
			<form:select path="pubType" >
				<form:option value="" label="SELECT" />
				<form:options items="${pubTypes}" />
			</form:select>
			<form:errors path="pubType" cssClass="errorMessage" />
		</div>
		<div id="pubAbbrDiv">
			<form:label path="pubAbbr" class="labelCol">Pub Abbreviation</form:label>
			<form:input path="pubAbbr" maxlength="15"/>
			<form:errors path="pubAbbr" cssClass="errorMessage" />
		</div>
		<div>
			<form:label path="pubInfo" class="labelCol">Pub Info</form:label>
			<form:input path="pubInfo" maxlength="40"/>
			<form:errors path="pubInfo" cssClass="errorMessage" />
		</div>
	</div>
</div>
<div class="leftDefinitionForm">
	<div class="row">
		<form:label path="titleId" class="labelCol">Title ID</form:label>
		<input id="titleIdBox" type="text" disabled="disabled" />
		<form:hidden path="titleId"/>
		<form:errors path="titleId" cssClass="errorMessage" />
	</div>
	<div class="row">
		<form:label path="bookName" class="labelCol">Name</form:label>
		<form:input path="bookName" />
		<form:errors path="bookName" cssClass="errorMessage" />
	</div>
	<div class="row">
		<form:label path="majorVersion" class="labelCol">Major Version</form:label>
		<form:input path="majorVersion" />
		<form:errors path="majorVersion" cssClass="errorMessage" />
	</div>
	<div class="row">
		<form:label path="minorVersion" class="labelCol">Minor Version</form:label>
		<form:input path="minorVersion" />
		<form:errors path="minorVersion" cssClass="errorMessage" />
	</div>
	<div class="row">
		<label class="labelCol">TOC or NORT</label>
		<input type="radio" name="TOCorNORT" value="TOC" />TOC
		<input type="radio" name="TOCorNORT" value="NORT" />NORT
	</div>
	<div id="displayTOC" style="display:none">
		<div class="row">
			<form:label path="tocCollectionName" class="labelCol">TOC Collection</form:label>
			<form:input path="tocCollectionName" />
			<form:errors path="tocCollectionName" cssClass="errorMessage" />
		</div>
		<div class="row">
			<form:label path="rootTocGuid" class="labelCol">Root TOC Guid</form:label>
			<form:input path="rootTocGuid" />
			<form:errors path="rootTocGuid" cssClass="errorMessage" />
		</div>
	</div>
	<div id="displayNORT" style="display:none">
		<div class="row">
			<form:label path="nortDomain" class="labelCol">NORT Domain</form:label>
			<form:input path="nortDomain" />
			<form:errors path="nortDomain" cssClass="errorMessage" />
		</div>
		<div class="row">
			<form:label path="nortFilterView" class="labelCol">NORT Filter View</form:label>
			<form:input path="nortFilterView" />
			<form:errors path="nortFilterView" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<label class="labelCol">Keywords</label>
		<div id="accordion">
			<h3><a href="#">Type</a> <form:errors path="typeKeyword" cssClass="errorMessage" /></h3>
			<div>
				<form:checkboxes path="typeKeyword" items="${typeKeywords}" />
			</div>
			<h3><a href="#">Subject</a> <form:errors path="subjectKeyword" cssClass="errorMessage" /></h3>
			<div>
				<form:checkboxes path="subjectKeyword" items="${subjectKeywords}" multiple="true" />
			</div>
			<h3><a href="#">Publisher</a> <form:errors path="publisherKeyword" cssClass="errorMessage" /></h3>
			<div>
				<form:checkboxes path="publisherKeyword" items="${publisherKeywords}" multiple="true" />
			</div>
			<h3><a href="#">Jurisdiction</a> <form:errors path="jurisdictionKeyword" cssClass="errorMessage" /></h3>
			<div>
				<form:checkboxes path="jurisdictionKeyword" items="${states}" multiple="true" />
			</div>
		</div>
	</div>
</div>

<div class="rightDefinitionForm">
	<div class="row">
		<form:label path="copyright" class="labelCol">Copyright</form:label>
		<form:input path="copyright" />
		<form:errors path="copyright" cssClass="errorMessage" />
	</div>
	<div class="row">
		<form:label path="materialId" class="labelCol">Material ID</form:label>
		<form:input path="materialId" />
		<form:errors path="materialId" cssClass="errorMessage" />
	</div>
	<div class="row">
		<form:label path="authorInfo" class="labelCol">Author Information</form:label>
		<form:input path="authorInfo" />
		<form:errors path="authorInfo" cssClass="errorMessage" />
	</div>
	<div class="row">
		<form:label path="additionalFrontMatterText" class="labelCol">Additional Front Matter Text</form:label>
		<form:textarea path="additionalFrontMatterText" />
		<form:errors path="additionalFrontMatterText" cssClass="errorMessage" />
	</div>
	<div class="row">
		<form:label path="isbn" class="labelCol">ISBN</form:label>
		<form:input path="isbn" />
		<form:errors path="isbn" cssClass="errorMessage" />
	</div>
	<div class="row">
		<form:label path="imageCollectionInformation" class="labelCol">Image Collection</form:label>
		<form:input path="imageCollectionInformation" />
		<form:errors path="imageCollectionInformation" cssClass="errorMessage" />
	</div>
	<div class="row">
		<form:label path="currency" class="labelCol">Currency</form:label>
		<form:input path="currency" />
		<form:errors path="currency" cssClass="errorMessage" />
	</div>
	<div class="row">
		<form:label path="autoUpdateSupport" class="labelCol">Auto Update Support</form:label>
		<form:radiobutton path="autoUpdateSupport" value="true" />True
		<form:radiobutton path="autoUpdateSupport" value="false" />False
		<form:errors path="autoUpdateSupport" cssClass="errorMessage" />
	</div>
	<div class="row">
		<form:label path="searchIndex" class="labelCol">Search Index</form:label>
		<form:radiobutton path="searchIndex" value="true" />True
		<form:radiobutton path="searchIndex" value="false" />False
		<form:errors path="searchIndex" cssClass="errorMessage" />
	</div>
	<div class="row">
		<form:label path="onePassSSOLinking" class="labelCol">One Pass SSO Linking</form:label>
		<form:radiobutton path="onePassSSOLinking" value="true" />True
		<form:radiobutton path="onePassSSOLinking" value="false" />False
		<form:errors path="onePassSSOLinking" cssClass="errorMessage" />
	</div>
	<div class="row">
		<form:label path="keyCiteToplineFlag" class="labelCol">KeyCite Topline Flag</form:label>
		<form:radiobutton path="keyCiteToplineFlag" value="true" />True
		<form:radiobutton path="keyCiteToplineFlag" value="false" />False
		<form:errors path="keyCiteToplineFlag" cssClass="errorMessage" />
	</div>
</div>

