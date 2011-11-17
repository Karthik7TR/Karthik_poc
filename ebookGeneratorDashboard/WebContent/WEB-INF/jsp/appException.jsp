<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@page import="java.io.StringWriter" %>
<%@page import="java.io.PrintWriter" %>

<%@ page isErrorPage="true" %>

<html>
<head>
	<TITLE>Error Occurred</TITLE>
</head>

<body>
<jsp:include page="stdHeader.jsp"/>

<strong>Error - An unexpected application exception has occurred:</strong><br/>
<strong><%=exception.getMessage() %></strong><br/>
<br/>
<font color="red">
<%
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
exception.printStackTrace(pw);
out.print(sw);
pw.close();
sw.close();
%>
</font>
			
</body>
</html>