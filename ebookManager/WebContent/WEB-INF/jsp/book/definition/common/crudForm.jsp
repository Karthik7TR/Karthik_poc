<%@page import="com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus"%>

<%-- Popup Preview window specifications (used in function and in onclick() handler) --%>
<c:set var="winSpecs" value="<%=WebConstants.FRONT_MATTER_PREVIEW_WINDOW_SPECS %>"/>
<c:choose>

<c:when test="${previewHtml != null}">
<script>
	var openFrontMatterPreviewWindow = function() {
		window.open('<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_EDIT%>?time=<%=System.currentTimeMillis()%>', null, '${winSpecs}');
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
		var excludeDocumentIndex = ${numberOfExcludeDocuments};
		var renameTocEntryIndex = ${numberOfRenameTocEntries};
		var tableViewerIndex = ${numberOfTableViewers};
		var documentCopyrightIndex = ${numberOfDocumentCopyrights};
		var documentCurrencyIndex = ${numberOfDocumentCurrencies};
		var contentType = "";
		var publisher = "";
		var state = "";
		var pubType = "";
		var pubAbbr = "";
		var pubInfo = "";
		var jurisdiction = "";
		var productCode = "";
		
		
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
			if(productCode) {
				var fullyQualifiedTitleIdArray = [];
				fullyQualifiedTitleIdArray.push(publisher, productCode, titleId.join("_"));
				
				var fullyQualifiedTitleId = fullyQualifiedTitleIdArray.join("/").toLowerCase();
				
				$('#titleId').val(fullyQualifiedTitleId);
				$('#titleIdBox').val(fullyQualifiedTitleId);
			} else if (contentType) {
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
			$('#productCodeDiv').hide();
			$('#contentTypeDiv').hide();
			
			if (publisher && publisher != "uscl") {
				$('#productCodeDiv').show();
				$('#publishDetailDiv').show();

			} else {
				$('#contentTypeDiv').show();
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
			}
		};
		
		// Add another author row
		var addAuthorRow = function() {
			var expandingBox = $("<div>").addClass("expandingBox");
			var id = "authorInfo" + authorIndex;
			var name = "authorInfo[" + authorIndex + "]";
			
			expandingBox.append($("<button>").attr("type","button").addClass("moveUp").html("Up"));
			expandingBox.append($("<button>").attr("type","button").addClass("moveDown").html("Down"));
			
			// Add sequence number
			var lastChild = $("#addAuthorHere .expandingBox:last-child");
			var lastSequenceNum = getSequenceNumber(lastChild);
			var sequenceBox = $("<input>").attr("type","hidden").addClass("sequence").attr("id",id +".sequenceNum").attr("name", name + ".sequenceNum").attr("value",lastSequenceNum + 1);
			expandingBox.append(sequenceBox);
			
			// Add author name input boxes
			expandingBox.append(addDynamicRow("input", id, name, "authorNamePrefix", "Prefix"));
			expandingBox.append(addDynamicRow("input", id, name, "authorFirstName", "First Name"));
			expandingBox.append(addDynamicRow("input", id, name, "authorMiddleName", "Middle Name"));
			expandingBox.append(addDynamicRow("input", id, name, "authorLastName", "Last Name"));
			expandingBox.append(addDynamicRow("input", id, name, "authorNameSuffix", "Suffix"));
			
			// Add addition text input box
			expandingBox.append(addDynamicRow("textarea", id, name, "authorAddlText", "Additional Text"));

			// Add Comma checkbox
			var useCommaBeforeSuffix = $("<div>").addClass("dynamicRow");
			useCommaBeforeSuffix.append("Use Comma Before Suffix");
			useCommaBeforeSuffix.append($("<input>").attr("id",id +".useCommaBeforeSuffix").attr("name", name + ".useCommaBeforeSuffix").attr("type", "checkbox"));
			expandingBox.append(useCommaBeforeSuffix);
			
			// Add delete button
			expandingBox.append($("<input>").addClass("rdelete").attr("title","Delete Author").attr("type", "button").val("Delete"));
		
			$("#addAuthorHere").append(expandingBox);
			authorIndex = authorIndex + 1;
		};
		
		var addDynamicRow = function(elementName, id, name, fieldName, label, maxLength, cssClass, type, value) {
			var dynamicRow = $("<div>").addClass("dynamicRow");
			
			if(label != null) {
				dynamicRow.append($("<label>").html(label));
			}
			
			var input = $("<"+ elementName +">").attr("id",id +"." + fieldName).attr("name", name + "." + fieldName);
			if(maxLength != null) {
				input.attr("maxlength", maxLength);
			}
			if(cssClass != null) {
				input.attr("class", cssClass);
			}
			if(elementName == "input") {
				input.attr("type", "text");
			}
			if(value != null) {
				input.attr("value", value);
			}
			if(type != null) {
				input.attr("type", type);
			}
			
			dynamicRow.append(input);
			
			return dynamicRow;
		}
		
		var getDateTimeString = function(date) {
			var month = date.getMonth() + 1;
			var day = date.getDate();
			var year = date.getFullYear();
			var hour = date.getHours();
			var minute = date.getMinutes();
			var second = date.getSeconds();
			
			return leadingZero(month) + "/" + leadingZero(day) + "/" + year + " " + leadingZero(hour) + ":" + leadingZero(minute) + ":" + leadingZero(second);
		}
		
		var leadingZero = function(val) {
			var str = val.toString();
			if(str.length == 1) {
				str = '0' + str;
			}
			return str;
		}
		
		// Add rows for Table Viewer, exclude document, rename toc entry, document copyright, and document currency
		var addGuidRow = function(elementName, index, guidAttrName, guidLabelName, titleMessage, textAttrArray, textLabelArray, addHere) {
			var expandingBox = $("<div>").addClass("expandingBox");
			var id = elementName + index;
			var name = elementName + "[" + index + "]";
			
			// Add Document Guid input boxes
			expandingBox.append(addDynamicRow("input", id, name, guidAttrName, guidLabelName, 33, "guid"));
			
			// Add new text
			if(textAttrArray != null) {
				for(var i = 0; i < textAttrArray.length; i++) {
					expandingBox.append(addDynamicRow("input", id, name, textAttrArray[i], textLabelArray[i]));
				}
			}
			
			// Add Note text box
			expandingBox.append(addDynamicRow("textarea", id, name, "note", "Note"));
			
			// Add Date input box
			var lastUpdated = $("<div>").addClass("dynamicRow");
			lastUpdated.append($("<label>").html("Last Updated"));
			lastUpdated.append($("<input>").attr("id",id +".lastUpdated").attr("name", name + ".lastUpdated").attr("type", "text").attr("disabled","disabled").val(getDateTimeString(new Date())));
			expandingBox.append(lastUpdated);
			
			// Add delete button
			expandingBox.append($("<input>").addClass("rdelete").attr("title",titleMessage).attr("type", "button").val("Delete"));

			addHere.before(expandingBox);
		};
		
		// Add another additional Front Matter Page row
		var addFrontMatterPageRow = function() {
			var expandingBox = $("<div>").addClass("row frontMatterPage");
			var id = "frontMatters" + frontMatterPageIndex;
			var name = "frontMatters[" + frontMatterPageIndex + "]";
			
			// Add sequence number
			var lastChild = $("#addAdditionPageHere .frontMatterPage:last-child");
			var lastSequenceNum = getSequenceNumber(lastChild);
			var sequenceBox = $("<input>").attr("type","hidden").addClass("sequence").attr("id",id +".sequenceNum").attr("name", name + ".sequenceNum").attr("value",lastSequenceNum + 1);
			expandingBox.append(sequenceBox);
			expandingBox.append($("<input>").attr("type","text").attr("id",id +".pageTocLabel").attr("name", name + ".pageTocLabel").attr("title", "Page TOC Label").addClass("pageTocLabel"));
			expandingBox.append($("<input>").attr("type","text").attr("id",id +".pageHeadingLabel").attr("name", name + ".pageHeadingLabel").attr("title", "Page Heading Label").addClass("pageHeadingLabel"));
			
			// Add buttons
			expandingBox.append($("<button>").attr("type","button").addClass("moveUp").html("Up"));
			expandingBox.append($("<button>").attr("type","button").addClass("moveDown").html("Down"));
			expandingBox.append($("<input>").addClass("rdelete").attr("title","Delete Page, Sections, and Pdfs?").attr("type", "button").attr("deleteMessage", "This will also delete all the sections and pdfs in this front matter page.").val("Delete Page"));
			expandingBox.append($("<button>").attr("type","button").addClass("fmPreview").html("Preview"));
			
			var section = $("<div>").attr("id", "addAdditionalSection_" + frontMatterPageIndex);
			section.append($("<input>").addClass("addSection").attr("pageIndex", frontMatterPageIndex).attr("sectionIndex", 0).attr("type", "button").val("Add Section"));
			expandingBox.append(section);

			$("#addAdditionPageHere").append(expandingBox);
			frontMatterPageIndex = frontMatterPageIndex + 1;
			
			textboxHint("additionFrontMatterBlock");
		};
		
		// Add another additional Front Matter Section row
		var addFrontMatterSectionRow = function(pageIndex, sectionIndex) {
			var expandingBox = $("<div>").addClass("row frontMatterSection");
			var id = "frontMatters" + pageIndex + ".frontMatterSections" + sectionIndex;
			var name = "frontMatters[" + pageIndex + "].frontMatterSections["+ sectionIndex +"]";
			
			var addAdditionalSection = "#addAdditionalSection_" + pageIndex;
			// Add sequence number
			var lastChild = $(addAdditionalSection + " .frontMatterSection:last-child");
			var lastSequenceNum = getSequenceNumber(lastChild);
			var sequenceBox = $("<input>").attr("type","hidden").addClass("sequence").attr("id",id +".sequenceNum").attr("name", name + ".sequenceNum").attr("value",lastSequenceNum + 1);
			expandingBox.append(sequenceBox);
			expandingBox.append($("<input>").attr("type","text").attr("id",id +".sectionHeading").attr("name", name + ".sectionHeading").attr("title", "Section Heading"));
			
			// Add buttons
			expandingBox.append($("<button>").attr("type","button").addClass("moveUp").html("Up"));
			expandingBox.append($("<button>").attr("type","button").addClass("moveDown").html("Down"));
			expandingBox.append($("<input>").addClass("rdelete").attr("title","Delete Section and Pdfs?").attr("type", "button").attr("deleteMessage", "This will also delete all the pdfs in this front matter section.").val("Delete Section"));

			// Add addition text input box
			var additionalText = $("<div>").addClass("dynamicRow");
			additionalText.append($("<textarea>").attr("id",id +".sectionText").attr("name", name + ".sectionText").addClass("frontMatterSectionTextArea"));
			expandingBox.append(additionalText);

			var pdfSection = $("<div>").attr("id", "addAdditionalPdf_" + pageIndex + "_" + sectionIndex);
			pdfSection.append($("<input>").addClass("addPdf").attr("pageIndex", pageIndex).attr("sectionIndex", sectionIndex).attr("pdfIndex", 0).attr("type", "button").val("Add Pdf"));
			expandingBox.append(pdfSection);
			
			$(addAdditionalSection).append(expandingBox);
			
			textboxHint("additionFrontMatterBlock");
		};
		
		// Add another additional Front Matter Pdf row
		var addFrontMatterPdfRow = function(pageIndex, sectionIndex, pdfIndex) {
			
			var expandingBox = $("<div>").addClass("row frontMatterPdf");
			var id = "frontMatters" + pageIndex + ".frontMatterSections" + sectionIndex + ".pdfs"+ pdfIndex;
			var name = "frontMatters[" + pageIndex + "].frontMatterSections["+ sectionIndex +"].pdfs["+ pdfIndex +"]";
			
			var addAdditionalPdf = "#addAdditionalPdf_" + pageIndex + "_" + sectionIndex;
			// Add sequence number
			var lastChild = $(addAdditionalPdf + " .row:last-child");
			var lastSequenceNum = getSequenceNumber(lastChild);
			var sequenceBox = $("<input>").attr("type","hidden").addClass("sequence").attr("id",id +".sequenceNum").attr("name", name + ".sequenceNum").attr("value",lastSequenceNum + 1);
			expandingBox.append(sequenceBox);
			
			// Add input boxes
			expandingBox.append($("<input>").attr("id",id +".pdfLinkText").attr("name", name + ".pdfLinkText").attr("type", "text").attr("title","PDF Link Text"));
			expandingBox.append($("<input>").attr("id",id +".pdfFilename").attr("name", name + ".pdfFilename").attr("type", "text").attr("title","PDF Filename"));
			
			// Add buttons
			expandingBox.append($("<button>").attr("type","button").addClass("moveUp").html("Up"));
			expandingBox.append($("<button>").attr("type","button").addClass("moveDown").html("Down"));
			expandingBox.append($("<input>").addClass("rdelete").attr("title","Delete Pdf").attr("type", "button").val("Delete Pdf"));
			
			$(addAdditionalPdf).append(expandingBox);
			
			textboxHint("additionFrontMatterBlock");
		};
		
		var updateTOCorNORT = function(isTOC) {
			$("#displayTOC").hide();
			$("#displayNORT").hide();
			
			if(isTOC == "true") {
				$("#displayTOC").show();
				$("#nortFilterView").val("");
				$("#nortDomain").val("");
				$("input:radio[name=useReloadContent][value=false]").attr('checked', true);
			} else {
				$("#displayNORT").show();
				$("#rootTocGuid").val("");
				$("#tocCollectionName").val("");
				$("#docCollectionName").val("");
			}
		};
		
		var clearTitleAndContentInformation = function() {
			contentType = "";
			$('#contentTypeId option:first-child').attr("selected", true);
			clearTitleInformation();
		};
		
		var clearTitleInformation = function() {
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
			productCode = "";
			$('#productCode').val("");
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
				updatePubCutoffDate($('input:radio[name=publicationCutoffDateUsed]:checked').val());
			} else {
				$("#displayPubCutoffDateOptions").hide();
				$('input:radio[name=publicationCutoffDateUsed]:nth(1)').attr('checked',true);
				updatePubCutoffDate(showPubCutoffDate);
			}
		};
		
		var showSelectOptions = function(choice, elementName) {
			if(choice == "true" || choice == true) {
				$(elementName).show();
			} else {
				$(elementName).hide();
				$(elementName +" .expandingBox").remove();
			};
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

		
		var getSequenceNumber = function(element) {
			var sequenceNum = parseInt(element.children(".sequence").val());
			// No sequence number, set sequence to zero
			if(isNaN(sequenceNum)) {
				sequenceNum = 0;
			}
			
			return sequenceNum;
		};

		
		$(document).ready(function() {
			<%-- If there is front matter preview content to display, then display it in its own window --%>
			openFrontMatterPreviewWindow();
			
			<%-- Setup change handlers  --%>
			$('#contentTypeId').change(function () {
				$('.generateTitleID .errorDiv').hide();
				clearTitleInformation();
				getContentTypeAbbr();
				showPubCutoffDateBox();
			});
			$('#publisher').change(function () {
				publisher = $(this).val();
				if(publisher == "uscl") {
					$('#contentTypeDiv').show();
				} else {
					$('#contentTypeDiv').hide();
				}
				determineOptions();
				
				// Clear out information when publisher changes
				clearTitleAndContentInformation();
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
			$('#productCode').change(function () {
				productCode = $.trim($(this).val());
				updateTitleId();
			});
			
			<%-- Setup Button Click handlers  --%>
			$('#addAuthor').click(function () {
				addAuthorRow();
				
				<%-- IE8 bug: forcing reflow/redraw to resize the parent div --%>
				$('#authorName').hide();
				$('#authorName').show();
			});
			
			$('#addExcludeDocument').click(function () {
				addGuidRow("excludeDocuments", excludeDocumentIndex, "documentGuid", "Document Guid", "Delete Exclude Document", null, null, $('#addExcludeDocumentHere'));
				excludeDocumentIndex = excludeDocumentIndex + 1;
			});
			
			$('#addTableViewer').click(function () {
				addGuidRow("tableViewers", tableViewerIndex, "documentGuid", "Document Guid", "Delete Table Viewer", null, null, $('#addTableViewerHere'));
				tableViewerIndex = tableViewerIndex + 1;
			});
			
			$('#addRenameTocEntry').click(function () {
				addGuidRow("renameTocEntries", renameTocEntryIndex, "tocGuid", "Guid", "Delete Rename TOC Entry", new Array("oldLabel", "newLabel"), new Array("Old Label", "New Label"), $("#addRenameTocEntryHere"));
				renameTocEntryIndex = renameTocEntryIndex + 1;
			});
			
			$('#addDocumentCopyright').click(function () {
				addGuidRow("documentCopyrights", documentCopyrightIndex, "copyrightGuid", "Copyright Guid", "Delete Document Copyright", new Array("newText"), new Array("New Text"), $("#addDocumentCopyrightHere"));
				documentCopyrightIndex = documentCopyrightIndex + 1;
			});
			
			$('#addDocumentCurrency').click(function () {
				addGuidRow("documentCurrencies", documentCurrencyIndex, "currencyGuid", "Currency Guid", "Delete Document Currency", new Array("newText"), new Array("New Text"), $("#addDocumentCurrencyHere"));
				documentCurrencyIndex = documentCurrencyIndex + 1;
			});
			
			// Clicking the Additional Front Matter preview button 
			$('.fmPreview').live("click", function() {
				var pageSequenceNumber = $(this).parent().children(".sequence").val();
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
				
				<%-- IE8 bug: forcing reflow/redraw to resize the parent div --%>
				$("#generalSection").hide();
				$("#generalSection").show();
			});
			
			// Determine to show block
			$('input:radio[name=excludeDocumentsUsed]').change(function () {
				showSelectOptions($(this).val(), "#displayExcludeDocument");
			});
			
			$('input:radio[name=renameTocEntriesUsed]').change(function () {
				showSelectOptions($(this).val(), "#displayRenameTocEntry");
			});
			
			$('input:radio[name=tableViewersUsed]').change(function () {
				showSelectOptions($(this).val(), "#displayTableViewer");
				showSelectOptions($(this).val(), "#addTableViewerRow");
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
					
					<%-- IE8 bug: forcing reflow/redraw to resize the parent div --%>
					$("#proviewSection").hide();
					$("#proviewSection").show();
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
							
							$( this ).dialog( "close" );
						},
						Cancel: function() {
							$( this ).dialog( "close" );
						}
					}
				});
				$( "#delete-confirm" ).dialog( "open" );
			});
			
			$("#additionFrontMatterBlock").delegate(".pageTocLabel", "focusout", function () {
				var pageHeadingLabel = $(this).siblings(".pageHeadingLabel");
				if(pageHeadingLabel.val() == "Page Heading Label"){
					// Add pageTocLabel to pageHeadingLabel if it is blank
					if($(this).val() != "") {
						pageHeadingLabel.val($(this).val());
						pageHeadingLabel.removeClass("blur");
					}
				}
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
				
				// Increment pdfIndex
				nextIndex = parseInt(pdfIndex) + 1;
				$(this).attr("pdfIndex", nextIndex);
			});
			
			$('body').delegate(".moveUp", "click", function(){
			  var current = $(this).parent();
			  var currentSequence = getSequenceNumber(current);
			  var previous = current.prev();
			  var previousSequence = getSequenceNumber(previous);
			  
			  if(previousSequence != 0) {
				  current.children(".sequence").val(previousSequence);
				  previous.children(".sequence").val(currentSequence);
				  previous.before(current);
			  }
			});
			
			$('body').delegate(".moveDown", "click",function(){
				var current = $(this).parent();
				var currentSequence = getSequenceNumber(current);
				var next = current.next();
				var nextSequence = getSequenceNumber(next);
				
				if(nextSequence != 0) {
				 current.children(".sequence").val(nextSequence);
				 next.children(".sequence").val(currentSequence);
				 next.after(current);
				}
			});

			
			// Initialize Global variables
			publisher = $('#publisher').val();
			state = $('#state').val();
			jurisdiction = $('#jurisdiction').val();
			pubType = $('#pubType').val();
			pubAbbr = $('#pubAbbr').val();
			pubInfo = $('#pubInfo').val();
			contentType = getContentTypeIdElement().attr("abbr");
			productCode = $('#productCode').val();
			
			// Setup view
			determineOptions();
			$('#titleIdBox').val($('#titleId').val());
			updateTOCorNORT($('input:radio[name=isTOC]:checked').val());
			showPubCutoffDateBox();
			showSelectOptions($("input:radio[name=excludeDocumentsUsed]:checked").val(), "#displayExcludeDocument");
			showSelectOptions($("input:radio[name=renameTocEntriesUsed]:checked").val(), "#displayRenameTocEntry");
			showSelectOptions($("input:radio[name=tableViewersUsed]:checked").val(), "#displayTableViewer");
			showSelectOptions($("input:radio[name=tableViewersUsed]:checked").val(), "#addTableViewerRow");
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
			<div id="publisherChooseDiv">
				<div id="publisherDiv">
					<form:label path="publisher" class="labelCol">Publisher</form:label>
					<form:select path="publisher" >
						<form:option value="" label="SELECT" />
						<form:options items="${publishers}" />
					</form:select>
					<div class="errorDiv">
						<form:errors path="publisher" cssClass="errorMessage" />
					</div>
				</div>
				<div id="contentTypeDiv" style="display:none">
					<form:label path="contentTypeId" class="labelCol">Content Type</form:label>
					<form:select path="contentTypeId" >
						<form:option value="" label="SELECT" />
						<c:forEach items="${contentTypes}" var="contentType">
							<form:option path="contentTypeId" value="${ contentType.id }" label="${ contentType.name }" abbr="${ contentType.abbreviation }" usecutoffdate="${contentType.usePublishCutoffDateFlag}" />
						</c:forEach>
					</form:select>
					<form:errors path="contentTypeId" cssClass="errorMessage" />
				</div>
			</div>
			<div id="publishDetailDiv" style="display:none">
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
				<div id="productCodeDiv">
					<form:label path="productCode" class="labelCol">Product Code</form:label>
					<form:input path="productCode" maxlength="40"/>
					<div class="errorDiv">
						<form:errors path="productCode" cssClass="errorMessage" />
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
		<form:hidden path="productCode" />
	</c:otherwise>
</c:choose>
<c:set var="disableOptions" value="true"/>
<sec:authorize access="hasRole('ROLE_SUPERUSER')">
	<c:set var="disableOptions" value=""/>
</sec:authorize>
<form:hidden path="bookdefinitionId" />
<div id="generalSection" class="section">
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
			<c:if test="${disableOptions}">
				<%-- Hidden fields needed when options are disabled.
					 Options reset to defaults if hidden fields are missing. --%>
				<form:hidden path="includeAnnotations"/>
			</c:if>
			<div class="row">
				<form:label path="includeAnnotations" class="labelCol">Include Annotations</form:label>
				<form:radiobutton disabled="${disableOptions}" path="includeAnnotations" value="true" />Yes
				<form:radiobutton disabled="${disableOptions}" path="includeAnnotations" value="false" />No
				<div class="errorDiv">
					<form:errors path="includeAnnotations" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="excludeDocumentsUsed" class="labelCol">Enable Exclude Documents</form:label>
				<form:radiobutton path="excludeDocumentsUsed" value="true" />Yes
				<form:radiobutton path="excludeDocumentsUsed" value="false" />No
				<div class="errorDiv">
					<form:errors path="excludeDocumentsUsed" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="renameTocEntriesUsed" class="labelCol">Enable Rename TOC Labels</form:label>
				<form:radiobutton path="renameTocEntriesUsed" value="true" />Yes
				<form:radiobutton path="renameTocEntriesUsed" value="false" />No
				<div class="errorDiv">
					<form:errors path="renameTocEntriesUsed" cssClass="errorMessage" />
				</div>
			</div>
		</div>
		
		<div class="rightDefinitionForm">
			<c:if test="${disableOptions}">
				<%-- Hidden fields needed when options are disabled.
					 Options reset to defaults if hidden fields are missing. --%>
				<form:hidden path="isTOC"/>
				<form:hidden path="tocCollectionName"/>
				<form:hidden path="docCollectionName"/>
				<form:hidden path="rootTocGuid"/>
				<form:hidden path="nortDomain"/>
				<form:hidden path="nortFilterView"/>
				<form:hidden path="keyCiteToplineFlag"/>
			</c:if>
			<div class="row">
				<label class="labelCol">TOC or NORT</label>
				<form:radiobutton disabled="${disableOptions}" path="isTOC" value="true" />TOC
				<form:radiobutton disabled="${disableOptions}" path="isTOC" value="false" />NORT
			</div>
			<div id="displayTOC" style="display:none">
				<div class="row">
					<form:label disabled="${disableOptions}" path="tocCollectionName" class="labelCol">TOC Collection</form:label>
					<form:input disabled="${disableOptions}" path="tocCollectionName" />
					<div class="errorDiv">
						<form:errors path="tocCollectionName" cssClass="errorMessage" />
					</div>
				</div>
				<div class="row">
					<form:label disabled="${disableOptions}" path="docCollectionName" class="labelCol">DOC Collection</form:label>
					<form:input disabled="${disableOptions}" path="docCollectionName" />
					<div class="errorDiv">
						<form:errors path="docCollectionName" cssClass="errorMessage" />
					</div>
				</div>
				<div class="row">
					<form:label disabled="${disableOptions}" path="rootTocGuid" class="labelCol">Root TOC Guid</form:label>
					<form:input disabled="${disableOptions}" path="rootTocGuid" maxlength="33"/>
					<div class="errorDiv">
						<form:errors path="rootTocGuid" cssClass="errorMessage" />
					</div>
				</div>
			</div>
			<div id="displayNORT" style="display:none">
				<div class="row">
					<form:label disabled="${disableOptions}" path="nortDomain" class="labelCol">NORT Domain</form:label>
					<form:input disabled="${disableOptions}" path="nortDomain" />
					<div class="errorDiv">
						<form:errors path="nortDomain" cssClass="errorMessage" />
					</div>
				</div>
				<div class="row">
					<form:label disabled="${disableOptions}" path="nortFilterView" class="labelCol">NORT Filter View</form:label>
					<form:input disabled="${disableOptions}" path="nortFilterView" />
					<div class="errorDiv">
						<form:errors path="nortFilterView" cssClass="errorMessage" />
					</div>
				</div>
				<div class="row">
					<form:label path="useReloadContent" class="labelCol">Use Reload Content</form:label>
					<form:radiobutton path="useReloadContent" value="true" />True
					<form:radiobutton path="useReloadContent" value="false" />False
					<div class="errorDiv">
						<form:errors path="useReloadContent" cssClass="errorMessage" />
					</div>
				</div>
			</div>
			<div class="row">
				<form:label path="keyCiteToplineFlag" class="labelCol">KeyCite Topline Flag</form:label>
				<form:radiobutton disabled="${disableOptions}" path="keyCiteToplineFlag" value="true" />True
				<form:radiobutton disabled="${disableOptions}" path="keyCiteToplineFlag" value="false" />False
				<div class="errorDiv">
					<form:errors path="keyCiteToplineFlag" cssClass="errorMessage" />
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
				<form:label path="finalStage" class="labelCol">Novus Stage</form:label>
				<form:radiobutton path="finalStage" value="true" />Final
				<form:radiobutton path="finalStage" value="false" />Review
				<div class="errorDiv">
					<form:errors path="finalStage" cssClass="errorMessage" />
				</div>
			</div>
		</div>
	</div>
</div>

<div id="displayExcludeDocument" class="dynamicContent" style="display:none;">
	<c:forEach items="${editBookDefinitionForm.excludeDocumentsCopy}" var="documentCopy" varStatus="aStatus">
			<form:hidden path="excludeDocumentsCopy[${aStatus.index}].documentGuid" maxlength="33" />
			<form:hidden path="excludeDocumentsCopy[${aStatus.index}].note" />
			<form:hidden path="excludeDocumentsCopy[${aStatus.index}].lastUpdated"/>
	</c:forEach>
	<form:label path="excludeDocumentsUsed" class="labelCol">Exclude Documents</form:label>
	<input type="button" id="addExcludeDocument" value="add" />
	<div class="errorDiv">
		<form:errors path="excludeDocuments" cssClass="errorMessage" />
	</div>
	<c:forEach items="${editBookDefinitionForm.excludeDocuments}" var="document" varStatus="aStatus">
		<div class="expandingBox">
			<div class="dynamicRow">
				<label>Document Guid</label>
				<form:input cssClass="guid" path="excludeDocuments[${aStatus.index}].documentGuid" maxlength="33" />
				<div class="errorDiv">
					<form:errors path="excludeDocuments[${aStatus.index}].documentGuid" cssClass="errorMessage" />
				</div>
			</div><div class="dynamicRow">
				<label>Note</label>
				<form:textarea path="excludeDocuments[${aStatus.index}].note" />
				<div class="errorDiv">
					<form:errors path="excludeDocuments[${aStatus.index}].note" cssClass="errorMessage" />
				</div>
			</div><div class="dynamicRow">
				<label>Last Updated</label>
				<form:input disabled="true" path="excludeDocuments[${aStatus.index}].lastUpdated" />
				<form:hidden path="excludeDocuments[${aStatus.index}].lastUpdated"/>
				<div class="errorDiv">
					<form:errors path="excludeDocuments[${aStatus.index}].lastUpdated" cssClass="errorMessage" />
				</div>
			</div><input type="button" value="Delete" class="rdelete" title="Delete Exclude Document" />
		</div>
	</c:forEach>
	<div id="addExcludeDocumentHere"></div>
</div>

<div id="displayRenameTocEntry" class="dynamicContent" style="display:none;">
	<c:forEach items="${editBookDefinitionForm.renameTocEntriesCopy}" var="tocCopy" varStatus="aStatus">
			<form:hidden path="renameTocEntriesCopy[${aStatus.index}].tocGuid" />
			<form:hidden path="renameTocEntriesCopy[${aStatus.index}].oldLabel" />
			<form:hidden path="renameTocEntriesCopy[${aStatus.index}].newLabel" />
			<form:hidden path="renameTocEntriesCopy[${aStatus.index}].note" />
			<form:hidden path="renameTocEntriesCopy[${aStatus.index}].lastUpdated"/>
	</c:forEach>
	<label class="labelCol">Rename TOC Labels</label>
	<input type="button" id="addRenameTocEntry" value="add" />
	<div class="errorDiv">
		<form:errors path="renameTocEntries" cssClass="errorMessage" />
	</div>
	<c:forEach items="${editBookDefinitionForm.renameTocEntries}" var="toc" varStatus="aStatus">
		<div class="expandingBox">
			<div class="dynamicRow">
				<label>Guid</label>
				<form:input cssClass="guid" path="renameTocEntries[${aStatus.index}].tocGuid" maxlength="33" />
				<div class="errorDiv">
					<form:errors path="renameTocEntries[${aStatus.index}].tocGuid" cssClass="errorMessage" />
				</div>
			</div><div class="dynamicRow">
				<label>Old Label</label>
				<form:input path="renameTocEntries[${aStatus.index}].oldLabel" />
				<div class="errorDiv">
					<form:errors path="renameTocEntries[${aStatus.index}].oldLabel" cssClass="errorMessage" />
				</div>
			</div><div class="dynamicRow">
				<label>New Label</label>
				<form:input path="renameTocEntries[${aStatus.index}].newLabel" />
				<div class="errorDiv">
					<form:errors path="renameTocEntries[${aStatus.index}].newLabel" cssClass="errorMessage" />
				</div>
			</div><div class="dynamicRow">
				<label>Note</label>
				<form:textarea path="renameTocEntries[${aStatus.index}].note" />
				<div class="errorDiv">
					<form:errors path="renameTocEntries[${aStatus.index}].note" cssClass="errorMessage" />
				</div>
			</div><div class="dynamicRow">
				<label>Last Updated</label>
				<form:input disabled="true" path="renameTocEntries[${aStatus.index}].lastUpdated" />
				<form:hidden path="renameTocEntries[${aStatus.index}].lastUpdated"/>
				<div class="errorDiv">
					<form:errors path="renameTocEntries[${aStatus.index}].lastUpdated" cssClass="errorMessage" />
				</div>
			</div><input type="button" value="Delete" class="rdelete" title="Delete Rename TOC Entry" />
		</div>
	</c:forEach>
	<div id="addRenameTocEntryHere"></div>
</div>

<div id="displayDocumentCopyright" class="dynamicContent">
	<c:forEach items="${editBookDefinitionForm.documentCopyrightsCopy}" varStatus="aStatus">
			<form:hidden path="documentCopyrightsCopy[${aStatus.index}].copyrightGuid" />
			<form:hidden path="documentCopyrightsCopy[${aStatus.index}].newText" />
			<form:hidden path="documentCopyrightsCopy[${aStatus.index}].note" />
			<form:hidden path="documentCopyrightsCopy[${aStatus.index}].lastUpdated"/>
	</c:forEach>
	<label class="labelCol">Document Copyright</label>
	<input type="button" id="addDocumentCopyright" value="add" />
	<div class="errorDiv">
		<form:errors path="documentCopyrights" cssClass="errorMessage" />
	</div>
	<c:forEach items="${editBookDefinitionForm.documentCopyrights}" varStatus="aStatus">
		<div class="expandingBox">
			<div class="dynamicRow">
				<label>Copyright Guid</label>
				<form:input cssClass="guid" path="documentCopyrights[${aStatus.index}].copyrightGuid" maxlength="33" />
				<div class="errorDiv">
					<form:errors path="documentCopyrights[${aStatus.index}].copyrightGuid" cssClass="errorMessage" />
				</div>
			</div>
			<div class="dynamicRow">
				<label>New Text</label>
				<form:input path="documentCopyrights[${aStatus.index}].newText" />
				<div class="errorDiv">
					<form:errors path="documentCopyrights[${aStatus.index}].newText" cssClass="errorMessage" />
				</div>
			</div>
			<div class="dynamicRow">
				<label>Note</label>
				<form:textarea path="documentCopyrights[${aStatus.index}].note" />
				<div class="errorDiv">
					<form:errors path="documentCopyrights[${aStatus.index}].note" cssClass="errorMessage" />
				</div>
			</div>
			<div class="dynamicRow">
				<label>Last Updated</label>
				<form:input disabled="true" path="documentCopyrights[${aStatus.index}].lastUpdated" />
				<form:hidden path="documentCopyrights[${aStatus.index}].lastUpdated"/>
				<div class="errorDiv">
					<form:errors path="documentCopyrights[${aStatus.index}].lastUpdated" cssClass="errorMessage" />
				</div>
			</div>
			<input type="button" value="Delete" class="rdelete" title="Delete Document Copyright" />
		</div>
	</c:forEach>
	<div id="addDocumentCopyrightHere"></div>
</div>

<div id="displayDocumentCurrency" class="dynamicContent">
	<c:forEach items="${editBookDefinitionForm.documentCurrenciesCopy}" varStatus="aStatus">
			<form:hidden path="documentCurrenciesCopy[${aStatus.index}].currencyGuid" />
			<form:hidden path="documentCurrenciesCopy[${aStatus.index}].newText" />
			<form:hidden path="documentCurrenciesCopy[${aStatus.index}].note" />
			<form:hidden path="documentCurrenciesCopy[${aStatus.index}].lastUpdated"/>
	</c:forEach>
	<label class="labelCol">Document Currency</label>
	<input type="button" id="addDocumentCurrency" value="add" />
	<div class="errorDiv">
		<form:errors path="documentCurrencies" cssClass="errorMessage" />
	</div>
	<c:forEach items="${editBookDefinitionForm.documentCurrencies}" varStatus="aStatus">
		<div class="expandingBox">
			<div class="dynamicRow">
				<label>Currency Guid</label>
				<form:input cssClass="guid" path="documentCurrencies[${aStatus.index}].currencyGuid" maxlength="33" />
				<div class="errorDiv">
					<form:errors path="documentCurrencies[${aStatus.index}].currencyGuid" cssClass="errorMessage" />
				</div>
			</div>
			<div class="dynamicRow">
				<label>New Text</label>
				<form:input path="documentCurrencies[${aStatus.index}].newText" />
				<div class="errorDiv">
					<form:errors path="documentCurrencies[${aStatus.index}].newText" cssClass="errorMessage" />
				</div>
			</div>
			<div class="dynamicRow">
				<label>Note</label>
				<form:textarea path="documentCurrencies[${aStatus.index}].note" />
				<div class="errorDiv">
					<form:errors path="documentCurrencies[${aStatus.index}].note" cssClass="errorMessage" />
				</div>
			</div>
			<div class="dynamicRow">
				<label>Last Updated</label>
				<form:input disabled="true" path="documentCurrencies[${aStatus.index}].lastUpdated" />
				<form:hidden path="documentCurrencies[${aStatus.index}].lastUpdated"/>
				<div class="errorDiv">
					<form:errors path="documentCurrencies[${aStatus.index}].lastUpdated" cssClass="errorMessage" />
				</div>
			</div>
			<input type="button" value="Delete" class="rdelete" title="Delete Document Currency" />
		</div>
	</c:forEach>
	<div id="addDocumentCurrencyHere"></div>
</div>

<div id="proviewSection" class="section">
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
			<c:if test="${disableOptions}">
				<%-- Hidden fields needed when options are disabled.
					 Options reset to defaults if hidden fields are missing. --%>
				<form:hidden path="autoUpdateSupport"/>
				<form:hidden path="searchIndex"/>
				<form:hidden path="enableCopyFeatureFlag"/>
				<form:hidden path="pilotBook"/>
			</c:if>
			<div class="row">
				<form:label path="autoUpdateSupport" class="labelCol">Auto-update Support</form:label>
				<form:radiobutton disabled="${disableOptions}" path="autoUpdateSupport" value="true" />True
				<form:radiobutton disabled="${disableOptions}" path="autoUpdateSupport" value="false" />False
				<div class="errorDiv">
					<form:errors path="autoUpdateSupport" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="searchIndex" class="labelCol">Search Index</form:label>
				<form:radiobutton disabled="${disableOptions}" path="searchIndex" value="true" />True
				<form:radiobutton disabled="${disableOptions}" path="searchIndex" value="false" />False
				<div class="errorDiv">
					<form:errors path="searchIndex" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="enableCopyFeatureFlag" class="labelCol">Enable Copy Feature</form:label>
				<form:radiobutton disabled="${disableOptions}" path="enableCopyFeatureFlag" value="true" />True
				<form:radiobutton disabled="${disableOptions}" path="enableCopyFeatureFlag" value="false" />False
				<div class="errorDiv">
					<form:errors path="enableCopyFeatureFlag" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="pilotBook" class="labelCol">Pilot Book: Notes Migration</form:label>
				<form:radiobutton disabled="${disableOptions}" path="pilotBook" value="<%= PilotBookStatus.TRUE.toString() %>" />True
				<form:radiobutton disabled="${disableOptions}" path="pilotBook" value="<%= PilotBookStatus.FALSE.toString() %>" />False
				<form:radiobutton disabled="${disableOptions}" path="pilotBook" value="<%= PilotBookStatus.IN_PROGRESS.toString() %>" />In Progress
				<div class="errorDiv">
					<form:errors path="pilotBook" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row" style="font-size:.7em; text-align: center;">
				*Only Super Users are able to modify above options.
			</div>
			<div class="row">
				<form:label path="tableViewersUsed" class="labelCol">Enable Table Viewer</form:label>
				<form:radiobutton path="tableViewersUsed" value="true" />Yes
				<form:radiobutton path="tableViewersUsed" value="false" />No
				<div class="errorDiv">
					<form:errors path="tableViewersUsed" cssClass="errorMessage" />
				</div>
			</div>
			<div id="addTableViewerRow" class="row" style="display:none;">
				<label class="labelCol">Table Viewer</label>
				<input type="button" id="addTableViewer" value="add" />
			</div>
		</div>
	</div>
</div>

<div id="displayTableViewer" class="dynamicContent" style="display:none;">
	<c:forEach items="${editBookDefinitionForm.tableViewersCopy}" var="documentCopy" varStatus="aStatus">
			<form:hidden path="tableViewersCopy[${aStatus.index}].documentGuid" maxlength="33" />
			<form:hidden path="tableViewersCopy[${aStatus.index}].note" />
			<form:hidden path="tableViewersCopy[${aStatus.index}].lastUpdated"/>
	</c:forEach>
	<div class="errorDiv">
		<form:errors path="tableViewers" cssClass="errorMessage" />
	</div>
	<c:forEach items="${editBookDefinitionForm.tableViewers}" var="document" varStatus="aStatus">
		<div class="expandingBox">
			<div class="dynamicRow">
				<label>Document Guid</label>
				<form:input cssClass="guid" path="tableViewers[${aStatus.index}].documentGuid" maxlength="33" />
				<div class="errorDiv">
					<form:errors path="tableViewers[${aStatus.index}].documentGuid" cssClass="errorMessage" />
				</div>
			</div><div class="dynamicRow">
				<label>Note</label>
				<form:textarea path="tableViewers[${aStatus.index}].note" />
				<div class="errorDiv">
					<form:errors path="tableViewers[${aStatus.index}].note" cssClass="errorMessage" />
				</div>
			</div><div class="dynamicRow">
				<label>Last Updated</label>
				<form:input disabled="true" path="tableViewers[${aStatus.index}].lastUpdated" />
				<form:hidden path="tableViewers[${aStatus.index}].lastUpdated"/>
				<div class="errorDiv">
					<form:errors path="tableViewers[${aStatus.index}].lastUpdated" cssClass="errorMessage" />
				</div>
			</div><input type="button" value="Delete" class="rdelete" title="Delete ProView Table Viewer Entry" />
		</div>
	</c:forEach>
	<div id="addTableViewerHere"></div>
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
				<form:label path="additionalTrademarkInfo" class="labelCol">Additional Patent/Trademark Message</form:label>
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
				<div id="addAuthorHere">
					<c:forEach items="${editBookDefinitionForm.authorInfo}" var="author" varStatus="aStatus">
						<div class="expandingBox">
							<div class="errorDiv">
								<form:errors path="authorInfo[${aStatus.index}].sequenceNum" cssClass="errorMessage" />
							</div>
							<button class="moveUp" type="button">Up</button>
							<button class="moveDown" type="button">Down</button>
							<form:hidden path="authorInfo[${aStatus.index}].authorId"/>
							<form:hidden path="authorInfo[${aStatus.index}].sequenceNum" class="sequence"/>
							<div class="dynamicRow">
								<label>Prefix</label>
								<form:input path="authorInfo[${aStatus.index}].authorNamePrefix" />
								<div class="errorDiv">
									<form:errors path="authorInfo[${aStatus.index}].authorNamePrefix" cssClass="errorMessage" />
								</div>
							</div>
							<div class="dynamicRow">
								<label>First Name</label>
								<form:input path="authorInfo[${aStatus.index}].authorFirstName" />
								<div class="errorDiv">
									<form:errors path="authorInfo[${aStatus.index}].authorFirstName" cssClass="errorMessage" />
								</div>
							</div>
							<div class="dynamicRow">
								<label>Middle Name</label>
								<form:input path="authorInfo[${aStatus.index}].authorMiddleName" />
								<div class="errorDiv">
									<form:errors path="authorInfo[${aStatus.index}].authorMiddleName" cssClass="errorMessage" />
								</div>
							</div>
							<div class="dynamicRow">
								<label>Last Name</label>
								<form:input path="authorInfo[${aStatus.index}].authorLastName" />
								<div class="errorDiv">
									<form:errors path="authorInfo[${aStatus.index}].authorLastName" cssClass="errorMessage" />
								</div>
							</div>
							<div class="dynamicRow">
								<label>Suffix</label>
								<form:input path="authorInfo[${aStatus.index}].authorNameSuffix"  />
								<div class="errorDiv">
									<form:errors path="authorInfo[${aStatus.index}].authorNameSuffix" cssClass="errorMessage" />
								</div>
							</div>
							<div class="dynamicRow">
								<label>Additional Text</label>
								<form:textarea path="authorInfo[${aStatus.index}].authorAddlText" />
								<div class="errorDiv">
									<form:errors path="authorInfo[${aStatus.index}].authorAddlText" cssClass="errorMessage" />
								</div>
							</div>
							<div class="dynamicRow">
								Use Comma Before Suffix
								<form:checkbox path="authorInfo[${aStatus.index}].useCommaBeforeSuffix"  title="Comma After Suffix" />
							</div>
							<input type="button" value="Delete" class="rdelete" title="Delete Author" />
						</div>
					</c:forEach>
				</div>
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
	<div id="addAdditionPageHere">
		<c:forEach items="${editBookDefinitionForm.frontMatters}" var="page" varStatus="pageStatus">
			<div class="row frontMatterPage">
				<form:hidden path="frontMatters[${pageStatus.index}].id"/>
				<form:hidden path="frontMatters[${pageStatus.index}].sequenceNum" cssClass="sequence" />
				<form:input path="frontMatters[${pageStatus.index}].pageTocLabel" title="Page TOC Label" cssClass="pageTocLabel" /><form:input path="frontMatters[${pageStatus.index}].pageHeadingLabel" title="Page Heading Label" cssClass="pageHeadingLabel" /><button class="moveUp" type="button">Up</button><button class="moveDown" type="button">Down</button><input type="button" value="Delete Page" class="rdelete" title="Delete Page, Sections, and Pdfs?" deleteMessage="This will also delete all the sections and pdfs in this front matter page." /><input type="button" value="Preview" class="fmPreview"/>   
				<div class="errorDiv2">
					<form:errors path="frontMatters[${pageStatus.index}].pageTocLabel" cssClass="errorMessage" />
					<form:errors path="frontMatters[${pageStatus.index}].pageHeadingLabel" cssClass="errorMessage" />
					<form:errors path="frontMatters[${pageStatus.index}].sequenceNum" cssClass="errorMessage" />
				</div>
				<c:set var="sectionIndex" value="0"/>
				<div id="addAdditionalSection_${pageStatus.index}">
					<c:forEach items="${page.frontMatterSections}" var="section" varStatus="sectionStatus">
						<div class="row frontMatterSection">
							<c:set var="sectionIndex" value="${sectionStatus.index}"/>
							<form:hidden path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].id"   />
							<form:hidden path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].sequenceNum" cssClass="sequence" />
							<form:input path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].sectionHeading" title="Section Heading" /><button class="moveUp" type="button">Up</button><button class="moveDown" type="button">Down</button><input type="button" value="Delete Section" class="rdelete" title="Delete Section and Pdfs?" deleteMessage="This will also delete all the pdfs in this front matter section."/>
							<div class="errorDiv2">
								<form:errors path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].sectionHeading" cssClass="errorMessage" />
								<form:errors path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].sequenceNum" cssClass="errorMessage" />
							</div>
							<form:textarea path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].sectionText" title="Section Text" class="frontMatterSectionTextArea" />
							<div class="errorDiv2">
								<form:errors path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].sectionText" cssClass="errorMessage" />
							</div>
							<c:set var="pdfIndex" value="0"/>
							<div id="addAdditionalPdf_${pageStatus.index}_${sectionStatus.index}">
								<c:forEach items="${section.pdfs}" var="pdf" varStatus="pdfStatus">
									<div class="row">
										<c:set var="pdfIndex" value="${pdfStatus.index}"/>
										<form:hidden path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].pdfs[${pdfStatus.index}].id" />
										<form:hidden path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].pdfs[${pdfStatus.index}].sequenceNum" cssClass="sequence" />
										<form:input path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].pdfs[${pdfStatus.index}].pdfLinkText"   title="PDF Link Text" /><form:input path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].pdfs[${pdfStatus.index}].pdfFilename"   title="PDF Filename" /><button class="moveUp" type="button">Up</button><button class="moveDown" type="button">Down</button><input type="button" value="Delete Pdf" class="rdelete" title="Delete Pdf?" />
										<div class="errorDiv2">
											<form:errors path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].pdfs[${pdfStatus.index}].pdfLinkText" cssClass="errorMessage" />
											<form:errors path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].pdfs[${pdfStatus.index}].pdfFilename" cssClass="errorMessage" />
											<form:errors path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].pdfs[${pdfStatus.index}].sequenceNum" cssClass="errorMessage" />
										</div>
									</div>
								</c:forEach>
							</div>
							<input type="button" value="Add Pdf" class="addPdf" pageIndex="${pageStatus.index}" sectionIndex="${sectionStatus.index}" pdfIndex="${pdfIndex + 1}"  />
						</div>
					</c:forEach>
				</div>
				<input type="button" value="Add Section" class="addSection" pageIndex="${pageStatus.index}" sectionIndex="${sectionIndex + 1}"  />
				<div class="errorDiv2">
				</div>
			</div>
		</c:forEach>
	</div>
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

