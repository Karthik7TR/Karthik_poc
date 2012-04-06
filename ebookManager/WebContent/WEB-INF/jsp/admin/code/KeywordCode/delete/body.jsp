<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordcode.KeywordCodeForm"%>

<c:choose>
	<c:when test="${keywordTypeCode != null}">
		<form:form commandName="<%= KeywordCodeForm.FORM_NAME %>">
			<form:hidden path="id" />
			<form:hidden path="name"/>
			Are you sure you want to delete Keyword Code: ${keywordTypeCode.name}
			<div class="buttons">
				<form:button id="delete">Delete</form:button>
				<a href="<%=WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW%>">Cancel</a>
			</div>
		</form:form>
		
		<div>Keywords in ${keywordTypeCode.name} will be removed from the following Book Definitions</div>
		
		<display:table id="<%= WebConstants.KEY_VDO %>" name="book" class="displayTagTable">
			<display:setProperty name="basic.msg.empty_list">No Books were found that used Keywords in ${keywordTypeCode.name}.</display:setProperty>
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
		<div class="errorMessage">Invalid Keyword Code ID</div>
	</c:otherwise>
</c:choose>