<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

</html>
<html>
<head>
	<link rel="stylesheet" href="css/Master.css">
</head>

<body>
<h2>Spring Batch Job Start Results</h2>

Created jobId: <c:out value="${jobExecution.jobId}"/><br/>
createTime: <c:out value="${jobExecution.createTime}"/><br/>
startTime : <c:out value="${jobExecution.startTime}"/><br/>
endTime: <c:out value="${jobExecution.endTime}"/><br/>
exitStatus : <c:out value="${jobExecution.exitStatus}"/><br/>
status : <c:out value="${jobExecution.status}"/><br/>


</body>
</html>
