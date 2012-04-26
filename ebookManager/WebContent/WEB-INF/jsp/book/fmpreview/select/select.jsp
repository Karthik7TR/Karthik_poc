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

<%-- Error message if find of book def failed --%>
<c:if test="${errMessage != null}">
<div>
	${errMessage.text}
</div>
</c:if>

<%-- Do not display the links if there is no book ID --%>
<c:if test="${book != null}">
${book.frontMatterTocLabel}<br/>
<div style="padding-left:2em">
	<a href="<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_TITLE%>?id=${book.ebookDefinitionId}">TITLE PAGE</a><br/>
	<a href="<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_COPYRIGHT%>?id=${book.ebookDefinitionId}">COPYRIGHT PAGE</a><br/>
	<c:forEach items="${book.frontMatterPages}" var="page">
		<a href="<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_ADDITIONAL%>?bookDefinitionId=${book.ebookDefinitionId}&frontMatterPageId=${page.id}">${page.pageTocLabel}</a><br/>
	</c:forEach>
	<a href="<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_RESEARCH%>?id=${book.ebookDefinitionId}">ADDITIONAL INFO OR RESEARCH ASSISTANCE</a><br/>
	<a href="<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_WESTLAWNEXT%>?id=${book.ebookDefinitionId}">WestlawNext</a><br/>
</div>
</c:if>

</body>

</html>