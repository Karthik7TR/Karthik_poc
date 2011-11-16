<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller.admin.AdminForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<link rel="stylesheet" href="css/Master.css">
</head>

<body>
<h2>E-Book Generator Engine Administration</h2>

	<form:form action="<%=WebConstants.URL_ADMIN_POST%>"
			   commandName="<%=AdminForm.FORM_NAME%>" name="theForm" method="post">
			   
		<%-- Error Message Presentation --%>
		<spring:hasBindErrors name="<%=AdminForm.FORM_NAME%>">
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
	    
	    Maximum Concurrent Jobs:
		<form:select path="maxConcurrentJobs">
			<form:option label="One (1)" value="1"/>
			<form:option label="Two (2)" value="2"/>
			<form:option label="Three (3)" value="3"/>
			<form:option label="Four (4)" value="4"/>
			<form:option label="Five (5)" value="5"/>
			<form:option label="Ten (10)" value="10"/>
			<form:option label="Fifteen (15)" value="15"/>
			<form:option label="Twenty (20)" value="20"/>
			<form:option label="Twenty Five (25)" value="25"/>
			<form:option label="Thirty (30)" value="30"/>
			<form:option label="Forty (40)" value="40"/>
			<form:option label="Fifty (50)" value="50"/>
			<form:option label="One Hundred (100)" value="100"/>
			<form:option label="Unlimited" value=""/>
		</form:select>

		<br/>
		<input type="submit" value="Submit"/>
	</form:form>
</body>
</html>
