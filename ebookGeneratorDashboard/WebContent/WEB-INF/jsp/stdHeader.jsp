<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="arial20" style="font-weight: bold;">
	E-Book Generator Dashboard &nbsp; (${environment}) &nbsp;
	<input type="button" value="Job Summary" onclick="location.href='<%=WebConstants.URL_JOB_SUMMARY%>'"/>
</div>