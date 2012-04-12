<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.security.LoginForm"%>

<script>
// Set initial cursor focus in the Username text input field
$(document).ready(function() {
	document.getElementById("username").focus();
});
</script>

<form:form action="<%=WebConstants.MVC_SEC_LOGIN%>" commandName="<%=LoginForm.FORM_NAME%>" method="post" name="loginForm">
	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=LoginForm.FORM_NAME%>">
	      <form:errors path="*">
			<c:forEach items="${messages}" var="message">
				<span style="color: red">${message}</span><br/>
			</c:forEach>
		  </form:errors>
		  <br/>
    </spring:hasBindErrors> 
    
    <%-- Login failure message --%>
    <c:if test="${fn:length(infoMessages) > 0}">
 	<c:forEach items="${infoMessages}" var="message">
		<span style="color:red;">${message.text}</span><br/>
	</c:forEach>
 	<br/>
    </c:if>

	<table>
		<tr>
			<td>Username:</td>
			<td><form:input path="username"/><br>
			<i style="color:grey;">network username (eg: u1234567)</i></td>
		</tr>
		<tr>
			<td>Password:</td>
			<td><form:password path="password"/><br>
			<i style="color:grey;">network password</i></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input type="submit" value="Login"></td>
		</tr>
	</table>

</form:form>
