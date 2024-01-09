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
<c:set var="onlySuperUserAndPublisherPlus" value="false"/>
<sec:authorize access="hasAnyRole('ROLE_SUPERUSER,ROLE_PUBLISHER_PLUS')">
	<c:set var="onlySuperUserAndPublisherPlus" value="true"/>
</sec:authorize>

<%-- Group create/edit section only Super User or ROLE_PUBLISHER_PLUS can access --%>
<c:if test="${onlySuperUserAndPublisherPlus}">
	<%-- Check if there is a book model to render, if not don't display a bunch of unvalued labels. --%>
	<c:choose>
		<%-- Error Messages area --%>
		<c:when test="${errMessage != null}">
			<div class="infoMessageError">
				${errMessage}
			</div>
			<c:if test="${warningMessage != null }">
				<div class="errorMessage">
				<br/><b>No information found for titles:</b><br/>
					<c:forEach items="${warningMessage}" var="title" varStatus="status">
						"${title}"<br/>
					</c:forEach>
				</div>
			</c:if>
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
						<form:button id="confirm">Save Group</form:button>
						<c:set var="cancelUrl" value="${editGroupDefinitionForm.combined ? WebConstants.MVC_COMBINED_BOOK_DEFINITION_VIEW : WebConstants.MVC_BOOK_DEFINITION_VIEW_GET}"/>
						<button type="button" onclick="location.href ='${cancelUrl}?<%=WebConstants.KEY_ID%>=${editGroupDefinitionForm.bookDefinitionId}';">Cancel</button>
					</div>
				</form:form>
			</div>
		</c:otherwise>
	</c:choose>
</c:if>

