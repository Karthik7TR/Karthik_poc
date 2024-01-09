<!--
	Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

<script type="text/javascript">
function openFullScreenWindow(url)
{
	window.open(url, "","channelmode,scrollbars");
}

</script>

<display:table id="<%= WebConstants.KEY_VDO %>" name="<%=WebConstants.KEY_BULK_PUBLISH_LIST%>" class="displayTagTable" cellpadding="2" 
   requestURI="<%=WebConstants.MVC_BOOK_BULK_GENERATE_PREVIEW%>"
   pagesize="50"
   partialList="true"
   size="bulkPublishtSize"
   >
			   
 	<display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
  	<display:setProperty name="paging.banner.onepage" value=" " />
  	<display:column title="Title ID" property="fullyQualifiedTitleId" sortable="false"/>
  	<display:column title="ProView Display Name" property="proviewDisplayName" sortable="false"/>
 	<display:column title="Action" sortable="false">
 			<c:choose>
 				<c:when test="${ vdo.deleted }">
 					<span class="errorMessage">This Book has been deleted</span>
 				</c:when>
 				<c:otherwise>
 					<input value="Generate this book" type="button" onclick="disabled=true; openFullScreenWindow('<%=WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW%>?<%=WebConstants.KEY_ID%>=${vdo.bookId}&isCombined=false')"/>
 				</c:otherwise>
 			</c:choose>
 	</display:column>
 </display:table>