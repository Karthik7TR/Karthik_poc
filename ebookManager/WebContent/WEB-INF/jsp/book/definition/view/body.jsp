<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm"%>


<script>
function submitForm(cmd)
{
	$('#command').val(cmd);
	$('#<%=ViewBookDefinitionForm.FORM_NAME%>').submit();
	return true;
}
</script>
<style type="text/css">
	.labelCol { text-align:right; font-weight: bold;}
</style>

<%-- Check if there is a book model to render, if not don't display a bunch of unvalued labels. --%>
<c:if test="${book != null}">
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
					<span class="field">${ book.proviewDisplayName }</span>
				</div>
				
				<div class="row">
					<label class="labelCol">ISBN</label>
					<span class="field">${ book.isbn }</span>
				</div>
			
				<div class="row">
					<label class="labelCol">Sub Material Number</label>
					<span class="field">${ book.materialId }</span>
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
					<label class="labelCol">Publication Cut-off Date</label>
					<span class="field"><fmt:formatDate value="${book.publishCutoffDate}" pattern="<%= WebConstants.DATE_FORMAT_PATTERN %>" /></span>
				</div>
				<div class="row">
					<label class="labelCol">Book Definition Status</label>
					<span class="field">${ book.bookStatus }</span>
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
					<label class="labelCol">Use ProView Table View</label>
					<span class="field">${ book.proviewTableViewFlag }</span>
				</div>
				<div class="row">
					<label class="labelCol">Auto-update Support</label>
					<span class="field">${ book.autoUpdateSupportFlag }</span>
				</div>
				<div class="row">
					<label class="labelCol">Search Index</label>
					<span class="field">${ book.searchIndexFlag }</span>
				</div>
				<div class="row">
					<label class="labelCol">KeyCite Topline Flag</label>
					<span class="field">${ book.keyciteToplineFlag }</span>
				</div>
				<div class="row">
					<label class="labelCol">Enable Copy Feature</label>
					<span class="field">${ book.enableCopyFeatureFlag }</span>
				</div>
				<div class="row">
					<label class="labelCol">Front Matter TOC Label</label>
					<span class="field">${ book.frontMatterTocLabel }</span>
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
					<label class="labelCol">Copyright</label>
					<span class="field">${ book.copyright }</span>
				</div>
				<div class="row">
					<label class="labelCol">Copyright Page Text</label>
					<span class="field">${ book.copyrightPageText }</span>
				</div>
				<div id="nameLine" class="row">
					<label class="labelCol">Name Line</label>
					<c:forEach items="${book.ebookNames}" var="name">
						<div>
							<c:out value="${name.bookNameText}"></c:out>
						</div>
					</c:forEach>
				</div> 
			</div>
			<div class="rightDefinitionForm">
				<div class="row">
					<label class="labelCol">Publish Date Text</label>
					<span class="field">${ book.publishDateText }</span>
				</div>
				
				<div class="row">
					<label class="labelCol">Currentness Message</label>
					<span class="field">${ book.currency }</span>
				</div>
				
				<div class="row">
					<label class="labelCol">Author Display</label>
					<c:set var="authorDisplay" value="${ book.authorDisplayVertical ? 'Vertical' : 'Horizontal'  }"/>
					<span class="field">${ authorDisplay }</span>
				</div>
				
				<div id="authorName" class="row">
					<label class="labelCol">Author Information</label>
					<c:forEach items="${book.authors}" var="author">
						<div>
							${author.fullName }
							<div>${author.authorAddlText}</div>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
	</div>
	</div>
	<div style="font-family:Arial">
		<%-- Action buttons for the displayed book definition --%>
		<br/>
		<form:form name="theForm" commandName="<%=ViewBookDefinitionForm.FORM_NAME%>" action="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_POST%>">
			<form:hidden path="command"/>
			<form:hidden path="<%=WebConstants.KEY_ID%>"/>
			<c:if test="${!isInJobRequest}">
				<input type="submit" ${buttonVisibility} value="Edit" onclick="submitForm('<%=ViewBookDefinitionForm.Command.EDIT%>')"/>
			</c:if>
			<input type="submit" ${buttonVisibility} value="Copy" onclick="submitForm('<%=ViewBookDefinitionForm.Command.COPY%>')"/>
			<input type="submit" ${buttonVisibility} value="Generate" onclick="submitForm('<%=ViewBookDefinitionForm.Command.GENERATE%>')"/>
			<input type="submit" ${buttonVisibility} value="Delete" onclick="submitForm('<%=ViewBookDefinitionForm.Command.DELETE%>')"/>
			<input type="submit" value="Audit Log" onclick="submitForm('<%=ViewBookDefinitionForm.Command.AUDIT_LOG%>')"/>
			<input type="submit" value="Job History" onclick="submitForm('<%=ViewBookDefinitionForm.Command.BOOK_JOB_HISTORY%>')"/>
		</form:form>
	</div>
</c:if>	<%-- If there is any book to display --%>
