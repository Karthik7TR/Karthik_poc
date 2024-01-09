<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.UserUtils"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>


<form:form action="<%=WebConstants.MVC_ADMIN_QUALITY_SWITCH_APPLY%>" method="post" modelAttribute="qualityReportsForm" class="quality-form">
		Text Quality Test:
	<div class="quality-step-switch">
		<form:radiobutton path="qualityStepEnabled" label="Enabled" checked="${qualityReportsForm.isQualityStepEnabled() ? 'checked' : ''}" value="<%=Boolean.TRUE%>"/>
		<form:radiobutton path="qualityStepEnabled" label="Disabled" checked="${qualityReportsForm.isQualityStepEnabled() ? '' : 'checked'}" value="<%=Boolean.FALSE%>"/>
	</div>
	<form:button type="submit" class="quality-form-button">Apply</form:button>
</form:form><br/>

<c:forEach items="${qualityReportsForm.getRecipients()}" var="recipient">
		<form:form action="<%=WebConstants.MVC_ADMIN_QUALITY_RECIPIENTS_DELETE%>" method="post"
			modelAttribute="recipient" class="quality-form">
			<form:label path="email" class="recipient-email">${recipient}</form:label>
			<form:hidden path="email" value="${recipient}" />
			<form:button type="submit" class="quality-form-button">Delete</form:button>
		</form:form>
</c:forEach>

<form:form action="<%=WebConstants.MVC_ADMIN_QUALITY_REPORTS%>"
	method="post" modelAttribute="recipient" class="quality-form">
	<div>
		<form:input type="text" path="email" />
		<div class="errorDiv">
			<form:errors path="email" cssClass="errorMessage" />
		</div>
	</div>
	<form:button type="submit" class="quality-form-button">Add</form:button>
</form:form>
