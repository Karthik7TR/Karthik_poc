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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 

<html>
<%-- Popup Preview window specifications (used in function and in onclick() handler) --%>
<c:set var="winSpecs" value="<%=WebConstants.FRONT_MATTER_PREVIEW_WINDOW_SPECS %>"/>

<head>
<script>
/**
 * Open the preview popup window used to display the static (title,copyright,research,westlawnext) content.
 */
function openStaticPreviewWindow(url, id, name) {
	var queryString = 'id=' + id;
	var win = window.open(url + '?' + queryString, name, '${winSpecs}');
	win.focus();
}
</script>
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
${fn:escapeXml(book.frontMatterTocLabel)}<br/>
<div id="frontMatterLinks">
	<a onclick="openStaticPreviewWindow('<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_TITLE%>', '${book.ebookDefinitionId}', 'titleWin')">Title Page</a><br/>
	<a onclick="openStaticPreviewWindow('<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_COPYRIGHT%>', '${book.ebookDefinitionId}', 'copyrightWin')">Copyright Page</a><br/>
	<c:forEach items="${book.frontMatterPages}" var="page">
		<a onclick="win=window.open('<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_ADDITIONAL%>?bookDefinitionId=${book.ebookDefinitionId}&frontMatterPageId=${page.id}', 'additionalWin', '${winSpecs}');win.focus()">
			${fn:escapeXml(page.pageTocLabel)}
		</a><br/>
	</c:forEach>
	<a onclick="openStaticPreviewWindow('<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_RESEARCH%>', '${book.ebookDefinitionId}', 'researchWin')">Additional Information or Research Assistance</a><br/>
	<a onclick="openStaticPreviewWindow('<%=WebConstants.MVC_FRONT_MATTER_PREVIEW_WESTLAWNEXT%>', '${book.ebookDefinitionId}', 'westlawNextWin')">WestlawNext</a><br/>
</div>
</c:if>

</body>

</html>