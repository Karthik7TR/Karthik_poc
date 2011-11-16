<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<title>Job Op Fail</title>
</head>

<body>
<h2 style="color: red">Job Execution <a href="${dashboardDetailsUrl}">${jobExecutionId}</a> ${action} failed</h2>

<div style="color: red">${errorMessage}</div>
<br/>
<div>${stackTrace}</div>

</body>

