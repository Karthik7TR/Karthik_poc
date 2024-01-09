<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.statecode.StateCodeForm"%>

<%-- Check if there is a model to render, if not display error message --%>
<c:choose>
	<c:when test="${stateCode != null}">
		<form:form commandName="<%= StateCodeForm.FORM_NAME %>">
			<form:hidden path="stateId" />
			<form:hidden path="name"/>
			Are you sure you want to delete State Code: ${stateCode.name}
			<div class="buttons">
				<form:button id="delete">Delete</form:button>
				<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_STATE_CODE_VIEW%>';">Cancel</button>
			</div>
		</form:form>
	</c:when>
	<c:otherwise>
		<div class="errorMessage">No State Code found</div>
	</c:otherwise>
</c:choose>

