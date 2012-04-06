<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jurisdictioncode.JurisdictionCodeForm"%>


<form:form commandName="<%= JurisdictionCodeForm.FORM_NAME %>">
	<form:hidden path="id" />
	<form:label path="name">Name</form:label>
	<form:input path="name"/>
	<form:errors path="name" cssClass="errorMessage"/>
	<div class="buttons">
		<form:button id="save">Save</form:button>
		<a href="<%=WebConstants.MVC_ADMIN_JURIS_CODE_VIEW%>">Cancel</a>
	</div>
</form:form>
