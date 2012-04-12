<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>

<script type="text/javascript">
		// Declare Global Variables
		var authorIndex = ${numberOfAuthors};
		var frontMatterPageIndex = ${numberOfFrontMatters};
		var contentType = "";
		var publisher = "";
		var state = "";
		var pubType = "";
		var pubAbbr = "";
		var pubInfo = "";
		var jurisdiction = "";
		
		
		// Function to create Fully Qualifed Title ID from the publisher options
		var updateTitleId = function() {

			var titleId = [];
			
			// Set up Title ID
			if(contentType == "<%= WebConstants.KEY_ANALYTICAL_ABBR%>") {
				if (pubInfo) {
					titleId.push(pubAbbr, pubInfo);
				} else {
					titleId.push(pubAbbr);
				};
			} else if(contentType == "<%= WebConstants.KEY_COURT_RULES_ABBR%>") {
				if (pubInfo) {
					titleId.push(state, pubType, pubInfo);
				} else {
					titleId.push(state, pubType);
				};
			} else if(contentType == "<%= WebConstants.KEY_SLICE_CODES_ABBR%>") {
				titleId.push(jurisdiction, pubInfo);
			} else {
				titleId.push(pubInfo);
			};

			// Set up Namespace
			if (contentType) {
				var fullyQualifiedTitleIdArray = [];
				fullyQualifiedTitleIdArray.push(publisher, contentType, titleId.join("_"));
				
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
			if(contentType == "<%= WebConstants.KEY_ANALYTICAL_ABBR %>") {
				$('#pubAbbrDiv').show();
			} else if(contentType == "<%= WebConstants.KEY_COURT_RULES_ABBR %>") {
				$('#stateDiv').show();
				$('#pubTypeDiv').show();
			} else if(contentType == "<%= WebConstants.KEY_SLICE_CODES_ABBR %>") {
				$('#jurisdictionDiv').show();
			}
			
			if (contentType) {
				$('#publishDetailDiv').show();
			} else {
				$('#publishDetailDiv').hide();
			}
		};
		
		// Add another author row
		var addAuthorRow = function() {
			var appendTxt = "<div class='row'>";
			appendTxt = appendTxt + "<input id=\"authorInfo" + authorIndex + ".authorId\" name=\"authorInfo[" + authorIndex + "].authorId\" type=\"hidden\" />";
			appendTxt = appendTxt + "<input id=\"authorInfo" + authorIndex + ".ebookDefinitionId\" name=\"authorInfo[" + authorIndex + "].ebookDefinitionId\" type=\"hidden\" />";
			appendTxt = appendTxt + "<input class=\"prefix\" id=\"authorInfo" + authorIndex + ".authorNamePrefix\" name=\"authorInfo[" + authorIndex + "].authorNamePrefix\" type=\"text\" title=\"prefix\"/>";
			appendTxt = appendTxt + "<input class=\"firstName\" id=\"authorInfo" + authorIndex + ".authorFirstName\" name=\"authorInfo[" + authorIndex + "].authorFirstName\" type=\"text\" title=\"first name\"/>";
			appendTxt = appendTxt + "<input class=\"middleName\" id=\"authorInfo" + authorIndex + ".authorMiddleName\" name=\"authorInfo[" + authorIndex + "].authorMiddleName\" type=\"text\" title=\"middle name\"/>";
			appendTxt = appendTxt + "<input class=\"lastName\" id=\"authorInfo" + authorIndex + ".authorLastName\" name=\"authorInfo[" + authorIndex + "].authorLastName\" type=\"text\" title=\"last name\"/>";
			appendTxt = appendTxt + "<input class=\"suffix\" id=\"authorInfo" + authorIndex + ".authorNameSuffix\" name=\"authorInfo[" + authorIndex + "].authorNameSuffix\" type=\"text\" title=\"suffix\"/>";
			appendTxt = appendTxt + "<input class=\"sequenceNumber\" id=\"authorInfo" + authorIndex + ".sequenceNum\" name=\"authorInfo[" + authorIndex + "].sequenceNum\" type=\"text\" title=\"Seq Num.\" maxlength=\"9\" />";
			appendTxt = appendTxt + "<div>";
			appendTxt = appendTxt + "Additional Text";
			appendTxt = appendTxt + "<textarea class=\"additionalText\" id=\"authorInfo" + authorIndex + ".authorAddlText\" name=\"authorInfo[" + authorIndex + "].authorAddlText\" title=\"Additional Text\"/>";
			appendTxt = appendTxt + "</div>";
			appendTxt = appendTxt + "<input type=\"button\" value=\"Delete\" class=\"rdelete\" />";
			appendTxt = appendTxt + "</div>";
			$("#addHere").before(appendTxt);
			authorIndex = authorIndex + 1;
			
			textboxHint("authorName");
		};
		
		// Add another additional Front Matter Page row
		var addFrontMatterPageRow = function() {
			var appendTxt = "<div class='row frontMatterPage'>";
			appendTxt = appendTxt + "<input id=\"frontMatters" + frontMatterPageIndex + ".pageTocLabel\" name=\"frontMatters[" + frontMatterPageIndex + "].pageTocLabel\" type=\"text\" title=\"Page TOC Label\"/>";
			appendTxt = appendTxt + "<input id=\"frontMatters" + frontMatterPageIndex + ".pageHeadingLabel\" name=\"frontMatters[" + frontMatterPageIndex + "].pageHeadingLabel\" type=\"text\" title=\"Page Heading Label\"/>";
			appendTxt = appendTxt + "<input class=\"sequenceNumber\" id=\"frontMatters" + frontMatterPageIndex + ".sequenceNum\" name=\"frontMatters[" + frontMatterPageIndex + "].sequenceNum\" type=\"text\" title=\"Page Seq Num.\" maxlength=\"9\" />";
			appendTxt = appendTxt + "<input type=\"button\" value=\"Delete Page\" class=\"rdelete\" />";
			appendTxt = appendTxt + "<div id='addAdditionalSection_" + frontMatterPageIndex + "'></div>";
			appendTxt = appendTxt + "<input type=\"button\" value=\"Add Section\" class=\"addSection\" pageIndex=\"" + frontMatterPageIndex + "\" sectionIndex=\"0\" />";
			appendTxt = appendTxt + "</div>";
			$("#addAdditionPageHere").before(appendTxt);
			frontMatterPageIndex = frontMatterPageIndex + 1;
			
			textboxHint("additionFrontMatterBlock");
		};
		
		// Add another additional Front Matter Section row
		var addFrontMatterSectionRow = function(pageIndex, sectionIndex) {
			var appendTxt = "<div class='row frontMatterSection'>";
			appendTxt = appendTxt + "<input id=\"frontMatters" + pageIndex + ".frontMatterSections" + sectionIndex + ".sectionHeading\" name=\"frontMatters[" + pageIndex + "].frontMatterSections["+ sectionIndex +"].sectionHeading\" type=\"text\" title=\"Section Heading\"/>";
			appendTxt = appendTxt + "<input id=\"frontMatters" + pageIndex + ".frontMatterSections" + sectionIndex + ".sequenceNum\" name=\"frontMatters[" + pageIndex + "].frontMatterSections["+ sectionIndex +"].sequenceNum\" type=\"text\" title=\"Section Seq Num.\" class=\"sequenceNumber\" maxlength=\"9\" />";
			appendTxt = appendTxt + "<input type=\"button\" value=\"Delete Section\" class=\"rdelete\" />";
			appendTxt = appendTxt + "<textarea id=\"frontMatters" + pageIndex + ".frontMatterSections" + sectionIndex + ".sectionText\" name=\"frontMatters[" + pageIndex + "].frontMatterSections["+ sectionIndex +"].sectionText\" title=\"Section Text\" class=\"frontMatterSectionTextArea\"/>";
			appendTxt = appendTxt + "<div id='addAdditionalPdf_" + pageIndex + "_" + sectionIndex + "'></div>";
			appendTxt = appendTxt + "<input type=\"button\" value=\"Add Pdf\" class=\"addPdf\" pageIndex=\"" + pageIndex + "\" sectionIndex=\"" + sectionIndex + "\" pdfIndex=\"0\"  />";
			appendTxt = appendTxt + "</div>";
			$("#addAdditionalSection_" + pageIndex).before(appendTxt);
			
			textboxHint("additionFrontMatterBlock");
		};
		
		// Add another additional Front Matter Pdf row
		var addFrontMatterPdfRow = function(pageIndex, sectionIndex, pdfIndex) {
			var appendTxt = "<div class='row frontMatterPdf'>";
			appendTxt = appendTxt + "<input id=\"frontMatters" + pageIndex + ".frontMatterSections" + sectionIndex + ".pdfs"+ pdfIndex +".pdfLinkText\" name=\"frontMatters[" + pageIndex + "].frontMatterSections["+ sectionIndex +"].pdfs["+ pdfIndex +"].pdfLinkText\" type=\"text\" title=\"PDF Link Text\"/>";
			appendTxt = appendTxt + "<input id=\"frontMatters" + pageIndex + ".frontMatterSections" + sectionIndex + ".pdfs"+ pdfIndex +".pdfFilename\" name=\"frontMatters[" + pageIndex + "].frontMatterSections["+ sectionIndex +"].pdfs["+ pdfIndex +"].pdfFilename\" type=\"text\" title=\"PDF Filename\"/>";
			appendTxt = appendTxt + "<input id=\"frontMatters" + pageIndex + ".frontMatterSections" + sectionIndex + ".pdfs"+ pdfIndex +".sequenceNum\" name=\"frontMatters[" + pageIndex + "].frontMatterSections["+ sectionIndex +"].pdfs["+ pdfIndex +"].sequenceNum\" type=\"text\" title=\"Section Seq Num.\" class=\"sequenceNumber\" maxlength=\"9\" />";
			appendTxt = appendTxt + "<input type=\"button\" value=\"Delete Pdf\" class=\"rdelete\" />";
			appendTxt = appendTxt + "</div>";
			$("#addAdditionalPdf_" + pageIndex + "_" + sectionIndex).before(appendTxt);
			
			textboxHint("additionFrontMatterBlock");
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
		
		// Only allow number inputs
		$(document).on("keydown", ".sequenceNumber", function(e) { 
	        if (e.shiftKey || e.ctrlKey || e.altKey) { // if shift, ctrl or alt keys held down 
	            e.preventDefault();         // Prevent character input 
	        } else { 
	            var n = e.keyCode; 
	            if (!((n == 8)              // backspace 
	            || (n == 9)              	// tab
	            || (n == 46)                // delete 
	            || (n >= 35 && n <= 40)     // arrow keys/home/end 
	            || (n >= 48 && n <= 57)     // numbers on keyboard 
	            || (n >= 96 && n <= 105))   // number on keypad 
	            ) { 
	                e.preventDefault();     // Prevent character input 
	            };
	        }; 
	    });
		
		var getContentTypeAbbr = function() {
			var contentIndex = $('#contentTypeId').val();
			if(contentIndex) {
			    $.getJSON("<%= WebConstants.MVC_GET_CONTENT_TYPE %>",
			    		{contentTypeId : contentIndex},
			    		function(data) { 
			    			contentType = data.abbreviation;
					    	updateTitleId();
							determineOptions();
			    		}).error(function(jqXHR, textStatus, errorThrown) {
			    		    alert("Error: " + textStatus + " errorThrown: " + errorThrown);
			    		});
			} else {
				contentType = "";
				updateTitleId();
				determineOptions();
			};
		};
		
		$(document).ready(function() {
			<%-- Setup change handlers  --%>
			$('#contentTypeId').change(function () {
				// Clear out information when content type changes
				clearTitleInformation();
				$('.generateTitleID .errorDiv').hide();
				
				getContentTypeAbbr();
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
			
			<%-- Setup Button Click handlers  --%>
			$('#addAuthor').click(function () {
				addAuthorRow();
				
				<%-- IE8 bug: forcing reflow/redraw to resize the parent div --%>
				$('#authorName').hide();
				$('#authorName').show();
			});
			
			//Update formValidation field if Validation button is pressed
			$('#validate').click(function () {
				$('#validateForm').val(true);
				$('<%= EditBookDefinitionForm.FORM_NAME %>').submit();
			});
			
			// Add Comment
			$('#save').click(function(e) {
		        //Cancel the link behavior
		        e.preventDefault();
		     
		        //Get the screen height and width
		        var maskHeight = $(document).height();
		        var maskWidth = $(window).width();
		     
		        //Set height and width to mask to fill up the whole screen
		        $('#mask').css({'width':maskWidth,'height':maskHeight});
		         
		        //transition effect 
		        $('#mask').fadeTo("fast",0.5);  
		     
		        //Get the window height and width
		        var winH = $(window).height();
		        var winW = $(window).width();
		               
		        //Set the popup window to center
		        $('#dialog').css('top',  winH/2-$('#dialog').height()/2);
		        $('#dialog').css('left', winW/2-$('#dialog').width()/2);
		     
		        //transition effect
		        $('#dialog').fadeIn(500); 
			});
			
			//if close button is clicked
		    $('#dialog .cancel').click(function (e) {
		        //Cancel the link behavior
		        e.preventDefault();
		        $('#mask, .window').hide();
		    });     
		     
		    //if mask is clicked
		    $('#mask').click(function () {
		        $(this).hide();
		        $('.window').hide();
		    });         
		     
			
			// Determine to show NORT or TOC fields
			$('input:radio[name=isTOC]').change(function () {
				updateTOCorNORT($(this).val());
			});
			
			$( "#accordion" ).accordion({
				fillSpace: true
			});
			
			$(".rdelete").live("click", function () {
				var srow = $(this).parent();
				srow.remove();
				
				<%-- IE8 bug: forcing reflow/redraw to resize the parent div --%>
				$('#authorName').hide();
				$('#authorName').show();
			});
			
			$('#addFrontMatterPage').click(function () {
				addFrontMatterPageRow();
			});
			
			$(".addSection").live("click", function () {
				// Retrieve additional page and section indexes from DOM object
				pageIndex = $(this).attr("pageIndex");
				sectionIndex = $(this).attr("sectionIndex");
				addFrontMatterSectionRow(pageIndex, sectionIndex);
				
				// Increment sectionIndex
				nextIndex = parseInt(sectionIndex) + 1;
				$(this).attr("sectionIndex", nextIndex);
			});
			
			$(".addPdf").live("click", function () {
				// Retrieve additional page, section, and pdf indexes from DOM object
				pageIndex = $(this).attr("pageIndex");
				sectionIndex = $(this).attr("sectionIndex");
				pdfIndex = $(this).attr("pdfIndex");
				addFrontMatterPdfRow(pageIndex, sectionIndex, pdfIndex);
				
				// Increment sectionIndex
				nextIndex = parseInt(pdfIndex) + 1;
				$(this).attr("pdfIndex", nextIndex);
			});
			
			// Initialize Global variables
			publisher = $('#publisher').val();
			state = $('#state').val();
			jurisdiction = $('#jurisdiction').val();
			pubType = $('#pubType').val();
			pubAbbr = $('#pubAbbr').val();
			pubInfo = $('#pubInfo').val();
			getContentTypeAbbr();
			
			// Setup view
			determineOptions();
			updateTitleId();
			updateTOCorNORT($('input:radio[name=isTOC]:checked').val());
			textboxHint("authorName");
			textboxHint("nameLine");
			textboxHint("additionFrontMatterBlock");
			$('#publicationCutoffDate').datepicker({
				minDate: new Date()
			});
			
			// Set validateForm
			$('#validateForm').val(false);
		});
</script>

<form:hidden path="validateForm" />
<div class="validateFormDiv">
	<h2><form:errors path="validateForm" cssClass="errorMessage" /></h2>
</div>
<%-- Check if book has been published --%>
<c:choose>
	<c:when test="${!isPublished}">
		<div class="generateTitleID">
			<div>
				<form:label path="contentTypeId" class="labelCol">Content Type</form:label>
				<form:select path="contentTypeId" >
					<form:option value="" label="SELECT" />
					<form:options items="${contentTypes}" />
				</form:select>
				<form:errors path="contentTypeId" cssClass="errorMessage" />
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
					<form:input path="pubAbbr" maxlength="14"/>
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
	</c:when>
	<c:otherwise>
		<form:hidden path="contentTypeId"/>
		<form:hidden path="publisher"/>
		<form:hidden path="state"/>
		<form:hidden path="jurisdiction"/>
		<form:hidden path="pubType"/>
		<form:hidden path="pubAbbr"/>
		<form:hidden path="pubInfo"/>
	</c:otherwise>
</c:choose>
<form:hidden path="bookdefinitionId" />
<div class="section">
	<div class="sectionLabel">
		General
	</div>
	<div class="centerSection">
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
				<form:label path="proviewDisplayName" class="labelCol">ProView Display Name</form:label>
				<form:input path="proviewDisplayName" />
				<div class="errorDiv">
					<form:errors path="proviewDisplayName" cssClass="errorMessage" />
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
				<form:input path="isbn" maxlength="17" />
				<div class="errorDiv">
					<form:errors path="isbn" cssClass="errorMessage" />
				</div>
			</div>
		
			<div class="row">
				<form:label path="materialId" class="labelCol">Sub Material Number</form:label>
				<form:input path="materialId" maxlength="18" />
				<div class="errorDiv">
					<form:errors path="materialId" cssClass="errorMessage" />
				</div>	
			</div>
		</div>
		
		<div class="rightDefinitionForm">
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
					<form:label path="docCollectionName" class="labelCol">DOC Collection</form:label>
					<form:input path="docCollectionName" />
					<div class="errorDiv">
						<form:errors path="docCollectionName" cssClass="errorMessage" />
					</div>
				</div>
				<div class="row">
					<form:label path="rootTocGuid" class="labelCol">Root TOC Guid</form:label>
					<form:input path="rootTocGuid" maxlength="33"/>
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
				<form:label path="publicationCutoffDate" class="labelCol">Publication Cut-off Date</form:label>
				<form:input path="publicationCutoffDate" />
				<div class="errorDiv">
					<form:errors path="publicationCutoffDate" cssClass="errorMessage" />
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
		</div>
	</div>
</div>

<div class="section">
	<div class="sectionLabel">
		ProView Options
	</div>
	<div class="centerSection">
		<div class="leftDefinitionForm">
			<div class="row">
				<label class="labelCol">Keywords</label>
				<div id="accordion">
					<c:forEach items="${keywordTypeCode}" var="keyword">
						<h3><a href="#">${keyword.name}</a></h3>
						<div>
							<c:forEach items="${keyword.values}" var="value">
								<div>
									<form:checkbox path="keywords" value="${value.id}"/>${value.name}
								</div>
							</c:forEach>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
		<div class="rightDefinitionForm">
			<div class="row">
				<form:label path="isProviewTableView" class="labelCol">Use ProView Table View</form:label>
				<form:radiobutton path="isProviewTableView" value="true" />True
				<form:radiobutton path="isProviewTableView" value="false" />False
				<div class="errorDiv">
					<form:errors path="isProviewTableView" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="autoUpdateSupport" class="labelCol">Auto-update Support</form:label>
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
				<form:label path="enableCopyFeatureFlag" class="labelCol">Enable Copy Feature</form:label>
				<form:radiobutton path="enableCopyFeatureFlag" value="true" />True
				<form:radiobutton path="enableCopyFeatureFlag" value="false" />False
				<div class="errorDiv">
					<form:errors path="enableCopyFeatureFlag" cssClass="errorMessage" />
				</div>
			</div>
		</div>
	</div>
</div>

<div class="section">
	<div class="sectionLabel">
		Front Matter
	</div>
	<div class="centerSection">
		<div class="leftDefinitionForm">
			<div class="row">
				<form:label path="frontMatterTocLabel" class="labelCol">Front Matter TOC Label</form:label>
				<form:input path="frontMatterTocLabel" />
				<div class="errorDiv">
					<form:errors path="frontMatterTocLabel" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="copyright" class="labelCol">Copyright</form:label>
				<form:textarea path="copyright" />
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
				<form:label path="frontMatterTitle" class="labelCol">Main Title</form:label>
				<form:hidden path="frontMatterTitle.ebookNameId" />
				<form:hidden path="frontMatterTitle.sequenceNum" value="1" />
				<form:textarea path="frontMatterTitle.bookNameText" />
				<div class="errorDiv2">
					<form:errors path="frontMatterTitle.bookNameText" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="frontMatterSubtitle" class="labelCol">Sub Title</form:label>
				<form:hidden path="frontMatterSubtitle.ebookNameId"/>
				<form:hidden path="frontMatterSubtitle.sequenceNum" value="2"/>
				<form:textarea path="frontMatterSubtitle.bookNameText" />
				<div class="errorDiv2">
					<form:errors path="frontMatterSubtitle.bookNameText" cssClass="errorMessage" />
				</div>
			</div> 
			<div class="row">
				<form:label path="frontMatterSeries" class="labelCol">Series</form:label>
				<form:hidden path="frontMatterSeries.ebookNameId"/>
				<form:hidden path="frontMatterSeries.sequenceNum" value="3"/>
				<form:textarea path="frontMatterSeries.bookNameText" />
				<div class="errorDiv2">
					<form:errors path="frontMatterSeries.bookNameText" cssClass="errorMessage" />
				</div>
			</div> 
		</div>
		<div class="rightDefinitionForm">
			<div class="row">
				<form:label path="additionalTrademarkInfo" class="labelCol">Additional Trademark/Patent Info</form:label>
				<form:textarea path="additionalTrademarkInfo" />
				<div class="errorDiv">
					<form:errors path="additionalTrademarkInfo" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="currency" class="labelCol">Currentness Message</form:label>
				<form:textarea path="currency" />
				<div class="errorDiv">
					<form:errors path="currency" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="isAuthorDisplayVertical" class="labelCol">Author Display</form:label>
				<form:radiobutton path="isAuthorDisplayVertical" value="true" />Vertical
				<form:radiobutton path="isAuthorDisplayVertical" value="false" />Horizontal
				<div class="errorDiv">
					<form:errors path="isAuthorDisplayVertical" cssClass="errorMessage" />
				</div>
			</div>
			
			<div id="authorName" class="row">
				<form:label path="authorInfo" class="labelCol">Author Information</form:label>
				<input type="button" id="addAuthor" value="add" />
				<div class="errorDiv">
					<form:errors path="authorInfo" cssClass="errorMessage" />
				</div>
				<c:forEach items="${editBookDefinitionForm.authorInfo}" var="author" varStatus="aStatus">
					<div class="row">
						<form:hidden path="authorInfo[${aStatus.index}].authorId"/>
						<form:input path="authorInfo[${aStatus.index}].authorNamePrefix" title="prefix" class="prefix"  />
						<form:input path="authorInfo[${aStatus.index}].authorFirstName"  title="first name" class="firstName" />
						<form:input path="authorInfo[${aStatus.index}].authorMiddleName"  title="middle name" class="middleName" />
						<form:input path="authorInfo[${aStatus.index}].authorLastName"   title="last name" class="lastName" />
						<form:input path="authorInfo[${aStatus.index}].authorNameSuffix"  title="suffix" class="suffix" />
						<form:input path="authorInfo[${aStatus.index}].sequenceNum"  title="Seq Num." class="sequenceNumber" maxlength="9" />
						<div>
							Additional Text
							<form:textarea path="authorInfo[${aStatus.index}].authorAddlText"  title="Additional Text" class="additionalText" />
						</div>
						<input type="button" value="Delete" class="rdelete" />
						<div class="errorDiv2">
							<form:errors path="authorInfo[${aStatus.index}].authorNamePrefix" cssClass="errorMessage" />
							<form:errors path="authorInfo[${aStatus.index}].authorFirstName" cssClass="errorMessage" />
							<form:errors path="authorInfo[${aStatus.index}].authorMiddleName" cssClass="errorMessage" />
							<form:errors path="authorInfo[${aStatus.index}].authorLastName" cssClass="errorMessage" />
							<form:errors path="authorInfo[${aStatus.index}].authorNameSuffix" cssClass="errorMessage" />
							<form:errors path="authorInfo[${aStatus.index}].authorAddlText" cssClass="errorMessage" />
							<form:errors path="authorInfo[${aStatus.index}].sequenceNum" cssClass="errorMessage" />
						</div>
					</div>
				</c:forEach>
				<div id="addHere"></div>
			</div>
		</div>
	</div>
</div>

<div id="additionFrontMatterBlock" class="centerSection">
	<form:label path="frontMatters" class="labelCol">Additional Front Matter Pages</form:label>
	<input type="button" id="addFrontMatterPage" value="add" />
	<div class="errorDiv">
		<form:errors path="frontMatters" cssClass="errorMessage" />
	</div>
	<c:forEach items="${editBookDefinitionForm.frontMatters}" var="page" varStatus="pageStatus">
		<div class="row frontMatterPage">
			<form:hidden path="frontMatters[${pageStatus.index}].id"/>
			<form:input path="frontMatters[${pageStatus.index}].pageTocLabel" title="Page TOC Label" />
			<form:input path="frontMatters[${pageStatus.index}].pageHeadingLabel" title="Page Heading Label" />
			<form:input path="frontMatters[${pageStatus.index}].sequenceNum" title="Page Seq Num." class="sequenceNumber" maxlength="9" />
			<input type="button" value="Delete Page" class="rdelete" />
			<div class="errorDiv2">
				<form:errors path="frontMatters[${pageStatus.index}].pageTocLabel" cssClass="errorMessage" />
				<form:errors path="frontMatters[${pageStatus.index}].pageHeadingLabel" cssClass="errorMessage" />
				<form:errors path="frontMatters[${pageStatus.index}].sequenceNum" cssClass="errorMessage" />
			</div>
			<c:set var="sectionIndex" value="0"/>
			<c:forEach items="${page.frontMatterSections}" var="section" varStatus="sectionStatus">
				<div class="row frontMatterSection">
					<c:set var="sectionIndex" value="${sectionStatus.index}"/>
					<form:hidden path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].id"   />
					<form:input path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].sectionHeading" title="Section Heading" />
					<form:input path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].sequenceNum" title="Section Seq Num." class="sequenceNumber" maxlength="9" />
					<input type="button" value="Delete Section" class="rdelete" />
					<div class="errorDiv2">
						<form:errors path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].sectionHeading" cssClass="errorMessage" />
						<form:errors path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].sequenceNum" cssClass="errorMessage" />
					</div>
					<form:textarea path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].sectionText" title="Section Text" class="frontMatterSectionTextArea" />
					<div class="errorDiv2">
						<form:errors path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].sectionText" cssClass="errorMessage" />
					</div>
					<c:set var="pdfIndex" value="0"/>
					<c:forEach items="${section.pdfs}" var="pdf" varStatus="pdfStatus">
						<div class="row">
							<c:set var="pdfIndex" value="${pdfStatus.index}"/>
							<form:hidden path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].pdfs[${pdfStatus.index}].id" />
							<form:input path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].pdfs[${pdfStatus.index}].pdfLinkText"   title="PDF Link Text" />
							<form:input path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].pdfs[${pdfStatus.index}].pdfFilename"   title="PDF Filename" />
							<form:input path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].pdfs[${pdfStatus.index}].sequenceNum"   title="Section Seq Num." class="sequenceNumber" maxlength="9" />
							<input type="button" value="Delete Pdf" class="rdelete" />
							<div class="errorDiv2">
								<form:errors path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].pdfs[${pdfStatus.index}].pdfLinkText" cssClass="errorMessage" />
								<form:errors path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].pdfs[${pdfStatus.index}].pdfFilename" cssClass="errorMessage" />
								<form:errors path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].pdfs[${pdfStatus.index}].sequenceNum" cssClass="errorMessage" />
							</div>
						</div>
					</c:forEach>
					<div id="addAdditionalPdf_${pageStatus.index}_${sectionStatus.index}"></div>
					<input type="button" value="Add Pdf" class="addPdf" pageIndex="${pageStatus.index}" sectionIndex="${sectionStatus.index}" pdfIndex="${pdfIndex + 1}"  />
				</div>
			</c:forEach>
			<div id="addAdditionalSection_${pageStatus.index}"></div>
			<input type="button" value="Add Section" class="addSection" pageIndex="${pageStatus.index}" sectionIndex="${sectionIndex + 1}"  />
			<div class="errorDiv2">
			</div>
		</div>
	</c:forEach>
	<div id="addAdditionPageHere"></div>
</div> 

<div id="modal"> 
    <div id="dialog" class="window" style="display:none;">
        <div class="modelTitle">Comments</div>
        <form:textarea path="comment"/>
        <form:errors path="comment" cssClass="errorMessage" />
        <div class="modalButtons">
        	<form:button>Save</form:button>
        	<a href="#" class="cancel">Cancel</a>
        </div>
    </div>
    <div id="mask"></div>
</div>

