<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>

<script type="text/javascript">
		// Global Variables
		var contentType = "";
		var publisher = "";
		var state = "";
		var pubType = "";
		var pubAbbr = "";
		var pubInfo = "";
		var jurisdiction = "";
		var contentTypeEnum = {
				ANALYTICAL : 1,
				COURT_RULES: 2,
				SLICE_CODES : 3
		}; 
		
		
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
				titleId.push(state, pubType, pubInfo);
			} else if(contentType == contentTypeEnum.SLICE_CODES) {
				titleId.push(jurisdiction, pubInfo);
			};

			// Set up Namespace
			if (contentType != "") {
				var fullyQualifiedTitleIdArray = [];
				var contentTypeAbbr = "";
				if(contentType == contentTypeEnum.ANALYTICAL) {
					contentTypeAbbr = "<%= WebConstants.KEY_ANALYTICAL_ABBR %>";
				} else if (contentType == contentTypeEnum.COURT_RULES) {
					contentTypeAbbr = "<%= WebConstants.KEY_COURT_RULES_ABBR %>";
				} else if(contentType == contentTypeEnum.SLICE_CODES) {
					contentTypeAbbr = "<%= WebConstants.KEY_SLICE_CODES_ABBR %>";
				} else {
					$('#titleId').val("##ERROR##");
					$('#titleIdBox').val("##ERROR##");
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
				$('#displayTOC').show();
				$('#displayNORT').hide();

				$('#pubAbbrDiv').show();
				
				$('#publishDetailDiv').show();
			} else if(contentType == contentTypeEnum.COURT_RULES || contentType == contentTypeEnum.SLICE_CODES) {
				$('#displayTOC').hide();
				$('#displayNORT').show();
				
				if(contentType == contentTypeEnum.SLICE_CODES) {
					$('#jurisdictionDiv').show();
				} else {
					$('#stateDiv').show();
					$('#pubTypeDiv').show();
				};

				$('#publishDetailDiv').show();
			} else {
				$('#displayTOC').hide();
				$('#displayNORT').hide();
			};
		};
		
		var setContentType = function(ct) {
			if (ct == "<%= WebConstants.KEY_ANALYTICAL %>") {
				contentType = contentTypeEnum.ANALYTICAL;
			} else if(ct == "<%= WebConstants.KEY_COURT_RULES %>") {
				contentType = contentTypeEnum.COURT_RULES;
			} else if (ct == "<%= WebConstants.KEY_SLICE_CODES%>") {
				contentType = contentTypeEnum.SLICE_CODES;
			}
		};
		
		$(document).ready(function() {
			
			// Setup change handlers
			$('#contentType').change(function () {
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
	</div>
	<div id="publishDetailDiv" style="display:none">
		<div>
			<label>Publisher</label><form:input path="publisher" maxlength="4" />
		</div>
		<div id="stateDiv">
			<form:label path="state" class="labelCol">State</form:label>
			<form:select path="state" >
				<form:option value="" label="SELECT" />
				<form:options items="${states}" />
			</form:select>
		</div>
		<div id="jurisdictionDiv">
			<form:label path="jurisdiction" class="labelCol">Juris</form:label>
			<form:select path="jurisdiction" >
				<form:option value="" label="SELECT" />
				<form:options items="${jurisdictions}" />
			</form:select>
		</div>
		<div id="pubTypeDiv">
			<form:label path="pubType" class="labelCol">Pub Type</form:label>
			<form:select path="pubType" >
				<form:option value="" label="SELECT" />
				<form:options items="${pubTypes}" />
			</form:select>
		</div>
		<div id="pubAbbrDiv">
			<label>Pub Abbreviation</label><form:input path="pubAbbr" maxlength="15"/>
		</div>
		<div>
			<label>Pub Info</label><form:input path="pubInfo" maxlength="40"/>
		</div>
	</div>
</div>
<div class="leftDefinitionForm">
	<div class="row">
		<form:label path="titleId" class="labelCol">Title ID</form:label>
		<input id="titleIdBox" type="text" disabled="disabled" />
		<form:hidden path="titleId"/>
	</div>
	<div class="row">
		<form:label path="bookName" class="labelCol">Book Name</form:label>
		<form:input path="bookName" />
	</div>
	<div class="row">
		<form:label path="majorVersion" class="labelCol">Major Version</form:label>
		<form:input path="majorVersion" />
	</div>
	<div class="row">
		<form:label path="minorVersion" class="labelCol">Minor Version</form:label>
		<form:input path="minorVersion" />
	</div>
	<fieldset class="keywords">
		<legend>Keywords</legend>
		<div class="row">
			<form:label path="typeKeyword" class="labelCol">Type</form:label>
			<form:select path="typeKeyword" items="${typeKeywords}" multiple="true" />
		</div>
		
		<div class="row">
			<form:label path="subjectKeyword" class="labelCol">Subject</form:label>
			<form:select path="subjectKeyword" items="${subjectKeywords}" multiple="true" />
		</div>
		
		<div class="row">
			<form:label path="publisherKeyword" class="labelCol">Publisher</form:label>
			<form:select path="publisherKeyword" items="${publisherKeywords}" multiple="true" />
		</div>
		
		<div class="row">
			<form:label path="jurisdictionKeyword" class="labelCol">Jurisdiction</form:label>
			<form:select path="jurisdictionKeyword" items="${jurisdictionKeywords}" multiple="true" />
		</div>
	</fieldset>

	<div class="row">
		<form:label path="copyright" class="labelCol">Copyright</form:label>
		<form:input path="copyright" />
	</div>
	<div class="row">
		<form:label path="materialId" class="labelCol">Material ID</form:label>
		<form:input path="materialId" />
	</div>
	<div class="row">
		<form:label path="authorInfo" class="labelCol">Author Information</form:label>
		<form:input path="authorInfo" />
	</div>
	<div id="displayTOC" style="display:none">
		<div class="row">
			<form:label path="tocCollectionName" class="labelCol">TOC Collection</form:label>
			<form:input path="tocCollectionName" />
		</div>
		<div class="row">
			<form:label path="rootTocGuid" class="labelCol">Root TOC Guid</form:label>
			<form:input path="rootTocGuid" />
		</div>
	</div>
	<div id="displayNORT" style="display:none">
		<div class="row">
			<form:label path="nortDomain" class="labelCol">NORT Domain</form:label>
			<form:input path="nortDomain" />
		</div>
		<div class="row">
			<form:label path="nortFilterView" class="labelCol">NORT Filter View</form:label>
			<form:input path="nortFilterView" />
		</div>
	</div>
</div>

<div class="rightDefinitionForm">
	<div class="row">
		<form:label path="coverImage" class="labelCol">Cover Image</form:label>
		<form:input path="coverImage" />
	</div>
	<div class="row">
		<form:label path="isbn" class="labelCol">ISBN</form:label>
		<form:input path="isbn" />
	</div>
	<div class="row">
		<form:label path="materialIdEmbeddedInDocText" class="labelCol">Material ID Embedded in Doc Text</form:label>
		<form:radiobutton path="materialIdEmbeddedInDocText" value="true" />True
		<form:radiobutton path="materialIdEmbeddedInDocText" value="false" />False
	</div>
	<div class="row">
		<form:label path="autoUpdateSupport" class="labelCol">Auto Update Support</form:label>
		<form:radiobutton path="autoUpdateSupport" value="true" />True
		<form:radiobutton path="autoUpdateSupport" value="false" />False
	</div>
	<div class="row">
		<form:label path="searchIndex" class="labelCol">Search Index</form:label>
		<form:radiobutton path="searchIndex" value="true" />True
		<form:radiobutton path="searchIndex" value="false" />False
	</div>
	<div class="row">
		<form:label path="onePassSSOLinking" class="labelCol">One Pass SSO Linking</form:label>
		<form:radiobutton path="onePassSSOLinking" value="true" />True
		<form:radiobutton path="onePassSSOLinking" value="false" />False
	</div>
	<div class="row">
		<form:label path="language" class="labelCol">Language</form:label>
		<form:input path="language" />
	</div>
	<div class="row">
		<form:label path="imageView" class="labelCol">Image View</form:label>
		<form:radiobutton path="imageView" value="true" />True
		<form:radiobutton path="imageView" value="false" />False
	</div>
	<div class="row">
		<form:label path="imageCollectionInformation" class="labelCol">Image Collection</form:label>
		<form:input path="imageCollectionInformation" />
	</div>
	<div class="row">
		<form:label path="nameSpacePubId" class="labelCol">Name Space Pub ID</form:label>
		<form:input path="nameSpacePubId" />
	</div>
	<div class="row">
		<form:label path="currency" class="labelCol">Currency</form:label>
		<form:input path="currency" />
	</div>
</div>

