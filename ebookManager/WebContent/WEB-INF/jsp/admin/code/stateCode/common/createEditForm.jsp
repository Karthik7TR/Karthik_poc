<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.statecode.StateCodeForm"%>


<form:form commandName="<%= StateCodeForm.FORM_NAME %>">
	<form:hidden path="stateId" />
	<form:label path="name">Name</form:label>
	<form:input path="name" maxlength="4000"/>
	<form:errors path="name" cssClass="errorMessage"/>
	<div class="buttons">
		<form:button id="save">Save</form:button>
		<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_STATE_CODE_VIEW%>';">Cancel</button>
	</div>
</form:form>
