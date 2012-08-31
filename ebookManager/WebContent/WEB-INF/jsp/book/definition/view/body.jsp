<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm"%>


<script>
function submitForm(cmd)
{
	$('#command').val(cmd);
	$('#<%=ViewBookDefinitionForm.FORM_NAME%>').submit();
	return true;
};
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
	<div class="bookDefinitionView">
		<div class="section">
			<div class="sectionLabel">
				General
			</div>
			<div class="centerSection">
				<div class="leftDefinitionForm">
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
					<div class="row">
						<label class="labelCol">Include Annotations</label>
						<span class="field">${ book.includeAnnotations }</span>
					</div>
				</div>
				
				<div class="rightDefinitionForm">
					<div class="row">
						<label class="labelCol">TOC or NORT</label>
						<c:set var="tocOrNort" value="${ book.tocFlag ? 'TOC' : 'NORT'  }"/>
						<span class="field">${tocOrNort}</span>
					</div>
					<c:choose>
					<c:when test="${ book.tocFlag }">
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
						</div>
					</c:when>
					<c:otherwise>
						<div id="displayNORT">
							<div class="row">
								<label class="labelCol">NORT Domain</label>
								<span class="field">${ book.nortDomain }</span>
							</div>
							<div class="row">
								<label class="labelCol">NORT Filter View</label>
								<span class="field">${ book.nortFilterView }</span>
							</div>
						</div>
					</c:otherwise>
					</c:choose>
					<div class="row">
						<label class="labelCol">KeyCite Topline Flag</label>
						<span class="field">${ book.keyciteToplineFlag }</span>
					</div>
					<div class="row">
						<label class="labelCol">Publication Cut-off Date</label>
						<span class="field"><fmt:formatDate value="${book.publishCutoffDate}" pattern="<%= CoreConstants.DATE_FORMAT_PATTERN %>" /></span>
					</div>
					<div class="row">
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
				</div>
			</div>
		</div>
		
		<div class="dynamicContent">
			<label class="labelCol">Exclude Documents</label>
			<c:forEach items="${ book.excludeDocuments }" var="document">
				<div class="expandingBox">
					<div class="dynamicRow">
						<label>Document Guid:</label>
						<span class="field"> ${ document.documentGuid }</span>
					</div>
					<div class="dynamicRow">
						<label>Note:</label>
						<div class="wordwrap">${ document.note }</div>
					</div>
					<div class="dynamicRow">
						<label>Last Updated:</label>
						<span class="field"><fmt:formatDate value="${document.lastUpdated}" pattern="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>" /></span>
					</div>
				</div>
			</c:forEach>
		</div>
		
		<div class="dynamicContent">
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
						<div class="wordwrap">${ toc.note }</div>
					</div>
					<div class="dynamicRow">
						<label>Last Updated:</label>
						<span class="field"><fmt:formatDate value="${toc.lastUpdated}" pattern="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>" /></span>
					</div>
				</div>
			</c:forEach>
		</div>
		
		<div class="dynamicContent">
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
						<div class="wordwrap">${ copyright.note }</div>
					</div>
					<div class="dynamicRow">
						<label>Last Updated:</label>
						<span class="field"><fmt:formatDate value="${copyright.lastUpdated}" pattern="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>" /></span>
					</div>
				</div>
			</c:forEach>
		</div>
		
		<div class="dynamicContent">
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
						<div class="wordwrap">${ currency.note }</div>
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
							<c:forEach items="${book.keywordTypeValues}" var="keyword">
								<div>
									<span class="field">${keyword.keywordTypeCode.name }</span>
									<span class="field">${keyword.name }</span>
								</div>
							</c:forEach>
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
			<div class="sectionLabel">
				Front Matter
			</div>
			<div class="centerSection">
				<div class="leftDefinitionForm">
					<div class="row">
						<label class="labelCol">Front Matter TOC Label</label>
						<span class="field">${ fn:escapeXml(book.frontMatterTocLabel) }</span>
					</div>
					<c:forEach items="${book.ebookNames}" var="name">
						<div class="row">
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
							<div class="field">${name.bookNameText}</div>
						</div>
					</c:forEach>
					<div class="row">
						<label class="labelCol">Copyright</label>
						<div class="field">${ book.copyright }</div>
					</div>
					<div class="row">
						<label class="labelCol">Copyright Page Text</label>
						<div class="field">${ book.copyrightPageText }</div>
					</div>					
				</div>
				<div class="rightDefinitionForm">
					<div class="row">
						<label class="labelCol">Additional Patent/Trademark Message</label>
						<div class="field">${ book.additionalTrademarkInfo }</div>
					</div>
					<div class="row">
						<label class="labelCol">Currentness Message</label>
						<div class="field">${ book.currency }</div>
					</div>
					
					<div class="row">
						<label class="labelCol">Author Display</label>
						<c:set var="authorDisplay" value="${ book.authorDisplayVertical ? 'Vertical' : 'Horizontal'  }"/>
						<span class="field">${ authorDisplay }</span>
					</div>
					
					<div class="row">
						<label class="labelCol">Author Information</label>
						<c:forEach items="${book.authors}" var="author">
								<div class="field">${fn:escapeXml(author.fullName) }</div>
								<div class="field"> ${author.authorAddlText}</div>
								<br>
						</c:forEach>
					</div>
				</div>
			</div>
		</div>
		<div class="section">
			<div class="sectionLabel">
				Additional Front Matter
			</div>
			<div class="centerSection">
				<c:forEach items="${book.frontMatterPages}" var="page" varStatus="pageStatus">
					<div class="row frontMatterPage">
						<div class="wordwrap"><label class="labelCol">Page TOC Label</label> ${page.pageTocLabel}</div>
						<div class="wordwrap"><label class="labelCol">Page Heading Label</label> ${page.pageHeadingLabel}</div>
						<c:forEach items="${page.frontMatterSections}" var="section" varStatus="sectionStatus">
							<div class="row frontMatterSection">
								<div class="wordwrap"><label class="labelCol">Section Heading</label> ${section.sectionHeading}</div>
								<label class="labelCol">Section Text</label>
								<div class="wordwrap">${section.sectionText}</div>
								<c:forEach items="${section.pdfs}" var="pdf" varStatus="pdfStatus">
									<div class="row">
										<div class="wordwrap"><label class="labelCol">PDF Link Text</label>${pdf.pdfLinkText}</div>
										<div class="wordwrap"><label class="labelCol">PDF Filename</label>${pdf.pdfFilename}</div>
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
						<input type="submit" ${copyGenerateBook} value="Copy" onclick="submitForm('<%=ViewBookDefinitionForm.Command.COPY%>')"/>
						<input type="submit" ${copyGenerateBook} value="Generate" onclick="submitForm('<%=ViewBookDefinitionForm.Command.GENERATE%>')"/>
						<input type="submit" ${superUser} value="Delete" onclick="submitForm('<%=ViewBookDefinitionForm.Command.DELETE%>')"/>
					</c:when>
					<c:otherwise>
						<input type="submit" ${superUser} value="Restore" onclick="submitForm('<%=ViewBookDefinitionForm.Command.RESTORE%>')"/>
					</c:otherwise>
				</c:choose>
				<input type="submit" value="Audit Log" onclick="submitForm('<%=ViewBookDefinitionForm.Command.AUDIT_LOG%>')"/>
				<input type="submit" value="Publishing Stats" onclick="submitForm('<%=ViewBookDefinitionForm.Command.BOOK_PUBLISH_STATS%>')"/>
				<input type="button" value="Front Matter Preview" onclick="location.href='<%=WebConstants.MVC_FRONT_MATTER_PREVIEW%>?id=${book.ebookDefinitionId}'"/>
			</div>
		</form:form>
	</div>
</c:if>	<%-- If there is any book to display --%>
