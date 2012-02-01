<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->

<%@page import="java.io.StringWriter" %>
<%@page import="java.io.PrintWriter" %>
<%@page isErrorPage="true" %>

<html>
<head>
	<TITLE>Error Occurred</TITLE>
</head>

<body>
<strong>Error - An unexpected application exception has occurred:</strong><br/>
<strong><%=exception.getMessage() %></strong><br/>
<br/>
<font color="red">
<%
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
try {
	exception.printStackTrace(pw);
	out.print(sw);
} finally {
	pw.close();
	sw.close();
}
%>
</font>	
</body>
</html>