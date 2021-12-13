<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryFilterForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryFilterForm.DisplayTagSortProperty"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="defaultPageSize" value="<%=WebConstants.DEFAULT_PAGE_SIZE%>"/>

<script type="text/javascript" src="js/form-utils.js"></script>
<script type="text/javascript" src="js/tables.js"></script>
<script type="text/javascript">
	const opp = "${ pageSize == null ? defaultPageSize : pageSize }";
	$(window).on('pageshow', function () {
		$('#objectsPerPage option[value=' + opp + ']').prop('selected', true);
		checkSelectAllIfAllCheckboxesAreChecked();
	});
	$(document).ready(function () {
		$('#selectAll').click(function () {
			$(this).parents('#<%= WebConstants.KEY_VDO %>').find(':checkbox').not(this).prop('checked', this.checked);
		});
		$('#command').prop('disabled', true);
	});
</script>
<script type="text/javascript">
	const showNoAnyCheckboxSelectedError = function() {
		$('#noCheckboxSelectedError').removeAttr('style');
	};
	const disableCheckboxes = function() {
		$(`table#<%= WebConstants.KEY_VDO %> input[type=checkbox]`).prop('disabled', true);
	};
	const submitBookLibraryForm = function() {
		disableCheckboxes();
		submitLeftFormAndBodyForm();
	};
	const submitBookLibraryFormWithEmptyLeftForm = function() {
		disableCheckboxes();
		submitEmptyLeftFormAndBodyForm();
	};
	const submitBookLibraryFormWithCommand = function(cmd) {
		if ($("#<%= WebConstants.KEY_VDO %> input:checkbox:checked").length > 0) {
			$('#command')
					.prop('disabled', false)
					.val(cmd);
			submitLeftFormAndBodyForm();
		} else {
			showNoAnyCheckboxSelectedError();
		}
	};
</script>

<form:form id="bodyForm"
					 action="<%=WebConstants.MVC_BOOK_LIBRARY_LIST%>"
					 modelAttribute="<%=BookLibraryFilterForm.FORM_NAME%>"
					 method="get">
	<c:if test="${fn:length(paginatedList.list) != 0}">
		Items to display:
		<form:select path="objectsPerPage" onchange="submitBookLibraryForm()">
			<form:option label="${ defaultPageSize }" value="${ defaultPageSize }"/>
			<form:option label="50" value="50"/>
			<form:option label="100" value="100"/>
			<form:option label="150" value="150"/>
			<form:option label="300" value="300"/>
			<%-- Shows to MAX_INT.  Needs to get updated once number of books reach this amount --%>
			<form:option label="ALL" value="<%= Integer.MAX_VALUE %>"/>
		</form:select>
	</c:if>  <%-- if (table row count > 0) --%>

	<form:hidden path="command"/>

	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=BookLibraryFilterForm.FORM_NAME%>">
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

	<div id="noCheckboxSelectedError" class="errorBox" style="display: none">
		<strong><spring:message code="please.fix.errors"/>:</strong><br/>
		<ul>
			<li style="color: black"><spring:message code="error.required.bookselection"/></li>
		</ul>
	</div>

	<c:set var="generateBook" value="disabled"/>
	<sec:authorize access="hasAnyRole('ROLE_PUBLISHER,ROLE_PUBLISHER_PLUS,ROLE_SUPERUSER')">
		<c:set var="generateBook" value=""/>
	</sec:authorize>
	<c:set var="superUser" value="disabled"/>
	<sec:authorize access="hasRole('ROLE_SUPERUSER')">
		<c:set var="superUser" value=""/>
	</sec:authorize>
	
	<div class="buttons">
		<input type="button" value="Generate" ${generateBook} onclick="submitBookLibraryFormWithCommand('<%= BookLibraryFilterForm.Command.GENERATE %>')" />
	</div>
	<form:hidden path="sort" value="${ param.sort }"/>
	<form:hidden path="dir" value="${ param.dir }"/>
	<c:set var="selectAll" value="<input type='checkbox' id='selectAll' value='false' />"/>
	<%-- Table of book library --%>
	<display:table id="<%= WebConstants.KEY_VDO %>"
								 name="paginatedList"
								 class="displayTagTable"
								 cellpadding="2"
								 requestURI="<%= WebConstants.MVC_BOOK_LIBRARY_LIST%>"
								 sort="external">
	  <display:setProperty name="paging.banner.onepage" value=" " />
	  <display:setProperty name="basic.msg.empty_list">No book definitions were found.</display:setProperty>
	  <display:column title="${selectAll}"  style="text-align: center">
	  		<form:checkbox path="selectedEbookKeys" value="${vdo.bookDefinitionId}" cssClass="simple-checkbox" onclick="updateSelectAll(this)"/>
	  </display:column>
	  <display:column title="ProView Display Name" sortable="true" sortProperty="<%=DisplayTagSortProperty.PROVIEW_DISPLAY_NAME.toString() %>" style="text-align: left">
	  	<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_ID%>=${vdo.bookDefinitionId}">${vdo.proviewDisplayName}</a>
	  </display:column>
	  <display:column title="Source Type" property="sourceType" sortProperty="<%=DisplayTagSortProperty.SOURCE_TYPE.toString() %>" sortable="true"/>
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
	
	<div class="buttons">
		<input type="button" value="Generate" ${generateBook} onclick="submitBookLibraryFormWithCommand('<%= BookLibraryFilterForm.Command.GENERATE %>')" />
	</div>

</form:form>
