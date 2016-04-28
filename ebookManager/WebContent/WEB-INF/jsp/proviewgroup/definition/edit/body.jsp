<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.UserUtils" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit.EditGroupDefinitionForm"%>

<%-- Check if user has Super User role --%>
<c:set var="onlySuperUser" value="false"/>
<sec:authorize access="hasRole('ROLE_SUPERUSER')">
	<c:set var="onlySuperUser" value="true"/>
</sec:authorize>

<%-- Group create/edit section only Super User can access --%>
<c:if test="${onlySuperUser}">
	<%-- Check if there is a book model to render, if not don't display a bunch of unvalued labels. --%>
	<c:choose>
		<%-- Error Messages area --%>
		<c:when test="${errMessage != null}">
			<div class="infoMessageError">
				${errMessage}
			</div>
			<br/>
		</c:when>
		<c:when test="${book == null}">  <%-- if no book definition was found for BookDefinitionId --%>
			<div class="errorMessage"><b>Book definition was not found.</b></div><br/>
		</c:when>
		<c:when test="${allProviewTitleInfo == 0}">  <%-- if no titles are found on ProView --%>
			<div class="errorMessage"><b>eBooks were not found on ProView.</b></div><br/>
		</c:when>
		<c:otherwise>
			<div class="bookDefinitionCRUD">
				<form:form commandName="<%= EditGroupDefinitionForm.FORM_NAME %>" action="<%=WebConstants.MVC_GROUP_DEFINITION_EDIT%>" >
					<jsp:include page="../common/crudForm.jsp" />
					<div class="buttons">
						<form:button id="confirm">Create Group</form:button>
						<button type="button" onclick="location.href ='<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${book.ebookDefinitionId}';">Cancel</button>
					</div>
				</form:form>
			</div>
		</c:otherwise>
	</c:choose>
</c:if>

