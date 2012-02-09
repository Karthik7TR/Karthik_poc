<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.SortProperty"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>


	<jsp:scriptlet>
	   <![CDATA[
	       org.displaytag.decorator.CheckboxTableDecorator decorator = new org.displaytag.decorator.CheckboxTableDecorator();
	       decorator.setId("fullyQualifiedTitleId");
	       decorator.setFieldName("selected");
	       pageContext.setAttribute("checkboxDecorator", decorator);
	           ]]>
	 </jsp:scriptlet>
	 
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js" type="text/javascript"> </script>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#selectAll').click(function () {
				$(this).parents('#vdo').find(':checkbox').attr('checked', this.checked);
			});
			
			// Post to import controller
			$('#importButton').click(function (){
				$('form#theForm').attr({action: ''});
				$('form#theForm').submit();
			});
			// Post to export controller
			$('#exportButton').click(function (){
				$('form#theForm').attr({action: ''});
				$('form#theForm').submit();		
			});
			// Post to generate
			$('#generateButton').click(function (){
				$('form#theForm').attr({action: ''});
				$('form#theForm').submit();
			});
			// Post to promote
			$('#promoteButton').click(function (){
				$('form#theForm').attr({action: ''});
				$('form#theForm').submit();
			});
		});
	</script>
  
<form:form id="theForm" method="post">
	<c:set var="selectAll" value="<input type='checkbox' id='selectAll' value='false' />"/>
	<%-- Table of book library --%>
	<display:table id="vdo" name="paginatedList" class="displayTagTable" cellpadding="2" 
				   requestURI="<%= WebConstants.MVC_BOOK_LIBRARY_LIST_PAGING%>"
				   pagesize="20"
				   partialList="true"
				   size="resultSize"
				   sort="external"
				   decorator="checkboxDecorator">
	  <display:setProperty name="basic.msg.empty_list">No book definitions were found.</display:setProperty>
	  <display:column title="${selectAll}" property="checkbox"/>
	  <display:column title="Book Name" property="bookName" sortable="true" sortName="bookName" style="text-align: left"/>
	  <display:column title="Author" property="author" sortable="true" sortName="authorInfo" style="text-align: left"/>
	</display:table>
	
	<input type="button" id="importButton" value="Import" disabled="disabled" />
	<input type="button" id="exportButton" value="Export" disabled="disabled" />
	<input type="button" id="generateButton" value="Generate" />
	<input type="button" id="promoteButton" value="Promote" />
</form:form>