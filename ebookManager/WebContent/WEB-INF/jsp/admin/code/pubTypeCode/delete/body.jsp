<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.publishtypecode.PublishTypeCodeForm"%>


<%-- Check if there is a model to render, if not display error message --%>
<c:choose>
	<c:when test="${pubTypeCode != null}">
		<form:form commandName="<%= PublishTypeCodeForm.FORM_NAME %>">
			<form:hidden path="id" />
			<form:hidden path="name"/>
			Are you sure you want to delete Jurisdiction Code: ${pubTypeCode.name}
			<div class="buttons">
				<form:button id="delete">Delete</form:button>
				<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW%>';">Cancel</button>
			</div>
		</form:form>
	</c:when>
	<c:otherwise>
		<div class="errorMessage">No Publish Type Code found</div>
	</c:otherwise>
</c:choose>

