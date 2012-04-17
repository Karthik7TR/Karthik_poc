<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<%-- Check if there is a bookAuditDetail model to render, if not don't display a bunch of unvalued labels. --%>
<c:choose>
<c:when test="${bookAuditDetail != null}">
	<div class="bookDefinitionView">
		<div class="section">
			<div class="sectionLabel">
				General
			</div>
			<div class="centerSection">
				<div class="leftDefinitionForm">
					<div class="row">
						<label class="labelCol">Title ID</label>
						<span class="field">${ bookAuditDetail.titleId }</span>
					</div>
					<div class="row">
						<label class="labelCol">ProView Display Name</label>
						<span class="field">${ bookAuditDetail.proviewDisplayName }</span>
					</div>
					<div class="row">
						<label class="labelCol">Publish Date Text</label>
						<span class="field">${ bookAuditDetail.publishDateText }</span>
					</div>
					<div class="row">
						<label class="labelCol">ISBN</label>
						<span class="field">${ bookAuditDetail.isbn }</span>
					</div>
				
					<div class="row">
						<label class="labelCol">Sub Material Number</label>
						<span class="field">${ bookAuditDetail.materialId }</span>
					</div>
				</div>
				
				<div class="rightDefinitionForm">
					<div class="row">
						<label class="labelCol">TOC or NORT</label>
						<c:set var="tocOrNort" value="${ bookAuditDetail.isTocFlag ? 'TOC' : 'NORT'  }"/>
						<span class="field">${tocOrNort}</span>
					</div>
					<c:choose>
					<c:when test="${ bookAuditDetail.isTocFlag }">
						<div id="displayTOC">
							<div class="row">
								<label class="labelCol">TOC Collection</label>
								<span class="field">${ bookAuditDetail.tocCollectionName }</span>
							</div>
							<div class="row">
								<label class="labelCol">DOC Collection</label>
								<span class="field">${ bookAuditDetail.docCollectionName }</span>
							</div>
							<div class="row">
								<label class="labelCol">Root TOC Guid</label>
								<span class="field">${ bookAuditDetail.rootTocGuid }</span>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<div id="displayNORT">
							<div class="row">
								<label class="labelCol">NORT Domain</label>
								<span class="field">${ bookAuditDetail.nortDomain }</span>
							</div>
							<div class="row">
								<label class="labelCol">NORT Filter View</label>
								<span class="field">${ bookAuditDetail.nortFilterView }</span>
							</div>
						</div>
					</c:otherwise>
					</c:choose>
					<div class="row">
						<label class="labelCol">Publication Cut-off Date</label>
						<span class="field"><fmt:formatDate value="${bookAuditDetail.publishCutoffDate}" pattern="<%= WebConstants.DATE_FORMAT_PATTERN %>" /></span>
					</div>
					<div class="row">
						<label class="labelCol">KeyCite Topline Flag</label>
						<span class="field">${ bookAuditDetail.keyciteToplineFlag }</span>
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
						<span class="field">${ bookAuditDetail.keywordsConcat }</span>
					</div>
				</div>
				<div class="rightDefinitionForm">
					<div class="row">
						<label class="labelCol">Use ProView Table View</label>
						<span class="field">${ bookAuditDetail.isProviewTableViewFlag }</span>
					</div>
					<div class="row">
						<label class="labelCol">Auto-update Support</label>
						<span class="field">${ bookAuditDetail.autoUpdateSupportFlag }</span>
					</div>
					<div class="row">
						<label class="labelCol">Search Index</label>
						<span class="field">${ bookAuditDetail.searchIndexFlag }</span>
					</div>
					<div class="row">
						<label class="labelCol">Enable Copy Feature</label>
						<span class="field">${ bookAuditDetail.enableCopyFeatureFlag }</span>
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
						<label class="labelCol">Front Matter TOC Label</label>
						<span class="field">${ bookAuditDetail.frontMatterTocLabel }</span>
					</div>
					<div class="row">
						<label class="labelCol">Book Name Lines</label>
						<div class="field">${bookAuditDetail.bookNamesConcat}</div>
					</div>
					<div class="row">
						<label class="labelCol">Copyright</label>
						<div class="field">${ bookAuditDetail.copyright }</div>
					</div>
					<div class="row">
						<label class="labelCol">Copyright Page Text</label>
						<div class="field">${ bookAuditDetail.copyrightPageText }</div>
					</div>					
				</div>
				<div class="rightDefinitionForm">
					<div class="row">
						<label class="labelCol">Additional Trademark/Patent Info</label>
						<div class="field">${ bookAuditDetail.additionalTrademarkInfo }</div>
					</div>
					<div class="row">
						<label class="labelCol">Currentness Message</label>
						<div class="field">${ bookAuditDetail.currency }</div>
					</div>
					
					<div class="row">
						<label class="labelCol">Author Display</label>
						<c:set var="authorDisplay" value="${ bookAuditDetail.authorDisplayVerticalFlag ? 'Vertical' : 'Horizontal'  }"/>
						<span class="field">${ authorDisplay }</span>
					</div>
					
					<div class="row">
						<label class="labelCol">Author Information</label>
						<div class="field">${ bookAuditDetail.authorNamesConcat }</div>
					</div>
				</div>
			</div>
		</div>
		<div class="section">
			<div class="sectionLabel">
				Additional Front Matter
			</div>
			<div class="centerSection">
				<div class="field">${ bookAuditDetail.frontMatterConcat }</div>
			</div>
		</div>
	</div>

</c:when>	<%-- If there is any bookAuditDetail to display --%>
<c:otherwise>
	<div class="errorMessage">Book definition audit was not found.</div>
</c:otherwise>
</c:choose>