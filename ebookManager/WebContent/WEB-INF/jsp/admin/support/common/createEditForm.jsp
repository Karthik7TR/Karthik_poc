<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.support.SupportForm"%>

<script type="text/javascript" src="js/form-utils.js"></script>

<form:form commandName="<%= SupportForm.FORM_NAME %>">
	<form:hidden path="supportPageLinkId" />
	<div class="row">
		<form:label path="linkDescription">Link Description</form:label>
		<form:input path="linkDescription"/>
		<form:errors path="linkDescription" cssClass="errorMessage"/>
	</div>
	<div class="row">
		<form:label path="linkAddress">Link Address</form:label>
		<form:input path="linkAddress"/>
		<form:errors path="linkAddress" cssClass="errorMessage"/>
	</div>
	<div class="buttons">
		<button id="save" onclick="submitFormAndDisableButton('<%=SupportForm.FORM_NAME%>', this)">Save</button>
		<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_SUPPORT_VIEW%>';">Cancel</button>
	</div>
</form:form>
