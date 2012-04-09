<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryController"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

	 
	<script type="text/javascript">
		$(document).ready(function() {
			$('#selectAll').click(function () {
				$(this).parents('#<%= WebConstants.KEY_VDO %>').find(':checkbox').attr('checked', this.checked);
			});
		});
		var submitForm = function(cmd){
			$('#command').val(cmd);
			$('<%=BookLibrarySelectionForm.FORM_NAME%>').submit();
		};
	</script>

<form:form commandName="<%=BookLibrarySelectionForm.FORM_NAME%>" method="post" action="<%= WebConstants.MVC_BOOK_LIBRARY_LIST%>">

	<form:hidden path="isAscending" />
	<form:hidden path="sort" />
	<form:hidden path="page" />
	<form:hidden path="command"/>

	<%-- Error Message Presentation --%>
	<spring:hasBindErrors name="<%=BookLibrarySelectionForm.FORM_NAME%>">
		<div class="errorBox">
	      <b><spring:message code="please.fix.errors"/></b><br/>
	      <form:errors path="*">
	      	<ul>
			<c:forEach items="${messages}" var="message">
				<li style="color: black">${message}</li>
			</c:forEach>
	      	</ul>
		  </form:errors>
		  <br/>
	    </div>
	    <br/>
    </spring:hasBindErrors>

	<c:set var="selectAll" value="<input type='checkbox' id='selectAll' value='false' />"/>
	<c:set var="DATE_FORMAT" value="MM/dd/yy HH:mm:ss"/>
	<%-- Table of book library --%>
	<display:table id="<%= WebConstants.KEY_VDO %>" name="paginatedList" class="displayTagTable" cellpadding="2" 
				   requestURI="<%= WebConstants.MVC_BOOK_LIBRARY_LIST_PAGING%>"
				   pagesize="<%= WebConstants.NUMBER_BOOK_DEF_SHOWN %>"
				   partialList="true"
				   size="resultSize"
				   sort="external">
	  <display:setProperty name="basic.msg.empty_list">No book definitions were found.</display:setProperty>
	  <display:column title="${selectAll}"  style="text-align: center">
	  		<form:checkbox path="selectedEbookKeys" value="${vdo.bookDefinitionId}"/>
	  </display:column>
	  <display:column title="Title ID" sortable="true" sortName="fullyQualifiedTitleId" >
	  	<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${vdo.bookDefinitionId}">${vdo.fullyQualifiedTitleId}</a>
	  </display:column>
	  <display:column title="Book Name" sortable="true" sortName="proviewDisplayName" style="text-align: left">
	  	<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${vdo.bookDefinitionId}">${vdo.proviewDisplayName}</a>
	  </display:column>
	  <display:column title="Author" property="authorList" />
	  <display:column title="Publish Date" sortable="true" sortName="publishEndTimestamp">
	  	<fmt:formatDate value="${vdo.lastPublishDate}" pattern="${DATE_FORMAT}"/>
	  </display:column>
	  <display:column title="Book Status" property="bookStatus" sortable="true" sortName="isDeletedFlag" />
	  <display:column title="Last Book Def. Edit" sortable="true" sortName="lastUpdated" >
	  	<fmt:formatDate value="${vdo.lastUpdated}" pattern="${DATE_FORMAT}"/>
	  </display:column>
	</display:table>
	<br/>	
	<input type="submit" disabled="disabled" value="Import" onclick="submitForm('<%= BookLibrarySelectionForm.Command.IMPORT %>')" />
	<input type="submit" disabled="disabled" value="Export" onclick="submitForm('<%= BookLibrarySelectionForm.Command.EXPORT %>')"/>
	<input type="submit" value="Generate" ${superPublisherPublisherplusVisibility} onclick="submitForm('<%= BookLibrarySelectionForm.Command.GENERATE %>')" />
	<input type="submit" value="Promote" onclick="submitForm('<%= BookLibrarySelectionForm.Command.PROMOTE %>')" />

</form:form>
