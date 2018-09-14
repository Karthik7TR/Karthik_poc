<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.UserUtils"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<c:forEach items="${qualityReportsRecipients}" var="recipient">
		<form:form action="${deletePath}" method="post"
			modelAttribute="recipient" class="quality-recipient">
			<form:label path="email" class="recipient-email">${recipient}</form:label>
			<form:hidden path="email" value="${recipient}" />
			<form:button type="submit" class="recipient-button">Delete</form:button>
		</form:form>
</c:forEach>

<form:form action="<%=WebConstants.MVC_ADMIN_QUALITY_RECIPIENTS_ADD%>"
	method="post" modelAttribute="recipient" class="quality-recipient">
	<form:input type="text" path="email" />
	<form:button type="submit" class="recipient-button">Add</form:button>
</form:form>
