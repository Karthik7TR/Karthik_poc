<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>

<%-- Popup Preview window specifications (used in function and in onclick() handler) --%>
<c:set var="winSpecs" value="<%=WebConstants.FRONT_MATTER_PREVIEW_WINDOW_SPECS %>"/>
<c:choose>

<c:when test="${previewHtml != null}">
<script>
	var openFrontMatterPreviewWindow = function() {
		var win = window.open('<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_EDIT%>?time=<%=System.currentTimeMillis()%>', null, '${winSpecs}');
		win.focus();
	};
</script>
</c:when>
<c:otherwise>
<script>
	var openFrontMatterPreviewWindow = function() {
		// No action if no front matter preview data (no popup window)
	};
</script>

</c:otherwise>
</c:choose>		
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
			if(contentType == "<%=WebConstants.DOCUMENT_TYPE_ANALYTICAL_ABBR%>") {
				if (pubInfo) {
					titleId.push(pubAbbr, pubInfo);
				} else {
					titleId.push(pubAbbr);
				};
			} else if(contentType == "<%=WebConstants.DOCUMENT_TYPE_COURT_RULES_ABBR%>") {
				if (pubInfo) {
					titleId.push(state, pubType, pubInfo);
				} else {
					titleId.push(state, pubType);
				};
			} else if(contentType == "<%=WebConstants.DOCUMENT_TYPE_SLICE_CODES_ABBR%>") {
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
			if(contentType == "<%=WebConstants.DOCUMENT_TYPE_ANALYTICAL_ABBR%>") {
				$('#pubAbbrDiv').show();
			} else if(contentType == "<%=WebConstants.DOCUMENT_TYPE_COURT_RULES_ABBR%>") {
				$('#stateDiv').show();
				$('#pubTypeDiv').show();
			} else if(contentType == "<%=WebConstants.DOCUMENT_TYPE_SLICE_CODES_ABBR%>") {
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
			var authorBox = $("<div>").addClass("authorBox");
			var id = "authorInfo" + authorIndex;
			var name = "authorInfo[" + authorIndex + "]";
			
			// Add author name input boxes
			authorBox.append(addAuthorNameRows(id, name, "authorNamePrefix", "Prefix"));
			authorBox.append(addAuthorNameRows(id, name, "authorFirstName", "First Name"));
			authorBox.append(addAuthorNameRows(id, name, "authorMiddleName", "Middle Name"));
			authorBox.append(addAuthorNameRows(id, name, "authorLastName", "Last Name"));
			authorBox.append(addAuthorNameRows(id, name, "authorNameSuffix", "Suffix"));
			
			// Add addition text input box
			var additionalText = $("<div>").addClass("authorRow");
			additionalText.append($("<label>").html("Additional Text"));
			additionalText.append($("<textarea>").attr("id",id +".authorAddlText").attr("name", name + ".authorAddlText"));
			authorBox.append(additionalText);
			
			// Add sequence number input box
			var sequenceBox = $("<div>").addClass("authorRow");
			sequenceBox.append("Sequence Number");
			sequenceBox.append($("<input>").addClass("sequenceNumber").attr("id",id +".sequenceNum").attr("name", name + ".sequenceNum").attr("type", "text").attr("maxlength", 9));
			authorBox.append(sequenceBox);
			
			// Add Comma checkbox
			var useCommaBeforeSuffix = $("<div>").addClass("authorRow");
			useCommaBeforeSuffix.append("Use Comma Before Suffix");
			useCommaBeforeSuffix.append($("<input>").attr("id",id +".useCommaBeforeSuffix").attr("name", name + ".useCommaBeforeSuffix").attr("type", "checkbox"));
			authorBox.append(useCommaBeforeSuffix);
			
			// Add delete button
			authorBox.append($("<input>").addClass("rdelete").attr("title","Delete Author").attr("type", "button").val("Delete"));
		
			$("#addHere").before(authorBox);
			authorIndex = authorIndex + 1;
		};
		
		var addAuthorNameRows = function(id, name, fieldName, label) {
			var authorRow = $("<div>").addClass("authorRow");
			authorRow.append($("<label>").html(label));
			authorRow.append($("<input>").attr("id",id +"." + fieldName).attr("name", name + "." + fieldName).attr("type", "text"));
			
			return authorRow;
		}
		
		// Add another additional Front Matter Page row
		var addFrontMatterPageRow = function() {
			var appendTxt = "<div class='row frontMatterPage'>";
			appendTxt = appendTxt + "<input id=\"frontMatters" + frontMatterPageIndex + ".pageTocLabel\" name=\"frontMatters[" + frontMatterPageIndex + "].pageTocLabel\" type=\"text\" title=\"Page TOC Label\"/>";
			appendTxt = appendTxt + "<input id=\"frontMatters" + frontMatterPageIndex + ".pageHeadingLabel\" name=\"frontMatters[" + frontMatterPageIndex + "].pageHeadingLabel\" type=\"text\" title=\"Page Heading Label\"/>";
			appendTxt = appendTxt + "<input class=\"sequenceNumber\" id=\"frontMatters" + frontMatterPageIndex + ".sequenceNum\" name=\"frontMatters[" + frontMatterPageIndex + "].sequenceNum\" type=\"text\" title=\"Page Seq Num.\" maxlength=\"9\" />";
			appendTxt = appendTxt + "<input type=\"button\" value=\"Delete Page\" class=\"rdelete\" title=\"Delete Page, Sections, and Pdfs?\" deleteMessage=\"This will also delete all the sections and pdfs in this front matter page.\" />";
			appendTxt = appendTxt + "<input type=\"button\" value=\"Preview\" class=\"fmPreview\"/>"; 
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
			appendTxt = appendTxt + "<input type=\"button\" value=\"Delete Section\" class=\"rdelete\" title=\"Delete Section and Pdfs?\" deleteMessage=\"This will also delete all the pdfs in this front matter section.\" />";
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
			appendTxt = appendTxt + "<input type=\"button\" value=\"Delete Pdf\" class=\"rdelete\" title=\"Delete Pdf?\" />";
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
				$("#docCollectionName").val("");
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
			contentType = getContentTypeIdElement().attr("abbr");
			updateTitleId();
			determineOptions();
		};
		
		var showPubCutoffDateBox = function() {
			showPubCutoffDate = getContentTypeIdElement().attr("usecutoffdate");
			if(showPubCutoffDate == "true") {
				$("#displayPubCutoffDateOptions").show();
			} else {
				$("#displayPubCutoffDateOptions").hide();
				$('input:radio[name=publicationCutoffDateUsed]:nth(1)').attr('checked',true);
				updatePubCutoffDate(showPubCutoffDate);
			}
		};
		
		var getContentTypeIdElement = function() {
			var element = $('#contentTypeId :selected');
			
			// Check selected element exists, if not look for just the element
			if(element.length == 0) {
				element = $('#contentTypeId');
			};
			
			return element;
		};
		
		var updatePubCutoffDate = function(showPubCutoffDate) {
			$("#displayCutoffDate").hide();
			if(showPubCutoffDate == "true") {
				$("#displayCutoffDate").show();
			} else {
				$("#publicationCutoffDate").val("");
			}
		};
		
		var submitFormForValidation = function() {
			warning = false;
			$('#validateForm').val(true);
			$('#<%= EditBookDefinitionForm.FORM_NAME %>').submit();
		};

		
	

		
		$(document).ready(function() {
			<%-- If there is front matter preview content to display, then display it in its own window --%>
			openFrontMatterPreviewWindow();
			
			<%-- Setup change handlers  --%>
			$('#contentTypeId').change(function () {
				// Clear out information when content type changes
				clearTitleInformation();
				$('.generateTitleID .errorDiv').hide();
				
				getContentTypeAbbr();
				showPubCutoffDateBox();
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
				pubAbbr = $.trim($(this).val());
				updateTitleId();
			});
			$('#pubInfo').change(function () {
				pubInfo = $.trim($(this).val());
				updateTitleId();
			});
			
			<%-- Setup Button Click handlers  --%>
			$('#addAuthor').click(function () {
				addAuthorRow();
				
				<%-- IE8 bug: forcing reflow/redraw to resize the parent div --%>
				$('#authorName').hide();
				$('#authorName').show();
			});
			
			// Clicking the Additional Front Matter preview button 
			$('.fmPreview').live("click", function() {
				var pageSequenceNumber = $(this).parent().children(".sequenceNumber").val();
				$('#selectedFrontMatterPreviewPage').val(pageSequenceNumber);
				submitFormForValidation();
			});
		
			//Update formValidation field if Validation button is pressed
			$('#validate').click(submitFormForValidation);
			
			// Add Comment
			$('#confirm').click(function(e) {
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
			
			$('#dialog .save').click(function () {
				warning= false;
			});
			
			//if close button is clicked
		    $('#dialog .closeModal').click(function (e) {
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
			
			// Determine to show publication cut-off date
			$('input:radio[name=publicationCutoffDateUsed]').change(function () {
				updatePubCutoffDate($(this).val());
			});
			
			// Close or open the Keyword values
			$( ".keywordLabel" ).click(function() {
					var divId = $(this).attr("id");
					var keywordValuesDiv = $("#"+ divId + "_values");
					
					var visible = !(keywordValuesDiv.is(":visible"));
					var imgSrc = (visible) ? "theme/images/wf_minus.gif" : "theme/images/wf_plus.gif";
					$(this).children("img").attr("src", imgSrc);
					
					if (visible) {
						keywordValuesDiv.show();
					} else {
						keywordValuesDiv.hide();
					};
			});
			
			// delete confirmation box
			$(".rdelete").live("click", function () {
				var deleteTitle = $(this).attr("title");
				var deleteMessage = $(this).attr("deleteMessage");
				
				// If there the attribute does not exist, reset the delete message
				if(!deleteMessage) {
					deleteMessage = "";
				}
				$("#deleteMessage").html(deleteMessage);
				var srow = $(this).parent();
				
				$( "#delete-confirm" ).dialog({
					autoOpen: false,
					resizable: false,
					height:260,
					width:500,
					title: deleteTitle,
					modal: true,
					draggable:false,
					buttons: {
						"Delete": function() {
							// Remove the element
							srow.remove();
							
							<%-- IE8 bug: forcing reflow/redraw to resize the parent div --%>
							$('#authorName').hide();
							$('#authorName').show();
							
							$( this ).dialog( "close" );
						},
						Cancel: function() {
							$( this ).dialog( "close" );
						}
					}
				});
				$( "#delete-confirm" ).dialog( "open" );
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
			contentType = getContentTypeIdElement().attr("abbr");
			
			// Setup view
			determineOptions();
			$('#titleIdBox').val($('#titleId').val());
			updateTOCorNORT($('input:radio[name=isTOC]:checked').val());
			updatePubCutoffDate($('input:radio[name=publicationCutoffDateUsed]:checked').val());
			showPubCutoffDateBox();
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
<form:hidden path="selectedFrontMatterPreviewPage" />
<div class="validateFormDiv">
	<form:errors path="validateForm" cssClass="errorMessage" />
</div>
<%-- Check if book has been published --%>
<c:choose>
	<c:when test="${!isPublished}">
		<div class="generateTitleID">
			<div>
				<form:label path="contentTypeId" class="labelCol">Content Type</form:label>
				<form:select path="contentTypeId" >
					<form:option value="" label="SELECT" />
					<c:forEach items="${contentTypes}" var="contentType">
						<form:option path="contentTypeId" value="${ contentType.id }" label="${ contentType.name }" abbr="${ contentType.abbreviation }" usecutoffdate="${contentType.usePublishCutoffDateFlag}" />
					</c:forEach>
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
		<%--Need extra attributes on contentType to determine if Cutoff Date is used --%>
		<c:forEach items="${contentTypes}" var="contentType">
			<c:if test="${ contentType.id == editBookDefinitionForm.contentTypeId }">
				<form:hidden path="contentTypeId" abbr="${ contentType.abbreviation }" usecutoffdate="${contentType.usePublishCutoffDateFlag}" />
			</c:if>
		</c:forEach>
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
			<div id="displayPubCutoffDateOptions">
				<div class="row">
					<form:label path="publicationCutoffDateUsed" class="labelCol">Enable Publication Cut-off Date</form:label>
					<form:radiobutton path="publicationCutoffDateUsed" value="true" />Yes
					<form:radiobutton path="publicationCutoffDateUsed" value="false" />No
					<div class="errorDiv">
						<form:errors path="publicationCutoffDateUsed" cssClass="errorMessage" />
					</div>
				</div>
				<div id="displayCutoffDate" class="row" style="display:none">
					<form:label path="publicationCutoffDate" class="labelCol">Publication Cut-off Date</form:label>
					<form:input path="publicationCutoffDate" />
					<div class="errorDiv">
						<form:errors path="publicationCutoffDate" cssClass="errorMessage" />
					</div>
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
				<div id="keywordBox">
					<c:forEach items="${keywordTypeCode}" var="keyword" varStatus="keywordStatus">
						<div id="keyword_${keywordStatus.index}"  class="keywordLabel">
							<img src="theme/images/wf_plus.gif"> ${keyword.name} <form:errors path="keywords[${keywordStatus.index}]" cssClass="errorMessage" />
						</div>
						<div id="keyword_${keywordStatus.index}_values" class="keywordValueBox" style="display:none;">
							<form:radiobutton path="keywords[${keywordStatus.index}]" value=""/>None
							<c:forEach items="${keyword.values}" var="value">
								<div class="keywordValues">
									<form:radiobutton  path="keywords[${keywordStatus.index}]" value="${value.id}"/>${value.name}
								</div>
							</c:forEach>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
		<div class="rightDefinitionForm">
			<c:set var="disableProviewOptions" value="true"/>
			<sec:authorize access="hasRole('ROLE_SUPERUSER')">
				<c:set var="disableProviewOptions" value=""/>
			</sec:authorize>
			<c:if test="${disableProviewOptions}">
				<%-- Hidden fields needed when ProView options are disabled.
					 Options reset to defaults if hidden fields are missing. --%>
				<form:hidden path="isProviewTableView"/>
				<form:hidden path="autoUpdateSupport"/>
				<form:hidden path="searchIndex"/>
				<form:hidden path="enableCopyFeatureFlag"/>
				<form:hidden path="pilotBook"/>
			</c:if>
			<div class="row">
				<form:label path="isProviewTableView" class="labelCol">Use ProView Table View</form:label>
				<form:radiobutton disabled="${disableProviewOptions}" path="isProviewTableView" value="true" />True
				<form:radiobutton disabled="${disableProviewOptions}" path="isProviewTableView" value="false" />False
				<div class="errorDiv">
					<form:errors path="isProviewTableView" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="autoUpdateSupport" class="labelCol">Auto-update Support</form:label>
				<form:radiobutton disabled="${disableProviewOptions}" path="autoUpdateSupport" value="true" />True
				<form:radiobutton disabled="${disableProviewOptions}" path="autoUpdateSupport" value="false" />False
				<div class="errorDiv">
					<form:errors path="autoUpdateSupport" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="searchIndex" class="labelCol">Search Index</form:label>
				<form:radiobutton disabled="${disableProviewOptions}" path="searchIndex" value="true" />True
				<form:radiobutton disabled="${disableProviewOptions}" path="searchIndex" value="false" />False
				<div class="errorDiv">
					<form:errors path="searchIndex" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="enableCopyFeatureFlag" class="labelCol">Enable Copy Feature</form:label>
				<form:radiobutton disabled="${disableProviewOptions}" path="enableCopyFeatureFlag" value="true" />True
				<form:radiobutton disabled="${disableProviewOptions}" path="enableCopyFeatureFlag" value="false" />False
				<div class="errorDiv">
					<form:errors path="enableCopyFeatureFlag" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="pilotBook" class="labelCol">Is Pilot Book</form:label>
				<form:radiobutton disabled="${disableProviewOptions}" path="pilotBook" value="true" />True
				<form:radiobutton disabled="${disableProviewOptions}" path="pilotBook" value="false" />False
				<div class="errorDiv">
					<form:errors path="pilotBook" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row" style="font-size:.7em; text-align: center;">
				*Only Super Users are able to modify above options.
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
				<div class="errorDiv">
					<form:errors path="frontMatterTitle.bookNameText" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="frontMatterSubtitle" class="labelCol">Sub Title</form:label>
				<form:hidden path="frontMatterSubtitle.ebookNameId"/>
				<form:hidden path="frontMatterSubtitle.sequenceNum" value="2"/>
				<form:textarea path="frontMatterSubtitle.bookNameText" />
				<div class="errorDiv">
					<form:errors path="frontMatterSubtitle.bookNameText" cssClass="errorMessage" />
				</div>
			</div> 
			<div class="row">
				<form:label path="frontMatterSeries" class="labelCol">Series</form:label>
				<form:hidden path="frontMatterSeries.ebookNameId"/>
				<form:hidden path="frontMatterSeries.sequenceNum" value="3"/>
				<form:textarea path="frontMatterSeries.bookNameText" />
				<div class="errorDiv">
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
					<div class="authorBox">
						<form:hidden path="authorInfo[${aStatus.index}].authorId"/>
						<div class="authorRow">
							<label>Prefix</label>
							<form:input path="authorInfo[${aStatus.index}].authorNamePrefix" />
							<div class="errorDiv">
								<form:errors path="authorInfo[${aStatus.index}].authorNamePrefix" cssClass="errorMessage" />
							</div>
						</div>
						<div class="authorRow">
							<label>First Name</label>
							<form:input path="authorInfo[${aStatus.index}].authorFirstName" />
							<div class="errorDiv">
								<form:errors path="authorInfo[${aStatus.index}].authorFirstName" cssClass="errorMessage" />
							</div>
						</div>
						<div class="authorRow">
							<label>Middle Name</label>
							<form:input path="authorInfo[${aStatus.index}].authorMiddleName" />
							<div class="errorDiv">
								<form:errors path="authorInfo[${aStatus.index}].authorMiddleName" cssClass="errorMessage" />
							</div>
						</div>
						<div class="authorRow">
							<label>Last Name</label>
							<form:input path="authorInfo[${aStatus.index}].authorLastName" />
							<div class="errorDiv">
								<form:errors path="authorInfo[${aStatus.index}].authorLastName" cssClass="errorMessage" />
							</div>
						</div>
						<div class="authorRow">
							<label>Suffix</label>
							<form:input path="authorInfo[${aStatus.index}].authorNameSuffix"  />
							<div class="errorDiv">
								<form:errors path="authorInfo[${aStatus.index}].authorNameSuffix" cssClass="errorMessage" />
							</div>
						</div>
						<div class="authorRow">
							<label>Additional Text</label>
							<form:textarea path="authorInfo[${aStatus.index}].authorAddlText" />
							<div class="errorDiv">
								<form:errors path="authorInfo[${aStatus.index}].authorAddlText" cssClass="errorMessage" />
							</div>
						</div>
						<div class="authorRow">
							Sequence Number
							<form:input path="authorInfo[${aStatus.index}].sequenceNum" class="sequenceNumber" maxlength="9" />
							<div class="errorDiv">
								<form:errors path="authorInfo[${aStatus.index}].sequenceNum" cssClass="errorMessage" />
							</div>
						</div>
						<div class="authorRow">
							Use Comma Before Suffix
							<form:checkbox path="authorInfo[${aStatus.index}].useCommaBeforeSuffix"  title="Comma After Suffix" />
						</div>
						<input type="button" value="Delete" class="rdelete" title="Delete Author" />
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
			<input type="button" value="Delete Page" class="rdelete" title="Delete Page, Sections, and Pdfs?" deleteMessage="This will also delete all the sections and pdfs in this front matter page." />
			<input type="button" value="Preview" class="fmPreview"/>   

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
					<input type="button" value="Delete Section" class="rdelete" title="Delete Section and Pdfs?" deleteMessage="This will also delete all the pdfs in this front matter section."/>
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
							<input type="button" value="Delete Pdf" class="rdelete" title="Delete Pdf?" />
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
        	<form:button class="save">Save</form:button>
        	<form:button class="closeModal">Cancel</form:button>
        </div>
    </div>
    <div id="mask"></div>
</div>

<div id="delete-confirm" title="Delete?" style="display:none;" >
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:4px 7px 70px 0;"></span>Are you sure you want to delete? <span id="deleteMessage"></span></p>
</div>

