<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.security.LoginForm"%>

<%--
	This form is used to submit the username and password to the "j_spring_security_check" URL.
	This URL is intercepted by the Spring Security filter chain, and causes user to be authenticated.
	The reason we have this hidden, self-submitting form is because we need to have the user submit
	the username/password from the data-entry form to a controller.  The controller will
	validate/process the data, and if no errors, redirects back to this page which will self-submit
	the j_username and j_password through to the filter chain via the j_spring_security_check URL 
	in order to perform authentication.
 --%>
<html>
  <body onload="autoLoginForm.submit()">
  	Performing login, please wait...
  	<form:form action="j_spring_security_check" commandName="<%=LoginForm.FORM_NAME%>" name="autoLoginForm">
  		<form:hidden path="j_username"/>
  		<form:hidden path="j_password"/>
  	</form:form>
  </body>
</html>