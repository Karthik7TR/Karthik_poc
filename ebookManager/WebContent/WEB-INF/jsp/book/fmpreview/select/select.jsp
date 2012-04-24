<!--
	Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
</head>

<body>

${book.frontMatterTocLabel}<br/>
<div style="padding-left:2em">
	<a href="<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_TITLE%>?id=${id}">TITLE PAGE</a><br/>
	<a href="<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_COPYRIGHT%>?id=${id}">COPYRIGHT PAGE</a><br/>
	TODO: all additional front matter here...<br/>
	<a href="<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_RESEARCH%>?id=${id}">ADDITIONAL INFO OR RESEARCH ASSISTANCE</a><br/>
	<a href="<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_WESTLAWNEXT%>?id=${id}">WestlawNext</a><br/>
</div>
</body>

</html>