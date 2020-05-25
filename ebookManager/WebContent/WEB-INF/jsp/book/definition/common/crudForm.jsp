<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType"%>

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
<script type="text/javascript" src="js/create-book.js"></script>
<script type="text/javascript" src="js/common-form.js"></script>
<script type="text/javascript" src="js/sourceType.js"></script>
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/additional-methods.min.js"></script>
<script type="text/javascript" src="js/sap/spinner.js"></script>
<script type="text/javascript" src="js/sap/sap-request.js"></script>
<form:hidden path="validateForm" />
<form:hidden path="selectedFrontMatterPreviewPage" />
<div class="validateFormDiv">
	<form:errors path="validateForm" cssClass="errorMessage" />
</div>
	<c:set var="xppHide" value=""/>
	<c:if test="${ book.sourceType == 'XPP' }">
		<c:set var="xppHide" value="style=\"display: none;\""/>
	</c:if>
<%-- Check if book has been published --%>
<c:choose>
	<c:when test="${!isPublished}">
		<div id="generateTitleId" class="generateTitleID">
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
						<c:forEach items="${contentTypes_uscl}" var="contentType">
							<form:option cssStyle="display: none" class="uscl_show" path="contentTypeId" value="${ contentType.id }"
										 label="${ contentType.name }" abbr="${ contentType.abbreviation }"
										 usecutoffdate="${contentType.usePublishCutoffDateFlag}"/>
						</c:forEach>
						<c:forEach items="${contentTypes_cw}" var="contentType">
							<form:option cssStyle="display: none" class="cw_show" path="contentTypeId" value="${ contentType.id }"
										 label="${ contentType.name }" abbr="${ contentType.abbreviation }"
										 usecutoffdate="${contentType.usePublishCutoffDateFlag}"/>
						</c:forEach>
					</form:select>
					<form:errors path="contentTypeId" cssClass="errorMessage" />
				</div>
			</div>
			<div id="publishDetailDiv" style="display:none">
				<div id="bookLanguageDiv">
					<form:label path="bookLanguage" class="labelCol">Language</form:label>
					<form:select path="bookLanguage">
						<c:forTokens items="en,fr" delims="," var="language">
							<form:option path="language" value="${language}" label="${language}"/>
						</c:forTokens>
					</form:select>
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
<c:set var="superUser" value="false"/>
<sec:authorize access="hasRole('ROLE_SUPERUSER')">
	<c:set var="disableOptions" value=""/>
	<c:set var="superUser" value="true"/>
</sec:authorize>

<sec:authorize access="!hasRole('ROLE_SUPERUSER')">
	<c:set var="superUser" value="false"/>
</sec:authorize>

<c:set var="disableUnderPubPlusRole" value="true"/>
<c:set var="disableUnderPubPlusRoleButton" value="disabled"/>
<sec:authorize access="hasAnyRole('ROLE_SUPERUSER,ROLE_PUBLISHER_PLUS')">
	<c:set var="disableUnderPubPlusRole" value=""/>
	<c:set var="disableUnderPubPlusRoleButton" value=""/>
</sec:authorize>

<form:hidden path="bookdefinitionId" />
<div id="generalSection" class="section">
	<div class="sectionLabel">
		General
	</div>
	<div class="centerSection">
		<div id="leftPanelFields" class="leftDefinitionForm leftTopPanel">
			<div class="row">
				<form:label path="titleId" class="labelCol">Title ID</form:label>
				<input id="titleIdBox" type="text" disabled="disabled" maxlength="40" />
				<form:hidden path="titleId"/>
				<div class="errorDiv">
					<form:errors path="titleId" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="proviewDisplayName" class="labelCol">ProView Display Name</form:label>
				<form:input path="proviewDisplayName" maxlength="1024" />
				<div class="errorDiv">
					<form:errors path="proviewDisplayName" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row">
				<form:label path="publishDateText" class="labelCol">Publish Date Text</form:label>
				<form:input path="publishDateText" maxlength="1024" />
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
			<div class="displayELooseleafs">
				<div id="publishedDateRow" class="row xppHideClass cwbHideClass">
					<form:label path="publishedDate" class="labelCol">Published Date</form:label>
					<form:input path="publishedDate"/>
					<div class="errorDiv">
						<form:errors path="publishedDate" cssClass="errorMessage" />
					</div>
				</div>
			</div>
			<c:if test="${disableUnderPubPlusRole}">
				<%-- Hidden fields needed when options are disabled.
					 Options reset to defaults if hidden fields are missing. --%>
				<form:hidden path="includeAnnotations"/>
				<form:hidden path="includeNotesOfDecisions"/>
			</c:if>
			<div class="row xppHideClass">
				<form:label path="includeAnnotations" class="labelCol">Include Annotations</form:label>
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="includeAnnotations" value="true" />Yes
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="includeAnnotations" value="false" />No
				<div class="errorDiv">
					<form:errors path="includeAnnotations" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row xppHideClass">
				<form:label path="includeNotesOfDecisions" class="labelCol">Include Notes of Decisions</form:label>
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="includeNotesOfDecisions" value="true" />Yes
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="includeNotesOfDecisions" value="false" />No
				<div class="errorDiv">
					<form:errors path="includeNotesOfDecisions" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row xppHideClass">
				<form:label path="excludeDocumentsUsed" class="labelCol">Enable Exclude Documents</form:label>
				<form:radiobutton path="excludeDocumentsUsed" value="true" />Yes
				<form:radiobutton path="excludeDocumentsUsed" value="false" />No
				<div class="errorDiv">
					<form:errors path="excludeDocumentsUsed" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row xppHideClass">
				<form:label path="renameTocEntriesUsed" class="labelCol">Enable Rename TOC Labels</form:label>
				<form:radiobutton path="renameTocEntriesUsed" value="true" />Yes
				<form:radiobutton path="renameTocEntriesUsed" value="false" />No
				<div class="errorDiv">
					<form:errors path="renameTocEntriesUsed" cssClass="errorMessage" />
				</div>
			</div>
		</div>
		<jsp:include page="../xppTable/printComponentsComparePanel.jsp" >
			<jsp:param name="printComponentsHistoryLastVersionNumber" value="${printComponentsHistoryLastVersionNumber}"/>
			<jsp:param name="bookdefinitionId" value="${editBookDefinitionForm.bookdefinitionId}"/>
			<jsp:param name="printComponentsHistoryVersions" value="${printComponentsHistoryVersions}"/>
			<jsp:param name="openCloseCompareButton" value="openCloseCompareButton"/>
			<jsp:param name="leftPanelFields" value="leftPanelFields"/>
			<jsp:param name="leftPanelPrintComponentsCompareFeature" value="leftPanelPrintComponentsCompareFeature"/>
		</jsp:include>
		<div class="rightDefinitionForm rightTopPanel">
			<c:if test="${disableUnderPubPlusRole}">
				<%-- Hidden fields needed when options are disabled.
					 Options reset to defaults if hidden fields are missing. --%>
				<form:hidden path="tocCollectionName"/>
				<form:hidden path="docCollectionName"/>
				<form:hidden path="rootTocGuid"/>
				<form:hidden path="nortDomain"/>
				<form:hidden path="printSetNumber"/>
				<form:hidden path="printSubNumber"/>
				<form:hidden path="nortFilterView"/>
				<form:hidden path="sourceType"/>
				<form:hidden path="removeEditorNoteHeading"/>
				<form:hidden path="delTagStyleEnabled"/>
				<form:hidden path="insTagStyleEnabled"/>
				<form:hidden path="splitBook"/>
				<form:hidden path="splitTypeAuto"/>
				<form:hidden path="splitEBookParts"/>
				<c:forEach items="${editBookDefinitionForm.splitDocuments}" var="document" varStatus="status">
					<form:hidden path="splitDocuments[${status.index}].tocGuid" maxlength="33" />
					<form:hidden path="splitDocuments[${status.index}].note" />
				</c:forEach>
			</c:if>
			<c:if test="${disableOptions}">
				<%-- Hidden fields needed when options are disabled.
					 Options reset to defaults if hidden fields are missing. --%>
				<form:hidden path="keyCiteToplineFlag"/>
			</c:if>
			<div class="row">
				<label class="labelCol">Source Type</label>
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="sourceType" value="<%= SourceType.TOC.toString() %>" />TOC
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="sourceType" value="<%= SourceType.NORT.toString() %>" />NORT
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="sourceType" value="<%= SourceType.FILE.toString() %>" />CWB FILE
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="sourceType" value="<%= SourceType.XPP.toString() %>" />XPP
				<div class="errorDiv">
					<form:errors path="sourceType" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row xppHideClass cwbHideClass">
				<form:label path="bucket" class="labelCol">Bucket</form:label>
				<form:select path="bucket" >
					<form:options items="${buckets}"/>
				</form:select>
				<div class="errorDiv">
					<form:errors path="bucket" cssClass="errorMessage" />
				</div>
			</div>
			<div id="displayTOC" style="display:none">
				<div class="row">
					<form:label disabled="${disableUnderPubPlusRole}" path="tocCollectionName" class="labelCol">TOC Collection</form:label>
					<form:input disabled="${disableUnderPubPlusRole}" path="tocCollectionName" maxlength="64" />
					<div class="errorDiv">
						<form:errors path="tocCollectionName" cssClass="errorMessage" />
					</div>
				</div>
				<div class="row">
					<form:label disabled="${disableUnderPubPlusRole}" path="docCollectionName" class="labelCol">DOC Collection</form:label>
					<form:input disabled="${disableUnderPubPlusRole}" path="docCollectionName" maxlength="64" />
					<div class="errorDiv">
						<form:errors path="docCollectionName" cssClass="errorMessage" />
					</div>
				</div>
				<div class="row">
					<form:label disabled="${disableUnderPubPlusRole}" path="rootTocGuid" class="labelCol">Root TOC Guid</form:label>
					<form:input disabled="${disableUnderPubPlusRole}" path="rootTocGuid" maxlength="33"/>
					<div class="errorDiv">
						<form:errors path="rootTocGuid" cssClass="errorMessage" />
					</div>
				</div>
				<div class="row">
					<form:label disabled="${disableUnderPubPlusRole}" path="printPageNumbers" class="labelCol">Print Page Numbers:</form:label>
					<form:radiobutton disabled="${disableUnderPubPlusRole}" path="printPageNumbers" value="true" />Enabled
					<form:radiobutton disabled="${disableUnderPubPlusRole}" path="printPageNumbers" value="false" />Disabled
				</div>
				<div class="row">
					<form:label disabled="${disableUnderPubPlusRole}" path="inlineTocIncluded" class="labelCol">Inline TOC:</form:label>
					<form:radiobutton disabled="${disableUnderPubPlusRole}" path="inlineTocIncluded" value="true" />Enabled
					<form:radiobutton disabled="${disableUnderPubPlusRole}" path="inlineTocIncluded" value="false" />Disabled
				</div>
                <div class="row">
                    <form:label disabled="${disableUnderPubPlusRole}" path="indexIncluded" class="labelCol">Index:</form:label>
                    <form:radiobutton disabled="${disableUnderPubPlusRole}" path="indexIncluded" value="true" />Enabled
                    <form:radiobutton disabled="${disableUnderPubPlusRole}" path="indexIncluded" value="false" />Disabled
                </div>
				<div id="indexData" style="display: none;">
					<div class="row">
						<form:label disabled="${disableUnderPubPlusRole}" path="indexTocCollectionName" class="labelCol">Index Collection:</form:label>
						<form:input disabled="${disableUnderPubPlusRole}" path="indexTocCollectionName" maxlength="64" />
						<div class="errorDiv">
							<form:errors path="indexTocCollectionName" cssClass="errorMessage" />
						</div>
					</div>
					<div class="row">
						<form:label disabled="${disableUnderPubPlusRole}" path="indexTocRootGuid" class="labelCol">Index Root Guid:</form:label>
						<form:input disabled="${disableUnderPubPlusRole}" path="indexTocRootGuid" maxlength="64" />
						<div class="errorDiv">
							<form:errors path="indexTocRootGuid" cssClass="errorMessage" />
						</div>
					</div>
				</div>
			</div>
			<div id="displayNORT" style="display:none">
				<div class="row">
					<form:label disabled="${disableUnderPubPlusRole}" path="nortDomain" class="labelCol">NORT Domain</form:label>
					<form:input disabled="${disableUnderPubPlusRole}" path="nortDomain" maxlength="64" />
					<div class="errorDiv">
						<form:errors path="nortDomain" cssClass="errorMessage" />
					</div>
				</div>
				<div class="row">
					<form:label disabled="${disableUnderPubPlusRole}" path="nortFilterView" class="labelCol">NORT Filter View</form:label>
					<form:input disabled="${disableUnderPubPlusRole}" path="nortFilterView" maxlength="64" />
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
				<div class="row">
					<form:label disabled="${disableUnderPubPlusRole}" path="inlineTocIncluded" class="labelCol">Inline TOC:</form:label>
					<form:radiobutton disabled="${disableUnderPubPlusRole}" path="inlineTocIncluded" value="true" />Enabled
					<form:radiobutton disabled="${disableUnderPubPlusRole}" path="inlineTocIncluded" value="false" />Disabled
				</div>
			</div>
			<div id="displayFILE" style="display:none">
				<div class="row">
					<label class="labelCol">Choose CWB Book</label>
					<button id="cwbFolderButton" type="button">Choose</button>&nbsp;
					<button id="cwbFolderClearButton" type="button">Clear</button>
				</div>
				<div class="row">
					<form:hidden path="codesWorkbenchBookName" />
					<label class="labelCol">Extract Name</label>
					<span id="codesWorkbenchBookNameValue">${editBookDefinitionForm.codesWorkbenchBookName}</span>
					<div class="errorDiv">
						<form:errors path="codesWorkbenchBookName" cssClass="errorMessage" />
					</div>
				</div>
				<div class="row">
					<form:label path="nortFileLocations" class="labelCol">Content Set</form:label>
					<div class="errorDiv" style="clear:both;">
						<form:errors path="nortFileLocations" cssClass="errorMessage" />
					</div>
					<div id="addNortFileLocationHere">
						<c:forEach items="${editBookDefinitionForm.nortFileLocations}" var="fileLocation" varStatus="aStatus">
							<div class="expandingBox">

								<div class="errorDiv">
									<form:errors path="nortFileLocations[${aStatus.index}].sequenceNum" cssClass="errorMessage" />
								</div>
								<form:hidden class="sequence" path="nortFileLocations[${aStatus.index}].sequenceNum" />
								<button type="button" class="moveUp">Up</button>
								<button type="button" class="moveDown">Down</button>
								<div class="dynamicRow">
									<span>${fileLocation.locationName}</span>
									<form:hidden path="nortFileLocations[${aStatus.index}].nortFileLocationId" />
									<form:hidden path="nortFileLocations[${aStatus.index}].locationName" />
									<div class="errorDiv">
										<form:errors path="nortFileLocations[${aStatus.index}].locationName" cssClass="errorMessage" />
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
				<div class="row">
					<form:label disabled="${disableUnderPubPlusRole}" path="inlineTocIncluded" class="labelCol">Inline TOC:</form:label>
					<form:radiobutton disabled="${disableUnderPubPlusRole}" path="inlineTocIncluded" value="true" />Enabled
					<form:radiobutton disabled="${disableUnderPubPlusRole}" path="inlineTocIncluded" value="false" />Disabled
				</div>
			</div>
			<div id="displayXPP" style="display:none">
				<div id="tableId" class="row">
					<div class="errorDiv">
							<form:errors path="printComponents" cssClass="errorMessage" />
					</div>
					<div id="print_component_expander" class="keywordLabel">
						<img src="theme/images/wf_minus.gif" /> Order details for XPP
					</div>
					<div id="print_component_expander_values">
						<jsp:include page="../xppTable/printComponentsTable.jsp" >
							<jsp:param name="jsGridId" value="jsGrid"/>
							<jsp:param name="edit" value="true"/>
							<jsp:param name="superUserParam" value="${superUser}"/>
							<jsp:param name="printComponents" value="${form.printComponents}"/>
							<jsp:param name="colorPrintComponentTable" value="${form.colorPrintComponentTable}"/>
							<jsp:param name="editBookDefinitionFormId" value="editBookDefinitionForm"/>
						</jsp:include>
					</div>
				</div>
				<div class="row">
					<input type="button" id="openCloseCompareButton" value="Open print component history panel" 
						${hasPrintComponentsHistory != null && hasPrintComponentsHistory ? '' : 'disabled="disabled"'}/>
				</div>
				<div class="row">
					<form:label disabled="${disableUnderPubPlusRole}" path="printSetNumber" class="labelCol">Print Set Number</form:label>
					<form:input disabled="${disableUnderPubPlusRole}" path="printSetNumber" />
					<div class="errorDiv">
						<form:errors path="printSetNumber" cssClass="errorMessage" />
					</div>
				</div>
				<div class="row">
					<form:label disabled="${disableUnderPubPlusRole}" path="printSubNumber" class="labelCol">Print Sub Number</form:label>
					<form:input disabled="${disableUnderPubPlusRole}" path="printSubNumber" />
					<div class="errorDiv">
						<form:errors path="printSubNumber" cssClass="errorMessage" />
					</div>
				</div>
				<div class="row">
					<input type="button" id="performSapRequest" value="Request data from SAP" onclick="requestDataFromSap();"/>
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
			<div id="displayPubCutoffDateOptions" class="xppHideClass">
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
			<div id="displayFinalStage" class="row xppHideClass cwbHideClass">
				<form:label path="finalStage" class="labelCol">Novus Stage</form:label>
				<form:radiobutton path="finalStage" value="true" />Final
				<form:radiobutton path="finalStage" value="false" />Review
				<div class="errorDiv">
					<form:errors path="finalStage" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row xppHideClass" >
				<form:label path="insTagStyleEnabled" class="labelCol">Added Material Blue Highlighting</form:label>
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="insTagStyleEnabled" value="true" />True
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="insTagStyleEnabled" value="false" />False
				<div class="errorDiv">
					<form:errors path="insTagStyleEnabled" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row xppHideClass" >
				<form:label path="delTagStyleEnabled" class="labelCol">Deleted Material Strike-Through</form:label>
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="delTagStyleEnabled" value="true" />True
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="delTagStyleEnabled" value="false" />False
				<div class="errorDiv">
					<form:errors path="delTagStyleEnabled" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row xppHideClass" >
				<form:label path="removeEditorNoteHeading" class="labelCol">Remove Editors' Notes Heading</form:label>
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="removeEditorNoteHeading" value="true" />True
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="removeEditorNoteHeading" value="false" />False
				<div class="errorDiv">
					<form:errors path="removeEditorNoteHeading" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row xppHideClass">
				<form:label path="splitBook" class="labelCol">Split book</form:label>
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="splitBook" value="true" />True
				<form:radiobutton disabled="${disableUnderPubPlusRole}" path="splitBook" value="false" />False
				<div class="errorDiv">
						<form:errors path="splitBook" cssClass="errorMessage" />
				</div>
			</div>
			<div id="splitTypeDiv" style="display:none">
				<div class="row">
					<form:label path="splitTypeAuto" class="labelCol">Choose Split Type</form:label>
					<form:radiobutton disabled="${disableUnderPubPlusRole}" path="splitTypeAuto" value="true" />Auto
					<form:radiobutton disabled="${disableUnderPubPlusRole}" path="splitTypeAuto" value="false" />Manual
				</div>
				<div class="errorDiv">
						<form:errors path="splitTypeAuto" cssClass="errorMessage" />
				</div>
			</div>
			<div id="ebookSizeDiv" style="display:none">
				<form:label path="splitEBookParts" class="labelCol">Number of eBook Splits</form:label>
						<form:select  disabled="${disableUnderPubPlusRole}" path="splitEBookParts">
							<form:option value="" label="SELECT" />
							<c:forEach var="i" begin="2" end="${maxEbookSplitParts}" step="1" varStatus ="status">
								<form:option label="${i}" value="${i}"/>
							</c:forEach>
						</form:select>
				<div class="errorDiv">
						<form:errors path="splitEBookParts" cssClass="errorMessage" />
				</div>
			</div>

			<div id="displaySplitDocument" style="display:none;">
				<div class="row field-note">
						Enter Toc/Nort GUID of split location(s). GUID entered indicates the beginning of the next eBook part.
				</div>

				<c:forEach items="${editBookDefinitionForm.splitDocuments}" var="toc" varStatus ="status">
						<div class="expandingBox">
							<div class="dynamicRow">
								<label>TOC/NORT GUID</label>
								<form:input disabled="${disableUnderPubPlusRole}" cssClass="guid" path="splitDocuments[${status.index}].tocGuid" maxlength="33" />
								<div class="errorDiv">
									<form:errors path="splitDocuments[${status.index}].tocGuid" cssClass="errorMessage" />
								</div>
							</div>
							<div class="dynamicRow">
								<label>Note</label>
								<form:textarea disabled="${disableUnderPubPlusRole}" path="splitDocuments[${status.index}].note" maxlength="512" />
								<div class="errorDiv">
									<form:errors  path="splitDocuments[${status.index}].note" cssClass="errorMessage" />
								</div>
						    </div>
						</div>
				</c:forEach>
				<div id="addSplitDocumentsHere"></div>
			</div>
		</div>
	</div>
</div>

<div id="displayExcludeDocument" class="dynamicContent xppHideClass" style="display:none;">
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
				<form:textarea path="excludeDocuments[${aStatus.index}].note" maxlength="512"/>
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

<div id="displayRenameTocEntry" class="dynamicContent xppHideClass" style="display:none;">
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
				<form:input path="renameTocEntries[${aStatus.index}].oldLabel" maxlength="1024" />
				<div class="errorDiv">
					<form:errors path="renameTocEntries[${aStatus.index}].oldLabel" cssClass="errorMessage" />
				</div>
			</div><div class="dynamicRow">
				<label>New Label</label>
				<form:input path="renameTocEntries[${aStatus.index}].newLabel" maxlength="1024" />
				<div class="errorDiv">
					<form:errors path="renameTocEntries[${aStatus.index}].newLabel" cssClass="errorMessage" />
				</div>
			</div><div class="dynamicRow">
				<label>Note</label>
				<form:textarea path="renameTocEntries[${aStatus.index}].note" maxlength="512" />
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

<div id="displayDocumentCopyright" class="dynamicContent xppHideClass" >
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
				<form:input path="documentCopyrights[${aStatus.index}].newText" maxlength="512" />
				<div class="errorDiv">
					<form:errors path="documentCopyrights[${aStatus.index}].newText" cssClass="errorMessage" />
				</div>
			</div>
			<div class="dynamicRow">
				<label>Note</label>
				<form:textarea path="documentCopyrights[${aStatus.index}].note" maxlength="512" />
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

<div id="displayDocumentCurrency" class="dynamicContent xppHideClass">
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
				<form:input path="documentCurrencies[${aStatus.index}].newText" maxlength="512" />
				<div class="errorDiv">
					<form:errors path="documentCurrencies[${aStatus.index}].newText" cssClass="errorMessage" />
				</div>
			</div>
			<div class="dynamicRow">
				<label>Note</label>
				<form:textarea path="documentCurrencies[${aStatus.index}].note" maxlength="512" />
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

<div class="section">
	<div class="centerSection">
		<div id="notes" class="dynamicContent leftDefinitionForm">
			<div class="row">
				<label class="labelCol">Notes</label>
				<form:textarea path="notes" rows="10" cols="54" maxlength="2048" />
			</div>
		</div>
		<div class="dynamicContent rightDefinitionForm">
			<div class="displayELooseleafs">
				<div class="row xppHideClass cwbHideClass">
					<div class="row">
						<label class="labelCol">Release notes</label>
						<form:textarea path="releaseNotes" rows="10" cols="54" maxlength="2000"/>
					</div>
					<div class="row field-note">
						*Content of this field is visible to Proview users
					</div>
					<div class="errorDiv">
						<form:errors path="releaseNotes" cssClass="errorMessage"/>
					</div>
				</div>
			</div>
		</div>
	</div>
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
						<div id="keyword_${keyword.id}" class="keywordLabel">
							<img src="theme/images/wf_plus.gif"> ${keyword.name} <form:errors path="keywords[${keyword.id}]" cssClass="errorMessage" />
						</div>
						<div id="keyword_${keyword.id}_values" class="keywordValueBox" style="display:none;">
							<c:choose>
                  	            <c:when test="${keyword.id == subjectId}">
                  	            	<div id="subjectKeywordInfoMessage" class="keyword-info-message">maximum three subjects allowed</div>
                  	            </c:when>
                  	            <c:otherwise>
                  	            	<c:choose>
		                            	<c:when test="${form.keywords.get(keyword.id) == null}" >
									    	<form:radiobutton path="keywords[${keyword.id}]" value="-1" checked="true"/>None
		                            	</c:when>
		                            	<c:otherwise>
									    	<form:radiobutton path="keywords[${keyword.id}]" value="-1"/>None
		                            	</c:otherwise>
	                            	</c:choose>
                  	            </c:otherwise>
                	        </c:choose>
							<c:forEach items="${keyword.values}" var="value">
								<div class="keywordValues">
                                    <c:choose>
                                        <c:when test="${keyword.id == subjectId}">
									         <c:choose>
									         	<c:when test="${form.keywords[keyword.id].contains(value.id)}">
									        		<form:checkbox path="keywords[${keyword.id}]" value="${value.id}" checked="true" class="subject-keyword"/>
									         	</c:when>
									         	<c:otherwise>
									        		<form:checkbox path="keywords[${keyword.id}]" value="${value.id}" class="subject-keyword"/>
									         	</c:otherwise>
									         </c:choose>
                                        </c:when>
                                        <c:otherwise>
									         <c:choose>
									         	<c:when test="${form.keywords[keyword.id].contains(value.id)}">
									        		<form:radiobutton path="keywords[${keyword.id}]" value="${value.id}" checked="true"/>
									         	</c:when>
									         	<c:otherwise>
									        		<form:radiobutton path="keywords[${keyword.id}]" value="${value.id}"/>
									         	</c:otherwise>
									         </c:choose>
                                        </c:otherwise>
                                    </c:choose>
                                    ${value.name}
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
			<div class="row field-note">
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
			<div class="row">
				<form:label path="groupsEnabled" class="labelCol">Enable Groups</form:label>
				<form:radiobutton path="groupsEnabled" value="true" />True
				<form:radiobutton path="groupsEnabled" value="false" />False
				<div class="errorDiv">
					<form:errors path="groupsEnabled" cssClass="errorMessage" />
				</div>
			</div>
			<div id=displayProviewGroup>
				<div class="row">
					<form:label path="groupName" class="labelCol">Group Name</form:label>
					<form:input path="groupName" />
				</div>
				<div class="errorDiv">
					<form:errors path="groupName" cssClass="errorMessage" />
				</div>
				<div class="row">
					<form:label path="subGroupHeading" class="labelCol">SubGroup Heading</form:label>
					<form:input path="subGroupHeading" />
				</div>
				<div class="errorDiv">
					<form:errors path="subGroupHeading" cssClass="errorMessage" />
				</div>
				<div id="pilotBook" class="row">
					<form:label path="pilotBookInfo" class="labelCol">Pilot Books</form:label>
					<input type="button" id="addPilotBook" value="add" />
					<div class="errorDiv">
						<form:errors path="pilotBookInfo" cssClass="errorMessage" />
					</div>
					<div id="addPilotBookHere">
						<c:forEach items="${editBookDefinitionForm.pilotBookInfo}" var="book" varStatus="aStatus">
							<div class="expandingBox">
								<div class="errorDiv">
									<form:errors path="pilotBookInfo[${aStatus.index}].sequenceNum" cssClass="errorMessage" />
								</div>
								<button id="pilotUp" class="moveUp" type="button">Up</button>
								<button id="pilotDown" class="moveDown" type="button">Down</button>
								<form:hidden path="pilotBookInfo[${aStatus.index}].sequenceNum" class="sequence"/>
								<div class="dynamicRow">
									<label>Title ID</label>
									<form:input path="pilotBookInfo[${aStatus.index}].pilotBookTitleId" maxlength="40" />
									<div class="errorDiv">
										<form:errors path="pilotBookInfo[${aStatus.index}].pilotBookTitleId" cssClass="errorMessage" />
									</div>
								</div>
								<div class="dynamicRow">
									<label>Note</label>
									<form:input path="pilotBookInfo[${aStatus.index}].note" maxlength="2048" />
									<div class="errorDiv">
										<form:errors path="pilotBookInfo[${aStatus.index}].note" cssClass="errorMessage" />
									</div>
								</div>
								<input type="button" value="Delete" class="rdelete" title="Delete Pilot Book" />
							</div>
						</c:forEach>
					</div>
				</div>
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
				<form:textarea path="tableViewers[${aStatus.index}].note" maxlength="512" />
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
	<div class="sectionLabel xppHideClass">
		Front Matter
	</div>
	<div class="centerSection">
		<div class="leftDefinitionForm">
			<div class="row xppHideClass" >
				<form:label path="frontMatterTocLabel" class="labelCol">Front Matter TOC Label</form:label>
				<form:input path="frontMatterTocLabel" />
				<div class="errorDiv">
					<form:errors path="frontMatterTocLabel" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row" >
				<form:label path="copyright" class="labelCol">Copyright</form:label>
				<form:textarea path="copyright" />
				<div class="errorDiv">
					<form:errors path="copyright" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row xppHideClass" >
				<form:label path="copyrightPageText" class="labelCol">Copyright Page Text</form:label>
				<form:textarea path="copyrightPageText" maxlength="2048" />
				<div class="errorDiv">
					<form:errors path="copyrightPageText" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row xppHideClass" >
				<form:label path="frontMatterTitle" class="labelCol">Main Title</form:label>
				<form:hidden path="frontMatterTitle.ebookNameId" />
				<form:hidden path="frontMatterTitle.sequenceNum" value="1" />
				<form:textarea path="frontMatterTitle.bookNameText" maxlength="2048" />
				<div class="errorDiv">
					<form:errors path="frontMatterTitle.bookNameText" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row xppHideClass" >
				<form:label path="frontMatterSubtitle" class="labelCol">Sub Title</form:label>
				<form:hidden path="frontMatterSubtitle.ebookNameId"/>
				<form:hidden path="frontMatterSubtitle.sequenceNum" value="2"/>
				<form:textarea path="frontMatterSubtitle.bookNameText" maxlength="2048" />
				<div class="errorDiv">
					<form:errors path="frontMatterSubtitle.bookNameText" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row xppHideClass" >
				<form:label path="frontMatterSeries" class="labelCol">Series</form:label>
				<form:hidden path="frontMatterSeries.ebookNameId"/>
				<form:hidden path="frontMatterSeries.sequenceNum" value="3"/>
				<form:textarea path="frontMatterSeries.bookNameText" maxlength="2048" />
				<div class="errorDiv">
					<form:errors path="frontMatterSeries.bookNameText" cssClass="errorMessage" />
				</div>
			</div>
		</div>
		<div class="rightDefinitionForm">
			<div class="row xppHideClass" >
				<form:label path="additionalTrademarkInfo" class="labelCol">Additional Patent/Trademark Message</form:label>
				<form:textarea path="additionalTrademarkInfo" maxlength="2048" />
				<div class="errorDiv">
					<form:errors path="additionalTrademarkInfo" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row xppHideClass" >
				<form:label path="currency" class="labelCol">Currentness Message</form:label>
				<form:textarea path="currency" maxlength="2048" />
				<div class="errorDiv">
					<form:errors path="currency" cssClass="errorMessage" />
				</div>
			</div>
			<div class="row xppHideClass" >
						<form:label path="fmThemeText" class="labelCol">Front Matter Theme</form:label>
						<form:select path="fmThemeText" >
							<form:options items="${frontMatterThemes}" />
						</form:select>
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
							<button id="authorUp" class="moveUp" type="button">Up</button>
							<button id="authorDown" class="moveDown" type="button">Down</button>
							<form:hidden path="authorInfo[${aStatus.index}].authorId"/>
							<form:hidden path="authorInfo[${aStatus.index}].sequenceNum" class="sequence"/>
							<div class="dynamicRow">
								<label>Prefix</label>
								<form:input path="authorInfo[${aStatus.index}].authorNamePrefix" maxlength="40" />
								<div class="errorDiv">
									<form:errors path="authorInfo[${aStatus.index}].authorNamePrefix" cssClass="errorMessage" />
								</div>
							</div>
							<div class="dynamicRow">
								<label>First Name</label>
								<form:input path="authorInfo[${aStatus.index}].authorFirstName" maxlength="1024" />
								<div class="errorDiv">
									<form:errors path="authorInfo[${aStatus.index}].authorFirstName" cssClass="errorMessage" />
								</div>
							</div>
							<div class="dynamicRow">
								<label>Middle Name</label>
								<form:input path="authorInfo[${aStatus.index}].authorMiddleName" maxlength="1024" />
								<div class="errorDiv">
									<form:errors path="authorInfo[${aStatus.index}].authorMiddleName" cssClass="errorMessage" />
								</div>
							</div>
							<div class="dynamicRow">
								<label>Last Name</label>
								<form:input path="authorInfo[${aStatus.index}].authorLastName" maxlength="1024" />
								<div class="errorDiv">
									<form:errors path="authorInfo[${aStatus.index}].authorLastName" cssClass="errorMessage" />
								</div>
							</div>
							<div class="dynamicRow">
								<label>Suffix</label>
								<form:input path="authorInfo[${aStatus.index}].authorNameSuffix" maxlength="40" />
								<div class="errorDiv">
									<form:errors path="authorInfo[${aStatus.index}].authorNameSuffix" cssClass="errorMessage" />
								</div>
							</div>
							<div class="dynamicRow">
								<label>Additional Text</label>
								<form:textarea path="authorInfo[${aStatus.index}].authorAddlText" maxlength="2048"/>
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

<div id="additionFrontMatterBlock" class="centerSection xppHideClass" >
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
				<form:input path="frontMatters[${pageStatus.index}].pageTocLabel" title="Page TOC Label" cssClass="pageTocLabel" maxlength="1024"/><form:input path="frontMatters[${pageStatus.index}].pageHeadingLabel" title="Page Heading Label" cssClass="pageHeadingLabel" maxlength="1024" /><button class="moveUp" type="button">Up</button><button class="moveDown" type="button">Down</button><input type="button" value="Delete Page" class="rdelete" title="Delete Page, Sections, and Pdfs?" deleteMessage="This will also delete all the sections and pdfs in this front matter page." /><input type="button" value="Preview" class="fmPreview"/>
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
							<form:input path="frontMatters[${pageStatus.index}].frontMatterSections[${sectionStatus.index}].sectionHeading" title="Section Heading" maxlength="1024" /><button class="moveUp" type="button">Up</button><button class="moveDown" type="button">Down</button><input type="button" value="Delete Section" class="rdelete" title="Delete Section and Pdfs?" deleteMessage="This will also delete all the pdfs in this front matter section."/>
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
        <form:textarea path="comment" maxlength="1024" />
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

<div id="codesWorkbenchFolder" title="Codes Workbench Folder" style="display:none;" >
	<div id="cwbFolderList"></div>
</div>

<input id="documentTypeAnalyticalAbbr" type="hidden" value="<%=WebConstants.DOCUMENT_TYPE_ANALYTICAL_ABBR%>" />
<input id="documentTypeCourtRulesAbbr" type="hidden" value="<%=WebConstants.DOCUMENT_TYPE_COURT_RULES_ABBR%>" />
<input id="documentTypeSliceCodesAbbr" type="hidden" value="<%=WebConstants.DOCUMENT_TYPE_SLICE_CODES_ABBR%>" />
<input id="numberOfPilotBooks" type="hidden" value="${numberOfPilotBooks}" />
<input id="numberOfAuthors" type="hidden" value="${numberOfAuthors}" />
<input id="numberOfFrontMatters" type="hidden" value="${numberOfFrontMatters}" />
<input id="numberOfExcludeDocuments" type="hidden" value="${numberOfExcludeDocuments}" />
<input id="numberOfSplitDocuments" type="hidden" value="${numberOfSplitDocuments}" />
<input id="numberOfRenameTocEntries" type="hidden" value="${numberOfRenameTocEntries}" />
<input id="numberOfTableViewers" type="hidden" value="${numberOfTableViewers}" />
<input id="numberOfDocumentCopyrights" type="hidden" value="${numberOfDocumentCopyrights}" />
<input id="numberOfDocumentCurrencies" type="hidden" value="${numberOfDocumentCurrencies}" />
