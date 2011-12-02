
<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobrun.JobRunForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<link rel="stylesheet" href="css/Master.css">
</head>

<body>
<h2>Run Job</h2>

	<form:form action="<%=WebConstants.URL_JOB_RUN%>"
			   commandName="<%=JobRunForm.FORM_NAME%>" name="theForm" method="post">
			   
		<%-- Error Message Presentation --%>
		<spring:hasBindErrors name="<%=JobRunForm.FORM_NAME%>">
			<div class="errorBox">
		      <b><spring:message code="please.fix.errors"/></b><br/>
		      <form:errors path="*">
		      	<ul>
				<c:forEach items="${messages}" var="message">
					<li style="color: black">${message}</li>
				</c:forEach>
		      	</ul>
			  </form:errors>
			  <br/>
		    </div>
		    <br/>
	    </spring:hasBindErrors>

		Book	<%-- Unique book discriminate --%>
		<form:select path="bookCode">
			<form:options items="${bookCodeOptions}" itemLabel="label" itemValue="value"/>
		</form:select>
		<br/>
		Job Priority  <%-- Indicates which launch queue to place job request on --%>
		<form:select path="highPriorityJob">
			<form:option label="Normal" value="false"/>
			<form:option label="High" value="true"/>
		</form:select>
		<br/>
		Thread Priority
		<form:select path="threadPriority">
			<form:option label="Normal" value="5"/>
			<form:option label="Minimum" value="1"/>
			<form:option label="Maximum" value="10"/>
		</form:select>
		<br/>
		<br/>
		
		<input type="submit" value="Run Job"/>
	</form:form>

</body>
</html>
