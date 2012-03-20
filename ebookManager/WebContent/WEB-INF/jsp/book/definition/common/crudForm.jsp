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
		var nameIndex = ${numberOfNameLines};
		var frontMatterIndex = ${numberOfFrontMatters};
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
			appendTxt = appendTxt + "<div>";
			appendTxt = appendTxt + "<input class=\"additionalText\" id=\"authorInfo" + authorIndex + ".authorAddlText\" name=\"authorInfo[" + authorIndex + "].authorAddlText\" type=\"text\" title=\"Additional Text\"/>";
			appendTxt = appendTxt + "<input type=\"button\" value=\"Delete\" class=\"rdelete\" />";
			appendTxt = appendTxt + "</div>";
			appendTxt = appendTxt + "</div>";
			$("#addHere").before(appendTxt);
			authorIndex = authorIndex + 1;
			
			textboxHint("authorName");
		};
		
		// Add another name row
		var addNameRow = function() {
			var appendTxt = "<div class='row'>";
			appendTxt = appendTxt + "<input id=\"nameLines" + nameIndex + ".ebookNameId\" name=\"nameLines[" + nameIndex + "].ebookNameId\" type=\"hidden\" />";
			appendTxt = appendTxt + "<input class=\"bookName\" id=\"nameLines" + nameIndex + ".bookNameText\" name=\"nameLines[" + nameIndex + "].bookNameText\" type=\"text\" title=\"Name Line\"/>";
			appendTxt = appendTxt + "<input class=\"sequenceNumber\" id=\"nameLines" + nameIndex + ".sequenceNum\" name=\"nameLines[" + nameIndex + "].sequenceNum\" type=\"text\" title=\"Seq Num.\"/>";
			appendTxt = appendTxt + "<span>";
			appendTxt = appendTxt + "<input type=\"button\" value=\"Delete\" class=\"rdelete\" />";
			appendTxt = appendTxt + "</span>";
			appendTxt = appendTxt + "</div>";
			$("#addNameHere").before(appendTxt);
			nameIndex = nameIndex + 1;
			
			textboxHint("nameLine");
		};
		
		// Add another front matter text row
		var addFrontMatterRow = function() {
			var appendTxt = "<div class='row'>";
			appendTxt = appendTxt + "<input id=\"additionalFrontMatter" + frontMatterIndex + ".frontMatterId\" name=\"additionalFrontMatter[" + frontMatterIndex + "].frontMatterId\" type=\"hidden\" />";
			appendTxt = appendTxt + "<textarea class=\"additionalFrontMatterText\" id=\"additionalFrontMatter" + frontMatterIndex + ".additionalFrontMatterText\" name=\"additionalFrontMatter[" + frontMatterIndex + "].additionalFrontMatterText\" title=\"Additional Front Matter Text\"/>";
			appendTxt = appendTxt + "<input class=\"sequenceNumber\" id=\"additionalFrontMatter" + frontMatterIndex + ".sequenceNum\" name=\"additionalFrontMatter[" + frontMatterIndex + "].sequenceNum\" type=\"text\" title=\"Seq Num.\"/>";
			appendTxt = appendTxt + "<span>";
			appendTxt = appendTxt + "<input type=\"button\" value=\"Delete\" class=\"rdelete\" />";
			appendTxt = appendTxt + "</span>";
			appendTxt = appendTxt + "</div>";
			$("#addFrontMatterHere").before(appendTxt);
			frontMatterIndex = frontMatterIndex + 1;
			
			textboxHint("additionalFrontMatter");
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
			<%-- Style buttons with jquery  --%>
			$( ".buttons input:submit,.buttons a,.buttons button,.modalButtons button,.modalButtons a" ).button();
			
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
		        $('#mask').fadeIn(500);    
		        $('#mask').fadeTo("fast",0.5);  
		     
		        //Get the window height and width
		        var winH = $(window).height();
		        var winW = $(window).width();
		               
		        //Set the popup window to center
		        $('#dialog').css('top',  winH/2-$('#dialog').height()/2);
		        $('#dialog').css('left', winW/2-$('#dialog').width()/2);
		     
		        //transition effect
		        $('#dialog').fadeIn(1000); 
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
				fillSpace: true,
			});
			
			$(".rdelete").live("click", function () {
				var srow = $(this).parent().parent();
				srow.remove();
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
			textboxHint("additionalFrontMatter");
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
		<form:label path="proviewDisplayName" class="labelCol">Proview Display Name</form:label>
		<form:input path="proviewDisplayName" />
		<div class="errorDiv">
			<form:errors path="proviewDisplayName" cssClass="errorMessage" />
		</div>
	</div>
	<div id="nameLine" class="row">
		<form:label path="nameLines" class="labelCol">Name Line</form:label>
		<input type="button" onclick="addNameRow();" id="addNameLine" value="add" />
		<div class="errorDiv">
			<form:errors path="nameLines" cssClass="errorMessage" />
		</div>
		<c:forEach items="${editBookDefinitionForm.nameLines}" var="name" varStatus="aStatus">
			<div class="row">
				<form:hidden path="nameLines[${aStatus.index}].ebookNameId"/>
				<form:input path="nameLines[${aStatus.index}].bookNameText" title="Name Line" class="bookName"  />
				<form:input path="nameLines[${aStatus.index}].sequenceNum" title="Seq Num." class="sequenceNumber"  />
				<div class="errorDiv">
					<form:errors path="nameLines[${aStatus.index}]" cssClass="errorMessage" />
				</div>
				<span>
					<input type="button" value="Delete" class="rdelete" />
				</span>
			</div>
		</c:forEach>
		<div id="addNameHere"></div>
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
		<form:input path="materialId" maxlength="18" />
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
		<label class="labelCol">Keywords</label>
		<div id="accordion">
			<c:forEach items="${typeKeywords}" var="keyword">
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
	<div id="authorName" class="row">
		<form:label path="authorInfo" class="labelCol">Author Information</form:label>
		<input type="button" onclick="addAuthorRow();" id="addAuthor" value="add" />
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
				<div>
					<form:input path="authorInfo[${aStatus.index}].authorAddlText"  title="Additional Text" class="additionalText" />
					<input type="button" value="Delete" class="rdelete" />
				</div>
			</div>
		</c:forEach>
		<div id="addHere"></div>
	</div>
	<div id="additionalFrontMatter" class="row">
		<form:label path="additionalFrontMatter" class="labelCol">Additional Front Matter</form:label>
		<input type="button" onclick="addFrontMatterRow();" id="addFrontMatterLine" value="add" />
		<div class="errorDiv">
			<form:errors path="additionalFrontMatter" cssClass="errorMessage" />
		</div>
		<c:forEach items="${editBookDefinitionForm.additionalFrontMatter}" var="frontMatter" varStatus="aStatus">
			<div class="row">
				<form:hidden path="additionalFrontMatter[${aStatus.index}].frontMatterId"/>
				<form:textarea path="additionalFrontMatter[${aStatus.index}].additionalFrontMatterText" title="Additional Front Matter Text" class="additionalFrontMatterText"  />
				<form:input path="additionalFrontMatter[${aStatus.index}].sequenceNum" title="Seq Num." class="sequenceNum"  />
				<div class="errorDiv">
					<form:errors path="additionalFrontMatter[${aStatus.index}]" cssClass="errorMessage" />
				</div>
				<span>
					<input type="button" value="Delete" class="rdelete" />
				</span>
			</div>
		</c:forEach>
		<div id="addFrontMatterHere"></div>
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
		<form:label path="currency" class="labelCol">Currentness Message</form:label>
		<form:input path="currency" />
		<div class="errorDiv">
			<form:errors path="currency" cssClass="errorMessage" />
		</div>
	</div>
	<div class="row">
		<form:label path="isProviewTableView" class="labelCol">Use Proview Table View</form:label>
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
		<form:label path="keyCiteToplineFlag" class="labelCol">KeyCite Topline Flag</form:label>
		<form:radiobutton path="keyCiteToplineFlag" value="true" />True
		<form:radiobutton path="keyCiteToplineFlag" value="false" />False
		<div class="errorDiv">
			<form:errors path="keyCiteToplineFlag" cssClass="errorMessage" />
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
		<form:label path="isComplete" class="labelCol">Book Definition Status</form:label>
		<form:radiobutton path="isComplete" value="true" />Complete
		<form:radiobutton path="isComplete" value="false" />Incomplete
		<div class="errorDiv">
			<form:errors path="isComplete" cssClass="errorMessage" />
		</div>
	</div>
</div>

<div id="modal"> 
    <div id="dialog" class="window">
        <div class="modelTitle">Comments</div>
        <form:textarea path="comment"/>
        <div class="modalButtons">
        	<form:button>Save</form:button>
        	<a href="#" class="cancel">Cancel</a>
        </div>
    </div>
    <div id="mask"></div>
</div>

