$(function() {
	$(document).ready(function() {
		// Declare Global Variables
		var authorIndex = parseInt($("#numberOfAuthors").val());
		var pilotBookIndex = parseInt($("#numberOfPilotBooks").val());
		var frontMatterPageIndex = parseInt($("#numberOfFrontMatters").val());
		var excludeDocumentIndex = parseInt($("#numberOfExcludeDocuments").val());
		var renameTocEntryIndex = parseInt($("#numberOfRenameTocEntries").val());
		var tableViewerIndex = parseInt($("#numberOfTableViewers").val());
		var documentCopyrightIndex = parseInt($("#numberOfDocumentCopyrights").val());
		var documentCurrencyIndex = parseInt($("#numberOfDocumentCurrencies").val());		
		var contentType = "";
		var publisher = "";
		var state = "";
		var pubType = "";
		var pubAbbr = "";
		var pubInfo = "";
		var jurisdiction = "";
		var productCode = "";	
		var splitDocumentIndex =  parseInt($("#numberOfSplitDocuments").val());	
		var isSplitTypeAuto = true;
		var splitSize ="";
		
		// Function to create Fully Qualifed Title ID from the publisher options
		var updateTitleId = function() {

			var titleId = [];
			
			// Set up Title ID
			if(contentType == $("#documentTypeAnalyticalAbbr").val()) {
				if (pubInfo) {
					titleId.push(pubAbbr, pubInfo);
				} else {
					titleId.push(pubAbbr);
				};
			} else if(contentType == $("#documentTypeCourtRulesAbbr").val()) {
				if (pubInfo) {
					titleId.push(state, pubType, pubInfo);
				} else {
					titleId.push(state, pubType);
				};
			} else if(contentType == $("#documentTypeSliceCodesAbbr").val()) {
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

			} else if (publisher) {
				$('#contentTypeDiv').show();
				if(contentType == $("#documentTypeAnalyticalAbbr").val()) {
					$('#pubAbbrDiv').show();
				} else if(contentType == $("#documentTypeCourtRulesAbbr").val()) {
					$('#stateDiv').show();
					$('#pubTypeDiv').show();
				} else if(contentType == $("#documentTypeSliceCodesAbbr").val()) {
					$('#jurisdictionDiv').show();
				}
				
				if (contentType) {
					$('#publishDetailDiv').show();
				} else {
					$('#publishDetailDiv').hide();
				}
			}
		};
		
		var onClickToFmPreviewButton = function() {
			var pageSequenceNumber = $(this).parent().children(".sequence").val();
			$('#selectedFrontMatterPreviewPage').val(pageSequenceNumber);
			submitFormForValidation();
		}; 
		
		var onClickToAddSectionButton = function () {
			// Retrieve additional page and section indexes from DOM object
			pageIndex = $(this).attr("pageIndex");
			sectionIndex = $(this).attr("sectionIndex");
			addFrontMatterSectionRow(pageIndex, sectionIndex);
			
			// Increment sectionIndex
			nextIndex = parseInt(sectionIndex) + 1;
			$(this).attr("sectionIndex", nextIndex);
		}
		
		var onClickToAddPdfButton = function () {
			// Retrieve additional page, section, and pdf indexes from DOM object
			pageIndex = $(this).attr("pageIndex");
			sectionIndex = $(this).attr("sectionIndex");
			pdfIndex = $(this).attr("pdfIndex");
			addFrontMatterPdfRow(pageIndex, sectionIndex, pdfIndex);
			
			// Increment pdfIndex
			nextIndex = parseInt(pdfIndex) + 1;
			$(this).attr("pdfIndex", nextIndex);
		}
		
		var onClickToDeleteButton = function () {
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
		};
		
		var addPilotBookRow = function() {
			var expandingBox = $("<div>").addClass("expandingBox");
			var id = "pilotBookInfo" + pilotBookIndex;
			var name = "pilotBookInfo[" + pilotBookIndex + "]";
			
			expandingBox.append($("<button>").attr("type","button").attr("id","pilotUp." +pilotBookIndex).addClass("moveUp").html("Up"));
			expandingBox.append($("<button>").attr("type","button").attr("id","pilotDown." +pilotBookIndex).addClass("moveDown").html("Down"));
			
			// Add sequence number
			var lastChild = $("#addPilotBookHere .expandingBox:last-child");
			var lastSequenceNum = getSequenceNumber(lastChild);
			var sequenceBox = $("<input>").attr("type","hidden").addClass("sequence").attr("id",id).attr("name", name + ".sequenceNum").attr("value",lastSequenceNum + 1);
			expandingBox.append(sequenceBox);
			
			// Add title information
			expandingBox.append(addDynamicRow("input", id, name, "pilotBookTitleId", "Title ID"));
			expandingBox.append(addDynamicRow("textarea", id, name, "pilotBookNote", "Note"));
			
			// Add delete button
			expandingBox.append($("<input>").addClass("rdelete").attr("title","Delete Pilot Book").attr("type", "button").attr("id","pilotDelete"+pilotBookIndex).val("Delete").on("click", onClickToDeleteButton));
		
			$("#addPilotBookHere").append(expandingBox);
			pilotBookIndex = pilotBookIndex + 1;
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
			var sequenceBox = $("<input>").attr("type","hidden").addClass("sequence").attr("id",id).attr("name", name + ".sequenceNum").attr("value",lastSequenceNum + 1);
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
			expandingBox.append($("<input>").addClass("rdelete").attr("title","Delete Author").attr("type", "button").attr("id","authorDelete"+authorIndex).val("Delete").on("click", onClickToDeleteButton));
		
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
		};
		
		var getDateTimeString = function(date) {
			var month = date.getMonth() + 1;
			var day = date.getDate();
			var year = date.getFullYear();
			var hour = date.getHours();
			var minute = date.getMinutes();
			var second = date.getSeconds();
			
			return leadingZero(month) + "/" + leadingZero(day) + "/" + year + " " + leadingZero(hour) + ":" + leadingZero(minute) + ":" + leadingZero(second);
		};
		
		var leadingZero = function(val) {
			var str = val.toString();
			if(str.length == 1) {
				str = '0' + str;
			}
			return str;
		};
		
		var addSplitGuidRow = function(elementName, index, guidAttrName, guidLabelName, addHere) {
			var expandingBox = $("<div>").addClass("expandingBox");
			
			var id = elementName + index;
			var name = elementName + "[" + index + "]";	
			
			// Add Document Guid input boxes
			expandingBox.append(addDynamicRow("input", id, name, guidAttrName, guidLabelName, 33, "guid"));
		
			// Add Note text box
			expandingBox.append(addDynamicRow("textarea", id, name, "note", "Note"));
				

			addHere.before(expandingBox);
		};
		
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
			expandingBox.append($("<input>").addClass("rdelete").attr("title",titleMessage).attr("type", "button").val("Delete").on("click", onClickToDeleteButton));

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
			expandingBox.append($("<input>").addClass("rdelete").attr("title","Delete Page, Sections, and Pdfs?").attr("type", "button").attr("deleteMessage", "This will also delete all the sections and pdfs in this front matter page.").val("Delete Page").on("click", onClickToDeleteButton));
			expandingBox.append($("<button>").attr("type","button").addClass("fmPreview").html("Preview").on("click", onClickToFmPreviewButton));
			
			var section = $("<div>").attr("id", "addAdditionalSection_" + frontMatterPageIndex);
			section.append($("<input>").addClass("addSection").attr("pageIndex", frontMatterPageIndex).attr("sectionIndex", 0).attr("type", "button").val("Add Section").on("click", onClickToAddSectionButton));
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
			expandingBox.append($("<input>").addClass("rdelete").attr("title","Delete Section and Pdfs?").attr("type", "button").attr("deleteMessage", "This will also delete all the pdfs in this front matter section.").val("Delete Section").on("click", onClickToDeleteButton));

			// Add addition text input box
			var additionalText = $("<div>").addClass("dynamicRow");
			additionalText.append($("<textarea>").attr("id",id +".sectionText").attr("name", name + ".sectionText").addClass("frontMatterSectionTextArea"));
			expandingBox.append(additionalText);

			var pdfSection = $("<div>").attr("id", "addAdditionalPdf_" + pageIndex + "_" + sectionIndex);
			pdfSection.append($("<input>").addClass("addPdf").attr("pageIndex", pageIndex).attr("sectionIndex", sectionIndex).attr("pdfIndex", 0).attr("type", "button").val("Add Pdf").on("click", onClickToAddPdfButton));
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
			expandingBox.append($("<input>").addClass("rdelete").attr("title","Delete Pdf").attr("type", "button").val("Delete Pdf").on("click", onClickToDeleteButton));
			
			$(addAdditionalPdf).append(expandingBox);
			
			textboxHint("additionFrontMatterBlock");
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
			//$('#<%= EditBookDefinitionForm.FORM_NAME %>').submit(); --todo
		};
		
		var getSequenceNumber = function(element) {
			var sequenceNum = parseInt(element.children(".sequence").val());
			// No sequence number, set sequence to zero
			if(isNaN(sequenceNum)) {
				sequenceNum = 0;
			}
			
			return sequenceNum;
		};
	
		// If there is front matter preview content to display, then display it in its own window 
		if (typeof openFrontMatterPreviewWindow === "function") {
			openFrontMatterPreviewWindow();
		}
		
		// Setup change handlers  
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
		
		$('input:radio[name=splitBook]').change(function () {
			var isSplitBook = $.trim($(this).val());
			$("#splitTypeDiv").hide();			
			if(isSplitBook == "true" || isSplitBook == true) {	
				$("#splitTypeDiv").show();	
				$("input:radio[name=splitTypeAuto][value=true]").attr('checked', true);	
			} 
			else{
				$("#splitTypeAuto").remove();
				$("input:radio[name=splitTypeAuto][value=false]").attr('checked', false);
				$("#ebookSizeDiv").hide();
				$("#splitTypeDiv").hide();
				$("#displaySplitDocument").hide();	
				$("#displaySplitDocument .expandingBox").remove();
				splitDocumentIndex = 0;
				$("#splitEBookParts").find('option').removeAttr('selected');
			}
			
		});
		
		$('input:radio[name=splitTypeAuto]').change(function () {
			$("#ebookSizeDiv").hide();		
			isSplitTypeAuto = $.trim($(this).val());
			if(isSplitTypeAuto == "false" || isSplitTypeAuto == false) {
				$('#splitEBookParts option:first-child').attr("selected", true);
				$("#ebookSizeDiv").show();	
			} 
			else{
				$("#displaySplitDocument").hide();
				$("#displaySplitDocument .expandingBox").remove();
				splitDocumentIndex = 0;
				$("#splitEBookParts").find('option').removeAttr('selected');
			}
		});		
		
		
		$('#splitEBookParts').change(function () {
			splitSize = parseInt($(this).val());	
			var size = 1;
			if (isNaN(splitSize)){
				splitSize = 0;
				splitDocumentIndex = 0;
			}
			
			$('#displaySplitDocument').children('.expandingBox').each(function() {
				size = size + 1;
				if (size > splitSize)
				{
					if (splitDocumentIndex > 0){
						splitDocumentIndex = splitDocumentIndex - 1;
					}
					$(this).remove();
				}
			});		
			
			$("#displaySplitDocument").show();
			
			if (splitSize > size){			
				for(var i = size; i < splitSize; i++) {
					addSplitGuidRow("splitDocuments", splitDocumentIndex, "tocGuid", "TOC/NORT GUID", $("#addSplitDocumentsHere"));
					splitDocumentIndex = splitDocumentIndex + 1;
				}
			}
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
		
		// Setup Button Click handlers 
		$('#addAuthor').click(function () {
			addAuthorRow();
			
			// IE8 bug: forcing reflow/redraw to resize the parent div 
			$('#authorName').hide();
			$('#authorName').show();
		});
		
		$('#addPilotBook').click(function () {
			addPilotBookRow();
			
			// IE8 bug: forcing reflow/redraw to resize the parent div 
			$('#pilotBook').hide();
			$('#pilotBook').show();
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
		$('.fmPreview').on("click", onClickToFmPreviewButton);
	
		//Update formValidation field if Validation button is pressed
		$('#validate').click(submitFormForValidation);
		
		// Determine to show publication cut-off date
		$('input:radio[name=publicationCutoffDateUsed]').change(function () {
			updatePubCutoffDate($(this).val());
			
			// IE8 bug: forcing reflow/redraw to resize the parent div 
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
		
		$('input:radio[name=groupsEnabled]').change(function () {
			showSelectOptions($(this).val(), "#displayProviewGroup");
			if($(this).val() == "false" || $(this).val() == false) {
				$("#groupName").val("");
				$("#subGroupHeading").val("");
			}
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
				
				// IE8 bug: forcing reflow/redraw to resize the parent div 
				$("#proviewSection").hide();
				$("#proviewSection").show();
		});
		
		// delete confirmation box
		$(".rdelete").on("click", onClickToDeleteButton);
		
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
		
		$(".addSection").on("click", onClickToAddSectionButton);
		
		$(".addPdf").on("click", onClickToAddPdfButton);
		
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
		
		var clearGrid = function(grid) {
			var gridData = grid.jsGrid("option", "data");
			grid.jsGrid("option", "confirmDeleting", false);
			while(gridData.length > 0) {
				grid.jsGrid("deleteItem", gridData[0]);
				gridData = grid.jsGrid("option", "data");
			}
			grid.jsGrid("option", "confirmDeleting", true);
		};
		
		var isSameData = function(grid, sapData) {
			var result = true; 
			var gridData = grid.jsGrid("option", "data");
			if(sapData.length == gridData.length) {
				for(var i=0; i<sapData.length; i++) {
					if(sapData[i].bom_component != gridData[i].materialNumber || sapData[i].material_desc != gridData[i].componentName) {
						result = false;
						break;
					}
				}
			} else {
				result = false;
			}
			return result;
		};
		
		$('#performSapRequest').click(function() {
			var subNumber = $('#printSetNumber').val();
			if(subNumber == "") {
				alert("Please enter Print Set/Sub Number first");
				return;
			}
			
			if(confirm("All data in print components table will be overwritten, continue?")) {
				$.ajax({
					url: "/ebookManager/getDataFromSap.mvc",
					type: "Post",
					data: {"subNumber":subNumber, "titleId":$('#titleIdBox').val()},
					dataType: "json",
					success: function(response) {
						var data = response.materialComponents;
						var grid = $('#jsGrid');
						
						if(data.length == 0) {
							alert(response.message);
							return;
						}
						
						if(isSameData(grid, data)) {
							alert('There is no changes in Print components table');
							return;
						}
						
						clearGrid(grid);
						for(let index = 0; index < data.length;) {
							let currentData = data[index];
							grid.jsGrid("insertItem", {
								componentOrder: ++index,
								materialNumber: currentData.bom_component,
								componentName: currentData.material_desc
							});
						}
					},
					error: function(jqXHR, textStatus, errorThrown) {
						alert("Unknown error")
					}
				});
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
		isSplitTypeAuto = $("input:radio[name=splitTypeAuto]:checked").val()
		splitSize = $('#splitEBookParts').val();
		
		// Setup view
		determineOptions();
		$('#titleIdBox').val($('#titleId').val());
		showPubCutoffDateBox();
		showSelectOptions($("input:radio[name=excludeDocumentsUsed]:checked").val(), "#displayExcludeDocument");
		showSelectOptions($("input:radio[name=renameTocEntriesUsed]:checked").val(), "#displayRenameTocEntry");
		showSelectOptions($("input:radio[name=tableViewersUsed]:checked").val(), "#displayTableViewer");
		showSelectOptions($("input:radio[name=tableViewersUsed]:checked").val(), "#addTableViewerRow");
		if ($("input:radio[name=groupsEnabled]:checked").val() == "true"){
			showSelectOptions(true, "#groupId");
		}
		showSelectOptions($("input:radio[name=splitBook]:checked").val(), "#splitTypeDiv");
		if (isSplitTypeAuto == false || isSplitTypeAuto == "false") {
			showSelectOptions(true, "#ebookSizeDiv");
		}
		if (splitSize > 1 && isSplitTypeAuto == "false") {
			showSelectOptions("true", "#displaySplitDocument");
		}
		
		enabledGroup = $("input:radio[name=groupsEnabled]:checked").val()
		if(enabledGroup == false || enabledGroup == "false") {
			showSelectOptions(false, "#displayProviewGroup");
		}
		
		textboxHint("additionFrontMatterBlock");
		$('#publicationCutoffDate').datepicker({
			minDate: new Date()
		});
		
		// Set validateForm
		$('#validateForm').val(false);
	});
});