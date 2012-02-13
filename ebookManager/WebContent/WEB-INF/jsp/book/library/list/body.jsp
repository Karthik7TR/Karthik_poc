<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.SortProperty"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

	 
	<script type="text/javascript">
		// Calculate how many checkboxes are selected
		var howManyChecked = function() {
			var selected = new Array();
			$("input:checkbox:checked").each(function() {
			       selected.push($(this).val());
			});
				
			return selected.length;
		}
		
		$(document).ready(function() {
			$('#selectAll').click(function () {
				$(this).parents('#vdo').find(':checkbox').attr('checked', this.checked);
			});
			
			// Post to import controller
			$('#importButton').click(function (){
				location.href  = "<%= WebConstants.MVC_BOOK_DEFINITION_IMPORT%>";
			});
			// Post to export controller
			$('#exportButton').click(function (){
				//$('form#theForm').attr({action: ''});
				//$('form#theForm').submit();		
			});
			// Post to generate
			$('#generateButton').click(function (){
				var numberSelected = howManyChecked();
				if(numberSelected == 0) {
					alert("Nothing selected");
					exit();
				} else if(numberSelected == 1) {
					$('form#<%=BookLibrarySelectionForm.FORM_NAME%>').attr({action:'<%= WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW%>'});
				} else {
					$('form#<%=BookLibrarySelectionForm.FORM_NAME%>').attr({action:'<%= WebConstants.MVC_BOOK_BULK_GENERATE_PREVIEW%>'});
				}
				
				$('form#<%=BookLibrarySelectionForm.FORM_NAME%>').submit();
			});
			// Post to promote
			$('#promoteButton').click(function (){
				var numberSelected = howManyChecked();
				
				if(numberSelected == 0) {
					alert("Nothing selected");
					exit();
				} else {
					$('form#<%=BookLibrarySelectionForm.FORM_NAME%>').attr({action: '<%= WebConstants.MVC_BOOK_DEFINITION_PROMOTION%>'});
					$('form#<%=BookLibrarySelectionForm.FORM_NAME%>').submit();	
				}
			});
		});
	</script>

<form:form commandName="<%=BookLibrarySelectionForm.FORM_NAME%>" name="selectionForm" method="post">
			   
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
	<%-- Table of book library --%>
	<display:table id="vdo" name="paginatedList" class="displayTagTable" cellpadding="2" 
				   requestURI="<%= WebConstants.MVC_BOOK_LIBRARY_LIST_PAGING%>"
				   pagesize="<%= WebConstants.KEY_NUMBER_BOOK_DEF_SHOWN %>"
				   partialList="true"
				   size="resultSize"
				   sort="external">
	  <display:setProperty name="basic.msg.empty_list">No book definitions were found.</display:setProperty>
	  <display:column title="${selectAll}">
	  		<%-- <input type="checkbox" value="${vdo.fullyQualifiedTitleId}" /> --%>
	  		<form:checkbox path="selectedEbookKeys" value="${vdo.fullyQualifiedTitleId}"/>
	  </display:column>
	  <display:column title="Book Name" sortable="true" sortName="bookName" style="text-align: left">
	  	<a href="<%=WebConstants.MVC_BOOK_DEFINITION_VIEW_GET%>?<%=WebConstants.KEY_TITLE_ID%>=${vdo.fullyQualifiedTitleId}">${vdo.bookName}</a>
	  </display:column>
	  <display:column title="Author" property="author" sortable="true" sortName="authorInfo" style="text-align: left"/>
	  <display:column title="Ver" property="version" />
	  <display:column title="Publish Date" property="publishDate" />
	  <display:column title="Publish Status" property="publishStatus" style="text-align: center"/>
	  <display:column title="Last eBook Def. Edited" property="lastEdit" />
	</display:table>
	
	<input type="button" id="importButton" value="Import" />
	<input type="button" id="exportButton" value="Export" disabled="disabled" />
	<input type="button" id="generateButton" value="Generate" />
	<input type="button" id="promoteButton" value="Promote" />
</form:form>
