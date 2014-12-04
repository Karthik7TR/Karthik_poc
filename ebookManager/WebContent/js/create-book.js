$(function() {
	$(document).ready(function() {
		// Declare Global Variables
		var authorIndex = $("#numberOfAuthors").val();
		var frontMatterPageIndex = $("#numberOfFrontMatters").val();
		var excludeDocumentIndex = $("#numberOfExcludeDocuments").val();
		var renameTocEntryIndex = $("#numberOfRenameTocEntries").val();
		var tableViewerIndex = $("#numberOfTableViewers").val();
		var documentCopyrightIndex = $("#numberOfDocumentCopyrights").val();
		var documentCurrencyIndex = $("#numberOfDocumentCurrencies").val();
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
	
		// If there is front matter preview content to display, then display it in its own window 
		openFrontMatterPreviewWindow();
		
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
});