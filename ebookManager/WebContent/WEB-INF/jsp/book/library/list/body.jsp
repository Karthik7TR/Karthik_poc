<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryController"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm.DisplayTagSortProperty"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

	 
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

<form:form commandName="<%=BookLibrarySelectionForm.FORM_NAME%>" method="post" action="<%= WebConstants.MVC_BOOK_LIBRARY_LIST_SELECTION_POST%>">
	<form:hidden path="command"/>

    <%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=BookLibrarySelectionForm.FORM_NAME%>">
		<div class="errorBox">
	      <b><spring:message code="please.fix.errors"/>:</b><br/>
	      <form:errors path="*">
	      	<ul>
			<c:forEach items="${messages}" var="message">
				<li style="color: black">${message}</li>
			</c:forEach>
	      	</ul>
		  </form:errors>
		  <br/>
	    </div>
    </spring:hasBindErrors>
    
	<div class="buttons">
		<input type="submit" value="Generate" ${generateBook} onclick="submitForm('<%= BookLibrarySelectionForm.Command.GENERATE %>')" />
	</div>
	<c:set var="selectAll" value="<input type='checkbox' id='selectAll' value='false' />"/>
	<%-- Table of book library --%>
	<display:table id="<%= WebConstants.KEY_VDO %>" name="paginatedList" class="displayTagTable" cellpadding="2" 
				   requestURI="<%= WebConstants.MVC_BOOK_LIBRARY_LIST_PAGING%>"
				   sort="external">
	  <display:setProperty name="paging.banner.onepage" value=" " />
	  <display:setProperty name="basic.msg.empty_list">No book definitions were found.</display:setProperty>
	  <display:column title="${selectAll}"  style="text-align: center">
	  		<form:checkbox path="selectedEbookKeys" value="${vdo.bookDefinitionId}"/>
	  </display:column>
	  <display:column title="ProView Display Name" sortable="true" sortProperty="<%=DisplayTagSortProperty.PROVIEW_DISPLAY_NAME.toString() %>" style="text-align: left">
	  	<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${vdo.bookDefinitionId}">${vdo.proviewDisplayName}</a>
	  </display:column>
	  <display:column title="Title ID" sortable="true" sortProperty="<%=DisplayTagSortProperty.TITLE_ID.toString() %>" >
	  	<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${vdo.bookDefinitionId}">${vdo.fullyQualifiedTitleId}</a>
	  </display:column>
	  <display:column title="Author" property="authorList" />
	  <display:column title="Last Generate Date/Time" sortable="true" sortProperty="<%=DisplayTagSortProperty.LAST_GENERATED_DATE.toString() %>">
	  	<fmt:formatDate value="${vdo.lastPublishDate}" pattern="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	  </display:column>
	  <display:column title="Definition Status" property="bookStatus" sortable="true" sortProperty="<%=DisplayTagSortProperty.DEFINITION_STATUS.toString() %>" />
	  <display:column title="Last Definition Edit Date/Time" sortable="true" sortProperty="<%=DisplayTagSortProperty.LAST_EDIT_DATE.toString() %>" >
	  	<fmt:formatDate value="${vdo.lastUpdated}" pattern="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	  </display:column>
	</display:table>
	<c:set var="generateBook" value="disabled"/>
	<sec:authorize access="hasAnyRole('ROLE_PUBLISHER,ROLE_PUBLISHER_PLUS,ROLE_SUPERUSER')">
		<c:set var="generateBook" value=""/>
	</sec:authorize>
	<c:set var="superUser" value="disabled"/>
	<sec:authorize access="hasRole('ROLE_SUPERUSER')">
		<c:set var="superUser" value=""/>
	</sec:authorize>
	<div class="buttons">
		<input type="submit" value="Generate" ${generateBook} onclick="submitForm('<%= BookLibrarySelectionForm.Command.GENERATE %>')" />
	</div>

</form:form>


<%-- Select for how may items (rows) per page to show --%>
<c:if test="${fn:length(paginatedList.list) != 0}">
  <form:form id="itemCountForm" action="<%=WebConstants.MVC_BOOK_LIBRARY_CHANGE_ROW_COUNT%>"
		     commandName="<%=BookLibrarySelectionForm.FORM_NAME%>" method="post">
	Items to display: 
	<c:set var="defaultItemsPerPage" value="<%=PageAndSort.DEFAULT_ITEMS_PER_PAGE%>"/>
	<form:select path="objectsPerPage" onchange="submit()">
		<form:option label="${defaultItemsPerPage}" value="${defaultItemsPerPage}"/>
		<form:option label="50" value="50"/>
		<form:option label="100" value="100"/>
		<form:option label="150" value="150"/>
		<form:option label="300" value="300"/>
		<%-- Shows to MAX_INT.  Needs to get updated once number of books reach this amount --%>
		<form:option label="ALL" value="<%= Integer.MAX_VALUE %>"/>
	</form:select>
  </form:form>
</c:if>  <%-- if (table row count > 0) --%>	
