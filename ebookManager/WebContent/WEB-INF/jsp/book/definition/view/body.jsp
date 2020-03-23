<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm"%>

<script type="text/javascript" src="js/create-book.js"></script>
<script type="text/javascript" src="js/book/definition/view.js"></script>
<script>
function submitForm(cmd)
{
	$('#command').val(cmd);
	$('#<%=ViewBookDefinitionForm.FORM_NAME%>').submit();
	return true;
};
$(document).ready(function() {
	if(${ book.sourceType == 'XPP' } == true) {
		$(".xppHideClass").css("display","none");
	} else {
		$(".xppHideClass").css("display","block");
	}
});
</script>

<%-- Check if there is a book model to render, if not don't display a bunch of unvalued labels. --%>
<c:if test="${book != null}">
	<%-- Informational Messages area --%>
    <c:if test="${infoMessage != null}">
    <div class="infoMessageWarning">
    	${infoMessage}
    </div>
    </c:if>
	<c:if test="${isInJobRequest}">
		<div style="color:red;">Note: This book definition is already in the job run queue, and thus cannot be edited.</div>
	</c:if>
	<c:set var="xppHide" value=""/>
	<c:set var="xppDisablePreviewButton" value=""/>
	<c:if test="${ book.sourceType == 'XPP' }">
		<c:set var="xppHide" value="style=\"display: none;\""/>
		<c:set var="xppDisablePreviewButton" value="disabled"/>
	</c:if>
	<div class="bookDefinitionView">
		<div class="section">
			<div class="sectionLabel">
				General
			</div>
			<div class="centerSection">
				<div id="leftPanelFields" class="leftDefinitionForm leftTopPanel">
					<div class="row">
						<label class="labelCol">Title ID</label>
						<span class="field">${ book.fullyQualifiedTitleId }</span>
					</div>
					<div class="row">
						<label class="labelCol">ProView Display Name</label>
						<span class="field">${ fn:escapeXml(book.proviewDisplayName) }</span>
					</div>
					<div class="row">
						<label class="labelCol">Publish Date Text</label>
						<span class="field">${ fn:escapeXml(book.publishDateText) }</span>
					</div>
					<div class="row">
						<label class="labelCol">ISBN</label>
						<span class="field">${ book.isbn }</span>
					</div>
				
					<div class="row">
						<label class="labelCol">Sub Material Number</label>
						<span class="field">${ book.materialId }</span>
					</div>
					<div class="row xppHideClass">
						<label class="labelCol">Include Annotations</label>
						<span class="field">${ book.includeAnnotations }</span>
					</div>
					<div class="row xppHideClass">
						<label class="labelCol">Include Notes of Decisions</label>
						<span class="field">${ book.includeNotesOfDecisions }</span>
					</div>
					<c:if test="${not empty book.notes}">
						<div class="row">
							<div class="notes-view" onclick="toggleNotes()">
								<label class="labelCol notes-label">Notes</label>
								<img src="theme/images/pencil.png" class="notes-icon" />
							</div>
							<blockquote class="notes-quote">${ book.notes }</blockquote>
						</div>
					</c:if>
				</div>
				<jsp:include page="../xppTable/printComponentsComparePanel.jsp" >
					<jsp:param name="printComponentsHistoryLastVersionNumber" value="${printComponentsHistoryLastVersionNumber}"/>
					<jsp:param name="bookdefinitionId" value="${book.ebookDefinitionId}"/>
					<jsp:param name="printComponentsHistoryVersions" value="${printComponentsHistoryVersions}"/>
					<jsp:param name="openCloseCompareButton" value="openCloseCompareButton"/>
					<jsp:param name="leftPanelFields" value="leftPanelFields"/>
					<jsp:param name="leftPanelPrintComponentsCompareFeature" value="leftPanelPrintComponentsCompareFeature"/>
				</jsp:include>
				<div class="rightDefinitionForm rightTopPanel">
					<div class="row">
						<label class="labelCol">Source Type</label>
						<span class="field">
							<c:choose>
							<c:when test="${ book.sourceType == 'TOC' }">
							TOC
							</c:when>
							<c:when test="${ book.sourceType == 'NORT' }">
							NORT
							</c:when>
							<c:when test="${ book.sourceType == 'FILE' }">
							CWB File
							</c:when>
							<c:when test="${ book.sourceType == 'XPP' }">
							XPP
							</c:when>
							<c:otherwise>
							Unknown
							</c:otherwise>
							</c:choose>
						</span>
					</div>
					<c:choose>
					<c:when test="${ book.sourceType == 'TOC' }">
						<div id="displayTOC">
							<div class="row">
								<label class="labelCol">TOC Collection</label>
								<span class="field">${ book.tocCollectionName }</span>
							</div>
							<div class="row">
								<label class="labelCol">DOC Collection</label>
								<span class="field">${ book.docCollectionName }</span>
							</div>
							<div class="row">
								<label class="labelCol">Root TOC Guid</label>
								<span class="field">${ book.rootTocGuid }</span>
							</div>
                            <div class="row">
                                <label class="labelCol">Print Page Numbers</label>
                                <span class="field">${ book.printPageNumbers }</span>
                            </div>
                            <div class="row">
                                <label class="labelCol">Inline TOC Included</label>
                                <span class="field">${ book.inlineTocIncluded }</span>
                            </div>
                            <div class="row">
                                <label class="labelCol">Index Included</label>
                                <span class="field">${ book.indexIncluded }</span>
                            </div>
							<c:if test="${book.indexIncluded=='true'}">
								<div class="row">
									<label class="labelCol">Index Collection</label>
									<span class="field">${ book.indexTocCollectionName }</span>
								</div>
								<div class="row">
									<label class="labelCol">Index Root Guid</label>
									<span class="field">${ book.indexTocRootGuid }</span>
								</div>
							</c:if>
						</div>
					</c:when>
					<c:when test="${ book.sourceType == 'NORT' }">
						<div id="displayNORT">
							<div class="row">
								<label class="labelCol">NORT Domain</label>
								<span class="field">${ book.nortDomain }</span>
							</div>
							<div class="row">
								<label class="labelCol">NORT Filter View</label>
								<span class="field">${ book.nortFilterView }</span>
							</div>
							<div class="row">
								<label class="labelCol">Use Reload Content</label>
								<span class="field">${ book.useReloadContent }</span>
							</div>
						</div>
					</c:when>
					<c:when test="${ book.sourceType == 'XPP' }">
						<div id="displayXPP">
							<div id="tableId" class="row">
								<div id="print_component_expander" class="keywordLabel">
									<img src="theme/images/wf_minus.gif" /> Order details for XPP
								</div>
								<div id="print_component_expander_values" class="keywordValueBox">
									<jsp:include page="../xppTable/printComponentsTable.jsp" >
									    <jsp:param name="jsGridId" value="jsGrid"/>
										<jsp:param name="edit" value="false"/>
										<jsp:param name="printComponents" value="${form.printComponents}"/>
										<jsp:param name="colorPrintComponentTable" value="${form.colorPrintComponentTable}"/>
									</jsp:include>
									<div class="print_component_legend_box">
										<div class="print_component_legend bundle_not_found" style="display:none">
											<div class="legend_color"></div>
											<span class="legend_description">- Bundle not found</span>
										</div>
										<div class="print_component_legend supplement" style="display:none">
											<div class="legend_color"></div>
											<span class="legend_description">- Supplement (Order will be ignored, appears in the list for completeness)</span>
										</div>
									</div>
								</div>
							</div>
							
							<div class="row">
								<input type="button" id="openCloseCompareButton" value="Open print component history panel"
									${hasPrintComponentsHistory != null && hasPrintComponentsHistory ? '' : 'disabled="disabled"'}/>
							</div>
							<div class="row">
								<label class="labelCol">Print Set Number</label>
								<span class="field">${ book.printSetNumber }</span>
							</div>
							<div class="row">
								<label class="labelCol">Print Sub Number</label>
								<span class="field">${ book.printSubNumber }</span>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<div id="displayFile">
							<div class="row">
								<label class="labelCol">Extract Name</label>
								<span class="field">${ book.cwbBookName }</span>
							</div>
							<div class="row">
								<div class="dynamicContent">
									<label class="labelCol">Content Set</label>
									<c:forEach items="${ book.nortFileLocations }" var="fileLocation">
										<div class="expandingBox">
											<div class="dynamicRow">
												<span class="field"> ${ fileLocation.locationName }</span>
											</div>
										</div>
									</c:forEach>
								</div>
							</div>
						</div>
					</c:otherwise>
					</c:choose>
					<div class="row">
						<label class="labelCol">eLooseleafs Enabled</label>
						<span class="field">${ book.ELooseleafsEnabled }</span>
					</div>
					<div class="row">
						<label class="labelCol">KeyCite Topline Flag</label>
						<span class="field">${ book.keyciteToplineFlag }</span>
					</div>
					<div class="row" ${xppHide}>
						<label class="labelCol">Publication Cut-off Date</label>
						<span class="field"><fmt:formatDate value="${book.publishCutoffDate}" pattern="<%= CoreConstants.DATE_FORMAT_PATTERN %>" /></span>
					</div>
					<div class="row" ${xppHide}>
						<label class="labelCol">Novus Stage</label>
						<span class="field">
							<c:choose>
								<c:when test="${ book.finalStage == true }">
									Final Stage
								</c:when>
								<c:otherwise>
									Review Stage
								</c:otherwise>
							</c:choose>
						</span>
					</div>
					<div class="row" ${xppHide}>
						<label class="labelCol" >Added Material Blue Highlighting</label>
						<span class="field">${ book.insStyleFlag }</span>
					</div>
					<div class="row" ${xppHide}>
						<label class="labelCol">Deleted Material Strike-Through</label>
						<span class="field">${ book.delStyleFlag }</span>
					</div>
					<div class="row" ${xppHide}>
						<label class="labelCol">Remove Editors' Notes Heading</label>
						<span class="field">${ book.removeEditorNoteHeadFlag }</span>
					</div>
                    <div class="row" ${xppHide}>
                        <label class="labelCol">Split book</label>
                        <span class="field">${ book.splitBook }</span>
                    </div>
					<c:choose>
						<c:when test="${ book.splitBook == true }">						
							<div class="row" ${xppHide}>
								<label class="labelCol">Split Type</label>
								<span class="field">
									<c:choose>
										<c:when test="${ book.splitTypeAuto == true }">
											Auto											
										</c:when>
										<c:otherwise>
											Manual
											<div class="row">
												<label class="labelCol">Number of eBook Splits</label>
												<span class="field">${ book.splitEBookParts }</span>
											</div>
											<div class="row">
												
													<label class="labelCol">Split Documents</label>
													<c:forEach items="${ book.splitDocuments }" var="toc">
														<div class="expandingBox">
															<div class="dynamicRow">
																<label>TOC/NORT GUID:</label>
																<span class="field"> ${ toc.tocGuid }</span>
															</div>
															<div class="dynamicRow">
																<label>Note:</label>
																<span class="wordwrap">${ fn:escapeXml(toc.note) }</span>
															</div>
														</div>
													</c:forEach>
												
											</div>
										</c:otherwise>
									</c:choose>
								</span>
						    </div>
						</c:when>
					</c:choose>
				</div>
			</div>
		</div>
		
		<div class="dynamicContent xppHideClass">
			<label class="labelCol">Exclude Documents</label>
			<c:forEach items="${ book.excludeDocuments }" var="document">
				<div class="expandingBox">
					<div class="dynamicRow">
						<label>Document Guid:</label>
						<span class="field"> ${ document.documentGuid }</span>
					</div>
					<div class="dynamicRow">
						<label>Note:</label>
						<div class="wordwrap">${ fn:escapeXml(document.note) }</div>
					</div>
					<div class="dynamicRow">
						<label>Last Updated:</label>
						<span class="field"><fmt:formatDate value="${document.lastUpdated}" pattern="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>" /></span>
					</div>
				</div>
			</c:forEach>
		</div>
		
		<div class="dynamicContent xppHideClass">
			<label class="labelCol">Rename TOC Labels</label>
			<c:forEach items="${ book.renameTocEntries }" var="toc">
				<div class="expandingBox">
					<div class="dynamicRow">
						<label>Guid:</label>
						<span class="field"> ${ toc.tocGuid }</span>
					</div>
					<div class="dynamicRow">
						<label>Old Label:</label>
						<div class="wordwrap">${ fn:escapeXml(toc.oldLabel) }</div>
					</div>
					<div class="dynamicRow">
						<label>New Label:</label>
						<div class="wordwrap">${ fn:escapeXml(toc.newLabel) }</div>
					</div>
					<div class="dynamicRow">
						<label>Note:</label>
						<div class="wordwrap">${ fn:escapeXml(toc.note) }</div>
					</div>
					<div class="dynamicRow">
						<label>Last Updated:</label>
						<span class="field"><fmt:formatDate value="${toc.lastUpdated}" pattern="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>" /></span>
					</div>
				</div>
			</c:forEach>
		</div>
		
		<div class="dynamicContent xppHideClass">
			<label class="labelCol">Document Copyright</label>
			<c:forEach items="${ book.documentCopyrights }" var="copyright">
				<div class="expandingBox">
					<div class="dynamicRow">
						<label>Copyright Guid:</label>
						<span class="field"> ${ copyright.copyrightGuid }</span>
					</div>
					<div class="dynamicRow">
						<label>New Text:</label>
						<div class="wordwrap">${ fn:escapeXml(copyright.newText) }</div>
					</div>
					<div class="dynamicRow">
						<label>Note:</label>
						<div class="wordwrap">${ fn:escapeXml(copyright.note) }</div>
					</div>
					<div class="dynamicRow">
						<label>Last Updated:</label>
						<span class="field"><fmt:formatDate value="${copyright.lastUpdated}" pattern="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>" /></span>
					</div>
				</div>
			</c:forEach>
		</div>
		
		<div class="dynamicContent xppHideClass">
			<label class="labelCol">Document Currency</label>
			<c:forEach items="${ book.documentCurrencies }" var="currency">
				<div class="expandingBox">
					<div class="dynamicRow">
						<label>Currency Guid:</label>
						<span class="field"> ${ currency.currencyGuid }</span>
					</div>
					<div class="dynamicRow">
						<label>New Text:</label>
						<div class="wordwrap">${ fn:escapeXml(currency.newText) }</div>
					</div>
					<div class="dynamicRow">
						<label>Note:</label>
						<div class="wordwrap">${ fn:escapeXml(currency.note) }</div>
					</div>
					<div class="dynamicRow">
						<label>Last Updated:</label>
						<span class="field"><fmt:formatDate value="${currency.lastUpdated}" pattern="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>" /></span>
					</div>
				</div>
			</c:forEach>
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
							<table>
							<c:forEach items="${keywords}" var="keywordEntry">
								<tr>
									<td class="field bold">${keywordEntry.key}</td>
									<td>
                                        <c:forEach items="${keywordEntry.value}" var="keyword">
                                            <span class="field">${keyword}</span><br/>
                                        </c:forEach>
							        </td>
								</tr>
							</c:forEach>
                            </table>
						</div>
					</div>
				</div>
				<div class="rightDefinitionForm">
					<div class="row">
						<label class="labelCol">Auto-update Support</label>
						<span class="field">${ book.autoUpdateSupportFlag }</span>
					</div>
					<div class="row">
						<label class="labelCol">Search Index</label>
						<span class="field">${ book.searchIndexFlag }</span>
					</div>
					<div class="row">
						<label class="labelCol">Enable Copy Feature</label>
						<span class="field">${ book.enableCopyFeatureFlag }</span>
					</div>
					<div class="row">
						<label class="labelCol">Pilot Book: Notes Migration</label>
						<span class="field">${ fn:toLowerCase(book.pilotBookStatus) }</span>
					</div>
					<c:if test="${not empty book.groupName}">
						<div class="row">
							<label class="labelCol">Group Name</label>
							<span class="field">${ book.groupName }</span>
						</div>
						<div class="row">
							<label class="labelCol">SubGroup Heading</label>
							<span class="field">${ book.subGroupHeading }</span>
						</div>
						<div class="row">
						<label class="labelCol">Pilot Books</label>
						<c:forEach items="${book.pilotBooks}" var="pilotBook">
								<div class="field">${fn:escapeXml(pilotBook.pilotBookTitleId) }</div>
								<div class="field wordwrap"> ${fn:escapeXml(pilotBook.note)}</div>
								<br>
						</c:forEach>
					</div>
					</c:if>
					
				</div>
			</div>
		</div>
		
		<div class="dynamicContent">
			<label class="labelCol">Table Viewer</label>
			<c:forEach items="${ book.tableViewers }" var="document">
				<div class="expandingBox">
					<div class="dynamicRow">
						<label>Document Guid:</label>
						<span class="field"> ${ document.documentGuid }</span>
					</div>
					<div class="dynamicRow">
						<label>Note:</label>
						<div class="wordwrap">${ fn:escapeXml(document.note) }</div>
					</div>
					<div class="dynamicRow">
						<label>Last Updated:</label>
						<span class="field"><fmt:formatDate value="${document.lastUpdated}" pattern="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>" /></span>
					</div>
				</div>
			</c:forEach>
		</div>
		
		<div class="section">
			<div class="sectionLabel" ${xppHide}>
				Front Matter
			</div>
			<div class="centerSection">
				<div class="leftDefinitionForm">
					<div class="row" ${xppHide}>
						<label class="labelCol">Front Matter TOC Label</label>
						<span class="field">${ fn:escapeXml(book.frontMatterTocLabel) }</span>
					</div>
					<c:forEach items="${book.ebookNames}" var="name">
						<div class="row" ${xppHide}>
							<c:if test="${ name.sequenceNum == 1 }">
								<c:set var="frontMatterNameLabel" value="Main Title" />
							</c:if>
							<c:if test="${ name.sequenceNum == 2 }">
								<c:set var="frontMatterNameLabel" value="Sub Title" />
							</c:if>
							<c:if test="${ name.sequenceNum == 3 }">
								<c:set var="frontMatterNameLabel" value="Series" />
							</c:if>
							<label class="labelCol">${frontMatterNameLabel}</label>
							<div class="field wordwrap">${fn:escapeXml(name.bookNameText)}</div>
						</div>
					</c:forEach>
					<div class="row">
						<label class="labelCol">Copyright</label>
						<div class="field wordwrap">${ fn:escapeXml(book.copyright) }</div>
					</div>
					<div class="row" ${xppHide}>
						<label class="labelCol">Copyright Page Text</label>
						<div class="field wordwrap">${ fn:escapeXml(book.copyrightPageText) }</div>
					</div>					
				</div>
				<div class="rightDefinitionForm">
					<div class="row" ${xppHide}>
						<label class="labelCol">Additional Patent/Trademark Message</label>
						<div class="field wordwrap">${ fn:escapeXml(book.additionalTrademarkInfo) }</div>
					</div>
					<div class="row" ${xppHide}>
						<label class="labelCol">Currentness Message</label>
						<div class="field wordwrap">${ fn:escapeXml(book.currency) }</div>
					</div>
					<div class="row" ${xppHide}>
						<label class="labelCol">Front Matter Theme</label>
						<div class="field wordwrap">${ fn:escapeXml(book.frontMatterTheme) }</div>
					</div>
					
					<div class="row" ${xppHide}>
						<label class="labelCol">Author Display</label>
						<c:set var="authorDisplay" value="${ book.authorDisplayVertical ? 'Vertical' : 'Horizontal'  }"/>
						<span class="field wordwrap">${ authorDisplay }</span>
					</div>
					
					<div class="row">
						<label class="labelCol">Author Information</label>
						<c:forEach items="${book.authors}" var="author">
								<div class="field">${fn:escapeXml(author.fullName) }</div>
								<div class="field wordwrap"> ${fn:escapeXml(author.authorAddlText)}</div>
								<br>
						</c:forEach>
					</div>
				</div>
			</div>
		</div>
		<div class="section" ${xppHide}>
			<div class="sectionLabel">
				Additional Front Matter
			</div>
			<div class="centerSection">
				<c:forEach items="${book.frontMatterPages}" var="page" varStatus="pageStatus">
					<div class="row frontMatterPage">
						<div class="wordwrap"><label class="labelCol">Page TOC Label</label> ${fn:escapeXml(page.pageTocLabel)}</div>
						<div class="wordwrap"><label class="labelCol">Page Heading Label</label> ${fn:escapeXml(page.pageHeadingLabel)}</div>
						<c:forEach items="${page.frontMatterSections}" var="section" varStatus="sectionStatus">
							<div class="row frontMatterSection">
								<div class="wordwrap"><label class="labelCol">Section Heading</label> ${fn:escapeXml(section.sectionHeading)}</div>
								<label class="labelCol">Section Text</label>
								<div class="wordwrap">${fn:escapeXml(section.sectionText)}</div>
								<c:forEach items="${section.pdfs}" var="pdf" varStatus="pdfStatus">
									<div class="row">
										<div class="wordwrap"><label class="labelCol">PDF Link Text</label>${fn:escapeXml(pdf.pdfLinkText)}</div>
										<div class="wordwrap"><label class="labelCol">PDF Filename</label>${fn:escapeXml(pdf.pdfFilename)}</div>
									</div>
								</c:forEach>
							</div>
						</c:forEach>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
	<%-- Setup different roles --%>
	<c:set var="editBook" value="disabled"/>
	<sec:authorize access="hasAnyRole('ROLE_EDITOR,ROLE_PUBLISHER,ROLE_PUBLISHER_PLUS,ROLE_SUPERUSER')">
		<c:set var="editBook" value=""/>
	</sec:authorize>
	<c:set var="copyGenerateBook" value="disabled"/>
	<sec:authorize access="hasAnyRole('ROLE_PUBLISHER,ROLE_PUBLISHER_PLUS,ROLE_SUPERUSER')">
		<c:set var="copyGenerateBook" value=""/>
	</sec:authorize>
	<c:set var="superUser" value="disabled"/>
	<sec:authorize access="hasRole('ROLE_SUPERUSER')">
		<c:set var="superUser" value=""/>
	</sec:authorize>
	<c:set var="editGroup" value="disabled"/>
	<sec:authorize access="hasAnyRole('ROLE_SUPERUSER,ROLE_PUBLISHER_PLUS')">
		<c:set var="editGroup" value=""/>
	</sec:authorize>
	<c:set var="generateButtonDisable" value=""/>
	<c:if test="${form.generateButtonDisabled}">
		<c:set var="generateButtonDisable" value="disabled"/>
	</c:if>
	<div style="font-family:Arial">
		<%-- Action buttons for the displayed book definition --%>
		<br/>
		<form:form name="theForm" commandName="<%=ViewBookDefinitionForm.FORM_NAME%>" action="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_POST%>">
			<form:hidden path="command"/>
			<form:hidden path="<%=WebConstants.KEY_ID%>"/>
			
			<div class="buttons">
				<c:choose>
					<c:when test="${not book.deletedFlag }">
						<c:if test="${!isInJobRequest}">
							<input id="editBookDefinition" type="submit" ${editBook} value="Edit" onclick="submitForm('<%=ViewBookDefinitionForm.Command.EDIT%>')"/>
						</c:if>
						<input id="copy" type="submit" ${copyGenerateBook} value="Copy" onclick="submitForm('<%=ViewBookDefinitionForm.Command.COPY%>')"/>
						<input id="generate" type="submit" ${generateButtonDisable} ${copyGenerateBook} value="Generate" onclick="submitForm('<%=ViewBookDefinitionForm.Command.GENERATE%>')"/>
						<input id="createEdit" type="submit" ${editGroup} value="Create/Edit Group" onclick="submitForm('<%=ViewBookDefinitionForm.Command.GROUP%>')"/>
						<input id="delete" type="submit" ${superUser} value="Delete" onclick="submitForm('<%=ViewBookDefinitionForm.Command.DELETE%>')"/>
					</c:when>
					<c:otherwise>
						<input id="restore" type="submit" ${superUser} value="Restore" onclick="submitForm('<%=ViewBookDefinitionForm.Command.RESTORE%>')"/>
					</c:otherwise>
				</c:choose>
				<input id="auditLog" type="submit" value="Audit Log" onclick="submitForm('<%=ViewBookDefinitionForm.Command.AUDIT_LOG%>')"/>
				<input id="publishingStats" type="submit" value="Publishing Stats" onclick="submitForm('<%=ViewBookDefinitionForm.Command.BOOK_PUBLISH_STATS%>')"/>
				<input id="frontMatterPreview" type="button" ${xppDisablePreviewButton} value="Front Matter Preview" onclick="location.href='<%=WebConstants.MVC_FRONT_MATTER_PREVIEW%>?id=${book.ebookDefinitionId}'"/>
			</div>
		</form:form>
	</div>
</c:if>	<%-- If there is any book to display --%>
