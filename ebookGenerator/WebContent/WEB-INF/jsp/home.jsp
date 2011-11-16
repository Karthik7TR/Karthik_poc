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
<h2>E-Book Generator Home</h2>

Max Concurrent Jobs: TODO<br/>
Current job count: nn<br/>

<br/>

<a href="<%=WebConstants.URL_ADMIN_GET%>">Generator Administration</a><br/>
<br/>
<a href="eReaderPublishingJob.mvc">Launch Play eReader Publishing Job</a><br/>

<%-- Hudson build information --%>
  <div style="vertical-align: middle; width:98%" align="right">
	${environmentName} Build # @buildTag@ (@buildTime@)
  </div>

</body>
</html>
