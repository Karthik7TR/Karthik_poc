<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordvalue.KeywordValueForm"%>

<c:choose>
	<c:when test="${ keywordTypeValue != null }">
		<form:form commandName="<%= KeywordValueForm.FORM_NAME %>">
			<form:hidden path="id" />
			<form:hidden path="name"/>
			<form:hidden path="keywordTypeCode.id" />
			<form:hidden path="keywordTypeCode.name" />
			Are you sure you want to delete Keyword Value: ${keywordTypeValue.name}?
			<div class="buttons">
				<form:button id="delete">Delete</form:button>
				<a href="<%=WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW%>">Cancel</a>
			</div>
		</form:form>
		
		<div>Keyword: ${keywordTypeValue.name} will be removed from the following Book Definitions</div>
		
		<display:table id="<%= WebConstants.KEY_VDO %>" name="book" class="displayTagTable">
			<display:setProperty name="basic.msg.empty_list">No Books were found that used Keyword: ${keywordTypeValue.name}.</display:setProperty>
			<display:column title="Title ID" >
				<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${vdo.ebookDefinitionId}">${vdo.fullyQualifiedTitleId}</a>
			</display:column>
			<display:column title="ProView Display Name" >
				<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${vdo.ebookDefinitionId}">${vdo.proviewDisplayName}</a>
			</display:column>
			<display:column title="Book Status" property="bookStatus"/>
		</display:table>
	</c:when>
	<c:otherwise>
		<div class="errorMessage">Invalid Keyword Value ID</div>
	</c:otherwise>
</c:choose>