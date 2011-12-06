<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
</head>

<body>
<h2>E-Book Generator Engine Home</h2>

<br/>

<%-- Hudson build information --%>
  <div style="vertical-align: middle; width:98%" align="right">
	${environmentName} Build # @buildTag@ (@buildTime@)
  </div>

</body>
</html>
