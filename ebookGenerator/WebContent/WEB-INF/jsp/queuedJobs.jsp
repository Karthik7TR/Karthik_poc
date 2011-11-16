<%@ page import="com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller.admin.AdminForm"%>
<%@ page import="com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
	Displays the job run requests sitting in the normal and high priority start JMS queues that are waiting to be started once
	the number of currently executing jobs drops below the upper throttle limit.
 --%>
<html>
<head>
	<link rel="stylesheet" href="css/Master.css">
</head>

<body>
<h2>E-Book Generator Job Queues</h2>

Maximum Concurrent Jobs: nn<br/>
Currently Executing Jobs: mm<br/>
<br/>
TODO: Display 2 tables, the List<JobRunRequest> sitting on both the high and normal JMS run queues.



	
</body>
</html>
