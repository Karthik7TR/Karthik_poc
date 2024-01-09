<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.doctypemetric.DocTypeMetricForm"%>


<form:form commandName="<%= DocTypeMetricForm.FORM_NAME %>">
	<form:hidden path="name" />
	<div class="row">
		<form:label id="name" path="name">Name</form:label>
		<form:input disabled="true" path="name"/>
	</div>
	<div class="row">
		<form:label id="thresholdValue" path="thresholdValue">Threshold Value</form:label>
		<form:input path="thresholdValue"/>
		<form:errors path="thresholdValue" cssClass="errorMessage"/>
	</div>
	<div class="row">
		<form:label id="thresholdPercent" path="thresholdPercent">Threshold Percent</form:label>
		<form:input path="thresholdPercent"/>
		<form:errors path="thresholdPercent" cssClass="errorMessage"/>
	</div>
	<div class="buttons">
		<form:button id="save">Save</form:button>
		<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_DOCTYPE_METRIC_VIEW%>';">Cancel</button>
	</div>
</form:form>